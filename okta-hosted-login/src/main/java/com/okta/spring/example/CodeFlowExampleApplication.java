package com.okta.spring.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class CodeFlowExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeFlowExampleApplication.class, args);
    }

    /**
     * The default Spring logout behavior redirects a user back to {code}/login?logout{code}, so you will likely want
     * to change that.
     */
    @Configuration
    static class WebConfig {

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

            http.authorizeHttpRequests((requests) -> requests
                            .requestMatchers("/").permitAll()
                            .anyRequest().authenticated()
                    )
                    .logout().logoutSuccessUrl("/")
                    .and()
                    .oauth2Client()
                    .and()
                    .oauth2Login();

            return http.build();
        }
    }

    /**
     * This example controller has endpoints for displaying the user profile info on {code}/{code} and "you have been
     * logged out page" on {code}/post-logout{code}.
     */
    @Controller
    public class ExampleController {

        @GetMapping("/")
        public String home() {
            return "home";
        }

        @GetMapping("/profile")
        @PreAuthorize("hasAuthority('SCOPE_profile')")
        public ModelAndView userDetails(OAuth2AuthenticationToken authentication) {
            return new ModelAndView("userProfile" , Collections.singletonMap("details", authentication.getPrincipal().getAttributes()));
        }
    }
}
