/**
 * 
 */
package com.elitecore.core.commons.fileio;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.elitecore.commons.logging.LogManager;


/**
 * @author pulin
 *
 */
public class EliteBufferedOutputStream {

	private boolean closed;
	private BufferedOutputStream outStream;
	private AtomicInteger atomicInteger;
	private final String MODULE = "ELITE-BUFFER-STREAM";
	/**
	 * 
	 */
	public EliteBufferedOutputStream(File activeFile) throws FileNotFoundException{
		closed = false;
		atomicInteger = new AtomicInteger(0);
		outStream = new BufferedOutputStream(new FileOutputStream(activeFile));
	}
	
	public void write(byte[] b) throws IOException{
		atomicInteger.incrementAndGet();
		outStream.write(b);
		atomicInteger.decrementAndGet();
	}
	
	public boolean isProcessingCompleted() {
		if(atomicInteger.get() == 0) {
			return true;
		}
		
		return false;
	}
	
	public void close() throws IOException{
		if(!closed) {
			synchronized (this) {
				if(!closed) {
					closed = true;
				}else {
					throw new IOException("Stream is already closed");
				}
			}
		}

		int haltCount = 0;
		while(atomicInteger.get() != 0 && haltCount < 3) {
			haltCount++;
			LogManager.getLogger().warn(MODULE, "Halt close for " + haltCount + " time");
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				throw new IOException(e);
			}
		}
		
		outStream.close();

	}
	
	public void flush() throws IOException {
		outStream.flush();
	}

}
