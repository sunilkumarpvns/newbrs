package com.elitecore.core.commons.fileio;

import static com.elitecore.commons.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.commons.fileio.EliteFileWriter.Builder;
import com.elitecore.core.commons.fileio.base.BaseFileStream;
import com.elitecore.core.commons.fileio.loactionalloactor.IFileLocationAllocater;
import com.elitecore.core.commons.util.sequencer.ISequencer;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.TaskScheduler;

/**
 * 
 * @author dhavan
 *
 */

public class EliteFileWriter extends BaseFileStream {
	
	private EliteTextFileAppender textFileAppender;
	private IFileLocationAllocater fileLocationAllocator;
	
	private static final PathEvaluator IDENTITY_EVALUATOR = new PathEvaluator() {
		
		@Override
		public String evaluate(String dynamicPath) {
			return dynamicPath;
		}
	};
	
	public static class Builder {
		
		private int rollingType; 
		private String prefixFileName;
		private String fileHeader;
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
		private Map<RollingTypeConstant, Integer> rollingTypeMap;
		private PathEvaluator pathEvaluator = IDENTITY_EVALUATOR;
		private TimeSource timesource = TimeSource.systemTimeSource();
		
		public Builder rollingType(int rollingType) {
			this.rollingType = rollingType;
			return this;
		}
		
		public Builder prefixFileName(@Nonnull String prefixFileName) {
			this.prefixFileName = prefixFileName;
			return this;
		}
		
		public Builder fileHeader(@Nullable String fileHeader) {
			this.fileHeader = fileHeader;
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
		
		public Builder rollingTypeMap (@Nullable Map<RollingTypeConstant, Integer> rollingTypeMap) {
			this.rollingTypeMap = rollingTypeMap;
			return this;
		}

		public EliteFileWriter build() throws IOException {
			if (rollingTypeMap != null) {
				return new EliteFileWriter(prefixFileName, fileName, activeFileExt, destinationPath, fileHeader, rollingTypeMap, createBlankFiles,
						cdrSequenceRequired, pattern, appendZerosInCDRSequenceNumber, sequencer, rolloverListener, taskScheduler, pathEvaluator,
						timesource);
			} else {
				checkState(rollingType == SIZE_BASED_ROLLING
						|| rollingType == TIME_BASED_ROLLING
						|| rollingType == RECORD_BASED_ROLLING, "Unknown rolling type: " + rollingType);
				
				return new EliteFileWriter(prefixFileName, fileName, activeFileExt, destinationPath, rollingType, fileHeader, 
						rollingUnit, createBlankFiles, cdrSequenceRequired, pattern, appendZerosInCDRSequenceNumber, 
						sequencer, rolloverListener, taskScheduler, pathEvaluator, timesource);
			}
		}

		public Builder pathEvaluator(@Nullable PathEvaluator pathEvaluator) {
			if (pathEvaluator != null) {
				this.pathEvaluator = pathEvaluator;
			}
			return this;
		}

		public Builder timesource(TimeSource timesource) {
			this.timesource = timesource;
			return this;
		}

	}
	
	private EliteFileWriter(String strPrefixFileName, String strFilename, String activeFileExt, 
			String strPath, int rolling, String fileHeader, long rollingUnit, boolean bCreateBlankFiles, 
			boolean bCdrSequenceRequired, String strPattern,boolean bAppendZerosInCDRSequenceNo,ISequencer sequencer, 
			IRolloverListener rolloverListener, TaskScheduler taskScheduler, PathEvaluator pathEvaluator,
			TimeSource timesource) throws IOException {
		this.taskScheduler = taskScheduler;
		if(rolling == SIZE_BASED_ROLLING){
			textFileAppender = new SizeBasedRollingFileAppender(strPrefixFileName, strFilename,activeFileExt,
					strPath,fileHeader,rollingUnit,bCreateBlankFiles,bCdrSequenceRequired,strPattern,
					bAppendZerosInCDRSequenceNo,sequencer,rolloverListener, pathEvaluator, timesource);
		}else if(rolling == TIME_BASED_ROLLING){
			textFileAppender = new TimeBasedRollingFileAppender(strPrefixFileName, strFilename,activeFileExt,
					strPath,fileHeader,rollingUnit,bCreateBlankFiles,bCdrSequenceRequired,strPattern,
					bAppendZerosInCDRSequenceNo,sequencer,rolloverListener, pathEvaluator, timesource);
		}else if (rolling == RECORD_BASED_ROLLING) {
			textFileAppender = new RecordBasedRollingFileAppender(strPrefixFileName, strFilename,activeFileExt,
					strPath,fileHeader,rollingUnit,bCreateBlankFiles,bCdrSequenceRequired,strPattern,
					bAppendZerosInCDRSequenceNo,sequencer,rolloverListener, pathEvaluator, timesource);
		}
	}
	
