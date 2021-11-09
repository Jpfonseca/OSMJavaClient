/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import pt.av.it.OsmDriverITAV.Requests.OsmClientProperties;
import pt.av.it.OsmDriverITAV.Requests.PropertiesHandler;

public class NetSliceTemplateTest {
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
    public void listNetSliceTemplatesTest(){
        logger.info("List Network Slicing Templates");
        assertNotNull(osmClient.netSliceTemplateOps.listNetSliceTemplates());
    }

    @Test
    public void createNetSliceTemplateTest() throws ParseException {
        logger.info("Create Empty Network Slice Template");
        String netSliceTemplate;
        /**netSliceTemplate="{\n" +
                "  \"nst\": [\n" +
                "    {\n" +
                "      \"id\": \"slice_nst_it1\",\n" +
                "      \"name\": \"slice_iperf\",\n" +
                "      \"description\": \"NST with 1 subnets\",\n" +
                "      \"author\": \"ATNOG\",\n" +
                "      \"vendor\": \"IT-Aveiro\",\n" +
                "      \"SNSSAI_identifier\": {\n" +
                "        \"slice-service-type\": \"eMBB\"\n" +
                "      },\n" +
                "      \"quality-of-service\": {\n" +
                "        \"id\": 1\n" +
                "      },\n" +
                "      \"usageState\": \"NOT_IN_USE\",\n" +
                "      \"netslice_subnet\": [\n" +
                "      ],\n" +
                "      \"slice_vld\": [\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
         **/
        netSliceTemplate="{}";
        JSONParser parser=new JSONParser();
        JSONObject deleteNetSliceTemplate,newNetsliceTemplate,slicetemplateinfo=(JSONObject) parser.parse(netSliceTemplate);

        newNetsliceTemplate=osmClient.netSliceTemplateOps.createNetSliceTemplate(slicetemplateinfo);
        assertNotNull(newNetsliceTemplate);

        deleteNetSliceTemplate=osmClient.netSliceTemplateOps.deleteNetSliceTemplate((String) newNetsliceTemplate.get("id"));
        assertNull(deleteNetSliceTemplate);
    }

