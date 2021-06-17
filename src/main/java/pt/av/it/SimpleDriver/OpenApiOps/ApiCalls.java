package pt.av.it.SimpleDriver.OpenApiOps;


import pt.av.it.SimpleDriver.Interfaces.ApiCallsInterface;
import pt.av.it.SimpleDriver.Requests.AsyncRequests;

public class ApiCalls implements ApiCallsInterface {

    private String currentTOKEN_ID;
    private final AsyncRequests  http;

    public ApiCalls(AsyncRequests asyncRequests,String token_id){
      this.currentTOKEN_ID=token_id;
      this.http=asyncRequests;
    }

    public String getCurrentTOKEN_ID() {
        return currentTOKEN_ID;
    }

    public void setCurrentTOKEN_ID(String currentTOKEN_ID) {
        this.currentTOKEN_ID = currentTOKEN_ID;
    }

    public AsyncRequests getHttp() {
        return http;
    }
}
