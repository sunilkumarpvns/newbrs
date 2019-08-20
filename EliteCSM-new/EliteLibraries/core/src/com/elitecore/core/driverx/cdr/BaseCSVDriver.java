package com.elitecore.core.driverx.cdr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.fileio.EliteFileWriter.Builder;
import com.elitecore.core.commons.fileio.IRolloverListener;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.commons.fileio.loactionalloactor.BaseCommonFileAllocator;
import com.elitecore.core.commons.fileio.loactionalloactor.FileAllocatorException;
import com.elitecore.core.commons.fileio.loactionalloactor.FileLocationAllocatorFactory;
import com.elitecore.core.commons.fileio.loactionalloactor.IFileLocationAllocater;
import com.elitecore.core.commons.util.sequencer.ISequencer;
import com.elitecore.core.commons.util.sequencer.SynchronizedSequencer;
import com.elitecore.core.driverx.ValueProviderExt;
import com.elitecore.core.driverx.cdr.data.CSVFieldMapping;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.TaskScheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

public class BaseCSVDriver extends ESCommunicatorImpl implements CDRDriver<ValueProviderExt> {

	private static final String MODULE = "BS-CSV-DRV";



	private static final String CDRTIMESTAMP = "CDRTIMESTAMP";
	private static final String CSV_EXTENSION = "csv";
	private static final Long SEQUENCER_INITIALIZED = 1L;
	private static final Long SEQUENCER_NOT_INITIALIZED = 0L;
	public static final String PREFIX = "PREFIX";
	public static final String SUFFIX = "SUFFIX";
	public static final String KEY = "KEY";
	public static final String GLOBAL_SEQUENCER = "GLOBAL_SEQUENCER";
	public static final String GLOBAL_COUNTER = "GLOBAL_COUNTER";
	public static final String INP_EXTENSION = "inp";
	private static final String ALLOCATOR_TYPE_LOCAL = "LOCAL";


	private boolean bIsSequencingEnabled;
	private static ISequencer cdrGlobalSequencer;	
	private String prefixVal;
	private String suffixVal;
	private String startSeq;
	private String endSeq;
	private ISequencer synchronizedSequencer = null;
	private ConcurrentHashMap<String,Object> cdrSequenceCounterMap;

	private static Hashtable<String,EliteFileWriter> htFileWriter;

	private String headerLine;

	private IFileLocationAllocater fileLocationAllocator;
	private IRolloverListener rolloverListener;

	private CSVLineBuilder csvLineBuilder;	
	private TaskScheduler taskSchedular;
	private List<AlertListener> alertListeners;
	private CSVDriverConfiguration csvDriverConfiguration;
	private CSVPathAllocator csvPathAllocator;
	private CSVBuilder csvBuilder;

	private String serverHome;

	public BaseCSVDriver(String serverHome, 
			TaskScheduler taskSchedular,
			CSVDriverConfiguration csvDriverConfiguration,
			CSVBuilder csvBuilder) {
		super(taskSchedular);
		this.taskSchedular = taskSchedular;
		this.serverHome = serverHome;
		this.csvBuilder = csvBuilder;
		this.alertListeners = new ArrayList<AlertListener>();
		htFileWriter = new Hashtable<String,EliteFileWriter>();
		this.csvDriverConfiguration = csvDriverConfiguration;

	}



	public void init() throws DriverInitializationFailedException {
		try {
			super.init();
			if (Collectionz.isNullOrEmpty(csvDriverConfiguration.getCSVFieldMappings())) {
				throw new DriverInitializationFailedException("Missing Field Mappings");
			}
			if(LogManager.getLogger().isInfoLogLevel()){
				LogManager.getLogger().info(MODULE, "Initializing Driver : " + csvDriverConfiguration.getDriverName());
			}

			if(csvDriverConfiguration.isHeader()){
				headerLine = buildCSVHeaderLine();
			}else{
				headerLine = "";
			}
			csvLineBuilder = csvBuilder.buildLineBuilder();
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){ 
				LogManager.getLogger().debug(MODULE, "Line Builder initialized successfully for " + csvDriverConfiguration.getDriverName() + " Driver");
			}
			csvPathAllocator = csvBuilder.buildPathAllocator(serverHome);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Path allocator initialized successfully for " + csvDriverConfiguration.getDriverName() + " Driver");
			}
			initSequencer();
			initFileAllocator();

