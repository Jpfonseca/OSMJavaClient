package pt.av.it.OsmDriverITAV;

import it.nextworks.nfvmano.catalogue.domainLayer.NspNbiType;
import it.nextworks.nfvmano.catalogues.template.repo.ConfigurationRuleRepository;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
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
import it.nextworks.nfvmano.sebastian.record.VsRecordService;
import it.nextworks.nfvmano.sebastian.record.elements.NetworkSliceInstance;
import it.nextworks.nfvmano.sebastian.record.elements.NetworkSliceStatus;
import it.nextworks.nfvmano.sebastian.record.elements.VerticalServiceInstance;
import it.nextworks.nfvmano.sebastian.record.repo.VerticalServiceInstanceRepository;
import it.nextworks.nfvmano.sebastian.vsfm.VsLcmService;
import it.nextworks.nfvmano.sebastian.vsfm.sbi.NsmfLcmOperationPollingManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 5Growth-VS Driver INterface.
 * @author João Alegria (joao.p@av.it.pt)
 */
public class OsmVsDriver implements NsmfLcmProviderInterface{
    
    private static final Logger log = LoggerFactory.getLogger(OsmVsDriver.class);

    private OSMClient client;
    private static Map<String, String> nsiNames;
//    private static Map<Long, Map<String, JSONObject>> allInterdomainInfo=new HashMap<Long, Map<String, JSONObject>>();
//    private static Map<Long, Map<String, JSONObject>> allMtdInfo=new HashMap<Long, Map<String, JSONObject>>();
//    private static Map<String, String> pendingConfigs=new HashMap<String, String>();
    private ConfigurationRuleRepository configurationRuleRepository;
    private NsmfLcmOperationPollingManager pollingManager;
    private VsRecordService vsRecordService;
    private String vimAccount;
    private String uri;
    private VsLcmService vsLcmService;
    private VerticalServiceInstanceRepository vsInstanceRepository;
    private List<String> processedActions=new ArrayList<String>();
    

    public OsmVsDriver(String uri,String username, String password, String project_id,String vimAccount, ConfigurationRuleRepository configurationRuleRepository,NsmfLcmOperationPollingManager nsmfLcmOperationPollingManager, VsRecordService vsRecordService, VsLcmService vsLcmService, VerticalServiceInstanceRepository vsInstanceRepository) {
        client = new OSMClient(uri, username, password, project_id, vimAccount);
        this.nsiNames=new HashMap<String, String>();
        this.configurationRuleRepository=configurationRuleRepository;
        this.pollingManager=nsmfLcmOperationPollingManager;
        this.vsRecordService=vsRecordService;
        this.vimAccount=vimAccount;
        this.uri=uri;
        this.vsLcmService=vsLcmService;
        this.vsInstanceRepository=vsInstanceRepository;
    }
    