	private EliteFileWriter(String strPrefixFileName, String strFilename, String activeFileExt, String strPath, 
			String fileHeader, Map<RollingTypeConstant, Integer> rollingTypeMap, boolean bCreateBlankFiles, 
			boolean bCdrSequenceRequired, String strPattern, boolean bAppendZerosInCDRSequenceNo, ISequencer sequencer, 
			IRolloverListener rolloverListener, TaskScheduler taskScheduler, PathEvaluator pathEvaluator,
			TimeSource timesource) throws IOException {
		
		this.taskScheduler = taskScheduler;
		textFileAppender = new EliteRollingTaskFileAppender(strPrefixFileName, strFilename, activeFileExt, strPath, 
				fileHeader, rollingTypeMap, bCreateBlankFiles, bCdrSequenceRequired, strPattern, bAppendZerosInCDRSequenceNo, 
				sequencer, rolloverListener, null, pathEvaluator, timesource);
	}

	public void appendRecord(String s){
		if (textFileAppender != null)
			textFileAppender.append(s);
	}

	public void appendRecordln(String s) {
		if (textFileAppender != null)
			textFileAppender.appendLine(s);
	}

	public void open() throws IOException {
		if (textFileAppender != null)
			textFileAppender.open();
	}	

	public void close() {
		super.close();
		if (textFileAppender != null)
			textFileAppender.close();
		
		if(fileLocationAllocator != null)
			fileLocationAllocator.disconnect();
	}

	public void flush() {
		if(textFileAppender != null) {
			textFileAppender.flush();
		}
	}

	public void doRollOver(boolean bForceFullRollOver){
		if(textFileAppender!=null){
			textFileAppender.doRollover(bForceFullRollOver);
		}	
	}

	public void setFileLocationAllocator(IFileLocationAllocater fileLocationAllocator) {
		this.fileLocationAllocator = fileLocationAllocator;
	}

	public void registerAlertListener (AlertListener alertListener) {
		if (textFileAppender != null ) {
			textFileAppender.registerAlertListener(alertListener);
		}
	}
	
	private class SizeBasedRollingFileAppender extends EliteTextFileAppender {
		private  long maxFileSize = 10 * 1024; // let 10 KB the default max size of file

