package com.elitecore.netvertex.gateway.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.config.util.FileUtil;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.commons.util.responsecode.BaseDriverResponseCode;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.corenetvertex.spr.ProcessFailException;
import com.elitecore.license.base.commons.LicenseNameConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.constant.OfflineConstants;
import com.elitecore.netvertex.core.constant.OfflineRnCConstants;
import com.elitecore.netvertex.gateway.file.parsing.AsciiParserPlugin;
import com.elitecore.netvertex.gateway.file.parsing.CommonResponsePacket;
import com.elitecore.netvertex.gateway.file.parsing.GeneralBatchPacket;
import com.elitecore.netvertex.gateway.file.parsing.JsonParsingPlugin;
import com.elitecore.netvertex.gateway.file.parsing.ParsingPlugin;
import com.elitecore.netvertex.gateway.file.parsing.ParsingPluginRequest;
import com.elitecore.netvertex.gateway.file.util.FileComparator;
import com.elitecore.netvertex.gateway.file.util.OFCSFileHelper;
import com.elitecore.netvertex.gateway.file.util.Utilities;
import com.elitecore.netvertex.service.offlinernc.conf.OfflineRnCServiceConfiguration;

public class FileGateway {

	private static final String MODULE = "FILE-GW";
	
	private static final String IS_DELETE_PARSED_FILE = "IS_DELETE_PARSED_FILE";

	private static final String FILE_COUNTER_FLAG = "FILE_COUNTER_FLAG";

	private static final String IS_ADD_TOTAL_RECORD_COUNT = "IS_ADD_TOTAL_RECORD_COUNT";

	private static final String TOTAL_RECORDS_COUNT_FOR_TPS = "TOTAL_RECORDS_COUNT_FOR_TPS";
	
	private FileGatewayConfiguration fileGatewayConfiguration;
	private ExecutorService threadPoolExecutor;
	private ScheduledExecutorService fixedDelayBatchDispatcher;
	private OFCSFileHelper fileProcessor;
	private NetVertexServerContext serverContext;

	private String outputDirFormat;
	private String processingInputDirPath;
	private String[] arrFileCopy;
	private boolean bFileCopyFlag;
	private String[] sourcePathList;
	private String inProcessExt;
	private String deviceName;
	private String sourcePath;
	private String sortingType;
	private String sortingCriteria;
	private int noFileAlertInt;
	private int iRecordBatchSize;
	private boolean bOverrideFileDate;
	private String overrideFileDateType;
	private String archivePath;
	private String fileRange;
	private boolean bCompressedFile;
	private boolean bCompressedOutputFile;
	private String errorPath;
	private Method totalSpaceMethod;
	private int requiredDiskSpaceinGB;
	private boolean isDiskSpaceProblem;
	private int noOfAttempt;
	private final Object lockObject = new Object();

	private AsciiParserPlugin parsingPlugin;
	private JsonParsingPlugin jsonParsingPlugin;

	private OfflineRnCServiceConfiguration offlineRnCServiceConfiguration;

	private FileGatewayEventListener fileGatewayEventListener;
	private volatile boolean started;

	private ExecutorService intermediateThreadPoolExecutor;

	private String intermediateOutputDir;

	private String outputDir;
	
	public FileGateway(NetVertexServerContext serverContext, FileGatewayConfiguration fileGatewayConfiguration, ExecutorService threadPoolExecutor, ExecutorService intermediateThreadPoolExecutor, 
			OfflineRnCServiceConfiguration offlineRnCServiceConfiguration, FileGatewayEventListener fileGatewayEventListener) {
		this.serverContext = serverContext;
		this.fileGatewayConfiguration = fileGatewayConfiguration;
		this.threadPoolExecutor = threadPoolExecutor;
		this.intermediateThreadPoolExecutor = intermediateThreadPoolExecutor;
		this.fileGatewayEventListener = fileGatewayEventListener;
		this.fixedDelayBatchDispatcher = Executors.newScheduledThreadPool(2, new EliteThreadFactory("FileDispatcher", "FileDispatcher", Thread.NORM_PRIORITY));
		this.offlineRnCServiceConfiguration = offlineRnCServiceConfiguration;
		this.fileProcessor = new OFCSFileHelper();
	}
	
	public void init() throws InitializationFailedException, ServiceInitializationException {
		this.parsingPlugin = new AsciiParserPlugin(serverContext, fileGatewayConfiguration, fileGatewayEventListener);
		this.jsonParsingPlugin = new JsonParsingPlugin(serverContext, fileGatewayConfiguration, fileGatewayEventListener, offlineRnCServiceConfiguration.getFileBatchSize(), offlineRnCServiceConfiguration.getScanIntervalInSeconds());
		initializeAttributes();
	}

	/**
	 * Initialise attributes.
	 *
	 * @throws ServiceInitializationException the service initialization exception
	 */
	private void initializeAttributes() throws InitializationFailedException, ServiceInitializationException {
		

		inProcessExt = OfflineRnCConstants.INPROCESS_EXTENSION;

		deviceName = fileGatewayConfiguration.getDeviceName();
		outputDirFormat = OfflineConstants.PROCESSING_ROOT + File.separator + deviceName + File.separator + OfflineConstants.INPUT;
		sourcePath = fileGatewayConfiguration.getSourcePath();
		processingInputDirPath = serverContext.getServerHome() + File.separator + OfflineConstants.PROCESSING_ROOT + File.separator + deviceName + File.separator + OfflineConstants.INPUT;
		String strFileCopyFolder = fileGatewayConfiguration.getFileCopyFolders();

		if (strFileCopyFolder != null && strFileCopyFolder.length() > 0)
			arrFileCopy = strFileCopyFolder.split(",");

		if (arrFileCopy != null && arrFileCopy.length > 0) {
			bFileCopyFlag = true;
		}

		sourcePathList = new String[1];
		sourcePathList[0] = sourcePath;

		sortingType = fileGatewayConfiguration.getSortingType();
		if (sortingType == null)
			sortingType = OfflineRnCConstants.NA_ORDER;

		sortingCriteria = fileGatewayConfiguration.getSortingCriteria();
		if (sortingCriteria == null || "".equals(sortingCriteria))
			sortingCriteria = OfflineRnCConstants.LAST_MODIFIED_DATE;

		fileRange = fileGatewayConfiguration.getFileRange();

		bCompressedFile = fileGatewayConfiguration.isInputFileCompressed();

		bCompressedOutputFile = fileGatewayConfiguration.isOutputFileCompressed();

		iRecordBatchSize = fileGatewayConfiguration.getRecordBatchSize();
		if (iRecordBatchSize <= 0)
			iRecordBatchSize = 50000;

		bOverrideFileDate = fileGatewayConfiguration.isOverrideFileDate();

		if (bOverrideFileDate) {
			overrideFileDateType = fileGatewayConfiguration.getOverrideFileDateType();
			if (overrideFileDateType == null || overrideFileDateType.length() == 0)
				overrideFileDateType = "min";
		}

		/* Archive and Error Paths */
		archivePath = fileGatewayConfiguration.getArchivePath();
		errorPath = fileGatewayConfiguration.getErrorPath();


		// Rename in-process source file and delete in-process destination file
		for (String path : sourcePathList) {
			List<String> inProcessFileNameList = fileProcessor.getInProcessFileList(path, inProcessExt);
			fileProcessor.renameInprocessFilesOnStartup(inProcessFileNameList, path, inProcessExt);
			List<String> csvInprocessFileNameList = new ArrayList<>();
			for (int i = 0; i < inProcessFileNameList.size(); i++) {
				String fileName = inProcessFileNameList.get(i);
				String newFileName = fileName;
				int iExtIndex = fileName.lastIndexOf(OfflineRnCConstants.DOT);
				if (iExtIndex > 0)
					newFileName = fileName.substring(0, iExtIndex);
				csvInprocessFileNameList.add(newFileName);
			}
			
			fileProcessor.deleteInprocessFiles(csvInprocessFileNameList, getDestinationPaths(), inProcessExt);
			if (bFileCopyFlag) {
				fileProcessor.deleteInProcessFilesOnCopyFolders(csvInprocessFileNameList, arrFileCopy, outputDirFormat, inProcessExt);
			}
		}

		try {
			totalSpaceMethod = File.class.getMethod("getUsableSpace", (Class[]) null);
		} catch (NoSuchMethodException noSuchMethodException) {
			LogManager.getLogger().trace(MODULE, noSuchMethodException);
			LogManager.getLogger().warn(MODULE, "Error occured while calling the getFreeSpace method, reason: " + noSuchMethodException.getMessage());
		}

		requiredDiskSpaceinGB = fileGatewayConfiguration.getMinDiskSpaceRequired();
		boolean bDestDirCreated = createDestDirs();
		if (!bDestDirCreated)
			throw new ServiceInitializationException(OFCSErrorMessages.DIR_CREATION_FAILED.getMessage("Destinatiom", null), ServiceRemarks.INVALID_CONFIGURATION);

		try{
			noFileAlertInt = fileGatewayConfiguration.getNoFileAlertInterval();
			if(noFileAlertInt > 0) {
				noOfAttempt = noFileAlertInt/offlineRnCServiceConfiguration.getScanIntervalInSeconds();
			}
		}catch(Exception e){
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().error(MODULE,"Error occured while getting no. of Attempt, reason: " + e.getMessage());
		}
		
		intermediateOutputDir = offlineRnCServiceConfiguration.getIntermediateOutputPath() + File.separator + fileGatewayConfiguration.getName();
		outputDir = fileGatewayConfiguration.getDestinationInputPath() + File.separator + fileGatewayConfiguration.getName();
		
		File intermediateOutputDirPath = new File(intermediateOutputDir);
		File outputDirPath = new File(outputDir);
		
		intermediateOutputDirPath.mkdirs();
		outputDirPath.mkdirs();
		
		parsingPlugin.init();
		jsonParsingPlugin.init();
	}
	
	private String[] getDestinationPaths() {
		String[] sourcePaths = new String[1];
		sourcePaths[0] = processingInputDirPath;
		return sourcePaths;
	}
	
