# Spring Security OAuth Sample Applications for Okta

This repository contains several sample applications that show you how to integrate various Okta use-cases into your Java application that uses the Spring framework.

Please find the sample that fits your use-case from the table below.

| Sample | Description | Use-Case |
|--------|-------------|----------|
| [Federated Okta Hosted Login Page](/federated-okta-hosted) | An application server that uses the Federated Okta hosted login page on your Okta org, then creates a cookie session for the user in the Spring application. | Traditional web applications with server-side rendered pages. |
| [Custom Login Page](/custom-login) | An application server that uses the Okta Sign-In Widget on a custom login page within the application, then creates a cookie session for the user in the Spring application. | Traditional web applications with a custom login page and server-side rendered pages. |
| [Resource Server](/resource-server) | This is a sample API resource server that shows you how to authenticate requests with access tokens that have been issued by Okta. | Single-Page applications. |
| [Front End](/front-end) (bonus) | A simple static Single-Page application that can be used with the resource-server | Test the above resource server |

:information_source: Federated Okta Hosted Login is used when you want Okta to handle authentication through its login page, hosted in Okta’s ecosystem.