    @Override
    public String createNetworkSliceIdentifier(CreateNsiIdRequest request, String domainId, String tenantId) throws NotExistingEntityException, MethodNotImplementedException, FailedOperationException, MalformattedElementException, NotPermittedOperationException {
        log.info("Sending request to create network slice id for template '"+request.getNstId()+"' in domain '"+domainId+"'");
        
        VerticalServiceInstance vsi = this.vsInstanceRepository.findByTenantId(tenantId).get(0);
        Map<String, String> data = vsi.getUserData();
        String config=null;
        for(String dataKey : data.keySet()){
            if(dataKey.contains("config")){
                //nsst.<nstID>.config
                if(dataKey.split("\\.")[1].equals(request.getNstId())){
                    config=data.get(dataKey);
                }
            }
        }
        
        JSONObject netslice = new JSONObject();
        netslice.put("nsiName", request.getName());
        netslice.put("nstId", request.getNstId());
        netslice.put("vimAccountId", this.vimAccount);
        
        if(config!=null){
            JSONParser parser = new JSONParser();
            JSONObject configData;
            try {
                configData = (JSONObject) parser.parse(config);
                for(Object entry : configData.keySet()){
                    netslice.put((String) entry, configData.get(entry));
                }
            } catch (ParseException ex) {
                java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        JSONObject response = client.netSliceInstanceOps.createNsi(netslice);
        
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
            
            NetworkSliceInstance nsi = new NetworkSliceInstance();
            nsi.setNsiId(nsiID);
            nsi.setNfvNsId((String)((List<JSONObject>)response.get("nsr-ref-list")).get(0).get("nsr-ref"));
            switch((String)response.get("operational-status")){
                case "init":
                    nsi.setStatus(NetworkSliceStatus.INSTANTIATING);
                    break;
                case "running":
                    nsi.setStatus(NetworkSliceStatus.INSTANTIATED);
                    break;
                case "terminating":
                    nsi.setStatus(NetworkSliceStatus.TERMINATING);
                    break;
                case "terminated":
                    nsi.setStatus(NetworkSliceStatus.TERMINATED);
                    break;
            }
            
            List<JSONObject> operations = this.getNsiOperations(nsi);
            if(!operations.isEmpty()){
                boolean allConfigured=true;
                for(JSONObject op : operations){
                    if(!this.processedActions.contains((String)op.get("id"))){
                        this.processedActions.add((String)op.get("id"));
                        if(((String)op.get("status")).equals("COMPLETED")){
                            VerticalServiceInstance vsi = this.vsRecordService.getVsInstancesFromNetworkSliceSubnet(nsi.getNsiId()).get(0);
                            Map<String, JSONObject> interdomainInfo = vsi.getInterdomainInfo();
                            Map<String, JSONObject> mtdInfo = vsi.getMtdInfo();
                            switch((String)op.get("name")){
                                case "getvnfinfo":{
                                    interdomainInfo.put(nsi.getNfvNsId(), response);
                                    this.vsRecordService.addInterdomainInfo(vsi.getVsiId(), interdomainInfo);
                                    break;
                                }
                                case "getmtdinfo":{
                                    mtdInfo.put(nsi.getNfvNsId(), response);
                                    this.vsRecordService.addMtdInfo(vsi.getVsiId(), mtdInfo);
                                    break;
                                }
                            }
                            nsi.setStatus(NetworkSliceStatus.CONFIGURED);
                        }else{
                            allConfigured=false;
                            this.processedActions.remove((String)op.get("id"));
                            nsi.setStatus(NetworkSliceStatus.CONFIGURING);
                        }
                        break;
                    }
                }
                if(allConfigured){
                    nsi.setStatus(NetworkSliceStatus.CONFIGURED);
                }
            }
            
            List<NetworkSliceInstance> nsis = new ArrayList<NetworkSliceInstance>();
            nsis.add(nsi);
            return nsis;
        }
        return null;
    }

    @Override
    public void configureNetworkSliceInstance(ConfigureNsiRequest request, String domainId, String tenantId) throws MethodNotImplementedException, FailedOperationException, MalformattedElementException {
        //Executing primitives over the NS instance due to OSM restrictions        
        log.info("Configuring Subnet "+request.getNsiId().toString());

        Map<String,String> params=request.getParameters();
        String ruleName=params.get("ruleName");
        params.remove("ruleName");
        
        String ruleId=params.get("ruleId");
        params.remove("ruleId");
        
        log.info("Processing action '"+ruleName+"' on Domain '"+domainId+"'");
        
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("NSI_ID", request.getNsiId());
        Filter filter=new Filter(parameters);
        GeneralizedQueryRequest query = new GeneralizedQueryRequest(filter, new ArrayList<>()   );
        NetworkSliceInstance nsi = this.queryNetworkSliceInstance(query, domainId, tenantId).get(0);
        String nssiId=nsi.getNfvNsId();
        
        VerticalServiceInstance vsi = this.vsRecordService.getVsInstancesFromNetworkSliceSubnet(request.getNsiId()).get(0);
        
        Map<String, JSONObject> interdomainInfo = vsi.getInterdomainInfo();
        Map<String, JSONObject> mtdInfo = vsi.getMtdInfo();
        Map<String,String> pendingActions = vsi.getPendingActions();
        
        switch(ruleName){
            case "addpeer":{
//                if(interdomainInfo.size()==vsi.getNssis().size()){

                while(interdomainInfo.size()!=vsi.getNssis().size()){
                    log.info("Waiting for all Interdomain info");
                    try {
                        Thread.sleep(30000);
                        vsi = this.vsRecordService.getVsInstancesFromNetworkSliceSubnet(request.getNsiId()).get(0);
                        interdomainInfo = vsi.getInterdomainInfo();
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                for(String nssiId2 : interdomainInfo.keySet()){                  
                    if(!nssiId.equals(nssiId2)){
                        log.info("Subnet with nsrId '"+nssiId+"' is receiving information from the Subnet with nsrId '"+nssiId2+"'");

                        JSONObject actionRequest = new JSONObject();
                        actionRequest.put("primitive", "addpeer");
                        Map<String,String> actionParameters = new HashMap<String,String>();
                        actionParameters.put("peer_key", (String)interdomainInfo.get(nssiId2).get("publicKey"));
                        actionParameters.put("peer_endpoint", (String)interdomainInfo.get(nssiId2).get("vnfIp"));
                        actionParameters.put("peer_network", "10.100.100.0/24");
                        actionRequest.put("primitive_params", actionParameters);
                        actionRequest.put("member_vnf_index", "1");
                        JSONObject response = client.nsInstances.actionNSi(nssiId, actionRequest);
                        String actionId = (String)response.get("id");
//                        String actionStatus="RUNNING";
//                        while(!actionStatus.equals("COMPLETED")){
//                            try {
//                                response = client.nsInstances.listNSLcmOpOcc(actionId);
//                                actionStatus=(String)response.get("operationState");
//                                if(!actionStatus.equals("COMPLETED")){
//                                    Thread.sleep(30000);
//                                }
//                            } catch (InterruptedException ex) {
//                                java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        }
//
//                        log.info("Action 'addpeer' executed with response: "+response.toString());
                    }
                }
                    
//                    if(pendingActions.size()>0){
//                        String actionId=null;
//                        for(Entry<String, String> action : pendingActions.entrySet()){
//                            if(action.getValue().equals("addpeer")){
//                                actionId=action.getKey();
//                                Day2ActionRequest message = new Day2ActionRequest(vsi.getVsiId(), actionId, new HashMap<String, String>());
//                                try {
//                                    this.vsLcmService.execDayTwoAction(message, "");
//                                } catch (NotExistingEntityException ex) {
//                                    java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
//                                } catch (NotPermittedOperationException ex) {
//                                    java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
//                                }
//                                break;
//                            }
//                        }
//                        if(actionId!=null){
//                            pendingActions.remove(actionId);
//                            this.vsRecordService.addPendingActions(vsi.getVsiId(), pendingActions);
//                        }
//                    }
//                }else{
//                    pendingActions.put(ruleId, ruleName);
//                    this.vsRecordService.addPendingActions(vsi.getVsiId(), pendingActions);
//                }
                break;
            }
            case "activatemtd":{
//                if(mtdInfo.size()==vsi.getNssis().size()){

                while(mtdInfo.size()!=vsi.getNssis().size()){
                    log.info("Waiting for all MTD info.");
                    try {
                        Thread.sleep(30000);
                        vsi = this.vsRecordService.getVsInstancesFromNetworkSliceSubnet(request.getNsiId()).get(0);
                        mtdInfo = vsi.getMtdInfo();
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                this.activateMTD(vsi, nssiId);
                    
//                    if(pendingActions.size()>0){
//                        String actionId=null;
//                        for(Entry<String, String> action : pendingActions.entrySet()){
//                            if(action.getValue().equals("activatemtd")){
//                                actionId=action.getKey();
//                                Day2ActionRequest message = new Day2ActionRequest(vsi.getVsiId(), actionId, new HashMap<String, String>());
//                                try {
//                                    this.vsLcmService.execDayTwoAction(message, "");
//                                } catch (NotExistingEntityException ex) {
//                                    java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
//                                } catch (NotPermittedOperationException ex) {
//                                    java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
//                                }
//                                break;
//                            }
//                        }
//                        if(actionId!=null){
//                            pendingActions.remove(actionId);
//                            this.vsRecordService.addPendingActions(vsi.getVsiId(), pendingActions);
//                        }
//                    }
//                }else{
//                    pendingActions.put(ruleId, ruleName);
//                    this.vsRecordService.addPendingActions(vsi.getVsiId(), pendingActions);
//                }
                
                break;
            }
            case "getvnfinfo":{
                JSONObject actionRequest = new JSONObject();
                actionRequest.put("primitive", "getvnfinfo");
                actionRequest.put("primitive_params", new JSONObject());
                //performing with the nsr id
                actionRequest.put("member_vnf_index", "1");
                JSONObject response = client.nsInstances.actionNSi(nsi.getNfvNsId(), actionRequest);
                String actionId = (String)response.get("id");

//                String actionStatus="RUNNING";
//                while(!actionStatus.equals("COMPLETED")){
//                    try {
//                        response = client.nsInstances.listNSLcmOpOcc(actionId);
//                        actionStatus=(String)response.get("operationState");
//                        if(!actionStatus.equals("COMPLETED")){
//                            Thread.sleep(30000);
//                        }
//                    } catch (InterruptedException ex) {
//                        java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }

//                JSONParser parser = new JSONParser();
//                try {
//                    response = (JSONObject) parser.parse((String)((JSONObject)response.get("detailed-status")).get("output"));
//                } catch (ParseException ex) {
//                    java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
//                }
//
//                interdomainInfo.put(nsi.getNfvNsId(), response);
//                this.vsRecordService.addInterdomainInfo(vsi.getVsiId(), interdomainInfo);
                break;
            }
            case "getmtdinfo":{
                JSONObject actionRequest = new JSONObject();
                actionRequest.put("primitive", "getmtdinfo");
                actionRequest.put("primitive_params", new JSONObject());
                //performing with the nsr id
                actionRequest.put("member_vnf_index", "2");
                JSONObject response = client.nsInstances.actionNSi(nsi.getNfvNsId(), actionRequest);
                String actionId = (String)response.get("id");

//                String actionStatus="RUNNING";
//                while(!actionStatus.equals("COMPLETED")){
//                    try {
//                        response = client.nsInstances.listNSLcmOpOcc(actionId);
//                        actionStatus=(String)response.get("operationState");
//                        if(!actionStatus.equals("COMPLETED")){
//                            Thread.sleep(30000);
//                        }
//                    } catch (InterruptedException ex) {
//                        java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }

//                JSONParser parser = new JSONParser();
//                try {
//                    response = (JSONObject) parser.parse((String)((JSONObject)response.get("detailed-status")).get("output"));
//                } catch (ParseException ex) {
//                    java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
//                }
//
//                mtdInfo.put(nsi.getNfvNsId(), response);
//                this.vsRecordService.addMtdInfo(vsi.getVsiId(), mtdInfo);
                break;
            }
            default:{
                JSONObject actionRequest = new JSONObject();
                actionRequest.put("primitive", ruleName);
                actionRequest.put("primitive_params", params);
                //performing with the nsr id
                actionRequest.put("member_vnf_index", "1");
                    
                JSONObject response = client.nsInstances.actionNSi(nsi.getNfvNsId(), actionRequest);
                String actionId = (String)response.get("id");

//                String actionStatus="RUNNING";
//                while(!actionStatus.equals("COMPLETED")){
//                    try {
//                        response = client.nsInstances.listNSLcmOpOcc(actionId);
//                        actionStatus=(String)response.get("operationState");
//                        if(!actionStatus.equals("COMPLETED")){
//                            Thread.sleep(30000);
//                        }
//                    } catch (InterruptedException ex) {
//                        java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
                break;
            }    
        }
        
//        NetworkSliceStatusChangeNotification notification = new NetworkSliceStatusChangeNotification(request.getNsiId(), NetworkSliceStatusChange.NSI_CONFIGURED, true);
//        this.vsLcmService.notifyNetworkSliceStatusChange(notification);
        this.pollingManager.addOperation(UUID.randomUUID().toString(), OperationStatus.SUCCESSFULLY_DONE, request.getNsiId(), "NSI_CONFIGURATION", domainId, NspNbiType.OSM);
    }
    
    private void activateMTD(VerticalServiceInstance vsi, String nssiId){
        log.info("Activating MTD in service '"+vsi.getId()+"'");

        JSONObject actionRequest = new JSONObject();
        actionRequest.put("primitive", "activatemtd");
        Map<String,String> actionParameters = new HashMap<String,String>();
        
        Map<String, JSONObject> interdomainInfo = vsi.getInterdomainInfo();
        Map<String, JSONObject> mtdInfo = vsi.getMtdInfo();
        
        for(Entry<String, JSONObject>mtdIps : mtdInfo.entrySet()){
            String tmpNssiId=mtdIps.getKey();
            JSONObject mtpInfo=mtdIps.getValue();
            Long mode = (Long)mtpInfo.get("mtdMode");
            if(mode == 3){
                actionParameters.put("ip-client",(String)interdomainInfo.get(tmpNssiId).get("vnfIp"));
                actionParameters.put("mac-client",(String)interdomainInfo.get(tmpNssiId).get("vnfMAC"));
                actionParameters.put("mac-gw-client",(String)interdomainInfo.get(tmpNssiId).get("gwMAC"));
                actionParameters.put("ip-mtd-client-internal",(String)mtpInfo.get("mtdInternalIp"));
                actionParameters.put("ip-mtd-client-public",(String)mtpInfo.get("mtdPublicIp"));
                actionParameters.put("mac-mtd-client",(String)mtpInfo.get("mtdMAC"));
                
            }else{
                actionParameters.put("ip-server",(String)interdomainInfo.get(tmpNssiId).get("vnfIp"));
                actionParameters.put("mac-server",(String)interdomainInfo.get(tmpNssiId).get("vnfMAC"));
                actionParameters.put("mac-gw-server",(String)interdomainInfo.get(tmpNssiId).get("gwMAC"));
                actionParameters.put("ip-mtd-server-internal",(String)mtpInfo.get("mtdInternalIp"));
                actionParameters.put("ip-mtd-server-public",(String)mtpInfo.get("mtdPublicIp"));
                actionParameters.put("mac-mtd-server",(String)mtpInfo.get("mtdMAC"));
            }
            
        }
        actionRequest.put("primitive_params", actionParameters);
        actionRequest.put("member_vnf_index", "2");
        
        log.info("MTD info sent: "+actionRequest.toJSONString());
        
        JSONObject response = client.nsInstances.actionNSi(nssiId, actionRequest);
        String actionId = (String)response.get("id");
        String actionStatus="RUNNING";
        while(!actionStatus.equals("COMPLETED")){
            try {
                response = client.nsInstances.listNSLcmOpOcc(actionId);
                actionStatus=(String)response.get("operationState");
                if(!actionStatus.equals("COMPLETED")){
                    Thread.sleep(30000);
                }
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        log.info("Activated MTD in subnet with nsrId '"+nssiId+"'");
    }

    private List<JSONObject> getNsiOperations(NetworkSliceInstance nsi) {
        JSONArray operations = this.client.nsInstances.listNSLcmOpOccs();
        List<JSONObject> finalOperations = new ArrayList<JSONObject>();
        for(int i=0; i<operations.size();i++){
            JSONObject op = (JSONObject) operations.get(i);
            if(op.get("nsInstanceId").equals(nsi.getNfvNsId())){
                if(op.get("lcmOperationType").equals("action")){
                    JSONObject action = new JSONObject();
                    action.put("name", (String)((JSONObject)op.get("operationParams")).get("primitive"));
                    action.put("id", (String)op.get("id"));
                    action.put("status", (String)op.get("operationState"));
                    
                    if(((String)op.get("operationState")).equals("COMPLETED")){
                        JSONParser parser = new JSONParser();
                        try {
                            action.put("output",(JSONObject) parser.parse((String)((JSONObject)op.get("detailed-status")).get("output")));
                        } catch (ParseException ex) {
                            java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    finalOperations.add(action);
                }
            }
        }
        return finalOperations;
    }
    
}