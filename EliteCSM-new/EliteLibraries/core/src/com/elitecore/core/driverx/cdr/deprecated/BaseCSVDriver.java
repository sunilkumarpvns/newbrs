package com.elitecore.core.driverx.cdr.deprecated;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
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
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.core.driverx.cdr.data.CSVStripMapping;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.TaskScheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

public abstract class BaseCSVDriver<T> extends ESCommunicatorImpl implements CDRDriver<T> {
	
	private static final String MODULE = "BS-CSV-DRV";
	
	protected String serverHome;
    private final FileParametersResolvers fileParametersResolver;

    private boolean bIsSequencingEnabled;
	
	private static final String CSV_EXTENSION = "csv";
	private static final Long SEQUENCER_INITIALIZED = 1L;
	private static final Long SEQUENCER_NOT_INITIALIZED = 0L;
	private static ISequencer cdrGlobalSequencer;
	private static Hashtable<String,EliteFileWriter> htFileWriter;

	public static final String PREFIX = "PREFIX";
	public static final String SUFFIX = "SUFFIX";
	public static final String KEY = "KEY";
	public static final String GLOBAL_SEQUENCER = "GLOBAL_SEQUENCER";
	public static final String GLOBAL_COUNTER = "GLOBAL_COUNTER";
	public static final String INP_EXTENSION = "inp";

	private static final String ALLOCATOR_TYPE_LOCAL = "LOCAL";
	
	private String strHeaderLine = "";
	private String prefixVal;
	private String suffixVal;
	private String currentLocation;
	private String startSeq;
	private String endSeq;

	private CSVLineBuilder<T> csvLineBuilder;
	
	private IFileLocationAllocater fileLocationAllocator;
	private IRolloverListener rolloverListener; 
	private ISequencer synchronizedSequencer = null;
	private ConcurrentHashMap<String,Object> cdrSequenceCounterMap;

	
	public BaseCSVDriver(String serverHome, TaskScheduler taskSchedular, FileParametersResolvers fileParametersResolver) {
		super(taskSchedular);
		this.serverHome = serverHome;
        this.fileParametersResolver = fileParametersResolver;
        csvLineBuilder = new CSVLineBuilderImpl();
		htFileWriter = new Hashtable<String,EliteFileWriter>();
	}
	
	public void init() throws DriverInitializationFailedException {
		try {
			super.init();
			initDirectory();
			initSequencer();
			initFileAllocator();
			
			if(isHeader()) {
				String headerLine = getCSVHeaderLine();
				strHeaderLine = headerLine == null ? "" : headerLine;
			}
			
		} catch (InitializationFailedException e) {
			throw new DriverInitializationFailedException("Driver initialization failed. Reason: " + e.getMessage(), e);
		}
	}
	
	private void initDirectory() throws DriverInitializationFailedException {
		currentLocation = getFileLocation();
		if(currentLocation == null) {
			LogManager.getLogger().warn(MODULE, "Driver: " + getDriverName() + ", file location not configured. Using default location.");
			createFolderAtDefaultLocation();
			return;
		}

		File file = new File(currentLocation);
		try{
			if(file.isAbsolute() == false) {
				currentLocation = serverHome + File.separator + currentLocation;
				file = new File(currentLocation);
			}

			if(file.exists()) {
				if(!file.canWrite()) {
					LogManager.getLogger().warn(MODULE, "Driver: " + getDriverName() + ", no write access at locaiton: "+getFileLocation() + ". Using default location");
					createFolderAtDefaultLocation();
				}
			} else {
				LogManager.getLogger().info(MODULE, "Driver: " + getDriverName() + ", directory : " + getFileLocation() + " does not exist. Creating the directory.");
				if(!file.mkdirs()) {
					LogManager.getLogger().warn(MODULE, "Driver: " + getDriverName() + ", problem in creating the directory: "+getFileLocation());
					createFolderAtDefaultLocation();
				}
			}
		} catch(SecurityException ex) {
			LogManager.getLogger().warn(MODULE, "Driver: " + getDriverName() + ", no read permission at location: " + getFileLocation() + ". Using default location");
			ignoreTrace(ex);
			createFolderAtDefaultLocation();
		}
	}
	
