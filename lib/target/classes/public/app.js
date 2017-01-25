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
import AppComponent from './app.component';
import Components from './components';
import initDoc from './util/doc';

export function bootstrap(config) {
  initDoc();

  const containerEl = document.querySelector(config.container);
  angular.element(containerEl).html('<app></app>');

  const app = angular
    .module('app', [Components, uiRouter])
    .constant('config', config)
    .component('app', AppComponent)
    .config(($urlRouterProvider, $locationProvider) => {
      $locationProvider.html5Mode({ enabled: true, rewriteLinks: false });
      $urlRouterProvider.otherwise('/');
    })
    .name;

  angular.bootstrap(document, [app]);
}

export default bootstrap;
