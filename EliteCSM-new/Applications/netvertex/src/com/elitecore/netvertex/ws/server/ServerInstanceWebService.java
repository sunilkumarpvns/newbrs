package com.elitecore.netvertex.ws.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.corenetvertex.DataSourceInfo;
import com.elitecore.corenetvertex.GatewayStatusInfo;
import com.elitecore.corenetvertex.GlobalListenersInfo;
import com.elitecore.corenetvertex.ServerInfo;
import com.elitecore.corenetvertex.ServiceInfo;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.sm.serverinstance.ConfigurationDatabase;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.netvertex.EliteNetVertexServer;
import com.elitecore.netvertex.core.NetVertexDBConnectionManager;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.restapi.DataSourceInfoProvider;
import com.elitecore.netvertex.restapi.DiameterGatewayStatusInfoProviderImpl;
import com.elitecore.netvertex.restapi.RadiusGatewayStatusInfoProviderImpl;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;
import org.apache.commons.lang3.StringUtils;


import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.CommonConstants.UTF_8;

@Path(CommonConstants.SERVER_INSTANCE_WS_CONTEXT_PATH)
public class ServerInstanceWebService {

    private static final String SERVER_INFO = "server-info";
    private static final String RELOAD_CONFIGURATION = "reload-configuration";
    private static final String MODULE = "SERVER-INSTANCE-WEB-SERVICE";
    private static final String SYSTEM_PATH = "system";
    private static final String SYS_INFO_FILE = "_sys.info";
    private static final String FAIL_REASON = "FAIL. Reason: ";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";
    private final ServerInfo serverInfo;
    private EliteNetVertexServer.ServerStatusProvider serverStatusProvider;
    private EliteNetVertexServer.InstanceWebServiceContext instanceWebServiceContext;
    private DataSourceInfoProvider dataSourceInfoProvider;
    private RadiusGatewayStatusInfoProviderImpl radiusGatewayStatusInfoProvider;
    private DiameterGatewayStatusInfoProviderImpl diameterGatewayStatusInfoProvider;
    private PolicyManager policyManager;

    public ServerInstanceWebService(ServerInfo serverInfo, EliteNetVertexServer.ServerStatusProvider serverStatusProvider, DataSourceInfoProvider dataSourceInfoProvider, EliteNetVertexServer.InstanceWebServiceContext instanceWebServiceContext) {
        this.serverInfo = serverInfo;
        this.serverStatusProvider = serverStatusProvider;
        this.dataSourceInfoProvider = dataSourceInfoProvider;
        this.instanceWebServiceContext = instanceWebServiceContext;
    }

    public void setGatewayStatusInfoProvider(RadiusGatewayStatusInfoProviderImpl radiusGatewayStatusInfoProvider, DiameterGatewayStatusInfoProviderImpl diameterGatewayStatusInfoProvider){
        this.radiusGatewayStatusInfoProvider = radiusGatewayStatusInfoProvider;
        this.diameterGatewayStatusInfoProvider = diameterGatewayStatusInfoProvider;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    @GET
    @Path("/getDatabaseDataSource")
    @Produces(MediaType.TEXT_PLAIN)
    public String getDatabaseDatasource(){

        try{
            com.elitecore.corenetvertex.core.db.DBDataSource dbDataSource = (com.elitecore.corenetvertex.core.db.DBDataSource) NetVertexDBConnectionManager.getInstance().getDataSource();

            if (dbDataSource == null) {
                if (getLogger().isErrorLogLevel()) {
                    getLogger().error(MODULE, "Unable to get data source information.");
                }
                return URLEncoder.encode(ResultCode.NOT_FOUND.name(), UTF_8);
            }

            ConfigurationDatabase configurationDatabase = new ConfigurationDatabase();
            configurationDatabase.setUsername(dbDataSource.getUsername());
            configurationDatabase.setPassword(dbDataSource.getPassword());
            configurationDatabase.setUrl(dbDataSource.getConnectionURL());
            configurationDatabase.setDriverClassName(DBVendors.fromUrl(dbDataSource.getConnectionURL()).driverClassName);
            configurationDatabase.setMaxTotal(dbDataSource.getMinimumPoolSize());
            configurationDatabase.setMaxIdle(dbDataSource.getMinimumPoolSize());
            configurationDatabase.setValidationQueryTimeout(dbDataSource.getTimeout());

            return GsonFactory.defaultInstance().toJson(configurationDatabase);
        }catch(Exception ex){
            getLogger().error(MODULE,"Error while reading database information. Reason: "+ex.getMessage());
            getLogger().trace(ex);
            return "FAILURE";
        }
    }

    @POST
    @Path("/" + SERVER_INFO)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
        public String writeServerInstanceData(@HeaderParam("serverInstanceId") String serverInstanceId,
                                              @HeaderParam("serverInstanceName") String serverInstanceName,
                                              String configurationDBStr) throws UnsupportedEncodingException {

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE,"Writing serverInstance having Id: "+serverInstanceId+", and Name: "+serverInstanceName);
        }