	private boolean createDestDirs() {
		boolean bDirectoryCreated = true;
		String destInputDirPath = serverContext.getServerHome()+File.separator+"intermediate"+File.separator+fileGatewayConfiguration.getName();
		File inputDir = new File(destInputDirPath);
		if (!inputDir.exists()) {
			boolean bMakeDir = inputDir.mkdirs();
			if (!bMakeDir) {
				LogManager.getLogger().error(MODULE, OFCSErrorMessages.DIR_CREATION_FAILED.getMessage("Destination", destInputDirPath));
			}
		}
		bDirectoryCreated = fileProcessor.isSpaceAvailableOnDisk(totalSpaceMethod, requiredDiskSpaceinGB, inputDir);
		/*if (bDirectoryCreated) {
			List<String> destPathList = Arrays.asList(fileGatewayConfiguration.getDestinationInputPath());
			bDirectoryCreated = fileProcessor.createDirOnCopyFolders(destPathList, outputDirFormat, totalSpaceMethod, requiredDiskSpaceinGB);
			if(isDiskSpaceProblem){
				LogManager.getLogger().error(MODULE, "Disk space is Resolved.");
				getServerContext().generateSystemAlert(AlertSeverity.INFO, Alerts.FILE_GATEWAY_DISK_SPACE_RESOLVED, MODULE, "Disk space is Resolved.");
				isDiskSpaceProblem = false;
			}
		}*/
		if (!bDirectoryCreated) {
			LogManager.getLogger().error(MODULE, "Could not create directory structure for source path - " + fileGatewayConfiguration.getSourcePath() + ", because of disk space or file system.");
			getServerContext().generateSystemAlert(AlertSeverity.CRITICAL, Alerts.FILE_GATEWAY_NO_SPACE_LEFT_ON_DEVICE, MODULE, "Could not create directory structure for source path - " + fileGatewayConfiguration.getSourcePath() + ", because of disk space or file system.");
			isDiskSpaceProblem = true;
		}
		return bDirectoryCreated;
	}

	private ServerContext getServerContext() {
		return serverContext;
	}

	public void start() {
		if (isLicenseValid()) {
			startInternal();
		}
		serverContext.registerLicenseObserver(this::licenseUpdated);
	}

	private void startInternal() {
		LogManager.getLogger().info(MODULE, "Starting file interface: " + fileGatewayConfiguration.getName());
		
		BatchDispatcher inputFileDispatcher = new BatchDispatcher.BatchDispatcherBuilder(serverContext, 
				fileGatewayConfiguration.getName(),
				threadPoolExecutor,
				(fList) -> new BatchProcessor(fList),
				fileProcessor)
				.inputPath(fileGatewayConfiguration.getSourcePath())
				.coreThreadSize(offlineRnCServiceConfiguration.getMinThread())
				.maxThreadSize(offlineRnCServiceConfiguration.getMaxThread())
				.queueSize(offlineRnCServiceConfiguration.getFileBatchQueueSize())
				.fileBatch(offlineRnCServiceConfiguration.getFileBatchSize())
				.interval(offlineRnCServiceConfiguration.getScanIntervalInSeconds())
				.fileRange(offlineRnCServiceConfiguration.getFileRange())
				.noOfAttempt(fileGatewayConfiguration.getNoOfAttempt())
				.maxFileLimit(fileGatewayConfiguration.getReadPathMaxFilesCount())
				.filters(new ArrayList<>())
				.suffixes(fileGatewayConfiguration.getSuffixList())
				.containsList(fileGatewayConfiguration.getContainsList())
				.inProcessExtension(fileGatewayConfiguration.getInProcessExtension())
				.sortingType(fileGatewayConfiguration.getSortingType())
				.sortingCriteria(fileGatewayConfiguration.getSortingCriteria())
				.excludingTypes(fileGatewayConfiguration.getExcludeFileTypeList())
				.build();
		
		
		BatchDispatcher intermediateFileDispatcher = new BatchDispatcher.BatchDispatcherBuilder(serverContext, 
				fileGatewayConfiguration.getName(),
				intermediateThreadPoolExecutor,
				(fList) -> new IntermediateOutputBatchProcessor(fList),
				fileProcessor)
				.inputPath(offlineRnCServiceConfiguration.getIntermediateOutputPath() + File.separator + fileGatewayConfiguration.getName())
				.coreThreadSize(offlineRnCServiceConfiguration.getMinThread())
				.maxThreadSize(offlineRnCServiceConfiguration.getMaxThread())
				.queueSize(offlineRnCServiceConfiguration.getFileBatchQueueSize())
				.fileBatch(offlineRnCServiceConfiguration.getFileBatchSize())
				.interval(offlineRnCServiceConfiguration.getScanIntervalInSeconds())
				.fileRange(offlineRnCServiceConfiguration.getFileRange())
				.noOfAttempt(fileGatewayConfiguration.getNoOfAttempt())
				.maxFileLimit(fileGatewayConfiguration.getReadPathMaxFilesCount())
				.filters(new ArrayList<>())
				.suffixes(fileGatewayConfiguration.getSuffixList())
				.containsList(fileGatewayConfiguration.getContainsList())
				.inProcessExtension(fileGatewayConfiguration.getInProcessExtension())
				.sortingType(fileGatewayConfiguration.getSortingType())
				.sortingCriteria(fileGatewayConfiguration.getSortingCriteria())
				.excludingTypes(fileGatewayConfiguration.getExcludeFileTypeList())
				.build();
		
		fixedDelayBatchDispatcher.scheduleWithFixedDelay(inputFileDispatcher, offlineRnCServiceConfiguration.getScanIntervalInSeconds(), offlineRnCServiceConfiguration.getScanIntervalInSeconds(), TimeUnit.SECONDS);
		fixedDelayBatchDispatcher.scheduleWithFixedDelay(intermediateFileDispatcher, offlineRnCServiceConfiguration.getScanIntervalInSeconds(), offlineRnCServiceConfiguration.getScanIntervalInSeconds(), TimeUnit.SECONDS);
		
		started = true;
		LogManager.getLogger().info(MODULE, "Started file interface: " + fileGatewayConfiguration.getName());
	}
	
	private void licenseUpdated() {
		boolean isLicenseValid = isLicenseValid();

		if (isLicenseValid == false) {
			LogManager.getLogger().warn(MODULE, " License for File interface is either not acquired or has expired.");
			if (started) {
				stop();
			}
		} else {
			LogManager.getLogger().warn(MODULE, " License for File interface acquired.");
			if (started == false) {
				startInternal();
			}
		}
		
	}

	private boolean isLicenseValid() {
		return serverContext.isLicenseValid(LicenseNameConstants.NV_FILE_INTERFACE,String.valueOf(System.currentTimeMillis()));
	}
	
	


	public void stop() {
		LogManager.getLogger().info(MODULE, "Stopping file interface: " + fileGatewayConfiguration.getName());
		
		fixedDelayBatchDispatcher.shutdown();
	}

	/**
	 * Main service process.
	 *
	 */
	private static class BatchDispatcher implements Runnable {

		private static final String MODULE = "File-Batch-Dispatcher";
		private final ExecutorService threadPoolExecutor;
		private final Function<List<String>, Runnable> taskFactory;
		private final String name;
		private final int iMaxThreadSize;
		private final int iMinThreadSize;
		private final int iQueueSize;
		private final String inputPath;
		private final String sortingType;
		private final String sortingCriteria;
		private final int scanInterval;
		private final List<String> strFilters;
		private final String inProcessExt;
		private final List<String> prefixes;
		private final List<String> selectFileOnSuffixes;
		private final List<String> exculdeFileTypes;
		private final String fileRange;
		private final Integer maxFileLimit;
		private final int noOfAttempt;
		private final List<String> containsList;
		private final NetVertexServerContext serverContext;
		
		private int iFileBatchSize;
		private int counter;
		private OFCSFileHelper fileProcessor;
		
		
		public static class BatchDispatcherBuilder {
			
			private ExecutorService threadPoolExecutor;
			private Function<List<String>, Runnable> taskFactory;
			private String name;
			private int iMaxThreadSize;
			private int iMinThreadSize;
			private int iQueueSize;
			private int iFileBatchSize;
			private String inputPath;
			private String sortingType;
			private String sortingCriteria;
			private int scanInterval;
			private List<String> strFilters;
			private String inProcessExt;
			private List<String> prefixes;
			private List<String> selectFileOnSuffixes;
			private List<String> exculdeFileTypes;
			private String fileRange;
			private Integer maxFileLimit;
			private int noOfAttempt;
			private List<String> containsList;
			private NetVertexServerContext serverContext;
			private OFCSFileHelper fileProcessor;
			
			public BatchDispatcherBuilder(NetVertexServerContext serverContext, String name,
					ExecutorService threadPoolExecutor,
					Function<List<String>, Runnable> taskFactory,
					OFCSFileHelper fileProcessor) {
				this.serverContext = serverContext;
				this.name = name;
				this.threadPoolExecutor = threadPoolExecutor;
				this.taskFactory = taskFactory;
				this.fileProcessor = fileProcessor;
			}
			
			public BatchDispatcherBuilder fileBatch(int fileBatchSize) {
				iFileBatchSize = fileBatchSize;
				return this;
			}

			public BatchDispatcherBuilder coreThreadSize(int coreThreadSize) {
				this.iMinThreadSize = coreThreadSize;
				return this;
			}
			
			public BatchDispatcherBuilder maxThreadSize(int maxThreadSize) {
				this.iMaxThreadSize = maxThreadSize;
				return this;
			}
			
			public BatchDispatcherBuilder queueSize(int iQueueSize) {
				this.iQueueSize = iQueueSize;
				return this;
			}

			public BatchDispatcherBuilder inputPath(String inputPath) {
				this.inputPath = inputPath;
				return this;
			}
			
			public BatchDispatcherBuilder sortingType(String sortingType) {
				this.sortingType = sortingType;
				return this;
			}
			
			public BatchDispatcherBuilder sortingCriteria(String sortingCriteria) {
				this.sortingCriteria = sortingCriteria;
				return this;
			}
			
			public BatchDispatcherBuilder interval(int scanInterval) {
				this.scanInterval = scanInterval;
				return this;
			}
			
			public BatchDispatcherBuilder inProcessExtension(String inProcessExt) {
				this.inProcessExt = inProcessExt;
				return this;
			}
			
			public BatchDispatcherBuilder suffixes(List<String> suffixes) {
				selectFileOnSuffixes = suffixes;
				return this;
			}
			
			public BatchDispatcherBuilder filters(List<String> filters) {
				strFilters = filters;
				return this;
			}
			
			public BatchDispatcherBuilder excludingTypes(List<String> excludeFileTypes) {
				exculdeFileTypes = excludeFileTypes;
				return this;
			}
			
			public BatchDispatcherBuilder containsList(List<String> containsList) {
				this.containsList = containsList;
				return this;
			}
			
			public BatchDispatcherBuilder fileRange(String fileRange) {
				this.fileRange = fileRange;
				return this;
			}
			
			public BatchDispatcherBuilder maxFileLimit(int maxFileLimit) {
				this.maxFileLimit = maxFileLimit;
				return this;
			}
			
