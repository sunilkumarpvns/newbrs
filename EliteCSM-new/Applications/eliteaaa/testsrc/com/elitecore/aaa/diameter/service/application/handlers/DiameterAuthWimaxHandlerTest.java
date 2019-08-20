package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.core.authprotocol.exception.AuthorizationFailedException;
import com.elitecore.aaa.core.conf.impl.DHCPKeysConfigurable;
import com.elitecore.aaa.core.conf.impl.DummyAAAServerConfigurationImpl;
import com.elitecore.aaa.core.conf.impl.SPIKeyConfigurable;
import com.elitecore.aaa.core.conf.impl.WimaxConfigurable;
import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.eap.session.EAPSessionManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.wimax.WimaxRequest;
import com.elitecore.aaa.core.wimax.WimaxResponse;
import com.elitecore.aaa.core.wimax.WimaxSessionManager;
import com.elitecore.aaa.core.wimax.keys.DhcpInMemoryKeyManager;
import com.elitecore.aaa.core.wimax.keys.HAInMemoryKeyManager;
import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.aaa.core.wimax.keys.KeyManagerImpl;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.coreeap.fsm.eap.IEapStateMachine;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import static com.elitecore.core.serverx.sessionx.inmemory.ISession.NO_SESSION;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class DiameterAuthWimaxHandlerTest {
	private static final String MSK = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.MSK.getIntValue();
	private static final String AAA_SESSION_ID = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.AAA_SESSION_ID.getIntValue();
	private static final String WIMAX_CAPABILITY = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue();
	private static final String HA_IP = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.HA_IP_MIP4.getIntValue();
	private static final String HA_RK = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.HA_RK_KEY.getIntValue();
	private static final String HA_RK_SPI = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.HA_RK_SPI.getIntValue();
	private static final String HA_RK_LIFETIME = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.HA_RK_LIFETIME.getIntValue();
	private static final String MN_HA_MIP4_KEY = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.MN_HA_MIP4_KEY.getIntValue();
	private static final String MN_HA_MIP4_SPI = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue();
	private static final String FA_RK = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.FA_RK_KEY.getIntValue();
	private static final String FA_RK_SPI = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.FA_RK_SPI.getIntValue();
	private static final String DHCP_RK = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.DHCP_RK.getIntValue();
	private static final String DHCP_RK_KEY_ID = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue();
	private static final String DHCP_RK_LIFETIME = String.valueOf(DiameterConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.DHCP_RK_LIFETIME.getIntValue();

	private static final String CALLING_STATION_ID_VALUE = "0A-0B-0C-0D-0E";
	private static final String TEST_USER = "TEST_USER";
	private static final String EAP_SESSION_ID = "EAP_SESSION_ID";
	private static final String EAP_IDENTITY = "EAP_IDENTITY";
	private static final String EAP_KEY_DATA = "EAP_KEY_DATA";

	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();

	@Mock private AAAServerContext serverContext;
	@Mock private RadAuthServiceContext serviceContext;
	@Mock private EAPSessionManager eapSessionManager;
	private KeyManager keyManager;
	@Mock private IEapStateMachine eapSession;

	private WimaxConfigurable wimaxConfiguration;
	private WimaxSessionManager wimaxSessionManager;
	private DiameterRequest diameterRequest;
	private ApplicationRequest request;
	private ApplicationResponse response;
	private DiameterAuthWimaxHandler<ApplicationRequest, ApplicationResponse> handler;

	@BeforeClass
	public static void loadDictionary() {
		DummyDiameterDictionary.getInstance();

	}

	@Before
	public void setUp() throws FileNotFoundException, UnsupportedEncodingException, JAXBException {
		MockitoAnnotations.initMocks(this);
		wimaxConfiguration = new WimaxConfigurable();
		wimaxSessionManager = new WimaxSessionManager();
		keyManager = new KeyManagerImpl(serverContext, new HAInMemoryKeyManager(wimaxConfiguration), new DhcpInMemoryKeyManager(new DHCPKeysConfigurable(), wimaxConfiguration));
		handler = spy(new DiameterAuthWimaxHandler<ApplicationRequest, ApplicationResponse>(
				null, wimaxConfiguration, createSpiKeyConfiguration(), wimaxSessionManager,
				eapSessionManager, keyManager));
		diameterRequest = new DiameterRequest();
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);
		response.addAVP(DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE));
		trainMocks();
	}

	private SPIKeyConfigurable createSpiKeyConfiguration() throws FileNotFoundException, UnsupportedEncodingException, JAXBException {
		SPIKeyConfigurable spiKeyConfigurable = ConfigUtil.deserialize(new File(ClasspathResource.at("wimax/spi-keys.xml").getAbsolutePath()), SPIKeyConfigurable.class);
		spiKeyConfigurable.postReadProcessing();
		return spiKeyConfigurable;
	}

	private void trainMocks() {
		DummyAAAServerConfigurationImpl serverConfiguration = new DummyAAAServerConfigurationImpl();
		serverConfiguration.setWimaxConfiguration(wimaxConfiguration);
		when(serviceContext.getServerContext()).thenReturn(serverContext);
		when(serverContext.getServerConfiguration()).thenReturn(serverConfiguration);
		when(serverContext.getWimaxSessionManager()).thenReturn(wimaxSessionManager);
		when(serverContext.getEapSessionManager()).thenReturn(eapSessionManager);
		when(eapSessionManager.getEAPStateMachine(Mockito.anyString())).thenReturn(eapSession);
	}

	public class Eligibility {

		@Test
		public void handlerIsAlwaysEligibleToBeApplied() {
			assertTrue(handler.isEligible(Mockito.any(ApplicationRequest.class), Mockito.any(ApplicationResponse.class)));
		}

		@Test
		public void isEligibleIfWimaxCapabilityAttributeIsPresentInRequest() throws AuthorizationFailedException {
			addWimaxCapability(request);

			handler.handleRequest(request, response,NO_SESSION);

			Mockito.verify(handler).handleWimaxRequest(Mockito.any(WimaxRequest.class),
					Mockito.any(WimaxResponse.class));
		}

		@Test
		public void isEligibleIfPPAQAttributeIsPresentInRequest() throws AuthorizationFailedException {
			addPPAQ();

			handler.handleRequest(request, response, NO_SESSION);

			Mockito.verify(handler).handleWimaxRequest(Mockito.any(WimaxRequest.class),
					Mockito.any(WimaxResponse.class));
		}

		@Test
		public void isEligibleIfPPACAttributeIsPresentInRequest() throws AuthorizationFailedException {
			addPPAC();

			handler.handleRequest(request, response, NO_SESSION);

			Mockito.verify(handler).handleWimaxRequest(Mockito.any(WimaxRequest.class),
					Mockito.any(WimaxResponse.class));
		}

		@Test
		public void isEligibleIfDhcpRkKeyIdAttributeIsPresentInRequest() throws AuthorizationFailedException {
			addDhcpRkKeyId(request);

			handler.handleRequest(request, response, NO_SESSION);

			Mockito.verify(handler).handleWimaxRequest(Mockito.any(WimaxRequest.class),
					Mockito.any(WimaxResponse.class));
		}

		@Test
		public void isNotEligibleIfNoneOfEligibilityAttributesArePresentInRequest() throws AuthorizationFailedException {
			DiameterAssertion.assertThat(request.getDiameterRequest()).doesNotContainAVP(
					DiameterConstants.WIMAX_VENDOR_ID + ":" +WimaxAttrConstants.WIMAX_CAPABILITY);

			DiameterAssertion.assertThat(request.getDiameterRequest()).doesNotContainAVP(
					DiameterConstants.WIMAX_VENDOR_ID + ":" +WimaxAttrConstants.PPAC);

			DiameterAssertion.assertThat(request.getDiameterRequest()).doesNotContainAVP(
					DiameterConstants.WIMAX_VENDOR_ID + ":" +WimaxAttrConstants.PPAQ);

			handler.handleRequest(request, response, NO_SESSION);

			Mockito.verify(handler, Mockito.never()).handleWimaxRequest(
					Mockito.any(WimaxRequest.class), Mockito.any(WimaxResponse.class));
		}
	}

	@Test
	public void skipsWimaxHandlingIfResultCodeIsAbsentFromResponse() throws AuthorizationFailedException {
		addWimaxCapability(request);
		response.getDiameterAnswer().removeAVP(response.getAVP(DiameterAVPConstants.RESULT_CODE));
		
		handler.handleRequest(request, response, NO_SESSION);
		
		Mockito.verify(handler, Mockito.never()).handleWimaxRequest(
				Mockito.any(WimaxRequest.class), Mockito.any(WimaxResponse.class));
	}

	@Test
	public void rejectsRequestInCaseOfAuthorizationFailedException() throws InvalidAttributeIdException, AuthorizationFailedException {
		addWimaxCapability(request);
		addPPAQ();
		addPPAC();
		addUsername(request);

		doThrow(AuthorizationFailedException.class).when(handler).handleWimaxRequest(Mockito.any(WimaxRequest.class),
				Mockito.any(WimaxResponse.class));

		handler.handleRequest(request, response, NO_SESSION);

		DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED);
		DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DiameterAVPConstants.ERROR_MESSAGE,
				AuthReplyMessageConstant.WIMAX_PROCESSING_FAILED);
	}

	@Test
	public void skipsProcessingInCaseOfAccessChallengeRequests() throws InvalidAttributeIdException, AuthorizationFailedException {
		addUsername(request);

		IDiameterAVP multiRoundAuthResultCode = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
		multiRoundAuthResultCode.setInteger(ResultCode.DIAMETER_MULTI_ROUND_AUTH.code);

		ApplicationResponse expectedResponse = new ApplicationResponse(diameterRequest);

		expectedResponse.addAVP(multiRoundAuthResultCode);

		ApplicationResponse response = new ApplicationResponse(diameterRequest);

		response.addAVP(multiRoundAuthResultCode);

		handler.handleRequest(request, response, NO_SESSION);

		Mockito.verify(handler, Mockito.never()).
		handleWimaxRequest(Mockito.any(WimaxRequest.class), Mockito.any(WimaxResponse.class));
	}

	public class InitialAuthRequest {

		@Before
		public void setUp() throws InvalidAttributeIdException {
			MockitoAnnotations.initMocks(this);
			addWimaxCapability(request);
			addPPAQ();
			addPPAC();
			addUsername(request);
		}

		@Test
		public void rejectsIfCallingStationIdIsNotPresentInRequest() {
			DiameterAssertion.assertThat(request.getDiameterRequest()).
			doesNotContainAVP(DiameterAVPConstants.CALLING_STATION_ID);

			handler.handleRequest(request, response, NO_SESSION);

			DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED);
			DiameterAssertion.assertThat(response.getDiameterAnswer()).
			containsAVP(DiameterAVPConstants.ERROR_MESSAGE, "Calling station id not found, mandatory for WiMAX authentication.");
		}

		public class NonEAPRequest {

			@Before
			public void setUp() throws InvalidAttributeIdException {
				addCallingStationId(request);
			}

			@Test
			public void addsOnlyMandatoryWimaxAttributes() throws InvalidAttributeIdException {
				setPeerData(response);

				handler.handleRequest(request, response, NO_SESSION);

				DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(WIMAX_CAPABILITY);
			}
		}

		public class EAPRequest {

			@Before
			public void setUp() throws InvalidAttributeIdException {
				addCallingStationId(request);
				addEAPMessage(request);
				addEAPIdentity(request);
				addEAPSessionId(request);
				addMSK(request);
				addSessionTimeout(response);
				addEapSession(request);
				setPeerData(request);
			}

			public class TlsOrTtlsMethod {

				@Before
				public void setUp() {
					when(eapSession.getCurrentMethod()).thenReturn(EapTypeConstants.TTLS.typeId);
				}

				public class EapSessionExists {

					@Before
					public void setUp() {
						when(eapSessionManager.isSessionExist(Mockito.anyString())).thenReturn(true);
					}

					@Test
					public void addsMandatoryWimaxAttributesInResponse() throws InvalidAttributeIdException {
						handler.handleRequest(request, response, NO_SESSION);

						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(WIMAX_CAPABILITY);
					}

					@Test
					public void updatesWimaxCapabilityAttributeInResponseIfAlreadyPresent() {
						addWimaxCapability(response);

						handler.handleRequest(request, response, NO_SESSION);

						assertResponseContainsMandatoryWimaxAttributes();
					}

					@Test
					public void addsHAKeyAttributesInResponse() throws InvalidAttributeIdException {
						handler.handleRequest(request, response, NO_SESSION);

						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_IP);
						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_RK);
						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_RK_SPI);
						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_RK_LIFETIME);
						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(MN_HA_MIP4_KEY);
						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(MN_HA_MIP4_SPI);
					}

					@Test
					public void addsFAKeyAttributesInResponse() throws InvalidAttributeIdException {
						handler.handleRequest(request, response, NO_SESSION);

						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(FA_RK);
						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(FA_RK_SPI);
					}

					@Test
					public void doesNotAddMskInAnswer() {
						
						 /*code performing the expected action has been commented out in 
						 addAuthOnlyAndEapRequestAttributes() method of DiameterAuthWimaxHandler.*/

						handler.handleRequest(request, response, NO_SESSION);

						DiameterAssertion.assertThat(response.getDiameterAnswer()).doesNotContainAVP(MSK);
					}

					@Test
					public void addsAAASessionIdInAnswer() {
						handler.handleRequest(request, response, NO_SESSION);
						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(AAA_SESSION_ID);
					}

					@Test
					public void addsDHCPKeysIfDHCPIpIsPresentInResponse() {
						addDhcpIp(response);

						handler.handleRequest(request, response, NO_SESSION);

						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DHCP_RK);
						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DHCP_RK_KEY_ID);
						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DHCP_RK_LIFETIME);
					}
				}

				public class EapSessionDoesNotExist {

					@Test
					public void addsMandatoryWimaxAttributes() {
						handler.handleRequest(request, response, NO_SESSION);

						DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(WIMAX_CAPABILITY);
					}
				}
			}

			public class NonTTLSorTLSMethod {

				@Before
				public void setUp() {
					when(eapSession.getCurrentMethod()).thenReturn(EapTypeConstants.AKA.typeId);
				}

				@Test
				public void addsMandatoryWimaxAttributes() {
					handler.handleRequest(request, response, NO_SESSION);

					DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(WIMAX_CAPABILITY);
				}
			}
		}
	}


	public class HARequest {

		@Before
		public void setUp() throws InvalidAttributeIdException {
			MockitoAnnotations.initMocks(this);
			addUsername(request);
			addCallingStationId(request);
		}

		public class EAPRequest {

			@Before
			public void setUp() throws InvalidAttributeIdException {
				addWimaxCapability(request);
				addEAPMessage(request);
				addEAPIdentity(request);
				addEAPSessionId(request);
				addMSK(request);

				addSessionTimeout(response);
				setPeerData(request);
			}

			public class WimaxSessionDoesNotExist {

				@Test
				public void rejectsTheRequestBeacuseWimaxSessionMustBePresent() throws InvalidAttributeIdException {
					request.getDiameterRequest().addAvp(MN_HA_MIP4_SPI, "0");

					handler.handleRequest(request, response, NO_SESSION);

					DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED);
					DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(
											DiameterAVPConstants.ERROR_MESSAGE_STR, 
											AuthReplyMessageConstant.WIMAX_SESSION_NOT_FOUND_FOR_HA);
				}
			}

			public class WimaxSessionExists {

				private static final int ANY_VALUE = 100;
				private InitialRequestAuthenticator initialRequestAuthenticator;

				@Before
				public void setUp() throws InvalidAttributeIdException {
					initialRequestAuthenticator = new InitialRequestAuthenticator();
					initialRequestAuthenticator.authenticate();
					request.getDiameterRequest().addAvp(MN_HA_MIP4_SPI, "0");
				}

				@Test
				public void addsOnlyMN_HAKeysIfHaRkSpiIsNotReceived() throws InvalidAttributeIdException {
					addSessionTimeout(response);
					setPeerData(request);

					handler.handleRequest(request, response, NO_SESSION);

					DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(MN_HA_MIP4_KEY);
					DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(MN_HA_MIP4_SPI);
				}

				@Test
				public void addsHaRkKeysIfHaRkSpiReceived() throws InvalidAttributeIdException {
					IDiameterAVP haRkSpi = wimaxAttribute(WimaxAttrConstants.HA_RK_SPI);
					haRkSpi.setInteger(initialRequestAuthenticator.receivedHaRkSpi);
					request.getDiameterRequest().addAvp(haRkSpi);
					
					addSessionTimeout(response);
					setPeerData(request);

					handler.handleRequest(request, response, NO_SESSION);
					
					DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_RK);
					DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_RK_SPI);
					DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_RK_LIFETIME);
				}

				@Test
				public void addsHaRkKeysEvenIfHaRkSpiReceivedIsUnknown() throws InvalidAttributeIdException {
					addHaRkSpi(ANY_VALUE);

					addSessionTimeout(response);
					setPeerData(request);

					handler.handleRequest(request, response,NO_SESSION);

					DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_RK);
					DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_RK_SPI);
					DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_RK_LIFETIME);
				}

				private void addHaRkSpi(int anyValue) {
					IDiameterAVP haRkSpi = wimaxAttribute(WimaxAttrConstants.HA_RK_SPI);
					haRkSpi.setInteger(anyValue);
					request.getDiameterRequest().addAvp(haRkSpi);
				}
			}
		}
	}

	public class DhcpRequest {

		private InitialRequestAuthenticator initialReqAuthenticator;

		@Before
		public void setUp() throws InvalidAttributeIdException {
			initialReqAuthenticator = new InitialRequestAuthenticator();
			initialReqAuthenticator.authenticate();
			addUsername(request);
			addCallingStationId(request);
			addDhcpRkKeyId(request);
			addNasIp(request);
			addSessionTimeout(response);
			setPeerData(request);
			setPeerData(response);
		}

		@Test
		public void rejectsRequestIfNoNasIpAddressIsPresentInRequest() {
			request.getDiameterRequest().removeAVP(request.getAVP(DiameterAVPConstants.NAS_IP_ADDRESS));

			handler.handleRequest(request, response, NO_SESSION);

			DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED);
			DiameterAssertion.assertThat(response.getDiameterAnswer()).
				containsAVP(DiameterAVPConstants.ERROR_MESSAGE,
						"No NAS-IP-ADDRESS is present in the request which is mandatory for DHCP-Request so sending Access-Reject.");
		}

		@Test
		public void rejectsRequestIfNoKeysAreCached() {
			handler.handleRequest(request, response,NO_SESSION);

			DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED);
			
			
			DiameterAssertion.assertThat(response.getDiameterAnswer()).
			containsAVP(DiameterAVPConstants.ERROR_MESSAGE, "No active keys found for DHCP Server");
		}

		@Test
		public void addsDhcpKeysInResponseIfKeysMappedWithDhcpRkKeyIdArePresent() {
			request.getAVP(DiameterConstants.WIMAX_VENDOR_ID + ":" + WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue()).setInteger(initialReqAuthenticator.receivedDhcpRkKeyId);
			handler.handleRequest(request, response, NO_SESSION);

			DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_SUCCESS);
			DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DHCP_RK);
			DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DHCP_RK_KEY_ID);
			DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DHCP_RK_LIFETIME);
		}
	}

	public class AuthorizeOnlyRequest {

		private InitialRequestAuthenticator initialRequestAuthenticator;

		@Before
		public void setUp() throws InvalidAttributeIdException {
			initialRequestAuthenticator = new InitialRequestAuthenticator();
			
			addUsername(request);
			addCallingStationId(request);
			request.getDiameterRequest().addAvp(DiameterAVPConstants.SERVICE_TYPE, "17");
			addSessionTimeout(response);
			setPeerData(request);
			addWimaxCapability(request);
		}

		@Test
		public void addsMandatoryWimaxAttributesToResponseIfWimaxSessionExists() throws InvalidAttributeIdException {
			initialRequestAuthenticator.authenticate();

			handler.handleRequest(request, response, NO_SESSION);
			
			DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_SUCCESS);
			DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(WIMAX_CAPABILITY);
		}

		@Test
		public void addsMandatoryWimaxAttributesToResponseIfWimaxSessionDoesNotExist() throws InvalidAttributeIdException {
			handler.handleRequest(request, response, NO_SESSION);

			DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_SUCCESS);
			assertResponseContainsMandatoryWimaxAttributes();
		}
	}

	public class AuthenticateOnlyRequest {

		private InitialRequestAuthenticator initialRequestAuthenticator;

		@Before
		public void setUp() throws InvalidAttributeIdException {
			initialRequestAuthenticator = new InitialRequestAuthenticator();
			addWimaxCapability(request);
			addUsername(request);
			addCallingStationId(request);
			addMSK(request);
			addEAPIdentity(request);
			addEAPSessionId(request);
			addEAPMessage(request);

			request.getDiameterRequest().addAvp(DiameterAVPConstants.SERVICE_TYPE, "8");
			addSessionTimeout(response);
			setPeerData(request);
		}

		public class EapSessionExists {

			@Before
			public void setUp() throws InvalidAttributeIdException {
				initialRequestAuthenticator.authenticate();
				addEapSession(request);
				when(eapSessionManager.isSessionExist(Mockito.anyString())).thenReturn(true);
			}

			@Test
			public void addsMandatoryWimaxAttributesToResponse() throws InvalidAttributeIdException {
				handler.handleRequest(request, response, NO_SESSION);

				assertResponseContainsMandatoryWimaxAttributes();
			}

			@Test
			public void addsHaKeyAttributesInResponse() {
				handler.handleRequest(request, response, NO_SESSION);

				DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_IP);
				DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_RK);
				DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_RK_SPI);
				DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(HA_RK_LIFETIME);
				DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(MN_HA_MIP4_KEY);
				DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(MN_HA_MIP4_SPI);
			}

			@Test
			public void addsFAKeyAttributesInResponse() throws InvalidAttributeIdException {
				handler.handleRequest(request, response, NO_SESSION);

				DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(FA_RK);
				DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(FA_RK_SPI);
			}

			@Test
			public void doesNotAddMskInAnswer() {
				/*code performing the expected action has been commented out in 
				addAuthOnlyAndEapRequestAttributes() method of DiameterAuthWimaxHandler.*/

				
				handler.handleRequest(request, response, NO_SESSION);
				DiameterAssertion.assertThat(response.getDiameterAnswer()).doesNotContainAVP(MSK);
			}

			@Test
			public void addsAAASessionIdInAnswer() {
				handler.handleRequest(request, response, NO_SESSION);
				DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(AAA_SESSION_ID);
			}

			@Test
			public void addsDHCPKeysIfDHCPIpIsPresentInResponse() {
				addDhcpIp(response);
				handler.handleRequest(request, response, NO_SESSION);

				DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DHCP_RK);
				DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DHCP_RK_KEY_ID);
				DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DHCP_RK_LIFETIME);
			}
		}

		public class EapSessionDoesNotExist {
			@Rule public ExpectedException exception = ExpectedException.none();

			@Test
			public void shouldOnlyAddMandatoryWimaxAttributesButFailsWithNPEWhileAddingHaKeysButTheyDontExist() {
				exception.expect(NullPointerException.class);

				handler.handleRequest(request, response, NO_SESSION);

				DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_SUCCESS);
				assertResponseContainsMandatoryWimaxAttributes();
			}
		}
	}

	private void addNasIp(ApplicationRequest request) {
		request.getDiameterRequest().addAvp(DiameterAVPConstants.NAS_IP_ADDRESS, "0.0.0.0");
	}

	private void addDhcpIp(ApplicationResponse response) {
		IDiameterAVP dhcpIP = wimaxAttribute(WimaxAttrConstants.DHCPV4_SERVER);
		dhcpIP.setStringValue("0.0.0.0");
		response.addAVP(dhcpIP);
	}

	private void addSessionTimeout(ApplicationResponse response) throws InvalidAttributeIdException {
		response.getDiameterAnswer().addAvp(DiameterAVPConstants.SESSION_TIMEOUT, 600);
	}

	private void addMSK(ApplicationRequest request) {
		request.getDiameterRequest().setParameter(EAP_KEY_DATA, new byte[128]);
	}

	private void addEapSession(ApplicationRequest request) {
		request.getDiameterRequest().setParameter("EAP_SESSION", eapSession);

	}

	private void setPeerData(ApplicationResponse response) {

		PeerData peerData = mock(PeerData.class);
		when(peerData.getDHCPAddress()).thenReturn("0.0.0.0");
		when(peerData.getHAAddress()).thenReturn("0.0.0.0");
		response.getDiameterAnswer().setPeerData(peerData);
	}

	private void setPeerData(ApplicationRequest request) {
		PeerData peerData = mock(PeerData.class);
		when(peerData.getDHCPAddress()).thenReturn("0.0.0.0");
		when(peerData.getHAAddress()).thenReturn("0.0.0.0");
		request.getDiameterRequest().setPeerData(peerData);
	}

	private void addEAPSessionId(ApplicationRequest request) {
		request.getDiameterRequest().setParameter(EAP_SESSION_ID, "FakeEapSession");
	}

	private void addEAPIdentity(ApplicationRequest request) {
		request.getDiameterRequest().setParameter(EAP_IDENTITY, TEST_USER);
	}

	private void addCallingStationId(ApplicationRequest request) throws InvalidAttributeIdException {
		request.getDiameterRequest().addAvp(DiameterAVPConstants.CALLING_STATION_ID, CALLING_STATION_ID_VALUE);
	}

	private void addEAPMessage(ApplicationRequest request) throws InvalidAttributeIdException {
		request.getDiameterRequest().addAvp(DiameterAVPConstants.EAP_PAYLOAD, "0x2423423423434234");
	}

	private void addUsername(ApplicationRequest request) throws InvalidAttributeIdException {
		request.getDiameterRequest().addAvp(DiameterAVPConstants.USER_NAME, TEST_USER);
	}

	private void addDhcpRkKeyId(ApplicationRequest request) {
		IDiameterAVP dhcpRkKeyId = wimaxAttribute(WimaxAttrConstants.DHCP_RK_KEY_ID);
		dhcpRkKeyId.setInteger(1);
		request.getDiameterRequest().addAvp(dhcpRkKeyId);
	}

	private void addPPAC() {
		request.getDiameterRequest().addAvp(wimaxAttribute(WimaxAttrConstants.PPAC));
	}

	private void addPPAQ() {
		request.getDiameterRequest().addAvp(wimaxAttribute(WimaxAttrConstants.PPAQ));
	}

	private void addWimaxCapability(ApplicationRequest request) {
		request.getDiameterRequest().addAvp(wimaxAttribute(WimaxAttrConstants.WIMAX_CAPABILITY));
	}

	private void addWimaxCapability(ApplicationResponse response) {
		response.getDiameterAnswer().addAvp(wimaxAttribute(WimaxAttrConstants.WIMAX_CAPABILITY));
	}

	private IDiameterAVP wimaxAttribute(WimaxAttrConstants attribute) {
		IDiameterAVP wimaxAttribute = DiameterDictionary.getInstance().getAttribute(DiameterConstants.WIMAX_VENDOR_ID, attribute.getIntValue());
		return wimaxAttribute;
	}

	private class InitialRequestAuthenticator {

		DiameterRequest diameterRequest = new DiameterRequest();
		ApplicationRequest request = new ApplicationRequest(diameterRequest);
		ApplicationResponse response = new ApplicationResponse(diameterRequest);
		
		private long receivedDhcpRkKeyId;
		private long receivedHaRkSpi;

		void authenticate() throws InvalidAttributeIdException {
			response.addAVP(DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE));
			addWimaxCapability(request);
			addUsername(request);
			addCallingStationId(request);
			addEAPMessage(request);
			addEAPIdentity(request);
			addEAPSessionId(request);
			addMSK(request);
			addSessionTimeout(response);
			
			setPeerData(request);
			
			addDhcpIp(response);
			
			addEapSession(request);
			
			when(eapSession.getCurrentMethod()).thenReturn(EapTypeConstants.TTLS.typeId);
			when(eapSessionManager.isSessionExist(Mockito.anyString())).thenReturn(true);

			handler.handleRequest(request, response, NO_SESSION);

			receivedDhcpRkKeyId = response.getAVP(DiameterConstants.WIMAX_VENDOR_ID + ":" + WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue()).getInteger();
			receivedHaRkSpi = response.getAVP(DiameterConstants.WIMAX_VENDOR_ID + ":" + WimaxAttrConstants.HA_RK_SPI.getIntValue()).getInteger();
		}
	}

	private void assertResponseContainsMandatoryWimaxAttributes() {
		DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(WIMAX_CAPABILITY);
	}
}
