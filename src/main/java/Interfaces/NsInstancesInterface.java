package Interfaces;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface NsInstancesInterface {
    /***
     * Query information about multiple NS isntances
     * @return
     */
    JSONArray listAllNSi();

    /***
     * Create a new NS instance resource
     * @return
     */
    JSONObject createNSiResource(JSONObject networkService);

    /***
     * Read an individual NS instance resource
     * @param nsInstanceId
     * @return
     */
    JSONObject readNSiResourceInfo(String nsInstanceId);

    /***
     * Delete an individual NS instance resource
     * @param nsInstanceId
     * @return
     */
    JSONObject deleteNSiResource(String nsInstanceId);

    /***
     * Instantiate a NS. The precondition is that the NS instance must have been created and must be in NOT_INSTANTIATED state.
     * As a result of the success of this operation, the NFVO creates a "NS Lifecycle Operation Occurrence" resource for the request, and the NS instance state becomes INSTANTIATED.
     * @param nsInstanceId
     * @return
     */
    JSONObject instantiateNSi(String nsInstanceId,JSONObject networkService);

    /***
     * Scale a NS instance.
     * The precondition is that the NS instance must have been created and must be in INSTANTIATED state.
     * As a result of the success of this operation, the NFVO creates a "NS Lifecycle Operation Occurrence" resource for the request, and the NS instance state remains INSTANTIATED.
     * @param nsInstanceId
     * @return
     */
    JSONObject scaleNSi(String nsInstanceId,JSONObject scaleNsRequest);

    /***
     * Terminate a NS instance.
     * The precondition is that the NS instance must have been created and must be in INSTANTIATED state.
     * As a result of the success of this operation, the NFVO creates a "NS Lifecycle Operation Occurrence" resource for the request, and the NS instance state becomes NOT_INSTANTIATED.
     * @param nsInstanceId
     * @return
     */
    JSONObject terminateNSi(String nsInstanceId,JSONObject terminateNsRequest);

    /***
     * Execute an action on a NS instance.
     * The NS instance must have been created and must be in INSTANTIATED state.
     * @param nsInstanceId
     * @return
     */
    JSONObject actionNSi(String nsInstanceId, JSONObject NSinstanceActionRequest);

    /***
     *
     *
     * The NS info should be passed as argument
     *
     *
     */

    /***
     *Query information about multiple NS isntances
     * @return
     */
    JSONArray listNsiContent();

    /**
     * Create a new NS instance
     * @param nsJsonpayload
     * @return
     */
    JSONObject createNSi(String nsJsonpayload);

    /**
     * Read an individual NS instance resource
     * @param nsInstanceContentId
     * @return
     */
    JSONObject readNSiContent(String nsInstanceContentId);

    /**
     * Delete an individual NS instance resource
     * @param nsInstanceContentId
     * @return
     */
    JSONObject deleteNSiContent(String nsInstanceContentId);

    /**
     * Query information about multiple NS LCM Operation Occurrences
     * @return
     */
    JSONArray listNSLcmOpOccs();

    /**
     * Query information about an individual NS LCM Operation Occurrence
     * @param nsLcmOpOccId
     * @return
     */
    JSONObject listNSLcmOpOcc(String nsLcmOpOccId);

    /**
     *
     * Vnf Information
     *
     */

    /**
     *Query information about multiple VNF Instances
     * @return
     */
    JSONArray listVNFInstaces();

    /**
     * Query information about an individual VNF Instance
     * @param vnfInstanceId
     * @return
     */
    JSONObject listVNFInstace(String vnfInstanceId);

}