        if(Strings.isNullOrBlank(serverInstanceId)){
            return "FAIL. Reason: Server instance id not provided";
        }

        if(Strings.isNullOrBlank(serverInstanceName)){
            return "FAIL. Reason: Server instance name not provided";
        }

        if(Strings.isNullOrBlank(configurationDBStr)){
            return "FAIL. Reason: Configuration DB detail not provided";
        }

        try {
            ConfigurationDatabase configurationDB = GsonFactory.defaultInstance().fromJson(configurationDBStr, ConfigurationDatabase.class);
            writeDBInfo(configurationDB);
        } catch (Exception e) {
            getLogger().error(MODULE, "Error while writing configuration db detail to file. Reason:" + e.getMessage());
            getLogger().trace(MODULE, e);
            return URLEncoder.encode(FAIL_REASON + e.getMessage(), UTF_8);
        }


        try {
            writeServerInfo(serverInstanceId, serverInstanceName);
        } catch (IOException e) {
            getLogger().error(MODULE, "Error while writing server info to file. Reason:" + e.getMessage());
            getLogger().trace(MODULE, e);
            return URLEncoder.encode(FAIL_REASON + e.getMessage(), UTF_8);

        } catch (DecryptionNotSupportedException | DecryptionFailedException | NoSuchEncryptionException e) {
            // NOT POSSIBLE IN REAL SCENARIO --harsh patel
            getLogger().error(MODULE, "Error while decrypting server info. Reason:" + e.getMessage());
            getLogger().trace(MODULE, e);
            return URLEncoder.encode(FAIL_REASON + e.getMessage(), UTF_8);

        }

