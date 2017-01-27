package com.okta.samples;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "port",
        "proxy",
        "proxied",
        "cdn"
})
public class MockOkta {

    private Integer port;
    private String proxy;
    private String proxied;
    private String cdn;

    public Integer getPort() {return port;}
    public void setPort(Integer port) {this.port = port;}

    public String getProxy() {return proxy;}
    public void setProxy(String proxy) {this.proxy = proxy;}

    public String getProxied() {return proxied;}
    public void setProxied(String proxied) {this.proxied = proxied;}

    public String getCdn() {return cdn;}
    public void setCdn(String cdn) {this.cdn = cdn;}
}
