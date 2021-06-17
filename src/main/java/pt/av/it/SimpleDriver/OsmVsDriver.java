package pt.av.it.SimpleDriver;

import it.nextworks.nfvmano.catalogue.domainLayer.Domain;
import it.nextworks.nfvmano.catalogue.domainLayer.DomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.NspNbiType;
import it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer.OsmNspDomainLayer;
import it.nextworks.nfvmano.catalogue.template.elements.NstConfigurationRule;
import it.nextworks.nfvmano.catalogues.domainLayer.services.DomainCatalogueService;
import it.nextworks.nfvmano.catalogues.template.repo.ConfigurationRuleRepository;
import it.nextworks.nfvmano.libs.ifa.common.enums.OperationStatus;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotPermittedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.sebastian.nsmf.interfaces.NsmfLcmProviderInterface;
import it.nextworks.nfvmano.sebastian.nsmf.messages.ConfigureNsiRequest;
import it.nextworks.nfvmano.sebastian.nsmf.messages.CreateNsiIdRequest;
import it.nextworks.nfvmano.sebastian.nsmf.messages.InstantiateNsiRequest;
import it.nextworks.nfvmano.sebastian.nsmf.messages.ModifyNsiRequest;
import it.nextworks.nfvmano.sebastian.nsmf.messages.TerminateNsiRequest;
import it.nextworks.nfvmano.sebastian.record.elements.NetworkSliceInstance;
import it.nextworks.nfvmano.sebastian.record.elements.NetworkSliceStatus;
import it.nextworks.nfvmano.sebastian.vsfm.sbi.NsmfLcmOperationPollingManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joaoalegria
 */
public class OsmVsDriver implements NsmfLcmProviderInterface{
    
    private static final Logger log = LoggerFactory.getLogger(OsmVsDriver.class);

    private OSMClient client;
    private DomainCatalogueService domainCatalogueService;
    private Map<String, String> nsiNames;
    private ConfigurationRuleRepository configurationRuleRepository;
    private NsmfLcmOperationPollingManager pollingManager;

    public OsmVsDriver(String uri,String username, String password, String project_id,String vimAccount, DomainCatalogueService domainCatalogueService, ConfigurationRuleRepository configurationRuleRepository,NsmfLcmOperationPollingManager nsmfLcmOperationPollingManager) {
        client = new OSMClient(uri, username, password, project_id, vimAccount);
        this.domainCatalogueService=domainCatalogueService;
        this.nsiNames=new HashMap<String, String>();
        this.configurationRuleRepository=configurationRuleRepository;
        this.pollingManager=nsmfLcmOperationPollingManager;
    }
    

    @Override
    public String createNetworkSliceIdentifier(CreateNsiIdRequest request, String domainId, String tenantId) throws NotExistingEntityException, MethodNotImplementedException, FailedOperationException, MalformattedElementException, NotPermittedOperationException {
        log.info("Sending request to create network slice id");
        JSONObject netslice = new JSONObject();
        netslice.put("nsiName", request.getName());
        netslice.put("nstId", request.getNstId());
        JSONObject subnet=new JSONObject();
        subnet.put("id","interdomain-tunnel-peer");
        
        List<JSONObject> list=new ArrayList<JSONObject>();
        JSONObject aux=new JSONObject();
        aux.put("tunnel_address", "10.100.100.1/24");
        aux.put("tunnel_id", "1");
        aux.put("vsi_id", "10");
        
        JSONObject aux2=new JSONObject();
        aux2.put("member-vnf-index", "1");
        aux2.put("additionalParams", aux);
        
        list.add(aux2);
        subnet.put("additionalParamsForVnf",list);
        
        List<JSONObject> tmp = new ArrayList<JSONObject>();
        tmp.add(subnet);
        netslice.put("netslice-subnet", tmp);
        
        Domain domain = this.domainCatalogueService.getDomain(domainId);
        List<DomainLayer> ownedLayers = domain.getOwnedLayers();
        OsmNspDomainLayer osm = (OsmNspDomainLayer) ownedLayers.get(0);
        
        netslice.put("vimAccountId", osm.getVimAccount());
        
        JSONObject response = client.netSliceInstanceOps.createNsi(netslice);
        log.info(response.toJSONString());
        
        String nsiId=(String)response.get("id");
        
        this.nsiNames.put(nsiId, request.getName());
        
        return nsiId;
    }

    @Override
    public void instantiateNetworkSlice(InstantiateNsiRequest request, String domainId, String tenantId) throws NotExistingEntityException, MethodNotImplementedException, FailedOperationException, MalformattedElementException, NotPermittedOperationException {
        log.info("Sending request to instantiate network slice");
        JSONObject netslice = new JSONObject();
        netslice.put("nsiName", this.nsiNames.get(request.getNsiId()));
        netslice.put("nstId", request.getNstId());
        
        
        Domain domain = this.domainCatalogueService.getDomain(domainId);
        List<DomainLayer> ownedLayers = domain.getOwnedLayers();
        OsmNspDomainLayer osm = (OsmNspDomainLayer) ownedLayers.get(0);
        
        netslice.put("vimAccountId", osm.getVimAccount());
        
        JSONObject response = client.netSliceInstanceOps.instantiateNsi(request.getNsiId(), netslice);
        this.pollingManager.addOperation(UUID.randomUUID().toString(), OperationStatus.SUCCESSFULLY_DONE, request.getNsiId(), "NSI_CREATION", domainId, NspNbiType.OSM);
    }   

