package com.elitecore.aaa.diameter.translators;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.translator.keyword.KeywordContextStub;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;

public class SessionSequenceKeywordTest {

	private static final String INCREMENTED_SESSION_SEQUENCE_VALUE = "2";
	private static final long SESSION_SEQUENCE_VALUE = 1l;
	private static final boolean IS_REQUEST = true;
	private static final String SESSION_ID_VALUE = "1";
	private static final long APP_ID = 1;
	private String policyName = "Dummy translation policy";
	private IStackContext dummyStackContext;
	private SessionSequenceKeyword sessionSequnceKeyWord;
	private TranslatorParams params;
	private ApplicationRequest fromRequest;
	private ApplicationRequest toRequest;
	private KeywordContextStub keywordContext = null;
	private ValueProvider valueProvider;

	@Mock private PeerSequenceKeyword peerSequenceKeyword;
	@Mock private Session session;

	@Rule public ExpectedException exception = ExpectedException.none(); 

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws InvalidAttributeIdException {
		MockitoAnnotations.initMocks(this);

		createSessionSequenceKeyword();

		createToAndFromRequest();

		valueProvider = spy(new DiameterPacketValueProvider(fromRequest.getDiameterRequest()));

		params = new TranslatorParamsImpl(fromRequest, toRequest);
		
		params.setParam(TranslatorConstants.APPLICATION_ID, APP_ID);

		when(dummyStackContext.getNextPeerSequence(SESSION_ID_VALUE)).thenReturn(0l);

	}

	@Test
	public void sessionWillBeFetchedBasedOnSessionIdentifierAVPIfNoArgumentIsSpecified() {
		keywordContext.getKeywordValue(TranslatorConstants.SEQSESS, params, IS_REQUEST, valueProvider);

		verify(valueProvider).getStringValue(DiameterAVPConstants.SESSION_ID);
	}

	@Test
	public void attributeIdConfiguredInArgumentIsConsideredAsSessionIdKey() {
		dummyStackContext.getOrCreateSession(SESSION_ID_VALUE, APP_ID).setParameter(policyName, SESSION_SEQUENCE_VALUE);
		reset(dummyStackContext);

		fromRequest.getDiameterRequest().getAVP(DiameterAVPConstants.CLASS).setStringValue(SESSION_ID_VALUE);

		assertThat(sessionSequnceKeyWord.getKeywordValue(params, "${SEQSESS}:0:25" , IS_REQUEST, valueProvider), is(equalTo(INCREMENTED_SESSION_SEQUENCE_VALUE)));

		verify(dummyStackContext).getOrCreateSession(SESSION_ID_VALUE, APP_ID);
	}

	@Test
	public void sequencingIsNotMaintainAndAlwaysReturnsZeroIfThereIsNoSessionForRequestedUser() {
		assertThat(keywordContext.getKeywordValue(TranslatorConstants.SEQSESS, params, IS_REQUEST, valueProvider),
				is(equalTo("0")));
	}

	@Test
	public void aNewSequenceForSessionIsStartedFromOneIfSessionExists() {
		dummyStackContext.getOrCreateSession(SESSION_ID_VALUE, APP_ID);

		assertThat(keywordContext.getKeywordValue(TranslatorConstants.SEQSESS, params, IS_REQUEST, valueProvider), is(equalTo("1")));

	}

	@Test
	public void incrementsExistingSequenceForSessionIfSessionExists() {
		dummyStackContext.getOrCreateSession(SESSION_ID_VALUE, APP_ID).setParameter(policyName, SESSION_SEQUENCE_VALUE);

		assertThat(keywordContext.getKeywordValue(TranslatorConstants.SEQSESS, params, IS_REQUEST, valueProvider), is(equalTo(INCREMENTED_SESSION_SEQUENCE_VALUE)));

	}

	@Test
	public void updatesSessionSequenceInSession() {
		Session session = spy(dummyStackContext.getOrCreateSession(SESSION_ID_VALUE, APP_ID));
		session.setParameter(policyName, SESSION_SEQUENCE_VALUE);

		when(dummyStackContext.getOrCreateSession(SESSION_ID_VALUE, APP_ID)).thenReturn(session);

		keywordContext.getKeywordValue(TranslatorConstants.SEQSESS, params, IS_REQUEST, valueProvider);

		assertThat((Long)session.getParameter(policyName), 
				is(equalTo(Long.parseLong(INCREMENTED_SESSION_SEQUENCE_VALUE))));

		verify(session).update(null);
	}

