package Requests;

import org.json.simple.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class Serialization {
    
    public static JSONObject jsonFromYamlString(String yamString){
        Yaml yaml= new Yaml();
        Map<String,Object> map= yaml.load(yamString);
        return new JSONObject(map);
    }
}
