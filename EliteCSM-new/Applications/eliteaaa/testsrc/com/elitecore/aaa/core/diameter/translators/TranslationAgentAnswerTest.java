package com.elitecore.aaa.core.diameter.translators;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.junit.BeforeClass;
import org.junit.Test;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.plugins.PluginRequestHandler;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurationDetail;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAsyncRequestExecutor;
import com.elitecore.aaa.diameter.translators.DiameterRatingTranslator;
import com.elitecore.aaa.diameter.translators.DiameterTranslator;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.tls.EliteSSLContextFactory;
import com.elitecore.core.serverx.internal.tasks.CallableSingleExecutionAsyncTask;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.diameterapi.core.common.PolicyDataRegistrationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.transport.INetworkConnector;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.impl.MappingDataImpl;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslationDetailImpl;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorPolicyDataImpl;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.DiameterStackContext;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;

public class TranslationAgentAnswerTest {
	static TranslationAgent agent;

	static DiameterAnswer diameterAnswer;
	static DiameterServiceContext context;
	static IStackContext stackContext;

	@BeforeClass
	public static void setup(){
		diameterAnswer = new DiameterAnswer();

		DummyDiameterDictionary.getInstance();
		byte b[] = new byte[]{0x01, 0x00, 0x00, (byte) 0xd8, 0x00, 0x00, 0x01, 0x01, 
				0x00, 0x00, 0x00, 0x00, 0x3c, 0x48, 0x3a, (byte) 0xbf, 
				0x4a, 0x42, 0x53, 0x53, 0x00, 0x00, 0x01, 0x02, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x00, 0x04, 
				0x00, 0x00, 0x01, 0x08, 0x60, 0x00, 0x00, 0x1e, 
				0x64, 0x69, 0x61, 0x6d, 0x65, 0x74, 0x65, 0x72, 
				0x2e, 0x65, 0x6c, 0x69, 0x74, 0x65, 0x63, 0x6f, 
				0x72, 0x65, 0x2e, 0x63, 0x6f, 0x6d, 0x00, 0x00, 
				0x00, 0x00, 0x01, 0x28, 0x60, 0x00, 0x00, 0x15, 
				0x65, 0x6c, 0x69, 0x74, 0x65, 0x63, 0x6f, 0x72, 
				0x65, 0x2e, 0x63, 0x6f, 0x6d, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x01, 0x0c, 0x60, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x07, (byte) 0xd1, 0x00, 0x00, 0x01, 0x01, 
				0x60, 0x00, 0x00, 0x0e, 0x00, 0x01, 0x7f, 0x00, 
				0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x01, 0x0a, 
				0x60, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x52, 0x4b, 
				0x00, 0x00, 0x01, 0x0d, 0x00, 0x00, 0x00, 0x10, 
				0x45, 0x6c, 0x69, 0x74, 0x65, 0x41, 0x41, 0x41, 
				0x00, 0x00, 0x01, 0x02, 0x60, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x01, 0x09, 
				0x60, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x28, (byte) 0xaf, 
				0x00, 0x00, 0x01, 0x09, 0x60, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x00, 0x09, 0x00, 0x00, 0x01, 0x09, 
				0x60, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x0d, (byte) 0xe9, 
				0x00, 0x00, 0x01, 0x09, 0x60, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x00, 0x5e, 0x00, 0x00, 0x01, 0x09, 
				0x60, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x09, 0x30};

		InputStream ips = new DataInputStream(new ByteArrayInputStream(b));

		try {
			diameterAnswer.readFrom(ips);
		} catch (IOException e) {
			System.out.println("error reading packet");
		}

		context = new DiameterServiceContext(){

			@Override
			public DiameterServiceConfigurationDetail getDiameterServiceConfigurationDetail() {
				return null;
			}

			@Override
			public ESCommunicator getDriver(String driverInstanceId) {
				return null;
			}

			@Override
			public AAAServerContext getServerContext() {
				return null;
			}

			@Override
			public PluginRequestHandler createPluginRequestHandler(
					List<PluginEntryDetail> prePluginList, List<PluginEntryDetail> postPluginList) {
				return null;
			}

			@Override
			public DriverConfiguration getDriverConfiguration(
					String driverInstanceId) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ServicePolicy<ApplicationRequest> selectServicePolicy(ApplicationRequest serviceRequest) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public DiameterStackContext getStackContext() {
				return null;
			}

			@Override
			public void resumeRequestInAsync(Session session, ApplicationRequest request, ApplicationResponse response,
					DiameterAsyncRequestExecutor unitOfWork) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void resumeRequestInAsync(Session session, ApplicationRequest request,
					ApplicationResponse response) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void resumeRequestInSync(DiameterSession originatorSession, ApplicationRequest originalRequest,
					ApplicationResponse originalResponse) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public SessionReleaseIndiactor getSessionReleaseIndicator() {
				// TODO Auto-generated method stub
				return null;
			}

			};

			stackContext = new IStackContext(){

				@Override
				public void finalPreResponseProcess(DiameterPacket packet) {

				}


				@Override
				public INetworkConnector getNetworkConnector(TransportProtocols transportProtocol) {
					return null;
				}

				@Override
				public PeerData getPeerData(String hostIdentity) {
					return null;
				}

				@Override
				public ScheduledFuture<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
					return null;
				}

				@Override
				public ScheduledFuture<?> scheduleSingleExecutionTask(
						SingleExecutionAsyncTask task) {
					return null;

				}

				@Override
				public long getNextPeerSequence(String hostIdentity) {
					return 0;
				}


				@Override
				public long getNextServerSequence() {
					return 0;
				}


				@Override
				public String getNextSessionID() {
					return null;
				}


				@Override
				public String getNextSessionID(String optionalVal) {
					return null;
				}


				@Override
				public <T> ScheduledFuture<T> scheduleCallableSingleExecutionTask(
						CallableSingleExecutionAsyncTask<T> task) {
					return null;
				}


				@Override
				public Session generateSession(String destPeer, long appId) {
					return null;
				}


				@Override
				public EliteSSLContextFactory getEliteSSLContextFactory() {
					return null;
				}


				@Override
				public void updateInputStatistics(DiameterPacket packet,
						String hostIdentity) {

				}


				@Override
				public void updateOutputStatistics(DiameterPacket packet,
						String hostIdentity) {

				}


				@Override
				public void updateTimeoutRequestStatistics(
						DiameterRequest request, String hostIdentity) {

				}


				@Override
				public void updateUnknownH2HDropStatistics(
						DiameterAnswer answer, String hostIdentity) {

				}


				@Override
				public void updateUnknownH2HDropStatistics(
						DiameterAnswer answer, String hostIdentity,
						String realmName, RoutingActions routeAction) {

				}


				@Override
				public void updateDiameterStatsMalformedPacket(
						DiameterPacket packet, String hostIdentity) {

				}


				@Override
				public void updateDiameterStatsPacketDroppedStatistics(
						DiameterPacket packet, String hostIdentity) {
					// TODO Auto-generated method stub

				}


				@Override
				public void updateRealmTimeoutRequestStatistics(
						DiameterRequest diameterRequest, String realmName,
						RoutingActions routingAction) {
					// TODO Auto-generated method stub

				}


				@Override
				public void updateDiameterStatsPacketDroppedStatistics(
						DiameterPacket packet, String hostIdentity,
						String realmName, RoutingActions routeAction) {

				}


				@Override
				public void updateRealmInputStatistics(DiameterPacket packet,
						String realmName, RoutingActions routeAction) {

				}


				@Override
				public void updateRealmOutputStatistics(DiameterPacket packet,
						String realmName, RoutingActions routeAction) {

				}


				@Override
				public void updateDuplicatePacketStatistics(
						DiameterPacket packet, String hostIdentity) {

				}


				@Override
				public void purgeCancelledTasks() {
				}

				@Override
				public int getOverloadResultCode() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public OverloadAction getActionOnOverload() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public TaskScheduler getTaskScheduler() {
					// TODO Auto-generated method stub
					return null;
				}


				@Override
				public Session generateSession(long appId) {
					// TODO Auto-generated method stub
					return null;
				}


				@Override
				public DiameterPeerCommunicator getPeerCommunicator(String hostIdentity) {
					// TODO Auto-generated method stub
					return null;
				}


				@Override
				public void scheduleSingleExecutionTask(Runnable command) {
					// TODO Auto-generated method stub
					
				}


				@Override
				public Session getOrCreateSession(String sessionId, long appId) {
					// TODO Auto-generated method stub
					return null;
				}


				@Override
				public boolean hasSession(String sessionId, long appId) {
					return false;
				}


				@Override
				public ISession readOnlySession(String sessionId, long appId) {
					return null;
				}
			};


