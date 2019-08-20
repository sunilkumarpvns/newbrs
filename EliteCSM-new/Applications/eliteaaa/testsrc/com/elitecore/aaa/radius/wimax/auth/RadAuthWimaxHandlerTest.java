package com.elitecore.aaa.radius.wimax.auth;

import static com.elitecore.aaa.EliteAAAMatchers.RadServiceResponseMatchers.acceptMessage;
import static com.elitecore.aaa.EliteAAAMatchers.RadServiceResponseMatchers.containsAttribute;
import static com.elitecore.aaa.EliteAAAMatchers.RadServiceResponseMatchers.rejectMessage;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

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

import com.elitecore.aaa.core.authprotocol.exception.AuthorizationFailedException;
import com.elitecore.aaa.core.conf.impl.DHCPKeysConfigurable;
import com.elitecore.aaa.core.conf.impl.DummyAAAServerConfigurationImpl;
import com.elitecore.aaa.core.conf.impl.SPIKeyConfigurable;
import com.elitecore.aaa.core.conf.impl.WimaxConfigurable;
import com.elitecore.aaa.core.conf.impl.SPIKeyConfigurable.SPIGroupDetail;
import com.elitecore.aaa.core.eap.session.EAPSessionManager;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.wimax.WimaxRequest;
import com.elitecore.aaa.core.wimax.WimaxResponse;
import com.elitecore.aaa.core.wimax.WimaxSessionManager;
import com.elitecore.aaa.core.wimax.keys.DhcpInMemoryKeyManager;
import com.elitecore.aaa.core.wimax.keys.HAInMemoryKeyManager;
import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.aaa.core.wimax.keys.KeyManagerImpl;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.coreeap.fsm.eap.IEapStateMachine;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.WimaxGroupedAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.PPAC;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.PPAQ;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.WimaxCapability;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class RadAuthWimaxHandlerTest {
	private static final String MSK = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.MSK.getIntValue();
	private static final String AAA_SESSION_ID = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.AAA_SESSION_ID.getIntValue();
	private static final String WIMAX_CAPABILITY = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue();
	private static final String WIMAX_DNS = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.DNS.getIntValue();
	private static final String HA_IP = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.HA_IP_MIP4.getIntValue();
	private static final String HA_RK = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.HA_RK_KEY.getIntValue();
	private static final String HA_RK_SPI = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.HA_RK_SPI.getIntValue();
	private static final String HA_RK_LIFETIME = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.HA_RK_LIFETIME.getIntValue();
	private static final String MN_HA_MIP4_KEY = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.MN_HA_MIP4_KEY.getIntValue();
	private static final String MN_HA_MIP4_SPI = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue();
	private static final String FA_RK = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.FA_RK_KEY.getIntValue();
	private static final String FA_RK_SPI = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.FA_RK_SPI.getIntValue();
	private static final String DHCP_RK = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.DHCP_RK.getIntValue();
	private static final String DHCP_RK_KEY_ID = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue();
	private static final String DHCP_RK_LIFETIME = String.valueOf(RadiusConstants.WIMAX_VENDOR_ID) + ":" + WimaxAttrConstants.DHCP_RK_LIFETIME.getIntValue();
	
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
	private RadAuthRequest request;
	private RadAuthResponse response;
	private RadAuthRequestBuilder requestBuilder;
	private RadAuthWimaxHandler handler;
	
	@BeforeClass
	public static void loadDictionary() {
		RadiusDictionaryTestHarness.getInstance();
	}
	
	@Before
	public void setUp() throws FileNotFoundException, UnsupportedEncodingException, JAXBException {
		MockitoAnnotations.initMocks(this);
		wimaxConfiguration = new WimaxConfigurable();
		wimaxSessionManager = new WimaxSessionManager();
		keyManager = new KeyManagerImpl(serverContext, new HAInMemoryKeyManager(wimaxConfiguration), new DhcpInMemoryKeyManager(new DHCPKeysConfigurable(), wimaxConfiguration));
		requestBuilder = new RadAuthRequestBuilder(serverContext).packetType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
		handler = spy(new RadAuthWimaxHandler(null, wimaxConfiguration, createSpiKeyConfiguration(), wimaxSessionManager, eapSessionManager, keyManager));
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
		public void isEligibleIfWimaxCapabilityAttributeIsPresentInRequest() {
			addWimaxCapability(requestBuilder);
			
			assertTrue(handler.isEligible(requestBuilder.build()));
		}
		
		@Test
		public void isEligibleIfPPAQAttributeIsPresentInRequest() {
			addPPAQ();
			
			assertTrue(handler.isEligible(requestBuilder.build()));
		}
		
		@Test
		public void isEligibleIfPPACAttributeIsPresentInRequest() {
			addPPAC();
			
			assertTrue(handler.isEligible(requestBuilder.build()));
		}
		
		@Test
		public void isEligibleIfDhcpRkKeyIdAttributeIsPresentInRequest() {
			addDhcpRkKeyId();
			
			assertTrue(handler.isEligible(requestBuilder.build()));
		}
		
		@Test
		public void isNotEligibleIfNoneOfEligibilityAttributesArePresentInRequest() {
			assertFalse(handler.isEligible(requestBuilder.build()));
		}
	}
	
	@Test
	public void rejectsRequestInCaseOfAuthorizationFailedException() throws InvalidAttributeIdException, AuthorizationFailedException {
		addWimaxCapability(requestBuilder);
		addPPAQ();
		addPPAC();
		addUsername(requestBuilder);
		
		RadAuthRequest request = requestBuilder.build();
		RadAuthResponse response = requestBuilder.buildResponse();
		
		doThrow(AuthorizationFailedException.class).when(handler).handleWimaxRequest(Mockito.any(WimaxRequest.class),
				Mockito.any(WimaxResponse.class));
		
		handler.handleRequest(request, response);
		
		assertThat(response, is(rejectMessage()));
		assertThat(response, containsAttribute(RadiusAttributeConstants.REPLY_MESSAGE_STR, 
				AuthReplyMessageConstant.WIMAX_PROCESSING_FAILED));
	}
	
	@Test
	public void skipsProcessingInCaseOfAccessChallengeRequests() throws InvalidAttributeIdException, AuthorizationFailedException {
		addUsername(requestBuilder);
		
		RadAuthRequest request = requestBuilder.build();
		RadAuthResponse expectedResponse = requestBuilder.buildResponse();
		expectedResponse.setPacketType(RadiusConstants.ACCESS_CHALLENGE_MESSAGE);
		RadAuthResponse response = requestBuilder.buildResponse();
		response.setPacketType(RadiusConstants.ACCESS_CHALLENGE_MESSAGE);
		
		handler.handleRequest(request, response);
		
		ReflectionAssert.assertReflectionEquals(expectedResponse, response);
	}
	
	public class InitialAuthRequest {
		
		@Before
		public void setUp() throws InvalidAttributeIdException {
			MockitoAnnotations.initMocks(this);
			addWimaxCapability(requestBuilder);
			addPPAQ();
			addPPAC();
			addUsername(requestBuilder);
		}
		
		@Test
		public void rejectsIfCallingStationIdIsNotPresentInRequest() {
			RadAuthRequest request = requestBuilder.build();
			RadAuthResponse response = requestBuilder.buildResponse();
			
			handler.handleRequest(request, response);
			
			assertThat(response, is(rejectMessage()));
			assertThat(response, containsAttribute(RadiusAttributeConstants.REPLY_MESSAGE_STR, 
					"Calling station id not found, mandatory for WiMAX authentication."));
		}
		
		public class NonEAPRequest {
			
			@Before
			public void setUp() throws InvalidAttributeIdException {
				addCallingStationId(requestBuilder);
			}

			@Test
			public void addsOnlyMandatoryWimaxAttributes() throws InvalidAttributeIdException {
				RadAuthRequest request = requestBuilder.build();
				RadAuthResponse response = requestBuilder.buildResponse();
				setClientData(response);
				
				handler.handleRequest(request, response);
				
				assertThat(response, containsAttribute(WIMAX_CAPABILITY));
				assertThat(response, containsAttribute(WIMAX_DNS));
			}
		}
		
		public class EAPRequest {
			
			@Before
			public void setUp() throws InvalidAttributeIdException {
				addCallingStationId(requestBuilder);
				addEAPMessage(requestBuilder);
				addEAPIdentity(requestBuilder);
				addEAPSessionId(requestBuilder);
				addMSK(requestBuilder);
				request = requestBuilder.build();
				response = requestBuilder.buildResponse();
				addSessionTimeout(response);
				setClientData(response);
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
						handler.handleRequest(request, response);
						
						assertThat(response, is(acceptMessage()));
						assertResponseContainsMandatoryWimaxAttributes();
					}
					
					@Test
					public void updatesWimaxCapabilityAttributeInResponseIfAlreadyPresent() {
						addWimaxCapability(response);
						
						handler.handleRequest(request, response);
						
						assertResponseContainsMandatoryWimaxAttributes();
					}
					
					@Test
					public void addsHAKeyAttributesInResponse() throws InvalidAttributeIdException {
						handler.handleRequest(request, response);
						
						assertThat(response, containsAttribute(HA_IP));
						assertThat(response, containsAttribute(HA_RK));
						assertThat(response, containsAttribute(HA_RK_SPI));
						assertThat(response, containsAttribute(HA_RK_LIFETIME));
						assertThat(response, containsAttribute(MN_HA_MIP4_KEY));
						assertThat(response, containsAttribute(MN_HA_MIP4_SPI));
					}
					
					@Test
					public void addsFAKeyAttributesInResponse() throws InvalidAttributeIdException {
						handler.handleRequest(request, response);
						
						assertThat(response, containsAttribute(FA_RK));
						assertThat(response, containsAttribute(FA_RK_SPI));
					}
					
					@Test
					public void addsMskInAnswer() {
						handler.handleRequest(request, response);

						assertThat(response, containsAttribute(MSK));
					}
					
					@Test
					public void addsAAASessionIdInAnswer() {
						handler.handleRequest(request, response);

						assertThat(response, containsAttribute(AAA_SESSION_ID));
					}
					
					@Test
					public void addsDHCPKeysIfDHCPIpIsPresentInResponse() {
						addDhcpIp(response);
						
						handler.handleRequest(request, response);
						
						assertThat(response, containsAttribute(DHCP_RK));
						assertThat(response, containsAttribute(DHCP_RK_KEY_ID));
						assertThat(response, containsAttribute(DHCP_RK_LIFETIME));
					}

				}
				
				public class EapSessionDoesNotExist {
					
					@Test
					public void addsMandatoryWimaxAttributes() {
						handler.handleRequest(request, response);
						
						assertThat(response, is(acceptMessage()));
						assertResponseContainsMandatoryWimaxAttributes();
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
					handler.handleRequest(request, response);
					
					assertThat(response, is(acceptMessage()));
					assertResponseContainsMandatoryWimaxAttributes();
				}
			}
			
		}
	}
	
	public class HARequest {
		
		@Before
		public void setUp() throws InvalidAttributeIdException {
			MockitoAnnotations.initMocks(this);
			addUsername(requestBuilder);
			addCallingStationId(requestBuilder);
		}
		
		public class EAPRequest {

			@Before
			public void setUp() throws InvalidAttributeIdException {
				addEAPMessage(requestBuilder);
				addEAPIdentity(requestBuilder);
				addEAPSessionId(requestBuilder);
				addMSK(requestBuilder);
				request = requestBuilder.build();
				response = requestBuilder.buildResponse();
				
				addSessionTimeout(response);
				setClientData(response);
			}
			
			public class WimaxSessionDoesNotExist {
				
				@Test
				public void rejectsTheRequestBeacuseWimaxSessionMustBePresent() throws InvalidAttributeIdException {
					requestBuilder.addAttribute(MN_HA_MIP4_SPI, "0");
					
					request = requestBuilder.build();
					handler.handleRequest(request, response);
					
					assertThat(response, is(rejectMessage()));
					assertThat(response, containsAttribute(RadiusAttributeConstants.REPLY_MESSAGE_STR, 
							AuthReplyMessageConstant.WIMAX_SESSION_NOT_FOUND_FOR_HA));
				}
			}
			
			public class WimaxSessionExists {
				
				private static final int ANY_VALUE = 100;
				private InitialRequestAuthenticator initialRequestAuthenticator;

				@Before
				public void setUp() throws InvalidAttributeIdException {
					initialRequestAuthenticator = new InitialRequestAuthenticator();
					initialRequestAuthenticator.authenticate();
					requestBuilder.addAttribute(MN_HA_MIP4_SPI, "0");
				}
				
				@Test
				public void addsOnlyMN_HAKeysIfHaRkSpiIsNotReceived() throws InvalidAttributeIdException {
					request = requestBuilder.build();
					response = requestBuilder.buildResponse();
					addSessionTimeout(response);
					setClientData(response);
					
					handler.handleRequest(request, response);
					
					assertThat(response, containsAttribute(MN_HA_MIP4_KEY));
					assertThat(response, containsAttribute(MN_HA_MIP4_SPI));
				}
				
				@Test
				public void addsHaRkKeysIfHaRkSpiReceived() throws InvalidAttributeIdException {
					IRadiusAttribute haRkSpi = Dictionary.getInstance().getAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.HA_RK_SPI.getIntValue());
					haRkSpi.setLongValue(initialRequestAuthenticator.receivedHaRkSpi);
					requestBuilder.addAttribute(haRkSpi);
					
					request = requestBuilder.build();
					response = requestBuilder.buildResponse();
					addSessionTimeout(response);
					setClientData(response);
					
					handler.handleRequest(request, response);
					
					assertThat(response, containsAttribute(HA_RK));
					assertThat(response, containsAttribute(HA_RK_SPI));
					assertThat(response, containsAttribute(HA_RK_LIFETIME));
				}
				
				@Test
				public void addsHaRkKeysEvenIfHaRkSpiReceivedIsUnknown() throws InvalidAttributeIdException {
					addHaRkSpi(ANY_VALUE);
					
					request = requestBuilder.build();
					response = requestBuilder.buildResponse();
					addSessionTimeout(response);
					setClientData(response);
					
					handler.handleRequest(request, response);
					
					assertThat(response, containsAttribute(HA_RK));
					assertThat(response, containsAttribute(HA_RK_SPI));
					assertThat(response, containsAttribute(HA_RK_LIFETIME));
				}

				private void addHaRkSpi(int anyValue) {
					IRadiusAttribute haRkSpi = Dictionary.getInstance().getAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.HA_RK_SPI.getIntValue());
					haRkSpi.setIntValue(anyValue);
					requestBuilder.addAttribute(haRkSpi);
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
			
			requestBuilder = new RadAuthRequestBuilder(serverContext);
			addUsername(requestBuilder);
			addCallingStationId(requestBuilder);
			addDhcpRkKeyId();
			addNasIp();
			request = requestBuilder.build();
			response = requestBuilder.buildResponse();
			addSessionTimeout(response);
			setClientData(response);
		}
		
		@Test
		public void rejectsRequestIfNoNasIpAddressIsPresentInRequest() {
			request.removeAttribute(request.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS), true);
			
			handler.handleRequest(request, response);
			
			assertThat(response, is(rejectMessage()));
			assertThat(response, containsAttribute(RadiusAttributeConstants.REPLY_MESSAGE_STR, 
					"No NAS-IP-ADDRESS is present in the request which is mandatory for DHCP-Request so sending Access-Reject."));
		}
		
		@Test
		public void rejectsRequestIfNoKeysAreCached() {
			handler.handleRequest(request, response);
			
			assertThat(response, is(rejectMessage()));
			assertThat(response, containsAttribute(RadiusAttributeConstants.REPLY_MESSAGE_STR, 
					"No active keys found for DHCP Server"));
		}

		@Test
		public void addsDhcpKeysInResponseIfKeysMappedWithDhcpRkKeyIdArePresent() {
			request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue()).setLongValue(initialReqAuthenticator.receivedDhcpRkKeyId);
			
			handler.handleRequest(request, response);
			
			assertThat(response, is(acceptMessage()));
			assertThat(response, containsAttribute(DHCP_RK));
			assertThat(response, containsAttribute(DHCP_RK_KEY_ID));
			assertThat(response, containsAttribute(DHCP_RK_LIFETIME));
			
		}
	}
	
	public class AuthorizeOnlyRequest {
		
		private InitialRequestAuthenticator initialRequestAuthenticator;

		@Before
		public void setUp() throws InvalidAttributeIdException {
			initialRequestAuthenticator = new InitialRequestAuthenticator();
			requestBuilder = new RadAuthRequestBuilder(serverContext);
			addUsername(requestBuilder);
			addCallingStationId(requestBuilder);
			requestBuilder.addAttribute(RadiusAttributeConstants.SERVICE_TYPE_STR, RadiusAttributeValuesConstants.AUTHORIZE_ONLY_STR);
			request = requestBuilder.build();
			response = requestBuilder.buildResponse();
			addSessionTimeout(response);
			setClientData(response);
		}
		
		@Test
		public void addsMandatoryWimaxAttributesToResponseIfWimaxSessionExists() throws InvalidAttributeIdException {
			initialRequestAuthenticator.authenticate();
			
			handler.handleRequest(request, response);
			
			assertThat(response, is(acceptMessage()));
			assertResponseContainsMandatoryWimaxAttributes();
		}
		
		@Test
		public void addsMandatoryWimaxAttributesToResponseIfWimaxSessionDoesNotExist() throws InvalidAttributeIdException {
			handler.handleRequest(request, response);
			
			assertThat(response, is(acceptMessage()));
			assertResponseContainsMandatoryWimaxAttributes();
		}
	}
	
	public class AuthenticateOnlyRequest {
		
		private InitialRequestAuthenticator initialRequestAuthenticator;

		@Before
		public void setUp() throws InvalidAttributeIdException {
			initialRequestAuthenticator = new InitialRequestAuthenticator();
			requestBuilder = new RadAuthRequestBuilder(serverContext);
			addUsername(requestBuilder);
			addCallingStationId(requestBuilder);
			addMSK(requestBuilder);
			addEAPIdentity(requestBuilder);
			addEAPSessionId(requestBuilder);
			addEAPMessage(requestBuilder);
			
			requestBuilder.addAttribute(RadiusAttributeConstants.SERVICE_TYPE_STR, RadiusAttributeValuesConstants.AUTHENTICATE_ONLY_STR);
			request = requestBuilder.build();
			response = requestBuilder.buildResponse();
			addSessionTimeout(response);
			setClientData(response);
		}
		
		public class EapSessionExists {
			
			@Before
			public void setUp() throws InvalidAttributeIdException {
				initialRequestAuthenticator.authenticate();
				when(eapSessionManager.isSessionExist(Mockito.anyString())).thenReturn(true);
			}
			
			@Test
			public void addsMandatoryWimaxAttributesToResponse() throws InvalidAttributeIdException {
				handler.handleRequest(request, response);
				
				assertThat(response, is(acceptMessage()));
				assertResponseContainsMandatoryWimaxAttributes();
			}
			
			@Test
			public void addsHaKeyAttributesInResponse() {
				handler.handleRequest(request, response);
				
				assertThat(response, containsAttribute(HA_IP));
				assertThat(response, containsAttribute(HA_RK));
				assertThat(response, containsAttribute(HA_RK_SPI));
				assertThat(response, containsAttribute(HA_RK_LIFETIME));
				assertThat(response, containsAttribute(MN_HA_MIP4_KEY));
				assertThat(response, containsAttribute(MN_HA_MIP4_SPI));
			}
			
			@Test
			public void addsFAKeyAttributesInResponse() throws InvalidAttributeIdException {
				handler.handleRequest(request, response);
				
				assertThat(response, containsAttribute(FA_RK));
				assertThat(response, containsAttribute(FA_RK_SPI));
			}
			
			@Test
			public void addsMskInAnswer() {
				handler.handleRequest(request, response);

				assertThat(response, containsAttribute(MSK));
			}
			
			@Test
			public void addsAAASessionIdInAnswer() {
				handler.handleRequest(request, response);

				assertThat(response, containsAttribute(AAA_SESSION_ID));
			}
			
			@Test
			public void addsDHCPKeysIfDHCPIpIsPresentInResponse() {
				addDhcpIp(response);
				
				handler.handleRequest(request, response);
				
				assertThat(response, containsAttribute(DHCP_RK));
				assertThat(response, containsAttribute(DHCP_RK_KEY_ID));
				assertThat(response, containsAttribute(DHCP_RK_LIFETIME));
			}
		}
		
		public class EapSessionDoesNotExist {
			@Rule public ExpectedException exception = ExpectedException.none();
			
			@Test
			public void shouldOnlyAddMandatoryWimaxAttributesButFailsWithNPEWhileAddingHaKeysButTheyDontExist() {
				exception.expect(NullPointerException.class);
				
				handler.handleRequest(request, response);
				
				assertThat(response, is(acceptMessage()));
				assertResponseContainsMandatoryWimaxAttributes();
			}
		}
	}
	
	private void addNasIp() {
		IRadiusAttribute nasIp = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
		nasIp.setStringValue("0.0.0.0");
		requestBuilder.addAttribute(nasIp);
	}
	
	private void addDhcpIp(RadAuthResponse response) {
		IRadiusAttribute dhcpIp = wimaxAttribute(WimaxAttrConstants.DHCPV4_SERVER);
		dhcpIp.setStringValue("0.0.0.0");
		response.addAttribute(dhcpIp);
	}
	
	private void addSessionTimeout(RadAuthResponse response) throws InvalidAttributeIdException {
		IRadiusAttribute sessionTimeout = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
		sessionTimeout.setIntValue(600);
		response.addAttribute(sessionTimeout);
	}
	
	private void addMSK(RadAuthRequestBuilder requestBuilder) {
		requestBuilder.addParameter(EAP_KEY_DATA, new byte[128]);
	}
	
	private void setClientData(RadAuthResponse response) {
		RadClientData clientData = mock(RadClientData.class);
		when(clientData.getClientIp()).thenReturn("0.0.0.0");
		when(clientData.getSharedSecret(Mockito.anyInt())).thenReturn("secret");
		when(clientData.getDnsList()).thenReturn(Arrays.asList(new byte[] {0, 0, 0, 0}));
		response.setClientData(clientData);
	}
	

	private void addEAPSessionId(RadAuthRequestBuilder requestBuilder) {
		requestBuilder.addParameter(EAP_SESSION_ID, "FakeEapSession");
	}

	private void addEAPIdentity(RadAuthRequestBuilder requestBuilder) {
		requestBuilder.addParameter(EAP_IDENTITY, TEST_USER);
	}
	
	private void addCallingStationId(RadAuthRequestBuilder requestBuilder) throws InvalidAttributeIdException {
		requestBuilder.addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, CALLING_STATION_ID_VALUE);
	}
	
	private void addEAPMessage(RadAuthRequestBuilder requestBuilder) throws InvalidAttributeIdException {
		requestBuilder.addAttribute(RadiusAttributeConstants.EAP_MESSAGE_STR, "0x2423423423434234");
	}
	
	private void addUsername(RadAuthRequestBuilder requestBuilder) throws InvalidAttributeIdException {
		requestBuilder.addAttribute(RadiusAttributeConstants.USER_NAME_STR, TEST_USER);
	}

	private void addDhcpRkKeyId() {
		IRadiusAttribute dhcpRkKeyId = wimaxAttribute(WimaxAttrConstants.DHCP_RK_KEY_ID);
		dhcpRkKeyId.setIntValue(1);
		requestBuilder.addAttribute(dhcpRkKeyId);
	}

	private void addPPAC() {
		WimaxGroupedAttribute ppac = (WimaxGroupedAttribute) wimaxAttribute(WimaxAttrConstants.PPAC);
		for (PPAC ppacSubAttribute : PPAC.values()) {
			ppac.addTLVAttribute(ppacSubAttribute(ppacSubAttribute, 1));
		}
		requestBuilder.addAttribute(ppac);
	}

	private BaseRadiusAttribute ppacSubAttribute(PPAC subAttribute, int value) {
		BaseRadiusAttribute attribute = (BaseRadiusAttribute)Dictionary.getInstance().getKnownAttribute(RadiusConstants.WIMAX_VENDOR_ID, 
				WimaxAttrConstants.PPAC.getIntValue(), 
				subAttribute.getIntValue());
		attribute.setIntValue(value);
		return attribute;
	}
	
	private void addPPAQ() {
		WimaxGroupedAttribute ppaq = (WimaxGroupedAttribute) wimaxAttribute(WimaxAttrConstants.PPAQ);
		for (PPAQ ppaqSubAttribute : PPAQ.values()) {
			ppaq.addTLVAttribute(ppaqSubAttribute(ppaqSubAttribute, 1));
		}
		requestBuilder.addAttribute(ppaq);
	}
	
	private BaseRadiusAttribute ppaqSubAttribute(PPAQ subAttribute, int value) {
		BaseRadiusAttribute attribute = (BaseRadiusAttribute)Dictionary.getInstance().getKnownAttribute(RadiusConstants.WIMAX_VENDOR_ID, 
				WimaxAttrConstants.PPAQ.getIntValue(), 
				subAttribute.getIntValue());
		attribute.setIntValue(value);
		return attribute;
	}
	
	private void addWimaxCapability(RadAuthResponse response) {
		response.addAttribute(wimaxAttribute(WimaxAttrConstants.WIMAX_CAPABILITY));
	}
	
	private void addWimaxCapability(RadAuthRequestBuilder requestBuilder) {
		requestBuilder.addAttribute(createWimaxCapabilityAttribute());
	}

	private WimaxGroupedAttribute createWimaxCapabilityAttribute() {
		WimaxGroupedAttribute wimaxCapability = (WimaxGroupedAttribute) wimaxAttribute(WimaxAttrConstants.WIMAX_CAPABILITY);
		for (WimaxCapability capability : WimaxCapability.values()) {
			wimaxCapability.addTLVAttribute(wimaxCapabilitySubAttribute(capability, 1));
		}
		return wimaxCapability;
	}

	private BaseRadiusAttribute wimaxCapabilitySubAttribute(WimaxCapability subAttribute, int value) {
		BaseRadiusAttribute acctCapAttr = (BaseRadiusAttribute)Dictionary.getInstance().getKnownAttribute(RadiusConstants.WIMAX_VENDOR_ID, 
				WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), 
				subAttribute.getIntValue());
		acctCapAttr.setIntValue(1);
		return acctCapAttr;
	}
	
	private IRadiusAttribute wimaxAttribute(WimaxAttrConstants attribute) {
		IRadiusAttribute wimaxAttribute = Dictionary.getInstance().getAttribute(RadiusConstants.WIMAX_VENDOR_ID, attribute.getIntValue());
		return wimaxAttribute;
	}
	
	private class InitialRequestAuthenticator {
		
		private RadAuthRequestBuilder initialRequestBuilder = new RadAuthRequestBuilder(serverContext);
		private long receivedDhcpRkKeyId;
		private long receivedHaRkSpi;
		
		void authenticate() throws InvalidAttributeIdException {
			addWimaxCapability(initialRequestBuilder);
			addUsername(initialRequestBuilder);
			addCallingStationId(initialRequestBuilder);
			addEAPMessage(initialRequestBuilder);
			addEAPIdentity(initialRequestBuilder);
			addEAPSessionId(initialRequestBuilder);
			addMSK(initialRequestBuilder);
			
			RadAuthRequest request = initialRequestBuilder.build();
			RadAuthResponse response = initialRequestBuilder.buildResponse();
			addSessionTimeout(response);
			setClientData(response);
			addDhcpIp(response);
			
			when(eapSession.getCurrentMethod()).thenReturn(EapTypeConstants.TTLS.typeId);
			when(eapSessionManager.isSessionExist(Mockito.anyString())).thenReturn(true);
			
			handler.handleRequest(request, response);
			
			receivedDhcpRkKeyId = response.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue()).getLongValue();
			receivedHaRkSpi = response.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.HA_RK_SPI.getIntValue()).getLongValue();
		}
	}
	
	private void assertResponseContainsMandatoryWimaxAttributes() {
		assertThat(response, containsAttribute(WIMAX_CAPABILITY));
		assertThat(response, containsAttribute(WIMAX_DNS));
	}
}
