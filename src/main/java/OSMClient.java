import Interfaces.OSMClientInterface;
import OpenApiOps.*;
import Requests.AsyncRequests;
import org.json.simple.JSONObject;

// Add to jvm for debugging purposes -Djavax.net.debug=SSL,trustmanager,handshake

public class OSMClient extends Admin implements OSMClientInterface {
    private  String vimAccount;
    protected final ApiCalls apiCalls;
    protected final VnfPackages vnfPackages;
    protected final NsPackages nsPackages;
    protected final NsInstances nsInstances;
    protected final NetSliceTemplate netSliceTemplateOps;
    protected final NetSliceInstance netSliceInstanceOps;
    protected final AdminOperations adminOperations;


    public OSMClient(String uri,String username, String password, String project_id,String vimAccount){
        super(new AsyncRequests(uri+"/osm"),username,password,project_id);

        //After authenticating sucessfully we can use the assigned token, unfortunately do to the way java handles arguments the Properties Handler is instanced twice :|

        uri=uri+"/osm";
        this.apiCalls= new ApiCalls(new AsyncRequests(uri) ,newToken());
        this.vnfPackages= VnfPackages.vnfPackages(apiCalls);
        this.nsPackages= NsPackages.nsPackages(apiCalls);
        this.nsInstances= NsInstances.nsInstances(apiCalls);
        this.netSliceTemplateOps= NetSliceTemplate.netSliceTemplate(apiCalls);
        this.netSliceInstanceOps= NetSliceInstance.netSliceInstance(apiCalls);
        this.adminOperations= AdminOperations.adminOperations(apiCalls);
    }


    @Override
    public void updateApiCallToken() {
        if(isApiCallTokenValid()){
            return;
        }
        apiCalls.setCurrentTOKEN_ID(newToken());
    }

    @Override
    public boolean isApiCallTokenValid() {
        String token=apiCalls.getCurrentTOKEN_ID();
        return isTokenValid(token);
    }

    @Override
    public String newToken() {
        JSONObject response=this.newCurrentToken();
        return (String) ((JSONObject) response.get("message")).get("id");
    }

    public String getVimAccount() {
        return vimAccount;
    }
}
