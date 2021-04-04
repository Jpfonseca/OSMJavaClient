package OpenApiOps;

import Interfaces.NsPackagesInterface;
import Requests.AsyncRequests;
import Requests.Headers;
import Requests.Serialization;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;

public class NsPackages  implements NsPackagesInterface {
    private static NsPackages instance;
    private final AsyncRequests http;
    private final ApiCalls apiCalls;
    private NsPackages(ApiCalls apiCalls){
        this.apiCalls=apiCalls;
        this.http=apiCalls.getHttp();
    }

    public static NsPackages nsPackages( ApiCalls apiCalls){
        if(instance==null){
            instance=new NsPackages(apiCalls);
        }
        return instance;
    }


    /***
     * Query information about multiple VNF package resources
     * @return Array of NS Descriptors
     */
    @Override
    public JSONArray listNsDescriptors() {
        JSONArray answer=null;
        JSONObject response=http.response(http.get("/nsd/v1/ns_descriptors", apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;

    }

    @Override
    public JSONObject createNewNsDescriptor(JSONObject additionalProperties) {
        JSONObject answer=null;
        JSONObject response=http.response(http.post("/nsd/v1/ns_descriptors",additionalProperties.toJSONString(), apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==201){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    @Override
    public JSONObject readNsDescriptor(String nsdInfoId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/nsd/v1/ns_descriptors/"+nsdInfoId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    @Override
    public JSONObject deleteNsDescriptor(String nsdInfoId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.delete("/nsd/v1/ns_descriptors/"+nsdInfoId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==204){
            if(response.get("message")==null){
                return null;
            }
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    @Override
    public JSONObject modifyNsDescriptor(String nsdInfoId, JSONObject nsdInfoModifications) {
        JSONObject answer=null;
        JSONObject response=http.response(http.patch("/nsd/v1/ns_descriptors/"+nsdInfoId,nsdInfoModifications.toJSONString() ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==204){
            return response;
        }
        return response;
    }

    @Override
    public String listNsDescriptorInfo(String nsdInfoId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/nsd/v1/ns_descriptors/"+nsdInfoId+"/nsd_content", apiCalls.getCurrentTOKEN_ID(), Headers.TEXT,Headers.JSON), Headers.TEXT);
        if((int)response.get("status_code")==200){
            Serialization serialization=new Serialization();
            answer=serialization.jsonFromYamlString((String) response.get("message"));
        }
        else{
            return response.toJSONString();
        }
        return answer.toJSONString();
    }

    @Override
    public JSONObject uploadNsd(String nsdInfoId, String filePath) {
        File newfile = new File(filePath);
        if (!newfile.exists() || !newfile.isFile()) {
            return null;
        }

        JSONObject answer=null;
        JSONObject response= http.response(http.put("/nsd/v1/ns_descriptors/"+nsdInfoId+"/nsd_content" ,newfile,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==202){
            answer= (JSONObject) response.get("message");
        }
        else{
            return response;
        }
        return answer;
    }

    @Override
    public JSONObject listNsPackageArtifact(String nsdInfoId, String artifactPath) {
        JSONObject answer=null;
        JSONObject response= http.response(http.get("/nsd/v1/ns_descriptors/"+nsdInfoId+"/artifacts/"+artifactPath,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;

    }

    @Override
    public String readOnboardedNsdPackage(String nsdInfoId) {

        JSONObject answer=null;
        JSONObject response= http.response(http.get("/nsd/v1/ns_descriptors/"+nsdInfoId+"/nsd",apiCalls.getCurrentTOKEN_ID(), Headers.TEXT,Headers.JSON),Headers.TEXT);
        if((int)response.get("status_code")==200){
            Serialization serialization=new Serialization();
            answer=serialization.jsonFromYamlString((String) response.get("message"));
        }
        else{
            return response.toJSONString();
        }
        return answer.toJSONString();
    }

    @Override
    public JSONObject uploadNsPackage(String filePathNsPackage) {
        File newfile = new File(filePathNsPackage);
        if (!newfile.exists() || !newfile.isFile()) {
            return null;
        }
        
        JSONObject answer=null;
        JSONObject response= http.response(http.post("/nsd/v1/ns_descriptors_content",newfile ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==201){
            return  (JSONObject) response.get("message");
        }
        return response;
    }

    @Override
    public JSONArray listNsDescriptorsInfo() {
        JSONArray answer=null;
        JSONObject response= http.response(http.get("/nsd/v1/ns_descriptors_content",apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;
    }

    @Override
    public JSONObject readNsPackageResource(String nsdInfoId) {

        JSONObject answer=null;
        JSONObject response= http.response(http.get("/nsd/v1/ns_descriptors_content/"+nsdInfoId,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    @Override
    public JSONObject modifyNsPackageResource(String nsdInfoId, JSONObject nsdInfoModifications) {
        JSONObject answer=null;
        JSONObject response= http.response(http.put("/nsd/v1/ns_descriptors_content/"+nsdInfoId,nsdInfoModifications.toJSONString() ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==204){
            return response;
        }
        return response;
    }

    @Override
    public JSONObject deleteNsPackageResource(String nsdInfoId) {
        JSONObject answer=null;
        JSONObject response= http.response(http.delete("/nsd/v1/ns_descriptors_content/"+nsdInfoId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==204){
            if(response.get("message")==null){
                try {
                    return (JSONObject) (new JSONParser()).parse("{}");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }
}
