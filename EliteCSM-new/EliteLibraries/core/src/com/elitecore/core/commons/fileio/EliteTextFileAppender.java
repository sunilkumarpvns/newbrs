package com.elitecore.core.commons.fileio;

import java.io.File;
import java.io.IOException;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.sequencer.ISequencer;

public abstract class EliteTextFileAppender extends EliteFileAppender {
	
	private ElitePrintWriter printWriter;
	private final String MODULE = "ELITE-TEXT-APPENDER";

	public EliteTextFileAppender(String prefixFileName, String fileName, String activeFileExt, String destinationPath, 
			String fileHeader, boolean bCreateBlankFiles, PathEvaluator pathEvaluator, TimeSource timesource) throws IOException {
		this(prefixFileName, fileName,activeFileExt,destinationPath,fileHeader,bCreateBlankFiles,false,null,false,null,null, pathEvaluator, timesource);
	}

	public EliteTextFileAppender(String prefixFileName, String fileName, String activeFileExt, String destinationPath, 
			String fileHeader, boolean bCreateBlankFiles, boolean bCdrSequenceRequired, String strPattern, 
			boolean bAppendZerosInCDRSequenceNo, ISequencer sequencer, IRolloverListener rolloverListener,
			PathEvaluator pathEvaluator, TimeSource timesource) throws IOException {
		super(prefixFileName, fileName, activeFileExt, destinationPath, fileHeader, bCreateBlankFiles, 
				bCdrSequenceRequired, strPattern, bAppendZerosInCDRSequenceNo, sequencer, rolloverListener, pathEvaluator, timesource);
		
		printWriter = new ElitePrintWriter(getActiveFile());

		if(getFileHeader() != null && getFileHeader().trim().length() > 0){
			printWriter.println(getFileHeader());
			printWriter.flush();
		}

	}
	
	public void append(String strData){
		if(isFileRollingRequired(getActiveFile())){
			doRollover();		
		}
		incrementAppendLineCounter();
		printWriter.print(strData);
	}
	
	public void appendLine(String strData){
		if(isFileRollingRequired(getActiveFile())){
			doRollover();		
		}
		incrementAppendLineCounter();
		printWriter.println(strData);
	}


	protected void openNewFileChannel() throws IOException {
		ElitePrintWriter oldLocalPrintWriter = printWriter;
		
		ElitePrintWriter newLocalPrintWriter = new ElitePrintWriter(getActiveFile());
		if(getFileHeader() != null && getFileHeader().trim().length() > 0){
			newLocalPrintWriter.println(getFileHeader());
			newLocalPrintWriter.flush();
		}
		
		printWriter = newLocalPrintWriter;
		
		oldLocalPrintWriter.close();
	}

	
	public final void close() {
		if (printWriter != null){
			try {
				printWriter.close();
			}catch(IOException e) {
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
	
	public final void flush() {
		if(printWriter != null) {
			String strActiveFile = getActiveFile().getName();
			String strRollingFile = strActiveFile.substring(0,strActiveFile.lastIndexOf('.'));
			strRollingFile = applySequencing(strRollingFile);
			File newfile = new File(getDestinationPath() + FILE_SEPARATOR + strRollingFile + "." + getFileExtension());
			getActiveFile().renameTo(newfile);
			processFileAllocator(newfile);
			serializeSequencer();
			printWriter.flush();
		}
	}
	
	public final void open() throws IOException {
		if (printWriter == null)
			printWriter = new ElitePrintWriter(getActiveFile());
	}
	
}
