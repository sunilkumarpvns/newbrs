package com.elitecore.commons.io;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;

import org.junit.Test;
import org.mockito.Mockito;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.NullLogger;

/**
 * 
 * @author narendra.pathai
 *
 */
public class CloseablesTest {

	@Test
	public void testClose_ShouldSkipClosing_WhenCloseablePassedIsNull(){
		Closeables.closeQuietly((Closeable)null);
	}
	
	@Test
	public void testClose_ShouldSkipNullCloseablesPassed(){
		Closeables.closeQuietly(null,null,null);
	}
	
	@Test
	public void testClose_ShouldSwallowIOException_WhenExceptionOccursWhileClosing() throws IOException{
		ByteArrayInputStream stream = new ByteArrayInputStreamThrowingExceptionOnClose();
		
		Closeables.closeQuietly(stream);
	}

	@Test
	public void testClose_WithMultipleCloseables_ShouldSkipNull(){
		Closeables.closeQuietly((Closeable[])null);
	}
	
	@Test
	public void testClose_WithMultipleCloseables_ShouldSwallowIOException_WhenExceptionOccursWhileClosing() throws IOException{
		ByteArrayInputStream stream1 = new ByteArrayInputStreamThrowingExceptionOnClose();
		ByteArrayInputStream stream2 = new ByteArrayInputStreamThrowingExceptionOnClose();
		
		Closeables.closeQuietly(stream1, stream2);
	}
	
	@Test
	public void testClose_ShouldCloseTheCloseablePassed() throws IOException{
		ByteArrayInputStream stream = spy(new ByteArrayInputStream(new byte[]{}));
		
		Closeables.closeQuietly(stream);
		
		verify(stream).close();
	}
	
	@Test
	public void testClose_WithMultipleCloseables_ShouldCloseAllTheCloseablesPassed() throws IOException{
		ByteArrayInputStream stream1 = spy(new ByteArrayInputStream(new byte[]{}));
		ByteArrayInputStream stream2 = spy(new ByteArrayInputStream(new byte[]{}));
		ByteArrayInputStream stream3 = spy(new ByteArrayInputStream(new byte[]{}));
		
		Closeables.closeQuietly(stream1,stream2, stream3);
		
		verify(stream1).close();
		verify(stream2).close();
		verify(stream3).close();
	}
	
	@Test
	public void testClose_ShouldLogTraceSwallowedException_WhenExceptionOccursWhileClosing() throws IOException{
		ILogger spyLogger = spy(new NullLogger());
		LogManager.setDefaultLogger(spyLogger);
		
		Closeables.closeQuietly(new ByteArrayInputStreamThrowingExceptionOnClose());
		
		verify(spyLogger).trace(any(IOException.class));
	}
	
	@Test
	public void testClose_WithMultipleCloseables_ShouldLogTraceSwallowedExceptions_WhenExceptionOccursWhileClosing() throws IOException{
		ILogger spyLogger = spy(new NullLogger());
		LogManager.setDefaultLogger(spyLogger);
		
		Closeables.closeQuietly(new ByteArrayInputStreamThrowingExceptionOnClose(),
								new ByteArrayInputStreamThrowingExceptionOnClose());
		
		verify(spyLogger, times(2)).trace((Throwable) any());
	}
	
	private class ByteArrayInputStreamThrowingExceptionOnClose extends ByteArrayInputStream{
		
		public ByteArrayInputStreamThrowingExceptionOnClose() {
			super(new byte[]{});
		}

		@Override
		public void close() throws IOException {
			super.close();
			throw new IOException();
		}
	}
	
	@Test
	public void testClose_forServerSocket_ShouldSkipClosing_IfServerSocketPassedIsNull() {
		ServerSocket serverSocket = null;
		Closeables.closeQuietly(serverSocket);
	}
	
	@Test
	public void testClose_forServerSocket_ShouldCloseServerSocket_IfNotNull() throws IOException {
		ServerSocket serverSocket = spy(new ServerSocket());
		Closeables.closeQuietly(serverSocket);
		verify(serverSocket).close();
	}
	
	@Test
	public void testClose_forServerSocket_ShouldSwallowIOException_WhenExceptionOccursWhileClosing() throws IOException {
		ServerSocket actualSocket = new ServerSocket();
		ServerSocket serverSocketThrowingException = spy(actualSocket);
		Mockito.doThrow(IOException.class).when(serverSocketThrowingException).close();
		Closeables.closeQuietly(serverSocketThrowingException);
		//we need to close the actual socket to prevent leaking resources
		Closeables.closeQuietly(actualSocket);
	}
	
	@Test
	public void testClose_forServerSocket_ShouldLogTraceSwallowedException_WhenExceptionOccursWhileClosing() throws IOException {
		NullLogger spyLogger = spy(new NullLogger());
		LogManager.setDefaultLogger(spyLogger);
		ServerSocket actualSocket = new ServerSocket();
		ServerSocket serverSocketThrowingException = spy(actualSocket);
		Mockito.doThrow(IOException.class).when(serverSocketThrowingException).close();
		Closeables.closeQuietly(serverSocketThrowingException);
		//we need to close the actual socket to prevent leaking resources
		Closeables.closeQuietly(actualSocket);
		
		//verification
		verify(spyLogger).trace(any(IOException.class));
	}
}
