package com.elitecore.commons.base;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class BytesTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testToInt_ShouldThrowNullPointerException_IfBytesPassedAreNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("bytes are null");
		
		Bytes.toInt(null);
	}
	
	@Test
	@Parameters({"5","6","10"})
	public void testToInt_ShouldThrowIllegalArgumentException_IfMoreThanFourBytesArePassed(int bufferSize){
		byte[] buffer = createBuffer(bufferSize);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("byteCount should be in closed-range [0:4], found: " + bufferSize);
		
		Bytes.toInt(buffer);
	}
	
	@Test
	@Parameters(method = "dataFor_testToInt_ShouldConvertBytesPassedToInt")
	public void testToInt_ShouldConvertBytesPassedToInt(byte[] buffer, int expectedInt){
		assertEquals(expectedInt, Bytes.toInt(buffer));
	}
	
	public Object[][] dataFor_testToInt_ShouldConvertBytesPassedToInt() {
		return new Object[][]{
				//buffer,								expectedValue
				$(new byte[]{},							0),
				$(new byte[]{0},						0),
				$(new byte[]{0,1},						1),
				$(new byte[]{1,0},						256),
				$(new byte[]{0,0,1,0},					256),
				$(new byte[]{0,0,1,2},					258),
				$(new byte[]{0,0,0,(byte) 255},			255),
				$(new byte[]{0,0,1,(byte) 255},			511),
				$(new byte[]{1,1,1,1},					16843009)
		};
	}
	
	@Test
	public void testToLong_ShouldThrowNullPointerException_IfBytesPassedAreNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("bytes are null");
		
		Bytes.toLong(null);
	}
	
	@Test
	@Parameters({"9","10","11"})
	public void testToLong_ShouldThrowIllegalArgumentException_IfMoreThanEightBytesArePassed(int bufferSize){
		byte[] buffer = createBuffer(bufferSize);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("byteCount should be in closed-range [0:8], found: " + bufferSize);
		
		Bytes.toLong(buffer);
	}
	
	@Test
	@Parameters(method = "dataFor_testToLong_ShouldConvertBytesPassedToLong")
	public void testToLong_ShouldConvertBytesPassedToLong(byte[] buffer, long expectedInt){
		assertEquals(expectedInt, Bytes.toLong(buffer));
	}
	
	public Object[][] dataFor_testToLong_ShouldConvertBytesPassedToLong() {
		return new Object[][]{
				//buffer,									expectedValue
				$(new byte[]{},								0),
				$(new byte[]{1},							1),
				$(new byte[]{1,0},							256),
				$(new byte[]{0,0,1,0},						256),
				$(new byte[]{1,2},							258),
				$(new byte[]{0,(byte) 255},					255),
				$(new byte[]{1,(byte) 255},					511),
				$(new byte[]{1,1,1,1},						16843009),
				$(new byte[]{127,-1,-1,-1,-1,-1,-1,-1},		Long.MAX_VALUE)
		};
	}
	
	private byte[] createBuffer(int anyBufferSize) {
		byte[] buffer = new byte[anyBufferSize];
		return buffer;
	}
	
	@Test
	public void testConcat_ShouldThrowNullPointerException_IfNullByteArrayIsPassed(){
		exception.expect(NullPointerException.class);
		
		Bytes.concat((byte[])null);
	}
	
	@Test
	public void testConcat_ShouldThrowNullPointerException_IfNullByteArraysIsPassed(){
		exception.expect(NullPointerException.class);
		
		Bytes.concat((byte[])null, (byte[])null);
	}
	
	@Test
	@Parameters(method = "dataFor_testConcat_ShouldConcatAllByteArraysIntoASingleByteArray")
	public void testConcat_ShouldConcatAllByteArraysIntoASingleByteArray(byte[][] sourceBytes, byte[] expectedConcatenatedBytes){
		byte[] actualBytes = Bytes.concat(sourceBytes);
		
		assertArrayEquals(expectedConcatenatedBytes, actualBytes);
	}

	public Object[][] dataFor_testConcat_ShouldConcatAllByteArraysIntoASingleByteArray() {
		return new Object[][] {
				//source bytes						expectedConcatenatedBytes
				{new byte[][]{new byte[]{}},
													new byte[]{}},
				{new byte[][]{new byte[]{1}},
													new byte[]{1}},
				{new byte[][]{new byte[]{1},
				 			  new byte[]{1}},
													new byte[]{1,1}},
				{new byte[][]{new byte[]{1,2},
							  new byte[]{2,3}},
													new byte[]{1,2,2,3}},
		};
	}
	
	@Test
	@Parameters(method = "dataFor_toHexReturnsAHexStringRepresentationOfBytesPassed_fromHexReturnsByteRepresentationOfHexStringPassed")
	public void toHexReturnsAHexStringRepresentationOfBytesPassed(String expectedHexString, byte[] inputBytes) {
		String hex = Bytes.toHex(inputBytes);
		assertEquals(expectedHexString, hex);
	}
	
	@Test
	@Parameters(method = "dataFor_toHexReturnsAHexStringRepresentationOfBytesPassed_fromHexReturnsByteRepresentationOfHexStringPassed")
	public void fromHexReturnsByteRepresentationOfHexStringPassed(String inputHexString, byte[] expectedBytes) {
		byte[] t = Bytes.fromHex(inputHexString);
		Assert.assertArrayEquals(expectedBytes, t);
	}
	
	public Object[][] dataFor_toHexReturnsAHexStringRepresentationOfBytesPassed_fromHexReturnsByteRepresentationOfHexStringPassed() {
		return new Object[][] {
			{"A1", new byte[] {(byte)0xA1}},
			{"FF", new byte[] {(byte)0xff}},
			{"A0123F", new byte[] {(byte)0xA0,(byte)0x12,(byte)0x3F}},
			//DOCUMENT this case
			{"010F", new byte[] {(byte)0x01, (byte)0xF}}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_fromHexReturnsByteValueOfHexCharacterPassed")
	public void fromHexReturnsByteValueOfHexCharacterPassed(char inputChar, byte expectedByte) {
		//TODO change it to assertEqual
		Assert.assertTrue(expectedByte == Bytes.fromHex(inputChar));
	}
	
	public Object[][] dataFor_fromHexReturnsByteValueOfHexCharacterPassed() {
		return new Object[][] {
			{'A', (byte) 10},
			{'a',(byte) 10},
			{'f',(byte) 15},
			{'3',(byte)3},
			{'0',(byte)0}
		};
	}
	
	@Test
	@Parameters({"ab0k","G231"})
	public void fromHexReturnsNullIfHexStringPassedToItContainsInvalidHexCharacters(String inputInvalidHexString) {
		Assert.assertNull(Bytes.fromHex(inputInvalidHexString));
	}
	
	@Test
	@Parameters({"0","AF1"})
	public void fromHexReturnsNullIfLengthOfHexStringPassedToItIsNotEven(String inputInvalidHexString) {
		Assert.assertNull(Bytes.fromHex(inputInvalidHexString));
	}
	
	@Test
	@Parameters({"-5","y"})
	public void fromHexReturnsMinusOneIfAnInvalidHexCharacterIsPassed(char inputChar) {
		//TODO change it to assertEqual
		Assert.assertTrue(Bytes.fromHex(inputChar) == -1);
	}
}
