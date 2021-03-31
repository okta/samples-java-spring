# Okta IDX Direct Auth Example

This example shows you how to use the Okta IDX Direct Auth flows to login a user.

## Prerequisites


## Running This Example

There is a pom.xml at the root of this project, that exists to build all the projects. Each project is independent and could be copied out of this repo as a primer for your own application.

```bash
cd direct-auth
mvn spring-boot:run
```

Now navigate to http://localhost:8080 in your browser.

If you see a home page that prompts you to login, then things are working!  Clicking the **Login** button will render a custom login page, served by the Spring Boot application, that uses the [Okta Sign In Widget][] to perform authentication.
