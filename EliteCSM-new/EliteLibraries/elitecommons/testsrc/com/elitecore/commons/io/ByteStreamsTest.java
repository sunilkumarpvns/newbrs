package com.elitecore.commons.io;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.commons.base.Numbers;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class ByteStreamsTest {

	private static final byte[] ANY_BUFFER = new byte[]{1};
	private static final String ANY_STRING = "ANY";
	private static final int ANY_INT = 0;

	@Rule public ExpectedException exception = ExpectedException.none();
	
	private final ByteArrayInputStream ANY_STREAM = new ByteArrayInputStream(new byte[]{});;
	
	@Test
	@Parameters(method = "dataFor_testReadBytes_ShouldReadRequestedBytesFromStream_IfSufficientBytesAreAvailable")
	public void testReadBytes_ShouldReadRequestedBytesFromStream_IfSufficientBytesAreAvailable(byte[] buffer, int noOfBytesToRead, byte[] expectedBytes) throws IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		byte[] readBytes = ByteStreams.readBytes(stream, noOfBytesToRead);
		assertArrayEquals(expectedBytes, readBytes);
	}
	
	public Object[][] dataFor_testReadBytes_ShouldReadRequestedBytesFromStream_IfSufficientBytesAreAvailable() {
		return new Object[][] {
				{new byte[]{},		 	0,	new byte[]{}},
				{new byte[]{0},			1,	new byte[]{0}},
				{new byte[]{0,1},		2,	new byte[]{0,1}},
				{new byte[]{0,1},		1,	new byte[]{0}}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testReadBytes_ShouldThrowIOException_WhenInsufficientBytesAreAvailableInStream")
	public void testReadBytes_ShouldThrowIOException_WhenInsufficientBytesAreAvailableInStream(byte[] buffer, int noOfBytesRequested) throws IOException{
		exception.expect(IOException.class);
		exception.expectMessage("insufficient bytes available: " + buffer.length + ", requested: " + noOfBytesRequested);
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		ByteStreams.readBytes(stream, noOfBytesRequested);
	}
	
	public Object[][] dataFor_testReadBytes_ShouldThrowIOException_WhenInsufficientBytesAreAvailableInStream() {
		return new Object[][] {
				{new byte[]{},		1},
				{new byte[]{0},		2},
				{new byte[]{0,0},	10}
		};
	}
	
	@Test
	@Parameters({"-1","-2","-3","-100","-255"})
	public void testReadBytes_ShouldThrowIllegalArgumentException_IfNoOfBytesIsNegative(int noOfBytesToRead) throws IOException{
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("noOfBytes cannot be negative");
		ByteStreams.readBytes(ANY_STREAM, noOfBytesToRead);
	}
	
	@Test
	@Parameters(method = "dataFor_testTryReadBytes_ShouldReadAndReturnAvailableNumberOfBytes_IfSufficientBytesAreUnavailableInStream")
	public void testTryReadBytes_ShouldReadAndReturnAvailableNumberOfBytes_IfSufficientBytesAreUnavailableInStream(byte[] buffer, int noOfBytesRequested, byte[] expectedBytes) throws IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		byte[] readBytes = ByteStreams.tryReadBytes(stream, noOfBytesRequested);
		assertArrayEquals(expectedBytes, readBytes);
	}
	
	public Object[][] dataFor_testTryReadBytes_ShouldReadAndReturnAvailableNumberOfBytes_IfSufficientBytesAreUnavailableInStream() {
		return new Object[][] {
				{new byte[]{},		 	0,	new byte[]{}},
				{new byte[]{0},			1,	new byte[]{0}},
				{new byte[]{0,1},		2,	new byte[]{0,1}},
				{new byte[]{0,1},		1,	new byte[]{0}},
				{new byte[]{0,1},		3,	new byte[]{0,1}},
				{new byte[]{0},			100,new byte[]{0}}
		};
	}

	@Test
	@Parameters({"-1","-2","-3","-100","-255"})
	public void testTryReadBytes_ShouldThrowIllegalArgumentException_IfNoOfBytesIsNegative(int noOfBytesToRead) throws IOException{
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("noOfBytes cannot be negative");
		ByteStreams.tryReadBytes(ANY_STREAM, noOfBytesToRead);
	}
	
	@Test
	public void testWriteBytesSilently_ShouldThrowNullPointerException_IfOutputStreamPassedIsNull() throws IOException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("inputStream is null");
		
		ByteStreams.writeBytesSilently(null, new byte[]{});
	}
	
	@Test
	public void testWriteBytesSilently_ShouldThrowNullPointerException_IfBytesPassedAreNull() throws IOException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("bytesToWrite are null");
		
		ByteStreams.writeBytesSilently(new ByteArrayOutputStream(), null);
	}
	
	@Test
	@Parameters(method = "dataFor_testWriteBytesSilently_ShouldWriteBytesToOutputStream_IfNoIOExceptionOccurs")
	public void testWriteBytesSilently_ShouldWriteBytesToOutputStream_IfNoIOExceptionOccurs(byte[] bytesToWrite){
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ByteStreams.writeBytesSilently(stream, bytesToWrite);
		byte[] bytesFromStream = stream.toByteArray();
		assertArrayEquals(bytesToWrite, bytesFromStream);
	}
	
	public Object[][] dataFor_testWriteBytesSilently_ShouldWriteBytesToOutputStream_IfNoIOExceptionOccurs() {
		return new Object[][] {
				{new byte[]{}},
				{new byte[]{0}},
				{new byte[]{0,1}},
				{new byte[]{0,1,2}}
		};
	}
	
	@Test
	public void testWriteBytesSilently_ShouldSwallowIOException_IfAnyIOExceptionWhileWriting_DoesNotOccurIdeally() throws IOException{
		byte[] bytesToWrite = new byte[]{};
		
		ByteArrayOutputStream stream = mock(ByteArrayOutputStream.class);
		doThrow(IOException.class).when(stream).write(bytesToWrite);
		
		ByteStreams.writeBytesSilently(stream, bytesToWrite);
	}
	
	@Test
	public void testReadFully() throws IOException{
		int AVAILABLE = 100;
		byte[] bytesToRead = createBuffer(AVAILABLE);
		
		ByteArrayInputStream stream = new ByteArrayInputStream(bytesToRead);
		
		byte[] actualBytesRead = ByteStreams.readFully(stream);
		assertArrayEquals(bytesToRead, actualBytesRead);
	}
	
	@Test
	public void testReadFully_ShouldReturnEmptyByteArray_IfStreamHasReachedEOF() throws IOException{
		int AVAILABLE = 0;
		byte[] bytesToRead = createBuffer(AVAILABLE);
		
		ByteArrayInputStream stream = new ByteArrayInputStream(bytesToRead);
		
		byte[] actualBytesRead = ByteStreams.readFully(stream);
		assertTrue(actualBytesRead.length == 0);
	}
	
	@Test
	public void testCheckAvailabilityWithoutMessage_ShouldThrowNullPointerException_IfInputStreamIsNull() throws IOException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("inputStream is null");
		
		ByteStreams.checkAvailability(null, ANY_INT);
	}
	
	@Test
	public void testCheckAvailabilityWithMessage_ShouldThrowNullPointerException_IfInputStreamIsNull() throws IOException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("inputStream is null");
		
		ByteStreams.checkAvailability(null, ANY_INT, ANY_STRING);
	}

	@Test
	@Parameters({
		"0,1",
		"1,2"}
	)
	public void testCheckAvailabilityWithoutMessage_ShouldThrowIOExceptionWithDefaultMessage_IfRequiredBytesAreUnavailableInStream(int bufferSize, int requiredAvailability) throws IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(createBuffer(bufferSize));
		
		exception.expect(IOException.class);
		exception.expectMessage("Insufficient bytes available in stream, required: "
								+ requiredAvailability + " available: " + bufferSize);
		
		ByteStreams.checkAvailability(stream, requiredAvailability);
	}
	
	@Test
	@Parameters({
		"0,1",
		"1,2"}
	)
	public void testCheckAvailabilityWithMessage_ShouldThrowIOExceptionWithGivenMessage_IfRequiredBytesAreUnavailableInStream(int bufferSize, int requiredAvailability) throws IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(createBuffer(bufferSize));
		
		exception.expect(IOException.class);
		exception.expectMessage("Test message");
		
		ByteStreams.checkAvailability(stream, requiredAvailability, "Test message");
	}

	
	@Test
	@Parameters({
		"0,0",
		"1,1",
		"2,1",
		"100,100",
		"255,100"
	})
	public void testCheckAvailability_ShouldNotThrowAnyException_IfRequiredBytesAreAvailableInStream(int bufferSize, int requiredAvailability) throws IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(createBuffer(bufferSize));
		
		ByteStreams.checkAvailability(stream, requiredAvailability, ANY_STRING);
	}
	
	@Test
	@Parameters({
		"0,0",
		"1,1",
		"2,1",
		"100,100",
		"255,100"
	})
	public void testCheckAvailabilityWithoutMessage_ShouldNotThrowAnyException_IfRequiredBytesAreAvailableInStream(int bufferSize, int requiredAvailability) throws IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(createBuffer(bufferSize));
		
		ByteStreams.checkAvailability(stream, requiredAvailability);
	}
	
	@Test
	@Parameters({
		"-1",
		"-2",
		"-255"
	})
	public void testCheckAvailability_ShouldThrowIllegalArgumentException_WhenRequiredByteAvailabilityIsLessThanZero(int requiredAvailability) throws IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(ANY_BUFFER);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("requiredAvailability count should be non-negative: " + requiredAvailability);
		
		ByteStreams.checkAvailability(stream, requiredAvailability, ANY_STRING);	
	}
	
	@Test
	@Parameters({
		"-1",
		"-2",
		"-255"
	})
	public void testCheckAvailabilityWithoutMessage_ShouldThrowIllegalArgumentException_WhenRequiredByteAvailabilityIsLessThanZero(int requiredAvailability) throws IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(ANY_BUFFER);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("requiredAvailability count should be non-negative: " + requiredAvailability);
		
		ByteStreams.checkAvailability(stream, requiredAvailability);	
	}
	
	
	@Test
	public void testReadInt_ShouldThrowNullPointerException_IfInputStreamIsNull() throws IOException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("inputStream is null");
		
		ByteStreams.readInt(null);
	}
	
	@Test
	@Parameters({"0","1","2","3"})
	public void testReadInt_ShouldThrowIOException_IfFourBytesAreUnavailableInInputStream(int bufferSize) throws IOException{
		exception.expect(IOException.class);
		
		ByteStreams.readInt(new ByteArrayInputStream(createBuffer(bufferSize)));
	}
	
	@Test
	@Parameters(method = "dataFor_testReadInt_ShouldConvertFourBytesToInt")
	public void testReadInt_ShouldConvertFourBytesToInt(byte[] buffer, int expectedValue) throws IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		
		assertEquals(expectedValue, ByteStreams.readInt(stream));
	}
	
	public Object[][] dataFor_testReadInt_ShouldConvertFourBytesToInt() {
		return new Object[][]{
				//buffer,								expectedValue
				$(new byte[]{0,0,0,0},					0),
				$(new byte[]{0,0,0,1},					1),
				$(new byte[]{0,0,1,0},					256),
				$(new byte[]{0,0,1,2},					258),
				$(new byte[]{0,0,0,(byte) 255},			255),
				$(new byte[]{0,0,1,(byte) 255},			511),
				$(new byte[]{1,1,1,1},					16843009)
		};
	}
	
	@Test
	@Parameters({"-2", "-1", "5", "6"})
	public void testReadInt_WithByteCount_ShouldThrowIllegalArgumentException_IfByteCountIsNotClosedInRangeFromZeroToFour(int byteCount) throws IOException{
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("byteCount should be in closed-range [0:4], found: " + byteCount);
		
		ByteStreams.readInt(new ByteArrayInputStream(ANY_BUFFER), byteCount);
	}
	
	@Test
	public void testReadInt_WithByteCount_ShouldThrowNullPointerException_IfInputStreamIsNull() throws IOException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("inputStream is null");
		
		ByteStreams.readInt(null, ANY_INT);
	}
	
	@Test
	@Parameters(method = "dataFor_testReadInt_WithByteCount_ShouldThrowIOException_IfSufficientBytesAreUnavailableInInputStream")
	public void testReadInt_WithByteCount_ShouldThrowIOException_IfSufficientBytesAreUnavailableInInputStream(int bufferSize, int byteCount) throws IOException{
		exception.expect(IOException.class);
		
		ByteStreams.readInt(new ByteArrayInputStream(createBuffer(bufferSize)), byteCount);
	}
	
	public Object[] dataFor_testReadInt_WithByteCount_ShouldThrowIOException_IfSufficientBytesAreUnavailableInInputStream() {
		return $(
				//buffer-size		byteCount
				$(0,				1),
				$(0,				2),
				$(1,				2),
				$(2,				3)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testReadInt_WithByteCount_ShouldConvertTheBytesToInt")
	public void testReadInt_WithByteCount_ShouldConvertTheBytesToInt(byte[] buffer, int byteCount, int expectedValue) throws IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		
		assertEquals(expectedValue, ByteStreams.readInt(stream, byteCount));
	}
	
	public Object[][] dataFor_testReadInt_WithByteCount_ShouldConvertTheBytesToInt() {
		return new Object[][]{
				//buffer,						count			expectedValue
				$(new byte[]{},					0,				0),
				$(new byte[]{1},				0,				0),
				$(new byte[]{1},				0,				0),
				$(new byte[]{1},				1,				1),
				$(new byte[]{1,0},				2,				256),
				$(new byte[]{1,2},				2,				258),
				$(new byte[]{0,(byte) 255},		2,				255),
				$(new byte[]{1,(byte) 255},		2,				511),
				$(new byte[]{1,1,1,1},			4,				16843009)
		};
	}
	
	
	@Test
	public void testReadLong_ShouldThrowNullPointerException_IfInputStreamIsNull() throws IOException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("inputStream is null");
		
		ByteStreams.readLong(null);
	}
	
	@Test
	@Parameters({"0","1","2","3","4","5","6","7"})
	public void testReadLong_ShouldThrowIOException_IfEightBytesAreUnavailableInInputStream(int bufferSize) throws IOException{
		exception.expect(IOException.class);
		
		ByteStreams.readLong(new ByteArrayInputStream(createBuffer(bufferSize)));
	}
	
	@Test
	@Parameters(method = "dataFor_testReadLong_ShouldConvertEightBytesToLong")
	public void testReadLong_ShouldConvertEightBytesToLong(byte[] buffer, long expectedValue) throws IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		
		System.out.println(Arrays.toString(Numbers.toByteArray(Long.MAX_VALUE, 8)));
		assertEquals(expectedValue, ByteStreams.readLong(stream));
	}
	
	public Object[][] dataFor_testReadLong_ShouldConvertEightBytesToLong() {
		return new Object[][]{
				//buffer,											expectedValue
				$(new byte[]{0,0,0,0,0,0,0,0},							0),
				$(new byte[]{0,0,0,0,0,0,0,1},							1),
				$(new byte[]{0,0,0,0,0,0,1,0},							256),
				$(new byte[]{0,0,0,0,0,0,1,2},							258),
				$(new byte[]{0,0,0,0,0,0,0,(byte) 255},					255),
				$(new byte[]{0,0,0,0,0,0,1,(byte) 255},					511),
				$(new byte[]{127,-1,-1,-1,-1,-1,-1,-1},					Long.MAX_VALUE)
		};
	}
	
	@Test
	@Parameters({"-2", "-1", "9", "10"})
	public void testReadLong_WithByteCount_ShouldThrowIllegalArgumentException_IfByteCountIsNotClosedInRangeFromZeroToEight(int byteCount) throws IOException{
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("byteCount should be in closed-range [0:8], found: " + byteCount);
		
		ByteStreams.readLong(new ByteArrayInputStream(ANY_BUFFER), byteCount);
	}
	
	@Test
	public void testReadLong_WithByteCount_ShouldThrowNullPointerException_IfInputStreamIsNull() throws IOException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("inputStream is null");
		
		ByteStreams.readLong(null, ANY_INT);
	}
	
	@Test
	@Parameters(method = "dataFor_testReadInt_WithByteCount_ShouldThrowIOException_IfSufficientBytesAreUnavailableInInputStream")
	public void testReadLong_WithByteCount_ShouldThrowIOException_IfSufficientBytesAreUnavailableInInputStream(int bufferSize, int byteCount) throws IOException{
		exception.expect(IOException.class);
		
		ByteStreams.readLong(new ByteArrayInputStream(createBuffer(bufferSize)), byteCount);
	}
	
	public Object[] dataFor_testReadLong_WithByteCount_ShouldThrowIOException_IfSufficientBytesAreUnavailableInInputStream() {
		return $(
				//buffer-size		byteCount
				$(0,				1),
				$(0,				2),
				$(1,				2),
				$(2,				3),
				$(3,				5),
				$(7,				8)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testReadLong_WithByteCount_ShouldConvertTheBytesToLong")
	public void testReadLong_WithByteCount_ShouldConvertTheBytesToLong(byte[] buffer, int byteCount, long expectedValue) throws IOException{
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		
		assertEquals(expectedValue, ByteStreams.readLong(stream, byteCount));
	}
	
	public Object[][] dataFor_testReadLong_WithByteCount_ShouldConvertTheBytesToLong() {
		return new Object[][]{
				//buffer,									count			expectedValue
				$(new byte[]{},								0,				0),
				$(new byte[]{1},							0,				0),
				$(new byte[]{1},							0,				0),
				$(new byte[]{1},							1,				1),
				$(new byte[]{1,0},							2,				256),
				$(new byte[]{1,2},							2,				258),
				$(new byte[]{0,(byte) 255},					2,				255),
				$(new byte[]{1,(byte) 255},					2,				511),
				$(new byte[]{1,1,1,1},						4,				16843009),
				$(new byte[]{127,-1,-1,-1,-1,-1,-1,-1},		8,				Long.MAX_VALUE)
		};
	}
	
	private byte[] createBuffer(int bufferSize) {
		byte[] buffer = new byte[bufferSize];
		Arrays.fill(buffer, (byte)1);
		return buffer;
	}
	
	@Test
	@Parameters(value = {"0", "10", "100"})
	public void testCopy_ShouldCopyAllBytesAvailableFromInputStreamToOutputStream(int bufferSize) throws IOException {
		byte[] inputBuffer = createBuffer(bufferSize);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBuffer);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteStreams.copy(inputStream, outputStream);
		assertArrayEquals(inputBuffer, outputStream.toByteArray());
	}
	
	@Test
	public void testCopy_ShouldThrowNPE_IfInputStreamIsNull() throws IOException {
		exception.expect(NullPointerException.class);
		exception.expectMessage("inputStream is null");
		
		ByteStreams.copy(null, new ByteArrayOutputStream());
	}
	
	@Test
	public void testCopy_ShouldThrowNPE_IfOutputStreamIsNull() throws IOException {
		exception.expect(NullPointerException.class);
		exception.expectMessage("outputStream is null");
		
		ByteStreams.copy(ANY_STREAM, null);
	}
}