			Map<RollingTypeConstant, Integer> rollingTypeMap = csvDriverConfiguration.getRollingTypeMap();

			if(rollingTypeMap != null && (rollingTypeMap.isEmpty() == false) && rollingTypeMap.containsKey(RollingTypeConstant.TIME_BOUNDRY_ROLLING)){
				int timeBoundryRollingUnit = rollingTypeMap.get(RollingTypeConstant.TIME_BOUNDRY_ROLLING);
				if(timeBoundryRollingUnit > 0){
					startTimeBoundryTask(timeBoundryRollingUnit);
				}
			}
			if(LogManager.getLogger().isInfoLogLevel()){
				LogManager.getLogger().info(MODULE, "Driver: " + csvDriverConfiguration.getDriverName() + " initialized successfully.");
			}

		} catch (Exception e) {
			throw new DriverInitializationFailedException("Driver: " + csvDriverConfiguration.getDriverName() + " initialization failed. Reason: " + e.getMessage(), e);
		}
	}


	public void registerAlertListener (AlertListener alertListener) {
		this.alertListeners.add(alertListener);
	}

	private void startTimeBoundryTask(long timeBoundryRollingUnit){

		taskSchedular.scheduleIntervalBasedTask(new TimeBoundryIntervalRollingTask(timeBoundryRollingUnit) {
			@Override
			public void execute(AsyncTaskContext context) {
				String cdrFlushResultString = cdrflush();

				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Driver: "+getName()+" - Performed Rollover action is: " +cdrFlushResultString);
				}
			}
		});
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "TimeBoundry rolling task scheduled with: "+timeBoundryRollingUnit+" min,for driver: "+getName());
		}
	}

	private void initSequencer() {

		String sequenceRange = csvDriverConfiguration.getSequenceRange();
		if (sequenceRange != null) {
			String regx = "[a-z0-9A-Z]*\\[[a-z0-9A-Z]+\\-[a-z0-9A-Z]+\\][a-z0-9A-Z]*";
			if (Pattern.matches(regx, sequenceRange)) {
				this.prefixVal = sequenceRange.substring(0, sequenceRange.indexOf('['));
				this.suffixVal = sequenceRange.substring(sequenceRange.indexOf(']') + 1);
				this.startSeq = sequenceRange.substring(sequenceRange.indexOf('[') + 1, sequenceRange.indexOf('-'));
				this.endSeq = sequenceRange.substring(sequenceRange.indexOf('-') + 1, sequenceRange.indexOf(']'));
				synchronizedSequencer = new SynchronizedSequencer(startSeq, endSeq, prefixVal, suffixVal);
				try {
					synchronizedSequencer.init();
					this.bIsSequencingEnabled = true;
				} catch (InitializationFailedException e) {
					LogManager.getLogger().warn(MODULE, "Invalid sequence specified: " + sequenceRange + ". So sequencing will be disabled.");
					LogManager.getLogger().trace(MODULE, e);
				}
			} else {
				LogManager.getLogger().warn(MODULE, "Invalid sequence specified: " + sequenceRange + ". So sequencing will be disabled.");
			}
		}

		if (bIsSequencingEnabled) {
			initSequence();
		}

		File parentDir = new File(csvDriverConfiguration.getFileLocation());
		try {
			if (parentDir.exists()) {
				changeFilesExtension(parentDir, INP_EXTENSION, CSV_EXTENSION);
				if (bIsSequencingEnabled && cdrSequenceCounterMap != null)
					serializeCounterMapInFile(csvDriverConfiguration.getCounterFileName(), cdrSequenceCounterMap);
			}
		} catch (SecurityException ex) {
			LogManager.getLogger().warn(MODULE, "Error in converting .inp files to .csv at location :" + csvDriverConfiguration.getFileLocation() + " for driver : " + getDriverName());
			ignoreTrace(ex);
		}
	}

	private void initSequence() {
		Long key = null;
		cdrSequenceCounterMap= (ConcurrentHashMap<String, Object>) setCDRSequenceObjectfromFile(csvDriverConfiguration.getCounterFileName());
		if(cdrSequenceCounterMap != null && cdrSequenceCounterMap.size() > 0 && (key=(Long)cdrSequenceCounterMap.get(KEY)) != null) {
			if(isSequenceGlobalization() && key.equals(SEQUENCER_INITIALIZED)) {
				cdrGlobalSequencer = (SynchronizedSequencer) cdrSequenceCounterMap.get(GLOBAL_SEQUENCER);
				if(cdrGlobalSequencer==null) {
					cdrGlobalSequencer = copySynchronizedSequence();
					if(bIsSequencingEnabled) {
						cdrSequenceCounterMap.put(GLOBAL_SEQUENCER, cdrGlobalSequencer);
					}
				} else {
					if(!cdrGlobalSequencer.equals(synchronizedSequencer)) {
						cdrGlobalSequencer = copySynchronizedSequence();
						if(bIsSequencingEnabled){
							cdrSequenceCounterMap = new ConcurrentHashMap<String, Object>();
							cdrSequenceCounterMap.put(KEY, SEQUENCER_INITIALIZED);
							cdrSequenceCounterMap.put(GLOBAL_SEQUENCER, cdrGlobalSequencer);
						}
					}
				}
			} else if(isSequenceGlobalization() && key.equals(SEQUENCER_NOT_INITIALIZED)) {
				cdrGlobalSequencer = copySynchronizedSequence();
				cdrSequenceCounterMap= new ConcurrentHashMap<String, Object>();
				if(bIsSequencingEnabled) {
					cdrSequenceCounterMap.put(GLOBAL_SEQUENCER, cdrGlobalSequencer);
				}

			} else if(!isSequenceGlobalization() && key.equals(SEQUENCER_INITIALIZED)) {
				cdrSequenceCounterMap= new ConcurrentHashMap<String, Object>();
			}
		}

		if(isSequenceGlobalization()) {
			if(cdrGlobalSequencer == null){
				cdrGlobalSequencer = copySynchronizedSequence();
				if(bIsSequencingEnabled) {
					cdrSequenceCounterMap.put(GLOBAL_SEQUENCER, cdrGlobalSequencer);
				}
			}
			cdrSequenceCounterMap.put(KEY,SEQUENCER_INITIALIZED);
			rolloverListener= () -> serializeCounterMapInFile(csvDriverConfiguration.getCounterFileName(), cdrSequenceCounterMap);
		} else {
			cdrSequenceCounterMap.put(KEY,SEQUENCER_NOT_INITIALIZED);
		}
	}

	private void initFileAllocator() {
		LogManager.getLogger().trace(MODULE, "Setting file allocator for CSV driver");
		String fileAllocatorType = csvDriverConfiguration.getAllocatingProtocol();
		String userName = csvDriverConfiguration.getUserName();
		String password = csvDriverConfiguration.getPassword();
		String serverAddress = csvDriverConfiguration.getIPAddress();
		int serverPort = csvDriverConfiguration.getPort();
		String destinationLocation = csvDriverConfiguration.getRemoteLocation();
		String postOpeartion = "delete";
		if(csvDriverConfiguration.getPostOperation() != null)
			postOpeartion = csvDriverConfiguration.getPostOperation();
		String archiveLocation = csvDriverConfiguration.getArchiveLocation();

		if(fileAllocatorType!=null && ALLOCATOR_TYPE_LOCAL.equalsIgnoreCase(fileAllocatorType) == false) {
			if(serverAddress != null && serverPort != -1 && destinationLocation != null) {
				try {
					if(postOpeartion.equalsIgnoreCase(BaseCommonFileAllocator.ARCHIVE) && (archiveLocation==null || archiveLocation.length()==0))
						throw new FileAllocatorException("Archive Location not specified");
					setFileLocationAllocator(fileAllocatorType, userName, password, serverAddress, destinationLocation,serverPort,postOpeartion,csvDriverConfiguration.getFileLocation(),archiveLocation,CSV_EXTENSION);
				} catch (FileAllocatorException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Error in Initializing remote file Allocator, reason : "+e.getMessage());
					}
					LogManager.getLogger().trace(MODULE, e);
				}
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error in initializing remote rolling for Local files for accounting service, As one or more of the parameters is missing for the configuration.");
				}
			}
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Local Rolling Configured.");
			}
		}
	}


	@Override
	public void handleRequest(ValueProviderExt request) throws DriverProcessFailedException {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Processing request for CSV Driver " + csvDriverConfiguration.getDriverName());
		}

		try {
			List<String> records = csvLineBuilder.getCSVRecords(request);
			String path = csvPathAllocator.getPath(request);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Path Location for storing CSV File is " + path);
			}

			writeRecord(records, path);


			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG) && records.isEmpty() == false) {
				LogManager.getLogger().debug(MODULE, "CDR dumped successfully into CSV file " + path + File.separator + csvDriverConfiguration.getPrefixFileName() + "_" + csvDriverConfiguration.getFileName());
			}
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Error while creating file writer. Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
		}

	}

	private void writeRecord(List<String> records, String path) {
		try{
            EliteFileWriter fileWriter = getOrCreateEliteFileWriter(path);
            for(String record : records) {
                fileWriter.appendRecordln(record);
            }
        }catch(DriverProcessFailedException e){
            if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
                LogManager.getLogger().error(MODULE, "Error while creating file writer. Reason: " + e.getMessage());
            }
            LogManager.getLogger().trace(MODULE, e);
        }
	}

	@SuppressWarnings("unchecked")
	private Map<String,Object> setCDRSequenceObjectfromFile(String fileName) {
		Map<String,Object> cdrSequenceCounterMap = new ConcurrentHashMap<String,Object>();
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE,"Reading CDR Sequence Number Details From File " + fileName);
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))){
			if((cdrSequenceCounterMap = ((ConcurrentHashMap<String,Object>)ois.readObject())) == null) {
				cdrSequenceCounterMap = new ConcurrentHashMap<String,Object>();
			}
		} catch (FileNotFoundException e1) { 
			LogManager.getLogger().trace(MODULE,"CDR-Sequence File Writer Not Found.");
			ignoreTrace(e1);
		} catch (IOException ioExp) { 
			LogManager.getLogger().trace(MODULE,"Error while creating sequence file writer " + ioExp);
			ignoreTrace(ioExp);
		} catch (ClassNotFoundException cnfExp) { 
			LogManager.getLogger().trace(MODULE,"Error while creating sequence file writer " + cnfExp);
			ignoreTrace(cnfExp);
		}
		return cdrSequenceCounterMap;
	}

	private void serializeCounterMapInFile(String fileName, ConcurrentHashMap<String,Object> cdrCounterMap){
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
			oos.writeObject(cdrCounterMap);
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE,"Error while writing the CDR sequencing information into the file. "+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}


	private void setFileLocationAllocator(String fileAllcatorType, String user, String password, String address, String destinationLocation, int port, String postOperation, String folderSepretor, String archiveLocation, String originalExtension) throws FileAllocatorException {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Setting file location allcator for CSV Driver");
		}
		if(password != null && password.contains(":")) {
			String[] encoded = password.split(":");
			if(encoded.length == 2 && ("16".equalsIgnoreCase(encoded[0]) || "32".equalsIgnoreCase(encoded[0]) ||"64".equalsIgnoreCase(encoded[0])))
				password = csvDriverConfiguration.decrypt(encoded[1], Integer.parseInt(encoded[0]));
		}
		fileLocationAllocator = FileLocationAllocatorFactory.getInstance().getFileLocationAllocater(fileAllcatorType);
		fileLocationAllocator.initialize(user, password, address, destinationLocation, port, postOperation, folderSepretor, archiveLocation, originalExtension);
		if(!fileLocationAllocator.getPermission()) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Error while setting file location allocator, Reason: Permission not available to " + destinationLocation);
			}
		}
	}


	private void changeFilesExtension(File file, String sourceExt, String destinationExt){
		if (file.isDirectory()) {
			try {
				File[] fileList = file.listFiles();
				if(fileList != null)
					for(File localFile : fileList)
						changeFilesExtension(localFile, sourceExt, destinationExt);
			}catch (Exception e) {
				LogManager.getLogger().trace(MODULE, e);
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE,"Unable to rename file " + file.getAbsolutePath());
			}
		} else {
			if(file.getName().endsWith('.'+sourceExt) || file.getName().endsWith('.'+BaseCommonFileAllocator.UIP_EXTENSION)) {
				try {
					String fileName = getFileName(file, destinationExt);



					if (fileLocationAllocator == null) {
						if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
							LogManager.getLogger().debug(MODULE, "Changing file extension for LOCAL File Allocator");
						}
						File newFile = new File(file.getParent(), fileName);
						file.renameTo(newFile);    //	Normal Scenario, RLA==LOCAL
						return;
					}

					file = fileLocationAllocator.manageExtension(file, INP_EXTENSION, BaseCommonFileAllocator.UIP_EXTENSION, fileName);
					if (file == null) {
						if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
							LogManager.getLogger().error(MODULE, "Error in STARTING operation of REMOTE FILE ALLOCATION of file : " + fileName);
						}

						return;
					}

					File newFile = fileLocationAllocator.transferFile(file);
					if (newFile == null) {
						return;
					}


					if (fileLocationAllocator.postOperation(newFile)) {
						if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
							LogManager.getLogger().trace(MODULE, "File uploded : " + file.getName());
						}
						return;
					}

					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
						LogManager.getLogger().error(MODULE, "Post operation failed in driver " + getDriverName() + ", Extension changed to pof.");
					}

					fileLocationAllocator.manageExtension(newFile, BaseCommonFileAllocator.STAR_WILDCARD_CHARACTER, BaseCommonFileAllocator.POF_EXTENSION, fileName);//Extension Changed to Post Operation Failed(.pof) and leave on the same location.
					if (newFile.delete()) {
						if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Error in changing the file extension " + BaseCommonFileAllocator.POF_EXTENSION + " for file : " + file.getName());
					} else {
						if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Post Process Failed,file extension changed to pof (Post Operation Failed).");
					}


				} catch (FileAllocatorException e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error in Transferring file, Reason : " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				} catch (Exception e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
						LogManager.getLogger().error(MODULE, "Unable to rename file " + (file != null ? file.getAbsolutePath() : ""));
					}
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}
	}

	private String getFileName(File file, String destinationExt) throws InitializationFailedException, CloneNotSupportedException {
		if (bIsSequencingEnabled == false || cdrSequenceCounterMap == null) {
			return file.getName().substring(0, file.getName().lastIndexOf('.')) + '.' + destinationExt;
		}

		String fileName;
		ISequencer tempSequencer;


		if (isSequenceGlobalization()) {
			tempSequencer = (ISequencer) cdrSequenceCounterMap.get(GLOBAL_COUNTER);
			if (tempSequencer == null) {
				tempSequencer = new SynchronizedSequencer(startSeq, endSeq, prefixVal, suffixVal);
				tempSequencer.init();
				cdrSequenceCounterMap.put(GLOBAL_COUNTER, tempSequencer);
			}
			String strSequence = tempSequencer.getSequence();
			tempSequencer.increment();

			if (csvDriverConfiguration.getSequencePosition().equalsIgnoreCase(PREFIX)) {
				fileName = strSequence + "_" + file.getName().substring(0, file.getName().lastIndexOf('.'));
			} else {
				fileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + "_" + strSequence;
			}
			fileName = fileName + '.' + destinationExt;

		} else {
			fileName = file.getName().substring(0, file.getName().lastIndexOf(getFileName().substring(0, getFileName().lastIndexOf('.'))));
			ISequencer localSequencer = (ISequencer) cdrSequenceCounterMap.get(file.getParent() + File.separator + fileName);

			if (localSequencer == null) {
				localSequencer = new SynchronizedSequencer(startSeq, endSeq, prefixVal, suffixVal);
				localSequencer.init();
				cdrSequenceCounterMap.put(file.getParent() + File.separatorChar + fileName, localSequencer);
			} else {
				tempSequencer = new SynchronizedSequencer(startSeq, endSeq, prefixVal, suffixVal);
				tempSequencer.init();
				if (!localSequencer.equals(tempSequencer)) {
					localSequencer = (ISequencer) tempSequencer.clone();
				}
			}
			String strSequence = localSequencer.getSequence();
			localSequencer.increment();

			if (csvDriverConfiguration.getSequencePosition().equalsIgnoreCase(PREFIX))
				fileName = strSequence + "_" + file.getName().substring(0, file.getName().lastIndexOf('.'));
			else
				fileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + "_" + strSequence;
			fileName = fileName + '.' + destinationExt;
		}

		return fileName;
	}

	private String getFileName() {
		return csvDriverConfiguration.getFileName();
	}

	public String cdrflush() {
		String strResult = "CDR Flushed Successfully";
		try {
			if(htFileWriter == null || htFileWriter.size() == 0) {
				strResult = "No inp File Found To Convert";
			} else {
				for(EliteFileWriter fileWriter : htFileWriter.values()) {
					fileWriter.doRollOver(true);
				}
			}
		}catch(Exception e){ 
			getLogger().trace(MODULE, e);
			strResult = "Failed";
		}
		return strResult;		
	}

	@Override
	public void stop() {
		super.stop();
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Stoping CSV driver: " + csvDriverConfiguration.getDriverName());
		}
		if(htFileWriter == null || htFileWriter.size() == 0)
			return;

		for(EliteFileWriter fileWriter : htFileWriter.values()) {
			try {
				fileWriter.flush();
				fileWriter.close();
			}catch(Exception  e) { 
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while stoping FileWriter. Reason: " + e.getMessage());
				}
				ignoreTrace(e);
			}				
		}
	}

	@Override
	public void scan() {
		///No need to implement
	}

	private EliteFileWriter getOrCreateEliteFileWriter(String path) throws DriverProcessFailedException {
		EliteFileWriter fileWriter = htFileWriter.get(path);
		if(Objects.nonNull(fileWriter)) {
			return fileWriter;
		}

		synchronized (htFileWriter) {
			fileWriter = htFileWriter.get(path);
			if(Objects.nonNull(fileWriter)) {
				return fileWriter;
			}

			try{
				String prefixFileName=(csvDriverConfiguration.getPrefixFileName()!=null)?csvDriverConfiguration.getPrefixFileName()+"_":"";
				ISequencer tempSequencer = null;
				if(!bIsSequencingEnabled) {
					fileWriter = createFileWriter(prefixFileName, path, tempSequencer, null);
				} else {
					if(!csvDriverConfiguration.isSequenceGlobalization()) {
						if(cdrSequenceCounterMap != null && cdrSequenceCounterMap.size() > 0) {
							tempSequencer =  (SynchronizedSequencer) (cdrSequenceCounterMap.containsKey(path) ? cdrSequenceCounterMap.get(path) : null);
							if(tempSequencer == null) {
								tempSequencer = copySynchronizedSequence();
								cdrSequenceCounterMap.put(path, tempSequencer);
							} else {
								if(!synchronizedSequencer.equals(tempSequencer)) {
									tempSequencer = new SynchronizedSequencer(startSeq, endSeq, prefixVal, suffixVal);
									tempSequencer.init();
									cdrSequenceCounterMap.put(path, tempSequencer);
								}
							}
						}

						fileWriter = createFileWriter(prefixFileName, path, tempSequencer, () -> serializeCounterMapInFile(csvDriverConfiguration.getCounterFileName(), cdrSequenceCounterMap));

					}else{

						fileWriter = createFileWriter(prefixFileName, path, cdrGlobalSequencer, rolloverListener);

					}
				}
				fileWriter.setFileLocationAllocator(fileLocationAllocator);
				for(AlertListener alertListener : alertListeners){
					fileWriter.registerAlertListener(alertListener);
				}
				htFileWriter.put(path,fileWriter);
			} catch(Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, e.getMessage());
				}
				throw new DriverProcessFailedException("Error while writing into CSV file " + csvDriverConfiguration.getFileName() + ". Reason: " + e.getMessage(), e);
			}

		}

		return fileWriter;
	}


	private ISequencer copySynchronizedSequence() {

		try {
			return (ISequencer) synchronizedSequencer.clone();
		} catch (CloneNotSupportedException e1) {
			ISequencer cdrGlobalSequencer = new SynchronizedSequencer(startSeq, endSeq, prefixVal, suffixVal);
			try {
				cdrGlobalSequencer.init();
			} catch (InitializationFailedException e) {
				bIsSequencingEnabled = false;
				ignoreTrace(e);
			}
			ignoreTrace(e1);

			return cdrGlobalSequencer;
		}
	}


	private EliteFileWriter createFileWriter(String strPrefixFileName, String destinationPath, ISequencer sequencer, IRolloverListener rolloverListener) throws IOException {
		return new Builder()
				.prefixFileName(strPrefixFileName)
				.fileName(csvDriverConfiguration.getFileName())
				.activeFileExt(INP_EXTENSION)
				.destinationPath(destinationPath)
				.rollingTypeMap(csvDriverConfiguration.getRollingTypeMap())
				.fileHeader(headerLine)
				.rolloverListener(rolloverListener)
				.pattern(csvDriverConfiguration.getSequencePosition())
				.sequencer(sequencer)
				.taskScheduler(getTaskScheduler())
				.createBlankFiles(csvDriverConfiguration.getCreateBlankFile())
				.appendZerosInCDRSequenceNumber(bIsSequencingEnabled)
				.cdrSequenceRequired(true)
				.build();
	}

	public String buildCSVHeaderLine() {
		StringBuilder csvHeaderLine = new StringBuilder();
		String enclosingChar = csvDriverConfiguration.getEnclosingCharacter();

		for (CSVFieldMapping mapping : csvDriverConfiguration.getCSVFieldMappings()) {

			if (enclosingChar != null) {
				csvHeaderLine.append(enclosingChar);
				csvHeaderLine.append(mapping.getHeaderField());
				csvHeaderLine.append(enclosingChar).append(csvDriverConfiguration.getDelimiter());
			} else {
				csvHeaderLine.append(mapping.getHeaderField()).append(
						csvDriverConfiguration.getDelimiter());
			}
		}
		if (csvDriverConfiguration.getCDRTimeStampFormat() != null) {
			if (enclosingChar != null) {
				csvHeaderLine.append(enclosingChar);
				csvHeaderLine.append(CDRTIMESTAMP);
				csvHeaderLine.append(enclosingChar);
			} else {
				csvHeaderLine.append(CDRTIMESTAMP);
			}
		}

		return csvHeaderLine.toString();
	}

	@Override
	public int getDriverType() {
		return csvDriverConfiguration.getDriverType();
	}

	@Override
	public String getTypeName() {
		return MODULE;
	}

	@Override
	public String getDriverInstanceUUID() {
		return null;
	}

	@Override
	public String getDriverName() {
		return this.csvDriverConfiguration.getDriverName();
	}



	@Override
	protected int getStatusCheckDuration() {
		return NO_SCANNER_THREAD;
	}



	@Override
	public String getName() {
		return csvDriverConfiguration.getDriverName();
	}

	public boolean isSequenceGlobalization() {
		return csvDriverConfiguration.isSequenceGlobalization();
	}

}
