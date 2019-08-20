package com.elitecore.diameterapi.core.common.transport.priority;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManagerImpl;
import com.elitecore.diameterapi.core.common.transport.priority.PriorityEntry.DiameterSessionTypes;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class PriorityEntryTest {
	
	@BeforeClass
	public static void init() {
		DummyDiameterDictionary.getInstance();
	}

	public Object[][] dataProviderFor_test_isApplicable() throws Exception {
		final boolean SESSION_EXIST = true;
		final boolean IS_APPLICABLE = true;
		int sid=0; //used for maintain uniqueness of packet session Id
		SessionFactoryManager sessionFactoryManager = spy(new SessionFactoryManagerImpl(new DummyStackContext(null)));
		Object[][] objects =  new Object[][] {
				/* 	caseDescripyion(entry config | scenario) 
					diameterPacker, 					sessionExist, 		expected result   */				
				
				////////// SUCCESS CASES
				{ 
					"for new Session | session not found so expected true",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withDiameterSessionType(DiameterSessionTypes.NEW).build(),
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST==false,		IS_APPLICABLE
				},
				{ 
					"for existing session | session exist so expected true",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withDiameterSessionType(DiameterSessionTypes.EXISTING).build(),
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST,				IS_APPLICABLE
				},
				{ 
					"for All session | session exist so expected true",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withDiameterSessionType(DiameterSessionTypes.ALL).build(),
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST,				IS_APPLICABLE
				},
				{ 
					"for All session | session not exist so expected true",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withDiameterSessionType(DiameterSessionTypes.ALL).build(),
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST==false,		IS_APPLICABLE
				},
				{
					"Valid AppIds, all other not configured  | session not exist, expected true",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withApplicationIds("101,102").build(),
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST==false,		IS_APPLICABLE
				},
				{
					"valid command codes, all other not configured | session not exist, expected true",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withCommandCodes("201, 202").build(),
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST==false,		IS_APPLICABLE
				},
				{
					"Valid AppIds, Valid command codes, newSession not configured | sessionExist, expected true",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withApplicationIds("101,102").withCommandCodes("201,202").build(),
					createDiameterPacket(101, 201, sid++), 	SESSION_EXIST,				IS_APPLICABLE
				},
				{ 
					"Valid AppIds, Valid command codes, newSession not configured | session doesn't exist, expected true",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withApplicationIds("101,102").withCommandCodes("201,202").build(),
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST==false,		IS_APPLICABLE
				},
				{ 
					"Valid AppIds, existing session | session exist, expected true",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withApplicationIds("101,102").withDiameterSessionType(DiameterSessionTypes.EXISTING).build(),
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST,				IS_APPLICABLE
				},
				/////////	FAIL CASE
				{ 
					"for New Session | session found so expected false",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withDiameterSessionType(DiameterSessionTypes.NEW).build(), //entry
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST, 				IS_APPLICABLE==false
				},
				{ 
					"for Existing Session | session not found so expected false",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withDiameterSessionType(DiameterSessionTypes.EXISTING).build(), //entry
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST==false, 		IS_APPLICABLE==false
				},
				{ 
					"Invalid AppIds and no commandCodes, all other not configured | session not exist, expected false",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withApplicationIds("555, 666").build(),
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST==false,		IS_APPLICABLE==false
				},
				{ 
					"Valid AppIds and Invalid commandCodes, all other not configured | session not exist, expected false",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withApplicationIds("101,102").withCommandCodes("555,666").build(),
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST==false,		IS_APPLICABLE==false
				},
				{ 
					"Invalid command codes, all other not configured | session not exist, expected false",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withCommandCodes("555,666").build(),
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST==false,		IS_APPLICABLE==false
				},
				{ 
					"Invalid AppIds and valid commandCodes, all other not configured | session not exist, expected false",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withApplicationIds("555,555").withCommandCodes("201,202").build(),
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST==false,		IS_APPLICABLE==false
				},
				{ 
					"Valid AppIds, existing session | session doesn't exist, expected false",
					new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager).withApplicationIds("101,102").withDiameterSessionType(DiameterSessionTypes.EXISTING).build(),
					createDiameterPacket(101, 201,sid++), 	SESSION_EXIST==false,		IS_APPLICABLE==false
				},
		};
		
		/*
		 * Mocking getSession method of DiameterPacket
		 * 
		 * IF sessionExist parameter is true THEN
		 * 		mock getSession to return Session
		 * ELSE
		 * 		getSession returns null
		 */
		for(Object [] innerObj : objects){
			boolean sessionExist = (Boolean)innerObj[3];
			if(sessionExist == true) {
				DiameterPacket packet = (DiameterPacket) innerObj[2];
				String sessionId = packet.getAVPValue(DiameterAVPConstants.SESSION_ID);
				when(sessionFactoryManager.hasSession(sessionId, packet.getApplicationID())).thenReturn(true);
			}
		}
		
		return objects;
	}
	
	
	/**
	 *@param description format "entry configuration   |   scenario" 
	 *@param priotityEntry
	 *@param diameterPacket with unique session id
	 *@param sessionExist boolean 
	 *@param expectedResult
	 *
	 *@author chetan.sankhala
	 */
	@Test
	@Parameters(method="dataProviderFor_test_isApplicable")
	public void test_isApplicable(
			String caseDesc,
			PriorityEntry priorityEntry,
			DiameterRequest diameterPacket,
			boolean sessionExist,
			boolean expectedResult) {
		assertEquals(caseDesc, expectedResult, priorityEntry.isApplicable(diameterPacket, null));
	}

	private DiameterPacket createDiameterPacket(int applicationId, int commandCode, int sessionId) {
		DiameterPacket diameterPacket = new DiameterRequest();
		diameterPacket.setApplicationID(applicationId);
		diameterPacket.setCommandCode(commandCode);
		IDiameterAVP diameterAVP = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		diameterAVP.setStringValue(String.valueOf(sessionId));
		diameterPacket.addAvp(diameterAVP);
		
		return diameterPacket;
	}
}
