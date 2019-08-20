package com.elitecore.netvertex.core.conf.impl.base;

import static junit.framework.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;

@RunWith(JUnitParamsRunner.class)
public class BaseConfigurationImplTest{

	private static final String BLANK_SPACE = "   ";
	private static final String BLANK_STRING_PARAMETER = "Blank String Parameter";
	private static final String NULL_STRING_PARAMETER = "Null String Parameter";
	private static final String BLANK_SPACE_PARAMETER = "Blank Space Parameter";
	private static final String INVALID_STRING_PARAMETER = "Invalid String Parameter";
	private static final String MIN_VALUE_PARAMETER = "Min Value Parameter";
	private static final String MAX_VALUE_PARAMETER = "Max Value Parameter";
	private static final String BOOL_TRUE_PARAMETER = "True Parameter";
	private static final String BOOL_FALSE_PARAMETER = "False Parameter";
	private static final String LESSER_THAN_MIN_PARAMETER = "LesserThenMinValue Parameter";
	private static final String HIGHER_THAN_MAX_PARAMETER = "HigherThenMaxValue Parameter";
	private static final String VALUE_BETWEEN_MIN_MAX = "Value Between MinValue And MaxValue";
	private static final String INVALID_VALUE = "Invalid Value";
	private static final String OUT_OF_RANGE_POSITIVE_INT = "Out of Range Positive Int";
	private static final String OUT_OF_RANGE_NEGATIVE_INT = "Out of Range Negative Int";
	private static final String OUT_OF_RANGE_POSITIVE_LONG = "Out of Range Positive Long";
	private static final String OUT_OF_RANGE_NEGATIVE_LONG = "Out of Range Negative Long";
	
	private DummyNetvertexServerContextImpl serverContext = new DummyNetvertexServerContextImpl();
	private DummyBaseConfigurationImpl dummyBaseConfigurationImpl = new DummyBaseConfigurationImpl(serverContext);

	private static class DummyBaseConfigurationImpl extends BaseConfigurationImpl{

		public DummyBaseConfigurationImpl(ServerContext serverContext) {
			super(serverContext);
		}

		@Override
		public void readConfiguration() throws LoadConfigurationException {	}
		
	}
	
	//testing Invalid Values
	public Object[][] dataProvider_For_Test_StringtoBoolean_Taking_DefaultValue_When_OriginalString_is_Not_Configured() throws Exception{
    	return new Object[][]{ 
    		{
    			BLANK_STRING_PARAMETER,""
    		},
    		{
    			NULL_STRING_PARAMETER,null
    		},
    		{
    			BLANK_SPACE_PARAMETER,BLANK_SPACE
    		},
    	};
    }
 
 @Test
 @Parameters(method = "dataProvider_For_Test_StringtoBoolean_Taking_DefaultValue_When_OriginalString_is_Not_Configured")
 public void test_StringtoBoolean_Taking_DefaultValue_When_OriginalString_is_Not_Configured(
		 String parameterName, String originalString) throws Exception{
	 boolean actualStringToBooleanValue ; 
	 actualStringToBooleanValue  = dummyBaseConfigurationImpl.stringToBoolean(parameterName, originalString, true);
	 assertEquals(parameterName, true, actualStringToBooleanValue);
 }
 
 @Test
 public void test_StringtoBoolean_Taking_False_When_OriginalString_is_Invalid() throws Exception{
	 boolean actualStringToBooleanValue ; 
	 actualStringToBooleanValue  = dummyBaseConfigurationImpl.stringToBoolean(INVALID_STRING_PARAMETER, INVALID_VALUE, true);
	 assertEquals(INVALID_STRING_PARAMETER, false, actualStringToBooleanValue);
 }
 
public Object[][] dataProvider_For_Test_AllMethod_Taking_DefaultValue_When_OriginalString_is_Not_Configured_Or_Invalid() throws Exception{
	    	return new Object[][]{ 
	    		{
	    			BLANK_STRING_PARAMETER,""
	    		},
	    		{
	    			NULL_STRING_PARAMETER,null
	    		},
	    		{
	    			BLANK_SPACE_PARAMETER,BLANK_SPACE
	    		},
	    		{
	    			INVALID_STRING_PARAMETER,INVALID_VALUE
	    		}
	    	};
	    }
	
