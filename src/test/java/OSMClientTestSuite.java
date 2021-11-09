/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.av.it.OsmDriverITAV.Requests.OsmClientProperties;
import pt.av.it.OsmDriverITAV.Requests.PropertiesHandler;

@RunWith(JUnitPlatform.class)
@DisplayName("OSM Test Suite")
public class OSMClientTestSuite {
    private pt.av.it.OsmDriverITAV.OSMClient osmClient;

    @Before
    public void setup(){
        PropertiesHandler handler=new PropertiesHandler();
        OsmClientProperties properties;
        properties=handler.getOsmClientProperties(0);
        osmClient=new pt.av.it.OsmDriverITAV.OSMClient(properties.getUri(), properties.getUser(), properties.getPassword(), properties.getProject(), properties.getVimAccount());
    }


    @Test
    public void version(){
        assertEquals(200, osmClient.versionInfo().get("status_code"));
    }

}
