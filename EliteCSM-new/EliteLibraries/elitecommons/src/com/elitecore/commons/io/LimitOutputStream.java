package com.elitecore.commons.io;

import static com.elitecore.commons.base.Preconditions.checkArgument;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A stream that limits the no of bytes that can be written.
 * 
 * @author narendra.pathai
 *
 */
public class LimitOutputStream extends FilterOutputStream {
	private int left;
	
	/**
	 * Constructs a limiting output stream over {@code out} with {@code limit} no of bytes.
	 * 
	 * @param out a non-null stream which is to be limited
	 * @param limit a non-negative integer
	 * @throws IllegalArgumentException if {@code limit} is negative
	 */
	public LimitOutputStream(OutputStream out, int limit) {
		super(out);
		checkNotNull(out, "outputStream is null");
		checkArgument(limit >= 0, "limit should be non-negative: " + limit);
		this.left = limit;
	}
	
	
	/**
	 * Writes the specified byte to the underlying output stream if limit is not exceeded
	 */
	@Override
	public void write(int b) throws IOException {
		if (left <= 0) {
			return;
		}
		
		super.write(b);
		--left;
	}
	
	/**
	 * Writes bytes until limit is reached. May write subset of bytes from {@code b} to
	 * the underlying output stream if remaining capacity < {@code b.length}.
	 * 
	 * <p><code><pre>
	 * Usage:
	 * LimitOutputStream limitStream = new LimitOutputStream(new ByteArrayOutputStream(), 4);
	 * ...
	 * limitStream.write(new byte[]{1,2,3,4,5,6,7});
	 * ...
	 * </pre></code>
	 * 
	 * will write <code>{1,2,3,4}</code> to the {@code ByteArrayOutputStream}
	 * <br/>
	 * <br/>
	 */
	@Override
	public void write(byte[] b) throws IOException {
		super.write(b);
	}
	
	/**
	 * Writes bytes until limit is reached. May write subset of bytes starting at
	 * offset {@code off} from {@code b} to the underlying output stream if remaining capacity < {@code len}.
	 * 
	 * <p><code><pre>
	 * Usage:
	 * LimitOutputStream limitStream = new LimitOutputStream(new ByteArrayOutputStream(), 4);
	 * ...
	 * limitStream.write(new byte[]{1,2,3,4,5,6,7}, 1, 6);
	 * ...
	 * </pre></code>
	 * 
	 * will write <code>{2,3,4,5}</code> to the {@code ByteArrayOutputStream}
	 * <br/>
	 * <br/>
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);
	}
}