    @Override
    public void modifyNetworkSlice(ModifyNsiRequest request, String domainId, String tenantId) throws NotExistingEntityException, MethodNotImplementedException, FailedOperationException, MalformattedElementException, NotPermittedOperationException {
        log.info("Sending request to modify network slice");
        JSONObject netslice = new JSONObject();
        client.netSliceInstanceOps.terminateNsi(request.getNsiId(), netslice);
        netslice.put("nstId", request.getNstId());
        JSONObject response = client.netSliceInstanceOps.instantiateNsi(request.getNsiId(), netslice);
    }

    @Override
    public void terminateNetworkSliceInstance(TerminateNsiRequest request, String domainId, String tenantId) throws NotExistingEntityException, MethodNotImplementedException, FailedOperationException, MalformattedElementException, NotPermittedOperationException {
        log.info("Sending request to terminate network slice");
        JSONObject netslice = new JSONObject();
        JSONObject response = client.netSliceInstanceOps.terminateNsi(request.getNsiId(), netslice);
        
        this.nsiNames.remove(request.getNsiId());
        
        this.pollingManager.addOperation(UUID.randomUUID().toString(), OperationStatus.SUCCESSFULLY_DONE, request.getNsiId(), "NSI_TERMINATION", domainId, NspNbiType.OSM);
    }

    @Override
    public List<NetworkSliceInstance> queryNetworkSliceInstance(GeneralizedQueryRequest request, String domainId, String tenantId) throws MethodNotImplementedException, FailedOperationException, MalformattedElementException {
        log.info("Sending request to query network slice");
        if(request.getFilter() != null){
            String nsiID = request.getFilter().getParameters().getOrDefault("NSI_ID","");
            JSONObject response = client.netSliceInstanceOps.readNsiResource(nsiID);

            if(response == null)
                throw new FailedOperationException("Error querying network slice instance");
            
//            ObjectMapper objectMapper = new ObjectMapper();
//            try {
//                nsii.add(objectMapper.readValue(response.toJSONString(), NetworkSliceInstance.class));
//            } catch (IOException ex) {
//                java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
//            }
            
            NetworkSliceInstance nsi = new NetworkSliceInstance();
            nsi.setNsiId(nsiID);
            switch((String)response.get("operational-status")){
                case "init":
                    nsi.setStatus(NetworkSliceStatus.INSTANTIATING);
                    break;
                case "running":
                    nsi.setStatus(NetworkSliceStatus.INSTANTIATED);
                    break;
            }
            
            List<NetworkSliceInstance> nsiis = new ArrayList<NetworkSliceInstance>();
            nsiis.add(nsi);
            return nsiis;
        }
//        else{}
        return null;
    }

    @Override
    public void configureNetworkSliceInstance(ConfigureNsiRequest request, String domainId, String tenantId) throws MethodNotImplementedException, FailedOperationException, MalformattedElementException {
        List<NstConfigurationRule> cfs=this.configurationRuleRepository.findByNstId(request.getNsstId());
        Map<String,String> parameters=request.getParameters();
        
        
        for(NstConfigurationRule rule : cfs){
            String actionName=rule.getName();
            Map<String,String> actionParameters = new HashMap<String,String>();
            boolean error=false;
            for(String paramName : rule.getParams()){
                if(!parameters.keySet().contains(paramName)){
                    log.info("Error while processing action '"+actionName+"', parameter '"+paramName+"' not provided");
                    error=true;
                    break;
                }
                actionParameters.put(paramName, parameters.get(paramName));
            }
            if(error){
                log.info("Action '"+actionName+"' will not be performed due to previous errors.");
            }
            else{
                //perform action
                
                
                JSONObject actionRequest = new JSONObject();
                actionRequest.put("primitive", actionName);
                actionRequest.put("primitive_params", actionParameters);
                
                //performing with the nsi id
                actionRequest.put("netsliceInstanceId", request.getNsiId());
                JSONObject response = client.netSliceInstanceOps.executeActionNsi(request.getNsiId(), actionRequest);
                
                //perform action by redirecting to the NS due to OSM limitations
//                JSONObject response = client.netSliceInstanceOps.readNsiResource(request.getNsiId());
//                List<JSONObject> sliceSubnets = (List<JSONObject>)((JSONObject)response.get("_admin")).get("nsrs-detailed-list");
//                
//                actionRequest.put("member-vnf-index", "1");
//                for(JSONObject subnet : sliceSubnets){
//                    if(subnet.get("nss-id").equals("interdomain-tunnel-peer")){
//                        String nsId=(String)subnet.get("nsrId");
//                        response=client.nsInstances.actionNSi(nsId, actionRequest);
//                        break;
//                    }
//                }
                
                log.info("Action '"+actionName+"' executed with response: "+response.toString());
            }
        }
        
        this.pollingManager.addOperation(UUID.randomUUID().toString(), OperationStatus.SUCCESSFULLY_DONE, request.getNsiId(), "NSI_CONFIGURATION", domainId, NspNbiType.OSM);
    }
    
}
