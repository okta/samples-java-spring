package com.okta.samples;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String email;
    private String givenName;
    private String familyName;
    private String subject;
    private String zoneInfo;
    private String locale;

    public User() {
        // Give default values for Mustache template rendering for non-authenticated user
        email = "";
        givenName = "";
        familyName = "";
        subject = "";
        zoneInfo = "";
        locale = "";
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getGivenName() {return givenName;}
    
    public void setGivenName(String givenName) {this.givenName = givenName;}

    public String getFamilyName() {return familyName;}
    
    public void setFamilyName(String familyName) {this.familyName = familyName;}

    public String getSubject() {return subject;}
    
    public void setSubject(String subject) {this.subject = subject;}

    public String getZoneInfo() {return zoneInfo;}
    
    public void setZoneInfo(String zoneInfo) {this.zoneInfo = zoneInfo;}

    public String getLocale() {return locale;}
    
    public void setLocale(String locale) {this.locale = locale;}

    public Map toDict() {
        // Output object as Map to be read by Mustache templates
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("email", email);
        obj.put("given_name", givenName);
        obj.put("family_name", familyName);
        obj.put("sub", subject);
        obj.put("zoneinfo", zoneInfo);
        obj.put("locale", locale);
        return obj;
    }
}
