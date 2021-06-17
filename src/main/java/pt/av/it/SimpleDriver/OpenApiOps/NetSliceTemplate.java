package pt.av.it.SimpleDriver.OpenApiOps;

import java.io.File;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import pt.av.it.SimpleDriver.Interfaces.NetSliceTemplateInterface;
import pt.av.it.SimpleDriver.Requests.AsyncRequests;
import pt.av.it.SimpleDriver.Requests.Headers;

public class NetSliceTemplate implements NetSliceTemplateInterface {
    private static NetSliceTemplate instance;
    private final ApiCalls apiCalls;
    private final AsyncRequests http;
    
    private NetSliceTemplate(ApiCalls apiCalls) {
        this.apiCalls=apiCalls;
        this.http = apiCalls.getHttp();
    }

    public static NetSliceTemplate netSliceTemplate( ApiCalls apiCalls){
        if(instance==null){
            instance=new NetSliceTemplate(apiCalls);
        }
        return instance;
    }

    /**
     * Query information about multiple NetSlice template resources
     *
     * @return
     */
    @Override
    public JSONArray listNetSliceTemplates() {
        JSONArray answer = null;

        JSONObject response = http.response(http.get("/nst/v1/netslice_templates", apiCalls.getCurrentTOKEN_ID()));
        if ((int) response.get("status_code") == 200) {
            answer = (JSONArray) response.get("message");
        }
        return answer;
    }

