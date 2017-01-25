package samples;

import java.util.HashMap;
import java.util.Map;


public class User {
    public String email;
    public Map claims;

    public User() {
        email = "";
        claims = new HashMap<String, Object>();
        claims.put("iss", "");
        claims.put("iat", 0);
        claims.put("exp", 0);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setClaims(Map claims) {
        this.claims = claims;
    }

    public Map toDict() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("email", email);
        obj.put("claims", claims);
        return obj;
    }
}
