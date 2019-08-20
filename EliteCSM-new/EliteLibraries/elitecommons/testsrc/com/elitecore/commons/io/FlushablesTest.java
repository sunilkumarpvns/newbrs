package com.elitecore.commons.io;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.Flushable;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.NullLogger;

public class FlushablesTest {

	private ByteArrayOutputStream streamWithException;
	private ByteArrayOutputStream stream;

	@Before
	public void setUp() {
		streamWithException = new ByteArrayOutputStreamThrowingExceptionOnFlush();
		stream = new ByteArrayOutputStream();
	}

	@Test
	public void testFlushQuietly_ShouldSkipFlushing_WhenFlushablePassedIsNull() {
		Flushables.flushQuietly((Flushable) null);
	}

	@Test
	public void testFlushQuietly_ShouldSwallowIOException_WhenExceptionOccursWhileFlushing() {
		Flushables.flushQuietly(streamWithException);
	}

	@Test
	public void testFlushQuietly_WithMultipleFlushables_ShouldSkipNull() {
		Flushables.flushQuietly((Flushable[]) null);
	}

	@Test
	public void testFlushQuietly_WithMultipleFlushables_ShouldSwallowIOException_WhenExceptionOccursWhileFlushing() {
		ByteArrayOutputStream streamWithException1 = new ByteArrayOutputStreamThrowingExceptionOnFlush();
		Flushables.flushQuietly(streamWithException, streamWithException1);
	}

	@Test
	public void testFlushQuietly_WithMultipleFlushables_ShouldSwallowIOException_WhenExceptionOccursWhileFlushingOneofThem() throws IOException {
		ByteArrayOutputStream stream1 = spy(stream);
		Flushables.flushQuietly(streamWithException, stream1);
		verify(stream1).flush();
	}

	@Test
	public void testFlushQuietly_ShouldFlushTheFlushablePassed() throws IOException {
		ByteArrayOutputStream stream1 = spy(stream);
		Flushables.flushQuietly(stream1);
		verify(stream1).flush();
	}

	@Test
	public void testFlushQuietly_WithMultipleFlushables_ShouldFlushAllTheFlushablesPassed() throws IOException {
		ByteArrayOutputStream stream1 = spy(new ByteArrayOutputStream());
		ByteArrayOutputStream stream2 = spy(new ByteArrayOutputStream());
		ByteArrayOutputStream stream3 = spy(new ByteArrayOutputStream());

		Flushables.flushQuietly(stream1, stream2, stream3);
		verify(stream1).flush();
		verify(stream2).flush();
		verify(stream3).flush();
	}

	@Test
	public void testFlushQuietly_ShouldLogTraceSwallowedException_WhenExceptionOccursWhileFlushing() {
		ILogger spyLogger = spy(new NullLogger());
		LogManager.setDefaultLogger(spyLogger);

		Flushables.flushQuietly(new ByteArrayOutputStreamThrowingExceptionOnFlush());
		verify(spyLogger).trace(any(IOException.class));
	}

	@Test
	public void testFlushQuietly_WithMultipleFlushables_ShouldLogTraceSwallowedExceptions_WhenExceptionOccursWhileFlushing() {
		ILogger spyLogger = spy(new NullLogger());
		LogManager.setDefaultLogger(spyLogger);
		Flushables.flushQuietly( new ByteArrayOutputStreamThrowingExceptionOnFlush(), new ByteArrayOutputStreamThrowingExceptionOnFlush());
		verify(spyLogger, times(2)).trace(any(IOException.class));
	}

	private class ByteArrayOutputStreamThrowingExceptionOnFlush extends ByteArrayOutputStream {

		@Override
		public void flush() throws IOException {
			throw new IOException();
		}
	}

}
