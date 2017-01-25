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

import angular from 'angular';
import uiRouter from 'angular-ui-router';
import LoginRedirectComponent from './login-redirect.component';

const loginRedirect = angular
  .module('authorizationCode.loginRedirect', [uiRouter])
  .component('loginRedirect', LoginRedirectComponent)
  .config(($stateProvider, $urlRouterProvider) => {
    $stateProvider
      .state('authorization-code/login-redirect', {
        url: '/authorization-code/login-redirect',
        component: 'loginRedirect',
      });
    $urlRouterProvider.otherwise('/');
  })
  .name;

export default loginRedirect;
