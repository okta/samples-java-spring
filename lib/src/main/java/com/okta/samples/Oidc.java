package com.okta.samples;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "oktaUrl",
        "clientId",
        "clientSecret",
        "redirectUri"
})
public class Oidc {

    private String oktaUrl;
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    public String getOktaUrl() {return oktaUrl;}
    public void setOktaUrl(String oktaUrl) {this.oktaUrl = oktaUrl;}

    public String getClientId() {return clientId;}
    public void setClientId(String clientId) {this.clientId = clientId;}

    public String getClientSecret() {return clientSecret;}
    public void setClientSecret(String clientSecret) {this.clientSecret = clientSecret;}

    public String getRedirectUri() {return redirectUri;}
    public void setRedirectUri(String redirectUri) {this.redirectUri = redirectUri;}
}
