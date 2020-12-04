# Okta Spring Security Resource Server Example

This sample application authenticates requests against your Spring application, using access tokens.

The access tokens are obtained via the [Authorization Code Flow + PKCE][].  As such, you will need to use one of our front-end samples with this project.  It is the responsibility of the front-end to authenticate the user, then use the obtained access tokens to make requests to this resource server.

## Prerequisites

Before running this sample, you will need the following:

* An Okta Developer Account, you can sign up for one at https://developer.okta.com/signup/.
* An Okta Application, configured for Singe-Page App (SPA) mode. This is done from the Okta Developer Console and you can find instructions [here][OIDC SPA Setup Instructions].  When following the wizard, use the default properties.  They are designed to work with our sample applications.
* One of our front-end sample applications to demonstrate the interaction with the resource server:
  * [Okta Angular Sample Apps][]
  * [Okta React Sample Apps][]
  * [Okta Vue Sample Apps][]

A typical resource-server requires a frontend and a backend application, so you will need to start each process:

## Running This Example

**backend:**
```bash
cd resource-server
mvn -Dokta.oauth2.issuer=https://{yourOktaDomain}/oauth2/default
```
> **NOTE:** The above command starts the resource server on port 8000. You can browse to `http://localhost:8000` to ensure it has started. If you get the message "401 Unauthorized", it indicates that the resource server is up. You will need to pass an access token to access the resource, which will be done by the front-end below.

**front-end:**

Instead of using one of our front-end sample applications listed above, you can also use the [front-end](../front-end) within this repo to quickly test the resource server.
To start the front-end, you need to gather the following information from the Okta Developer Console:

- **Client Id** - The client ID of the SPA application that you created earlier. This can be found on the "General" tab of an application, or the list of applications. The resource server will validate that tokens have been minted for this application.
- **Base URL** - This is the URL of the developer org that you created. For example, `https://dev-1234.oktapreview.com`.

Now start the front-end.

```bash
cd front-end
mvn \
  -Dokta.oauth2.issuer=https://{yourOktaDomain}/oauth2/default \
  -Dokta.oauth2.client-id={clientId}
```

Browse to: `http://localhost:8080/` to login!

> **NOTE:** If you want to use one of our front-end samples, open a new terminal window and run the [front-end sample project of your choice](Prerequisites).  Once the front-end sample is running, you can navigate to http://localhost:8080 in your browser and log in to the front-end application.  Once logged in, you can navigate to the "Messages" page to see the interaction with the resource server.


[Authorization Code Flow + PKCE]: https://developer.okta.com/docs/guides/implement-auth-code-pkce/overview/
[Okta Angular Sample Apps]: https://github.com/okta/samples-js-angular
[Okta Vue Sample Apps]: https://github.com/okta/samples-js-vue
[Okta React Sample Apps]: https://github.com/okta/samples-js-react
[OIDC SPA Setup Instructions]: https://developer.okta.com/authentication-guide/implementing-authentication/implicit#1-setting-up-your-application
