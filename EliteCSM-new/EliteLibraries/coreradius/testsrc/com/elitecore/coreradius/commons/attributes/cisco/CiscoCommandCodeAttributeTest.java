package com.elitecore.coreradius.commons.attributes.cisco;

import java.io.StringReader;
import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Test;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.cisco.CiscoCommandCodeAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class CiscoCommandCodeAttributeTest extends TestCase{
	private String ciscoDic = "<attribute-list vendorid=\"9\" vendor-name=\"Cisco\" avpair-separator=\";\"><attribute id=\"252\" name=\"Cisco-Command-Code\" type=\"CiscoCommandCode\"/></attribute-list>";
	private static final String CODE_KEY = "code";
	private static final String VALUE_KEY = "value";
	
	public CiscoCommandCodeAttributeTest(String name) {
		super(name);
		StringReader reader = new StringReader(ciscoDic);
		try {
			Dictionary.getInstance().load(reader);
		} catch (Exception e) {
			//will not occur
			fail();
		}
	}
	
	@Test
	public void testSetAndGetStringValueTest(){
		//normal scenario case
		IRadiusAttribute ciscoCommandCode = Dictionary.getInstance().getAttribute(RadiusConstants.CISCO_VENDOR_ID, RadiusAttributeConstants.CISCO_COMMAND_CODE);
		ciscoCommandCode.setStringValue("code=1;value=IP_UC1");
		assertEquals(true,ciscoCommandCode.getStringValue().equals("code=1;value=IP_UC1"));


		//when the code key contains spaces within, this is a valid value
		ciscoCommandCode.setStringValue(" code =    4;value=INTERNET_SERVICE_UC1");
		//the spaces in the code key will be trimmed
		assertEquals(true, ciscoCommandCode.getStringValue().equals("code=4;value=INTERNET_SERVICE_UC1"));
	
		//when the value key contains spaces 
		ciscoCommandCode.setStringValue("code=4;   value   =INTERNET_SERVICE_UC1");
		assertEquals(true, ciscoCommandCode.getStringValue().equals("code=4;value=INTERNET_SERVICE_UC1"));
		
		//when the value portion of the string contains spaces
		ciscoCommandCode.setStringValue("code=4;value= INTERNET_SERVICE_UC1 &");
		//in this case the spaces will not be trimmed as they have some specific meaning
		assertEquals(true, ciscoCommandCode.getStringValue().equals("code=4;value= INTERNET_SERVICE_UC1 &"));
		
		//the keys are reordered
		ciscoCommandCode.setStringValue("value= INTERNET_SERVICE_UC1 &;code=4");
		//in this case the spaces will not be trimmed as they have some specific meaning
		assertEquals(true, ciscoCommandCode.getStringValue().equals("code=4;value= INTERNET_SERVICE_UC1 &"));
		
		//value key is not present as it is optional
		ciscoCommandCode.setStringValue("code=4");
		//in this case the spaces will not be trimmed as they have some specific meaning
		//TODO check the code for this test case and ask what should be appropriate value
		assertEquals(true, ciscoCommandCode.getStringValue().equals("code=4;value="));
		
		//Negative test cases
		try{
			//invalid key for code
			ciscoCommandCode.setStringValue("code1 = 1; value = SOME_VALUE");
			fail();
		}catch(IllegalArgumentException ex){
			//this is valid
		}
		
		
		try{
			//invalid key for value
			ciscoCommandCode.setStringValue("code = 1; valu1e = SOME_VALUE");
			fail();
		}catch(IllegalArgumentException ex){
			//this is valid
		}
		
		try{
			//more than 2 tokens
			ciscoCommandCode.setStringValue("code1 = 1; valu1e = SOME_VALUE;code=123");
			fail();
		}catch(IllegalArgumentException ex){
			//this is valid
		}
		
		try{
			//more than 2 tokens
			ciscoCommandCode.setStringValue("code1 = 1;;code=123");
			fail();
		}catch(IllegalArgumentException ex){
			//this is valid
		}
		
		try{
			//no tokens
			ciscoCommandCode.setStringValue("");
			fail();
		}catch(IllegalArgumentException ex){
			//this is valid
		}
		
		try{
			//only key but blank value
			ciscoCommandCode.setStringValue("code =;value=SOME_VALUE");
			fail();
		}catch(IllegalArgumentException ex){
			//this is valid
		}
		
		try{
			//invalid value for code key
			ciscoCommandCode.setStringValue("code =asd;value=SOME_VALUE");
			fail();
		}catch(IllegalArgumentException ex){
			//this is valid
		}
		
		try{
			//only key but blank value
			ciscoCommandCode.setStringValue("code =;value=SOME_VALUE");
			fail();
		}catch(IllegalArgumentException ex){
			//this is valid
		}
		
		
		try{
			//only key but blank value
			ciscoCommandCode.setStringValue("code =1;value=");
			fail();
		}catch(IllegalArgumentException ex){
			//this is valid
		}
	}
	
	@Test
	public void testSetAndGetValueBytes(){
		CiscoCommandCodeAttribute ciscoCommandCode = new CiscoCommandCodeAttribute();
		byte[] valueBytes;
		
		
		//most general case
		valueBytes = new byte[]{04,0x49,0x4E,0x54,0x45,0x52,0x4E,0x45,0x54,0x5F,0x53,0x45,0x52,0x56,0x49,0x43,0x45,0x5F,0x55,0x43,0x31};
		ciscoCommandCode.setValueBytes(valueBytes);
		assertEquals(true, Arrays.equals(valueBytes, ciscoCommandCode.getValueBytes()));
		
		//where the value portion is a 1 in ASCII
		valueBytes = new byte[]{04,0x31};
		ciscoCommandCode.setValueBytes(valueBytes);
		assertEquals(true, Arrays.equals(valueBytes, ciscoCommandCode.getValueBytes()));
		
		//where the value portion is not present
		valueBytes = new byte[]{04};
		ciscoCommandCode.setValueBytes(valueBytes);
		assertEquals(true, Arrays.equals(valueBytes, ciscoCommandCode.getValueBytes()));
		
		//where the value portion contains a ' '
		valueBytes = new byte[]{04,0x20,0x26};
		ciscoCommandCode.setValueBytes(valueBytes);
		assertEquals(true, Arrays.equals(valueBytes, ciscoCommandCode.getValueBytes()));
	}
	
	@Test
	public void testGetKeyValue(){
		//normal scenario case
		IRadiusAttribute ciscoCommandCode = Dictionary.getInstance().getAttribute(RadiusConstants.CISCO_VENDOR_ID, RadiusAttributeConstants.CISCO_COMMAND_CODE);
		ciscoCommandCode.setStringValue("code=1;value=IP_UC1");
		assertEquals(true,ciscoCommandCode.getKeyValue(CODE_KEY).equals("1"));
		assertEquals(true,ciscoCommandCode.getKeyValue(VALUE_KEY).equals("IP_UC1"));


		//when the code key contains spaces within, this is a valid value
		ciscoCommandCode.setStringValue(" code =    4;value=INTERNET_SERVICE_UC1");
		//the spaces in the code key will be trimmed
		assertEquals(true, ciscoCommandCode.getKeyValue(CODE_KEY).equals("4"));
		assertEquals(true, ciscoCommandCode.getKeyValue(VALUE_KEY).equals("INTERNET_SERVICE_UC1"));
	
		//when the value key contains spaces 
		ciscoCommandCode.setStringValue("code=4;   value   =INTERNET_SERVICE_UC1");
		assertEquals(true, ciscoCommandCode.getKeyValue(CODE_KEY).equals("4"));
		assertEquals(true, ciscoCommandCode.getKeyValue(VALUE_KEY).equals("INTERNET_SERVICE_UC1"));
		
		//when the value portion of the string contains spaces
		ciscoCommandCode.setStringValue("code=4;value= INTERNET_SERVICE_UC1 &");
		//in this case the spaces will not be trimmed as they have some specific meaning
		assertEquals(true, ciscoCommandCode.getKeyValue(CODE_KEY).equals("4"));
		assertEquals(true, ciscoCommandCode.getKeyValue(VALUE_KEY).equals(" INTERNET_SERVICE_UC1 &"));
		
		//the keys are reordered
		ciscoCommandCode.setStringValue("value= INTERNET_SERVICE_UC1 &;code=4");
		//in this case the spaces will not be trimmed as they have some specific meaning
		assertEquals(true, ciscoCommandCode.getKeyValue(CODE_KEY).equals("4"));
		assertEquals(true, ciscoCommandCode.getKeyValue(VALUE_KEY).equals(" INTERNET_SERVICE_UC1 &"));
		
		//value key is not present as it is optional
		ciscoCommandCode.setStringValue("code=4");
		//in this case the spaces will not be trimmed as they have some specific meaning
		//TODO check the code for this test case and ask what should be appropriate value
		assertEquals(true, ciscoCommandCode.getKeyValue(CODE_KEY).equals("4"));
		assertEquals(true, ciscoCommandCode.getKeyValue(VALUE_KEY).equals(""));
		
	}
}
