package com.elitecore.aaa.diameter.translators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.translator.keyword.KeywordContextStub;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;

public class WsSrcDstKeywordTest {

	private static final String WEB_SERVICE_FIVE_WITH_SPACE_KEY = "webServiceFiveWithSpace";
	private static final String WEB_SERVICE_FIVE_KEY = "webServiceFive";
	private static final String WEB_SERVICE_FOUR_KEY = "webServiceFour"; 
	private static final String CALL_WEB_SERVICE_FOUR = "callWebServiceFour";
	private static final String CALL_WEB_SERVICE_TWO_FIVE = "callWebServiceTwoFive" ;

	private WsSrcDstKeyword wsSrcDstKeyword ;
	private TranslatorParamsImpl translatorParam ;
	private ValueProvider valueProvider;
	private Map<String, String> keyValueMap;
	
	
	private KeywordContextStub keywordContext;

	@Mock private AAAServerContext aaaServerContext;

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		
		
		MockitoAnnotations.initMocks(this);
		
		keyValueMap = new HashMap<String, String>();

		keywordContext = new KeywordContextStub();
		wsSrcDstKeyword = new WsSrcDstKeyword(TranslatorConstants.SOURCE_REQUEST,keywordContext);
		keywordContext.addRequestKeyword(new StrOptKeyword(TranslatorConstants.STROPT, keywordContext))
		.addRequestKeyword(wsSrcDstKeyword);

		translatorParam = new TranslatorParamsImpl(null, null);

		translatorParam.setParam(TranslatorConstants.SOURCE_REQUEST, keyValueMap);
		
		keyValueMap.put(WEB_SERVICE_FOUR_KEY, CALL_WEB_SERVICE_FOUR);
		keyValueMap.put(WEB_SERVICE_FIVE_KEY, CALL_WEB_SERVICE_TWO_FIVE);
		keyValueMap.put(WEB_SERVICE_FIVE_WITH_SPACE_KEY, "         webServiceFive");
	}

	@Test
	public void returnsConfiguredAttributeValueFromSourceRequest() {
		assertEquals(CALL_WEB_SERVICE_FOUR, 
				keywordContext.getKeywordValue("${SRCREQ}:webServiceFour", translatorParam, true, valueProvider));
	}

	@Test
	public void evaluatesKeywordAndReturnsAttributeValueFromSourceRequest() {
		assertEquals(CALL_WEB_SERVICE_TWO_FIVE, 
				keywordContext.getKeywordValue("${SRCREQ}:${STROP-(trim)}:webServiceFiveWithSpace", translatorParam, true, valueProvider));
	}

	@Test
	public void returnsNullWhenConfiguredAttributeNotFoundInSourceRequest() {
		assertNull(keywordContext.getKeywordValue("${SRCREQ}:${STROP-(trim)}:unknownService", translatorParam, true, valueProvider));
	}

	@Test
	public void returnsNullWhenArgumentNotFoundInKeyword() {
		assertNull(keywordContext.getKeywordValue("${SRCREQ}", translatorParam, true, valueProvider));
	}


	@Test
	public void returnsNullIfSourceRequestNotFound() {

		translatorParam.setParam(TranslatorConstants.SOURCE_REQUEST, null);

		assertNull(keywordContext.getKeywordValue("${SRCREQ}:webServiceFive", translatorParam, true, valueProvider));
	}
}
