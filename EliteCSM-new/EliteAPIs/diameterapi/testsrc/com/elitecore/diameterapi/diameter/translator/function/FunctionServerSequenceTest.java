package com.elitecore.diameterapi.diameter.translator.function;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionServerSequenceTest {
public static final long SERVER_SEQUENCE = 30;
private FunctionServerSequence serverSequence ;
	
	private ValueProvider valueProvider = new ValueProviderImpl();
	private @Mock IStackContext stackContext;
	
	@Before
	public void setUp(){
		
		MockitoAnnotations.initMocks(this);
		serverSequence = (FunctionServerSequence)new FunctionServerSequence(stackContext).clone();
		Mockito.when(stackContext.getNextServerSequence()).thenReturn(SERVER_SEQUENCE);
	}
	
	
	@Test
	public void test_function_should_call_getLongValue_function() throws InvalidTypeCastException, MissingIdentifierException, IllegalArgumentException{

		serverSequence.getLongValue(valueProvider);
		Assert.assertEquals(30, serverSequence.getLongValue(valueProvider));
	}
	
	@Test
	public void test_function_should_call_getStringValue_function() throws InvalidTypeCastException, MissingIdentifierException, IllegalArgumentException{

		serverSequence.getLongValue(valueProvider);
		Assert.assertEquals("30", serverSequence.getStringValue(valueProvider));
	}
	

	private static class ValueProviderImpl implements ValueProvider{

		@Override
		public String getStringValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
				return String.valueOf(getLongValue(identifier));
		}

		@Override
		public long getLongValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			return SERVER_SEQUENCE;
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
