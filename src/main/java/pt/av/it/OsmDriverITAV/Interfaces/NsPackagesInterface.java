/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.Interfaces;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public interface NsPackagesInterface {
    /**
     * Query information about multiple NS descriptor resources
     * @return
     */
    JSONArray listNsDescriptors();

    /**
     * Create a new NS descriptor resource
     * @param payload
     * @return
     */
    JSONObject createNewNsDescriptor(JSONObject payload);

    /**
     * Read information about an individual NS descriptor resource
     * @param nsdInfoId
     * @return
     */
    JSONObject readNsDescriptor(String nsdInfoId);

    /**
     * Delete an individual NS descriptor resource
     * @param nsInfoId
     * @return
     */
    JSONObject deleteNsDescriptor(String nsInfoId);

    /**
     * Modify the data of an individual NS descriptor resource
     * @param nsInfoId
     * @param payload
     * @return
     */
    JSONObject modifyNsDescriptor(String nsInfoId, JSONObject payload);

    /**
     * Fetch the content of a NSD
     * @param nsdInfoId
     * @return
     */
    String listNsDescriptorInfo(String nsdInfoId);

    /**
     * Upload the content of a NSD
     * @param nsdInfoId
     * @param zipfilefilepath
     * @return
     */
    JSONObject uploadNsd(String nsdInfoId, String zipfilefilepath);

    /**
     * Fetch individual NS package artifact
     * @param nsInfoId
     * @param artifactPath
     * @return
     */
    JSONObject listNsPackageArtifact(String nsInfoId, String artifactPath);

    /**
     * Read NSD of an on-boarded NS package
     * @param nsdInfoId
     * @return
     */
    String readOnboardedNsdPackage(String nsdInfoId);

    /**
     * Upload a NS package by providing the content of the NS package
     * @param nsdPath
     * @return
     */
    JSONObject uploadNsPackage(String nsdPath);

    /**
     * Query information about multiple NS package resources
     * @return
     */
    JSONArray listNsDescriptorsInfo();

    /**
     * Read information about an individual NS package resource
     * @param nsdInfoId
     * @return
     */
    JSONObject readNsPackageResource(String nsdInfoId);

    /**
     * Modify an individual NS package resource
     * @param nsdInfoId
     * @param nsdInfoModifications
     * @return
     */
    JSONObject modifyNsPackageResource(String nsdInfoId, JSONObject nsdInfoModifications);

    /**
     * Delete an individual NS package resource
     * @param nsdInfoId
     * @return
     */
    JSONObject deleteNsPackageResource(String nsdInfoId);
}
