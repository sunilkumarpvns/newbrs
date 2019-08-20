package com.elitecore.diameterapi.diameter.stack.application.sessionrelease;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.NullLogger;
import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;

@RunWith(value=JUnitParamsRunner.class)
public class AppDefaultSessionReleaseIndicatorTest {
	
	
	private static AppDefaultSessionReleaseIndicatorExt appDefaultSessionReleaseIndicator;
	
	
	@BeforeClass
	public static void testSetup(){
		appDefaultSessionReleaseIndicator = new AppDefaultSessionReleaseIndicatorExt();
		LogManager.setDefaultLogger(new NullLogger());
		DummyDiameterDictionary.getInstance();
	}
	
	@Test
	public void testDiameterRequestProvideForSessionRelease(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		
		try{
			createAndAddDiameterAVP(diameterRequest, DiameterAVPConstants.CC_REQUEST_TYPE, Integer.toString(DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST));
		}catch(Exception ex){
			fail(ex.getMessage());
		}
		
		assertEquals(false, appDefaultSessionReleaseIndicator.isEligible(diameterRequest));
		
	}
	
	@Test
	public void testCCATerminationSessionReleaseWithSuccess(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		try{
			createAndAddDiameterAVP(diameterRequest, DiameterAVPConstants.CC_REQUEST_TYPE, Integer.toString(DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST));
		}catch(Exception ex){
			fail(ex.getMessage());
		}
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_SUCCESS);
		
		assertEquals(true, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
	}
	
	@Test
	public void testCCAUpdateSessionReleaseWithSuccess(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_SUCCESS);
		
		try{
			createAndAddDiameterAVP(diameterAnswer, DiameterAVPConstants.CC_REQUEST_TYPE, Integer.toString(DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST));
		}catch(Exception ex){
			fail(ex.getMessage());
		}
		
		assertEquals(false, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
	}
	
	@Test
	public void testCCAInitialSessionReleaseWithSuccess(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_SUCCESS);
		
		try{
			createAndAddDiameterAVP(diameterAnswer, DiameterAVPConstants.CC_REQUEST_TYPE, Integer.toString(DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST));
		}catch(Exception ex){
			fail(ex.getMessage());
		}
		
		assertEquals(false, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
	}
	
	@Test
	public void testCCAWithUnknownRequestType(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_SUCCESS);
		
		try{
			createAndAddDiameterAVP(diameterAnswer, DiameterAVPConstants.CC_REQUEST_TYPE, "10");
		}catch(Exception ex){
			fail(ex.getMessage());
		}
		
		assertEquals(false, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
	}
	
	@Test
	public void testNoRequestTypeAVPInCCA(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_SUCCESS);
	
		assertEquals(true, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
	}
	
