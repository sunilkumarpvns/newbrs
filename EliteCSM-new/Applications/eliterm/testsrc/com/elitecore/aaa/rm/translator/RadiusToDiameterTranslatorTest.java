package com.elitecore.aaa.rm.translator;

import java.io.File;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.aaa.rm.translator.data.RadToDiaTranslationTestCaseData;
import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.config.ListWrapper;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.rm.core.diameter.DummyDiameterDictionary;
import com.elitecore.rm.core.radius.dictionary.RadiusDictionaryTestHarness;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class RadiusToDiameterTranslatorTest {

	private static final String MODULE = "RADIUS-TO-DIAMETER-TRANSLATOR-TEST";
	private RadiusToDiameterTranslator translator;
	
	@Mock private ServerContext serverContext; 
	
	@BeforeClass
	public static void setupBeforeClass() {
		DummyDiameterDictionary.getInstance();
		RadiusDictionaryTestHarness.getInstance();
	}
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	public static Object[] dataForRequestTranslation() throws Exception {
		ClasspathResource resource = ClasspathResource.at("translator/translate-request-test-data.xml");
		ListWrapper<RadToDiaTranslationTestCaseData> caseCollection = ConfigUtil.deserialize(new File(resource.getAbsolutePath()), ListWrapper.class, RadToDiaTranslationTestCaseData.class);
		return caseCollection.getList().toArray();
	}
	
	@Test
	@Parameters(method="dataForRequestTranslation")
	public void requestTranslation(RadToDiaTranslationTestCaseData testCaseData) throws Exception {
		
		LogManager.getLogger().info(MODULE, "=====================================");
		LogManager.getLogger().info(MODULE, testCaseData.getBehaviour());
		LogManager.getLogger().info(MODULE, "=====================================");
		
		translator = new RadiusToDiameterTranslator(serverContext);
		translator.init(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj());
		
		RadServiceRequest fromRadiusRequest = testCaseData.getFromRadiusPacket().create(new RadAuthRequestBuilder());
		DiameterRequest translatedRequest = new DiameterRequest();
		TranslatorParams params = new TranslatorParamsImpl(fromRadiusRequest, translatedRequest);
	
		translator.translateRequest(testCaseData.getTranslationMappingConfigurationData().getTransMappingConfId(),
				params);
		
		MatcherAssert.assertThat(translatedRequest, testCaseData.getCustomAssertion().createRequestMatcher());
		
		/*Translation keywords can be applied for advanced support. 
					Keywords can be configured in RHS part of expression like LHS=${KEYWORD[-ARG]}:SRC_ARG,
					SRC_ARG can either be identifier or another keyword expression
		 * 
		 * <contains-attribute id="0:25" value="poolname=ippool-1" />
		<contains-attribute id="0:25" value="poolid=1" />
		<contains-attribute id="0:25" value="serialnumber=1" />*/
		
	}
	
	public static Object[] dataForResponseTranslation() throws Exception{
		ClasspathResource resource = ClasspathResource.at("translator/translate-response-test-data.xml");
		ListWrapper<RadToDiaTranslationTestCaseData> caseCollection = ConfigUtil.deserialize(new File(resource.getAbsolutePath()), ListWrapper.class, RadToDiaTranslationTestCaseData.class);
		return caseCollection.getList().toArray();
	}
	
	/*@Test
	@Parameters(method="dataForResponseTranslation")
	public void responseTranslation(RadToDiaTranslationTestCaseData testCaseData) throws Exception {
		
		LogManager.getLogger().info(MODULE, "=====================================");
		LogManager.getLogger().info(MODULE, testCaseData.getBehaviour());
		LogManager.getLogger().info(MODULE, "=====================================");
		
		translator = new RadiusToDiameterTranslator(serverContext);
		translator.init(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj());
		
		RadServiceRequest fromRadiusRequest = testCaseData.getFromRadiusPacket().create(new RadAuthRequestBuilder());
		DiameterRequest destDiameterRequest = (DiameterRequest) testCaseData.getDestDiameterRequest().create();
		DiameterAnswer diameterAnswer = (DiameterAnswer) testCaseData.getDiameterAnswer().create();

		
		
		
		
	}*/
}