    /**
     * Create a new NetSlice Template
     *
     * @param additionalProperties
     * @return
     */
    @Override
    public JSONObject createNetSliceTemplate(JSONObject additionalProperties) {

        JSONObject answer = null;
        JSONObject response = null;
        response = http.response(http.post("/nst/v1/netslice_templates", additionalProperties.toJSONString(), apiCalls.getCurrentTOKEN_ID()));
        if ((int) response.get("status_code") == 201) {
            answer = (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Read information about an individual NetSlice template resource
     *
     * @param netSliceTemplateId
     * @return
     */
    @Override
    public JSONObject readNetSliceTemplate(String netSliceTemplateId) {
        JSONObject answer = null;
        JSONObject response = null;
        response = http.response(http.get("/nst/v1/netslice_templates/" + netSliceTemplateId, apiCalls.getCurrentTOKEN_ID()));
        if ((int) response.get("status_code") == 200) {
            answer = (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Delete an individual NetSlice template resource
     *
     * @param netSliceTemplateId
     * @return
     */
    @Override
    public JSONObject deleteNetSliceTemplate(String netSliceTemplateId) {
        JSONObject answer = null;
        JSONObject response = null;
        response = http.response(http.delete("/nst/v1/netslice_templates/" + netSliceTemplateId, apiCalls.getCurrentTOKEN_ID()));
        if ((int) response.get("status_code") == 204) {
            if(response.get("message")==null){
                return null;
            }
            answer = (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Fetch individual NetSlice template resource
     *
     * @param netSliceTemplateId
     * @param artifactPath
     * @return
     */
    @Override
    public String fetchNetSliceTemplate(String netSliceTemplateId, String artifactPath) {
        String answer = null;
        JSONObject response = null;
        response = http.response(http.get("/nst/v1/netslice_templates/" + netSliceTemplateId + "/artifacts/" + artifactPath, apiCalls.getCurrentTOKEN_ID()));
        if ((int) response.get("status_code") == 200) {
            answer = (String) response.get("message");
        }
        return answer;
    }

    /**
     * Read NST od an on-boarded NetSlice Template
     *
     * @param netSliceTemplateId
     * @return
     */
    @Override
    public String readNetSliceTemplateNst(String netSliceTemplateId) {
        String answer = null;
        JSONObject response = null;
        response = http.response(http.get("/nst/v1/netslice_templates/" + netSliceTemplateId + "/nst/", apiCalls.getCurrentTOKEN_ID(),Headers.TEXT,Headers.JSON),Headers.TEXT );
        if ((int) response.get("status_code") == 200) {
            answer = (String) response.get("message");
        }
        return answer;
    }

    /**
     *Fetch the content of a NST
     *
     * @param netSliceTemplateId
     * @return
     */
    @Override
    public String fetchNstContent(String netSliceTemplateId) {
        String answer = null;
        JSONObject response = null;
        response = http.response(http.get("/nst/v1/netslice_templates/" + netSliceTemplateId + "/nst_content/", apiCalls.getCurrentTOKEN_ID(),Headers.ZIP,Headers.JSON));
        if ((int) response.get("status_code") == 200) {
            answer = (String) response.get("message");
        }
        return answer;
    }

    /**
     * Upload the content of a NST
     *
     * @param netSliceTemplateId
     * @param filePath
     * @return
     */
    @Override
    public String uploadNstContent(String netSliceTemplateId, String filePath) {
        String answer = null;
        String payload = "";
        JSONObject response = null;
        File file = new File(filePath);
        if(!file.exists()||!file.canRead()){
            System.out.println("File doesn't exist or can't be Read");
            return null;
        }
        
        response = http.response(http.put("/nst/v1/netslice_templates/" + netSliceTemplateId + "/nst_content", file , apiCalls.getCurrentTOKEN_ID(),Headers.JSON,Headers.JSON));

        if (response == null) {
            return "File Error";
        }
        if((int)response.get("status_code")==202){
            answer= (String) response.get("message");
        }

        if ((int) response.get("status_code") == 204) {
            return "";
        }
        return answer;
    }

    /**
     * Upload a NetSlice package by providing the content of the NetSlice package using a file
     *
     * @param filePath
     * @return
     */
    @Override
    public JSONObject uploadNSTPackage(String filePath) {
        JSONObject answer = null;
        JSONObject response = null;
        File newfile = new File(filePath);
        if (!newfile.exists() || !newfile.isFile()) {
            return null;
        }

        response = http.response(http.post("/nst/v1/netslice_templates_content/", newfile, apiCalls.getCurrentTOKEN_ID(), Headers.JSON,Headers.JSON));

        if ((int) response.get("status_code") == 201) {
            answer = (JSONObject) response.get("message");
        }
        if ((int) response.get("status_code") == 202) {
            answer = (JSONObject) response.get("message");
        }
        if ((int) response.get("status_code") == 204) {
            answer = (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Upload a NetSlice package by providing the content of the NetSlice package
     *
     * @return
     */
    @Override
    public JSONArray listNstContent() {
        JSONArray answer = null;
        JSONObject response = null;
        response = http.response(http.get("/nst/v1/netslice_templates_content", apiCalls.getCurrentTOKEN_ID()));

        if ((int) response.get("status_code") == 200) {
            answer = (JSONArray) response.get("message");
        }
        return answer;
    }

    /**
     * Read information about an individual NetSlice Template resource
     *
     * @param netSliceTemplateContentId
     * @return
     */
    @Override
    public JSONObject readNstResource(String netSliceTemplateContentId) {
        JSONObject answer = null;
        JSONObject response = null;

        response = http.response(http.get("/nst/v1/netslice_templates_content/" + netSliceTemplateContentId, apiCalls.getCurrentTOKEN_ID()));
        if ((int) response.get("status_code") == 200) {
            answer = (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Modify an individual NetSlice Template resource
     *
     * @param netSliceTemplateContentId
     * @param payload
     * @return
     */
    @Override
    public JSONObject modifyNSTResource(String netSliceTemplateContentId, JSONObject payload) {
        JSONObject answer = null;
        JSONObject response = null;

        response = http.response(http.put("/nst/v1/netslice_templates_content/" + netSliceTemplateContentId, payload, apiCalls.getCurrentTOKEN_ID()));
        if ((int) response.get("status_code") == 204) {
            return  (JSONObject) response.get("message");
        }
        return response;
    }

    /**
     * Delete an individual NetSlice Template resource
     *
     * @param netSliceTemplateContentId
     * @return
     */
    @Override
    public JSONObject deleteNSTResource(String netSliceTemplateContentId) {
        JSONObject answer = null;
        JSONObject response = null;

        response = http.response(http.delete("/nst/v1/netslice_templates_content/" + netSliceTemplateContentId, apiCalls.getCurrentTOKEN_ID()));
        if ((int) response.get("status_code") == 204) {
            answer = (JSONObject) response.get("message");
        }
        return answer;
    }
}
