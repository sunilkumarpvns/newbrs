package com.elitecore.aaa.core.radius.service.auth.handlers;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.conf.EAPConfigurationData;
import com.elitecore.aaa.core.conf.EAPConfigurations;
import com.elitecore.aaa.core.conf.impl.EAPConfigurationDataImpl;
import com.elitecore.aaa.core.conf.impl.ServerCertificateProfileImpl;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.eap.session.EAPSessionManager;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher103DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher108DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher10DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher18DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher19DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher21DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher22DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher24DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher47DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher4DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher50DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher51DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher52DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher5DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher60DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher64DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.Cipher9DataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.EapMethodNegotiationDataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.EapSIMDataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.OUICasesDataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.PathValidationDataProvider;
import com.elitecore.aaa.core.radius.service.auth.handlers.data.provider.TlsVersionNegotiationDataProvider;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.subscriber.IAccountInfoProvider;
import com.elitecore.aaa.radius.conf.RadAuthConfiguration;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadEAPHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthenticationHandlerData;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthRequestImpl;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthResponseImpl;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.NullLogger;
import com.elitecore.core.commons.tls.CRLConfiguration;
import com.elitecore.core.commons.tls.TrustedCAConfiguration;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreeap.util.tls.TLSUtility;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;


/**
 * 
 * @author narendra.pathai
 *
 */
//FIXME NARENDRA - make sure to uncomment the providers after build process is stable 
@RunWith(JUnitParamsRunner.class)
public class RadEapHandlerTest{

	private static final String DUMMY_ID = "0";

	@Mock private EAPConfigurations eapConfigurations;
	@Mock private DriverConfigurationProvider driverConfigurationProvider;
	@Mock private DriverConfiguration driverConfiguration;
	@Mock private RadAuthConfiguration radAuthConfiguration;
	@Mock private AAAServerConfiguration aaaServerConfiguration;
	@Mock private AAAServerContext serverContext;
	@Mock private RadAuthServiceContext radAuthServiceContext;
	@Mock private RadClientData clientData;
	
	private EAPConfigurationData eapConfigurationData;
	private IAccountInfoProvider<RadServiceRequest, RadServiceResponse> accountInfoProvider;
	
	//Actual object under test
	private RadEAPHandler radEapHandler;
	
	public RadEapHandlerTest() {
		//for using the JUnit params runner
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		loadDictionary();
		LogManager.setDefaultLogger(new NullLogger());
	}

