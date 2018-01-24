package com.okta.spring.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@EnableResourceServer
@SpringBootApplication
public class ResourceServerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServerExampleApplication.class, args);
    }

    @EnableGlobalMethodSecurity(prePostEnabled = true)
    protected static class GlobalSecurityConfiguration extends GlobalMethodSecurityConfiguration {
        @Override
        protected MethodSecurityExpressionHandler createExpressionHandler() {
            return new OAuth2MethodSecurityExpressionHandler();
        }
    }

    @Configuration
    @Order(0)
    static class ResourceSecurityConfigurer extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                    .anyRequest().authenticated();
        }
    }

    @RestController
    @CrossOrigin(origins = "http://localhost:8080")
    public class MessageOfTheDayController {

        @GetMapping("/api/userProfile")
        @PreAuthorize("#oauth2.hasScope('profile')")
        public Map<String, Object> getUserDetails(OAuth2Authentication authentication) {
            return (Map<String, Object>) authentication.getUserAuthentication().getDetails();
        }

        @GetMapping("/api/messages")
        @PreAuthorize("#oauth2.hasScope('email')")
        public Map<String, Object> messages() {

            Map<String, Object> result = new HashMap<>();
            result.put("messages", Arrays.asList(
                    new Message("I am a robot."),
                    new Message("Hello, word!")
            ));

            return result;
        }
    }

    class Message {
        public Date date = new Date();
        public String text;

        Message(String text) {
            this.text = text;
        }
    }
}