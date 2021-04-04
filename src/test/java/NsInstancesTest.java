import Requests.OsmClientProperties;
import Requests.PropertiesHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NsInstancesTest {
    private OSMClient osmClient;
    private static final Logger logger = Logger.getLogger(AdminOperationsTest.class.getName());

    private String token,path=null;
    private String projectId;


    @Before
    public void setup(){
        logger.info("Before Setup");
        PropertiesHandler handler=new PropertiesHandler("local");
        OsmClientProperties properties;
        assert(handler.getNumberofOsmClients()>0);
        if(handler.getNumberofOsmClients()>0){
            properties=handler.getOsmClientProperties(handler.getNumberofOsmClients()-1);
            osmClient=new OSMClient(properties.getUri(), properties.getUser(), properties.getPassword(), properties.getProject(), properties.getVimAccount());
            osmClient.newCurrentToken();
            token=osmClient.getcurrentTOKEN_ID();
            path=handler.getPath();
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
    public void listALlNsiTest(){
        logger.info("Query information about multiple NS instances");
        assertNotNull(osmClient.nsInstances.listAllNSi());
    }

    @Test
    public void createNsiResourceTest(){

        String filepath=path+ "NS_instances/";
        filepath=filepath+"test1_nsi.json";
        JSONParser parser=new JSONParser();
        JSONObject nsdinfo=null;
        try {
            File newfile =new File(filepath);
            if (!newfile.exists() || !newfile.isFile()) {
                return ;
            }
            nsdinfo=(JSONObject)parser.parse(new FileReader(newfile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        logger.info("Create a new NS instance resource");

        JSONObject nsi=osmClient.nsInstances.createNSi(nsdinfo.toJSONString());
        
        assertNotNull(nsi);
        assertNotNull(nsi.get("id"));

        assertNull(osmClient.nsInstances.deleteNSiContent((String) nsi.get("id")));

    }
    @Test
    public void readNSiResourceInfoTest(){
        String filepath=path+ "NS_instances/";
        filepath=filepath+"test1_nsi.json";
        JSONParser parser=new JSONParser();
        JSONObject nsdinfo=null;
        try {
            File newfile =new File(filepath);
            if (!newfile.exists() || !newfile.isFile()) {
                return ;
            }
            nsdinfo=(JSONObject)parser.parse(new FileReader(newfile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject nsi=osmClient.nsInstances.createNSi(nsdinfo.toJSONString());
        assertNotNull(nsi);
        logger.info("Read an individual NS instance resource");
        assertNotNull(osmClient.nsInstances.readNSiResourceInfo((String) nsi.get("id")));

        assertNull(osmClient.nsInstances.deleteNSiResource((String) nsi.get("id")));

    }

    @Test
    public void deleteNSiResourceTest(){
        String filepath=path+ "NS_instances/";
        filepath=filepath+"test1_nsi.json";
        JSONParser parser=new JSONParser();
        JSONObject nsdinfo=null;
        try {
            File newfile =new File(filepath);
            if (!newfile.exists() || !newfile.isFile()) {
                return ;
            }
            nsdinfo=(JSONObject)parser.parse(new FileReader(newfile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject nsi=osmClient.nsInstances.createNSi(nsdinfo.toJSONString());
        logger.info("Read an individual NS instance resource");
        assertNotNull(nsi);
        assertNotNull(nsi.get("id"));

        assertNull(osmClient.nsInstances.deleteNSiResource((String) nsi.get("id")));
    }

    public JSONObject createNSI(){
        String filepath=path+ "NS_instances/";
        filepath=filepath+"test1_nsi.json";
        JSONParser parser=new JSONParser();
        JSONObject nsdinfo=null;
        try {
            File newfile =new File(filepath);
            if (!newfile.exists() || !newfile.isFile()) {
                return null;
            }
            nsdinfo=(JSONObject)parser.parse(new FileReader(newfile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        logger.info("Read an individual NS instance resource");
        return osmClient.nsInstances.createNSi(nsdinfo.toJSONString());
    }

    public JSONObject auxNsiInfo(String filename){
        String filepath=path+ "NS_instances/";
        filepath=filepath+filename;
        JSONParser parser=new JSONParser();
        JSONObject nsdinfo=null;
        try {
            File newfile =new File(filepath);
            if (!newfile.exists() || !newfile.isFile()) {
                return null;
            }
            nsdinfo=(JSONObject)parser.parse(new FileReader(newfile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return nsdinfo;
    }

    @Test
    public void instantiateNSiTest() throws InterruptedException {
        JSONObject instanstiateInfo,terminateInfo,nsi=createNSI();

        Thread.sleep(6000);
        terminateInfo=auxNsiInfo("terminate_test1_nsi.json");
        assertNotNull(osmClient.nsInstances.terminateNSi((String) nsi.get("id"),terminateInfo));

        instanstiateInfo=auxNsiInfo("test1_nsi.json");
        Thread.sleep(6000);
        logger.info("Instantiate a NS. The precondition is that the NS instance must have been created and must be in NOT_INSTANTIATED state. As a result of the success of this operation, the NFVO creates a \"NS Lifecycle Operation Occurrence\" resource for the request, and the NS instance state becomes INSTANTIATED.\n");
        assertNotNull(osmClient.nsInstances.instantiateNSi((String) nsi.get("id"),instanstiateInfo));
        assertNull(osmClient.nsInstances.deleteNSiResource((String) nsi.get("id")));
    }

    @Test
    public void scaleNsiTest(){
        logger.info("Scale a NS instance. The precondition is that the NS instance must have been created and must be in INSTANTIATED state. As a result of the success of this operation, the NFVO creates a \"NS Lifecycle Operation Occurrence\" resource for the request, and the NS instance state remains INSTANTIATED.\n");
        //NOT POSSIBLE TO TEST right now
    }

    @Test
    public void terminateNsiTest() throws InterruptedException {
        JSONObject instanstiateInfo,terminateInfo,nsi=createNSI();

        logger.info("Terminate a NS instance. The precondition is that the NS instance must have been created and must be in INSTANTIATED state. As a result of the success of this operation, the NFVO creates a \"NS Lifecycle Operation Occurrence\" resource for the request, and the NS instance state becomes NOT_INSTANTIATED.\n");
        Thread.sleep(6000);
        terminateInfo=auxNsiInfo("terminate_test1_nsi.json");
        assertNotNull(osmClient.nsInstances.terminateNSi((String) nsi.get("id"),terminateInfo));

        instanstiateInfo=auxNsiInfo("test1_nsi.json");
        Thread.sleep(6000);
        assertNotNull(osmClient.nsInstances.instantiateNSi((String) nsi.get("id"),instanstiateInfo));

        logger.info("Terminate a NS instance.");
        Thread.sleep(6000);
        terminateInfo.replace("skip_terminate_primitives",false);
        assertNotNull(osmClient.nsInstances.terminateNSi((String) nsi.get("id"),terminateInfo));

        instanstiateInfo=auxNsiInfo("test1_nsi.json");
        Thread.sleep(6000);
        logger.info("Create a new NS instance resource\n");
        assertNotNull(osmClient.nsInstances.instantiateNSi((String) nsi.get("id"),instanstiateInfo));

        logger.info("Terminate a NS instance.");
        Thread.sleep(6000);
        terminateInfo.replace("autoremove",true);
        assertNotNull(osmClient.nsInstances.terminateNSi((String) nsi.get("id"),terminateInfo));

    }

    @Test
    public void actionNSiTest() throws InterruptedException {
        logger.info("Execute an action on a NS instance. The NS instance must have been created and must be in INSTANTIATED state.\n");
        JSONObject actionInfo,actionNsi,nsi=createNSI();

        actionInfo=auxNsiInfo("action_nsi.json");
        Thread.sleep(6000);
        assertNotNull(osmClient.nsInstances.actionNSi((String) nsi.get("id"),actionInfo));

        assertNull(osmClient.nsInstances.deleteNSiResource((String) nsi.get("id")));

    }

    @Test 
    public void readNSiContentTest() throws InterruptedException {
        JSONObject instanstiateInfo,terminateInfo,nsi=createNSI();

        logger.info("Query information about multiple NS isntances\n" );
        assertNotNull(osmClient.nsInstances.readNSiContent((String) nsi.get("id")));
        Thread.sleep(6000);
        terminateInfo=auxNsiInfo("terminate_test1_nsi.json");
        assertNotNull(osmClient.nsInstances.terminateNSi((String) nsi.get("id"),terminateInfo));

        logger.info("Query information about multiple NS isntances\n" );
        assertNotNull(osmClient.nsInstances.readNSiContent((String) nsi.get("id")));
        
        instanstiateInfo=auxNsiInfo("test1_nsi.json");
        Thread.sleep(6000);
        logger.info("Create a new NS instance resource\n");
        assertNotNull(osmClient.nsInstances.instantiateNSi((String) nsi.get("id"),instanstiateInfo));
        assertNull(osmClient.nsInstances.deleteNSiContent((String) nsi.get("id")));

    }

    @Test
    public void deleteNSiContentTest(){
        String filepath=path+ "NS_instances/";
        filepath=filepath+"test1_nsi.json";
        JSONParser parser=new JSONParser();
        JSONObject nsdinfo=null;
        try {
            File newfile =new File(filepath);
            if (!newfile.exists() || !newfile.isFile()) {
                return ;
            }
            nsdinfo=(JSONObject)parser.parse(new FileReader(newfile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject nsi=osmClient.nsInstances.createNSi(nsdinfo.toJSONString());
        logger.info("Read an individual NS instance resource");
        assertNotNull(nsi);
        assertNotNull(nsi.get("id"));

        assertNull(osmClient.nsInstances.deleteNSiContent((String) nsi.get("id")));
    }

    @Test
    public void listNSLcmOpOccsTest(){
        logger.info("Query information about multiple NS LCM Operation Occurrences\n");
        String filepath=path+ "NS_instances/";
        filepath=filepath+"test1_nsi.json";
        JSONParser parser=new JSONParser();
        JSONObject nsdinfo=null;
        try {
            File newfile =new File(filepath);
            if (!newfile.exists() || !newfile.isFile()) {
                return ;
            }
            nsdinfo=(JSONObject)parser.parse(new FileReader(newfile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject nsi=osmClient.nsInstances.createNSi(nsdinfo.toJSONString());

        assertNotNull(nsi);
        assertNotNull(osmClient.nsInstances.listNSLcmOpOccs());


        assertNull(osmClient.nsInstances.deleteNSiContent((String) nsi.get("id")));
    }

    @Test
    public void listNSLcmOpOccTest(){
        JSONObject nsi=createNSI();
        JSONArray list=osmClient.nsInstances.listNSLcmOpOccs();
        String lcmOpid=(String) ((JSONObject) list.get(0)).get("id");
        assertNotNull(lcmOpid);

        logger.info("Query information about an individual NS LCM Operation Occurrence\n");
        assertNotNull(osmClient.nsInstances.listNSLcmOpOcc(lcmOpid));

        assertNull(osmClient.nsInstances.deleteNSiContent((String) nsi.get("id")));
    }

    @Test
    public void listVNFInstacesTest() throws InterruptedException {
        JSONObject nsi=createNSI();
        assertNotNull(osmClient.nsInstances.listVNFInstaces());
        Thread.sleep(1000);
        assertNull(osmClient.nsInstances.deleteNSiResource((String) nsi.get("id")));
    }


    @Test
    public void listVNFInstaceTest() throws InterruptedException {
        JSONObject nsi=createNSI();
        JSONArray list=osmClient.nsInstances.listVNFInstaces();
        
        String vnfi=(String) ((JSONObject) list.get(0)).get("id");
        assertNotNull(vnfi);

        assertNotNull(osmClient.nsInstances.listVNFInstace(vnfi));
        Thread.sleep(1000);
        assertNull(osmClient.nsInstances.deleteNSiResource((String) nsi.get("id")));
    }
}
