package com.elitecore.aaa.core.accountdata;

import java.util.Date;
import java.util.Calendar;

import org.junit.Assert;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.data.AccountData;

@RunWith(JUnitParamsRunner.class)
public class AccountDataTest {
	
	@Test
	public void testUserIdentity_ShouldHaveNULLAsDefaultValue() {
		AccountData accountData = new AccountData();
		Assert.assertEquals(null, accountData.getUserIdentity());
	}
	
	@Test
	public void testUserName_ShouldHaveNULLAsDefaultValue() {
		AccountData accountData = new AccountData();
		Assert.assertEquals(null, accountData.getUserName());
	}
	
	@Test
	public void testCheckExpirtyDateEnable_ShouldHaveFalseAsDefaultValue() {
		AccountData accountData = new AccountData();
		Assert.assertEquals(false, accountData.isExpiryDateCheckRequired());
	}

	@Test
	public void testAppend_ShouldReturnPrimaryAccountDataUnchanged_WhenSeconaryAccountDataIsNull() {
		
		AccountData primaryData = new AccountData();
		AccountData secondaryData = null;
		
		primaryData.append(secondaryData);
		
		Assert.assertEquals(primaryData, primaryData);
		Assert.assertSame(primaryData, primaryData);
	}
	
	
	public Object[][] dataFor_testAppend_ShouldReturnPrimaryAccountDataUnchanged_WhenPrimaryAccountDataIsAvailable(){
		return new Object[][]{

				// UserIdentity     // UserName
				{"testeliteaaa",	"testeliteaaa1"},
				{"testeliteaaa",	" "},
				{" ",				"testeliteaaa"},
		};
	}

	@Test
	@Parameters(method="dataFor_testAppend_ShouldReturnPrimaryAccountDataUnchanged_WhenPrimaryAccountDataIsAvailable")
	public void testAppend_ShouldReturnPrimaryAccountDataUnchanged_WhenPrimaryAccountDataIsAvailable(String userIdentity, String userName) {
		
		AccountData primaryAccountData = new AccountData();
		AccountData secondaryAccountData = new AccountData();
		
		primaryAccountData.setUserIdentity(userIdentity);
		primaryAccountData.setUserName(userName);
		
		primaryAccountData.append(secondaryAccountData);
		
		AccountData expectedAccountData = primaryAccountData;
		
		Assert.assertEquals(expectedAccountData, primaryAccountData);
		Assert.assertSame(expectedAccountData, primaryAccountData);
	}
	
	public Object[][] dataFor_testAppend_ShouldUseUserIdentityOfSecondaryData_WhenUserIdentityOfPrimaryDataIsNull(){
		return new Object[][]{
				{null},
				{" "},
				{"testeliteaaa"}
		};
	}
	
	@Test
	@Parameters(method="dataFor_testAppend_ShouldUseUserIdentityOfSecondaryData_WhenUserIdentityOfPrimaryDataIsNull")
	public void testAppend_shouldUseUserIdentityOfSecondaryData_WhenUserIdentityOfPrimaryDataIsNull(String secondaryUserIdentity) {
		
		AccountData primaryAccountData = new AccountData();

		AccountData secondaryAccountData = new AccountData();
		secondaryAccountData.setUserIdentity(secondaryUserIdentity);

		primaryAccountData.append(secondaryAccountData);

		Assert.assertEquals(secondaryUserIdentity , primaryAccountData.getUserIdentity());
	}
	
	public Object[][] dataFor_testAppend_ShouldUseUserNameOfSecondaryData_WhenUserNameOfPrimaryDataIsNull(){
		return new Object[][]{
				{null},
				{" "},
				{"testeliteaaa"}
		};
	}
	
	@Test
	@Parameters(method="dataFor_testAppend_ShouldUseUserNameOfSecondaryData_WhenUserNameOfPrimaryDataIsNull")
	public void testAppend_ShouldUseUserNameOfSecondaryData_WhenUserNameOfPrimaryDataIsNull(String secondaryUserName) {
		
		AccountData primaryAccountData = new AccountData();

		AccountData secondaryAccountData = new AccountData();
		secondaryAccountData.setUserName(secondaryUserName);

		primaryAccountData.append(secondaryAccountData);
		
		Assert.assertEquals(secondaryUserName , primaryAccountData.getUserName());
	}
	
