/*!
 * Copyright (c) 2015-2016, Okta, Inc. and/or its affiliates. All rights reserved.
 * The Okta software accompanied by this notice is provided pursuant to the Apache License, Version 2.0 (the "License.")
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

/* eslint prefer-template: 0 */
const config = require('../../../.samples.config.json').oktaSample;

exports.SERVER_PORT = `
      The app server is not running, or it is not running on
      port ${config.server.port}.

      Note: The app server is expected to run on port ${config.server.port}. If
      this is not possible in your framework, let DevEx know.
`;

exports.MISSING_FRONTEND_ASSET = `
      You are missing static assets from the installed frontend sample.

      Static files are requests to /assets/*, i.e. /assets/bundle.js. These
      files are copied in the "prestart" script to server.staticDir, which is
      configured in .samples.config.json.

      NOTE: It is important to map server.staticDir to /assets!

      To fix this:
      1. Verify that the "prestart" script in package.json copies the included
         sample frontend app from node_modules to server.staticDir
      2. Run "npm run prestart" to copy these files
      3. Configure your app server to serve all requests to /assets/* from
         server.staticDir
`;

exports.INVALID_TEMPLATE = `
      The best way to render the correct html is to use the included
      mustache templates, which are found in tools/templates/*.mustache.

      If rendering mustache templates is not supported in your framework,
      you'll need to copy the mustache template html to the supported
      format for your template rendering engine.

      Read more about mustache and how to add it to different languages here:
      https://mustache.github.io/
`;

exports.DOC_PARTIAL = `
      You are probably loading the wrong mustache template. To fix this, load
      the correct mustache template for this page.
`;

exports.CODE_COOKIES_MISSING = `
      The /authorization-code/callback endpoint should return status code 401 if
      no state or nonce cookies are sent.

      Validate that these cookies are set:
        - okta-oauth-nonce
        - okta-oauth-state
`;

exports.CODE_QUERY_STATE_MISSING = `
      The /authorization-code/callback endpoint should return status code 401 if
      no "state" query param is sent.

      Validate that the 'state' query parameter is present.
`;

exports.CODE_QUERY_CODE_MISSING = `
      The /authorization-code/callback endpoint should return status code 401 if
      no "code" query param is sent.

      Validate that the "code" query parameter is present.
`;

exports.CODE_TOKEN_INVALID_URL = `
      The /authorization-code/callback endpoint should make a request to the
      Okta token endpoint. The url should look EXACTLY like this - Note, the
      order of query params is important.

      ${config.mockOkta.proxy}:${config.mockOkta.port}/oauth2/v1/token
        ?grant_type=authorization_code
        &code=SOME_CODE
        &redirect_uri=http%3A%2F%2Flocalhost%3A3000%2Fauthorization-code%2Fcallback
`;

exports.CODE_TOKEN_INVALID_METHOD = `
      The /authorization-code/callback endpoint should make a request to the
      Okta token endpoint. The /token request should be a POST. To fix this,
      set the request method to POST.
`;

exports.CODE_TOKEN_INVALID_CONTENT_TYPE = `
      When making a request to the Okta token endpoint, please make sure 
      to set the content-type header correctly. To fix this, set this 
      header:

      content-type: application/x-www-form-urlencoded
`;

exports.CODE_TOKEN_INVALID_HEADER_ACCEPT = `
     When making a request to the Okta token endpoint, please make sure 
      to set the accept header correctly. To fix this, set this 
      header:

      accept: application/json
`;

exports.CODE_TOKEN_INVALID_HEADER_CONNECTION = `
      When making a request to the Okta token endpoint, please make sure 
      to set the connection header correctly. To fix this, set this 
      header:

      connection: close
`;

exports.CODE_TOKEN_INVALID_HEADER_CONTENT_LENGTH = `
      When making a request to the Okta token endpoint, please make sure 
      to set the content-length header correctly. To fix this, set this 
      header:

      content-length: 0
`;

exports.CODE_TOKEN_INVALID_AUTHORIZATION = `
      The /authorization-code/callback endpoint should make a request to the
      Okta token endpoint. The /token request should send the correct
      authorization header.

      1. Construct the secret: ENCODE_BASE_64(clientId:clientSecret)

         Where:
           clientId: ${config.oidc.clientId}
           clientSecret: ${config.oidc.clientSecret}

      2. Set the header:

         authorization: Basic {{secret}}
`;

exports.CODE_TOKEN_REDIRECT = `
      The /authorization-code/callback endpoint should redirect to the profile
      page when there are no errors with the /token response.

      Redirect to: http://localhost:${config.server.port}/authorization-code/profile
`;

exports.CODE_TOKEN_ERROR = `
      The /authorization-code/callback endpoint should return status code 401 if
      there is an error getting the token.
`;

exports.CODE_TOKEN_NO_ID_TOKEN = `
      The /authorization-code/callback endpoint should return status code 401 if
      there is no id_token in the response.
`;

