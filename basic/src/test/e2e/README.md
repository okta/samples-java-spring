Auth Code Flow end-to-end Test
==============================

This section contains the Happy path E2E test for Authorization code flow

## Prerequisites

* You will need an Okta Developer Org, you can sign up for an account at https://developer.okta.com/signup/..
* An OIDC application in your Org, configured as Web Application.

**NOTE:** You can follow the instructions [here](https://developer.okta.com/authentication-guide/implementing-authentication/auth-code#1-setting-up-your-application) to create an OIDC Web Application in your Okta org

* Create & activate a user with the following credentials

  **Username** - george@acme.com

  **Password** - Asdf1234

* Ensure that this user is assigned to the above created Web Application

## How to run the tests

### Set the environment variables
Set the following environment variables to reflect your settings

OKTA_URL -> Domain of your okta org

CLIENT_ID -> Client ID of the web application created in your okta org

CLIENT_SECRET -> Client secret of the web application created in your okta org

```bash
export ORG_URL=https://dev-123456.oktapreview.com
export CLIENT_ID=xxxxx
export CLIENT_SECRET=yyyyyyyyyyy
``` 

### Run the npm command
Run the following npm command from the base directory

```bash
cd samples-java-spring-mvc
npm run test:basic
```