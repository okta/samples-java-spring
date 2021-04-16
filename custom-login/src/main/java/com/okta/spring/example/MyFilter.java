package com.okta.spring.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.commons.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MyFilter extends AbstractAuthenticationProcessingFilter {

    private final Logger logger = LoggerFactory.getLogger(MyFilter.class);

    private static final String AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE = "authorization_request_not_found";
    private static final String CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE = "client_registration_not_found";

    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository =
            new HttpSessionOAuth2AuthorizationRequestRepository();

    public void setClientRegistrationRepository(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    private ClientRegistrationRepository clientRegistrationRepository;

    @Value("${okta.oauth2.issuer}")
    private String issuer;

    @Value("${okta.oauth2.clientId}")
    private String clientId;

    @Value("${okta.oauth2.clientSecret}")
    private String clientSecret;

    private GrantedAuthoritiesMapper authoritiesMapper = ((authorities) -> authorities);

    private Converter<OAuth2UserRequest, RequestEntity<?>> requestEntityConverter = new OAuth2UserRequestEntityConverter();

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, Object>>() {
    };

    ParameterizedTypeReference<HashMap<String, String>> responseType =
            new ParameterizedTypeReference<HashMap<String, String>>() {};

    protected MyFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, JsonProcessingException {
        logger.info("======= attemptAuthentication ====== ");

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();

        MultiValueMap<String, String> params = OAuth2AuthorizationResponseUtils.toMultiMap(request.getParameterMap());
        String interactionCode = params.getFirst("interaction_code");

        String codeChallenge = (String) session.getAttribute("codeChallenge");
        String codeVerifier = (String) session.getAttribute("codeVerifier");

        logger.info("==== interaction code ==== {}", interactionCode);
        logger.info("==== code challenge ==== {}", codeChallenge);
        logger.info("==== code verifier ==== {}", codeVerifier);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type","interaction_code");
        map.add("client_id", clientId);
        if (Strings.hasText(clientSecret)) {
            map.add("client_secret", clientSecret);
        }
        map.add("interaction_code", interactionCode);
        map.add("code_verifier", codeVerifier);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        ResponseEntity<JsonNode> responseEntity =
                restTemplate.postForEntity(issuer + "/v1/token", requestEntity, JsonNode.class);

        logger.info("===== RESPONSE ==== {}", responseEntity.getBody());

        String accessTokenStr = responseEntity.getBody().get("access_token").textValue();

        String redirectUri = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replaceQuery(null)
                .build()
                .toUriString();

        OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository
                .removeAuthorizationRequest(request, response);

        String registrationId = authorizationRequest.getAttribute(OAuth2ParameterNames.REGISTRATION_ID);
        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            OAuth2Error oauth2Error = new OAuth2Error(CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE,
                    "Client Registration not found with Id: " + registrationId, null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponseUtils.convert(params,
                redirectUri);
        Object authenticationDetails = this.authenticationDetailsSource.buildDetails(request);
//        OAuth2LoginAuthenticationToken authenticationRequest = new OAuth2LoginAuthenticationToken(clientRegistration,
//                new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse));
//        authenticationRequest.setDetails(authenticationDetails);
//        OAuth2LoginAuthenticationToken authenticationResult = (OAuth2LoginAuthenticationToken) this
//                .getAuthenticationManager().authenticate(authenticationRequest);

        //OAuth2AuthenticationToken oauth2Authentication = new OAuth2AuthenticationToken(
          //      authenticationResult.getPrincipal(), authenticationResult.getAuthorities(),
            //    authenticationResult.getClientRegistration().getRegistrationId());

        //Jwt jwt = Jwt.withTokenValue(responseEntity.getBody().get("access_token").toString()).build();

        OAuth2AuthorizationExchange oAuth2AuthorizationExchange = new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);

        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                accessTokenStr,
                null,
                Instant.now().plusMillis(Long.parseLong(responseEntity.getBody().get("expires_in").toString())),
                new HashSet<>(Collections.singletonList(responseEntity.getBody().get("scope").textValue())));

        //OAuth2UserRequest oAuth2UserRequest = new OAuth2UserRequest(clientRegistration, oAuth2AccessToken, Collections.emptyMap());
        //RequestEntity<?> req = this.requestEntityConverter.convert(oAuth2UserRequest);
        logger.info("=== Retrieve user details with accessToken === {}", accessTokenStr);

        RestTemplate restTemplate1 = new RestTemplate();
        HttpHeaders hdrs = new HttpHeaders();
        hdrs.set("Authorization", "Bearer " + accessTokenStr);
        hdrs.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(null, hdrs);
        ResponseEntity<String> respEntity =
                restTemplate1.exchange(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri(),
                        HttpMethod.POST, entity, String.class);

        logger.info("=== USER INFO RESPONSE == {}", respEntity.getBody());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> userAttributes = objectMapper.readValue(respEntity.getBody(), Map.class);
        logger.info("=== USER INFO ATTR MAP == {}", userAttributes);
        //Map<String, Object> userAttributes = response1.getBody();
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new OAuth2UserAuthority(userAttributes));
        //OAuth2AccessToken token = oAuth2UserRequest.getAccessToken();
        logger.info("=== SCOPES inside oAuth2AccessToken object == {}", oAuth2AccessToken.getScopes());
        for (String authority : oAuth2AccessToken.getScopes()) {
            logger.info("=== Authority == {}", authority);
            authorities.add(new SimpleGrantedAuthority("SCOPE_" + authority));
        }
        OAuth2User oauth2User = new DefaultOAuth2User(authorities, userAttributes, "name");
        //OAuth2User oauth2User = new OktaOAuth2UserService().loadUser(new OAuth2UserRequest(
          //      clientRegistration, oAuth2AccessToken, Collections.emptyMap()));
        logger.info(" oauth2user {}", oauth2User);
        logger.info(" oauth2user authorities {}", oauth2User.getAuthorities());
        Collection<? extends GrantedAuthority> mappedAuthorities = this.authoritiesMapper
                .mapAuthorities(oauth2User.getAuthorities());
        OAuth2LoginAuthenticationToken authenticationResult = new OAuth2LoginAuthenticationToken(
                clientRegistration, oAuth2AuthorizationExchange,
                oauth2User, mappedAuthorities, oAuth2AccessToken, null);
        authenticationResult.setDetails(authenticationDetails);
        //return authenticationResult;

                OAuth2AuthenticationToken oauth2Authentication = new OAuth2AuthenticationToken(
                oauth2User, authenticationResult.getAuthorities(),
                authenticationResult.getClientRegistration().getRegistrationId());
        oauth2Authentication.setDetails(authenticationDetails);
        return oauth2Authentication;

//        OAuth2AuthorizationCodeAuthenticationToken authenticationResult = new OAuth2AuthorizationCodeAuthenticationToken(
//                clientRegistration,
//                oAuth2AuthorizationExchange, oAuth2AccessToken,
//                null);
//        authenticationResult.setAuthenticated(true);
//        authenticationResult.setDetails(authenticationDetails);
//        //return authenticationResult;
//
//        logger.info("=== Principal == {}", authenticationResult.getPrincipal());
//        logger.info("=== Authorities == {}", authenticationResult.getAuthorities());
//
//        OAuth2LoginAuthenticationToken oAuth2LoginAuthenticationToken =
//                new OAuth2LoginAuthenticationToken(clientRegistration, oAuth2AuthorizationExchange,
//                        (OAuth2User) authenticationResult.getPrincipal(), authenticationResult.getAuthorities(), oAuth2AccessToken);
//        oAuth2LoginAuthenticationToken.setAuthenticated(true);
//        oAuth2LoginAuthenticationToken.setDetails(authenticationDetails);

//        OAuth2User oAuth2User = new DefaultOAuth2User(authenticationResult.getAuthorities(), Collections.emptyMap(), "name");
//        OAuth2AuthenticationToken oauth2Authentication = new OAuth2AuthenticationToken(
//                oAuth2User, authenticationResult.getAuthorities(),
//                authenticationResult.getClientRegistration().getRegistrationId());
//        oauth2Authentication.setDetails(authenticationDetails);
//
//        return oauth2Authentication;

//        String jwkSetUri = clientRegistration.getProviderDetails().getJwkSetUri();
//        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
//        Jwt jwt = jwtDecoder.decode(responseEntity.getBody().get("access_token").toString());
//
//        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
//        return jwtAuthenticationToken;

//        return oAuth2LoginAuthenticationToken;
    }


    static Collection<? extends GrantedAuthority> tokenScopesToAuthorities(OAuth2AccessToken accessToken) {

        if (accessToken == null || accessToken.getScopes() == null) {
            return Collections.emptySet();
        }

        return accessToken.getScopes().stream()
                .map(scope -> "SCOPE_" + scope)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
