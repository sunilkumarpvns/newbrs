package com.elitecore.commons.io;

import static com.elitecore.commons.base.Preconditions.checkArgument;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.elitecore.commons.logging.LogManager;

/**
 * 
 * A set of utilities for byte oriented streams.
 * 
 * @author narendra.pathai
 *
 */
public class ByteStreams {

	private static final int EOF = -1;
	private static final int ONE_KB = 1024;

	/**
	 * Reads {@code noOfBytesRequested} bytes from given input stream and returns
	 * read bytes as a byte-array.
	 * 
	 * <p>Reads from the stream only when sufficient amount of bytes are available
	 * in the stream, throws IOException otherwise.
	 * 
	 * @param inputStream a non-null input stream from which bytes will be read
	 * @param noOfBytesRequested a non-negative number of bytes to read
	 * @return bytes read from the stream
	 * @throws IOException if sufficient bytes are unavailable in stream or if there
	 * is some error while reading from the stream.
	 * @throws IllegalArgumentException if {@code noOfBytesRequested} is less than 0 
	 */
	public static byte[] readBytes(InputStream inputStream, int noOfBytesRequested) throws IOException, IllegalArgumentException {
		checkNotNull(inputStream, "inputStream is null");
		checkArgument(noOfBytesRequested >= 0, "noOfBytes cannot be negative");

		if (inputStream.available() < noOfBytesRequested) {
			throw new IOException("insufficient bytes available: " 
					+ inputStream.available() + ", requested: " + noOfBytesRequested);
		}

		byte[] bytes = new byte[noOfBytesRequested];
		inputStream.read(bytes);
		return bytes;
	}

	/**
	 * Reads {@code noOfBytesRequested} bytes from given input stream and returns
	 * read bytes as a byte-array.
	 * 
	 * <p>Does best effort to read requested number of bytes from the stream. Reads requested
	 * number of bytes from stream if available or reads available number of bytes.
	 * 
	 * @param inputStream a non-null input stream from which bytes will be read
	 * @param noOfBytesRequested a non-negative number of bytes to read
	 * @return bytes read from the stream
	 * @throws IOException if sufficient bytes are unavailable in stream or if there
	 * is some error while reading from the stream.
	 * @throws IllegalArgumentException if {@code noOfBytesRequested} is less than 0 
	 */
	public static byte[] tryReadBytes(InputStream inputStream, int noOfBytesRequested) throws IOException {
		checkNotNull(inputStream, "inputStream is null");
		checkArgument(noOfBytesRequested >= 0, "noOfBytes cannot be negative");

		int byteCountToRead = inputStream.available() < noOfBytesRequested
								? inputStream.available() : noOfBytesRequested;

		byte[] bytes = new byte[byteCountToRead];
		inputStream.read(bytes);
		return bytes;
	}

	/**
	 * A utility to provide non-exception throwing method for writing bulk bytes to 
	 * {@code ByteArrayOutputStream}.
	 *  
	 * <p>Tries to <i>silently</i> write given bytes to stream swallowing any I/O exception
	 * while writing by logging.
	 *  
	 * @param inputStream a non-null {@code ByteArrayOutputStream} in which bytes will be written
	 * @param bytesToWrite non-null bytes to write
	 * 
	 */
	public static void writeBytesSilently(ByteArrayOutputStream inputStream, byte... bytesToWrite) {
		checkNotNull(inputStream, "inputStream is null");
		checkNotNull(bytesToWrite, "bytesToWrite are null");

		try {
			inputStream.write(bytesToWrite);
		} catch (IOException e) {
			LogManager.getLogger().trace(e);
		}
	}

	/**
	 * Read all available bytes in the stream until EOF (-1) is detected.
	 * 
	 * <p><b>WARNING:</b> This call may block until {@code read()} on input stream
	 * returns.
	 * 
	 * <p><b>NOTE:</b> Does not close the input stream
	 * 
	 * @param inputStream a non-null input stream from which bytes will be read
	 * @return bytes read from the stream. Returns an empty byte array if the stream has
	 * reached EOF.
	 * 
	 * @throws IOException if there is any exception while reading from stream
	 */
	public static byte[] readFully(InputStream inputStream) throws IOException {
		checkNotNull(inputStream, "inputStream is null");

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[ONE_KB];
		int i;
		
		while (true) {
			i = inputStream.read(buffer);

			if (i == EOF){
				break;
			}

			out.write(buffer, 0, i);
		}
		
		return out.toByteArray();
	}

	
	/**
	 * Checks if {@code requiredAvailability} number of bytes are available in {@code stream}. If insufficient bytes are available
	 * then it throws {@code IOException}.
	 * 
	 * @param inputStream a non-null stream from which availability will be checked
	 * @param requiredAvailability a non-negative value 
	 * @throws IOException if any I/O error occurs
	 * @throws IllegalArgumentException if {@code requiredAvailability} is negative
	 */
	public static void checkAvailability(InputStream inputStream, int requiredAvailability) throws IOException {
		checkNotNull(inputStream, "inputStream is null");
		checkArgument(requiredAvailability >= 0, "requiredAvailability count should be non-negative: " + requiredAvailability);
		
		int available = inputStream.available();
		if (available < requiredAvailability) {
			throw new IOException("Insufficient bytes available in stream, required: "
									+ requiredAvailability + " available: " + available);
		}
	}
	
