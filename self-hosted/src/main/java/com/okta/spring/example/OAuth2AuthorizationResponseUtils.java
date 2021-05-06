/*
 * Copyright 2021-Present Okta, Inc.
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
package com.okta.spring.example;

import java.util.Map;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

class OAuth2AuthorizationResponseUtils {

    static MultiValueMap<String, String> toMultiMap(final Map<String, String[]> map) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>(map.size());
        map.forEach((key, values) -> {
            if (values.length > 0) {
                for (String value : values) {
                    params.add(key, value);
                }
            }
        });
        return params;
    }

    static OAuth2AuthorizationResponse convert(final MultiValueMap<String, String> request, final String redirectUri) {
        final String code = request.getFirst("interaction_code");
        final String errorCode = request.getFirst(OAuth2ParameterNames.ERROR);
        final String state = request.getFirst(OAuth2ParameterNames.STATE);
        if (StringUtils.hasText(code)) {
            return OAuth2AuthorizationResponse.success(code).redirectUri(redirectUri).state(state).build();
        }
        final String errorDescription = request.getFirst(OAuth2ParameterNames.ERROR_DESCRIPTION);
        final String errorUri = request.getFirst(OAuth2ParameterNames.ERROR_URI);

        return OAuth2AuthorizationResponse.error(errorCode)
                .redirectUri(redirectUri)
                .errorDescription(errorDescription)
                .errorUri(errorUri)
                .state(state)
                .build();
    }
}
