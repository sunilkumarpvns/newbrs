package com.elitecore.aaa.core.radius.translators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService;
import com.elitecore.aaa.radius.translators.RadiusSrcResKeyword;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;

public class RadiusSrcResKeywordTest {
	
	@Mock private KeywordContext context;
	private RadiusSrcResKeyword radiusSrcResKeyword;
	private RadServiceResponse radServiceResponse;
	private TranslatorParams translatorParams;
	
	static {
		RadiusDictionaryTestHarness.getInstance();
	}
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		radiusSrcResKeyword = new RadiusSrcResKeyword(TranslatorConstants.SRCRES, context);
		radServiceResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(new byte[]{}, 0, null);
		translatorParams = new TranslatorParamsImpl(null, null);
		translatorParams.setParam(TranslatorConstants.SRCRES, radServiceResponse);
	}

	private void addAttribute(String attrId, String attrValue) {
		Mockito.when(context.isKeyword(attrId)).thenReturn(false);
		
		IRadiusAttribute attribute = Dictionary.getInstance().getKnownAttribute(attrId);
		attribute.setStringValue(attrValue);
		radServiceResponse.addAttribute(attribute);
	}
	
	@Test
	public void testGetKeywordValue_WhenIsRequestBooleanIsFalse() {
		String keywordValue = radiusSrcResKeyword.getKeywordValue(translatorParams, "${SRCRES}:0:1", false, null);
		assertNull(keywordValue);
	}
	
	@Test
	public void testGetKeywordValue_WhenKeywordIsWithoutArgument() {
		String keywordValue = radiusSrcResKeyword.getKeywordValue(translatorParams, "${SRCRES}", true, null);
		assertNull(keywordValue);
	}
	
	@Test
	public void testGetKeywordValue_WhenFromPacketInTranslationParamsIsNull() {
		translatorParams = new TranslatorParamsImpl(null, null);
		
		String keywordValue = radiusSrcResKeyword.getKeywordValue(translatorParams, "${SRCRES}:0:1", true, null);
		assertNull(keywordValue);
	}
	
	
	@Test
	public void testGetKeywordValue_WhenArgumentAttributeIsFoundInPacket_ShouldReturnValueOfAttribute() throws InvalidAttributeIdException {
		addAttribute("0:1", "eliteaaa");
		
		String keywordValue = radiusSrcResKeyword.getKeywordValue(translatorParams, "${SRCRES}:0:1", true, null);
		assertEquals("eliteaaa", keywordValue);
	}
	
	@Test
	public void testGetKeywordValue_WhenArgumentAttributeIsNotFoundInPacket_ShouldReturnNull() throws InvalidAttributeIdException {
		addAttribute("0:1", "eliteaaa");
		
		String keywordValue = radiusSrcResKeyword.getKeywordValue(translatorParams, "${SRCRES}:0:4", true, null);
		assertNull(keywordValue);
	}
	
	@Test
	public void testGetKeywordValue_WhenArgumentKeywordIsInfoAttribute_ShouldReturnValueFromAttribute() throws InvalidAttributeIdException {
		String attrId = "21067:148";
		String attrValue = "eliteaaa";
		Mockito.when(context.isKeyword(attrId)).thenReturn(false);
		
		IRadiusAttribute attribute = Dictionary.getInstance().getKnownAttribute(attrId);
		attribute.setStringValue(attrValue);
		radServiceResponse.addInfoAttribute(attribute);
		
		String keywordValue = radiusSrcResKeyword.getKeywordValue(translatorParams, "${SRCRES}:21067:148", true, null);

		assertEquals(attrValue, keywordValue);
	}
	
	@Test
	public void testGetKeywordValue_WhenArgumentAttributeHasMultipleOccuranceInPacket_ShouldReturnValueOfFirstOccurance() throws InvalidAttributeIdException {
		addAttribute("0:1", "eliteaaa1");
		addAttribute("0:1", "eliteaaa2");
		addAttribute("0:1", "eliteaaa3");
		
		String keywordValue = radiusSrcResKeyword.getKeywordValue(translatorParams, "${SRCRES}:0:1", true, null);
		assertEquals("eliteaaa1", keywordValue);
	}
	
	@Test
	public void testGetKeywordValue_WhenArgumentIsAKeyword_AndIsAvailableInPacket_ShouldReturnValueOfAttribute() throws InvalidAttributeIdException {
		Mockito.when(context.isKeyword("${TIMESTEMP}")).thenReturn(true);
		Mockito.when(context.getKeywordValue("${TIMESTEMP}", translatorParams, true, null)).thenReturn("0:1");
		
		addAttribute("0:1", "eliteaaa");
		
		String keywordValue = radiusSrcResKeyword.getKeywordValue(translatorParams, "${SRCRES}:${TIMESTEMP}", true, null);
		assertEquals("eliteaaa", keywordValue);
	}
	
	@Test
	public void testGetKeywordValue_WhenArgumentIsAKeyword_AndIsUnavailableInPacket_ShouldReturnNull() throws InvalidAttributeIdException {
		Mockito.when(context.isKeyword("${TIMESTEMP}")).thenReturn(true);
		Mockito.when(context.getKeywordValue("${TIMESTEMP}", translatorParams, true, null)).thenReturn("0:4");
		
		addAttribute("0:1", "eliteaaa");
		
		String keywordValue = radiusSrcResKeyword.getKeywordValue(translatorParams, "${SRCRES}:${TIMESTEMP}", true, null);
		assertNull(keywordValue);
	}
	
	@Test
	public void testGetKeywordValue_WhenArgumentIsAKeyword_AndOutPutOfItIsNull_ShouldReturnNull() throws InvalidAttributeIdException {
		Mockito.when(context.isKeyword("${TIMESTEMP}")).thenReturn(true);
		Mockito.when(context.getKeywordValue("${TIMESTEMP}", translatorParams, true, null)).thenReturn(null);
		
		addAttribute("0:1", "eliteaaa");
		
		String keywordValue = radiusSrcResKeyword.getKeywordValue(translatorParams, "${SRCRES}:${TIMESTEMP}", true, null);
		assertNull(keywordValue);
	}
	
}
