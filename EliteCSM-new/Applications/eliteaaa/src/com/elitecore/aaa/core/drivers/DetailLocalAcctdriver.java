package com.elitecore.aaa.core.drivers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.data.AttributeRelation;
import com.elitecore.aaa.core.util.FileLocationParser;
import com.elitecore.aaa.radius.service.acct.TimeBoundryRollingIntervalTask;
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

public abstract class DetailLocalAcctdriver extends BaseDetailLocalAcctDriver {
	private static String MODULE = "DETAIL-LOCAL-ACCT-Drvr";
	private ConcurrentHashMap<String,Object> cdrSequenceCounterMap;
	private static final String LOCAL_EXTENSION = "local";
	public static final String GLOBAL_SEQUENCER="GLOBAL_SEQUENCER";
	private IRolloverListener rolloverListener; 
	private Hashtable<String,EliteFileWriter> htFileWriter = new Hashtable<String, EliteFileWriter>();
	public static final String KEY="KEY";
	private static final String PREFIX = "PREFIX";
	public static final String INP_EXTENSION = "inp";
	public static final String ATTRIBUTE_IDS = "ATTRIBUTE_IDS";	
	public static final String VENDOR_ID = "VENDOR_ID";
	public static final String ID = "ID";
	public static final String DEFAULT_VALUE = "DEFAULT_VALUE";	
	public static final String USE_DICTIONARY_VALUE = "USE_DICTIONARY_VALUE";
	private SimpleDateFormat simpleDateFormat = null;
	protected String currentLocation;
	
	private boolean bIsSequencingEnabled;
	private String sequenceRange;
	private String prefix;
	private String suffix;
	private String startSeq;
	private String endSeq;
	private static final Long SEQUENCER_INITIALIZED = 1L;
	private static final Long SEQUENCER_NOT_INITIALIZED = 0L;
	private static final String NONE = "NONE";
	private static final String LOCAL = "LOCAL";
	private static ISequencer cdrGlobalSequencer;
	private ISequencer newSequencer = null;
	private FileLocationParser fileLocationParser;
	private AlertListener alertListener;
	
	public DetailLocalAcctdriver(ServerContext serverContext) {
		super(serverContext);
		this.simpleDateFormat = new SimpleDateFormat("E M dd HH:mm:ss yyyy");
	}

