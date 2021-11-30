/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.OpenApiOps;

import java.time.Instant;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import pt.av.it.OsmDriverITAV.Interfaces.AdminInterface;
import pt.av.it.OsmDriverITAV.Interfaces.ApiCallsInterface;
import pt.av.it.OsmDriverITAV.Requests.AsyncRequests;

public abstract class Admin implements AdminInterface, ApiCallsInterface {

    private final AsyncRequests http;
    private String currentTOKEN_ID;
    private Double tokenExpirationTimestamp;
    private String payload;
    
    public Admin(AsyncRequests asyncRequests, String username, String password, String project_id){

        this.http=asyncRequests;
        this.payload="{"+
                " \"username\": \""+username+"\"," +
                " \"password\": \""+password+"\"," +
                " \"project_id\": \""+project_id+"\"" +
                "}";

        //System.out.println(versionInfo());

        /*JSONObject response = http.response(http.post("/admin/v1/tokens", , payload, ""));
        this.currentTOKEN_ID= (String) ((JSONObject) response.get("message")).get("id");
        System.out.println(this.currentTOKEN_ID);*/
        newToken();

    }

    public Admin(ApiCallsInterface apiCalls){
        this.http=apiCalls.getHttp();
    }

    /***
     *
     * Authentication Operations
     *
     */

    /***
     *Query information on multiple tokens
     * @return
     */
    public JSONArray listAllTokensInfo(){
        JSONArray answer=null;
        JSONObject response=http.response(http.get("/admin/v1/tokens", getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (JSONArray) response.get("message");
        }
        return answer;
    }

    /***
     * Request a new Token
     * This will save the Token in the variable currentToken_ID
     * @return
     */
    public JSONObject newCurrentToken(){
        JSONObject response=null;
        response = http.response(http.post("/admin/v1/tokens", payload, ""));
        return response;
    }

    /***
     * Delete the Token indicated in the Authorization Header.
     * In this case the Token is saved in the currentToken_ID
     * @return
     */
    public String deleteCurrentToken(){
        String answer=null;
        JSONObject response=http.response(http.delete("/admin/v1/tokens", getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (String) response.get("message");
        }
        setCurrentTOKEN_ID("");
        return answer;
    }

    


    public JSONObject listTokenById(String tokenID){
        JSONObject response=http.response(http.get("/admin/v1/tokens/"+tokenID+"", getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            response= (JSONObject) response.get("message");
        }
        return response;
    }

    public String deleteTokenById(String tokenID){
        String answer=null;
        JSONObject response=http.response(http.delete("/admin/v1/tokens/"+tokenID+"", getCurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (String) response.get("message");
        }
        return answer;
    }





    /***
     *
     * Extra Methods
     *
     */

    
    
    public String newToken() {
        JSONObject response=this.newCurrentToken();
        this.currentTOKEN_ID = (String) ((JSONObject) response.get("message")).get("id");
        this.tokenExpirationTimestamp = (Double) ((JSONObject) response.get("message")).get("expires");
        return this.currentTOKEN_ID;
    }
    
    /***
     *
     * @return Returns the version of the OSM installed
     */
    public JSONObject versionInfo(){
        return http.response(http.get("/version", ""));
    }
    
    public String getCurrentTOKEN_ID() {
        double curr,diff;
        curr=currentDate();
        diff=this.tokenExpirationTimestamp-curr;
        if(diff<=0){
            this.newToken();
        }
        return this.currentTOKEN_ID;
    }

    /***
     *
     * @param currentTOKEN_ID Token being used at the time by the Admin class
     */
    public void setCurrentTOKEN_ID(String currentTOKEN_ID) {
        this.currentTOKEN_ID = currentTOKEN_ID;
    }

    public boolean isCurrentTokenValid(){
        return isTokenValid(getCurrentTOKEN_ID());
    }

    public boolean isTokenValid(String TOKEN_ID){
        JSONObject jsonObject=listTokenById(TOKEN_ID);
        double curr,diff,expiredate=(double)jsonObject.get("expires");
        curr=currentDate();
        diff=expiredate-curr;
        return diff>0;
    }
    
    private double currentDate(){
        return (double)Instant.now().getEpochSecond();
    }

    public AsyncRequests getHttp() {
        return http;
    }
}
