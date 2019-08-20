package com.elitecore.diameterapi.core.common.transport.priority;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManagerImpl;
import com.elitecore.diameterapi.core.common.transport.priority.PriorityEntry.DiameterSessionTypes;
import com.elitecore.diameterapi.core.common.transport.priority.PriorityEntry.PriorityEntryBuilder;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class PriorityEntryBuilderTest {

	public @Rule ExpectedException expectedException = ExpectedException.none();
	
	public Object[][] dataProviderFor_test_buid_success_cases() throws Exception {
		// applicationIds, commandCodes, ipAddressses, newSession, priority, expectedPriorityEntry
		
		SessionFactoryManager sessionFactoryManager = new SessionFactoryManagerImpl(new DummyStackContext(null));
		
		return new Object[][] {
				{
					"all empty values",sessionFactoryManager, "", "", "", DiameterSessionTypes.ALL, Priority.MEDIUM, 
						new PriorityEntry(sessionFactoryManager, 
											new int[0], 
											new int[0], 
											new InetAddress[0], 
											DiameterSessionTypes.ALL, 
											Priority.MEDIUM)  /*  Expected Entry */
				},
				{
					"all null values",sessionFactoryManager, null, null, null, DiameterSessionTypes.ALL, Priority.MEDIUM, 
						new PriorityEntry(sessionFactoryManager, 
											new int[0],
											new int[0], 
											new InetAddress[0], 
											DiameterSessionTypes.ALL, 
											Priority.MEDIUM)
				},
				
				//TODO IP address test case 
//				/* all valid values */
//				{
//					"1001,1002,1003", "201,202,203", "10.106.1.1, 10.106.1.2", true, Priority.HIGH, 
//						new PriorityEntry(dummyStackContext,
//											new int[]{1001,1002,1003}, 
//											new int[]{201,202,203},
//											new InetAddress[]{
//												InetAddress.getByName("10.106.1.1"),
//												InetAddress.getByName("10.106.1.2"),
//											}, true, Priority.HIGH)
//				},
				{
					"all valid values", sessionFactoryManager, "1001,1002,1003", "201,202,203", "", DiameterSessionTypes.ALL, Priority.HIGH, 
						new PriorityEntry(sessionFactoryManager,
											new int[]{1001,1002,1003}, 
											new int[]{201,202,203},
											new InetAddress[0], DiameterSessionTypes.ALL, Priority.HIGH)
				},
				{
					"valid + blank values", sessionFactoryManager, "1001,,1003", "201,202,", "", DiameterSessionTypes.ALL, Priority.HIGH, 
						new PriorityEntry(sessionFactoryManager,
											new int[]{1001,1003}, 
											new int[]{201,202},
											new InetAddress[0],
											DiameterSessionTypes.ALL, Priority.HIGH)
				},
				{
					"valid with extra spaces ", sessionFactoryManager, "1001  ,   1003", "201,   202  ", "", DiameterSessionTypes.ALL, Priority.HIGH, 
						new PriorityEntry(sessionFactoryManager,
											new int[]{1001,1003}, 
											new int[]{201,202},
											new InetAddress[0],
											DiameterSessionTypes.ALL, Priority.HIGH)
				}
		};
	}
	
	
	@Test
	@Parameters(method="dataProviderFor_test_buid_success_cases")
	public void  test_buid_success_cases(
		String caseDesc,
		SessionFactoryManager sessionFactoryManager,
		String appIds,
		String commandCodes,
		String ipAddresses,
		DiameterSessionTypes diameterSessionTypes,
		Priority priority,
		PriorityEntry expectedPriorityEntry) throws Exception {
		
		PriorityEntry actualPriorityEntry = new PriorityEntryBuilder( sessionFactoryManager)
											.withApplicationIds(appIds)
											.withCommandCodes(commandCodes)
											.withIpAddresses(ipAddresses)
											.withDiameterSessionType(diameterSessionTypes)
											.withPriority(priority)
											.build();
		
		ReflectionAssert.assertReflectionEquals(caseDesc, expectedPriorityEntry, actualPriorityEntry, ReflectionComparatorMode.LENIENT_ORDER);
	}
	
	public Object[][] dataProviderFor_test_build_fail_cases() {
		final String ERROR_MSG_FOR_INVALID_APPIDS = "Invalid applications-IDs: ";
		final String ERROR_MSG_FOR_INVALID_COMMANDCODES = "Invalid command code: ";
		final String INVALID_VALUE = "Invalid value: ";
		
		
		// applicationIds, commandCodes, ipAddressses, newSession, priority, error message
		return new Object[][] {
				{ 
					"invalid application Ids", "101,che,103", "", "", DiameterSessionTypes.ALL, Priority.MEDIUM, 
						ERROR_MSG_FOR_INVALID_APPIDS + "101,che,103"  + ". Reason: " + INVALID_VALUE  + "che" /*expected error message*/
				},
				{
					"invalid command code", "", "201,202,abc","", DiameterSessionTypes.ALL, Priority.MEDIUM,
						ERROR_MSG_FOR_INVALID_COMMANDCODES + "201,202,abc" + ". Reason: " + INVALID_VALUE  + "abc"
				},
//				/* invalid IP */
//				{
//					"", "", "10.106.1.1, 10.106.1.256, 10.106.1.259", false, Priority.MEDIUM,
//						INVALID_IP + "10.106.1.256"
//				},
//				/*	invalid hostname */
//				{ //TODO change host
//					"", "", "^%&.test", false, Priority.MEDIUM,
//						INVALID_IP + "^%&.test"
//				},
//				/* for IP range with all numeric value */
//				{
//					"", "", "10.106.1.1-10.106.1.256", false, Priority.MEDIUM,
//					INVALID_RANGE + "10.106.1.1-10.106.1.256. Reason: Invalid IP Range"
//				},
//				/* for IP range with string value */
//				{
//					"", "", "10.106.1.1-10.106.1.che", false, Priority.MEDIUM,
//					INVALID_RANGE + "10.106.1.1-10.106.1.che. Reason: For input string: \"che\""
//				},
				//TODO need to check mask IP
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_build_fail_cases")
	public void test_build_fail_cases(
			String caseDesc,
			String appIds,
			String commandCodes,
			String ipAddresses,
			DiameterSessionTypes diameterSessionTypes,
			Priority priority,
			String expectedErrorMessage) throws Exception {
		
		expectedException.expect(Exception.class);
		expectedException.expectMessage(expectedErrorMessage);
	
		new PriorityEntryBuilder( new SessionFactoryManagerImpl(new DummyStackContext(null)))
											.withApplicationIds(appIds)
											.withCommandCodes(commandCodes)
											.withIpAddresses(ipAddresses)
											.withDiameterSessionType(diameterSessionTypes)
											.withPriority(priority)
											.build();	
		
		//if exception not throw it show test case message
		assertTrue(caseDesc, true);
	}
}
