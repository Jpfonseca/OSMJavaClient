package pt.av.it.SimpleDriver.Interfaces;

import pt.av.it.SimpleDriver.OpenApiOps.*;
import pt.av.it.SimpleDriver.Requests.AsyncRequests;
import pt.av.it.SimpleDriver.Requests.OsmClientProperties;

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
