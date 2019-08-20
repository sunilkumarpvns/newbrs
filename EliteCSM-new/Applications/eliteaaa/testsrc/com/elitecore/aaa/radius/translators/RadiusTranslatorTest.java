package com.elitecore.aaa.radius.translators;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.aaa.radius.translators.data.RadiusToRadiusTranslationMappingTestCaseData;
import com.elitecore.aaa.radius.translators.data.RequestResponseTranslationTestCaseData;
import com.elitecore.aaa.script.TranslationMappingScript;
import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.config.ListWrapper;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.manager.scripts.ExternalScriptsManager;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacketBuilder;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.DummyResponseDetail;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;

@RunWith(JUnitParamsRunner.class)
public class RadiusTranslatorTest {

	private final static String MODULE = "RADIUS-TRANSLATOR-TEST";
	@Mock private ServerContext serverContext;
	@Mock ExternalScriptsManager externalScriptsManager;
	
	private RadiusTranslator radiusTranslator;
	@Rule public ExpectedException exception = ExpectedException.none();
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	static {
		RadiusDictionaryTestHarness.getInstance();
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		radiusTranslator = new RadiusTranslator(serverContext);
	}

	@Test
	public void inputTypeIsRadius() {
		assertEquals(AAATranslatorConstants.RADIUS_TRANSLATOR, radiusTranslator.getFromId());
	}

	@Test
	public void outputTypeIsRadius() {
		assertEquals(AAATranslatorConstants.RADIUS_TRANSLATOR, radiusTranslator.getToId());
	}

