package com.elitecore.core.commons.fileio;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.commons.alert.Events;
import com.elitecore.core.commons.fileio.loactionalloactor.BaseCommonFileAllocator;
import com.elitecore.core.commons.fileio.loactionalloactor.FileAllocatorException;
import com.elitecore.core.commons.fileio.loactionalloactor.IFileLocationAllocater;
import com.elitecore.core.commons.util.sequencer.ISequencer;


public abstract class EliteFileAppender {
	
	private static final String MODULE = "File Appender";
	
	protected static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private File activeFile ;
	private String baseFilenamePrefix = "";
	private String baseFilenameSuffix = "";
	private String fileTimestampFormat = "";
	private String prefixFileName="";
	private String fileExtension;
	private String destinationPath;
	private String fileHeader;
	private String activeEilteExt;
	private boolean bCreateBlankFiles = false;
	private int iAppendLineCDRCount = 0;
	
	private boolean bCdrSequenceRequired;
	private String strPattern; 
	private IRolloverListener rolloverListener;
	private boolean rolloverProcessing = false;
	private ISequencer sequencer;
	private boolean hasRolloverFailed = false;
	private List<AlertListener> fileAlertListeners;
	private PathEvaluator pathEvaluator;

	private TimeSource timesource;
	
	public EliteFileAppender(String fileName,String activeFileExt,String destinationPath, boolean bCreateBlankFiles, PathEvaluator pathEvaluator,
			TimeSource timesource) throws IOException {
		this("",fileName,activeFileExt,destinationPath,null, bCreateBlankFiles,false,null,false,null,null, pathEvaluator, timesource);
	}

	public EliteFileAppender(String fileName,String activeFileExt,String destinationPath,String fileHeader, boolean bCreateBlankFiles, PathEvaluator pathEvaluator, TimeSource timesource) throws IOException {
		this("",fileName,activeFileExt,destinationPath,fileHeader,bCreateBlankFiles,false,null,false,null,null, pathEvaluator, timesource);
	}
		
