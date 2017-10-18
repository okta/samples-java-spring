package com.okta.samples;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.Map;
import java.util.HashMap;

import static java.lang.System.out;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@EnableAutoConfiguration
@SpringBootApplication
@EnableOAuth2Sso
public class Application extends WebSecurityConfigurerAdapter {

    // GLOBALS
    private OktaConfig CONFIG = getConfig();
    private User user = new User();
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
        .antMatcher("/**")
        .authorizeRequests()
          .antMatchers("/", "/login**", "/webjars/**", "/assets/**","/authorization-code/login**")
          .permitAll()
        .anyRequest()
          .authenticated();
    }

    @RequestMapping("/")
    public String index(OAuth2Authentication authentication, Map<String, Object> model, HttpServletResponse response, HttpServletRequest request) {
        if (authentication != null) {
            Map<String, Object> details = (Map<String, Object>) authentication.getUserAuthentication().getDetails();
            for (Object key : details.values()) {
                System.out.println(key);    
            }
            user.setEmail(details.get("email").toString());
            user.setGivenName(details.get("given_name").toString());
            user.setFamilyName(details.get("family_name").toString());
            user.setSubject(details.get("sub").toString());
            user.setZoneInfo(details.get("zoneinfo").toString());
            user.setLocale(details.get("locale").toString());
            
            System.out.println("Greetings from Spring Boot, " + authentication.getName());
            
            try {
                response.sendRedirect("/authorization-code/profile");
                return null;
            } catch (IOException e) {
                return send401(response, e.getMessage());
            }
        }

        model.put("config", CONFIG.getOktaSample());
        return "overview";
    }

    @RequestMapping(value = "/authorization-code/login-redirect", method = GET)
    public String loginRedirect(Map<String, Object> model) {
        model.put("user", user.toDict());
        model.put("config", CONFIG.getOktaSample());
        return "login-redirect";
    }

    @RequestMapping(value = "/authorization-code/login-custom", method = GET)
    public String loginCustom(Map<String, Object> model) {
        model.put("user", user.toDict());
        model.put("config", CONFIG.getOktaSample());
        return "login-custom";
    }

    @RequestMapping(value = "/authorization-code/profile", method = GET)
    public String profile(Map<String, Object> model, HttpServletResponse response, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            // No session - redirect
            try {
                response.sendRedirect("/");
                return null;
            } catch (IOException e) {
                return send401(response, e.getMessage());
            }
        }

        model.put("user", user.toDict());
        model.put("config", CONFIG.getOktaSample());
        return "profile";
    }


    private String send401(HttpServletResponse resp, String message) {
        try {
            resp.sendError(401, message);
            return null; // Let Spring MVC know response was already sent
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @RequestMapping("authorization-code/logout")
     public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        user = new User();
        return "redirect:/";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity missingRequestParams(MissingServletRequestParameterException ex) {
        String param = ex.getParameterName();
        return ResponseEntity
                .status(401)
                .body("Missing required parameter: {{ " + param + " }}");
    }

    private OktaConfig getConfig() {
        String path = System.getProperty("user.dir") + "/.samples.config.json";
        try {
            // Import config file
            ObjectMapper map = new ObjectMapper();
            return map.readValue(new File(path), OktaConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to read configuration");
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        out.println("Server Started");
    }
}
