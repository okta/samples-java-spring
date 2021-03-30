# Okta Spring Security & Custom Login Page Example

This example shows you how to use the [Okta Spring Boot Library][] to login a user.  The login is achieved through the [Authorization Code Flow][] using the [Okta Sign In Widget][], which gives you more control to customize the login experience within your app.  After the user authenticates, they are redirected back to the application and a local cookie session is created.


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

## Running This Example

There is a pom.xml at the root of this project, that exists to build all of the projects.  Each project is independent and could be copied out of this repo as a primer for your own application.

You also need to gather the following information from the Okta Developer Console:

- **Client ID** and **Client Secret** - These can be found on the "General" tab of the Web application that you created earlier in the Okta Developer Console.

- **Issuer** - This is the URL of the authorization server that will perform authentication.  All Developer Accounts have a "default" authorization server.  The issuer is a combination of your Org URL (found in the upper right of the console home page) and `/oauth2/default`. For example, `https://dev-1234.okta.com/oauth2/default`.

Plug these values into the `mvn` commands used to start the application.

```bash
cd custom-login
mvn -Dokta.oauth2.issuer=https://{yourOktaDomain}/oauth2/default \
    -Dokta.oauth2.clientId={clientId} \
    -Dokta.oauth2.clientSecret={clientSecret}
```

> **NOTE:** Putting secrets on the command line should ONLY be done for examples, do NOT do this in production. Instead, we recommend you store them as environment variables. For example:

```bash
export OKTA_OAUTH2_ISSUER=https://{yourOktaDomain}/oauth2/default
export OKTA_OAUTH2_CLIENT_ID={clientId}
export OKTA_OAUTH2_CLIENT_SECRET={clientSecret}
```

Then you can simply use `mvn` to start your app.

Now navigate to http://localhost:8080 in your browser.

If you see a home page that prompts you to login, then things are working!  Clicking the **Login** button will render a custom login page, served by the Spring Boot application, that uses the [Okta Sign In Widget][] to perform authentication.

You can login with the same account that you created when signing up for your Developer Org, or you can use a known username and password from your Okta Directory.

> **NOTE:** If you are currently using your Developer Console, you already have a Single Sign-On (SSO) session for your Org.  You will be automatically logged into your application as the same user that is using the Developer Console.  You may want to use an incognito tab to test the flow from a blank slate.

[Okta Spring Boot Library]: https://github.com/okta/okta-spring-boot
[OIDC Web Application Setup Instructions]: https://developer.okta.com/authentication-guide/implementing-authentication/auth-code#1-setting-up-your-application
[Authorization Code Flow]: https://developer.okta.com/authentication-guide/implementing-authentication/auth-code
[Okta Sign In Widget]: https://github.com/okta/okta-signin-widget
