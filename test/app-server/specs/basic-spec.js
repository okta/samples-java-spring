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

const expect = require('chai').expect;
const util = require('../lib/util');
const errors = require('../lib/errors');
const config = require('../../../.samples.config.json');

describe('The Basics', () => {
  describe('General', () => {
    it(`runs the app server on port ${config.server.port}`, () => (
      util.get('/')
      .then(res => expect(res).to.have.status(200))
      .catch((err) => {
        if (err.code === 'ECONNREFUSED') {
          throw new Error(errors.SERVER_PORT);
        }
        throw err;
      })
    ));
  });

  describe('Frontend Static Assets', () => {
    const assetsToCheck = [
      'bundle.js',
      'css/okta-sign-in.min.css',
      'css/okta-theme.css',
      'font/montserrat-light-webfont.woff',
    ];
    assetsToCheck.forEach((asset) => {
      it(`serves ${asset}`, () => (
        util.get(`/${asset}`)
        .then(res => expect(res).to.have.status(200))
        .catch((err) => {
          throw new Error(err.message + errors.MISSING_FRONTEND_ASSET);
        })
      ));
    });
  });
});