			public BatchDispatcherBuilder noOfAttempt(int noOfAttempt) {
				this.noOfAttempt = noOfAttempt;
				return this;
			}
			
			public BatchDispatcher build() {
				return new BatchDispatcher(serverContext, threadPoolExecutor, taskFactory, fileProcessor, name, iMinThreadSize, iMaxThreadSize, iQueueSize, inputPath, inProcessExt, sortingCriteria, sortingType,
						noOfAttempt, maxFileLimit, fileRange, scanInterval, prefixes, strFilters, selectFileOnSuffixes, exculdeFileTypes, containsList,
						iFileBatchSize);
			}
		}
		
		public BatchDispatcher(NetVertexServerContext serverContext, ExecutorService threadPoolExecutor,
				Function<List<String>, Runnable> taskFactory,
				OFCSFileHelper fileProcessor, 
				String name, 
				int iMinThreadSize, 
				int iMaxThreadSize, 
				int iQueueSize, 
				String inputPath, 
				String inProcessExt, 
				String sortingCriteria, String sortingType, int noOfAttempt, 
				Integer maxFileLimit, String fileRange, int scanInterval, 
				List<String> prefixes, List<String> strFilters, 
				List<String> selectFileOnSuffixes, List<String> exculdeFileTypes, 
				List<String> containsList, int iFileBatchSize) {
		
			this.serverContext = serverContext;
			this.threadPoolExecutor = threadPoolExecutor;
			this.taskFactory = taskFactory;
			this.fileProcessor = fileProcessor;
			this.name = name;
			this.iMinThreadSize = iMinThreadSize;
			this.iMaxThreadSize = iMaxThreadSize;
			this.iQueueSize = iQueueSize;
			this.inputPath = inputPath;
			this.inProcessExt = inProcessExt;
			this.sortingCriteria = sortingCriteria;
			this.sortingType = sortingType;
			this.noOfAttempt = noOfAttempt;
			this.maxFileLimit = maxFileLimit;
			this.fileRange = fileRange;
			this.scanInterval = scanInterval;
			this.prefixes = prefixes;
			this.strFilters = strFilters;
			this.selectFileOnSuffixes = selectFileOnSuffixes;
			this.exculdeFileTypes = exculdeFileTypes;
			this.containsList = containsList;
			this.iFileBatchSize = iFileBatchSize;
		}
		
		@Override
		public void run(){
			try {
				String[] filesToBeProcessed = getFileNameList();
				processReceivedFiles(filesToBeProcessed);
			} catch (Exception ex) {
				LogManager.getLogger().error(MODULE, "Unexpected error occured while processing: " + ex.getMessage());
				LogManager.getLogger().trace(MODULE, ex);
			}
		}

		private boolean isStopRequested() {
			return false;
		}

		private boolean isImmediateExecuteOnStartUp() {
			return false;
		}


		private void execute(Runnable serviceSubProcess) {
			try {
				
				threadPoolExecutor.execute(serviceSubProcess);
			} catch (Exception e) {
				LogManager.getLogger().trace(MODULE, e);
			}
		}

		protected String[] getFileNameList() {
			LogManager.getLogger().debug(MODULE, "Scanning path: " + inputPath);
			
			String[] fileNameList = null;
			try {
				List<File> fileList = new ArrayList<>();

				if (true) {
					List<File> file = fileProcessor.getFilterFileList(new File(inputPath), selectFileOnSuffixes, inProcessExt,
							containsList, exculdeFileTypes, fileRange, maxFileLimit,
							serverContext,
							Alerts.FILE_GATEWAY_MAX_FILES_LIMIT_REACHED, MODULE);
					if(file.size() < 1){
						if(noOfAttempt > counter){
							counter++;
						} else if(noOfAttempt >= 0){
							//									serverContext.generateSystemAlert(AlertSeverity.INFO, Alerts.PARSING_SERVICE_NO_FILE_RECEIVED,MODULE, "No Files Received From Path :" + sourcePathList[i] + " ,Since Last " +  noFileAlertInt  + "Seconds. Parsing Service:" + getServiceIdentifier());
							LogManager.getLogger().error(MODULE, "No Files Received From Path :" + name + " ,Since Last " +  scanInterval  + "Seconds. Parsing Service:" + name);
						}
					}else{
						counter = 0;
						fileList.addAll(file);
					}

				} 
				if (fileList != null) {
					Collections.sort(fileList, new FileComparator(sortingType,
							sortingCriteria));
					fileNameList = new String[fileList.size()];
					int i = 0;
					for (File file : fileList) {
						fileNameList[i++] = file.getAbsolutePath();
					}
				}
			} catch (Exception e) {
				LogManager.getLogger().trace(MODULE, e);
			}
			return fileNameList;
		}


		private void processReceivedFiles(String[] filesToBeProcessed){
			int iTotalFiles = filesToBeProcessed.length;
			
			if (filesToBeProcessed != null && filesToBeProcessed.length > 0) {
				int iRequiredFileBatchSize;
				if(true){
					if(iTotalFiles > iFileBatchSize){
						int diff = iTotalFiles/iFileBatchSize;
						if(diff < iMaxThreadSize){
							iRequiredFileBatchSize = iTotalFiles/iMaxThreadSize;
							if(iRequiredFileBatchSize > 0)
								iFileBatchSize = iRequiredFileBatchSize;
						}
					}else{
						iRequiredFileBatchSize = iTotalFiles/iMaxThreadSize;
						if(iRequiredFileBatchSize > 0)
							iFileBatchSize = iRequiredFileBatchSize;
					}

					if(iTotalFiles < iMaxThreadSize)
						iFileBatchSize = 1;
				}/*else{
					iMinThreadSize = 1;
				}*/

				if(iTotalFiles > iFileBatchSize*iQueueSize){
					iFileBatchSize = (iTotalFiles/iQueueSize)+1;
				}
				LogManager.getLogger().info(MODULE, "Requierd File batch size in " + name + " for processing " + iFileBatchSize);

				StringWriter stringBuffer = new StringWriter();
				PrintWriter traceOut = new PrintWriter(stringBuffer);
				traceOut.println("Files received for processing from " + name);
				for (int i= 0 ; i < filesToBeProcessed.length ; i ++ ) {
					traceOut.println(filesToBeProcessed[i]);
				}
				LogManager.getLogger().trace(MODULE, stringBuffer.toString());

				List<List<String>> batchList = new ArrayList<>();

				int indexCounter = 0;
				int threadBatch = iMinThreadSize;
				List <String>fileList = new ArrayList<>();
				List <String>tmpfileList = new ArrayList<>();
				List <Integer>mapperList = new ArrayList<>();

				for(int index = 0; index < iTotalFiles; index++){
					tmpfileList.add(filesToBeProcessed[index]);
				}
				for(int index = 0; index < iTotalFiles; index++){
					fileList.add(tmpfileList.get(indexCounter));
					mapperList.add(indexCounter);
					if(threadBatch <= 0)
						break;
					indexCounter = indexCounter + threadBatch;
					if(fileList.size() == iFileBatchSize){
						batchList.add(fileList);
						if(true){
							threadBatch--;
						}
						fileList = new ArrayList<>();
						int rCounter = 0;
						for(int i : mapperList){
							tmpfileList.remove(i-rCounter);
							rCounter++;
						}
						mapperList = new ArrayList<>();
						indexCounter = 0;
					}
				}
				if(!tmpfileList.isEmpty() && !batchList.isEmpty()){
					mapperList = new ArrayList<>();
					for(int batchindex = 0; batchindex < iMinThreadSize; batchindex++){
						for(int index = 0; index < tmpfileList.size(); index++){
							if(batchindex >= iMinThreadSize)
								batchindex = 0;
							batchList.get(batchindex).add(tmpfileList.get(index));
							mapperList.add(index);
							batchindex++;
						}
						int rCounter = 0;
						for(int i : mapperList){
							tmpfileList.remove(i-rCounter);
							rCounter++;
						}
						mapperList = new ArrayList<>();
					}
				}
				if(!tmpfileList.isEmpty() && tmpfileList.size() < iFileBatchSize ){
					batchList.add(tmpfileList);
				}

				LogManager.getLogger().info(MODULE, "Total batches created for processing for " + name + " is " + batchList.size());
				
				for(List<String> batch : batchList) {
					if(!isStopRequested()) {
						try{
							execute(taskFactory.apply(batch));
						}catch(RejectedExecutionException rejectedException){
							LogManager.getLogger().warn(MODULE, "Problem while adding reqeust to queue, skipping processing for files  \n" + fileList);
							LogManager.getLogger().trace(MODULE, rejectedException);
						}
					}
				}
				
			} else {
				LogManager.getLogger().trace(MODULE, "For "+ name +", no file available for processing.");
			}
		}

	}


	class BatchProcessor implements Runnable {
		private static final String MODULE = "BATCH-PROCESSOR";
		private List <String>fileList;
		private HashMap <Object, Object> map;

		private BatchProcessor(List <String>fileList){
			this.fileList = fileList;
			map = new HashMap<>();
		}

		@Override
		public void run() {
			if (Thread.interrupted()) {
				LogManager.getLogger().info(MODULE,"Cancelling file batch process that is in the queue as stop signal is received for " + fileGatewayConfiguration.getName());
				LogManager.getLogger().trace(MODULE,"File batch for which processing is cancelled for " + fileGatewayConfiguration.getName() + fileList);
				return;
			}

			GeneralBatchPacket packet = null;
			try{
				packet = new GeneralBatchPacket(fileList);

				handleRequest(packet);

			}catch(Exception e){
				LogManager.getLogger().trace(MODULE, e);
			}catch(OutOfMemoryError e){
				throw e;
			}
		}

