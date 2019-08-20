package com.elitecore.aaa.core.drivers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.data.AttributesRelation;
import com.elitecore.aaa.core.data.StripAttributeRelation;
import com.elitecore.aaa.core.util.FileLocationParser;
import com.elitecore.aaa.radius.service.acct.TimeBoundryRollingIntervalTask;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.commons.alert.Events;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.fileio.EliteFileWriter.Builder;
import com.elitecore.core.commons.fileio.IRolloverListener;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.commons.fileio.loactionalloactor.BaseCommonFileAllocator;
import com.elitecore.core.commons.fileio.loactionalloactor.FileAllocatorException;
import com.elitecore.core.commons.util.sequencer.ISequencer;
import com.elitecore.core.commons.util.sequencer.SynchronizedSequencer;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.servicex.ServiceRequest;
import static com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration.PREFIX;
import static com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration.SUFFIX;

public abstract class ClassicCSVAcctDriver extends BaseClassicCSVAcctDriver {
	private static String MODULE = "CLASSIC-CSV-ACCT-Drvr";
	
	public static final String GLOBAL_SEQUENCER="GLOBAL_SEQUENCER";
	private IRolloverListener rolloverListener; 
	private Hashtable<String,EliteFileWriter> htFileWriter = new Hashtable<String,EliteFileWriter>();
	public static final String KEY="KEY";
	private String strHeaderLine;
	private static final String CSV_EXTENSION = "csv";
	private ThreadLocal<SimpleDateFormat> simpleDateFormat;
	private List<StripAttributeRelation> stripAttributeRelList;
	protected String currentLocation;
	
	private String sequenceRange;
	private String prefix;
	private String suffix;
	protected boolean bIsSequencingEnabled;
	protected String startSeq;
	protected String endSeq;
	private static final Long SEQUENCER_INITIALIZED = 1L;
	private static final Long SEQUENCER_NOT_INITIALIZED = 0L;

	private static final String NONE = "NONE";
	private static final String LOCAL = "LOCAL";
	public ConcurrentHashMap<String,Object> cdrSequenceCounterMap;
	private static ISequencer cdrGlobalSequencer;
	ISequencer newSequencer = null;
	private FileLocationParser fileLocationParser;
	private AlertListener alertListener;

	@Nonnull
	private TimeSource timesource;
	
	public ClassicCSVAcctDriver(ServerContext serverContext) { // NOSONAR
		this(serverContext, TimeSource.systemTimeSource()); 
	}
	
	ClassicCSVAcctDriver(ServerContext serverContext, TimeSource timesource) {
		super(serverContext);
		this.timesource = timesource;
	}
	
	// All errors in CSV are considered as permanent
	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		
		List<AttributesRelation> attributeRelationList = getAttributesRelationList();
		if(attributeRelationList == null || attributeRelationList.size() == 0){
			throw new DriverInitializationFailedException("No valid Attributes Mapping Found in driver : "+getName());
		}
		
