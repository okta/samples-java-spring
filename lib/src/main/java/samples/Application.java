package samples;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.util.UriUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Map;
import java.util.HashMap;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@EnableAutoConfiguration
public class Application {

    // GLOBALS
    private OktaConfig CONFIG = new OktaConfig();
    private Map<String, Key> CACHED_KEYS = new HashMap<String, Key>();
    private User user = new User();


    @RequestMapping(value = "/", method = GET)
    public String scenarios(Map<String, Object> model) {

        model.put("user", user.toDict());
        model.put("config", CONFIG.config);

        return "index";
    }

    @RequestMapping(value = "/authorization-code/login-redirect", method = GET)
    public String loginRedirect(Map<String, Object> model) {

        model.put("user", user.toDict());
        model.put("config", CONFIG.config);

        return "redirect";
    }

    @RequestMapping(value = "/authorization-code/login-custom", method = GET)
    public String loginCustom(Map<String, Object> model) {

        model.put("user", user.toDict());
        model.put("config", CONFIG.config);

        return "custom";
    }

    @RequestMapping(value = "/authorization-code/profile", method = GET)
    public String profile(Map<String, Object> model,
                          HttpServletResponse response,
                          HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null ) {
            // No session - redirect
            try {
                response.sendRedirect("/");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        model.put("user", user.toDict());
        model.put("config", CONFIG.config);

        return "userProfile";
    }

    @RequestMapping("/authorization-code/callback")
    @ResponseBody
    public String callback(@RequestParam("state") String state,
                    @RequestParam("code") String code,
                    @CookieValue(value="okta-oauth-state", defaultValue = "") String cookieState,
                    @CookieValue(value="okta-oauth-nonce", defaultValue = "") String cookieNonce,
                    HttpServletResponse response, HttpServletRequest request){

        Map claims;
        try {

            if (cookieState.equals("") || cookieNonce.equals("")) {
                // Verify state and nonce from cookie
                throw new Exception("error retrieving cookies");
            }

            if (!state.equals(cookieState)) {
                // Verify state
                throw new Exception("State from cookie does not match query state");
            }

            String queryString = getTokenUri(code);
            String tokenEndpoint = CONFIG.getValue("oktaUrl") + "/oauth2/v1/token?";

            String clientId = CONFIG.getValue("clientId");
            String clientSecret = CONFIG.getValue("clientSecret");
            byte[] encodedAuth = Base64.encodeBase64((clientId + ":" + clientSecret).getBytes());

            // Bypass JSESSIONID Cookie to pass yakbak tests
            CloseableHttpClient httpClient = HttpClients.custom()
                    .disableCookieManagement()
                    .build();

            // Call /token endpoint
            Unirest.setHttpClient(httpClient);
            HttpResponse<JsonNode> jsonResponse = Unirest.post(tokenEndpoint + queryString)
                    .header("user-agent", null)
                    .header("content-type", "application/x-www-form-urlencoded")
                    .header("authorization", "Basic " + new String(encodedAuth))
                    .header("connection", "close")
                    .header("accept", "application/json")
                    .asJson();

            if(jsonResponse.getStatus() != 200) {
                // Error returning JSON object from /token endpoint
                throw new Exception(jsonResponse.getStatusText());
            }

            JsonNode tokens = jsonResponse.getBody();
            String idToken = tokens.getObject().get("id_token").toString();

            if (idToken == null || idToken.equals("")) {
                // No id token present
                throw new Exception("No id_token in response from /token endpoint");
            }

            claims = validateToken(idToken, cookieNonce);

        } catch (Exception e) {
            // Send 401 and error message
            send401(response, e.getMessage());
            return null;
        }
        if (claims != null) {
            // Set user session
            user.setClaims(claims);
            user.setEmail(claims.get("email").toString());

            HttpSession session = request.getSession();
            session.setAttribute("user", user.email);

            try {
                // Redirect to profile
                response.sendRedirect("/authorization-code/profile");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        } else {
            // Handle unknown error
            send401(response, "Unknown Error");
            return null;

        }

    }

    private void send401(HttpServletResponse resp, String message) {
        try {
            resp.sendError(401, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map validateToken(String idToken, String nonce) {
        Map claims;

        try {
            Key key = fetchJwk(idToken);

            if (key == null) {
                throw new Exception("Error validating token signature");
            }

            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                    .setRequireExpirationTime()
                    .setAllowedClockSkewInSeconds(300)
                    .setExpectedAudience(CONFIG.getValue("clientId"))
                    .setExpectedIssuer(CONFIG.getValue("oktaUrl"))
                    .setVerificationKey(key)
                    .build();

            //  Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(idToken);

            String claimsNonce = jwtClaims.getClaimsMap().get("nonce").toString();

            if (!claimsNonce.equals(nonce)) {
                throw new Exception("Claims nonce does not mach cookie nonce");
            }

            NumericDate current = NumericDate.now();
            current.addSeconds(300);

            if(jwtClaims.getIssuedAt().isAfter(current)){
                throw new Exception("invalid iat claim");
            }

            claims = jwtClaims.getClaimsMap();

        } catch (Exception e ) {
            // Error
            return null;
        }

        return claims;
    }


    private String getTokenUri(String code) throws UnsupportedEncodingException {
        String redirectUri = CONFIG.getValue("redirectUri");

        return "grant_type=authorization_code&code=" +
                UriUtils.encode(code, "UTF-8") +
                "&redirect_uri=" +
                UriUtils.encode(redirectUri, "UTF-8");
    }

    private Key fetchJwk(String idToken) {
        JsonWebSignature jws = new JsonWebSignature();

        try {
            jws.setCompactSerialization(idToken);

            String keyID = jws.getKeyIdHeaderValue();

            if (CACHED_KEYS.get(keyID) != null) {
                return CACHED_KEYS.get(keyID);
            }

            String jwksUri = CONFIG.getValue("oktaUrl") + "/oauth2/v1/keys";
            HttpsJwks httpJkws = new HttpsJwks(jwksUri);

            for (JsonWebKey key : httpJkws.getJsonWebKeys()) {
                CACHED_KEYS.put(key.getKeyId(), key.getKey());
            }

            if (CACHED_KEYS.get(keyID) != null) {
                return CACHED_KEYS.get(keyID);
            }

        } catch (Exception e) {
           // Error
            return null;
        }

        return null;
    }

    @RequestMapping("authorization-code/logout")
     public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        user = new User();

        return "redirect:/";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity missingRequestParams(MissingServletRequestParameterException ex) {
        String param = ex.getParameterName();

        return ResponseEntity
                .status(401)
                .body("Missing required parameter: {{ " + param + " }}");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        System.out.println("Server Started");
    }
}
