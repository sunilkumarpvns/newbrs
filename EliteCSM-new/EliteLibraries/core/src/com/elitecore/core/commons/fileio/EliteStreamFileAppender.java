package com.elitecore.core.commons.fileio;

import java.io.File;
import java.io.IOException;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.sequencer.ISequencer;

public abstract class EliteStreamFileAppender extends EliteFileAppender {
	
	private EliteBufferedOutputStream outStream;

	public EliteStreamFileAppender(String fileName, String activeFileExt, String destinationPath, boolean bCreateBlankFiles,
			PathEvaluator pathEvaluator, TimeSource timesource) throws IOException {
		this("",fileName,activeFileExt,destinationPath, bCreateBlankFiles,false,null,false,null,null, pathEvaluator, timesource);
	}
	
	public EliteStreamFileAppender(String prefixFileName, String fileName, String activeFileExt, String destinationPath, 
			boolean bCreateBlankFiles, boolean bCdrSequenceRequired, String strPattern, boolean bAppendZerosInCDRSequenceNo, 
			ISequencer sequencer, IRolloverListener rolloverListener, PathEvaluator pathEvaluator, TimeSource timesource) throws IOException {
		super(prefixFileName, fileName, activeFileExt, destinationPath, null, bCreateBlankFiles, bCdrSequenceRequired, 
				strPattern, bAppendZerosInCDRSequenceNo, sequencer, rolloverListener, pathEvaluator, timesource);
		outStream = new EliteBufferedOutputStream(getActiveFile());
	}

	
	public void append(byte[] data) throws IOException {
		if(isFileRollingRequired(getActiveFile())){
			doRollover();		
		}
		incrementAppendLineCounter();
		outStream.write(data);
	}
	
	protected void openNewFileChannel() throws IOException {
		EliteBufferedOutputStream oldLocalPrintWriter = outStream;
		
		EliteBufferedOutputStream newLocalPrintWriter = new EliteBufferedOutputStream(getActiveFile());

		outStream = newLocalPrintWriter;
		
		if (oldLocalPrintWriter != null){
			try {
				oldLocalPrintWriter.close();
			} catch (IOException e) {
				LogManager.getLogger().trace("", e);
			}
		}
	}

	
	public final void close() {
		if (outStream != null){
			try {
				outStream.close();
			} catch (IOException e) {
				LogManager.getLogger().trace("", e);
			}
		}
	}
	
	public final void flush() {
		if(outStream != null) {
			String strActiveFile = getActiveFile().getName();
			String strRollingFile = strActiveFile.substring(0,strActiveFile.lastIndexOf('.'));
			getActiveFile().renameTo(new File(getDestinationPath()+FILE_SEPARATOR+strRollingFile+"."+getFileExtension()));
			try {
				outStream.flush();
			} catch (IOException e) {
				LogManager.getLogger().trace("", e);
			}
		}
	}
	
	public final void open() throws IOException {
		if (outStream == null)
			outStream = new EliteBufferedOutputStream(getActiveFile());
	}
	
}
