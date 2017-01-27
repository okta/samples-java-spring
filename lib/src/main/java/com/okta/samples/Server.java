package com.okta.samples;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    private String staticDir;
    private String startSignal;
    private Integer port;
    private String framework;
    private String environment;


    public String getStaticDir() {return staticDir;}
    public void setStaticDir(String staticDir) {this.staticDir = staticDir;}


    public String getStartSignal() {return startSignal;}
    public void setStartSignal(String startSignal) {this.startSignal = startSignal;}

    public Integer getPort() {return port;}
    public void setPort(Integer port) {this.port = port;}


    public String getFramework() {return framework;}
    public void setFramework(String framework) {this.framework = framework;}

    public String getEnvironment() {return environment;}
    public void setEnvironment(String environment) {this.environment = environment;}
}
