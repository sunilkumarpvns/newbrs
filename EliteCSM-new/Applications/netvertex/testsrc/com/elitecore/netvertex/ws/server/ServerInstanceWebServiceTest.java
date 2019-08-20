package com.elitecore.netvertex.ws.server;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.corenetvertex.GlobalListenersInfo;
import com.elitecore.corenetvertex.ServerInfo;
import com.elitecore.corenetvertex.ServiceInfo;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.sm.serverinstance.ConfigurationDatabase;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.netvertex.EliteNetVertexServer;
import com.elitecore.netvertex.restapi.DataSourceInfoProvider;
import com.elitecore.netvertex.restapi.DiameterGatewayStatusInfoProviderImpl;
import com.elitecore.netvertex.restapi.RadiusGatewayStatusInfoProviderImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.elitecore.corenetvertex.constants.CommonConstants.EMPTY_STRING;
import static java.nio.file.attribute.PosixFilePermission.*;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;


@RunWith(HierarchicalContextRunner.class)
public class ServerInstanceWebServiceTest {

    private static final String SERVER_INSTANCE_ID = "000123";
    private static final String PCRF_SERVICE = "PCRF";
    private static final String RNC_OFFLINE_SERVICE = "rncOffline";
    public static final String SUCCESS = "SUCCESS";
    private ServerInstanceWebService serverInstanceWebService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private File systemFolder;

    private EliteNetVertexServer.ServerStatusProvider serverStatusProvider;
    private DataSourceInfoProvider dataSourceInfoProvider;
    private EliteNetVertexServer.InstanceWebServiceContext context;

    @Before
    public void setup() throws IOException {

        temporaryFolder.create();
        systemFolder = temporaryFolder.newFolder("system");
        ServerInfo serverInfo = new ServerInfo(temporaryFolder.getRoot().getAbsolutePath());
        serverInfo.setServerInstanceId(SERVER_INSTANCE_ID);
        serverStatusProvider = mock(EliteNetVertexServer.ServerStatusProvider.class);
        dataSourceInfoProvider = mock(DataSourceInfoProvider.class);

        context = mock(EliteNetVertexServer.InstanceWebServiceContext.class);
        serverInstanceWebService = new ServerInstanceWebService(serverInfo, serverStatusProvider, dataSourceInfoProvider, context);
    }

    @Test
    public void reloadServerConfigurationShouldPassCallToInstanceWebServiceContext() {
        serverInstanceWebService.reloadConfiguration();
        verify(context, times(1)).reloadConfiguration();
    }

    @Test
    public void reloadServerSuccessfulConfiguration() throws Exception {

        doReturn(true).when(context).reloadConfiguration();

        String message = serverInstanceWebService.reloadConfiguration();
        assertEquals(SUCCESS, URLDecoder.decode(message, CommonConstants.UTF_8));
    }

    @Test
    public void reloadServerFAILUREConfiguration() throws Exception {

        doReturn(false).when(context).reloadConfiguration();

        String message = serverInstanceWebService.reloadConfiguration();
        assertEquals("FAILURE", URLDecoder.decode(message, CommonConstants.UTF_8));
    }

    public class FailRestCallWhen {
        private String sysInfoFileContents;
        private String databaseJsonFileContents;

        @Before
        public void setup() throws UnsupportedEncodingException {

            ConfigurationDatabase configurationDatabase = new ConfigurationDatabase();
            configurationDatabase.setDriverClassName(UUID.randomUUID().toString());
            configurationDatabase.setUrl(UUID.randomUUID().toString());
            configurationDatabase.setUsername(UUID.randomUUID().toString());
            configurationDatabase.setPassword(UUID.randomUUID().toString());

            String serverInstanceId = UUID.randomUUID().toString();
            String serverInstanceName = UUID.randomUUID().toString();
            sysInfoFileContents = Strings.join(",", Arrays.asList("id=" + serverInstanceId, "name=" + serverInstanceName));
            databaseJsonFileContents = GsonFactory.defaultInstance().toJson(configurationDatabase);

            serverInstanceWebService.writeServerInstanceData(serverInstanceId, serverInstanceName, databaseJsonFileContents);

        }



