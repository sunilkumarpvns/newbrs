package com.elitecore.aaa.diameter.service.application.handlers;

import static com.elitecore.diameterapi.diameter.common.util.constant.ResultCode.DIAMETER_LIMITED_SUCCESS;
import static com.elitecore.diameterapi.diameter.common.util.constant.ResultCode.DIAMETER_SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.conf.impl.PeerGroupData;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterExternalCommunicationEntryData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterSynchronousCommunicationHandlerData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.api.DiameterPeerSpy;
import com.elitecore.diameterapi.diameter.common.routerx.agent.DiameterTestSupport;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

// TODO add test case for multiple communication and check that if first sends negative result code then second communication entry should not be checked for eligibility and final answer should be converted into negative response
@RunWith(HierarchicalContextRunner.class)
public class DiaToDiaProxyHandlerTest extends DiameterTestSupport {

	private static final String TEST_REALM = "test1.com";
	private static final String PEER1_IDENTITY = "peer1.test1.com";
	private static final String PEER1_NAME = "peer1";
	private static final String GROUP1_NAME = "GROUP1_NAME";
	
	private static final String TEST2_REALM = "test2.com";
	private static final String PEER2_IDENTITY = "peer2.test2.com";
	private static final String PEER2_NAME = "peer2";
	private static final String GROUP2_NAME = "GROUP2_NAME";
	
	private static final String INITIATOR_PEER_IDENTITY = "initiator.test3.com";
	private static final String INITIATOR_PEER_NAME = "initiator";
	private static final String INITIATOR_TEST_REALM = "test3.com";
	
	@Mock private DiameterServiceContext serviceContext;
	
	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();

	private DiameterSynchronousCommunicationHandlerData data = new DiameterSynchronousCommunicationHandlerData() {

		@Override
		public void postRead() {
			List<Integer> priorityResultCodes;
			priorityResultCodes = Collectionz.map(Strings.splitter(',').split(getStrPriorityResultCodes()), Strings.toInt());

			for (DiameterExternalCommunicationEntryData data : getEntries()) {
				data.setPriorityResultCodes(priorityResultCodes);
				data.setProtocol(getProtocol());
			}
		}
	};
		
	private DiaToDiaProxyHandler handler;

	private PeerGroupData group1Data;
	private PeerGroupData group2Data;
	private DiameterPeerSpy initiatorPeerOperation;
	private DiameterPeerSpy peerOperation1;
	private DiameterPeerSpy peerOperation2;
	
