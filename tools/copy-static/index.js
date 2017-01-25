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

/* eslint no-console: 0 */
const fs = require('fs-extra');
const path = require('path');
const config = require('../../.samples.config.json');

const rootDir = path.resolve(__dirname, '../../');

// When we have more frontend samples, expose option to switch out angular
// with a frontend of their choice.
const frontend = 'samples-js-angular-1';

const fromPath = `${rootDir}/node_modules/@okta/${frontend}/dist`;
const toPath = `${rootDir}/${config.server.staticDir}`;

console.log(`Copying ${frontend}\n  from ${fromPath}\n    to ${toPath}`);
fs.copySync(fromPath, toPath);