        @Test
        public void serviceInstanceNameIsNull() throws Exception {
            String message = serverInstanceWebService.writeServerInstanceData(UUID.randomUUID().toString(), null, "");
            assertEquals("FAIL. Reason: Server instance name not provided", URLDecoder.decode(message, CommonConstants.UTF_8));
        }

        @Test
        public void serviceInstanceNameIsEmpty() throws Exception {
            String message = serverInstanceWebService.writeServerInstanceData(UUID.randomUUID().toString(), "", "");
            Assert.assertEquals("FAIL. Reason: Server instance name not provided", URLDecoder.decode(message, CommonConstants.UTF_8));
        }

        @Test
        public void serviceInstanceIdIsNull() throws Exception {
            String message = serverInstanceWebService.writeServerInstanceData(null, UUID.randomUUID().toString(), "");
            Assert.assertEquals("FAIL. Reason: Server instance id not provided", URLDecoder.decode(message, CommonConstants.UTF_8));
        }

        @Test
        public void serviceInstanceIdIsEmpty() throws Exception {
            String message = serverInstanceWebService.writeServerInstanceData("", UUID.randomUUID().toString(), "");
            Assert.assertEquals("FAIL. Reason: Server instance id not provided", URLDecoder.decode(message, CommonConstants.UTF_8));
        }

        @Test
        public void configurationDBIsNull() throws Exception {
            String message = serverInstanceWebService.writeServerInstanceData(UUID.randomUUID().toString(), UUID.randomUUID().toString(), null);
            Assert.assertEquals("FAIL. Reason: Configuration DB detail not provided", URLDecoder.decode(message, CommonConstants.UTF_8));
        }

        @Test
        public void configurationDBIsEmpty() throws Exception {
            String message = serverInstanceWebService.writeServerInstanceData(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "");
            Assert.assertEquals("FAIL. Reason: Configuration DB detail not provided", URLDecoder.decode(message, CommonConstants.UTF_8));
        }

        @Test
        public void configurationDBIsNotValideJson() throws Exception {
            String message = serverInstanceWebService.writeServerInstanceData(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "{{{");
            Assert.assertThat(URLDecoder.decode(message, CommonConstants.UTF_8), startsWith("FAIL"));
        }

        @Test
        @Ignore
        public void systemFolderAreNotWritable() throws Exception {
            Files.setPosixFilePermissions(Paths.get(systemFolder.getAbsolutePath()), EnumSet.of(OWNER_WRITE));
            String message = serverInstanceWebService.writeServerInstanceData(UUID.randomUUID().toString(), UUID.randomUUID().toString(), databaseJsonFileContents);
            Assert.assertThat(URLDecoder.decode(message, CommonConstants.UTF_8), startsWith("FAIL"));
        }

        @Test
        @Ignore
        public void sysInfoFileAreNotWritable() throws Exception {
            Files.setPosixFilePermissions(Paths.get(systemFolder.getAbsolutePath(), "_sys.info"), EnumSet.of(OWNER_READ));
            String message = serverInstanceWebService.writeServerInstanceData(UUID.randomUUID().toString(), UUID.randomUUID().toString(), databaseJsonFileContents);
            Assert.assertThat(URLDecoder.decode(message, CommonConstants.UTF_8), startsWith("FAIL"));
        }

        @Test
        @Ignore
        public void whenSystemFolderAreNotWritable() throws Exception {
            Files.setPosixFilePermissions(Paths.get(systemFolder.getAbsolutePath(), "database.json"), EnumSet.of(OWNER_READ));
            String message = serverInstanceWebService.writeServerInstanceData(UUID.randomUUID().toString(), UUID.randomUUID().toString(), databaseJsonFileContents);
            Assert.assertThat(URLDecoder.decode(message, CommonConstants.UTF_8), startsWith("FAIL"));
        }