	private DiameterRequest request;
	private ApplicationRequest applicationRequest;
	private ApplicationResponse applicationResponse;
	private RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> executor;
	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);
		
		DiameterExternalCommunicationEntryData group1Entry = new DiameterExternalCommunicationEntryData();
		group1Entry.setPeerGroupId("0");
		group1Entry.setPeerGroupName(GROUP1_NAME);
		group1Entry.setPolicyName("test");
		
		PeerInfoImpl peerInfo = new PeerInfoImpl();
		peerInfo.setPeerName(PEER1_NAME);
		peerInfo.setLoadFactor(1);
		
		group1Data = new PeerGroupData();
		group1Data.setName(GROUP1_NAME);
		group1Data.getPeers().add(peerInfo);
		
		data.getEntries().add(group1Entry);
		
		DiameterExternalCommunicationEntryData group2Entry = new DiameterExternalCommunicationEntryData();
		group2Entry.setPeerGroupId("0");
		group2Entry.setPeerGroupName(GROUP2_NAME);
		group2Entry.setPolicyName("test");
		
		peerInfo = new PeerInfoImpl();
		peerInfo.setPeerName(PEER2_NAME);
		peerInfo.setLoadFactor(1);
		
		group2Data = new PeerGroupData();
		group2Data.setName(GROUP2_NAME);
		group2Data.getPeers().add(peerInfo);
		
		data.getEntries().add(group2Entry);
		
		setUpStackContext();
		
		when(serviceContext.getStackContext()).thenReturn(getStackContext());
		
		request = createCCInitialRequest();
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.DESTINATION_HOST, request, initiatorPeerOperation.getDiameterPeer().getHostIdentity());
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.DESTINATION_REALM, request, initiatorPeerOperation.getDiameterPeer().getRealm());
		
		applicationRequest = new ApplicationRequest(request);
		applicationResponse = new ApplicationResponse(request);
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
    	
    	
    	data.setPeerName(INITIATOR_PEER_NAME);
    	data.setHostIdentity(INITIATOR_PEER_IDENTITY);
    	data.setRealmName(INITIATOR_TEST_REALM);
    	addPeer(data);

    	peerOperation1 = getPeerOperation(PEER1_IDENTITY);
    	peerOperation2 = getPeerOperation(PEER2_IDENTITY);
    	initiatorPeerOperation = getPeerOperation(INITIATOR_PEER_IDENTITY);
	}
	
	public class NoTranslationWhileProxy {

		@Before
		public void setUp() throws InitializationFailedException {
			handler = new DiaToDiaProxyHandler(serviceContext, data, mock(ITranslationAgent.class)) {
				@Override
				PeerGroupData getPeerGroupDataFor(DiameterExternalCommunicationEntryData entry) {
					if (entry.getPeerGroupName().equals(GROUP1_NAME)) {
						return group1Data;
					} else {
						return group2Data;
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
		
		@Test
		public void handleRequest_ShouldProxyDiameterRequestWithoutTranslationToDestinationGroup() {
			DiameterRequest request = createCCInitialRequest();
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.DESTINATION_HOST, request, initiatorPeerOperation.getDiameterPeer().getHostIdentity());
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.DESTINATION_REALM, request, initiatorPeerOperation.getDiameterPeer().getRealm());
			
			ApplicationRequest applicationRequest = new ApplicationRequest(request);
			ApplicationResponse applicationResponse = createApplicationResponse(request);
			

			RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> executor = new RadiusRequestExecutor<ApplicationRequest, ApplicationResponse>(handler, applicationRequest, applicationResponse);

			applicationRequest.setExecutor(executor);
			
			executor.startRequestExecution(getSession());
			
			DiameterAnswer remoteAnswer1 = createSuccessAnswerFrom(peerOperation1);
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.FRAMED_IP_ADDRESS, remoteAnswer1, "1.1.1.1");
			
			remoteResponseReceivedFrom(peerOperation1, remoteAnswer1, getSession());
			verify(serviceContext, times(1)).resumeRequestInSync(Mockito.any(DiameterSession.class),
					Mockito.eq(applicationRequest), Mockito.eq(applicationResponse));
			
			applicationResponse.setFurtherProcessingRequired(true);
			executor.resumeRequestExecution(getSession());
			
			DiameterAnswer remoteAnswer2 = createSuccessAnswerFrom(peerOperation2);
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.FRAMED_IP_ADDRESS, remoteAnswer2, "2.2.2.2");
			remoteResponseReceivedFrom(peerOperation2, remoteAnswer2, getSession());
			verify(serviceContext, times(2)).resumeRequestInSync(Mockito.any(DiameterSession.class),
					Mockito.eq(applicationRequest), Mockito.eq(applicationResponse));
			
			assertEquals(2, applicationResponse.getDiameterAnswer().getAVPList(DiameterAVPConstants.FRAMED_IP_ADDRESS).size());
			assertEquals("1.1.1.1", applicationResponse.getDiameterAnswer().getAVPList(DiameterAVPConstants.FRAMED_IP_ADDRESS).get(0).getStringValue());
			assertEquals("2.2.2.2", applicationResponse.getDiameterAnswer().getAVPList(DiameterAVPConstants.FRAMED_IP_ADDRESS).get(1).getStringValue());
		}
	}
	
	private ApplicationResponse createApplicationResponse(DiameterRequest request) {
		ApplicationResponse applicationResponse = new ApplicationResponse(request);
		applicationResponse.getDiameterAnswer().addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_SUCCESS.code + "");
		return applicationResponse;
	}
	
	public class WithPriorityResultCode {
		
		private DiameterAnswer havingLimitedSuccess;
		private DiameterAnswer havingDiameterSuccess;
		
		@Before
		public void setUp() throws InitializationFailedException {
			havingLimitedSuccess = new DiameterAnswer(request, ResultCode.DIAMETER_LIMITED_SUCCESS);
			havingDiameterSuccess = new DiameterAnswer(request, ResultCode.DIAMETER_SUCCESS);
			
			handler = new DiaToDiaProxyHandler(serviceContext, data, mock(ITranslationAgent.class)) {
				@Override
				PeerGroupData getPeerGroupDataFor(DiameterExternalCommunicationEntryData entry) {
					if (entry.getPeerGroupName().equals(GROUP1_NAME)) {
						return group1Data;
					} else {
						return group2Data;
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
		
		@Test
		public void finalAnswerHasTheResultCodeHavingHighestPriority() {
			setPriorityResultCode(DIAMETER_SUCCESS.code, DIAMETER_LIMITED_SUCCESS.code);
			
			sendRequest();
			
			remoteResponseReceivedFrom(peerOperation1, havingLimitedSuccess, getSession());
			
			resumeFurtherExecution();
			
			remoteResponseReceivedFrom(peerOperation2, havingDiameterSuccess, getSession());
			
			DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_LIMITED_SUCCESS);
		}

		private void setPriorityResultCode(int... codes) {
			String priorityResultCodeString =""; 
			for(int code : codes) {
				priorityResultCodeString += code;
				priorityResultCodeString += ",";
			}
			data.setStrPriorityResultCodes(priorityResultCodeString);
			data.postRead();
		}

		private void resumeFurtherExecution() {
			applicationResponse.setFurtherProcessingRequired(true);
			executor.resumeRequestExecution(getSession());
		}

		private void sendRequest() {
			applicationRequest.setExecutor(executor);
			
			executor.startRequestExecution(getSession());
		}
	}
	
	private void remoteResponseReceivedFrom(DiameterPeerSpy peerOperation, DiameterAnswer diameterAnswer, DiameterSession session) {
		peerOperation.getDiameterPeer().getResponseListener().responseReceived(diameterAnswer, peerOperation.getDiameterPeer().getHostIdentity(), session);
	}
}
