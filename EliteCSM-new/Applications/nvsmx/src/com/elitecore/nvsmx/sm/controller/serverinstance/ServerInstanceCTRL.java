package com.elitecore.nvsmx.sm.controller.serverinstance;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.ServerInfo;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.ServerGroups;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.filelocation.FileLocationData;
import com.elitecore.corenetvertex.sm.notificationagents.EmailAgentData;
import com.elitecore.corenetvertex.sm.notificationagents.SMSAgentData;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupServerInstanceRelData;
import com.elitecore.corenetvertex.sm.serverinstance.ConfigurationDatabase;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceGroovyScriptData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.RMIGroup;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.remotecommunications.RMIResponse;
import com.elitecore.nvsmx.remotecommunications.RemoteMessageCommunicator;
import com.elitecore.nvsmx.remotecommunications.RemoteMethod;
import com.elitecore.nvsmx.remotecommunications.RemoteMethodConstant;
import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.sm.controller.serverinstancelivedetail.ServerInstanceLiveDetailProvider;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.RegexConstants;
import com.elitecore.nvsmx.system.db.NVSMXDBConnectionManager;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;


import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by kirpalsinh on 2/8/17.
 */
@ParentPackage(value = NVSMXCommonConstants.REST_PARENT_PKG_SM)
@Namespace("/sm/serverinstance")
@org.apache.struts2.convention.annotation.Results({
        @Result(name= SUCCESS, type= RestGenericCTRL.REDIRECT_ACTION,   params = {NVSMXCommonConstants.ACTION_NAME,"../servergroup/server-group"}),
})
public class ServerInstanceCTRL extends RestGenericCTRL<ServerInstanceData>{
    private static final String MODULE = ServerInstanceCTRL.class.getSimpleName();
    private static final String SERVER_GROUP_ID = "serverGroupId";
    private static final String UTF_8 = "UTF-8";
    private static final String COLON = ":";
    private static final String INFORMATION_REASON = " information. Reason: ";
    private static final String DATA_FOR_ID = " data for id ";
    private static final String BOLD_ITALIC_TAG_INIT = " <b><i>";
    private static final String ITALIC_BOLD_TAG_END = "</i></b> ";
    private static final String RELOAD_RESPONSES = "reloadResponses";
    private static final String RELOAD = "reload";
    
    private List<FileLocationData> fileLocationList = Collectionz.newArrayList();
    private String fileLocationListJson;
    private transient List<SMSAgentData> smsAgentDataList = new ArrayList<>();
    private transient List<EmailAgentData> emailAgentDataList = new ArrayList<>();
    private static final Predicate<ServerInstanceGroovyScriptData> GROOVY_PREDICATE = groovyScript -> groovyScript == null ? false : groovyScript.getScriptName() !=null;
    private List<ServerInstanceGroovyScriptData> groovyScriptList = Collectionz.newArrayList();


    @SkipValidation
    @Override
    public void prepareValuesForSubClass() throws Exception {
    	 setFileLocationList(CRUDOperationUtil.findAll(FileLocationData.class));
    	 String serverGroupType = getRequest().getParameter(Attributes.SERVER_GROUP_TYPE);
    	 setEmailAgentDataList(CRUDOperationUtil.findAll(EmailAgentData.class));
         setSmsAgentDataList(CRUDOperationUtil.findAll(SMSAgentData.class));

    	 if(Strings.isNullOrBlank(serverGroupType)==false){
             getRequest().getSession().setAttribute(Attributes.SERVER_GROUP_TYPE, serverGroupType);
             getRequest().getSession().setAttribute(Attributes.SERVER_GROUP_TAB_TYPE, serverGroupType);
         }
    }
    
