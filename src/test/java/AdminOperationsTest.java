/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import pt.av.it.OsmDriverITAV.Requests.OsmClientProperties;
import pt.av.it.OsmDriverITAV.Requests.PropertiesHandler;

@DisplayName("Admin Operations Test Scenario")
public class AdminOperationsTest {
    private pt.av.it.OsmDriverITAV.OSMClient osmClient;
    private static final Logger logger = Logger.getLogger(AdminOperationsTest.class.getName());

    private String token;
    private String projectId;

    @Before
    public void setup(){
        logger.info("Before Setup");
        PropertiesHandler handler=new PropertiesHandler("local");
        OsmClientProperties properties;
        assert(handler.getNumberofOsmClients()>0);
        if(handler.getNumberofOsmClients()>0){
            properties=handler.getOsmClientProperties(handler.getNumberofOsmClients()-1);
            osmClient=new pt.av.it.OsmDriverITAV.OSMClient(properties.getUri(), properties.getUser(), properties.getPassword(), properties.getProject(), properties.getVimAccount());
            osmClient.newCurrentToken();
            token=osmClient.getcurrentTOKEN_ID();
        }
        if (token==null||token.length()<1){
            osmClient.apiCalls.setCurrentTOKEN_ID(token);
        }
        if(!osmClient.isCurrentTokenValid()||!osmClient.isApiCallTokenValid()){
            osmClient.updateApiCallToken();
        }
    }

    @After
    public void tokenCleanup(){
        logger.info("Token Cleanup");
        assertNotNull(token);
        assertNotNull(osmClient);
        if(osmClient.listTokenById(token).containsKey("status_code")){
            return;
        }
        assertTrue(osmClient.deleteTokenById(token).contains("deleted"));
    }
    
    @Test
    public void listProjectsTest(){
        logger.info("List Projects");
        assertNotNull(osmClient.adminOperations.listProjects());
    }

    @Test
    @Disabled("Test purposes")
    public void newProjectTest()throws ParseException {
        assertNotNull(token);

        logger.info("New Project ");
        String projectProperties="{\n" +
                "  \"name\": \"testing1\",\n" +
                "  \"admin\": true,\n" +
                "  \"quotas\": {\n" +
                "    \"vnfds\": 0,\n" +
                "    \"nsds\": 0,\n" +
                "    \"nsts\": 0,\n" +
                "    \"pdus\": 0,\n" +
                "    \"nsrs\": 0,\n" +
                "    \"nsis\": 0,\n" +
                "    \"vim_accounts\": 0,\n" +
                "    \"wim_accounts\": 0,\n" +
                "    \"sdns\": 0\n" +
                "  }\n" +
                "}\n";

        JSONParser parser=new JSONParser();
        JSONObject newProject,jsonObject=(JSONObject) parser.parse(projectProperties);
        newProject=osmClient.adminOperations.newProject(jsonObject);
        assertNotNull(newProject);
        assertTrue(newProject.containsKey("id"));
        this.projectId= (String) newProject.get("id");

        JSONObject deletedProject=osmClient.adminOperations.deleteProjectById(projectId);
        assertNotNull(deletedProject);

    }


    @Test
    public void listProjectByIdTest(){
        logger.info("List Project By ID ");
        projectId="d280de6b-4778-40d4-ab6a-d964359a6e9e";

        assertNotNull(token);
        JSONObject jsonObject=osmClient.adminOperations.listProjectById(projectId);

        assertNotNull(jsonObject);

    }

    @Test
    public void modifyProjectByIdTest() throws ParseException {
        logger.info("Delete Project By Id");

        assertNotNull(token);

        String projectProperties="{\n" +
                "  \"name\": \"testing2\",\n" +
                "  \"admin\": false,\n" +
                "  \"quotas\": {\n" +
                "    \"vnfds\": 0,\n" +
                "    \"nsds\": 0,\n" +
                "    \"nsts\": 0,\n" +
                "    \"pdus\": 0,\n" +
                "    \"nsrs\": 0,\n" +
                "    \"nsis\": 0,\n" +
                "    \"vim_accounts\": 0,\n" +
                "    \"wim_accounts\": 1,\n" +
                "    \"sdns\": 0\n" +
                "  }\n" +
                "}\n";
        JSONParser parser=new JSONParser();
        JSONObject modify,jsonObject=(JSONObject) parser.parse(projectProperties);

        projectId="b4c95e5c-1f72-496c-9b10-b63750a91ffc";

        modify=osmClient.adminOperations.modifyProjectById(projectId,jsonObject);

        jsonObject = osmClient.adminOperations.listProjectById(projectId);
        logger.info(jsonObject.toJSONString());
    }

    @Test
    @Disabled("Test purposes")
    public void deleteProjectByIdTest() throws ParseException {


        assertNotNull(token);

        String projectProperties="{\n" +
                "  \"name\": \"testing1\",\n" +
                "  \"admin\": true,\n" +
                "  \"quotas\": {\n" +
                "    \"vnfds\": 0,\n" +
                "    \"nsds\": 0,\n" +
                "    \"nsts\": 0,\n" +
                "    \"pdus\": 0,\n" +
                "    \"nsrs\": 0,\n" +
                "    \"nsis\": 0,\n" +
                "    \"vim_accounts\": 0,\n" +
                "    \"wim_accounts\": 0,\n" +
                "    \"sdns\": 0\n" +
                "  }\n" +
                "}\n";

        JSONParser parser=new JSONParser();
        JSONObject newProject,jsonObject=(JSONObject) parser.parse(projectProperties);
        newProject=osmClient.adminOperations.newProject(jsonObject);
        String projectId= (String) newProject.get("id");

        logger.info("Delete Project by Id ");

        JSONObject deleteProject=osmClient.adminOperations.deleteProjectById(projectId);
        assertNotNull(deleteProject);
    }

    @Test
    public void listVimAccountsTest(){
        logger.info("List Vim Accounts");
        
        assertNotNull(osmClient.adminOperations.listVimAccounts());
    }

    @Test
    public  void newVimAccountTest(){
        logger.info("New Vim Account ");
        
        String vimAccountProperties="{\n" +
                "  \"schema_version\": \"string\",\n" +
                "  \"schema_type\": \"No idea. Thanks Matilda ;P (https://private.matilda-5g.eu/documents/PublicDownload/487)\",\n" +
                "  \"name\": \"test2\",\n" +
                "  \"description\": \"vimtest\",\n" +
                "  \"vim\": \"string\",\n" +
                "  \"datacenter\": \"IT Aveiro 1\",\n" +
                "  \"vim_type\": \"openvim\",\n" +
                "  \"vim_url\": \"https://127.0.0.1/\",\n" +
                "  \"vim_tenant_name\": \"admin\",\n" +
                "  \"vim_user\": \"admin\",\n" +
                "  \"vim_password\": \"admin\",\n" +
                "  \"config\": {\n" +
                "    \"additionalProp1\": {}\n" +
                "  }\n" +
                "}";

        
    }


    @Test
    public void listVimAccountByIdTest(){

    }

    @Test
    public void modifyVimAccountByIdTest(){
        
    }

    @Test
    public void deleteVimAccountByIdTest(){

    }


    @Test
    public  void  listVimsTest(){

    }

    @Test
    public  void  newVimTest(){

    }

    @Test
    public void listVimByIdTest(){}

    @Test
    public void modifyVimByIdTest(){

    }

    @Test 
    public void deleteVimById(){

    }

}
