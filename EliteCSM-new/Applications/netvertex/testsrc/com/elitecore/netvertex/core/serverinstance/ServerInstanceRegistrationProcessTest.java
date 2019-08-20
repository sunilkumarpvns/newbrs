package com.elitecore.netvertex.core.serverinstance;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.http.HTTPMethodType;
import com.elitecore.core.systemx.esix.http.RemoteMethod;
import com.elitecore.corenetvertex.ServerInfo;
import com.elitecore.corenetvertex.sm.serverinstance.ConfigurationDatabase;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceRegistrationRequest;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRule;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(HierarchicalContextRunner.class)
public class ServerInstanceRegistrationProcessTest {

	@Rule
	public MockitoJUnitRule mockitoJUnitRule = new MockitoJUnitRule(this);
	@Mock
	private ServerInstanceRequestData serverInstanceReqData;
	@Mock
	private ServerInfo serverInfo;
	@Mock
	private ServerInstanceReaderAndWriter readerAndWriter;
	@Mock
	private ServerInstanceDBInfoWriter serverInstanceDbProcessor;
	@Mock
	private ServerInstanceRegistrationCall instanceRegCall;
	@Mock
	private ServerInstanceRegistrationRequest serverInstanceRegistrationRequest;
	private ConfigurationDatabase configurationDB;
	private ServerInstanceRegistrationProcess serverInstanceRegistrationProcess;


	private RemoteMethod remoteMethod;
	String serverHome = "nextvertex";
	String serverid = "testid";
	String servername = "testname";

	@Before
	public void setup() {
		String dbDriverClassName = "oracle.jdbc.driver.OracleDriver";
		String dbServerIP = "jdbc:oracle:thin:@localhost:1521/pora12c1.sciporacle";
		String dbUsername = "netvertex";
		String dbPasssword = "netvertex";
		int maxIdle = 10;
		int maxTotal = 20;
		int validationQueryTimeout = 3;
		serverInstanceRegistrationRequest = new ServerInstanceRegistrationRequest();
		serverInstanceRegistrationRequest.setServerName(servername);
		serverInstanceRegistrationRequest.setServerInstanceId(serverid);
		serverInstanceRegistrationRequest.setQueryTimeout(validationQueryTimeout);
		serverInstanceRegistrationRequest.setDriverClassName(dbDriverClassName);
		serverInstanceRegistrationRequest.setUserName(dbUsername);
		serverInstanceRegistrationRequest.setPassword(dbPasssword);
		serverInstanceRegistrationRequest.setConnectionUrl(dbServerIP);
		serverInstanceRegistrationRequest.setMaxIdle(maxIdle);
		serverInstanceRegistrationRequest.setMaxTotal(maxTotal);

		configurationDB = new ConfigurationDatabase();
		configurationDB.setDriverClassName(dbDriverClassName);
		configurationDB.setUsername(dbUsername);
		configurationDB.setPassword(dbPasssword);
		configurationDB.setUrl(dbServerIP);
		configurationDB.setMaxIdle(maxIdle);
		configurationDB.setMaxTotal(maxTotal);
		configurationDB.setValidationQueryTimeout(validationQueryTimeout);

		serverInstanceRegistrationProcess = new ServerInstanceRegistrationProcess(serverHome,serverInstanceReqData,readerAndWriter,serverInstanceDbProcessor,instanceRegCall,String.valueOf(serverInfo.getJmxPort()));
		remoteMethod = spy(createRemoteMethod());
	}

	public class WhenSysInfoFileExist{

		@Before
		public void setUp(){
			when(readerAndWriter.isFileExits()).thenReturn(true);
		}

		@Test
        public void serverInstanceReaderShouldReadDataFromSysInfoFile(){
			serverInstanceRegistrationProcess.calltoSMToGetDBAndServerInstance();
			Mockito.verify(readerAndWriter,times(1)).read();
		}

		@Test
		public void callToSMShouldBeMadeWithServerInstanceIdWhenEnvironmentServerNameIsSameAsSysInfoFile() throws InitializationFailedException, IOException, CommunicationException {
        	doReturn(servername).when(readerAndWriter).getName();
			doReturn(servername).when(serverInstanceReqData).getServerName();
			doReturn(serverid).when(readerAndWriter).getId();
			ArgumentCaptor<RemoteMethod> remoteMethod = ArgumentCaptor.forClass(RemoteMethod.class);
			serverInstanceRegistrationProcess.calltoSMToGetDBAndServerInstance();
			Mockito.verify(readerAndWriter,times(1)).read();
			verify(instanceRegCall).callToSm(remoteMethod.capture());
			RemoteMethod value = remoteMethod.getValue();
			Assert.assertEquals(serverid,value.getArguments().get("serverInstanceId"));
		}