		public SizeBasedRollingFileAppender(String strPrefixFileName, String strFilename,String activeFileExt,
				String strDestinationPath,final String strCSVHeader,final long rollUnit, boolean bCreateBlankFiles, 
				boolean bCdrSequenceRequired,String strPattern,boolean bAppendZerosInCDRSequenceNo,
				ISequencer sequencer, IRolloverListener rolloverListener, PathEvaluator pathEvaluator,
				TimeSource timesource) throws IOException{
			super(strPrefixFileName, strFilename,activeFileExt, strDestinationPath,strCSVHeader,
					bCreateBlankFiles,bCdrSequenceRequired,strPattern,bAppendZerosInCDRSequenceNo,
					sequencer,rolloverListener, pathEvaluator, timesource);
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
	
	private class RecordBasedRollingFileAppender extends EliteTextFileAppender {
		private  long maxNumberOfRecords=EliteFileWriter.RECORD_BASED_MAX_NUMBER_OF_RECORDS;
		public RecordBasedRollingFileAppender(String strPrefixFileName, String strFilename,String activeFileExt,
				String strDestinationPath,final String strCSVHeader,final long rollUnit, boolean bCreateBlankFiles, 
				boolean bCdrSequenceRequired,String strPattern,boolean bAppendZerosInCDRSequenceNo,
				ISequencer sequencer, IRolloverListener rolloverListener, PathEvaluator pathEvaluator,
				TimeSource timesource) throws IOException{
			super(strPrefixFileName, strFilename,activeFileExt, strDestinationPath,strCSVHeader,
					bCreateBlankFiles,bCdrSequenceRequired,strPattern,bAppendZerosInCDRSequenceNo,
					sequencer,rolloverListener, pathEvaluator, timesource);
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


	private class TimeBasedRollingFileAppender extends EliteTextFileAppender {
		private boolean isRollingRequired = false;
		private final long rollUnit;

		public TimeBasedRollingFileAppender(String strPrefixFileName, String strFilename,String activeFileExt,
				String strDestinationPath,final String strCSVHeader,final long rollUnit, boolean bCreateBlankFiles, 
				boolean bCdrSequenceRequired,String strPattern, boolean bAppendZerosInCDRSequenceNo, ISequencer sequencer, 
				IRolloverListener rolloverListener, PathEvaluator pathEvaluator, TimeSource timesource) throws IOException{
			super(strPrefixFileName,strFilename,activeFileExt, strDestinationPath,strCSVHeader,
					bCreateBlankFiles,bCdrSequenceRequired,strPattern,bAppendZerosInCDRSequenceNo,
					sequencer,rolloverListener, pathEvaluator, timesource);
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
	
	public class EliteRollingTaskFileAppender extends EliteTextFileAppender{
		
		private List<EliteRollingType> fileAppenderList;
		
		public EliteRollingTaskFileAppender(String strPrefixFileName, String strFilename,String activeFileExt, String strPath, String fileHeader,
				Map<RollingTypeConstant, Integer> rollingTypeMap,boolean bCreateBlankFiles, boolean bCdrSequenceRequired,String strPattern, 
				boolean bAppendZerosInCDRSequenceNo,ISequencer sequencer, IRolloverListener rolloverListener,
				EliteRollingTypeFactory rollingTypeFactory, PathEvaluator pathEvaluator, TimeSource timesource) throws IOException {
			
			super(strPrefixFileName, strFilename, activeFileExt, strPath, fileHeader, bCreateBlankFiles, bCdrSequenceRequired, 
					strPattern, bAppendZerosInCDRSequenceNo, sequencer, rolloverListener, pathEvaluator, timesource);
			
			fileAppenderList = new ArrayList<EliteRollingType>();
			
			if(rollingTypeFactory != null){
				if(rollingTypeMap != null && rollingTypeMap.size() > 0){
					for(Entry<RollingTypeConstant, Integer> rollingType : rollingTypeMap.entrySet()){
						EliteRollingType rollingTypeWriter = rollingTypeFactory.getEliteRollingType(rollingType.getKey(),rollingType.getValue());
						if(rollingTypeWriter != null){
							fileAppenderList.add(rollingTypeWriter);
						}
					}
				}
			}else{
				if(rollingTypeMap != null && rollingTypeMap.size() > 0){
					for (Entry<RollingTypeConstant, Integer> rollingType : rollingTypeMap.entrySet()) {
						if(rollingType.getKey() == RollingTypeConstant.TIME_BASED_ROLLING){
							fileAppenderList.add(new TimeBaseRolling(rollingType.getValue()));
						}else if(rollingType.getKey() == RollingTypeConstant.SIZE_BASED_ROLLING){
							fileAppenderList.add(new SizeBaseRolling(rollingType.getValue()));
						}else if(rollingType.getKey() == RollingTypeConstant.RECORD_BASED_ROLLING){
							fileAppenderList.add(new RecordBaseRolling(rollingType.getValue()));
						}
					}
				}
			}
		}
		
		@Override
		public boolean isFileRollingRequired(File file) {
			for (EliteRollingType rollingUnits : fileAppenderList){
				if(rollingUnits.isRollingRequired(file))
					return true;
			}
			return false;
		}

		@Override
		public IFileLocationAllocater getFileLocationAllocator() {
			return fileLocationAllocator;
		}

		public class RecordBaseRolling implements EliteRollingType{
			
			private long maxRecord = 0 ;
			
			public RecordBaseRolling(long rollUnit) {
				maxRecord = rollUnit;
			}

			@Override
			public boolean isRollingRequired(File file) {
				return getIAppendLineCDRCount() >= maxRecord;
			}
		}

		public class SizeBaseRolling implements EliteRollingType{

			private long maxFileSize;
			
			public SizeBaseRolling(long rollUnit) {
				maxFileSize = rollUnit * 1024;
			}

			@Override
			public boolean isRollingRequired(File file) {
				return file.length() > maxFileSize;
			}
		}

		public class TimeBaseRolling implements EliteRollingType{

			private boolean isRollingRequired = false;
			private final long rollUnit;
			
			public TimeBaseRolling(long rollUnit) {
				this.rollUnit = rollUnit;
				TimeBasedRollingTask rollingTask = new TimeBasedRollingTask();
				taskScedulerFutureList.add(taskScheduler.scheduleIntervalBasedTask(rollingTask));
			}

			@Override
			public boolean isRollingRequired(File file) {
				return isRollingRequired;
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
}
