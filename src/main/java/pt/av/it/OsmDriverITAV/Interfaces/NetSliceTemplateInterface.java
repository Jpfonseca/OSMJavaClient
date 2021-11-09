/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.Interfaces;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface NetSliceTemplateInterface {
 /**
  * Query information about multiple NetSlice template resources
  * @return
  */
 JSONArray listNetSliceTemplates();

 /**
  * Create a new NetSlice Template
  * @param additionalProperties
  * @return
  */
 JSONObject createNetSliceTemplate(JSONObject additionalProperties);

 /**
  * Read information about an individual NetSlice template resource
  * @param netSliceTemplateId
  * @return
  */
 JSONObject readNetSliceTemplate(String netSliceTemplateId);

 /**
  * Delete an individual NetSlice template resource
  * @param netSliceTemplateId
  * @return
  */
 JSONObject deleteNetSliceTemplate(String netSliceTemplateId);

 /**
  * Fetch individual NetSlice template resource
  * @param netSliceTemplateId
  * @param artifactPath
  * @return
  */
 String fetchNetSliceTemplate(String netSliceTemplateId,String artifactPath);

 /**
  * Read NST od an on-boarded NetSlice Template
  * @param netSliceTemplateId
  * @return
  */
 String readNetSliceTemplateNst(String netSliceTemplateId);

 /**
  *
  * @param netSliceTemplateId
  * @return
  */
 String fetchNstContent(String netSliceTemplateId);

 /**
  * Upload the content of a NST
  * @param netSliceTemplateId
  * @param filePath
  * @return
  */
 String uploadNstContent(String netSliceTemplateId, String filePath);

 /**
  * Upload a NetSlice package by providing the content of the NetSlice package
  * @param filePath
  * @return
  */

  JSONObject uploadNSTPackage(String filePath);

    /**
  *Upload a NetSlice package by providing the content of the NetSlice package
  * @return
  */
 JSONArray listNstContent();

 /**
  * Read information about an individual NetSlice Template resource
  * @param netSliceTemplateContentId
  * @return
  */
 JSONObject readNstResource(String netSliceTemplateContentId);

 /**
  *Modify an individual NetSlice Template resource
  * @param netSliceTemplateContentId
  * @param payload
  * @return
  */
 JSONObject modifyNSTResource(String netSliceTemplateContentId, JSONObject payload);

 /**
  * Delete an individual NetSlice Template resource
  * @param netSliceTemplateContentId
  * @return
  */
 JSONObject deleteNSTResource(String netSliceTemplateContentId);
}
