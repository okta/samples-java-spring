/*
 * Copyright 2017 Okta, Inc.
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
package com.okta.spring.example.controllers;

import com.okta.commons.lang.Assert;
import com.okta.idx.sdk.api.client.Clients;
import com.okta.idx.sdk.api.client.IDXClient;
import com.okta.idx.sdk.api.exception.ProcessingException;
import com.okta.idx.sdk.api.model.AuthenticationOptions;
import com.okta.idx.sdk.api.model.IDXClientContext;
import com.okta.idx.sdk.api.model.RecoverPasswordOptions;
import com.okta.idx.sdk.api.response.AuthenticationResponse;
import com.okta.idx.sdk.api.wrapper.AuthenticationWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Controller
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private static final IDXClient client = Clients.builder().build();

    @GetMapping(value = "/custom-login")
    public ModelAndView getLogin() {
        return new ModelAndView("login");
    }

    @PostMapping("/custom-login")
    public ModelAndView postLogin(@RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                      HttpSession httpSession) {

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        ModelAndView mav = new ModelAndView("home");
        IDXClientContext clientContext;
        String interactionHandle;

        try {
            clientContext = client.interact();
            interactionHandle = clientContext.getInteractionHandle();
            Assert.hasText(clientContext.getInteractionHandle(), "Missing interaction handle");
        } catch (ProcessingException e) {
            logger.error("Exception occurred while trying to invoke interact API:", e);
            List<String> errors = new LinkedList<>();
            Arrays.stream(e.getErrorResponse().getMessages().getValue()).forEach(msg -> errors.add(msg.getMessage()));
            authenticationResponse.setErrors(errors);
            return mav;
        } catch (IllegalArgumentException e) {
            authenticationResponse.addError(e.getMessage());
            return mav;
        }

        authenticationResponse = AuthenticationWrapper.authenticate(client, new AuthenticationOptions(username, password));

        logger.info("Stored interaction handle {} in http session", interactionHandle);
        httpSession.setAttribute("interactionHandle", interactionHandle);
        mav.addObject("authenticationResponse", authenticationResponse);
        return mav;
    }

    @GetMapping("/forgot-password")
    public ModelAndView getForgotPassword() {
        return new ModelAndView("forgotpassword");
    }

    @PostMapping("/forgot-password")
    public ModelAndView postForgotPassword(RecoverPasswordOptions recoverPasswordOptions) {
        logger.info(":: Forgot Password ::");

        AuthenticationResponse authenticationResponse = AuthenticationWrapper.recoverPassword(client, recoverPasswordOptions);

       return null;
    }

    @GetMapping("/403")
    public String error403() {
        return "403";
    }
}