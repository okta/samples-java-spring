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

import Controller from './profile.controller';

const ProfileComponent = {
  controller: Controller,
  template: `
    <div class="profile">
      <h2 class="ui icon header">
        <i class="hand peace icon"></i>
        <div class="content">
          Signed In
        </div>
      </h2>
      <table class="ui collapsing celled table inverted black">
        <thead>
          <tr>
            <th colspan="2">Some claims from the id_token</th>
          </tr>
        </thead>
        <tbody>
          <tr><td>email</td><td data-se="email">{{$ctrl.email}}</td></tr>
          <tr><td>exp</td><td>{{$ctrl.expFormatted}}</td></tr>
        </tbody>
      </table>
      <p>
        <button
          id="logout"
          data-se="logout-link"
          ng-click="$ctrl.logout()"
          class="ui grey icon button">
          <i class="sign out icon"></i>
          Sign out
        </button>
      </p>
    </div>
  `,
};

export default ProfileComponent;