	private void initSequencer() {

		String sequenceRange = getSequenceRange();
		if(sequenceRange != null) {
			String regx = "[a-z0-9A-Z]*\\[[a-z0-9A-Z]+\\-[a-z0-9A-Z]+\\][a-z0-9A-Z]*";
			if(Pattern.matches(regx, sequenceRange)){
				this.prefixVal = sequenceRange.substring(0, sequenceRange.indexOf('['));
				this.suffixVal = sequenceRange.substring(sequenceRange.indexOf(']')+1);
				this.startSeq = sequenceRange.substring(sequenceRange.indexOf('[')+1, sequenceRange.indexOf('-'));
				this.endSeq = sequenceRange.substring(sequenceRange.indexOf('-')+1, sequenceRange.indexOf(']'));
				synchronizedSequencer = new SynchronizedSequencer(startSeq, endSeq, prefixVal, suffixVal);
				try {
					synchronizedSequencer.init();
					this.bIsSequencingEnabled = true;
				} catch (InitializationFailedException e) {
					LogManager.getLogger().warn(MODULE, "Invalid sequence specified: " + sequenceRange + ". So sequencing will be disabled.");
					LogManager.getLogger().trace(MODULE, e);
				}
			}else{
				LogManager.getLogger().warn(MODULE, "Invalid sequence specified: " + sequenceRange + ". So sequencing will be disabled.");
			}
		}
		
		if(bIsSequencingEnabled) {
			initSequence();
		}
		
		File parentDir = new File(getFileLocation());
		try {
			if(parentDir.exists()) {
				changeFilesExtension(parentDir, INP_EXTENSION, CSV_EXTENSION);
				if(bIsSequencingEnabled && cdrSequenceCounterMap != null)
					serializeCounterMapInFile(getCounterFileName(), cdrSequenceCounterMap);
			}
		} catch(SecurityException ex) { 
			LogManager.getLogger().warn(MODULE, "Error in converting .inp files to .csv at location :" + getFileLocation() + " for driver : " + getDriverName());
			ignoreTrace(ex);
		}
	}

	private void initSequence() {
		Long key = null;
		cdrSequenceCounterMap= (ConcurrentHashMap<String, Object>) setCDRSequenceObjectfromFile(getCounterFileName());
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
            rolloverListener= () -> serializeCounterMapInFile(getCounterFileName(), cdrSequenceCounterMap);
        } else {
            cdrSequenceCounterMap.put(KEY,SEQUENCER_NOT_INITIALIZED);
        }
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

