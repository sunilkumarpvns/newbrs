package com.elitecore.diameterapi.core.common.transport.priority;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManagerImpl;
import com.elitecore.diameterapi.core.common.session.SessionsFactory;
import com.elitecore.diameterapi.core.common.transport.priority.PriorityEntry.DiameterSessionTypes;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionHandler;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.session.InMemorySessionFactory;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.PacketProcess;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class PriorityTableTest {

	private PriorityTable priorityTable;
	private @Mock PacketProcess packetProcess;
	private @Mock ConnectionHandler connectionHandler;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	public Object[][] dataProviderFor_test_prioritize() throws Exception {
		
		final boolean SESSION_EXIST = true;
		final boolean SESSION_NOT_EXIST = false;
		
		SessionFactoryManager sessionFactoryManager = spy(new SessionFactoryManagerImpl(new DummyStackContext(null)));
		
		//NOTE : DON'T CHANGE ENTRY LIST VALUE
		PriorityEntry[] pe = new PriorityEntry[] {
				//0
				new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager)
				.withPriority(Priority.LOW)
				.build(), 
				//1
				new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager)
				.withDiameterSessionType(DiameterSessionTypes.NEW)
				.withPriority(Priority.HIGH)
				.build(), 
				//2 
				new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager)
				.withApplicationIds("101")
				.withDiameterSessionType(DiameterSessionTypes.NEW)
				.withCommandCodes("201")
				.withPriority(Priority.LOW)
				.build(),
				//3
				new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager)
				.withApplicationIds("101,102")
				.withCommandCodes("201,202")
				.withPriority(Priority.LOW)
				.build(),
				//4
				new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager)
				.withApplicationIds("101,102,103")
				.withDiameterSessionType(DiameterSessionTypes.NEW)
				.withCommandCodes("201,202,203")
				.withPriority(Priority.HIGH)
				.build(),
				//5
				new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager)
				.withApplicationIds("101")
				.withPriority(Priority.LOW)
				.build(),
				//6
				new PriorityEntry.PriorityEntryBuilder(sessionFactoryManager)
				.withCommandCodes("201")
				.withDiameterSessionType(DiameterSessionTypes.NEW)
				.withPriority(Priority.HIGH)
				.build(),
		};
		
		Object[][] objects = new Object[][] {
				
				{
					"first entry is blank and LOW priority so always satisfy it", 
					Arrays.asList(pe[0],pe[1]),		createDiameterPacket(101, 201,"s1"), 	SESSION_EXIST, 			Priority.LOW.val,
				},	
				{
					"both entry with newSession true, but session exist so not applicable so must take default ", 
					Arrays.asList(pe[1],pe[2]),		createDiameterPacket(101, 201,"s2"), 	SESSION_EXIST, 			Priority.MEDIUM.val
				},
				{
					"both entry with newSession true, but session does not exist, both applicable so must take first in sequence",
					Arrays.asList(pe[1],pe[2]),			createDiameterPacket(101, 201,"s3"), 	SESSION_NOT_EXIST, 		Priority.HIGH.val
				},
				{	"both with newSession true, but session does not exist so applicable so must take configured in sequence ",
					Arrays.asList(pe[2],pe[1]),			createDiameterPacket(101, 201,"s4"), 	SESSION_NOT_EXIST, 		Priority.LOW.val
				},
				{
					"1st entry new Session true, 2nd entry newSessin not configured, session exist so 2nd applicable",
					Arrays.asList(pe[1],pe[3]),			createDiameterPacket(101, 201,"s5"), 	SESSION_EXIST, 			Priority.LOW.val
				},
				{
					"1st entry new Session true, 2nd entry newSessin not configured, session not exist so 1st applicable",
					Arrays.asList(pe[1],pe[3]),			createDiameterPacket(101, 201,"s6"), 	SESSION_NOT_EXIST, 		Priority.HIGH.val
				},
				{
					"1st newSession not configured, 2nd newSessin true, session not exist so 1st applicable",
					Arrays.asList(pe[3],pe[1]),			createDiameterPacket(101, 201,"s7"), 	SESSION_NOT_EXIST, 		Priority.LOW.val
				},
				{
					"both valid for packet, first in sequence will be applicable",
					Arrays.asList(pe[2],pe[4]),			createDiameterPacket(101, 201,"s8"), 	SESSION_NOT_EXIST, 		Priority.LOW.val
				},
				{
					"all entry AppId not satisfy for packet, should take default ",
					Arrays.asList(pe[2],pe[4]),			createDiameterPacket(401, 201,"s9"), 	SESSION_NOT_EXIST, 		Priority.MEDIUM.val
				},
				{
					"all entry commandCode not satisfy for packet, should take default ",
					Arrays.asList(pe[2],pe[4]),			createDiameterPacket(101, 501,"s10"), 	SESSION_NOT_EXIST, 		Priority.MEDIUM.val
				},
				{
					"all entry satisfied, first will be applicable",
					Arrays.asList(pe[4],pe[2],pe[5]),	createDiameterPacket(101, 201,"s11"), 	SESSION_NOT_EXIST, 		Priority.HIGH.val
				},
				{
					"1st entry only command code is configured, 2nd only AppId configured, 1st should be applicable",
					Arrays.asList(pe[6],pe[5]),			createDiameterPacket(101, 201,"s11"), 	SESSION_NOT_EXIST, 		Priority.HIGH.val
				},
				{
					"1st entry only AppId configured, 2nd entry only command code is configured, 1st should be applicable",
					Arrays.asList(pe[5],pe[6]),			createDiameterPacket(101, 201,"s11"), 	SESSION_NOT_EXIST, 		Priority.LOW.val
				},
				
		};
		
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

	@Test
	@Parameters(method="dataProviderFor_test_prioritize")
	public void test_prioritize(
			String caseDesc,
			List<PriorityEntry> priorityEntries,
			DiameterPacket packet,
			boolean sessionExist,
			int expected
			) throws Exception {
		
		setUpFor_test_prioritize(packet, sessionExist);
		
		priorityTable = new PriorityTable(priorityEntries);
		
		assertEquals(caseDesc,expected, priorityTable.prioritize(packetProcess));
	}

	private void setUpFor_test_prioritize(DiameterPacket packet, boolean sessionExist) throws Exception {
		
		when(packetProcess.getConnectionHandler()).thenReturn(connectionHandler);
		when(packetProcess.getPacket()).thenReturn(packet);
	}

	private DiameterPacket createDiameterPacket(int applicationId, int commandCode, String sessionId) {
		
		DiameterPacket diameterPacket = new DiameterRequest();
		diameterPacket.setApplicationID(applicationId);
		diameterPacket.setCommandCode(commandCode);
		IDiameterAVP diameterAVP = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		diameterAVP.setStringValue(sessionId);
		diameterPacket.addAvp(diameterAVP);
		
		return diameterPacket;
	}
}