	 @Test
	 @Parameters(method = "dataProvider_For_Test_AllMethod_Taking_DefaultValue_When_OriginalString_is_Not_Configured_Or_Invalid")
	 public void test_StringtoInteger_3params_Taking_DefaultValue_When_OriginalString_is_Not_Configured_Or_Invalid(
			 String parameterName, String originalString) throws Exception{
		 int actualStringToIntegerValue ; 
		 actualStringToIntegerValue = dummyBaseConfigurationImpl.stringToInteger(parameterName, originalString, 1);
		 assertEquals(parameterName, 1, actualStringToIntegerValue);
	 }
		 
	 @Test
	 @Parameters(method = "dataProvider_For_Test_AllMethod_Taking_DefaultValue_When_OriginalString_is_Not_Configured_Or_Invalid")
	 public void test_StringtoInteger_5params_Taking_DefaultValue_When_OriginalString_is_Not_Configured_Or_Invalid(
			 String parameterName, String originalString) throws Exception{
		 int actualStringToIntegerValue ; 
		 actualStringToIntegerValue = dummyBaseConfigurationImpl.stringToInteger(parameterName, originalString, 1,1,10000);
		 assertEquals(parameterName, 1, actualStringToIntegerValue);
	 }
	
	 @Test
	 @Parameters(method = "dataProvider_For_Test_AllMethod_Taking_DefaultValue_When_OriginalString_is_Not_Configured_Or_Invalid")
	 public void test_StringtoLong_Taking_DefaultValue_When_OriginalString_is_Not_Configured_Or_Invalid(
			 String parameterName, String originalString) throws Exception{
		 long actualStringToLongValue ; 
		 actualStringToLongValue = dummyBaseConfigurationImpl.stringToLong(parameterName, originalString, 1l);
		 assertEquals(parameterName, 1l, actualStringToLongValue);
		 
	 }

	 //testing Valid Values
	 public Object[][] dataProvider_For_Test_StringtoBoolean_Returning_Valid_ResultValue_When_OriginalString_is_Valid() throws Exception{
	    	return new Object[][]{ 
	    		{
	    			BOOL_FALSE_PARAMETER,"false"
	    		},
	    		{
	    			BOOL_FALSE_PARAMETER,"fAlse"
	    		},
	    		{
	    			BOOL_FALSE_PARAMETER,"faLSe"
	    		},
	    		{
	    			BOOL_TRUE_PARAMETER,"true"
	    		},
	    		{
	    			BOOL_TRUE_PARAMETER,"True"
	    		},
	    		{
	    			BOOL_TRUE_PARAMETER,"truE"
	    		}
	    		
	    	};
	    }
	 
	@Test
	@Parameters(method = "dataProvider_For_Test_StringtoBoolean_Returning_Valid_ResultValue_When_OriginalString_is_Valid")
	public void test_StringtoBoolean_Returning_Valid_ResultValue_When_OriginalString_is_Valid(
			String parameterName,String originalString){
		boolean actualStringToBooleanValue,expectedStringToBooleanValue;
		actualStringToBooleanValue = dummyBaseConfigurationImpl.stringToBoolean(parameterName, originalString, false);
		expectedStringToBooleanValue = Boolean.parseBoolean(originalString);
		assertEquals(parameterName, expectedStringToBooleanValue, actualStringToBooleanValue);
	}
	
