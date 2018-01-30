# Okta Spring Security Resource Server Example

This sample application authenticates requests against your Spring application, using access tokens.

The access tokens are obtained via the [Implicit Flow][].  As such, you will need to use one of our front-end samples with this project.  It is the responsibility of the front-end to authenticate the user, then use the obtained access tokens to make requests to this resource server.

## Prerequisites

Before running this sample, you will need the following:

* An Okta Developer Account, you can sign up for one at https://developer.okta.com/signup/.
* An Okta Application, configured for Singe-Page App (SPA) mode. This is done from the Okta Developer Console and you can find instructions [here][OIDC SPA Setup Instructions].  When following the wizard, use the default properties.  They are are designed to work with our sample applications.
* One of our front-end sample applications to demonstrate the interaction with the resource server:
  * [Okta React Sample Apps][]
  * [Okta Angular Sample Apps][]

A typical resource-server requires a frontend and a backend application, so you will need to start each process:

**backend:**
```bash
cd resource-server
mvn -Dokta.oauth2.issuer=https://{yourOktaDomain}.com/oauth2/default
```

**front-end:**

Instead of using one of our front-end sample applications listed above, you can also use a front-end within this repo to quickly test the resource server.

This project is just serving a static [`index.html`](front-end/src/main/resources/static/index.html) so you WILL need to update the configuration in that file, see line 53:

```javascript
const data = {
  baseUrl: 'https://{yourOktaDomain}.com',
  clientId: 'YourClientId',
  redirectUri: 'http://localhost:8080',
  authParams: {
    issuer: 'https://{yourOktaDomain}.com/oauth2/default',
    responseType: ['id_token', 'token'],
    scopes: ["openid", "profile", "email"]
  }
};
```

```bash
cd front-end
mvn
```

Browse to: `http://localhost:8080/` to login!

> **NOTE:** If you want to use one of our front-end samples, open a new terminal window and run the front-end sample project of your choice (see links in Prerequisites).  Once the front-end sample is running, you can navigate to http://localhost:8080 in your browser and log in to the front-end application.  Once logged in, you can navigate to the "Messages" page to see the interaction with the resource server.


[Implicit Flow]: https://developer.okta.com/authentication-guide/implementing-authentication/implicit
[Okta Angular Sample Apps]: https://github.com/okta/samples-js-angular
[Okta React Sample Apps]: https://github.com/okta/samples-js-react
[OIDC SPA Setup Instructions]: https://developer.okta.com/authentication-guide/implementing-authentication/implicit#1-setting-up-your-application