	@Test
	public void resultOfNestedKeywordConsideredAsSessionIdKey() {
		dummyStackContext.getOrCreateSession(SESSION_ID_VALUE, APP_ID).setParameter(policyName, SESSION_SEQUENCE_VALUE);

		params.setParam(TranslatorConstants.SOURCE_REQUEST, fromRequest.getDiameterRequest());
		assertThat(keywordContext.getKeywordValue("${SEQSESS}:${SRCREQ}:0:25", params, IS_REQUEST, valueProvider), is(equalTo(INCREMENTED_SESSION_SEQUENCE_VALUE)));
	}

	@Test
	public void sessionWillBeRetrivedFromDiameterStackContext() {
		dummyStackContext.getOrCreateSession(SESSION_ID_VALUE, APP_ID).setParameter(policyName, SESSION_SEQUENCE_VALUE);
		reset(dummyStackContext);

		keywordContext.getKeywordValue(TranslatorConstants.SEQSESS, params, IS_REQUEST, valueProvider);

		verify(dummyStackContext).getOrCreateSession(SESSION_ID_VALUE, APP_ID);
	}

	@Test
	public void translationSequenceWillBeNullIfStackContextIsNull() {
		keywordContext = new KeywordContextStub();
		keywordContext.addRequestKeyword( new SessionSequenceKeyword(TranslatorConstants.SEQSESS, policyName, keywordContext, DiameterAVPConstants.SESSION_ID));

		assertThat(
				keywordContext.getKeywordValue(TranslatorConstants.SEQSESS, params, IS_REQUEST, valueProvider),
				is(equalTo(null)));
	}
	
	@Test
	public void sessionSequenceWillBeNullIfApplicationIdIsNotSet() {
		params.setParam(TranslatorConstants.APPLICATION_ID, null);
		
		assertThat(keywordContext.getKeywordValue(TranslatorConstants.SEQSESS, params, IS_REQUEST, valueProvider),
				is(equalTo(null)));
	}

	// defect
	@Test
	public void throwsNullPointerExceptionStackContextIsRetrievedFromTranslatorParam() {
		keywordContext.addRequestKeyword( new SessionSequenceKeyword(TranslatorConstants.SEQSESS, policyName, keywordContext, DiameterAVPConstants.SESSION_ID));

		params.setParam(TranslatorConstants.STACK_CONTEXT, dummyStackContext);

		exception.expect(NullPointerException.class);

		keywordContext.getKeywordValue(TranslatorConstants.SEQSESS, params, true, valueProvider);
	}

	private void createToAndFromRequest() throws InvalidAttributeIdException {
		DiameterRequest request = new DiameterRequest();
		toRequest =  new ApplicationRequest(request);
		fromRequest = new ApplicationRequest(request);
		fromRequest.getDiameterRequest().addAvp(DiameterAVPConstants.SESSION_ID, SESSION_ID_VALUE);
		fromRequest.getDiameterRequest().addInfoAvp(DiameterAVPConstants.EC_APPLICATION_ID, APP_ID);
		fromRequest.getDiameterRequest().addAvp(DiameterAVPConstants.CLASS, SESSION_ID_VALUE);
	}

	private void createSessionSequenceKeyword() {
		keywordContext = spy(new KeywordContextStub());

		dummyStackContext = spy(new DummyStackContext(null));

		sessionSequnceKeyWord = new SessionSequenceKeyword(TranslatorConstants.SEQSESS, policyName, keywordContext,
				DiameterAVPConstants.SESSION_ID, dummyStackContext);

		peerSequenceKeyword = spy(new PeerSequenceKeyword(TranslatorConstants.SEQPEER, keywordContext, dummyStackContext));

		keywordContext.addRequestKeyword(sessionSequnceKeyWord).addRequestKeyword(peerSequenceKeyword);
		keywordContext.addRequestKeyword(new DiameterSrcDstKeyword(TranslatorConstants.SOURCE_REQUEST, keywordContext));
	}
}
