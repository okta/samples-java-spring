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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

@Configuration
public class OAuth2SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .exceptionHandling()
                .accessDeniedHandler((req, res, e) -> res.sendRedirect("/403"))

                .and().addFilterBefore(customAuthenticationProcessingFilter(), OAuth2LoginAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/", "/custom-login", "/css/**").permitAll()
                .anyRequest().authenticated()

                // send the user back to the root page when they logout
                .and()
                .logout().logoutSuccessUrl("/")

                .and().oauth2Client()
                .and().oauth2Login().redirectionEndpoint()
                .baseUri("/authorization-code/callback*");
    }

    @Bean
    public CustomAuthenticationProcessingFilter customAuthenticationProcessingFilter() throws Exception {
        CustomAuthenticationProcessingFilter customAuthenticationProcessingFilter =
                new CustomAuthenticationProcessingFilter("/authorization-code/callback",
                        authenticationManagerBean());
        customAuthenticationProcessingFilter.setAuthenticationDetailsSource(new WebAuthenticationDetailsSource());
        customAuthenticationProcessingFilter.setClientRegistrationRepository(clientRegistrationRepository);
        return customAuthenticationProcessingFilter;
    }
}