		if(getCDRTimeStampFormat() != null && !getCDRTimeStampFormat().trim().isEmpty()) {
			try{
				simpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
					@Override
					protected SimpleDateFormat initialValue() {
						return new SimpleDateFormat(getCDRTimeStampFormat());
					}
				};
			}catch(IllegalArgumentException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid CDRTimeStampFormat: " + getCDRTimeStampFormat() + " for driver :" + getName());
			}
		}
		
		alertListener = new AlertListener() {
			
			@Override
			public void generateAlert(Events event, String message) {
				switch (event) {
				case ROLLOVER_FAILED:
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL, Alerts.CDR_STORAGE_PROBLEM,MODULE, message, 0, message);
					break;
				case ROLLOVER_BACK_TO_NORMAL:
					getServerContext().generateSystemAlert(AlertSeverity.CLEAR, Alerts.CDR_STORAGE_PROBLEM,MODULE, message, 0, message);
					break;
				default:
					break;
				}
				
			}
		};
		
		if(currentLocation != null) {
			File file = new File(currentLocation);
			try{
				if(file.isAbsolute()){
					if(file.exists()){
						if(!file.canWrite()){
							LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", no write access at locaiton: "+getFileLocation());

							createFolderAtDefaultLocation();
						}
					}else{

						LogManager.getLogger().info(MODULE, "Driver: " + getName() + ", directory : " + getFileLocation() + " does not exist. Creating the directory.");

						try{
							if(!file.mkdirs()){
								LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", problem in creating the directory: "+getFileLocation());

								createFolderAtDefaultLocation();
							}
						}catch(SecurityException ex){
							LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", problem in creating the directory: "+getFileLocation());

							createFolderAtDefaultLocation();
						}
					
					}
				}else{
					currentLocation = getServerContext().getServerHome() + File.separator + currentLocation;
					file = new File(currentLocation);
					if(file.exists()){
						if(!file.canWrite()){	
							LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", no write access at locaiton: "+getFileLocation());

							createFolderAtDefaultLocation();
						}
					}else{

						LogManager.getLogger().info(MODULE, "Driver: " + getName() + ", directory : " + getFileLocation() + " does not exist. Creating the directory.");

						try{
							if(!file.mkdirs()){
								LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", problem in creating the directory: "+getFileLocation());

								createFolderAtDefaultLocation();
							}
						}catch(SecurityException ex){		
							LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", problem in creating the directory: "+getFileLocation());
							
							createFolderAtDefaultLocation();
						}
					
					}
					
				}
			}catch(SecurityException ex){	
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", no read permission at location: " + getFileLocation());
				}
				
				createFolderAtDefaultLocation();
			}
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", file location not configured. Using default location.");
			
			createFolderAtDefaultLocation();
		}
		
		fileLocationParser = new FileLocationParser(currentLocation, getName(), new FileLocationParser.AttributeValueProvider() {
			
			@Override
			public boolean isValidAttributeID(String attrID) {
				return ClassicCSVAcctDriver.this.isAttributeID(attrID);
			}
			
			@Override
			public String getAttributeValue(ServiceRequest serviceRequest, String attrID) {
				return ClassicCSVAcctDriver.this.getAttributeValue(attrID, serviceRequest);
			}
		});
		
		this.stripAttributeRelList = getStripAttributeRelationList();
		
		strHeaderLine = getHeader();
		if(strHeaderLine != null && strHeaderLine.equalsIgnoreCase("TRUE")){
			strHeaderLine = getConfiguredCSVHeaderLine();
		}else{
			strHeaderLine = "";
		}
		
		this.sequenceRange = getSequenceRange();
		if(this.sequenceRange != null){
			String regx = "[a-z0-9A-Z]*\\[[a-z0-9A-Z]+\\-[a-z0-9A-Z]+\\][a-z0-9A-Z]*";
			if(Pattern.matches(regx, sequenceRange)){
				this.prefix = this.sequenceRange.substring(0, this.sequenceRange.indexOf('['));
				this.suffix = this.sequenceRange.substring(this.sequenceRange.indexOf(']')+1);
				this.startSeq = this.sequenceRange.substring(this.sequenceRange.indexOf('[')+1, this.sequenceRange.indexOf('-'));
				this.endSeq = this.sequenceRange.substring(this.sequenceRange.indexOf('-')+1, this.sequenceRange.indexOf(']'));
				newSequencer = new SynchronizedSequencer(startSeq, endSeq, prefix, suffix);
				try {
					newSequencer.init();
					this.bIsSequencingEnabled = true;
				} catch (InitializationFailedException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Invalid sequence specified: " + this.sequenceRange + ". So sequencing will be disabled.");
					}
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Invalid sequence specified: " + this.sequenceRange + ". So sequencing will be disabled.");
				}
			}
		}
		
		if(bIsSequencingEnabled){
			Long key = null;
			cdrSequenceCounterMap= (ConcurrentHashMap<String, Object>) setCDRSequenceObjectfromFile(getCounterFileName());
			if(cdrSequenceCounterMap != null && cdrSequenceCounterMap.size() > 0 && (key=(Long)cdrSequenceCounterMap.get(KEY))!=null) {
				if(getGlobalization() && key.equals(SEQUENCER_INITIALIZED)) {
					cdrGlobalSequencer = (SynchronizedSequencer) cdrSequenceCounterMap.get(GLOBAL_SEQUENCER);

					if(cdrGlobalSequencer==null){
						try {
							cdrGlobalSequencer = (ISequencer) newSequencer.clone();
						} catch (CloneNotSupportedException e) {
							cdrGlobalSequencer = new SynchronizedSequencer(startSeq, endSeq,prefix,suffix);
							try {
								cdrGlobalSequencer.init();
							} catch (InitializationFailedException e1) {
								bIsSequencingEnabled = false;
							}
						}
						if(bIsSequencingEnabled)
							cdrSequenceCounterMap.put(GLOBAL_SEQUENCER, cdrGlobalSequencer);
					}else{
						if(!cdrGlobalSequencer.equals(newSequencer)){
							try {
								cdrGlobalSequencer = (SynchronizedSequencer) newSequencer.clone();
							} catch (CloneNotSupportedException e) {
								cdrGlobalSequencer = new SynchronizedSequencer(startSeq, endSeq,prefix,suffix);
								try {
									cdrGlobalSequencer.init();
								} catch (InitializationFailedException e1) {
									bIsSequencingEnabled = false;
								}
							}
							if(bIsSequencingEnabled){
								cdrSequenceCounterMap = new ConcurrentHashMap<String, Object>();
								cdrSequenceCounterMap.put(KEY, 1L);
								cdrSequenceCounterMap.put(GLOBAL_SEQUENCER, cdrGlobalSequencer);
							}
						}
					}
				}else if(getGlobalization() && key.equals(SEQUENCER_NOT_INITIALIZED)){
					try {
						cdrGlobalSequencer = (ISequencer) newSequencer.clone();
					} catch (CloneNotSupportedException e1) {
						cdrGlobalSequencer = new SynchronizedSequencer(startSeq, endSeq,prefix,suffix);
						try {
							cdrGlobalSequencer.init();
						} catch (InitializationFailedException e) {
							bIsSequencingEnabled = false;
						}
					}
					cdrSequenceCounterMap= new ConcurrentHashMap<String, Object>();
					if(bIsSequencingEnabled)
						cdrSequenceCounterMap.put(GLOBAL_SEQUENCER, cdrGlobalSequencer);

				}else if(!getGlobalization() && key.equals(SEQUENCER_INITIALIZED)){
					cdrSequenceCounterMap= new ConcurrentHashMap<String, Object>();
				}
			}

			if(getGlobalization()) {
				if(cdrGlobalSequencer == null){
					try {
						cdrGlobalSequencer = (SynchronizedSequencer)newSequencer.clone();
					} catch (CloneNotSupportedException e1) {
						cdrGlobalSequencer = new SynchronizedSequencer(startSeq, endSeq, prefix, suffix);
						try {
							cdrGlobalSequencer.init();
						} catch (InitializationFailedException e) {
							bIsSequencingEnabled = false;
						}
					}
					if(bIsSequencingEnabled)
						cdrSequenceCounterMap.put(GLOBAL_SEQUENCER, cdrGlobalSequencer);
					
				}
				cdrSequenceCounterMap.put(KEY,SEQUENCER_INITIALIZED);
				rolloverListener=new IRolloverListener() {
					public void serializeSequencer() {
						serializeCounterMapInFile(getCounterFileName(), cdrSequenceCounterMap);
					}
				};
			}else{
				cdrSequenceCounterMap.put(KEY,SEQUENCER_NOT_INITIALIZED);
			}

		}
		setFileAllocator();
		
		File parentDir = null;
		parentDir = new File(getFileLocation());
		try{
			if(parentDir.exists()){
				changeFilesExtension(parentDir,INP_EXTENSION,CSV_EXTENSION);
				if(bIsSequencingEnabled && cdrSequenceCounterMap!=null){
					serializeCounterMapInFile(getCounterFileName(), cdrSequenceCounterMap);
				}	
			}
		}catch(SecurityException ex){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Error in converting .inp files to .csv at location :" + getFileLocation() + " for driver : " + getName());
		}
		
		Map<RollingTypeConstant, Integer> rollingTypeMap = getRollingTypeMap();
		
		if(rollingTypeMap != null && !(rollingTypeMap.isEmpty()) && rollingTypeMap.containsKey(RollingTypeConstant.TIME_BOUNDRY_ROLLING)){
			int timeBoundryRollingUnit = rollingTypeMap.get(RollingTypeConstant.TIME_BOUNDRY_ROLLING);
			if(timeBoundryRollingUnit > 0)
				startTimeBoundryTask(timeBoundryRollingUnit);
		}
	}
	
	private void createFolderAtDefaultLocation() throws DriverInitializationFailedException{
		currentLocation = getServerContext().getServerHome()+File.separator+"data"+File.separator+"csvfiles";
		if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
			LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", creating folder at default location: " + currentLocation);
		}
		File file = new File(currentLocation);
		try{
			if(!file.exists()){
				if(!file.mkdirs()){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Driver: " + getName() + ", cannot create directory at default location: " + currentLocation);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Driver: " + getName() + ", directory created successfully at default location.");
				}
			}else{
				LogManager.getLogger().debug(MODULE, "Driver: " + getName() + ", directory:" + currentLocation + " already exists.");
				if(!file.canWrite()){
					LogManager.getLogger().error(MODULE, "Driver: " + getName() + ", no write access in Default directory: "+ currentLocation);
					throw new DriverInitializationFailedException("Driver: " + getName() + ", problem in creating directory at configured and default location.");
				}
			}
		}catch(SecurityException ex){
			LogManager.getLogger().error(MODULE, "Driver: " + getName() + ", problem in creating the Default directory: "+ currentLocation);
			throw new DriverInitializationFailedException("Driver: " + getName() + ", problem in creating directory at configured and default location.");
		}
	}
	
	public Map<String,Object> getCdrSequenceCounterMap() {
		return this.cdrSequenceCounterMap;
	}
	
	public ISequencer getCdrGlobalSequence() {
		return cdrGlobalSequencer;
	}
	
	private void setFileAllocator() {
		LogManager.getLogger().trace(MODULE, "Setting File Allocator For Classic-Csv-Acct Driver");
		
		String fileAllocatorType = getAllocatingProtocol();
		if(fileAllocatorType != null && NONE.equals(fileAllocatorType.toUpperCase())) {
			return;
		}
		
		String userName = getUserName();
		String password = getPassword();
		String serverAddress = getIpAddress();
		int serverPort = getPort();
		String destinationLocation = getDestinationLocation();
		String postOperation = "Archive";
		if(getPostOperation() != null)
			postOperation = getPostOperation();
		String archiveLocations = getArchiveLocations();
		int rolloverTime = 5 * 60 * 1000;
		if(getFailOverTime() != 0)
			rolloverTime = getFailOverTime() * 60 * 1000;
		
		try {
			if(!LOCAL.equals(fileAllocatorType.toUpperCase()) && (serverAddress == null || serverPort < 0 || destinationLocation == null)) {
				throw new FileAllocatorException("One or more of the parameters are missing");
			}
			
			if(BaseCommonFileAllocator.ARCHIVE.equals(postOperation.toUpperCase()) && (archiveLocations == null || archiveLocations.trim().isEmpty())) {
				throw new FileAllocatorException("Archive Location not specified");
			}
			
			setFileLocationAllocator(fileAllocatorType, rolloverTime, userName, password, serverAddress, destinationLocation,serverPort,postOperation,getFileLocation(),archiveLocations,CSV_EXTENSION);
		} catch (FileAllocatorException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error while setting File Allocator in Driver " + getName() + ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
	}
	
	@Override
	protected void handleServiceRequest(ServiceRequest serviceRequest) throws DriverProcessFailedException{

		LogManager.getLogger().info(MODULE, "Request Process through Classic-Csv-Accct Driver :"+getName());
		
		String strPrefixFileName = getNameFromArray(getFileNameAttributes(), serviceRequest);
		String strFolderName = getNameFromArray(getFolderNameAttributes(), serviceRequest); 
		
		ISequencer tempSequencer = null;
		
		strPrefixFileName=(strPrefixFileName!=null)?strPrefixFileName+"_":"";

		if(strFolderName ==null){
			strFolderName = (getDefaultDirName()!=null)?getDefaultDirName():"";
		}

		String destinationPath = fileLocationParser.getLocation(serviceRequest) + File.separator + strFolderName;
		final String strWriterKey = destinationPath+ File.separator +strPrefixFileName;

		EliteFileWriter fileWriter = (EliteFileWriter)htFileWriter.get(strWriterKey);

		if(fileWriter == null){
			synchronized (htFileWriter) {
				fileWriter = (EliteFileWriter)htFileWriter.get(strWriterKey);
				if(fileWriter == null){
					try{
						if(!bIsSequencingEnabled){
							fileWriter = new Builder()
									.prefixFileName(strPrefixFileName)
									.fileName(getFileName())
									.activeFileExt(INP_EXTENSION)
									.destinationPath(destinationPath)
									.fileHeader(strHeaderLine)
									.rollingTypeMap(getRollingTypeMap())
									.pattern(getPattern())
									.sequencer(tempSequencer)
									.taskScheduler(getTaskScheduler())
									.createBlankFiles(getCreateBlankFile())
									.cdrSequenceRequired(bIsSequencingEnabled)
									.timesource(timesource)
									.build();
							
						}else{
							if(!getGlobalization()){
								if(cdrSequenceCounterMap!=null && cdrSequenceCounterMap.size()>0){
									tempSequencer =  (SynchronizedSequencer) (cdrSequenceCounterMap.containsKey(strWriterKey)?cdrSequenceCounterMap.get(strWriterKey):null);
									if(tempSequencer == null){
										try{
											tempSequencer = (SynchronizedSequencer)newSequencer.clone();
										}catch(CloneNotSupportedException ex){
											tempSequencer = new SynchronizedSequencer(startSeq, endSeq, prefix, suffix);
											tempSequencer.init();
										}
										cdrSequenceCounterMap.put(strWriterKey, tempSequencer);
									}else{
										if(!newSequencer.equals(tempSequencer)){
											tempSequencer = new SynchronizedSequencer(startSeq, endSeq, prefix, suffix);
											tempSequencer.init();
											cdrSequenceCounterMap.put(strWriterKey, tempSequencer);
										}
									}
								}
								fileWriter = new Builder()
										.prefixFileName(strPrefixFileName)
										.fileName(getFileName())
										.activeFileExt(INP_EXTENSION)
										.destinationPath(destinationPath)
										.fileHeader(strHeaderLine)
										.rollingTypeMap(getRollingTypeMap())
										.pattern(getPattern())
										.sequencer(tempSequencer)
										.rolloverListener(new IRolloverListener(){
											public void serializeSequencer(){
												serializeCounterMapInFile(getCounterFileName(), cdrSequenceCounterMap);
											}
										})
										.taskScheduler(getTaskScheduler())
										.createBlankFiles(getCreateBlankFile())
										.cdrSequenceRequired(bIsSequencingEnabled)
										.timesource(timesource)
										.build();

							}else{
								fileWriter = new Builder()
										.prefixFileName(strPrefixFileName)
										.fileName(getFileName())
										.activeFileExt(INP_EXTENSION)
										.destinationPath(destinationPath)
										.fileHeader(strHeaderLine)
										.rollingTypeMap(getRollingTypeMap())
										.pattern(getPattern())
										.sequencer(cdrGlobalSequencer)
										.rolloverListener(rolloverListener)
										.taskScheduler(getTaskScheduler())
										.createBlankFiles(getCreateBlankFile())
										.cdrSequenceRequired(bIsSequencingEnabled)
										.timesource(timesource)
										.build();

							}
						}
						fileWriter.setFileLocationAllocator(getFileLocationAllocator());
						fileWriter.registerAlertListener(alertListener);
						htFileWriter.put(strWriterKey,fileWriter);
					}catch(Exception e){
						getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.CDR_STORAGE_PROBLEM, MODULE, "CDR dump failed", 0, "CDR dump failed");
						LogManager.getLogger().error(MODULE, e.getMessage());
						throw new DriverProcessFailedException(MODULE,e);
					}
				}
			}
		}
		fileWriter.appendRecordln(getConfiguredCSVLine(serviceRequest));
		LogManager.getLogger().info(MODULE, "Configured Attributes dumped through Classic-Csv-Acct Driver");
		LogManager.getLogger().info(MODULE, "Location Of Dumped File:" +destinationPath);
	}

	protected String getStrippedValue(String strAttributeId, String strValueToBeStripped){
		String strippedValue = strValueToBeStripped;
		
		if(strAttributeId!=null && strValueToBeStripped!=null && this.stripAttributeRelList!=null ){
			int stripAttributeRelListSize = stripAttributeRelList.size();
			StripAttributeRelation stripAttributeRelation = null;
			for(int i=0;i<stripAttributeRelListSize;i++){
				stripAttributeRelation = stripAttributeRelList.get(i);
				String pattern = null;
				String seperator;
				if(stripAttributeRelation!=null){
					if(strAttributeId.equalsIgnoreCase(stripAttributeRelation.getAttributeId())){
						pattern = stripAttributeRelation.getPattern();
						seperator = stripAttributeRelation.getSeparator();
						
						int indexOfSeprator = strValueToBeStripped.indexOf(seperator);
						if(indexOfSeprator!=-1){
							if(pattern.equalsIgnoreCase(SUFFIX)){
								strippedValue =  strValueToBeStripped.substring(0, indexOfSeprator).trim();
							}else if(pattern.equalsIgnoreCase(PREFIX)){
								strippedValue = strValueToBeStripped.substring(indexOfSeprator+1, strValueToBeStripped.length()).trim();
							}
						}
						return strippedValue;
					}
				}
			}
		}
		return strippedValue;
	}
	
	protected SimpleDateFormat getSimpleDateFormat() {
		return simpleDateFormat.get();
	}
	@Override
	public String cdrflush() {
		String strResult = "CDR Flushed Successfully";
		try{
			if(htFileWriter==null)
				strResult = "No Inp File Found To Convert";
			else if(htFileWriter.size()==0 )
				strResult = "No Inp File Found To Convert";
			else{
				Iterator<String> fileWriterIterator = htFileWriter.keySet().iterator();
				while(fileWriterIterator.hasNext()){
					EliteFileWriter fileWriter = (EliteFileWriter) htFileWriter.get(fileWriterIterator.next());
					fileWriter.doRollOver(true);
				}
			}
		}catch(Exception e){
			strResult = "Failed";
		}
		return strResult;		
	}
	
	@Override
	public void stop() {
		LogManager.getLogger().info(MODULE, "Stoping driver: " + getName());
		if(htFileWriter==null)
			return;
		else if(htFileWriter.size()==0 )
			return;
		else{								
			for(EliteFileWriter fileWriter : htFileWriter.values()){
				try{
					fileWriter.flush();
					fileWriter.close();
				}catch(Exception  e) {
					LogManager.getLogger().error(MODULE, "Error while stoping FileWriter reason : " + e.getMessage());
				}				
			}
		}
	
	}
	
	protected final String getFileLocation(){
		return currentLocation;
	}

	protected ISequencer getCdrGlobalSequencer(){
		return cdrGlobalSequencer;
	}
	
	protected String getPrefix(){
		return prefix;
	}
	
	protected String getSuffix(){
		return suffix;
	}
	
	protected boolean isSequencingEnabled(){
		return bIsSequencingEnabled;
	}
	
	protected String getStartSequence(){
		return startSeq;
	}
	
	protected String getEndSequence(){
		return endSeq;
	}
	
	private void startTimeBoundryTask(long timeBoundryRollingUnit){
		getServerContext().getTaskScheduler().scheduleIntervalBasedTask(new TimeBoundryRollingIntervalTask(timeBoundryRollingUnit) {
			@Override
			public void execute(AsyncTaskContext context) {
				String cdrFlushResultString = cdrflush();

				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Driver: "+getName()+" - Performed Rollover action is: " +cdrFlushResultString);
				}
			}
		});
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "TimeBoundry rolling task scheduled with: "+timeBoundryRollingUnit+" min,for driver: "+getName());
	}
	
	protected abstract String getCDRTimeStampHeader();
	protected abstract String getCDRTimeStampFormat();
	protected abstract String getCDRTimeStampPosition();
	
	protected abstract String getEnclosingChar();
	
	protected abstract String[] getFileNameAttributes();
	protected abstract String[] getFolderNameAttributes();
	protected abstract String getCounterFileName();
	protected abstract String getAllocatingProtocol();
	protected abstract String getUserName();
	protected abstract String getPassword();
	protected abstract int getPort();
	protected abstract String getDestinationLocation();
	protected abstract String getIpAddress();
	protected abstract int getFailOverTime();
	protected abstract String getPostOperation();
	protected abstract String getArchiveLocations();
	protected abstract boolean getCreateBlankFile();
	protected abstract String getmultiValueDelimeter();
	protected abstract String getDefaultDirName();
	protected abstract List<StripAttributeRelation> getStripAttributeRelationList();
	protected abstract String getNameFromArray(String[] fileAttributes,ServiceRequest request) ;
	protected abstract String getConfiguredCSVLine(ServiceRequest request) throws DriverProcessFailedException;
	protected abstract String getConfiguredCSVHeaderLine() throws DriverInitializationFailedException ;
	protected abstract boolean isAttributeID(String str);
	protected abstract String getAttributeValue(String attrID, ServiceRequest request);
	protected abstract Map<RollingTypeConstant, Integer> getRollingTypeMap();		
}
