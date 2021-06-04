# Okta Spring Security & Self Hosted Login Page Example

This example shows you how to use the [Okta Spring Boot Library][] to login a user.  The login is achieved through the Interaction Code flow using the [Okta Sign In Widget][], which gives you more control to customize the login experience within your app.  After the user authenticates, they are redirected back to the application and a local cookie session is created.

## Introduction

> :grey_exclamation: The use of this Sample uses an SDK that requires usage of the Okta Identity Engine.
This functionality is in general availability but is being gradually rolled out to customers. If you want
to request to gain access to the Okta Identity Engine, please reach out to your account manager. If you
do not have an account manager, please reach out to oie@okta.com for more information.

This Sample Application will show you the best practices for integrating Authentication by embedding the
Sign In Widget into your application. The Sign In Widget is powered by [Okta's Identity Engine](https://
developer.okta.com/docs/concepts/ie-intro/) and will adjust your user experience based on policies.
Specifically, this sample application will cover some basic needed use cases to get you up and running
quickly with Okta.

These Examples are:
1. Sign In
2. Sign Out
3. Sign Up
4. Sign In/Sign Up with Social Identity Providers
5. Sign In with Multifactor Authentication using Email or Phone
6. Progressive Profiling

For information and guides on how to build your app with this sample, please take a look at the [Java
guides for Embedded Sign In Widget Authentication](link to DevDoc SBS guide)

## Prerequisites

Before running this sample, you will need the following:

* An Okta Developer Account, you can sign up for one at https://developer.okta.com/signup/.
* An Okta Application, configured for Web mode. This is done from the Okta Developer Console and you can find instructions [here][OIDC Web Application Setup Instructions].  When following the wizard, use the default properties.  They are designed to work with our sample applications.
* Your Okta Application entry needs a login redirect URI. Go to "Login redirect URIs" under "General Settings" for your application, click "Edit" and add http://localhost:8080/authorization-code/callback.
* Your Okta Application entry needs the logout callback. "Logout redirect URIs" under "General" for the application should list http://localhost:8080. If it is not present, click "Edit" and add it.
* Ensure that your Okta Application is assigned to "Everyone" group or a custom group or a set of users that need to access the application. Navigate to "Assignments" tab for the application, and click "Assign -> Assign to People" or "Assign -> Assign to Groups" to do this.
* The source code from this repository:

    ```
    git clone https://github.com/okta/samples-java-spring.git
    cd samples-java-spring
    ```

## Installation & Running The App

There is a pom.xml at the root of this project, that exists to build all of the projects.  Each project is independent and could be copied out of this repo as a primer for your own application.

You also need to gather the following information from the Okta Developer Console:

- **Client ID** and **Client Secret** - These can be found on the "General" tab of the Web application that you created earlier in the Okta Developer Console.

- **Issuer** - This is the URL of the authorization server that will perform authentication.  All Developer Accounts have a "default" authorization server.  The issuer is a combination of your Org URL (found in the upper right of the console home page) and `/oauth2/default`. For example, `https://dev-1234.okta.com/oauth2/default`.

Plug these values into the `mvn` commands used to start the application.

```bash
cd self-hosted
mvn -Dokta.oauth2.issuer=https://{yourOktaDomain}/oauth2/default \
    -Dokta.oauth2.clientId={clientId} \
    -Dokta.oauth2.clientSecret={clientSecret} \
    -Dokta.idx.scopes={scopes} \   # e.g. "openid email profile"
    -Dokta.oauth2.redirectUri={redirectUri} # should match with what is set in app settings
```

> **NOTE:** Putting secrets on the command line should ONLY be done for examples, do NOT do this in production. Instead, we recommend you store them as environment variables. For example:

```bash
export OKTA_OAUTH2_ISSUER=https://{yourOktaDomain}/oauth2/default
export OKTA_OAUTH2_CLIENT_ID={clientId}
export OKTA_OAUTH2_CLIENT_SECRET={clientSecret}
export OKTA_IDX_SCOPES={scopes}
export OKTA_OAUTH2_REDIRECTURI={redirectUri}
```

Then you can simply use `mvn` to start your app.

Now navigate to http://localhost:8080 in your browser.

If you see a home page that prompts you to login, then things are working!  Clicking the **Login** button will render a custom login page, served by the Spring Boot application, that uses the [Okta Sign In Widget][] to perform authentication.

You can login with the same account that you created when signing up for your Developer Org, or you can use a known username and password from your Okta Directory.

> **NOTE:** You should use an incognito tab to test the flow from a blank slate.

To see some examples for use cases using this sample application, please take a look at the [Java guides
for Embedded Authentication](link to DevDoc SBS guide)

[Okta Spring Boot Library]: https://github.com/okta/okta-spring-boot
[OIDC Web Application Setup Instructions]: https://developer.okta.com/authentication-guide/implementing-authentication/auth-code#1-setting-up-your-application
[Authorization Code Flow]: https://developer.okta.com/authentication-guide/implementing-authentication/auth-code
[Okta Sign In Widget]: https://github.com/okta/okta-signin-widget
