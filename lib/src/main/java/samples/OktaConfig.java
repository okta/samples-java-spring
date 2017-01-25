package samples;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Map;

public class OktaConfig {
    public Map config;

    public OktaConfig() {
        String path = System.getProperty("user.dir") + "/.samples.config.json";
        try {
            // Import config file
            File f = new File(path);
            ObjectMapper map = new ObjectMapper();
            config = map.readValue(f, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getValue(String name) {
        // Parse through config object for key matching name
        for (Object key : config.keySet()) {
            Map map = (Map) config.get(key.toString());

            if ( map.get(name) != null) {
                return map.get(name).toString();
            }
        }
        return null;
    }
}
