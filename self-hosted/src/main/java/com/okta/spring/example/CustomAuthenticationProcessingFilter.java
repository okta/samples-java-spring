package com.okta.spring.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.commons.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProcessingFilter.class);

    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository =
            new HttpSessionOAuth2AuthorizationRequestRepository();

    private ClientRegistrationRepository clientRegistrationRepository;

    @Value("${okta.oauth2.issuer}")
    private String issuer;

    @Value("${okta.oauth2.clientId}")
    private String clientId;

    @Value("${okta.oauth2.clientSecret}")
    private String clientSecret;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    private GrantedAuthoritiesMapper authoritiesMapper = ((authorities) -> authorities);

    protected CustomAuthenticationProcessingFilter(String defaultFilterProcessesUrl,
                                                   AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, JsonProcessingException {
        logger.info("======= attemptAuthentication ====== ");

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();

        MultiValueMap<String, String> requestParams =
                OAuth2AuthorizationResponseUtils.toMultiMap(request.getParameterMap());
        String interactionCode = requestParams.getFirst("interaction_code");
        String codeVerifier = (String) session.getAttribute("codeVerifier");

        JsonNode jsonNode = exchangeCodeForToken(interactionCode, codeVerifier);
        OAuth2AccessToken oAuth2AccessToken = buildOAuth2AccessToken(jsonNode);
        OAuth2RefreshToken oAuth2RefreshToken = buildOAuth2RefreshToken(jsonNode);

        String redirectUri = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replaceQuery(null)
                .build()
                .toUriString();

        OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository
                .removeAuthorizationRequest(request, response);

        String registrationId = authorizationRequest.getAttribute(OAuth2ParameterNames.REGISTRATION_ID);
        ClientRegistration clientRegistration =
                this.clientRegistrationRepository.findByRegistrationId(registrationId);
        OAuth2AuthorizationResponse authorizationResponse =
                OAuth2AuthorizationResponseUtils.convert(requestParams, redirectUri);
        Object authenticationDetails = this.authenticationDetailsSource.buildDetails(request);
        OAuth2AuthorizationExchange oAuth2AuthorizationExchange =
                new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);

        Map<String, Object> userAttributes =
                getUserAttributes(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri(),
                        oAuth2AccessToken);

        Collection<? extends GrantedAuthority> authorities = tokenScopesToAuthorities(oAuth2AccessToken);
        OAuth2User oauth2User = new DefaultOAuth2User(authorities, userAttributes, "name");

        Collection<? extends GrantedAuthority> mappedAuthorities = this.authoritiesMapper
                .mapAuthorities(oauth2User.getAuthorities());
        OAuth2LoginAuthenticationToken authenticationResult = new OAuth2LoginAuthenticationToken(
                clientRegistration, oAuth2AuthorizationExchange,
                oauth2User, mappedAuthorities, oAuth2AccessToken, oAuth2RefreshToken);
        authenticationResult.setDetails(authenticationDetails);

        OAuth2AuthenticationToken oauth2Authentication = new OAuth2AuthenticationToken(
                oauth2User, authenticationResult.getAuthorities(),
                authenticationResult.getClientRegistration().getRegistrationId());
        oauth2Authentication.setDetails(authenticationDetails);
        return oauth2Authentication;
    }

    // helpers

    private Map<String, Object> getUserAttributes(String uri, OAuth2AccessToken oAuth2AccessToken)
            throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + oAuth2AccessToken.getTokenValue());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> respEntity =
                restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        Map<String, Object> userAttributes = objectMapper.readValue(respEntity.getBody(), Map.class);
        return userAttributes;
    }

    private JsonNode exchangeCodeForToken(String interactionCode, String codeVerifier) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "interaction_code");
        map.add("client_id", clientId);
        if (Strings.hasText(clientSecret)) {
            map.add("client_secret", clientSecret);
        }
        map.add("interaction_code", interactionCode);
        map.add("code_verifier", codeVerifier);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);
        ResponseEntity<JsonNode> responseEntity =
                restTemplate.postForEntity(issuer + "/v1/token", requestEntity, JsonNode.class);

        return responseEntity.getBody();
    }

    public void setClientRegistrationRepository(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    private OAuth2AccessToken buildOAuth2AccessToken(JsonNode node) {
        String accessTokenStr = node.get("access_token").textValue();
        String scopes = node.get("scope").textValue();
        Set<String> scopesSet = new HashSet<>(Arrays.asList(scopes.split(" ")));

        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                accessTokenStr,
                null,
                Instant.now().plusMillis(Long.parseLong(node.get("expires_in").toString())),
                scopesSet);
    }

    private OAuth2RefreshToken buildOAuth2RefreshToken(JsonNode node) {
        OAuth2RefreshToken oAuth2RefreshToken = null;
        JsonNode refreshTokenNode = node.get("refresh_token");
        if (refreshTokenNode != null) {
            String refreshTokenStr = refreshTokenNode.textValue();
            if (Strings.hasText(refreshTokenStr)) {
                oAuth2RefreshToken = new OAuth2RefreshToken(refreshTokenStr, null);
            }
        }
        return oAuth2RefreshToken;
    }

    private Collection<? extends GrantedAuthority> tokenScopesToAuthorities(OAuth2AccessToken accessToken) {
        if (accessToken == null || accessToken.getScopes() == null) {
            return Collections.emptySet();
        }

        return accessToken.getScopes().stream()
                .map(scope -> "SCOPE_" + scope)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