	private static void loadDictionary() {
		RadiusDictionaryTestHarness.getInstance();
	}

	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
	}
	
	public void setUp(RadEapHandlerValues radEapHandlerValues) throws Exception {
		eapConfigurationData = createEapConfigurationData(radEapHandlerValues);

		when(eapConfigurations.getEAPConfigurationDataForID(DUMMY_ID)).thenReturn(eapConfigurationData);

		when(aaaServerConfiguration.getDriverConfigurationProvider()).thenReturn(driverConfigurationProvider);
		when(driverConfigurationProvider.getDriverConfiguration(DUMMY_ID)).thenReturn(driverConfiguration);
		when(driverConfiguration.getDriverInstanceId()).thenReturn(DUMMY_ID);
		
		when(aaaServerConfiguration.getEAPConfigurations()).thenReturn(eapConfigurations);
		when(aaaServerConfiguration.getTrustedCAConfiguration()).thenReturn(new TestTrustedCAConfiguration(radEapHandlerValues));
		when(aaaServerConfiguration.getCRLConfiguration()).thenReturn(new TestCRLConfiguration());
		when(aaaServerConfiguration.getAuthConfiguration()).thenReturn(radAuthConfiguration);
		
		when(serverContext.getServerConfiguration()).thenReturn(aaaServerConfiguration);
		when(serverContext.getEapSessionManager()).thenReturn(new EAPSessionManager());
		when(serverContext.getTaskScheduler()).thenReturn(Mockito.mock(TaskScheduler.class));
		
		when(radAuthServiceContext.getAuthConfiguration()).thenReturn(radAuthConfiguration);
		when(radAuthServiceContext.getServerContext()).thenReturn(serverContext);
		
		AuthenticationHandlerData authenticationHandlerData = new AuthenticationHandlerData();
		authenticationHandlerData.setEapConfigId(DUMMY_ID);
		
		radEapHandler = new RadEAPHandler(radAuthServiceContext, authenticationHandlerData, true);
		radEapHandler.init();
		
		accountInfoProvider = new TestAccountInfoProvider(radEapHandlerValues.getAccountData());
		
		when(clientData.getClientIp()).thenReturn("127.0.0.1");
		when(clientData.getSharedSecret(anyInt())).thenReturn("secret");
	}

	private EAPConfigurationData createEapConfigurationData(RadEapHandlerValues eapHandlerValues){
		EAPConfigurationDataImpl eapConfigurationData = new EAPConfigurationDataImpl();
		eapConfigurationData.setDefaultNegotiationMethod(eapHandlerValues.getDefaultNegotiationMethod());
		List<Integer> enabledAuthMethods = eapHandlerValues.getEnabledEapMethods();
		eapConfigurationData.setEnableAuthMethods(enabledAuthMethods);
		eapConfigurationData.setMaxEapPacketSize(1024);
		
		createTLSConfigurationData(eapConfigurationData,eapHandlerValues);

		return eapConfigurationData;
	}
	
	private void createTLSConfigurationData(EAPConfigurationDataImpl eapConfigurationData,RadEapHandlerValues eapHandlerValues){
		eapConfigurationData.getTLSConfiguration().setMinProtocolVersion(eapHandlerValues.getMinProtocolVersion());
		eapConfigurationData.getTLSConfiguration().setMaxProtocolVersion(eapHandlerValues.getMaxProtocolVersion());
		eapConfigurationData.getTLSConfiguration().setIsTlsCertificateRequest(eapHandlerValues.isSendCertificateRequest());
		eapConfigurationData.setTTLSCertificateRequest(eapHandlerValues.isSendCertificateRequest());
		eapConfigurationData.setIsPEAPCertificateRequest(eapHandlerValues.isSendCertificateRequest());
		eapConfigurationData.getTLSConfiguration().setSessionResumptionDuration(1000);
		eapConfigurationData.getTLSConfiguration().setSessionResumptionLimit(1);
		eapConfigurationData.getTLSConfiguration().setDefaultCompressionMethod(0);
		
		List<Integer> cipherSuites = eapHandlerValues.getEnabledCipherSuites();
		eapConfigurationData.getTLSConfiguration().setCipherSuiteIDs(cipherSuites);
		
		
		ServerCertificateProfileImpl serverCertificateProfile = new ServerCertificateProfileImpl();
		serverCertificateProfile.setCertificateChainBytes(eapHandlerValues.getServerCertificateChainBytes());
		serverCertificateProfile.setPrivateKey(eapHandlerValues.getPrivateKey());
		
		//setting the certificate configuration
		eapConfigurationData.getTLSConfiguration().getCertificateConfiguration().setCertificateTypes(new ArrayList<Integer>(eapHandlerValues.getCertificateTypes()));
		eapConfigurationData.getTLSConfiguration().getCertificateConfiguration().setServerCertificateId(DUMMY_ID);
		eapConfigurationData.getTLSConfiguration().getCertificateConfiguration().setServerCertificateProfile(serverCertificateProfile);
		eapConfigurationData.getTLSConfiguration().getCertificateConfiguration().setVendorSpecificCertList(eapHandlerValues.getVendorSpecificCertificates());
		
	}

	//FIXME Remove this when pcap taken with the test mode flag set
	@Test
	@Parameters(source = {
			Cipher4DataProvider.class,
			Cipher5DataProvider.class,
			Cipher9DataProvider.class,
			Cipher10DataProvider.class,
			Cipher18DataProvider.class,
			Cipher19DataProvider.class,
			Cipher21DataProvider.class,
			Cipher22DataProvider.class,
			Cipher24DataProvider.class,
			Cipher47DataProvider.class,
			Cipher50DataProvider.class,
			Cipher51DataProvider.class,
			Cipher52DataProvider.class, 
			Cipher60DataProvider.class,
			Cipher64DataProvider.class,
			Cipher103DataProvider.class,
			Cipher108DataProvider.class,
			
//			Cipher109DataProvider.class,
//			Cipher107DataProvider.class,
//			Cipher106DataProvider.class,
//			Cipher53DataProvider.class,
//			Cipher56DataProvider.class,
//			Cipher57DataProvider.class,
//			Cipher58DataProvider.class,
//			Cipher61DataProvider.class,
			
			TlsVersionNegotiationDataProvider.class,
			EapMethodNegotiationDataProvider.class,
			OUICasesDataProvider.class,
			PathValidationDataProvider.class,
			
			EapSIMDataProvider.class,
			EapAKADataProvider.class
			})
	public void test(String[] clientMessages, String[] serverMessages, RadEapHandlerValues values) throws Exception{
		setUp(values);
		
		for(int i=0;i<clientMessages.length;i++){
			RadiusAuthRequestImpl radAuthRequest = new RadiusAuthRequestImpl(TLSUtility.HexToBytes(clientMessages[i]), InetAddress.getLocalHost(), 0, serverContext, new SocketDetail(InetAddress.getLocalHost().getHostAddress(), 0));
			RadiusAuthResponseImpl radAuthResponse = new RadiusAuthResponseImpl(null, 0, serverContext);
			radAuthRequest.setParameter(AAAServerConstants.DRIVER_INSTANCE_ID, DUMMY_ID);
			
			radAuthResponse.setClientData(clientData);
	
			relayClientPacketAndVerifyResponse(radAuthRequest, radAuthResponse,clientMessages[i],serverMessages[i],values);
		}
	}

	private void relayClientPacketAndVerifyResponse(RadAuthRequest radAuthRequest,RadAuthResponse radAuthResponse, String clientMessage, String serverMessage,RadEapHandlerValues values){
		//Relaying packets
		RadiusPacket testDataRadiusPacket = new RadiusPacket();
		testDataRadiusPacket.setBytes(TLSUtility.HexToBytes(serverMessage));
		
		try{
			radEapHandler.handleRequest(radAuthRequest, radAuthResponse, accountInfoProvider);

			//verification
			Collection<IRadiusAttribute> responseEAPAttributes = radAuthResponse.getRadiusAttributes(RadiusAttributeConstants.EAP_MESSAGE);
			Collection<IRadiusAttribute> testDataEAPResponseAttributes = testDataRadiusPacket.getRadiusAttributes(RadiusAttributeConstants.EAP_MESSAGE);

			Assert.assertEquals(values.getScenario(),testDataEAPResponseAttributes, responseEAPAttributes);
		}catch (AuthenticationFailedException e) {
			if(!values.isFailureExpected()){
				Assert.fail(values.getScenario() + ": Unexpected authentication failure received.");
			}
		}catch(Throwable unExpectedException){
			unExpectedException.printStackTrace();
			fail(values.getScenario() + ": Unexpected exception received: " + unExpectedException.getLocalizedMessage());
		}
	}
	
	private class TestAccountInfoProvider implements IAccountInfoProvider<RadServiceRequest, RadServiceResponse> {

		private AccountData accountData;
		
		public TestAccountInfoProvider(AccountData accountData) {
			this.accountData = accountData;
		}
		
		@Override
		public AccountData getAccountData(RadServiceRequest request, RadServiceResponse serviceResponse) {
			RadAuthRequest radAuthRequest = (RadAuthRequest) request;
			if(radAuthRequest.getRadiusAttribute(RadiusAttributeConstants.USER_NAME)
					.getStringValue().equals(accountData.getUserName())){
				return accountData;
			}
			return null;
		}

		@Override
		public AccountData getAccountData(RadServiceRequest serviceRequest, RadServiceResponse serviceResponse, String userIdentity) {
			if(userIdentity.equals(accountData.getUserIdentity())){
				return accountData;
			}
			return null;
		}
		
	}

	private class TestTrustedCAConfiguration implements TrustedCAConfiguration{
		private RadEapHandlerValues eapHandlerValues;
		public TestTrustedCAConfiguration(RadEapHandlerValues values) {
			this.eapHandlerValues = values;
		}
		
		@Override
		public List<X509Certificate> getCACertificates() {
			List<X509Certificate> certificates = new ArrayList<X509Certificate>();
			certificates.addAll(eapHandlerValues.getX509TrustedCertificate());
			return certificates;
		}
	}
	
	private class TestCRLConfiguration implements CRLConfiguration{

		@Override
		public List<X509CRL> getCRLs() {
			return Collections.emptyList();
		}

		@Override
		public boolean getOCSPEnabled() {
			return false;
		}

		@Override
		public String getOCSPURL() {
			return null;
		}
	}
}