			DiameterTranslator translator = new DiameterTranslator(context.getServerContext(),stackContext);
			DiameterRatingTranslator ratingTranslator = new DiameterRatingTranslator(context.getServerContext(),stackContext);
			agent = TranslationAgent.getInstance();
			agent.registerTranslator(translator);
			agent.registerTranslator(ratingTranslator);
	}

	@Test 
	public void testDiameterToDiameter1() throws TranslationFailedException{
		DiameterAnswer diameterResponse = new DiameterAnswer();

		MappingDataImpl mappingData2 = new MappingDataImpl();
		mappingData2.setCheckExpression("*");
		mappingData2.setMappingExpression("0:485=0:258");
		mappingData2.setValueMapping("4=\"12\", 6=\"09\"");


		List<MappingDataImpl> mappingDataList = new ArrayList<MappingDataImpl>();
		mappingDataList.add(mappingData2);

		TranslationDetailImpl translationDetail = new TranslationDetailImpl();
		translationDetail.setMappingName("mapping1");
		translationDetail.setInRequestType("commandcode=\"257\" AND applicationid=\"0\"");
		translationDetail.setResponseMappingDataList(mappingDataList);
		List<TranslationDetailImpl> translationDetailList = new ArrayList<TranslationDetailImpl>();
		translationDetailList.add(translationDetail);

		TranslatorPolicyDataImpl policyData1 = new TranslatorPolicyDataImpl();
		policyData1.setFromTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);
		policyData1.setToTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);
		policyData1.setName("policy1");
		policyData1.setTransMapConfId("1");
		policyData1.setTranslationDetailList(translationDetailList);

		try {
			agent.registerPolicyData(policyData1);
		} catch (PolicyDataRegistrationFailedException e) {
			e.printStackTrace();
			System.out.println("Policy Data Registration Failed Exception ");
		}


		TranslatorParamsImpl translatorParams = new TranslatorParamsImpl(diameterAnswer,diameterResponse ,new DiameterRequest(),null);

		translatorParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, "mapping1");
		try {
			agent.translate("policy1", translatorParams, TranslatorConstants.RESPONSE_TRANSLATION);
			assertEquals("12",diameterResponse.getAVP("0:485").getStringValue());
		} catch (TranslationFailedException e) {
			System.out.println("translation failed exception");
			throw e;
		}
	}

	@Test 
	public void testDiameterToDiameter2() throws TranslationFailedException{
		DiameterAnswer diameterResponse = new DiameterAnswer();

		MappingDataImpl mappingData = new MappingDataImpl();
		mappingData.setCheckExpression("*");
		mappingData.setDefaultValue("DefaultValue");
		mappingData.setMappingExpression("0:1=0:257");

		List<MappingDataImpl> mappingDataList = new ArrayList<MappingDataImpl>();
		mappingDataList.add(mappingData);

		TranslationDetailImpl translationDetail = new TranslationDetailImpl();
		translationDetail.setMappingName("mapping2");
		translationDetail.setInRequestType("commandcode=\"257\" AND applicationid=\"0\"");
		translationDetail.setResponseMappingDataList(mappingDataList);

		List<TranslationDetailImpl> translationDetailList = new ArrayList<TranslationDetailImpl>();
		translationDetailList.add(translationDetail);

		TranslatorPolicyDataImpl policyData1 = new TranslatorPolicyDataImpl();
		policyData1.setName("policy2");
		policyData1.setTransMapConfId("2");
		policyData1.setTranslationDetailList(translationDetailList);
		policyData1.setFromTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);
		policyData1.setToTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);

		policyData1.setIsDummyResponse(true);

		HashMap<String, String> setDummyResponseMap = new HashMap<String, String>();
		setDummyResponseMap.put("0:1", "\"Dhaval\"");
		policyData1.setDummyResponseMap(setDummyResponseMap);

		try {
			agent.registerPolicyData(policyData1);
		} catch (PolicyDataRegistrationFailedException e) {
			e.printStackTrace();
			System.out.println("Policy Data Registration Failed Exception ");
		}
		diameterResponse.setCommandCode(257);
		diameterResponse.setApplicationID(0);

		TranslatorParamsImpl translatorParams = new TranslatorParamsImpl(diameterAnswer, diameterResponse,new DiameterRequest(),null);

		try {
			translatorParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, "mapping2");
			agent.translate("policy2", translatorParams, TranslatorConstants.RESPONSE_TRANSLATION);
			System.out.println("Response Obj******");
			System.out.println(diameterResponse);
			System.out.println();
			System.out.println("Answer Obj*******");
			System.out.println(diameterAnswer);
		} catch (TranslationFailedException e) {
			System.out.println("translation failed exception");
			throw e;
		}
	}

	@Test 
	public void testDiameterToDiameter3() throws TranslationFailedException{

		/*
		 * Creating and adding grouped avp
		 */

		AvpGrouped groupedAvp = (AvpGrouped) DiameterDictionary.getInstance().getAttribute("0:284");
		IDiameterAVP avp1 = DiameterDictionary.getInstance().getAttribute(0, 280);
		avp1.setStringValue("Elitecore");
		IDiameterAVP avp2 = DiameterDictionary.getInstance().getAttribute(0, 33);
		avp2.setStringValue("Proxy-State");

		ArrayList<IDiameterAVP> list = new ArrayList<IDiameterAVP>();
		list.add(avp1);
		list.add(avp2);

		groupedAvp.setGroupedAvp(list);

		diameterAnswer.addAvp(groupedAvp);
		/**/
		DiameterAnswer diameterResponse = new DiameterAnswer();

		MappingDataImpl mappingData = new MappingDataImpl();
		mappingData.setCheckExpression("*");
		mappingData.setMappingExpression("0:284.0:280=0:284.0:280,0:284.0:1=0:257");
		//mappingData.setMappingExpression("0:284{0:280=\"Hello\",0:1=0:257");

		MappingDataImpl mappingData1 = new MappingDataImpl();
		mappingData1.setCheckExpression("*");
		mappingData1.setMappingExpression("0:284.0:1=0:257");

		List<MappingDataImpl> mappingDataList = new ArrayList<MappingDataImpl>();
		mappingDataList.add(mappingData);
		//mappingDataList.add(mappingData1);

		TranslationDetailImpl translationDetail = new TranslationDetailImpl();
		translationDetail.setMappingName("mapping3");
		translationDetail.setInRequestType("commandcode=\"257\" AND applicationid=\"0\"");
		translationDetail.setResponseMappingDataList(mappingDataList);

		List<TranslationDetailImpl> translationDetailList = new ArrayList<TranslationDetailImpl>();
		translationDetailList.add(translationDetail);

		TranslatorPolicyDataImpl policyData1 = new TranslatorPolicyDataImpl();
		policyData1.setName("policy3");
		policyData1.setTransMapConfId("3");
		policyData1.setTranslationDetailList(translationDetailList);
		policyData1.setFromTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);
		policyData1.setToTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);


		try {
			agent.registerPolicyData(policyData1);
		} catch (PolicyDataRegistrationFailedException e) {
			e.printStackTrace();
			System.out.println("Policy Data Registration Failed Exception ");
		}
		diameterResponse.setCommandCode(257);
		diameterResponse.setApplicationID(0);

		TranslatorParamsImpl translatorParams = new TranslatorParamsImpl(diameterAnswer, diameterResponse,new DiameterRequest(),null);


		try {
			translatorParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, "mapping3");
			agent.translate("policy3", translatorParams, TranslatorConstants.RESPONSE_TRANSLATION);
			AvpGrouped avpGrouped = (AvpGrouped) diameterResponse.getAVP("0:284");
			assertEquals("Elitecore",avpGrouped.getSubAttribute("0:280").getStringValue());
			assertEquals(diameterAnswer.getAVP("0:257").getStringValue(),avpGrouped.getSubAttribute("0:1").getStringValue());
		} catch (TranslationFailedException e) {
			System.out.println("translation failed exception");
			throw e;
		}
	}

	@Test 
	public void testDiameterToDiameter4() throws TranslationFailedException{

		/*
		 * Creating and adding grouped avp to DIAMETER_IN_MSG
		 */

		IDiameterAVP groupedAvp =  DiameterDictionary.getInstance().getAttribute("0:284");
		IDiameterAVP avp1 = DiameterDictionary.getInstance().getAttribute(0, 280);
		avp1.setStringValue("Elitecore");
		IDiameterAVP avp2 = DiameterDictionary.getInstance().getAttribute(0, 33);
		avp2.setStringValue("Proxy-State");

		ArrayList<IDiameterAVP> list = new ArrayList<IDiameterAVP>();
		list.add(avp1);
		list.add(avp2);

		groupedAvp.setGroupedAvp(list);

		diameterAnswer.addAvp(groupedAvp);
		/**/
		DiameterAnswer diameterResponse = new DiameterAnswer();

		MappingDataImpl mappingData = new MappingDataImpl();
		mappingData.setCheckExpression("*");
		mappingData.setDefaultValue("DefaultValue");
		mappingData.setMappingExpression("0:284.0:280=0:257,0:284.0:1=0:257");
		//mappingData.setMappingExpression("0:284{9:131190=\"13123\",0:1=0:257");

		MappingDataImpl mappingData1 = new MappingDataImpl();
		mappingData1.setCheckExpression("*");
		mappingData1.setDefaultValue("DefaultValue");
		mappingData1.setMappingExpression("0:284.0:284.0:33=\"a\"");

		List<MappingDataImpl> mappingDataList = new ArrayList<MappingDataImpl>();
		mappingDataList.add(mappingData);
		mappingDataList.add(mappingData1);

		TranslationDetailImpl translationDetail = new TranslationDetailImpl();
		translationDetail.setMappingName("mapping4");
		translationDetail.setInRequestType("commandcode=\"257\" AND applicationid=\"0\"");
		translationDetail.setResponseMappingDataList(mappingDataList);

		List<TranslationDetailImpl> translationDetailList = new ArrayList<TranslationDetailImpl>();
		translationDetailList.add(translationDetail);

		TranslatorPolicyDataImpl policyData1 = new TranslatorPolicyDataImpl();
		policyData1.setName("policy4");
		policyData1.setTransMapConfId("4");
		policyData1.setTranslationDetailList(translationDetailList);
		policyData1.setFromTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);
		policyData1.setToTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);

		try {
			agent.registerPolicyData(policyData1);
		} catch (PolicyDataRegistrationFailedException e) {
			e.printStackTrace();
			System.out.println("Policy Data Registration Failed Exception ");
		}
		diameterResponse.setCommandCode(257);
		diameterResponse.setApplicationID(0);

		TranslatorParamsImpl translatorParams = new TranslatorParamsImpl(diameterAnswer,diameterResponse,new DiameterRequest(),null);


		try {
			translatorParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, "mapping4");
			agent.translate("policy4", translatorParams, TranslatorConstants.RESPONSE_TRANSLATION);
			System.out.println("Response Obj******");
			System.out.println(diameterResponse);
			System.out.println();
			System.out.println("Answer Obj*******");
			System.out.println(diameterAnswer);
		} catch (TranslationFailedException e) {
			System.out.println("translation failed exception");
			throw e;
		}
	}
}