        @After
        public void checkPreviousInformationIsUnchanged() throws IOException {
            systemFolder.setWritable(true);
            Files.setPosixFilePermissions(Paths.get(systemFolder.getAbsolutePath()), EnumSet.of(OWNER_WRITE, OWNER_READ, OWNER_EXECUTE));
            Assert.assertEquals(sysInfoFileContents, readSysInfoFile());
            Assert.assertEquals(databaseJsonFileContents, readDatabaseJsonFile());
        }


    }


    public class WhenAllDetailsAreValidThen {

        private String sysInfoFileContents;
        private String databaseJsonFileContents;

        @Before
        public void setup() throws UnsupportedEncodingException {

            ConfigurationDatabase configurationDatabase = new ConfigurationDatabase();
            configurationDatabase.setDriverClassName(UUID.randomUUID().toString());
            configurationDatabase.setUrl(UUID.randomUUID().toString());
            configurationDatabase.setUsername(UUID.randomUUID().toString());
            configurationDatabase.setPassword(UUID.randomUUID().toString());

            String serverInstanceId = UUID.randomUUID().toString();
            String serverInstanceName = UUID.randomUUID().toString();
            sysInfoFileContents = Strings.join(",", Arrays.asList("id=" + serverInstanceId, "name=" + serverInstanceName));
            databaseJsonFileContents = GsonFactory.defaultInstance().toJson(configurationDatabase);

            serverInstanceWebService.writeServerInstanceData(serverInstanceId, serverInstanceName, databaseJsonFileContents);

        }

        @Test
        public void writeServiceInstanceNameAndServerInstanceIdToSysInfoFile() throws Exception {
            Assert.assertEquals(sysInfoFileContents, readSysInfoFile());
        }


        @Test
        public void writeConfigurationDBToDatabaseJsonFile() throws Exception {
            Assert.assertEquals(databaseJsonFileContents, readDatabaseJsonFile());
        }
    }

    private String readSysInfoFile() throws IOException {
        Path path = Paths.get(systemFolder.getAbsolutePath(), "_sys.info");
        return Files.lines(path).collect(Collectors.joining(","));
    }

    private String readDatabaseJsonFile() throws IOException {
        Path path = Paths.get(systemFolder.getAbsolutePath(), "database.json");
        return Files.lines(path).collect(Collectors.joining());
    }

    @After
    public void deleteFile() {
        temporaryFolder.delete();
    }

    @Test
    public void testGetStatus_return_InputParameterMissing_WhenNullNetServerCodeIsPassed() {
        String resultString = serverInstanceWebService.getStatus(null);
        Assert.assertEquals(ResultCode.INPUT_PARAMETER_MISSING.name(), resultString);

    }

    @Test
    public void testGetStatus_call_ServerStatusProvider_GetStatus_ForValidServerIdentifier() {
        serverStatusProvider.getStatus();
        serverInstanceWebService.getStatus(SERVER_INSTANCE_ID);
        verify(serverStatusProvider, atLeastOnce()).getStatus();
    }

    @Test
    public void testGetStatus_return_InputParameterMissing_WhenEmptyNetServerCodeIsPassed() {
        String resultString = serverInstanceWebService.getStatus(EMPTY_STRING);
        Assert.assertEquals(ResultCode.INPUT_PARAMETER_MISSING.name(), resultString);
    }

    @Test
    public void testGetStatus_return_InvalidInputParameter_WhenNetServerCode_DoesnotMatch_WithServerId() {
        String INVALID_SERVER_ID = "12345";
        String resultString = serverInstanceWebService.getStatus(INVALID_SERVER_ID);
        Assert.assertEquals(ResultCode.INVALID_INPUT_PARAMETER.name(), resultString);
    }

