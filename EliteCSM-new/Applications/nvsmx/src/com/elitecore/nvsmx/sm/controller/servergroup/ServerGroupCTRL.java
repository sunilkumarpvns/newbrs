package com.elitecore.nvsmx.sm.controller.servergroup;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.ServerGroups;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupServerInstanceRelData;
import com.elitecore.corenetvertex.sm.serverinstance.ConfigurationDatabase;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.remotecommunications.BroadCastCompletionResult;
import com.elitecore.nvsmx.remotecommunications.EndPoint;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.ErrorRMIResponse;
import com.elitecore.nvsmx.remotecommunications.RMIGroup;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.remotecommunications.RMIResponse;
import com.elitecore.nvsmx.remotecommunications.RemoteMessageCommunicator;
import com.elitecore.nvsmx.remotecommunications.RemoteMethod;
import com.elitecore.nvsmx.remotecommunications.RemoteMethodConstant;
import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;
import com.elitecore.nvsmx.remotecommunications.ws.WebServiceMethods;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.sm.controller.serverinstance.ServerInstanceWeightage;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.db.NVSMXDBConnectionManager;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.passwordutil.PasswordEncryption;
import com.google.gson.Gson;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by kirpalsinh on 2/8/17.
 */
@ParentPackage(value = NVSMXCommonConstants.REST_PARENT_PKG_SM)
@Namespace("/sm/servergroup")
@org.apache.struts2.convention.annotation.Results({
        @Result(name= SUCCESS, type=RestGenericCTRL.REDIRECT_ACTION,params = {NVSMXCommonConstants.ACTION_NAME,"server-group"}),
})

public class ServerGroupCTRL extends RestGenericCTRL<ServerGroupData> {

    private static final String SERVER_INSTANCE = "' ServerInstance '";
    private static final String SM_SERVERGROUP_SERVER_GROUP_IDEX_URL = "sm/servergroup/server-group/";
    private static final String BACK_URL = "backUrl";
    private List<ServerGroupData> list;
    
