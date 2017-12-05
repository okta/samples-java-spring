Resource Server end-to-end Test
===============================

This section contains the Happy path E2E test for Resource Server (Implicit) flow


## How to run the tests
You need to set the client secret environment variable before running the test.
Please contact vijet.mahabaleshwar@okta.com for the client secret.

TODO - Set the client secret in travis settings. (Contact rel-eng)

You can run these tests from the base directory using the following command.

```bash
cd samples-java-spring-mvc
export CLIENT_SECRET=xxxxx
npm run test:resource-server
```

Currently the test runs against a real Okta org (https://samples-test.oktapreview.com)

