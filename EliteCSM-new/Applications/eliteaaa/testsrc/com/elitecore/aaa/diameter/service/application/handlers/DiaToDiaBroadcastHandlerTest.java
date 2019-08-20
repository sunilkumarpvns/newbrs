package com.elitecore.aaa.diameter.service.application.handlers;

import static com.elitecore.diameterapi.diameter.common.util.constant.ResultCode.DIAMETER_LIMITED_SUCCESS;
import static com.elitecore.diameterapi.diameter.common.util.constant.ResultCode.DIAMETER_SUCCESS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.conf.impl.PeerGroupData;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterBroadcastCommunicationEntryData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterBroadcastCommunicationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterExternalCommunicationEntryData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy;
import com.elitecore.diameterapi.diameter.common.routerx.agent.DiameterTestSupport;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class DiaToDiaBroadcastHandlerTest extends DiameterTestSupport {

	private static final String defaultPeerGroupID = "0";
	public static final String CLASS_AVP_ID = DiameterDictionary.getInstance().getStrAVPId(DiameterAVPConstants.CLASS);
	private static final boolean WILL_WAIT_FOR_RESPONSE = true;	
	private static final String PEER1_NAME = "peer1";
	private static final String TEST_REALM = "test1.com";
	private static final String PEER1_IDENTITY = "peer1.test1.com";
	private static final String GROUP1_NAME = "GROUP1_NAME";

	private static final String PEER2_NAME = "peer2";
	private static final String TEST2_REALM = "test2.com";
	private static final String PEER2_IDENTITY = "peer2.test2.com";
	private static final String GROUP2_NAME = "GROUP2_NAME";

	private static final String PEER3_NAME = "peer3";
	private static final String TEST3_REALM = "test3.com";
	private static final String PEER3_IDENTITY = "peer3.test3.com";
	private static final String GROUP3_NAME = "GROUP3_NAME";

	private static final String INITIATOR_PEER_IDENTITY = "initiator.test3.com";
	private static final String INITIATOR_PEER_NAME = "initiator";
	private static final String INITIATOR_TEST_REALM = "test3.com";

	@Mock private DiameterServiceContext serviceContext;

	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();

	private DiameterBroadcastCommunicationHandlerData data = new DiameterBroadcastCommunicationHandlerData(){
		@Override
		public void postRead() {
			List<Integer> priorityResultCodes;
			priorityResultCodes = Collectionz.map(Strings.splitter(',').split(getStrPriorityResultCodes()), Strings.toInt());
			
			List<DiameterBroadcastCommunicationEntryData> entries = getEntries();
			for (DiameterExternalCommunicationEntryData data : entries) {
				data.setPriorityResultCodes(priorityResultCodes);
				data.setProtocol(getProtocol());
			}
		};
	};
	private DiaToDiaBroadcastHandler handler;

	private PeerGroupData group1Data;
	private PeerGroupData group2Data;
	private PeerGroupData group3Data;
	private DiameterPeerSpy initiatorPeerOperation;
	private DiameterPeerSpy peerOperation1;
	private DiameterPeerSpy peerOperation2;
	private DiameterPeerSpy peerOperation3;

	private DiameterRequest request;
	private ApplicationRequest applicationRequest;
	private ApplicationResponse applicationResponse;
	
	private DiameterAnswer havingLimitedSuccess;
	private DiameterAnswer havingDiameterSuccess;
	private DiameterAnswer havingMultiRoundAuth;
	private DiameterAnswer havingDiameterTooBusy;
	private DiameterAnswer havingDiameterOutOfSpace;
	private DiameterAnswer havingAvpUnsupported;
	
	private final String AVP_IN_MULTI_ROUND_AUTH_PACKET = DummyDiameterDictionary.getInstance().getStrAVPId(DiameterAVPConstants.CLASS);
	private final String AVP_IN_DIAMETER_SUCCESS_PACKET = DummyDiameterDictionary.getInstance().getStrAVPId(DiameterAVPConstants.CALLING_STATION_ID);
	private final String AVP_IN_LIMITED_SUCCESS_PACKET = DummyDiameterDictionary.getInstance().getStrAVPId(DiameterAVPConstants.CALLED_STATION_ID);
	private final String AVP_IN_DIAMETER_TOO_BUSY_PACKET = DummyDiameterDictionary.getInstance().getStrAVPId(DiameterAVPConstants.USER_NAME);
	private final String AVP_IN_DIAMETER_OUT_OF_SPACE = DummyDiameterDictionary.getInstance().getStrAVPId(DiameterAVPConstants.ERROR_MESSAGE);
	private final String AVP_IN_DIAMETER_AVP_UNSUPPORTED = DummyDiameterDictionary.getInstance().getStrAVPId(DiameterAVPConstants.FAILED_AVP);
	
	private RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> executor;
	private DiameterBroadcastCommunicationEntryData group1Entry;
	private DiameterBroadcastCommunicationEntryData group2Entry;
	private DiameterBroadcastCommunicationEntryData group3Entry;

	static  {
		DummyDiameterDictionary.getInstance();
	}
	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws Exception {
		super.setUp();

		MockitoAnnotations.initMocks(this);

		group1Entry = new DiameterBroadcastCommunicationEntryData();
		group1Entry.setPeerGroupId(defaultPeerGroupID);
		group1Entry.setPeerGroupName(GROUP1_NAME);
		group1Entry.setPolicyName("test");
		group1Entry.setWait(WILL_WAIT_FOR_RESPONSE);

		PeerInfoImpl peerInfo = new PeerInfoImpl();
		peerInfo.setPeerName(PEER1_NAME);
		peerInfo.setLoadFactor(1);

		group1Data = new PeerGroupData();
		group1Data.setName(GROUP1_NAME);
		group1Data.getPeers().add(peerInfo);

		data.getEntries().add(group1Entry);

		group2Entry = new DiameterBroadcastCommunicationEntryData();
		group2Entry.setPeerGroupId(defaultPeerGroupID);
		group2Entry.setPeerGroupName(GROUP2_NAME);
		group2Entry.setPolicyName("test");
		group2Entry.setWait(WILL_WAIT_FOR_RESPONSE);

		peerInfo = new PeerInfoImpl();
		peerInfo.setPeerName(PEER2_NAME);
		peerInfo.setLoadFactor(1);

		group2Data = new PeerGroupData();
		group2Data.setName(GROUP2_NAME);
		group2Data.getPeers().add(peerInfo);

		data.getEntries().add(group2Entry);

		group3Entry = new DiameterBroadcastCommunicationEntryData();
		group3Entry.setPeerGroupId(defaultPeerGroupID);
		group3Entry.setPeerGroupName(GROUP3_NAME);
		group3Entry.setPolicyName("test");
		group3Entry.setWait(WILL_WAIT_FOR_RESPONSE);

		peerInfo = new PeerInfoImpl();
		peerInfo.setPeerName(PEER3_NAME);
		peerInfo.setLoadFactor(1);

		group3Data = new PeerGroupData();
		group3Data.setName(GROUP3_NAME);
		group3Data.getPeers().add(peerInfo);

		data.getEntries().add(group3Entry);
		
		setUpStackContext();

		when(serviceContext.getStackContext()).thenReturn(getStackContext());
		Mockito.doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				DiameterAsyncRequestExecutor requestExecutor = invocation.getArgumentAt(3, DiameterAsyncRequestExecutor.class);
				requestExecutor.handleServiceRequest(applicationRequest, applicationResponse, getSession());
				return null;
			}
		}).when(serviceContext).resumeRequestInAsync(Matchers.any(Session.class),
				Matchers.any(ApplicationRequest.class), 
				Matchers.any(ApplicationResponse.class), 
				(DiameterAsyncRequestExecutor)Matchers.any(DiameterAsyncRequestExecutor.class));

		request = createCCInitialRequest();
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.DESTINATION_HOST, request, initiatorPeerOperation.getDiameterPeer().getHostIdentity());
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.DESTINATION_REALM, request, initiatorPeerOperation.getDiameterPeer().getRealm());

		applicationRequest = new ApplicationRequest(request);
		applicationResponse = new ApplicationResponse(request);
		
		havingMultiRoundAuth = new DiameterAnswer(request,ResultCode.DIAMETER_MULTI_ROUND_AUTH);
		havingMultiRoundAuth.addAvp(AVP_IN_MULTI_ROUND_AUTH_PACKET, "random string value");
		havingDiameterSuccess = new DiameterAnswer(request,DIAMETER_SUCCESS);
		havingDiameterSuccess.addAvp(AVP_IN_DIAMETER_SUCCESS_PACKET, "random string value");
		havingLimitedSuccess = new DiameterAnswer(request,DIAMETER_LIMITED_SUCCESS);
		havingLimitedSuccess.addAvp(AVP_IN_LIMITED_SUCCESS_PACKET, "random string value");
		havingDiameterTooBusy = new DiameterAnswer(request, ResultCode.DIAMETER_TOO_BUSY);
		havingDiameterTooBusy.addAvp(AVP_IN_DIAMETER_TOO_BUSY_PACKET, "random string value");
		havingDiameterOutOfSpace = new DiameterAnswer(request,ResultCode.DIAMETER_OUT_OF_SPACE);
		havingDiameterOutOfSpace.addAvp(AVP_IN_DIAMETER_OUT_OF_SPACE, "random string value");
		havingAvpUnsupported = new DiameterAnswer(request,ResultCode.DIAMETER_AVP_UNSUPPORTED);
		havingAvpUnsupported.addAvp(AVP_IN_DIAMETER_AVP_UNSUPPORTED, "random string value");

		handler = new DiaToDiaBroadcastHandler(serviceContext, data, mock(ITranslationAgent.class)){
			@Override
			PeerGroupData getPeerGroupDataFor(DiameterExternalCommunicationEntryData entry) {
				if (entry.getPeerGroupName().equals(GROUP1_NAME)) {
					return group1Data;
				} else if (entry.getPeerGroupName().equals(GROUP2_NAME)) {
					return group2Data;
				} else {
					return group3Data;
				}
			}
			
			@Override
			PeerGroupData getGeoRedunduncyPeerGroupDataFor(DiameterExternalCommunicationEntryData entry) {
				return null;
			}
		};
		
		handler.init();
		executor = new RadiusRequestExecutor<ApplicationRequest, ApplicationResponse>(handler, applicationRequest, applicationResponse);
	}

	private void setUpStackContext() {

		PeerDataImpl data = new PeerDataImpl();
		data.setPeerName(PEER1_NAME);
		data.setHostIdentity(PEER1_IDENTITY);
		data.setRealmName(TEST_REALM);
		addPeer(data);

		data.setPeerName(PEER2_NAME);
		data.setHostIdentity(PEER2_IDENTITY);
		data.setRealmName(TEST2_REALM);
		addPeer(data);

		data.setPeerName(PEER3_NAME);
		data.setHostIdentity(PEER3_IDENTITY);
		data.setRealmName(TEST3_REALM);
		addPeer(data);


		data.setPeerName(INITIATOR_PEER_NAME);
		data.setHostIdentity(INITIATOR_PEER_IDENTITY);
		data.setRealmName(INITIATOR_TEST_REALM);
		addPeer(data);

		peerOperation1 = getPeerOperation(PEER1_IDENTITY);
		peerOperation2 = getPeerOperation(PEER2_IDENTITY);
		peerOperation3 = getPeerOperation(PEER3_IDENTITY);
		initiatorPeerOperation = getPeerOperation(INITIATOR_PEER_IDENTITY);

	}

	public class GivenPriorityResultCodeListIsConfiguredfinalResponseContainsHighestPriorityResultCodeAnd {
		
		@Before
		public void setup() throws InitializationFailedException {
			
		}
		
		@Test
		public void attributesOfAllPositiveResponsesArePresentInFinalPacket() {
			setPriorityResultCode(ResultCode.DIAMETER_MULTI_ROUND_AUTH,
					ResultCode.DIAMETER_SUCCESS,ResultCode.DIAMETER_LIMITED_SUCCESS);
			
			sendRequest();
			
			remoteResponseReceivedFrom(peerOperation1, havingMultiRoundAuth, getSession());
			remoteResponseReceivedFrom(peerOperation2, havingLimitedSuccess, getSession());
			remoteResponseReceivedFrom(peerOperation3, havingDiameterSuccess, getSession());
			
			DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).
				hasResultCode(ResultCode.DIAMETER_LIMITED_SUCCESS).
				containsAVP(AVP_IN_DIAMETER_SUCCESS_PACKET).
				containsAVP(AVP_IN_LIMITED_SUCCESS_PACKET).
				containsAVP(AVP_IN_MULTI_ROUND_AUTH_PACKET);
		}
		
		@Test
		public void attributesOfPositiveResponsesAreAggregatedWhileThatOfNegativeResponseAreNot() {
			setPriorityResultCode(ResultCode.DIAMETER_MULTI_ROUND_AUTH,
					ResultCode.DIAMETER_SUCCESS,ResultCode.DIAMETER_LIMITED_SUCCESS);
			
			sendRequest();
			
			remoteResponseReceivedFrom(peerOperation1, havingMultiRoundAuth, getSession());
			remoteResponseReceivedFrom(peerOperation2, havingDiameterTooBusy, getSession());
			remoteResponseReceivedFrom(peerOperation3, havingDiameterSuccess, getSession());
			
			DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).
				hasResultCode(ResultCode.DIAMETER_SUCCESS).
				containsAVP(AVP_IN_MULTI_ROUND_AUTH_PACKET).
				containsAVP(AVP_IN_DIAMETER_SUCCESS_PACKET).
				doesNotContainAVP(AVP_IN_DIAMETER_TOO_BUSY_PACKET);
		}
		
		@Test
		public void responseWhoseResultCodeIsNotPresentInPriorityCodeListIsNotAggregated () {
			setPriorityResultCode( ResultCode.DIAMETER_MULTI_ROUND_AUTH,
								ResultCode.DIAMETER_LIMITED_SUCCESS);
			
			sendRequest();
			
			remoteResponseReceivedFrom(peerOperation1, havingDiameterSuccess, getSession());
			remoteResponseReceivedFrom(peerOperation2, havingLimitedSuccess, getSession());
			remoteResponseReceivedFrom(peerOperation3, havingMultiRoundAuth, getSession());
			
			DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).
				hasResultCode(DIAMETER_LIMITED_SUCCESS).
				containsAVP(AVP_IN_LIMITED_SUCCESS_PACKET).
				containsAVP(AVP_IN_MULTI_ROUND_AUTH_PACKET).
				doesNotContainAVP(AVP_IN_DIAMETER_SUCCESS_PACKET);
		}
		
		@Test
		public void highestPriorityNegativeResponsesPacketIsSentAsItIs() {
			setPriorityResultCode( ResultCode.DIAMETER_TOO_BUSY,
					ResultCode.DIAMETER_OUT_OF_SPACE, ResultCode.DIAMETER_AVP_UNSUPPORTED);
			
			sendRequest();
			
			remoteResponseReceivedFrom(peerOperation1, havingDiameterTooBusy, getSession());
			remoteResponseReceivedFrom(peerOperation2, havingAvpUnsupported, getSession());
			remoteResponseReceivedFrom(peerOperation3, havingDiameterOutOfSpace, getSession());
			
			DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).
				hasResultCode(ResultCode.DIAMETER_AVP_UNSUPPORTED).
				containsAVP(AVP_IN_DIAMETER_AVP_UNSUPPORTED).
				doesNotContainAVP(AVP_IN_DIAMETER_OUT_OF_SPACE).
				doesNotContainAVP(AVP_IN_DIAMETER_TOO_BUSY_PACKET);
			
		}
		
		@Test
		public void ignoresTheResponsesWhoseResultCodeIsConfiguredInAcceptedResultCode() {
			setPriorityResultCode( ResultCode.DIAMETER_TOO_BUSY,
					ResultCode.DIAMETER_OUT_OF_SPACE, ResultCode.DIAMETER_AVP_UNSUPPORTED);
			group2Entry.getAcceptableResultCodes().add(ResultCode.DIAMETER_AVP_UNSUPPORTED.code);
			
			sendRequest();
			
			remoteResponseReceivedFrom(peerOperation1, havingDiameterTooBusy, getSession());
			remoteResponseReceivedFrom(peerOperation2, havingAvpUnsupported, getSession());
			remoteResponseReceivedFrom(peerOperation3, havingDiameterOutOfSpace, getSession());
			
			DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).
				hasResultCode(ResultCode.DIAMETER_OUT_OF_SPACE).
				containsAVP(AVP_IN_DIAMETER_OUT_OF_SPACE).
				doesNotContainAVP(AVP_IN_DIAMETER_AVP_UNSUPPORTED).
				doesNotContainAVP(AVP_IN_DIAMETER_TOO_BUSY_PACKET);
			
		}
		
		@Test
		public void PriorityListMayContainResultCodeCategoryAsWell() {
			data.setStrPriorityResultCodes(ResultCode.DIAMETER_AVP_UNSUPPORTED.code + "," + ResultCodeCategory.RC3XXX.value);
			data.postRead();
			System.out.println(data.getStrPriorityResultCodes());
			
			sendRequest();
			
			remoteResponseReceivedFrom(peerOperation1, havingAvpUnsupported, getSession());
			remoteResponseReceivedFrom(peerOperation2, havingDiameterTooBusy, getSession());
			remoteResponseReceivedFrom(peerOperation3, havingDiameterSuccess, getSession());
			
			DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).
				hasResultCode(ResultCode.DIAMETER_TOO_BUSY).
				containsAVP(AVP_IN_DIAMETER_TOO_BUSY_PACKET).
				doesNotContainAVP(AVP_IN_DIAMETER_AVP_UNSUPPORTED).
				doesNotContainAVP(AVP_IN_DIAMETER_SUCCESS_PACKET);
		}
		
		private void setPriorityResultCode(ResultCode ... codes) {
			String priorityResultCodeList = "";
			for(ResultCode resultCode : codes) {
				priorityResultCodeList += resultCode.code;
				priorityResultCodeList += ",";
			}
			
			data.setStrPriorityResultCodes(priorityResultCodeList);
			data.postRead();
		}
		
	}
	
	public class whenPriorityResultCodeFieldIsEmpty {

		@Test
		public void aggregatesAllPositiveResponseReplacingResultCodeWithLatestOne() {
			sendRequest();

			remoteResponseReceivedFrom(peerOperation1, havingDiameterSuccess, getSession());
			remoteResponseReceivedFrom(peerOperation2, havingMultiRoundAuth, getSession());
			remoteResponseReceivedFrom(peerOperation3, havingLimitedSuccess, getSession());

			DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).
			hasResultCode(DIAMETER_LIMITED_SUCCESS).
			containsAVP(AVP_IN_LIMITED_SUCCESS_PACKET).
			containsAVP(AVP_IN_DIAMETER_SUCCESS_PACKET).
			containsAVP(AVP_IN_MULTI_ROUND_AUTH_PACKET);
		}
		
		@Test
		public void sendsFirstNegativeResponseReceived() {
			
			sendRequest();
			
			remoteResponseReceivedFrom(peerOperation1, havingDiameterSuccess, getSession());
			remoteResponseReceivedFrom(peerOperation2, havingAvpUnsupported, getSession());
			remoteResponseReceivedFrom(peerOperation3, havingDiameterTooBusy, getSession());
			
			DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).
				hasResultCode(ResultCode.DIAMETER_AVP_UNSUPPORTED).
				containsAVP(AVP_IN_DIAMETER_AVP_UNSUPPORTED).
				doesNotContainAVP(AVP_IN_DIAMETER_SUCCESS_PACKET).
				doesNotContainAVP(AVP_IN_DIAMETER_TOO_BUSY_PACKET);
		}
		
		@Test
		public void ignoresResponseWhoseResultCodeIsConfiguredInAcceptedResultCode() {
			
			group2Entry.getAcceptableResultCodes().add(ResultCode.DIAMETER_AVP_UNSUPPORTED.code);
			
			sendRequest();
			
			remoteResponseReceivedFrom(peerOperation1, havingDiameterSuccess, getSession());
			remoteResponseReceivedFrom(peerOperation2, havingAvpUnsupported, getSession());
			remoteResponseReceivedFrom(peerOperation3, havingDiameterTooBusy, getSession());
			
			DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).
				hasResultCode(ResultCode.DIAMETER_TOO_BUSY).
				containsAVP(AVP_IN_DIAMETER_TOO_BUSY_PACKET).
				doesNotContainAVP(AVP_IN_DIAMETER_SUCCESS_PACKET).
				doesNotContainAVP(AVP_IN_DIAMETER_AVP_UNSUPPORTED);
			
		}

	}
	
	private void sendRequest() {
		applicationRequest.setExecutor(executor);
		executor.startRequestExecution(getSession());
	}
	
	private void remoteResponseReceivedFrom(DiameterPeerSpy peerOperation, DiameterAnswer diameterAnswer, DiameterSession session) {
		peerOperation.getDiameterPeer().getResponseListener().responseReceived(diameterAnswer, peerOperation.getDiameterPeer().getHostIdentity(), session);
	}
}
