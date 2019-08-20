package com.elitecore.commons.io;

import static com.elitecore.commons.base.Preconditions.checkArgument;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A stream that limits the number of bytes that can be read.
 * 
 * <p><b>WARNING:</b> {@code mark} and {@code reset} operations are not supported.
 * 
 * @author narendra.pathai
 *
 */
public class LimitInputStream extends FilterInputStream {

	private int left = 0;
	
	/**
	 * Creates a limit input stream over {@code byteArrayInputStream} and limits the 
	 * size of bytes that can be read to {@code limit}.
	 * 
	 * @param byteArrayInputStream a non-null stream which is to be limited
	 * @param limit a non-negative integer
	 * @throws IllegalArgumentException if {@code limit} is negative
	 */
	public LimitInputStream(InputStream inStream, int limit) {
		super(inStream);
		checkNotNull(inStream, "inStream is null");
		checkArgument(limit >= 0, "limit should be a non-negative value: " + limit);
		this.left = limit;
	}

	@Override
	public int available() throws IOException {
		return Math.min(super.available(), left);
	}
	
	@Override
	public int read() throws IOException {
		if (left == 0) {
			return -1;
		}
		
		int result = super.read();
		if (result != -1) {
			--left;
		}
		
		return result;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (left == 0) {
			return -1;
		}
		
		len = Math.min(left, len);
		int result = super.read(b, off, len);
		if (result != -1) {
			left -= len;
		}
		return result;
	}
	
	/**
	 * Mark is <i>not</i> supported
	 */
	@Override
	public boolean markSupported() {
		return false;
	}
}