    private List<DatabaseData> databaseDatas;
    private static final String BOLD_ITALIC_TAG_INIT = " <b><i>";
    private static final String ITALIC_BOLD_TAG_END = "</i></b> ";
    private transient  RemoteMethod remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_RELOAD_CONFIGURATION_BASE_PATH_URI, RemoteMethodConstant.NETVERTEX_RELOAD_CONFIGURATION, "", HTTPMethodType.GET);
    private static final String RELOAD_RESPONSES = "reloadResponses";
    private static final String RELOAD = "reload";
    private String tabType;

    @SkipValidation
    @Override
    public HttpHeaders index() { // list or search
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Calling index method");
        }

        try{
            list = CRUDOperationUtil.findAll(ServerGroupData.class);
            setTabType((String)getRequest().getSession().getAttribute(Attributes.SERVER_GROUP_TAB_TYPE));
            boolean serverInstancesAvailable = false;
              
            Map<String,Boolean> serverInstDbSyncStatusMap =  new HashMap<String, Boolean>();

            for(ServerGroupData serverGroupData : list){
                List<String> ids =  new ArrayList<String>(2);
                if(!Collectionz.isNullOrEmpty(serverGroupData.getServerInstances())) {

                    serverGroupData.getServerInstances().sort( (ServerInstanceData data1, ServerInstanceData data2)
                            -> data1.getServerGroupServerInstanceRelData().getServerWeightage()
                            - data2.getServerGroupServerInstanceRelData().getServerWeightage());


                    for (ServerInstanceData serverInstanceData : serverGroupData.getServerInstances()) {
                        serverInstancesAvailable = true;
                        ids.add(serverInstanceData.getId());

                        boolean verificationFlag = verifyServerInstanceDatabaseWithContextDatabase(serverInstanceData);
                        if(verificationFlag==false) {
                            serverInstDbSyncStatusMap.put(serverInstanceData.getId(), verificationFlag);
                        }

                    }
                }
                serverGroupData.setServerInstanceIds(ids);
            }

            list.sort((ServerGroupData data1, ServerGroupData data2) -> data1.getName().compareTo(data2.getName()));

            databaseDatas = CRUDOperationUtil.findAll(DatabaseData.class);
            getRequest().setAttribute("isServerInstanceAvailable",String.valueOf(serverInstancesAvailable));
            getRequest().setAttribute("serverInstanceDbSyncStatusMap",serverInstDbSyncStatusMap);
            setActionChainUrl(getRedirectURL(METHOD_INDEX));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while fetching "+getModule().getDisplayLabel()+" information.Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Failed to perform Search Operation");
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);
        }
    }

    @Override
    public HttpHeaders show() { // View
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called show()");
        }

        try{
            ServerGroupData serverGroupData = (ServerGroupData) getModel();
            String id = serverGroupData.getId();
            serverGroupData = CRUDOperationUtil.get(ServerGroupData.class,serverGroupData.getId());

            if(serverGroupData==null){
                addActionError("No Server Group found for Id :" + id );
                return new DefaultHttpHeaders(METHOD_INDEX).withStatus(ResultCode.NOT_FOUND.code);
            }

            List<String> ids =  new ArrayList<String>(2);
            for (ServerInstanceData serverInstanceData : serverGroupData.getServerInstances()) {
                ids.add(serverInstanceData.getId());
            }
            serverGroupData.setServerInstanceIds(ids);

            setModel(serverGroupData);
            setActionChainUrl(getRedirectURL(METHOD_SHOW));

            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);

        }catch (Exception e){
            addActionError("Error while viewing " + getModule().getDisplayLabel() + " data");
            getLogger().error(getLogModule(), "Error while viewing Server Group Data. Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        }
        return new DefaultHttpHeaders(METHOD_INDEX).withStatus(ResultCode.NOT_FOUND.code);
    }

    @Override
    public Object getModel() {
        return list == null ? super.getModel() : list;
    }
    
    private boolean verifyServerInstanceDatabaseWithContextDatabase(ServerInstanceData serverInstanceData){
        try {

            ConfigurationDatabase configurationDatabase = wsGetDatabaseDataSource(serverInstanceData);
            if (configurationDatabase != null) {

                String connectionUrl = configurationDatabase.getUrl();
                if(Strings.isNullOrEmpty(connectionUrl)){
                    if(getLogger().isDebugLogLevel()) {
                        getLogger().debug(getLogModule(), "ServerInstance Database datasource has Null/Empty connectionUrl");
                    }
                    return false;
                }

                String username = configurationDatabase.getUsername();
                if(Strings.isNullOrEmpty(username)){
                    if(getLogger().isDebugLogLevel()) {
                        getLogger().debug(getLogModule(), "ServerInstance Database datasource has Null/Empty username");
                    }
                    return false;
                }

                if(Strings.isNullOrEmpty(configurationDatabase.getPassword())){
                    if(getLogger().isDebugLogLevel()) {
                        getLogger().debug(getLogModule(), "ServerInstance Database datasource has Null/Empty password");
                    }
                    return false;
                }

                NVSMXDBConnectionManager nvsmxdbConnectionManager = NVSMXDBConnectionManager.getInstance();
                if(getLogger().isDebugLogLevel()){
                    getLogger().debug(getLogModule(),"SM DataSource Detail: "+nvsmxdbConnectionManager.getDataSource());
                }

                String smDbUrl = nvsmxdbConnectionManager.getDataSource().getConnectionURL();
                if(configurationDatabase.getUrl().equals(smDbUrl)==false){
                    if(getLogger().isDebugLogLevel()) {
                        getLogger().debug(getLogModule(), "Received DB connection URL did not match with SM DB connection URL for ServerInstance'" + serverInstanceData.getName() + "'");
                    }
                    return false;
                }

                String smDbUsername = nvsmxdbConnectionManager.getDataSource().getUsername();
                if(configurationDatabase.getUsername().equals(smDbUsername)==false){
                    if(getLogger().isDebugLogLevel()) {
                        getLogger().debug(getLogModule(), "Received DB username did not match with SM DB username for ServerInstance'" + serverInstanceData.getName() + "'");
                    }
                    return false;
                }

                String smDbPasswordd = nvsmxdbConnectionManager.getDataSource().getPassword();
                String serverDbPassword = PasswordEncryption.getInstance().decrypt(configurationDatabase.getPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
                if(serverDbPassword.equals(smDbPasswordd)==false){
                    if(getLogger().isDebugLogLevel()) {
                        getLogger().debug(getLogModule(), "Received DB password did not match with SM DB password for ServerInstance'" + serverInstanceData.getName() + "'");
                    }
                    return false;
                }

                return true;
            }
            if(getLogger().isDebugLogLevel()) {
                getLogger().debug(getLogModule(), "Received Null database detail for ServerInstance '" + serverInstanceData.getName() + "'");
            }
        }catch(Exception ex){
            getLogger().debug(getLogModule(),"Error while verify ServerInstance and ServerManager Database credentials. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(), ex);
        }
        return false;
    }


    private ConfigurationDatabase wsGetDatabaseDataSource(ServerInstanceData serverInstanceData) {

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called wsGetDatabaseDataSource()");
        }
        try {

            EndPoint netvertexEndPoint = EndPointManager.getInstance().getByServerCode(serverInstanceData.getId());
            if(netvertexEndPoint!=null && netvertexEndPoint.isAlive()) {
                RemoteMethod remoteMethodLocal = new RemoteMethod(RemoteMethodConstant.NETVERTEX_RELOAD_CONFIGURATION_BASE_PATH_URI, WebServiceMethods.NETVERTEX_GET_DATABASE_DATASOURCE.name(), "", HTTPMethodType.GET);
                Future<RMIResponse<String>> rmiResponseFuture = netvertexEndPoint.submit(remoteMethodLocal);
                RMIResponse<String> rmiResponse = rmiResponseFuture.get(3, TimeUnit.SECONDS);
                String response = rmiResponse.getResponse();
                getLogger().debug(getLogModule(), "Received Response: " + response);
                if (response != null) {
                    return  new Gson().fromJson(response, ConfigurationDatabase.class);
                }
            }else{
                if(getLogger().isDebugLogLevel()){
                    getLogger().debug(getLogModule(),"Server Instance having Id: '"+serverInstanceData.getId()+"' is not Alive. Therefore returning Null response.");
                }
            }

        }catch(Exception ex){
            getLogger().error(getLogModule(),"Error while making REST call to get DataBase datasource configuration for ServerInstance '"+serverInstanceData.getName()+"'. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(), ex);
        }
        return null;
    }

    public void cacheServerGroups() {
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Caching server group started");
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

    @Override
    public List<ServerGroupData> getList() {
        return list;
    }

    public List<DatabaseData> getDatabaseDatas() {
        return databaseDatas;
    }
    @Override
    public HttpHeaders create() { // create
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called create()");
        }

        try {

            String result = authorize();
            if(result.equals(SUCCESS) == false){
                setActionChainUrl(getRedirectURL(METHOD_EDITNEW));
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SERVICE_UNAVAILABLE.code);
            }

            ServerGroupData serverGroupData = (ServerGroupData)getModel();
            if (!isValidDatasource(serverGroupData)) {
                return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
            }

            serverGroupData.setCreatedDateAndStaff(getStaffData());

            Integer maxOrderNo =CRUDOperationUtil.getMaxValueForProperty(ServerGroupData.class,"orderNo");
            if(maxOrderNo==null){
                serverGroupData.setOrderNo(1);
            }else{
                serverGroupData.setOrderNo(maxOrderNo+1);
            }
            
            if(Strings.isNullOrEmpty(serverGroupData.getGroups())){
            	serverGroupData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
            }
            
            if(ServerGroups.OFFLINE_RNC.getValue().equals(serverGroupData.getServerGroupType())){
                getRequest().getSession().setAttribute(Attributes.SERVER_GROUP_TAB_TYPE, ServerGroups.OFFLINE_RNC.getValue());
            }
            else{
            	 getRequest().getSession().setAttribute(Attributes.SERVER_GROUP_TAB_TYPE, ServerGroups.PCC.getValue());
            }

            if(Strings.isNullOrBlank(serverGroupData.getGroups())){
                serverGroupData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
            }

            if(Strings.isNullOrBlank(serverGroupData.getNotificationDataSourceId())){
                serverGroupData.setNotificationDataSourceId(null);
            } else {
                DatabaseData notificationDatasource = getNotificationDatasource(serverGroupData);
                if (notificationDatasource == null) {
                    return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
                }
                serverGroupData.setNotificationDataSourceData(notificationDatasource);
            }

            CRUDOperationUtil.save(serverGroupData);
            String message = getModule().getDisplayLabel()  + BOLD_ITALIC_TAG_INIT + serverGroupData.getResourceName() + ITALIC_BOLD_TAG_END + "Created";

            CRUDOperationUtil.audit(serverGroupData, serverGroupData.getResourceName(), AuditActions.CREATE, getStaffData(), getRequest().getRemoteAddr(), serverGroupData.getHierarchy(), message);
            addActionMessage("Server Group created successfully");
            return new DefaultHttpHeaders(SUCCESS).setLocationId(serverGroupData.getId());

        }catch(Exception ex){
            LogManager.getLogger().error(getLogModule(), "Error while creating Server Group");
            LogManager.getLogger().trace(getLogModule(), ex);

            addActionError("Failed to create Server Group");
            return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }

    @Override
    public boolean prepareAndValidateDestroy(ServerGroupData serverGroupData) {

        try {
            List<ServerInstanceData> serverInstances = serverGroupData.getServerInstances();
            if (Collectionz.isNullOrEmpty(serverInstances) == false) {
                addActionError("Unable to delete '" + serverGroupData.getName() + "'. <br/>Reason :  " + ACLModules.SERVER_INSTANCE.getDisplayLabel() + " is associated with  " + ACLModules.SERVER_GROUP.getDisplayLabel());
                LogManager.getLogger().error(getLogModule(), "Unable to delete " + serverGroupData.getName() + " Server Instance is associated with server group");
                return false;
            }

        } catch (Exception e) {
            addActionError("Can not perform delete operation. Reason:" + e.getMessage());
            getLogger().error(getLogModule(), "Error while " + getModule().getDisplayLabel() + " for id " + serverGroupData.getId() + " . Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            return false;
        }
        return true;
    }

    @SkipValidation
    public HttpHeaders swapServerInstancesRole(){

        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called swapServerInstancesRole()");
        }

        try{
            ServerGroupData serverGroupData = (ServerGroupData) getModel();
            swapServerInstance(serverGroupData.getId());
            addActionMessage("Server Instances Role Swapped successfully");
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(getLogModule(),"ServerInstance(s) role swapped Successfully");
            }
            return new DefaultHttpHeaders(SUCCESS).setLocationId(serverGroupData.getId());

        }catch(Exception ex){
            getLogger().error(getLogModule(),"Error while swapping Server Instances Role. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            addActionError("Failed to Swap Server Instances Role");
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }

    private void swapServerInstance(String serverGroupId) throws HibernateDataException {

        ServerGroupData serverGroupData = CRUDOperationUtil.get(ServerGroupData.class, serverGroupId);
        if (Collectionz.isNullOrEmpty(serverGroupData.getServerInstances()) == false && serverGroupData.getServerInstances().size() == 2) {

            for (ServerInstanceData data : serverGroupData.getServerInstances()) {
                ServerGroupServerInstanceRelData relationData = data.getServerGroupServerInstanceRelData();

                if (relationData.getServerWeightage() == ServerInstanceWeightage.PRIMARY.val) {
                    relationData.setServerWeightage(ServerInstanceWeightage.SECONDARY.val);
                } else {
                    relationData.setServerWeightage(ServerInstanceWeightage.PRIMARY.val);
                }

                CRUDOperationUtil.update(relationData);
            }
        }
    }
    @Override
    public HttpHeaders update() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called update()");
        }

        try{

            String result = authorize();
            if (result.equals(SUCCESS) == false) {
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            ServerGroupData serverGroupData = (ServerGroupData)getModel();
            if (isEntityExists(serverGroupData.getId()) == false) {
                getLogger().error(getLogModule(),"Error while updating "+getModule().getDisplayLabel()+" with id: "+ serverGroupData.getId()+". Reason: Not found");
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }

            if (!isValidDatasource(serverGroupData)) {
                return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
            }

            ServerGroupData serverGroupDataToSave = CRUDOperationUtil.get(ServerGroupData.class,serverGroupData.getId());
            serverGroupDataToSave.setName(serverGroupData.getName());
            serverGroupDataToSave.setSessionDataSourceId(serverGroupData.getSessionDataSourceId());
            serverGroupDataToSave.setDatabaseData(serverGroupData.getDatabaseData());
            if(Strings.isNullOrBlank(serverGroupData.getNotificationDataSourceId())){
               serverGroupDataToSave.setNotificationDataSourceId(null);
               serverGroupDataToSave.setNotificationDataSourceData(null);
            }else{
                DatabaseData notificationDatasource = getNotificationDatasource(serverGroupData);
                if (notificationDatasource == null) {
                    return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
                }
                serverGroupDataToSave.setNotificationDataSourceId(serverGroupData.getNotificationDataSourceId());
                serverGroupDataToSave.setNotificationDataSourceData(notificationDatasource);
            }
            serverGroupDataToSave.setGroups(serverGroupData.getGroups());
            serverGroupDataToSave.setGroupNames(serverGroupData.getGroupNames());
            
            if(ServerGroups.OFFLINE_RNC.getValue().equals(serverGroupData.getServerGroupType())){
                getRequest().getSession().setAttribute(Attributes.SERVER_GROUP_TAB_TYPE, ServerGroups.OFFLINE_RNC.getValue());
            }
            else{
            	 getRequest().getSession().setAttribute(Attributes.SERVER_GROUP_TAB_TYPE, ServerGroups.PCC.getValue());
            }
            serverGroupDataToSave.setModifiedDateAndStaff(getStaffData());

            CRUDOperationUtil.update(serverGroupDataToSave);
            String message = getModule().getDisplayLabel() + BOLD_ITALIC_TAG_INIT + serverGroupData.getResourceName() + ITALIC_BOLD_TAG_END + "Updated";
            CRUDOperationUtil.audit(serverGroupDataToSave,serverGroupDataToSave.getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(),serverGroupDataToSave.getHierarchy(), message);

            addActionMessage("Server Group Updated successfully");
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(getLogModule(),"Server Group Updated successfully");
            }

            CRUDOperationUtil.flushSession();

            cacheServerGroups();
            setActionChainUrl(getRedirectURL(METHOD_INDEX));
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);

        }catch (Exception ex){
            getLogger().error(getLogModule(), "Error while updating Server Group");
            getLogger().trace(ex);

            addActionError("Failed to update Server Group");
            return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }

    private boolean isValidDatasource(ServerGroupData serverGroupData) {
        if(!isValidDatasourceId(serverGroupData)){
            addActionError("No SessionDatasource found for given ID");
            return false;
        }
        return true;
    }

    private boolean isValidDatasourceId(ServerGroupData serverGroupData) {
        if(Collectionz.isNullOrEmpty(databaseDatas)){
            databaseDatas = CRUDOperationUtil.findAll(DatabaseData.class);
        }
        boolean isValidDatasourceId = false;
        for(DatabaseData databaseData : databaseDatas){
            if(databaseData.getId().equals(serverGroupData.getSessionDataSourceId())){
                serverGroupData.setDatabaseData(databaseData);
                isValidDatasourceId = true;
            }
        }
        return isValidDatasourceId;
    }

    private DatabaseData getNotificationDatasource(ServerGroupData serverGroupData) {
        DatabaseData notificationDataSource = CRUDOperationUtil.get(DatabaseData.class, serverGroupData.getNotificationDataSourceId());
        if(notificationDataSource == null){
            addActionError("No NotificationDatasource found for given ID");
            return null;
        }
        return notificationDataSource;
    }

    @Override
    public ACLModules getModule() {
        return ACLModules.SERVER_GROUP;
    }

    @Override
    public ServerGroupData createModel() {
        return new ServerGroupData();
    }

    @SkipValidation
    public HttpHeaders globalReloadServerGroupsConfiguration(){
        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called globalReloadServerGroupsConfiguration()");
        }
        try {

            BroadCastCompletionResult<String>  reloadServerInstanceConfiguration = RemoteMessageCommunicator.broadcast(EndPointManager.getInstance().getAllNetvertexEndPoint(), remoteMethod);
            Collection<RMIResponse<String>> allServerInstanceResponse = reloadServerInstanceConfiguration.getAll(3, TimeUnit.SECONDS);

            if(!Collectionz.isNullOrEmpty(allServerInstanceResponse)) {
                for (RMIResponse<String> response : allServerInstanceResponse) {
                    getLogger().debug(getLogModule(), "GroupName: " + response.getInstanceGroupData().getName() + ", InstanceName: " + response.getInstanceData().getName() + ", Result: " + response.getResponse());
                }

                ArrayList<RMIResponse<String>> responses = new ArrayList<RMIResponse<String>>();
                responses.addAll(allServerInstanceResponse);

                Comparator<RMIResponse> comparator = Comparator.comparing(o -> o.getInstanceGroupData().getName());
                Collections.sort(responses,comparator);

                getRequest().setAttribute(BACK_URL, SM_SERVERGROUP_SERVER_GROUP_IDEX_URL);
                getRequest().setAttribute(RELOAD_RESPONSES, responses);
                setActionChainUrl(getRedirectURL(RELOAD));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
            }

            if(getLogger().isDebugLogLevel()){
                getLogger().debug(getLogModule(),"Global reload Failed. No response received from REST call.");
            }

            addActionError("Global reload Failed. No response received.");
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Global reload Failed. Reason: "+ex.getMessage());
            getLogger().trace(ex);
            addActionError("Global reload Failed");
        }
        return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
    }

    @SkipValidation
    public HttpHeaders reloadServerGroupConfiguration(){
        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called reloadServerGroupConfiguration()");
        }
        try {

            ServerGroupData serverGroupData = (ServerGroupData) getModel();
            serverGroupData = CRUDOperationUtil.get(ServerGroupData.class,serverGroupData.getId());

            ArrayList<RMIResponse<String>> responses = reloadServerInstanceConfiguration(serverGroupData);

            Comparator<RMIResponse> comparator = Comparator.comparing(o -> o.getInstanceData().getName());

            Collections.sort(responses, comparator);

            getRequest().setAttribute(BACK_URL, SM_SERVERGROUP_SERVER_GROUP_IDEX_URL);
            getRequest().setAttribute(RELOAD_RESPONSES, responses);
            setActionChainUrl(getRedirectURL(RELOAD));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();

        }catch(Exception ex){
            getLogger().debug(getLogModule(),"Failed to reload ServerGroup Configuration. Reason: "+ex.getMessage());
            getLogger().trace(ex);
            addActionError("Failed to reload ServerGroup Configuration");
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }

    private ArrayList<RMIResponse<String>> reloadServerInstanceConfiguration(ServerGroupData serverGroupData) {
        getLogger().debug(getLogModule(), "Performing reload for ServerGroup having Id: " + serverGroupData.getId());

        ArrayList<RMIResponse<String>> responses = new ArrayList<RMIResponse<String>>();
        for (ServerInstanceData serverInstanceData : serverGroupData.getServerInstances()) {
            RMIResponse<String> rmiResponse = null;
            try {

                String instanceId = serverInstanceData.getId();
                RMIGroup rmiGroup = RMIGroupManager.getInstance().getRMIGroupFromServerCode(instanceId);
                rmiResponse = RemoteMessageCommunicator.callSync(remoteMethod,rmiGroup, instanceId);

                logRmiResponse(serverGroupData, serverInstanceData, rmiResponse);
                responses.add(rmiResponse);
            }catch (Exception ex){

                ServerInformation tempServerGroup = new ServerInformation(serverGroupData.getName(),serverGroupData.getId(), serverGroupData.getId());
                ServerInformation tempServerInstance = new ServerInformation(serverInstanceData.getName(), serverInstanceData.getId(), serverInstanceData.getId());
                rmiResponse = new ErrorRMIResponse<>(ex, tempServerGroup, tempServerInstance);

                responses.add(rmiResponse);
                getLogger().debug(getLogModule(),"Failed to reload ServerGroup '"+serverGroupData.getName()+ SERVER_INSTANCE +serverInstanceData.getName()+"' Configuration. Reason: "+ex.getMessage());
                getLogger().trace(ex);
            }
        }
        return responses;
    }

    private void logRmiResponse(ServerGroupData serverGroupData, ServerInstanceData serverInstanceData, RMIResponse<String> rmiResponse) {
        if (rmiResponse != null) {
            if(SUCCESS.equalsIgnoreCase(rmiResponse.getResponse())){
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(getLogModule(), "Reload successful for ServerGroup '"+serverGroupData.getName()+ SERVER_INSTANCE +serverInstanceData.getName()+"'");
                }
            }else{
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(getLogModule(), "Reload failed for ServerGroup '"+serverGroupData.getName()+ SERVER_INSTANCE +serverInstanceData.getName()+"'");
                }
            }
        }else {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(getLogModule(), "Received NULL response for ServerInstance '"+serverInstanceData.getName()+"'");
            }
        }
    }

	public String getTabType() {
		return tabType;
	}

	public void setTabType(String tabType) {
		this.tabType = tabType;
	}


}