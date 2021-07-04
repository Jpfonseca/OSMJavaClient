package pt.av.it.SimpleDriver;

import it.nextworks.nfvmano.catalogue.domainLayer.NspNbiType;
import it.nextworks.nfvmano.catalogue.template.elements.NstConfigurationRule;
import it.nextworks.nfvmano.catalogues.template.repo.ConfigurationRuleRepository;
import it.nextworks.nfvmano.catalogues.template.repo.NsTemplateRepository;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.enums.OperationStatus;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotPermittedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.libs.ifa.templates.NST;
import it.nextworks.nfvmano.sebastian.nsmf.interfaces.NsmfLcmProviderInterface;
import it.nextworks.nfvmano.sebastian.nsmf.messages.ConfigureNsiRequest;
import it.nextworks.nfvmano.sebastian.nsmf.messages.CreateNsiIdRequest;
import it.nextworks.nfvmano.sebastian.nsmf.messages.InstantiateNsiRequest;
import it.nextworks.nfvmano.sebastian.nsmf.messages.ModifyNsiRequest;
import it.nextworks.nfvmano.sebastian.nsmf.messages.NetworkSliceStatusChange;
import it.nextworks.nfvmano.sebastian.nsmf.messages.NetworkSliceStatusChangeNotification;
import it.nextworks.nfvmano.sebastian.nsmf.messages.TerminateNsiRequest;
import it.nextworks.nfvmano.sebastian.record.VsRecordService;
import it.nextworks.nfvmano.sebastian.record.elements.NetworkSliceInstance;
import it.nextworks.nfvmano.sebastian.record.elements.NetworkSliceStatus;
import it.nextworks.nfvmano.sebastian.record.elements.VerticalServiceInstance;
import it.nextworks.nfvmano.sebastian.vsfm.VsLcmService;
import it.nextworks.nfvmano.sebastian.vsfm.sbi.NsmfLcmOperationPollingManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joaoalegria
 */
public class OsmVsDriver implements NsmfLcmProviderInterface{
    
    private static final Logger log = LoggerFactory.getLogger(OsmVsDriver.class);

    private OSMClient client;
    private static Map<String, String> nsiNames;
    private static Map<Long, Map<String, JSONObject>> interdomainInfo=new HashMap<Long, Map<String, JSONObject>>();
    private static Map<ConfigureNsiRequest, String> pendingConfigs=new HashMap<ConfigureNsiRequest, String>();
    private ConfigurationRuleRepository configurationRuleRepository;
    private NsmfLcmOperationPollingManager pollingManager;
    private VsRecordService vsRecordService;
    private NsTemplateRepository nstRepository;
    private static int tunnelPeerCount=1;
    private String vimAccount;
    private String uri;
    private VsLcmService vsLcmService;

    public OsmVsDriver(String uri,String username, String password, String project_id,String vimAccount, ConfigurationRuleRepository configurationRuleRepository,NsmfLcmOperationPollingManager nsmfLcmOperationPollingManager, VsRecordService vsRecordService, NsTemplateRepository nstRepository, VsLcmService vsLcmService) {
        client = new OSMClient(uri, username, password, project_id, vimAccount);
        this.nsiNames=new HashMap<String, String>();
        this.configurationRuleRepository=configurationRuleRepository;
        this.pollingManager=nsmfLcmOperationPollingManager;
        this.vsRecordService=vsRecordService;
        this.nstRepository=nstRepository;
        this.vimAccount=vimAccount;
        this.uri=uri;
        this.vsLcmService=vsLcmService;
    }
    

