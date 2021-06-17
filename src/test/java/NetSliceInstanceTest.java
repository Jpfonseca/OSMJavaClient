import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import pt.av.it.SimpleDriver.Requests.OsmClientProperties;
import pt.av.it.SimpleDriver.Requests.PropertiesHandler;

public class NetSliceInstanceTest {
    private pt.av.it.SimpleDriver.OSMClient osmClient;
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
            osmClient=new pt.av.it.SimpleDriver.OSMClient(properties.getUri(), properties.getUser(), properties.getPassword(), properties.getProject(), properties.getVimAccount());
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
    public void listNsisTest(){
        logger.info("List NSIs");
        assertNotNull(token);
        
        assertNotNull(osmClient.netSliceInstanceOps.listNsis());
    }

    public void uploadNstContentTest() {

        logger.info("Upload a NetSlice package by providing the content of the NetSlice package using a file\n");
        JSONObject deleteNetSliceTemplate,newNetSliceTemplate;

        String netSliceTemplate="{}";
        JSONParser parser=new JSONParser();
        JSONObject slicetemplateinfo=null;
        try {
            slicetemplateinfo=(JSONObject) parser.parse(netSliceTemplate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        newNetSliceTemplate= osmClient.netSliceTemplateOps.createNetSliceTemplate(slicetemplateinfo);

        assertNotNull(newNetSliceTemplate);
        String templateId= (String) newNetSliceTemplate.get("id");

        String nstpath=path+"NSTs/";

        String uploadNstContent =osmClient.netSliceTemplateOps.uploadNstContent(templateId,nstpath+"slice_vim_emu_nst.json");
        assertNotNull(uploadNstContent);

        deleteNetSliceTemplate=osmClient.netSliceTemplateOps.deleteNetSliceTemplate(templateId);
        assertNull(deleteNetSliceTemplate);
    }

    @Test
    public void createNsiResourceTest() throws ParseException {
        String filepath=path+ "NSI_instances/";
        filepath=filepath+"test_create_nsi.json";
        JSONParser parser=new JSONParser();
        JSONObject sliceinfo=null;
        try {
            File newfile =new File(filepath);
            if (!newfile.exists() || !newfile.isFile()) {
                return ;
            }
            sliceinfo=(JSONObject)parser.parse(new FileReader(newfile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        logger.info("Create a new NetSlice instance resource\n");

        JSONObject newslice;
        newslice=osmClient.netSliceInstanceOps.createNsi(sliceinfo);
        assertNotNull(newslice);

        assertNull(osmClient.netSliceInstanceOps.deleteNsi((String) newslice.get("id")));
    }

    public JSONObject createNSI(){
        String filepath=path+ "NSI_instances/";
        filepath=filepath+"test_create_nsi.json";
        JSONParser parser=new JSONParser();
        JSONObject sliceinfo=null;
        try {
            File newfile =new File(filepath);
            if (!newfile.exists() || !newfile.isFile()) {
                return null;
            }
            sliceinfo=(JSONObject)parser.parse(new FileReader(newfile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        logger.info("Create a new NetSlice instance resource\n");

        JSONObject newslice;
        return osmClient.netSliceInstanceOps.createNsi(sliceinfo);
    }
    
    public JSONObject aux(String filename){
        String filepath=path+ "NSI_instances/";
        filepath=filepath+filename;
        JSONParser parser=new JSONParser();
        JSONObject sliceinfo=null;
        try {
            File newfile =new File(filepath);
            if (!newfile.exists() || !newfile.isFile()) {
                return null;
            }
            sliceinfo=(JSONObject)parser.parse(new FileReader(newfile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sliceinfo;
    }
    
    @Test
    public void readNsiResourceTest(){
        JSONObject nsi=createNSI();
        String nsi_id=(String)nsi.get("id");

        logger.info("Read an individual NetSlice instance resource");
        assertNotNull(osmClient.netSliceInstanceOps.readNsiResource(nsi_id));

        assertNull(osmClient.netSliceInstanceOps.deleteNsi(nsi_id));
    }

    @Test
    public void deleteNsiTest(){
        JSONObject nsi=createNSI();
        String nsi_id=(String)nsi.get("id");

        logger.info("Delete an individual NetSlice instance resource\n");
        assertNull(osmClient.netSliceInstanceOps.deleteNsi(nsi_id));
    }

    @Test
    public void instantiateNsiTest() throws InterruptedException {
        JSONObject nsi=createNSI();
        String nsi_id=(String)nsi.get("id");
        Thread.sleep(6000);
        JSONObject instantiation_test=aux("test_create_nsi.json");
        instantiation_test.put("netsliceInstanceId",nsi_id);
        logger.info("Instantiate a NetSlice. The precondition is that the NetSlice instance must have been created and must be in NOT_INSTANTIATED state. As a result of the success of this operation, the NFVO creates a \"NetSlice Lifecycle Operation Occurrence\" resource for the request, and the NS instance state becomes INSTANTIATED.\n");
        nsi=osmClient.netSliceInstanceOps.instantiateNsi(nsi_id,instantiation_test);
        assertNotNull(nsi);
        
        assertNull(osmClient.netSliceInstanceOps.deleteNsi(nsi_id));
    }

    @Test
    public void terminateNsiTest() throws InterruptedException {
        JSONObject nsi=createNSI();
        String nsi_id=(String)nsi.get("id");
        Thread.sleep(6000);

        JSONObject termination,instantiation_test=aux("test_create_nsi.json");
        instantiation_test.put("netsliceInstanceId",nsi_id);
        logger.info("Instantiate a NetSlice. The precondition is that the NetSlice instance must have been created and must be in NOT_INSTANTIATED state. As a result of the success of this operation, the NFVO creates a \"NetSlice Lifecycle Operation Occurrence\" resource for the request, and the NS instance state becomes INSTANTIATED.\n");
        nsi=osmClient.netSliceInstanceOps.instantiateNsi(nsi_id,instantiation_test);
        assertNotNull(nsi);

        logger.info("Terminate a NetSlice instance. The precondition is that the NetSlice instance must have been created and must be in INSTANTIATED state. As a result of the success of this operation, the NFVO creates a \"NetSlice Lifecycle Operation Occurrence\" resource for the request, and the NetSlice instance state becomes NOT_INSTANTIATED.\n");
        Thread.sleep(20000);
        termination=aux("termination_nsi.json");

        //Set specific time
        //assertNotNull(osmClient.netSliceInstanceOps.terminateNsi(nsi_id,termination));

        //Termination now
        termination.replace("terminationTime","0");
        assertNotNull(osmClient.netSliceInstanceOps.terminateNsi(nsi_id,termination));
        Thread.sleep(6000);

        assertNull(osmClient.netSliceInstanceOps.deleteNsi(nsi_id));
    }

    @Test
    public void executeActionNsiTest() throws InterruptedException {
        JSONObject nsi=createNSI();
        String nsi_id=(String)nsi.get("id");


        Thread.sleep(6000);
        JSONObject action,info,instantiation_test=aux("test_create_nsi.json");
        instantiation_test.put("netsliceInstanceId",nsi_id);
        logger.info("Instantiate a NetSlice. The precondition is that the NetSlice instance must have been created and must be in NOT_INSTANTIATED state. As a result of the success of this operation, the NFVO creates a \"NetSlice Lifecycle Operation Occurrence\" resource for the request, and the NS instance state becomes INSTANTIATED.\n");
        nsi=osmClient.netSliceInstanceOps.instantiateNsi(nsi_id,instantiation_test);
        assertNotNull(nsi);

        logger.info("Read an individual NetSlice instance resource");
        info=osmClient.netSliceInstanceOps.readNsiResource(nsi_id);
        assertNotNull(info);

        logger.info("Execute an action on a NetSlice instance. The NetSlice instance must have been created and must be in INSTANTIATED state.\n");
        if((((JSONObject)info.get("_admin")).get("nsiState"))=="INSTANTIATED"){
            //do action
            action=aux("action_nsi.json");
            action.replace("netsliceInstanceId",nsi_id);

            assertNotNull(osmClient.netSliceInstanceOps.executeActionNsi(nsi_id,action));
            //It must point to the new "NS Lifecycle Operation Occurrence" resource, i.e. an URI like ".../ns_lcm_op_occs/{nsLcmOpOccId}"
        }
        else {
            logger.info("NOT INstantiated" );
        }
        JSONObject termination=aux("termination_nsi.json");
        //Termination now
        termination.replace("terminationTime","0");
        assertNotNull(osmClient.netSliceInstanceOps.terminateNsi(nsi_id,termination));

        assertNull(osmClient.netSliceInstanceOps.deleteNsi(nsi_id));
    }

    @Test
    public void listNsiContentTest() throws InterruptedException {

        JSONObject nsi=createNSI();
        String nsi_id=(String)nsi.get("id");
        Thread.sleep(6000);

        JSONObject instantiation_test=aux("test_create_nsi.json");
        instantiation_test.put("netsliceInstanceId",nsi_id);
        logger.info("Instantiate a NetSlice. The precondition is that the NetSlice instance must have been created and must be in NOT_INSTANTIATED state. As a result of the success of this operation, the NFVO creates a \"NetSlice Lifecycle Operation Occurrence\" resource for the request, and the NS instance state becomes INSTANTIATED.\n");
        nsi=osmClient.netSliceInstanceOps.instantiateNsi(nsi_id,instantiation_test);
        assertNotNull(nsi);

        logger.info("Query information about multiple NetSlice instances");
        assertNotNull(osmClient.netSliceInstanceOps.listNsiContent());
        Thread.sleep(6000);

        JSONObject termination=aux("termination_nsi.json");
        //Termination now
        termination.replace("terminationTime","0");
        assertNotNull(osmClient.netSliceInstanceOps.terminateNsi(nsi_id,termination));

        assertNull(osmClient.netSliceInstanceOps.deleteNsi(nsi_id));
    }

    @Test
    public void createNsiContentTest() throws InterruptedException {
        JSONObject nsi,instantiation_info=aux("test_create_nsi.json");
        logger.info("Create a new NetSlice instance\n");
        nsi=osmClient.netSliceInstanceOps.createNsiContent(instantiation_info);
        assertNotNull(nsi);

        String nsi_id=(String)nsi.get("id");
        Thread.sleep(6000);

        JSONObject termination=aux("termination_nsi.json");
        //Termination now
        termination.replace("terminationTime","0");
        assertNotNull(osmClient.netSliceInstanceOps.terminateNsi(nsi_id,termination));

        assertNull(osmClient.netSliceInstanceOps.deleteNsi(nsi_id));
    }

    
    public JSONObject createNsiCont(){
        JSONObject instantiation_info=aux("test_create_nsi.json");
        logger.info("Create a new NetSlice instance\n");
        return osmClient.netSliceInstanceOps.createNsiContent(instantiation_info);
    }

    @Test
    public void readNsiResourceContentTest() throws InterruptedException {
        JSONObject nsi=createNsiCont();
        assertNotNull(nsi);
        String nsi_id=(String)nsi.get("id");

        logger.info("Read an individual NetSlice instance resource\n");
        JSONObject info=osmClient.netSliceInstanceOps.readNsiResourceContent(nsi_id);
        assertNotNull(info);

        Thread.sleep(6000);
        JSONObject termination=aux("termination_nsi.json");
        //Termination now
        termination.replace("terminationTime","0");
        assertNotNull(osmClient.netSliceInstanceOps.terminateNsi(nsi_id,termination));

        assertNull(osmClient.netSliceInstanceOps.deleteNsi(nsi_id));
    }

    @Test
    public void deleteNsiResourceTest() throws InterruptedException {
        JSONObject nsi=createNsiCont();
        assertNotNull(nsi);
        String nsi_id=(String)nsi.get("id");

        logger.info("Delete an individual NS instance resource\n");
        Thread.sleep(6000);

        JSONObject termination=aux("termination_nsi.json");
        //Termination now
        termination.replace("terminationTime","0");
        assertNotNull(osmClient.netSliceInstanceOps.terminateNsi(nsi_id,termination));

        assertNotNull(osmClient.netSliceInstanceOps.deleteNsiResource(nsi_id));
    }

    @Test
    public void listLcmOpOccsTest() throws InterruptedException {
        JSONObject nsi=createNsiCont();
        assertNotNull(nsi);
        String nsi_id=(String)nsi.get("id");

        Thread.sleep(6000);
        logger.info("Query information about multiple NetSlice LCM Operation Occurrences\n");
        assertNotNull(osmClient.netSliceInstanceOps.listLcmOpOccs());

        JSONObject termination=aux("termination_nsi.json");
        //Termination now
        termination.replace("terminationTime","0");
        assertNotNull(osmClient.netSliceInstanceOps.terminateNsi(nsi_id,termination));

        assertNull(osmClient.netSliceInstanceOps.deleteNsiResource(nsi_id));
    }

    @Test
    public void listLcmOpOccTest() throws InterruptedException {
        JSONObject nsi=createNsiCont();
        assertNotNull(nsi);
        String nsi_id=(String)nsi.get("id");
        String nsilcmop_id=(String)nsi.get("nsilcmop_id");

        Thread.sleep(6000);
        logger.info("Query information about an individual NetSlice LCM Operation Occurrence\n");
        JSONObject info=osmClient.netSliceInstanceOps.listLcmOpOcc(nsilcmop_id);

        JSONObject termination=aux("termination_nsi.json");
        //Termination now
        termination.replace("terminationTime","0");
        assertNotNull(osmClient.netSliceInstanceOps.terminateNsi(nsi_id,termination));

        assertNull(osmClient.netSliceInstanceOps.deleteNsiResource(nsi_id));
    }
}