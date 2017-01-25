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

import Controller from './login-redirect.controller';

const LoginRedirectComponent = {
  controller: Controller,
  template: `
    <p>
      Click <strong>Login with Okta</strong> to redirect to your Okta org for
      authentication.
    </p>
    <table class="ui collapsing celled table compact inverted grey">
      <thead>
        <tr>
          <th colspan="2">If you're using the mock-okta server:</th>
        </tr>
      </thead>
      <tbody>
        <tr><td>User</td><td><strong>george</strong></td></tr>
        <tr><td>Pass</td><td><strong>Asdf1234</strong></td></tr>
      </tbody>
    </table>
    <p>
      <button
        id="login"
        data-se="login-link"
        class="ui icon button blue"
        ng-click="$ctrl.login()">
        <i class="sign in icon"></i>
        Login with Okta
      </button>
    </p>
  `,
};

export default LoginRedirectComponent;
