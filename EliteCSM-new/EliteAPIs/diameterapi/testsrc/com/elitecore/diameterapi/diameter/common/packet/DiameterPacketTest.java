package com.elitecore.diameterapi.diameter.common.packet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.basic.AvpInteger32;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.AvpUTF8String;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class DiameterPacketTest {
	
	static {
		DummyDiameterDictionary.getInstance();
	}
	
	private static ValueProvider defaultValueProvider = new ValueProvider() {
		
		@Override
		public String getStringValue(String identifier) {
			return identifier;
		}
	};

	@Test
	public void testZeroLengthDiameterPacket(){
		try{
			DiameterPacket diameterPacket = new DiameterRequest(false);
			diameterPacket.refreshPacketHeader();
			diameterPacket.refreshInfoPacketHeader();
			assertEquals("Diameter Packet default length MUST be 20",DiameterPacket.DEFAULT_DIAMETER_PACKET_LENGTH,diameterPacket.getLength());
			assertEquals("Diameter Packet info length MUST be 0",0,diameterPacket.getInfoLength());
		}catch(Exception e){
			e.printStackTrace();
			fail("testZeroLengthDiameterPacket failed, reason: "+e.getMessage());
		}
	}

	@Test
	public void testGetSetBytes(){
		DiameterPacket diameterPacket = new DiameterRequest();
		diameterPacket.setBytes(DiameterUtility.getBytesFromHexValue("0x01000208c0000110010000160c468aec5081bb5b00000107400000307361736e2d64656c2d332d362e6572696373736f6e2e636f6d3b313335303938303835313b3932340000011b40000015656c697465636f72652e636f6d000000000001024000000c010000160000012540000014616161532d756c7469636f6d000000374000000cd430d3730000019f4000000c00000000000001a04000000c00000001000001bb40000028000001c24000000c00000000000001bc40000014393139393130323232353331000001bb4000002c000001c24000000c00000001000001bc4000001734303431303032313230343131393700000001ca4000002c000001cb4000000c00000000000001cc40000018393233333439373032303431313237320000012280000014000000c15341504372756c650000042480000010000000c100003b980000000140000017343034313030323132303431313937000000000d80000010000028af30313030000000084000000cdfefbe3d00000015c000000d000028af030000000000001e4000001661697274656c2068616e676f7574000000000006c0000010000028af0ae2cd0100000016c0000014000028af02040401270f000100000108400000217361736e2d64656c2d332d362e6572696373736f6e2e636f6d00000000000128400000146572696373736f6e2e636f6d000001164000000c50819cbc"));
		assertArrayEquals(
				DiameterUtility.getBytesFromHexValue("0x01000208c0000110010000160c468aec5081bb5b00000107400000307361736e2d64656c2d332d362e6572696373736f6e2e636f6d3b313335303938303835313b3932340000011b40000015656c697465636f72652e636f6d000000000001024000000c010000160000012540000014616161532d756c7469636f6d000000374000000cd430d3730000019f4000000c00000000000001a04000000c00000001000001bb40000028000001c24000000c00000000000001bc40000014393139393130323232353331000001bb4000002c000001c24000000c00000001000001bc4000001734303431303032313230343131393700000001ca4000002c000001cb4000000c00000000000001cc40000018393233333439373032303431313237320000012280000014000000c15341504372756c650000042480000010000000c100003b980000000140000017343034313030323132303431313937000000000d80000010000028af30313030000000084000000cdfefbe3d00000015c000000d000028af030000000000001e4000001661697274656c2068616e676f7574000000000006c0000010000028af0ae2cd0100000016c0000014000028af02040401270f000100000108400000217361736e2d64656c2d332d362e6572696373736f6e2e636f6d00000000000128400000146572696373736f6e2e636f6d000001164000000c50819cbc"), 
				diameterPacket.getBytes());
	}
	
	@Test
	public void testGetSetAttribute(){
		try{			
			final String DEFAULT_USER_NAME = "eliteaaa";
			DiameterPacket diameterPacket = new DiameterRequest();
			AvpUTF8String strAttribute = new AvpUTF8String(DiameterAVPConstants.USER_NAME_INT,0,(byte)0,DiameterAVPConstants.USER_NAME,"yes");
			strAttribute.setStringValue(DEFAULT_USER_NAME);
			diameterPacket.addAvp(strAttribute);
			diameterPacket.refreshPacketHeader();
			IDiameterAVP diameterAVP = diameterPacket.getAVP(DiameterAVPConstants.USER_NAME);
			if(diameterAVP == null)
				fail("testGetSetAttribute failed, reason : AVP returns is null");
			assertEquals("Diameter Packet resultant AVP value must be " + DEFAULT_USER_NAME,DEFAULT_USER_NAME,diameterAVP.getStringValue());
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetAttribute failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testContainsAttribute(){
		try{			
			final String DEFAULT_USER_NAME = "eliteaaa";
			DiameterPacket diameterPacket = new DiameterRequest();
			AvpUTF8String strAttribute = new AvpUTF8String(DiameterAVPConstants.USER_NAME_INT,0,(byte)0,DiameterAVPConstants.USER_NAME,"yes");
			strAttribute.setStringValue(DEFAULT_USER_NAME);
			diameterPacket.addAvp(strAttribute);
			diameterPacket.refreshPacketHeader();
			if(!diameterPacket.containsAVP(strAttribute)){
				fail("testGetSetAttribute failed, reason : AVP returns is null");
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetAttribute failed, reason: "+e.getMessage());			
		}
	}

	@Test
	public void testGetSetVSA(){
		try{
			final int DEFAULT_VALUE = 10;			
			DiameterPacket diameterPacket = new DiameterRequest();
			//<attribute id="2" name="3GPP-Charging-ID" mandatory="yes" protected="yes"  encryption="no" type="Unsigned32"/>
			AvpInteger32 intAVP = new AvpInteger32(2,10415,(byte)0,DiameterAVPConstants.TGPP_CHARGING_ID,"no");
			intAVP.setInteger(DEFAULT_VALUE);
			
			diameterPacket.addAvp(intAVP);

			IDiameterAVP diameterAVP = diameterPacket.getAVP(DiameterAVPConstants.TGPP_CHARGING_ID);
			if(diameterAVP == null)
				fail("testGetSetVSA failed, reason : attribute returns is null");
			assertEquals("Diameter Packet resultant AVP value must be " +DEFAULT_VALUE,String.valueOf(DEFAULT_VALUE),diameterAVP.getStringValue());
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetVSA failed, reason: "+e.getMessage());					
		}
	}

	@Test
	public void testGetSetInfoAttr(){
		try{
			DiameterPacket diameterPacket = new DiameterRequest();
			
			//	<attribute id="65536" name="EC-Diameter-version"  mandatory="yes" protected="yes"  encryption="no" type="Integer32"/>
			AvpInteger32 intAVP = new AvpInteger32(65536,21067,(byte)0,DiameterAVPConstants.EC_DIAMETER_VERSION,"no");
			intAVP.setInteger(diameterPacket.getVersion());
			

			diameterPacket.addInfoAvp(intAVP);
			diameterPacket.refreshPacketHeader();
			IDiameterAVP diameterAVP = diameterPacket.getAVP(DiameterAVPConstants.EC_DIAMETER_VERSION);
			if(diameterAVP != null)
				fail("testGetSetInfoAttr failed, reason : avp should be null");
			diameterAVP = diameterPacket.getAVP(DiameterAVPConstants.EC_DIAMETER_VERSION,false);
			if(diameterAVP != null)
				fail("testGetSetInfoAttr failed, reason : avp should be null");			
			diameterAVP = diameterPacket.getAVP(DiameterAVPConstants.EC_DIAMETER_VERSION,true);
			if(diameterAVP == null)
				fail("testGetSetInfoAttr failed, reason : avp returns is null");
			assertEquals("Diameter Packet resultant AVP value must be " + diameterPacket.getVersion(),diameterPacket.getVersion(),diameterAVP.getInteger());			
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetInfoAttr failed, reason: "+e.getMessage());					
		}

	}
	
	@Test	
	public void testContainsInfoAttribute(){
		try{
			DiameterPacket diameterPacket = new DiameterRequest();
			
			//	<attribute id="65536" name="EC-Diameter-version"  mandatory="yes" protected="yes"  encryption="no" type="Integer32"/>
			AvpInteger32 intAVP = new AvpInteger32(65536,21067,(byte)0,DiameterAVPConstants.EC_DIAMETER_VERSION,"no");
			intAVP.setInteger(diameterPacket.getVersion());
			

			diameterPacket.addInfoAvp(intAVP);
			diameterPacket.refreshPacketHeader();
			if(diameterPacket.containsInfoAVP(intAVP) == false) {
				fail("testContainsInfoAttribute failed, Reason : must result true as AVP was present.");
			}
			if(diameterPacket.containsAVP(intAVP, true) == false) {
				fail("testContainsInfoAttribute failed, Reason : must result true as AVP was present.");
			}
			if(diameterPacket.containsAVP(intAVP, false)) {
				fail("testContainsInfoAttribute failed, reason : must result false as Info AVP was not present.");
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetInfoAttr failed, reason: "+e.getMessage());					
		}

	}

	@Test
	public void testRefreshPacketHeader(){
		try{
			final String DEFAULT_USER_NAME = "eliteaaa";
			final int EXPECTED_LENGTH = 36;
			DiameterRequest diameterPacket = new DiameterRequest(false);
			diameterPacket.refreshPacketHeader();
			diameterPacket.refreshInfoPacketHeader();
			assertEquals("Diameter Packet default length MUST be 20",DiameterPacket.DEFAULT_DIAMETER_PACKET_LENGTH,diameterPacket.getLength());
			assertEquals("Diameter Packet info length MUST be 0",0,diameterPacket.getInfoLength());
			AvpUTF8String strAvp = new AvpUTF8String(DiameterAVPConstants.USER_NAME_INT,0,(byte)0,DiameterAVPConstants.USER_NAME,"yes");
			strAvp.setStringValue(DEFAULT_USER_NAME);
			diameterPacket.addAvp(strAvp);
			diameterPacket.refreshPacketHeader();
			assertEquals("Diameter Packet default length MUST be "+EXPECTED_LENGTH,EXPECTED_LENGTH,diameterPacket.getLength());
			assertEquals("Diameter Packet info length MUST be 0",0,diameterPacket.getInfoLength());
		}catch(Exception e){
			e.printStackTrace();
			fail("testRefreshPacketHeader failed, reason: "+e.getMessage());					
		}

	}

	@Test
	public void testRefreshInfoPacketHeader(){
		try{
			final String DEFAULT_USER_NAME = "eliteaaa";
			final int EXPECTED_LENGTH = 16;
			DiameterRequest diameterPacket = new DiameterRequest(false);
			diameterPacket.refreshPacketHeader();
			diameterPacket.refreshInfoPacketHeader();
			
			assertEquals("Diameter Packet default length MUST be 20",DiameterPacket.DEFAULT_DIAMETER_PACKET_LENGTH,diameterPacket.getLength());
			assertEquals("Diameter Packet info length MUST be 0",0,diameterPacket.getInfoLength());
			AvpUTF8String strAvp = new AvpUTF8String(DiameterAVPConstants.USER_NAME_INT,0,(byte)0,DiameterAVPConstants.USER_NAME,"yes");
			strAvp.setStringValue(DEFAULT_USER_NAME);
			
			diameterPacket.addInfoAvp(strAvp);
			diameterPacket.refreshInfoPacketHeader();
			assertEquals("Diameter Packet default length MUST be "+DiameterPacket.DEFAULT_DIAMETER_PACKET_LENGTH,DiameterPacket.DEFAULT_DIAMETER_PACKET_LENGTH,diameterPacket.getLength());
			assertEquals("Diameter Packet info length MUST be "+EXPECTED_LENGTH,EXPECTED_LENGTH,diameterPacket.getInfoLength());
		}catch(Exception e){
			e.printStackTrace();
			fail("testRefreshInfoPacketHeader failed, reason: "+e.getMessage());					
		}

	}

	@Test
	public void testGetSetCommandType(){
		try{			 
			final int NEGATIVE_TYPE = -1;
			DiameterRequest diameterPacket = new DiameterRequest();
			diameterPacket.setCommandCode(CommandCode.CAPABILITIES_EXCHANGE.code);
			assertEquals("Diameter Packet Command-Code MUST be "+ CommandCode.CAPABILITIES_EXCHANGE.code,CommandCode.CAPABILITIES_EXCHANGE.code,diameterPacket.getCommandCode());
			diameterPacket.setCommandCode(NEGATIVE_TYPE);
			assertEquals("Diameter Packet Command-Code MUST be "+NEGATIVE_TYPE,NEGATIVE_TYPE,diameterPacket.getCommandCode());
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetPacketType failed, reason: "+e.getMessage());					
		}

	}

	@Test
	public void testGetSetHopByHopIdentifier(){
		try{
			final int DEFAULT_IDENTIFIER = 1;
			final int NEGATIVE_IDENTIFIER = -1;
			DiameterRequest diameterPacket = new DiameterRequest();
			diameterPacket.setHop_by_hopIdentifier(DEFAULT_IDENTIFIER);
			assertEquals("Diameter Packet Hop-by-Hop-Identifier MUST be "+ DEFAULT_IDENTIFIER,DEFAULT_IDENTIFIER,diameterPacket.getHop_by_hopIdentifier());
			diameterPacket.setHop_by_hopIdentifier(NEGATIVE_IDENTIFIER);
			assertEquals("Diameter Packet Hop-by-Hop-Identifier MUST be "+ NEGATIVE_IDENTIFIER,NEGATIVE_IDENTIFIER,diameterPacket.getHop_by_hopIdentifier());
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetIdentifier failed, reason: "+e.getMessage());					
		}

	}
	
	@Test
	public void testGetSetEndToEndIdentifier(){
		try{
			final int DEFAULT_IDENTIFIER = 1;
			final int NEGATIVE_IDENTIFIER = -1;
			DiameterRequest diameterPacket = new DiameterRequest();
			diameterPacket.setEnd_to_endIdentifier(DEFAULT_IDENTIFIER);
			assertEquals("Diameter Packet End-To-End Identifier MUST be "+ DEFAULT_IDENTIFIER,DEFAULT_IDENTIFIER,diameterPacket.getEnd_to_endIdentifier());
			diameterPacket.setEnd_to_endIdentifier(NEGATIVE_IDENTIFIER);
			assertEquals("Diameter Packet End-To-End Identifier MUST be "+ NEGATIVE_IDENTIFIER,NEGATIVE_IDENTIFIER,diameterPacket.getEnd_to_endIdentifier());
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetIdentifier failed, reason: "+e.getMessage());					
		}

	}
	
	@Test
	public void testClone(){
		try{
			final String DEFAULT_USER_NAME = "eliteaaa";
			final String DEFAULT_INFO_USER_NAME = "eliteaaaInfo";
			final int DEFAULT_INT_AVP_VALUE = 1;
			DiameterPacket diameterPacket = new DiameterRequest(false);

			AvpUTF8String strAVP = new AvpUTF8String(DiameterAVPConstants.USER_NAME_INT,0,(byte)0,DiameterAVPConstants.USER_NAME,"yes");
			strAVP.setStringValue(DEFAULT_USER_NAME);
			diameterPacket.addAvp(strAVP);
			
			diameterPacket.refreshPacketHeader();
			
			AvpUTF8String strInfoAVP = new AvpUTF8String(DiameterAVPConstants.USER_NAME_INT,0,(byte)0,DiameterAVPConstants.USER_NAME,"yes");
			strInfoAVP.setStringValue(DEFAULT_INFO_USER_NAME);
			diameterPacket.addInfoAvp(strInfoAVP);
			
			diameterPacket.refreshInfoPacketHeader();


			DiameterPacket clonePacket = (DiameterPacket) diameterPacket.clone();
			IDiameterAVP userName = clonePacket.getAVP(DiameterAVPConstants.USER_NAME);
			if(userName == null)
				fail("testClone failed, reason : attribute returns is null");
			assertEquals("Diameter Packet resultant attribute value must be " + DEFAULT_USER_NAME,DEFAULT_USER_NAME,userName.getStringValue());

			IDiameterAVP userInfoName = clonePacket.getInfoAVP(DiameterAVPConstants.USER_NAME);
			if(userInfoName == null)
				fail("testClone failed, reason : info AVP returns is null");
			assertEquals("Diameter Packet resultant AVP value must be " + DEFAULT_INFO_USER_NAME,DEFAULT_INFO_USER_NAME,userInfoName.getStringValue());
			
			AvpUTF8String intAVP = new AvpUTF8String(DiameterAVPConstants.FIRMWARE_REVISION_INT,0,(byte)0,DiameterAVPConstants.FIRMWARE_REVISION,"yes");
			intAVP.setInteger(DEFAULT_INT_AVP_VALUE);
			diameterPacket.addAvp(intAVP);
			diameterPacket.refreshPacketHeader();
			
			IDiameterAVP tempIntAvp = clonePacket.getAVP(DiameterAVPConstants.FIRMWARE_REVISION);
			if(tempIntAvp!=null)
				fail("testClone failed, reason : AVP added to original packet found in cloned Packet");
			tempIntAvp =  diameterPacket.getAVP(DiameterAVPConstants.FIRMWARE_REVISION);
			if(tempIntAvp==null)
				fail("testClone failed, reason : AVP added to original packet not found");


		}catch(Exception e){
			e.printStackTrace();
			fail("testClone failed, reason: "+e.getMessage());
		}
	}
	
	@Test
	public void testTouch(){
		DiameterPacket request = new DiameterRequest();
		long msCreationTime = request.creationTimeMillis();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		request.touch();
		long msUpdatedTime = request.creationTimeMillis();
		assertTrue(msUpdatedTime > msCreationTime);
	}
	
	@Test
	@Parameters(method = "dataProviderFor_testRemoveAVP")
	public void testRemoveAVP(List<IDiameterAVP> avpList, IDiameterAVP avpToRemove, int removedCount){
		DiameterPacket request = new DiameterRequest();
		request.addAvps(avpList);
		assertEquals(removedCount, request.removeAVP(avpToRemove));
		if(removedCount > 0){
			assertNull(request.getAVP(avpToRemove.getAVPId()));
		}
	}
	
	public static Object[][] dataProviderFor_testRemoveAVP() throws Exception {
		return new Object[][]{
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.createAvp("0:416", "1"), 
				1},
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.createAvp("0:415", "1"),
				1},
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.createAvp("0:263", "1"),
				0},
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.createAvp("0:415", "2"),
				0}
		};
	}
	
	@Test
	@Parameters(method = "dataProviderFor_testRemoveInfoAVP")
	public void testRemoveInfoAVP(List<IDiameterAVP> infoAvpList, 
			List<IDiameterAVP> packetAvpList, 
			IDiameterAVP avpToRemove, int removedCount){
		
		DiameterPacket request = new DiameterRequest();
		request.addAvps(packetAvpList);
		
		for(IDiameterAVP diameterAvp : infoAvpList){
			request.addInfoAvp(diameterAvp);
		}
		
		assertEquals(removedCount, request.removeInfoAVP(avpToRemove));
		if(removedCount > 0){
			assertNull(request.getInfoAVP(avpToRemove.getAVPId()));
		}
		
		for(IDiameterAVP avp : packetAvpList){
			assertTrue(request.containsAVP(avp));
		}
	}
	
	public static Object[][] dataProviderFor_testRemoveInfoAVP() throws Exception {
		return new Object[][]{
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.createAvp("0:416", "1"), 
				1},
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.getDiameterAttributes("0:1=username", defaultValueProvider),
				DiameterUtility.createAvp("21067:65536", "1"),
				0},
		};
	}

	
	@Test
	@Parameters(method = "dataProviderFor_testRemoveAVP_Include_Info_AVP")
	public void testRemoveAVP_Include_Info_AVP(List<IDiameterAVP> infoAvpList, 
			List<IDiameterAVP> packetAvpList, 
			IDiameterAVP avpToRemove, int removedCount){
		
		DiameterPacket request = new DiameterRequest();
		request.addAvps(packetAvpList);
		
		for(IDiameterAVP diameterAvp : infoAvpList){
			request.addInfoAvp(diameterAvp);
		}
		
		assertEquals(removedCount, request.removeAVP(avpToRemove, DiameterPacket.INCLUDE_INFO_ATTRIBUTE));
		if(removedCount > 0){
			assertNull(request.getInfoAVP(avpToRemove.getAVPId()));
		}
		
	}
	
	public static Object[][] dataProviderFor_testRemoveAVP_Include_Info_AVP() throws Exception {
		return new Object[][]{
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.createAvp("0:416", "1"), 
				2},
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.getDiameterAttributes("0:415=1", defaultValueProvider),
				DiameterUtility.createAvp("0:416", "1"),
				1},
			{ DiameterUtility.getDiameterAttributes("0:415=1", defaultValueProvider),
				DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.createAvp("0:416", "1"),
				1},
		};
	}


	@Test
	@Parameters(method = "dataProviderFor_testGetAVP")
	public void testGetAVP(List<IDiameterAVP> avpList,
			IDiameterAVP avpToGet,
			String id){
		DiameterPacket request = new DiameterRequest();
		request.addAvps(avpList);
		IDiameterAVP avp = request.getAVP(id);
		assertEquals(avpToGet, avp);
		
	}
	
	public static Object[][] dataProviderFor_testGetAVP() throws Exception {
		return new Object[][]{
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.createAvp("0:416", "1"), 
				"0:416" },
				
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				null,
				null },
				
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				null,
				"0:268" },
		
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.createAvp("0:415", "1"), 
				"0:415" },
				
			{ DiameterUtility.getDiameterAttributes("0:456={'0:268'='5021'},0:268=2001", defaultValueProvider),
				DiameterUtility.createAvp("0:268", "5021"), 
				"0:456.0:268" },
				
			{ DiameterUtility.getDiameterAttributes("0:456={'0:268'='5021'},0:268=2001", defaultValueProvider),
				DiameterUtility.createAvp("0:268", "2001"), 
				"0:268" },
			
			{ DiameterUtility.getDiameterAttributes("0:456={'0:431'={'0:421'='123'}},0:268=2001", defaultValueProvider),
				DiameterUtility.createAvp("0:421", "123"), 
				"0:456.0:431.0:421" }
		};
	}
	
	@Test
	@Parameters(method = "dataProviderFor_testGetAVPList")
	public void testGetAVPList(List<IDiameterAVP> avpList,
			List<IDiameterAVP> avpsToGet,
			String id){
		DiameterPacket request = new DiameterRequest();
		request.addAvps(avpList);
		List<IDiameterAVP> avpsFound = request.getAVPList(id);
		if(avpsToGet == null){
			assertNull(avpsFound);
		} else {
			assertArrayEquals(avpsToGet.toArray(), avpsFound.toArray());
		}
		
	}
	
	public static Object[][] dataProviderFor_testGetAVPList() throws Exception {
		return new Object[][]{
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.getDiameterAttributes("0:416=1", defaultValueProvider), 
				"0:416" },
				
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1,0:416=2", defaultValueProvider),
				DiameterUtility.getDiameterAttributes("0:416=1,0:416=2", defaultValueProvider), 
				"0:416" },
				
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1,0:416=2", defaultValueProvider),
				null,
				"0:268" },
		
			{ DiameterUtility.getDiameterAttributes("0:415=1,0:416=1", defaultValueProvider),
				DiameterUtility.getDiameterAttributes("0:415=1", defaultValueProvider), 
				"0:415" },
				
			{ DiameterUtility.getDiameterAttributes("0:456={'0:268'='5021'},0:268=2001", defaultValueProvider),
				DiameterUtility.getDiameterAttributes("0:268=5021", defaultValueProvider), 
				"0:456.0:268" },
			
			{ DiameterUtility.getDiameterAttributes("0:456={'0:268'='5021'},0:456={'0:268'='3010'},0:268=2001", defaultValueProvider),
				DiameterUtility.getDiameterAttributes("0:268=5021,0:268=3010", defaultValueProvider), 
				"0:456.0:268" },
				
			{ DiameterUtility.getDiameterAttributes("0:456={'0:268'='5021'},0:268=2001", defaultValueProvider),
				DiameterUtility.getDiameterAttributes("0:268=2001", defaultValueProvider),
				"0:268" },
			
			{ DiameterUtility.getDiameterAttributes("0:456={'0:431'={'0:421'='123'}},0:268=2001", defaultValueProvider),
				DiameterUtility.getDiameterAttributes("0:421=123", defaultValueProvider), 
				"0:456.0:431.0:421" },
			
			{ DiameterUtility.getDiameterAttributes("0:456={'0:431'={'0:421'='123'}},0:268=2001", defaultValueProvider),
				null,
				"0:456.0:268" },
			
			{ DiameterUtility.getDiameterAttributes("0:456={'0:431'={'0:421'='123'}},0:456={'0:431'={'0:421'='456'}},0:268=2001", defaultValueProvider),
				DiameterUtility.getDiameterAttributes("0:421=123,0:421=456", defaultValueProvider), 
				"0:456.0:431.0:421" },
				
			{ DiameterUtility.getDiameterAttributes("0:268=2001,0:456={'0:431'={'0:421'='123'}},0:456={'0:431'={'0:421'='456'}}", defaultValueProvider),
				null,
				"0:268.0:431.0:421" },
			
			{ DiameterUtility.getDiameterAttributes("0:456={'0:268'='2001';'0:431'={'0:421'='123'}},0:456={'0:268'='2001';'0:431'={'0:421'='456'}}", defaultValueProvider),
				null,
				"0:431.0:268.0:421" },
			
			{ DiameterUtility.getDiameterAttributes("0:456={'0:268'='2001';'0:431'={'0:421'='123'}},0:456={'0:268'='2001';'0:431'={'0:421'='456'}}", defaultValueProvider),
				null,
				null }
		};
	}
	
	public Object[][] dataFor_isServerInitiated_ServerInitiatedCodes() {
		return new Object[][] {
			{CommandCode.RE_AUTHORIZATION},
			{CommandCode.PUSH_PROFILE},
			{CommandCode.ABORT_SESSION}
		};
	}
	
	public Object[][] dataFor_isServerInitiated_ClientInitiatedCodes() {
		return new Object[][] {
			{CommandCode.ACCOUNTING},
			{CommandCode.AUTHENTICATION_AUTHORIZATION},
			{CommandCode.SESSION_TERMINATION}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_isServerInitiated_ServerInitiatedCodes")
	public void testIsServerInitiatedReturnsTrueForServerInitiatedCommandCodes(CommandCode commandCode) {
		DiameterPacket packet = new DiameterPacket() {
			
			@Override
			public DiameterRequest getAsDiameterRequest() {
				return null;
			}
			
			@Override
			public DiameterAnswer getAsDiameterAnswer() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		packet.setCommandCode(commandCode.code);
		assertTrue(packet.isServerInitiated());
	}
	
	@Test
	@Parameters(method = "dataFor_isServerInitiated_ClientInitiatedCodes")
	public void testIsServerInitiatedReturnsFalseForClientInitiatedCommandCodes(CommandCode commandCode) {
		DiameterPacket packet = new DiameterPacket() {
			
			@Override
			public DiameterRequest getAsDiameterRequest() {
				return null;
			}
			
			@Override
			public DiameterAnswer getAsDiameterAnswer() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		packet.setCommandCode(commandCode.code);
		assertFalse(packet.isServerInitiated());
	}
}

