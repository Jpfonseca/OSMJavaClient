/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.Interfaces;

import pt.av.it.OsmDriverITAV.OpenApiOps.ApiCalls;
import pt.av.it.OsmDriverITAV.OpenApiOps.NetSliceInstance;
import pt.av.it.OsmDriverITAV.OpenApiOps.NetSliceTemplate;
import pt.av.it.OsmDriverITAV.OpenApiOps.NsInstances;
import pt.av.it.OsmDriverITAV.OpenApiOps.NsPackages;
import pt.av.it.OsmDriverITAV.OpenApiOps.VnfPackages;
import pt.av.it.OsmDriverITAV.Requests.AsyncRequests;
import pt.av.it.OsmDriverITAV.Requests.OsmClientProperties;

public interface OSMClientInterface {
    String uri="";
    AsyncRequests asyncRequests=null;
    ApiCalls apiCalls=null;
    VnfPackages vnfPackages=null;
    NsPackages nsPackages=null;
    NsInstances nsInstances=null;
    NetSliceTemplate netSliceTemplateOps=null;
    NetSliceInstance netSliceInstanceOps=null;
    OsmClientProperties osmClientProperties=null;
    
    void updateApiCallToken();
    boolean isApiCallTokenValid();
    String newToken();
}
