/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 * Modified by:
 * - João Alegria (joao.p@av.it.pt)
 */
package pt.av.it.OsmDriverITAV;
import pt.av.it.OsmDriverITAV.Interfaces.OSMClientInterface;
import pt.av.it.OsmDriverITAV.OpenApiOps.Admin;
import pt.av.it.OsmDriverITAV.OpenApiOps.AdminOperations;
import pt.av.it.OsmDriverITAV.OpenApiOps.NetSliceInstance;
import pt.av.it.OsmDriverITAV.OpenApiOps.NetSliceTemplate;
import pt.av.it.OsmDriverITAV.OpenApiOps.NsInstances;
import pt.av.it.OsmDriverITAV.OpenApiOps.NsPackages;
import pt.av.it.OsmDriverITAV.OpenApiOps.VnfPackages;
import pt.av.it.OsmDriverITAV.Requests.AsyncRequests;

// Add to jvm for debugging purposes -Djavax.net.debug=SSL,trustmanager,handshake

public class OSMClient extends Admin implements OSMClientInterface {
    private  String vimAccount;
//    public ApiCalls apiCalls;
    public VnfPackages vnfPackages;
    public NsPackages nsPackages;
    public NsInstances nsInstances;
    public NetSliceTemplate netSliceTemplateOps;
    public NetSliceInstance netSliceInstanceOps;
    public AdminOperations adminOperations;


    public OSMClient(String uri,String username, String password, String project_id,String vimAccount){
        super(new AsyncRequests(uri+"/osm"),username,password,project_id);

        //After authenticating sucessfully we can use the assigned token, unfortunately do to the way java handles arguments the Properties Handler is instanced twice :|

//        uri=uri+"/osm";

        this.vnfPackages= new VnfPackages(this);
        this.nsPackages= new NsPackages(this);
        this.nsInstances= new NsInstances(this);
        this.netSliceTemplateOps= new NetSliceTemplate(this);
        this.netSliceInstanceOps= new NetSliceInstance(this);
        this.adminOperations= new AdminOperations(this);
    }


    @Override
    public void updateApiCallToken() {
        if(isApiCallTokenValid()){
            return;
        }
        newToken();
    }

    @Override
    public boolean isApiCallTokenValid() {
        String token=this.getCurrentTOKEN_ID();
        return isTokenValid(token);
    }

    public String getVimAccount() {
        return vimAccount;
    }
}
