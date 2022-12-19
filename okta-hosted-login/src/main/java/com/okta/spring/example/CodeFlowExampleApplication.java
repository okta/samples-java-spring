package com.okta.spring.example;

import com.okta.spring.boot.oauth.config.OktaOAuth2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
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

        @Value("${enrollmentCallbackUri}")
        private String enrollmentCallbackUri;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    // allow anonymous access to the root page and callback url
                    .antMatchers("/", enrollmentCallbackUri).permitAll()
                    // all other requests
                    .anyRequest().authenticated()

                    // set logout URL
                    .and().logout().logoutSuccessUrl("/")

                    // enable OAuth2/OIDC
                    .and().oauth2Client()
                    .and().oauth2Login();

            http.oauth2ResourceServer().opaqueToken();
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
        private OktaOAuth2Properties oktaOAuth2Properties;

        @Value("${acrValues}")
        private String acrValues;

        @Value("${enrollAmrValues}")
        private String enrollAmrValues;

        @Value("${enrollmentCallbackUri}")
        private String enrollmentCallbackUri;

        @GetMapping("/")
        public String home() {
            return "home";
        }

        @GetMapping("/profile")
        @PreAuthorize("hasAuthority('SCOPE_profile')")
        public ModelAndView userDetails(OAuth2AuthenticationToken authentication) {
            return new ModelAndView("userProfile", Collections.singletonMap("details", authentication.getPrincipal().getAttributes()));
        }

        @GetMapping("/enroll")
        @PreAuthorize("hasAuthority('SCOPE_profile')")
        public RedirectView enroll(OAuth2AuthenticationToken authentication) throws MalformedURLException {

            logger.info("Enrolling Authenticator for {}", authentication.getPrincipal().getName());

            String redirectUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .replacePath(enrollmentCallbackUri).build().toUriString();

            String state = "state12345";

            String pathSegment;

            if (Util.isRootOrgIssuer(oktaOAuth2Properties.getIssuer())) {
                pathSegment = "/oauth2/v1/authorize";
            } else {
                pathSegment = "/v1/authorize";
            }

            String uri = UriComponentsBuilder.fromUriString(oktaOAuth2Properties.getIssuer())
                    .pathSegment(pathSegment)
                    .queryParam(OAuth2ParameterNames.RESPONSE_TYPE, "none")
                    .queryParam(OAuth2ParameterNames.CLIENT_ID, oktaOAuth2Properties.getClientId())
                    .queryParam("acr_values", acrValues)
                    .queryParam("enroll_amr_values", enrollAmrValues)
                    .queryParam("prompt", "enroll_authenticator")
                    .queryParam(OAuth2ParameterNames.STATE, state)
                    .queryParam("max_age", 0)
                    .queryParam(OAuth2ParameterNames.REDIRECT_URI, redirectUri)
                    .build()
                    .toUriString();

            logger.info("Authorize request URL: {}", uri);

            return new RedirectView(uri);
        }

        @GetMapping("${enrollmentCallbackUri}")
        public ModelAndView handleEnrollCallback(HttpServletRequest request) {
            logger.info("Enroll callback received with query string: {}", request.getQueryString());
            return new ModelAndView("home");
        }
    }
}