    @Test
    public void test_returned_DatabaseDataSoruce_NotNull() {
        String databaserDatasource = serverInstanceWebService.getDatabaseDatasource();
        assertNotNull(databaserDatasource);
    }

    @Test
    public void test_returned_ReloadConfiguration_Response_NotNull() {
        String response = serverInstanceWebService.reloadConfiguration();
        assertNotNull(response);
    }

    public class WebServiceTo {

        private static final String LISTENER_ADDRESS = "0.0.0.0";
        private static final int RADIUS_PORT = 2813;
        private ServerInfo serverInfo;

        private Map<String, ServiceInfo> nameToServiceInfo;
        private Map<String, GlobalListenersInfo> nameToGlobalListenerInfos;

        @Mock
        private DiameterGatewayStatusInfoProviderImpl diameterGatewayStatusInfoProvider;
        @Mock
        private RadiusGatewayStatusInfoProviderImpl radiusGatewayStatusInfoProvider;
        @Mock
        private PolicyManager policyManager;

        @Before
        public void setup() {
            MockitoAnnotations.initMocks(this);
            serverInfo = spy(new ServerInfo(temporaryFolder.getRoot().getAbsolutePath()));
            setServiceDescription(serverInfo);
            setGlobalListeners(serverInfo);
            serverInstanceWebService = new ServerInstanceWebService(serverInfo, serverStatusProvider, dataSourceInfoProvider, context);
        }

        @Test
        public void getServices_return_InputParameterMissing_WhenEmptyServiceNameIsPassed() throws UnsupportedEncodingException {
            String resultString = serverInstanceWebService.getServicesInfo(EMPTY_STRING);
            Assert.assertEquals(ResultCode.INPUT_PARAMETER_MISSING.name(), URLDecoder.decode(resultString, CommonConstants.UTF_8));
        }

        @Test
        public void getServices_return_InvalidInputParameter_WhenServiceNameNotFound() throws UnsupportedEncodingException {
            String resultString = serverInstanceWebService.getServicesInfo(UUID.randomUUID().toString());
            Assert.assertEquals(ResultCode.INVALID_INPUT_PARAMETER.name(), URLDecoder.decode(resultString, CommonConstants.UTF_8));
        }

        @Test
        public void getServices_returnEncodedServiceInfo_ForValidServiceName() throws UnsupportedEncodingException {
            String actualServicesInfo = serverInstanceWebService.getServicesInfo(PCRF_SERVICE);
            verify(serverInfo, atLeastOnce()).getServiceDescription();
            assertEquals(GsonFactory.defaultInstance().toJson(nameToServiceInfo.get(PCRF_SERVICE)), URLDecoder.decode(actualServicesInfo, CommonConstants.UTF_8));
        }

        @Test
        public void getGlobalListners_return_InputParameterMissing_WhenEmptyListenerNameIsPassed() throws UnsupportedEncodingException {
            String resultString = serverInstanceWebService.getGlobalListenersInfo(EMPTY_STRING);
            Assert.assertEquals(ResultCode.INPUT_PARAMETER_MISSING.name(), URLDecoder.decode(resultString, CommonConstants.UTF_8));
        }

        @Test
        public void getGlobalListners_return_InvalidInputParameter_WhenListenerNameIsInvalid() throws UnsupportedEncodingException {
            String resultString = serverInstanceWebService.getGlobalListenersInfo(UUID.randomUUID().toString());
            Assert.assertEquals(ResultCode.INVALID_INPUT_PARAMETER.name(), URLDecoder.decode(resultString, CommonConstants.UTF_8));
        }

