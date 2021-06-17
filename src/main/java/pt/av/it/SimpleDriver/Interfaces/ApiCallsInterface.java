package pt.av.it.SimpleDriver.Interfaces;

import pt.av.it.SimpleDriver.Requests.AsyncRequests;

public interface ApiCallsInterface {
    String currentTOKEN_ID="";
    AsyncRequests http=null;

    String getCurrentTOKEN_ID();
    void setCurrentTOKEN_ID(String currentTOKEN_ID);
    AsyncRequests getHttp();
}