		protected void handleRequest(GeneralBatchPacket requestPacket) {
			GeneralBatchPacket batchPacket = null;
			boolean isFileDeletedSuccessfully = false;
			if (requestPacket != null && (requestPacket instanceof GeneralBatchPacket)) {
				batchPacket = (GeneralBatchPacket) requestPacket;
			} else {
				//				LogManager.getLogger().debug(MODULE, OFCSErrorMessageConstants.BATCH_EMPTY.getMessage(null, getServiceName()));
			}

			if (batchPacket != null && batchPacket.getBatch() != null && !batchPacket.getBatch().isEmpty()) {
				try {
					int batchId = 0;
					String strInpProcess = fileGatewayConfiguration.getInProcessExtension();

					Iterator<Object> iterator = (Iterator<Object>) batchPacket.getBatch().iterator();
					while (iterator.hasNext()) {
						if (Thread.interrupted()) {
							LogManager.getLogger().info(MODULE,"Cancelling file batch process that is in the queue as stop signal is received for " + fileGatewayConfiguration.getName());
							LogManager.getLogger().trace(MODULE,"File batch for which processing is cancelled for " + fileGatewayConfiguration.getName() + fileList);
							break;
						}
						String archivedFilePath = fileGatewayConfiguration.getArchivePath();
						long totalRecordCountForTps = 0;
						long totalMalformCount = 0;
						String fileName = null;
						File inProcessFile = null;
						File inputFile = null;
						boolean isValidFileForProcessing = false;
						long fileProcessStartMillis = System.currentTimeMillis();
						String outputFileExt = null;
						boolean isCompressedFile = false;
						boolean isCompressedOutputFile = false;
						String inProcessFileName = (String) iterator.next();
						//						crestelMediationParsingServiceCounterListner.decrementTotalPendingFiles();
						//						crestelMediationParsingServiceCounterListner.incrementTotalInprocessFiles();
						if (inProcessFileName != null) {
							inProcessFile = new File(inProcessFileName);
							int indx = inProcessFileName.indexOf(strInpProcess);
							fileName = inProcessFileName.substring(0, indx);
							if (inProcessFile.exists() && inProcessFile.canRead() && inProcessFile.canWrite()) {
								inputFile = new File(fileName);
								isValidFileForProcessing = true;
							}
							if (isValidFileForProcessing) {
								String srcPath = inputFile.getParent();
								boolean isParsedFileToDelete = false;
								boolean currFlag = true;
								boolean addTotalRecordCount = true;
								Timestamp timestamp = Utilities.getFileDateFromFileName(fileGatewayConfiguration, inputFile.getName());

								//String destinationPath = fileGatewayConfiguration.getDestinationInputPath();

								LogManager.getLogger().debug(MODULE, "Starting parsing process for file " + inProcessFileName);
								// MED-4116 Configured Error and Archived path at Service level
								String errorRecordDirPath = null;
								String errorFileDirPath = null;
								StringBuilder errorDir = new StringBuilder();
								if(Strings.isNullOrBlank(fileGatewayConfiguration.getErrorPath()) == false) {
									errorDir.append(fileGatewayConfiguration.getErrorPath());
								} else {
									errorDir.append(inputFile.getParentFile().getParent());
								}
								errorDir.append(File.separator);
								errorDir.append(OfflineRnCConstants.ERROR);
								errorDir.append(File.separator);
								errorDir.append(inputFile.getParentFile().getName());
								errorDir.append(OfflineRnCConstants.DASH);
								errorDir.append(fileGatewayConfiguration.getName());

								errorRecordDirPath = fileProcessor.getDatedPath(OfflineConstants.FILEGROUPINGTYPEDAILY, 
										errorDir.toString() + OfflineConstants.RECORD);

								errorFileDirPath = fileProcessor.getDatedPath(OfflineConstants.FILEGROUPINGTYPEDAILY, 
										errorDir.toString() + OfflineConstants.FILE);

								if(!Utilities.createDirIfNotExist(errorRecordDirPath)) {
									LogManager.getLogger().error(MODULE, OFCSErrorMessages.DIR_CREATION_FAILED.getMessage("Error Records", errorRecordDirPath));
								}
								if(!Utilities.createDirIfNotExist(errorFileDirPath)) {
									LogManager.getLogger().error(MODULE, OFCSErrorMessages.DIR_CREATION_FAILED.getMessage("Error Files", errorFileDirPath));
								}

								String strPath = inputFile.getParent();
								String malformRecordDirPath = fileProcessor.getDatedPath(OfflineConstants.FILEGROUPINGTYPEDAILY, inputFile.getParentFile().getParent() +
										File.separator + OfflineConstants.MALFORM + File.separator + inputFile.getParentFile().getName() + File.separator + OfflineConstants.RECORD);

								isCompressedFile = fileGatewayConfiguration.isInputFileCompressed();
								isCompressedOutputFile = fileGatewayConfiguration.isOutputFileCompressed();
								
								outputFileExt= OfflineConstants.JSON;
								if(isCompressedOutputFile){
									outputFileExt = OfflineConstants.GZ;
								}
								
								if (fileProcessor.isSpaceAvailableOnDisk(totalSpaceMethod, requiredDiskSpaceinGB, intermediateOutputDir)) {
									int fileLen = (int) inProcessFile.length();

									if (fileLen == 0) {
										Map<String, Object> resultMap = handleZeroLengthFile(inputFile,inProcessFile,strPath,fileName, inProcessFileName, batchId, fileGatewayConfiguration, strInpProcess, intermediateOutputDir,
												errorFileDirPath, fileLen, srcPath, timestamp);

										if(resultMap.get(IS_DELETE_PARSED_FILE) != null)
											isParsedFileToDelete = Boolean.valueOf(resultMap.get(IS_DELETE_PARSED_FILE).toString());
										if(resultMap.get(FILE_COUNTER_FLAG) != null)
											currFlag = Boolean.valueOf(resultMap.get(FILE_COUNTER_FLAG).toString());

									} else {
										Map<String, Object> resultMap = handleValidFile(inputFile, inProcessFile,
												fileGatewayConfiguration, intermediateOutputDir, isCompressedFile, outputFileExt, errorRecordDirPath,malformRecordDirPath, batchId, fileLen, errorFileDirPath, strPath, strInpProcess,
												inProcessFileName, fileName, addTotalRecordCount, totalRecordCountForTps, srcPath, "", timestamp);

										if(resultMap.get(IS_DELETE_PARSED_FILE) != null)
											isParsedFileToDelete = Boolean.valueOf(resultMap.get(IS_DELETE_PARSED_FILE).toString());
										if(resultMap.get(FILE_COUNTER_FLAG) != null)
											currFlag = Boolean.valueOf(resultMap.get(FILE_COUNTER_FLAG).toString());
										if(resultMap.get(IS_ADD_TOTAL_RECORD_COUNT) != null)
											addTotalRecordCount = Boolean.valueOf(resultMap.get(IS_ADD_TOTAL_RECORD_COUNT).toString());
										if(resultMap.get(TOTAL_RECORDS_COUNT_FOR_TPS) != null)
											totalRecordCountForTps = Long.parseLong(resultMap.get(TOTAL_RECORDS_COUNT_FOR_TPS).toString());
										if(resultMap.get(FileGatewayConstants.TOTAL_MALFORM_CDR_COUNT) != null){
											totalMalformCount = (Integer)resultMap.get(FileGatewayConstants.TOTAL_MALFORM_CDR_COUNT);
										}
									}
									if(isDiskSpaceProblem){
										LogManager.getLogger().warn(MODULE, "Disk space is Resolved.");
										getServerContext().generateSystemAlert(AlertSeverity.INFO, Alerts.FILE_GATEWAY_DISK_SPACE_RESOLVED, MODULE, "Disk space is Resolved.");
										isDiskSpaceProblem = false;
									}
								} else {
									LogManager.getLogger().warn(MODULE, "Disk space is not available for FileName : " + inProcessFileName + " on path - " + intermediateOutputDir);
									getServerContext().generateSystemAlert(AlertSeverity.CRITICAL, Alerts.FILE_GATEWAY_NO_SPACE_LEFT_ON_DEVICE, MODULE, "Disk space is not available for FileName : " + inProcessFileName + " on path - " + intermediateOutputDir);
									isDiskSpaceProblem = true;
									boolean bRenameSuccess = inProcessFile.renameTo(new File(fileName));
									if (!bRenameSuccess) {
										LogManager.getLogger().debug(MODULE, "Could rename the inprocess file to normal - " + inProcessFile.getName());
									}
									currFlag = false;
								}

								if(totalMalformCount > 0){
									//	crestelMediationParsingServiceCounterListner.incrementTotalPartiallySuccessFiles();
								}else{

									if(currFlag){
										// crestelMediationParsingServiceCounterListner.incrementTotalSuccessFiles();
									}else{
										// crestelMediationParsingServiceCounterListner.incrementTotalFailureFiles();
									}
								}

								// MOVING PARSED FILES
								try{
									if(isParsedFileToDelete){
										String archivedDirPath = null;
										StringBuilder archivedDir = new StringBuilder();
										if(StringUtils.isNotEmpty(archivePath)) {
											archivedDir.append(archivePath);
										} else {
											archivedDir.append(inputFile.getParentFile().getParent());
										}
										archivedDir.append(File.separator);
										/*archivedDir.append(OfflineConstants.ARCHIVED);
											archivedDir.append(File.separator);*/
										if(true){
											archivedDir.append(inputFile.getParentFile().getName());
											archivedDir.append(OfflineRnCConstants.DASH);
											archivedDir.append(fileGatewayConfiguration.getName());
											archivedDir.append(File.separator);
										}
										archivedDirPath = fileProcessor.getDatedPath("test", archivedDir.toString());
										File archiveDir = new File(archivedDirPath);
										if (!archiveDir.exists()) {
											boolean bMakeDir = archiveDir.mkdirs();
											if (!bMakeDir) {
												LogManager.getLogger().error(MODULE,OFCSErrorMessages.DIR_CREATION_FAILED.getMessage("Destination", archivedDirPath));
											}
										}


										boolean isFileRenameSucceeded = false;
										archivedFilePath = archivedDirPath + File.separator + inputFile.getName();
										isFileRenameSucceeded = inProcessFile.renameTo(new File(archivedFilePath));
										if (!isFileRenameSucceeded) {
											try {
												FileUtil.move(inProcessFile, new File(archivedFilePath));
											}catch(IOException e) {
												LogManager.getLogger().trace(MODULE, e);
												LogManager.getLogger().warn(MODULE, "Could not move to archive after processing file " + inProcessFile.getName());
												isFileRenameSucceeded = inProcessFile.renameTo(new File(fileName + OfflineRnCConstants.PROCESSED_FILE_EXTENSION));
												if (!isFileRenameSucceeded) {
													LogManager.getLogger().warn(MODULE, "Could not rename to processed after parsing file " + inProcessFile.getName());
												}
											}
										}
									}//END OF MOVING PARSED FILES
								} catch (Exception e) {
									LogManager.getLogger().error(MODULE, "Error while MOVING PARSED FILES, reason : " + e.getMessage());
									LogManager.getLogger().trace(MODULE, e);
								}
							} else {
								LogManager.getLogger().warn(MODULE, "Not a valid file for process - FileName : " + inProcessFileName);
								boolean bRenameSuccess = inProcessFile.renameTo(new File(fileName));
								if (!bRenameSuccess) {
									LogManager.getLogger().debug(MODULE, "Could rename the inprocess file to normal - " + inProcessFile.getName());
								}
							}
						} else {
							LogManager.getLogger().error(MODULE, "Could not start parsing process for file " + inProcessFileName + ", is not a valid file, so renaming to error");
						}
						long timeDifference = System.currentTimeMillis() - fileProcessStartMillis;
						if(timeDifference > 0){
							double timeDifferenceInSeconds = timeDifference / 1000.0;
							long tps = (long) (totalRecordCountForTps / timeDifferenceInSeconds);
							//							crestelMediationParsingServiceCounterListner.setAvgTPS(tps * getCoreThreadSize());
							LogManager.getLogger().info(MODULE, "Entity PARSING, File : " + inProcessFileName + " , Process Time : " + (timeDifference) + "ms. " + ", Total Records Processed : " + totalRecordCountForTps + " , TPS :" + tps);
						}else{
							LogManager.getLogger().info(MODULE, "Entity PARSING, File : " + inProcessFileName + " , Process Time : " + (timeDifference) + "ms. " + ", Total Records Processed : " + totalRecordCountForTps);
						}
						//						crestelMediationParsingServiceCounterListner.decrementTotalInprocessFiles();
					}

				} catch (Exception e) {
					LogManager.getLogger().error(MODULE, OFCSErrorMessages.HANDLE_REQUEST_FAIL.generateTraceMessage(e, null, fileGatewayConfiguration.getName()));
					LogManager.getLogger().trace(MODULE, e);
				} finally {
				}
				batchPacket = null;
			} else {
				LogManager.getLogger().debug(MODULE, OFCSErrorMessages.BATCH_EMPTY.getMessage(null, fileGatewayConfiguration.getName()));
			}
		}

