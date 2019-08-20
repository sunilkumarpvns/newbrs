package com.elitecore.netvertex.gateway.file.util;



import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


/**
 * The Class CrestelServicesOutputStream.
 */
public class CrestelServiceOutputStream{

	/** The b closed flag.  */
	private boolean bClosed;

	/** The buffered out stream. */
	private BufferedOutputStream bufferedOutStream;

	/** The file output stream. */
	private FileOutputStream fileOutputStream;

	/** The file channel. */
	private FileChannel fileChannel;

	/**
	 * Instantiates a new eliteoutputstream object. This constructor creates necessary streams and channel to handle write operations
	 * for the given file. This channel will be used to write data in uncompressed format to the file pass in the constructor.
	 *
	 * @param activeFile the active file
	 * @throws FileNotFoundException the file not found exception
	 */
	CrestelServiceOutputStream(File activeFile) throws FileNotFoundException{
		bClosed = false;
		fileOutputStream = new FileOutputStream(activeFile,true);
		fileChannel = fileOutputStream.getChannel();
		bufferedOutStream = new BufferedOutputStream(fileOutputStream);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.fileio.base.ICrestelServicesOutputStream#write(byte[])
	 */
	
	public void write(byte[] b) throws IOException{
		bufferedOutStream.write(b);
		bufferedOutStream.flush();
		fileOutputStream.getFD().sync();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.fileio.base.ICrestelServicesOutputStream#write(java.nio.ByteBuffer)
	 */

	public int write(ByteBuffer b) throws IOException{
		int writeCount = fileChannel.write(b);
		fileChannel.force(true);
		fileOutputStream.getFD().sync();
		b.clear();
		return writeCount;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.fileio.base.ICrestelServicesOutputStream#close()
	 */

	public void close() throws IOException{
		if(!bClosed) {
			synchronized (this) {
				if(!bClosed) {
					bClosed = true;
				}else {
					throw new IOException("Stream is already closed");
				}
			}
		}

		flush();
		fileChannel.close();
		bufferedOutStream.close();
		fileOutputStream.close();

	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.fileio.base.ICrestelServicesOutputStream#flush()
	 */
	
	public void flush() throws IOException {
		bufferedOutStream.flush();
		fileOutputStream.flush();
	}
}
