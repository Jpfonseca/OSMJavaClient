/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.OpenApiOps;

import java.time.Instant;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import pt.av.it.OsmDriverITAV.Interfaces.AdminInterface;
import pt.av.it.OsmDriverITAV.Requests.AsyncRequests;

public abstract class Admin implements AdminInterface {

    private final AsyncRequests http;
    private String currentTOKEN_ID;
    private String payload;
public Admin(AsyncRequests asyncRequests, String username, String password, String project_id){

    this.http=asyncRequests;
    payload="{"+
            " \"username\": \""+username+"\"," +
            " \"password\": \""+password+"\"," +
            " \"project_id\": \""+project_id+"\"" +
            "}";

    //System.out.println(versionInfo());

    /*JSONObject response = http.response(http.post("/admin/v1/tokens", , payload, ""));
    this.currentTOKEN_ID= (String) ((JSONObject) response.get("message")).get("id");
    System.out.println(this.currentTOKEN_ID);*/

}

    public Admin(ApiCalls apiCalls){
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
        JSONObject response=http.response(http.get("/admin/v1/tokens", getcurrentTOKEN_ID()));
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
        String newTOKEN_ID= (String) ((JSONObject) response.get("message")).get("id");
        setCurrentTOKEN_ID(newTOKEN_ID);
        return response;
    }

    /***
     * Delete the Token indicated in the Authorization Header.
     * In this case the Token is saved in the currentToken_ID
     * @return
     */
    public String deleteCurrentToken(){
        String answer=null;
        JSONObject response=http.response(http.delete("/admin/v1/tokens", getcurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            answer= (String) response.get("message");
        }
        setCurrentTOKEN_ID("");
        return answer;
    }

    


    public JSONObject listTokenById(String tokenID){
        JSONObject response=http.response(http.get("/admin/v1/tokens/"+tokenID+"", getcurrentTOKEN_ID()));
        if((int)response.get("status_code")==200){
            response= (JSONObject) response.get("message");
        }
        return response;
    }

    public String deleteTokenById(String tokenID){
        String answer=null;
        JSONObject response=http.response(http.delete("/admin/v1/tokens/"+tokenID+"", getcurrentTOKEN_ID()));
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

    /***
     *
     * @return Returns the version of the OSM installed
     */

    public JSONObject versionInfo(){
        return http.response(http.get("/version", ""));
    }
    
    public String getcurrentTOKEN_ID() {
        return this.currentTOKEN_ID;
    }

    /***
     *
     * @param currentTOKEN_ID Token being used at the time by the Admin class
     */
    private void setCurrentTOKEN_ID(String currentTOKEN_ID) {
        this.currentTOKEN_ID = currentTOKEN_ID;
    }

    public boolean isCurrentTokenValid(){
        return isTokenValid(getcurrentTOKEN_ID());
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


}
