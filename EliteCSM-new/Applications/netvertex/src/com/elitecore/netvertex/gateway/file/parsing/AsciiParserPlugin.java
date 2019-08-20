package com.elitecore.netvertex.gateway.file.parsing;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.SimpleDateFormatThreadLocal;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginInitializationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.corenetvertex.spr.ProcessFailException;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.core.constant.OfflineConstants;
import com.elitecore.netvertex.core.constant.OfflineRnCConstants;
import com.elitecore.netvertex.gateway.file.FileGatewayConfiguration;
import com.elitecore.netvertex.gateway.file.FileGatewayConstants;
import com.elitecore.netvertex.gateway.file.FileGatewayControllerCounter;
import com.elitecore.netvertex.gateway.file.FileGatewayEventListener;
import com.elitecore.netvertex.gateway.file.OFCSErrorMessages;
import com.elitecore.netvertex.gateway.file.util.CrestelServiceOutputStream;
import com.elitecore.netvertex.gateway.file.util.EliteOutputStreamFactory;
import com.elitecore.netvertex.gateway.file.util.OFCSFileHelper;
import com.elitecore.netvertex.gateway.file.util.Utilities;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCEvent;
import com.elitecore.netvertex.service.offlinernc.core.PacketOutputStream;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.util.RnCRequestParser;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

public class AsciiParserPlugin implements ParsingPlugin {

	private static final String MODULE = "ASCII-PARSER-PLUGIN";
	private static final String STANDARD = "STANDARD";
	public static final String CLONE = "CLONE CDR";
	private SimpleDateFormatThreadLocal simpleDateFormatThreadLocal;
	
	protected Set<String> attributeList = new LinkedHashSet<>();

	protected Set<String> trialAttributeList = new LinkedHashSet<>();

	private Date fileDate = null;

	private String inProcessExt;

	private boolean bPathMapAvailable;

	private boolean bSplittingEnabled;

	private int iRecordBatchSize;

	private Method totalSpaceMethod;

	private long requiredDiskSpaceinGB;

	private String outputDirFormat;

	private boolean bFileCopyFlag;

	private String[] arrFileCopy;

	private String equalCheckField;

	private String equalCheckValue;

	private Method equalCheckMethod;

	private Set<String> headerAttributes;

	private OFCSFileHelper fileProcessor;

	private boolean isDiskSpaceProblem = false;

	protected static final String DEFAULT_INSTANCE_ID = "000";

	/** The is file statistics. */
	private boolean isFileStatistics = true;

	/** The statistics defined path. */
	private String statisticsPath = null;

	/** The processing statistics dir path. */
	private String parStatDirPath = null;

	/** The processing statistics file name. */
	private static final String PAR_STAT_FILE_NAME = "ParsingServiceFileStatistics";

	/** The file grouping type. */
	private String fileGroupingType = null;

	private long totalRecords;
	private String charsetName;
	private ServerContext serverContext;
	//	private AsciiParserPluginConfiguration asciiParserPluginConfiguration;
	//	private String newpluginID;
	private FileGatewayConfiguration fileGatewayConfiguration;
	private FileGatewayEventListener fileGatewayEventListener;
	private TimeSource timesource;

	public AsciiParserPlugin(ServerContext serverContext,
			FileGatewayConfiguration fileGatewayConfiguration,
			FileGatewayEventListener fileGatewayEventListener) {
		this(serverContext, fileGatewayConfiguration, fileGatewayEventListener, TimeSource.systemTimeSource());
	}
	
	public AsciiParserPlugin(ServerContext serverContext,
			FileGatewayConfiguration fileGatewayConfiguration,
			FileGatewayEventListener fileGatewayEventListener,
			TimeSource timesource) {
		this.serverContext = serverContext;
		this.fileGatewayConfiguration = fileGatewayConfiguration;
		this.fileGatewayEventListener = fileGatewayEventListener;
		this.timesource = timesource;

	}

	public void init() throws InitializationFailedException {
		
		this.simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(((NetVertexServerContext)serverContext).getServerConfiguration().getSystemParameterConfiguration().getEdrDateTimeStampFormat());
		try {
			loadInitializeAttributes();
		} catch (PluginInitializationFailedException e) {
			throw new InitializationFailedException(e);
		}
	}

	protected void loadInitializeAttributes() throws PluginInitializationFailedException{
		superInitializeAttributes();
		initializeAttributes();
	}



	private void superInitializeAttributes() throws PluginInitializationFailedException {
		fileProcessor = new OFCSFileHelper();

		inProcessExt = OfflineRnCConstants.INPROCESS_EXTENSION;

		outputDirFormat = "";
		bPathMapAvailable = true;

		iRecordBatchSize = fileGatewayConfiguration.getRecordBatchSize();
		if (iRecordBatchSize <= 0)
			iRecordBatchSize = 50000;

		boolean bOverrideFileDate = fileGatewayConfiguration.isOverrideFileDate();
		String overrideFileDateType = null;

		if (bOverrideFileDate) {
			overrideFileDateType = fileGatewayConfiguration.getOverrideFileDateType();
			if (overrideFileDateType == null || overrideFileDateType.length() == 0)
				overrideFileDateType = "min";
		}

		//		String cdrDate = fileGatewayConfiguration.getCdrDateSummaryDate();
		//		if (cdrDate != null && cdrDate.length() > 0) {
		//			try {
		//				duplicateCheckDateFieldMethod = RnCRequest.class.getMethod("get" + cdrDate, (Class[]) null);
		//			} catch (NoSuchMethodException noSuchMethodException) {
		//				LogManager.getLogger().warn(MODULE, "Setter method not found for " + cdrDate);
		//				LogManager.getLogger().trace(MODULE, noSuchMethodException);
		//			} catch (Exception e) {
		//				LogManager.getLogger().warn(MODULE, "Error ocuured while preparing the unified csv packet, reason: " + e.getMessage());
		//				LogManager.getLogger().trace(MODULE, e);
		//			}
		//		}

		try {
			totalSpaceMethod = File.class.getMethod("getUsableSpace", (Class[]) null);
		} catch (NoSuchMethodException noSuchMethodException) {
			LogManager.getLogger().warn(MODULE, "Error occured while calling the getFreeSpace method, reason: " + noSuchMethodException);
		}

		requiredDiskSpaceinGB = fileGatewayConfiguration.getMinDiskSpaceRequired();

		//		equalCheckField = parsingServiceConfiguration.getStringAttribute(ParsingServiceConfiguration.EQUAL_CHECK_FIELD);
		//		equalCheckValue = parsingServiceConfiguration.getStringAttribute(ParsingServiceConfiguration.EQUAL_CHECK_VALUE);
		//		if (equalCheckField != null && !"".equalsIgnoreCase(equalCheckField)) {
		//			try {
		//				equalCheckMethod = RnCRequest.class.getMethod("get" + equalCheckField, new Class[] {});
		//			} catch (SecurityException | NoSuchMethodException e) {
		//				LogManager.getLogger().error("Error occured while", e.getMessage());
		//				LogManager.getLogger().trace(MODULE, e);
		//			}
		//		}
		headerAttributes = getAttributeList();
		if(headerAttributes == null)
			headerAttributes = new LinkedHashSet<>();
		headerAttributes.clear();
		initDefaultAttributeList();

		if(isFileStatistics){
			boolean isPathConfigValid = true;
			if(statisticsPath != null && !statisticsPath.isEmpty()){
				parStatDirPath = statisticsPath;
				if(!Utilities.createDirIfNotExist(parStatDirPath)){
					isPathConfigValid = false;
				}
			}
			else
			{
				isPathConfigValid = false;
			}

			if(!isPathConfigValid)
			{
				parStatDirPath = getServerContext().getServerHome() + File.separator + "SERVICE-FILESTATISTICS";
				if(!Utilities.createDirIfNotExist(parStatDirPath)){
					throw new PluginInitializationFailedException("File Statistics Path is missing for "+getPluginName()+ " at server configuration.");
				}
				else
				{
					LogManager.getLogger().error(MODULE, "File Statistics path is invalid or not provided for Services - so applied default File Statistic Path: " + parStatDirPath);
				}
			}
		}
	}

