package com.elitecore.netvertex.license;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.systemx.esix.configuration.EndPointConfiguration;
import com.elitecore.core.systemx.esix.configuration.EndPointConfigurationImpl;
import com.elitecore.core.systemx.esix.http.EndPointManager;
import com.elitecore.core.systemx.esix.http.HTTPClientFactory;
import com.elitecore.core.systemx.esix.http.HTTPMethodType;
import com.elitecore.core.systemx.esix.http.RemoteMethod;
import com.elitecore.core.systemx.esix.http.Scanner;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.commons.LicenseMessages;
import com.elitecore.license.publickey.ElitePublickeyGenerator;
import com.elitecore.license.util.SystemUtil;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.conf.NetvertexServerConfiguration;
import com.elitecore.netvertex.escommunication.data.PDInstanceConfiguration;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.BasicHttpEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LicenseRequesterTest {

    private static final String MODULE= "LICENSE-REQUESTER-TEST";

    @Mock
    private NetVertexServerContext serverContext;
    @Mock
    private NetvertexServerConfiguration serverConfiguration;
    @Mock
    private TaskScheduler taskScheduler;

    private PDInstanceConfiguration pdInstanceConfiguration = null;
    private RemoteMethod remoteMethod= null;
    private BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
    private String licenseKey="thisislicense";
    private String responseString = "{messageCode:200,licenseKey:"+licenseKey+",message:Success}";
    private String responseStringForFail = "{messageCode:"+LicenseMessages.LICENSE_AUTHENTICITY_CHECK_FAILURE.getMessageCode()+",message:\""+LicenseMessages.LICENSE_AUTHENTICITY_CHECK_FAILURE.getMessage()+"\"}";
    private AsyncTaskContext asyncTaskContext = new AsyncTaskContext() {
        @Override
        public void setAttribute(String key, Object attribute) {

        }

        @Override
        public Object getAttribute(String key) {
            return null;
        }
    };
    private HttpClient poolableHttpClient = new HTTPClientFactory().getPoolableHttpClient(10,3000,3000);
    private Scanner scanner = new Scanner(){
        public boolean isAccessible(String ip, int port){
            return true;
        }
    };
    private EmulatedServer emulatedServer;

    @Before
    public void setup() throws UnsupportedEncodingException, InitializationFailedException{
        serverContext = mock(NetVertexServerContext.class);
        serverConfiguration = mock(NetvertexServerConfiguration.class);
        taskScheduler = mock(TaskScheduler.class);

        when(serverContext.getServerConfiguration()).thenReturn(serverConfiguration);
        when(serverContext.getServerHome()).thenReturn("home");
        when(serverContext.getTaskScheduler()).thenReturn(taskScheduler);
        when(serverContext.getServerName()).thenReturn("NV");
        when(serverContext.getServerInstanceId()).thenReturn("1");
        doNothing().when(serverContext).generateSystemAlert(AlertSeverity.CRITICAL, Alerts.LICENSE_REQUEST_FAILED, "LICENSE-REQUESTER",
                "NetVertex is not able to connect to the Server Manager for license purpose. If this behaviour continues for longer period of time, NetVertex will be forced to stop."
        );
        buildRemoteMethod();
        basicHttpEntity.setContent(new ByteArrayInputStream(responseString.getBytes(StandardCharsets.UTF_8.name())));
        addConnectorIntoEndpoint();
        poolableHttpClient = spy(poolableHttpClient);
        emulatedServer = new EmulatedServer();
    }

    private void addConnectorIntoEndpoint(){
        EndPointManager.getInstance().removeEndPoint("1");
        EndPointConfiguration endPointConfiguration = new EndPointConfigurationImpl("1",
                "google.com,localhost,http://exception.com",
                "10055","/hello","endpoint","","");
        EndPointManager.getInstance().addEndPoint(endPointConfiguration,poolableHttpClient,taskScheduler,scanner);
    }

    @After
    public void cleanup() throws IOException{
        if(emulatedServer!=null){
            emulatedServer.stopServer();
        }
    }

    private void doPDInstanceRelatedStuff(){
        instantiatePDConfiguration();
        when(serverConfiguration.getPDInstanceConfiguration()).thenReturn(getPDInstanceList());
    }

    private void prepareEmptyConnectionGroup(){
        when(serverConfiguration.getPDInstanceConfiguration()).thenReturn(new ArrayList<>());
    }

    @Test
    public void initialize_license_requester_successfully_with_stubbed_serverContext_object(){
        LicenseRequester licenseRequester = new LicenseRequester(serverContext);
        licenseRequester.initPDConnectorGroup();
    }

    @Test
    public void execute_requester_without_initializing_and_no_further_processing() throws CommunicationException, IOException{
        LicenseRequester licenseRequester = new LicenseRequester(serverContext);
        licenseRequester.execute(asyncTaskContext);
        verify(serverContext,times(0)).uploadLicense(any(Integer.class),any(String.class),any(String.class));
    }

    @Test
    @Ignore
    public void execute_method_and_it_must_handle_success_response_and_upload_license_when_server_response_is_200() throws CommunicationException, IOException{
        emulatedServer.startSocket(10055, responseString, "200");
        addConnectorIntoEndpoint();
        doPDInstanceRelatedStuff();
        LicenseRequester licenseRequester = new LicenseRequester(serverContext);
        licenseRequester.initPDConnectorGroup();
        licenseRequester.execute(asyncTaskContext);
        verify(serverContext,times(1)).uploadLicense(LicenseMessages.SUCCESS.getMessageCode(),
                LicenseMessages.SUCCESS.getMessage(),licenseKey);

    }

    @Test
    public void execute_method_and_it_must_handle_success_response_and_upload_license_when_HTTP_code_is_not_200() throws CommunicationException, IOException{
        emulatedServer.startSocket(10055, responseString,"300");
        addConnectorIntoEndpoint();
        doPDInstanceRelatedStuff();
        LicenseRequester licenseRequester = new LicenseRequester(serverContext);
        licenseRequester.initPDConnectorGroup();
        licenseRequester.execute(asyncTaskContext);
        verify(serverContext,times(1)).uploadLicense(LicenseMessages.FAILED_TO_CONNECT_WITH_SM_SERVER.getMessageCode(),
                LicenseMessages.FAILED_TO_CONNECT_WITH_SM_SERVER.getMessage(),null);

    }

    @Test
    @Ignore
    public void execute_method_and_log_server_response_when_server_reports_any_other_response_then_success() throws CommunicationException, IOException{
        doPDInstanceRelatedStuff();

        LicenseRequester licenseRequester = new LicenseRequester(serverContext);
        licenseRequester.initPDConnectorGroup();
        emulatedServer.startSocket(10055, responseStringForFail, "200");
        licenseRequester.execute(asyncTaskContext);

        verify(serverContext,times(1)).uploadLicense(LicenseMessages.LICENSE_AUTHENTICITY_CHECK_FAILURE.getMessageCode(),
                LicenseMessages.LICENSE_AUTHENTICITY_CHECK_FAILURE.getMessage(),null);
    }

    @Test
    public void throws_communication_exception_when_connector_group_is_empty() throws CommunicationException, IOException, InitializationFailedException{
        prepareEmptyConnectionGroup();

        LicenseRequester licenseRequester = new LicenseRequester(serverContext);
        licenseRequester.initPDConnectorGroup();
        licenseRequester.execute(asyncTaskContext);

        verify(serverContext,times(1)).uploadLicense(LicenseMessages.FAILED_TO_CONNECT_WITH_SM_SERVER.getMessageCode(),
                LicenseMessages.FAILED_TO_CONNECT_WITH_SM_SERVER.getMessage(),null);
        verify(serverContext,times(1)).generateSystemAlert(AlertSeverity.CRITICAL, Alerts.LICENSE_REQUEST_FAILED, "LICENSE-REQUESTER",
                "NetVertex is not able to connect to the Server Manager for license purpose. " +
                        "If this behaviour continues for longer period of time, NetVertex will be forced to stop.");
    }

    @Test
    public void throws_communication_exception_and_generates_system_alert_when_connector_is_dead_as_no_server_to_connect() throws CommunicationException, IOException, InitializationFailedException{
        doPDInstanceRelatedStuff();
        LicenseRequester licenseRequester = new LicenseRequester(serverContext);
        licenseRequester.initPDConnectorGroup();
        licenseRequester.execute(asyncTaskContext);
        verify(serverContext,times(1)).uploadLicense(LicenseMessages.FAILED_TO_CONNECT_WITH_SM_SERVER.getMessageCode(),
                LicenseMessages.FAILED_TO_CONNECT_WITH_SM_SERVER.getMessage(),null);
        verify(serverContext,times(1)).generateSystemAlert(AlertSeverity.CRITICAL, Alerts.LICENSE_REQUEST_FAILED, "LICENSE-REQUESTER",
                "NetVertex is not able to connect to the Server Manager for license purpose. " +
                        "If this behaviour continues for longer period of time, NetVertex will be forced to stop.");
    }

    @Test
    public void when_any_exception_occur_an_alert_should_be_generated() throws CommunicationException, IOException{
        doPDInstanceRelatedStuff();

        LicenseRequester licenseRequester = new LicenseRequester(serverContext);
        licenseRequester.initPDConnectorGroup();
        emulatedServer.startSocket(10055, "random response", "200");
        licenseRequester.execute(asyncTaskContext);

        verify(serverContext,times(1)).uploadLicense(LicenseMessages.FAILED_TO_CONNECT_WITH_SM_SERVER.getMessageCode(),
                LicenseMessages.FAILED_TO_CONNECT_WITH_SM_SERVER.getMessage(), null);
        verify(serverContext,times(1)).generateSystemAlert(AlertSeverity.CRITICAL, Alerts.LICENSE_REQUEST_FAILED, "LICENSE-REQUESTER",
                "NetVertex is not able to connect to the Server Manager for license purpose. " +
                        "If this behaviour continues for longer period of time, NetVertex will be forced to stop.");
    }

    private void instantiatePDConfiguration(){
        PDInstanceConfiguration pdInstanceConfiguration = new PDInstanceConfiguration();

        pdInstanceConfiguration.setContextPath("/hello");
        pdInstanceConfiguration.setDeploymentPath("/home/NetVertex");
        pdInstanceConfiguration.setHostName("Test-Machine");
        pdInstanceConfiguration.setId("1");
        pdInstanceConfiguration.setIpAddresses("google.com,0.0.0.0,http://exception.com");
        pdInstanceConfiguration.setPort("80");
        pdInstanceConfiguration.setStatus("STARTED");
        pdInstanceConfiguration.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));

        this.pdInstanceConfiguration = spy(pdInstanceConfiguration);
    }

    private List<PDInstanceConfiguration> getPDInstanceList(){
        List<PDInstanceConfiguration> pdInstanceConfigurationList = new ArrayList<>();
        pdInstanceConfigurationList.add(pdInstanceConfiguration);

        return pdInstanceConfigurationList;
    }

    private String getIPAddress(){
        String ipAddress = null;
        try {
            ipAddress = SystemUtil.getIPAddress();
        } catch (UnknownHostException e){
            getLogger().error(MODULE, "Could not get host IP address. " +
                    "Reason: "+e.getMessage());
            getLogger().trace(e);
        }
        return  ipAddress;
    }

    private RemoteMethod buildRemoteMethod(){
        ElitePublickeyGenerator elitePublickeyGen = new ElitePublickeyGenerator();
        String validationKey = elitePublickeyGen.generatePublicKey(serverContext.getServerHome(), LicenseConstants.DEFAULT_ADDITIONAL_KEY_STERLITE);

        remoteMethod = new RemoteMethod("license","getLicense", HTTPMethodType.POST);

        remoteMethod.addArgument("instanceName",serverContext.getServerName());
        remoteMethod.addArgument("id",serverContext.getServerInstanceId());
        remoteMethod.addArgument("ip", getIPAddress());
        remoteMethod.addArgument("key",validationKey);
        remoteMethod.addArgument("version", serverContext.getServerVersion());

        return remoteMethod;
    }
}
