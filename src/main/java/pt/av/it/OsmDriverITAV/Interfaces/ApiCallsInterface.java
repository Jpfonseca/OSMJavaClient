/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.Interfaces;

import pt.av.it.OsmDriverITAV.Requests.AsyncRequests;

public interface ApiCallsInterface {
    String currentTOKEN_ID="";
    AsyncRequests http=null;

    String getCurrentTOKEN_ID();
    void setCurrentTOKEN_ID(String currentTOKEN_ID);
    AsyncRequests getHttp();
}