	public static Object[] dataFor_testMappingSelection() throws FileNotFoundException, UnsupportedEncodingException, JAXBException {

		ClasspathResource resource = ClasspathResource.at("translationmapping/radius2radius/radius-to-radius-translation-mapping-data.xml");

		ListWrapper<RadiusToRadiusTranslationMappingTestCaseData> caseCollection = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()),
				ListWrapper.class, RadiusToRadiusTranslationMappingTestCaseData.class);

		return caseCollection.getList().toArray();
	}

	@Test
	@Parameters(method="dataFor_testMappingSelection")
	public void testMappingSelection(RadiusToRadiusTranslationMappingTestCaseData testCaseData) throws Exception {
		radiusTranslator.init(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj());

		RadServiceRequest requestPacket = testCaseData.getRadiusPacketData()
				.create(new RadAuthRequestBuilder());
		
		RadiusPacket translatedPacket = new RadiusPacket();
		TranslatorParams params = new TranslatorParamsImpl(requestPacket, translatedPacket);

		radiusTranslator.translateRequest(testCaseData.getTranslationMappingConfigurationData().getTransMappingConfId(), params);

		assertEquals(testCaseData.getExpectedMapping(), params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING));
	}

	public static Object[] dataFor_testDummyMappingSelection() throws FileNotFoundException, UnsupportedEncodingException, JAXBException {

		ClasspathResource resource = ClasspathResource.at("translationmapping/radius2radius/radius-to-radius-translation-dummy-mapping-data.xml");

		ListWrapper<RadiusToRadiusTranslationMappingTestCaseData> caseCollection = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()),
				ListWrapper.class, RadiusToRadiusTranslationMappingTestCaseData.class);

		return caseCollection.getList().toArray();
	}

	@Test
	@Parameters(method = "dataFor_testDummyMappingSelection")
	public void testDummyMappingSelection(RadiusToRadiusTranslationMappingTestCaseData testCaseData) throws Exception {

		radiusTranslator.init(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj());

		RadServiceRequest requestPacket = testCaseData.getRadiusPacketData()
				.create(new RadAuthRequestBuilder());
		
		RadiusPacket translatedPacket = new RadiusPacket();
		TranslatorParams params = new TranslatorParamsImpl(requestPacket, translatedPacket);

		radiusTranslator.translateRequest(testCaseData.getTranslationMappingConfigurationData().getTransMappingConfId(), params);

		assertEquals(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj().getIsDummyResponse(), params.getParam(TranslatorConstants.DUMMY_MAPPING));
	}

	@Test
	@Parameters(method = "dataFor_testDummyMappingSelection")
	public void requestTranslationFailsWithTranslationFailedExceptionIfTranslationPolicyDoesNotExist(RadiusToRadiusTranslationMappingTestCaseData testCaseData) throws Exception {

		radiusTranslator.init(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj());
		
		RadServiceRequest requestPacket = testCaseData.getRadiusPacketData()
				.create(new RadAuthRequestBuilder());
		
		RadiusPacket translatedPacket = new RadiusPacket();
		TranslatorParams params = new TranslatorParamsImpl(requestPacket, translatedPacket);

		String nonExistentPolicyId = "NON-EXISTENT";

		exception.expect(TranslationFailedException.class);
		exception.expectMessage("Invalid Policy id: " + nonExistentPolicyId);

		radiusTranslator.translateRequest(nonExistentPolicyId, params);
	}
	
	@Test
	@Parameters(method = "dataFor_testDummyMappingSelection")
	public void invokesTranslateScriptPostRequestTranslation(RadiusToRadiusTranslationMappingTestCaseData testCaseData) throws Exception  {

		radiusTranslator.init(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj());

		RadServiceRequest requestPacket = testCaseData.getRadiusPacketData()
				.create(new RadAuthRequestBuilder());
		
		RadiusPacket translatedPacket = new RadiusPacket();
		TranslatorParams params = new TranslatorParamsImpl(requestPacket, translatedPacket);

		when(serverContext.getExternalScriptsManager()).thenReturn(externalScriptsManager);
		
		radiusTranslator.translateRequest(testCaseData.getTranslationMappingConfigurationData().getTransMappingConfId(), params);
		radiusTranslator.postTranslateRequest(testCaseData.getTranslationMappingConfigurationData().getTransMappingConfId(), params);
		verify(serverContext.getExternalScriptsManager(), times(1)).execute(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj().getScript(), TranslationMappingScript.class, "requestTranslationExtension", new Class<?>[]{TranslatorParams.class}, new Object[]{params});
	}

	public static Object[] dataFor_translationRequest() throws FileNotFoundException, UnsupportedEncodingException, JAXBException {

		ClasspathResource resource = ClasspathResource.at("translationmapping/radius2radius/radius-to-radius-request-translation-data.xml");

		ListWrapper<RequestResponseTranslationTestCaseData> caseCollection = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()),
				ListWrapper.class, RequestResponseTranslationTestCaseData.class);

		return caseCollection.getList().toArray();
	}

	@Test
	@Parameters(method = "dataFor_translationRequest")
	public void translateRequest(RequestResponseTranslationTestCaseData testCaseData) throws Exception {
		
		LogManager.getLogger().info(MODULE, "=====================================");
		LogManager.getLogger().info(MODULE, testCaseData.getBehaviour());
		LogManager.getLogger().info(MODULE, "=====================================");
		
		radiusTranslator.init(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj());
		
		RadServiceRequest requestPacket = testCaseData.getFromRadiusRequest()
				.create(new RadAuthRequestBuilder());
		
		RadiusPacket translatedPacket = new RadiusPacket();
		RadiusPacket toResponsePacket = testCaseData.getToRadiusRequest().create(new RadiusPacketBuilder());
		
		translatedPacket.setPacketType(toResponsePacket.getPacketType());
		TranslatorParams params = new TranslatorParamsImpl(requestPacket, translatedPacket);

		radiusTranslator.translateRequest(testCaseData.getTranslationMappingConfigurationData().getTransMappingConfId(), params);
		
		assertArrayEquals(testCaseData.getToRadiusRequest().create(new RadiusPacketBuilder()).getBytes(true), translatedPacket.getBytes(true));
	}

	public static Object[] dataFor_translateResponse() throws FileNotFoundException, UnsupportedEncodingException, JAXBException {

		ClasspathResource resource = ClasspathResource.at("translationmapping/radius2radius/radius-to-radius-response-translation-data.xml");

		ListWrapper<RequestResponseTranslationTestCaseData> caseCollection = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()),
				ListWrapper.class, RequestResponseTranslationTestCaseData.class);

		
		List<RequestResponseTranslationTestCaseData> list = caseCollection.getList();
		
		for (RequestResponseTranslationTestCaseData testCaseData : list) {
			HashMap<String, String> dummyResponseMap = new HashMap<String,String>();
			List<DummyResponseDetail> dummyResponseList = testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj().getDummyResposeList();
			for (DummyResponseDetail dummyResponseDetail : dummyResponseList) {
				dummyResponseMap.put(dummyResponseDetail.getOutfield(), dummyResponseDetail.getValue());
			}
			testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj().setDummyResponseMap(dummyResponseMap);
		}
		
		return list.toArray();
	}
	
	@Test
	@Parameters(method = "dataFor_translateResponse")
	public void translateResponse(RequestResponseTranslationTestCaseData testCaseData) throws Exception {
		
		LogManager.getLogger().info(MODULE, "=====================================");
		LogManager.getLogger().info(MODULE, testCaseData.getBehaviour());
		LogManager.getLogger().info(MODULE, "=====================================");

		
		radiusTranslator.init(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj());

		RadServiceRequest originalRequestPacket = testCaseData.getFromRadiusRequest()
				.create(new RadAuthRequestBuilder());
		
		RadiusPacket translatedRequest = new RadiusPacket();
		translatedRequest.setBytes(originalRequestPacket.getRequestBytes());
		
		RadiusPacket fromResponsePacket = testCaseData.getFromRadiusResponse().create(new RadiusPacketBuilder());

		RadiusPacket translatedResponsePacket = new RadiusPacket();
		translatedResponsePacket.setPacketType(fromResponsePacket.getPacketType());
		
		TranslatorParams params = new TranslatorParamsImpl(fromResponsePacket, translatedResponsePacket, originalRequestPacket, translatedRequest);

		params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, testCaseData.getSelectedMapping());
		
		radiusTranslator.translateResponse(testCaseData.getTranslationMappingConfigurationData().getTransMappingConfId(), params);

		assertArrayEquals(testCaseData.getToRadiusRadiusResponse().create(new RadiusPacketBuilder()).getBytes(true), translatedResponsePacket.getBytes(true));
	}
	
	
	@Test
	@Parameters(method = "dataFor_translateResponse")
	public void responseTranslationFailsWithTranslationFailedExceptionIfTranslationPolicyDoesNotExist(RequestResponseTranslationTestCaseData testCaseData) throws Exception {

		radiusTranslator.init(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj());

		RadServiceRequest originalRequestPacket = testCaseData.getFromRadiusRequest()
				.create(new RadAuthRequestBuilder());
		
		RadiusPacket translatedRequest = new RadiusPacket();
		translatedRequest.setBytes(originalRequestPacket.getRequestBytes());
		
		RadiusPacket fromResponsePacket = testCaseData.getFromRadiusResponse().create(new RadiusPacketBuilder());

		RadiusPacket translatedResponsePacket = new RadiusPacket();
		translatedResponsePacket.setPacketType(fromResponsePacket.getPacketType());
		
		TranslatorParams params = new TranslatorParamsImpl(fromResponsePacket, translatedResponsePacket, originalRequestPacket, translatedRequest);

		String nonExistentId = "NON-EXISTENT";

		exception.expect(TranslationFailedException.class);
		exception.expectMessage("Given Policy not found. Policy Id: " + nonExistentId);

		radiusTranslator.translateResponse(nonExistentId, params);
	}
	
	public static Object[] dataFor_translateDummyResponse() throws FileNotFoundException, UnsupportedEncodingException, JAXBException {

		ClasspathResource resource = ClasspathResource.at("translationmapping/radius2radius/radius-to-radius-dummy-response-translation.xml");

		ListWrapper<RequestResponseTranslationTestCaseData> caseCollection = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()),
				ListWrapper.class, RequestResponseTranslationTestCaseData.class);

		
		List<RequestResponseTranslationTestCaseData> list = caseCollection.getList();
		
		for (RequestResponseTranslationTestCaseData testCaseData : list) {
			HashMap<String, String> dummyResponseMap = new HashMap<String,String>();
			List<DummyResponseDetail> dummyResponseList = testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj().getDummyResposeList();
			for (DummyResponseDetail dummyResponseDetail : dummyResponseList) {
				dummyResponseMap.put(dummyResponseDetail.getOutfield(), dummyResponseDetail.getValue());
			}
			testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj().setDummyResponseMap(dummyResponseMap);
		}
		
		return list.toArray();
	}
	
	@Test
	@Parameters(method = "dataFor_translateDummyResponse")
	public void responseTranslationWithDummyMapping(RequestResponseTranslationTestCaseData testCaseData) throws Exception {

		LogManager.getLogger().info(MODULE, "=====================================");
		LogManager.getLogger().info(MODULE, testCaseData.getBehaviour());
		LogManager.getLogger().info(MODULE, "=====================================");

		
		radiusTranslator.init(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj());

		RadServiceRequest originalRequestPacket = testCaseData.getFromRadiusRequest()
				.create(new RadAuthRequestBuilder());
		
		RadiusPacket translatedRequest = new RadiusPacket();
		translatedRequest.setBytes(originalRequestPacket.getRequestBytes());
		
		RadiusPacket fromResponsePacket = testCaseData.getFromRadiusResponse().create(new RadiusPacketBuilder());

		RadiusPacket translatedResponsePacket = new RadiusPacket();
		translatedResponsePacket.setPacketType(fromResponsePacket.getPacketType());
		
		TranslatorParams params = new TranslatorParamsImpl(fromResponsePacket, translatedResponsePacket, originalRequestPacket, translatedRequest);

		params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, testCaseData.getSelectedMapping());
		
		radiusTranslator.translateResponse(testCaseData.getTranslationMappingConfigurationData().getTransMappingConfId(), params);

		assertArrayEquals(testCaseData.getToRadiusRadiusResponse().create(new RadiusPacketBuilder()).getBytes(true), translatedResponsePacket.getBytes(true));
	}
}