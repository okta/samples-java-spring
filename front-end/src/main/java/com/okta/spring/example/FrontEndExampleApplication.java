package com.okta.spring.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class FrontEndExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontEndExampleApplication.class, args);
    }

    @RestController
    static class WidgetConfigController {

        private final Environment environment;

        WidgetConfigController(Environment environment) {
            this.environment = environment;
        }

        @GetMapping(value = "config.js", produces = "application/javascript")
        String getConfig() {

            String issuer = environment.getRequiredProperty("okta.oauth2.issuer");
            String baseUrl = issuer.substring(0, issuer.indexOf("/oauth2"));
            String clientId = environment.getRequiredProperty("okta.oauth2.client-id");

            // okta widget configuration
            return
            "window.oktaSignIn = new OktaSignIn( {\n" +
            "  baseUrl: '" + baseUrl + "',\n" +
            "  clientId: '" + clientId + "',\n" +
            "  redirectUri: window.location.href,\n" +
            "  authParams: {\n" +
            "    issuer: '" + issuer + "',\n" +
            "    responseType: ['id_token', 'token'],\n" +
            "    scopes: ['openid', 'profile', 'email']\n" +
            "  }\n" +
            "});\n" +
            // the baseUrl of the resource server
            "window.resourceServerBaseUrl = 'http://localhost:8000';\n";
        }
    }
}