	 public Object[][] dataProvider_For_Test_StringtoInteger__Returning_Valid_ResultValue_When_OriginalString_is_Valid() throws Exception{
	    	return new Object[][]{ 
	    		{
	    			MAX_VALUE_PARAMETER,"2147483647"
	    		},
	    		{
	    			MAX_VALUE_PARAMETER,"2147483646"
	    		},
	    		{
	    			VALUE_BETWEEN_MIN_MAX,"100"
	    		},
	    		{
	    			MIN_VALUE_PARAMETER,"-2147483647"
	    		},
	    		{
	    			MIN_VALUE_PARAMETER,"-2147483648"
	    		}
	    	};
	    }
	 
	@Test
	@Parameters(method = "dataProvider_For_Test_StringtoInteger__Returning_Valid_ResultValue_When_OriginalString_is_Valid")
	public void test_StringtoInteger_3params_Returning_Valid_ResultValue_When_OriginalString_is_Valid(
			String parameterName,String originalString){
		int actualStringToIntegerValue,expectedStringToIntegerValue;
		actualStringToIntegerValue = dummyBaseConfigurationImpl.stringToInteger(parameterName, originalString, 1);
		expectedStringToIntegerValue = Integer.parseInt(originalString);
		assertEquals(parameterName, expectedStringToIntegerValue, actualStringToIntegerValue);
	}
	
	@Test
	@Parameters(method = "dataProvider_For_Test_StringtoInteger__Returning_Valid_ResultValue_When_OriginalString_is_Valid")
	public void test_StringtoInteger_5params_Returning_Valid_ResultValue_When_OriginalString_is_Valid(
			String parameterName, String originalString) {
		int actualStringToIntegerValue, expectedStringToIntegerValue;
		actualStringToIntegerValue = dummyBaseConfigurationImpl.stringToInteger(parameterName, originalString, 1,
						Integer.MIN_VALUE, Integer.MAX_VALUE);
		expectedStringToIntegerValue = Integer.parseInt(originalString);
		assertEquals(parameterName, expectedStringToIntegerValue, actualStringToIntegerValue);
	}
	 
	 public Object[][] dataProvider_For_Test_StringtoLong__Returning_Valid_ResultValue_When_OriginalString_is_Valid() throws Exception{
	    	return new Object[][]{ 
	    		{
	    			MAX_VALUE_PARAMETER,"9223372036854775807"
	    		},
	    		{
	    			MAX_VALUE_PARAMETER,"9223372036854775806"
	    		},
	    		{
	    			VALUE_BETWEEN_MIN_MAX,"100"
	    		},
	    		{
	    			MIN_VALUE_PARAMETER,"-9223372036854775807"
	    		},
	    		{
	    			MIN_VALUE_PARAMETER,"-9223372036854775808"
	    		}
	    	};
	    }
	 
	@Test
	@Parameters(method = "dataProvider_For_Test_StringtoLong__Returning_Valid_ResultValue_When_OriginalString_is_Valid")
	public void test_StringtoLong__Returning_Valid_ResultValue_When_OriginalString_is_Valid(
			String parameterName, String originalString) {
		long actualStringToLongValue, expectedStringToLongValue;
		actualStringToLongValue = dummyBaseConfigurationImpl.stringToLong(
				parameterName, originalString, 1l);
		expectedStringToLongValue = Long.parseLong(originalString);
		assertEquals(parameterName, expectedStringToLongValue, actualStringToLongValue);
	}
	
	//testing Out of Range VAlues
	public Object[][] dataProvider_For_Test_StringtoInteger_Taking_DefaultValue_When_Value_Out_of_Range() throws Exception{
    	return new Object[][]{ 
    		{
    			OUT_OF_RANGE_POSITIVE_INT,"2147483648"
    		},
    		{
    			OUT_OF_RANGE_NEGATIVE_INT,"-2147483649"
    		},
    		
    	};
    }
	
	@Test
	@Parameters(method = "dataProvider_For_Test_StringtoInteger_Taking_DefaultValue_When_Value_Out_of_Range")
	public void test_StringtoInteger_3params_Taking_DefaultValue_When_Value_Out_of_Range(
			String parameterName, String originalString){
		 int actualStringToIntegerValue ; 
		 actualStringToIntegerValue = dummyBaseConfigurationImpl.stringToInteger(parameterName, originalString, 1);
		 assertEquals(parameterName, 1, actualStringToIntegerValue);
	}
	
