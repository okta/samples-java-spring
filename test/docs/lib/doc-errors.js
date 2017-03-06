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
exports.MISSING_README = `
      Readme did not get installed correctly. Please run the generator again.
`;

exports.MISSING_README_START_SCRIPT = `
      Replace {{ SAMPLE DEVELOPER: ADD START SCRIPT HERE }} in the README file with
      the script needed to boot up your server.

      Example:
      --------
      python3 lib/manage.py runserver 3000
      --------
`;

exports.MISSING_README_SETUP = `
      Replace {{ SAMPLE-DEVELOPER: ADD EXTRA SETUP HERE }} in the README file to
      include framework specific setup instructions. This includes installing framework
      dependencies, setting up a virtual environment, etc.

      Make sure this is styled with Markdown.

      Example:
      --------
      Create an isolated virtual environment
      Install [virtualenv](http://docs.python-guide.org/en/latest/dev/virtualenvs/) via pip:
      \`\`\`
      [samples-python-django]$ pip install virtualenv
      \`\`\`
      --------
`;

exports.MISSING_README_CHECK_COOKIES = `
      Replace {{ SAMPLE-DEVELOPER: ADD CHECKING FOR COOKIES HERE }} in the README file to
      show how to retrieve state and nonce cookies.

      Make sure this is styled with Markdown.

      Example:
      --------
      \`\`\`python
      # views.py

      if ('okta-oauth-state' in request.COOKIES and 'okta-oauth-nonce' in request.COOKIES):
          # Current AuthJS Cookie Setters
          state = request.COOKIES['okta-oauth-state']
          nonce = request.COOKIES['okta-oauth-nonce']
      else:
          return HttpResponse('Error setting and/or retrieving cookies', status=401)
      \`\`\`
      --------
`;

exports.MISSING_README_TOKEN_REQUEST = `
      Replace {{ SAMPLE-DEVELOPER: ADD TOKEN REQUEST CODE HERE }} in the README file to show
      how to construct the /token endpoint call.


      Make sure this is styled with Markdown.

      Example:
      --------
      \`\`\`python
      # openid.py

      def call_token_endpoint(url, code, config):
          """ Call /token endpoint
              Returns accessToken, idToken, or both
          """
          auth = HTTPBasicAuth(config['clientId'], config['clientSecret'])
          header = {
              'Content-Type': 'application/x-www-form-urlencoded',
              'Accept': 'application/json',
              'Connection': 'close'
          }

          params = 'grant_type=authorization_code&code={}&redirect_uri={}'.format(
              urllib.parse.quote_plus(code),
              urllib.parse.quote_plus(config['redirectUri'])
          )

          url_encoded = '{}{}'.format(url, params)

          # Send token request
          r = requests.post(url_encoded, auth=auth, headers=header)

          return r.json()

      \`\`\`
      --------
`;

exports.MISSING_README_LIBRARY_INFO = `
      Replace {{ SAMPLE-DEVELOPER: ADD TOKEN LIBRARY INFO HERE }} with information about
      the library you used.

      Example:
      --------
      [JSON Object Signing and Encryption (JOSE)](https://github.com/mpdavis/python-jose)
      --------
`;

exports.MISSING_README_JWKS = `
      Replace {{ SAMPLE-DEVELOPER: ADD JWKS AND CACHING CODE HERE }} in the README file to
      show how to request keys at the /keys endpoint. Show how to cache or retrieve the key.

      Make sure this is styled with Markdown.

      Example:
      --------
      \`\`\`python
      # tokens.py

      def fetch_jwk_for(id_token=None):
          unverified_header = jws.get_unverified_header(id_token)

          if 'kid' in unverified_header:
              key_id = unverified_header['kid']

          if key_id in settings.PUBLIC_KEY_CACHE:
              # If we've already cached this JWK, return it
              return settings.PUBLIC_KEY_CACHE[key_id]

          # If it's not in the cache, get the latest JWKS from /oauth2/v1/keys
          jwks = requests.get(jwks_uri).json()

          for key in jwks['keys']:
              jwk_id = key['kid']
              settings.PUBLIC_KEY_CACHE[jwk_id] = key

          if key_id in settings.PUBLIC_KEY_CACHE:
              return settings.PUBLIC_KEY_CACHE[key_id]
          else:
              # Error
      \`\`\`
      --------
`;

exports.MISSING_README_VERIFY_FIELDS = `
      Replace {{ SAMPLE-DEVELOPER: ADD VERIFY FIELDS CODE HERE }} in the README file to show
      how to validate the issuer, audience, and token expiration time.

      Make sure this is styled with Markdown.

      Example:
      --------
      \`\`\`python
      # tokens.py

      # A clock skew of five minutes is considered to account for
      # differences in server times
      clock_skew = 300

      jwks_with_public_key = fetch_jwk_for(tokens['id_token'])

      jwt_kwargs = {
          'algorithms': jwks_with_public_key['alg'],
          'options': {
              'verify_at_hash': False,
              # Used for leeway on the 'exp' claim
              'leeway': clock_skew
          },
          'issuer': okta_config.oidc['oktaUrl'],
          'audience': okta_config.oidc['clientId']
      }

      claims = jwt.decode(
          tokens['id_token'],
          jwks_with_public_key,
          **jwt_kwargs)
      \`\`\`
      --------
`;

exports.MISSING_README_VERIFY_IAT = `
      Replace {{ SAMPLE-DEVELOPER: ADD VERIFY IAT CODE HERE }} in the README file to show
      how to validate the issued time (iat) of the token, with some leeway for clock skew. 
      
      Make sure this is styled with Markdown.

      Example:
      --------
      \`\`\`python
      # tokens.py

      # Validate 'iat' claim
      plus_time_now_with_clock_skew = (datetime.utcnow() +
                                       timedelta(seconds=clock_skew))
      plus_acceptable_iat = calendar.timegm(
          (plus_time_now_with_clock_skew).timetuple())

      if 'iat' in claims and claims['iat'] > plus_acceptable_iat:
          return 'invalid iat claim', 401
      \`\`\`
      --------
`;

exports.MISSING_README_VERIFY_NONCE = `
      Replace {{ SAMPLE-DEVELOPER: ADD VERIFY NONCE CODE HERE }} in the README file to show
      how to verify the token nonce value matches the saved cookie nonce.

      Make sure this is styled with Markdown.

      Example:
      --------
      \`\`\`python
      # tokens.py
      if nonce != claims['nonce']:
          return 'invalid nonce', 401
      \`\`\`
      --------
`;

exports.MISSING_README_USER_SESSION = `
      Replace {{ SAMPLE-DEVELOPER: ADD SETTING USER SESSION CODE HERE }} in the README file to show how
      to set the user session in your framework.

      Make sure this is styled with Markdown.

      Example:
      --------
      \`\`\`python
      # views.py

      def validate_user(claims):
          # Create user for django session

          user = authenticate(
            # Authenticate user credentials
          )

          if user is None:
              # Create user
              
          return user
      \`\`\`
      --------
`;

exports.MISSING_README_LOGOUT = `
      Replace {{ SAMPLE-DEVELOPER: ADD LOGOUT CODE HERE }} in the README file to show how to
      log the user out of your framework's session.

      Make sure this is styled with Markdown.

      Example:
      --------
      \`\`\`python
      # views.py

      def logout_controller(request):
          # Log user out

          # Clear existing user
          user = User.objects.get(username=request.user).delete()
          logout(request)

          return redirect('/')
      \`\`\`
      --------
`;
