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

'use strict';

const chai = require('chai');
const chaiHttp = require('chai-http');
const config = require('../../../.samples.config.json').oktaSample;
const errors = require('./errors');

const expect = chai.expect;
const util = module.exports = {};
const baseAppUrl = `http://localhost:${config.server.port}`;
const baseMockOktaUrl = `http://127.0.0.1:${config.mockOkta.port}`;

chai.use(chaiHttp);

/**
 * Simple helper function that wraps chaiHttp's request method.
 */
util.request = () => chai.request(baseAppUrl);

/**
 * Gets chai agent, which is used to start a chain of requests where cookies
 * are preserved.
 */
util.agent = () => chai.request.agent(baseAppUrl);

/**
 * Convenience method for GET requests that do not require setting special
 * headers or cookie state.
 */
util.get = path => util.request().get(path).send();

/**
 * Sends a /mock/set request to the test mock-server to set expectations for the
 * next request.
 */
util.mockOktaRequest = reqs => (
  chai.request(baseMockOktaUrl).post('/mock/set').send(reqs)
);

util.mockVerify = () => (
  chai.request(baseMockOktaUrl).post('/mock/done').send()
);

/**
 * Helper function to construct nested objects.
 *
 * Example:
 * util.expand('a.b.c', 3) -> { a: { b: c: 3 } }
 */
util.expand = (key, val) => {
  const parts = key.split('.');
  const obj = {};
  let cursor = obj;
  parts.forEach((part, i) => {
    cursor[part] = i === parts.length - 1 ? val : {};
    cursor = cursor[part];
  });
  return obj;
};

/**
 * Verifies that the response sets a 401 status code
 */
util.should401 = (reqPromise, msg) => {
  // Handler for "success" responses - since we are expecting a 401, this will
  // always throw an error.
  function success() {
    throw new Error(msg);
  }

  // This is the expected response - additionally, we also expect that the
  // statusCode is 401.
  function fail(err) {
    try {
      expect(err).to.have.status(401);
    } catch (e) {
      throw new Error(`${e.message}\n${msg}`);
    }
  }

  return reqPromise.then(success, fail);
};

/**
 * Verifies that the response does not send an error code
 */
util.shouldNotError = (reqPromise, msg) => (
  reqPromise.catch((err) => {
    throw new Error(`${err.message}. ${err.response.text}\n${msg}`);
  })
);

/**
 * Verifies that the response redirects to the given redirectUri
 */
util.shouldRedirect = (reqPromise, redirectUri, msg) => (
  reqPromise
  .then((res) => {
    expect(res).redirectTo(redirectUri);
  })
  .catch((err) => {
    throw new Error(`${err.message}\n${msg}`);
  })
);

/**
 * Verifies that the response does not redirect
 */
util.shouldNotRedirect = (reqPromise, msg) => (
  reqPromise
  .then(res => expect(res).to.not.redirect)
  .catch((err) => {
    throw new Error(`${err.message}\n${msg}`);
  })
);

/**
 * Validates that the given route returns an html response that matches the
 * mustache template.
 *
 * Note: This is intentionally loose - in some frameworks it might not be
 * possible (or desired) to use the template - in that case, we only check that
 * the response matches the minimum to serve the frontend assets.
 */
util.itLoadsTemplateFor = (docPartial, reqFn) => {
  function hasBodyText(text) {
    return reqFn()
    .then(res => expect(res.text).to.contain(text))
    .catch(() => {
      const err = `Expected response to contain ${text}`;
      throw new Error(`${err}\n${errors.INVALID_TEMPLATE}`);
    });
  }

  it('returns status code 200', () => (
    reqFn().then(res => expect(res).to.have.status(200))
  ));
  it('is html', () => (
    reqFn().then(res => expect(res).to.be.html)
  ));
  it('loads sign-in css', () => (
    hasBodyText('<link href="/assets/css/okta-sign-in.min.css" type="text/css" rel="stylesheet"/>')
  ));
  it('loads theme css', () => (
    hasBodyText('<link href="/assets/css/okta-theme.css" type="text/css" rel="stylesheet"/>')
  ));
  it('sets base anchor for frameworks like angular', () => (
    hasBodyText('<base href="/"/>')
  ));
  it('loads javascript bundle', () => (
    hasBodyText('<script src="/assets/bundle.js"></script>')
  ));
  it('runs bootstrap', () => (
    hasBodyText('bundle.bootstrap(')
  ));
  it('includes the correct template', () => (
    hasBodyText(`class="doc-${docPartial}"`).catch(() => {
      const err = `Expected tools/templates/${docPartial}.mustache to be loaded`;
      throw new Error(`${err}\n${errors.DOC_PARTIAL}`);
    })
  ));
};
