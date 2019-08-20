package com.elitecore.aaa.core.diameter.translators;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.diameterapi.core.common.PolicyDataRegistrationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.impl.MappingDataImpl;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslationDetailImpl;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorPolicyDataImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.DiameterStackContext;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.ratingapi.data.RequestParameters;
import com.elitecore.ratingapi.util.IRequestParameters;

public class TranslationAgentRequestTest {
	static TranslationAgent agent;

	static DiameterRequest diameterRequest;
	static DiameterServiceContext context;
	static IStackContext stackContext;


	@BeforeClass
	public static void setup(){
		diameterRequest = new DiameterRequest(false);
		DummyDiameterDictionary.getInstance();
		
		byte b[] = new byte[]{0x01, 0x00, 0x03, 0x2c, (byte) 0x80, 0x00, 0x01, 0x01, 
				0x00, 0x00, 0x00, 0x00, (byte) 0x9c, 0x57, (byte) 0x8d, 0x65, 
				0x75, (byte) 0x9d, (byte) 0xff, (byte) 0xcc, 0x00, 0x00, 0x01, 0x08, 
				0x40, 0x00, 0x00, 0x1b, 0x64, 0x69, 0x61, 0x6d, 
				0x65, 0x74, 0x65, 0x72, 0x2e, 0x61, 0x69, 0x72, 
				0x74, 0x65, 0x6c, 0x2e, 0x63, 0x6f, 0x6d, 0x00, 
				0x00, 0x00, 0x01, 0x01, 0x40, 0x00, 0x00, 0x0e, 
				0x00, 0x01, 0x0a, 0x6a, 0x01, 0x2e, 0x00, 0x00, 
				0x00, 0x00, 0x01, 0x28, 0x40, 0x00, 0x00, 0x12, 
				0x61, 0x69, 0x72, 0x74, 0x65, 0x6c, 0x2e, 0x63, 
				0x6f, 0x6d, 0x00, 0x00, 0x00, 0x00, 0x01, 0x0a, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x03, 0x3f, 
				0x00, 0x00, 0x01, 0x0d, 0x00, 0x00, 0x00, 0x1f, 
				0x41, 0x6c, 0x63, 0x61, 0x74, 0x65, 0x6c, 0x2d, 
				0x4c, 0x75, 0x63, 0x65, 0x6e, 0x74, 0x20, 0x38, 
				0x39, 0x35, 0x30, 0x20, 0x41, 0x41, 0x41, 0x00, 
				0x00, 0x00, 0x01, 0x16, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x0b, 
				0x00, 0x00, 0x00, 0x0c, 0x00, 0x00, (byte) 0xea, (byte) 0xce, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x00, (byte) 0xa6, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x03, 0x20, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x0e, (byte) 0x91, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x06, 0x30, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x05, (byte) 0x83, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x00, 0x3d, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x01, 0x33, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x06, (byte) 0xd7, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x1f, (byte) 0xe4, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x0f, 0x3e, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x00, 0x09, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x19, 0x7f, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x39, (byte) 0xe7, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x09, 0x30, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x14, (byte) 0x8f, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x32, (byte) 0x97, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x15, (byte) 0x9f, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x03, 0x46, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x01, 0x37, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x01, (byte) 0xad, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x23, (byte) 0xbc, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x15, (byte) 0xbf, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x05, (byte) 0xb1, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x38, (byte) 0xbd, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x4c, 0x1f, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x37, 0x63, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x02, 0x7d, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x13, 0x0a, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x0c, 0x0d, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x28, (byte) 0xaf, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x00, 0x2b, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x04, 0x19, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x0d, (byte) 0xe9, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x00, (byte) 0xff, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x0a, 0x4c, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x0c, 0x7f, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x00, 0x5e, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x07, (byte) 0xc7, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x0c, (byte) 0xed, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x00, 0x05, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x0c, 0x04, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x22, 0x28, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x1e, 0x47, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x1d, 0x3b, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x0a, 0x4d, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x12, (byte) 0xee, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x02, 0x11, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x09, 0x1e, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x0d, (byte) 0xdf, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x60, (byte) 0xb5, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x00, (byte) 0xc1, 0x00, 0x00, 0x01, 0x09, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x37, 0x2a, 
				0x00, 0x00, 0x01, 0x09, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x38, 0x21, 0x00, 0x00, 0x01, 0x02, 
				0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x00, 0x04, 
				0x00, 0x00, 0x01, 0x2b, 0x40, 0x00, 0x00, 0x0c, 
				0x00, 0x00, 0x00, 0x00};

		InputStream ips = new DataInputStream(new ByteArrayInputStream(b));

		try {
			diameterRequest.readFrom(ips);
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
				// TODO Auto-generated method stub
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
			}};