exports.CODE_TOKEN_BAD_ID_TOKEN = `
      The /authorization-code/callback endpoint should return status code 401 if
      the id_token is malformed.
`;

exports.CODE_KEYS_INVALID_URL = `
      The /authorization-code/callback endpoint should make a request to
      /oauth2/v1/keys to get the public key used to validate the id_token
      signature.

      Read more about validating id_tokens here:
      http://developer.okta.com/docs/api/resources/oidc.html#validating-id-tokens
`;

exports.CODE_TOKEN_INVALID_SIG = `
      The /authorization-code/callback endpoint should return status code 401 if
      the id_token signature is invalid.

      Most languages have libraries that can validate JWTs for you - find the
      one that is common in your language or framework. If there is no JWT
      validation library, try searching for a JWS validation library.

      Some helpful resources:
      JWT.io - https://jwt.io/
      JWS (JSON Web Signature) - https://tools.ietf.org/html/rfc7515
      JWT (JSON Web Token) - https://tools.ietf.org/html/rfc7519
`;

exports.CODE_TOKEN_INVALID_ALG = `
      The /authorization-code/callback endpoint should return status code 401 if
      the id_token header is invalid.

      When verifying the id_token signature, use the algorithm for the kid
      specified in the /oauth2/v1/keys response. Do not trust the id_token
      header algorithm!

      Quick exploit - Swap an id_token header with alg: none
`;

exports.CODE_KEYS_CACHE = `
      The /authorization-code/callback endpoint should cache requests to
      /oauth2/v1/keys.

      Pseudo Code:
      1. Make token request
      2. Extract kid from id_token header
      3. If kid does not exist in cache, make /oauth2/v1/keys request. Cache
         this response.
`;

const DECODE_CLAIMS = `
      If you're not using a library to decode the JWT, validate claims like this:

      1. Split the returned id_token by "."
         > payload = returnedJson.split('.')

      2. The second element contains the encoded claims
         > encodedClaims = payload[1]

      3. Decode the encoded claims
         > claims = TO_JSON(DECODE_BASE_64(encodedClaims))
`;

exports.CODE_TOKEN_BAD_NONCE = `
      The /authorization-code/callback endpoint should return status code 401 if
      idtoken.nonce does not match the "okta-oauth-nonce" cookie
` + DECODE_CLAIMS;

exports.CODE_TOKEN_BAD_ISSUER = `
      The /authorization-code/callback endpoint should return status code 401 if
      id_token.iss does not match our okta server "${config.oidc.oktaUrl}"
` + DECODE_CLAIMS;

exports.CODE_TOKEN_BAD_AUD = `
      The /authorization-code/callback endpoint should return status code 401 if
      id_token.aud does not match our clientId "${config.oidc.clientId}"
` + DECODE_CLAIMS;

exports.CODE_TOKEN_EXPIRED = `
      The /authorization-code/callback endpoint should return status code 401 if
      id_token.exp is in the past
` + DECODE_CLAIMS;

exports.CODE_TOKEN_EXP_CLOCK_SKEW = `
      The /authorization-code/callback endpoint should not return status code
      401 if id_token.exp is within the clock skew window.

      When validating id_token.exp, allow for a 5 minute clock skew - this is
      useful when the app server time is out of sync with the client time.

      > MAX_CLOCK_SKEW = 300 // 5 minutes, in seconds
      > if (now - MAX_CLOCK_SKEW > claims.exp) return 401
` + DECODE_CLAIMS;

exports.CODE_TOKEN_IAT_FUTURE = `
      The /authorization-code/callback endpoint should return status code 401 if
      id_token.iat is in the future
` + DECODE_CLAIMS;

exports.CODE_TOKEN_IAT_CLOCK_SKEW = `
      The /authorization-code/callback endpoint should not return status code
      401 if id_token.iat is within the clock skew window.

      When validating id_token.iat, allow for a 5 minute clock skew - this is
      useful when the app server time is out of sync with the client time.

      > MAX_CLOCK_SKEW = 300 // 5 minutes, in seconds
      > if (claims.iat > now + MAX_CLOCK_SKEW) return 401
` + DECODE_CLAIMS;

exports.CODE_PROFILE_NO_SESSION = `
      The /authorization-code/profile page should redirect to / if there is
      no active session. To fix, redirect to "http://localhost:${config.server.port}/"
      if there is no active session.
`;

exports.CODE_PROFILE_NO_REDIRECT = `
      The /authorization-code/profile page should not redirect if there is an
      active user session. To fix, verify that you are creating a user session
      after receiving the id_token in the /callback request.
`;

exports.CODE_LOGOUT_SESSION = `
      The /authorization-code/logout page should kill the active user session.
      To fix, delete the user session when requests are made to this endpoint.
`;

exports.CODE_LOGOUT_REDIRECT = `
      The /authorization-code/logout page should redirect to /. To fix, redirect
      to "http://localhost:${config.server.port}/" after deleting the user session.
`;
