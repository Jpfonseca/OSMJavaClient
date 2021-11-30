/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import pt.av.it.OsmDriverITAV.Requests.OsmClientProperties;
import pt.av.it.OsmDriverITAV.Requests.PropertiesHandler;

@DisplayName("Admin Test Scenario")
public class AdminTest {
    private pt.av.it.OsmDriverITAV.OSMClient osmClient;
    private String token;
    private static final Logger logger = Logger.getLogger(AdminTest.class.getName());


    @Before
    public void setup(){
        logger.info("Before Setup");
        PropertiesHandler handler=new PropertiesHandler("local");
        OsmClientProperties properties=null;
        assert(handler.getNumberofOsmClients()>0);
        if(handler.getNumberofOsmClients()>0){
            properties=handler.getOsmClientProperties(handler.getNumberofOsmClients()-1);
            osmClient=new pt.av.it.OsmDriverITAV.OSMClient(properties.getUri(), properties.getUser(), properties.getPassword(), properties.getProject(), properties.getVimAccount());
            osmClient.newCurrentToken();
            token=osmClient.getCurrentTOKEN_ID();
        }
    }

    @After
    public void tokenCleanup(){
        assertNotNull(token);
        assertNotNull(osmClient);
        if(osmClient.listTokenById(token).containsKey("status_code")){
            return;
        }
        assertTrue(osmClient.deleteTokenById(token).contains("deleted"));
    }


    @Test
    public void listAllTokensInfo() {
        logger.info("List Auth Token Info");
        assert(osmClient.listAllTokensInfo().toString().length()>2);

    }

    @Test
    public void newAuthToken(){
        logger.info("New Auth Token");
        assertEquals(200,osmClient.newCurrentToken().get("status_code"));
    }

    @Test
    public void deleteAuthToken(){
        logger.info("Delete Auth Token");
        assertTrue(osmClient.deleteCurrentToken().contains("deleted"));
    }
    
    @Test
    public void listTokenById(){
        logger.info("List Token By Id");
        String tokenID=(String) ((JSONObject) osmClient.newCurrentToken().get("message")).get("id");
        assertEquals(true,osmClient.listTokenById(tokenID).keySet().contains("expires"));
    }
    
    @Test
    public void deleteTokenById(){

        logger.info("Delete Token by Id");
        String tokenID=osmClient.newToken();
        //assertNotNull(osmClient.newToken());
        assertTrue(osmClient.deleteTokenById(tokenID).contains("deleted"));
    }
    
    @Test
    public void version(){
        assertEquals(200, osmClient.versionInfo().get("status_code"));
    }

    /*@Test
    public void (){

    }*/
}

