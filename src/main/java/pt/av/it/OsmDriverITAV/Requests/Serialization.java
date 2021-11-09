/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.Requests;

import java.util.Map;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.Yaml;

public class Serialization {
    
    public static JSONObject jsonFromYamlString(String yamString){
        Yaml yaml= new Yaml();
        Map<String,Object> map= yaml.load(yamString);
        return new JSONObject(map);
    }
}
