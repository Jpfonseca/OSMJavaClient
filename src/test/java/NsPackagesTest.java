/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import pt.av.it.OsmDriverITAV.Requests.OsmClientProperties;
import pt.av.it.OsmDriverITAV.Requests.PropertiesHandler;

public class NsPackagesTest {
    private pt.av.it.OsmDriverITAV.OSMClient osmClient;
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
            osmClient=new pt.av.it.OsmDriverITAV.OSMClient(properties.getUri(), properties.getUser(), properties.getPassword(), properties.getProject(), properties.getVimAccount());
            osmClient.newCurrentToken();
            token=osmClient.getCurrentTOKEN_ID();
            path=handler.getPath();
        }
        if (token==null||token.length()<1){
            osmClient.setCurrentTOKEN_ID(token);
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
    public void listNsDescriptorsTest(){
        logger.info("Query information about multiple VNF package resources");
        assertNotNull(osmClient.nsPackages.listNsDescriptors());
    }
    
    @Test
    public void createNewNsDescriptorTest() throws ParseException {
        logger.info("Create a new NS descriptor resource\n");

        String additionalProperties="{}";
        JSONParser parser=new JSONParser();
        JSONObject nsd,nsdinfo=(JSONObject) parser.parse(additionalProperties);
        nsd=osmClient.nsPackages.createNewNsDescriptor(nsdinfo);
        assertNotNull(nsd);

        assertNotNull((String)nsd.get("id"));

        assertNull(osmClient.nsPackages.deleteNsDescriptor((String) nsd.get("id")));
    }

    @Test
    public void readNsDecriptorTest() throws ParseException {
        String additionalProperties="{}";
        JSONParser parser=new JSONParser();
        JSONObject nsd,nsdinfo=(JSONObject) parser.parse(additionalProperties);
        nsd=osmClient.nsPackages.createNewNsDescriptor(nsdinfo);
        assertNotNull(nsd);

        logger.info("Read information about an individual NS descriptor resource\n");
        assertNotNull(osmClient.nsPackages.readNsDescriptor((String) nsd.get("id")));

        assertNull(osmClient.nsPackages.deleteNsDescriptor((String) nsd.get("id")));
    }

    @Test
    public void deleteNsDescriptorTest() throws ParseException {
        String additionalProperties="{}";
        JSONParser parser=new JSONParser();
        JSONObject nsd,nsdinfo=(JSONObject) parser.parse(additionalProperties);
        nsd=osmClient.nsPackages.createNewNsDescriptor(nsdinfo);
        assertNotNull(nsd);

        logger.info("Delete an individual VNF package resource\n");
        assertNull(osmClient.nsPackages.deleteNsDescriptor((String) nsd.get("id")));
    }

    @Test
    public void modifyNsDescriptorTest() throws ParseException {
        String additionalProperties="{}";
        JSONParser parser=new JSONParser();
        JSONObject modifynsd,nsd,vnfnewinfo,nsdnewinfo,nsdinfo=(JSONObject) parser.parse(additionalProperties);
        nsd=osmClient.nsPackages.createNewNsDescriptor(nsdinfo);
        assertNotNull(nsd);

        logger.info("Modify the data of an individual NS descriptor resource\n");
        nsd.put("description","test");
        
        modifynsd=osmClient.nsPackages.modifyNsDescriptor((String) nsd.get("id"),nsd);

        assertNotNull(modifynsd);

        assertNull(osmClient.nsPackages.deleteNsDescriptor((String) nsd.get("id")));
    }

    @Test
    public void listNsDescriptorInfoTest(){
        logger.info("Fetch individual NS package artifact\n");
        assertNotNull(osmClient.nsPackages.listNsDescriptorInfo("d6ac48d8-5ee2-4df8-8e8d-7dff0eb10c08"));
    }
    
    @Test
    public void uploadNsdTest() throws ParseException {
        String additionalProperties="{}";
        JSONParser parser=new JSONParser();
        JSONObject nsd,nsdinfo=(JSONObject) parser.parse(additionalProperties);
        nsd=osmClient.nsPackages.createNewNsDescriptor(nsdinfo);
        assertNotNull(nsd);

        String filepath=path+ "NSDs/";

        osmClient.nsPackages.uploadNsd((String) nsd.get("id"), filepath+"test2_nsd.json");

        assertNull(osmClient.nsPackages.deleteNsDescriptor((String) nsd.get("id")));

    }
    
    @Test
    public void fetchNsPackageArtifactTest(){
        //Not Implemented
        //logger.info("Fetch individual NS package artifact\n");

        //assertNotNull(osmClient.nsPackages.listNsPackageArtifact());
    }

    @Test
    public void readOnboardedNsPackageTest() throws ParseException, InterruptedException {
        Thread.sleep(6000);
        String additionalProperties="{}";
        JSONParser parser=new JSONParser();
        JSONObject nsd,nsdinfo=(JSONObject) parser.parse(additionalProperties);
        nsd=osmClient.nsPackages.createNewNsDescriptor(nsdinfo);
        assertNotNull(nsd);

        String filepath=path+ "NSDs/";

        osmClient.nsPackages.uploadNsd((String) nsd.get("id"), filepath+"test2_nsd.json");


        logger.info("Read NSD of an on-boarded NS package\n");

        assertNotNull(osmClient.nsPackages.readOnboardedNsdPackage((String) nsd.get("id")));

        assertNull(osmClient.nsPackages.deleteNsDescriptor((String) nsd.get("id")));
    }

    @Test
    public void uploadNsPackageTest() throws InterruptedException {
        Thread.sleep(6000);
        logger.info("Upload a NS package by providing the content of the NS package\n");

        String filepath=path+ "NSDs/";

        JSONObject nsd=osmClient.nsPackages.uploadNsPackage( filepath+"test2_nsd.json");
        assertNotNull(nsd);

        assertNull(osmClient.nsPackages.deleteNsDescriptor((String) nsd.get("id")));

    }
    
    @Test
    public void listNsDescriptorTest(){
        logger.info("Query information about multiple NS package resources\n");

        assertNotNull(osmClient.nsPackages.listNsDescriptorsInfo());
    }

    @Test
    public void readNsPackageResourcesTest() throws InterruptedException {
        Thread.sleep(6000);
        logger.info("Read information about an individual NS package resource\n");

        String filepath=path+ "NSDs/";

        JSONObject nsd=osmClient.nsPackages.uploadNsPackage( filepath+"test2_nsd.json");
        assertNotNull(nsd);

        assertNotNull(osmClient.nsPackages.readNsPackageResource((String)nsd.get("id")));

        assertNull(osmClient.nsPackages.deleteNsDescriptor((String) nsd.get("id")));
    }

    @Test
    public void modifyNsPackageResource() throws InterruptedException {
        Thread.sleep(6000);
        logger.info("Modify an individual NS package resource");
        String filepath=path+ "NSDs/";

        JSONObject nsd=osmClient.nsPackages.uploadNsPackage( filepath+"test2_nsd.json");
        assertNotNull(nsd);

        filepath=filepath+"test1_nsd.json";
        JSONParser parser=new JSONParser();
        JSONObject nsdinfo=null;
        String nsdId=(String) nsd.get("id");
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

        assertNotNull(osmClient.nsPackages.modifyNsDescriptor(nsdId,nsdinfo));

        assertNull(osmClient.nsPackages.deleteNsDescriptor((String) nsd.get("id")));

    }
    
    @Test
    public void deleteNsPackageResourceTest() throws InterruptedException {

        Thread.sleep(6000);

        String filepath=path+ "NSDs/";

        JSONObject nsd=osmClient.nsPackages.uploadNsPackage( filepath+"test2_nsd.json");
        assertNotNull(nsd);

        logger.info("Delete an individual NS package resource\n");

        assertNotNull(osmClient.nsPackages.deleteNsPackageResource((String) nsd.get("id")));

        assertNull(osmClient.nsPackages.deleteNsDescriptor((String) nsd.get("id")));
    }
}
