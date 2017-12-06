Resource Server end-to-end Test
===============================

This section contains the Happy path E2E test for Resource Server (Implicit) flow

## Prerequisites

* You will need an Okta Developer Org, you can sign up for an account at https://developer.okta.com/signup/..
* An OIDC application in your Org, configured as SPA.

**NOTE:** You can follow the instructions [here](https://developer.okta.com/authentication-guide/implementing-authentication/implicit#1-setting-up-your-application) to create an OIDC SPA in your Okta org 

* Create & activate a user with the following credentials

  **Username** - george@acme.com

  **Password** - Asdf1234

* Ensure that this user is assigned to the above created SPA

## How to run the tests

### Set the environment variables
Set the following environment variables to reflect your settings

OKTA_URL -> Domain of your okta org

```bash
export ORG_URL=https://dev-123456.oktapreview.com
```

### Update front-end index.html
This project is just serving a static [`index.html`](front-end/src/main/resources/static/index.html) so you WILL need to update the configuration in that file, see line 53:

Update the file with your Okta URL (baseUrl), clientId for the SPA created in your okta org and issuer in the code block below
```javascript
const data = {
  baseUrl: 'https://dev-123456.oktapreview.com',
  clientId: 'YourClientId',
  redirectUri: 'http://localhost:8080',
  authParams: {
    issuer: 'https://dev-123456.oktapreview.com/oauth2/default',
    responseType: ['id_token', 'token'],
    scopes: ["openid", "profile", "email"]
  }
};
```

### Run the npm command
Run the following npm command from the base directory

```bash
cd samples-java-spring-mvc
npm run test:resource-server
```

