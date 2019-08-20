package com.elitecore.aaa.radius.service.auth.policy.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.impl.DummyAAAServerConfigurationImpl;
import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.aaa.radius.conf.RadESConfiguration;
import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.aaa.radius.conf.impl.RadiusESIGroupConfigurable;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyData;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.AsyncGroupCommunicationEntryData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.GroupBroadcastCommunicationHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadiusEsiGroupData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.aaa.radius.session.SessionsFactory;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.DummyAAAServerContext;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadUDPCommunicatorManagerImpl;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusESIGroupFactory;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusESIGroupTestSupport;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusEsiGroup.EsiType;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusEsiGroup.RedundancyMode;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.UDPCommunicatorSpy;
import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class AuthGroupBroadcastHandlerTest {

	private static final String SESSION_ID_1 = "1";
	private static final int LOAD_FACTOR_1 = 1;

	private AuthGroupBroadcastHandler handler;
	private GroupBroadcastCommunicationHandlerData handlerData;
	private DummyAAAServerContext dummyServerContext = new DummyAAAServerContext();
	private DummyAAAServerConfigurationImpl dummyServerConfig = new DummyAAAServerConfigurationImpl();
	private RadiusServicePolicyData radiusServicePolicyData = new RadiusServicePolicyData();

	private RadiusESIGroupConfigurable radiusEsiGroupConfigurable = new RadiusESIGroupConfigurable();
	private RadiusEsiGroupData groupData1, groupData2;
	private DefaultExternalSystemData primaryEsi1, primaryEsi2;
	private UDPCommunicatorSpy primaryEsi1Communicator, primaryEsi2Communicator;

	private ISession session1;
	private RadAuthRequest request;
	private RadAuthResponse response;
	private RadAuthRequestBuilder radAuthBuilder;
	private InOrder order;

	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	@Mock RadAuthServiceContext authServiceContext;
	@Mock SessionsFactory radiusSessionFactory;
	@Mock RadESConfiguration radESConfiguration;
	@Mock RadUDPCommunicatorManagerImpl radUDPCommunicatorManagerImpl;
	@Mock RadClientData clientData;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		session1 = new HazelcastRadiusSession(SESSION_ID_1, radiusSessionFactory, new ArrayList<ImdgIndexDetail>(), new ArrayList<>());

		createRequestAndResponse();

		primaryEsi1 = RadiusESIGroupTestSupport.createESI().setEsiName("primaryEsi1").
				setUUID("primaryEsi1").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:1812").
				getEsiData();

		primaryEsi2 = RadiusESIGroupTestSupport.createESI().setEsiName("primaryEsi2").
				setUUID("primaryEsi2").
				setESIType(RadESTypeConstants.RAD_AUTH_PROXY.type).
				setStringIpAddress("127.0.0.1:2812").
				getEsiData();

		groupData1 = RadiusESIGroupTestSupport.createESIGroupData().groupName("esigroup1").
				isStatefulEnable(true).
				isSwitchBackEnable(false).
				esiType(EsiType.AUTH.typeName).
				redundancyMode(RedundancyMode.NM.redundancyModeName).
				addPrimaryEsiIdWithLoadFactor("primaryEsi1",LOAD_FACTOR_1).
				radiusGroupData;

		groupData2 = RadiusESIGroupTestSupport.createESIGroupData().groupName("esigroup2").
				isStatefulEnable(true).
				isSwitchBackEnable(false).
				esiType(EsiType.AUTH.typeName).
				redundancyMode(RedundancyMode.NM.redundancyModeName).
				addPrimaryEsiIdWithLoadFactor("primaryEsi2",LOAD_FACTOR_1).
				radiusGroupData;

		radiusServicePolicyData.setName("policy1");

		primaryEsi1Communicator = new RadiusESIGroupTestSupport().getCommunicator(primaryEsi1);
		primaryEsi2Communicator = new RadiusESIGroupTestSupport().getCommunicator(primaryEsi2);
		
		order = inOrder(primaryEsi1Communicator.getCommunicator(), primaryEsi2Communicator.getCommunicator());

		radiusEsiGroupConfigurable.getEsiGroups().add(groupData1);
		radiusEsiGroupConfigurable.getEsiGroups().add(groupData2);
		radiusEsiGroupConfigurable.postRead();

		dummyServerConfig.setRadiusEsiGroupConfigurable(radiusEsiGroupConfigurable);
		dummyServerConfig.setRadESConfiguration(radESConfiguration);
		dummyServerContext.setServerConfiguration(dummyServerConfig);
		dummyServerContext.setRadUdpCommunicatorManager(radUDPCommunicatorManagerImpl);
		dummyServerContext.setRadiusESIGroupFactory(new RadiusESIGroupFactory());

		when(authServiceContext.getServerContext()).thenReturn(dummyServerContext);
		when(radESConfiguration.getESDataByName("primaryEsi1")).thenReturn(Optional.of(primaryEsi1));
		when(radESConfiguration.getESDataByName("primaryEsi2")).thenReturn(Optional.of(primaryEsi2));

		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(primaryEsi1.getUUID(),
				dummyServerContext, primaryEsi1)).thenReturn(primaryEsi1Communicator.getCommunicator());
		when(radUDPCommunicatorManagerImpl.findCommunicatorByIDOrCreate(primaryEsi2.getUUID(),
				dummyServerContext, primaryEsi2)).thenReturn(primaryEsi2Communicator.getCommunicator());

		handlerData = readHandlerDataFromXML("stateful/broadcast/handler/broadcast-handler-wait-for-response-enable.xml");
		handlerData.setRadiusServicePolicyData(radiusServicePolicyData);
		handler = new AuthGroupBroadcastHandler(authServiceContext, handlerData);

	}
	
	@Test
	public void broadcastHandlerIsAlwaysEligible() throws InitializationFailedException {
		handler.init();
		request = radAuthBuilder.build();
		
		assertTrue(handler.isEligible(request, response));
	}

	public class WaitForResponseEnableInAllEntry {

		@Before
		public void setUp() throws Exception {
			handler.init();
		}

		@Test
		public void entryWillApplicableIfConfiguredRuleSetIsSatisfied() throws InvalidAttributeIdException {
			request = radAuthBuilder.addAttribute(RadiusAttributeConstants.USER_NAME_STR, "test1").buildRequest();

			RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> executor = new RadiusRequestExecutor<RadAuthRequest, RadAuthResponse>(handler, request, response);
			request.setExecutor(executor);

			executor.startRequestExecution(session1);

			primaryEsi1Communicator.verifyRequestNotReceived();

			primaryEsi2Communicator.verifyRequestReceipt().sendsAccessAccept();
		}

		@Test
		public void resumeRequestExecutionOnlyWhenResponseReceivedFromAllConfiguredEntry() throws InvalidAttributeIdException {
			request = radAuthBuilder.addAttribute(RadiusAttributeConstants.USER_NAME_STR, "test").buildRequest();
			RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> executor = new RadiusRequestExecutor<RadAuthRequest, RadAuthResponse>(handler, request, response);
			request.setExecutor(executor);

			executor.startRequestExecution(session1);

			primaryEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

			verify(authServiceContext, times(0)).submitAsyncRequest(Mockito.any(RadAuthRequest.class), Mockito.any(RadAuthResponse.class), Mockito.any(AsyncRequestExecutor.class));

			primaryEsi2Communicator.verifyRequestReceipt().sendsAccessAccept();

			verify(authServiceContext).submitAsyncRequest(Mockito.any(RadAuthRequest.class), Mockito.any(RadAuthResponse.class), Mockito.any(AsyncRequestExecutor.class));
		}
		
		@Test
		public void eligibleForDefaultResponseBehaviorIfAnyOfGroupIsDeadMarked() {
			assertFalse(handler.isResponseBehaviorApplicable());
			
			primaryEsi1Communicator.markDead();
			
			assertTrue(handler.isResponseBehaviorApplicable());
		}
	}

	public class WaitForResponseDisableInAllEntry {

		@Before
		public void setUp() throws JAXBException, InitializationFailedException, UnsupportedEncodingException, FileNotFoundException {
			
			for (AsyncGroupCommunicationEntryData entryData: handlerData.getProxyCommunicationEntries()) {
				entryData.setWait(false);
			}
			
			handler.init();
		}

		@Test
		public void entryWillApplicableIfConfiguredRuleSetIsSatisfied() throws InvalidAttributeIdException {
			request = radAuthBuilder.addAttribute(RadiusAttributeConstants.USER_NAME_STR, "test1").buildRequest();

			RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> executor = new RadiusRequestExecutor<RadAuthRequest, RadAuthResponse>(handler, request, response);
			request.setExecutor(executor);

			executor.startRequestExecution(session1);

			primaryEsi1Communicator.verifyRequestNotReceived();

			primaryEsi2Communicator.verifyRequestReceipt().sendsAccessAccept();
		}

		@Test
		public void willNotWaitForResponseAndStartFurtherExecution() throws InvalidAttributeIdException {
			request = radAuthBuilder.addAttribute(RadiusAttributeConstants.USER_NAME_STR, "test").buildRequest();
			RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> executor = new RadiusRequestExecutor<RadAuthRequest, RadAuthResponse>(handler, request, response);
			request.setExecutor(executor);

			executor.startRequestExecution(session1);

			primaryEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();
			primaryEsi2Communicator.verifyRequestReceipt().sendsAccessAccept();

			verify(authServiceContext, times(0)).submitAsyncRequest(Mockito.any(RadAuthRequest.class), Mockito.any(RadAuthResponse.class), Mockito.any(AsyncRequestExecutor.class));
		}
		
		@Test
		public void notEligibleForDefaultResponseBehaviorEvenIfAllGroupsAreDeadMarked() {
			
			assertFalse(handler.isResponseBehaviorApplicable());
			
			primaryEsi1Communicator.markDead();
			primaryEsi2Communicator.markDead();
			
			assertFalse(handler.isResponseBehaviorApplicable());
		}
	}

	public class MixEntryWithWaitForResponseEnableAndDisable{

		@Before
		public void setUp() throws InitializationFailedException {
			
			// enable wait for response true for first entry of handler and disable for second entry of handler
			 handlerData.getProxyCommunicationEntries().get(0).setWait(true);
			 handlerData.getProxyCommunicationEntries().get(1).setWait(false);
			
			handler.init();
		}

		@Test
		public void entryWillApplicableIfConfiguredRuleSetIsSatisfied() throws InvalidAttributeIdException {
			request = radAuthBuilder.addAttribute(RadiusAttributeConstants.USER_NAME_STR, "test1").buildRequest();

			RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> executor = new RadiusRequestExecutor<RadAuthRequest, RadAuthResponse>(handler, request, response);
			request.setExecutor(executor);

			executor.startRequestExecution(session1);

			primaryEsi1Communicator.verifyRequestNotReceived();

			primaryEsi2Communicator.verifyRequestReceipt().sendsAccessAccept();
		}

		@Test
		public void firstSendRequestToAllEntriesWhereWaitForResponseIsDisableAndThenToOtherEntries() throws InvalidAttributeIdException {

			request = radAuthBuilder.addAttribute(RadiusAttributeConstants.USER_NAME_STR, "test").buildRequest();
			RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> executor = new RadiusRequestExecutor<RadAuthRequest, RadAuthResponse>(handler, request, response);
			request.setExecutor(executor);

			executor.startRequestExecution(session1);

			order.verify(primaryEsi2Communicator.getCommunicator()).handleUDPRequest(Mockito.any(UDPRequest.class));

			order.verify(primaryEsi1Communicator.getCommunicator()).handleUDPRequest(Mockito.any(UDPRequest.class));

		}

		@Test
		public void resumeFurtherExecutionOnlyWhenResponseIsReceivedFromAllEntryWhereWailForResponseIsEnable() throws InvalidAttributeIdException {
			request = radAuthBuilder.addAttribute(RadiusAttributeConstants.USER_NAME_STR, "test").buildRequest();
			RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> executor = new RadiusRequestExecutor<RadAuthRequest, RadAuthResponse>(handler, request, response);
			request.setExecutor(executor);

			executor.startRequestExecution(session1);

			primaryEsi2Communicator.verifyRequestReceipt().sendsAccessAccept();

			verify(authServiceContext, times(0)).submitAsyncRequest(Mockito.any(RadAuthRequest.class), Mockito.any(RadAuthResponse.class), Mockito.any(AsyncRequestExecutor.class));

			primaryEsi1Communicator.verifyRequestReceipt().sendsAccessAccept();

			verify(authServiceContext, times(1)).submitAsyncRequest(Mockito.any(RadAuthRequest.class), Mockito.any(RadAuthResponse.class), Mockito.any(AsyncRequestExecutor.class));
		}
		
		@Test
		public void eligibleForDefaultResponseBehaviorForEntryWhereWaitForResonseIsEnableAndGroupInThatEntryIsDeadMarked() {
			assertFalse(handler.isResponseBehaviorApplicable());
			
			primaryEsi2Communicator.markDead();
			
			assertFalse(handler.isResponseBehaviorApplicable());
			
			primaryEsi1Communicator.markDead();
			
			assertTrue(handler.isResponseBehaviorApplicable());
		}
	}

	private GroupBroadcastCommunicationHandlerData readHandlerDataFromXML(String filePath) throws JAXBException, UnsupportedEncodingException, FileNotFoundException {
		ClasspathResource resource = ClasspathResource.at(filePath);
		File file = new File(resource.getAbsolutePath());

		JAXBContext context = JAXBContext.newInstance(GroupBroadcastCommunicationHandlerData.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (GroupBroadcastCommunicationHandlerData) unmarshaller.unmarshal(file);
	}

	private void createRequestAndResponse() throws Exception {

		radAuthBuilder = new RadAuthRequestBuilder()
				.packetType(RadiusConstants.ACCESS_REQUEST_MESSAGE);

		when(clientData.getSharedSecret(anyInt())).thenReturn("secret");
		response = new RadAuthRequestBuilder()
				.addAttribute("0:18", AuthReplyMessageConstant.AUTHENTICATION_SUCCESS)
				.packetType(2)
				.buildResponse();

		response.setClientData(clientData);
	}

}