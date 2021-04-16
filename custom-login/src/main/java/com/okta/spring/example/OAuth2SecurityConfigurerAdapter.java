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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

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

                .and().addFilterBefore(myFilter(), OAuth2LoginAuthenticationFilter.class).authorizeRequests()
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
    public CustomAuthenticationProcessingFilter myFilter() throws Exception {
        CustomAuthenticationProcessingFilter myFilter =
                new CustomAuthenticationProcessingFilter("/authorization-code/callback", authenticationManagerBean());
        myFilter.setAuthenticationDetailsSource(new WebAuthenticationDetailsSource());
        myFilter.setClientRegistrationRepository(clientRegistrationRepository);
        return myFilter;
    }
}
