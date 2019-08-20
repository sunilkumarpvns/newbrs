package com.elitecore.diameterapi.diameter.translator.function;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionPeerSequenceTest {
	private static final long PEER_SEQUENCE = 10;
	private FunctionPeerSequence peerSequence ;
	
	private ValueProvider valueProvider = new ValueProviderImpl();
	private static final String ORIGIN_HOST = "netvertex.elitecore.com";
	private @Mock IStackContext stackContext;

	@Before
	public void setUp(){
		
		MockitoAnnotations.initMocks(this);
		peerSequence = (FunctionPeerSequence)new FunctionPeerSequence(stackContext).clone();
		Mockito.when(stackContext.getNextPeerSequence(ORIGIN_HOST)).thenReturn(PEER_SEQUENCE);
	}
	
	@Test
	public void test_function_should_call_getLongValue_function() throws InvalidTypeCastException, MissingIdentifierException{

		peerSequence.getLongValue(valueProvider);
		Assert.assertEquals(10, peerSequence.getLongValue(valueProvider));
	}
	
	@Test
	public void test_function_should_call_getStringValue_function() throws InvalidTypeCastException, MissingIdentifierException{

		peerSequence.getLongValue(valueProvider);
		Assert.assertEquals("10", peerSequence.getStringValue(valueProvider));
	}
	
	
	private static class ValueProviderImpl implements ValueProvider{

		@Override
		public String getStringValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
				return ORIGIN_HOST;
			
			
		}

		@Override
		public long getLongValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			return 0;
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

		@Override
		public Object getValue(String key) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