        @Test
        public void getGlobalListners_returnEncodedGlobalListenerInfo_ForValidListenerName() throws UnsupportedEncodingException {
            String actualGlobalListenersInfo = serverInstanceWebService.getGlobalListenersInfo(CommunicationProtocol.DIAMETER.name);
            verify(serverInfo, atLeastOnce()).getGlobalListeners();
            assertEquals(GsonFactory.defaultInstance().toJson(nameToGlobalListenerInfos.get(CommunicationProtocol.DIAMETER.name)), URLDecoder.decode(actualGlobalListenersInfo, CommonConstants.UTF_8));
        }

        private void setServiceDescription(ServerInfo serverInfo) {
            nameToServiceInfo = new HashMap<>();
            nameToServiceInfo.put(PCRF_SERVICE, new ServiceInfo(PCRF_SERVICE, LifeCycleState.RUNNING.message, new Date(), null));
            nameToServiceInfo.put(RNC_OFFLINE_SERVICE, new ServiceInfo(RNC_OFFLINE_SERVICE, LifeCycleState.STOPPED.message, null, null));
            serverInfo.setServiceDescription(new ArrayList<>(nameToServiceInfo.values()));
        }

        private void setGlobalListeners(ServerInfo serverInfo) {
            nameToGlobalListenerInfos = new HashMap<>();
            nameToGlobalListenerInfos.put(CommunicationProtocol.RADIUS.name, new GlobalListenersInfo(CommunicationProtocol.RADIUS.name, LISTENER_ADDRESS, RADIUS_PORT, new Date(), LifeCycleState.NOT_STARTED.message, null));
            nameToGlobalListenerInfos.put(CommunicationProtocol.DIAMETER.name, new GlobalListenersInfo(CommunicationProtocol.DIAMETER.name, LISTENER_ADDRESS, DiameterConstants.DIAMETER_SERVICE_PORT, new Date(), LifeCycleState.RUNNING.message, null));
            serverInfo.setGlobalListeners(new ArrayList<>(nameToGlobalListenerInfos.values()));
        }

        @Test
        public void getDataSourceInfo_return_InputParameterMissing_WhenEmptyDataSourceNameIsPassed() throws UnsupportedEncodingException {
            String resultString = serverInstanceWebService.getDataSourceInfo(EMPTY_STRING);
            Assert.assertEquals(ResultCode.INPUT_PARAMETER_MISSING.name(), URLDecoder.decode(resultString, CommonConstants.UTF_8));
        }

        @Test
        public void getDataSourceInfo_return_InvalidInputParameter_WhenDataSourceNameIsInvalid() throws UnsupportedEncodingException {
            String resultString = serverInstanceWebService.getDataSourceInfo(UUID.randomUUID().toString());
            Assert.assertEquals(ResultCode.INVALID_INPUT_PARAMETER.name(), URLDecoder.decode(resultString, CommonConstants.UTF_8));
        }

        @Test
        public void getDataSourceInfo_returnEncodedDataSourceInfo_ForValidDataSourceName() throws UnsupportedEncodingException {
            serverInstanceWebService.getDataSourceInfo("DefaultDatabaseDatasource");
            verify(dataSourceInfoProvider, atLeastOnce()).getDataSourceInfo();
        }

        @Test
        public void getDataSourceInfoByType_return_InputParameterMissing_WhenEmptyDataSourceTypeIsPassed() throws UnsupportedEncodingException {
            String resultString = serverInstanceWebService.getDataSourceInfoByType(EMPTY_STRING);
            Assert.assertEquals(ResultCode.INPUT_PARAMETER_MISSING.name(), URLDecoder.decode(resultString, CommonConstants.UTF_8));
        }

        @Test
        public void getDataSourceInfoByType_return_InvalidInputParameter_WhenDataSourceTypeIsInvalid() throws UnsupportedEncodingException {
            String resultString = serverInstanceWebService.getDataSourceInfoByType(UUID.randomUUID().toString());
            Assert.assertEquals(ResultCode.INVALID_INPUT_PARAMETER.name(), URLDecoder.decode(resultString, CommonConstants.UTF_8));
        }