		private HashMap<String, Object> handleZeroLengthFile(File inputFile,File inProcessFile,String strPath,String fileName, String inProcessFileName, int batchId, FileGatewayConfiguration pathData, String strInpProcess, String destInputDirPath,
				String errorFileDirPath, int fileLen,String sourcePath, Timestamp timestamp) throws SQLException{

			HashMap<String, Object> resultMap = new HashMap<>();
			boolean bRenameSuccess = inProcessFile.renameTo(new File(fileName + OfflineRnCConstants.WARN_FILE_EXTENSION));
			if (!bRenameSuccess) {
				LogManager.getLogger().debug(MODULE, "Could rename the inprocess file to .warn when connection is null - " + inProcessFile.getName());
			}
			resultMap.put(FILE_COUNTER_FLAG, false);
			//			crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).incrementTotalFailureFiles();
			//			getServiceContext().getServerContext().getAlertManager().getCrestelAlertManager().generateAlert(AlertsEnum.PARSING_SERVICE_ERROR, MODULE, "Error in Processing File in Parsing Service " + getServiceContext().getServiceIdentifier(),getServiceContext().getServiceIdentifier());
			return resultMap;
		}

		@SuppressWarnings("unchecked")
		private Map<String,Object> handleValidFile(File inputFile, File inProcessFile, FileGatewayConfiguration pathData,String destInputDirPath, boolean isCompressedFile,
				String outputFileExt, String errorRecordDirPath,String malformRecordPath, int batchId, int fileLen, String errorFileDirPath, String strPath, String strInpProcess,
				String inProcessFileName, String fileName, boolean addTotalRecordCount, long totalRecordCountForTps,
				String sourcePath, String writefilePrefix, Timestamp timestamp) throws SQLException {
			int totalCloneCDR = 0;
			int totalRecordCountForLogs = 0;
			int pluginWiseErrorCounter = 0;
			int totalMalformPacketCount = 0;
			int totalPacketCount = 0;
			long destinationFileSize = 0;
			long fileDateInFile = 0;
			long errorFileSize = 0; 
			Map<String,Object> returnMap = new HashMap<>();
			String fileNameWithOutExt = null;
			int iExtIndex = inputFile.getName().lastIndexOf(".");
			if (iExtIndex > 0)
				fileNameWithOutExt = inputFile.getName().substring(0, iExtIndex);
			else
				fileNameWithOutExt = inputFile.getName();
			Set<String> splittedFileNameList = null;
			try {
				List<File> fileList = new ArrayList<>();
				fileList.add(inProcessFile);
				GeneralBatchPacket pluginProcessRequest = new GeneralBatchPacket(fileList);
				pluginProcessRequest.setParameter(FileGatewayConstants.INPUT_FILE, inputFile);
				pluginProcessRequest.setParameter(FileGatewayConstants.FILE_NAME_WITHOUT_EXT, fileNameWithOutExt);
				pluginProcessRequest.setParameter(FileGatewayConstants.PATH_DATA, pathData);
				pluginProcessRequest.setParameter(FileGatewayConstants.DEST_DIR_PATH, destInputDirPath);
				pluginProcessRequest.setParameter(FileGatewayConstants.IS_COMPRESSED_FILE, isCompressedFile);
				pluginProcessRequest.setParameter(FileGatewayConstants.OUTFILE_EXT, outputFileExt);
				pluginProcessRequest.setParameter(FileGatewayConstants.ERROR_RECORD_DIR_PATH, errorRecordDirPath);
				pluginProcessRequest.setParameter(FileGatewayConstants.MALFORM_RECORD_DIR_PATH, malformRecordPath);
		//		pluginProcessRequest.setParameter(FileGatewayConstants.COUNTER_LISTENER, crestelMediationParsingServiceCounterListner);
				pluginProcessRequest.setParameter(FileGatewayConstants.PLUGGABLE_AGGREGATION, null);
				CommonResponsePacket responsePacket = processUsingServicePlugins(pluginProcessRequest);
				Map<String, HashMap<String, Object>> resultMap = (Map<String, HashMap<String, Object>>) responsePacket.getParameter(FileGatewayConstants.RESULT_MAP);
				if(responsePacket.getParameter(FileGatewayConstants.FILE_DATE) != null){
					Date fileDate = (Date) responsePacket.getParameter(FileGatewayConstants.FILE_DATE);
					fileDateInFile = fileDate.getTime();
				}
				// RENAMING PROCESSED FILES
				boolean isFileRenameSucceeded = false;
				if (resultMap != null && !resultMap.isEmpty()) {
					splittedFileNameList = resultMap.keySet();
					Iterator<Map.Entry<String, HashMap<String, Object>>> iterator2 = resultMap.entrySet().iterator();
					long fileSize = inProcessFile.length();
					while (iterator2.hasNext()) {
						Map.Entry<String, HashMap<String, Object>> entry = iterator2.next();
						String splittedFileName = entry.getKey();
						Map<String, Object> valueMap = entry.getValue();
						String destInputFilePath = (String) valueMap.get(FileGatewayConstants.FILE_PATH_NAME);
						int iTotalCDR = (Integer) valueMap.get(FileGatewayConstants.TOTAL_CDR_COUNT);
						int iTotalCloneCDR = (Integer) valueMap.get(FileGatewayConstants.TOTAL_CLONE_CDR_COUNT);
						int iTotalMalformCDR = (Integer) valueMap.get(FileGatewayConstants.TOTAL_MALFORM_CDR_COUNT);
						int iTotalErrorCDR = (Integer) valueMap.get(FileGatewayConstants.TOTAL_ERROR_CDR_COUNT);
						int iTotalPacketCount = 0;
						if(valueMap.get(FileGatewayConstants.TOTAL_PACKET_COUNT) != null){
							iTotalPacketCount = (Integer) valueMap.get(FileGatewayConstants.TOTAL_PACKET_COUNT);
						}else{
							iTotalPacketCount = iTotalCDR ;
						}
						if(valueMap.get(FileGatewayConstants.FILE_SIZE) != null){
							destinationFileSize = (Long) valueMap.get(FileGatewayConstants.FILE_SIZE);
						}
						if(valueMap.get(FileGatewayConstants.ERROR_FILE_SIZE) != null){
							errorFileSize = (Long) valueMap.get(FileGatewayConstants.ERROR_FILE_SIZE);
						}
						File successFile = new File(destInputFilePath);
						if(successFile.exists()){
							isFileRenameSucceeded = new File(destInputFilePath).renameTo(new File(destInputDirPath + File.separator + writefilePrefix + splittedFileName.split("\\.")[0] + outputFileExt));
							if (!isFileRenameSucceeded) {
								LogManager.getLogger().warn(MODULE, "Could not change the state of file to normal after processing " + destInputFilePath);
								isFileRenameSucceeded = new File(destInputFilePath).renameTo(new File(destInputDirPath + File.separator + splittedFileName + outputFileExt + OfflineRnCConstants.PROCESSED_FILE_EXTENSION));
								if (!isFileRenameSucceeded) {
									LogManager.getLogger().warn(MODULE, "Could not rename to processed after parsing file " + destInputFilePath);
								}
							}
						}
						if (true) {
							if (pathData != null && pathData.isDestinationPathAvailable()) {
								fileProcessor.renameInProcessFileOnCopyFolders(splittedFileName + outputFileExt, pathData.getDestinationPathList(), outputDirFormat, strInpProcess);
							}
						} else if (bFileCopyFlag) {
							fileProcessor.renameInProcessFileOnCopyFolders(splittedFileName + outputFileExt, arrFileCopy, outputDirFormat, strInpProcess);
						}
						totalRecordCountForTps += iTotalCDR;
						totalRecordCountForLogs += iTotalCDR;
						pluginWiseErrorCounter += iTotalErrorCDR;
						totalCloneCDR += iTotalCloneCDR;
						totalMalformPacketCount += iTotalMalformCDR;
						totalPacketCount += iTotalPacketCount;
					}
//					crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).incrementTotalSuccessFiles();
//					crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalSuccessRecords(totalRecordCountForLogs);
//					crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalFailureRecords(pluginWiseErrorCounter);
//					crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalCloneRecords(totalCloneCDR);
//					crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalPackets(totalPacketCount);

					if(addTotalRecordCount)	{
//						crestelMediationParsingServiceCounterListner.addTotalCloneRecords(totalCloneCDR);
//						crestelMediationParsingServiceCounterListner.addTotalProcessedPackets(totalPacketCount);
//						crestelMediationParsingServiceCounterListner.addTotalRecords((long) totalRecordCountForLogs + pluginWiseErrorCounter - totalCloneCDR);
						returnMap.put(IS_ADD_TOTAL_RECORD_COUNT,false);
					}
				}
				returnMap.put(IS_DELETE_PARSED_FILE, true);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, OFCSErrorMessages.HANDLE_REQUEST_FAIL.generateTraceMessage(e,null,fileGatewayConfiguration.getName()+", so renaming to error"));
				LogManager.getLogger().trace(MODULE, e);

				actionWhileException(pathData, fileNameWithOutExt, strInpProcess, inProcessFile, inputFile, fileName, splittedFileNameList, strPath, batchId, fileLen, e, errorFileDirPath, fileDateInFile, timestamp);
				returnMap.put(FILE_COUNTER_FLAG, false);
//				crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).incrementTotalFailureFiles();
//				crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalSuccessRecords(totalRecordCountForLogs);
//				crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalFailureRecords(pluginWiseErrorCounter);
//				crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalCloneRecords(totalCloneCDR);
//				crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalPackets(totalPacketCount);

				getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.FILE_GATEWAY_PARSING_ERROR, MODULE, "Error in Processing File in Parsing Service " + fileGatewayConfiguration.getName());
			}
			