	/**
	 * Checks if {@code requiredAvailability} number of bytes are available in {@code stream}. If insufficient bytes are available
	 * then it throws {@code IOException}.
	 * 
	 * @param inputStream a non-null stream from which availability will be checked
	 * @param requiredAvailability a non-negative value 
	 * @param message will be used as message of IOException if sufficient bytes are unavailable
	 * @throws IOException if any I/O error occurs
	 * @throws IllegalArgumentException if {@code requiredAvailability} is negative
	 */
	public static void checkAvailability(InputStream inputStream, int requiredAvailability, String message) throws IOException {
		checkNotNull(inputStream, "inputStream is null");
		checkArgument(requiredAvailability >= 0, "requiredAvailability count should be non-negative: " + requiredAvailability);
		
		if (inputStream.available() < requiredAvailability) {
			throw new IOException(message);
		}
	}

	/**
	 * Reads {@code byteCount} number of bytes from the {@code inputStream} and converts
	 * them to integer.
	 * 
	 * <p>NOTE: It is expected that at least {@code byteCount} number of bytes must be available in
	 * the stream.
	 * 
	 * @param inputStream a non-null input stream
	 * @param byteCount number of bytes in closed range [0:4] that will be read
	 * @return integer value read from the stream
	 * @throws IOException if any I/O error occurs or {@code byteCount} number of bytes 
	 * are unavailable in stream
	 * @throws IllegalArgumentException if {@code byteCount} does not fall in closed range [0:4]
	 */ 
	/*
	 * This code has been copied from Bytes.toInt() for performance purposes. Reflect any changes
	 * there as well.
	 */
	public static int readInt(InputStream inputStream, int byteCount) throws IOException {
		checkArgument(byteCount >= 0 && byteCount <= 4, "byteCount should be in closed-range [0:4], found: " + byteCount);
		checkAvailability(inputStream, byteCount);
		
		int value = 0;
		for (int i = 0; i < byteCount; i++) {
			value = value << 8 | (inputStream.read() & 0xFF);
		}
		
		return value;
	}
	
	/**
	 * Reads 4 bytes from the {@code inputStream} and converts them to integer.
	 * 
	 * <p>NOTE: It is expected that at least 4 bytes must be available in the stream.
	 * 
	 * @param inputStream a non-null input stream
	 * @return integer value read from the stream
	 * @throws IOException if any I/O error occurs or 4 bytes are unavailable in stream
	 */
	public static int readInt(InputStream inputStream) throws IOException{
		return readInt(inputStream, 4);
	}

	/**
	 * Reads {@code byteCount} number of bytes from the {@code inputStream} and converts
	 * them to long.
	 * 
	 * <p>NOTE: It is expected that at least {@code byteCount} number of bytes must be available in
	 * the stream.
	 * 
	 * @param inputStream a non-null input stream
	 * @param byteCount number of bytes in closed range [0:8] that will be read
	 * @return long value read from the stream
	 * @throws IOException if any I/O error occurs or {@code byteCount} number of bytes 
	 * are unavailable in stream
	 * @throws IllegalArgumentException if {@code byteCount} does not fall in closed range [0:8]
	 */
	/*
	 * This code has been copied from Bytes.toLong() for performance purposes. Reflect any changes
	 * there as well.
	 */
	public static long readLong(InputStream inputStream, int byteCount) throws IOException {
		checkArgument(byteCount >= 0 && byteCount <= 8, "byteCount should be in closed-range [0:8], found: " + byteCount);
		checkAvailability(inputStream, byteCount);
		
		long value = 0;
		for (int i = 0; i < byteCount; i++) {
			value = value << 8 | (inputStream.read() & 0xFF);
		}		
		return value;
	}

	/**
	 * Reads 8 bytes from the {@code inputStream} and converts them to long.
	 * 
	 * <p>NOTE: It is expected that at least 8 bytes must be available in the stream.
	 * 
	 * @param inputStream a non-null input stream
	 * @return long value read from the stream
	 * @throws IOException if any I/O error occurs or 8 bytes are unavailable in stream
	 */
	public static long readLong(InputStream inputStream) throws IOException {
		return readLong(inputStream, 8);
	}
	
	/**
	 * Copies all bytes from the input stream to the output stream.
	 * <p>It is recommended to use this method when copying whole streams as it works
	 * in a memory efficient manner. It reads bytes from input stream in chunks of 
	 * 1 KB and writes them to the output stream. So this method does not hog up a lot
	 * of memory.
	 * 
	 * <p><b>NOTE:</b> Does not close any of the streams
	 * 
	 * @param inputStream a non-null input stream to read from
	 * @param outputStream a non-null output stream to write to
	 * @throws IOException if an I/O error occurs
	 * @throws NullPointerException if any of stream is null
	 */
	public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
		checkNotNull(inputStream, "inputStream is null");
		checkNotNull(outputStream, "outputStream is null");
		
		byte[] buffer = new byte[ONE_KB];
		int i;
		while (true) {
			i = inputStream.read(buffer);
			if (i == EOF) {
				break;
			}
			outputStream.write(buffer, 0, i);
		}
	}
}
