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

import com.okta.idx.sdk.api.client.IDXClient;
import com.okta.idx.sdk.api.exception.ProcessingException;
import com.okta.idx.sdk.api.model.IDXClientContext;
import com.okta.spring.boot.oauth.config.OktaOAuth2Properties;
import com.okta.spring.example.HostedLoginCodeFlowExampleApplication;
import com.okta.spring.example.PkceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private static final String STATE = "state";
    private static final String NONCE = "nonce";
    private static final String SCOPES = "scopes";
    private static final String OKTA_BASE_URL = "oktaBaseUrl";
    private static final String OKTA_CLIENT_ID = "oktaClientId";
    private static final String REDIRECT_URI = "redirectUri";
    private static final String ISSUER_URI = "issuerUri";

    private final OktaOAuth2Properties oktaOAuth2Properties;

    @Autowired
    private IDXClient client;

    public LoginController(OktaOAuth2Properties oktaOAuth2Properties) {
        this.oktaOAuth2Properties = oktaOAuth2Properties;
    }

    @GetMapping(value = "/custom-login")
    public ModelAndView login(HttpServletRequest request,
                              @RequestParam(name = "state", required = false) String state,
                              @RequestParam(name = "nonce") String nonce,
                              HttpSession session) throws MalformedURLException, NoSuchAlgorithmException, ProcessingException {

        IDXClientContext idxClientContext = client.interact();

        logger.info("== after interact === code verifier: {} code challenge: {}",
                idxClientContext.getCodeVerifier(), idxClientContext.getCodeChallenge());

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
        mav.addObject("interactionHandle", idxClientContext.getInteractionHandle());
        mav.addObject("codeVerifier", idxClientContext.getCodeVerifier());
        mav.addObject("codeChallenge", idxClientContext.getCodeChallenge());
        mav.addObject("codeChallengeMethod", PkceUtil.CODE_CHALLENGE_METHOD);

        // from ClientRegistration.redirectUriTemplate, if the template is change you must update this
        mav.addObject(REDIRECT_URI,
            request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
            request.getContextPath() + "/authorization-code/callback"
        );
        mav.addObject(ISSUER_URI, issuer);

        session.setAttribute("interactionHandle", idxClientContext.getInteractionHandle());
        session.setAttribute("codeVerifier", idxClientContext.getCodeVerifier());
        session.setAttribute("codeChallenge", idxClientContext.getCodeChallenge());
        return mav;
    }

    @GetMapping("/post-logout")
    public String logout() {
        return "logout";
    }

    @GetMapping("/403")
    public String error403() {
        return "403";
    }
}