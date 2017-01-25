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

/**
 * Simple test server that mocks requests to Okta.
 *
 * Basic flow:
 * 1. Send /mock/set request to prime the expected requests for the test. It
 *    should send a json array of objects representing the requests:
 *    - 'req' property to validate the next request against
 *    - 'res' property that will be sent back after validation passes
 * 2. Make requests to the test server. If 'req' validation passes, it will
 *    return a JSON response with a 'res' response body.
 * 3. Send /mock/done request to clear expectations, and return an error if
 *    any non-optional requests were not invoked.
 */

/* eslint no-param-reassign: 0, no-console:0 */

'use strict';

const http = require('http');

const config = require('../../../.samples.config.json');

let mocks = [];

function handleSetRequest(req, res) {
  const chunks = [];
  req.on('data', chunk => chunks.push(chunk));
  req.on('end', () => {
    mocks = JSON.parse(Buffer.concat(chunks).toString());
    res.end();
  });
}

function handleDoneRequest(req, res) {
  let body;
  const required = mocks.find(mock => !mock.optional);
  if (required) {
    body = `Missing required request: ${required.req.url}`;
    res.statusCode = 500;
  } else {
    res.statusCode = 200;
  }
  mocks = [];
  res.end(body);
}

function validateReq(expected, req) {
  Object.keys(expected).forEach((key) => {
    const expectedVal = expected[key];
    const reqVal = req[key];
    if (expectedVal instanceof Object) {
      validateReq(expectedVal, reqVal);
    } else if (expectedVal !== reqVal) {
      throw new Error(`Expected ${expectedVal}, but got ${reqVal}`);
    }
  });
}

function handleNextRequest(req, res) {
  let body;
  try {
    if (mocks.length === 0) {
      throw new Error(`Unexpected request: ${req.url}`);
    }
    const nextRequest = mocks.shift();
    if (req.url !== nextRequest.req.url && nextRequest.optional) {
      handleNextRequest(req, res);
      return;
    }
    validateReq(nextRequest.req, req);
    body = nextRequest.res;
  } catch (e) {
    res.statusCode = 500;
    body = {
      error: 'Expectation not met',
      error_description: e.message,
    };
  }
  res.setHeader('Content-Type', 'application/json');
  res.end(JSON.stringify(body, null, 2));
}

const server = http.createServer((req, res) => {
  switch (req.url) {
    case '/mock/set':
      return handleSetRequest(req, res);
    case '/mock/done':
      return handleDoneRequest(req, res);
    default:
      return handleNextRequest(req, res);
  }
});

server.listen(config.mockOkta.port, () => {
  console.log(`Test server listening on port ${config.mockOkta.port}`);
});
