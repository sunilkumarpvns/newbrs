package com.elitecore.aaa.radius.translators.keyword;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.InetAddress;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.translator.keyword.KeywordContextStub;
import com.elitecore.aaa.diameter.translators.StrOptKeyword;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService;
import com.elitecore.aaa.radius.translators.RadiusDstKeyword;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;

public class RadiusDstKeywordTest {

	private static final String INVALID_PARAM = "INVALID_PARAM";
	private static final String DUMMY_VALUE = "test";
	private static final String CLASS_ATTRIB_ID_WITH_SPACE = "  0:25";
	private static final String CALLING_STATION_ID_ATTRIB_ID_WITH_SPACE = "  0:31";

	private RadiusDstKeyword radiusDstKeyword;
	private RadiusPacket packet;
	private TranslatorParamsImpl translatorParam;
	private RequestValueProvider valueProvider;
	private KeywordContextStub keywordContext;
	private RadServiceRequest radServiceRequest;

	@Mock private AAAServerContext aaaServerContext;

	@BeforeClass
	public static void setUpBeforeClass() {
		RadiusDictionaryTestHarness.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		keywordContext = new KeywordContextStub();
		packet = new RadiusPacket();
		
		addAttribute(RadiusAttributeConstants.STATE_STR, CALLING_STATION_ID_ATTRIB_ID_WITH_SPACE);
		addAttribute(RadiusAttributeConstants.CLASS_STR,DUMMY_VALUE);
		addAttribute(RadiusAttributeConstants.USER_NAME_STR, CLASS_ATTRIB_ID_WITH_SPACE);
		addAttribute(RadiusAttributeConstants.CALLED_STATION_ID_STR, RadiusAttributeConstants.NAS_IP_ADDRESS_STR);

		radiusDstKeyword = new RadiusDstKeyword(TranslatorConstants.DESTINATION_REQUEST, keywordContext);
		keywordContext.addRequestKeyword(new StrOptKeyword(TranslatorConstants.STROPT, keywordContext)).addRequestKeyword(radiusDstKeyword);
		translatorParam = new TranslatorParamsImpl(null, null);
		translatorParam.setParam(TranslatorConstants.DESTINATION_REQUEST,packet);

		radServiceRequest = new BaseRadiusAuthService.RadiusAuthRequestImpl(packet.getBytes(),
				InetAddress.getLocalHost(), 0, aaaServerContext, null);
		valueProvider = new RequestValueProvider(radServiceRequest);
	}

	@Test
	public void returnsConfiguredAttributeValueFromDestinationRequest() {
		assertEquals(DUMMY_VALUE, 
				radiusDstKeyword.getKeywordValue(translatorParam,"${DSTREQ}:25", true, valueProvider));
	}
	
	@Test
	public void evaluatesKeywordAndReturnsAttributeValueFromDestinationRequest(){

		assertEquals(DUMMY_VALUE,radiusDstKeyword.getKeywordValue(translatorParam,"${DSTREQ}:${STROP-(trim)}:0:1", true, valueProvider));
	}

	@Test
	public void returnsNULLWhenConfiguredAttributeNotFoundInDestinationRequest() {
		assertNull(radiusDstKeyword.getKeywordValue(translatorParam,"${DSTREQ}:0:31", true, valueProvider));
	}
	
	@Test
	public void returnsNullWhenAttributeEvaluatedFromKeywordNotFoundInDestinationRequest() {
		assertNull(radiusDstKeyword.getKeywordValue(translatorParam,"${DSTREQ}:${STROP-(trim)}:0:24", true, valueProvider));
	}

	@Test
	public void returnsNULLWhenPacketSelectionTypeIsInvalid(){

		radiusDstKeyword = new RadiusDstKeyword(INVALID_PARAM, keywordContext);

		assertNull(radiusDstKeyword.getKeywordValue(translatorParam,"${DSTREQ}:0:25", true, valueProvider));
	}

	@Test
	public void returnsNULLWhenKeywordArgumentIsInvalid(){

		assertNull(radiusDstKeyword.getKeywordValue(translatorParam,"${DSTREQ :", true, valueProvider));
	}

	@Test
	public void returnsNULLWhenAttributeNotFoundInKeyword(){

		assertNull(radiusDstKeyword.getKeywordValue(translatorParam,"${DSTREQ}:${STROP-(toUpperCase)}:0:31", true, valueProvider));
	}

	@Test
	public void returnsFirstOccurrenceFromMultipleOccurrenceOfSameAttributes() throws Exception{

		addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "OccurrenceOne");  
		addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "OccurrenceTwo");  
		addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "OccurrenceThree");

		assertEquals("OccurrenceOne",radiusDstKeyword.getKeywordValue(translatorParam,"${DSTREQ}:0:31", true, valueProvider));
	}

	private void addAttribute(String attr, String attrValue) throws Exception {

		IRadiusAttribute attribute = Dictionary.getInstance().getKnownAttribute(attr);
		attribute.setStringValue(attrValue);

		packet.addAttribute(attribute);
		packet.refreshPacketHeader();
		packet.refreshInfoPacketHeader();
	}
}
