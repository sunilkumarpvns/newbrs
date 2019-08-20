/**
 * 
 */
package com.elitecore.core.commons.fileio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import com.elitecore.commons.logging.LogManager;

/**
 * @author pulin
 *
 */
public class ElitePrintWriter {
	
	private PrintWriter printWriter;
	
	private boolean closed = false;
	
	private AtomicInteger atomicInteger;
	
	private final String MODULE = "ELITE-PRINT-WRITER";
	/**
	 * 
	 */
	public ElitePrintWriter(File activeFile) throws IOException{
		closed = false;
		atomicInteger = new AtomicInteger(0);
		printWriter = new PrintWriter(new BufferedWriter(new FileWriter(activeFile, true)));
	}
	
	public void println(String x) {
		atomicInteger.incrementAndGet();
		printWriter.println(x);
		atomicInteger.decrementAndGet();
	}
	
	public void print(String x) {
		atomicInteger.incrementAndGet();
		printWriter.print(x);
		atomicInteger.decrementAndGet();
	}
	
	public void flush(){
		printWriter.flush();
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
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		
		printWriter.close();

	}
}
