package com.elitecore.aaa.radius.plugins.quotamgmt;

import static com.elitecore.core.CoreLibMatchers.ServiceResponseMatchers.isDropped;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.net.UnknownHostException;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementData;
import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementPluginConfiguration;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.base.RadAcctRequestBuilder;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacketBuilder;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.expression.impl.FunctionFailTestCase;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class PostThresholdActionsTest {

	private static final String ACCOUNTING_STATUS_TYPE = "0:40";
	private static final String SESSION_ID = "0:44";	

	private static final String VOLUME_TYPE_QUOTA = "VOLUME";
	private static final String DURATION_TYPE_QUOTA = "TIME";
	private static final String BOTH_TYPE_QUOTA = "BOTH";

	private static final int ACTION_ACCEPT = 1;
	private static final int ACTION_DROP = 3;

	private static final PluginCallerIdentity DUMMY_IDENTITY = null;
	private static final String DUMMY_ARGUMENT = "";
	
	private static final boolean THRESHOLD_EXCEEDED = true;
	private static final boolean THRESHOLD_NOT_EXCEEDED = false;
	private static final String ANY_VALUE = "ANY_VALUE";

	private RadAcctRequest request;
	private RadAcctResponse response;

	@Mock private RadClientData clientData;

	private QuotaManagementPluginConfiguration pluginConfiguration;
	private QuotaManagementData pluginPolicyData;

	@Mock private EliteAAAServiceExposerManager exposerManager;

	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	@BeforeClass
	public static void registerFailFunction() {
		Compiler.getDefaultCompiler().addFunction(new FunctionFailTestCase());
	}
	
	@Before
	public void setUp() throws JAXBException, UnknownHostException, InvalidAttributeIdException  {
		MockitoAnnotations.initMocks(this);
	
		when(clientData.getSharedSecret(anyInt())).thenReturn("secret");
		setUpDefaultConfigurationOfPlugin();

		request = new RadAcctRequestBuilder().addAttribute("0:1", "test")
				.addClassAttribute(ANY_VALUE)
				.addAttribute(ACCOUNTING_STATUS_TYPE, RadiusAttributeValuesConstants.INTERIM_UPDATE)
				.addAttribute(SESSION_ID, "123")
				.build();
		createResponse();

	}

	private void setUpDefaultConfigurationOfPlugin() {
		pluginConfiguration = new QuotaManagementPluginConfiguration();
		pluginPolicyData = new QuotaManagementData();
		pluginPolicyData.setName("TestPolicy");
		pluginPolicyData.setRuleset("0:1=\"*\"");
		pluginPolicyData.setEnabled(true);
		pluginPolicyData.setKeyForTime("MAX_SESSION_TIME=");
		pluginPolicyData.setKeyForVolume("MAX_SESSION_VOLUME=");
		pluginPolicyData.setAction(ACTION_ACCEPT);
		pluginPolicyData.setPrepaidQuotaType(VOLUME_TYPE_QUOTA);
		pluginPolicyData.setPacketType(RadiusConstants.COA_REQUEST_MESSAGE + "");
		pluginPolicyData.setFurtherProcessing("true");
		pluginConfiguration.getPluginsData().add(pluginPolicyData);
	}

	@Test
	public void pluginExecutionWillOnlyContinueWhenAccountingStatusTypeIsIntrim() throws Exception {
		request = new RadAcctRequestBuilder().addAttribute("0:1", "test")
				.addAttribute(ACCOUNTING_STATUS_TYPE, RadiusAttributeValuesConstants.START)
				.build();
		
		createResponse();
		
		pluginPolicyData.setRuleset("0:1 = fail(\"Plugin execution should not have proceeded when status type is other than interim update\")");
		QuotaManagementPlugin plugin =  createVolumeTypePlugin(THRESHOLD_EXCEEDED);

		plugin.handlePreRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);

		Mockito.verify(exposerManager, never()).sendBlockingLocalRequest(Mockito.any(RadiusPacket.class));
	}

	@Test
	public void doesNotExecuteDisabledPolicies() throws Exception {
		
		setPoliciesData();
		
		QuotaManagementPlugin plugin1 =  createVolumeTypePlugin(THRESHOLD_EXCEEDED);
		QuotaManagementPlugin plugin = plugin1;

		plugin.handlePreRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);

		Mockito.verify(exposerManager).sendBlockingLocalRequest(Mockito.any(RadiusPacket.class));
	}

	public class VolumeBased {

		@Before
		public void setUp() {
			pluginPolicyData.setPrepaidQuotaType(VOLUME_TYPE_QUOTA);
		}

		public class ThresholdExceeded {

			public class ContinueFutherProcessing {

				@Before
				public void setUp() {
					pluginPolicyData.setFurtherProcessing("true");
				}

				public class AcceptAction {

					@Before
					public void setUp() {
						pluginPolicyData.setAction(ACTION_ACCEPT);
					}

					@Test
					public void hasNoEffectOnResponse() throws InitializationFailedException {
						QuotaManagementPlugin plugin =  createVolumeTypePlugin(THRESHOLD_EXCEEDED);

						byte[] prePluginBytes = response.getResponseBytes();

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);

						assertArrayEquals(prePluginBytes, response.getResponseBytes());
					}
				}

				public class DropAction {


					@Before
					public void setUp() {
						pluginPolicyData.setAction(ACTION_DROP);
					}

					@Test
					public void hasNoEffectOnResponse() throws InitializationFailedException {
						QuotaManagementPlugin plugin =  createVolumeTypePlugin(THRESHOLD_EXCEEDED);

						byte[] prePluginBytes = response.getResponseBytes();

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);

						assertArrayEquals(prePluginBytes, response.getResponseBytes());
					}
				}
			}

			public class SkipFutherProcessing {

				@Before
				public void setUp() {
					pluginPolicyData.setFurtherProcessing("false");
				}

				public class AcceptAction {

					@Before
					public void setUp() {
						pluginPolicyData.setAction(ACTION_ACCEPT);
					}

					@Test
					public void copiesAcctSessionIdFromRequestToResponse() throws InitializationFailedException {
						QuotaManagementPlugin plugin = createVolumeTypePlugin(THRESHOLD_EXCEEDED);

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);

						IRadiusAttribute sessionId =  response.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_ID);

						assertThat(sessionId.getStringValue(), is(equalTo("123")));
					}

					@Test
					public void setsResponsePacketTypeToAccountingResponseMessage() throws JAXBException, InitializationFailedException, UnknownHostException, InvalidAttributeIdException {
						QuotaManagementPlugin plugin = createVolumeTypePlugin(THRESHOLD_EXCEEDED);

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

						assertThat(response.getPacketType(), is(equalTo(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE)));
					}

					@Test
					public void hintsSystemToSkippingFurtherProcessing() throws JAXBException, InitializationFailedException, UnknownHostException, InvalidAttributeIdException {
						QuotaManagementPlugin plugin = createVolumeTypePlugin(THRESHOLD_EXCEEDED);

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

						assertThat(response.isFurtherProcessingRequired(), is(equalTo(false)));
					}
				}

				public class DropAction {

					@Before
					public void setUp() {
						pluginPolicyData.setAction(ACTION_DROP);
					}

					@Test
					public void hintsSystemToDropPacket() throws JAXBException, InitializationFailedException, UnknownHostException, InvalidAttributeIdException {
						QuotaManagementPlugin plugin = createVolumeTypePlugin(THRESHOLD_EXCEEDED);

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

						assertThat(response, isDropped());
					}

					@Test
					public void hintsSystemToSkippingFurtherProcessing() throws JAXBException, InitializationFailedException, UnknownHostException, InvalidAttributeIdException {
						QuotaManagementPlugin plugin = createVolumeTypePlugin(THRESHOLD_EXCEEDED);

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

						assertThat(response.isFurtherProcessingRequired(), is(equalTo(false)));
					}
				}
			}

			public class CoAPacketType {

				ArgumentCaptor<RadiusPacket> packetCaptor;
				
				@Before
				public void setUp() throws Exception {
					pluginPolicyData.setPacketType(RadiusConstants.COA_REQUEST_MESSAGE + "");
					pluginPolicyData.setStrAttributes("0:1");
					packetCaptor = ArgumentCaptor.forClass(RadiusPacket.class);
				}

				@Test
				public void triggersAChangeOfAuthorizationRequest() throws InitializationFailedException, Exception {
					QuotaManagementPlugin plugin = createVolumeTypePlugin(THRESHOLD_EXCEEDED);

					plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

					Mockito.verify(exposerManager).sendBlockingLocalRequest(packetCaptor.capture());
					
					assertThat(packetCaptor.getValue().getPacketType(), is(equalTo(RadiusConstants.COA_REQUEST_MESSAGE)));
				}

				@Test
				public void doesNotAlterRequestOrResponseOnCoA_ACK() throws Exception {
					QuotaManagementPlugin plugin = createVolumeTypePlugin(THRESHOLD_EXCEEDED);

					byte[] prePluginRequestByte = request.getRequestBytes();
					byte[] prePluginResponseByte = response.getResponseBytes();

					plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

					RadiusPacket coaAck = new RadiusPacketBuilder().packetType(RadiusConstants.COA_ACK_MESSAGE).build();

					Mockito.when(exposerManager.sendBlockingLocalRequest(Mockito.any(RadiusPacket.class))).thenReturn(coaAck);

					assertArrayEquals(prePluginRequestByte, request.getRequestBytes());
					assertArrayEquals(prePluginResponseByte, response.getResponseBytes());
				}

				@Test
				public void doesNotAlterRequestOrResponseOnCoA_NAK() throws Exception {
					QuotaManagementPlugin plugin = createVolumeTypePlugin(THRESHOLD_EXCEEDED);

					byte[] prePluginRequestByte = request.getRequestBytes();
					byte[] prePluginResponseByte = response.getResponseBytes();

					plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

					RadiusPacket coaNak = new RadiusPacketBuilder().packetType(RadiusConstants.COA_NAK_MESSAGE).build();

					Mockito.when(exposerManager.sendBlockingLocalRequest(Mockito.any(RadiusPacket.class))).thenReturn(coaNak);

					assertArrayEquals(prePluginRequestByte, request.getRequestBytes());
					assertArrayEquals(prePluginResponseByte, response.getResponseBytes());
				}
			}

			public  class DMPacketType {
				
				ArgumentCaptor<RadiusPacket> packetCaptor;
				
				@Before
				public void setUp() throws Exception {
					pluginPolicyData.setPacketType(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE + "");
					pluginPolicyData.setStrAttributes("0:1");
					packetCaptor = ArgumentCaptor.forClass(RadiusPacket.class);
				}

				@Test
				public void triggersDisconnectMessageRequest() throws InitializationFailedException, Exception {
					QuotaManagementPlugin plugin = createVolumeTypePlugin(THRESHOLD_EXCEEDED);

					plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

					Mockito.verify(exposerManager, times(1)).sendBlockingLocalRequest(packetCaptor.capture());
					
					assertThat(packetCaptor.getValue().getPacketType(), is(equalTo(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE)));
				}

				@Test
				public void doesNotAlterRequestOrResponseOnDM_ACK() throws Exception {
					QuotaManagementPlugin plugin = createVolumeTypePlugin(THRESHOLD_EXCEEDED);

					byte[] prePluginRequestByte = request.getRequestBytes();
					byte[] prePluginResponseByte = response.getResponseBytes();

					plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

					RadiusPacket dmAck = new RadiusPacketBuilder().packetType(RadiusConstants.DISCONNECTION_ACK_MESSAGE).build();

					Mockito.when(exposerManager.sendBlockingLocalRequest(Mockito.any(RadiusPacket.class))).thenReturn(dmAck);

					assertArrayEquals(prePluginRequestByte, request.getRequestBytes());
					assertArrayEquals(prePluginResponseByte, response.getResponseBytes());

				}

				@Test
				public void doesNotAlterRequestOrResponseOnDM_NAK() throws Exception {
					QuotaManagementPlugin plugin = createVolumeTypePlugin(THRESHOLD_EXCEEDED);

					byte[] prePluginRequestByte = request.getRequestBytes();
					byte[] prePluginResponseByte = response.getResponseBytes();


					plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

					RadiusPacket dmNak = new RadiusPacketBuilder().packetType(RadiusConstants.DISCONNECTION_NAK_MESSAGE).build();

					Mockito.when(exposerManager.sendBlockingLocalRequest(Mockito.any(RadiusPacket.class))).thenReturn(dmNak);

					assertArrayEquals(prePluginRequestByte, request.getRequestBytes());
					assertArrayEquals(prePluginResponseByte, response.getResponseBytes());
				}
			}
		}

		public class ThresholdNotExceeded {

			@Test
			public void doesNotAlterRequestOrResponse() throws Exception {
				QuotaManagementPlugin plugin = createVolumeTypePlugin(THRESHOLD_NOT_EXCEEDED);

				byte[] prePluginRequestByte = request.getRequestBytes();
				byte[] prePluginResponseByte = response.getResponseBytes();

				plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

				assertArrayEquals(prePluginRequestByte, request.getRequestBytes());
				assertArrayEquals(prePluginResponseByte, response.getResponseBytes());
			}

			@Test
			public void doesNotTriggerCoAOrDMRequest() throws Exception {
				QuotaManagementPlugin plugin = createVolumeTypePlugin(THRESHOLD_NOT_EXCEEDED);

				plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

				Mockito.verify(exposerManager, never()).sendBlockingLocalRequest(Mockito.any(RadiusPacket.class));
			}
		}
	}


	public class TimeBased {

		@Before
		public void setUp() {
			pluginPolicyData.setPrepaidQuotaType(DURATION_TYPE_QUOTA);
		}

		public class ThresholdExceeded {

			public class ContinueFutherProcessing {

				@Before
				public void setUp() {
					pluginPolicyData.setFurtherProcessing("true");
				}

				public class AcceptAction {

					@Before
					public void setUp() {
						pluginPolicyData.setAction(ACTION_ACCEPT);
					}

					@Test
					public void hasNoEffectOnResponse() throws InitializationFailedException {
						QuotaManagementPlugin plugin =  createTimeTypePlugin(THRESHOLD_EXCEEDED);

						byte[] prePluginBytes = response.getResponseBytes();

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);

						assertArrayEquals(prePluginBytes, response.getResponseBytes());
					}
				}

				public class DropAction {


					@Before
					public void setUp() {
						pluginPolicyData.setAction(ACTION_DROP);
					}

					@Test
					public void hasNoEffectOnResponse() throws InitializationFailedException {
						QuotaManagementPlugin plugin =  createTimeTypePlugin(THRESHOLD_EXCEEDED);

						byte[] prePluginBytes = response.getResponseBytes();

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);

						assertArrayEquals(prePluginBytes, response.getResponseBytes());
					}
				}
			}

			public class SkipFutherProcessing {

				@Before
				public void setUp() {
					pluginPolicyData.setFurtherProcessing("false");
				}

				public class AcceptAction {

					@Before
					public void setUp() {
						pluginPolicyData.setAction(ACTION_ACCEPT);
					}

					@Test
					public void copiesAcctSessionIdFromRequestToResponse() throws InitializationFailedException {
						QuotaManagementPlugin plugin = createTimeTypePlugin(THRESHOLD_EXCEEDED);

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);

						IRadiusAttribute sessionId =  response.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_ID);

						assertThat(sessionId.getStringValue(), is(equalTo("123")));
					}

					@Test
					public void setsResponsePacketTypeToAccountingResponseMessage() throws JAXBException, InitializationFailedException, UnknownHostException, InvalidAttributeIdException {
						QuotaManagementPlugin plugin = createTimeTypePlugin(THRESHOLD_EXCEEDED);

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

						assertThat(response.getPacketType(), is(equalTo(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE)));
					}

					@Test
					public void hintsSystemToSkippingFurtherProcessing() throws JAXBException, InitializationFailedException, UnknownHostException, InvalidAttributeIdException {
						QuotaManagementPlugin plugin = createTimeTypePlugin(THRESHOLD_EXCEEDED);

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

						assertThat(response.isFurtherProcessingRequired(), is(equalTo(false)));
					}
				}

				public class DropAction {

					@Before
					public void setUp() {
						pluginPolicyData.setAction(ACTION_DROP);
					}

					@Test
					public void hintsSystemToDropPacket() throws JAXBException, InitializationFailedException, UnknownHostException, InvalidAttributeIdException {
						QuotaManagementPlugin plugin = createTimeTypePlugin(THRESHOLD_EXCEEDED);

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

						assertThat(response, isDropped());
					}

					@Test
					public void hintsSystemToSkippingFurtherProcessing() throws JAXBException, InitializationFailedException, UnknownHostException, InvalidAttributeIdException {
						QuotaManagementPlugin plugin = createTimeTypePlugin(THRESHOLD_EXCEEDED);

						plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

						assertThat(response.isFurtherProcessingRequired(), is(equalTo(false)));
					}
				}
			}

			public class CoAPacketType {

				ArgumentCaptor<RadiusPacket> packetCaptor;
				
				@Before
				public void setUp() throws Exception {
					pluginPolicyData.setPacketType(RadiusConstants.COA_REQUEST_MESSAGE + "");
					pluginPolicyData.setStrAttributes("0:1");
					packetCaptor = ArgumentCaptor.forClass(RadiusPacket.class);
				}

				@Test
				public void triggersAChangeOfAuthorizationRequest() throws InitializationFailedException, Exception {
					QuotaManagementPlugin plugin = createTimeTypePlugin(THRESHOLD_EXCEEDED);

					plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

					Mockito.verify(exposerManager).sendBlockingLocalRequest(packetCaptor.capture());
					
					assertThat(packetCaptor.getValue().getPacketType(), is(equalTo(RadiusConstants.COA_REQUEST_MESSAGE)));
				}

				@Test
				public void doesNotAlterRequestOrResponseOnCoA_ACK() throws Exception {
					QuotaManagementPlugin plugin = createTimeTypePlugin(THRESHOLD_EXCEEDED);

					byte[] prePluginRequestByte = request.getRequestBytes();
					byte[] prePluginResponseByte = response.getResponseBytes();

					plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

					RadiusPacket coaAck = new RadiusPacketBuilder().packetType(RadiusConstants.COA_ACK_MESSAGE).build();

					Mockito.when(exposerManager.sendBlockingLocalRequest(Mockito.any(RadiusPacket.class))).thenReturn(coaAck);

					assertArrayEquals(prePluginRequestByte, request.getRequestBytes());
					assertArrayEquals(prePluginResponseByte, response.getResponseBytes());
				}

				@Test
				public void doesNotAlterRequestOrResponseOnCoA_NAK() throws Exception {
					QuotaManagementPlugin plugin = createTimeTypePlugin(THRESHOLD_EXCEEDED);

					byte[] prePluginRequestByte = request.getRequestBytes();
					byte[] prePluginResponseByte = response.getResponseBytes();

					plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

					RadiusPacket coaNak = new RadiusPacketBuilder().packetType(RadiusConstants.COA_NAK_MESSAGE).build();

					Mockito.when(exposerManager.sendBlockingLocalRequest(Mockito.any(RadiusPacket.class))).thenReturn(coaNak);

					assertArrayEquals(prePluginRequestByte, request.getRequestBytes());
					assertArrayEquals(prePluginResponseByte, response.getResponseBytes());
				}
			}

			public  class DMPacketType {
				
				ArgumentCaptor<RadiusPacket> packetCaptor;
				
				@Before
				public void setUp() throws Exception {
					pluginPolicyData.setPacketType(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE + "");
					pluginPolicyData.setStrAttributes("0:1");
					packetCaptor = ArgumentCaptor.forClass(RadiusPacket.class);
				}

				@Test
				public void triggersDisconnectMessageRequest() throws InitializationFailedException, Exception {
					QuotaManagementPlugin plugin = createTimeTypePlugin(THRESHOLD_EXCEEDED);

					plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

					Mockito.verify(exposerManager, times(1)).sendBlockingLocalRequest(packetCaptor.capture());
					
					assertThat(packetCaptor.getValue().getPacketType(), is(equalTo(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE)));
				}

				@Test
				public void doesNotAlterRequestOrResponseOnDM_ACK() throws Exception {
					QuotaManagementPlugin plugin = createTimeTypePlugin(THRESHOLD_EXCEEDED);

					byte[] prePluginRequestByte = request.getRequestBytes();
					byte[] prePluginResponseByte = response.getResponseBytes();

					plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

					RadiusPacket dmAck = new RadiusPacketBuilder().packetType(RadiusConstants.DISCONNECTION_ACK_MESSAGE).build();

					Mockito.when(exposerManager.sendBlockingLocalRequest(Mockito.any(RadiusPacket.class))).thenReturn(dmAck);

					assertArrayEquals(prePluginRequestByte, request.getRequestBytes());
					assertArrayEquals(prePluginResponseByte, response.getResponseBytes());

				}

				@Test
				public void doesNotAlterRequestOrResponseOnDM_NAK() throws Exception {
					QuotaManagementPlugin plugin = createTimeTypePlugin(THRESHOLD_EXCEEDED);

					byte[] prePluginRequestByte = request.getRequestBytes();
					byte[] prePluginResponseByte = response.getResponseBytes();


					plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

					RadiusPacket dmNak = new RadiusPacketBuilder().packetType(RadiusConstants.DISCONNECTION_NAK_MESSAGE).build();

					Mockito.when(exposerManager.sendBlockingLocalRequest(Mockito.any(RadiusPacket.class))).thenReturn(dmNak);

					assertArrayEquals(prePluginRequestByte, request.getRequestBytes());
					assertArrayEquals(prePluginResponseByte, response.getResponseBytes());
				}
			}
		}

		public class ThresholdNotExceeded {

			@Test
			public void doesNotAlterRequestOrResponse() throws Exception {
				QuotaManagementPlugin plugin = createTimeTypePlugin(THRESHOLD_NOT_EXCEEDED);

				byte[] prePluginRequestByte = request.getRequestBytes();
				byte[] prePluginResponseByte = response.getResponseBytes();

				plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

				assertArrayEquals(prePluginRequestByte, request.getRequestBytes());
				assertArrayEquals(prePluginResponseByte, response.getResponseBytes());
			}

			@Test
			public void doesNotTriggerCoAOrDMRequest() throws Exception {
				QuotaManagementPlugin plugin = createTimeTypePlugin(THRESHOLD_NOT_EXCEEDED);

				plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

				Mockito.verify(exposerManager, never()).sendBlockingLocalRequest(Mockito.any(RadiusPacket.class));
			}
		}
	}

	public class VolumeAndTimeBased {
		private ArgumentCaptor<RadiusPacket> captor = ArgumentCaptor.forClass(RadiusPacket.class);
		
		@Before
		public void setUp() throws Exception {
			pluginPolicyData.setPrepaidQuotaType(BOTH_TYPE_QUOTA);
		}

		public class ThresholdExceeded {
			
			@Test
			public void triggersCoAorDM() throws Exception {
				QuotaManagementPlugin plugin = createBothTypePlugin(THRESHOLD_EXCEEDED);

				plugin.handlePreRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);

				Mockito.verify(exposerManager).sendBlockingLocalRequest(captor.capture());
				
				assertThat(captor.getValue().getPacketType(), anyOf(
						equalTo(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE),
						equalTo(RadiusConstants.COA_REQUEST_MESSAGE)));
			}

		}

		public class ThresholdNotExceeded {

			@Test
			public void doesNotAlterRequestOrResponse() throws Exception {
				QuotaManagementPlugin plugin = createBothTypePlugin(THRESHOLD_NOT_EXCEEDED);

				byte[] prePluginRequestByte = request.getRequestBytes();
				byte[] prePluginResponseByte = response.getResponseBytes();

				plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

				assertArrayEquals(prePluginRequestByte, request.getRequestBytes());
				assertArrayEquals(prePluginResponseByte, response.getResponseBytes());
			}

			@Test
			public void doesNotGenerateAndSendCoAOrDMRequest() throws Exception {
				QuotaManagementPlugin plugin = createBothTypePlugin(THRESHOLD_NOT_EXCEEDED);

				plugin.handlePreRequest(request, response, DUMMY_ARGUMENT , DUMMY_IDENTITY, ISession.NO_SESSION);

				Mockito.verify(exposerManager, never()).sendBlockingLocalRequest(Mockito.any(RadiusPacket.class));
			}
		}
	}

	private QuotaManagementPlugin createVolumeTypePlugin(boolean isThresholdExceeded) throws InitializationFailedException {
		pluginConfiguration.postRead();
		QuotaManagementPlugin plugin = new QuotaManagementPlugin(new PluginContext() {
			
			@Override
			public ServerContext getServerContext() {
				return null;
			}
			
			@Override
			public PluginConfiguration getPluginConfiguration(String pluginName) {
				return null;
			}
		}, pluginConfiguration, exposerManager);
		plugin.init();
		
		QuotaManagementPlugin spy = Mockito.spy(plugin);
		Mockito.doReturn(isThresholdExceeded).when(spy).isVolumeExceeded(Mockito.any(RadServiceRequest.class), Mockito.any(QuotaManagementData.class));
		Mockito.doThrow(new AssertionError("Time based method called")).when(spy).isTimeExceeded(Mockito.any(RadServiceRequest.class), Mockito.any(QuotaManagementData.class));
		Mockito.doThrow(new AssertionError("Both based method called")).when(spy).isBothVolumeAndDurationExceeded(Mockito.any(RadServiceRequest.class), Mockito.any(QuotaManagementData.class));
		
		return spy;
	}
	
	private QuotaManagementPlugin createTimeTypePlugin(boolean isThresholdExceeded) throws InitializationFailedException {
		pluginConfiguration.postRead();
		QuotaManagementPlugin plugin = new QuotaManagementPlugin(new PluginContext() {
			
			@Override
			public ServerContext getServerContext() {
				return null;
			}
			
			@Override
			public PluginConfiguration getPluginConfiguration(String pluginName) {
				return null;
			}
		}, pluginConfiguration, exposerManager);
		plugin.init();
		
		QuotaManagementPlugin spy = Mockito.spy(plugin);
		Mockito.doReturn(isThresholdExceeded).when(spy).isTimeExceeded(Mockito.any(RadServiceRequest.class), Mockito.any(QuotaManagementData.class));
		Mockito.doThrow(new AssertionError("Volume based method called")).when(spy).isVolumeExceeded(Mockito.any(RadServiceRequest.class), Mockito.any(QuotaManagementData.class));
		Mockito.doThrow(new AssertionError("Both based method called")).when(spy).isBothVolumeAndDurationExceeded(Mockito.any(RadServiceRequest.class), Mockito.any(QuotaManagementData.class));
		
		return spy;
	}
	
	private QuotaManagementPlugin createBothTypePlugin(boolean isThresholdExceeded) throws InitializationFailedException {
		pluginConfiguration.postRead();
		QuotaManagementPlugin plugin = new QuotaManagementPlugin(new PluginContext() {
			
			@Override
			public ServerContext getServerContext() {
				return null;
			}
			
			@Override
			public PluginConfiguration getPluginConfiguration(String pluginName) {
				return null;
			}
		}, pluginConfiguration, exposerManager);
		plugin.init();
		
		QuotaManagementPlugin spy = Mockito.spy(plugin);
		Mockito.doThrow(new AssertionError("Volume based method called")).when(spy).isVolumeExceeded(Mockito.any(RadServiceRequest.class), Mockito.any(QuotaManagementData.class));
		Mockito.doThrow(new AssertionError("Time based method called")).when(spy).isTimeExceeded(Mockito.any(RadServiceRequest.class), Mockito.any(QuotaManagementData.class));
		Mockito.doReturn(isThresholdExceeded).when(spy).isBothVolumeAndDurationExceeded(Mockito.any(RadServiceRequest.class), Mockito.any(QuotaManagementData.class));
		
		return spy;
	}

	private void setPoliciesData() {

		QuotaManagementData pluginPolicyDataEnable = new QuotaManagementData();

		pluginPolicyData.setEnabled(false);
		pluginPolicyData.setAction(ACTION_ACCEPT);
		pluginPolicyData.setPacketType(RadiusConstants.COA_REQUEST_MESSAGE + "");
		pluginPolicyData.setPrepaidQuotaType(VOLUME_TYPE_QUOTA);

		pluginPolicyDataEnable.setName("TestPolicyTwo");
		pluginPolicyDataEnable.setRuleset("0:1=\"*\"");
		pluginPolicyDataEnable.setEnabled(true);
		pluginPolicyDataEnable.setFurtherProcessing("true");
		pluginPolicyDataEnable.setKeyForTime("MAX_SESSION_TIME=");
		pluginPolicyDataEnable.setKeyForVolume("MAX_SESSION_VOLUME=");
		pluginPolicyDataEnable.setAction(ACTION_ACCEPT);
		pluginPolicyDataEnable.setPacketType(RadiusConstants.COA_REQUEST_MESSAGE + "");
		pluginPolicyDataEnable.setPrepaidQuotaType(VOLUME_TYPE_QUOTA);

		pluginConfiguration.getPluginsData().add(pluginPolicyDataEnable);
	}

	private void createResponse() throws UnknownHostException {
		response = new RadAcctRequestBuilder().buildResponse(request);
		response.setClientData(clientData);
	}
}
