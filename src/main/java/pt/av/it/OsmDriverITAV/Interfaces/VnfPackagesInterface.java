/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.Interfaces;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public interface VnfPackagesInterface {
    /**
     * Query information about multiple VNF package resources
     * @return
     */
    JSONArray listVnfPackages();

    /**
     * Create a new VNF package resource
     * @param additionalProps
     * @return
     */
    JSONObject createVnfPackage(JSONObject additionalProps);

    /**
     * Read information about an individual VNF package resource
     * @param vnfPkgId
     * @return
     */
    JSONObject readVnfPackageInfo(String vnfPkgId);

    /**
     * Delete an individual VNF package resource
     * @param vnfPkgId
     * @return
     */
    JSONObject deleteVnfPackage(String vnfPkgId);

    /**
     * Modify an individual VNF package resource
     * @param vnfPkgId
     * @param vnfPackageResource
     * @return
     */
    JSONObject modifyVnfPackage(String vnfPkgId, JSONObject vnfPackageResource);

    /**
     * Read VNFD of an on-boarded VNF package
     * @param vnfPkgId
     * @return
     */
    JSONObject readVnfdFromOnboardedVnf(String vnfPkgId);

    /**
     * Fetch an on-boarded VNF package
     * @param vnfPkgId
     * @return
     */
    String fetchOnboardedVnfPackage(String vnfPkgId);

    /**
     * Upload a VNF package by providing the content of the VNF package
     * @param vnfPkgId
     * @param vnfPackage
     * @return
     */
    JSONObject uploadVnfPackage(String vnfPkgId, String vnfPackage);

    /**
     * Fetch individual VNF package artifact
     * @param vnfPkgId
     * @param artifactPath
     * @return
     */
    JSONObject fetchVnfPackageArtifact(String vnfPkgId,String artifactPath);

    /**
     * Upload a VNF package by providing the content of the VNF package
     * @param VnfPackage
     * @return
     */
    JSONObject uploadVnfPackageContent(String VnfPackage);

    /**
     * Query information about multiple VNF package resources
     * @return
     */
    JSONArray listVnfPackageResources();

    /**
     * Read information about an individual VNF package resource
     * @param packageContentId
     * @return
     */
    JSONObject readVnfPackageResource(String packageContentId);

    /**
     * Modify an individual VNF package resource
     * @param packageContentId
     * @param VnfPackage
     * @return
     */
    JSONObject modifyVnfPackageResource(String packageContentId, JSONObject VnfPackage);

    /**
     * Delete an individual VNF package resource
     * @param packageContentId
     * @return
     */
    JSONObject deleteVnfPackageResource(String packageContentId);
}
