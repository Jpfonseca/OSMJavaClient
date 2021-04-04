package Interfaces;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface NetSliceInstanceInterface {
    /**
     * Query information about multiple NetSlice isntances
     * @return
     */
    JSONArray listNsis();

    /**
     * Create a new NetSlice instance resource
     * @return
     */
    JSONObject createNsi(JSONObject Netslice);

    /**
     * Read an individual NetSlice instance resource
     * @param netSliceInstaceId
     * @return
     */
    JSONObject readNsiResource(String netSliceInstaceId);

    /**
     * Delete an individual NetSlice instance resource
     * @param netsliceInstanceId
     * @return
     */
    JSONObject deleteNsi(String netsliceInstanceId);

    /**
     * Instantiate a NetSlice.
     * The precondition is that the NetSlice instance must have been created and must be in NOT_INSTANTIATED state. As a result of the success of this operation, the NFVO creates a "NetSlice Lifecycle Operation Occurrence" resource for the request, and the NS instance state becomes INSTANTIATED.
     * @param netSliceInstaceId
     * @return
     */
    JSONObject instantiateNsi(String netSliceInstaceId, JSONObject instantiationRequest);

    /**
     * Terminate a NetSlice instance.
     * The precondition is that the NetSlice instance must have been created and must be in INSTANTIATED state. As a result of the success of this operation, the NFVO creates a "NetSlice Lifecycle Operation Occurrence" resource for the request, and the NetSlice instance state becomes NOT_INSTANTIATED.
     * @param netSliceInstaceId
     * @param terminationTime 
     * @return
     */
    JSONObject terminateNsi(String netSliceInstaceId,JSONObject terminationTime);

    /**
     * Execute an action on a NetSlice instance.
     * The NetSlice instance must have been created and must be in INSTANTIATED state.
     * @param netSliceInstaceId
     * @return
     */
    JSONObject executeActionNsi(String netSliceInstaceId, JSONObject actionNsi);

    /**
     * Query information about multiple NetSlice isntances
     * @return
     */
    JSONArray listNsiContent();

    /**
     * Create a new NetSlice instance
     * @param Nsi
     * @return
     */
    JSONObject createNsiContent(JSONObject Nsi);

    /**
     * Read an individual NetSlice instance resource
     * @param netsliceInstanceContentId
     * @return
     */
    JSONObject readNsiResourceContent(String netsliceInstanceContentId);

    /**
     * Delete an individual NS instance resource
     * @param netsliceInstanceContentId
     * @return
     */
    JSONObject deleteNsiResource(String netsliceInstanceContentId);

    /**
     * Query information about multiple NetSlice LCM Operation Occurrences
     * @return
     */
    JSONArray listLcmOpOccs();

    /**
     * Query information about an individual NetSlice LCM Operation Occurrence
     * @param nsiLcmOpOccId
     * @return
     */
    JSONObject listLcmOpOcc(String nsiLcmOpOccId);
}
