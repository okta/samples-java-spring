# Spring Security OAuth Sample Applications for Okta

This repository contains several sample applications that show you how to integrate various Okta use-cases into your Java application that uses the Spring framework.

Please find the sample that fits your use-case from the table below.

| Sample | Description | Use-Case |
|--------|-------------|----------|
| [Okta-Hosted Login](/okta-hosted-login) | An application server that uses the hosted login page on your Okta org, then creates a cookie session for the user in the Spring application. | Traditional web applications with server-side rendered pages. |
| [Custom Login Page](/custom-login) | An application server that uses the Okta Sign-In Widget on a custom login page within the application, then creates a cookie session for the user in the Spring application. | Traditional web applications with server-side rendered pages. |
| [Resource Server](/resource-server) | This is a sample API resource server that shows you how to authenticate requests with access tokens that have been issued by Okta. | Single-Page applications. |
| [Front End](/front-end) (bonus) | A simple static Single-Page application that can be used with the resource-server | Test resource server |

## Prerequisites

Before running the okta-hosted-login or custom-login examples, you will need the following:

* An Okta Developer Account, you can sign up for one at https://developer.okta.com/signup/.
* An Okta Application, configured for Web mode. This is done from the Okta Developer Console and you can find instructions [here][OIDC Web Application Setup Instructions].  When following the wizard, use the default properties.  They are designed to work with our sample applications.

## How to run the examples

There is a pom.xml at the root of this project, that exists to build all of the projects.  Each project is independent and could be copied out of this repo as a primer for your own application.

### `okta-hosted-login`

The login is achieved through the [Authorization Code Flow], where the user is redirected to the Okta-Hosted login page. After the user authenticates, they are redirected back to the application.

```bash
cd okta-hosted-login
mvn -Dokta.oauth2.issuer=https://{yourOktaDomain}.com/oauth2/default \
    -Dokta.oauth2.clientId={YourClientId} \
    -Dokta.oauth2.clientSecret={YourClientSecret}
```

**NOTE:** Putting secrets on the command line should ONLY be done for examples, do NOT do this in production. Instead update the projects `application.yml`

Browse to: `http://localhost:8080/` to login!


### `custom-login`

The login is achieved with the [Okta Sign In Widget][], which gives you more control to customize the login experience within your application.  After the user authenticates, they are redirected back to the application.

```bash
cd custom-login
mvn -Dokta.oauth2.issuer=https://{yourOktaDomain}.com/oauth2/default \
    -Dokta.oauth2.clientId={YourClientId} \
    -Dokta.oauth2.clientSecret={YourClientSecret}
```

**NOTE:** Putting secrets on the command line should ONLY be done for examples, do NOT do this in production. Instead update the projects `application.yml`

Browse to: `http://localhost:8080/` to login!

### `resource-server`

This sample application authenticates requests against your Spring application, using access tokens.

The access tokens are obtained via the [Implicit Flow][].  As such, you will need to use one of our front-end samples with this project.  It is the responsibility of the front-end to authenticate the user, then use the obtained access tokens to make requests of this resource server.

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

Instead of using one of our front-end sample applications listed above, you can use this front-end to quickly test the resource server.

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

[Implicit Flow]: https://developer.okta.com/authentication-guide/implementing-authentication/implicit
[Okta Angular Sample Apps]: https://github.com/okta/samples-js-angular
[Okta React Sample Apps]: https://github.com/okta/samples-js-react
[OIDC SPA Setup Instructions]: https://developer.okta.com/authentication-guide/implementing-authentication/implicit#1-setting-up-your-application
[OIDC Web Application Setup Instructions]: https://developer.okta.com/authentication-guide/implementing-authentication/auth-code#1-setting-up-your-application
[Authorization Code Flow]: https://developer.okta.com/authentication-guide/implementing-authentication/auth-code
[Okta Sign In Widget]: https://github.com/okta/okta-signin-widget
