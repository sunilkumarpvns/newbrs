package com.elitecore.aaa.core.diameter.translators;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.aaa.diameter.translators.DBSessionKeyword;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

@RunWith(JUnitParamsRunner.class)
public class DBSessionKeywordTest {
	
	@Rule public ExpectedException expectedException = ExpectedException.none(); 
	
	@Mock private KeywordContext keywordContext;
	@Mock private TranslatorParams translatorParams;
	@Mock private DiameterRequest diameterRequest;
	@Mock private ValueProvider valueProvider;
	private List<SessionData> locatedSessionData = new ArrayList<SessionData>();
	private DBSessionKeyword sessKeyword = new DBSessionKeyword(TranslatorConstants.DBSESSION, keywordContext);

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(translatorParams.getParam(AAATranslatorConstants.SOURCE_REQUEST)).thenReturn(diameterRequest);
		Mockito.when(diameterRequest.getLocatedSessionData()).thenReturn(locatedSessionData);
		
		for ( int i = 1; i <= 5; i++) {
			SessionData sessionData = new SessionDataImpl("test");
			sessionData.addValue("a" + i, "a" + i);
			sessionData.addValue("b" + i, "b" + i);
			sessionData.addValue("c" + i, "c" + i);
			sessionData.addValue("d" + i, "d" + i);
			sessionData.addValue("e" + i, "e" + i);
			locatedSessionData.add(sessionData);
		}
	}
	
	@Test
	public void testGetKeywordValue_ShouldThorowClassCastException_WhenTranslatorParamsDoesNotContainsDiameterRequestAsSourceRequest() {
		Mockito.when(translatorParams.getParam(AAATranslatorConstants.SOURCE_REQUEST)).thenReturn(new DiameterAnswer());
		expectedException.expect(ClassCastException.class);
		expectedException.expectMessage("com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer cannot be cast to com.elitecore.diameterapi.diameter.common.packet.DiameterRequest");
		sessKeyword.getKeywordValue(translatorParams, "${DBSESSION - a1}", TranslatorConstants.RESPONSE_TRANSLATION, valueProvider);
	}
	
	public static Object[][] dataFor_testGetKeywordValue_ShouldReturnNull() {
		return new Object[][] {
				{"${DBSESSION - a1", null},
				{"${DBSESSION}", null},
				{"${DBSESSION }", null},
				{"${DBSESSION - }", null},
				{"${DBSESSION - a1, a2}", null},
				
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testGetKeywordValue_ShouldReturnNull")
	public void testGetKeywordValue_ShouldReturnNull(String keyword, String expectedAnswer) {
		String keywordValue = sessKeyword.getKeywordValue(translatorParams, keyword, TranslatorConstants.RESPONSE_TRANSLATION, valueProvider);
		assertEquals(expectedAnswer ,keywordValue);
	}
	
	public static Object[][] dataFor_testGetKeywordValue_ShouldReturnExpectedOperator() {
		return new Object[][] {
				{"${DBSESSION - a1}", "a1"},
				{"${DBSESSION- a1}", "a1"},
				{"${DBSESSION -a1}", "a1"},
				{"${DBSESSION-a1}", "a1"},
				{"$DBSESSION - a1}", "a1"},
				{"{DBSESSION - a1}", "a1"},
				{"- a1}", "a1"},
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testGetKeywordValue_ShouldReturnExpectedOperator")
	public void testGetKeywordValue_ShouldReturnExpectedOperator(String keyword, String expectedAnswer) {
		String keywordValue = sessKeyword.getKeywordValue(translatorParams, keyword, TranslatorConstants.RESPONSE_TRANSLATION, valueProvider);
		assertEquals(expectedAnswer ,keywordValue);
	}
	
	@Test
	public void estGetKeywordValue_ShouldReturnNull_WhenLocatedSessionDataIsNull() {
		Mockito.when(diameterRequest.getLocatedSessionData()).thenReturn(null);
		String keywordValue = sessKeyword.getKeywordValue(translatorParams, "${DBSESSION - a1}", TranslatorConstants.RESPONSE_TRANSLATION, valueProvider);
		assertEquals(null, keywordValue);
	}
}