	public Object[][] dataFor_testAppend_ShouldIgnoreUserIdentityOfSecondaryData_WhenUserIdentityOfPrimaryDataIsNonNull(){
		return new Object[][]{
				
				// Primary UserIdentity   // Secondary UserIdentity
				{" ",						"testeliteaaa1"},
				{"testeliteaaa",			" "},
				{"testeliteaaa",			null},
				{"testeliteaaa",			"testeliteaaa1"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testAppend_ShouldIgnoreUserIdentityOfSecondaryData_WhenUserIdentityOfPrimaryDataIsNonNull")
	public void testAppend_ShouldIgnoreUserIdentityOfSecondaryData_WhenUserIdentityOfPrimaryDataIsNonNull(String primaryUserIdentity, String secondaryUserIdentity) {
		
		AccountData primaryAccountData = new AccountData();
		AccountData secondaryAccountData = new AccountData();
		
		primaryAccountData.setUserIdentity(primaryUserIdentity);
		
		secondaryAccountData.setUserIdentity(secondaryUserIdentity);

		primaryAccountData.append(secondaryAccountData);

		Assert.assertEquals(primaryUserIdentity , primaryAccountData.getUserIdentity());
	}
	
	public Object[][] dataFor_testAppend_ShouldIgnoreUserNameOfSecondaryData_WhenUserNameOfPrimaryDataIsNonNull(){
		return new Object[][]{
				
				// Primary UserIdentity   // Secondary UserIdentity
				{" ",						"testeliteaaa1"},
				{"testeliteaaa",			" "},
				{"testeliteaaa",			null},
				{"testeliteaaa",			"testeliteaaa1"},
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testAppend_ShouldIgnoreUserNameOfSecondaryData_WhenUserNameOfPrimaryDataIsNonNull")
	public void testAppend_ShouldIgnoreUserNameOfSecondaryData_WhenUserNameOfPrimaryDataIsNonNull(String primaryUserName, String secondaryUserName) {
		
		AccountData primaryAccountData = new AccountData();
		AccountData secondaryAccountData = new AccountData();
		
		primaryAccountData.setUserName(primaryUserName);
		
		secondaryAccountData.setUserName(secondaryUserName);

		primaryAccountData.append(secondaryAccountData);
		
		Assert.assertEquals(primaryUserName , primaryAccountData.getUserName());
	}
	
	public Object[][] datafor_testAppend_ShouldReturnTrueValueForExpiryCheckFlag_WhenExpiryCheckFlagIsTrueEitherInPrimaryAccountDataOrInSecondaryAccountData(){
		return new Object[][]{
				//PrimaryExpiryCheckFlag      //SecondaryExpiryCheckFlag		// ExpectedExpiryCheckFlag
				{false,							false,								false},
				{false,							true,								true},
				{true,							false,								true},
				{true,							true,								true}
		};
	}
	
	@Test
	@Parameters(method = "datafor_testAppend_ShouldReturnTrueValueForExpiryCheckFlag_WhenExpiryCheckFlagIsTrueEitherInPrimaryAccountDataOrInSecondaryAccountData")
	public void testAppend_ShouldReturnTrueValueForExpiryCheckFlag_WhenExpiryCheckFlagIsTrueEitherInPrimaryAccountDataOrInSecondaryAccountData(boolean primaryExpiryCheck,boolean secondaryExpiryCheck,boolean expectedExpiryCheck) {
		
		AccountData primaryAccountData = new AccountData();
		AccountData secondaryAccountData = new AccountData();
		
		primaryAccountData.setExpiryDateCheckRequired(primaryExpiryCheck);
		secondaryAccountData.setExpiryDateCheckRequired(secondaryExpiryCheck);
		
		primaryAccountData.append(secondaryAccountData);
		
		Assert.assertEquals(expectedExpiryCheck, primaryAccountData.isExpiryDateCheckRequired());
	}
	
	public Object[][] dataFor_testAppend_ShouldReturnProperExpiryDate_WhileAppendingAccountData(){
		
		Calendar calendar = Calendar.getInstance();
		Date primaryExpiryDate = calendar.getTime();
		
		calendar.add(Calendar.DATE, 2);
		
		/**
		 * Secondary Expiry is greater than primary expiry Date.
		 */
		Date secondaryExpiryDate = calendar.getTime();
		
		calendar.add(Calendar.DATE, 2);
		
		/**
		 * maxPrimaryExpiry Date is greater than secondary expiry Date.
		 * used in case 11.
		 */
		Date maxPrimaryExpiryDate = calendar.getTime();

		Date nullPrimaryExpiryDate = null;
		Date nullSecondaryExpiryDate = null;
		
		return new Object[][]{
				
		/**
		 * ExpiryDate of AccountData remains unchanged after append operation when
		 * ExpiryDateCheck is set to false.	
		 */
		//PrimaryExpiryDateCheck  PrimaryDate    			SecondaryExpiryDateCheck  	SecondaryDate  				ExpectedExpiryDate
		{false, 				  nullPrimaryExpiryDate, 	false, 						nullSecondaryExpiryDate, 	nullPrimaryExpiryDate},
		{false, 				  primaryExpiryDate, 	    false, 					    nullSecondaryExpiryDate, 	primaryExpiryDate},
		{false, 				  nullPrimaryExpiryDate, 	false, 						secondaryExpiryDate, 	    nullPrimaryExpiryDate},
		{false, 				  primaryExpiryDate, 	    false, 					    secondaryExpiryDate, 	    primaryExpiryDate},

		
		{true,					  nullPrimaryExpiryDate, 	false, 						nullSecondaryExpiryDate, 	nullPrimaryExpiryDate},
		{true, 					  primaryExpiryDate, 		false, 						nullSecondaryExpiryDate, 	primaryExpiryDate},
		{true, 					  nullPrimaryExpiryDate, 	false, 						secondaryExpiryDate, 		nullPrimaryExpiryDate},
		{true, 					  primaryExpiryDate, 		false, 						secondaryExpiryDate, 		primaryExpiryDate},
		
		/**
		 * ExpiryDate of PrimaryAccountData will be override if ExpiryDateCheck is set to true.
		 * case:
		 * --> if both AccountData contains the ExpiryDate then took minimum case 3 and case 6.
		 * --> if one of the AccountData(primary or secondary) then override with secondaryExpiryDate
		 */
		{false, 				  nullPrimaryExpiryDate, 	true, 						nullSecondaryExpiryDate, 	nullSecondaryExpiryDate},
		// Minimum expiry date is primary so expected expiry date is = primaryExpiryDate 
		{false, 				  primaryExpiryDate, 		true, 						secondaryExpiryDate, 		primaryExpiryDate},
		{false, 				  nullPrimaryExpiryDate, 	true, 						secondaryExpiryDate, 		secondaryExpiryDate},
		{false, 				  primaryExpiryDate, 		true, 						nullSecondaryExpiryDate, 	nullSecondaryExpiryDate},

		
		{true, 					  nullPrimaryExpiryDate, 	true, 						nullSecondaryExpiryDate, 	nullSecondaryExpiryDate},
		{true, 					  primaryExpiryDate, 		true, 						nullSecondaryExpiryDate, 	nullSecondaryExpiryDate},
		// secondary expiry date is minimum so expected expiry date is = secondaryExpiryDate
		{true, 				  	  maxPrimaryExpiryDate, 	true, 						secondaryExpiryDate, 		secondaryExpiryDate},
		{true, 					  nullPrimaryExpiryDate, 	true, 						secondaryExpiryDate, 		secondaryExpiryDate}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testAppend_ShouldReturnProperExpiryDate_WhileAppendingAccountData")
	public void testAppend_ShouldReturnProperExpiryDate_WhileAppendingAccountData(boolean primaryExpiryCheck,Date primaryExpiryDate, boolean secondaryExpiryCheck,Date secondaryExpiryDate, Date expectedDate) {
		
		AccountData primaryAccountData = new AccountData();
		AccountData secondaryAccountData = new AccountData();
		
		primaryAccountData.setExpiryDateCheckRequired(primaryExpiryCheck);
		primaryAccountData.setExpiryDate(primaryExpiryDate);
		
		secondaryAccountData.setExpiryDateCheckRequired(secondaryExpiryCheck);
		secondaryAccountData.setExpiryDate(secondaryExpiryDate);
		
		primaryAccountData.append(secondaryAccountData);
		
		Assert.assertEquals(expectedDate, primaryAccountData.getExpiryDate());
	}
}