    @Override
    public String createNetworkSliceIdentifier(CreateNsiIdRequest request, String domainId, String tenantId) throws NotExistingEntityException, MethodNotImplementedException, FailedOperationException, MalformattedElementException, NotPermittedOperationException {
        log.info("Sending request to create network slice id for template '"+request.getNstId()+"' in domain '"+domainId+"'");
        
        JSONObject netslice = new JSONObject();
        netslice.put("nsiName", request.getName());
        netslice.put("nstId", request.getNstId());
        JSONObject subnet=new JSONObject();
        subnet.put("id","interdomain-tunnel-peer");
        
        List<JSONObject> list=new ArrayList<JSONObject>();
        JSONObject aux=new JSONObject();
        aux.put("tunnel_address", "10.100.100."+String.valueOf(this.tunnelPeerCount)+"/24");
        aux.put("tunnel_id", String.valueOf(this.tunnelPeerCount));
        aux.put("vsi_id", "10");
        this.tunnelPeerCount++;
        aux.put("use_data_interfaces", "false");
        
        JSONObject aux2=new JSONObject();
        aux2.put("member-vnf-index", "1");
        aux2.put("additionalParams", aux);
        
        list.add(aux2);
        subnet.put("additionalParamsForVnf",list);
        
        List<JSONObject> tmp = new ArrayList<JSONObject>();
        tmp.add(subnet);
        netslice.put("netslice-subnet", tmp);
        
        netslice.put("vimAccountId", this.vimAccount);
        
        
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
        
        netslice.put("vimAccountId", this.vimAccount);
        
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
            nsi.setNfvNsId((String)((List<JSONObject>)response.get("nsr-ref-list")).get(0).get("nsr-ref"));
            switch((String)response.get("operational-status")){
                case "init":
                    nsi.setStatus(NetworkSliceStatus.INSTANTIATING);
                    break;
                case "running":
                    nsi.setStatus(NetworkSliceStatus.INSTANTIATED);
                    
                    
                    String nstId=(String)((JSONObject)response.get("instantiation_parameters")).get("nstId");
                    List<NST> nsts = this.nstRepository.findAll();
                    List<NstConfigurationRule> cfs=new ArrayList<NstConfigurationRule>();
                    for(NST nst : nsts){
                        if(nst.getNsstIds().contains(nstId)){
                            cfs = configurationRuleRepository.findByNstId(nst.getNstId());
                        }
                    }

                    List<String> actionNames=new ArrayList<String>();
                    for(NstConfigurationRule rule : cfs){
                        actionNames.add(rule.getName());
                    }

                    if(actionNames.contains("getvnfinfo")){
                        VerticalServiceInstance vsi = this.vsRecordService.getVsInstancesFromNetworkSliceSubnet(nsiID).get(0);
                        this.getInterdomainInfo(vsi, nsi, nsiID, domainId, tenantId);
                    }
                    
                    
                    break;
                case "terminating":
                    nsi.setStatus(NetworkSliceStatus.TERMINATING);
                    break;
                case "terminated":
                    nsi.setStatus(NetworkSliceStatus.TERMINATED);
                    break;
            }
            
            List<NetworkSliceInstance> nsis = new ArrayList<NetworkSliceInstance>();
            nsis.add(nsi);
            return nsis;
        }
//        else{}
        return null;
    }

    @Override
    public void configureNetworkSliceInstance(ConfigureNsiRequest request, String domainId, String tenantId) throws MethodNotImplementedException, FailedOperationException, MalformattedElementException {
        //Executing primitives over the NS instance due to OSM restrictions        
        log.info("Configuring Subnet "+request.getNsiId().toString());
//        List<NstConfigurationRule> cfs=this.configurationRuleRepository.findByNstId(request.getNsstId());

        List<NST> nsts = this.nstRepository.findAll();
        List<NstConfigurationRule> cfs=new ArrayList<NstConfigurationRule>();
        for(NST nst : nsts){
            if(nst.getNsstIds().contains(request.getNsstId())){
                cfs = configurationRuleRepository.findByNstId(nst.getNstId());
            }
        }
        
        List<String> actionNames=new ArrayList<String>();
        for(NstConfigurationRule rule : cfs){
            actionNames.add(rule.getName());
        }
        
        if(actionNames.contains("addpeer")){
            VerticalServiceInstance vsi = this.vsRecordService.getVsInstancesFromNetworkSliceSubnet(request.getNsiId()).get(0);
            
            if(interdomainInfo.get(vsi.getId()).size()==vsi.getNssis().size()){
                log.info("Interdomain Mechanism: Exchanging information between tunnel peers");
                
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("NSI_ID", request.getNsiId());
                Filter filter=new Filter(parameters);
                GeneralizedQueryRequest query = new GeneralizedQueryRequest(filter, new ArrayList<>()   );
                NetworkSliceInstance nsi = this.queryNetworkSliceInstance(query, domainId, tenantId).get(0);
                String nssiId=nsi.getNfvNsId();
                
                for(String nssiId2 : interdomainInfo.get(vsi.getId()).keySet()){
                    if(!nssiId.equals(nssiId2)){
                        log.info("Subnet with nsrId '"+nssiId+"' is receiving information from the Subnet with nsrId '"+nssiId2+"'");

                        JSONObject actionRequest = new JSONObject();
                        actionRequest.put("primitive", "addpeer");
                        Map<String,String> actionParameters = new HashMap<String,String>();
                        actionParameters.put("peer_key", (String)interdomainInfo.get(vsi.getId()).get(nssiId2).get("publicKey"));
                        actionParameters.put("peer_endpoint", (String)interdomainInfo.get(vsi.getId()).get(nssiId2).get("vnfIp"));
                        actionParameters.put("peer_network", "10.100.100.0/24");
                        actionRequest.put("primitive_params", actionParameters);
                        actionRequest.put("member_vnf_index", "1");
                        JSONObject response = client.nsInstances.actionNSi(nssiId, actionRequest);

                        log.info("Action 'addpeer' executed with response: "+response.toString());
                    }
                }
                
                if(pendingConfigs.size()>0){
                    ConfigureNsiRequest tmp = (ConfigureNsiRequest)pendingConfigs.keySet().toArray()[0];
                    NetworkSliceStatusChangeNotification notification = new NetworkSliceStatusChangeNotification(tmp.getNsiId(), NetworkSliceStatusChange.NSI_CREATED, true);
                    pendingConfigs.remove(tmp);
                    this.vsLcmService.notifyNetworkSliceStatusChange(notification);
                }
            }else{
                pendingConfigs.put(request, domainId);
            }
        }
        
//        this.pollingManager.addOperation(UUID.randomUUID().toString(), OperationStatus.SUCCESSFULLY_DONE, request.getNsiId(), "NSI_CREATION", domainId, NspNbiType.OSM);
        NetworkSliceStatusChangeNotification notification = new NetworkSliceStatusChangeNotification(request.getNsiId(), NetworkSliceStatusChange.NSI_CONFIGURED, true);
        this.vsLcmService.notifyNetworkSliceStatusChange(notification);
    }

    private void getInterdomainInfo(VerticalServiceInstance vsi, NetworkSliceInstance nsi, String nssiId, String domainId, String tenantId) throws MethodNotImplementedException, FailedOperationException, MalformattedElementException { 
        if(!interdomainInfo.containsKey(vsi.getId())){
            JSONObject actionRequest = new JSONObject();
            actionRequest.put("primitive", "getvnfinfo");
            actionRequest.put("primitive_params", new JSONObject());
            //performing with the nsr id
            actionRequest.put("member_vnf_index", "1");
            JSONObject response = client.nsInstances.actionNSi(nsi.getNfvNsId(), actionRequest);
            log.info(response.toJSONString());
            String actionId = (String)response.get("id");
            log.info("Processed action '"+actionId+"'");
            
            String actionStatus="RUNNING";
            while(!actionStatus.equals("COMPLETED")){
                try {
                    response = client.nsInstances.listNSLcmOpOcc(actionId);
                    actionStatus=(String)response.get("operationState");
                    Thread.sleep(30000);
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            JSONParser parser = new JSONParser();
            try {
                response = (JSONObject) parser.parse((String)((JSONObject)response.get("detailed-status")).get("output"));
            } catch (ParseException ex) {
                java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Map<String, JSONObject> aux=new HashMap<String, JSONObject>();
            aux.put(nsi.getNfvNsId(), response);
            interdomainInfo.put(vsi.getId(), aux);
        }else{
            if(!interdomainInfo.get(vsi.getId()).containsKey(nsi.getNfvNsId())){
                JSONObject actionRequest = new JSONObject();
                actionRequest.put("primitive", "getvnfinfo");
                actionRequest.put("primitive_params", new JSONObject());
                //performing with the nsr id
                actionRequest.put("member_vnf_index", "1");
                JSONObject response = client.nsInstances.actionNSi(nsi.getNfvNsId(), actionRequest);
                log.info(response.toJSONString());
                String actionId = (String)response.get("id");
                log.info("Processed action '"+actionId+"'");

                String actionStatus="RUNNING";
                while(!actionStatus.equals("COMPLETED")){
                    try {
                        response = client.nsInstances.listNSLcmOpOcc(actionId);
                        actionStatus=(String)response.get("operationState");
                        Thread.sleep(30000);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                JSONParser parser = new JSONParser();
                try {
                    response = (JSONObject) parser.parse((String)((JSONObject)response.get("detailed-status")).get("output"));
                } catch (ParseException ex) {
                    java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            
                Map<String, JSONObject> aux=interdomainInfo.get(vsi.getId());
                aux.put(nsi.getNfvNsId(), response);
                interdomainInfo.put(vsi.getId(), aux);
            }
        }
    }
    
}
