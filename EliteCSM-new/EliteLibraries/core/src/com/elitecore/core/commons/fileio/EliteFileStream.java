package com.elitecore.core.commons.fileio;

import static com.elitecore.commons.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.commons.fileio.base.BaseFileStream;
import com.elitecore.core.commons.fileio.loactionalloactor.IFileLocationAllocater;
import com.elitecore.core.commons.util.sequencer.ISequencer;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.TaskScheduler;

public class EliteFileStream extends BaseFileStream {

	private EliteStreamFileAppender textFileAppender;
	private IFileLocationAllocater fileLocationAllocator;
	
	public static class Builder {
		
		private int rollingType; 
		private String fileName;
		private String activeFileExt;
		private String destinationPath;
		private boolean createBlankFiles = false;
		private boolean cdrSequenceRequired = false;
		private String pattern;
		private boolean appendZerosInCDRSequenceNumber = false;
		private ISequencer sequencer;
		private IRolloverListener rolloverListener;
		private TaskScheduler taskScheduler;
		private long rollingUnit;
		private PathEvaluator pathEvaluator;
		private TimeSource timesource = TimeSource.systemTimeSource();
		
		public Builder rollingType(int rollingType) {
			this.rollingType = rollingType;
			return this;
		}
		
		public Builder fileName(@Nonnull String fileName) {
			this.fileName = fileName;
			return this;
		}
		
		public Builder activeFileExt(@Nonnull String activeFileExt) {
			this.activeFileExt = activeFileExt;
			return this;
		}
		
		public Builder destinationPath(@Nonnull String destinationPath) {
			this.destinationPath = destinationPath;
			return this;
		}

		public Builder createBlankFiles(boolean createBlankFiles) {
			this.createBlankFiles = createBlankFiles;
			return this;
		}

		public Builder cdrSequenceRequired(boolean cdrSequenceRequired) {
			this.cdrSequenceRequired = cdrSequenceRequired;
			return this;
		}

		public Builder pattern(@Nonnull String pattern) {
			this.pattern = pattern;
			return this;
		}

		public Builder appendZerosInCDRSequenceNumber(boolean appendZerosInCDRSequenceNumber) {
			this.appendZerosInCDRSequenceNumber = appendZerosInCDRSequenceNumber;
			return this;
		}

		public Builder sequencer(@Nullable ISequencer sequencer) {
			this.sequencer = sequencer;
			return this;
		}

		public Builder rolloverListener(@Nonnull IRolloverListener rolloverListener) {
			this.rolloverListener = rolloverListener;
			return this;
		}

		public Builder taskScheduler(@Nonnull TaskScheduler taskScheduler) {
			this.taskScheduler = taskScheduler;
			return this;
		}

		public Builder rollingUnit(long rollingUnit) {
			this.rollingUnit = rollingUnit;
			return this;
		}
		
		public EliteFileStream build() throws IOException {
			checkState(rollingType == SIZE_BASED_ROLLING
					|| rollingType == TIME_BASED_ROLLING
					|| rollingType == RECORD_BASED_ROLLING, "Unknown rolling type: " + rollingType);
			
			return new EliteFileStream(fileName, activeFileExt, destinationPath, rollingType, rollingUnit, createBlankFiles,
					cdrSequenceRequired, pattern, appendZerosInCDRSequenceNumber, sequencer, rolloverListener, taskScheduler, pathEvaluator, timesource);
		}
		
		public Builder pathEvaluator(@Nullable PathEvaluator pathEvaluator) {
			this.pathEvaluator = pathEvaluator;
			return this;
		}
		
		public Builder timesource(@Nonnull TimeSource timesource) {
			this.timesource = timesource;
			return this;
		}
	}
	
	private EliteFileStream(String strFilename, String activeFileExt, String strPath, int rolling, long rollingUnit,
				boolean bCreateBlankFiles, boolean bCdrSequenceRequired, String strPattern, boolean bAppendZerosInCDRSequenceNo, 
				ISequencer sequencer, IRolloverListener rolloverListener,TaskScheduler taskScheduler, PathEvaluator pathEvaluator,
				TimeSource timesource) throws IOException {
		this.taskScheduler = taskScheduler;
		if(rolling == SIZE_BASED_ROLLING){
			textFileAppender = new SizeBasedRollingFileAppender(strFilename,activeFileExt,strPath,rollingUnit,bCreateBlankFiles,
					bCdrSequenceRequired,strPattern,bAppendZerosInCDRSequenceNo,sequencer,rolloverListener, pathEvaluator, timesource);
		}else if(rolling == TIME_BASED_ROLLING){
			textFileAppender = new TimeBasedRollingFileAppender(strFilename,activeFileExt,strPath,rollingUnit,bCreateBlankFiles,
					bCdrSequenceRequired,strPattern,bAppendZerosInCDRSequenceNo,sequencer ,rolloverListener, pathEvaluator, timesource);
		}else if (rolling == RECORD_BASED_ROLLING) {
			textFileAppender = new RecordBasedRollingFileAppender(strFilename,activeFileExt,strPath,rollingUnit,bCreateBlankFiles,
					bCdrSequenceRequired,strPattern,bAppendZerosInCDRSequenceNo,sequencer,rolloverListener, pathEvaluator, timesource);
		}
	}


