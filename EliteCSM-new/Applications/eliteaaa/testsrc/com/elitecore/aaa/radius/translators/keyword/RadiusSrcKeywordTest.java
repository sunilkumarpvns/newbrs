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
import com.elitecore.aaa.radius.translators.RadiusSrcKeyword;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;

public class RadiusSrcKeywordTest {

	private static final String INVALID_PARAM = "INVALID_PARAM";
	private static final String DUMMY_VALUE = "test";
	private static final String CLASS_ATTRIB_ID_WITH_SPACE = "  0:25";
	private static final String CALLING_STATION_ID_ATTRIB_ID_WITH_SPACE = "  0:31";
	
	private RadiusSrcKeyword radiusSrcKeyword;
	private RadServiceRequest radServiceRequest; 
	private TranslatorParamsImpl translatorParam;
	private RequestValueProvider valueProvider;
	private KeywordContextStub keywordContext;

	@Mock private AAAServerContext aaaServerContext;

	@BeforeClass
	public static void setUpBeforeClass() {
		RadiusDictionaryTestHarness.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		keywordContext = new KeywordContextStub();
		radiusSrcKeyword = new RadiusSrcKeyword(TranslatorConstants.SOURCE_REQUEST, keywordContext);
		keywordContext.addRequestKeyword(new StrOptKeyword(TranslatorConstants.STROPT, keywordContext)).addRequestKeyword(radiusSrcKeyword);
		radServiceRequest = new BaseRadiusAuthService.RadiusAuthRequestImpl(new RadiusPacket().getBytes(),
				InetAddress.getLocalHost(), 0, aaaServerContext, null);

		addAttribute(RadiusAttributeConstants.CLASS_STR, DUMMY_VALUE);
		addAttribute(RadiusAttributeConstants.STATE_STR, CALLING_STATION_ID_ATTRIB_ID_WITH_SPACE);
		addAttribute(RadiusAttributeConstants.USER_NAME_STR, CLASS_ATTRIB_ID_WITH_SPACE);
		addAttribute(RadiusAttributeConstants.CALLED_STATION_ID_STR, RadiusAttributeConstants.NAS_IP_ADDRESS_STR);

		translatorParam = new TranslatorParamsImpl(null, null);
		translatorParam.setParam(TranslatorConstants.SOURCE_REQUEST, radServiceRequest);
		valueProvider = new RequestValueProvider(radServiceRequest);
	}

	@Test
	public void returnsConfiguredAttributeValueFromSourceRequest() {
		assertEquals(DUMMY_VALUE, 
				radiusSrcKeyword.getKeywordValue(translatorParam,"${SRCREQ}:25", true, valueProvider));
	}
	
	@Test
	public void evaluatesKeywordAndReturnsAttributeValueFromSourceRequest() {
		assertEquals(DUMMY_VALUE, 
				radiusSrcKeyword.getKeywordValue(translatorParam,"${SRCREQ}:${STROP-(trim)}:0:1", true, valueProvider));
	}

	@Test
	public void returnsNullWhenConfiguredAttributeNotFoundInSourceRequest() {
		assertNull(radiusSrcKeyword.getKeywordValue(translatorParam,"${SRCREQ}:0:31", true, valueProvider));
	}
	
	@Test
	public void returnsNullWhenAttributeEvaluatedFromKeywordNotFoundInSourceRequest() {
		assertNull(radiusSrcKeyword.getKeywordValue(translatorParam,"${SRCREQ}:${STROP-(trim)}:0:24", true, valueProvider));
	}

	@Test
	public void returnsNullWhenPacketSelectionTypeIsInvalid() {
		radiusSrcKeyword = new RadiusSrcKeyword(INVALID_PARAM, keywordContext);

		assertNull(radiusSrcKeyword.getKeywordValue(translatorParam,"${SRCREQ}:0:25", true, valueProvider));
	}

	@Test
	public void returnsNullWhenKeywordArgumentIsInvalid() {
		assertNull(radiusSrcKeyword.getKeywordValue(translatorParam, "${SRCREQ}:", true, valueProvider));
	}

	@Test
	public void returnsNullWhenAttributeNotFoundInKeyword() {
		assertNull(radiusSrcKeyword.getKeywordValue(translatorParam,"${SRCREQ}:${STROP-(toUpperCase)}:0:31", true, valueProvider));
	}

	@Test
	public void returnsFirstOccurrenceFromMultipleOccurrencesOfSameAttribute() throws Exception {
		addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "OccurrenceOne");
		addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "OccurrenceTwo");
		addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "OccurrenceThree");

		assertEquals("OccurrenceOne", radiusSrcKeyword.getKeywordValue(translatorParam,"${SRCREQ}:0:31", true, valueProvider));
	}

	private void addAttribute(String attrId, String attrValue) throws Exception {

		IRadiusAttribute attribute = Dictionary.getInstance().getKnownAttribute(attrId);
		attribute.setStringValue(attrValue);
		radServiceRequest.addInfoAttribute(attribute);
	}
}
