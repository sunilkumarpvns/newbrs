package com.elitecore.commons.io;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class LimitInputStreamTest {
	private static final byte BUFFER_VALUE = (byte)1;
	private static final int EMPTY_BUFFER = 0;
	private static final int ANY_INT = 0;
	private static final int EOF = -1;

	@Rule public ExpectedException exception = ExpectedException.none();
	
	@SuppressWarnings("resource")
	@Test
	public void testConstructor_ShouldThrowNullPointerException_WhenUnderlyingInputStreamPassedIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("inStream is null");
		
		new LimitInputStream(null, ANY_INT);
	}
	
	@SuppressWarnings("resource")
	@Test
	@Parameters({"-1","-2","-255"})
	public void testConstructor_ShouldThrowIllegalArgumentException_IfLimitIsLessThanZero(int limit){
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("limit should be a non-negative value: " + limit);
		
		new LimitInputStream(createBuffer(ANY_INT), limit);
	}
	
	@Test
	@Parameters(method = "dataFor_testReadIntoByteArray_ShouldReadBytesFromUnderlyingStreamAndReturnByteCountRead_IfLimitIsNotExceeded")
	public void testReadIntoByteArray_ShouldReadBytesFromUnderlyingStreamAndReturnByteCountRead_IfLimitIsNotExceededAndRequiredBytesAreAvailableInUnderlyingStream(int bufferSize, int limit, int readCount) throws IOException{
		ByteArrayInputStream stream = createBuffer(bufferSize);
		
		
		LimitInputStream limitStream = new LimitInputStream(stream, limit);

		byte[] bytesRead = new byte[readCount];
		int count = limitStream.read(bytesRead);
		
		assertEquals(readCount, count);
		limitStream.close();
	}


	public Object[] dataFor_testReadIntoByteArray_ShouldReadBytesFromUnderlyingStreamAndReturnByteCountRead_IfLimitIsNotExceeded() {
		return $(
				$(1,	1,		1),
				$(2,	1,		1),
				$(100,	10,		1)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testReadIntoByteArray_ShouldReadBytesFromUnderlyingStreamAndReturnCountOfBytesRemainingInUnderlyingStream_IfLimitIsNotExceededAndSufficientBytesAreUnavailableInUnderlyingStream")
	public void testReadIntoByteArray_ShouldReadBytesFromUnderlyingStreamAndReturnCountOfBytesRemainingInUnderlyingStream_IfLimitIsNotExceededAndSufficientBytesAreUnavailableInUnderlyingStream(int bufferSize, int limit, int readCount) throws IOException{
		ByteArrayInputStream inputStream = createBuffer(bufferSize);
		
		LimitInputStream limitStream = new LimitInputStream(inputStream, limit);
		
		byte[] readBytes = new byte[readCount]; 
		int actualReadCount = limitStream.read(readBytes);
		
		assertEquals(bufferSize, actualReadCount);
		limitStream.close();
	}
	
	public Object[] dataFor_testReadIntoByteArray_ShouldReadBytesFromUnderlyingStreamAndReturnCountOfBytesRemainingInUnderlyingStream_IfLimitIsNotExceededAndSufficientBytesAreUnavailableInUnderlyingStream() {
		return $(
			//buffer-size 	limit 	   requiredReadCount
				$(1, 		10,			2),
				$(10, 		20,			15)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testReadIntoByteArray_ShouldReturnEOF_WhenLimitIsExceeded")
	public void testReadIntoByteArray_ShouldReturnEOF_WhenLimitIsExceeded(int bufferSize, int limit) throws IOException{
		ByteArrayInputStream inputStream = createBuffer(bufferSize);
		
		LimitInputStream limitInputStream = new LimitInputStream(inputStream, limit);
		
		readUntilLimit(limitInputStream, limit);
		
		int actualReadCount = limitInputStream.read(new byte[]{});
		
		assertEquals(EOF, actualReadCount);
	}

	private void readUntilLimit(LimitInputStream limitInputStream, int limit) throws IOException {
		for(int i = 0; i < limit; i++){
			assertTrue(limitInputStream.read() != EOF);
		}
	}
	
	public Object[] dataFor_testReadIntoByteArray_ShouldReturnEOF_WhenLimitIsExceeded() {
		return $(
			//buffer-size	limit
				$(0,		0),
				$(1,		1),
				$(2,		1),
				$(10,		5)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testReadIntoByteArray_ShouldReturnEOF_WhenLimitIsNotExceededButUnderlyingStreamHitsEOF")
	public void testReadIntoByteArray_ShouldReturnEOF_WhenLimitIsNotExceededButUnderlyingStreamHitsEOF(int limit, int readCount) throws IOException{
		ByteArrayInputStream inputStream = createBuffer(EMPTY_BUFFER);
		
		LimitInputStream limitInputStream = new LimitInputStream(inputStream, limit);
		
		byte[] readBytes = new byte[readCount];
		int actualReadCount = limitInputStream.read(readBytes);
		
		assertTrue(actualReadCount == EOF);
		limitInputStream.close();
	}
	
	public Object[] dataFor_testReadIntoByteArray_ShouldReturnEOF_WhenLimitIsNotExceededButUnderlyingStreamHitsEOF() {
		return $(
				//limit
				$(2,		1),
				$(10,		1),
				$(10,		10)
		);
	}
	

	@Test
	@Parameters(method = "dataFor_testReadSingleByte_ShouldReadAByteFromUnderlyingStreamAndReturnByteRead_IfLimitIsNotExceededAndRequiredBytesAreAvailableInUnderlyingStream")
	public void testReadSingleByte_ShouldReadAByteFromUnderlyingStreamAndReturnByteRead_IfLimitIsNotExceededAndRequiredBytesAreAvailableInUnderlyingStream(int bufferSize, int limit, int readCount) throws IOException{
		ByteArrayInputStream stream = createBuffer(bufferSize);
		
		LimitInputStream limitStream = new LimitInputStream(stream, limit);

		for(int i = 0; i < readCount; i++){
			int byteRead = limitStream.read();
			assertEquals(BUFFER_VALUE, byteRead);
		}
		limitStream.close();
	}

	public Object[] dataFor_testReadSingleByte_ShouldReadAByteFromUnderlyingStreamAndReturnByteRead_IfLimitIsNotExceededAndRequiredBytesAreAvailableInUnderlyingStream() {
		return $(
				$(1,	1,		1),
				$(10,	10,		10),
				$(100,	100,	10)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testReadSingleByte_ShouldReturnEOF_WhenLimitIsExceeded")
	public void testReadSingleByte_ShouldReturnEOF_WhenLimitIsExceeded(int bufferSize, int limit) throws IOException{
		ByteArrayInputStream inputStream = createBuffer(bufferSize);
		
		LimitInputStream limitInputStream = new LimitInputStream(inputStream, limit);
		
		readUntilLimit(limitInputStream, limit);
		
		int actualByteRead = limitInputStream.read();
		
		assertEquals(EOF, actualByteRead);
	}

	public Object[] dataFor_testReadSingleByte_ShouldReturnEOF_WhenLimitIsExceeded() {
		return $(
				//buffer-size	limit
				$(0,		0),
				$(1,		1),
				$(2,		1),
				$(10,		5)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testReadSingleByte_ShouldReturnEOF_WhenLimitIsNotExceededButUnderlyingStreamHitsEOF")
	public void testReadSingleByte_ShouldReturnEOF_WhenLimitIsNotExceededButUnderlyingStreamHitsEOF(int limit, int readCount) throws IOException{
		ByteArrayInputStream inputStream = createBuffer(EMPTY_BUFFER);
		
		LimitInputStream limitInputStream = new LimitInputStream(inputStream, limit);
		
		int actualByteRead = limitInputStream.read();
		
		limitInputStream.close();
		assertTrue(actualByteRead == EOF);
	}
	
	public Object[] dataFor_testReadSingleByte_ShouldReturnEOF_WhenLimitIsNotExceededButUnderlyingStreamHitsEOF() {
		return $(
				//limit
				$(2,		1),
				$(10,		1),
				$(10,		10)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testAvailable_ShouldReturnRemainingLeftLimit_WhenBufferHasMoreBytesThanLimit")
	public void testAvailable_ShouldReturnRemainingLeftLimit_WhenBufferHasMoreBytesThanLimit(int bufferSize, int limit, int readLimit, Object expectedAvailableBytes) throws IOException{
		ByteArrayInputStream inputStream = createBuffer(bufferSize);
		
		LimitInputStream limitStream = new LimitInputStream(inputStream, limit);
		
		readUntilLimit(limitStream, readLimit);
		
		int actualAvailableBytes = limitStream.available();
		
		assertEquals(expectedAvailableBytes, actualAvailableBytes);
	}
	
	public Object[]dataFor_testAvailable_ShouldReturnRemainingLeftLimit_WhenBufferHasMoreBytesThanLimit() {
		return $(
			//buffer-size	limit	initial-read	expectedAvailable
				$(1,		0,			0,				0),
				$(1,		1,			0,				1),
				$(1,		1,			1,				0),
				$(1,		1,			0,				1),
				$(10,		1,			0,				1),
				$(10,		5,			0,				5),
				$(10,		10,			5,				5)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testAvailable_ShouldReturnRemainingBufferSize_WhenLimitIsGreaterThanAvailabilityOfBytesInUnderlyingStream")
	public void testAvailable_ShouldReturnRemainingBufferSize_WhenLimitIsGreaterThanAvailabilityOfBytesInUnderlyingStream(int bufferSize, int limit, int readLimit, Object expectedAvailableBytes) throws IOException{
		ByteArrayInputStream inputStream = createBuffer(bufferSize);
		
		LimitInputStream limitStream = new LimitInputStream(inputStream, limit);
		
		readUntilLimit(limitStream, readLimit);
		
		int actualAvailableBytes = limitStream.available();
		
		assertEquals(expectedAvailableBytes, actualAvailableBytes);
	}
	
	public Object[] dataFor_testAvailable_ShouldReturnRemainingBufferSize_WhenLimitIsGreaterThanAvailabilityOfBytesInUnderlyingStream() {
		return $(
			//buffer-size	limit	initial-read	expectedAvailable
				$(0,		1,			0,				0),
				$(1,		2,			0,				1),
				$(1,		2,			1,				0),
				$(10,		100,		5,				5)
		);
	}
	
	@SuppressWarnings("resource")
	@Test
	public void testMarkSupported_ShouldReturnFalse_AsMarkingAndResettingIsNotYetSupportedInLimitStream(){
		assertFalse(new LimitInputStream(createBuffer(ANY_INT), ANY_INT).markSupported());
	}
	
	/* --------- private helper methods ---------------- */
	
	private ByteArrayInputStream createBuffer(int bufferSize) {
		byte[] buffer = new byte[bufferSize];
		Arrays.fill(buffer, BUFFER_VALUE);
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		return stream;
	}
}
