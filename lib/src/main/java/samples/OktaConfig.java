package samples;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "oidc",
        "server",
        "mockOkta"
})
public class OktaConfig {

    @JsonProperty("oidc")
    private Oidc oidc;
    @JsonProperty("server")
    private Server server;
    @JsonProperty("mockOkta")
    private MockOkta mockOkta;

    @JsonProperty("oidc")
    public Oidc getOidc() {return oidc;}

    @JsonProperty("oidc")
    public void setOidc(Oidc oidc) {this.oidc = oidc;}

    @JsonProperty("server")
    public Server getServer() {return server;}

    @JsonProperty("server")
    public void setServer(Server server) {this.server = server;}

    @JsonProperty("mockOkta")
    public MockOkta getMockOkta() {return mockOkta;}

    @JsonProperty("mockOkta")
    public void setMockOkta(MockOkta mockOkta) {this.mockOkta = mockOkta;}
}
