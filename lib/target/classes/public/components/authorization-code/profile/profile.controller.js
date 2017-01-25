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

import OktaAuth from '@okta/okta-auth-js/jquery';

class ProfileController {

  constructor(config) {
    this.user = config.user;
    this.oktaUrl = config.oktaUrl;
  }

  $onInit() {
    this.email = this.user.email;
    this.iat = this.user.iat;
    this.iatFormatted = new Date(this.user.iat * 1000);
    this.exp = this.user.exp;
    this.expFormatted = new Date(this.user.exp * 1000);
    this.authClient = new OktaAuth({ url: this.oktaUrl });
  }

  logout() {
    this.authClient.session.close().then(() => {
      window.location = '/authorization-code/logout';
    });
  }

}

export default ProfileController;
