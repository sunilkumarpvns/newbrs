
/**
 *  Copyright (C) Elitecore Technologies LTD.
 *  @author raghug
 *  Created on: Nov 3, 2008
 */
package com.elitecore.classicrating;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.elitecore.classicrating.api.EliteClassicRatingEngine;
import com.elitecore.classicrating.base.IBaseConstant;
import com.elitecore.classicrating.base.IRatingAppContext;
import com.elitecore.classicrating.commons.request.RequestParameters;
import com.elitecore.classicrating.commons.response.ResponseObject;
import com.elitecore.classicrating.datasource.EliteClassicDataSourceProvider;
import com.elitecore.classicrating.datasource.EliteClassicRatingAppContext;
import com.elitecore.classicrating.datasource.IDataSourceProvider;


/**
 *
 * @author raghug
 */
public class TestAuthorization extends TestCase {

	static IRatingAppContext ratingAppContext;

	static EliteClassicRatingEngine ratingEngine;

	static IDataSourceProvider provider;

	public TestAuthorization(String testName) {
		super(testName);
		try {
			provider = new EliteClassicDataSourceProvider();
			ratingAppContext = new EliteClassicRatingAppContext(provider);
			ratingEngine = new EliteClassicRatingEngine(ratingAppContext);
		} catch (Exception ex) {
			System.out.println("Error in initializing driver: " + ex);
			System.exit(0);
		}
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();

		suite.addTest(new TestAuthorization("testDataAuthorization"));
		suite.addTest(new TestAuthorization("testVoIPAuthorization"));

		suite.addTest(new TestAuthorization("testInsufficientBalance"));
		suite.addTest(new TestAuthorization("testUsernameValidation"));

		suite.addTest(new TestAuthorization("testCalledStationId"));
		suite.addTest(new TestAuthorization("testCallStartParseException"));

		suite.addTest(new TestAuthorization("testCallEndParseException"));
		suite.addTest(new TestAuthorization("testCustomerNotFound"));

		return suite;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	
	
	   /*  Test for successful authorization call for Data */
  
    public void testDataAuthorization() {
        RequestParameters request = new RequestParameters();
        request.put(IBaseConstant.USERID, "SEG_1");
        request.put(IBaseConstant.SERVICE_TYPE, "VOIP");
        request.put(IBaseConstant.CALLED_STATION_ID, "9898675634");
        request.put(IBaseConstant.NAS_IP_ADDRESS, "192.168.3.43");

        ResponseObject actualResponse = (ResponseObject) ratingEngine.authorizationRequest(request);
		System.out.println(  (actualResponse.getResponseMessage()!=null? actualResponse.getResponseMessage() : "" ) );
     }

 	/*  Test for successful authorization call for Voice */

   
    public void testVoIPAuthorization() {
        RequestParameters request = new RequestParameters();
        request.put(IBaseConstant.USERID, "StepwiseVoIP");
        request.put(IBaseConstant.CALLED_STATION_ID, "9898767654");
        request.put(IBaseConstant.SERVICE_TYPE, "VOIP");
        ResponseObject actualResponse = (ResponseObject) ratingEngine.authorizationRequest(request);
		System.out.println(  (actualResponse.getResponseMessage()!=null? actualResponse.getResponseMessage() : "" ) );
    }


    /*  Test for insufficient balance */
   
    public void testInsufficientBalance() {
        RequestParameters request = new RequestParameters();
        request.put(IBaseConstant.USERID, "Customer_Data");
        request.put(IBaseConstant.SERVICE_TYPE, "DATA");
        ResponseObject actualResponse = (ResponseObject) ratingEngine.authorizationRequest(request);
		System.out.println(  (actualResponse.getResponseMessage()!=null? actualResponse.getResponseMessage() : "" ) );
    }

    /*  Test for validation  of username */
   
    public void testUsernameValidation() {
        RequestParameters request = new RequestParameters();
        //request.put(IBaseConstant.USERID, "SEG_1");
        ResponseObject actualResponse = (ResponseObject) ratingEngine.authorizationRequest(request);
		System.out.println(  (actualResponse.getResponseMessage()!=null? actualResponse.getResponseMessage() : "" ) );
    }

    /*  Test for validation  of called station ID */
  
    public void testCalledStationId() {
        RequestParameters request = new RequestParameters();
        request.put(IBaseConstant.USERID, "SEG_1");
        //request.put(IBaseConstant.CALLED_STATION_ID, "9898767654");
        ResponseObject actualResponse = (ResponseObject) ratingEngine.authorizationRequest(request);
		System.out.println(  (actualResponse.getResponseMessage()!=null? actualResponse.getResponseMessage() : "" ) );
    }

    /*  Test for Parse Exception for the value of Call Start Time */
    
    public void testCallStartParseException() {
        RequestParameters request = new RequestParameters();
        request.put(IBaseConstant.USERID, "SEG_1");
        request.put(IBaseConstant.CALLED_STATION_ID, "9898767654");
        request.put(IBaseConstant.ACCT_SESSION_TIME, "00");
        request.put(IBaseConstant.CALL_START, "1/9/200810:00:00AM");  //error
        request.put(IBaseConstant.CALL_END, "1/9/2008  10:03:01 AM");
        request.put(IBaseConstant.EVENT_TYPE, "Voice");
		ResponseObject actualResponse = (ResponseObject) ratingEngine.authorizationRequest(request);
		System.out.println(  (actualResponse.getResponseMessage()!=null? actualResponse.getResponseMessage() : "" ) );
    }

    /*  Test for Parse Exception for the value of Call end Time */
   
    public void testCallEndParseException()  {
        RequestParameters request = new RequestParameters();
        request.put(IBaseConstant.USERID, "SEG_1");
        request.put(IBaseConstant.CALLED_STATION_ID, "9898767654");
        request.put(IBaseConstant.ACCT_SESSION_TIME, "00");
        request.put(IBaseConstant.CALL_START, "1/9/2008  10:00:00AM");
        request.put(IBaseConstant.CALL_END, "9/2008  10:03:01 AM"); //error data
        request.put(IBaseConstant.EVENT_TYPE, "Voice");
        ResponseObject actualResponse = (ResponseObject) ratingEngine.authorizationRequest(request);
		System.out.println(  (actualResponse.getResponseMessage()!=null? actualResponse.getResponseMessage() : "" ) );
    }

    /*  Test for customer not found exception */

	public void testCustomerNotFound()  {
        RequestParameters request = new RequestParameters();
        request.put(IBaseConstant.USERID, "test customer");
        request.put(IBaseConstant.CALLED_STATION_ID, "9898767654");
        ResponseObject actualResponse = (ResponseObject) ratingEngine.authorizationRequest(request);
		System.out.println(  (actualResponse.getResponseMessage()!=null? actualResponse.getResponseMessage() : "" ) );
    }
}
