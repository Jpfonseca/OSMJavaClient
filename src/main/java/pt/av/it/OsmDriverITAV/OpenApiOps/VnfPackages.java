/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.OpenApiOps;

import java.io.File;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pt.av.it.OsmDriverITAV.Interfaces.ApiCallsInterface;
import pt.av.it.OsmDriverITAV.Interfaces.VnfPackagesInterface;
import pt.av.it.OsmDriverITAV.Requests.AsyncRequests;
import pt.av.it.OsmDriverITAV.Requests.Headers;
import pt.av.it.OsmDriverITAV.Requests.Serialization;

public class VnfPackages implements VnfPackagesInterface {
    private static VnfPackages instance;
    
    private AsyncRequests http;
    private ApiCallsInterface apiCalls;
    
    public VnfPackages(ApiCallsInterface apiCalls){
        this.apiCalls=apiCalls;
        this.http=apiCalls.getHttp();
    }

    public static VnfPackages vnfPackages( ApiCallsInterface apiCalls){
        if(instance==null){
            instance=new VnfPackages(apiCalls);
        }
        return instance;
    }

    /**
     * Query information about multiple VNF package resources
     *
     * @return
     */
    @Override
    public JSONArray listVnfPackages() {
        JSONArray answer=null;
        JSONObject response=http.response(http.get("/vnfpkgm/v1/vnf_packages", apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;

    }

    /**
     * Create a new VNF package resource
     *
     * @param additionalProperties
     * @return
     */
    @Override
    public JSONObject createVnfPackage(JSONObject additionalProperties) {
        JSONObject answer=null;
        JSONObject response=http.response(http.post("/vnfpkgm/v1/vnf_packages",  additionalProperties.toJSONString(),apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==201){
            answer= (JSONObject) response.get("message");
        }
        return answer;

    }

    /**
     * Read information about an individual VNF package resource
     *
     * @param vnfPkgId
     * @return
     */
    @Override
    public JSONObject readVnfPackageInfo(String vnfPkgId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/vnfpkgm/v1/vnf_packages/"+vnfPkgId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        else {
            return response;
        }
        return answer;
    }

    /**
     * Delete an individual VNF package resource
     *
     * @param vnfPkgId
     * @return
     */
    @Override
    public JSONObject deleteVnfPackage(String vnfPkgId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.delete("/vnfpkgm/v1/vnf_packages/"+vnfPkgId,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==204){
            if(response.get("message")==null){
                return null;
            }
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Modify an individual VNF package resource
     *https://opensourcemano.slack.com/apps/manage?utm_source=in-prod&utm_medium=inprod-apps_link-slack_menu-click
     * @param vnfPkgId
     * @param vnfPkgInfoModifications
     * @return
     */
    @Override
    public JSONObject modifyVnfPackage(String vnfPkgId, JSONObject vnfPkgInfoModifications) {
        JSONObject answer=null;
        JSONObject response=http.response(http.patch("/vnfpkgm/v1/vnf_packages/"+vnfPkgId,vnfPkgInfoModifications.toJSONString(), apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        else{
            return response;
        }
        return answer;
    }

    /**
     * Read VNFD of an on-boarded VNF package
     *
     * @param vnfPkgId
     * @return
     */
    @Override
    public JSONObject readVnfdFromOnboardedVnf(String vnfPkgId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/vnfpkgm/v1/vnf_packages/"+vnfPkgId+"/vnfd", apiCalls.getCurrentTOKEN_ID(), Headers.TEXT, Headers.JSON),Headers.TEXT);
        if((int)response.get("status_code")==200){
            Serialization serialization=new Serialization();
            return serialization.jsonFromYamlString((String) response.get("message"));
        }
        else{
            return response;
        }
    }

    /**
     * Fetch an on-boarded VNF package
     *
     * @param vnfPkgId
     * @return
     */
    @Override
    public String fetchOnboardedVnfPackage(String vnfPkgId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/vnfpkgm/v1/vnf_packages/"+vnfPkgId+"/package_content", apiCalls.getCurrentTOKEN_ID(),Headers.TEXT,Headers.JSON), Headers.TEXT);
        if((int)response.get("status_code")==200){
            Serialization serialization=new Serialization();
            answer=serialization.jsonFromYamlString((String) response.get("message"));
        }
        else{
            return response.toJSONString();
        }
        return answer.toJSONString();
    }

    /**
     * Upload a VNF package by providing the content of the VNF package
     *
     * @param vnfPkgId
     * @param filePath
     * @return
     */
    @Override
    public JSONObject uploadVnfPackage(String vnfPkgId, String filePath) {
        File newfile = new File(filePath);
        if (!newfile.exists() || !newfile.isFile()) {
            return null;
        }

        JSONObject answer=null;
        JSONObject response=http.response(http.put("/vnfpkgm/v1/vnf_packages/"+vnfPkgId+"/package_content",  newfile, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==202){
            answer= (JSONObject) response.get("message");
        }
        else if((int)response.get("status_code")==204){
            if(response.get("message")==null){
                try {
                    return (JSONObject) (new JSONParser()).parse("{}");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return answer;
    }

    /**
     * Fetch individual VNF package artifact
     *
     * @param vnfPkgId
     * @param artifactPath
     * @return
     */
    @Override
    public JSONObject fetchVnfPackageArtifact(String vnfPkgId, String artifactPath) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/vnfpkgm/v1/vnf_packages/"+vnfPkgId+"/artifacts/"+artifactPath, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Upload a VNF package by providing the content of the VNF package
     *
     * @param filePath
     * @return
     */
    @Override
    public JSONObject uploadVnfPackageContent(String filePath) {
        JSONObject answer=null;
        File newfile = new File(filePath);
        if (!newfile.exists() || !newfile.isFile()) {
            return null;
        }


        JSONObject response=http.response(http.post("/vnfpkgm/v1/vnf_packages_content", newfile,apiCalls.getCurrentTOKEN_ID(),Headers.JSON,Headers.JSON));
        if((int)response.get("status_code")==201){
            answer= (JSONObject) response.get("message");
            return answer;
        }
        return response;
    }

    /**
     * Query information about multiple VNF package resources
     *
     * @return
     */
    @Override
    public JSONArray listVnfPackageResources() {
        JSONArray answer=null;
        JSONObject response=http.response(http.get("/vnfpkgm/v1/vnf_packages_content", apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;
    }

    /**
     * Read information about an individual VNF package resource
     *
     * @param packageContentId
     * @return
     */
    @Override
    public JSONObject readVnfPackageResource(String packageContentId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/vnfpkgm/v1/vnf_packages_content/"+packageContentId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Modify an individual VNF package resource
     *
     * @param packageContentId
     * @param vnfPkgInfoModifications
     * @return
     */
    @Override
    public JSONObject modifyVnfPackageResource(String packageContentId, JSONObject vnfPkgInfoModifications) {
        JSONObject answer=null;
        JSONObject response=http.response(http.put("/vnfpkgm/v1/vnf_packages_content/"+packageContentId,vnfPkgInfoModifications.toJSONString() ,apiCalls.getCurrentTOKEN_ID()));
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

    /**
     * Delete an individual VNF package resource
     *
     * @param packageContentId
     * @return
     */
    @Override
    public JSONObject deleteVnfPackageResource(String packageContentId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.delete("/vnfpkgm/v1/vnf_packages_content/"+packageContentId, apiCalls.getCurrentTOKEN_ID()));
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
