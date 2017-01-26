package samples;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "port",
        "proxy",
        "proxied",
        "cdn"
})
public class MockOkta {

    @JsonProperty("port")
    private Integer port;
    @JsonProperty("proxy")
    private String proxy;
    @JsonProperty("proxied")
    private String proxied;
    @JsonProperty("cdn")
    private String cdn;

    @JsonProperty("port")
    public Integer getPort() {return port;}

    @JsonProperty("port")
    public void setPort(Integer port) {this.port = port;}

    @JsonProperty("proxy")
    public String getProxy() {return proxy;}

    @JsonProperty("proxy")
    public void setProxy(String proxy) {this.proxy = proxy;}

    @JsonProperty("proxied")
    public String getProxied() {return proxied;}

    @JsonProperty("proxied")
    public void setProxied(String proxied) {this.proxied = proxied;}

    @JsonProperty("cdn")
    public String getCdn() {return cdn;}

    @JsonProperty("cdn")
    public void setCdn(String cdn) {this.cdn = cdn;}
}