    public JSONObject createNST(){
        String netSliceTemplate="{}";
        JSONParser parser=new JSONParser();
        JSONObject slicetemplateinfo=null;
        try {
            slicetemplateinfo=(JSONObject) parser.parse(netSliceTemplate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return osmClient.netSliceTemplateOps.createNetSliceTemplate(slicetemplateinfo);
    }

    @Test
    public void readNetSliceTemplateTest(){
        logger.info("Read Network Slice Template Info");
        JSONObject nst=createNST();
    
        assertNotNull(osmClient.netSliceTemplateOps.readNetSliceTemplate((String) nst.get("id")));
        assertNull(osmClient.netSliceTemplateOps.deleteNetSliceTemplate((String) nst.get("id")));
    }

    @Test
    public void deleteNetSliceTemplateTest() throws ParseException {
        JSONObject deleteNetSliceTemplate,newNetsliceTemplate;

        newNetsliceTemplate=createNST();
        assertNotNull(newNetsliceTemplate);

        logger.info("Delete Network Slice Template\n");
        deleteNetSliceTemplate=osmClient.netSliceTemplateOps.deleteNetSliceTemplate((String) newNetsliceTemplate.get("id"));
        assertNull(deleteNetSliceTemplate);
    }

    @Test
    public void fetchNetSliceTemplateTest(){

    }

    @Test
    public void readNetSliceTemplateNstTest() throws InterruptedException {
        JSONObject deleteNetSliceTemplate,nst=createNST();

        assertNotNull(nst);
        String templateId= (String) nst.get("id");

        String nstpath=path+"NSTs/";

        String uploadNstContent =osmClient.netSliceTemplateOps.uploadNstContent(templateId,nstpath+"slice_basic_nst_test.json");
        assertNotNull(uploadNstContent);

        Thread.sleep(2000);
        logger.info("Read NST od an on-boarded NetSlice Template");
        assertNotNull(osmClient.netSliceTemplateOps.readNetSliceTemplateNst((String) nst.get("id")));

        deleteNetSliceTemplate=osmClient.netSliceTemplateOps.deleteNetSliceTemplate((String) nst.get("id"));
        assertNull(deleteNetSliceTemplate);

    }

    @Test
    public void fetchNstContentTest(){
        logger.info("Fetch the content of a NST\n");
        JSONObject nst=createNST();

        assertNull(osmClient.netSliceTemplateOps.fetchNstContent((String) nst.get("id")));
        assertNull(osmClient.netSliceTemplateOps.deleteNetSliceTemplate((String) nst.get("id")));
    }

    @Test
    public void uploadNstContentTest() {

        logger.info("Upload a NetSlice package by providing the content of the NetSlice package using a file\n");
        JSONObject deleteNetSliceTemplate,newNetSliceTemplate;

        newNetSliceTemplate=createNST();
        assertNotNull(newNetSliceTemplate);
        String templateId= (String) newNetSliceTemplate.get("id");

        String nstpath=path+"NSTs/";

        String uploadNstContent =osmClient.netSliceTemplateOps.uploadNstContent(templateId,nstpath+"slice_basic_nst_test.json");
        assertNotNull(uploadNstContent);

        deleteNetSliceTemplate=osmClient.netSliceTemplateOps.deleteNetSliceTemplate(templateId);
        assertNull(deleteNetSliceTemplate);
    }
     @Test
    public void uploadNSTPackageTest(){

        logger.info("Upload a NST package by providing a file\n");

        JSONObject deleteNetSliceTemplate;

        String nstpath=path+"NSTs/";

        JSONObject uploadNstContent =osmClient.netSliceTemplateOps.uploadNSTPackage(nstpath+"slice_basic_nst_test.json");
        assertNotNull(uploadNstContent);

        deleteNetSliceTemplate=osmClient.netSliceTemplateOps.deleteNetSliceTemplate((String) uploadNstContent.get("id"));
        assertNull(deleteNetSliceTemplate);
    }
    @Test
    public void listNstContentTest(){
        JSONObject deleteNetSliceTemplate,nst=createNST();

        assertNotNull(nst);
        String templateId= (String) nst.get("id");
        JSONArray list=osmClient.netSliceTemplateOps.listNstContent();
        assertNotNull(list);

        deleteNetSliceTemplate=osmClient.netSliceTemplateOps.deleteNetSliceTemplate((String) nst.get("id"));
        assertNull(deleteNetSliceTemplate);

    }
    @Test
    public void readNstResourceTest() throws InterruptedException {
        JSONObject deleteNetSliceTemplate,nst=createNST();

        assertNotNull(nst);
        String templateId= (String) nst.get("id");

        String nstpath=path+"NSTs/";

        String uploadNstContent =osmClient.netSliceTemplateOps.uploadNstContent(templateId,nstpath+"slice_basic_nst_test.json");
        assertNotNull(uploadNstContent);

        Thread.sleep(2000);
        logger.info("Read information about an individual NetSlice Template resource\n");
        JSONArray list=osmClient.netSliceTemplateOps.listNstContent();
        assertNotNull(list);

        String nstrid=(String) ((JSONObject) list.get(0)).get("_id");
        assertNotNull(nstrid);
        assertNotNull(osmClient.netSliceTemplateOps.readNstResource(nstrid));

        deleteNetSliceTemplate=osmClient.netSliceTemplateOps.deleteNetSliceTemplate((String) nst.get("id"));
        assertNull(deleteNetSliceTemplate);
    }
    @Test
    public void modifyNSTResourceTest() throws InterruptedException {
        JSONObject deleteNetSliceTemplate,nst=createNST();

        assertNotNull(nst);
        String templateId= (String) nst.get("id");

        String nstpath=path+"NSTs/";

        String uploadNstContent =osmClient.netSliceTemplateOps.uploadNstContent(templateId,nstpath+"slice_basic_nst_test.json");
        assertNotNull(uploadNstContent);

        nstpath=nstpath+"slice_basic_nst_test.json";
        JSONObject nstinfo = null;
        JSONParser parser=new JSONParser();
        try {
            File newfile =new File(nstpath);
            if (!newfile.exists() || !newfile.isFile()) {
                return ;
            }
            nstinfo=(JSONObject)parser.parse(new FileReader(newfile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        nstinfo.replace("name","slice_basic_nst_2_test");
        logger.info("Modify an individual NetSlice Template resource\n");

        assertNull(osmClient.netSliceTemplateOps.modifyNSTResource((String) nst.get("id"),nstinfo));
        

        deleteNetSliceTemplate=osmClient.netSliceTemplateOps.deleteNetSliceTemplate((String) nst.get("id"));
        assertNull(deleteNetSliceTemplate);
    }

    @Test
    public void deleteNSTResourceTest(){
        logger.info("Upload a NST package by providing a file\n");

        JSONObject deleteNetSliceTemplate;

        String nstpath=path+"NSTs/";

        JSONObject uploadNstContent =osmClient.netSliceTemplateOps.uploadNSTPackage(nstpath+"slice_basic_nst_test.json");
        assertNotNull(uploadNstContent);

        assertNull(osmClient.netSliceTemplateOps.deleteNSTResource((String) uploadNstContent.get("id")));

        deleteNetSliceTemplate=osmClient.netSliceTemplateOps.deleteNetSliceTemplate((String) uploadNstContent.get("id"));
        assertNull(deleteNetSliceTemplate);
    }
}
