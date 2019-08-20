package com.elitecore.commons.io;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
public class LimitOutputStreamTest {
	private static final int ANY_INT = 0;
	
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@SuppressWarnings("resource")
	@Test
	public void testConstructor_ShouldThrowNullPointerException_IfUnderlyingOutputStreamIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("outputStream is null");
		
		new LimitOutputStream(null, ANY_INT);
	}
	
	@SuppressWarnings("resource")
	@Test
	@Parameters({"-1","-2","-255"})
	public void testConstructor_ShouldThrowIllegalArgumentException_IfLimitPassedIsNegative(int limit){
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("limit should be non-negative: " + limit);
		
		new LimitOutputStream(anyStream(), limit);
	}
	
	@Test
	@Parameters(method = "dataFor_testWrite_Int_ShouldWriteIntToTheUnderlyingStream_IfLimitIsNotExceeded")
	public void testWrite_Int_ShouldWriteIntToTheUnderlyingStream_IfLimitIsNotExceeded(int limit, byte[] bytesToWrite) throws IOException{
		ByteArrayOutputStream outputStream = anyStream();
		
		LimitOutputStream limitOutputStream = new LimitOutputStream(outputStream, limit);
		
		writeToStream(bytesToWrite, limitOutputStream);

		assertArrayEquals(bytesToWrite, outputStream.toByteArray());
	}
	
	public Object[] dataFor_testWrite_Int_ShouldWriteIntToTheUnderlyingStream_IfLimitIsNotExceeded() {
		return $(
				//limit		bytes to write
				$(0,		new byte[]{}),
				$(1,		new byte[]{1}),
				$(2,		new byte[]{1}),
				$(2,		new byte[]{1,1}),
				$(10,		new byte[]{1,1,1})
		);
	}

	@Test
	@Parameters(method = "dataFor_testWrite_Int_ShouldIgnoreExtraBytesAndNotWriteThemToUnderlyingStream_IfLimitIsExceeded")
	public void testWrite_Int_ShouldIgnoreExtraBytesAndNotWriteThemToUnderlyingStream_IfLimitIsExceeded(int limit, byte[] bytesToWrite, byte[] expectedBytes) throws IOException{
		ByteArrayOutputStream outputStream = anyStream();
		
		LimitOutputStream limitOutputStream = new LimitOutputStream(outputStream, limit);
		
		writeToStream(bytesToWrite, limitOutputStream);
		
		assertArrayEquals(expectedBytes, outputStream.toByteArray());
	}
	
	public Object[] dataFor_testWrite_Int_ShouldIgnoreExtraBytesAndNotWriteThemToUnderlyingStream_IfLimitIsExceeded() {
		return $(
				//limit		bytes to write			expected bytes
				$(0,		new byte[]{1},			new byte[]{}),
				$(1,		new byte[]{1,1},		new byte[]{1}),
				$(2,		new byte[]{1,1,1},		new byte[]{1,1})
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testWrite_ByteArrayWithOffset_ShouldWriteAllBytesFromAGivenOffsetToUnderlyingStream_IfLimitNotExceededAndIsGreaterThanOrEqualsToBytesToWrite")
	public void testWrite_ByteArrayWithOffset_ShouldWriteAllBytesFromAGivenOffsetToUnderlyingStream_IfLimitNotExceededAndIsGreaterThanOrEqualsToBytesToWrite(int limit, byte[] bytesToWrite, int offset, int length, byte[] expectedBytes) throws IOException{
		ByteArrayOutputStream outputStream = anyStream();
		
		LimitOutputStream limitOutputStream = new LimitOutputStream(outputStream, limit);
		
		limitOutputStream.write(bytesToWrite, offset, length);
		
		limitOutputStream.close();
		assertArrayEquals(expectedBytes, outputStream.toByteArray());
	}
	
	
	public Object[] dataFor_testWrite_ByteArrayWithOffset_ShouldWriteAllBytesFromAGivenOffsetToUnderlyingStream_IfLimitNotExceededAndIsGreaterThanOrEqualsToBytesToWrite() {
		return $(
				//limit		bytes to write			offset		length		expected bytes
				$(0,		new byte[]{},			0,			0,			new byte[]{}),
				$(0,		new byte[]{1},			1,			0,			new byte[]{}),
				$(1,		new byte[]{},			0,			0,			new byte[]{}),
				$(1,		new byte[]{1},			0,			1,			new byte[]{1}),
				$(2,		new byte[]{1},			0,			1,			new byte[]{1}),
				$(2,		new byte[]{1,2},		1,			1,			new byte[]{2}),
				$(2,		new byte[]{1,2},		0,			2,			new byte[]{1,2})
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testWrite_ByteArrayWithOffset_ShouldNotWriteAnyBytesToOutputStream_IfLimitIsLessThanBytesToWriteAndIsNotExceeded")
	public void testWrite_ByteArrayWithOffset_ShouldNotWriteAnyBytesToOutputStream_IfLimitIsLessThanBytesToWriteAndIsNotExceeded(int limit, byte[] bytesToWrite, int offset, int length, byte[] expectedBytes) throws IOException{
		ByteArrayOutputStream outputStream = anyStream();
		
		LimitOutputStream limitOutputStream = new LimitOutputStream(outputStream, limit);
		
		limitOutputStream.write(bytesToWrite, offset, length);
		
		limitOutputStream.close();
		assertArrayEquals(expectedBytes, outputStream.toByteArray());
	}
	
	public Object[] dataFor_testWrite_ByteArrayWithOffset_ShouldNotWriteAnyBytesToOutputStream_IfLimitIsLessThanBytesToWriteAndIsNotExceeded() {
		return $(
				//limit		bytes to write			offset		length		expected bytes
				$(0,		new byte[]{1},			0,			1,			new byte[]{}),
				$(1,		new byte[]{1,2},		0,			1,			new byte[]{1}),
				$(1,		new byte[]{1,2},		1,			1,			new byte[]{2}),
				$(2,		new byte[]{1,2,3},		0,			3,			new byte[]{1,2})
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testWrite_ShouldNotWriteFurtherBytesToOutputStream_OnceLimitIsReached")
	public void testWrite_ShouldNotWriteFurtherBytesToOutputStream_OnceLimitIsReached(int limit, List<byte[]> bytesToWriteVector, byte[] expectedBytes) throws IOException{
		ByteArrayOutputStream outputStream = anyStream();
		
		LimitOutputStream limitOutputStream = new LimitOutputStream(outputStream, limit);
		
		for(byte[] bytesToWrite : bytesToWriteVector){
			limitOutputStream.write(bytesToWrite);
		}
		
		limitOutputStream.close();
		assertArrayEquals(expectedBytes, outputStream.toByteArray());
	}

	public Object[] dataFor_testWrite_ShouldNotWriteFurtherBytesToOutputStream_OnceLimitIsReached() {
		return $(
				$(1,		Arrays.asList(new byte[]{},new byte[]{1,2}),	new byte[]{1}),
				$(1,		Arrays.asList(new byte[]{1},new byte[]{2,3}),	new byte[]{1}),
				$(2,		Arrays.asList(new byte[]{1},new byte[]{2,3}),	new byte[]{1,2})
		);
	}
	
	private void writeToStream(byte[] bytesToWrite, LimitOutputStream stream) throws IOException {
		for(int i = 0; i < bytesToWrite.length; i++){
			stream.write(bytesToWrite[i]);
		}
	}

	private static ByteArrayOutputStream anyStream() {
		return new ByteArrayOutputStream();
	}
	
	
}
