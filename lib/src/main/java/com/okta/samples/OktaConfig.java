package com.okta.samples;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "oidc",
        "server",
        "mockOkta"
})
public class OktaConfig {

    private Oidc oidc;
    private Server server;
    private MockOkta mockOkta;

    public Oidc getOidc() {return oidc;}
    public void setOidc(Oidc oidc) {this.oidc = oidc;}

    public Server getServer() {return server;}
    public void setServer(Server server) {this.server = server;}

    public MockOkta getMockOkta() {return mockOkta;}
    public void setMockOkta(MockOkta mockOkta) {this.mockOkta = mockOkta;}
}