			stackContext = new DummyStackContext(null);

			DiameterTranslator translator = new DiameterTranslator(context.getServerContext(),stackContext);
			DiameterRatingTranslator ratingTranslator = new DiameterRatingTranslator(context.getServerContext(),stackContext);
			agent = TranslationAgent.getInstance();
			agent.registerTranslator(translator);
			agent.registerTranslator(ratingTranslator);
	}

	@Test
	public void testDiameterToDiameter1() throws TranslationFailedException{

		DiameterRequest diameterResponse = new DiameterRequest(true);

		MappingDataImpl mappingData = new MappingDataImpl();
		mappingData.setCheckExpression("*");
		mappingData.setMappingExpression("0:1=0:264");
		List<MappingDataImpl> mappingDataList = new ArrayList<MappingDataImpl>();
		mappingDataList.add(mappingData);

		TranslationDetailImpl translationDetail = new TranslationDetailImpl();
		translationDetail.setMappingName("mapping1");
		translationDetail.setInRequestType("commandcode=\"257\" AND applicationid=\"0\"");
		translationDetail.setRequestMappingDataList(mappingDataList);
		List<TranslationDetailImpl> translationDetailList = new ArrayList<TranslationDetailImpl>();
		translationDetailList.add(translationDetail);

		TranslatorPolicyDataImpl policyData = new TranslatorPolicyDataImpl();
		policyData.setName("policy1");
		policyData.setTransMapConfId("1");
		policyData.setTranslationDetailList(translationDetailList);
		policyData.setFromTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);
		policyData.setToTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);

		try {
			agent.registerPolicyData(policyData);
		} catch (PolicyDataRegistrationFailedException e) {
			e.printStackTrace();
			System.out.println("Policy Data Registration Failed Exception ");
		}

		TranslatorParamsImpl translatorParams = new TranslatorParamsImpl(diameterRequest, diameterResponse);


		try {
			agent.translate("policy1", translatorParams, TranslatorConstants.REQUEST_TRANSLATION);
			translatorParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, "mapping1");
			assertEquals(diameterRequest.getAVP("0:264").getStringValue(), diameterResponse.getAVP("0:1").getStringValue());
		} catch (TranslationFailedException e) {
			e.printStackTrace();
			System.out.println("translation failed exception");
			throw e;
		}
	}

	@Test
	public void testDiameterToDiameter2() throws TranslationFailedException{

		DiameterRequest diameterResponse = new DiameterRequest(true);

		MappingDataImpl mappingData = new MappingDataImpl();
		mappingData.setCheckExpression("0:257=\"10.106.1.46\"");
		mappingData.setDefaultValue(" (Default Value)");
		mappingData.setMappingExpression("0:1=0:257");
		List<MappingDataImpl> mappingDataList = new ArrayList<MappingDataImpl>();
		mappingDataList.add(mappingData);

		TranslationDetailImpl translationDetail = new TranslationDetailImpl();
		translationDetail.setMappingName("mapping2");
		translationDetail.setInRequestType("commandcode=\"257\" AND applicationid=\"0\"");
		translationDetail.setRequestMappingDataList(mappingDataList);
		List<TranslationDetailImpl> translationDetailList = new ArrayList<TranslationDetailImpl>();
		translationDetailList.add(translationDetail);

		TranslatorPolicyDataImpl policyData1 = new TranslatorPolicyDataImpl();
		policyData1.setName("policy2");
		policyData1.setTransMapConfId("2");
		policyData1.setTranslationDetailList(translationDetailList);
		policyData1.setFromTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);
		policyData1.setToTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);
		try {
			agent.registerPolicyData(policyData1);
		} catch (PolicyDataRegistrationFailedException e) {
			e.printStackTrace();
			System.out.println("Policy Data Registration Failed Exception ");
		}

		TranslatorParamsImpl translatorParams = new TranslatorParamsImpl(diameterRequest, diameterResponse);

		try {
			agent.translate("policy2", translatorParams, TranslatorConstants.REQUEST_TRANSLATION);
			translatorParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, "mapping2");
			diameterResponse = (DiameterRequest) translatorParams.getParam(TranslatorConstants.TO_PACKET);
			assertEquals(diameterRequest.getAVP("0:257").getStringValue(), diameterResponse.getAVP("0:1").getStringValue());
		} catch (TranslationFailedException e) {
			e.printStackTrace();
			System.out.println("translation failed exception");
			throw e;
		}
	}

	@Test 
	public void testDiameterToDiameter3() throws TranslationFailedException{
		DiameterRequest diameterResponse = new DiameterRequest(true);

		MappingDataImpl mappingData2 = new MappingDataImpl();
		mappingData2.setCheckExpression("*");
		mappingData2.setMappingExpression("0:85=\"1\"");

		MappingDataImpl mappingData = new MappingDataImpl();
		mappingData.setCheckExpression("*");
		mappingData.setMappingExpression("0:1=\"\"");

		MappingDataImpl mappingData1 = new MappingDataImpl();
		mappingData1.setCheckExpression("*");
		mappingData1.setMappingExpression("0:257=0:257");

		List<MappingDataImpl> mappingDataList = new ArrayList<MappingDataImpl>();
		mappingDataList.add(mappingData);
		mappingDataList.add(mappingData1);
		mappingDataList.add(mappingData2);

		TranslationDetailImpl translationDetail = new TranslationDetailImpl();
		translationDetail.setMappingName("mapping3");
		translationDetail.setInRequestType("commandcode=\"257\" AND applicationid=\"0\"");
		translationDetail.setRequestMappingDataList(mappingDataList);
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

		TranslatorParamsImpl translatorParams = new TranslatorParamsImpl(diameterRequest, diameterResponse);


		try {
			agent.translate("policy3", translatorParams, TranslatorConstants.REQUEST_TRANSLATION);
			translatorParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, "mapping3");
			diameterResponse = (DiameterRequest) translatorParams.getParam(TranslatorConstants.TO_PACKET);
			System.out.println(diameterResponse);
			assertEquals("", diameterResponse.getAVP("0:1").getStringValue());			
		} catch (TranslationFailedException e) {
			System.out.println("translation failed exception");
			throw e;
		}
	}

	@Test
	public void testDiameterToDiameter4() throws TranslationFailedException{

		DiameterRequest diameterResponse = new DiameterRequest(true);

		MappingDataImpl mappingData = new MappingDataImpl();
		mappingData.setCheckExpression("*");
		mappingData.setDefaultValue("0:1=DefaultValue");
		mappingData.setMappingExpression("0:1=0:222");
		List<MappingDataImpl> mappingDataList = new ArrayList<MappingDataImpl>();
		mappingDataList.add(mappingData);

		TranslationDetailImpl translationDetail = new TranslationDetailImpl();
		translationDetail.setMappingName("mapping4");
		translationDetail.setInRequestType("commandcode=\"257\" AND applicationid=\"0\"");
		translationDetail.setRequestMappingDataList(mappingDataList);
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

		TranslatorParamsImpl translatorParams = new TranslatorParamsImpl(diameterRequest, diameterResponse);


		try {
			agent.translate("policy4", translatorParams, TranslatorConstants.REQUEST_TRANSLATION);
			translatorParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, "mapping4");
			assertEquals("DefaultValue", diameterResponse.getAVP("0:1").getStringValue());			
		} catch (TranslationFailedException e) {
			e.printStackTrace();
			System.out.println("translation failed exception");
			throw e;
		}
	}

	@Test 
	public void testDiameterToDiameter5() throws TranslationFailedException{

		DiameterRequest diameterResponse = new DiameterRequest(true);

		MappingDataImpl mappingData = new MappingDataImpl();
		mappingData.setCheckExpression("*");
		mappingData.setDefaultValue("0:1=DefaultValue");
		mappingData.setMappingExpression("0:1=0:222");
		List<MappingDataImpl> mappingDataList = new ArrayList<MappingDataImpl>();
		mappingDataList.add(mappingData);

		TranslationDetailImpl translationDetail = new TranslationDetailImpl();
		translationDetail.setMappingName("mapping5");
		translationDetail.setInRequestType("commandcode=\"257\" AND applicationid=\"0\"");
		translationDetail.setRequestMappingDataList(mappingDataList);
		List<TranslationDetailImpl> translationDetailList = new ArrayList<TranslationDetailImpl>();
		translationDetailList.add(translationDetail);

		TranslatorPolicyDataImpl policyData1 = new TranslatorPolicyDataImpl();
		policyData1.setName("policy5");
		policyData1.setTransMapConfId("5");
		policyData1.setTranslationDetailList(translationDetailList);
		policyData1.setFromTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);
		policyData1.setToTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);

		try {
			agent.registerPolicyData(policyData1);
		} catch (PolicyDataRegistrationFailedException e) {
			e.printStackTrace();
			System.out.println("Policy Data Registration Failed Exception. Reason:  " + e.getMessage());
		} catch (Exception e){
			System.out.println(" Policy Data Registration Failed. Reason: " +e.getMessage());
		}

		TranslatorParamsImpl translatorParams = new TranslatorParamsImpl(diameterRequest, diameterResponse);


		try {
			translatorParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, "mapping5");
			agent.translate("policy5", translatorParams, TranslatorConstants.REQUEST_TRANSLATION);
			assertEquals("DefaultValue", diameterResponse.getAVP("0:1").getStringValue());			
		} catch (TranslationFailedException e) {
			e.printStackTrace();
			System.out.println("translation failed exception");
			throw e;
		}
	}

	@Test
	public void testDiameterToRating1() throws TranslationFailedException{

		IRequestParameters diameterResponse = new RequestParameters();

		IDiameterAVP ccType = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CC_REQUEST_TYPE);
		if(ccType!=null){
			ccType.setInteger(1);
			diameterRequest.addAvp(ccType);
		}

		MappingDataImpl mappingData = new MappingDataImpl();
		mappingData.setCheckExpression("*");
		mappingData.setDefaultValue("0:1=\"DefaultValue\"");
		mappingData.setMappingExpression("0:1=0:264");
		List<MappingDataImpl> mappingDataList = new ArrayList<MappingDataImpl>();
		mappingDataList.add(mappingData);

		TranslationDetailImpl translationDetail = new TranslationDetailImpl();
		translationDetail.setMappingName("mapping6");
		translationDetail.setInRequestType("commandcode=\"257\" AND applicationid=\"0\"");
		translationDetail.setRequestMappingDataList(mappingDataList);
		List<TranslationDetailImpl > translationDetailList = new ArrayList<TranslationDetailImpl>();
		translationDetailList.add(translationDetail);

		TranslatorPolicyDataImpl policyData1 = new TranslatorPolicyDataImpl();
		policyData1.setName("policy6");
		policyData1.setTransMapConfId("6");
		policyData1.setTranslationDetailList(translationDetailList);
		policyData1.setFromTranslatorId(TranslatorConstants.DIAMETER_TRANSLATOR);
		policyData1.setToTranslatorId(TranslatorConstants.RATING_TRANSLATOR);

		try {
			agent.registerPolicyData(policyData1);
		} catch (PolicyDataRegistrationFailedException e) {
			e.printStackTrace();
			System.out.println("Policy Data Registration Failed Exception ");
		}

		TranslatorParamsImpl translatorParams = new TranslatorParamsImpl(diameterRequest, diameterResponse);


		try {
			translatorParams.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, "mapping6");
			agent.translate("policy6", translatorParams, TranslatorConstants.REQUEST_TRANSLATION);
			System.out.println(diameterResponse);
		} catch (TranslationFailedException e) {
			e.printStackTrace();
			System.out.println("translation failed exception");
			throw e;
		}


	}
}
