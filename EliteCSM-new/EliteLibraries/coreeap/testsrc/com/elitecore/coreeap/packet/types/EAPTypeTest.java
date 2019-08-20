package com.elitecore.coreeap.packet.types;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elitecore.coreeap.packet.InvalidEAPTypeException;

public class EAPTypeTest {

	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testParseBytes_ShouldThrowInvalidEAPTypeExceptionException_IfEapTypeIsNull() throws InvalidEAPTypeException{
		byte[] eapTypeBytes = null;
	
		exception.expect(InvalidEAPTypeException.class);
		
		// Test Case: null eap type
		new EAPType(eapTypeBytes);
			
	}
	
	@Test
	public void testParseBytes_ShouldThrowIllegalArgumentException_IfLengthOfDataBytesOfEapTypeIsLessThanOne() throws InvalidEAPTypeException {
		byte[] eapTypeBytes = null;
		// Test Case: length of data of eap type less than 2
		eapTypeBytes = new byte[1];
		eapTypeBytes[0] = 4;
		
		exception.expect(IllegalArgumentException.class);
		
		new EAPType(eapTypeBytes);
	}	
}
