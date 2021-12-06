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
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
    private ConfigurationRuleRepository configurationRuleRepository;
    private NsmfLcmOperationPollingManager pollingManager;
    private VsRecordService vsRecordService;
    private String vimAccount;
    private String uri;
    private VsLcmService vsLcmService;
    private VerticalServiceInstanceRepository vsInstanceRepository;
    private List<String> processedActions=new ArrayList<String>();
    private Integer poolingTime=10;
    

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
        try {
            Instant instant = Instant.now();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/instantiate/stop/instantiateVsOSM/"+instant.toString());
            httpClient.execute(valueCollection);
            valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+request.getNsiId()+"/start/instantiateOSM/"+instant.toString());
            httpClient.execute(valueCollection);
        } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}
        
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
        try {
            Instant instant = Instant.now();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/terminate/stop/terminateVsOSM/"+instant.toString());
            httpClient.execute(valueCollection);
            valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+request.getNsiId()+"/start/terminateOSM/"+instant.toString());
            httpClient.execute(valueCollection);
        } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}
        
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
            
            List<NetworkSliceInstance> nsis = new ArrayList<NetworkSliceInstance>();
            
            NetworkSliceInstance nsi = new NetworkSliceInstance();
            nsi.setNsiId(nsiID);
            nsi.setNfvNsId((String)((List<JSONObject>)response.get("nsr-ref-list")).get(0).get("nsr-ref"));
            switch((String)response.get("operational-status")){
                case "init":
                    nsi.setStatus(NetworkSliceStatus.INSTANTIATING);
                    break;
                case "running":
                    try {
                        Instant instant = Instant.now();
                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+nsiID+"/stop/instantiateOSM/"+instant.toString());
                        httpClient.execute(valueCollection);
                    } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}
                    nsi.setStatus(NetworkSliceStatus.INSTANTIATED);
                    break;
                case "terminating":
                    nsi.setStatus(NetworkSliceStatus.TERMINATING);
                    nsis.add(nsi);
                    return nsis;
                case "terminated":
                    try {
                        Instant instant = Instant.now();
                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+nsiID+"/stop/terminateOSM/"+instant.toString());
                        httpClient.execute(valueCollection);
                    } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}
                    nsi.setStatus(NetworkSliceStatus.TERMINATED);
                    nsis.add(nsi);
                    return nsis;
            }
            
            List<JSONObject> operations = this.getNsiOperations(nsi);
            if(!operations.isEmpty()){
                boolean allConfigured=true;
                for(JSONObject op : operations){
                    if(!this.processedActions.contains((String)op.get("id"))){
                        this.processedActions.add((String)op.get("id"));
                        if(((String)op.get("status")).equals("COMPLETED")){
                            VerticalServiceInstance vsi = this.vsRecordService.getVsInstancesFromNetworkSliceSubnet(nsi.getNsiId()).get(0);
                            switch((String)op.get("name")){
                                case "getvnfinfo":{
                                    JSONObject aux = (JSONObject)op.get("output");
                                    log.info(aux.toJSONString());
                                    this.vsRecordService.addInterdomainInfo(vsi.getVsiId(), nsi.getNfvNsId(), aux);
                                    
                                    try {
                                        Instant instant = Instant.now();
                                        CloseableHttpClient httpClient = HttpClients.createDefault();
                                        HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+nsiID+"/stop/OSMgetvnfinfo/"+instant.toString());
                                        httpClient.execute(valueCollection);
                                    } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}
                                    
                                    break;
                                }
                                case "getmtdinfo":{
                                    JSONObject aux = (JSONObject)op.get("output");
                                    log.info(aux.toJSONString());
                                    this.vsRecordService.addMtdInfo(vsi.getVsiId(), nsi.getNfvNsId(), aux);
                                    
                                    try {
                                        Instant instant = Instant.now();
                                        CloseableHttpClient httpClient = HttpClients.createDefault();
                                        HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+nsiID+"/stop/OSMgetmtdinfo/"+instant.toString());
                                        httpClient.execute(valueCollection);
                                    } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}
                                    
                                    break;
                                }
                                case "addpeer":{
                                    try {
                                        Instant instant = Instant.now();
                                        CloseableHttpClient httpClient = HttpClients.createDefault();
                                        HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+nsiID+"/stop/OSMaddpeer/"+instant.toString());
                                        httpClient.execute(valueCollection);
                                    } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}
                                    break;
                                }
                                case "activatemtd":{
                                    try {
                                        Instant instant = Instant.now();
                                        CloseableHttpClient httpClient = HttpClients.createDefault();
                                        HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+nsiID+"/stop/OSMactivatemtd/"+instant.toString());
                                        httpClient.execute(valueCollection);
                                    } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}
                                    break;
                                }
                                case "routemgmt":{
                                    try {
                                        Instant instant = Instant.now();
                                        CloseableHttpClient httpClient = HttpClients.createDefault();
                                        HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+nsiID+"/stop/OSMroutemgmt/"+instant.toString());
                                        httpClient.execute(valueCollection);
                                    } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}
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
                new AsyncConfiguration(ruleName, request.getNsiId(), nssiId, domainId, params).start();
                return;
            }
            case "activatemtd":{
                new AsyncConfiguration(ruleName, request.getNsiId(), nssiId, domainId, params).start();
                return;
            }
            case "routemgmt":{
                new AsyncConfiguration(ruleName, request.getNsiId(), nssiId, domainId, params).start();
                return;
            }
            case "getvnfinfo":{
                try {
                    Instant instant = Instant.now();
                    CloseableHttpClient httpClient = HttpClients.createDefault();
                    HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+nssiId+"/start/OSMgetvnfinfo/"+instant.toString());
                    httpClient.execute(valueCollection);
                } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}
                JSONObject actionRequest = new JSONObject();
                actionRequest.put("primitive", "getvnfinfo");
                actionRequest.put("primitive_params", new JSONObject());
                //performing with the nsr id
                actionRequest.put("member_vnf_index", "1");
                JSONObject response = client.nsInstances.actionNSi(nsi.getNfvNsId(), actionRequest);
                break;
            }
            case "getmtdinfo":{
                try {
                    Instant instant = Instant.now();
                    CloseableHttpClient httpClient = HttpClients.createDefault();
                    HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+nssiId+"/start/OSMgetmtdinfo/"+instant.toString());
                    httpClient.execute(valueCollection);
                } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}
                JSONObject actionRequest = new JSONObject();
                actionRequest.put("primitive", "getmtdinfo");
                actionRequest.put("primitive_params", new JSONObject());
                //performing with the nsr id
                actionRequest.put("member_vnf_index", "2");
                JSONObject response = client.nsInstances.actionNSi(nsi.getNfvNsId(), actionRequest);
                break;
            }
            default:{
                JSONObject actionRequest = new JSONObject();
                actionRequest.put("primitive", ruleName);
                actionRequest.put("primitive_params", params);
                //performing with the nsr id
                actionRequest.put("member_vnf_index", "1");
                    
                JSONObject response = client.nsInstances.actionNSi(nsi.getNfvNsId(), actionRequest);
                break;
            }    
        }
        
        this.pollingManager.addOperation(UUID.randomUUID().toString(), OperationStatus.SUCCESSFULLY_DONE, request.getNsiId(), "NSI_CONFIGURATION", domainId, NspNbiType.OSM);
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
                        } catch (Exception ex) {
                            java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.WARNING, null, "Error while parsing action output. Not in JSON format.");
                        }
                    }
                    
                    finalOperations.add(action);
                }
            }
        }
        return finalOperations;
    }
    
    private class AsyncConfiguration extends Thread{
        
        private String ruleName;
        private String nssiId;
        private String domainId;
        private String nssiNfvId;
        private Map<String, String> params;

        public AsyncConfiguration(String ruleName, String nssiId, String nssiNfvId, String domainId, Map<String, String> params) {
            this.nssiId=nssiId;
            this.ruleName=ruleName;
            this.nssiNfvId=nssiNfvId;
            this.domainId=domainId;
            this.params=params;
        }
        
        @Override
        public void run() {
            switch(ruleName){
                case "addpeer":{
                    VerticalServiceInstance auxVsi = vsRecordService.getVsInstancesFromNetworkSliceSubnet(nssiId).get(0);
                    Map<String, JSONObject> auxInterdomainInfo = auxVsi.getInterdomainInfo();

                    while(auxInterdomainInfo.size()!=auxVsi.getNssis().size()){
                        log.info("Waiting for all Interdomain info");
                        try {
                            Thread.sleep(poolingTime*1000);
                            auxVsi = vsRecordService.getVsInstancesFromNetworkSliceSubnet(nssiId).get(0);
                            auxInterdomainInfo = auxVsi.getInterdomainInfo();
                        } catch (InterruptedException ex) {
                            java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        Instant instant = Instant.now();
                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+nssiId+"/start/OSMaddpeer/"+instant.toString());
                        httpClient.execute(valueCollection);
                    } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}
                    

                    for(String nssiId2 : auxInterdomainInfo.keySet()){                  
                        if(!nssiNfvId.equals(nssiId2)){
                            log.info("Subnet with nsrId '"+nssiId+"' is receiving information from the Subnet with nsrId '"+nssiId2+"'");

                            JSONObject actionRequest = new JSONObject();
                            actionRequest.put("primitive", "addpeer");
                            Map<String,String> actionParameters = new HashMap<String,String>();
                            
                            for(Entry<String,String> entry:params.entrySet()){
                                actionParameters.put(entry.getKey(), entry.getValue());
                            }
                            
                            actionParameters.put("peer_key", (String)auxInterdomainInfo.get(nssiId2).get("public_key"));
                            
                            String peerIp;
                            if(auxInterdomainInfo.get(nssiId2).containsKey("publicEndpoint")){
                                peerIp=(String)auxInterdomainInfo.get(nssiId2).get("publicEndpoint");
                            }else{
                                peerIp=(String)auxInterdomainInfo.get(nssiId2).get("internalEndpoint");
                            }
                            
                            actionParameters.put("peer_endpoint", peerIp);
                            actionParameters.put("peer_network", (String)params.get("peer_network") + auxInterdomainInfo.get(nssiId2).get("PEER_ALLOWED_NETWORK"));
                            actionRequest.put("primitive_params", actionParameters);
                            actionRequest.put("member_vnf_index", "1");
                            JSONObject response = client.nsInstances.actionNSi(nssiNfvId, actionRequest);
                        }
                    }
                    break;
                }
                case "activatemtd":{
                    VerticalServiceInstance auxVsi = vsRecordService.getVsInstancesFromNetworkSliceSubnet(nssiId).get(0);
                    Map<String, JSONObject> auxInterdomainInfo = auxVsi.getInterdomainInfo();
                    Map<String, JSONObject> auxMtdInfo = auxVsi.getMtdInfo();
                    
                    while((auxMtdInfo.size()!=auxVsi.getNssis().size()) || (auxInterdomainInfo.size()!=auxVsi.getNssis().size())){
                        log.info("Waiting for all MTD info.");
                        try {
                            Thread.sleep(poolingTime*1000);
                            auxVsi = vsRecordService.getVsInstancesFromNetworkSliceSubnet(nssiId).get(0);
                            auxInterdomainInfo = auxVsi.getInterdomainInfo();
                            auxMtdInfo = auxVsi.getMtdInfo();
                        } catch (InterruptedException ex) {
                            java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    try {
                        Instant instant = Instant.now();
                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+nssiId+"/start/OSMactivatemtd/"+instant.toString());
                        httpClient.execute(valueCollection);
                    } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}

                    log.info("Activating MTD in the NSSI '"+nssiId+"' of service '"+auxVsi.getId()+"'");

                    JSONObject actionRequest = new JSONObject();
                    actionRequest.put("primitive", "activatemtd");
                    Map<String,String> actionParameters = new HashMap<String,String>();

                    for(Entry<String, JSONObject>mtdIps : auxMtdInfo.entrySet()){
                        String tmpNssiId=mtdIps.getKey();
                        JSONObject mtdInfo=mtdIps.getValue();
                        Long mode;
                        try{
                            mode = Long.valueOf((String)mtdInfo.get("mtdMode"));
                        }catch(Exception e){
                            mode = (Long)mtdInfo.get("mtdMode");
                        }
                        
                        if(mode == 3){
                            
                            String peerIp;
                            if(tmpNssiId.equals(this.nssiNfvId)){
                                peerIp=(String)auxInterdomainInfo.get(tmpNssiId).get("internalEndpoint");
                            }else{
                                if(auxInterdomainInfo.get(tmpNssiId).containsKey("publicEndpoint")){
                                    peerIp=(String)auxInterdomainInfo.get(tmpNssiId).get("publicEndpoint");
                                }else{
                                    peerIp=(String)auxInterdomainInfo.get(tmpNssiId).get("internalEndpoint");
                                }
                            }
                            
                            actionParameters.put("ip-peer1",peerIp);
                            actionParameters.put("mac-peer1",(String)auxInterdomainInfo.get(tmpNssiId).get("vnfMAC"));
                            actionParameters.put("mac-gw-peer1",(String)mtdInfo.get("gwMAC"));
                            actionParameters.put("ip-mtd-peer1-internal",(String)mtdInfo.get("mtdInternalIp"));
                            actionParameters.put("ip-mtd-peer1-public",(String)mtdInfo.get("mtdPublicIp"));
                            actionParameters.put("mac-mtd-peer1",(String)mtdInfo.get("mtdMAC"));

                        }else{
                            
                            String peerIp;
                            if(tmpNssiId.equals(this.nssiNfvId)){
                                peerIp=(String)auxInterdomainInfo.get(tmpNssiId).get("internalEndpoint");
                            }else{
                                if(auxInterdomainInfo.get(tmpNssiId).containsKey("publicEndpoint")){
                                    peerIp=(String)auxInterdomainInfo.get(tmpNssiId).get("publicEndpoint");
                                }else{
                                    peerIp=(String)auxInterdomainInfo.get(tmpNssiId).get("internalEndpoint");
                                }
                            }
                            
                            actionParameters.put("ip-peer2",peerIp);
                            actionParameters.put("mac-peer2",(String)auxInterdomainInfo.get(tmpNssiId).get("vnfMAC"));
                            actionParameters.put("mac-gw-peer2",(String)mtdInfo.get("gwMAC"));
                            actionParameters.put("ip-mtd-peer2-internal",(String)mtdInfo.get("mtdInternalIp"));
                            actionParameters.put("ip-mtd-peer2-public",(String)mtdInfo.get("mtdPublicIp"));
                            actionParameters.put("mac-mtd-peer2",(String)mtdInfo.get("mtdMAC"));
                        }

                    }
                    actionRequest.put("primitive_params", actionParameters);
                    actionRequest.put("member_vnf_index", "2");

                    log.info("MTD info sent: "+actionRequest.toJSONString());

                    JSONObject response = client.nsInstances.actionNSi(nssiNfvId, actionRequest);
                    log.info("Activated MTD in subnet with nsrId '"+nssiId+"'");
                    
                    break;
                }
                case "routemgmt":{
                    VerticalServiceInstance auxVsi = vsRecordService.getVsInstancesFromNetworkSliceSubnet(nssiId).get(0);
                    Map<String, JSONObject> auxInterdomainInfo = auxVsi.getInterdomainInfo();
                    Map<String, JSONObject> auxMtdInfo = auxVsi.getMtdInfo();
                    
                    while((auxMtdInfo.size()!=auxVsi.getNssis().size()) || (auxInterdomainInfo.size()!=auxVsi.getNssis().size())){
                        log.info("Waiting for all MTD info.");
                        try {
                            Thread.sleep(poolingTime*1000);
                            auxVsi = vsRecordService.getVsInstancesFromNetworkSliceSubnet(nssiId).get(0);
                            auxInterdomainInfo = auxVsi.getInterdomainInfo();
                            auxMtdInfo = auxVsi.getMtdInfo();
                        } catch (InterruptedException ex) {
                            java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    try {
                        Instant instant = Instant.now();
                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        HttpGet valueCollection = new HttpGet("http://10.0.12.120:9999/VS/"+nssiId+"/start/OSMroutemgmt/"+instant.toString());
                        httpClient.execute(valueCollection);
                    } catch (Exception ex) {java.util.logging.Logger.getLogger(OsmVsDriver.class.getName()).log(Level.SEVERE, null, ex);}
                    
                    log.info("Performing route managment action on NSI '"+nssiId+"'");

                    JSONObject actionRequest = new JSONObject();
                    actionRequest.put("primitive", "routemgmt");
                    Map<String,String> actionParameters = new HashMap<String,String>();

                    for(Entry<String,String> entry:params.entrySet()){
                        actionParameters.put(entry.getKey(), entry.getValue());
                    }
                    
                    for(String nssiId2 : auxInterdomainInfo.keySet()){                  
                        if(!nssiNfvId.equals(nssiId2)){
                            actionParameters.put("gw-address",(String)auxMtdInfo.get(nssiNfvId).get("mtdInternalIp"));
                            
                            String peerIp;
                            
                            if(auxInterdomainInfo.get(nssiId2).containsKey("publicEndpoint")){
                                peerIp=(String)auxInterdomainInfo.get(nssiId2).get("publicEndpoint");
                            }else{
                                peerIp=(String)auxInterdomainInfo.get(nssiId2).get("internalEndpoint");
                            }
                            
                            actionParameters.put("allowed-ips",peerIp+"/32");
                            break;
                        }
                    }

                    actionRequest.put("primitive_params", actionParameters);
                    actionRequest.put("member_vnf_index", "1");

                    JSONObject response = client.nsInstances.actionNSi(nssiNfvId, actionRequest);
                    log.info("Route Management action on NSI '"+nssiId+"' successfully performed.");
                    
                    break;
                }
            }
            pollingManager.addOperation(UUID.randomUUID().toString(), OperationStatus.SUCCESSFULLY_DONE, nssiId, "NSI_CONFIGURATION", domainId, NspNbiType.OSM);
        }
        
    }
    
}
