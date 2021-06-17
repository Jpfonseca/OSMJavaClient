import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.av.it.SimpleDriver.Requests.OsmClientProperties;
import pt.av.it.SimpleDriver.Requests.PropertiesHandler;

@RunWith(JUnitPlatform.class)
@DisplayName("OSM Test Suite")
public class OSMClientTestSuite {
    private pt.av.it.SimpleDriver.OSMClient osmClient;

    @Before
    public void setup(){
        PropertiesHandler handler=new PropertiesHandler();
        OsmClientProperties properties;
        properties=handler.getOsmClientProperties(0);
        osmClient=new pt.av.it.SimpleDriver.OSMClient(properties.getUri(), properties.getUser(), properties.getPassword(), properties.getProject(), properties.getVimAccount());
    }


    @Test
    public void version(){
        assertEquals(200, osmClient.versionInfo().get("status_code"));
    }

}
