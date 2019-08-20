package com.elitecore.classicrating;


import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.elitecore.classicrating.base.IBaseConstant;
import com.elitecore.classicrating.blmanager.base.MandatoryParameterMissingException;
import com.elitecore.classicrating.commons.request.RequestParameters;
import com.elitecore.classicrating.commons.validators.RequestParameterValidator;

public class ValidateParameterTest extends TestCase {
	
	public ValidateParameterTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		
		// For Data
		suite.addTest(new ValidateParameterTest("testAuthorizationDATAParameters"));
		suite.addTest(new ValidateParameterTest("testAccountingDATAParameters"));
		
		//For Voice
		suite.addTest(new ValidateParameterTest("testAuthorizationVOIPParameters"));
		suite.addTest(new ValidateParameterTest("testAccountingVOIPParameters"));
		
		return suite;
	}


	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testAuthorizationDATAParameters() {
		RequestParameters params = new RequestParameters();
		
		params.put(IBaseConstant.USERID, "Test_Usage");
		params.put(IBaseConstant.ACCT_INPUT_OCTETS, "1048567");
		params.put(IBaseConstant.ACCT_OUTPUT_OCTETS, "1048567");
		params.put(IBaseConstant.SERVICE_TYPE, "DATA");
		
	     try{
	    	 params.put(IBaseConstant.REQUEST_TYPE, IBaseConstant.AUTHORISATION);
	    	 RequestParameterValidator.validateParameters(params);
	    	 //fail("Mandatory paramter check failed.");
	     }catch(MandatoryParameterMissingException e){
	    	 assertFalse(e.getMessage() , true);
	     }catch(Exception e){
	    	 assertFalse(e.getMessage() , true);
	     }
	     
	     assertTrue("Required Parameters present for Accounting Request", true);
	}
	
	public void testAccountingDATAParameters()  {
		
		RequestParameters params = new RequestParameters();
		
		params.put(IBaseConstant.USERID, "Test_Usage");
		params.put(IBaseConstant.ACCT_INPUT_OCTETS, "1048567");
		params.put(IBaseConstant.ACCT_OUTPUT_OCTETS, "1048567");
		params.put(IBaseConstant.SESSION_TIME, "100000");
		params.put(IBaseConstant.SERVICE_TYPE, "DATA");
		
	     try{
	    	 params.put(IBaseConstant.REQUEST_TYPE, IBaseConstant.AUTHORISATION);
	    	 RequestParameterValidator.validateParameters(params);
	    	 //fail("Mandatory paramter check failed.");
	     }catch(MandatoryParameterMissingException e){
	    	 assertFalse(e.getMessage() , true);
	     }catch(Exception e){
	    	 assertFalse(e.getMessage() , true);
	     }
	     
	     assertTrue("Required Parameters present for Accounting Request", true);
	}
	
	public void testAuthorizationVOIPParameters() {
		
		RequestParameters params = new RequestParameters();
		
		params.put(IBaseConstant.USERID, "Test_Usage");
		params.put(IBaseConstant.ACCT_SESSION_TIME, "100000");
		params.put(IBaseConstant.EVENT_TYPE, "EVENT");
		//params.put(IBaseConstant.SERVICE_TYPE, "VOIP");
		params.put(IBaseConstant.CALLED_STATION_ID, "842143");
		params.put(IBaseConstant.CALL_START, "1/9/2008  10:00:00 AM");
		params.put(IBaseConstant.CALL_END, "1/9/2008  10:04:09 AM");
		
	     try{
	    	 params.put(IBaseConstant.REQUEST_TYPE, IBaseConstant.AUTHORISATION);
	    	 RequestParameterValidator.validateParameters(params);
	    	 fail("Mandatory paramter check failed.");
	     }catch(MandatoryParameterMissingException e){
	    	 //assertFalse(e.getMessage() , true);
	     }catch(Exception e){
	    	 //assertFalse(e.getMessage() , true);
	     }  
	}
	
	public void testAccountingVOIPParameters() {
		
		RequestParameters params = new RequestParameters();
		
		params.put(IBaseConstant.USERID, "Test_Usage");
		params.put(IBaseConstant.ACCT_SESSION_TIME, "100000");
		params.put(IBaseConstant.SERVICE_TYPE, "VOIP");
		params.put(IBaseConstant.CALLED_STATION_ID, "842143");
		params.put(IBaseConstant.CALL_START, "1/9/2008  10:00:00 AM");
		params.put(IBaseConstant.CALL_END, "1/9/2008  10:04:09 AM");
		
	     try{
	    	 params.put(IBaseConstant.REQUEST_TYPE, IBaseConstant.ACCOUNTING);
	    	 RequestParameterValidator.validateParameters(params);
	    	 fail("Mandatory parameter check failed");
	     }catch(MandatoryParameterMissingException e){
	    	 //assertFalse(e.getMessage() , true);
	     }catch(Exception e){
	    	 //assertFalse(e.getMessage() , true);
	     }
	     
	     assertTrue("Required Parameters present for Accounting Request", true);
	}
	
}
