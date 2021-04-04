package OpenApiOps;

import Interfaces.NsInstancesInterface;
import Requests.AsyncRequests;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Network Service Instances related Operations
 */
public class NsInstances implements NsInstancesInterface {
    private static NsInstances instance;
    
    private final AsyncRequests http;
    private final ApiCalls apiCalls;
    
    private NsInstances(ApiCalls apiCalls) {
        this.http=apiCalls.getHttp();
        this.apiCalls=apiCalls;
    }

    public static NsInstances nsInstances( ApiCalls apiCalls){
        if(instance==null){
            instance=new NsInstances(apiCalls);
        }
        return instance;
    }

    /***
     * Query information about multiple NS instances
     * @return
     */
    @Override
    public JSONArray listAllNSi() {
        JSONArray answer=null;
        JSONObject response=http.response(http.get("/nslcm/v1/ns_instances", apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;
    }

    /***
     * Create a new NS instance resource
     * @return
     */
    @Override
    public JSONObject createNSiResource( JSONObject InstantiateNsRequest) {

        JSONObject answer=null;
        JSONObject response=http.response(http.post("/nslcm/v1/ns_instances",InstantiateNsRequest.toJSONString(),apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /***
     * Read an individual NS instance resource
     * @param nsInstanceId
     * @return
     */
    @Override
    public JSONObject readNSiResourceInfo(String nsInstanceId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/nslcm/v1/ns_instances/"+nsInstanceId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /***
     * Delete an individual NS instance resource
     * @param nsInstanceId
     * @return
     */
    @Override
    public JSONObject deleteNSiResource(String nsInstanceId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.delete("/nslcm/v1/ns_instances/"+nsInstanceId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /***
     * Instantiate a NS. The precondition is that the NS instance must have been created and must be in NOT_INSTANTIATED state.
     * As a result of the success of this operation, the NFVO creates a "NS Lifecycle Operation Occurrence" resource for the request, and the NS instance state becomes INSTANTIATED.
     * @param nsInstanceId
     * @return
     */
    @Override
    public JSONObject instantiateNSi(String nsInstanceId, JSONObject InstantiateNsRequest) {
        JSONObject answer=null;
        JSONObject response=http.response(http.post("/nslcm/v1/ns_instances/"+nsInstanceId+"/instantiate",InstantiateNsRequest.toJSONString(), apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==202){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /***
     * Scale a NS instance.
     * The precondition is that the NS instance must have been created and must be in INSTANTIATED state.
     * As a result of the success of this operation, the NFVO creates a "NS Lifecycle Operation Occurrence" resource for the request, and the NS instance state remains INSTANTIATED.
     * @param nsInstanceId
     * @return
     */
    @Override
    public JSONObject scaleNSi(String nsInstanceId, JSONObject scaleNsRequest) {
        JSONObject answer=null;
        JSONObject response=http.response(http.post("/nslcm/v1/ns_instances/"+nsInstanceId+"/scale",scaleNsRequest.toJSONString(), apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;

    }

    /***
     * Terminate a NS instance.
     * The precondition is that the NS instance must have been created and must be in INSTANTIATED state.
     * As a result of the success of this operation, the NFVO creates a "NS Lifecycle Operation Occurrence" resource for the request, and the NS instance state becomes NOT_INSTANTIATED.
     * @param nsInstanceId
     * @return
     */
    @Override
    public JSONObject terminateNSi(String nsInstanceId,JSONObject terminateNsRequest) {

        JSONObject answer=null;
        JSONObject response=http.response(http.post("/nslcm/v1/ns_instances/"+nsInstanceId+"/terminate",terminateNsRequest.toJSONString(), apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==202){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /***
     * Execute an action on a NS instance.
     * The NS instance must have been created and must be in INSTANTIATED state.
     * @param nsInstanceId
     * @return
     */
    @Override
    public JSONObject actionNSi(String nsInstanceId, JSONObject NSinstanceActionRequest) {

        JSONObject answer=null;
        JSONObject response=http.response(http.post("/nslcm/v1/ns_instances/"+nsInstanceId+"/action",NSinstanceActionRequest
                .toJSONString(), apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /***
     *Query information about multiple NS isntances
     * @return
     */
    @Override
    public JSONArray listNsiContent() {
        JSONArray answer=null;
        JSONObject response=http.response(http.get("/nslcm/v1/ns_instances_content", apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;
    }

    /**
     * Create a new NS instance
     *
     * @param nsJsonpayload
     * @return
     */
    @Override
    public JSONObject createNSi(String nsJsonpayload) {

        JSONObject answer=null;
        JSONObject response=http.response(http.post("/nslcm/v1/ns_instances_content",nsJsonpayload , apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==201){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Read an individual NS instance resource
     *
     * @param nsInstanceContentId
     * @return
     */
    @Override
    public JSONObject readNSiContent(String nsInstanceContentId) {

        JSONObject answer=null;
        JSONObject response=http.response(http.get("/nslcm/v1/ns_instances_content/"+nsInstanceContentId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Delete an individual NS instance resource
     *
     * @param nsInstanceContentId
     * @return
     */
    @Override
    public JSONObject deleteNSiContent(String nsInstanceContentId) {

        JSONObject answer=null;
        JSONObject response=http.response(http.delete("/nslcm/v1/ns_instances_content/"+nsInstanceContentId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Query information about multiple NS LCM Operation Occurrences
     *
     * @return
     */
    @Override
    public JSONArray listNSLcmOpOccs() {
        JSONArray answer=null;
        JSONObject response=http.response(http.get("/nslcm/v1/ns_lcm_op_occs", apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;
    }

    /**
     * Query information about an individual NS LCM Operation Occurrence
     *
     * @param nsLcmOpOccId
     * @return
     */
    @Override
    public JSONObject listNSLcmOpOcc(String nsLcmOpOccId) {

        JSONObject answer=null;
        JSONObject response=http.response(http.get("/nslcm/v1/ns_lcm_op_occs/"+nsLcmOpOccId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Query information about multiple VNF Instances
     *
     * @return
     */
    @Override
    public JSONArray listVNFInstaces() {
        JSONArray answer=null;
        JSONObject response=http.response(http.get("/nslcm/v1/vnf_instances", apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;
    }

    /**
     * Query information about an individual VNF Instance
     *
     * @param vnfInstanceId
     * @return
     */
    @Override
    public JSONObject listVNFInstace(String vnfInstanceId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/nslcm/v1/vnf_instances/"+vnfInstanceId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }
}
