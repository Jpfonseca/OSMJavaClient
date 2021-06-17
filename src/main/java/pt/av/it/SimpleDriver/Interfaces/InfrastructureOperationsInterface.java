package pt.av.it.SimpleDriver.Interfaces;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface InfrastructureOperationsInterface {

    /***
     * Vim Operations
     **/

    JSONArray listVims();

    JSONObject newVim(JSONObject vimProperties);

    JSONObject listVimById(String vimId);

    JSONObject modifyVimById(String vimId, JSONObject vimInfo);

    JSONObject deleteVimById(String vimID);
}
