package com.elitecore.aaa.diameter.translators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.diameter.translators.data.MappingSelectionTestCaseData;
import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.config.ListWrapper;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorPolicyDataImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class DiameterWebServiceTranslatorTest {

	private static final String MODULE = "DiameterWebServiceTranslator";
	private static final String DUMMY_ID = "1";

	private DiameterWebServiceTranslator diameterWebServiceTranslator;

	@Mock private AAAServerContext serverContext;
	@Mock private IStackContext stackContext;

	@Rule public ExpectedException exception = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		diameterWebServiceTranslator = new DiameterWebServiceTranslator(serverContext, stackContext);
	}
	
	@Test
	public void inputTypeIsRadius() {
		assertEquals(AAATranslatorConstants.WEBSERVICE_TRANSLATOR, diameterWebServiceTranslator.getFromId());
	}

	@Test
	public void outputTypeIsRadius() {
		assertEquals(AAATranslatorConstants.DIAMETER_TRANSLATOR, diameterWebServiceTranslator.getToId());
	}

	public static Object[] dataFor_requestBaseTranslationMapping() throws Exception {

		ClasspathResource resource = ClasspathResource.at("ws2diametertestcasexml/base-translation-request-mapping.xml");

		ListWrapper<MappingSelectionTestCaseData> caseCollection = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()),
				ListWrapper.class, MappingSelectionTestCaseData.class);

		return caseCollection.getList().toArray();
	}
	
	@Test
	@Parameters(method = "dataFor_requestBaseTranslationMapping")
	public void baseTranslationMappingWillBeAppliedInRequestAsPerConfiguration(MappingSelectionTestCaseData testCaseData) throws Exception {

		LogManager.getLogger().info(MODULE, "=====================================");
		LogManager.getLogger().info(MODULE, testCaseData.getBehaviour());
		LogManager.getLogger().info(MODULE, "=====================================");

		TranslatorPolicyDataImpl translationPolicyDataObj = testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj();
		diameterWebServiceTranslator.init(translationPolicyDataObj);

		diameterWebServiceTranslator.init(testCaseData.getBaseTranslationMappingConfigurationData().getTranslationPolicyDataObj());

		DiameterPacket diameterPacket = new DiameterRequest(false);

		Map<String,String> wsMap = testCaseData.getWebServiceRequestMap();
		TranslatorParams params = new TranslatorParamsImpl(wsMap, diameterPacket);

		diameterWebServiceTranslator.translateRequest(DUMMY_ID, params);

		assertThat(diameterPacket.getAsDiameterRequest(), testCaseData.getAssertion().createRequestMatcher());
	}
	

	public static Object[] dataFor_mappingSelection() throws Exception {

		ClasspathResource resource = ClasspathResource.at("ws2diametertestcasexml/mapping-selection-data.xml");

		ListWrapper<MappingSelectionTestCaseData> caseCollection = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()),
				ListWrapper.class, MappingSelectionTestCaseData.class);

		return caseCollection.getList().toArray();
	}

	@Test
	@Parameters(method = "dataFor_mappingSelection")
	public void mappingSelectionWillBeAppliedAsPerConfiguration(MappingSelectionTestCaseData testCaseData) throws Exception {

		LogManager.getLogger().info(MODULE, "=====================================");
		LogManager.getLogger().info(MODULE, testCaseData.getBehaviour());
		LogManager.getLogger().info(MODULE, "=====================================");

		TranslatorPolicyDataImpl translationPolicyDataObj = testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj();
		diameterWebServiceTranslator.init(translationPolicyDataObj);

		DiameterPacket diameterPacket = new DiameterRequest(false);

		Map<String,String> wsMap = testCaseData.getWebServiceRequestMap();
		TranslatorParams params = new TranslatorParamsImpl(wsMap, diameterPacket);

		diameterWebServiceTranslator.translateRequest(testCaseData.getTranslationMappingConfigurationData().getTransMappingConfId(), params);

		assertThat(diameterPacket.getAsDiameterRequest(), testCaseData.getAssertion().createRequestMatcher());
	}

	public static Object[] dataFor_requestTranslationMapping() throws Exception {

		ClasspathResource resource = ClasspathResource.at("ws2diametertestcasexml/webservice-to-diameter-request-translation-data.xml");

		ListWrapper<MappingSelectionTestCaseData> caseCollection = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()),
				ListWrapper.class, MappingSelectionTestCaseData.class);

		return caseCollection.getList().toArray();
	}

	@Test
	@Parameters(method = "dataFor_requestTranslationMapping")
	public void translateRequestFromWebServiceToDiameter(MappingSelectionTestCaseData testCaseData) throws Exception {

		LogManager.getLogger().info(MODULE, "=====================================");
		LogManager.getLogger().info(MODULE, testCaseData.getBehaviour());
		LogManager.getLogger().info(MODULE, "=====================================");

		TranslatorPolicyDataImpl translationPolicyDataObj = testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj();
		diameterWebServiceTranslator.init(translationPolicyDataObj);

		DiameterPacket diameterPacket = new DiameterRequest(false);

		Map<String,String> wsMap = testCaseData.getWebServiceRequestMap();
		TranslatorParams params = new TranslatorParamsImpl(wsMap, diameterPacket);

		diameterWebServiceTranslator.translateRequest(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj().getTransMapConfId(), params);

		assertThat(diameterPacket.getAsDiameterRequest(), testCaseData.getAssertion().createRequestMatcher());
	}

	public static Object[] dataFor_responseTranslationMapping() throws Exception {

		ClasspathResource resource = ClasspathResource.at("ws2diametertestcasexml/diameter-to-webservice-response-translation-data.xml");

		ListWrapper<MappingSelectionTestCaseData> caseCollection = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()),
				ListWrapper.class, MappingSelectionTestCaseData.class);

		return caseCollection.getList().toArray();
	}
	
	public static Object[] dataFor_baseResponseTranslationMapping() throws Exception {

		ClasspathResource resource = ClasspathResource.at("ws2diametertestcasexml/base-translation-response-mapping.xml");

		ListWrapper<MappingSelectionTestCaseData> caseCollection = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()),
				ListWrapper.class, MappingSelectionTestCaseData.class);

		return caseCollection.getList().toArray();
	}
	
	@Test
	@Parameters(method = "dataFor_baseResponseTranslationMapping")
	public void baseTranslationMappingWillBeAppliedInResponseAsPerConfiguration(MappingSelectionTestCaseData testCaseData) throws Exception {

		LogManager.getLogger().info(MODULE, "=====================================");
		LogManager.getLogger().info(MODULE, testCaseData.getBehaviour());
		LogManager.getLogger().info(MODULE, "=====================================");

		TranslatorPolicyDataImpl translationPolicyDataObj = testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj();
		diameterWebServiceTranslator.init(translationPolicyDataObj);

		diameterWebServiceTranslator.init(testCaseData.getBaseTranslationMappingConfigurationData().getTranslationPolicyDataObj());
	
		DiameterAnswer fromDiameterPacket = (DiameterAnswer) testCaseData.getFromDiameterRequest().create();
		Map<String,String> srcMap = testCaseData.getWebServiceRequestMap();

		Map<String,String> toWSMap = new LinkedHashMap<String, String>(); 
		DiameterPacket destDiameterPacket = new DiameterAnswer();
		TranslatorParams params = new TranslatorParamsImpl(fromDiameterPacket, toWSMap, srcMap,  destDiameterPacket);

		diameterWebServiceTranslator.translateResponse(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj().getTransMapConfId(), params);

		assertEquals(testCaseData.getWebServiceRequestMap(),toWSMap);

	}

	@Test
	@Parameters(method = "dataFor_responseTranslationMapping")
	public void translateResponseFromDiameterToWebService(MappingSelectionTestCaseData testCaseData) throws Exception {

		LogManager.getLogger().info(MODULE, "=====================================");
		LogManager.getLogger().info(MODULE, testCaseData.getBehaviour());
		LogManager.getLogger().info(MODULE, "=====================================");

		TranslatorPolicyDataImpl translationPolicyDataObj = testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj();
		diameterWebServiceTranslator.init(translationPolicyDataObj);

		DiameterAnswer fromDiameterPacket = (DiameterAnswer) testCaseData.getFromDiameterRequest().create();
		Map<String,String> srcMap = testCaseData.getWebServiceRequestMap();

		Map<String,String> toWSMap = new LinkedHashMap<String, String>(); 
		DiameterPacket destDiameterPacket = new DiameterAnswer();
		TranslatorParams params = new TranslatorParamsImpl(fromDiameterPacket, toWSMap, srcMap,  destDiameterPacket);

		diameterWebServiceTranslator.translateResponse(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj().getTransMapConfId(), params);

		assertEquals(testCaseData.getWebServiceRequestMap(),toWSMap);
	}

	@Test
	public void throwsTranslationFailedExceptionWhenWebServiceIsNull() throws Exception {

		Map<String,String> wsMap = null;
		DiameterPacket diameterPacket = new DiameterAnswer();

		TranslatorParams params = new TranslatorParamsImpl(wsMap, diameterPacket);

		exception.expect(TranslationFailedException.class);
		exception.expectMessage("Invalid Input Parameters");

		diameterWebServiceTranslator.translateRequest(DUMMY_ID, params);
	}
	
	@Test
	public void throwsTranslationFailedExceptionWhenDiameterPacketIsNull() throws Exception {

		Map<String,String> wsMap =  new LinkedHashMap<String, String>();
		DiameterPacket diameterPacket = null;

		TranslatorParams params = new TranslatorParamsImpl(wsMap, diameterPacket);

		exception.expect(TranslationFailedException.class);
		exception.expectMessage("Invalid Input Parameters");

		diameterWebServiceTranslator.translateRequest(DUMMY_ID, params);
	}
	
	@Test
	public void throwsTranslationFailedExceptionWhenErrorBitOfDiameterPacketIsSet() throws Exception {

		Map<String,String> wsMap =  new LinkedHashMap<String, String>();
		DiameterPacket diameterPacket =new DiameterAnswer();
		diameterPacket.setErrorBit();

		TranslatorParams params = new TranslatorParamsImpl(wsMap, diameterPacket);

		exception.expect(TranslationFailedException.class);
		exception.expectMessage("Invalid Input Parameters");

		diameterWebServiceTranslator.translateRequest(DUMMY_ID, params);
	}

	@Test
	public void throwsTranslationFailedExceptionWhenDiameterAnswerIsNull() throws Exception {

		DiameterAnswer fromDiameterPacket = null;
		Map<String,String> toWSMap = null;
		Map<String,String> srcMap = null;
		DiameterPacket destDiameterPacket = null;

		TranslatorParams params = new TranslatorParamsImpl(fromDiameterPacket, toWSMap, srcMap,  destDiameterPacket);

		exception.expect(TranslationFailedException.class);
		exception.expectMessage("Source Diameter Answer (From Packet) is null");

		diameterWebServiceTranslator.translateResponse(DUMMY_ID, params);
	}
}
