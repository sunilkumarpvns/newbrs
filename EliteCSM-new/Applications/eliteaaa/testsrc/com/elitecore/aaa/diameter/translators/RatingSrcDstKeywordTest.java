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
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.ratingapi.data.RequestPacketFactory;
import com.elitecore.ratingapi.util.IRequestPacketFactory;
import com.elitecore.ratingapi.util.IRequestParameters;

public class RatingSrcDstKeywordTest {



	private static final String CLASS_AVP_VALUE = "test" ;
	private static final String CLASS_ATTRIB_ID_WITH_SPACE = "  0:25" ;

	private RatingSrcDstKeyword ratingSrcDstKeyword ;
	private TranslatorParamsImpl translatorParam ;
	private ValueProvider valueProvider;
	private IRequestParameters requestPacket;
	private IRequestPacketFactory iRequestPacketFactory ;
	
	
	private KeywordContextStub keywordContext;

	@Mock private AAAServerContext aaaServerContext;

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		
		
		MockitoAnnotations.initMocks(this);
		
		iRequestPacketFactory = new RequestPacketFactory();
		requestPacket = iRequestPacketFactory.getRequestPacket();

		keywordContext = new KeywordContextStub();
		ratingSrcDstKeyword = new RatingSrcDstKeyword(TranslatorConstants.SOURCE_REQUEST,keywordContext);
		keywordContext.addRequestKeyword(new StrOptKeyword(TranslatorConstants.STROPT, keywordContext))
		.addRequestKeyword(ratingSrcDstKeyword);

		translatorParam = new TranslatorParamsImpl(null, null);

		translatorParam.setParam(TranslatorConstants.SOURCE_REQUEST, requestPacket);
		valueProvider = new RatingRequestValueProvider(requestPacket);
		
		requestPacket.put("keyOne", "valueOne");
		requestPacket.put("key2", "valueTwo");
		requestPacket.put("0:4", "1454");
		requestPacket.put("0:25", CLASS_AVP_VALUE);
		requestPacket.put("0:1", CLASS_ATTRIB_ID_WITH_SPACE);
	}

	@Test
	public void returnsConfiguredAttributeValueFromSourceRequest() {
		assertEquals("1454", 
				keywordContext.getKeywordValue("${SRCREQ}:0:4", translatorParam, true, valueProvider));
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
	public void returnsNullIfSourceRequestNotFound() {

		translatorParam.setParam(TranslatorConstants.SOURCE_REQUEST, null);

		assertNull(keywordContext.getKeywordValue("${SRCREQ}:0:25", translatorParam, true, valueProvider));
	}

	private static class RatingRequestValueProvider implements ValueProvider{

		private IRequestParameters requestParameters;
		
		public RatingRequestValueProvider(IRequestParameters requestParameters) {
			this.requestParameters = requestParameters;
		}
		
		@Override
		public String getStringValue(String identifier) {
			return requestParameters.get(identifier);
		}
	}
}
