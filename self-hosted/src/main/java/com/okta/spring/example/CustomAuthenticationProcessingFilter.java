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

import com.fasterxml.jackson.databind.JsonNode;
import com.okta.commons.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class CustomAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository =
            new HttpSessionOAuth2AuthorizationRequestRepository();

    private ClientRegistrationRepository clientRegistrationRepository;

    private final GrantedAuthoritiesMapper authoritiesMapper = ((authorities) -> authorities);

    protected CustomAuthenticationProcessingFilter(final String defaultFilterProcessesUrl,
                                                   final AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl, authenticationManager);
    }

    @Autowired
    private HelperUtil helperUtil;

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request,
                                                final HttpServletResponse response)
            throws AuthenticationException, IOException {

        final ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpSession session = servletRequestAttributes.getRequest().getSession();

        final MultiValueMap<String, String> requestParams =
                OAuth2AuthorizationResponseUtils.toMultiMap(request.getParameterMap());

        final String interactionCode = requestParams.getFirst("interaction_code");
        final String codeVerifier = (String) session.getAttribute("codeVerifier");

        if (!Strings.hasText(interactionCode)) {
            String error = requestParams.getFirst("error");
            String errorDesc = requestParams.getFirst("error_description");
            throw new OAuth2AuthenticationException(new OAuth2Error(error, errorDesc, null));
        }

        final JsonNode jsonNode = helperUtil.exchangeCodeForToken(interactionCode, codeVerifier);
        final OAuth2AccessToken oAuth2AccessToken = helperUtil.buildOAuth2AccessToken(jsonNode);
        final OAuth2RefreshToken oAuth2RefreshToken = helperUtil.buildOAuth2RefreshToken(jsonNode);

        final String redirectUri = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replaceQuery(null)
                .build()
                .toUriString();

        final OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository
                .removeAuthorizationRequest(request, response);

        final String registrationId = authorizationRequest.getAttribute(OAuth2ParameterNames.REGISTRATION_ID);
        final ClientRegistration clientRegistration =
                this.clientRegistrationRepository.findByRegistrationId(registrationId);
        final OAuth2AuthorizationResponse authorizationResponse =
                OAuth2AuthorizationResponseUtils.convert(requestParams, redirectUri);
        final Object authenticationDetails = this.authenticationDetailsSource.buildDetails(request);
        final OAuth2AuthorizationExchange oAuth2AuthorizationExchange =
                new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);

        final Map<String, Object> userAttributes =
                helperUtil.getUserAttributes(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri(),
                        oAuth2AccessToken);

        final Collection<? extends GrantedAuthority> authorities =
                helperUtil.tokenScopesToAuthorities(oAuth2AccessToken);
        final OAuth2User oauth2User = new DefaultOAuth2User(authorities, userAttributes, "name");

        final Collection<? extends GrantedAuthority> mappedAuthorities = this.authoritiesMapper
                .mapAuthorities(oauth2User.getAuthorities());
        final OAuth2LoginAuthenticationToken authenticationResult = new OAuth2LoginAuthenticationToken(
                clientRegistration, oAuth2AuthorizationExchange,
                oauth2User, mappedAuthorities, oAuth2AccessToken, oAuth2RefreshToken);
        authenticationResult.setDetails(authenticationDetails);

        final OAuth2AuthenticationToken oauth2Authentication = new OAuth2AuthenticationToken(
                oauth2User, authenticationResult.getAuthorities(),
                authenticationResult.getClientRegistration().getRegistrationId());
        oauth2Authentication.setDetails(authenticationDetails);
        return oauth2Authentication;
    }

    public void setClientRegistrationRepository(final ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }
}
