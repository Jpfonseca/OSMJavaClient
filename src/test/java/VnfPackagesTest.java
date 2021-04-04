import Requests.OsmClientProperties;
import Requests.PropertiesHandler;
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

import static org.junit.jupiter.api.Assertions.*;

public class VnfPackagesTest {
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
        assertNotNull(osmClient);
        if(osmClient.listTokenById(token).containsKey("status_code")){
            return;
        }
        assertTrue(osmClient.deleteTokenById(token).contains("deleted"));
    }

    @Test
    public void listVnfPackagesTest(){
        logger.info("Query information about multiple VNF package resources");
        assertNotNull(osmClient.vnfPackages.listVnfPackages());
    }
    
    @Test
    public void createVnfPackageTest() throws ParseException {
        logger.info("Create a new VNF package resource\n");
        String additionalProperties="{}";
        JSONParser parser=new JSONParser();
        JSONObject vnfpkg,vnfinfo=(JSONObject) parser.parse(additionalProperties);
        vnfpkg=osmClient.vnfPackages.createVnfPackage(vnfinfo);
        assertNotNull(vnfpkg);

        vnfpkg=osmClient.vnfPackages.deleteVnfPackage((String) vnfpkg.get("id"));
        assertNull(vnfpkg);

    }

    @Test
    public void readPackagesTest(){
        logger.info("Read Read information about an individual VNF package resource\n");
        String packageId= "1f572262-c768-4108-9cab-6af8e95c6b67";

        assertNotNull(osmClient.vnfPackages.readVnfPackageInfo(packageId));
    }

    @Test
    public void deleteVnfPackageTest() throws ParseException {
        String additionalProperties="{}";
        JSONParser parser=new JSONParser();
        JSONObject deleteVnfPkg,vnfPkg,vnfinfo=(JSONObject) parser.parse(additionalProperties);
        vnfPkg=osmClient.vnfPackages.createVnfPackage(vnfinfo);
        assertNotNull(vnfPkg);
        
        logger.info("Delete Vnf Package\n");
        deleteVnfPkg=osmClient.vnfPackages.deleteVnfPackage((String) vnfPkg.get("id"));
        assertNull(deleteVnfPkg);
    }

    @Test
    public void modifyVnfPackageTest() throws ParseException {
        String additionalProperties="{}";
        JSONParser parser=new JSONParser();
        JSONObject vnfPkg, modifyVnfpkg,vnfnewinfo,vnfinfo=(JSONObject) parser.parse(additionalProperties);

        vnfPkg=osmClient.vnfPackages.createVnfPackage(vnfinfo);

        logger.info("Modify    Vnf Package\n");
        additionalProperties="{\n" +
                "\"additionalProp1\":{"+
                "\"short-name\":\"test\""+
                "}"+
                "}";

        vnfnewinfo=(JSONObject) parser.parse(additionalProperties);
        modifyVnfpkg=osmClient.vnfPackages.modifyVnfPackage((String) vnfPkg.get("id"),vnfnewinfo);
        assertNotNull(modifyVnfpkg);
        assertNull(osmClient.vnfPackages.deleteVnfPackage((String) vnfPkg.get("id")));
    }
    
    @Test
    public void readVnfdOnboardedTest(){
        logger.info("Fetch an on-boarded VNF package\n");
        String packageId= "5025c5cd-d1fd-4b93-b8f1-c202a2e81c8d";
        assertNotNull(osmClient.vnfPackages.readVnfdFromOnboardedVnf(packageId));
    }

    @Test
    public void fetchOnboardedVnfPackageTest(){
        logger.info("Upload a VNF package by providing the content of the VNF package");
        String packageId= "5025c5cd-d1fd-4b93-b8f1-c202a2e81c8d";
        assertNotNull(osmClient.vnfPackages.fetchOnboardedVnfPackage(packageId));

    }

    @Test
    public void uploadVnfPackageTest() throws ParseException {
        String additionalProperties="{}";
        JSONParser parser=new JSONParser();
        JSONObject deleteVnfPkg,vnfPkg,uploadVnfPkg,vnfinfo=(JSONObject) parser.parse(additionalProperties);
        vnfPkg=osmClient.vnfPackages.createVnfPackage(vnfinfo);

        logger.info("Upload a VNF package by providing the content of the VNF package");
        String filepath=path+"VNFs/";
        uploadVnfPkg=osmClient.vnfPackages.uploadVnfPackage((String) vnfPkg.get("id"),filepath+"test1_vnfd.json");
        if (uploadVnfPkg.containsKey("status_code")){
            assertNull(uploadVnfPkg);
        }
        assertNotNull(uploadVnfPkg);

        deleteVnfPkg=osmClient.vnfPackages.deleteVnfPackage((String) vnfPkg.get("id"));
        assertNull(deleteVnfPkg);
    }

    @Test
    public void fetchVnfPackageArtifactTest() {
        //Not Implemented
        //Refer to :https://osm.etsi.org/wikipub/index.php/Creating_your_own_VNF_package_(Release_THREE)
    }


    @Test
    public void uploadVnfPackageContentTest(){
        logger.info("Upload a VNF package by providing the content of the VNF package");

        String filepath=path+"VNFs/";
        JSONObject deleteVnfPkg, vnfPkg=osmClient.vnfPackages.uploadVnfPackageContent(filepath+"test2_vnfd.json");
        assertNotNull(vnfPkg);

        deleteVnfPkg=osmClient.vnfPackages.deleteVnfPackage((String) vnfPkg.get("id"));
        assertNull(deleteVnfPkg);
    }

    @Test
    public void listVnfPackageResourcesTest(){
        logger.info("Query information about multiple VNF package resources\n");

        assertNotNull(osmClient.vnfPackages.listVnfPackageResources());
    }

    @Test
    public void readVnfPackageResourceTest(){
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String filepath=path+"VNFs/";
        JSONObject deleteVnfPkg, vnfPkg=osmClient.vnfPackages.uploadVnfPackageContent(filepath+"test2_vnfd.json");
        assertNotNull(vnfPkg);

        logger.info("Query information about multiple VNF package resources\n");

        assertNotNull(osmClient.vnfPackages.readVnfPackageResource((String) vnfPkg.get("id")));

        deleteVnfPkg=osmClient.vnfPackages.deleteVnfPackage((String) vnfPkg.get("id"));
        assertNull(deleteVnfPkg);
    }

    @Test
    public void modifyVnfPackageResourceTest() throws InterruptedException {
        logger.info("Upload a VNF package by providing the content of the VNF package");

        Thread.sleep(6000);
        String filepath=path+"VNFs/";
        JSONObject deleteVnfPkg, vnfPkg=osmClient.vnfPackages.uploadVnfPackageContent(filepath+"test2_vnfd.json");
        assertNotNull(vnfPkg);

        Thread.sleep(6000);
        logger.info("Modify an individual VNF package resource");
        filepath=filepath+"test1_vnfd.json";
        JSONParser parser=new JSONParser();
        JSONObject vnfinfo=null;
        String vnfId=(String) vnfPkg.get("id");
        try {
            File newfile =new File(filepath);
            if (!newfile.exists() || !newfile.isFile()) {
                return ;
            }
            vnfinfo=(JSONObject)parser.parse(new FileReader(newfile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assertNotNull(osmClient.vnfPackages.modifyVnfPackageResource(vnfId,vnfinfo));

        deleteVnfPkg=osmClient.vnfPackages.deleteVnfPackage((String) vnfPkg.get("id"));
        assertNull(deleteVnfPkg);
    }

    @Test
    public void deleteVnfPackageResourceTest() throws InterruptedException {
        logger.info("Upload a VNF package by providing the content of the VNF package");

        Thread.sleep(6000);
        String filepath=path+"VNFs/";
        JSONObject deleteVnfPkg, vnfPkg=osmClient.vnfPackages.uploadVnfPackageContent(filepath+"test2_vnfd.json");
        assertNotNull(vnfPkg);

        Thread.sleep(6000);
        logger.info("Modify an individual VNF package resource");
        assertNotNull(osmClient.vnfPackages.deleteVnfPackageResource((String) vnfPkg.get("id")));

        deleteVnfPkg=osmClient.vnfPackages.deleteVnfPackage((String) vnfPkg.get("id"));
        assertNull(deleteVnfPkg);
    }

}