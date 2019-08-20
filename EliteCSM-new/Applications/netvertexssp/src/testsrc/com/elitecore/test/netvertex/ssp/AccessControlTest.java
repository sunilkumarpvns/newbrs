/*package testsrc.com.elitecore.test.netvertex.ssp;

import static org.junit.Assert.assertArrayEquals;

import java.util.List;

import org.junit.Test;

import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ParentalPolicy;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage;
import com.elitecore.ssp.util.ChildAccountUtility;
import com.elitecore.ssp.util.logger.Logger;

public class AccessControlTest {

	final static String MODULE = AccessControlTest.class.getSimpleName();
	
	@Test
	public void parseToDayAndTimeSlotsForEmptyConfiguration() throws Exception{
		Logger.logInfo(MODULE, "Method called : parseToDayAndTimeSlotsForEmptyConfiguration()");

		// promotionalData Array contains number of addon's you may configured
		AddOnPackage[] promotionalData = new AddOnPackage[4];
		int addOnId = 100;
		for(int i=0; i<promotionalData.length; i++){
			promotionalData[i] = new AddOnPackage();
			promotionalData[i].setAddOnPackageID((long) addOnId++);
			promotionalData[i].setDescription("I am a static Addon");
			promotionalData[i].setAddOnPackageName("Add-On"+addOnId);
			promotionalData[i].setOfferEndDate(1389294199365L);
			promotionalData[i].setPrice((long)100);
			promotionalData[i].setAddOnPackageType("AccessControl");
			promotionalData[i].setValidityPeriod(30L);
			promotionalData[i].setValidityPeriodUnit("day");
		}
		
		// "silver","gold","Tan","violet","purple","olive","teal","YellowGreen","Lime","brown","peru","gray","green","red","black","yellow","indigo","blue","cyan","magenta",		// 
		// As many number of PromotionalData you have that many number of colours you can use from above given colour list , in the given sequence only		
		
		ParentalPolicy[] expectedPolicies = new ParentalPolicy[0];
		
		String selectedColorPattern = ",,,,,,,,,,,,,,,,,,,,,,,," +
									  ",,,,,,,,,,,,,,,,,,,,,,,," +
									  ",,,,,,,,,,,,,,,,,,,,,,,," +
									  ",,,,,,,,,,,,,,,,,,,,,,,," +
									  ",,,,,,,,,,,,,,,,,,,,,,,," + 
									  ",,,,,,,,,,,,,,,,,,,,,,,," +
									  ",,,,,,,,,,,,,,,,,,,,,,,,";
		
		List<ParentalPolicy> configuredPolicies = ChildAccountUtility.parseToDayAndTimeSlots(selectedColorPattern,promotionalData);
		System.out.println("\nPolicy syntax is : days1,days1,..day7| TimePeriod |AddOn-Id");
		int counter = 0;
		for(ParentalPolicy policy : configuredPolicies){
			   System.out.println("Policy "+(++counter)+" : "+policy.getDaysOfTheWeek()+"|"+policy.getTimePeriod()+"|"+policy.getAddOnPackageID());
		}
		
		assertArrayEquals(expectedPolicies, configuredPolicies);
		
		
		System.out.println("AccessControlTest for Blank-Configuration successfully completed.\n");

	}
	   
	@Test
	public void parseToDayAndTimeSlotsForEachRowUniqueColorConfiguration() throws Exception{
		Logger.logInfo(MODULE, "Method called : parseToDayAndTimeSlotsForEachRowUniqueColorConfiguration()");

		// promotionalData Array contains number of addon's you may configured
		AddOnPackage[] promotionalData = new AddOnPackage[7];
		int addOnId = 100;
		for(int i=0; i<promotionalData.length; i++){
			promotionalData[i] = new AddOnPackage();
			promotionalData[i].setAddOnPackageID((long)addOnId++);
			promotionalData[i].setDescription("I am a static Addon");
			promotionalData[i].setAddOnPackageName("Add-On"+addOnId);
			promotionalData[i].setOfferEndDate(1389294199365L);
			promotionalData[i].setPrice(100L);
			promotionalData[i].setAddOnPackageType("AccessControl");
			promotionalData[i].setValidityPeriod(30L);
			promotionalData[i].setValidityPeriodUnit("day");
			//promotionalData[i].setAddOnSubReqID(null);
		}
		
		// "silver","gold","Tan","violet","purple","olive","teal","YellowGreen","Lime","brown","peru","gray","green","red","black","yellow","indigo","blue","cyan","magenta",		// 
		// As many number of PromotionalData you have that many number of colours you can use from above given colour list , in the given sequence only		
		
		ParentalPolicy [] expectedPolicies = new ParentalPolicy[7];
		expectedPolicies[0] = new ParentalPolicy();//100L,"1","0-23:59:59"
		expectedPolicies[0].setAddOnPackageID(100L);
		expectedPolicies[0].setDaysOfTheWeek("1");
		expectedPolicies[0].setTimePeriod("0-23:59:59");
		expectedPolicies[1] = new ParentalPolicy();
		expectedPolicies[1].setAddOnPackageID(101L);
		expectedPolicies[1].setDaysOfTheWeek("2");
		expectedPolicies[1].setTimePeriod("0-23:59:59");
		expectedPolicies[2] = new ParentalPolicy(102L,"3","0-23:59:59");
		expectedPolicies[3] = new ParentalPolicy(103L,"4","0-23:59:59");
		expectedPolicies[4] = new ParentalPolicy104L,"5","0-23:59:59");
		expectedPolicies[5] = new ParentalPolicy(105L,"6","0-23:59:59");
		expectedPolicies[6] = new ParentalPolicy(106L,"7","0-23:59:59");
		
		String selectedColorPattern = 
				"silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver," +
				"gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold,gold," +
				"Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan,Tan," +
				"violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet,violet," +
				"purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple,purple," + 
				"olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive,olive," +
				"teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal,teal" ;

		
		ParentalPolicyData[] configuredPolicies = ChildAccountUtility.parseToDayAndTimeSlots(selectedColorPattern,promotionalData);
		System.out.println("\nPolicy syntax is : days1,days1,..day7| TimePeriod |AddOn-Id");
		int counter = 0;
		for(ParentalPolicyData policy : configuredPolicies){
			   System.out.println("Policy "+(++counter)+" : "+policy.getDaysOfTheWeek()+"|"+policy.getTimePeriod()+"|"+policy.getAddOnPackageId());
		}
		
		assertArrayEquals(expectedPolicies, configuredPolicies);
		
		
		System.out.println("AccessControlTest for each row different color Configuration successfully completed.\n");

	}
	  
	
	@Test
	public void parseToDayAndTimeSlotsForFullySameColorConfiguration() throws Exception{
		Logger.logInfo(MODULE, "Method called : parseToDayAndTimeSlotsForFullySameColorConfiguration()");

		// promotionalData Array contains number of addon's you may configured
		PromotionalData[] promotionalData = new PromotionalData[4];
		int addOnId = 100;
		for(int i=0; i<promotionalData.length; i++){
			promotionalData[i] = new PromotionalData();
			promotionalData[i].setAddOnPackageId(addOnId++);
			promotionalData[i].setDescription("I am a static Addon");
			promotionalData[i].setName("Add-On"+addOnId);
			promotionalData[i].setOfferEndDate(1389294199365L);
			promotionalData[i].setPrice(100L);
			promotionalData[i].setType("AccessControl");
			promotionalData[i].setValidity(30);
			promotionalData[i].setValidityPeriod("day");
			promotionalData[i].setAddOnSubReqID(null);
		}
		
  
		// "silver","gold","Tan","violet","purple","olive","teal","YellowGreen","Lime","brown","peru","gray","green","red","black","yellow","indigo","blue","cyan","magenta",		// 
		// As many number of PromotionalData you have that many number of colours you can use from above given colour list , in the given sequence only		
		ParentalPolicyData [] expectedPolicies = new ParentalPolicyData[1];
		expectedPolicies[0] = new ParentalPolicyData(100L, "1,2,3,4,5,6,7", "0-23:59:59");
	
		// comma separated string value for 24/7 time duration with configured colour(promotionalData/AddOn) codes for respective AddOns/Promotional data
		String selectedColorPattern = 
				"silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver," +
				"silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver," +
				"silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver," +
				"silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver," +
				"silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver," + 
				"silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver," +
				"silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver,silver" ;
		
		ParentalPolicyData[] configuredPolicies = ChildAccountUtility.parseToDayAndTimeSlots(selectedColorPattern,promotionalData);
		System.out.println("\nPolicy syntax is : days1,days1,..day7| TimePeriod |AddOn-Id");
		int counter = 0;
		for(ParentalPolicyData policy : configuredPolicies){
			   System.out.println("Policy "+(++counter)+" : "+policy.getDaysOfTheWeek()+"|"+policy.getTimePeriod()+"|"+policy.getAddOnPackageId());
		}
		
		assertArrayEquals(expectedPolicies, configuredPolicies);
		
		System.out.println("AccessControlTest for fully one color Configuration successfully completed.\n");

	}
	 
	 
	@Test
	public void parseToDayAndTimeSlots() throws Exception{
		Logger.logInfo(MODULE, "Method called : parseToDayAndTimeSlots()");

		// promotionalData Array contains number of addon's you may configured
		PromotionalData[] promotionalData = new PromotionalData[4];
		int addOnId = 100;
		for(int i=0; i<promotionalData.length; i++){
			promotionalData[i] = new PromotionalData();
			promotionalData[i].setAddOnPackageId(addOnId++);
			promotionalData[i].setDescription("I am a static Addon");
			promotionalData[i].setName("Add-On"+addOnId);
			promotionalData[i].setOfferEndDate("29-SEP-12 11.59.59.000000000 PM");
			promotionalData[i].setPrice(100);
			promotionalData[i].setType("AccessControl");
			promotionalData[i].setValidity(30);
			promotionalData[i].setValidityPeriod("day");
			promotionalData[i].setAddOnSubReqID(null);
		}
		
		// "silver","gold","Tan","violet","purple","olive","teal","YellowGreen","Lime","brown","peru","gray","green","red","black","yellow","indigo","blue","cyan","magenta",		// 
		// As many number of PromotionalData you have that many number of colours you can use from above given colour list , in the given sequence only		
		
		ParentalPolicyData [] expectedPolicies = new ParentalPolicyData[4];
		expectedPolicies[0] = new ParentalPolicyData(100L, "1", "0-0:59:59");
		expectedPolicies[1] = new ParentalPolicyData(101L, "2", "1-3:59:59");
		expectedPolicies[2] = new ParentalPolicyData(102L, "2", "0-0:59:59");
		expectedPolicies[3] = new ParentalPolicyData(103L, "1", "1-1:59:59");
		
	 	String selectedColorPattern = "silver,violet,,,,,,,,,,,,,,,,,,,,,,," +
									  "Tan,gold,gold,gold,,,,,,,,,,,,,,,,,,,,," +
									  ",,,,,,,,,,,,,,,,,,,,,,,," +
									  ",,,,,,,,,,,,,,,,,,,,,,,," +
									  ",,,,,,,,,,,,,,,,,,,,,,,," + 
									  ",,,,,,,,,,,,,,,,,,,,,,,," +
									  ",,,,,,,,,,,,,,,,,,,,,,,,"; 

		ParentalPolicyData[] configuredPolicies = ChildAccountUtility.parseToDayAndTimeSlots(selectedColorPattern,promotionalData);
		
		int counter = 0;
		System.out.println("\nPolicy syntax is : days1,days1,..day7| TimePeriod |AddOn-Id");
		for(ParentalPolicyData policy : configuredPolicies){
			   System.out.println("Policy "+(++counter)+" : "+policy.getDaysOfTheWeek()+"|"+policy.getTimePeriod()+"|"+policy.getAddOnPackageId());
		}
		
		assertArrayEquals(expectedPolicies, configuredPolicies);
		
		System.out.println("AccessControlTest successfully completed.\n");
	}

	@Test
	public void parseToDayAndTimeSlotsForOneHourEachday() throws Exception{
		Logger.logInfo(MODULE, "Method called : parseToDayAndTimeSlotsForOneHourEachday()");

		// promotionalData Array contains number of addon's you may configured
		PromotionalData[] promotionalData = new PromotionalData[7];
		int addOnId = 100;
		for(int i=0; i<promotionalData.length; i++){
			promotionalData[i] = new PromotionalData();
			promotionalData[i].setAddOnPackageId(addOnId++);
			promotionalData[i].setDescription("I am a static Addon");
			promotionalData[i].setName("Add-On"+addOnId);
			promotionalData[i].setOfferEndDate("29-SEP-12 11.59.59.000000000 PM");
			promotionalData[i].setPrice(100);
			promotionalData[i].setType("AccessControl");
			promotionalData[i].setValidity(30);
			promotionalData[i].setValidityPeriod("day");
			promotionalData[i].setAddOnSubReqID(null);
		}
		
		// "silver","gold","Tan","violet","purple","olive","teal","YellowGreen","Lime","brown","peru","gray","green","red","black","yellow","indigo","blue","cyan","magenta",		// 
		// As many number of PromotionalData you have that many number of colours you can use from above given colour list , in the given sequence only		
		
		ParentalPolicyData [] expectedPolicies = new ParentalPolicyData[7];
		expectedPolicies[0] = new ParentalPolicyData(100L, "1", "0-0:59:59");
		expectedPolicies[1] = new ParentalPolicyData(101L, "2", "0-0:59:59");
		expectedPolicies[2] = new ParentalPolicyData(102L, "3", "0-0:59:59");
		expectedPolicies[3] = new ParentalPolicyData(103L, "4", "0-0:59:59");
		expectedPolicies[4] = new ParentalPolicyData(104L, "5", "0-0:59:59");
		expectedPolicies[5] = new ParentalPolicyData(105L, "6", "0-0:59:59");
		expectedPolicies[6] = new ParentalPolicyData(106L, "7", "0-0:59:59");
		
	 	String selectedColorPattern = "silver,,,,,,,,,,,,,,,,,,,,,,,," +
									  "gold,,,,,,,,,,,,,,,,,,,,,,,," +
									  "Tan,,,,,,,,,,,,,,,,,,,,,,,," +
									  "violet,,,,,,,,,,,,,,,,,,,,,,,," +
									  "purple,,,,,,,,,,,,,,,,,,,,,,,," + 
									  "olive,,,,,,,,,,,,,,,,,,,,,,,," +
									  "teal,,,,,,,,,,,,,,,,,,,,,,,,"; 

		ParentalPolicyData[] configuredPolicies = ChildAccountUtility.parseToDayAndTimeSlots(selectedColorPattern,promotionalData);
		
		int counter = 0;
		System.out.println("\nPolicy syntax is : days1,days1,..day7| TimePeriod |AddOn-Id");
		for(ParentalPolicyData policy : configuredPolicies){
			   System.out.println("Policy "+(++counter)+" : "+policy.getDaysOfTheWeek()+"|"+policy.getTimePeriod()+"|"+policy.getAddOnPackageId());
		}		
		assertArrayEquals(expectedPolicies, configuredPolicies);		
		System.out.println("AccessControlTest for One hour each day of week successfully completed.\n");
	}
	
	
}
*/