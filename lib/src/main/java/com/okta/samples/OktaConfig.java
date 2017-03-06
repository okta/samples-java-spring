package com.okta.samples;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "oktaSample"
})
public class OktaConfig {

    private OktaSample oktaSample;

    public OktaSample getOktaSample() {return oktaSample;}
    public void setOktaSample(OktaSample oktaSample) {this.oktaSample = oktaSample;}
}
