package samples;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "staticDir",
        "startSignal",
        "port",
        "framework",
        "environment"
})
public class Server {

    @JsonProperty("staticDir")
    private String staticDir;
    @JsonProperty("startSignal")
    private String startSignal;
    @JsonProperty("port")
    private Integer port;
    @JsonProperty("framework")
    private String framework;
    @JsonProperty("environment")
    private String environment;


    @JsonProperty("staticDir")
    public String getStaticDir() {return staticDir;}

    @JsonProperty("staticDir")
    public void setStaticDir(String staticDir) {this.staticDir = staticDir;}

    @JsonProperty("startSignal")
    public String getStartSignal() {return startSignal;}

    @JsonProperty("startSignal")
    public void setStartSignal(String startSignal) {this.startSignal = startSignal;}

    @JsonProperty("port")
    public Integer getPort() {return port;}

    @JsonProperty("port")
    public void setPort(Integer port) {this.port = port;}

    @JsonProperty("framework")
    public String getFramework() {return framework;}

    @JsonProperty("framework")
    public void setFramework(String framework) {this.framework = framework;}

    @JsonProperty("environment")
    public String getEnvironment() {return environment;}

    @JsonProperty("environment")
    public void setEnvironment(String environment) {this.environment = environment;}
}
