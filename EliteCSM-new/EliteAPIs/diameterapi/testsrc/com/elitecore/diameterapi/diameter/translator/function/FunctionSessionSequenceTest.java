package com.elitecore.diameterapi.diameter.translator.function;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionEventListener;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.MappedValueProvider;


public class FunctionSessionSequenceTest {
	private static final long APP_ID = 1l;
	private static final long SESSION_SEQUENCE = 20;
	private static final long OTHER_SESSION_SEQUENCE = 2L;
	private FunctionSessionSequence sessionSequence ;
	private MappedValueProvider knownSessionIdvalueProvider = new ValueProviderImpl(SESSION_ID);
	public static String SESSION_ID = "netvertex.elitecore.com;123;456";
	public static String UNKNOWN_SESSION_ID = "netvertex.elitecore.com;133;456";
	private @Mock IStackContext stackContext;
	private DiameterSession session = new DiameterSession(SESSION_ID, new SessionEventListener() {
		
		@Override
		public void update(Session session) {
			
		}
		
		@Override
		public boolean removeSession(Session session) {
			return false;
		}
	});
	private String translationMappingName = "Test";
	private String otherTranslationMappingName = "Test2";
	
	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		sessionSequence = new FunctionSessionSequence(stackContext);
		doReturn(session).when(stackContext).getOrCreateSession(SESSION_ID, APP_ID);
		doReturn(true).when(stackContext).hasSession(SESSION_ID, APP_ID);
	}
	
	@Test
	public void returnTheAssociatedSequenceForTranslationMapping_FromSession_AndIncrementsSequenceForThatTranslationMapping_AndUpdatesTheSession() throws InvalidTypeCastException, MissingIdentifierException{
		session.setParameter(translationMappingName, SESSION_SEQUENCE);
		knownSessionIdvalueProvider.setValue(TranslatorConstants.COPY_PACKET_MAPPING_NAME, translationMappingName);
		knownSessionIdvalueProvider.setValue(TranslatorConstants.APPLICATION_ID, APP_ID);
		
		assertEquals(SESSION_SEQUENCE, sessionSequence.getLongValue(knownSessionIdvalueProvider));
		assertEquals(SESSION_SEQUENCE + 1, session.getParameter(translationMappingName));
	}
	
	@Test
	public void returnsIncrementedSequenceValuesForTranslationMapping_AndUpdatesSessionAccordingly() throws InvalidTypeCastException, MissingIdentifierException {
		session.setParameter(translationMappingName, SESSION_SEQUENCE);
		knownSessionIdvalueProvider.setValue(TranslatorConstants.COPY_PACKET_MAPPING_NAME, translationMappingName);
		knownSessionIdvalueProvider.setValue(TranslatorConstants.APPLICATION_ID, APP_ID);
		
		assertEquals(SESSION_SEQUENCE, sessionSequence.getLongValue(knownSessionIdvalueProvider));
		assertEquals(SESSION_SEQUENCE + 1, sessionSequence.getLongValue(knownSessionIdvalueProvider));
		assertEquals(SESSION_SEQUENCE + 2, sessionSequence.getLongValue(knownSessionIdvalueProvider));
		
		assertEquals(SESSION_SEQUENCE + 3, session.getParameter(translationMappingName));
	}
	
	@Test
	public void startsANewSequenceFromZero_IfNoSequenceIsAttachedWithSessionForTranslationMapping() throws InvalidTypeCastException, MissingIdentifierException {
		knownSessionIdvalueProvider.setValue(TranslatorConstants.COPY_PACKET_MAPPING_NAME, translationMappingName);
		knownSessionIdvalueProvider.setValue(TranslatorConstants.APPLICATION_ID, APP_ID);
		
		assertEquals(0L, sessionSequence.getLongValue(knownSessionIdvalueProvider));
		assertEquals(1L, session.getParameter(translationMappingName));
	}
	
	@Test
	public void keepsReturningZero_IfAssociatedTranslationMapping_IsNotFoundFromValueProvider() throws InvalidTypeCastException, MissingIdentifierException {
		assertEquals(0L, sessionSequence.getLongValue(knownSessionIdvalueProvider));
		assertEquals(0L, sessionSequence.getLongValue(knownSessionIdvalueProvider));
		assertEquals(0L, sessionSequence.getLongValue(knownSessionIdvalueProvider));
	}
	
	@Test
	public void returnZeroAsSequence_IfSessionForThatSessionIdIsNotFound() throws InvalidTypeCastException, MissingIdentifierException {
		ValueProviderImpl unknownSessionIdValueProvider = new ValueProviderImpl(UNKNOWN_SESSION_ID);
		unknownSessionIdValueProvider.setValue(TranslatorConstants.COPY_PACKET_MAPPING_NAME, translationMappingName);
		unknownSessionIdValueProvider.setValue(TranslatorConstants.APPLICATION_ID, APP_ID);
		assertEquals(0L, sessionSequence.getLongValue(unknownSessionIdValueProvider));
	}
	
	@Test
	public void allowsMultipleTranslationSequencesInSameSession_AssociatedWithTranslationMappingName() throws InvalidTypeCastException, MissingIdentifierException {
		session.setParameter(translationMappingName, SESSION_SEQUENCE);
		session.setParameter(otherTranslationMappingName, OTHER_SESSION_SEQUENCE);
		
		knownSessionIdvalueProvider.setValue(TranslatorConstants.COPY_PACKET_MAPPING_NAME, translationMappingName);
		knownSessionIdvalueProvider.setValue(TranslatorConstants.APPLICATION_ID, APP_ID);
		assertEquals(SESSION_SEQUENCE, sessionSequence.getLongValue(knownSessionIdvalueProvider));
		
		knownSessionIdvalueProvider.setValue(TranslatorConstants.COPY_PACKET_MAPPING_NAME, otherTranslationMappingName);
		knownSessionIdvalueProvider.setValue(TranslatorConstants.APPLICATION_ID, APP_ID);
		assertEquals(OTHER_SESSION_SEQUENCE, sessionSequence.getLongValue(knownSessionIdvalueProvider));
	}
	
	private class ValueProviderImpl extends MappedValueProvider {
		private String sessionId;

		public ValueProviderImpl(String sessionId) {
			super(new HashMap<String, Object>());
			this.sessionId = sessionId;
		}
		
		@Override
		public String getStringValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
				return sessionId;
		}

		@Override
		public long getLongValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			return (Long)getValue(identifier);
		}

		@Override
		public List<String> getStringValues(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			return null;
		}

		@Override
		public List<Long> getLongValues(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			return null;
		}
	}
}