	public void appendRecord(byte[] record) throws IOException{
		if (textFileAppender != null)
			textFileAppender.append(record);
	}

	public void open() throws IOException {
		if (textFileAppender != null)
			textFileAppender.open();
	}	

	public void close() {
		super.close();
		if (textFileAppender != null)
			textFileAppender.close();
	}

	public void flush() {
		if(textFileAppender != null) {
			textFileAppender.flush();
		}
	}

	public ISequencer getCounter(){
		if(textFileAppender != null) {
			return textFileAppender.getCDRSequence();
		}
		return null;
	}

	public void doRollOver(boolean bForceFullRollOver){
		if(textFileAppender!=null){
			textFileAppender.doRollover(bForceFullRollOver);
		}	
	}

	public void setFileLocationAllocator(IFileLocationAllocater fileLocationAllocator) {
		this.fileLocationAllocator = fileLocationAllocator;
	}

	private class SizeBasedRollingFileAppender extends EliteStreamFileAppender {
		private  long maxFileSize = 10 * 1024; // let 10 KB the default max size of file

		public SizeBasedRollingFileAppender(String strFilename,String activeFileExt,
				String strDestinationPath,final long rollUnit, boolean bCreateBlankFiles, 
				boolean bCdrSequenceRequired,String strPattern,boolean bAppendZerosInCDRSequenceNo,
				ISequencer sequencer, IRolloverListener rolloverListener, PathEvaluator pathEvaluator,
				TimeSource timesource) throws IOException{
			super("",strFilename,activeFileExt, strDestinationPath,bCreateBlankFiles,bCdrSequenceRequired,
					strPattern,bAppendZerosInCDRSequenceNo,sequencer,rolloverListener, pathEvaluator, timesource);
			this.maxFileSize = rollUnit*1024;
		}

		public boolean isFileRollingRequired(File file) {
			return (file.length() >= getMaxFileSize());
		}

		public long getMaxFileSize() {
			return maxFileSize;
		}

		@Override
		public IFileLocationAllocater getFileLocationAllocator() {
			return fileLocationAllocator;
		}
	}
	
	private class RecordBasedRollingFileAppender extends EliteStreamFileAppender {
		private  long maxNumberOfRecords=EliteFileWriter.RECORD_BASED_MAX_NUMBER_OF_RECORDS;
		public RecordBasedRollingFileAppender(String strFilename,String activeFileExt,
				String strDestinationPath,final long rollUnit, boolean bCreateBlankFiles, 
				boolean bCdrSequenceRequired,String strPattern,boolean bAppendZerosInCDRSequenceNo,
				ISequencer sequencer, IRolloverListener rolloverListener, PathEvaluator pathEvaluator,
				TimeSource timesource) throws IOException{
			super("",strFilename,activeFileExt, strDestinationPath,bCreateBlankFiles,bCdrSequenceRequired,
					strPattern,bAppendZerosInCDRSequenceNo,sequencer,rolloverListener, pathEvaluator, timesource);
			if(rollUnit>0)
				this.maxNumberOfRecords = rollUnit;
		}

		@Override
		public IFileLocationAllocater getFileLocationAllocator() {
			return fileLocationAllocator;
		}

		@Override
		public boolean isFileRollingRequired(File file) {
			return getIAppendLineCDRCount()>=maxNumberOfRecords;
			
		}

	}


	private class TimeBasedRollingFileAppender extends EliteStreamFileAppender {
		private boolean isRollingRequired = false;
		private final long rollUnit;
		
		public TimeBasedRollingFileAppender(String strFilename,String activeFileExt, String strDestinationPath,
				final long rollUnit, boolean bCreateBlankFiles, boolean bCdrSequenceRequired,String strPattern, 
				boolean bAppendZerosInCDRSequenceNo, ISequencer sequencer, IRolloverListener rolloverListener,
				PathEvaluator pathEvaluator, TimeSource timesource) throws IOException{
			super("",strFilename,activeFileExt, strDestinationPath,bCreateBlankFiles,bCdrSequenceRequired,strPattern,
					bAppendZerosInCDRSequenceNo,sequencer,rolloverListener, pathEvaluator, timesource);
			this.rollUnit = rollUnit;
			TimeBasedRollingTask rollingTask = new TimeBasedRollingTask();
			taskScedulerFutureList.add(taskScheduler.scheduleIntervalBasedTask(rollingTask));
		}

		public boolean isFileRollingRequired(File file) {
			return isRollingRequired;
		}

		public synchronized void doRollover(){
			super.doRollover();
		}

		@Override
		public IFileLocationAllocater getFileLocationAllocator() {
			return fileLocationAllocator;
		}
		
		private class TimeBasedRollingTask extends BaseIntervalBasedTask{

			@Override
			public long getInterval() {
				return rollUnit;
			}
			
			@Override
			public long getInitialDelay() {
				return rollUnit;
			}
			
			@Override
			public TimeUnit getTimeUnit() {
				return TimeUnit.MINUTES;
			}

			@Override
			public void execute(AsyncTaskContext context) {
				isRollingRequired = true;
				doRollover();
				isRollingRequired = false;
			}
		}
	}


}
