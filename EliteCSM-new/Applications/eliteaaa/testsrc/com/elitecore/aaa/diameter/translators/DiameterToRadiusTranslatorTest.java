package com.elitecore.aaa.diameter.translators;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.elitecore.aaa.core.commons.util.exception.AttributeNotFoundException;
import com.elitecore.aaa.core.commons.util.exception.InvalidPacketException;
import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.diameter.translators.data.MappingSelectionTestCaseData;
import com.elitecore.aaa.diameter.translators.data.ResponseTranslationTestCaseData;
import com.elitecore.aaa.diameter.translators.data.ResquestTranslationTestCaseData;
import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.config.ListWrapper;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.NullLogger;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.packet.RadiusPacketBuilder;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

@RunWith(JUnitParamsRunner.class)
public class DiameterToRadiusTranslatorTest {

	@Mock private ServerContext aaaServerContext;
	private DiameterToRadiusTranslator translatorUnderTest;
	
	static {
		DummyDiameterDictionary.getInstance();
		RadiusDictionaryTestHarness.getInstance();
	}
	
	@BeforeClass
	public static void init() {
		LogManager.setDefaultLogger(new NullLogger());
	}
	
	public static Object[] dataFor_testMappingSelection() throws FileNotFoundException, UnsupportedEncodingException, JAXBException {
		
		ClasspathResource resource = ClasspathResource.at("d2rtestcasexmls/mapping-selection-test-data.xml");
		
		ListWrapper<MappingSelectionTestCaseData> caseCollection = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()),
				ListWrapper.class, MappingSelectionTestCaseData.class);
		
		return caseCollection.getList().toArray();
	}
	
	@Before
	public void beforeTest() {
		translatorUnderTest = new DiameterToRadiusTranslator(aaaServerContext);
	}
	
	@Test
	@Parameters(method="dataFor_testMappingSelection")
	public void testMappingSelection(MappingSelectionTestCaseData testCaseData) 
			throws TranslationFailedException, AttributeNotFoundException, InvalidPacketException, InitializationFailedException {
		
		translatorUnderTest.init(testCaseData.getTranslationMappingConfigurationData().getTranslationPolicyDataObj());
		
		RadiusPacket translatedPacket = new RadiusPacket();
		TranslatorParams params = new TranslatorParamsImpl(
				testCaseData.getFromDiameterRequest().create(), 
				translatedPacket);
		translatorUnderTest.translateRequest(testCaseData.getTranslationMappingConfigurationData()
				.getTransMappingConfId(), params);

		assertEquals(testCaseData.getExpectedMapping(), 
				params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING));
		
	}
	
	public static Object[] dataFor_testTranslateRequest() 
			throws FileNotFoundException, UnsupportedEncodingException, JAXBException {
		
		ClasspathResource resource = ClasspathResource.at("d2rtestcasexmls/translate-request-test-data.xml");
		
		ListWrapper<ResquestTranslationTestCaseData> caseCollection = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()),
				ListWrapper.class, ResquestTranslationTestCaseData.class);
		
		return caseCollection.getList().toArray();
	}
	
	@Test
	@Parameters(method="dataFor_testTranslateRequest")
	public void testTranslateRequest(ResquestTranslationTestCaseData testCaseData) 
			throws TranslationFailedException, AttributeNotFoundException, InvalidPacketException, InitializationFailedException {
		
		translatorUnderTest.init(testCaseData.getTranslationMappingConfigurationData()
				.getTranslationPolicyDataObj());
		
		RadiusPacket translatedPacket = new RadiusPacket();
		TranslatorParams params = new TranslatorParamsImpl(
				testCaseData.getFromDiameterRequest().create(), 
				translatedPacket);
		translatorUnderTest.translateRequest(testCaseData.getTranslationMappingConfigurationData()
				.getTransMappingConfId(), params);

		RadiusPacket expectedRadPacket = testCaseData.getExpectedRadPacket().create(new RadiusPacketBuilder());
		translatedPacket.refreshPacketHeader();
		
		assertArrayEquals(expectedRadPacket.getBytes(true), translatedPacket.getBytes(true));
		
	}
	
	
	public static Object[] dataFor_testTranslateResponse() 
			throws FileNotFoundException, UnsupportedEncodingException, JAXBException {
		
		ClasspathResource resource = ClasspathResource.at("d2rtestcasexmls/translate-response-test-data.xml");
		
		ListWrapper<ResponseTranslationTestCaseData> caseCollection = ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()),
				ListWrapper.class, ResponseTranslationTestCaseData.class);
		
		return caseCollection.getList().toArray();
	}
	
	@Test
	@Parameters(method="dataFor_testTranslateResponse")
	public void testTranslateResponse(ResponseTranslationTestCaseData testCaseData) 
			throws TranslationFailedException, AttributeNotFoundException, InvalidPacketException, InitializationFailedException {
		
		translatorUnderTest.init(testCaseData.getTranslationMappingConfigurationData()
				.getTranslationPolicyDataObj());
		
		DiameterRequest sourceRequest = (DiameterRequest) testCaseData.getSourceDiameterRequest().create();

		DiameterAnswer translatedPacket = new DiameterAnswer(sourceRequest);
		
		TranslatorParams params = new TranslatorParamsImpl(
				testCaseData.getRadResponsePacket().create(new RadiusPacketBuilder()), 
				translatedPacket, sourceRequest, 
				testCaseData.getDestRadPacket().create(new RadiusPacketBuilder()));
		
		params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, testCaseData.getSelectedMapping());
		
		translatorUnderTest.translateResponse(testCaseData.getTranslationMappingConfigurationData()
				.getTransMappingConfId(), params);

		DiameterPacket expectedPacket = testCaseData.getExpectedDiameterAnswer().create();
		
		assertArrayEquals(
				expectedPacket.getBytes(DiameterPacket.INCLUDE_INFO_ATTRIBUTE), 
				translatedPacket.getBytes(DiameterPacket.INCLUDE_INFO_ATTRIBUTE));
		
		
	}
	
}
