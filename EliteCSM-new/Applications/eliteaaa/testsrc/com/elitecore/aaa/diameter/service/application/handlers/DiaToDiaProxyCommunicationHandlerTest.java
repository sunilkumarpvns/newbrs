package com.elitecore.aaa.diameter.service.application.handlers;

import static com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion.assertThat;
import static com.elitecore.diameterapi.diameter.common.util.constant.ResultCode.DIAMETER_COMMAND_UNSUPPORTED;
import static com.elitecore.diameterapi.diameter.common.util.constant.ResultCode.DIAMETER_SUCCESS;
import static com.elitecore.diameterapi.diameter.common.util.constant.ResultCode.DIAMETER_TOO_BUSY;
import static com.elitecore.diameterapi.diameter.common.util.constant.ResultCode.DIAMETER_UNABLE_TO_DELIVER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.conf.impl.PeerGroupData;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterExternalCommunicationEntryData;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy;
import com.elitecore.diameterapi.diameter.common.routerx.agent.DiameterTestSupport;
import com.elitecore.diameterapi.diameter.common.routerx.agent.data.PeerDataProvider;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

// TODO test case when group is dead in case of server initiated request
@RunWith(HierarchicalContextRunner.class)
public class DiaToDiaProxyCommunicationHandlerTest extends DiameterTestSupport {
	private static final String GROUP_NAME = "GROUP_NAME";

	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	@Mock private DiameterServiceContext serviceContext;
	private DiameterExternalCommunicationEntryData data;
	private DiaToDiaProxyCommunicationHandler handler;
	private PeerGroupData groupData;
	private DiameterPeerSpy remotePeer;
	private DiameterPeerSpy otherRemotePeer;

	private DiameterPeerSpy initiatorPeer;
	private DiameterPeerSpy initiatorPeerSecondary;
	private DiameterPeerSpy otherInitiatorPeer;
	private DiameterPeerSpy otherInitatorPeerSecondary;
	