	@Test
	public void testNegativeResultCodeCCA(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.CREDIT_CONTROL.code);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_USER_UNKNOWN);
		
		try{
			createAndAddDiameterAVP(diameterAnswer, DiameterAVPConstants.CC_REQUEST_TYPE, Integer.toString(DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST));
		}catch(Exception ex){
			fail(ex.getMessage());
		}
	
		assertEquals(true, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
	}
	
	///Accouting Packet Test cases
	@Test
	public void testAccoutingRequestTermination(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.ACCOUNTING.code);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_SUCCESS);
		
		try{
			createAndAddDiameterAVP(diameterRequest, DiameterAVPConstants.ACCOUNTING_RECORD_TYPE, Integer.toString(DiameterAttributeValueConstants.STOP_RECORD));
		}catch(Exception ex){
			fail(ex.getMessage());
		}
		
		assertEquals(true, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
	}
	
	@Test
	public void testAccountingStartWithSuccess(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.ACCOUNTING.code);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_SUCCESS);
		
		try{
			createAndAddDiameterAVP(diameterAnswer, DiameterAVPConstants.ACCOUNTING_RECORD_TYPE, Integer.toString(DiameterAttributeValueConstants.START_RECORD));
		}catch(Exception ex){
			fail(ex.getMessage());
		}
		
		assertEquals(false, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
	}
	
	@Test
	public void testAccountingUpdatelSessionReleaseWithSuccess(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.ACCOUNTING.code);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_SUCCESS);
		
		try{
			createAndAddDiameterAVP(diameterAnswer, DiameterAVPConstants.ACCOUNTING_RECORD_TYPE, Integer.toString(DiameterAttributeValueConstants.INTERIM_RECORD));
		}catch(Exception ex){
			fail(ex.getMessage());
		}
		
		assertEquals(false, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
	}
	
	@Test
	public void testAccoutingWithUnknownRecordType(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.ACCOUNTING.code);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_SUCCESS);
		
		try{
			createAndAddDiameterAVP(diameterAnswer, DiameterAVPConstants.ACCOUNTING_RECORD_TYPE, "10");
		}catch(Exception ex){
			fail(ex.getMessage());
		}
		
		assertEquals(false, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
	}
	
	@Test
	public void testNoRecordTypeAVPInAccounting(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.ACCOUNTING.code);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_SUCCESS);		
	
		assertEquals(true, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
	}
	
	@Test
	public void testNegativeResultCodeForAccouting(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.ACCOUNTING.code);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_USER_UNKNOWN);
		
		try{
			createAndAddDiameterAVP(diameterAnswer, DiameterAVPConstants.ACCOUNTING_RECORD_TYPE, Integer.toString(DiameterAttributeValueConstants.INTERIM_RECORD));
		}catch(Exception ex){
			fail(ex.getMessage());
		}
	
		assertEquals(true, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
	}
	
	//Session-Termination
	@Test
	public void testSessionTermination(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.SESSION_TERMINATION.code);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_SUCCESS);
		
		assertEquals(true, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
		diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_UNABLE_TO_COMPLY);
		
		assertEquals(true, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
	}
	
	//Packe Type other than Session-Termination, Accounting, Credit-Control\
	@Test
	public void testUnknownPackeType(){
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.AUTHENTICATION_AUTHORIZATION.code);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_SUCCESS);
		
		assertEquals(false, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
		diameterAnswer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_UNABLE_TO_COMPLY);
		
		assertEquals(true, appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
		
	}
	

	
	
	
	
	
	
	public Object[][] data_for_Session_should_release_whne_result_code_category_is_other_than_1XXX_2XXX(){
		
		
		
		Object [][] obj  =  {
				
			//3XXX and initial-request
				{ 3000 }, { 3001 }, { 3999 }, { 3998 }, { 3264 },
				
			// 4XXX and initial-request	
				{ 4000 }, { 4001 }, { 4999 }, { 4998 }, { 4264 },
				
			// 5XXX and initial-request
				{ 5000 }, { 5001 }, { 5999 }, { 5998 }, { 5264 },
				
			// 5XXX and initial-request
				{ 6000 }, { 6001 }, { Integer.MAX_VALUE }, { 7000 }, { 52640 }
		};
		
		
		
		List<Object[]> objects = new ArrayList<Object[]>();
		
		
		for(ResultCode resultCode : ResultCode.values()){
			if(resultCode.category != ResultCodeCategory.RC1XXX &&
					resultCode.category != ResultCodeCategory.RC2XXX){
				
				objects.add(new Object [] {
						resultCode.code
				});
			}
		}
		
		Object [][] finalObject = new Object[objects.size() + obj.length][];
		
		finalObject = objects.toArray(finalObject);
		
		System.arraycopy(obj, 0, finalObject, objects.size(), obj.length);
		
		return finalObject;
		
	}
	
	
	
	@Parameters(method="data_for_Session_should_release_whne_result_code_category_is_other_than_1XXX_2XXX")
	@Test
	public void test_Session_should_release_whne_result_code_category_is_other_than_1XXX_2XXX(int resultCode){
		DiameterAnswer diameterAnswer = new DiameterAnswer();
		
		IDiameterAVP resultCodeAVp = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.RESULT_CODE);
		resultCodeAVp.setInteger(resultCode);
		diameterAnswer.addAvp(resultCodeAVp);
		
		assertTrue(appDefaultSessionReleaseIndicator.checkResultCodeForSessionRemoval(diameterAnswer));
	}
	
	
	public Object[][] data_for_Session_should_release_whne_result_code_category_is_1XXX_2XXX(){
		
		
		
		Object [][] obj  =  {
				
			//3XXX and initial-request
				{ 1000 }, { 1001 }, { 1999 }, { 1998 }, { 1264 },
				
			// 4XXX and initial-request	
				{ 2000 }, { 2001 }, { 2999 }, { 2998 }, { 2264 }				
		};
		
		
		
		List<Object[]> objects = new ArrayList<Object[]>();
		
		
		for(ResultCode resultCode : ResultCode.values()){
			if(resultCode.category == ResultCodeCategory.RC1XXX ||
					resultCode.category == ResultCodeCategory.RC2XXX){
				
				objects.add(new Object [] {
						resultCode.code
				});
			}
		}
		
		Object [][] finalObject = new Object[objects.size() + obj.length][];
		
		finalObject = objects.toArray(finalObject);
		
		System.arraycopy(obj, 0, finalObject, objects.size(), obj.length);
		
		return finalObject;
		
	}
	
	
	
	@Parameters(method="data_for_Session_should_release_whne_result_code_category_is_1XXX_2XXX")
	@Test
	public void test_Session_should_not_release_whne_result_code_category_is_1XXX_2XXX(int resultCode){
		DiameterAnswer diameterAnswer = new DiameterAnswer();
		
		IDiameterAVP resultCodeAVp = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.RESULT_CODE);
		resultCodeAVp.setInteger(resultCode);
		diameterAnswer.addAvp(resultCodeAVp);
		
		assertFalse(appDefaultSessionReleaseIndicator.checkResultCodeForSessionRemoval(diameterAnswer));
	}
	
	public Object[][] data_for_session_should_not_release_whne_command_code_is_server_initiated(){
		
		return new Object [][] {
			{CommandCode.ABORT_SESSION.code},
			{CommandCode.RE_AUTHORIZATION.code},
			{CommandCode.PUSH_PROFILE.code}
		};
	}
	
	
	@Parameters(method="data_for_session_should_not_release_whne_command_code_is_server_initiated")
	@Test
	public void test_session_should_not_release_whne_command_code_is_server_initiated(int commandCode){
		DiameterAnswer diameterAnswer = new DiameterAnswer();
		diameterAnswer.setCommandCode(commandCode);
		
		IDiameterAVP resultCodeAVp = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.RESULT_CODE);
		resultCodeAVp.setInteger(ResultCode.DIAMETER_APPLICATION_UNSUPPORTED.code);
		diameterAnswer.addAvp(resultCodeAVp);

		assertFalse(appDefaultSessionReleaseIndicator.isEligible(diameterAnswer));
	}
	

	@Test
	public void test_Session_should_release_whne_result_code_not_found_in_request(){
		DiameterAnswer diameterAnswer = new DiameterAnswer();
		assertTrue(appDefaultSessionReleaseIndicator.checkResultCodeForSessionRemoval(diameterAnswer));
	}
	
	

	private static class AppDefaultSessionReleaseIndicatorExt extends AppDefaultSessionReleaseIndicator{
		@Override
		public boolean checkResultCodeForSessionRemoval(DiameterAnswer diameterAnswer) {
			return super.checkResultCodeForSessionRemoval(diameterAnswer);
		}
	}

	
	
	
	public void createAndAddDiameterAVP(DiameterPacket diameterPacket, String avpID, String value) throws NullPointerException{
		IDiameterAVP diameterAVP = DummyDiameterDictionary.getInstance().getKnownAttribute(avpID);
		diameterAVP.setStringValue(value);
		diameterPacket.addAvp(diameterAVP);
	}
	
	public void createAndAddDiameterInfoAVP(DiameterPacket diameterPacket, String avpID, String value) throws NullPointerException{
		IDiameterAVP diameterAVP = DummyDiameterDictionary.getInstance().getKnownAttribute(avpID);
		diameterAVP.setStringValue(value);
		diameterPacket.addInfoAvp(diameterAVP);
	}
	
	public void createAndAddDiameterAVP(AvpGrouped diameterPacket, String avpID, String value) throws NullPointerException{
		IDiameterAVP diameterAVP = DummyDiameterDictionary.getInstance().getKnownAttribute(avpID);
		diameterAVP.setStringValue(value);
		diameterPacket.addSubAvp(diameterAVP);
	}

}