    @Override
    public HttpHeaders create() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called create()");
        }

        try {
            ServerInstanceData serverInstanceData = (ServerInstanceData) getModel();
            boolean isAlreadyExist = isDuplicateEntity("name", serverInstanceData.getResourceName(), "update");
            if (isAlreadyExist) {
                addFieldError("name", "Name already exists");
                new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.ALREADY_EXIST.code);
            }
            ServerGroupData serverGroupData = getServerGroupData(serverInstanceData);
            if (serverGroupData == null) {
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
            }

            boolean associationFlag = isValidSeverGroupAssociations(serverInstanceData.getId(), serverGroupData);
            if (!associationFlag) {
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
            }

            ServerGroupServerInstanceRelData serverGroupServerInstanceRelData = new ServerGroupServerInstanceRelData();
            serverGroupServerInstanceRelData.setServerGroupData(serverGroupData);
            serverGroupServerInstanceRelData.setServerInstanceData(serverInstanceData);

            if (Collectionz.isNullOrEmpty(serverGroupData.getServerInstances())) {
                serverGroupServerInstanceRelData.setServerWeightage(ServerInstanceWeightage.PRIMARY.val);
            } else {
                serverGroupServerInstanceRelData.setServerWeightage(ServerInstanceWeightage.SECONDARY.val);
            }

            serverInstanceData.setServerGroupServerInstanceRelData(serverGroupServerInstanceRelData);


            String result = authorize();
            if (result.equals(SUCCESS) == false) {
                setActionChainUrl(getRedirectURL(METHOD_EDITNEW));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }
            String serverGroupIdTemp = getRequest().getParameter(SERVER_GROUP_ID);
            if(Strings.isNullOrBlank(serverGroupIdTemp)==false){
                serverInstanceData.setServerGroupId(serverGroupIdTemp);
            }

            String serverGroupType =getRequest().getParameter(Attributes.SERVER_GROUP_TYPE);
            if(Strings.isNullOrEmpty(serverGroupType) == false && ServerGroups.OFFLINE_RNC.getValue().equals(serverGroupType)){
            	serverInstanceData.setRadiusEnabled(false);
            	serverInstanceData.setDiameterEnabled(false);
            }


            serverInstanceData.setStatus(NVSMXCommonConstants.ACTIVE);

            StaffData staffData = getStaffData();
            serverInstanceData.setCreatedDateAndStaff(staffData);

            if(Strings.isNullOrBlank(serverInstanceData.getId())){
                serverInstanceData.setId(UUID.randomUUID().toString());
            }
            setGroovyScriptDatas();
            wsWriteServerInstanceData(serverInstanceData);

            CRUDOperationUtil.save(serverInstanceData);
            String message = getModule().getDisplayLabel() + BOLD_ITALIC_TAG_INIT + serverInstanceData.getResourceName() + ITALIC_BOLD_TAG_END + "Created";
            CRUDOperationUtil.audit(serverInstanceData, serverInstanceData.getServerGroupServerInstanceRelData().getServerGroupData().getResourceName(), AuditActions.CREATE, getStaffData(), getRequest().getRemoteAddr(), serverInstanceData.getHierarchy(), message);
            addActionMessage(getModule().getDisplayLabel() + " created successfully");
            CRUDOperationUtil.flushSession();

            reload();
            setActionChainUrl(getRedirectURL(METHOD_INDEX));
            return new DefaultHttpHeaders(SUCCESS).withStatus(HttpServletResponse.SC_CREATED);

        } catch(NoRouteToHostException | ConnectTimeoutException | ConnectException ce){
            getLogger().error(getLogModule(),"Error while creating "+ getModule().getDisplayLabel() + INFORMATION_REASON +ce.getMessage());
            getLogger().trace(getLogModule(),ce);
            ServerInstanceData model = (ServerInstanceData) getModel();
            String restURL = ServerInstanceLiveDetailProvider.getRestApiUrl(model.getRestApiUrl());
            addActionError("Failed to Create "+getModule().getDisplayLabel()+" due to Connection refused on URL: " + restURL);

        } catch (Exception e){
            getLogger().error(getLogModule(),"Error while creating "+ getModule().getDisplayLabel() +INFORMATION_REASON+e.getMessage());
            getLogger().trace(getLogModule(),e);
            if(e.getMessage()==null){
                addActionError("Failed to Create Server Instance.");
            }else{
                addActionError("Failed to Create Server Instance. "+e.getMessage());
            }

        }
        return new DefaultHttpHeaders(SUCCESS).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
    }

	public void wsWriteServerInstanceData(ServerInstanceData serverInstanceData)
            throws NoSuchEncryptionException, EncryptionFailedException, InterruptedException, IOException{

            if(getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Method called 'writeServerInstanceData' WebService");
            }

            String encryptedServerId = PasswordEncryption.getInstance().crypt(serverInstanceData.getId(),PasswordEncryption.ELITECRYPT);
            String encryptedServerName = PasswordEncryption.getInstance().crypt(serverInstanceData.getName(),PasswordEncryption.ELITECRYPT);

            Gson gson = GsonFactory.defaultInstance();
            ConfigurationDatabase configurationDatabase = getConfigurationDatabaseFromContext();
            JsonObject databaseDataJson = gson.toJsonTree(configurationDatabase).getAsJsonObject();

            HttpResponse response = prepareHttpPostAndGetHttpResponse(encryptedServerId, encryptedServerName, databaseDataJson);

            HttpEntity httpEntity = response.getEntity();
            String responseString = EntityUtils.toString(httpEntity, UTF_8);
            String decodedResponse = URLDecoder.decode(responseString,UTF_8);

            if(Strings.isNullOrBlank(decodedResponse) || decodedResponse.charAt(0)!='{'){
                throw new JsonParseException("Failed to write Server Instance on NetVertex Server. Reason: "+decodedResponse);
            }

            ServerInfo serverInfo = GsonFactory.defaultInstance().fromJson(decodedResponse, ServerInfo.class);

            if(getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Received ServerInfo: " + serverInfo);
            }

            if(serverInfo==null){
                throw new NullPointerException("Received Empty ServerInfo");
            }

            if(Strings.isNullOrBlank(serverInfo.getServerHome())){
                throw new NullPointerException("Received ServerInfo has Empty ServerHome");
            }

            if(Strings.isNullOrBlank(serverInfo.getJavaHome())){
                throw new NullPointerException("Received ServerInfo has Empty JavaHome");
            }

            serverInstanceData.setServerHome(serverInfo.getServerHome());
            serverInstanceData.setJavaHome(serverInfo.getJavaHome());
            String restIp = serverInstanceData.getRestApiUrl().split(COLON)[0];
            serverInstanceData.setJmxUrl(restIp+COLON+serverInfo.getJmxPort());

            getLogger().info(MODULE, "WebService 'writeServerInstanceData' called Successfully");

    }

    private HttpResponse prepareHttpPostAndGetHttpResponse(String encryptedServerId, String encryptedServerName, JsonObject databaseDataJson) throws InterruptedException, IOException {
        StringEntity entity = new StringEntity(databaseDataJson.toString());

        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        String path = ServerInstanceLiveDetailProvider.getRestApiUrl(((ServerInstanceData) getModel()).getRestApiUrl())+"/server-info";
        HttpPost postRequest = new HttpPost(path);

        postRequest.setEntity(entity);
        postRequest.setHeader("Accept", MediaType.APPLICATION_JSON);
        postRequest.setHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        postRequest.setHeader("serverInstanceId",encryptedServerId);
        postRequest.setHeader("serverInstanceName",encryptedServerName);

        if (Thread.interrupted()){
            throw new InterruptedException("WebService Call Interrupted");
        }

        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);

        HttpClient httpClient = new DefaultHttpClient(httpParams);
        return httpClient.execute(postRequest);
    }

    private ConfigurationDatabase getConfigurationDatabaseFromContext() throws NoSuchEncryptionException, EncryptionFailedException {

        BasicDataSource basicDataSource = NVSMXDBConnectionManager.getInstance().getBasicDataSource();

        ConfigurationDatabase configurationDatabase = new ConfigurationDatabase();
        configurationDatabase.setDriverClassName(basicDataSource.getDriverClassName());
        configurationDatabase.setUsername(basicDataSource.getUsername());
        String encryptedPassword = PasswordEncryption.getInstance().crypt(basicDataSource.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
        configurationDatabase.setPassword(encryptedPassword);
        configurationDatabase.setUrl(basicDataSource.getUrl());
        configurationDatabase.setMaxIdle(basicDataSource.getMaxIdle());
        configurationDatabase.setMaxTotal(basicDataSource.getMaxTotal());
        configurationDatabase.setValidationQueryTimeout(basicDataSource.getValidationQueryTimeout());
        return configurationDatabase;
    }

    private ServerGroupData getServerGroupData(ServerInstanceData serverInstanceData){
        /* This method check, the provided serverGroupId is valid or not, and
        * the provided serverGroupId data is available in the database or not. */

        String  serverGroupId = serverInstanceData.getServerGroupId();
        if(Strings.isNullOrBlank(serverGroupId)){
            setActionChainUrl(getRedirectURL(METHOD_INDEX));
            addActionError("ServerGroupId Required");
            return null;
        }

        ServerGroupData serverGroupData = CRUDOperationUtil.get(ServerGroupData.class,serverGroupId);

        if ( serverGroupData==null ){
            setActionChainUrl(getRedirectURL(METHOD_INDEX));
            addActionError("No ServerGroup Found for given ID");
        }

        return serverGroupData;
    }

    private boolean isValidSeverGroupAssociations(String serverInstanceId, ServerGroupData serverGroupData){
        /*  This method is for checking the provided serverGroupId in REST call has already two ServerInstances associated or not.
            If not then serverInstance can be associated with the provided serverGroupId. But
            If serverGroupId has already two serverInstances then we need to check that, this current serverInstance is among the two
            already associated serverInstances !    */

        if (serverGroupData.getServerInstances()!=null && serverGroupData.getServerInstances().size()==2){
            boolean isServerInstanceAlreadyAssociated =  false;
            for(ServerInstanceData serverInstanceData : serverGroupData.getServerInstances()){
                if(serverInstanceData.getId().equals(serverInstanceId)){
                    isServerInstanceAlreadyAssociated= true;
                    break;
                }
            }
            if(!isServerInstanceAlreadyAssociated) {
                setActionChainUrl(getRedirectURL(METHOD_INDEX));
                addActionError("Maximum two ServerInstances can be Associated with one ServerGroup");
                return false;
            }
        }

        return true;
    }

    @SkipValidation
    public HttpHeaders synchDatabaseDetail(){

        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called synchDatabaseDetail()");
        }

        try{

            String result = authorize();
            if (result.equals(SUCCESS) == false) {
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            ServerInstanceData serverInstanceData = (ServerInstanceData) getModel();
            serverInstanceData = CRUDOperationUtil.get(ServerInstanceData.class, serverInstanceData.getId());

            if(serverInstanceData==null){
                addActionError("Failed to Synchronize database detail");
            }else{
                if(getLogger().isDebugLogLevel()){
                    getLogger().debug(getLogModule(),"Synchronizing database detail for "+getModule().getDisplayLabel()+" having Id: "+serverInstanceData.getId());
                }
                setModel(serverInstanceData);
                wsWriteServerInstanceData(serverInstanceData);
                addActionMessage("Database detail Synchronized successfully");
            }

        } catch(NoRouteToHostException | ConnectTimeoutException | ConnectException ce){
            getLogger().error(getLogModule(),"Error during synchronization of Database detail. Reason: "+ ce.getMessage());
            getLogger().trace(getLogModule(),ce);
            ServerInstanceData model = (ServerInstanceData) getModel();
            String restURL = ServerInstanceLiveDetailProvider.getRestApiUrl(model.getRestApiUrl());
            addActionError("Failed to Synchronize database detail, due to Connection refused on URL: "+restURL);
            return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        } catch (Exception e){
            getLogger().error(getLogModule(),"Error while Synchronizing database detail for "+ getModule().getDisplayLabel() +". Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Failed to Synchronize database detail.");
            return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
        return new DefaultHttpHeaders(SUCCESS).disableCaching().withStatus(ResultCode.SUCCESS.code);
    }

	@Override
    public HttpHeaders update(){

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called update()");
        }

        try{

            String result = authorize();
            if (result.equals(SUCCESS) == false) {
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            ServerInstanceData newServerInstanceData = (ServerInstanceData) getModel();
            if(getLogger().isDebugLogLevel()){
                getLogger().debug(getLogModule(),"updating "+getModule().getDisplayLabel()+" with Id: "+newServerInstanceData.getId());
            }

            ServerInfo serverInfoData = ServerInstanceLiveDetailProvider.getRestIpPortStatus(((ServerInstanceData) getModel()).getRestApiUrl());
            if(serverInfoData==null){
                addActionError("No server alive on "+newServerInstanceData.getRestApiUrl());
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
            }

            boolean isAlreadyExist = isDuplicateEntity("name",newServerInstanceData.getResourceName(), "update");
            if(isAlreadyExist){
                addFieldError("name","Name already exists");
                new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.ALREADY_EXIST.code);
            }

            String isCallFromWeb = getRequest().getParameter("isCallFromWeb");
            if("true".equalsIgnoreCase(isCallFromWeb) == false ) {

                ServerGroupData serverGroupData = getServerGroupData(newServerInstanceData);
                if (serverGroupData == null) {
                    return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
                }

                boolean associationFlag = isValidSeverGroupAssociations(newServerInstanceData.getId(), serverGroupData);
                if (!associationFlag) {
                    return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
                }
            }

            ServerInstanceData oldServerInstanceData = CRUDOperationUtil.get(ServerInstanceData.class,newServerInstanceData.getId());

            ServerGroupServerInstanceRelData serverGroupServerInstanceRelData = oldServerInstanceData.getServerGroupServerInstanceRelData();
            serverGroupServerInstanceRelData.setServerInstanceData(newServerInstanceData);

            newServerInstanceData.setServerGroupServerInstanceRelData(serverGroupServerInstanceRelData);
            newServerInstanceData.setJavaHome(oldServerInstanceData.getJavaHome());
            newServerInstanceData.setServerHome(oldServerInstanceData.getServerHome());
            newServerInstanceData.setModifiedDateAndStaff(getStaffData());
            String restIp = newServerInstanceData.getRestApiUrl().split(COLON)[0];
            newServerInstanceData.setJmxUrl(restIp+COLON+serverInfoData.getJmxPort());
            setGroovyScriptDatas();
            CRUDOperationUtil.merge(newServerInstanceData);
            String message = getModule().getDisplayLabel() + BOLD_ITALIC_TAG_INIT + newServerInstanceData.getResourceName() + ITALIC_BOLD_TAG_END + "Updated";

            CRUDOperationUtil.audit(newServerInstanceData, newServerInstanceData.getServerGroupServerInstanceRelData().getServerGroupData().getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), newServerInstanceData.getHierarchy(), message);

            if(getLogger().isDebugLogLevel()){
                getLogger().debug(getLogModule(),"ServerInstance '"+newServerInstanceData.getName()+"' updated Successfully");
            }

            addActionMessage(getModule().getDisplayLabel()+" updated successfully");

            CRUDOperationUtil.flushSession();
            reload();
            setActionChainUrl(getRedirectURL(METHOD_INDEX));
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);

        } catch(NoRouteToHostException | ConnectTimeoutException | ConnectException ce){
            getLogger().error(getLogModule(),"Error while updating "+ getModule().getDisplayLabel() +INFORMATION_REASON+ce.getMessage());
            getLogger().trace(getLogModule(),ce);
            ServerInstanceData model = (ServerInstanceData) getModel();
            String restURL = ServerInstanceLiveDetailProvider.getRestApiUrl(model.getRestApiUrl());
            addActionError("Failed to update "+getModule().getDisplayLabel()+" due to Connection refused on URL: "+restURL);

        } catch (Exception e){
            getLogger().error(getLogModule(),"Error while updating "+ getModule().getDisplayLabel() +INFORMATION_REASON+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Failed to update ServerInstance. "+e.getMessage());
        }

        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
    }

    @Override
    @SkipValidation
    public HttpHeaders destroy() { // delete

        ServerInstanceData serverInstanceData = (ServerInstanceData) getModel();

        try{

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(getLogModule(), "Deleting " + getModule().getDisplayLabel() + " with Id: "+serverInstanceData.getId());
            }

            String result = authorize();
            if (result.equals(SUCCESS) == false) {
                setActionChainUrl(getRedirectURL(METHOD_INDEX));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            if(Strings.isNullOrBlank(serverInstanceData.getId()) == false){
                serverInstanceData = CRUDOperationUtil.get(ServerInstanceData.class,serverInstanceData.getId());
                if(serverInstanceData == null){
                    addActionError(getModule().getDisplayLabel()+" Not Found");
                    return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
                }
            }

            String message = getModule().getDisplayLabel() + BOLD_ITALIC_TAG_INIT + serverInstanceData.getResourceName() + ITALIC_BOLD_TAG_END + " Deleted";
            CRUDOperationUtil.audit(serverInstanceData, serverInstanceData.getResourceName(), AuditActions.DELETE, getStaffData(), null, serverInstanceData.getHierarchy(), message);
            CRUDOperationUtil.delete(serverInstanceData);

            makeSecondaryInstancePrimary(serverInstanceData);

            if(getLogger().isDebugLogLevel()){
                getLogger().debug(getLogModule(),"ServerInstance having Id '"+serverInstanceData.getId()+"' deleted Successfully");
            }

            addActionMessage("Server Instance removed successfully");

            CRUDOperationUtil.flushSession();

            reload();
            setActionChainUrl(getRedirectURL(METHOD_INDEX));
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);
        }catch (Exception e) {
            addActionError("Can not perform delete operation. Reason:" + e.getMessage());
            if(serverInstanceData!=null) {
                getLogger().error(getLogModule(), "Error while deleting " + getModule().getDisplayLabel() + DATA_FOR_ID + serverInstanceData.getId() + " .Reason: " + e.getMessage());
            }
            getLogger().trace(getLogModule(), e);
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code);
    }

    private void makeSecondaryInstancePrimary(ServerInstanceData serverInstanceData) {

        /* If there are two instances then only we will proceed further */
        /* If there current deleted instance is primary then only we will proceed further */
        if(serverInstanceData.getServerGroupServerInstanceRelData().getServerGroupData().getServerInstances().size()==2
                && serverInstanceData.getServerGroupServerInstanceRelData().getServerWeightage() == ServerInstanceWeightage.PRIMARY.val) {

                ServerInstanceData secondaryServerInstanceData = null;

                for (ServerInstanceData serverInstanceDataTemp : serverInstanceData.getServerGroupServerInstanceRelData().getServerGroupData().getServerInstances()) {
                    if (serverInstanceDataTemp.getServerGroupServerInstanceRelData().getServerWeightage() == ServerInstanceWeightage.SECONDARY.val) {
                        secondaryServerInstanceData = serverInstanceDataTemp;
                        secondaryServerInstanceData.getServerGroupServerInstanceRelData().setServerWeightage(ServerInstanceWeightage.PRIMARY.val);
                        CRUDOperationUtil.update(secondaryServerInstanceData);
                        if(getLogger().isDebugLogLevel()){
                            getLogger().debug(MODULE,"Secondary ServeInstance successfully converted to Primary");
                        }
                        break;
                    }
                }
        }
    }

    @Override
    public HttpHeaders show() { // View
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called show()");
        }
        String serverGroupType =getRequest().getParameter(Attributes.SERVER_GROUP_TYPE);
        if(Strings.isNullOrBlank(serverGroupType)==true){
            serverGroupType = "PCC";
        }
        getRequest().getSession().setAttribute(Attributes.SERVER_GROUP_TYPE, serverGroupType);
        getRequest().getSession().setAttribute(Attributes.SERVER_GROUP_TAB_TYPE, serverGroupType);

        try{
        	ServerInstanceData serverInstanceData = (ServerInstanceData) getModel();
            serverInstanceData = CRUDOperationUtil.get(ServerInstanceData.class,serverInstanceData.getId());
			if (Strings.isNullOrEmpty(serverGroupType) == false && ServerGroups.OFFLINE_RNC.getValue().equals(serverGroupType)) {
				setFileLocationList(CRUDOperationUtil.findAll(FileLocationData.class));
				setFileLocationJson(getFileLocationList(), serverInstanceData.getFileLocation());
			}
            
            setModel(serverInstanceData);
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);

        }catch (Exception e){
            addActionError("Error while viewing " + getModule().getDisplayLabel());
            getLogger().error(getLogModule(), "Error while viewing" + getModule().getDisplayLabel()+". Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        }

        addActionError(getModule().getDisplayLabel()+" Not Found");
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
    }

    @Override
    public ACLModules getModule() {
        return ACLModules.SERVER_INSTANCE;
    }

    @Override
    public ServerInstanceData createModel() {
        return new ServerInstanceData();
    }

    @SkipValidation
    public HttpHeaders reloadConfiguration(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called reloadConfiguration()");
        }

        try{
            ServerInstanceData serverInstanceData = (ServerInstanceData) getModel();
            if(getLogger().isDebugLogLevel()){
                getLogger().debug(getLogModule(),"Reloading  ServerInstance configuration having Id: '"+serverInstanceData.getId()+"'");
            }
            RemoteMethod remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_RELOAD_CONFIGURATION_BASE_PATH_URI, RemoteMethodConstant.NETVERTEX_RELOAD_CONFIGURATION, "", HTTPMethodType.GET);
            RMIGroup rmiGroup = RMIGroupManager.getInstance().getRMIGroupFromServerCode(serverInstanceData.getId());

            RMIResponse<String> reloadServerInstanceConfiguration = RemoteMessageCommunicator.callSync(remoteMethod , rmiGroup , serverInstanceData.getId());

            Collection<RMIResponse<String>> responses = new ArrayList<RMIResponse<String>>();

            responses.add(reloadServerInstanceConfiguration);
            if (SUCCESS.equalsIgnoreCase(reloadServerInstanceConfiguration.getResponse())) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(getLogModule(), "ServerInstance configuration Reloaded successfully");
                    getLogger().debug(getLogModule(), "Server Response: " + reloadServerInstanceConfiguration.getResponse());
                }
            } else {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(getLogModule(), "Reload failed with Server Response: " + reloadServerInstanceConfiguration.getError());
                }
            }

            getRequest().setAttribute("backUrl", "sm/serverinstance/server-instance/"+serverInstanceData.getId());
            getRequest().setAttribute(RELOAD_RESPONSES, responses);
            setActionChainUrl(getRedirectURL(RELOAD));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();

         }catch (Exception ex){
            getLogger().error(getLogModule(),"Failed to reload ServerInstance configuration. Reason: "+ex.getMessage());
            getLogger().trace(ex);
            addActionError("Failed to reload ServerInstance configuration");
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }

    public String getGroovyScripts(){
        Gson gson = GsonFactory.defaultInstance();
        ServerInstanceData serverInstanceData = (ServerInstanceData) getModel();
        return gson.toJsonTree(serverInstanceData.getGroovyScriptDatas(), new TypeToken<List<ServerInstanceData>>() {}.getType()).getAsJsonArray().toString();

    }

    @SkipValidation
    public HttpHeaders initManageOrder(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called initManageOrder()");
        }
        try{

            ServerInstanceData serverInstanceData = (ServerInstanceData) getModel();
            ServerInstanceData serverInstanceFromDB = CRUDOperationUtil.get(ServerInstanceData.class, serverInstanceData.getId());
            setGroovyScriptList(serverInstanceFromDB.getGroovyScriptDatas());
            groovyScriptList.sort(Comparator.comparing(ServerInstanceGroovyScriptData::getOrderNumber));
            setActionChainUrl(getRedirectURL("groovy-script-manageorder"));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Error while going to Manage Order view. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);
        }
    }

    @SkipValidation
    public HttpHeaders manageOrder(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called manageOrderForGroovy()");
        }
        try{

            ServerInstanceData serverInstanceData = (ServerInstanceData) getModel();
            serverInstanceData = CRUDOperationUtil.get(ServerInstanceData.class,serverInstanceData.getId());
            String[] groovyScriptIds = getRequest().getParameterValues("groovyScriptIds");
            int index = 1;
            for(String id: groovyScriptIds){
                ServerInstanceGroovyScriptData groovyScriptData = CRUDOperationUtil.get(ServerInstanceGroovyScriptData.class,id);
                JsonObject oldJsonObject = groovyScriptData.toJson();
                int oldOrderNumber = groovyScriptData.getOrderNumber();

                groovyScriptData.setOrderNumber(index);
                int newOrderNumber = groovyScriptData.getOrderNumber();
                JsonObject newJsonObject = groovyScriptData.toJson();

                CRUDOperationUtil.update(groovyScriptData);

                JsonArray difference = ObjectDiffer.diff(oldJsonObject, newJsonObject);
                String message = getModule().getDisplayLabel() + " <b><i>" + groovyScriptData.getScriptName() + "</i></b> " + "Updated";
                CRUDOperationUtil.audit(serverInstanceData, groovyScriptData.getScriptName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference, serverInstanceData.getHierarchy(), message);
                if( getLogger().isDebugLogLevel() ){
                    getLogger().debug(getLogModule(), "Server Instance's Groovy Script order changed to  '" + newOrderNumber + "' from old OrderNumber " + oldOrderNumber);
                }
                index++;
            }

            setActionChainUrl(getRedirectURL("../../../server-instance/"+serverInstanceData.getId()));
            addActionMessage("Groovy Script order changed successfully");
            return new DefaultHttpHeaders(com.elitecore.nvsmx.system.constants.Results.REDIRECT_ACTION.getValue());
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Failed to manage order of Groovy Script. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }
    
	private void setFileLocationJson(List<FileLocationData> fileLocationDatas, String locationIds) {
		StringBuilder name = new StringBuilder();
		List<String> locationIdList = CommonConstants.COMMA_SPLITTER.split(locationIds);
		for (String locationId : locationIdList) {

			for (FileLocationData fileLocationData : fileLocationDatas) {
				if (fileLocationData.getId().equals(locationId) && 
						Strings.isNullOrBlank(fileLocationData.getName()) == false) {
					name.append(fileLocationData.getName());
					name.append(CommonConstants.COMMA);
				}
			}
		}

		if (Strings.isNullOrBlank(name.toString()) == false) {
			name.deleteCharAt(name.lastIndexOf(","));
			setFileLocationListJson(name.toString());
		}
	}
	
	
	@Override
	public void validate() {
		ServerInstanceData serverInstanceData = (ServerInstanceData) getModel();
		String selectedType = (String) getRequest().getSession().getAttribute(Attributes.SERVER_GROUP_TYPE);
        if(Strings.isNullOrBlank(serverInstanceData.getServerGroupId())){
            setActionChainUrl(getRedirectURL(METHOD_INDEX));
            addActionError("ServerGroupId Required");
            return;
        }

        ServerGroupData serverGroupData = CRUDOperationUtil.get(ServerGroupData.class,serverInstanceData.getServerGroupId());
        if(Strings.isNullOrBlank(selectedType)){

            selectedType = serverGroupData!= null ? serverGroupData.getServerGroupType() : null;
        }
		if (ServerGroups.OFFLINE_RNC.getValue().equals(selectedType) && serverInstanceData.getOfflineRncService() != null) {
			Boolean isValidOfflineService = String.valueOf(serverInstanceData.getOfflineRncService())
					.matches(RegexConstants.BOOLEANPATTERN);
			if (isValidOfflineService == false) {
				addFieldError("offlineRncService", getText("invalid.offlineRnC"));
			}
			
			if(Strings.isNullOrBlank(serverInstanceData.getFileLocation())){
				addFieldError("fileLocation", getText("required.offlinernc.file.location"));
			}
		}

		if (ServerGroups.PCC.getValue().equals(selectedType)) {
			if ((serverInstanceData.getDiameterEnabled() == true || serverInstanceData.getDiameterEnabled() == false)) {
				Boolean isValidDiameter = String.valueOf(serverInstanceData.getDiameterEnabled()).matches(RegexConstants.BOOLEANPATTERN);

				if(isValidDiameter == true && serverInstanceData.getDiameterEnabled() == true){
					if ((Strings.isNullOrBlank(serverInstanceData.getDiameterOriginHost()) == false) || (isValidDiameter == false)) {
						if (serverInstanceData.getDiameterOriginHost().matches(RegexConstants.HOSTPATTERN) == false) {
							addFieldError("diameterOriginHost",  getText("invalid.diameter.origin.host"));
						}

					} else {
						addFieldError("diameterOriginHost", getText("required.diameter.origin.host"));
					}

					if ((Strings.isNullOrBlank(serverInstanceData.getDiameterOriginRealm()) == false) || (isValidDiameter == false)) {
						if (serverInstanceData.getDiameterOriginRealm().matches(RegexConstants.HOSTPATTERN) == false) {
							addFieldError("diameterOriginRealm", getText("invalid.diameter.origin.realm"));
						}

					} else {
						addFieldError("diameterOriginRealm", getText("required.diameter.origin.host"));
					}

					if ((Strings.isNullOrBlank(serverInstanceData.getDiameterUrl()) == false) || (isValidDiameter == false)) {

						if (serverInstanceData.getDiameterUrl().matches(RegexConstants.URLPATTERN) == false) {
							addFieldError("diameterUrl", getText("invalid.diameter.url"));
						}
					} else {
						addFieldError("diameterUrl", getText("required.diameter.url"));
					}
				}
			}

			if ((serverInstanceData.getRadiusEnabled() != null)
					&& (serverInstanceData.getRadiusEnabled() || !serverInstanceData.getRadiusEnabled())) {

				Boolean isValidRadius = String.valueOf(serverInstanceData.getRadiusEnabled())
						.matches(RegexConstants.BOOLEANPATTERN);

				if(isValidRadius == true && serverInstanceData.getRadiusEnabled() == true){
					if ((Strings.isNullOrBlank(serverInstanceData.getRadiusUrl()) == false) || (isValidRadius == false)) {

						if (serverInstanceData.getRadiusUrl().matches(RegexConstants.URLPATTERN) == false) {
							addFieldError("radiusUrl", getText("invalid.radius.url"));
						}
					} else {
						addFieldError("radiusUrl", getText("required.radius.url"));
					}
				}
			}
            String emailAgentId = serverInstanceData.getEmailAgentId();
            if(Strings.isNullOrBlank(emailAgentId) == false){
			    EmailAgentData emailAgentData = CRUDOperationUtil.get(EmailAgentData.class, emailAgentId);
			    if(emailAgentData == null){
                    addFieldError("emailAgentId","Email Agent Id does not exist");
                }else{
			        serverInstanceData.setEmailAgentData(emailAgentData);
                }
            }
            String smsAgentId = serverInstanceData.getSmsAgentId();
            if(Strings.isNullOrBlank(smsAgentId) == false){
                SMSAgentData smsAgentData = CRUDOperationUtil.get(SMSAgentData.class, smsAgentId);
                if(smsAgentData == null){
                    addFieldError("smsAgentId","SMS Agent Id does not exist");
                }else{
                    serverInstanceData.setSmsAgentData(smsAgentData);
                }
            }
            //validate groovy script name
            if(Collectionz.isNullOrEmpty(serverInstanceData.getGroovyScriptDatas()) == false){
                serverInstanceData.getGroovyScriptDatas().forEach(groovyScriptData -> {
                    if(groovyScriptData == null){
                        addActionError("Groovy Script Data must be configured");
                    }else if(Strings.isNullOrBlank(groovyScriptData.getScriptName())){
                        addActionError("Groovy Script Name must be configured");
                    }
                });
            }
		}
		super.validate();
	}
	
	public String getFileLocationListJson() {
		return fileLocationListJson;
	}

	public void setFileLocationListJson(String fileLocationListJson) {
		this.fileLocationListJson = fileLocationListJson;
	}
    
	public List<FileLocationData> getFileLocationList() {
		return fileLocationList;
	}

	public void setFileLocationList(List<FileLocationData> fileLocationList) {
		this.fileLocationList = fileLocationList;
	}

    private void setGroovyScriptDatas() {
        ServerInstanceData serverInstanceData = (ServerInstanceData) getModel();
        filterGroovyScripts(serverInstanceData);
        int index = 1;
        List<ServerInstanceGroovyScriptData> groovyScriptDatas = serverInstanceData.getGroovyScriptDatas();
        if(Collectionz.isNullOrEmpty(groovyScriptDatas) ==false){
            for (ServerInstanceGroovyScriptData groovyScript : groovyScriptDatas) {
                groovyScript.setServerInstanceData(serverInstanceData);
                groovyScript.setOrderNumber(index);
                index++;
            }
        }

    }

    private void filterGroovyScripts(ServerInstanceData serverInstanceData) {
        List<ServerInstanceGroovyScriptData> groovyScriptDatas = serverInstanceData.getGroovyScriptDatas();
        if(Collectionz.isNullOrEmpty(groovyScriptDatas) == false) {
            Collectionz.filter(groovyScriptDatas, GROOVY_PREDICATE);
        }
    }

    public List<SMSAgentData> getSmsAgentDataList() {
        return smsAgentDataList;
    }

    public void setSmsAgentDataList(List<SMSAgentData> smsAgentDataList) {
        this.smsAgentDataList = smsAgentDataList;
    }

    public List<EmailAgentData> getEmailAgentDataList() {
        return emailAgentDataList;
    }

    public void setEmailAgentDataList(List<EmailAgentData> emailAgentDataList) {
        this.emailAgentDataList = emailAgentDataList;
    }

    public List<ServerInstanceGroovyScriptData> getGroovyScriptList() {
        return groovyScriptList;
    }

    public void setGroovyScriptList(List<ServerInstanceGroovyScriptData> groovyScriptList) {
        this.groovyScriptList = groovyScriptList;
    }

    public void reload() {

        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Reload server group cache started");
        }

        try {
           EndPointManager.getInstance().reload();
            RMIGroupManager.getInstance().reload();
        }catch (Exception ex){
            getLogger().error(getLogModule(),"Error while reloading the server group cache. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
        }

        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Server group caching completed successfully");
        }

    }
}

