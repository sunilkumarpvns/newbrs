package com.elitecore.diameterapi.diameter.common.packet.avps;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.elitecore.diameterapi.diameter.common.packet.avps.basic.AvpFloat32;
import com.elitecore.diameterapi.diameter.common.packet.avps.basic.AvpFloat64;
import com.elitecore.diameterapi.diameter.common.packet.avps.basic.AvpInteger32;
import com.elitecore.diameterapi.diameter.common.packet.avps.basic.AvpInteger64;
import com.elitecore.diameterapi.diameter.common.packet.avps.basic.AvpUnsigned32;
import com.elitecore.diameterapi.diameter.common.packet.avps.basic.AvpUnsigned64;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.AvpAddress;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.AvpUTF8String;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;


public class DiameterAvpTest  extends TestCase{
	


	public DiameterAvpTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTest(new DiameterAvpTest("testStringAVPDoPlus"));
		suite.addTest(new DiameterAvpTest("testInterger32AVPDoPlus"));
		suite.addTest(new DiameterAvpTest("testInterger64AVPDoPlus"));
		suite.addTest(new DiameterAvpTest("testFloat32AVPDoPlus"));
		suite.addTest(new DiameterAvpTest("testFloat64AVPDoPlus"));
		suite.addTest(new DiameterAvpTest("testAddressAVPDoPlus"));
		suite.addTest(new DiameterAvpTest("testInterger32AVPDoPlusInvalidValue"));
		suite.addTest(new DiameterAvpTest("testInterger64AVPDoPlusInvalidValue"));
		suite.addTest(new DiameterAvpTest("testInterger32AVPDoPlusNullValue"));
		suite.addTest(new DiameterAvpTest("testInterger64AVPDoPlusNullValue"));
		suite.addTest(new DiameterAvpTest("testFloat32AVPDoPlusNullValue"));
		suite.addTest(new DiameterAvpTest("testFloat64AVPDoPlusNullValue"));
		suite.addTest(new DiameterAvpTest("testInterger32AVPDoPlusMinus"));
		suite.addTest(new DiameterAvpTest("testInterger64AVPDoPlusMinus"));
		suite.addTest(new DiameterAvpTest("testFloat32AVPDoPlusMinus"));
		suite.addTest(new DiameterAvpTest("testFloat64AVPDoPlusMinus"));
		suite.addTest(new DiameterAvpTest("testInterger32AVPDoPlusExpression"));
		suite.addTest(new DiameterAvpTest("testInterger64AVPDoPlusExpression"));
		suite.addTest(new DiameterAvpTest("testFloat32AVPDoPlusExpression"));
		suite.addTest(new DiameterAvpTest("testFloat64AVPDoPlusExpression"));
		suite.addTest(new DiameterAvpTest("testUnsigned32AVPDoPlus"));
		suite.addTest(new DiameterAvpTest("testUnsigned64AVPDoPlus"));
		suite.addTest(new DiameterAvpTest("testUnsigned32AVPDoPlusNull"));
		suite.addTest(new DiameterAvpTest("testUnsigned64AVPDoPlusNull"));
		suite.addTest(new DiameterAvpTest("testUnsigned32AVPDoPlusExpression"));
		suite.addTest(new DiameterAvpTest("testUnsigned64AVPDoPlusExpression"));
		
		
		return suite;
	}

		
	public void testStringAVPDoPlus(){
		try{			
			final String DEFAULT_USER_NAME = "eliteaaa";
			AvpUTF8String strAttribute = new AvpUTF8String(DiameterAVPConstants.USER_NAME_INT,0,(byte)0,DiameterAVPConstants.USER_NAME,"yes");
			strAttribute.setStringValue(DEFAULT_USER_NAME);
			String appendString = "elitecore";
			String resultString = DEFAULT_USER_NAME.concat(appendString);
			strAttribute.doPlus(appendString);
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStringAVPDoPlus failed, reason: "+e.getMessage());			
		}
	}
	
	public void testInterger32AVPDoPlus(){
		try{			
			final int DEFAULT_VALUE=10;
			AvpInteger32 strAttribute = new AvpInteger32(0,0,(byte)0,"0:0","yes");
			strAttribute.setInteger(DEFAULT_VALUE);
			int appendValue = 20;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testInterger32AVPDoPlus failed, reason: "+e.getMessage());			
		}
	}
	
	public void testInterger64AVPDoPlus(){
		try{			
			final int DEFAULT_VALUE=10;
			AvpInteger64 strAttribute = new AvpInteger64(0,0,(byte)0,"0:0","yes");
			strAttribute.setInteger(DEFAULT_VALUE);
			int appendValue = 20;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testInterger64AVPDoPlus failed, reason: "+e.getMessage());			
		}
	}
	
	public void testFloat32AVPDoPlus(){
		try{			
			final float DEFAULT_VALUE=(float)10.5;
			AvpFloat32 strAttribute = new AvpFloat32(0,0,(byte)0,"0:0","yes");
			strAttribute.setFloat(DEFAULT_VALUE);
			float appendValue = (float)2.5;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testFloat32AVPDoPlus failed, reason: "+e.getMessage());			
		}
	}
	
	public void testFloat64AVPDoPlus(){
		try{			
			final float DEFAULT_VALUE=(float)10.5;
			AvpFloat64 strAttribute = new AvpFloat64(0,0,(byte)0,"0:0","yes");
			strAttribute.setFloat(DEFAULT_VALUE);
			float appendValue = (float)2.5;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testFloat64AVPDoPlus failed, reason: "+e.getMessage());			
		}
	}
	
	public void testAddressAVPDoPlus(){
		try{			
			final String DEFAULT_VALUE= "1.1.1.1";
			AvpAddress strAttribute = new AvpAddress(0,0,(byte)0,"0:0","yes");
			strAttribute.setStringValue(DEFAULT_VALUE);
			String appendValue = "2.2.2.2";
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + appendValue,strAttribute.getStringValue(),appendValue);
		}catch(Exception e){
			e.printStackTrace();
			fail("testAddressAVPDoPlus failed, reason: "+e.getMessage());			
		}
	}
	
	public void testInterger32AVPDoPlusInvalidValue(){
		try{			
			final int DEFAULT_VALUE=10;
			AvpInteger32 strAttribute = new AvpInteger32(0,0,(byte)0,"0:0","yes");
			strAttribute.setInteger(DEFAULT_VALUE);
			String appendValue = "invalidString";
			String resultString = String.valueOf(DEFAULT_VALUE);
			try{
				strAttribute.doPlus(String.valueOf(appendValue));
			}catch (IllegalArgumentException e) {
			}	
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testInterger32AVPDoPlusInvalidValue failed, reason: "+e.getMessage());			
		}
	}
	
	public void testInterger64AVPDoPlusInvalidValue(){
		try{			
			final int DEFAULT_VALUE=10;
			AvpInteger64 strAttribute = new AvpInteger64(0,0,(byte)0,"0:0","yes");
			strAttribute.setInteger(DEFAULT_VALUE);
			String appendValue = "invalidString";
			String resultString = String.valueOf(DEFAULT_VALUE);
			try{
				strAttribute.doPlus(String.valueOf(appendValue));
			}catch (IllegalArgumentException e) {
			}	
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testInterger32AVPDoPlusInvalidValue failed, reason: "+e.getMessage());			
		}
	}
	

	public void testInterger32AVPDoPlusNullValue(){
		try{			
			final int DEFAULT_VALUE=10;
			AvpInteger32 strAttribute = new AvpInteger32(0,0,(byte)0,"0:0","yes");
			strAttribute.setInteger(DEFAULT_VALUE);
			String appendValue = null;
			String resultString = String.valueOf(DEFAULT_VALUE);
			try{
				strAttribute.doPlus(appendValue);
			}catch (IllegalArgumentException e) {
			}	
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testInterger32AVPDoPlusNullValue failed, reason: "+e.getMessage());			
		}
	}
	
	public void testInterger64AVPDoPlusNullValue(){
		try{			
			final int DEFAULT_VALUE=10;
			AvpInteger64 strAttribute = new AvpInteger64(0,0,(byte)0,"0:0","yes");
			strAttribute.setInteger(DEFAULT_VALUE);
			String appendValue = null;
			String resultString = String.valueOf(DEFAULT_VALUE);
			try{
				strAttribute.doPlus(appendValue);
			}catch (IllegalArgumentException e) {
			}	
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testInterger64AVPDoPlusNullValue failed, reason: "+e.getMessage());			
		}
	}
	
	
	
	public void testFloat32AVPDoPlusNullValue(){
		try{			
			final float DEFAULT_VALUE=(float)10.5;
			AvpFloat32 strAttribute = new AvpFloat32(0,0,(byte)0,"0:0","yes");
			strAttribute.setFloat(DEFAULT_VALUE);
			String appendValue = null;
			String resultString = String.valueOf(DEFAULT_VALUE);
			strAttribute.doPlus(appendValue);
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testFloat32AVPDoPlusNullValue failed, reason: "+e.getMessage());			
		}
	}
	
	public void testFloat64AVPDoPlusNullValue(){
		try{			
			final float DEFAULT_VALUE=(float)10.5;
			AvpFloat64 strAttribute = new AvpFloat64(0,0,(byte)0,"0:0","yes");
			strAttribute.setFloat(DEFAULT_VALUE);
			String appendValue = null;
			String resultString = String.valueOf(DEFAULT_VALUE);
			strAttribute.doPlus(appendValue);
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testFloat64AVPDoPlusNullValue failed, reason: "+e.getMessage());			
		}
	}
	
	
	public void testInterger32AVPDoPlusMinus(){
		try{			
			final int DEFAULT_VALUE=30;
			AvpInteger32 strAttribute = new AvpInteger32(0,0,(byte)0,"0:0","yes");
			strAttribute.setInteger(DEFAULT_VALUE);
			int appendValue = -20;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testInterger32AVPDoPlusMinus failed, reason: "+e.getMessage());			
		}
	}
	
	public void testInterger64AVPDoPlusMinus(){
		try{			
			final int DEFAULT_VALUE=30;
			AvpInteger64 strAttribute = new AvpInteger64(0,0,(byte)0,"0:0","yes");
			strAttribute.setInteger(DEFAULT_VALUE);
			int appendValue = -20;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testInterger64AVPDoPlusMinus failed, reason: "+e.getMessage());			
		}
	}
	
	
	public void testFloat32AVPDoPlusMinus(){
		try{			
			final float DEFAULT_VALUE=(float)10.5;
			AvpFloat32 strAttribute = new AvpFloat32(0,0,(byte)0,"0:0","yes");
			strAttribute.setFloat(DEFAULT_VALUE);
			float appendValue = -(float)2.5;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testFloat32AVPDoPlusMinus failed, reason: "+e.getMessage());			
		}
	}
	
	public void testFloat64AVPDoPlusMinus(){
		try{			
			final float DEFAULT_VALUE=(float)10.5;
			AvpFloat64 strAttribute = new AvpFloat64(0,0,(byte)0,"0:0","yes");
			strAttribute.setFloat(DEFAULT_VALUE);
			float appendValue = -(float)2.5;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testFloat64AVPDoPlusMinus failed, reason: "+e.getMessage());			
		}
	}
	
	public void testInterger32AVPDoPlusExpression(){
		try{			
			final int DEFAULT_VALUE=10;
			AvpInteger32 strAttribute = new AvpInteger32(0,0,(byte)0,"0:0","yes");
			strAttribute.setInteger(DEFAULT_VALUE);
			int appendValue = 20*10;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testInterger32AVPDoPlus failed, reason: "+e.getMessage());			
		}
	}
	
	
	public void testInterger64AVPDoPlusExpression(){
		try{			
			final int DEFAULT_VALUE=10;
			AvpInteger64 strAttribute = new AvpInteger64(0,0,(byte)0,"0:0","yes");
			strAttribute.setInteger(DEFAULT_VALUE);
			int appendValue = 20*10;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testInterger64AVPDoPlusExpression failed, reason: "+e.getMessage());			
		}
	}
	
	public void testFloat32AVPDoPlusExpression(){
		try{			
			final float DEFAULT_VALUE=(float)10.5;
			AvpFloat32 strAttribute = new AvpFloat32(0,0,(byte)0,"0:0","yes");
			strAttribute.setFloat(DEFAULT_VALUE);
			float appendValue = (float)2.5*10;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testFloat32AVPDoPlusExpression failed, reason: "+e.getMessage());			
		}
	}
	
	public void testFloat64AVPDoPlusExpression(){
		try{			
			final float DEFAULT_VALUE=(float)10.5;
			AvpFloat64 strAttribute = new AvpFloat64(0,0,(byte)0,"0:0","yes");
			strAttribute.setFloat(DEFAULT_VALUE);
			float appendValue = (float)2.5*10;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testFloat64AVPDoPlusExpression failed, reason: "+e.getMessage());			
		}
	}
	
	public void testUnsigned32AVPDoPlus(){
		try{			
			final int DEFAULT_VALUE=10;
			Map<Integer, String> supportedValue = new HashMap<Integer, String>();
			supportedValue.put(30, "30");
			AvpUnsigned32 strAttribute = new AvpUnsigned32(0,0,(byte)0,"0:0","yes",supportedValue);
			strAttribute.setInteger(DEFAULT_VALUE);
			int appendValue = 20;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testUnsigned32AVPDoPlus failed, reason: "+e.getMessage());			
		}
	}
	
	public void testUnsigned64AVPDoPlus(){
		try{			
			final int DEFAULT_VALUE=10;
			AvpUnsigned64 strAttribute = new AvpUnsigned64(0,0,(byte)0,"0:0","yes");
			strAttribute.setInteger(DEFAULT_VALUE);
			int appendValue = 20;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testUnsigned64AVPDoPlus failed, reason: "+e.getMessage());			
		}
	}
	
	public void testUnsigned32AVPDoPlusNull(){
		try{			
			final int DEFAULT_VALUE=10;
			Map<Integer, String> supportedValue = new HashMap<Integer, String>();
			supportedValue.put(10, "10");
			AvpUnsigned32 strAttribute = new AvpUnsigned32(0,0,(byte)0,"0:0","yes",supportedValue);
			strAttribute.setInteger(DEFAULT_VALUE);
			String appendValue = null;
			String resultString = String.valueOf(DEFAULT_VALUE);
			strAttribute.doPlus(appendValue);
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testUnsigned32AVPDoPlusNull failed, reason: "+e.getMessage());			
		}
	}
	
	public void testUnsigned64AVPDoPlusNull(){
		try{			
			final int DEFAULT_VALUE=10;
			AvpUnsigned64 strAttribute = new AvpUnsigned64(0,0,(byte)0,"0:0","yes");
			strAttribute.setInteger(DEFAULT_VALUE);
			String appendValue = null;
			String resultString = String.valueOf(DEFAULT_VALUE);
			strAttribute.doPlus(appendValue);
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testUnsigned64AVPDoPlusNull failed, reason: "+e.getMessage());			
		}
	}
	
	
	
	public void testUnsigned32AVPDoPlusExpression(){
		try{			
			final int DEFAULT_VALUE=10;
			Map<Integer, String> supportedValue = new HashMap<Integer, String>();
			supportedValue.put(210, "210");
			AvpUnsigned32 strAttribute = new AvpUnsigned32(0,0,(byte)0,"0:0","yes",supportedValue);
			strAttribute.setInteger(DEFAULT_VALUE);
			int appendValue = 20*10;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testUnsigned32AVPDoPlusExpression failed, reason: "+e.getMessage());			
		}
	}
	
	public void testUnsigned64AVPDoPlusExpression(){
		try{			
			final int DEFAULT_VALUE=10;
			AvpUnsigned64 strAttribute = new AvpUnsigned64(0,0,(byte)0,"0:0","yes");
			strAttribute.setInteger(DEFAULT_VALUE);
			int appendValue = 20*10;
			String resultString = String.valueOf(DEFAULT_VALUE+appendValue);
			strAttribute.doPlus(String.valueOf(appendValue));
			assertEquals("Diameter Packet resultant AVP value must be " + resultString,strAttribute.getStringValue(),resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testUnsigned64AVPDoPlusExpression failed, reason: "+e.getMessage());			
		}
	}

}