	DiameterRequest request;
	ApplicationRequest applicationRequest;
	ApplicationResponse applicationResponse;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);
		
		registerPeers();
		
		request = createCCInitialRequest();
		applicationRequest = new ApplicationRequest(request);
		applicationResponse = new ApplicationResponse(request);
		
		data = new DiameterExternalCommunicationEntryData();
		data.setPeerGroupId("0");
		data.setPeerGroupName(GROUP_NAME);
		data.setPolicyName("test");

		groupData = new PeerGroupData();
		groupData.setName(GROUP_NAME);

		PeerInfoImpl peerInfo1 = new PeerInfoImpl();
		peerInfo1.setPeerName(ROUTING_PEER[0]);
		peerInfo1.setLoadFactor(1);
		groupData.getPeers().add(peerInfo1);

		PeerInfoImpl peerInfo2 = new PeerInfoImpl();
		peerInfo2.setPeerName(ROUND_ROBIN_PEER[0]);
		peerInfo2.setLoadFactor(1);
		groupData.getPeers().add(peerInfo2);

		when(serviceContext.getStackContext()).thenReturn(getStackContext());

		remotePeer = getPeerOperation(ROUTING_PEER[1]);
		otherRemotePeer = getPeerOperation(ROUND_ROBIN_PEER[1]);

		handler = new DiaToDiaProxyCommunicationHandler(data, serviceContext, groupData, null, mock(ITranslationAgent.class));
		handler.init();
	}

	private void registerPeers() {
		PeerData initiatorSecondaryPeerData = new PeerDataProvider()
				.withPeerName("initiatorSecondaryPeer")
				.withHostIdentity("initiator-secondary.example.com")
				.withRealmName("example.com")
				.build();
		
		PeerData initiatorPeerData = new PeerDataProvider()
				.withPeerName("initiatorPeer")
				.withHostIdentity("initiator.example.com")
				.withSecondaryPeer(initiatorSecondaryPeerData.getPeerName())
				.withRealmName("example.com")
				.build();
		
		PeerData otherInitiatorSecondaryPeerData = new PeerDataProvider()
				.withPeerName("otherInitiatorSecondaryPeer")
				.withHostIdentity("other-initiator-secondary.example.com")
				.withRealmName("example.com")
				.build();
		PeerData otherInitiatorPeerData = new PeerDataProvider()
				.withPeerName("otherInitiatorPeer")
				.withHostIdentity("other-initiator.example.com")
				.withSecondaryPeer(otherInitiatorSecondaryPeerData.getPeerName())
				.withRealmName("example.com")
				.build();
		
		addPeer(initiatorSecondaryPeerData)
		.addPeer(initiatorPeerData)
		.addPeer(otherInitiatorSecondaryPeerData)
		.addPeer(otherInitiatorPeerData);
		
		initiatorPeer = getPeerOperation(initiatorPeerData.getHostIdentity());
		initiatorPeerSecondary = getPeerOperation(initiatorSecondaryPeerData.getHostIdentity());
		otherInitiatorPeer = getPeerOperation(otherInitiatorPeerData.getHostIdentity());
		otherInitatorPeerSecondary = getPeerOperation(otherInitiatorSecondaryPeerData.getHostIdentity());
	}
	
	@Test
	public void rejectsRequestIfAllPeersInTheGroupAreDead() {
		remotePeer.markDead();
		otherRemotePeer.markDead();

		ApplicationRequest applicationRequest = new ApplicationRequest(request);
		ApplicationResponse applicationResponse = new ApplicationResponse(request);

		handler.handleRequest(applicationRequest, applicationResponse, getSession());
		
		DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer())
		.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
		
	}
	
	@Test
	public void throwsNPEIfSessionPassedIsNull() {
		
		ApplicationRequest applicationRequest = new ApplicationRequest(request);
		ApplicationResponse applicationResponse = new ApplicationResponse(request);
		
		handler.handleRequest(applicationRequest, applicationResponse, null);
	}

	private ApplicationResponse createApplicationResponse(DiameterRequest request) {
		ApplicationResponse applicationResponse = new ApplicationResponse(request);
		applicationResponse.getDiameterAnswer().addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_SUCCESS.code + "");
		return applicationResponse;
	}

	@Test
	public void shouldProxyDiameterRequestWithoutTranslationToDestinationGroup() {
		DiameterRequest request = createCCInitialRequest();

		ApplicationRequest applicationRequest = new ApplicationRequest(request);
		ApplicationResponse applicationResponse = new ApplicationResponse(request);

		handler.handleRequest(applicationRequest, applicationResponse, getSession());

		DiameterAnswer remoteAnswer = createSuccessAnswerFrom(remotePeer);
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.FRAMED_IP_ADDRESS, remoteAnswer, "1.1.1.1");

		remotePeer.sendsAnswer(remoteAnswer, getSession());

		assertNotNull(applicationResponse.getAVP(DiameterAVPConstants.FRAMED_IP_ADDRESS));
		assertEquals("1.1.1.1", applicationResponse.getAVP(DiameterAVPConstants.FRAMED_IP_ADDRESS).getStringValue());
	}
	
	@Test
	public void addsHostIdentityOfRequestingPeerAsRouteRecordAVPInRemoteRequestInCaseOfProxyCommunication() {
		handler.handleRequest(applicationRequest, applicationResponse, getSession());

		assertThat(remotePeer.getLastSentDiameterPacket()).containsAVP(DiameterAVPConstants.ROUTE_RECORD);
		assertThat(remotePeer.getLastSentDiameterPacket()).containsAVP(DiameterAVPConstants.ROUTE_RECORD , ORIGINATOR_PEER_1[1] );
	}
	
	@Test
	public void routeRecordAVPWillNotBeAddedForLocallyGeneratedRequest() {
		String serverIdentity = Parameter.getInstance().getOwnDiameterIdentity();
		applicationRequest.getDiameterRequest().setRequestingHost(serverIdentity);
		
		handler.handleRequest(applicationRequest, applicationResponse, getSession());
		
		assertThat(remotePeer.getLastSentDiameterPacket()).doesNotContainAVP(DiameterAVPConstants.ROUTE_RECORD);
	}

	@Test
	public void addsHostIdentityOfRequestingPeerAsRouteRecordAVPInRemoteRequestInCaseOfBroadcastCommunication() {
		handler.handleAsyncRequest(applicationRequest, applicationResponse, getSession(), dummyBroadcastResponseListener());
		
		assertThat(remotePeer.getLastSentDiameterPacket()).containsAVP(DiameterAVPConstants.ROUTE_RECORD);
		assertThat(remotePeer.getLastSentDiameterPacket()).containsAVP(DiameterAVPConstants.ROUTE_RECORD , ORIGINATOR_PEER_1[1] );
	}

	private DiameterBroadcastResponseListener dummyBroadcastResponseListener() {
		return new DiameterBroadcastResponseListener() {
		
			@Override
			public int getNextOrder() {
				return 0;
			}
		
			@Override
			public void addAsyncRequestExecutor(DiameterOrderedAsyncRequestExecutor executor) {
				
			}
		
			@Override
			public void responseReceived(DiameterRequest remoteRequest, DiameterAnswer remoteAnswer, ISession isession) {
			}
		
			@Override
			public void addHandler(DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> handler) {
			}
			
		};
	}

	public class StatefulCommunication {

		private DiameterRequest subsequentRequest;
		private ApplicationRequest subsequentApplicationRequest;
		private ApplicationResponse subsequentApplicationResponse;

		@Before
		public void setUp() throws InitializationFailedException {
			handler = new DiaToDiaProxyCommunicationHandler(data, serviceContext, groupData, null, mock(ITranslationAgent.class));
			handler.init();

			initialRequestIsProcessed();
			subsequentRequest = createCCUpdateRequest();
			subsequentApplicationRequest = new ApplicationRequest(subsequentRequest);
			subsequentApplicationResponse = new ApplicationResponse(subsequentRequest);
		}

		@Test
		public void subsequentClientInitiatedRequestsForSameSession_WillBeForwardedToStatefullPeer() {
			handler.handleRequest(subsequentApplicationRequest, subsequentApplicationResponse, getSession());

			remotePeer.verifyRequestReceipts(2);
		}
	}
	
	public class ServerInitiatedMessage {

		private ApplicationRequest rarApplicationRequest;
		private ApplicationResponse rarApplicationResponse;

		@Before
		public void setUp() throws InitializationFailedException {
			groupData = new PeerGroupData();
			groupData.setName(GROUP_NAME);
			groupData.setRetryLimit(2);

			PeerInfoImpl peerInfo2 = new PeerInfoImpl();
			peerInfo2.setPeerName(ORIGINATOR_PEER_2[0]);
			peerInfo2.setLoadFactor(1);
			groupData.getPeers().add(peerInfo2);

			PeerInfoImpl peerInfo1 = new PeerInfoImpl();
			peerInfo1.setPeerName(ORIGINATOR_PEER_1[0]);
			peerInfo1.setLoadFactor(1);
			groupData.getPeers().add(peerInfo1);


			handler = new DiaToDiaProxyCommunicationHandler(data, serviceContext, groupData, null, mock(ITranslationAgent.class));
			handler.init();

			DiameterRequest rarRequest = createRAR(remotePeer.getDiameterPeer().getPeerData());
			rarApplicationRequest = new ApplicationRequest(rarRequest);
			rarApplicationResponse = new ApplicationResponse(rarRequest);
		}

		public class SessionContainsOriginHost {

			@Before
			public void setUp() {
				getSession().setParameter(DiameterAVPConstants.ORIGIN_HOST, initiatorPeer.getDiameterPeer().getHostIdentity());
			}

			public class DestinationHostIsAbsentInRequest { 

				@Test
				public void requestShouldBeProxiedToOriginHostInSession() {
					handler.handleRequest(rarApplicationRequest, rarApplicationResponse, getSession());

					initiatorPeer.verifyRequestReceipt();
				}

				@Test
				public void requestShouldFailoverToFailoverPeerOfOriginPeer_IfOriginPeerInSessionDoesNotAnswerWithinRequestTimeout() {
					handler.handleRequest(rarApplicationRequest, rarApplicationResponse, getSession());

					initiatorPeer.doesNotAnswerWithinRequestTimeout(getSession());

					initiatorPeerSecondary.verifyRequestReceipt();
				}
			}

			public class DestinationHostIsPresentInRequest {

				public class DestinationHostIsKnown {

					@Before
					public void setUp() {
						rarApplicationRequest.getDiameterRequest().addAvp(DiameterAVPConstants.DESTINATION_HOST, otherInitiatorPeer.getDiameterPeer().getHostIdentity());
					}

					@Test
					public void requestShouldBeProxiedToDestinationHostInRequest() {
						handler.handleRequest(rarApplicationRequest, rarApplicationResponse, getSession());

						otherInitiatorPeer.verifyRequestReceipt();
					}

					@Test
					public void requestShouldFailoverToFailoverPeerOfDestinationHost_IfDestinationHostDoesNotAnswerWithinRequestTimeout() {
						handler.handleRequest(rarApplicationRequest, rarApplicationResponse, getSession());

						otherInitiatorPeer.doesNotAnswerWithinRequestTimeout(getSession());

						otherInitatorPeerSecondary.verifyRequestReceipt();
					}

				}

				public class DestinationHostIsUnknown {

					@Before
					public void setUp() {
						rarApplicationRequest.getDiameterRequest().addAvp(DiameterAVPConstants.DESTINATION_HOST, UNKNOWN_PEER[1]);
					}

					@Test
					public void raaWithUnableToDeliverResultCodeIsSentToRequestingPeer() {
						handler.handleRequest(rarApplicationRequest, rarApplicationResponse, getSession());

						requestIsNotForwarded();

						DiameterAssertion.assertThat(rarApplicationResponse.getDiameterAnswer())
						.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
					}

					private void requestIsNotForwarded() {
						initiatorPeer.verifyRequestNotReceived();
						otherInitiatorPeer.verifyRequestNotReceived();
					}
				}
			}
		}

		public class NewlyCreatedSessionForRARDoesNotContainOriginHost {

			public class DestinationHostPresentInRequest {

				public class DestinationHostIsKnown {

					@Before
					public void setUp() {
						rarApplicationRequest.getDiameterRequest().addAvp(DiameterAVPConstants.DESTINATION_HOST, initiatorPeer.getDiameterPeer().getHostIdentity());
					}

					@Test
					public void requestShouldBeProxiedToDestinationPeerBasedOnDestinationHostAVP() {
						handler.handleRequest(rarApplicationRequest, rarApplicationResponse, getSession());

						initiatorPeer.verifyRequestReceipt();
					}
				}

				public class DestinationHostIsUnknown {

					@Before
					public void setUp() {
						rarApplicationRequest.getDiameterRequest().addAvp(DiameterAVPConstants.DESTINATION_HOST, UNKNOWN_PEER[1]);
					}

					@Test
					public void raaWithUnableToDeliverResultCodeIsSentToRequestingPeer() {
						handler.handleRequest(rarApplicationRequest, rarApplicationResponse, getSession());

						requestIsNotForwarded();

						DiameterAssertion.assertThat(rarApplicationResponse.getDiameterAnswer())
						.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
					}

					private void requestIsNotForwarded() {
						initiatorPeer.verifyRequestNotReceived();
						otherInitiatorPeer.verifyRequestNotReceived();
					}
				}
			}

			public class DestinationHostAbsentInRequest {

				@Test
				public void raaWithUnableToDeliverResultCodeIsSentToRequestingPeer() {
					handler.handleRequest(rarApplicationRequest, rarApplicationResponse, getSession());

					DiameterAssertion.assertThat(rarApplicationResponse.getDiameterAnswer())
					.hasHeaderOf(rarApplicationRequest.getDiameterRequest())
					.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
				}
			}
		}
	}
	
	public class WhileAggregatingRemoteResponse {

		List<Integer> priorityResultCodes;
		List<Integer> acceptableResultCodes;

		@Before
		public void setup() {
			priorityResultCodes = new ArrayList<Integer>();
			acceptableResultCodes = new ArrayList<Integer>();
			
			DiameterProcessHelper.toSuccess(applicationResponse);
		}
		
		public class HandlerAggregates {

			@Test
			public void ifTheResultCodeIsOfFailureCategoryAndIsNotConfiguredInAcceptedResultCodes() throws InitializationFailedException {
				setPriorityResultCodes(ResultCodeCategory.RC3XXX.value, ResultCodeCategory.RC2XXX.value);
				
				handleRequest();

				answerReceivedFromRemotePeerHaving(ResultCode.DIAMETER_UNABLE_TO_DELIVER);
				
				DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).hasResultCode(DIAMETER_UNABLE_TO_DELIVER);
			}
			
		}

		public class HandlerDoesNotAggregates {
			
			@Test
			public void ifTheResultCodeIsOfFailureCategoryAndItIsConfiguredInAcceptedResultCodes() throws InitializationFailedException {

				setAcceptableResultCode(DIAMETER_TOO_BUSY.code);
				
				handleRequest();
				
				answerReceivedFromRemotePeerHaving(ResultCode.DIAMETER_TOO_BUSY);

				DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).hasResultCode(DIAMETER_SUCCESS);
			}
			
		}
		
		@Test
		public void resultCodeOfResponseDelegatedToProxyHandlerByPreviousHandlerIsNotConsideredWhileCalculatingHighestPriorityResultCode() throws InitializationFailedException {
			setPriorityResultCodes(DIAMETER_COMMAND_UNSUPPORTED.code, DIAMETER_SUCCESS.code);
			
			handleRequest();
			
			answerReceivedFromRemotePeerHaving(ResultCode.DIAMETER_COMMAND_UNSUPPORTED);
			
			DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).hasResultCode(DIAMETER_COMMAND_UNSUPPORTED);
		}
  
		private void setAcceptableResultCode(int...codes) {
			
			for(int code : codes) {
				acceptableResultCodes.add(code);
			}
			data.getAcceptableResultCodes().addAll(acceptableResultCodes);
		}
		
		private void answerReceivedFromRemotePeerHaving(ResultCode resultCode, IDiameterAVP ...avps) {
			DiameterAnswer remoteAnswer = new DiameterAnswer(request,resultCode);
			for(IDiameterAVP avp : avps) {
				remoteAnswer.addAvp(avp);
			}
			remotePeer.sendsAnswer(remoteAnswer, getSession());
		}

		private void handleRequest() throws InitializationFailedException {
			handler.init();
			handler.handleRequest(applicationRequest, applicationResponse, getSession());
		}

		private void setPriorityResultCodes(int ...codes) {
			for(int resultCode : codes) {
				priorityResultCodes.add(resultCode);
			}
			data.setPriorityResultCodes(priorityResultCodes);
		}
	}
	
	private void initialRequestIsProcessed() {
		DiameterRequest request = createCCInitialRequest();
		ApplicationRequest applicationRequest = new ApplicationRequest(request);
		ApplicationResponse applicationResponse = createApplicationResponse(request);

		handler.handleRequest(applicationRequest, applicationResponse, getSession());

		DiameterAnswer remoteAnswer = createSuccessAnswerFrom(remotePeer);

		remoteResponseReceivedFrom(remotePeer, remoteAnswer);
	}

	private void remoteResponseReceivedFrom(DiameterPeerSpy peerOperation, DiameterAnswer diameterAnswer) {
		peerOperation.getDiameterPeer().getResponseListener().responseReceived(diameterAnswer, peerOperation.getDiameterPeer().getHostIdentity(), getSession());
	}
}