	private ServerContext getServerContext() {
		return serverContext;
	}

	private Set<String> getAttributeList() {
		return attributeList;
	}

	private void initializeAttributes() throws PluginInitializationFailedException {
		try {
			charsetName = fileGatewayConfiguration.getCharsetName();
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error occured while initializing attributes, reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new PluginInitializationFailedException(e.getMessage());
		}
	}

	public String getPluginName() {
		return fileGatewayConfiguration.getName();
	}

	public String getDescription() {
		return fileGatewayConfiguration.getDescription();
	}

	/*public PluginContext getPluginContext() {
		return new PluginContext() {

			@Override
			public ServerContext getServerContext() {
				return serverContext;
			}

			@Override
			public PluginConfiguration getPluginConfiguration(String pluginName) {
				return asciiParserPluginConfiguration;
			}
		};
	}*/

	@Override
	public boolean assignRequest(ParsingPluginRequest pluginRequest) {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processRequest(ParsingPluginRequest pluginRequest) throws ProcessFailException {
		GeneralBatchPacket pluginRequestPacket = pluginRequest.getPacket();
		List<File> fileList = (List<File>) pluginRequestPacket.getBatch();
		for(File inProcessFile : fileList){
			try {
				CommonResponsePacket responsePacket = new CommonResponsePacket();
				Map<String, HashMap<String, Object>> resultMap = readSourceFile(inProcessFile,
						(File) pluginRequestPacket.getParameter(FileGatewayConstants.INPUT_FILE),
						(FileGatewayConfiguration) pluginRequestPacket.getParameter(FileGatewayConstants.PATH_DATA),
						(String) pluginRequestPacket.getParameter(FileGatewayConstants.DEST_DIR_PATH),
						(String) pluginRequestPacket.getParameter(FileGatewayConstants.OUTFILE_EXT),
						(String) pluginRequestPacket.getParameter(FileGatewayConstants.ERROR_RECORD_DIR_PATH),
						(String) pluginRequestPacket.getParameter(FileGatewayConstants.MALFORM_RECORD_DIR_PATH),
						(FileGatewayControllerCounter) pluginRequestPacket.getParameter(FileGatewayConstants.COUNTER_LISTENER),
						(String) pluginRequestPacket.getParameter(FileGatewayConstants.FILE_NAME_WITHOUT_EXT));
				responsePacket.setParameter(FileGatewayConstants.RESULT_MAP, resultMap);
				responsePacket.setParameter(FileGatewayConstants.FILE_DATE, getFileDate());
				pluginRequest.setResponsePacket(responsePacket);
			} catch (Exception e) {
				LogManager.getLogger().trace(MODULE, e);
				throw new ProcessFailException(e);
			}
		}
	}

	/**
	 * Read source file.
	 *
	 * @param parsingPlugin the parsing plugin
	 * @param inProcessFile the in process file
	 * @param sourceDateFormat the source date format
	 * @param mediationDateFormat the mediation date format
	 * @param dateFormatWithoutTime the date format without time
	 * @param parsingUnitInfo the parsing unit info
	 * @param pstmtForRawInputCDR the pstmt for raw input cdr
	 * @param inputFileName the input file name
	 * @param pathData the path data
	 * @param destInputDirPath the dest input dir path
	 * @param string 
	 * @return the map
	 */
	private Map<String, HashMap<String, Object>> readSourceFile(File inProcessFile, File inputFile , FileGatewayConfiguration pathData,
			String destInputDirPath, String outputFileExt, String errorRecordDirPath,String malformRecordDirPath,
			FileGatewayControllerCounter crestelMediationParsingServiceCounterListner, String fileNameWithoutExt) throws Exception  {

		Map<String, HashMap<String, Object>> returnMap = new HashMap<>();
		String inProcessFileName = inProcessFile.getName();
		String inProcessFilePath = inProcessFile.getAbsolutePath();
		int fileLength = (int) inProcessFile.length();
		AsciiParsingUnitInfo parsingUnitInfo = getParsingUnitInfo(inputFile.getName(), inputFile.getAbsolutePath());
		Set<String> lstDiffDate = new HashSet<>();
		try {
			boolean isInvalidFile = false;
			int iSerialNumber = 1;

			//MED-3617 : check if file is in GZip or Zip format  
			isInvalidFile = Utilities.isGzipFile(inProcessFile,MODULE) || Utilities.isZipFile(inProcessFile,MODULE);
			if(!isInvalidFile){
				try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inProcessFile), getSourceCharsetName()), fileLength)) {

					returnMap = readFileWithPlugin(bufferedReader, parsingUnitInfo, iSerialNumber, inputFile.getName(),
							0, pathData, destInputDirPath, outputFileExt, errorRecordDirPath, malformRecordDirPath,
							inProcessFilePath, crestelMediationParsingServiceCounterListner, lstDiffDate,
							fileNameWithoutExt);
				}
			}else{
				LogManager.getLogger().trace(MODULE, "Input file is not in text Format");
				throw new Exception("Input file is not in text Format");	
			}
		} catch (UnsupportedCharsetException charsetException) {
			LogManager.getLogger().debug(MODULE, "Invalid character set " + charsetException.getCharsetName() + " specified.");
			LogManager.getLogger().debug(MODULE, "Could not created input stream or reader for file " + inProcessFileName + " ,reason: " + charsetException.getMessage());
			LogManager.getLogger().trace(MODULE, charsetException);
			throw new Exception(charsetException);
		} catch (FileNotFoundException fe) {
			LogManager.getLogger().debug(MODULE, "File Not found exception " + fe.getMessage());
			LogManager.getLogger().trace(MODULE, fe);
			throw new Exception(fe);
		} catch (UnsupportedEncodingException ue) {
			LogManager.getLogger().debug(MODULE, "Unsupported Encoding Exception " + ue.getMessage());
			LogManager.getLogger().trace(MODULE, ue);
			throw new Exception(ue);
		}catch (Exception e) {
			throw new Exception(e);
		} 
		return returnMap;
	}

	public AsciiParsingUnitInfo getParsingUnitInfo(String fileName, String filePath) {
		//		AsciiParsingUnitInfo asciiParsingUnitInfo = new AsciiParsingUnitInfo(fileName, filePath, newPluginId);
		return new AsciiParsingUnitInfo(fileName, filePath, "");
	}

	public Date getFileDate(){
		return fileDate;
	}

