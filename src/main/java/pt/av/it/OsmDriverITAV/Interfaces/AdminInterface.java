/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.Interfaces;

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