	public EliteFileAppender(String prefixFileName, String fileName,String activeFileExt,String destinationPath,String fileHeader, 
			boolean bCreateBlankFiles,boolean bCdrSequenceRequired,String strPattern,boolean bAppendZerosInCDRSequenceNo,
			ISequencer sequencer, IRolloverListener rolloverListener, PathEvaluator pathEvaluator, TimeSource timesource) throws IOException {
		this.timesource = timesource;
		this.prefixFileName = prefixFileName != null ? prefixFileName : this.prefixFileName;
		this.destinationPath = destinationPath;
		this.fileHeader = fileHeader;
		this.fileAlertListeners = new ArrayList<AlertListener>();
		this.pathEvaluator = pathEvaluator;

		if (activeFileExt == null)
			activeFileExt = "";
		
		this.bCreateBlankFiles = bCreateBlankFiles;
		
		this.activeEilteExt = activeFileExt;
		
		destinationPath = pathEvaluator.evaluate(destinationPath);
		File destinationDir = new File(destinationPath);
		
		if(!destinationDir.exists()){
			try {
				boolean isCreated = destinationDir.mkdirs();
				if (!isCreated) {
					LogManager.getLogger().warn(MODULE, "Problem creating destination folder " + destinationDir);
				}
			} catch (SecurityException se) {
				LogManager.getLogger().warn(MODULE, "Security problem creating destination folder " + destinationDir);
				throw new IOException("Security problem creating folder " + destinationDir +", reason : " + se.getMessage());
			}
		}
		
		Date date = new Date(timesource.currentTimeInMillis());
		SimpleDateFormat formatter = null;
		
		parseFileName(fileName);
		
		if (this.fileTimestampFormat == null || this.fileTimestampFormat.equals("")){
			this.fileTimestampFormat = "yyyy-MM-dd_HHmmss_S";
		}

		try{
			formatter = new SimpleDateFormat(this.fileTimestampFormat);
		} catch (IllegalArgumentException e){
			LogManager.getLogger().warn(MODULE, "Timestamp Format is invalid. Reason: " + e.getMessage() + " Format: " +  this.fileTimestampFormat);
			LogManager.getLogger().warn(MODULE, "Default format will be used. Format: yyyy-MM-dd_HHmmss_S");
			this.fileTimestampFormat = "yyyy-MM-dd_HHmmss_S";
			formatter = new SimpleDateFormat(this.fileTimestampFormat);
		}
		String strDate = formatter.format(date);

		String newFilename = "";
		if (baseFilenameSuffix!= null && baseFilenameSuffix.length() > 0 ){
			newFilename = this.prefixFileName + baseFilenamePrefix + strDate+ baseFilenameSuffix + "." + activeFileExt;
		} else {
			newFilename = this.prefixFileName + baseFilenamePrefix + strDate + "." + activeFileExt;
		}

		this.bCdrSequenceRequired=bCdrSequenceRequired;
		this.strPattern=strPattern;
		this.sequencer = sequencer;
		this.rolloverListener=rolloverListener;

		File newFile = new File(destinationDir, newFilename);
		try{
			newFile.createNewFile();
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "ERROR creating file. Reason: " + e.getMessage());
		}
		setFile(newFile);

	}
	
	private void parseFileName(String fileName){
		int curlyBraceIndex = -1;
		String timestampFormat = "";

		char[] fileNameArray = fileName.toCharArray();
		String newFileName = "";
		try{
			for (int i=0 ; i<fileNameArray.length ; i++){
				if (fileNameArray[i] == '\\'){
					if (fileNameArray.length > i+1){	
						i++;
						if (fileNameArray[i]!= '\\'){
							newFileName += fileNameArray[i];
						}
					}
				} else if (fileNameArray[i] == '{'){
					boolean hasClosingBrace = false;
					curlyBraceIndex = newFileName.length();
					i++;
					for ( ; i<fileNameArray.length ; i++){
						if (fileNameArray[i] != '}'){
							if (fileNameArray[i] != '\\'){
								timestampFormat += fileNameArray[i];
							}
						} else {
							hasClosingBrace = true;
							break;
						}
					}
					if (hasClosingBrace){
						this.fileTimestampFormat = timestampFormat;
					} else {
						curlyBraceIndex = -1;
						newFileName += "{" + timestampFormat;
					}

				} else {
					newFileName = newFileName + fileNameArray[i];
				}
			}
			fileName = newFileName;
			
			if (curlyBraceIndex == -1){
				if (fileName.indexOf('.') != -1){
					curlyBraceIndex = fileName.lastIndexOf('.');
				} else {
					curlyBraceIndex = fileName.length();
				}
			}
			
			if (fileName.indexOf('.') != -1){
				baseFilenamePrefix = fileName.substring(0,curlyBraceIndex);
				baseFilenameSuffix = fileName.substring(curlyBraceIndex,fileName.lastIndexOf('.'));
				fileExtension = fileName.substring(fileName.lastIndexOf('.')+1);
			}else {
				baseFilenamePrefix = fileName.substring(0,curlyBraceIndex);
				baseFilenameSuffix = fileName.substring(curlyBraceIndex);
				fileExtension = "";
			}
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error in file name parsing. Reason: " + e.getMessage());
			
			fileName = removeEscapeChar(fileName);
			if (fileName.indexOf('{') != -1){
				newFileName  = fileName.substring(0 , fileName.indexOf('{'));
				if (fileName.indexOf('.')!= -1){
					newFileName += fileName.substring(fileName.lastIndexOf('.')+1);
				}
			} else {
				newFileName = fileName;
			}
			
			fileName = newFileName;
			LogManager.getLogger().info(MODULE, "File Name: "+ fileName +" will be used");
			baseFilenamePrefix = fileName;
			baseFilenameSuffix = "";
			
			if (fileName.indexOf('.') != -1){
				fileExtension = fileName.substring(fileName.lastIndexOf('.')+1);
			}else {
				fileExtension = "";
			}
		}
	}
	
	private String removeEscapeChar(String str){
		char[] charArray = str.toCharArray();
		str = "";
		for (int i=0 ; i<charArray.length ; i++){
			if (charArray[i]!='\\'){
				str += charArray[i];
			}
		}
		return str;
	}
	
	public abstract boolean isFileRollingRequired(File file);
	public abstract void close();
	
	public synchronized void doRollover(){
		doRollover(false);
	}
	
	//ForceFull ROll Over than bForceFullRollOver will be set to true..
	public void doRollover(boolean bForcefullRollOver){
		
		if(!rolloverProcessing) {
			synchronized (this) {
				if(!rolloverProcessing) {
					rolloverProcessing = true;
				}else {
					return;
				}
			}
		}
		try {
			LogManager.getLogger().debug(MODULE, "Rollover called for file : " + getActiveFile().getAbsolutePath());
			
			if(!bCreateBlankFiles && iAppendLineCDRCount<1){
				LogManager.getLogger().debug(MODULE, "Count : "+iAppendLineCDRCount);
				return;
			}
					
			if(!bForcefullRollOver && !isFileRollingRequired(activeFile)){
				LogManager.getLogger().debug(MODULE, "Rollover already performed for " + getActiveFile() + " possibly by other thread.");
				return;		
			}
			
			/*
			 * From here on code execution steps matters a lot.
			 * 1) apply sequencing to the current input file.
			 * 2) create new file
			 * 3) if file created, roll the file
			 * 
			 */
			applySeqToActiveFile(getActiveFile());
			LogManager.getLogger().debug(MODULE, "Count : "+iAppendLineCDRCount+". Count reset to 0. Moving ahead for rollover.");
			iAppendLineCDRCount = 0;
	
			Date date = new Date(timesource.currentTimeInMillis());
			SimpleDateFormat formatter = null;
			formatter = new SimpleDateFormat(this.fileTimestampFormat);
			String strDate = formatter.format(date);
			String newFilename="";
			if (baseFilenameSuffix!= null && baseFilenameSuffix.length() > 0 ){
				newFilename = prefixFileName + baseFilenamePrefix + strDate + baseFilenameSuffix + "." + activeEilteExt;
			} else {
				newFilename = prefixFileName + baseFilenamePrefix + strDate + "." + activeEilteExt;
			}
			String destinationPath = pathEvaluator.evaluate(this.destinationPath);
			String strFilePath = destinationPath + FILE_SEPARATOR + newFilename;
			File newFile1 = new File(strFilePath);
			LogManager.getLogger().trace(MODULE, "New File to be created : " + newFile1.getAbsolutePath());
			boolean isNewFileCreated = false;
			try {

				File parent = newFile1.getParentFile();
				if (parent!=null && parent.exists() == false) {
					parent.mkdirs();
				}
					
				isNewFileCreated = newFile1.createNewFile();

				//localPrintWriter = new PrintWriter(new BufferedWriter( new FileWriter(newFile1, true)));

				if (isNewFileCreated) {
					LogManager.getLogger().debug(MODULE, "New File created successfully.");
				}
			} catch (IOException e) {
				LogManager.getLogger().warn(MODULE, "New File creation failed during file rollover, reason : " + e.getMessage());
				notifyAlertListeners(Events.ROLLOVER_FAILED, "Rollover failed for file " + getActiveFile().getName() + ", Reason: "  + e.getMessage());
				hasRolloverFailed = true;
			}
	
			if (isNewFileCreated) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "New File created successfully.");
				}
				File fileToRollover = getActiveFile();
				setFile(newFile1);
				
				try {
					this.openNewFileChannel();
				} catch (IOException e1) {
					LogManager.getLogger().trace(MODULE, e1);
				}
				//printWriter.close();
				//Rename old file - remove "active" from filename
				
				String strActiveFile = fileToRollover.getAbsolutePath();
				String strRollingFile = strActiveFile.substring(0,strActiveFile.lastIndexOf('.'));
				
				LogManager.getLogger().trace(MODULE, "Rolling File Name : "+strRollingFile);
				LogManager.getLogger().trace(MODULE, "Rolling File Extension : "+fileExtension);
				LogManager.getLogger().trace(MODULE, "Renaming the active file.");
				String fileName;
				if(fileExtension != null && fileExtension.trim().length() > 0)
					fileName = strRollingFile+"."+fileExtension;
				else
					fileName = strRollingFile;
				File tempFile = new File(fileName);
				fileToRollover.renameTo(tempFile);
				File newFile = tempFile;
				
				if (hasRolloverFailed) {
					hasRolloverFailed = false;
					notifyAlertListeners(Events.ROLLOVER_BACK_TO_NORMAL, "Rollover Restored for file " + fileToRollover.getName());
				}
				processFileAllocator(newFile);
				
				LogManager.getLogger().warn(MODULE, "Active File renamed to "+tempFile.getAbsolutePath());
				LogManager.getLogger().trace(MODULE, "New Active file : " + newFile.getAbsolutePath());
				
				serializeSequencer();
				LogManager.getLogger().trace(MODULE, "Rollover completed and new file is  " + getActiveFile().getAbsolutePath());
			}
		}catch(Exception e) {
			LogManager.getLogger().trace(MODULE, e);
		}finally {
			this.rolloverProcessing = false;
		}
	}
	
	private void applySeqToActiveFile(File activeFile) {
		String fileName = activeFile.getAbsolutePath();
		String fileWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
		String fileNameWithoutExt = fileWithoutExt.substring(fileName.lastIndexOf(FILE_SEPARATOR)+1);
		String filePathWithoutFileName =  fileWithoutExt.substring(0,fileName.lastIndexOf(FILE_SEPARATOR));
		fileName =  applySequencing(fileNameWithoutExt) + fileName.substring(fileName.lastIndexOf('.'));
		File file = new File(filePathWithoutFileName + FILE_SEPARATOR + fileName);
		boolean rename = activeFile.renameTo(file);
		if (rename) {
			setFile(file);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Applying sequence to file " + getActiveFile().getAbsolutePath() + " failed");
			}
		}
	}

	protected final void serializeSequencer() {
		if(sequencer != null && bCdrSequenceRequired && rolloverListener!=null)
			rolloverListener.serializeSequencer();
	}

	protected final void processFileAllocator(File newFile) {
		try{
			if(getFileLocationAllocator()!=null){
				String fileName = newFile.getName();
				newFile = getFileLocationAllocator().manageExtension(newFile, BaseCommonFileAllocator.STAR_WILDCARD_CHARACTER, BaseCommonFileAllocator.UIP_EXTENSION,null); 
				if(newFile != null){
					File file = getFileLocationAllocator().transferFile(newFile);
					if(file!=null) {
						if(!getFileLocationAllocator().postOperation(file)){
							if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
								LogManager.getLogger().error(MODULE, "Post Process for the CSV files for FILE REMOTE ALLOCATION unsuccessfull, Extension changed to pof.");
							getFileLocationAllocator().manageExtension(newFile, BaseCommonFileAllocator.STAR_WILDCARD_CHARACTER, BaseCommonFileAllocator.POF_EXTENSION,null);
							file.delete();
						}
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Error in uploading file : "+newFile.getName());
					}
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in STARTING operation of REMOTE FILE ALLOCATION of file : "+fileName);
				}
			}
		}catch (FileAllocatorException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error in REMOTE FILE ALLOCATION of file : "+newFile.getName()+", Reason : "+e.getMessage());
		}
	}

	protected final String applySequencing(String strRollingFile) {
		if(sequencer != null && bCdrSequenceRequired){
			String strCdrCounter = sequencer.getNextSequence();
			
			if("prefix".equalsIgnoreCase(strPattern)){
				strRollingFile = strCdrCounter + "_"+strRollingFile;
			}else if("suffix".equalsIgnoreCase(strPattern)){
				strRollingFile = strRollingFile + "_" +strCdrCounter;
			}
		}
		return strRollingFile;
	}

	protected abstract void openNewFileChannel() throws IOException;
	
	
	public final void setFile(File file){
		this.activeFile = file;
	}

	public final void setFilename(String strFilename) {
		this.baseFilenamePrefix = strFilename;
	}

	public final void setDestinationDir(String strDestinationDir) {
		this.destinationPath = strDestinationDir;
	}

	public final File getActiveFile() {
		return activeFile;
	}
	
	protected int getCDRCount(){
		return 0;
	}
	
	protected final String fillChar(String input, int length, char chr){
		
		if (input != null && input.length() >0){

			StringBuilder stringBuffer = new StringBuilder();
			for(int i = input.length(); i<=length; i++){
				stringBuffer.append(chr);
			}
			stringBuffer.append(input);
			return stringBuffer.toString();
		}
		return "";
	}
	
	public ISequencer getCDRSequence(){
		return sequencer;
	}
	
	protected final int getIAppendLineCDRCount(){
		return iAppendLineCDRCount;
	}
	
	protected void incrementAppendLineCounter() {
		this.iAppendLineCDRCount++;
	}
	
	public abstract IFileLocationAllocater getFileLocationAllocator();
	
	protected final String getFileHeader() {
		return fileHeader;
	}
	
	protected final String getDestinationPath() {
		return destinationPath;
	}
	
	protected final String getFileExtension() {
		return fileExtension;
	}
	
	public void registerAlertListener (AlertListener fileAlertListener) {
		this.fileAlertListeners.add(fileAlertListener);
	}
	
	private void notifyAlertListeners (Events event, String message) {
		for (AlertListener alertListener : fileAlertListeners) {
			alertListener.generateAlert(event, message);
		}
	}
}
