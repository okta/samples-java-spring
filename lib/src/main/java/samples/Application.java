package samples;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
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
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Map;
import java.util.HashMap;

import static java.lang.System.out;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@EnableAutoConfiguration
public class Application {

    // GLOBALS
    private OktaConfig CONFIG = getConfig();
    private Map<String, Key> CACHED_KEYS = new HashMap<String, Key>();
    private User user = new User();

    @RequestMapping(value = "/", method = GET)
    public String scenarios(Map<String, Object> model) {
        model.put("user", user.toDict());
        model.put("config", CONFIG);
        return "index";
    }

    @RequestMapping(value = "/authorization-code/login-redirect", method = GET)
    public String loginRedirect(Map<String, Object> model) {
        model.put("user", user.toDict());
        model.put("config", CONFIG);
        return "redirect";
    }

    @RequestMapping(value = "/authorization-code/login-custom", method = GET)
    public String loginCustom(Map<String, Object> model) {
        model.put("user", user.toDict());
        model.put("config", CONFIG);
        return "custom";
    }

    @RequestMapping(value = "/authorization-code/profile", method = GET)
    public String profile(Map<String, Object> model, HttpServletResponse response, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null ) {
            // No session - redirect
            try {
                response.sendRedirect("/");
                return null;
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        model.put("user", user.toDict());
        model.put("config", CONFIG);
        return "userProfile";
    }

    @RequestMapping("/authorization-code/callback")
    @ResponseBody
    public String callback(@RequestParam("state") String state,
                    @RequestParam("code") String code,
                    @CookieValue(value="okta-oauth-state", defaultValue = "") String cookieState,
                    @CookieValue(value="okta-oauth-nonce", defaultValue = "") String cookieNonce,
                    HttpServletResponse response, HttpServletRequest request){

        if (cookieState.equals("") || cookieNonce.equals("")) {
            // Verify state and nonce from cookie
            return send401(response, "Error retrieving cookies");
        }

        if (cookieState.equals("") || cookieNonce.equals("")) {
            // Verify state and nonce from cookie
            return send401(response, "Error retrieving cookies");
        }

        if (!state.equals(cookieState)) {
            // Verify state
            return send401(response, "State from cookie does not match query state");
        }

        String queryString = null;
        try {
            queryString = getTokenUri(code);
        } catch (UnsupportedEncodingException e) {
            return send401(response, e.getMessage());
        }

        String tokenEndpoint = CONFIG.getOidc().getOktaUrl() + "/oauth2/v1/token?";
        String clientId = CONFIG.getOidc().getClientId();
        String clientSecret = CONFIG.getOidc().getClientSecret();
        byte[] encodedAuth = Base64.encodeBase64((clientId + ":" + clientSecret).getBytes());

        // Bypass JSESSIONID Cookie to pass yakbak tests
        CloseableHttpClient httpClient = HttpClients.custom()
                .disableCookieManagement()
                .build();

        // Call /token endpoint
        Unirest.setHttpClient(httpClient);
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.post(tokenEndpoint + queryString)
                    .header("user-agent", null)
                    .header("content-type", "application/x-www-form-urlencoded")
                    .header("authorization", "Basic " + new String(encodedAuth))
                    .header("connection", "close")
                    .header("accept", "application/json")
                    .asJson();
        } catch (UnirestException e) {
            return send401(response, e.getMessage());
        }

        if(jsonResponse.getStatus() != 200) {
            // Error returning JSON object from /token endpoint
            return send401(response, jsonResponse.getStatusText());
        }

        JsonNode tokens = jsonResponse.getBody();
        String idToken = tokens.getObject().get("id_token").toString();

        if (idToken == null || idToken.equals("")) {
            // No id token present
            return send401(response, "No id_token in response from /token endpoint");
        }

        Map claims;
        try {
            claims = validateToken(idToken, cookieNonce);
        } catch (Exception e) {
            // Token was not valid
            return send401(response, e.getMessage());
        }

        // Set new user session
        user.setEmail(claims.get("email").toString());
        user.setClaims(claims);

        HttpSession session = request.getSession();
        session.setAttribute("user", user.getEmail());

        try {
            response.sendRedirect("/authorization-code/profile");
            return null;
        } catch (IOException e) {
            // FIXME- Should return custom error page
            return "Error redirecting to /authorization-code/profile";
        }
    }

    private String send401(HttpServletResponse resp, String message) {
        try {
            resp.sendError(401, message);
            return null; // Let Spring MVC know response was already sent
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private Map validateToken(String idToken, String nonce) throws Exception {
        Map decoded;
        Key key = fetchJwk(idToken);

        // Allow for 5 minute clock skew
        int clock_skew = 300;

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(clock_skew)
                .setExpectedAudience(CONFIG.getOidc().getClientId())
                .setExpectedIssuer(CONFIG.getOidc().getOktaUrl())
                .setVerificationKey(key)
                .build();

        //  Validate the JWT and process it to the Claims
        JwtClaims jwtClaims = jwtConsumer.processToClaims(idToken);

        String claimsNonce = jwtClaims.getClaimsMap().get("nonce").toString();

        if (!claimsNonce.equals(nonce)) {
            throw new Exception("Claims nonce does not mach cookie nonce");
        }

        // Verify token was not issued in the future (accounting for clock skew)
        NumericDate current = NumericDate.now();
        current.addSeconds(clock_skew);

        if(jwtClaims.getIssuedAt().isAfter(current)){
            throw new Exception("invalid iat claim");
        }

        decoded = jwtClaims.getClaimsMap();
        return decoded;
    }

    private String getTokenUri(String code) throws UnsupportedEncodingException {
        String redirectUri = CONFIG.getOidc().getRedirectUri();
        return "grant_type=authorization_code&code=" +
                UriUtils.encode(code, "UTF-8") +
                "&redirect_uri=" +
                UriUtils.encode(redirectUri, "UTF-8");
    }

    private Key fetchJwk(String idToken) throws JoseException, IOException {
        JsonWebSignature jws = new JsonWebSignature();
        jws.setCompactSerialization(idToken);
        String keyID = jws.getKeyIdHeaderValue();

        if (CACHED_KEYS.get(keyID) != null) {
            return CACHED_KEYS.get(keyID);
        }

        String jwksUri = CONFIG.getOidc().getOktaUrl() + "/oauth2/v1/keys";
        HttpsJwks httpJkws = new HttpsJwks(jwksUri);

        for (JsonWebKey key : httpJkws.getJsonWebKeys()) {
            CACHED_KEYS.put(key.getKeyId(), key.getKey());
        }

        if (CACHED_KEYS.get(keyID) != null) {
            return CACHED_KEYS.get(keyID);
        }
        return null; // No Key found
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

    public OktaConfig getConfig() {
        String path = System.getProperty("user.dir") + "/.samples.config.json";
        try {
            // Import config file
            ObjectMapper map = new ObjectMapper();
            return map.readValue(new File(path), OktaConfig.class);
        } catch (Exception e) {
            e.printStackTrace();

        }
        System.exit(1);
        return null;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        out.println("Server Started");
    }
}
