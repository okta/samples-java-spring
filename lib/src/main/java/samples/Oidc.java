package samples;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "oktaUrl",
        "clientId",
        "clientSecret",
        "redirectUri"
})
public class Oidc {

    @JsonProperty("oktaUrl")
    private String oktaUrl;
    @JsonProperty("clientId")
    private String clientId;
    @JsonProperty("clientSecret")
    private String clientSecret;
    @JsonProperty("redirectUri")
    private String redirectUri;

    @JsonProperty("oktaUrl")
    public String getOktaUrl() {return oktaUrl;}

    @JsonProperty("oktaUrl")
    public void setOktaUrl(String oktaUrl) {this.oktaUrl = oktaUrl;}

    @JsonProperty("clientId")
    public String getClientId() {return clientId;}

    @JsonProperty("clientId")
    public void setClientId(String clientId) {this.clientId = clientId;}

    @JsonProperty("clientSecret")
    public String getClientSecret() {return clientSecret;}

    @JsonProperty("clientSecret")
    public void setClientSecret(String clientSecret) {this.clientSecret = clientSecret;}

    @JsonProperty("redirectUri")
    public String getRedirectUri() {return redirectUri;}

    @JsonProperty("redirectUri")
    public void setRedirectUri(String redirectUri) {this.redirectUri = redirectUri;}
}