	@Test
	@Parameters(method = "dataProvider_For_Test_StringtoInteger_Taking_DefaultValue_When_Value_Out_of_Range")
	public void test_StringtoInteger_5params_Taking_DefaultValue_When_Value_Out_of_Range(
			String parameterName, String originalString){
		 int actualStringToIntegerValue ; 
		 actualStringToIntegerValue = dummyBaseConfigurationImpl.stringToInteger(parameterName, originalString, 1, 1, 10000);
		 assertEquals(parameterName, 1, actualStringToIntegerValue);
	}
	
	public Object[][] dataProvider_For_Test_StringtoLong_Taking_DefaultValue_When_Value_Out_of_Range() throws Exception{
    	return new Object[][]{ 
    		{
    			OUT_OF_RANGE_POSITIVE_LONG,"9223372036854775808"
    		},
    		{
    			OUT_OF_RANGE_NEGATIVE_LONG,"-9223372036854775809"
    		},
    		
    	};
    }
	
	@Test
	@Parameters(method = "dataProvider_For_Test_StringtoLong_Taking_DefaultValue_When_Value_Out_of_Range")
	public void test_StringtoLong_Taking_DefaultValue_When_Value_Out_of_Range(
			String parameterName, String originalString){
		 long actualStringToLongValue ; 
		 actualStringToLongValue = dummyBaseConfigurationImpl.stringToLong(parameterName, originalString, 1l);
		 assertEquals(parameterName, 1l, actualStringToLongValue);
	}
	
	public Object[][] dataProvider_For_Test_StringtoInteger_5params_Taking_DefaultValue_When_ResultValue_is_Lesser_than_MinValue() throws Exception{
    	return new Object[][]{ 
    		{
    			LESSER_THAN_MIN_PARAMETER+1,"0"
    		},
    		{
    			LESSER_THAN_MIN_PARAMETER+2,"1"
    		},
    		{
    			LESSER_THAN_MIN_PARAMETER+3,"4"
    		},
    	};
    }

	@Test
	@Parameters(method = "dataProvider_For_Test_StringtoInteger_5params_Taking_DefaultValue_When_ResultValue_is_Lesser_than_MinValue")
	public void test_StringtoInteger_5params_Taking_DefaultValue_When_ResultValue_is_Lesser_than_MinValue(
			String parameterName, String originalString){
		
		 int actualStringToIntegerValue ; 
		 actualStringToIntegerValue = dummyBaseConfigurationImpl.stringToInteger(parameterName, originalString, 10, 5, 10000);
		 assertEquals( parameterName, 10, actualStringToIntegerValue);
		
	}
	
	public Object[][] dataProvider_For_Test_StringtoInteger_5params_Taking_DefaultValue_When_ResultValue_is_Higher_than_MaxValue() throws Exception{
    	return new Object[][]{ 
    		{
    			HIGHER_THAN_MAX_PARAMETER+1,"10001"
    		},
    		{
    			HIGHER_THAN_MAX_PARAMETER+2,"11000"
    		},
    		{
    			HIGHER_THAN_MAX_PARAMETER+3,"12000"
    		},
    	};
    }

	@Test
	@Parameters(method = "dataProvider_For_Test_StringtoInteger_5params_Taking_DefaultValue_When_ResultValue_is_Higher_than_MaxValue")
	public void test_StringtoInteger_5params_Taking_DefaultValue_When_ResultValue_is_Higher_than_MaxValue(
			String parameterName, String originalString){
		
		 int actualStringToIntegerValue ; 
		 actualStringToIntegerValue = dummyBaseConfigurationImpl.stringToInteger(parameterName, originalString, 10, 5, 10000);
		 assertEquals(parameterName, 10, actualStringToIntegerValue);
		
	}
}
