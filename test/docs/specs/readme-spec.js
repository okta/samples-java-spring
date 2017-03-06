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

const chai = require('chai');
const chaiFiles = require('chai-files');
const errors = require('../lib/doc-errors');
const path = require('path');

chai.use(chaiFiles);

const expect = chai.expect;
const file = chaiFiles.file;

const readmePath = path.join(__dirname, '../../../README.md');

/**
 * Verifies that the README contains specific text
 */
function checkContains(str) {
  expect(file(readmePath)).to.not.contain(str);
}

/**
 * Test to check for specific content in README
 */
function expectReplaced(tag, error) {
  try {
    checkContains(tag);
  } catch (e) {
    throw new Error(e.message + error);
  }
}

describe('Has README Components', () => {
  describe('Has a README', () => {
    it('README file exists in the sample project', () => {
      try {
        return expect(file(readmePath)).to.exist;
      } catch (e) {
        throw new Error(e.message + errors.MISSING_README);
      }
    });
  });

  describe('Start Script', () => {
    it('contains start script in README', () => {
      const tag = '{{ SAMPLE-DEVELOPER: ADD START SCRIPT HERE }}';
      expectReplaced(tag, errors.MISSING_README_START_SCRIPT);
    });
  });

  describe('Setup', () => {
    it('contains additional setup instructions', () => {
      const tag = '{{ SAMPLE-DEVELOPER: ADD EXTRA SETUP HERE }}';
      expectReplaced(tag, errors.MISSING_README_SETUP);
    });
  });

  describe('Cookies', () => {
    it('contains code snippet to get cookies', () => {
      const tag = '{{ SAMPLE-DEVELOPER: ADD CHECKING FOR COOKIES HERE }}';
      expectReplaced(tag, errors.MISSING_README_CHECK_COOKIES);
    });
  });

  describe('Token Request', () => {
    it('contains code snippet for creating the token request', () => {
      const tag = '{{ SAMPLE-DEVELOPER: ADD TOKEN REQUEST CODE HERE }}';
      expectReplaced(tag, errors.MISSING_README_TOKEN_REQUEST);
    });
  });

  describe('JWS Library Info', () => {
    it('contains information about the JWS/JOSE token library used', () => {
      const tag = '{{ SAMPLE-DEVELOPER: ADD TOKEN LIBRARY INFO HERE }}';
      expectReplaced(tag, errors.MISSING_LIBRARY_INFO);
    });
  });

  describe('JWKS Request and Cache', () => {
    it('contains code snippet for requesting jwks and cache key', () => {
      const tag = '{{ SAMPLE-DEVELOPER: ADD JWKS AND CACHING CODE HERE }}';
      expectReplaced(tag, errors.MISSING_README_JWKS);
    });
  });

  describe('Verify Token Fields', () => {
    it('contains code snippet for checking unverified token fields', () => {
      const tag = '{{ SAMPLE-DEVELOPER: ADD VERIFY FIELDS CODE HERE }}';
      expectReplaced(tag, errors.MISSING_README_VERIFY_FIELDS);
    });
  });

  describe('Verify IAT', () => {
    it('contains code snippet for checking issued at time', () => {
      const tag = '{{ SAMPLE-DEVELOPER: ADD VERIFY IAT CODE HERE }}';
      expectReplaced(tag, errors.MISSING_README_VERIFY_IAT);
    });
  });

  describe('Verify Nonce', () => {
    it('contains code snippet for checking nonce values', () => {
      const tag = '{{ SAMPLE-DEVELOPER: ADD VERIFY NONCE CODE HERE }}';
      expectReplaced(tag, errors.MISSING_README_VERIFY_NONCE);
    });
  });

  describe('Set User Session', () => {
    it('contains code snippet for setting user session', () => {
      const tag = '{{ SAMPLE-DEVELOPER: ADD SETTING USER SESSION CODE HERE }}';
      expectReplaced(tag, errors.MISSING_README_USER_SESSION);
    });
  });

  describe('Logout', () => {
    it('contains code snippet for logging the user out', () => {
      const tag = '{{ SAMPLE-DEVELOPER: ADD LOGOUT CODE HERE }}';
      expectReplaced(tag, errors.MISSING_README_LOGOUT);
    });
  });
});
