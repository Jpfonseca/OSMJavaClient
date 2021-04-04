package Interfaces;

import OpenApiOps.*;
import Requests.AsyncRequests;
import Requests.OsmClientProperties;

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
