package com.elitecore.aaa.diameter.service.application.handlers.conf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;

/**
 * 
 * @author narendra.pathai
 *
 */
public class DiameterExternalCommunicationEntryDataTest {
	private static final int ANY_RESULT_CODE = ResultCode.DIAMETER_SUCCESS.code;
	private DiameterExternalCommunicationEntryData data;

	@Before
	public void setUp() {
		data = new DiameterExternalCommunicationEntryData();
	}
	
	@Test
	public void testPriorityOf_ShouldProvideHigherPriorityToResultCode_HavingHigherIndex() {
		data.getPriorityResultCodes().add(ResultCode.DIAMETER_SUCCESS.code);
		data.getPriorityResultCodes().add(ResultCode.DIAMETER_LIMITED_SUCCESS.code); // higher priority
		
		assertTrue(data.priorityOf(ResultCode.DIAMETER_LIMITED_SUCCESS.code) 
					> data.priorityOf(ResultCode.DIAMETER_SUCCESS.code));
	}
	
	@Test
	public void testPriorityOf_ShouldProvideHigherPriorityToResultCodeCategory_HavingHigherIndex() {
		data.getPriorityResultCodes().add(ResultCodeCategory.RC1XXX.value);
		data.getPriorityResultCodes().add(ResultCodeCategory.RC2XXX.value);
		
		assertTrue(data.priorityOf(2001) > data.priorityOf(1001));
	}
	
	@Test
	public void testGetPrioritizedResultCode_ShouldReturnExistingResultCodeIfItsPriorityIsHigherThanRemoteResultCode() {
		int exsitingResultCode = ResultCode.DIAMETER_SUCCESS.code;
		int remoteResultCode = ResultCode.DIAMETER_LIMITED_SUCCESS.code;
		
		data.getPriorityResultCodes().add(ResultCode.DIAMETER_LIMITED_SUCCESS.code);
		data.getPriorityResultCodes().add(ResultCode.DIAMETER_SUCCESS.code); // higher priority
		
		assertEquals(exsitingResultCode, data.getPrioritizedResultCode(exsitingResultCode, remoteResultCode));
	}
	
	@Test
	public void testGetPrioritizedResultCode_ShouldReturnRemoteResultCodeIfItsPriorityIsHigherThanExistingResultCode() {
		int exsitingResultCode = ResultCode.DIAMETER_LIMITED_SUCCESS.code;
		int remoteResultCode = ResultCode.DIAMETER_SUCCESS.code;
		
		data.getPriorityResultCodes().add(ResultCode.DIAMETER_LIMITED_SUCCESS.code);
		data.getPriorityResultCodes().add(ResultCode.DIAMETER_SUCCESS.code); // higher priority
		
		assertEquals(remoteResultCode, data.getPrioritizedResultCode(exsitingResultCode, remoteResultCode));
	}
	
	@Test
	public void testGetPrioritizedResultCode_ShouldReturnExistingResultCode_IfRemoteResultCodeIsMissingInPriorityList_AndRemoteResultCodeCategoryPriorityIsLowerThanExistingResultCodePriority() {
		int existingResultCode = ResultCode.DIAMETER_SUCCESS.code;
		int remoteResultCode = ResultCode.DIAMETER_APPLICATION_UNSUPPORTED.code;
		
		data.getPriorityResultCodes().add(ResultCodeCategory.RC3XXX.value);
		data.getPriorityResultCodes().add(ResultCode.DIAMETER_SUCCESS.code); // higher priority than 3XXX category
		
		assertEquals(existingResultCode, data.getPrioritizedResultCode(existingResultCode, remoteResultCode));
	}
	