	// All errors of detail local driver are permanent
	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		
		alertListener = new AlertListener() {
			
			@Override
			public void generateAlert(Events event, String message) {
				switch (event) {
				case ROLLOVER_FAILED:
					getServerContext().generateSystemAlert(AlertSeverity.CRITICAL, Alerts.CDR_STORAGE_PROBLEM,MODULE, message,0,message);
					break;
				case ROLLOVER_BACK_TO_NORMAL:
					getServerContext().generateSystemAlert(AlertSeverity.CLEAR, Alerts.CDR_STORAGE_PROBLEM,MODULE, message,0,message);
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
							LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", no write access at location: "+getFileLocation());

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
							LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", no write access at location: "+getFileLocation());

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
					LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", no read permission at location :" + getFileLocation());
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
				return DetailLocalAcctdriver.this.isAttributeID(attrID);
			}
			
			@Override
			public String getAttributeValue(ServiceRequest serviceRequest, String attrID) {
				return DetailLocalAcctdriver.this.getAttributeValue(attrID, serviceRequest);
			}
		});

		this.sequenceRange = getSequenceRange();
		if(sequenceRange != null){
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
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Changing the INP files to LOCAL if any.");
		
		File parentDir = null;
		parentDir = new File(currentLocation);
		try{
			if(parentDir.exists()){
				changeFilesExtension(parentDir,INP_EXTENSION,LOCAL_EXTENSION);
				if(bIsSequencingEnabled && cdrSequenceCounterMap!=null){
					serializeCounterMapInFile(getCounterFileName(), cdrSequenceCounterMap);
				}	
			}
		}catch(SecurityException ex){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Error in converting .inp files to .local at location :" + getFileLocation() + " for driver : " + getName());
		}
		
		if(getEventDateFormat() != null){
			try{
				simpleDateFormat.applyPattern(getEventDateFormat());
			}catch(IllegalArgumentException ex){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid CDRTimeStampFormat: " + getEventDateFormat() + " for driver:" + getName() + " .Considering default CDRTimeStampFormat: E M dd HH:mm:ss yyyy.");
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "CDRTimeStampFormat not specified for driver: " + getName() + ". Using default value: E M dd HH:mm:ss yyyy.");
		}
	
		Map<RollingTypeConstant, Integer> rollingTypeMap = getRollingTypeMap();
		
		if(rollingTypeMap != null && !(rollingTypeMap.isEmpty()) && rollingTypeMap.containsKey(RollingTypeConstant.TIME_BOUNDRY_ROLLING)){
			int timeBoundryRollingUnit = rollingTypeMap.get(RollingTypeConstant.TIME_BOUNDRY_ROLLING);
			if(timeBoundryRollingUnit > 0)
				startTimeBoundryTask(timeBoundryRollingUnit);
		}
	}
	
	private void createFolderAtDefaultLocation() throws DriverInitializationFailedException{
		currentLocation = getServerContext().getServerHome()+File.separator+"data"+File.separator+"detail-local-files";
		if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
			LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", creating folder at default location: " + currentLocation);
		}
		File file = new File(currentLocation);
		try{
			if(!file.exists()){
				if(!file.mkdirs()){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Driver: " + getName() + ", cannot create directory at default location: " + currentLocation);

					throw new DriverInitializationFailedException("Driver: " + getName() + ", cannot create directory at default location: " + currentLocation);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Driver: " + getName() + ", directory created successfully at default location.");
				}
			}else{
				LogManager.getLogger().debug(MODULE, "Driver: " + getName() + ", directory:" + currentLocation + " already exists.");
				if(!file.canWrite()){
					LogManager.getLogger().error(MODULE, "Driver: " + getName() + ", no write access in Default directory: "+ currentLocation);
					throw new DriverInitializationFailedException("Driver: " + getName() + ", problem in creating directory at configured and default location");
				}
			}
		}catch(SecurityException ex){
			LogManager.getLogger().warn(MODULE, "Driver: " + getName() + ", problem in creating the Default directory: "+ currentLocation);
			throw new DriverInitializationFailedException("Driver: " + getName() + ", problem in creating directory at configured and default location");
		}
	}

	@Override
	protected void handleServiceRequest(ServiceRequest request) throws DriverProcessFailedException{
		LogManager.getLogger().trace(MODULE, "Processing Request through Detail Local Acct Driver");
		String strPrefixFileName = getNameFromArray(getFileNameAttributes(),request);
		String strFolderName = getNameFromArray(getFolderNameAttributes(), request); 
		
		ISequencer tempSequencer = null;
		
		strPrefixFileName=(strPrefixFileName!=null)?strPrefixFileName+"_":"";

		if(strFolderName ==null){
			strFolderName = (getDefaultDirName()!=null)?getDefaultDirName():"";
		}

		String destinationPath = fileLocationParser.getLocation(request) + File.separator + strFolderName;
		final String strWriterKey = destinationPath+ File.separator +strPrefixFileName;

		EliteFileWriter fileWriter = htFileWriter.get(strWriterKey);

		if(fileWriter == null){
			synchronized (htFileWriter) {
				fileWriter = (EliteFileWriter)htFileWriter.get(strWriterKey);
				if(fileWriter == null){
					try{
						if(!bIsSequencingEnabled ){
							fileWriter = new Builder()
										.prefixFileName(strPrefixFileName)
										.fileName(getFileName())
										.activeFileExt(INP_EXTENSION)
										.destinationPath(destinationPath)
										.rollingTypeMap(getRollingTypeMap())
										.pattern(getPattern())
										.sequencer(tempSequencer)
										.taskScheduler(getTaskScheduler())
										.createBlankFiles(getCreateBlankFile())
										.cdrSequenceRequired(bIsSequencingEnabled)
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
										.build();

							}else{
								fileWriter = new Builder()
										.prefixFileName(strPrefixFileName)
										.fileName(getFileName())
										.activeFileExt(INP_EXTENSION)
										.destinationPath(destinationPath)
										.rollingTypeMap(getRollingTypeMap())
										.pattern(getPattern())
										.sequencer(cdrGlobalSequencer)
										.rolloverListener(rolloverListener)
										.taskScheduler(getTaskScheduler())
										.createBlankFiles(getCreateBlankFile())
										.cdrSequenceRequired(bIsSequencingEnabled)
										.build();

							}
						}
						fileWriter.setFileLocationAllocator(getFileLocationAllocator());
						fileWriter.registerAlertListener(alertListener);
						htFileWriter.put(strWriterKey,fileWriter);
					}catch(Exception e){
						LogManager.getLogger().error(MODULE, e.getMessage());
						throw new DriverProcessFailedException(MODULE, e);
					}
				}
			}
		}
		
		writeAttributes(request, fileWriter, destinationPath);
	}
	
	private void setFileAllocator() {
		LogManager.getLogger().trace(MODULE, "Setting File Allocator For DetailLocal Configuration");
		String fileAllocatorType = getAllocatingProtocol();
		if(fileAllocatorType != null && NONE.equals(fileAllocatorType.toUpperCase())) {
			return;
		}
		
		String userName = getUserName();
		String password = getPassword();
		String serverAddress = getIpAddress();
		int serverPort = getPort();
		String destinationLocation = getDestinationLocation();
		String postOperation = getPostOperation();
		int rolloverTime = getFailOverTime() * 60 * 1000;
		String archiveLocations = getArchiveLocations();
		
		try {
			if(!LOCAL.equals(fileAllocatorType.toUpperCase()) && (serverAddress == null || serverPort < 0 || destinationLocation == null)) {
				throw new FileAllocatorException("One or more of the parameters are missing");
			}
			
			if(BaseCommonFileAllocator.ARCHIVE.equals(postOperation.toUpperCase()) && (archiveLocations == null || archiveLocations.trim().isEmpty())) {
				throw new FileAllocatorException("Archive Location not specified");
			}
			
			setFileLocationAllocator(fileAllocatorType, rolloverTime, userName, password, serverAddress, destinationLocation,serverPort,postOperation,getFileLocation(),archiveLocations,LOCAL_EXTENSION);
		} catch (FileAllocatorException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error while setting File Allocator in Driver " + getName() + ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
	}
	
	private void changeFilesExtension(File file, String sourceExt, String destinationExt){
		if (file.isDirectory()) {
			try {
				File [] fileList = file.listFiles();
				if(fileList != null){
					for (int i=0;i<fileList.length;i++){
						changeFilesExtension(fileList[i],sourceExt,destinationExt);
					}
				}
			}catch (Exception e) {
				LogManager.getLogger().trace(MODULE, e);
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE,"Unable to rename file "+file.getAbsolutePath());
			}
		} else {
			if(file.getName().endsWith("."+sourceExt) || file.getName().endsWith("."+BaseCommonFileAllocator.UIP_EXTENSION)){
				try {
					File newFile = null;
					String fileName =null;
					if(bIsSequencingEnabled){

						if(getGlobalization() && cdrSequenceCounterMap!=null){
							cdrGlobalSequencer = (ISequencer) cdrSequenceCounterMap.get(GLOBAL_SEQUENCER);
							if(cdrGlobalSequencer == null){
								try{
									cdrGlobalSequencer = (ISequencer) newSequencer.clone();
								}catch(CloneNotSupportedException ex){
									cdrGlobalSequencer = new SynchronizedSequencer(startSeq, endSeq, prefix, suffix);
									cdrGlobalSequencer.init();
								}
								cdrSequenceCounterMap.put(GLOBAL_SEQUENCER, cdrGlobalSequencer);
							}
							String strSequence = cdrGlobalSequencer.getSequence();
							cdrGlobalSequencer.increment();
							
							if(getPattern().equalsIgnoreCase(PREFIX))
								fileName = strSequence + "_"+ file.getName().substring(0,file.getName().lastIndexOf("."));
							else
								fileName =file.getName().substring(0,file.getName().lastIndexOf(".")) + "_" + strSequence;
							fileName  =  fileName+"."+destinationExt;
						}else if(!getGlobalization() && cdrSequenceCounterMap!=null) {
							fileName =file.getName().substring(0,file.getName().lastIndexOf(getFileName().substring(0,getFileName().lastIndexOf('.'))));
							ISequencer localSequencer = (ISequencer) cdrSequenceCounterMap.get(file.getParent()+ File.separator + fileName);
							if(localSequencer == null){
								try{
									localSequencer = (ISequencer) newSequencer.clone();
								}catch(CloneNotSupportedException ex){
									localSequencer = new SynchronizedSequencer(startSeq, endSeq, prefix, suffix);
									localSequencer.init();
								}finally{
									cdrSequenceCounterMap.put(file.getParent() + File.separatorChar+ fileName, localSequencer);
								}
							}else{
								if(!localSequencer.equals(newSequencer)){
									localSequencer = (ISequencer)newSequencer.clone();
									cdrSequenceCounterMap.put(file.getParent() + File.separatorChar+ fileName, localSequencer);
								}
							}
							
							String strSequence = localSequencer.getSequence();
							localSequencer.increment();

							if(getPattern().equalsIgnoreCase(PREFIX))
								fileName = strSequence + "_"+file.getName().substring(0,file.getName().lastIndexOf("."));
							else
								fileName =file.getName().substring(0,file.getName().lastIndexOf(".")) + "_" + strSequence;
							fileName = fileName+"."+destinationExt;
						}
					}else{
						fileName = file.getName().substring(0,file.getName().lastIndexOf("."))+"."+destinationExt; 
					}
					if(file != null) {
						if(getFileLocationAllocator()!=null){
							file = getFileLocationAllocator().manageExtension(file, INP_EXTENSION, BaseCommonFileAllocator.UIP_EXTENSION,fileName);
							if(file != null){
								newFile = getFileLocationAllocator().transferFile(file); 
								if(newFile!=null) {
									if(!getFileLocationAllocator().postOperation(newFile)){
										if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
											LogManager.getLogger().error(MODULE,"Post Process for the Detail-Local files for FILE REMOTE ALLOCATION unsuccessfull, Extension changed to pof.");
										getFileLocationAllocator().manageExtension(newFile, BaseCommonFileAllocator.STAR_WILDCARD_CHARACTER, BaseCommonFileAllocator.POF_EXTENSION,fileName);//Extension Changed to Post Operation Failed(.pof) and leave on the same location.
										newFile.delete();
										if(newFile==null){
											if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
												LogManager.getLogger().error(MODULE,"Error in changing the file extension "+BaseCommonFileAllocator.POF_EXTENSION+" for file : "+file.getName());
										}else{
											if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
												LogManager.getLogger().error(MODULE,"Post Process Failed,file extension changed to pof (Post Operation Failed).");
										}

									}else{
										if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
											LogManager.getLogger().trace(MODULE,"File uploded : "+newFile.getName());
									}
								}
							}else{
								if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
									LogManager.getLogger().error(MODULE,"Error in STARTING operation of REMOTE FILE ALLOCATION of file : "+fileName);
							}
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE,"Remote File Allocator for LOCAL");
							newFile = new File(file.getParent(),fileName);
							file.renameTo(newFile);//Normal Scenario. RLA==LOCAL
						}
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE,"Error in Rollover Detail-Local files, Reason : Null");
					}
				}catch (FileAllocatorException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE,"Error in Transferring file, Reason : "+e.getMessage());
				} catch (Exception e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE,"Unable to rename file "+file.getAbsolutePath());
				}
			}
		}
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
	
	protected SimpleDateFormat getSimpleDateFormat(){
		return this.simpleDateFormat;
	}
	
	protected final String getFileLocation(){
		return currentLocation;
	}

	private void startTimeBoundryTask(int timeBoundryRollingUnit){
		getServerContext().getTaskScheduler().scheduleIntervalBasedTask(new TimeBoundryRollingIntervalTask(timeBoundryRollingUnit) {

			@Override
			public void execute(AsyncTaskContext context) {
				String cdrFlushResultString = cdrflush();

				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Driver: "+getName()+" - Performed Rollover action is: "+cdrFlushResultString);
				}
			}
		});
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "TimeBoundry rolling task scheduled with: "+timeBoundryRollingUnit+" min,for driver: "+getName());
	}
	
	protected abstract String getPattern();
	protected abstract boolean getGlobalization();
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
	protected abstract String getFileName();
	protected abstract String getWriteAttributes();
	protected abstract boolean getUseDictionaryValue();
	protected abstract String getAvPairSeparator();
	protected abstract String getEventDateFormat();
	protected abstract String getDefaultDirName();
	protected abstract boolean getCreateBlankFile();
	protected abstract List<AttributeRelation> getAttributeRelationList();
	protected abstract String getNameFromArray(String[] fileAttributes,ServiceRequest request) ;
	protected abstract void writeAttributes(ServiceRequest request,EliteFileWriter fileWriter,String destinationPath);
	protected abstract String getSequenceRange();
	protected abstract boolean isAttributeID(String str);
	protected abstract String getAttributeValue(String attrID, ServiceRequest request);
	
	protected abstract Map<RollingTypeConstant, Integer> getRollingTypeMap();
}
