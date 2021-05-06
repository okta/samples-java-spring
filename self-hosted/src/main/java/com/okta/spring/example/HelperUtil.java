/*
 * Copyright 2021-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.spring.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.commons.lang.Strings;
import com.okta.idx.sdk.api.util.ClientUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HelperUtil {

    private final String issuer;

    private final String clientId;

    private final String clientSecret;

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    public HelperUtil(final String issuer, final String clientId, final String clientSecret,
                      final ObjectMapper objectMapper, final RestTemplate restTemplate) {
        this.issuer = issuer;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getUserAttributes(final String uri, final OAuth2AccessToken oAuth2AccessToken)
            throws JsonProcessingException {

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + oAuth2AccessToken.getTokenValue());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        final ResponseEntity<String> respEntity =
                restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        return objectMapper.readValue(respEntity.getBody(), Map.class);
    }

    public JsonNode exchangeCodeForToken(final String interactionCode, final String codeVerifier)
            throws MalformedURLException {

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        final MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("grant_type", "interaction_code");
        requestParams.add("client_id", clientId);
        if (Strings.hasText(clientSecret)) {
            requestParams.add("client_secret", clientSecret);
        }
        requestParams.add("interaction_code", interactionCode);
        requestParams.add("code_verifier", codeVerifier);

        final HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<>(requestParams, headers);
        final String tokenUri = ClientUtil.getNormalizedUri(issuer, "/v1/token");

        final ResponseEntity<JsonNode> responseEntity =
                restTemplate.postForEntity(tokenUri, requestEntity, JsonNode.class);

        return responseEntity.getBody();
    }

    public OAuth2AccessToken buildOAuth2AccessToken(final JsonNode node) {

        final String accessTokenStr = node.get("access_token").textValue();
        final String scopes = node.get("scope").textValue();
        final Set<String> scopesSet = new HashSet<>(Arrays.asList(scopes.split(" ")));

        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                accessTokenStr,
                null,
                Instant.now().plusMillis(Long.parseLong(node.get("expires_in").toString())),
                scopesSet);
    }

    public OAuth2RefreshToken buildOAuth2RefreshToken(final JsonNode node) {

        OAuth2RefreshToken oAuth2RefreshToken = null;
        final JsonNode refreshTokenNode = node.get("refresh_token");
        if (refreshTokenNode != null) {
            final String refreshTokenStr = refreshTokenNode.textValue();
            if (Strings.hasText(refreshTokenStr)) {
                oAuth2RefreshToken = new OAuth2RefreshToken(refreshTokenStr, null);
            }
        }
        return oAuth2RefreshToken;
    }

    public Collection<? extends GrantedAuthority> tokenScopesToAuthorities(final OAuth2AccessToken accessToken) {

        if (accessToken == null || accessToken.getScopes() == null) {
            return Collections.emptySet();
        }

        return accessToken.getScopes().stream()
                .map(scope -> "SCOPE_" + scope)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