        @Test
        public void getDataSourceInfoByType_returnEncodedDataSourceInfo_ForValidDataSourceType() throws UnsupportedEncodingException {
            serverInstanceWebService.getDataSourceInfoByType("DefaultDatabaseDatasource");
            verify(dataSourceInfoProvider, atLeastOnce()).getDataSourceInfoByType();
        }

        @Test
        public void getGatewayStatusInfo_return_InputParameterMissing_WhenEmptyCommunicationProtocolIsPassed() throws UnsupportedEncodingException {
            String resultString = serverInstanceWebService.getGatewayStatusInfo(EMPTY_STRING);
            Assert.assertEquals(ResultCode.INPUT_PARAMETER_MISSING.name(), URLDecoder.decode(resultString, CommonConstants.UTF_8));
        }

        @Test
        public void getGatewayStatusInfo_return_InvalidInputParameter_WhenCommunicationProtocolIsInvalid() throws UnsupportedEncodingException {
            String resultString = serverInstanceWebService.getGatewayStatusInfo(UUID.randomUUID().toString());
            Assert.assertEquals(ResultCode.INVALID_INPUT_PARAMETER.name(), URLDecoder.decode(resultString, CommonConstants.UTF_8));
        }

        @Test
        public void getGatewayStatusInfo_returnEncodedGatewayStatusInfo_ForValidCommunicationProtocol() throws UnsupportedEncodingException {
            serverInstanceWebService.setGatewayStatusInfoProvider(radiusGatewayStatusInfoProvider, diameterGatewayStatusInfoProvider);
            serverInstanceWebService.getGatewayStatusInfo(CommunicationProtocol.DIAMETER.name);
            verify(diameterGatewayStatusInfoProvider, atLeastOnce()).getGatewayStatusInfo();
        }

        @Test
        public void getServicesInfo_returnInternalError_WhenServicesNotConfigured() throws UnsupportedEncodingException {
            when(serverInfo.getServiceDescription()).thenReturn(null);
            Assert.assertEquals(ResultCode.INTERNAL_ERROR.name(), URLDecoder.decode(serverInstanceWebService.getServicesInfo(), CommonConstants.UTF_8));
        }

        @Test
        public void getServicesInfo_returnEncodedServicesInfo() throws UnsupportedEncodingException {
            String actualServicesInfo = serverInstanceWebService.getServicesInfo();
            verify(serverInfo, atLeastOnce()).getServiceDescription();
            assertEquals(GsonFactory.defaultInstance().toJson(nameToServiceInfo), URLDecoder.decode(actualServicesInfo, CommonConstants.UTF_8));
        }

        @Test
        public void getGlobalListenersInfo_returnInternalError_WhenGlobalListenersNotConfigured() throws UnsupportedEncodingException {
            when(serverInfo.getGlobalListeners()).thenReturn(null);
            Assert.assertEquals(ResultCode.INTERNAL_ERROR.name(), URLDecoder.decode(serverInstanceWebService.getGlobalListenersInfo(), CommonConstants.UTF_8));
        }

        @Test
        public void getGlobalListenersInfo_returnEncodedGlobalListenersInfo() throws UnsupportedEncodingException {
            String actualGlobalListenersInfo = serverInstanceWebService.getGlobalListenersInfo();
            verify(serverInfo, atLeastOnce()).getGlobalListeners();
            assertEquals(GsonFactory.defaultInstance().toJson(nameToGlobalListenerInfos), URLDecoder.decode(actualGlobalListenersInfo, CommonConstants.UTF_8));
        }

        @Test
        public void getDataSourceInfo_returnInternalError_WhenDataSourceNotConfigured() throws UnsupportedEncodingException {
            when(dataSourceInfoProvider.getDataSourceInfo()).thenReturn(null);
            Assert.assertEquals(ResultCode.INTERNAL_ERROR.name(), URLDecoder.decode(serverInstanceWebService.getDataSourceInfo(), CommonConstants.UTF_8));
        }