//	public void checkMinMaxDates(RnCRequest packet,SimpleDateFormat sdf, SimpleDateFormat mediationDateFormat,Method duplicateCheckDateFieldMethod , String fildeDateType, Set<String> lstDiffDate){
//		try{
//			if(packet != null){
//				String strDate = (String)duplicateCheckDateFieldMethod.invoke(packet,(Object[])null);
//				if(strDate != null && strDate.trim().length() > 0) {
//
//					Date date = mediationDateFormat.parse(strDate);
//
//					if(fildeDateType != null){
//						if("minimum".equalsIgnoreCase(fildeDateType) || "min".equalsIgnoreCase(fildeDateType)){
//							if(fileDate==null){
//								fileDate = date;
//							}else if(date.before(fileDate)){
//								fileDate = date;
//							}
//						}else if("maximum".equalsIgnoreCase(fildeDateType) || "max".equalsIgnoreCase(fildeDateType)){
//							if(fileDate==null){
//								fileDate = date;
//							}else if(date.after(fileDate)){
//								fileDate = date;
//							}
//						}
//					}
//					lstDiffDate.add(sdf.format(date));
//				}
//			}
//		}catch (Exception e) {
//			LogManager.getLogger().error(MODULE, "Error occured while checking minimum and maximum dates, reason : "  + e.getMessage());
//			LogManager.getLogger().trace(MODULE, e);
//		}
//	}

	protected void initDefaultAttributeList(){
		/*attributeList.add("SerialNumber");
		if(!trialAttributeList.isEmpty()){
			trialAttributeList.clear();
		}
		trialAttributeList.add("SourceUnitName");
		trialAttributeList.add("LogicalFileName");
		trialAttributeList.add("StatusMessage");
		trialAttributeList.add("AutoCorrectedCDR");*/
	}

	public String getSourceCharsetName() {
		return charsetName;
	}

	/**
	 * Read file with plugin.
	 *
	 * @param reaByteChannel the rea byte channel
	 * @param sourceStream the source stream
	 * @param bufferedReader the buffered reader
	 * @param parsingPlugin the parsing plugin
	 * @param parsingUnitInfo the parsing unit info
	 * @param sourceDateFormat the source date format
	 * @param mediationDateFormat the mediation date format
	 * @param dateFormatWithoutTime the date format without time
	 * @param iSerialNumber the i serial number
	 * @param pstmtForRawInputCDR the pstmt for raw input cdr
	 * @param inputFileName the input file name
	 * @param sourceFileCnt the source file cnt
	 * @param pathData the path data
	 * @param destInputDirPath the dest input dir path
	 * @param fileNameWithoutExt 
	 * @return the map
	 */
	@SuppressWarnings("unchecked")
	private Map<String, HashMap<String, Object>> readFileWithPlugin(BufferedReader bufferedReader, AsciiParsingUnitInfo parsingUnitInfo, 
			int iSerialNumber, String inputFileName, int sourceFileCnt,
			FileGatewayConfiguration pathData, String destInputDirPath, String outputFileExt, String errorRecordDirPath, String malformRecordDirPath, String inProcessFilePath,
			FileGatewayControllerCounter crestelMediationParsingServiceCounterListner,
			Set<String> lstDiffDate, String fileNameWithoutExt) throws Exception {

		Map<String, HashMap<String, Object>> returnMap = new HashMap<>();
		
		Set<AsciiPacket> invalidCDRMap = new HashSet<>();
		long startTime = System.currentTimeMillis();

		String destInputFilepath = null;
		String splittedFileName = inputFileName;
		String splittedFileNameExt = "";
		OFCSFileHelper fileHelper = new OFCSFileHelper();

		int fileCounter = 1;
		String newFileName = inputFileName;

		if (bPathMapAvailable ? pathData.isSplittingEnable() : bSplittingEnabled) {
			int iExtIndex = inputFileName.lastIndexOf(".");
			if (iExtIndex > 0) {
				splittedFileName = inputFileName.substring(0, inputFileName.lastIndexOf("."));
				splittedFileNameExt = inputFileName.substring(inputFileName.lastIndexOf("."), inputFileName.length());
			}
			newFileName = splittedFileName + "_" + sourceFileCnt + fileCounter + splittedFileNameExt;
			destInputFilepath = destInputDirPath + File.separator + newFileName + outputFileExt + inProcessExt;
		} else {
			destInputFilepath = destInputDirPath + File.separator + fileNameWithoutExt + outputFileExt + inProcessExt;
		}
		String json = null;
		if(parsingUnitInfo.getObjectValue(OfflineConstants.HEADER_SUMMARY) != null){
			json = (String)parsingUnitInfo.getObjectValue(OfflineConstants.HEADER_SUMMARY);
		}

		PacketOutputStreamImpl outputStream = new PacketOutputStreamImpl(fileHelper, pathData, destInputFilepath, newFileName, json, inProcessFilePath, outputFileExt, lstDiffDate);
		int totalMalformRecordsInFile = 0;
		try {
			AsciiPacket mediationPacket = null;

			if (bufferedReader != null) {
				mediationPacket = readMediationPacket(bufferedReader, parsingUnitInfo);
			}

			while (mediationPacket != null) {
				if (mediationPacket.isValidPacket()) {
					RnCRequest rncRequest = mediationPacket.toRnCRequest(simpleDateFormatThreadLocal.get(), simpleDateFormatThreadLocal.get());
					rncRequest.setSourceUnitName(newFileName);
					rncRequest.setLogicalFileName(inputFileName);
					rncRequest.setAttribute(OfflineRnCKeyConstants.FILE_LOCATION_NAME.getName(), fileGatewayConfiguration.getName());
					rncRequest.setAttribute(OfflineRnCKeyConstants.FILE_LOCATION.getName(), fileGatewayConfiguration.getSourcePath());
					rncRequest.setAttribute(OfflineRnCKeyConstants.ABSLOUTE_FILE_NAME.getName(), inProcessFilePath);
					rncRequest.setSerialNumber(String.valueOf(iSerialNumber++));
					
					rncRequest.setProcessDate(simpleDateFormatThreadLocal.get().format(new Date(timesource.currentTimeInMillis())));
					
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Processing EDR: \n" + rncRequest);
					}
					/*if(pluggableAggregation != null){
						pluggableAggregation.populateAggregatedStats(csvPacket, aggregatedDataMap);
					}*/

//					if(duplicateCheckDateFieldMethod != null){
//						checkMinMaxDates(rncRequest, dateFormatWithoutTime, mediationDateFormat, duplicateCheckDateFieldMethod, overrideFileDateType, lstDiffDate);
//
//					}

					RnCResponse rncResponse = RnCResponse.of(rncRequest);
					// TODO add mandatory attributes if any
					fileGatewayEventListener.received(rncRequest, rncResponse, outputStream);

				}else if(mediationPacket.isMalformPacket()){
					////Skipped, Malform Packets will be dump at the end of the whole file get parsed.
				}else{
					LogManager.getLogger().warn(MODULE, "Error occured while reading the record with serial no: " + iSerialNumber);
					invalidCDRMap.add(mediationPacket);
				}

				if (bufferedReader != null) {
					mediationPacket = readMediationPacket(bufferedReader, parsingUnitInfo);
				}
			}
			
			RnCRequest eofRequest = new RnCRequest();
			eofRequest.setEventType(OfflineRnCEvent.EOF);
			eofRequest.setAttribute(OfflineRnCKeyConstants.ABSLOUTE_FILE_NAME.getName(), inProcessFilePath);
			eofRequest.setLogicalFileName(inputFileName);
			RnCResponse rncResponse = RnCResponse.of(eofRequest);
			fileGatewayEventListener.received(eofRequest, rncResponse, outputStream);

			Object malformRecords = parsingUnitInfo.getObjectValue(FileGatewayConstants.TOTAL_MALFORM_RECORDS);
			if(malformRecords != null){
				totalMalformRecordsInFile = (Integer) malformRecords;
			}
			//Writing malformed packets into files for later re-processing
			Object malformPackets = parsingUnitInfo.getObjectValue(FileGatewayConstants.LIST_OF_MALFORM_PACKETS);
			List<ByteBuffer> malformPacketList = null;
			if(malformPackets != null){
				malformPacketList = (List<ByteBuffer>)malformPackets;
				if(!malformPacketList.isEmpty()){
					try {
						crestelMediationParsingServiceCounterListner.addTotalMalformRecords(totalMalformRecordsInFile);
						writeMalformPacketFile(malformPacketList, malformRecordDirPath, inputFileName, false);
					} catch (Exception e) {
						LogManager.getLogger().error(MODULE, "Error occured while writing file with plugin for inputstream, reason : " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
						throw e;
					}
				}
			}
			if (outputStream.validCDRList != null && !outputStream.validCDRList.isEmpty()) {
				writeDestinationFile(outputStream.validCDRList, destInputFilepath, newFileName, true,"", inProcessFilePath, headerAttributes, lstDiffDate, json);
				//isFileStatistics && !bSplittingEnabled)
				/*changed by KP*/	if(true){
					writeFileStatistic(inProcessFilePath, totalRecords, new File(destInputFilepath));
				}
				if (bPathMapAvailable) {
					if (pathData != null && pathData.isDestinationPathAvailable()) {
						boolean isDiskSpaceAvailable = true;
						for (int i = 0; i < pathData.getDestinationPathCount(); i++) {
							File destFile = new File(pathData.getDestinationPath(i));
							isDiskSpaceAvailable = fileHelper.isSpaceAvailableOnDisk(totalSpaceMethod, requiredDiskSpaceinGB, destFile);
							if (!isDiskSpaceAvailable) {
								break;
							}
						}
						if (isDiskSpaceAvailable) {
							fileHelper.copyFileToMultipleFolders(pathData.getDestinationPathList(), destInputFilepath, newFileName + outputFileExt, outputDirFormat, inProcessExt);
							if(isDiskSpaceProblem){
								getServerContext().generateSystemAlert(AlertSeverity.INFO, Alerts.FILE_GATEWAY_DISK_SPACE_RESOLVED, MODULE, "Disk space is Resolved for File Copy Folders");
								isDiskSpaceProblem = false;
							}
						} else {
							getServerContext().generateSystemAlert(AlertSeverity.CRITICAL.name(), Alerts.FILE_GATEWAY_NO_SPACE_LEFT_ON_DEVICE, MODULE, "Disk space is not available for File Copy Folders");
							isDiskSpaceProblem = true;
						}
					}
				} else {
					if (bFileCopyFlag) {
						boolean isDiskSpaceAvailable = true;
						for (int i = 0; i < arrFileCopy.length; i++) {
							File destFile = new File(arrFileCopy[i]);
							isDiskSpaceAvailable = fileHelper.isSpaceAvailableOnDisk(totalSpaceMethod, requiredDiskSpaceinGB, destFile);
							if (!isDiskSpaceAvailable) {
								break;
							}
						}
						if (isDiskSpaceAvailable) {
							fileHelper.copyFileToMultipleFolders(arrFileCopy, destInputFilepath, newFileName + outputFileExt, outputDirFormat, inProcessExt);
						} else {
							fileHelper.copyFileToMultipleFolders(arrFileCopy, destInputFilepath, newFileName + outputFileExt, outputDirFormat, inProcessExt);
						}
					}
				}
			}


			// Write error records
			if (Collectionz.isNullOrEmpty(outputStream.errorCDRList) == false) {

				writeProcessingErrorFileAtOnce(inProcessFilePath, outputStream.errorCDRList, errorRecordDirPath, newFileName, false, parsingUnitInfo);
			}

			HashMap<String, Object> tempMap = new HashMap<>();
			if (invalidCDRMap != null && !invalidCDRMap.isEmpty()) {
				try {
				//	crestelMediationParsingServiceCounterListner.addTotalFailureRecords(invalidCDRMap.size());
					long errorFileSize = writeErrorFile(inProcessFilePath,invalidCDRMap, errorRecordDirPath, newFileName, false);
					tempMap.put(FileGatewayConstants.ERROR_FILE_SIZE, errorFileSize);
				} catch (Exception e) {
					LogManager.getLogger().error(MODULE, "Error occured while writing file with plugin for inputstream, reason : " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
					throw e;
				}
			}
			if (bPathMapAvailable ? pathData.isSplittingEnable() : bSplittingEnabled) {
				tempMap.put(FileGatewayConstants.TOTAL_CDR_COUNT, outputStream.validCDRList.size());
				tempMap.put(FileGatewayConstants.TOTAL_CLONE_CDR_COUNT, outputStream.cloneCDRList.size());
			} else {
				tempMap.put(FileGatewayConstants.TOTAL_CDR_COUNT, outputStream.iTotalCDRCounter);
				tempMap.put(FileGatewayConstants.TOTAL_CLONE_CDR_COUNT, outputStream.cloneCounter);
			}
			tempMap.put(FileGatewayConstants.TOTAL_ERROR_CDR_COUNT, invalidCDRMap.size());
			tempMap.put(FileGatewayConstants.FILE_SIZE, new File(destInputFilepath).length());
			tempMap.put(FileGatewayConstants.FILE_PATH_NAME, destInputFilepath);
			tempMap.put(FileGatewayConstants.TOTAL_MALFORM_CDR_COUNT, totalMalformRecordsInFile);
			tempMap.put(FileGatewayConstants.TOTAL_PACKET_COUNT, parsingUnitInfo.getObjectValue(FileGatewayConstants.TOTAL_PACKET_COUNT));

			returnMap.put(newFileName, tempMap);

		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error occured while reading file with plugin for inputstream, reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} finally {
			LogManager.getLogger().info(MODULE, "Time taken to process whole files(milli seconds) :"+ (System.currentTimeMillis() - startTime));
		}
		return returnMap;
	}
	
	private class PacketOutputStreamImpl implements PacketOutputStream {

		int iCounter = 0;
		int iTotalCDRCounter = 0;
		int cloneCounter = 0;
		
		private List<RnCResponse> validCDRList = new ArrayList<>();
		private List<RnCRequest> cloneCDRList = new ArrayList<>();
		private List<RnCRequest> errorCDRList = new ArrayList<>();
		
		private OFCSFileHelper fileHelper;
		private FileGatewayConfiguration pathData;
		private String destInputFilepath;
		private String newFileName;
		private String json;
		private String inProcessFilePath;
		private String outputFileExt;
		private Set<String> lstDiffDate;

		public PacketOutputStreamImpl(OFCSFileHelper fileHelper, FileGatewayConfiguration pathData,
				String destInputFilepath, String newFileName, String json, String inProcessFilePath, String outputFileExt, Set<String> lstDiffDate) {
			this.fileHelper = fileHelper;
			this.pathData = pathData;
			this.destInputFilepath = destInputFilepath;
			this.newFileName = newFileName;
			this.json = json;
			this.inProcessFilePath = inProcessFilePath;
			this.outputFileExt = outputFileExt;
			this.lstDiffDate = lstDiffDate;
		}

		@Override
		public void writeSuccessful(RnCRequest request, RnCResponse response) throws Exception {
			if (OfflineRnCEvent.EOF == request.getEventType()) {
				return;
			}
			
			if (iCounter >= iRecordBatchSize) {
				if (bPathMapAvailable ? pathData.isSplittingEnable() : bSplittingEnabled)
					writeDestinationFile(validCDRList, destInputFilepath, newFileName, true,outputFileExt, inProcessFilePath, headerAttributes, lstDiffDate, json);
				else
					writeDestinationFile(validCDRList, destInputFilepath, newFileName, false,outputFileExt, inProcessFilePath, headerAttributes, lstDiffDate, json);
				if (bPathMapAvailable) {
					if (pathData != null && pathData.isDestinationPathAvailable()) {
						boolean isDiskSpaceAvailable = true;
						for (int i = 0; i < pathData.getDestinationPathCount(); i++) {
							File destFile = new File(pathData.getDestinationPath(i));
							isDiskSpaceAvailable = fileHelper.isSpaceAvailableOnDisk(totalSpaceMethod, requiredDiskSpaceinGB, destFile);
							if (!isDiskSpaceAvailable) {
								break;
							}
						}
						if (isDiskSpaceAvailable) {
							fileHelper.copyFileToMultipleFolders(pathData.getDestinationPathList(), destInputFilepath, newFileName + outputFileExt, outputDirFormat, inProcessExt);
							if(isDiskSpaceProblem){
								getServerContext().generateSystemAlert(AlertSeverity.INFO, Alerts.FILE_GATEWAY_DISK_SPACE_RESOLVED, MODULE, "Disk space is Resolved for File Copy Folders");
								isDiskSpaceProblem = false;
							}
						} else {
							getServerContext().generateSystemAlert(AlertSeverity.CRITICAL, Alerts.FILE_GATEWAY_NO_SPACE_LEFT_ON_DEVICE, MODULE, "Disk space is not available for File Copy Folders");
							isDiskSpaceProblem = true;
						}
					}
				} else {
					if (bFileCopyFlag) {
						boolean isDiskSpaceAvailable = true;
						for (int i = 0; i < arrFileCopy.length; i++) {
							File destFile = new File(arrFileCopy[i]);
							isDiskSpaceAvailable = fileHelper.isSpaceAvailableOnDisk(totalSpaceMethod, requiredDiskSpaceinGB, destFile);
							if (!isDiskSpaceAvailable) {
								break;
							}
						}
						if (isDiskSpaceAvailable) {
							fileHelper.copyFileToMultipleFolders(arrFileCopy, destInputFilepath, newFileName + outputFileExt, outputDirFormat, inProcessExt);
							if(isDiskSpaceProblem){
								getServerContext().generateSystemAlert(AlertSeverity.INFO, Alerts.FILE_GATEWAY_DISK_SPACE_RESOLVED, MODULE, "Disk space is Resolved for File Copy Folders");
								isDiskSpaceProblem = false;
							}
						} else {
							getServerContext().generateSystemAlert(AlertSeverity.CRITICAL, Alerts.FILE_GATEWAY_NO_SPACE_LEFT_ON_DEVICE, MODULE, "Disk space is not available for File Copy Folders");
							isDiskSpaceProblem = true;
						}
					}
				}

				iCounter = 0;
				cloneCounter = 0;
				validCDRList = new ArrayList<>();
				cloneCDRList.clear();
			}

			boolean bFlag = true;
			// MED-3069 Chintan Patel Changed equal check condition
			if(StringUtils.isNotEmpty(equalCheckField) && equalCheckMethod != null &&
					!StringUtils.contains(equalCheckValue, (String) equalCheckMethod.invoke(request, new Object[] {}))) {
				bFlag = false;
			}

			if(bFlag){

				validCDRList.add(response);
				if(request.getStatusMessage().equals(CLONE)){
					cloneCounter++;
				}
				iCounter++;
				iTotalCDRCounter++;
			}
		}

		@Override
		public void writeError(RnCRequest request, RnCResponse response) {
			if (OfflineRnCEvent.EOF == request.getEventType()) {
				return;
			}
			request.getOriginalPacket().setError(response.getErrorCode(), response.getErrorMessage());
			this.errorCDRList.add(request);
		}
	}

	public AsciiPacket readMediationPacket(BufferedReader bufferedReader, AsciiParsingUnitInfo parsingUnitInfo) throws IOException {
		AsciiParsingUnitInfo asciiParsingUnitInfo = parsingUnitInfo;
		boolean bheaderRead = false;
		Boolean temp = (Boolean)asciiParsingUnitInfo.getObjectValue(AsciiParsingUnitInfo.HEADER_READ);
		if(temp != null)
			bheaderRead = temp;
		if(fileGatewayConfiguration.isHeaderPresent() && !bheaderRead){
			String header = bufferedReader.readLine();
			if(STANDARD.equalsIgnoreCase(fileGatewayConfiguration.getHeaderType())
					&& fileGatewayConfiguration.isHeaderContainsField()){
				List<String> headerList = new LinkedList<>();
				Pattern pattern = fileGatewayConfiguration.getRegexPattern();
				Matcher mat = pattern.matcher(header);
				while(mat.find()) {
					headerList.add(mat.group(1));
				}
				asciiParsingUnitInfo.setObjectValue(AsciiParsingUnitInfo.HEADER, header);
				asciiParsingUnitInfo.setObjectValue(AsciiParsingUnitInfo.HEADER_LIST, headerList);
			}else{
				LogManager.getLogger().warn(MODULE,"File Header Parser is configured : " + fileGatewayConfiguration.getHeaderType() + ", which is not supported.");
			}
			asciiParsingUnitInfo.setObjectValue(AsciiParsingUnitInfo.HEADER_READ, Boolean.valueOf("true"));
		}

		AsciiPacket asciiPacket = new AsciiPacket(parsingUnitInfo, fileGatewayConfiguration);
		if(asciiPacket.readFrom(bufferedReader)!=-1){
			asciiParsingUnitInfo.incrementTotalRecordsCount();
			return asciiPacket;
		}else{
			return null;
		}
	}

	/**
	 * Write destination file.
	 *
	 * @param cdrList the cdr list
	 * @param destFilePath the dest file path
	 * @param pstmt the pstmt
	 * @param strLogicalFileName the str logical file name
	 * @param parsingPlugin the parsing plugin
	 * @param bWriteDates the b write dates
	 */
	protected void writeDestinationFile(Collection<RnCResponse> cdrList, String destFilePath,
			String strLogicalFileName, boolean bWriteDates,String outputFileExt, String inProcessFilePath,
			Set<String> headerAttributes, Set<String> lstDiffDate, String json) throws Exception {

		RnCRequestParser parser = new RnCRequestParser(TimeSource.systemTimeSource(), simpleDateFormatThreadLocal);
		ByteBuffer buffer = null;
		CrestelServiceOutputStream iCrestelServicesOutputStream = null;
		/*List<String> headerAttributeList = new ArrayList<>(headerAttributes);
		headerAttributeList.addAll(trialAttributeList);
		StringBuilder headerBuilder = new StringBuilder();
		List<String> headersOfAttributesToBereadFromPacket = new ArrayList<>();
		for(String headerToken : headerAttributeList){
			headersOfAttributesToBereadFromPacket.add(headerToken);
			headerBuilder.append(headerToken);
			headerBuilder.append(",");
		}
		if(headerBuilder.length() > 0){
			headerBuilder.deleteCharAt(headerBuilder.length() -1 );
		}
		headerBuilder.append("\n");*/
		if (cdrList != null && !cdrList.isEmpty()) {
			try {
				StringBuilder stringBuilder = new StringBuilder();
				Iterator<RnCResponse> cdrIterator = cdrList.iterator();
				while (cdrIterator.hasNext()) {
					RnCResponse tempCSVPacket = cdrIterator.next();
					//					stringBuilder.append(tempCSVPacket.toString(headerAttributeList));
					//	stringBuilder.append(tempCSVPacket.toString(headersOfAttributesToBereadFromPacket));

					stringBuilder.append(parser.serialize(tempCSVPacket));
					stringBuilder.append("\n");
				}
				if (bWriteDates) {
					if(json != null && json.trim().length() > 2){
						stringBuilder.append(OfflineConstants.HEADER_SUMMARY);
						stringBuilder.append("\n");
						stringBuilder.append(json);
						stringBuilder.append("\n");
					}
				//	stringBuilder.append(OfflineConstants.CDR_DATE_SUMMARY);
					stringBuilder.append("-");
					if (lstDiffDate != null) {
						Iterator<String> iter = lstDiffDate.iterator();
						while (iter.hasNext()) {
							String date = iter.next();
							stringBuilder.append(date);
							stringBuilder.append(",");
						}
					}
					stringBuilder.deleteCharAt(stringBuilder.length() - 1);
					stringBuilder.append("\n");
					lstDiffDate = new HashSet<>();
				}
				File destFile = new File(destFilePath);
				String streamObjType = OfflineConstants.JSON;
				if(outputFileExt.equals(OfflineConstants.GZ))
					streamObjType = OfflineConstants.GZ;

				boolean bDestFileExist = destFile.exists();
				if (!bDestFileExist) {
					boolean bFlag = destFile.createNewFile();
					if (bFlag) {
						iCrestelServicesOutputStream = EliteOutputStreamFactory.createStream(streamObjType, destFile);
						//						iCrestelServicesOutputStream.write(ByteBuffer.wrap(headerBuilder.toString().getBytes(Charset.defaultCharset())));
					} else {
						LogManager.getLogger().warn(MODULE, "File creation failed for file - " + destFile.getAbsolutePath());
					}
				} else {
					iCrestelServicesOutputStream = EliteOutputStreamFactory.createStream(streamObjType, destFile);
				}
				int totalWriteBytes = 0;
				byte[] writeBytes = stringBuilder.toString().getBytes(Charset.defaultCharset());
				if(iCrestelServicesOutputStream != null){
					buffer = ByteBuffer.wrap(writeBytes);
					totalWriteBytes = iCrestelServicesOutputStream.write(buffer);
				}else {
					LogManager.getLogger().warn(MODULE, "File write failed for file - " + destFile.getAbsolutePath());
				}
				if(isFileStatistics && bSplittingEnabled){
					writeFileStatistic(inProcessFilePath,cdrList.size(),destFile);
				}else{
					totalRecords =  totalRecords + cdrList.size();
				}
				if (writeBytes.length != totalWriteBytes)
					throw new IOException("CDRs are not written properly in file by channel.");
				stringBuilder = null;
			} catch (IOException e) {
				LogManager.getLogger().error(MODULE, "IO Error occured while writing or inserting cdrs into file or db, reason : " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
				throw new Exception(e);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Error occured while writing or inserting cdrs into file or db, reason : " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
				throw new Exception(e);
			} finally {
				if(iCrestelServicesOutputStream != null)
					try {
						iCrestelServicesOutputStream.close();
					} catch (IOException e) {
						LogManager.getLogger().trace(MODULE, e);
					}
				buffer = null;
			}
		}
		iCrestelServicesOutputStream = null;
		buffer = null;
	}

	private long writeErrorFile(String inProcessFilePath, Set<AsciiPacket> invalidList, String destFilePath, String strLogicalFileName, boolean bCompressedFile) throws Exception {
		FileOutputStream fileOutputStream = null;
		WritableByteChannel writableByteChannel = null;
		File destFile = new File(destFilePath + File.separator + strLogicalFileName);
		try{
			File errorRecordDirPathStr = new File(destFilePath);
			if (!errorRecordDirPathStr.exists()) {
				boolean bMakeDir = errorRecordDirPathStr.mkdirs();
				if (!bMakeDir) {
					LogManager.getLogger().error(MODULE, OFCSErrorMessages.DIR_CREATION_FAILED.getMessage(null, destFilePath));
				}
			}

			boolean bDestFileExist = destFile.exists();
			if (!bDestFileExist) {
				boolean bFlag = destFile.createNewFile();
				if (!bFlag) {
					LogManager.getLogger().warn(MODULE, "File creation failed for file - " + destFile.getAbsolutePath());
				}
			}
			LogManager.getLogger().warn(MODULE, "File moved to Error Directory - " + destFile.getAbsolutePath());
			fileOutputStream = new FileOutputStream(destFile, true);
			if(bCompressedFile){
				GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
				writableByteChannel = Channels.newChannel(gzipOutputStream);
			}else{
				writableByteChannel = Channels.newChannel(fileOutputStream);
			}

			Iterator<AsciiPacket> iter = invalidList.iterator();
			while (iter.hasNext()) {
				AsciiPacket tempMediationPacket = iter.next();
				byte[] writeBytes = tempMediationPacket.getRawData();
				if(writeBytes != null && writeBytes.length > 0){
					ByteBuffer buffer = ByteBuffer.wrap(writeBytes);
					writableByteChannel.write(buffer);
				}
			}

			if(isFileStatistics){
				writeFileStatistic(inProcessFilePath,invalidList.size(),destFile);
			}
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Error occured while writing error cdrs into file, reason : " + e.getMessage() + ", for file - "
					+ strLogicalFileName);
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		}finally {
			if(writableByteChannel != null){
				try {
					writableByteChannel.close();
				} catch (IOException e) {
					LogManager.getLogger().trace(MODULE, e);
				}
			}
			if(fileOutputStream != null){
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}
		return destFile.length();
	}


	/**
	 * This method is not meant to be called in batched form, because this method writes header in file. 
	 * If called in batched form then the file will contain multiple headers.
	 */
	private long writeProcessingErrorFileAtOnce(String inProcessFilePath, List<RnCRequest> invalidList, String destFilePath, String strLogicalFileName, boolean bCompressedFile, AsciiParsingUnitInfo asciiParsingUnitInfo) throws Exception {
		FileOutputStream fileOutputStream = null;
		WritableByteChannel writableByteChannel = null;
		File destFile = new File(destFilePath + File.separator + strLogicalFileName);
		try{
			File errorRecordDirPathStr = new File(destFilePath);
			if (!errorRecordDirPathStr.exists()) {
				boolean bMakeDir = errorRecordDirPathStr.mkdirs();
				if (!bMakeDir) {
					LogManager.getLogger().error(MODULE, OFCSErrorMessages.DIR_CREATION_FAILED.getMessage(null, destFilePath));
				}
			}

			boolean bDestFileExist = destFile.exists();
			if (!bDestFileExist) {
				boolean bFlag = destFile.createNewFile();
				if (!bFlag) {
					LogManager.getLogger().warn(MODULE, "File creation failed for file - " + destFile.getAbsolutePath());
				}
			}
			LogManager.getLogger().warn(MODULE, "File moved to Error Directory - " + destFile.getAbsolutePath());
			fileOutputStream = new FileOutputStream(destFile, true);
			if(bCompressedFile){
				GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
				writableByteChannel = Channels.newChannel(gzipOutputStream);
			}else{
				writableByteChannel = Channels.newChannel(fileOutputStream);
			}

			// TODO Write error header
			String header = (String)asciiParsingUnitInfo.getObjectValue(AsciiParsingUnitInfo.HEADER);
			header = header + ";" + OfflineRnCConstants.ERROR_CODE_HEADER + ";" + OfflineRnCConstants.ERROR_MESSAGE_HEADER + "\n";
			byte[] headerBytes = header.getBytes();
			ByteBuffer headerBuffer = ByteBuffer.wrap(headerBytes);
			writableByteChannel.write(headerBuffer);

			Iterator<RnCRequest> iter = invalidList.iterator();
			while (iter.hasNext()) {
				RnCRequest rncRequest = iter.next();
				AsciiPacket tempMediationPacket = rncRequest.getOriginalPacket();
				byte[] writeBytes = tempMediationPacket.getRawData();
				if(writeBytes != null && writeBytes.length > 0){
					ByteBuffer buffer = ByteBuffer.wrap(writeBytes);
					writableByteChannel.write(buffer);
				}
			}

			if(isFileStatistics){
				writeFileStatistic(inProcessFilePath,invalidList.size(),destFile);
			}
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Error occured while writing error cdrs into file, reason : " + e.getMessage() + ", for file - "
					+ strLogicalFileName);
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		}finally {
			if(writableByteChannel != null){
				try {
					writableByteChannel.close();
				} catch (IOException e) {
					LogManager.getLogger().trace(MODULE, e);
				}
			}
			if(fileOutputStream != null){
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}
		return destFile.length();
	}


	/**
	 * Write malform file to malform directory
	 *
	 * @param malformedList the malform packet list
	 * @param destFilePath the destination file path
	 * @param strLogicalFileName the str logical file name
	 * @param parsingPlugin the parsing plugin
	 */
	private void writeMalformPacketFile(List<ByteBuffer> malformedList, String destFilePath, String strLogicalFileName, boolean bCompressedFile) throws Exception {
		FileOutputStream fileOutputStream = null;
		WritableByteChannel writableByteChannel = null;
		try{
			File errorRecordDirPathStr = new File(destFilePath);
			if (!errorRecordDirPathStr.exists()) {
				boolean bMakeDir = errorRecordDirPathStr.mkdirs();
				if (!bMakeDir) {
					LogManager.getLogger().error(MODULE, OFCSErrorMessages.DIR_CREATION_FAILED.getMessage("Error Files", destFilePath));
				}
			}

			File destFile = new File(destFilePath + File.separator + strLogicalFileName);
			boolean bDestFileExist = destFile.exists();
			if (!bDestFileExist) {
				boolean bFlag = destFile.createNewFile();
				if (!bFlag) {
					LogManager.getLogger().warn(MODULE, "File creation failed for file - " + destFile.getAbsolutePath());
				}
			}
			fileOutputStream = new FileOutputStream(destFile, true);
			if(bCompressedFile){
				GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
				writableByteChannel = Channels.newChannel(gzipOutputStream);
			}else{
				writableByteChannel = Channels.newChannel(fileOutputStream);
			}

			Iterator<ByteBuffer> iter = malformedList.iterator();
			while (iter.hasNext()) {
				ByteBuffer tempMediationPacket = iter.next();
				writableByteChannel.write(tempMediationPacket);
			}
			LogManager.getLogger().debug(MODULE, "File created to Malform Directory - " + destFile.getAbsolutePath());
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Error occured while writing malform cdrs into file, reason : " + e.getMessage() + ", for file - "
					+ strLogicalFileName);
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		}finally {
			if(writableByteChannel != null){
				try {
					writableByteChannel.close();
				} catch (IOException e) {
					LogManager.getLogger().trace(MODULE, e);
				}
			}
			if(fileOutputStream != null){
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}
	}

	private void writeFileStatistic(String inputFilePath, long totalRecordCount, File destFile){
		StringBuilder stringBuilder = new StringBuilder();
		if (inputFilePath != null) {
			File inputFile = new File(inputFilePath);
			stringBuilder.append("Time=" + simpleDateFormatThreadLocal.get().format(new Date()));
			stringBuilder.append(",");
			stringBuilder.append("InputFileName=" + inputFile.getName().replace(inProcessExt, ""));
			stringBuilder.append(",");
			stringBuilder.append("InputFilePath=" + inputFile.getParent());
			stringBuilder.append(",");
			stringBuilder.append("TotalRecords=" + totalRecordCount);
			stringBuilder.append(",");
			stringBuilder.append("PluginID=" + getPluginName());
			stringBuilder.append(",");
			stringBuilder.append("DestinationFileName=" + destFile.getName().replace(inProcessExt, ""));
			stringBuilder.append(",");
			stringBuilder.append("DestinationFilePath=" + destFile.getParentFile());
			stringBuilder.append(",");
			fileProcessor.writeStatisticsForFile(PAR_STAT_FILE_NAME, stringBuilder.toString(), fileGroupingType, parStatDirPath);
		} else {
			LogManager.getLogger().error(MODULE, "Could not add file status, reason - cannot find input file path.");
		}
	}

	/*private void checkPreviousSequenceFileStatus(Object lockObject, LinkedHashMap<String, Integer> indexControllerMap,
			LinkedHashMap<String, SequentialProcessValueHolder> sequentialControllerMap, String strSourceFileName){
		synchronized (lockObject) {
			if(indexControllerMap.get(strSourceFileName) != 0){
				int previousFileIndex = indexControllerMap.get(strSourceFileName) - 1;
				String previousFileName = null;
				for(Map.Entry<String,Integer> entry: indexControllerMap.entrySet()){
					if(previousFileIndex == entry.getValue()){
						previousFileName = entry.getKey();
						break; //breaking because its one to one map
					}
				}
				try{
					while(!sequentialControllerMap.get(previousFileName).getWriteValue()){
						lockObject.wait();
					}
				}catch(Exception e){
					LogManager.getLogger().error(MODULE,"Error occurred while processing, reason : " + e.getMessage());
					LogManager.getLogger().trace(MODULE,e);
				}
			}
		}
	}*/
	
	public static void main(String args[]) {
		
		String inputFileName="input.csv";
		LogManager.getLogger().info(MODULE,inputFileName.split("\\.")[0]);
	}
}
