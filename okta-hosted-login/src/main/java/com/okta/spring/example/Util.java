package com.okta.spring.example;

import com.okta.commons.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    static boolean isRootOrgIssuer(String issuerUri) throws MalformedURLException {
        String uriPath = new URL(issuerUri).getPath();

        if (Strings.hasText(uriPath)) {
            String[] tokenizedUri = uriPath.substring(uriPath.indexOf("/")+1).split("/");

            if (tokenizedUri.length >= 2 &&
                    "oauth2".equals(tokenizedUri[0]) &&
                    Strings.hasText(tokenizedUri[1])) {
                logger.debug("The issuer URL: '{}' is an Okta custom authorization server", issuerUri);
                return false;
            }
        }

        logger.info("The issuer URL: '{}' is an Okta root/org authorization server", issuerUri);
        return true;
    }
}
