package com.elitecore.aaa.diameter.translators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.translator.keyword.KeywordContextStub;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacketBuilder;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;

public class DiameterSrcDstKeywordTest {

	private static final String CLASS_AVP_VALUE = "test" ;
	private static final String CLASS_ATTRIB_ID_WITH_SPACE = "  0:25" ;

	private DiameterSrcDstKeyword diameterSrcDstKeyword ;
	private TranslatorParamsImpl translatorParam ;
	private DiameterPacketValueProvider valueProvider ;
	private DiameterRequest diameterRequest ;
	private KeywordContextStub keywordContext;

	@Mock private AAAServerContext aaaServerContext;

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		keywordContext = new KeywordContextStub();
		diameterSrcDstKeyword = new DiameterSrcDstKeyword(TranslatorConstants.SOURCE_REQUEST,keywordContext);
		keywordContext.addRequestKeyword(new StrOptKeyword(TranslatorConstants.STROPT, keywordContext))
		.addRequestKeyword(diameterSrcDstKeyword);

		translatorParam = new TranslatorParamsImpl(null, null);

		diameterRequest = DiameterPacketBuilder.localRequestBuilder().build();
		translatorParam.setParam(TranslatorConstants.SOURCE_REQUEST, diameterRequest);
		valueProvider = new DiameterPacketValueProvider(diameterRequest);

		addAttribute(RadiusAttributeConstants.NAS_PORT_STR, "1454");
		addAttribute(RadiusAttributeConstants.CLASS_STR, CLASS_AVP_VALUE);
		addAttribute(RadiusAttributeConstants.USER_NAME_STR, CLASS_ATTRIB_ID_WITH_SPACE);

	}

	@Test
	public void returnsConfiguredAttributeValueFromSourceRequest() {
		assertEquals("1454", 
				keywordContext.getKeywordValue("${SRCREQ}:0:5", translatorParam, true, valueProvider));
	}

	@Test
	public void evaluatesKeywordAndReturnsAttributeValueFromSourceRequest() {
		assertEquals(CLASS_AVP_VALUE, 
				keywordContext.getKeywordValue("${SRCREQ}:${STROP-(trim)}:0:1", translatorParam, true, valueProvider));
	}

	@Test
	public void returnsNullWhenConfiguredAttributeNotFoundInSourceRequest() {
		assertNull(keywordContext.getKeywordValue("${SRCREQ}:${STROP-(trim)}:0:30", translatorParam, true, valueProvider));
	}

	@Test
	public void returnsNullWhenArgumentNotFoundInKeyword() {
		assertNull(keywordContext.getKeywordValue("${SRCREQ}", translatorParam, true, valueProvider));
	}

	@Test
	public void returnsFirstValueAmongMultipleOccurrenceOfAttributes() throws Exception {
		addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "OccurrenceOne");
		addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "OccurrenceTwo");
		addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "OccurrenceThree");

		assertEquals("OccurrenceOne",keywordContext.getKeywordValue("${SRCREQ}:0:31", translatorParam,true, valueProvider));
	}

	@Test
	public void returnsNullIfSourceRequestNotFound() {

		translatorParam.setParam(TranslatorConstants.SOURCE_REQUEST, null);

		assertNull(keywordContext.getKeywordValue("${SRCREQ}:0:25", translatorParam, true, valueProvider));
	}

	private void addAttribute(String attrId, String attrValue) throws Exception {
		IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(attrId);
		diameterAVP.setStringValue(attrValue);
		diameterRequest.addAvp(diameterAVP);
	}
}