	private void initFileAllocator() {
		LogManager.getLogger().trace(MODULE, "Setting file allocator for CSV driver");
		String fileAllocatorType = getAllocatingProtocol();
		String userName = getUserName();
		String password = getPassword();
		String serverAddress = getIPAddress();
		int serverPort = getPort();
		String destinationLocation = getRemoteLocation();
		String postOpeartion = "delete";
		if(getPostOperation() != null)
			postOpeartion = getPostOperation();
		String archiveLocation = getArchiveLocation();
		
		if(fileAllocatorType!=null && !ALLOCATOR_TYPE_LOCAL.equalsIgnoreCase(fileAllocatorType)) {
			if(serverAddress != null && serverPort != -1 && destinationLocation != null) {
				try {
					if(postOpeartion.equalsIgnoreCase(BaseCommonFileAllocator.ARCHIVE) && (archiveLocation==null || archiveLocation.length()==0))
						throw new FileAllocatorException("Archive Location not specified");
					setFileLocationAllocator(fileAllocatorType, userName, password, serverAddress, destinationLocation,serverPort,postOpeartion,getFileLocation(),archiveLocation,CSV_EXTENSION);
				} catch (FileAllocatorException e) {
					LogManager.getLogger().error(MODULE, "Error in Initializing remote file Allocator, reason : "+e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			} else {
				LogManager.getLogger().error(MODULE, "Error in initializing remote rolling for Local files for accounting service, As one or more of the parameters is missing for the configuration.");
			}
		} else {
			LogManager.getLogger().info(MODULE, "Local Rolling Configured.");
		}
	}

	public final void registerCSVLineBuilder(CSVLineBuilder<T> csvLineBuilder) {
		if(csvLineBuilder != null) {
			this.csvLineBuilder = csvLineBuilder;
		}
	}
	
	@Override
	public void handleRequest(T response) throws DriverProcessFailedException {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Processing request for CSV Driver " + getDriverName());
			
		try {
			List<String> records = csvLineBuilder.getCSVRecords(response);
			for(String record : records) {
				writeRecord(record, response);
			}

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG) && !records.isEmpty())
				LogManager.getLogger().debug(MODULE, "CDR dumped successfully into CSV file " + currentLocation + File.separator + getFileName());
			
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while creating csv record. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
	}
	
	private void writeRecord(String record, T response) throws DriverProcessFailedException {
        String strPrefixFileName;
        String strFolderName;
	    if(Objects.nonNull(fileParametersResolver)) {
            strPrefixFileName = fileParametersResolver.getFilePrefix(response);
            strFolderName = fileParametersResolver.getFolderName(response);
        } else {
            strPrefixFileName = getPrefixFileName();
            strFolderName = getDirectoryName();
        }

		
		strPrefixFileName = (Strings.isNullOrBlank(strPrefixFileName)) ? "" : strPrefixFileName + "_";

		if(Strings.isNullOrBlank(strFolderName))
			strFolderName = (getDefaultDirectoryName() != null) ? getDefaultDirectoryName() : "";

		String destinationPath = currentLocation + File.separator + strFolderName;
		final String strWriterKey = destinationPath + File.separator + strPrefixFileName;

		EliteFileWriter fileWriter = htFileWriter.get(strWriterKey);

		if(fileWriter == null) {
			synchronized (htFileWriter) {
				fileWriter = htFileWriter.get(strWriterKey);
				if(fileWriter == null) {
					try{
						ISequencer tempSequencer = null;
						if(!bIsSequencingEnabled) {
							fileWriter = new Builder()
									.prefixFileName(strPrefixFileName)
									.fileName(getFileName())
									.activeFileExt(INP_EXTENSION)
									.destinationPath(destinationPath)
									.rollingType(getRollingType())
									.rollingTypeMap(getRollingTypeMap())
									.fileHeader(strHeaderLine)
									.rollingUnit(getRollingUnit())
									.pattern(getSequencePosition())
									.taskScheduler(getTaskScheduler())
									.build();
						} else {
							if(!isSequenceGlobalization()) {
								if(cdrSequenceCounterMap != null && cdrSequenceCounterMap.size() > 0) {
									tempSequencer =  (SynchronizedSequencer) (cdrSequenceCounterMap.containsKey(strWriterKey) ? cdrSequenceCounterMap.get(strWriterKey) : null);
									if(tempSequencer == null) {
										tempSequencer = copySynchronizedSequence();
										cdrSequenceCounterMap.put(strWriterKey, tempSequencer);
									} else {
										if(!synchronizedSequencer.equals(tempSequencer)) {
											tempSequencer = new SynchronizedSequencer(startSeq, endSeq, prefixVal, suffixVal);
											tempSequencer.init();
											cdrSequenceCounterMap.put(strWriterKey, tempSequencer);
										}
									}
								}
								fileWriter = createFileWriter(strPrefixFileName, destinationPath, tempSequencer);
										
							}else{
								fileWriter = createFileWriter(strPrefixFileName, destinationPath, cdrGlobalSequencer);
							}
						}
						fileWriter.setFileLocationAllocator(fileLocationAllocator);
						htFileWriter.put(strWriterKey,fileWriter);
					} catch(Exception e) {
						LogManager.getLogger().error(MODULE, e.getMessage());
						throw new DriverProcessFailedException("Error while writing into CSV file " + getFileName() + ". Reason: " + e.getMessage(), e);
					}
				}
			}
		}
		fileWriter.appendRecordln(record);
	}

	private EliteFileWriter createFileWriter(String strPrefixFileName, String destinationPath, ISequencer sequencer) throws IOException {
		return new Builder()
                .prefixFileName(strPrefixFileName)
                .fileName(getFileName())
                .activeFileExt(INP_EXTENSION)
                .destinationPath(destinationPath)
                .rollingType(getRollingType())
                .rollingTypeMap(getRollingTypeMap())
                .fileHeader(strHeaderLine)
                .rollingUnit(getRollingUnit())
                .pattern(getSequencePosition())
                .sequencer(sequencer)
                .rolloverListener(rolloverListener)
                .taskScheduler(getTaskScheduler())
                .cdrSequenceRequired(true)
                .build();
	}

	public interface CSVLineBuilder<T> {
		
		public List<String> getCSVRecords(T request);
		
	}
	
	private class CSVLineBuilderImpl implements CSVLineBuilder<T> {

		@Override
		public List<String> getCSVRecords(T serviceRequest) {
			List<String> records = new ArrayList<String>();
			StringBuilder record = new StringBuilder(100);
			for(CSVFieldMapping mapping : getCSVFieldMappings()) {
				appendValue(record, getStrippedValue(mapping.getKey(), getParameterValue(serviceRequest, mapping.getKey())));
			}
			appendTimestamp(record);
			records.add(record.toString());
			return records;
		}
		
	}
	
	public final void appendValue(StringBuilder record, String value) {
		if(value != null) {
			if(value.contains(getDelimiter()))
				value = value.replaceAll(getDelimiter(), "\\\\" + getDelimiter());
			record.append(value).append(getDelimiter());
		} else {
			record.append(getDelimiter());
		}
	}
	
	public final String getStrippedValue(String key, String value) {
		CSVStripMapping stripMapping = getStripMapping(key);
		if(stripMapping != null) {
			String pattern = stripMapping.getPattern();
			String seperator = stripMapping.getSeperator();
			
			if(seperator == null || value == null) 
				return value;
			
			int index = value.indexOf(seperator);
			if(index != -1) {
				if (SUFFIX.equalsIgnoreCase(pattern))
					value =  value.substring(0, index).trim();
				else if (PREFIX.equalsIgnoreCase(pattern))
					value =  value.substring(index+1, value.length()).trim();
			}
		}
		return value;
	}
	
	public final void appendTimestamp(StringBuilder record) {
		String strDateFormat = getCDRTimeStampFormat().format(new Timestamp(System.currentTimeMillis()));
		if(strDateFormat.contains(getDelimiter()))
			strDateFormat = strDateFormat.replaceAll(getDelimiter(), "\\\\" + getDelimiter());
		record.append(strDateFormat);
	}

	@SuppressWarnings("unchecked")
	private Map<String,Object> setCDRSequenceObjectfromFile(String fileName) {
		Map<String,Object> cdrSequenceCounterMap = new ConcurrentHashMap<String,Object>();
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE,"Reading CDR Sequence Number Details From File " + fileName);
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
			if((cdrSequenceCounterMap = ((ConcurrentHashMap<String,Object>)ois.readObject())) == null) {
				cdrSequenceCounterMap = new ConcurrentHashMap<String, Object>();
			}
		} catch (FileNotFoundException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, e);
		} catch (IOException ioExp) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE,ioExp);
		} catch (ClassNotFoundException cnfExp) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE,cnfExp);
		}
		return cdrSequenceCounterMap;
	}
	
	private void serializeCounterMapInFile(String fileName, ConcurrentHashMap<String,Object> cdrCounterMap){
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
			oos.writeObject(cdrCounterMap);
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE,"Error while writing the CDR sequencing information into the file. "+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	private void createFolderAtDefaultLocation() throws DriverInitializationFailedException {
		currentLocation = serverHome + File.separator + "data" + File.separator + "csvfiles";
		LogManager.getLogger().warn(MODULE, "Driver: " + getDriverName() + ", creating folder at default location: " + currentLocation);
		
		File file = new File(currentLocation);
		try {
			if(!file.exists()) {
				if(!file.mkdirs()) {
					LogManager.getLogger().error(MODULE, "Driver: " + getDriverName() + ", cannot create directory at default location: " + currentLocation);
					throw new DriverInitializationFailedException("Driver: " + getDriverName() + ", problem in creating directory at configured and default location.");
				} else {
					LogManager.getLogger().debug(MODULE, "Driver: " + getDriverName() + ", directory created successfully at default location.");
				}
			} else {
				LogManager.getLogger().debug(MODULE, "Driver: " + getDriverName() + ", directory:" + currentLocation + " already exists.");
				if(!file.canWrite()) {
					LogManager.getLogger().error(MODULE, "Driver: " + getDriverName() + ", no write access in Default directory: "+ currentLocation);
					throw new DriverInitializationFailedException("Driver: " + getDriverName() + ", problem in creating directory at configured and default location.");
				}
			}
		} catch(SecurityException ex) {
			LogManager.getLogger().error(MODULE, "Driver: " + getDriverName() + ", problem in creating the Default directory: "+ currentLocation);
			throw new DriverInitializationFailedException("Driver: " + getDriverName() + ", problem in creating directory at configured and default location.", ex);
		}
	}
	
	private void setFileLocationAllocator(String fileAllcatorType, String user, String password, String address, String destinationLocation, int port, String postOperation, String folderSepretor, String archiveLocation, String originalExtension) throws FileAllocatorException {
		LogManager.getLogger().info(MODULE, "Setting file location allcator for CSV Driver");
		if(password != null && password.contains(":")) {
			String[] encoded = password.split(":");
			if(encoded.length == 2 && ("16".equalsIgnoreCase(encoded[0]) || "32".equalsIgnoreCase(encoded[0]) ||"64".equalsIgnoreCase(encoded[0])))
				password = decrypt(encoded[1], Integer.parseInt(encoded[0]));
		}
		fileLocationAllocator = FileLocationAllocatorFactory.getInstance().getFileLocationAllocater(fileAllcatorType);
		fileLocationAllocator.initialize(user, password, address, destinationLocation, port, postOperation, folderSepretor, archiveLocation, originalExtension);
		if(!fileLocationAllocator.getPermission()) {
			LogManager.getLogger().error(MODULE, "Error while setting file location allocator, Reason: Permission not available to " + destinationLocation);
		}
	}
	
	@SuppressWarnings("unused")
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

			if (getSequencePosition().equalsIgnoreCase(PREFIX)) {
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

			if (getSequencePosition().equalsIgnoreCase(PREFIX))
				fileName = strSequence + "_" + file.getName().substring(0, file.getName().lastIndexOf('.'));
			else
				fileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + "_" + strSequence;
			fileName = fileName + '.' + destinationExt;
		}

		return fileName;
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
			LogManager.getLogger().trace(MODULE, e);
			strResult = "Failed";
		}
		return strResult;		
	}
	
	@Override
	public void stop() {
		super.stop();
		LogManager.getLogger().info(MODULE, "Stoping CSV driver: " + getDriverName());
		if(htFileWriter == null || htFileWriter.size() == 0)
			return;

		for(EliteFileWriter fileWriter : htFileWriter.values()) {
			
			try {
				fileWriter.flush();
			}catch(Exception  e) {
				LogManager.getLogger().error(MODULE, "Error while stoping FileWriter. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
			
			try {
				fileWriter.close();
			}catch(Exception  e) {
				LogManager.getLogger().error(MODULE, "Error while stoping FileWriter. Reason: " + e.getMessage());
				LogManager.ignoreTrace(e);
			}				
		}
	}
	
	@Override
	public void scan() {
		///No need to implement
	}
	
	public abstract String getCSVHeaderLine();
	public abstract String getCounterFileName();
	public abstract SimpleDateFormat getCDRTimeStampFormat();
	public abstract long getRollingUnit();
	public abstract int getRollingType();
	public abstract int getPort();
	public abstract int getFailOverTime();
	public abstract boolean isHeader();
	public abstract boolean isSequenceGlobalization();
	public abstract String getDelimiter();
	public abstract String getMultipleDelimiter();
	public abstract String getFileName();
	public abstract String getFileLocation();
	public abstract String getPrefixFileName();
	public abstract String getDefaultDirectoryName();
	public abstract String getDirectoryName();
	public abstract String getSequenceRange();
	public abstract String getSequencePosition();
	public abstract String getAllocatingProtocol();
	public abstract String getIPAddress();
	public abstract String getRemoteLocation();
	public abstract String getUserName();
	public abstract String getPassword();
	public abstract String decrypt(String encriptedPassword, int encriptionType);
	public abstract String getPostOperation();
	public abstract String getArchiveLocation();
	public abstract CSVStripMapping getStripMapping(String key);
	public abstract List<CSVFieldMapping> getCSVFieldMappings();
	public abstract Map<RollingTypeConstant, Integer> getRollingTypeMap();
	public abstract String getParameterValue(T request, String key);
	 
}
