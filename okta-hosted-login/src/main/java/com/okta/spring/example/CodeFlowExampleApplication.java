package com.okta.spring.example;

import com.okta.spring.boot.oauth.config.OktaOAuth2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class CodeFlowExampleApplication {

    private final Logger logger = LoggerFactory.getLogger(CodeFlowExampleApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CodeFlowExampleApplication.class, args);
    }

    /**
     * The default Spring logout behavior redirects a user back to {code}/login?logout{code}, so you will likely want
     * to change that.  The easiest way to do this is by extending from {@link WebSecurityConfigurerAdapter}.
     */
    @Configuration
    static class WebConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    // allow antonymous access to the root page
                    .antMatchers("/", "/enroll*").permitAll()
                    // all other requests
                    .anyRequest().authenticated()

                // set logout URL
                .and().logout().logoutSuccessUrl("/")

                // enable OAuth2/OIDC
                .and().oauth2Client()
                .and().oauth2Login();
        }

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    /**
     * This example controller has endpoints for displaying the user profile info on {code}/{code} and "you have been
     * logged out page" on {code}/post-logout{code}.
     */
    @Controller
    public class ExampleController {

        @Autowired
        private RestTemplate restTemplate;

        @Autowired
        private OktaOAuth2Properties oktaOAuth2Properties;

        @GetMapping("/")
        public String home() {
            return "home";
        }

        @GetMapping("/profile")
        @PreAuthorize("hasAuthority('SCOPE_profile')")
        public ModelAndView userDetails(OAuth2AuthenticationToken authentication) {
            return new ModelAndView("userProfile" , Collections.singletonMap("details", authentication.getPrincipal().getAttributes()));
        }

        @GetMapping("/enroll")
        @PreAuthorize("hasAuthority('SCOPE_profile')")
        public ModelAndView enroll(OAuth2AuthenticationToken authentication) throws UnsupportedEncodingException {

            logger.info("Enrolling Authenticator for {}", authentication.getPrincipal().getName());

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(oktaOAuth2Properties.getIssuer() + "/v1/authorize?");
            stringBuffer.append("response_type=" + "none");
            stringBuffer.append("&");
            stringBuffer.append("state=" + "state12345");
            stringBuffer.append("&");
            stringBuffer.append("client_id=" + oktaOAuth2Properties.getClientId());
            stringBuffer.append("&");
            stringBuffer.append("acr_values=" + oktaOAuth2Properties.getAcrValues());
            stringBuffer.append("&");
            stringBuffer.append("enroll_amr_values=" + URLEncoder.encode(oktaOAuth2Properties.getEnrollAmrValues(), "UTF-8"));
            stringBuffer.append("&");
            stringBuffer.append("prompt=" + "enroll_authenticator");
            stringBuffer.append("&");
            stringBuffer.append("max_age=" + "0");
            stringBuffer.append("&");
            stringBuffer.append("redirect_uri=" + oktaOAuth2Properties.getEnrollmentCallbackUri());

            logger.info("Sending Authorize request to: {}", stringBuffer);

            ResponseEntity<String> responseEntity = restTemplate.getForEntity(stringBuffer.toString(), String.class);

            logger.info("Authorization result HTTP status code: {}", responseEntity.getStatusCodeValue());

            return new ModelAndView("userProfile" , Collections.singletonMap("details", authentication.getPrincipal().getAttributes()));
        }

        @GetMapping("/enroll-callback")
        public ModelAndView handleEnrollCallback(HttpServletRequest request) {
            logger.info("Enroll Callback Received: {}", request.getQueryString());
            return new ModelAndView("home");
        }
    }
}
