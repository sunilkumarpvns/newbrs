package com.elitecore.exprlib.parser.expression.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.StringFunctionExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

@RunWith(Parameterized.class)
public class FunctionMac2TgppTest{
	
	private class TestValueProviderImpl implements ValueProvider{

		@Override
		public String getStringValue(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			if(identifier == null){
				return null;
			}else{
				return identifier;
			}
		}

		@Override
		public long getLongValue(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			return 0;
		}

		@Override
		public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			return null;
		}

		@Override
		public List<Long> getLongValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			return null;
		}

		@Override
		public Object getValue(String key) {
			// TODO Auto-generated method stub
			return null;
		}	
	}
	
	private ValueProvider valueProvider;
	private String condition;
	private String result;
	private boolean isFailureExpected;

	public FunctionMac2TgppTest(String condition,String result,boolean isFailureExpected){
		this.condition = condition;
		this.result = result;
		this.isFailureExpected = isFailureExpected;
	}

	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][]{

				/**
				 * MAC-Address d4-87-d8-b7-a0-40 ---> 0233679921586240
				 * IMEI
				 */
				{"mac2tgpp(\"IMEI\",\"d487.d8b7.a040\",\"\",\"\")","170761309143684",false},
				
				{"mac2tgpp(\"IMEI\",\"d487.d8b7.a040\",\"\",\"178\")","0761309143684",false},
				{"mac2tgpp(\"IMEI\",\"d4:87:d8:b7:a0:40\",\"\",\"404##\")","4040761309143684",false},
				{"mac2tgpp(\"IMEI\",\"d4:87:d8:b7:a0:40\",\"\",\"###404\")","761309143684",false},
				{"mac2tgpp(\"IMEI\",\"d4:87:d8:b7:a0:40\",\"\",\"###76\")","1309143684",false},
				
				/**
				 * IMSI
				 */
				{"mac2tgpp(\"IMSI\",\"d487.d8b7.a040\",\"\",\"\")","233679921586240",false},
				
				{"mac2tgpp(\"IMSI\",\"d487d8b7a040\",\"\",\"234\")","3679921586240",false},
				{"mac2tgpp(\"IMSI\",\"d487.d8b7.a040\",\"\",\"###67\")","9921586240",false},
				{"mac2tgpp(\"IMSI\",\"d487.d8b7.a040\",\"\",\"404\")","233679921586240",false},
				{"mac2tgpp(\"IMSI\",\"d487.d8b7.a040\",\"\",\"404###\")","233679921586240",false},
				{"mac2tgpp(\"IMSI\",\"d4-87-d8-b7-a0-40\",\"\",\"9##\")","93679921586240",false},
				{"mac2tgpp(\"IMSI\",\"d4:87:d8:b7:a0:40\",\"\",\"0000##36\")","000079921586240",false},
				{"mac2tgpp(\"IMSI\",\"\",\"123123123\",\"\")","123123123",false},
				
				/**
				 * IMEISV
				 */
				{"mac2tgpp(\"IMEISV\",\"d487.d8b7.a040\",\"\",\"\")","0233679921586240",false},

				{"mac2tgpp(\"IMEISV\",\"d4-87-d8-b7-a0-40\",\"\",\"0234\")","3679921586240",false},
				{"mac2tgpp(\"IMEISV\",\"d4-87-d8-b7-a0-40\",\"\",\"9##\")","933679921586240",false},
				
				{"mac2tgpp(\"IMEISV\",\"\",\"123123123\",\"\")","123123123",false},
				{"mac2tgpp(\"IMEISV\",\"d487.d8b7.a040\",\"\",\"###304\")","679921586240",false},
				{"mac2tgpp(\"IMEISV\",\"d4-87-d8-b7-a0-40\",\"\",\"404###\")","4043679921586240",false},
				{"mac2tgpp(\"IMEISV\",\"d4-87-d8-b7-a0-40\",\"\",\"404##33#\")","40479921586240",false},
				
				{"mac2tgpp(\"IMEISV\",\"d4-87-d8-b7-a0-40\",\"\",\"404###\")","4043679921586240",false},
				{"mac2tgpp(\"IMEISV\",\"d4:87:d8:b7:a0:40\",\"\",\"0404##\")","040433679921586240",false},
				
				/**
				 * MSISDN
				 */
				{"mac2tgpp(\"MSISDN\",\"d487.d8b7.a040\",\"\",\"\")","233679921586240",false},
				{"mac2tgpp(\"MSISDN\",\"d487.d8b7.a040\",\"\",\"+404###\")","+404679921586240",false},
				{"mac2tgpp(\"MSISDN\",\"d4-87-d8-b7-a0-40\",\"\",\"0##37\")","0679921586240",false},
				{"mac2tgpp(\"MSISDN\",\"d4:87:d8:b7:a0:40\",\"\",\"+###6#04\")","+9921586240",false},	
				
				/**
				 * GENERAL
				 */
				{"mac2tgpp(\"GENERAL\",\"\",\"01020304050607\",\"404\")","01020304050607",false},
				{"mac2tgpp(\"GENERAL\",\"d487.d8b7.a040\",\"01020304050607\",\"234\")","3679921586240",false},
				{"mac2tgpp(\"GENERAL\",\"d487.d8b7.a040\",\"01020304050607\",\"0233###\")","0233679921586240",false},
				
				/**
				 * failure case
				 * Length mismatch for revision,due to prefix String.
				 */
				
				{"mac2tgpp(\"IMSI\",\"d4:87:d8:b7:a0:40\",\"\",\"9#3#6\")","979921586240",true},
				{"mac2tgpp(\"IMSI\",\"d4:87:d8:b7:a0:40\",\"\",\"09#9#9\")","093679921586240",true},
				{"mac2tgpp(\"IMEISV\",\"d4:87:d8:b7:a0:40\",\"\",\"###4##9\")","3921586240",true},
				{"mac2tgpp(\"IMEISV\",\"d487.d8b7.a040\",\"\",\"404#43#\")","4042679921586240",true},
				{"mac2tgpp(\"IMEISV\",\"d4:87:d8:b7:a0:40\",\"\",\"40###66#\")","4039921586240",true},
				
				
				/**
				 * TestCase With Configured Length Argument.
				 */

				// IMEISV    0233679921586240
				
				{"mac2tgpp(\"IMEISV\",\"d4:87:d8:b7:a0:40\",\"\",\"404###\",\"5\")","40436",true},
				{"mac2tgpp(\"IMEISV\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"0\")","0233679921586240",false},
				{"mac2tgpp(\"IMEISV\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"-5\")","86240",true},
				{"mac2tgpp(\"IMEISV\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"20\")","02336799215862400000",true},
				{"mac2tgpp(\"IMEISV\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"10\")","0233679921",true},
				{"mac2tgpp(\"IMEISV\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"-10\")","9921586240",true},
				
				// IMEISV with perfix string
				
				{"mac2tgpp(\"IMEISV\",\"d4:87:d8:b7:a0:40\",\"\",\"404###\",\"0\")","4043679921586240",false},
				{"mac2tgpp(\"IMEISV\",\"d4:87:d8:b7:a0:40\",\"\",\"404###\",\"-12\")","679921586240",true},
				{"mac2tgpp(\"IMEISV\",\"d4:87:d8:b7:a0:40\",\"\",\"404###\",\"12\")","404367992158",true},
				{"mac2tgpp(\"IMEISV\",\"d4:87:d8:b7:a0:40\",\"\",\"####67\",\"12\")","992158624000",true},
				
				
				// IMSI 233679921586240
				
				{"mac2tgpp(\"IMSI\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"5\")","23367",true},
				{"mac2tgpp(\"IMSI\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"0\")","233679921586240",true},
				{"mac2tgpp(\"IMSI\",\"\",\"123\",\"\",\"5\")","123",true},
				{"mac2tgpp(\"IMSI\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"-5\")","86240",true},
				{"mac2tgpp(\"IMSI\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"20\")","23367992158624000000",true},
				
				
				// 	IMSI with prefix
				
				{"mac2tgpp(\"IMSI\",\"d4:87:d8:b7:a0:40\",\"\",\"404###\",\"5\")","40467",true},
				{"mac2tgpp(\"IMSI\",\"d4:87:d8:b7:a0:40\",\"\",\"404###\",\"0\")","404679921586240",true},
				{"mac2tgpp(\"IMSI\",\"d4:87:d8:b7:a0:40\",\"\",\"404###\",\"-5\")","86240",true},
				{"mac2tgpp(\"IMSI\",\"d4:87:d8:b7:a0:40\",\"\",\"###679\",\"11\")","92158624000",true},
				{"mac2tgpp(\"IMSI\",\"d4:87:d8:b7:a0:40\",\"\",\"###679\",\"-11\")","00921586240",true},
				
				// MSISDN 233679921586240
				
				{"mac2tgpp(\"MSISDN\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"10\")","2336799215",true},
				{"mac2tgpp(\"MSISDN\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"0\")","233679921586240",true},
				{"mac2tgpp(\"MSISDN\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"-5\")","86240",true},
				{"mac2tgpp(\"MSISDN\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"20\")","23367992158624000000",true},
				{"mac2tgpp(\"MSISDN\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"-20\")","00000233679921586240",true},
				
				{"mac2tgpp(\"MSISDN\",\"d4:87:d8:b7:a0:40\",\"\",\"##########\",\"5\")","86240",true},
				
				// MSISDN with prefix
				
				{"mac2tgpp(\"MSISDN\",\"d4:87:d8:b7:a0:40\",\"\",\"404###\",\"5\")","40423",true},
				{"mac2tgpp(\"MSISDN\",\"d4:87:d8:b7:a0:40\",\"\",\"404###\",\"15\")","404679921586240",true},
				{"mac2tgpp(\"MSISDN\",\"d4:87:d8:b7:a0:40\",\"\",\"404####\",\"-5\")","86240",true},
				{"mac2tgpp(\"MSISDN\",\"d4:87:d8:b7:a0:40\",\"\",\"######\",\"8\")","92158624",true},
				{"mac2tgpp(\"MSISDN\",\"d4:87:d8:b7:a0:40\",\"\",\"######\",\"12\")","921586240000",true},
				{"mac2tgpp(\"MSISDN\",\"d4:87:d8:b7:a0:40\",\"\",\"######\",\"-12\")","009921586240",true},
				
				// General 233679921586240
				
				{"mac2tgpp(\"GENERAL\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"5\")","23367",true},
				{"mac2tgpp(\"GENERAL\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"0\")","233679921586240",true},
				{"mac2tgpp(\"GENERAL\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"-5\")","86240",true},
				{"mac2tgpp(\"GENERAL\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"-20\")","00000233679921586240",true},

				{"mac2tgpp(\"GENERAL\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"5\")","23367",true},
				{"mac2tgpp(\"GENERAL\",\"d4:87:d8:b7:a0:40\",\"\",\"\",\"0\")","233679921586240",true},
				{"mac2tgpp(\"GENERAL\",\"d4:87:d8:b7:a0:40\",\"\",\"233#####\",\"-5\")","86240",true},
				{"mac2tgpp(\"GENERAL\",\"d4:87:d8:b7:a0:40\",\"\",\"###6799\",\"7\")","2158624",true},
				{"mac2tgpp(\"GENERAL\",\"d4:87:d8:b7:a0:40\",\"\",\"###6799\",\"-9\")","021586240",true},
				{"mac2tgpp(\"GENERAL\",\"d4:87:d8:b7:a0:40\",\"\",\"###6799\",\"8\")","21586240",true},

				// IMEI 170761309143684 [60-56=4 [check digit] i.e. 56 is total sum as per logic]
				
				{"mac2tgpp(\"IMEI\",\"d487.d8b7.a040\",\"\",\"\",\"10\")","1707613099",true},
				{"mac2tgpp(\"IMEI\",\"d487.d8b7.a040\",\"\",\"\",\"-10\")","1309143681",true},
				{"mac2tgpp(\"IMEI\",\"d487.d8b7.a040\",\"\",\"\",\"-16\")","0170761309143682",true},
				{"mac2tgpp(\"IMEI\",\"d487.d8b7.a040\",\"\",\"\",\"0\")","170761309143684",true},
				{"mac2tgpp(\"IMEI\",\"d487.d8b7.a040\",\"\",\"404###\",\"10\")","4047613097",true},
				
				// IMEI With Prefix Value
				
				{"mac2tgpp(\"IMEI\",\"d487.d8b7.a040\",\"\",\"\",\"5\")","17079",true},
				{"mac2tgpp(\"IMEI\",\"d487.d8b7.a040\",\"\",\"178###76\",\"-5\")","43687",true},
				{"mac2tgpp(\"IMEI\",\"d487.d8b7.a040\",\"\",\"178###76\",\"5\")","17814",true},
				{"mac2tgpp(\"IMEI\",\"d4:87:d8:b7:a0:40\",\"\",\"###7613\",\"9\")","091436840",true},
				{"mac2tgpp(\"IMEI\",\"d4:87:d8:b7:a0:40\",\"\",\"###7613\",\"-9\")","009143686",true},
				
				
				// MAC Address with Octet String
				
				{"mac2tgpp(\"IMEISV\",\"0x1589746236\",\"\",\"\",\"0\")","0000092500419126",true},
				{"mac2tgpp(\"IMSI\",\"0x1589746236\",\"\",\"\",\"0\")","000092500419126",true},
				{"mac2tgpp(\"IMEI\",\"0x1589746236\",\"\",\"\",\"10\")","6673061530",true}, // IMEI 667306153500009
				{"mac2tgpp(\"IMEI\",\"0x1589746236\",\"\",\"233###\",\"10\")","2333061535",true},
				{"mac2tgpp(\"MSISDN\",\"0x1589746236\",\"\",\"\",\"0\")","92500419126",true},
				{"mac2tgpp(\"GENERAL\",\"1589746236\",\"\",\"\")","92500419126",true},
		});
	}

	@Before
	public void setUp(){
		valueProvider = new TestValueProviderImpl();
	}

	@Test
	public void mac2TgppConversion(){
		StringFunctionExpression macTOgpp ;
		String IMEISV = null;
		try {
			macTOgpp = (StringFunctionExpression) Compiler.getDefaultCompiler().parseExpression(condition);
			IMEISV = macTOgpp.getStringValue(valueProvider);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("failed to generate IMEISV. reason :"+e.getMessage());
		}
		if(result.length() != IMEISV.length()){
			if(!isFailureExpected)
				Assert.fail("Revision wise length is not generated due to replacements string(Prefix string)");
		}
	}
}