	@Test
	public void testGetPrioritizedResultCode_ShouldReturnRemoteResultCode_IfRemoteResultCodeIsMissingInPriorityList_AndRemoteResultCodeCategoryPriorityIsHigherThanExistingResultCodePriority() {
		int existingResultCode = ResultCode.DIAMETER_SUCCESS.code;
		int remoteResultCode = ResultCode.DIAMETER_APPLICATION_UNSUPPORTED.code;
		
		data.getPriorityResultCodes().add(ResultCode.DIAMETER_SUCCESS.code);
		data.getPriorityResultCodes().add(ResultCodeCategory.RC3XXX.value); // higher priority
		
		assertEquals(remoteResultCode, data.getPrioritizedResultCode(existingResultCode, remoteResultCode));
	}
	
	
	@Test
	// TODO this behavior needs to be discussed with sumit as with this behavior we will not be able to run handler without any configuration
	public void testGetPrioritizedResultCode_ShouldReturnExistingResultCode_IfNeitherRemoteResultCodeNorResultCodeCategoryIsFoundInPriorityList() {
		int existingResultCode = ANY_RESULT_CODE;
		int remoteResultCode = ResultCode.DIAMETER_LOOP_DETECTED.code;
		
		data.getPriorityResultCodes().add(ResultCode.DIAMETER_SUCCESS.code);
		data.getPriorityResultCodes().add(ResultCodeCategory.RC4XXX.value);
		
		assertEquals(existingResultCode, data.getPrioritizedResultCode(existingResultCode, remoteResultCode));
	}
	
	@Test
	public void testGetPrioritizedResultCode_ShouldReturnExistingResultCode_IfExistingResultCodeCategoryPriorityIsHigherThanRemoteResultCodeCategoryPriority() {
		int existingResultCode = ResultCode.DIAMETER_SUCCESS.code;
		int remoteResultCode = ResultCode.DIAMETER_AUTHENTICATION_REJECTED.code;
		
		data.getPriorityResultCodes().add(ResultCodeCategory.RC4XXX.value);
		data.getPriorityResultCodes().add(ResultCodeCategory.RC2XXX.value);
		
		assertEquals(existingResultCode, data.getPrioritizedResultCode(existingResultCode, remoteResultCode));
	}
	
	@Test
	public void testGetPrioritizedResultCode_ShouldReturnRemoteResultCode_IfRemoteResultCodeCategoryPriorityIsHigherThanExistingResultCodeCategoryPriority() {
		int existingResultCode = ResultCode.DIAMETER_SUCCESS.code;
		int remoteResultCode1 = ResultCode.DIAMETER_AUTHENTICATION_REJECTED.code;
		int remoteResultCode2 = ResultCode.DIAMETER_OUT_OF_SPACE.code;
		
		data.getPriorityResultCodes().add(ResultCodeCategory.RC2XXX.value);
		data.getPriorityResultCodes().add(ResultCodeCategory.RC4XXX.value);
		
		assertEquals(remoteResultCode1, data.getPrioritizedResultCode(existingResultCode, remoteResultCode1));
		
		existingResultCode = remoteResultCode1;
		
		assertEquals(remoteResultCode2, data.getPrioritizedResultCode(existingResultCode, remoteResultCode2));
	}
	
	@Test
	public void testGetPrioritizedResultCode_ShouldReturnRemoteResultCode_IfRemoteResultCodeCategoryPriorityIsHigherThanExistingResultCodeCategoryPriority1() {
		int existingResultCode = ResultCode.DIAMETER_SUCCESS.code;
		int remoteResultCode1 = ResultCode.DIAMETER_AUTHENTICATION_REJECTED.code;
		int remoteResultCode2 = ResultCode.DIAMETER_OUT_OF_SPACE.code;
		int remoteResultCode3 = ResultCode.ELECTION_LOST.code;
		
		data.getPriorityResultCodes().add(ResultCodeCategory.RC2XXX.value);
		data.getPriorityResultCodes().add(ResultCodeCategory.RC4XXX.value);
		data.getPriorityResultCodes().add(ResultCode.ELECTION_LOST.code);
		
		assertEquals(remoteResultCode1, data.getPrioritizedResultCode(existingResultCode, remoteResultCode1));
		
		existingResultCode = remoteResultCode1;
		
		assertEquals(remoteResultCode2, data.getPrioritizedResultCode(existingResultCode, remoteResultCode2));
		
		existingResultCode = remoteResultCode2;
		
		assertEquals(remoteResultCode3, data.getPrioritizedResultCode(existingResultCode, remoteResultCode3));
	}
}