        @Test
        public void getDataSourceInfo_returnEncodedDataSourceInfo() throws UnsupportedEncodingException {
            serverInstanceWebService.getDataSourceInfo();
            verify(dataSourceInfoProvider, atLeastOnce()).getDataSourceInfo();
        }

        @Test
        public void getDataSourceInfoByType_returnInternalError_WhenDataSourceByTypeNotConfigured() throws UnsupportedEncodingException {
            when(dataSourceInfoProvider.getDataSourceInfoByType()).thenReturn(null);
            Assert.assertEquals(ResultCode.INTERNAL_ERROR.name(), URLDecoder.decode(serverInstanceWebService.getDataSourceInfoByType(), CommonConstants.UTF_8));
        }

        @Test
        public void getDataSourceInfoByType_returnEncodedDataSourceInfo() throws UnsupportedEncodingException {
            serverInstanceWebService.getDataSourceInfoByType();
            verify(dataSourceInfoProvider, atLeastOnce()).getDataSourceInfoByType();
        }

        @Test
        public void getGatewayStatusInfo_returnEncodedGatewayStatusInfo() throws UnsupportedEncodingException {
            serverInstanceWebService.setGatewayStatusInfoProvider(radiusGatewayStatusInfoProvider, diameterGatewayStatusInfoProvider);
            serverInstanceWebService.getGatewayStatusInfo();
            verify(diameterGatewayStatusInfoProvider, atLeastOnce()).getGatewayStatusInfo();
        }

        @Test
        public void getGatewayStatusInfo_returnInternalError_WhenGatewayStatusNotConfigured() throws UnsupportedEncodingException {
            serverInstanceWebService.setGatewayStatusInfoProvider(radiusGatewayStatusInfoProvider, diameterGatewayStatusInfoProvider);
            when(diameterGatewayStatusInfoProvider.getGatewayStatusInfo()).thenReturn(null);
            when(radiusGatewayStatusInfoProvider.getGatewayStatusInfo()).thenReturn(null);
            Assert.assertEquals(ResultCode.INTERNAL_ERROR.name(), URLDecoder.decode(serverInstanceWebService.getGatewayStatusInfo(), CommonConstants.UTF_8));
        }


        @Test
        public void getPolicyStatusInfo_returnInternalError_WhenPolicyManagerNotInitialized() throws UnsupportedEncodingException {
            serverInstanceWebService.setPolicyManager(null);
            Assert.assertEquals(ResultCode.INTERNAL_ERROR.name(), URLDecoder.decode(serverInstanceWebService.getPolicyStatusInfo(), CommonConstants.UTF_8));
        }

        @Test
        public void getPolicyStatusInfo_returnEncodedPolicyStatusInfo() throws UnsupportedEncodingException {
            serverInstanceWebService.setPolicyManager(policyManager);
            serverInstanceWebService.getPolicyStatusInfo();
            verify(policyManager, atLeastOnce()).getPolicyDetail();
        }

        @Test
        public void getPolicyStatusInfo_returnInternalError_WhenPolicyStatusNotFound() throws UnsupportedEncodingException {
            serverInstanceWebService.setPolicyManager(policyManager);
            when(policyManager.getPolicyDetail()).thenReturn(null);
            Assert.assertEquals(ResultCode.INTERNAL_ERROR.name(), URLDecoder.decode(serverInstanceWebService.getPolicyStatusInfo(), CommonConstants.UTF_8));
        }

        @Test
        public void getPolicyStatusInfo_returnPolicyDetailFromPolicyManager() throws UnsupportedEncodingException {
            serverInstanceWebService.setPolicyManager(policyManager);
            serverInstanceWebService.getPolicyStatusInfo();
            verify(policyManager, atLeastOnce()).getPolicyDetail();
        }
    }


}