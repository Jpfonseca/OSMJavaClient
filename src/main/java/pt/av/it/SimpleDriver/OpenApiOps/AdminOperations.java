package pt.av.it.SimpleDriver.OpenApiOps;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import pt.av.it.SimpleDriver.Interfaces.AdminOperationsInterface;
import pt.av.it.SimpleDriver.Requests.AsyncRequests;

public class AdminOperations extends Admin implements AdminOperationsInterface {

    private static AdminOperations instance;

    private ApiCalls apiCalls;
    private AsyncRequests http;

    public AdminOperations(ApiCalls apiCalls){
        super(apiCalls);
        this.apiCalls=apiCalls;
        this.http=apiCalls.getHttp();
    }

    public static AdminOperations adminOperations( ApiCalls apiCalls){
        if(instance==null){
            instance=new AdminOperations(apiCalls);
        }
        return instance;
    }

    /***
     * Project Operations
     **/
    @Override
    public JSONArray listProjects() {

        JSONArray answer=null;
        JSONObject response=http.response(http.get("/admin/v1/projects", apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;
        
    }

    @Override
    public JSONObject newProject(JSONObject projectProperties) {
        JSONObject answer=null;
        JSONObject response=http.response(http.post("/admin/v1/projects" ,projectProperties.toJSONString(),apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==201){
            answer= (JSONObject) response.get("message");
        }

        return answer;
    }

    @Override
    public JSONObject listProjectById(String projectId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/admin/v1/projects/"+projectId ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }

        return answer;
    }

    @Override
    public JSONObject modifyProjectById(String projectId, JSONObject projectProperties) {
        JSONObject answer=null;
        JSONObject response=http.response(http.patch("/admin/v1/projects/"+projectId,projectProperties.toJSONString() ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200||(int)response.get("status_code")==204){
            answer= (JSONObject) response.get("message");
        }

        return answer;
    }

    @Override
    public JSONObject deleteProjectById(String projectID) {
        JSONObject answer=null;
        JSONObject response=http.response(http.delete("/admin/v1/projects/"+projectID ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200|(int)response.get("status_code")==202|(int)response.get("status_code")==204){
            answer= (JSONObject) response.get("message");
        }

        return answer;
    }

    /***
     * Vim Account Operations
     **/
    @Override
    public JSONArray listVimAccounts() {
        JSONArray answer=null;
        JSONObject response=http.response(http.get("/admin/v1/vim_accounts", apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;
    }

    @Override
    public JSONObject newVimAccount(JSONObject vimAccountProperties) {
        JSONObject answer=null;
        JSONObject response=http.response(http.post("/admin/v1/vim_accounts",vimAccountProperties.toJSONString(), apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;

    }

    @Override
    public JSONObject listVimAccountById(String vimAccountId) {

        JSONObject answer=null;
        JSONObject response=http.response(http.get("/admin/v1/vim_accounts/"+vimAccountId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    @Override
    public JSONObject modifyVimAccountById(String vimAccountId, JSONObject vimAccountInfo) {
        JSONObject answer=null;
        JSONObject response=http.response(http.patch("/admin/v1/vim_accounts/"+vimAccountId,vimAccountInfo.toJSONString(), apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    @Override
    public JSONObject deleteVimAccountById(String vimAccountId) {

        JSONObject answer=null;
        JSONObject response=http.response(http.delete("/admin/v1/vim_accounts/"+vimAccountId, apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }
        return answer;
    }

    /***
     * Vim Operations
     **/
    @Override
    public JSONArray listVims() {
        JSONArray answer=null;
        JSONObject response=http.response(http.get("/admin/v1/vims", apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;
    }

    @Override
    public JSONObject newVim(JSONObject vimProperties) {
        JSONObject answer=null;
        JSONObject response=http.response(http.post("/admin/v1/vims/" ,vimProperties.toJSONString(),apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }

        return answer;
    }

    @Override
    public JSONObject listVimById(String vimId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.get("/admin/v1/vims/"+vimId ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }

        return answer;
    }

    @Override
    public JSONObject modifyVimById(String vimId, JSONObject vimInfo) {
        JSONObject answer=null;
        JSONObject response=http.response(http.patch("/admin/v1/vims/"+vimId,vimInfo.toJSONString() ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONObject) response.get("message");
        }

        return answer;
    }

    @Override
    public JSONObject deleteVimById(String vimId) {
        JSONObject answer=null;
        JSONObject response=http.response(http.delete("/admin/v1/vims/"+vimId ,apiCalls.getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200||(int)response.get("status_code")==202||(int)response.get("status_code")==204){
            answer= (JSONObject) response.get("message");
        }

        return answer;
    }

}
