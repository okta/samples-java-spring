package com.okta.spring.example.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

@Controller
public class UserDetailsController {
    @GetMapping("/profile")
    @PreAuthorize("#oauth2.hasScope('profile')")
    public ModelAndView userDetails(OAuth2Authentication authentication) {
        return new ModelAndView("userProfile" , Collections.singletonMap("details", authentication.getUserAuthentication().getDetails()));
    }
}