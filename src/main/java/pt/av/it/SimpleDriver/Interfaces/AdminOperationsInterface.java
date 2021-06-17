package pt.av.it.SimpleDriver.Interfaces;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface AdminOperationsInterface extends AdminInterface, InfrastructureOperationsInterface{
    /***
     * Project Operations
     **/


    JSONArray listProjects();

    JSONObject newProject(JSONObject projectProperties);

    JSONObject listProjectById(String projectId);

    JSONObject modifyProjectById(String projectId, JSONObject projectProperties);

    JSONObject deleteProjectById(String projectId);

    /***
     * Vim Account Operations
     **/

    JSONArray listVimAccounts();

    JSONObject listVimAccountById(String vimAccountId);

    JSONObject newVimAccount(JSONObject vimAccountProperties);

    JSONObject modifyVimAccountById(String vimAccountId, JSONObject vimAccountInfo);

    JSONObject deleteVimAccountById(String vimAccountId);


}