        return URLEncoder.encode(GsonFactory.defaultInstance().toJson(serverInfo), UTF_8);
    }


    private void writeServerInfo(String serverID, String serverName) throws IOException, DecryptionNotSupportedException, DecryptionFailedException, NoSuchEncryptionException {

        final String SERVER_ID = "id";
        final String SERVER_NAME = "name";
        final String id = serverID;

        serverID = SERVER_ID + "=" + serverID.trim();
        String serverNameLocal = SERVER_NAME + "=" + serverName.trim();

        java.nio.file.Path path = Paths.get(serverInfo.getServerHome() + File.separator + SYSTEM_PATH + File.separator + SYS_INFO_FILE);

        try (BufferedWriter fileWriter = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE)
        ){
            fileWriter.write(serverID);
            fileWriter.newLine();
            fileWriter.write(serverNameLocal);
            setServerInstanceId(id);
        }
    }

    private void writeDBInfo(ConfigurationDatabase configurationDB) throws  IOException {

        try (FileWriter fileWriter = new FileWriter(new File(serverInfo.getServerHome() + File.separator + SYSTEM_PATH + File.separator + "database.json"), false)){
            GsonFactory.defaultInstance().toJson(configurationDB, fileWriter);
        }
    }

    private void setServerInstanceId(String encryptedInstanceId) throws DecryptionNotSupportedException,
            DecryptionFailedException,
            NoSuchEncryptionException {

        serverInfo.setServerInstanceId(PasswordEncryption.getInstance().decrypt(encryptedInstanceId, PasswordEncryption.ELITECRYPT));
    }

    @GET
    @Path("/"+RELOAD_CONFIGURATION)
    @Produces(MediaType.TEXT_PLAIN)
    public String reloadConfiguration(){
        if (instanceWebServiceContext.reloadConfiguration()) {
            return SUCCESS;
        }
        return FAILURE;
    }

    @GET
    @Path("/serverStatus/{serverInstanceId}")
    @Produces({MediaType.TEXT_PLAIN})
    public String getStatus(@PathParam("serverInstanceId")String serverInstanceId) {

        if(Strings.isNullOrBlank(serverInstanceId) ){
            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Unable to get server status. Reason: ServerInstanceId not provided");
            }
            return ResultCode.INPUT_PARAMETER_MISSING.name();
        }

        if (serverInfo.getServerInstanceId().equalsIgnoreCase(serverInstanceId) == false) {
            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Unable to get server status. Reason: received ServerInstance Id: " + serverInstanceId + " doesn't match with current Server Id: "+serverInfo.getServerInstanceId());
            }
            return ResultCode.INVALID_INPUT_PARAMETER.name();
        }

        return serverStatusProvider.getStatus();
    }

    @GET
    @Path("/serverStatus")
    @Produces({MediaType.TEXT_PLAIN})
    public String getStatus() {
        return serverStatusProvider.getStatus();
    }

    @GET
    @Path("/serviceInfo/{serviceName}")
    @Produces({MediaType.TEXT_PLAIN})
    public String getServicesInfo(@PathParam("serviceName")String serviceName) throws UnsupportedEncodingException {

        if(Strings.isNullOrBlank(serviceName) ){
            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Unable to get service information. Reason: ServiceName not provided");
            }
            return URLEncoder.encode(ResultCode.INPUT_PARAMETER_MISSING.name(), UTF_8);
        }

        if (serverInfo.getServiceDescription().containsKey(serviceName.toUpperCase()) == false) {
            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Unable to get Service Information. Reason: received serviceName: " + serviceName + " doesn't match with current Service Name: "+serverInfo.getServerInstanceId());
            }
            return URLEncoder.encode(ResultCode.INVALID_INPUT_PARAMETER.name(), UTF_8);
        }

        return URLEncoder.encode(GsonFactory.defaultInstance().toJson(serverInfo.getServiceDescription().get(serviceName)), UTF_8);
    }

    @GET
    @Path("/globalListenerInfo/{listenerName}")
    @Produces({MediaType.TEXT_PLAIN})
    public String getGlobalListenersInfo(@PathParam("listenerName")String listenerName) throws UnsupportedEncodingException {

        if(Strings.isNullOrBlank(listenerName) ){
            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Unable to get global listener information. Reason: listenerName not provided");
            }
            return URLEncoder.encode(ResultCode.INPUT_PARAMETER_MISSING.name(), UTF_8);
        }

        if (serverInfo.getGlobalListeners().containsKey(listenerName.toUpperCase()) == false) {
            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Unable to get global listener Information. Reason: received listenerName: " + listenerName + " doesn't match with current Listener Name ");
            }
            return URLEncoder.encode(ResultCode.INVALID_INPUT_PARAMETER.name(), UTF_8);
        }

        return URLEncoder.encode(GsonFactory.defaultInstance().toJson(serverInfo.getGlobalListeners().get(listenerName)), UTF_8);
    }

    @GET
    @Path("/dataSourceInfo/{dataSourceName}")
    @Produces({MediaType.TEXT_PLAIN})
    public String getDataSourceInfo(@PathParam("dataSourceName")String dataSourceName) throws UnsupportedEncodingException {

        if(Strings.isNullOrBlank(dataSourceName) ){
            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Unable to get data source information. Reason: dataSourceName not provided");
            }
            return URLEncoder.encode(ResultCode.INPUT_PARAMETER_MISSING.name(), UTF_8);
        }

        Map<String, DataSourceInfo> dataSourceInfoMap = dataSourceInfoProvider.getDataSourceInfo();
        if (dataSourceInfoMap.containsKey(dataSourceName) == false) {
            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Unable to get data source Information. Reason: received dataSourceName: " + dataSourceName + " doesn't match with current Data Source Name ");
            }
            return URLEncoder.encode(ResultCode.INVALID_INPUT_PARAMETER.name(), UTF_8);
        }

        return URLEncoder.encode(GsonFactory.defaultInstance().toJson(dataSourceInfoMap.get(dataSourceName)), UTF_8);
    }

    @GET
        @Path("/dataSourceInfoByType/{dataSourceType}")
    @Produces({MediaType.TEXT_PLAIN})
    public String getDataSourceInfoByType(@PathParam("dataSourceType")String dataSourceType) throws UnsupportedEncodingException {

        if(Strings.isNullOrBlank(dataSourceType) ){
            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Unable to get data source information. Reason: Type not provided");
            }
            return URLEncoder.encode(ResultCode.INPUT_PARAMETER_MISSING.name(), UTF_8);
        }

        Map<String, List<DataSourceInfo>> dataSourceInfoMap = dataSourceInfoProvider.getDataSourceInfoByType();

        if (dataSourceInfoMap.containsKey(dataSourceType) == false) {
            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Unable to get data source Information. Reason: Received type: " + dataSourceType + " doesn't match with current data source type ");
            }
            return URLEncoder.encode(ResultCode.INVALID_INPUT_PARAMETER.name(), UTF_8);
        }

        return URLEncoder.encode(GsonFactory.defaultInstance().toJson(dataSourceInfoMap.get(dataSourceType)), UTF_8);
    }

    @GET
    @Path("/gatewayStatusInfo/{communicationProtocol}")
    @Produces({MediaType.TEXT_PLAIN})
    public String getGatewayStatusInfo(@PathParam("communicationProtocol")String communicationProtocol) throws UnsupportedEncodingException {

        if(Strings.isNullOrBlank(communicationProtocol) ){
            getLogger().error(MODULE, "Unable to get gateway status information. Reason: communicationProtocol not provided");
            return URLEncoder.encode(ResultCode.INPUT_PARAMETER_MISSING.name(), UTF_8);
        }

        Map<String, List<GatewayStatusInfo>> gatewayStatusInfo = null;
        if(CommunicationProtocol.DIAMETER.name().equalsIgnoreCase(communicationProtocol) && Objects.nonNull(diameterGatewayStatusInfoProvider.getGatewayStatusInfo())){
            gatewayStatusInfo = diameterGatewayStatusInfoProvider.getGatewayStatusInfo();
        }else if(CommunicationProtocol.RADIUS.name().equalsIgnoreCase(communicationProtocol) && Objects.nonNull(radiusGatewayStatusInfoProvider.getGatewayStatusInfo())){
            gatewayStatusInfo = radiusGatewayStatusInfoProvider.getGatewayStatusInfo();
        }else {
            getLogger().error(MODULE, "Unable to get gateway status Information. Reason: received communication protocol: " + communicationProtocol + " doesn't match with current communication protocol ");
            return URLEncoder.encode(ResultCode.INVALID_INPUT_PARAMETER.name(), UTF_8);
        }

        return URLEncoder.encode(GsonFactory.defaultInstance().toJson(gatewayStatusInfo.get(communicationProtocol)), UTF_8);
    }

    @GET
    @Path("/serviceInfo/")
    @Produces({MediaType.TEXT_PLAIN})
    public String getServicesInfo() throws UnsupportedEncodingException {

        Map<String, ServiceInfo> nameToserviceInfo = serverInfo.getServiceDescription();
        if (Objects.nonNull(nameToserviceInfo) == false) {
            getLogger().info(MODULE, "Unable to get service information. Reason: Service info is not configured.");
            return URLEncoder.encode(ResultCode.INTERNAL_ERROR.name(), UTF_8);
        }

        return URLEncoder.encode(GsonFactory.defaultInstance().toJson(nameToserviceInfo), UTF_8);
    }

    @GET
    @Path("/globalListenerInfo/")
    @Produces({MediaType.TEXT_PLAIN})
    public String getGlobalListenersInfo() throws UnsupportedEncodingException {

        Map<String, GlobalListenersInfo> nameToGlobalLitenersInfo = serverInfo.getGlobalListeners();
        if (Objects.nonNull(nameToGlobalLitenersInfo) == false) {
            getLogger().info(MODULE, "Unable to get global listener information. Reason: Global listeners info is not configured.");
            return URLEncoder.encode(ResultCode.INTERNAL_ERROR.name(), UTF_8);
        }

		return URLEncoder.encode(GsonFactory.defaultInstance().toJson(nameToGlobalLitenersInfo), UTF_8);
    }

    @GET
    @Path("/dataSourceInfo/")
    @Produces({MediaType.TEXT_PLAIN})
    public String getDataSourceInfo() throws UnsupportedEncodingException {

        Map<String, DataSourceInfo> nameToDataSourceInfo = new HashMap<>();
        if(Objects.nonNull(dataSourceInfoProvider.getDataSourceInfo())){
            nameToDataSourceInfo.putAll(dataSourceInfoProvider.getDataSourceInfo());
        }else {
            getLogger().info(MODULE, "Unable to get data source information. Reason: Data source info is not configured.");
            return URLEncoder.encode(ResultCode.INTERNAL_ERROR.name(), UTF_8);
        }

		return URLEncoder.encode(GsonFactory.defaultInstance().toJson(nameToDataSourceInfo), UTF_8);
    }


    @GET
    @Path("/dataSourceInfoByType/")
    @Produces({MediaType.TEXT_PLAIN})
    public String getDataSourceInfoByType() throws UnsupportedEncodingException {

        Map<String, List<DataSourceInfo>> typeToDataSourceInfo = new HashMap<>();
        if(Objects.nonNull(dataSourceInfoProvider.getDataSourceInfoByType())) {
            typeToDataSourceInfo.putAll(dataSourceInfoProvider.getDataSourceInfoByType());
        }else {
            getLogger().info(MODULE, "Unable to get data source information by type. Reason: Data source info is not configured.");
            return URLEncoder.encode(ResultCode.INTERNAL_ERROR.name(), UTF_8);
        }

		return URLEncoder.encode(GsonFactory.defaultInstance().toJson(typeToDataSourceInfo), UTF_8);
    }

    @GET
    @Path("/gatewayStatusInfo/")
    @Produces({MediaType.TEXT_PLAIN})
    public String getGatewayStatusInfo() throws UnsupportedEncodingException {

        Map<String, List<GatewayStatusInfo>> protocolToGatewayStatusInfo = new HashMap<>();
        if(Objects.nonNull(diameterGatewayStatusInfoProvider.getGatewayStatusInfo())){
            protocolToGatewayStatusInfo.putAll(diameterGatewayStatusInfoProvider.getGatewayStatusInfo());
        }
        if(Objects.nonNull(radiusGatewayStatusInfoProvider.getGatewayStatusInfo())){
            protocolToGatewayStatusInfo.putAll(radiusGatewayStatusInfoProvider.getGatewayStatusInfo());
        }

        if(protocolToGatewayStatusInfo.isEmpty()) {
            getLogger().info(MODULE, "Unable to get gateway status Information. Reason: Diameter and radius gateway status info is not configured.");
            return URLEncoder.encode(ResultCode.INTERNAL_ERROR.name(), UTF_8);
        }

		return URLEncoder.encode(GsonFactory.defaultInstance().toJson(protocolToGatewayStatusInfo), UTF_8);
    }

    @GET
    @Path("/serverInfo/")
    @Produces({MediaType.TEXT_PLAIN})
    public String getServerInfo() throws UnsupportedEncodingException {

        if (Objects.nonNull(serverInfo) == false) {
            getLogger().info(MODULE, "Unable to get server information. Reason: Server info is not configured.");
            return URLEncoder.encode(ResultCode.INTERNAL_ERROR.name(), UTF_8);
        }

		return URLEncoder.encode(GsonFactory.defaultInstance().toJson(this.serverInfo), UTF_8);
    }

    @GET
    @Path("/policyStatusInfo/")
    @Produces({MediaType.TEXT_PLAIN})
    public String getPolicyStatusInfo() throws UnsupportedEncodingException {

        if(Objects.nonNull(policyManager) == false){
            getLogger().info(MODULE, "Unable to get policy status information. Reason: Error in initializing Policy Manager.");
            return URLEncoder.encode(ResultCode.INTERNAL_ERROR.name(), UTF_8);
        }

        List<PolicyDetail> policyCacheDetails = policyManager.getPolicyDetail();
        if (Objects.nonNull(policyCacheDetails) == false) {
            getLogger().info(MODULE, "Unable to get policy status information. Reason: No policy found.");
            return URLEncoder.encode(ResultCode.INTERNAL_ERROR.name(), UTF_8);
        }

		return URLEncoder.encode(GsonFactory.defaultInstance().toJson(policyCacheDetails), UTF_8);
    }


    @GET
    @Path("/removeAlternateIdFromCache/{alternateId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String removeAlternateIdFromCache(@PathParam("alternateId") String alternateId){
        if (StringUtils.isBlank(alternateId)) {

            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Unable to remove alternate id from cache. Reason: alternate id not received");
            }
            return ResultCode.INPUT_PARAMETER_MISSING.name();
        }
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Removing alternate id: " + alternateId + " from cache");
        }

        String subscriberId = CacheAwareDDFTable.getInstance().removeSecondaryCache(alternateId);
        if (StringUtils.isBlank(subscriberId)) {
            return ResultCode.NOT_FOUND.name;
        }
        return ResultCode.SUCCESS.name;
    }
}