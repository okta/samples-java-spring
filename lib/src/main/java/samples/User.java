package samples;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String email;
    private Map claims;

    public User() {
        // Give default values for Mustache template rendering for non-authenticated user
        email = "";
        claims = new HashMap<String, Object>();
        claims.put("iss", "");
        claims.put("iat", 0);
        claims.put("exp", 0);
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public void setClaims(Map claims) {this.claims = claims;}

    public Map toDict() {
        // Output object as Map to be read by Mustache templates
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("email", email);
        obj.put("claims", claims);
        return obj;
    }
}
