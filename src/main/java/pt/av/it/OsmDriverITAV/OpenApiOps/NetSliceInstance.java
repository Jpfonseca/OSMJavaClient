/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.OpenApiOps;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.av.it.OsmDriverITAV.Interfaces.ApiCallsInterface;
import pt.av.it.OsmDriverITAV.Interfaces.NetSliceInstanceInterface;
import pt.av.it.OsmDriverITAV.OsmVsDriver;
import pt.av.it.OsmDriverITAV.Requests.AsyncRequests;

public class NetSliceInstance implements NetSliceInstanceInterface {
    
    private static final Logger log = LoggerFactory.getLogger(OsmVsDriver.class);
    
    private static NetSliceInstance instance;

    private ApiCallsInterface apiCalls;
    private AsyncRequests http;

    public NetSliceInstance(ApiCallsInterface apiCalls){
        this.apiCalls=apiCalls;
        this.http=apiCalls.getHttp();
    }

    public static NetSliceInstance netSliceInstance( ApiCallsInterface apiCalls){
        if(instance==null){
            instance=new NetSliceInstance(apiCalls);
        }
        return instance;
    }

    /**
     * Query information about multiple NetSlice instances
     *
     * @return List of Nsis
     */
    @Override
    public JSONArray listNsis() {
        JSONArray answer=null;
        JSONObject response=http.response(http.get("/nsilcm/v1/netslice_instances", apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;
    }
    
    
    /**
     * Create a new NetSlice instance resource
     *
     * @param Netslice
     * @return
     */
    @Override
    public JSONObject createNsi(JSONObject Netslice) {
        JSONObject answer=null;
        JSONObject response=http.response(http.post("/nsilcm/v1/netslice_instances" ,Netslice.toJSONString(),apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==201){
            answer= (JSONObject) response.get("message");
        }

        return answer;
    }

    /**
     * Read an individual NetSlice instance resource
     *
     * @param netSliceInstaceId
     * @return
     */
    @Override
    public JSONObject readNsiResource(String netSliceInstaceId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/nsilcm/v1/netslice_instances/"+netSliceInstaceId,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Delete an individual NetSlice instance resource
     *
     * @param netsliceInstanceId
     * @return
     */
    @Override
    public JSONObject deleteNsi(String netsliceInstanceId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.delete("/nsilcm/v1/netslice_instances/"+netsliceInstanceId ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==204){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Instantiate a NetSlice.
     * The precondition is that the NetSlice instance must have been created and must be in NOT_INSTANTIATED state. As a result of the success of this operation, the NFVO creates a "NetSlice Lifecycle Operation Occurrence" resource for the request, and the NS instance state becomes INSTANTIATED.
     *
     * @param netSliceInstaceId
     * @return
     */
    @Override
    public JSONObject instantiateNsi(String netSliceInstaceId, JSONObject instantiationRequest) {
        JSONObject answer=null;
        JSONObject response=http.response(http.post("/nsilcm/v1/netslice_instances/"+netSliceInstaceId+"/instantiate" ,instantiationRequest.toJSONString(),apiCalls.getCurrentTOKEN_ID()));
        log.info(response.toJSONString());
        if((int)response.get("status_code")==202){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Terminate a NetSlice instance.
     * The precondition is that the NetSlice instance must have been created and must be in INSTANTIATED state. As a result of the success of this operation, the NFVO creates a "NetSlice Lifecycle Operation Occurrence" resource for the request, and the NetSlice instance state becomes NOT_INSTANTIATED.
     *
     * @param netSliceInstaceId
     * @return
     */
    @Override
    public JSONObject terminateNsi(String netSliceInstaceId,JSONObject terminationTime) {
        JSONObject answer=null;
        JSONObject response=http.response(http.post("/nsilcm/v1/netslice_instances/"+netSliceInstaceId+"/terminate",terminationTime.toJSONString() ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==202){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Execute an action on a NetSlice instance.
     * The NetSlice instance must have been created and must be in INSTANTIATED state.
     *
     * @param netSliceInstaceId
     * @param actionNsi
     * @return
     */
    @Override
    public JSONObject executeActionNsi(String netSliceInstaceId, JSONObject actionNsi) {
        JSONObject answer=null;
        JSONObject response=http.response(http.post("/nsilcm/v1/netslice_instances/"+netSliceInstaceId+"/action",actionNsi.toJSONString() ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==202){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Query information about multiple NetSlice isntances
     *
     * @return
     */    @Override
    public JSONArray listNsiContent() {
        JSONArray answer=null;
        JSONObject response=http.response(http.get("/nsilcm/v1/netslice_instances_content", apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;
    }

    /**
     * Create a new NetSlice instance
     *
     * @param Nsi
     * @return
     */
    @Override
    public JSONObject createNsiContent(JSONObject Nsi) {
        JSONObject answer=null;
        JSONObject response=http.response(http.post("/nsilcm/v1/netslice_instances_content",Nsi.toJSONString() ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==201){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Read an individual NetSlice instance resource
     *
     * @param netsliceInstanceContentId
     * @return
     */
    @Override
    public JSONObject readNsiResourceContent(String netsliceInstanceContentId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/nsilcm/v1/netslice_instances_content/"+netsliceInstanceContentId,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Delete an individual NS instance resource
     *
     * @param netsliceInstanceContentId
     * @return
     */
    @Override
    public JSONObject deleteNsiResource(String netsliceInstanceContentId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.delete("/nsilcm/v1/netslice_instances_content/"+netsliceInstanceContentId ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==202){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /**
     * Query information about multiple NetSlice LCM Operation Occurrences
     *
     * @return
     */
    @Override
    public JSONArray listLcmOpOccs() {
        JSONArray answer=null;
        JSONObject response=http.response(http.get("/nsilcm/v1/nsi_lcm_op_occs", apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;
    }

    /**
     * Query information about an individual NetSlice LCM Operation Occurrence
     *
     * @param nsiLcmOpOccId
     * @return
     */
    @Override
    public JSONObject listLcmOpOcc(String nsiLcmOpOccId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/nsilcm/v1/nsi_lcm_op_occs/"+nsiLcmOpOccId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }
}