		@Test
		public void callToSMShouldBeMadeWithEmptyServerInstanceIdWhenEnvironmentServerNameIsNotSame() throws InitializationFailedException, IOException, CommunicationException {
			doReturn(servername).when(readerAndWriter).getName();
			doReturn(servername+"UNKNOWN").when(serverInstanceReqData).getServerName();
			doReturn(serverid).when(readerAndWriter).getId();
			ArgumentCaptor<RemoteMethod> remoteMethod = ArgumentCaptor.forClass(RemoteMethod.class);
			serverInstanceRegistrationProcess.calltoSMToGetDBAndServerInstance();
			Mockito.verify(readerAndWriter,times(1)).read();
			verify(instanceRegCall).callToSm(remoteMethod.capture());
			RemoteMethod value = remoteMethod.getValue();
			Assert.assertNull(value.getArguments().get("serverInstanceId"));
		}

	}

	public class WhenSysInfoDoesnotExist{

		@Before
		public void setup(){
			when(readerAndWriter.isFileExits()).thenReturn(false);
		}

		@Test
		public void serverInstanceReaderShouldNotReadDataFromSysInfoFile(){
			serverInstanceRegistrationProcess.calltoSMToGetDBAndServerInstance();
			Mockito.verify(readerAndWriter,never()).read();
		}

		@Test
		public void callToSMShouldBeMadeWithEmptyServerInstanceId() throws InitializationFailedException, IOException, CommunicationException {
			ArgumentCaptor<RemoteMethod> remoteMethod = ArgumentCaptor.forClass(RemoteMethod.class);
			serverInstanceRegistrationProcess.calltoSMToGetDBAndServerInstance();
			Mockito.verify(readerAndWriter,never()).read();
			verify(instanceRegCall).callToSm(remoteMethod.capture());
			RemoteMethod value = remoteMethod.getValue();
			Assert.assertNull(value.getArguments().get("serverInstanceId"));
		}

	}

    @Test
    public void callToSmShouldReturnNullDataWhenResponseFromServerManagerIsFailed() throws InitializationFailedException, IOException, CommunicationException,DecryptionNotSupportedException, DecryptionFailedException,NoSuchEncryptionException,EncryptionFailedException{
        ArgumentCaptor<RemoteMethod> remoteMethod = ArgumentCaptor.forClass(RemoteMethod.class);
        serverInstanceRegistrationRequest = null;
        serverInstanceRegistrationProcess.calltoSMToGetDBAndServerInstance();
        verify(instanceRegCall).callToSm(remoteMethod.capture());
        RemoteMethod value = remoteMethod.getValue();
        when(instanceRegCall.callToSm(value)).thenReturn(serverInstanceRegistrationRequest);
        Assert.assertNull(serverInstanceRegistrationRequest);
    }

	@Test
	public void callToSmShouldReturnServerInstanceIdAndServerNameAndWriteIntoSysInfoFile() throws InitializationFailedException, IOException, CommunicationException,DecryptionNotSupportedException, DecryptionFailedException,NoSuchEncryptionException,EncryptionFailedException{
        when(readerAndWriter.isFileExits()).thenReturn(false);
        when(instanceRegCall.callToSm(any())).thenReturn(serverInstanceRegistrationRequest);
        serverInstanceRegistrationProcess.calltoSMToGetDBAndServerInstance();
        verify(readerAndWriter).writeServerInfo(serverid,servername);
   }

    private RemoteMethod createRemoteMethod(){
        RemoteMethod remoteMethod = new RemoteMethod("/integration/serverinstanceregistration/server-instance-registration/*","/registerServerInstance.json", HTTPMethodType.POST);
        remoteMethod.addArgument("serverInstanceId",serverid);
        remoteMethod.addArgument("serverName",serverInstanceReqData.getServerName());
        remoteMethod.addArgument("serverGroupName",serverInstanceReqData.getServerGroupName());
        remoteMethod.addArgument("originHost",serverInstanceReqData.getOriginHost());
        remoteMethod.addArgument("originRealm",serverInstanceReqData.getOriginRealm());
        remoteMethod.addArgument("jmxPort",String.valueOf(serverInfo.getJmxPort()));
        remoteMethod.addArgument("serverHome",serverHome);
        remoteMethod.addArgument("javaHome",System.getenv("JAVA_HOME"));
        return remoteMethod;
    }
}
