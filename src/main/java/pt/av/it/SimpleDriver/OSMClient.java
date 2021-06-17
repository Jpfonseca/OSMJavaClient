package pt.av.it.SimpleDriver;
import org.json.simple.JSONObject;
import pt.av.it.SimpleDriver.Interfaces.OSMClientInterface;
import pt.av.it.SimpleDriver.OpenApiOps.*;
import pt.av.it.SimpleDriver.Requests.AsyncRequests;

// Add to jvm for debugging purposes -Djavax.net.debug=SSL,trustmanager,handshake

public class OSMClient extends Admin implements OSMClientInterface {
    private  String vimAccount;
    public final ApiCalls apiCalls;
    public final VnfPackages vnfPackages;
    public final NsPackages nsPackages;
    public final NsInstances nsInstances;
    public final NetSliceTemplate netSliceTemplateOps;
    public final NetSliceInstance netSliceInstanceOps;
    public final AdminOperations adminOperations;


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
