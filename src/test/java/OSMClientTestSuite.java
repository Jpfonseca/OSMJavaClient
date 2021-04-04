import Requests.OsmClientProperties;
import Requests.PropertiesHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitPlatform.class)
@DisplayName("OSM Test Suite")
public class OSMClientTestSuite {
    private OSMClient osmClient;

    @Before
    public void setup(){
        PropertiesHandler handler=new PropertiesHandler();
        OsmClientProperties properties;
        properties=handler.getOsmClientProperties(0);
        osmClient=new OSMClient(properties.getUri(), properties.getUser(), properties.getPassword(), properties.getProject(), properties.getVimAccount());
    }


    @Test
    public void version(){
        assertEquals(200, osmClient.versionInfo().get("status_code"));
    }

}
