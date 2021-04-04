package Interfaces;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface AdminInterface {

    /***
     * Token Operations
    **/


    JSONArray listAllTokensInfo();

    JSONObject newCurrentToken();

    String deleteCurrentToken();

    JSONObject listTokenById(String tokenID);

    String deleteTokenById(String tokenID);

    boolean isCurrentTokenValid();
}
