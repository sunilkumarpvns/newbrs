package com.elitecore.coreeap.packet;


import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elitecore.coreeap.packet.types.tls.TLSEAPType;
import com.elitecore.coreeap.packet.types.tls.record.TLSPlaintext;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class EAPPacketTest {

	@Rule public ExpectedException exception = ExpectedException.none();

	// Test parsePacket method of EAPPacket
	// Test Cases:
	//	1. null packet
	//	2. value of length < 4
	//	3. invalid code
	//	4. disputable length for a particular code; e.g. code = 3, length > 4
	//	5. value of length field more than actual length of the packet
	//	6. valid EAP packet
	@Test
	public void testParsePacket_ShouldThrowInvalidEAPPacketException_IfPacketIsNull() throws InvalidEAPPacketException{

		byte[] eapPacketBytes = null;

		exception.expect(InvalidEAPPacketException.class);

		//Test Case: null packet
		new EAPPacket(eapPacketBytes);
	}

	@Test
	public void testParsePacket_ShouldThrowInvalidEAPPacketException_IfValueOfLengthFieldIsLessThan4() throws InvalidEAPPacketException {
		byte[] eapPacketBytes = null;
		eapPacketBytes = new byte[4];

		eapPacketBytes[0] = (byte)1;
		eapPacketBytes[1] = (byte)1;
		eapPacketBytes[3] = (byte)3;
		eapPacketBytes[2] = (byte)(3 >>> 8);

		exception.expect(InvalidEAPPacketException.class);

		new EAPPacket(eapPacketBytes);			
	}

	@Test
	public void testParsePacket_ShouldThrowInvalidEAPPacketException_IfCodeIsInvalid() throws InvalidEAPPacketException {
		byte[] eapPacketBytes = null;
		eapPacketBytes = new byte[4];

		eapPacketBytes[0] = (byte)12;
		eapPacketBytes[1] = (byte)1;
		eapPacketBytes[3] = (byte)3;
		eapPacketBytes[2] = (byte)(3 >>> 8);

		exception.expect(InvalidEAPPacketException.class);

		new EAPPacket(eapPacketBytes);
	}

	@Test
	public void testParsePacket_ShouldThrowINvalidEAPPacketException_IfLengthIsNotProperForTheCode() throws InvalidEAPPacketException {
		byte[] eapPacketBytes = null;
		eapPacketBytes = new byte[4];

		eapPacketBytes[0] = (byte)3;
		eapPacketBytes[1] = (byte)1;
		eapPacketBytes[3] = (byte)5;
		eapPacketBytes[2] = (byte)(5 >>> 8);

		exception.expect(InvalidEAPPacketException.class);

		new EAPPacket(eapPacketBytes);
	}		

	@Test
	public void testParsePacket_ShouldThrowInvalidEAPPacketException_IfValueOfLengthFieldDoesNotMatchTheActualLengthOfPacket() throws InvalidEAPPacketException {
		byte[] eapPacketBytes = null;
		eapPacketBytes = new byte[8];

		eapPacketBytes[0] = (byte)1;
		eapPacketBytes[1] = (byte)1;
		eapPacketBytes[3] = (byte)16;
		eapPacketBytes[2] = (byte)(16 >>> 8);
		eapPacketBytes[4] = (byte)13;
		eapPacketBytes[5] = (byte)32;
		eapPacketBytes[6] = (byte)0;
		eapPacketBytes[7] = (byte)1;

		exception.expect(InvalidEAPPacketException.class);

		new EAPPacket(eapPacketBytes);
	}

	@Test
	public void testParsePacket_ShouldParsePacketSuccessfully_IfValidBytesAreProvided() throws InvalidEAPPacketException {
		byte[] eapPacketBytes = null;
		//Test Case: valid EAP packet
		eapPacketBytes = new byte[6];

		eapPacketBytes[0] = (byte)1;
		eapPacketBytes[1] = (byte)1;
		eapPacketBytes[3] = (byte)6;
		eapPacketBytes[2] = (byte)(6 >>> 8);
		eapPacketBytes[4] = (byte)13;
		eapPacketBytes[5] = (byte)32;

		new EAPPacket(eapPacketBytes);
	}

	// Test get/setCode method of EAPPacket
	// Test Cases:
	//	1. invalid code
	//	2. valid code
	@Test
	public void testSetCode_ShouldThrowInvalidEAPPacketException_IfInvalidCodeIsAttemptedToBeSet() throws InvalidEAPPacketException{
		EAPPacket eapPacket = new EAPPacket(1);

		exception.expect(InvalidEAPPacketException.class);
		// Test Case: invalid code
		eapPacket.setCode(12);
	}

	@Test
	public void testGetCode_ShouldReturnTheCodeSet() throws InvalidEAPPacketException {
		//Test Case: valid code
		int iCode = 2;
		EAPPacket eapPacket = new EAPPacket(1);
		eapPacket.setCode(iCode);

		assertEquals(eapPacket.getCode(), iCode);
	}

	// Test get/setIdentifier method of EAPPacket
	// Test Cases:
	//	1. invalid identifier
	//	2. valid identifier
	@Test
	public void testSetIdentifier_ShouldThrowInvalidEAPPacketException_IfInvalidIdentifierIsAttemptedToBeSet() throws InvalidEAPPacketException{
		EAPPacket eapPacket = new EAPPacket(1);

		exception.expect(InvalidEAPPacketException.class);

		eapPacket.setIdentifier(1112);
	}	

	@Test
	public void testGetIdentifier_ShouldReturnTheIdentifierSet() throws InvalidEAPPacketException {
		//Test Case: valid identifier
		int iIdentifier = 2;
		EAPPacket eapPacket = new EAPPacket(1);
		eapPacket.setIdentifier(iIdentifier);
		assertEquals(eapPacket.getIdentifier(), iIdentifier);
	}

	// Test get/setLength method of EAPPacket
	@Test
	public void testGetSetLength(){
		EAPPacket eapPacket = new EAPPacket(1);

		int iLength = 34;
		eapPacket.setLength(iLength);
		assertEquals(eapPacket.getLength(), iLength);
	}		

	@Test
	public void testTLSType() throws InvalidEAPPacketException{

		//with more than one compression methods (failed in past- issue was raised)
		byte[] eapPacketBytes = TLSUtility.HexToBytes("0202006b158000000061160301005c010000580301507f4ca09f693bd1ba10fd04df1466684bf2053eac2f094c235ed6ce26016ee200002a00390038003500160013000a00330032002f0007000500040015001200090014001100080006000300ff020100000400230000");

		EAPPacket eapPacket = new EAPPacket(eapPacketBytes);

		List<byte[]> list = ((TLSEAPType)eapPacket.getEAPType()).getTLSRecords();

		for(byte[] bytes : list){
			TLSPlaintext tlsRecord = new TLSPlaintext();
			tlsRecord.setBytes(bytes);
		}
	}
}