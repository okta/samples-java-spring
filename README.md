Spring Security OAuth Examples
==============================

This repository contains three examples:

- basic - A standard OAuth 2.0 redirect (code flow).
- custom-login - An OAuth 2.0 code flow with a custom login page (using the Okta Sign-In Widget )
- resource-server - An OAuth 2.0 resource server (implicit flow)
- (bonus) front-end - A simple static SPA app that can be used with the resource-server


## How to run the examples

There is a pom.xml at the root of this project, that exists to build all of the projects.  Each project is independent and could be copied out of this repo as a primer for your own application.

### `basic`

```bash
cd basic
mvn -Dokta.oauth2.issuer=https://dev-123456.oktapreview.com/oauth2/default \
    -Dokta.oauth2.clientId=YourClientId \
    -Dokta.oauth2.clientSecret=YourClientSecret
```

**NOTE:** Putting secrets on the command line should ONLY be done for examples, do NOT do this in production. Instead update the projects `application.yml`

Browse to: [http://localhost:8080/] to login!


### `custom-login`

```bash
cd basic
mvn -Dokta.oauth2.issuer=https://dev-123456.oktapreview.com/oauth2/default \
    -Dokta.oauth2.clientId=YourClientId \
    -Dokta.oauth2.clientSecret=YourClientSecret
```

**NOTE:** Putting secrets on the command line should ONLY be done for examples, do NOT do this in production. Instead update the projects `application.yml`

Browse to: [http://localhost:8080/] to login!

### `resource-server`

A typical resource-server requires a frontend and a backend application, so you will need to start each process:

backend:
```bash
cd resource-server
mvn -Dokta.oauth2.issuer=https://dev-123456.oktapreview.com/oauth2/default
```

front-end:

This project is just serving a static `index.html` so you WILL need to update the configuration in that file, see line 53:

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
This project is temporary, and will be replaced with references to our other frontend examples.

```bash
cd front-end
mvn
```

Browse to: [http://localhost:8080/] to login!