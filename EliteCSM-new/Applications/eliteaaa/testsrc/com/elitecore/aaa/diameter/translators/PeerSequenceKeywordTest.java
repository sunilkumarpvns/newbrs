package com.elitecore.aaa.diameter.translators;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.translator.keyword.KeywordContextStub;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;

public class PeerSequenceKeywordTest {

	private static final long SEQUENCE = 1l;
	private static final boolean IS_REQUEST = true;
	private static final String ORIGIN_HOST_VALUE = "any";
	private IStackContext dummyStackContext;
	private PeerSequenceKeyword peerSequnceKeyWord;
	private TranslatorParams params;
	private ApplicationRequest fromRequest;
	private ApplicationRequest toRequest;
	private DiameterPacketValueProvider valueProvider;
	private KeywordContextStub keywordContext = null;

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws InvalidAttributeIdException {
		MockitoAnnotations.initMocks(this);
		
		createToFromRequest();
		
		dummyStackContext = spy(new DummyStackContext(null));
		keywordContext = new KeywordContextStub();
		peerSequnceKeyWord = new PeerSequenceKeyword(TranslatorConstants.SEQPEER, keywordContext, dummyStackContext);
		keywordContext.addRequestKeyword(peerSequnceKeyWord);
		valueProvider = new DiameterPacketValueProvider(fromRequest.getDiameterRequest());
		params = spy( new TranslatorParamsImpl(fromRequest, toRequest));

		when(dummyStackContext.getNextPeerSequence(ORIGIN_HOST_VALUE)).thenReturn(SEQUENCE);
	}

	@Test
	public void returnsNextTranslationSequenceForRequestedPeer() {
		
		assertThat(keywordContext.getKeywordValue(TranslatorConstants.SEQPEER, params, IS_REQUEST, valueProvider),
				is(equalTo(String.valueOf(SEQUENCE))));

		verify(dummyStackContext).getNextPeerSequence(ORIGIN_HOST_VALUE);
	}

	@Test
	public void getsDiameterStackContextFromTranslationParamIfContextInjectedInConstructorIsNull() {
		
		params.setParam(TranslatorConstants.STACK_CONTEXT, dummyStackContext);
		
		peerSequnceKeyWord = new PeerSequenceKeyword(TranslatorConstants.SEQPEER, keywordContext);
		
		keywordContext.addRequestKeyword(peerSequnceKeyWord);
		
		keywordContext.getKeywordValue(TranslatorConstants.SEQPEER, params, IS_REQUEST, valueProvider);
		
		verify(params).getParam(TranslatorConstants.STACK_CONTEXT);

	}
	
	@Test
	public void returnsNullfStackContextIsNull() {

		peerSequnceKeyWord = new PeerSequenceKeyword(TranslatorConstants.SEQPEER, keywordContext);
		
		keywordContext.addRequestKeyword(peerSequnceKeyWord);

		assertThat(keywordContext.getKeywordValue(TranslatorConstants.SEQPEER, params, IS_REQUEST, valueProvider), is(nullValue()));

	}

	private void createToFromRequest() throws InvalidAttributeIdException {
		DiameterRequest request = new DiameterRequest();
		toRequest = new ApplicationRequest(request);
		fromRequest = new ApplicationRequest(request);
		fromRequest.getDiameterRequest().getAVP(DiameterAVPConstants.ORIGIN_HOST).setStringValue(ORIGIN_HOST_VALUE);
	}

}
