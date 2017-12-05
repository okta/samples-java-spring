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

/* eslint no-console:0 */

const Monitor = require('forever-monitor').Monitor;
const waitOn = require('wait-on');
const chalk = require('chalk');

const daemonUtil = module.exports;

function startNpmScript(script, color) {
  const child = new Monitor(script, {
    command: 'npm run',
    max: 1,
    silent: true
  });

  const normal = chalk[color || 'yellow'];
  const bold = normal.bold.underline;

  child.on('stdout', (msg) => {
    console.log(normal(msg));
  });

  child.on('stderr', (msg) => {
    console.log(bold(`Failed to start "npm run ${script}"`));
    console.log(bold(msg));
  });

  child.on('exit', () => {
    console.log(normal(`Finished "npm run ${script}"`));
  });

  return new Promise((resolve, reject) => {
    child.on('start', () => {
      resolve(child);
    });
    console.log(normal(`Running "npm run ${script}"`));
    child.start();
  });
}

function waitOnPromise(opts, context) {
  const normal = chalk[context.color || 'yellow'];
  const bold = normal.bold.underline;

  return new Promise((resolve, reject) => {
    const resourceJSON = JSON.stringify(opts.resources, null, 2);
    waitOn(opts, function (err) {
      if (err) {
        console.log(bold(`Failed while waiting for "npm run ${context.script}"`));
        console.log(bold(`Waiting for ${resourceJSON}`));
        console.log(bold(msg));
        return reject(err);
      }
      console.log(normal(`The following resources are available after "npm run ${context.script}":`));
      console.log(normal(resourceJSON));
      resolve();
    });
  });
}

function startAndWait(opts) {
  return startNpmScript(opts.script, opts.color)
  .then(child => waitOnPromise({
    resources: opts.resources
  }, opts)
  .then(() => child));
}

daemonUtil.startAppServer = () => {
  return startAndWait({
    script: 'start-basic-server',
    color: 'green',
    resources: [
      `tcp:8080`,
    ],
  });
};

daemonUtil.startResourceServer = () => {
  return startAndWait({
    script: 'start-resource-server',
    color: 'green',
    resources: [
      `tcp:8000`,
    ],
  });
};

daemonUtil.startFrontEndServer = () => {
  return startAndWait({
    script: 'start-front-end-server',
    color: 'green',
    resources: [
      `tcp:8080`,
    ],
  });
};
