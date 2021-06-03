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

import com.okta.idx.sdk.api.client.IDXAuthenticationWrapper;
import com.okta.idx.sdk.api.exception.ProcessingException;
import com.okta.idx.sdk.api.model.IDXClientContext;
import com.okta.idx.sdk.api.response.ErrorResponse;
import com.okta.spring.boot.oauth.config.OktaOAuth2Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

@Controller
public class LoginController {

    private static final String STATE = "state";
    private static final String NONCE = "nonce";
    private static final String SCOPES = "scopes";
    private static final String OKTA_BASE_URL = "oktaBaseUrl";
    private static final String OKTA_CLIENT_ID = "oktaClientId";
    private static final String REDIRECT_URI = "redirectUri";
    private static final String ISSUER_URI = "issuerUri";
    /* idx related properties */
    private static final String INTERACTION_HANDLE = "interactionHandle";
    private static final String CODE_VERIFIER = "codeVerifier";
    private static final String CODE_CHALLENGE = "codeChallenge";
    private static final String CODE_CHALLENGE_METHOD = "codeChallengeMethod";
    private static final String CODE_CHALLENGE_METHOD_VALUE = "S256";
    private static final String IDX_CLIENT_CONTEXT = "idxClientContext";

    private final OktaOAuth2Properties oktaOAuth2Properties;

    @Autowired
    private IDXAuthenticationWrapper idxAuthenticationWrapper;

    private IDXClientContext idxClientContext;

    public LoginController(OktaOAuth2Properties oktaOAuth2Properties) {
        this.oktaOAuth2Properties = oktaOAuth2Properties;
    }

    @GetMapping(value = "/custom-login")
    public ModelAndView login(HttpServletRequest request,
                              @RequestParam(name = "state", required = false) String state,
                              @RequestParam(name = "nonce") String nonce,
                              HttpSession session) throws MalformedURLException, NoSuchAlgorithmException {

        if (session.getAttribute(IDX_CLIENT_CONTEXT) == null) {
            try {
                idxClientContext = idxAuthenticationWrapper.getClientContext();
            } catch (ProcessingException e) {
                ModelAndView modelAndView = new ModelAndView("error");
                ErrorResponse errorResponse = e.getErrorResponse();
                if (errorResponse != null) {
                    modelAndView.addObject("errorDetails",
                            errorResponse.getError() + "," + errorResponse.getErrorDescription());
                } else {
                    modelAndView.addObject("errorDetails", "Unknown error");
                }
                return modelAndView;
            }
            session.setAttribute(IDX_CLIENT_CONTEXT, idxClientContext);
        }

        if (idxClientContext == null) {
            ModelAndView modelAndView = new ModelAndView("error");
            modelAndView.addObject("error_details", "Unknown error");
            return modelAndView;
        }

        // if we don't have the state parameter redirect
        if (state == null) {
            return new ModelAndView("redirect:" + oktaOAuth2Properties.getRedirectUri());
        }

        String issuer = oktaOAuth2Properties.getIssuer();
        // the widget needs the base url, just grab the root of the issuer
        String orgUrl = new URL(new URL(issuer), "/").toString();

        ModelAndView mav = new ModelAndView("login");
        mav.addObject(STATE, state);
        mav.addObject(NONCE, nonce);
        mav.addObject(SCOPES, oktaOAuth2Properties.getScopes());
        mav.addObject(OKTA_BASE_URL, orgUrl);
        mav.addObject(OKTA_CLIENT_ID, oktaOAuth2Properties.getClientId());
        mav.addObject(INTERACTION_HANDLE, idxClientContext.getInteractionHandle());
        mav.addObject(CODE_VERIFIER, idxClientContext.getCodeVerifier());
        mav.addObject(CODE_CHALLENGE, idxClientContext.getCodeChallenge());
        mav.addObject(CODE_CHALLENGE_METHOD, CODE_CHALLENGE_METHOD_VALUE);

        // from ClientRegistration.redirectUriTemplate, if the template is change you must update this
        mav.addObject(REDIRECT_URI,
            request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
            request.getContextPath() + "/authorization-code/callback"
        );
        mav.addObject(ISSUER_URI, issuer);

        session.setAttribute(CODE_VERIFIER, idxClientContext.getCodeVerifier());
        return mav;
    }

    @GetMapping("/post-logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "logout";
    }

    @GetMapping("/403")
    public String error403() {
        return "403";
    }
}