			returnMap.put(TOTAL_RECORDS_COUNT_FOR_TPS, totalRecordCountForTps);
			returnMap.put(FileGatewayConstants.TOTAL_MALFORM_CDR_COUNT, totalMalformPacketCount);
			return returnMap;
		}

		public final CommonResponsePacket processUsingServicePlugins(GeneralBatchPacket requestPacket) {
			CommonResponsePacket responsePacket = null;

			ParsingPluginRequest pluginRequest = new ParsingPluginRequest(requestPacket);
			executeUsingPlugin(parsingPlugin, 0, pluginRequest);
			responsePacket = pluginRequest.getResponsePacket();

			return responsePacket;
		}

		/**
		 * @param plugin
		 * the runnable object for execution.
		 */
		private void executeUsingPlugin(final ParsingPlugin plugin, long timeout, final ParsingPluginRequest pluginRequest) {
			try {
				plugin.processRequest(pluginRequest);
				pluginRequest.setProcessOver();
			} catch (ProcessFailException e) {
				LogManager.getLogger().trace(MODULE, e);
				pluginRequest.setException(e);
				pluginRequest.setResponseCode(BaseDriverResponseCode.ERROR);
				pluginRequest.setResponseMessage("General Error, " + e.getMessage());
				LogManager.getLogger().warn(MODULE, "Error during processRequest operation for driver <"	+ fileGatewayConfiguration.getName() + ">, reason : "	+ e.getMessage());
			} catch (Exception e) {
				pluginRequest.setException(e);
				pluginRequest.setResponseCode(BaseDriverResponseCode.ERROR);
				pluginRequest.setResponseMessage("General Error, " + e.getMessage());
				LogManager.getLogger().warn(MODULE, "Error during processRequest operation for driver <"	+ fileGatewayConfiguration.getName() + ">, reason : "	+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}

		/**
		 * Action while exception.
		 *
		 * @param pathData the path data
		 * @param fileNameWithOutExt the file name with out ext
		 * @param strInpProcess the str inp process
		 * @param inProcessFile the in process file
		 * @param inputFile the input file
		 * @param fileName the file name
		 * @param connection the connection
		 * @param pstmtForFileStat the pstmt for file stat
		 * @param splittedFileNameList the splitted file name list
		 * @param parsingPlugin the parsing plugin
		 * @param strPath the str path
		 * @param batchId the batch id
		 * @param fileLen the file len
		 * @param e the e
		 * @throws GeneralInsertUpdateSQLException the general insert update sql exception
		 * @throws SQLException the sQL exception
		 */
		private void actionWhileException(FileGatewayConfiguration pathData, String fileNameWithOutExt, String strInpProcess, File inProcessFile,
				File inputFile, String fileName,
				Set<String> splittedFileNameList, String strPath, int batchId, int fileLen, Exception e, 
				String errorFileDirPath, long fileDateInFile, Timestamp timestamp) {

			if (fileNameWithOutExt != null) {
				fileProcessor.deleteInprocessFile(fileNameWithOutExt, getDestinationPaths(), strInpProcess);
				if (pathData.isDestinationPathAvailable()) {
					fileProcessor.deleteInProcessFileOnCopyFolders(fileNameWithOutExt, pathData.getDestinationPathList(), outputDirFormat, strInpProcess);
				}
			}

			LogManager.getLogger().warn(MODULE, "File failed to process, So moving inprocess file to error directory, file name : " + inProcessFile.getName());
			File errorFileDirPathStr = new File(errorFileDirPath);
			if (!errorFileDirPathStr.exists()) {
				boolean bMakeDir = errorFileDirPathStr.mkdirs();
				if (!bMakeDir) {
					LogManager.getLogger().error(MODULE, "Could not create the directory - " + errorFileDirPathStr);
				}
			}
			boolean isFileRenameSucceeded = inProcessFile.renameTo(new File(errorFileDirPath + File.separator + inputFile.getName()));
			if (!isFileRenameSucceeded) {
				LogManager.getLogger().warn(MODULE, "Could not rename inProcessFile file to .error during IOException - s" + inProcessFile.getName());
			}
		}
	}
	
	
	private class IntermediateOutputBatchProcessor implements Runnable {

		private static final String MODULE = "INTERMEDIATE-OUT-BATCH-PROCESSOR";
		private List <String>fileList;
		private HashMap <Object, Object> map;

		private IntermediateOutputBatchProcessor(List <String>fileList){
			this.fileList = fileList;
			map = new HashMap<>();
		}

		@Override
		public void run() {
			if (Thread.interrupted()) {
				LogManager.getLogger().info(MODULE,"Cancelling file batch process that is in the queue as stop signal is received for " + fileGatewayConfiguration.getName());
				LogManager.getLogger().trace(MODULE,"File batch for which processing is cancelled for " + fileGatewayConfiguration.getName() + fileList);
				return;
			}

			GeneralBatchPacket packet = null;
			try{
				packet = new GeneralBatchPacket(fileList);

				handleRequest(packet);

			}catch(Exception e){
				LogManager.getLogger().trace(MODULE, e);
			}
		}

		protected void handleRequest(GeneralBatchPacket requestPacket) {
			GeneralBatchPacket batchPacket = null;
			boolean isFileDeletedSuccessfully = false;
			if (requestPacket != null && (requestPacket instanceof GeneralBatchPacket)) {
				batchPacket = (GeneralBatchPacket) requestPacket;
			} else {
				//				LogManager.getLogger().debug(MODULE, OFCSErrorMessageConstants.BATCH_EMPTY.getMessage(null, getServiceName()));
			}

			if (batchPacket != null && batchPacket.getBatch() != null && (batchPacket.getBatch().isEmpty() == false)) {
				try {
					int batchId = 0;
					String strInpProcess = fileGatewayConfiguration.getInProcessExtension();

					for(Object batch : batchPacket.getBatch()) {
							if (Thread.interrupted()) {
							LogManager.getLogger().info(MODULE,"Cancelling file batch process that is in the queue as stop signal is received for " + fileGatewayConfiguration.getName());
							LogManager.getLogger().trace(MODULE,"File batch for which processing is cancelled for " + fileGatewayConfiguration.getName() + fileList);
							break;
						}
						long totalRecordCountForTps = 0;
						long totalMalformCount = 0;
						String fileName = null;
						File inProcessFile = null;
						File inputFile = null;
						boolean isValidFileForProcessing = false;
						long fileProcessStartMillis = System.currentTimeMillis();
						String outputFileExt = null;
						boolean isCompressedFile = false;
						boolean isCompressedOutputFile = false;
						String inProcessFileName = (String) batch;
						//						crestelMediationParsingServiceCounterListner.decrementTotalPendingFiles();
						//						crestelMediationParsingServiceCounterListner.incrementTotalInprocessFiles();
						if (inProcessFileName != null) {
							inProcessFile = new File(inProcessFileName);
							int indx = inProcessFileName.indexOf(strInpProcess);
							fileName = inProcessFileName.substring(0, indx);
							if (inProcessFile.exists() && inProcessFile.canRead() && inProcessFile.canWrite()) {
								inputFile = new File(fileName);
								isValidFileForProcessing = true;
							}
							if (isValidFileForProcessing) {
								String srcPath = inputFile.getParent();
								boolean isParsedFileToDelete = false;
								boolean currFlag = true;
								boolean addTotalRecordCount = true;
								Timestamp timestamp = Utilities.getFileDateFromFileName(fileGatewayConfiguration, inputFile.getName());

								String destinationPath = fileGatewayConfiguration.getDestinationInputPath();

								LogManager.getLogger().debug(MODULE, "Starting parsing process for file " + inProcessFileName);
								// MED-4116 Configured Error and Archived path at Service level
								String errorRecordDirPath = null;
								String errorFileDirPath = null;
								StringBuilder errorDir = new StringBuilder();
								if(Strings.isNullOrBlank(fileGatewayConfiguration.getErrorPath()) == false) {
									errorDir.append(fileGatewayConfiguration.getErrorPath());
								} else {
									errorDir.append(inputFile.getParentFile().getParent());
								}
								errorDir.append(File.separator);
								errorDir.append(OfflineRnCConstants.ERROR);
								errorDir.append(File.separator);
								errorDir.append(inputFile.getParentFile().getName());
								errorDir.append(OfflineRnCConstants.DASH);
								errorDir.append(fileGatewayConfiguration.getName());

								errorRecordDirPath = fileProcessor.getDatedPath(OfflineConstants.FILEGROUPINGTYPEDAILY, 
										errorDir.toString() + OfflineConstants.RECORD);

								errorFileDirPath = fileProcessor.getDatedPath(OfflineConstants.FILEGROUPINGTYPEDAILY, 
										errorDir.toString() + OfflineConstants.FILE);

								if(!Utilities.createDirIfNotExist(errorRecordDirPath)) {
									LogManager.getLogger().error(MODULE, OFCSErrorMessages.DIR_CREATION_FAILED.getMessage("Error Records", errorRecordDirPath));
								}
								if(!Utilities.createDirIfNotExist(errorFileDirPath)) {
									LogManager.getLogger().error(MODULE, OFCSErrorMessages.DIR_CREATION_FAILED.getMessage("Error Files", errorFileDirPath));
								}

								String strPath = inputFile.getParent();
								String malformRecordDirPath = fileProcessor.getDatedPath(OfflineConstants.FILEGROUPINGTYPEDAILY, inputFile.getParentFile().getParent() +
										File.separator + OfflineConstants.MALFORM + File.separator + inputFile.getParentFile().getName() + File.separator + OfflineConstants.RECORD);

								isCompressedFile = fileGatewayConfiguration.isInputFileCompressed();
								isCompressedOutputFile = fileGatewayConfiguration.isOutputFileCompressed();
								
								outputFileExt= OfflineConstants.CSV;
								if(isCompressedOutputFile){
									outputFileExt = OfflineConstants.GZ;
								}
								
								if (fileProcessor.isSpaceAvailableOnDisk(totalSpaceMethod, requiredDiskSpaceinGB, outputDir)) {
									int fileLen = (int) inProcessFile.length();

									if (fileLen == 0) {
										Map<String, Object> resultMap = handleZeroLengthFile(inputFile,inProcessFile,strPath,fileName, inProcessFileName, batchId, fileGatewayConfiguration, strInpProcess, outputDir,
												errorFileDirPath, fileLen, srcPath, timestamp);

										if(resultMap.get(IS_DELETE_PARSED_FILE) != null)
											isParsedFileToDelete = Boolean.valueOf(resultMap.get(IS_DELETE_PARSED_FILE).toString());
										if(resultMap.get(FILE_COUNTER_FLAG) != null)
											currFlag = Boolean.valueOf(resultMap.get(FILE_COUNTER_FLAG).toString());

									} else {
										Map<String, Object> resultMap = handleValidIntermediateFile(inputFile, inProcessFile,
												fileGatewayConfiguration, outputDir, isCompressedFile, outputFileExt, errorRecordDirPath,malformRecordDirPath, batchId, fileLen, errorFileDirPath, strPath, strInpProcess,
												inProcessFileName, fileName, addTotalRecordCount, totalRecordCountForTps, srcPath, "", timestamp);

										if(resultMap.get(IS_DELETE_PARSED_FILE) != null)
											isParsedFileToDelete = Boolean.valueOf(resultMap.get(IS_DELETE_PARSED_FILE).toString());
										if(resultMap.get(FILE_COUNTER_FLAG) != null)
											currFlag = Boolean.valueOf(resultMap.get(FILE_COUNTER_FLAG).toString());
										if(resultMap.get(IS_ADD_TOTAL_RECORD_COUNT) != null)
											addTotalRecordCount = Boolean.valueOf(resultMap.get(IS_ADD_TOTAL_RECORD_COUNT).toString());
										if(resultMap.get(TOTAL_RECORDS_COUNT_FOR_TPS) != null)
											totalRecordCountForTps = Long.parseLong(resultMap.get(TOTAL_RECORDS_COUNT_FOR_TPS).toString());
										if(resultMap.get(FileGatewayConstants.TOTAL_MALFORM_CDR_COUNT) != null){
											totalMalformCount = (Integer)resultMap.get(FileGatewayConstants.TOTAL_MALFORM_CDR_COUNT);
										}
									}
									if(isDiskSpaceProblem){
										LogManager.getLogger().warn(MODULE, "Disk space is Resolved.");
										getServerContext().generateSystemAlert(AlertSeverity.INFO, Alerts.FILE_GATEWAY_DISK_SPACE_RESOLVED, MODULE, "Disk space is Resolved.");
										isDiskSpaceProblem = false;
									}
								} else {
									LogManager.getLogger().warn(MODULE, "Disk space is not available for FileName : " + inProcessFileName + " on path - " + outputDir);
									getServerContext().generateSystemAlert(AlertSeverity.CRITICAL, Alerts.FILE_GATEWAY_NO_SPACE_LEFT_ON_DEVICE, MODULE, "Disk space is not available for FileName : " + inProcessFileName + " on path - " + outputDir);
									isDiskSpaceProblem = true;
									boolean bRenameSuccess = inProcessFile.renameTo(new File(fileName));
									if (!bRenameSuccess) {
										LogManager.getLogger().debug(MODULE, "Could rename the inprocess file to normal - " + inProcessFile.getName());
									}
									currFlag = false;
								}

								if(totalMalformCount > 0){
									//	crestelMediationParsingServiceCounterListner.incrementTotalPartiallySuccessFiles();
								}else{

									if(currFlag){
										// crestelMediationParsingServiceCounterListner.incrementTotalSuccessFiles();
									}else{
										// crestelMediationParsingServiceCounterListner.incrementTotalFailureFiles();
									}
								}

								// MOVING PARSED FILES
								try{
									if(isParsedFileToDelete){
										String archivedDirPath = null;
										StringBuilder archivedDir = new StringBuilder();
										if(StringUtils.isNotEmpty(archivePath)) {
											archivedDir.append(archivePath);
										} else {
											archivedDir.append(inputFile.getParentFile().getParent());
										}
										archivedDir.append(File.separator);
										/*archivedDir.append(OfflineConstants.ARCHIVED);
											archivedDir.append(File.separator);*/
										if(true){
											archivedDir.append(inputFile.getParentFile().getName());
											archivedDir.append(OfflineRnCConstants.DASH);
											archivedDir.append(fileGatewayConfiguration.getName());
											archivedDir.append(File.separator);
										}
										archivedDirPath = fileProcessor.getDatedPath("test", archivedDir.toString());
										File archiveDir = new File(archivedDirPath);
										if (!archiveDir.exists()) {
											boolean bMakeDir = archiveDir.mkdirs();
											if (!bMakeDir) {
												LogManager.getLogger().error(MODULE,OFCSErrorMessages.DIR_CREATION_FAILED.getMessage("Destination", archivedDirPath));
											}
										}

									}//END OF MOVING PARSED FILES
								} catch (Exception e) {
									LogManager.getLogger().error(MODULE, "Error while MOVING PARSED FILES, reason : " + e.getMessage());
									LogManager.getLogger().trace(MODULE, e);
								}
							} else {
								LogManager.getLogger().warn(MODULE, "Not a valid file for process - FileName : " + inProcessFileName);
								boolean bRenameSuccess = inProcessFile.renameTo(new File(fileName));
								if (!bRenameSuccess) {
									LogManager.getLogger().debug(MODULE, "Could rename the inprocess file to normal - " + inProcessFile.getName());
								}
							}
						} else {
							LogManager.getLogger().error(MODULE, "Could not start parsing process for file " + inProcessFileName + ", is not a valid file, so renaming to error");
						}
						long timeDifference = System.currentTimeMillis() - fileProcessStartMillis;
						if(timeDifference > 0){
							double timeDifferenceInSeconds = timeDifference / 1000.0;
							long tps = (long) (totalRecordCountForTps / timeDifferenceInSeconds);
							//							crestelMediationParsingServiceCounterListner.setAvgTPS(tps * getCoreThreadSize());
							LogManager.getLogger().info(MODULE, "Entity PARSING, File : " + inProcessFileName + " , Process Time : " + (timeDifference) + "ms. " + ", Total Records Processed : " + totalRecordCountForTps + " , TPS :" + tps);
						}else{
							LogManager.getLogger().info(MODULE, "Entity PARSING, File : " + inProcessFileName + " , Process Time : " + (timeDifference) + "ms. " + ", Total Records Processed : " + totalRecordCountForTps);
						}
						//						crestelMediationParsingServiceCounterListner.decrementTotalInprocessFiles();
					}

				} catch (Exception e) {
					LogManager.getLogger().error(MODULE, OFCSErrorMessages.HANDLE_REQUEST_FAIL.generateTraceMessage(e, null, fileGatewayConfiguration.getName()));
					LogManager.getLogger().trace(MODULE, e);
				} finally {
				}
				batchPacket = null;
			} else {
				LogManager.getLogger().debug(MODULE, OFCSErrorMessages.BATCH_EMPTY.getMessage(null, fileGatewayConfiguration.getName()));
			}
		}

		private HashMap<String, Object> handleZeroLengthFile(File inputFile,File inProcessFile,String strPath,String fileName, String inProcessFileName, int batchId, FileGatewayConfiguration pathData, String strInpProcess, String destInputDirPath,
				String errorFileDirPath, int fileLen,String sourcePath, Timestamp timestamp) throws SQLException{

			HashMap<String, Object> resultMap = new HashMap<>();
			boolean bRenameSuccess = inProcessFile.renameTo(new File(fileName + OfflineRnCConstants.WARN_FILE_EXTENSION));
			if (!bRenameSuccess) {
				LogManager.getLogger().debug(MODULE, "Could rename the inprocess file to .warn when connection is null - " + inProcessFile.getName());
			}
			resultMap.put(FILE_COUNTER_FLAG, false);
			//			crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).incrementTotalFailureFiles();
			//			getServiceContext().getServerContext().getAlertManager().getCrestelAlertManager().generateAlert(AlertsEnum.PARSING_SERVICE_ERROR, MODULE, "Error in Processing File in Parsing Service " + getServiceContext().getServiceIdentifier(),getServiceContext().getServiceIdentifier());
			return resultMap;
		}

		@SuppressWarnings("unchecked")
		private Map<String,Object> handleValidIntermediateFile(File inputFile, File inProcessFile, FileGatewayConfiguration pathData,String destInputDirPath, boolean isCompressedFile,
				String outputFileExt, String errorRecordDirPath,String malformRecordPath, int batchId, int fileLen, String errorFileDirPath, String strPath, String strInpProcess,
				String inProcessFileName, String fileName, boolean addTotalRecordCount, long totalRecordCountForTps,
				String sourcePath, String writefilePrefix, Timestamp timestamp) throws SQLException {
			int totalCloneCDR = 0;
			int totalRecordCountForLogs = 0;
			int pluginWiseErrorCounter = 0;
			int totalMalformPacketCount = 0;
			int totalPacketCount = 0;
			long destinationFileSize = 0;
			long fileDateInFile = 0;
			long errorFileSize = 0; 
			Map<String,Object> returnMap = new HashMap<>();
			String fileNameWithOutExt = null;
			int iExtIndex = inputFile.getName().lastIndexOf(".");
			if (iExtIndex > 0)
				fileNameWithOutExt = inputFile.getName().substring(0, iExtIndex);
			else
				fileNameWithOutExt = inputFile.getName();
			Set<String> splittedFileNameList = null;
			try {
				List<File> fileList = new ArrayList<>();
				fileList.add(inProcessFile);
				GeneralBatchPacket pluginProcessRequest = new GeneralBatchPacket(fileList);
				pluginProcessRequest.setParameter(FileGatewayConstants.INPUT_FILE, inputFile);
				pluginProcessRequest.setParameter(FileGatewayConstants.FILE_NAME_WITHOUT_EXT, fileNameWithOutExt);
				pluginProcessRequest.setParameter(FileGatewayConstants.PATH_DATA, pathData);
				pluginProcessRequest.setParameter(FileGatewayConstants.DEST_DIR_PATH, destInputDirPath);
				pluginProcessRequest.setParameter(FileGatewayConstants.IS_COMPRESSED_FILE, isCompressedFile);
				pluginProcessRequest.setParameter(FileGatewayConstants.OUTFILE_EXT, outputFileExt);
				pluginProcessRequest.setParameter(FileGatewayConstants.ERROR_RECORD_DIR_PATH, errorRecordDirPath);
				pluginProcessRequest.setParameter(FileGatewayConstants.MALFORM_RECORD_DIR_PATH, malformRecordPath);
		//		pluginProcessRequest.setParameter(FileGatewayConstants.COUNTER_LISTENER, crestelMediationParsingServiceCounterListner);
				pluginProcessRequest.setParameter(FileGatewayConstants.PLUGGABLE_AGGREGATION, null);
				CommonResponsePacket responsePacket = processIntermediateUsingServicePlugins(pluginProcessRequest);
				Map<String, HashMap<String, Object>> resultMap = (Map<String, HashMap<String, Object>>) responsePacket.getParameter(FileGatewayConstants.RESULT_MAP);
				if(responsePacket.getParameter(FileGatewayConstants.FILE_DATE) != null){
					Date fileDate = (Date) responsePacket.getParameter(FileGatewayConstants.FILE_DATE);
					fileDateInFile = fileDate.getTime();
				}
				// RENAMING PROCESSED FILES
				boolean isFileRenameSucceeded = false;
				if (resultMap != null && !resultMap.isEmpty()) {
					splittedFileNameList = resultMap.keySet();
					Iterator<Map.Entry<String, HashMap<String, Object>>> iterator2 = resultMap.entrySet().iterator();
					long fileSize = inProcessFile.length();
					while (iterator2.hasNext()) {
						Map.Entry<String, HashMap<String, Object>> entry = iterator2.next();
						String splittedFileName = entry.getKey();
						Map<String, Object> valueMap = entry.getValue();
						String destInputFilePath = (String) valueMap.get(FileGatewayConstants.FILE_PATH_NAME);
						int iTotalCDR = (Integer) valueMap.get(FileGatewayConstants.TOTAL_CDR_COUNT);
						int iTotalCloneCDR = (Integer) valueMap.get(FileGatewayConstants.TOTAL_CLONE_CDR_COUNT);
						int iTotalMalformCDR = (Integer) valueMap.get(FileGatewayConstants.TOTAL_MALFORM_CDR_COUNT);
						int iTotalErrorCDR = (Integer) valueMap.get(FileGatewayConstants.TOTAL_ERROR_CDR_COUNT);
						int iTotalPacketCount = 0;
						if(valueMap.get(FileGatewayConstants.TOTAL_PACKET_COUNT) != null){
							iTotalPacketCount = (Integer) valueMap.get(FileGatewayConstants.TOTAL_PACKET_COUNT);
						}else{
							iTotalPacketCount = iTotalCDR ;
						}
						if(valueMap.get(FileGatewayConstants.FILE_SIZE) != null){
							destinationFileSize = (Long) valueMap.get(FileGatewayConstants.FILE_SIZE);
						}
						if(valueMap.get(FileGatewayConstants.ERROR_FILE_SIZE) != null){
							errorFileSize = (Long) valueMap.get(FileGatewayConstants.ERROR_FILE_SIZE);
						}
						File successFile = new File(destInputFilePath);
						if(successFile.exists()){
							isFileRenameSucceeded = new File(destInputFilePath).renameTo(new File(destInputDirPath + File.separator + writefilePrefix + fileNameWithOutExt + OfflineConstants.CSV));
							if (!isFileRenameSucceeded) {
								LogManager.getLogger().warn(MODULE, "Could not change the state of file to normal after processing " + destInputFilePath);
								isFileRenameSucceeded = new File(destInputFilePath).renameTo(new File(destInputDirPath + File.separator + splittedFileName + outputFileExt + OfflineRnCConstants.PROCESSED_FILE_EXTENSION));
								if (!isFileRenameSucceeded) {
									LogManager.getLogger().warn(MODULE, "Could not rename to processed after parsing file " + destInputFilePath);
								}
							}
						}
						
						// Delete intermediate file
						fileProcessor.deleteInprocessFile(fileNameWithOutExt + ".json", new String[] {inputFile.getParent()}, inProcessExt);
						
						if (true) {
							if (pathData != null && pathData.isDestinationPathAvailable()) {
								fileProcessor.renameInProcessFileOnCopyFolders(splittedFileName + outputFileExt, pathData.getDestinationPathList(), outputDirFormat, strInpProcess);
							}
						} else if (bFileCopyFlag) {
							fileProcessor.renameInProcessFileOnCopyFolders(splittedFileName + outputFileExt, arrFileCopy, outputDirFormat, strInpProcess);
						}
						totalRecordCountForTps += iTotalCDR;
						totalRecordCountForLogs += iTotalCDR;
						pluginWiseErrorCounter += iTotalErrorCDR;
						totalCloneCDR += iTotalCloneCDR;
						totalMalformPacketCount += iTotalMalformCDR;
						totalPacketCount += iTotalPacketCount;
					}
//					crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).incrementTotalSuccessFiles();
//					crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalSuccessRecords(totalRecordCountForLogs);
//					crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalFailureRecords(pluginWiseErrorCounter);
//					crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalCloneRecords(totalCloneCDR);
//					crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalPackets(totalPacketCount);

					if(addTotalRecordCount)	{
//						crestelMediationParsingServiceCounterListner.addTotalCloneRecords(totalCloneCDR);
//						crestelMediationParsingServiceCounterListner.addTotalProcessedPackets(totalPacketCount);
//						crestelMediationParsingServiceCounterListner.addTotalRecords((long) totalRecordCountForLogs + pluginWiseErrorCounter - totalCloneCDR);
						returnMap.put(IS_ADD_TOTAL_RECORD_COUNT,false);
					}
				}
				returnMap.put(IS_DELETE_PARSED_FILE, true);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, OFCSErrorMessages.HANDLE_REQUEST_FAIL.generateTraceMessage(e,null,fileGatewayConfiguration.getName()+", so renaming to error"));
				LogManager.getLogger().trace(MODULE, e);

				actionWhileException(pathData, fileNameWithOutExt, strInpProcess, inProcessFile, inputFile, fileName, splittedFileNameList, strPath, batchId, fileLen, e, errorFileDirPath, fileDateInFile, timestamp);
				returnMap.put(FILE_COUNTER_FLAG, false);
//				crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).incrementTotalFailureFiles();
//				crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalSuccessRecords(totalRecordCountForLogs);
//				crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalFailureRecords(pluginWiseErrorCounter);
//				crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalCloneRecords(totalCloneCDR);
//				crestelMediationParsingServiceCounterListner.getCrestelMediationPluginWiseCounter().get(sourcePath).get(pluginId).addTotalPackets(totalPacketCount);

				getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.FILE_GATEWAY_PARSING_ERROR, MODULE, "Error in Processing File in Parsing Service " + fileGatewayConfiguration.getName());
			}
			
			returnMap.put(TOTAL_RECORDS_COUNT_FOR_TPS, totalRecordCountForTps);
			returnMap.put(FileGatewayConstants.TOTAL_MALFORM_CDR_COUNT, totalMalformPacketCount);
			return returnMap;
		}

		public final CommonResponsePacket processIntermediateUsingServicePlugins(GeneralBatchPacket requestPacket) {
			CommonResponsePacket responsePacket = null;

			ParsingPluginRequest pluginRequest = new ParsingPluginRequest(requestPacket);
			executeIntermediateUsingPlugin(jsonParsingPlugin, 0, pluginRequest);
			responsePacket = pluginRequest.getResponsePacket();

			return responsePacket;
		}

		/**
		 * @param plugin
		 * the runnable object for execution.
		 */
		private void executeIntermediateUsingPlugin(final ParsingPlugin plugin, long timeout, final ParsingPluginRequest pluginRequest) {
			try {
				plugin.processRequest(pluginRequest);
				pluginRequest.setProcessOver();
			} catch (ProcessFailException e) {
				LogManager.getLogger().trace(MODULE, e);
				pluginRequest.setException(e);
				pluginRequest.setResponseCode(BaseDriverResponseCode.ERROR);
				pluginRequest.setResponseMessage("General Error, " + e.getMessage());
				LogManager.getLogger().warn(MODULE, "Error during processRequest operation for driver <"	+ fileGatewayConfiguration.getName() + ">, reason : "	+ e.getMessage());
			} catch (Exception e) {
				pluginRequest.setException(e);
				pluginRequest.setResponseCode(BaseDriverResponseCode.ERROR);
				pluginRequest.setResponseMessage("General Error, " + e.getMessage());
				LogManager.getLogger().warn(MODULE, "Error during processRequest operation for driver <"	+ fileGatewayConfiguration.getName() + ">, reason : "	+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}

		/**
		 * Action while exception.
		 *
		 * @param pathData the path data
		 * @param fileNameWithOutExt the file name with out ext
		 * @param strInpProcess the str inp process
		 * @param inProcessFile the in process file
		 * @param inputFile the input file
		 * @param fileName the file name
		 * @param connection the connection
		 * @param pstmtForFileStat the pstmt for file stat
		 * @param splittedFileNameList the splitted file name list
		 * @param parsingPlugin the parsing plugin
		 * @param strPath the str path
		 * @param batchId the batch id
		 * @param fileLen the file len
		 * @param e the e
		 * @throws GeneralInsertUpdateSQLException the general insert update sql exception
		 * @throws SQLException the sQL exception
		 */
		private void actionWhileException(FileGatewayConfiguration pathData, String fileNameWithOutExt, String strInpProcess, File inProcessFile,
				File inputFile, String fileName,
				Set<String> splittedFileNameList, String strPath, int batchId, int fileLen, Exception e, 
				String errorFileDirPath, long fileDateInFile, Timestamp timestamp) {

			if (fileNameWithOutExt != null) {
				fileProcessor.deleteInprocessFile(fileNameWithOutExt, getDestinationPaths(), strInpProcess);
				if (pathData.isDestinationPathAvailable()) {
					fileProcessor.deleteInProcessFileOnCopyFolders(fileNameWithOutExt, pathData.getDestinationPathList(), outputDirFormat, strInpProcess);
				}
			}

			LogManager.getLogger().warn(MODULE, "File failed to process, So moving inprocess file to error directory, file name : " + inProcessFile.getName());
			File errorFileDirPathStr = new File(errorFileDirPath);
			if (!errorFileDirPathStr.exists()) {
				boolean bMakeDir = errorFileDirPathStr.mkdirs();
				if (!bMakeDir) {
					LogManager.getLogger().error(MODULE, "Could not create the directory - " + errorFileDirPathStr);
				}
			}
			boolean isFileRenameSucceeded = inProcessFile.renameTo(new File(errorFileDirPath + File.separator + inputFile.getName()));
			if (!isFileRenameSucceeded) {
				LogManager.getLogger().warn(MODULE, "Could not rename inProcessFile file to .error during IOException - s" + inProcessFile.getName());
			}
		}
	
	}
}
