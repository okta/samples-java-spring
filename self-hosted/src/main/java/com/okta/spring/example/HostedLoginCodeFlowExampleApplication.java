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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.idx.sdk.api.client.IDXAuthenticationWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;

/**
 * This example renders a self-hosted login page (hosted within this application). You can use a standard login with less
 * code (if you don't need to customize the login page) see the 'basic' example at the root of this repository.
 */
@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class HostedLoginCodeFlowExampleApplication {

    private final Logger logger = LoggerFactory.getLogger(HostedLoginCodeFlowExampleApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(HostedLoginCodeFlowExampleApplication.class, args);
    }

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Value("${okta.oauth2.issuer}")
    private String issuer;

    @Value("${okta.oauth2.clientId}")
    private String clientId;

    @Value("${okta.oauth2.clientSecret}")
    private String clientSecret;

    @Value("${okta.idx.scopes}")
    private String scopes;

    @Value("${okta.oauth2.redirectUri}")
    private String redirectUri;

    /**
     * Create an ApplicationListener that listens for successful logins and simply just logs the principal name.
     * @return a new listener
     */
    @Bean
    protected ApplicationListener<AuthenticationSuccessEvent> authenticationSuccessEventApplicationListener() {
        return event -> logger.info("Authentication Success with principal: {}", event.getAuthentication().getPrincipal());
    }

    @Bean
    public IDXAuthenticationWrapper idxClient() {
        return new IDXAuthenticationWrapper(issuer, clientId, clientSecret,
                new HashSet<>(Arrays.asList(scopes.split(" "))),redirectUri);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public HelperUtil helperUtil() {
        return new HelperUtil(issuer, clientId, clientSecret, objectMapper(), restTemplate());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}