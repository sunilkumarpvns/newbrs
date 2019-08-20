package com.elitecore.netvertex.gateway.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.elitecore.core.commons.util.constants.DataTypeConstant;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.driverx.cdr.data.DBFieldMapping;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.sm.filelocation.ColumnMappingData;
import com.elitecore.corenetvertex.sm.filelocation.FileLocationData;
import com.elitecore.corenetvertex.sm.filemapping.FileMappingData;
import com.elitecore.corenetvertex.sm.filemapping.FileMappingDetail;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;
import com.elitecore.netvertex.gateway.file.parsing.FileMapping;

public class FileGatewayConfiguration implements ToStringable {

	private static final int FILE_ALERT_INTERVAL = 100;

	private static final String DEVICE = "device";

	private static final String INP = ".inp";

	private static final int NUMBER_OF_ATTEMPT = 0;

	/** The Path Id */
	private String pathId;
	
	/** The source path. */
	private String sourcePath;

	/** The destination input path. */
	private String destinationInputPath;

	/** The write file name prefix. */
	private String writeFileNamePrefix;

	/** The destination path list. */
	private List<String> destinationPathList;

	/** The suffix list. */
	private List<String> suffixList;

	/** The contains list. */
	private List<String> containsList;

	/** The exclude file type list. */
	private List<String> excludeFileTypeList;

	/** The file date position. */
	private String fileDatePosition;

	/** The file date start index. */
	private int fileDateStartIndex;

	/** The file date end index. */
	private int fileDateEndIndex;

	/** The file date format. */
	private String fileDateFormat;

	/** The filter pattern action. */
	private String filterPatternAction;

	/** The filter pattern name. */
	private String filterPatternName;

	/** The filter pattern value. */
	private String filterPatternValue;

	/** The is date available in file. */
	private boolean isDateAvailableInFile;

	/** The is splitting enable. */
	private boolean isSplittingEnable;

	/** The is destination path available. */
	private boolean isDestinationPathAvailable;

	/** The is dest directory created. */
	private boolean isDestDirectoryCreated;

	/** The duplicate dir. */
	private File duplicateDir;

	/** The is input file compressed. */
	private boolean isInputFileCompressed;

	/** The is output file compressed. */
	private boolean isOutputFileCompressed;

	/** The plugin list. */
	private List<Map<String,Object>> pluginList;

	//File Alert

	/** The is file sequence alert. */
	private boolean isFileSequenceAlert;

	/** The file sequence alert start index. */
	private int fileSequenceAlertStartIndex;

	/** The file sequence alert end index. */
	private int fileSequenceAlertEndIndex;

	/** The file sequence alert counter. */
	private int fileSequenceAlertCounter;

	/** The Read Path Max File Count */
	private int readPathMaxFilesCount;

	/** The policy name. */
	private String policyName;

	private String name;

	private String sortingType;

	private String sortingCriteria;

	private String errorPath;
	private String description;
	private String ratingType;
	private String archivePath;

	private FileMappingData inputFileMappingData;

	private List<FileMapping> inputFileMappings;

	private ArrayList<FileMappingDetail> outputFileMappingDetails;
	private String tableName;
	private DBDataSource dataSource;

	private List<DBFieldMapping> dbFieldMappings;

	private int queryTimeout;

	/**
	 * Instantiates a new path data impl.
	 */
	public FileGatewayConfiguration() {
		
		destinationPathList = new ArrayList<>();
		suffixList = new ArrayList<>();
		containsList = new ArrayList<>();
		excludeFileTypeList = new ArrayList<>();
	}
	
	public FileGatewayConfiguration(FileLocationData fileLocationData){
		destinationPathList = new ArrayList<>();
		suffixList = new ArrayList<>();
		containsList = new ArrayList<>();
		excludeFileTypeList = new ArrayList<>();
		this.archivePath=fileLocationData.getArchivePath();
		this.name=fileLocationData.getName();
		this.sourcePath=fileLocationData.getInputPath();
		this.destinationInputPath=fileLocationData.getOutputPath();
		this.errorPath=fileLocationData.getErrorPath();
		this.ratingType=fileLocationData.getRatingType();
		this.description=fileLocationData.getDescription();
		this.sortingCriteria=fileLocationData.getSortingCriteria();
		this.sortingType=fileLocationData.getSortingType();
		this.inputFileMappingData = fileLocationData.getFileMappingData();
		this.description = fileLocationData.getDescription();
		
		this.inputFileMappings = readFileMappingDetails(fileLocationData.getFileMappingData().getFileMappingDetail());
		this.outputFileMappingDetails = new ArrayList<>();
		this.tableName = fileLocationData.getTableName();
		this.dataSource = createDataSource(fileLocationData.getDatabaseData());
		this.dbFieldMappings = createDbFieldMapping(fileLocationData.getColumnMappingData());
		for (OfflineRnCKeyConstants offlineRncKey : OfflineRnCKeyConstants.values()) {
			FileMappingDetail outputFileMappingDetail = new FileMappingDetail();
			outputFileMappingDetail.setSourceKey(offlineRncKey.getName());
			outputFileMappingDetail.setDestinationKey(offlineRncKey.getName());
			outputFileMappingDetails.add(outputFileMappingDetail);
		}
		this.queryTimeout = fileLocationData.getDatabaseData().getQueryTimeout();
	}

	
	private List<DBFieldMapping> createDbFieldMapping(List<ColumnMappingData> columnMappingDatas) {
		List<DBFieldMapping> dbFieldMappings = new ArrayList<>();
		DBFieldMapping dbFieldMapping;
		for (ColumnMappingData columnMappingData : columnMappingDatas) {
			dbFieldMapping = new DBFieldMapping(columnMappingData.getColumnName(),
					columnMappingData.getSourceKey(),
					getDataType(columnMappingData.getSourceKey()),
					columnMappingData.getDefaultValue()
				);
			dbFieldMappings.add(dbFieldMapping);
		}
		return dbFieldMappings;
	}

	private int getDataType(String sourceKey) {
		OfflineRnCKeyConstants offlineSourceKey = OfflineRnCKeyConstants.fromKeyName(sourceKey);
		if (offlineSourceKey != null) {
			return offlineSourceKey.isTimestamp() ? DataTypeConstant.TIME_STAMP_DATA_TYPE : DataTypeConstant.STRING_DATA_TYPE;
		} else {
			return DataTypeConstant.STRING_DATA_TYPE;
		}
	}

	private DBDataSource createDataSource(DatabaseData data) {
		if (data == null) {
			return null;
		}

		DBDataSourceImpl ds = new DBDataSourceImpl();
		ds.setConnectionURL(data.getConnectionUrl());
		ds.setUsername(data.getUserName());
		ds.setPassword(data.getPassword());
		ds.setDataSourceName(data.getName());
		ds.setMinimumPoolSize(data.getMinimumPool());
		ds.setMaximumPoolSize(data.getMaximumPool());
		ds.setStatusCheckDuration(data.getStatusCheckDuration());
		return ds;
	}

	private List<FileMapping> readFileMappingDetails(List<FileMappingDetail> fileMappingDetails) {
		List<FileMapping> fileMappings = new ArrayList<>();
		
		for (FileMappingDetail fileMappingDetail : fileMappingDetails) {
			FileMapping fileMapping = new FileMapping();
			fileMapping.setSourceKey(fileMappingDetail.getSourceKey());
			fileMapping.setDestinationKey(fileMappingDetail.getDestinationKey());
			fileMapping.setDefaultValue(fileMappingDetail.getDefaultValue());
			fileMapping.setValueMapping(fileMappingDetail.getValueMapping());
			fileMapping.setKeyValueMapping(fileMapping.getValueMapping());
			fileMappings.add(fileMapping);
		}
		return fileMappings;
	}
	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getSourcePath()
	 */
	
	public String getSourcePath() {
		return sourcePath;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setSourcePath(java.lang.String)
	 */
	
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getDestinationPath(int)
	 */
	
	public String getDestinationPath(int index) {
		return destinationPathList.get(index);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#addDestinationPath(java.lang.String)
	 */
	
	public void addDestinationPath(String destPath) {
		destinationPathList.add(destPath);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#addAllDestinationPath(java.util.List)
	 */
	
	public void addAllDestinationPath(List<String> destPathList) {
		destinationPathList.addAll(destPathList);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getDestinationPathList()
	 */
	
	public List<String> getDestinationPathList() {
		return destinationPathList;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getDestinationPathCount()
	 */
	
	public int getDestinationPathCount() {
		return destinationPathList.size();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#isDestinationPathAvailable()
	 */
	
	public boolean isDestinationPathAvailable() {
		return isDestinationPathAvailable;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setDestinationPathAvailable(boolean)
	 */
	
	public void setDestinationPathAvailable(boolean isDestinationPathAvailable) {
		this.isDestinationPathAvailable = isDestinationPathAvailable;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#isDestDirectoryCreated()
	 */
	
	public boolean isDestDirectoryCreated() {
		return isDestDirectoryCreated;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setDestDirectoryCreated(boolean)
	 */
	
	public void setDestDirectoryCreated(boolean isDestDirectoryCreated) {
		this.isDestDirectoryCreated = isDestDirectoryCreated;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getSuffixList()
	 */
	
	public List<String> getSuffixList() {
		return suffixList;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setSuffixList(java.util.List)
	 */
	
	public void setSuffixList(List<String> suffixList) {
		this.suffixList = suffixList;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#addSuffix(java.lang.String)
	 */
	
	public void addSuffix(String suffix) {
		suffixList.add(suffix);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getContainsList()
	 */
	
	public List<String> getContainsList() {
		return containsList;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setContainsList(java.util.List)
	 */
	
	public void setContainsList(List<String> containsList) {
		this.containsList = containsList;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#addContains(java.lang.String)
	 */
	
	public void addContains(String contains) {
		containsList.add(contains);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getExcludeFileTypeList()
	 */
	
	public List<String> getExcludeFileTypeList() {
		return excludeFileTypeList;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setExcludeFileTypeList(java.util.List)
	 */
	
	public void setExcludeFileTypeList(List<String> excludeFileTypeList) {
		this.excludeFileTypeList = excludeFileTypeList;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#addExcludeFileType(java.lang.String)
	 */
	
	public void addExcludeFileType(String excludeFileType) {
		excludeFileTypeList.add(excludeFileType);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getDestinationInputPath()
	 */
	
	public String getDestinationInputPath() {
		return destinationInputPath;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setDestinationInputPath(java.lang.String)
	 */
	
	public void setDestinationInputPath(String destinationInputPath) {
		this.destinationInputPath = destinationInputPath;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#isSplittingEnable()
	 */
	
	public boolean isSplittingEnable() {
		return isSplittingEnable;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setSplittingEnable(boolean)
	 */
	
	public void setSplittingEnable(boolean isSplittingEnable) {
		this.isSplittingEnable = isSplittingEnable;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getWriteFileNamePrefix()
	 */
	
	public String getWriteFileNamePrefix() {
		return writeFileNamePrefix;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setWriteFileNamePrefix(java.lang.String)
	 */
	
	public void setWriteFileNamePrefix(String writeFileNamePrefix) {
		this.writeFileNamePrefix = writeFileNamePrefix;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getFileDatePosition()
	 */
	
	public String getFileDatePosition() {
		return fileDatePosition;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setFileDatePosition(java.lang.String)
	 */
	
	public void setFileDatePosition(String fileDatePosition) {
		this.fileDatePosition = fileDatePosition;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getFileDateStartIndex()
	 */
	
	public int getFileDateStartIndex() {
		return fileDateStartIndex;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setFileDateStartIndex(int)
	 */
	
	public void setFileDateStartIndex(int fileDateStartIndex) {
		this.fileDateStartIndex = fileDateStartIndex;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getFileDateEndIndex()
	 */
	
	public int getFileDateEndIndex() {
		return fileDateEndIndex;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setFileDateEndIndex(int)
	 */
	
	public void setFileDateEndIndex(int fileDateEndIndex) {
		this.fileDateEndIndex = fileDateEndIndex;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getFileDateFormat()
	 */
	
	public String getFileDateFormat() {
		return fileDateFormat;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setFileDateFormat(java.lang.String)
	 */
	
	public void setFileDateFormat(String fileDateFormat) {
		this.fileDateFormat = fileDateFormat;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getFilterPatternAction()
	 */
	
	public String getFilterPatternAction() {
		return filterPatternAction;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setFilterPatternAction(java.lang.String)
	 */
	
	public void setFilterPatternAction(String filterPatternAction) {
		this.filterPatternAction = filterPatternAction;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getFilterPatternName()
	 */
	
	public String getFilterPatternName() {
		return filterPatternName;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setFilterPatternName(java.lang.String)
	 */
	
	public void setFilterPatternName(String filterPatternName) {
		this.filterPatternName = filterPatternName;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getFilterPatternValue()
	 */
	
	public String getFilterPatternValue() {
		return filterPatternValue;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setFilterPatternValue(java.lang.String)
	 */
	
	public void setFilterPatternValue(String filterPatternValue) {
		this.filterPatternValue = filterPatternValue;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#isDateAvailableInFile()
	 */
	
	public boolean isDateAvailableInFile() {
		return isDateAvailableInFile;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setDateAvailableInFile(boolean)
	 */
	
	public void setDateAvailableInFile(boolean isDateAvailableInFile) {
		this.isDateAvailableInFile = isDateAvailableInFile;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setDestinationPathList(java.util.List)
	 */
	
	public void setDestinationPathList(List<String> destinationPathList) {
		this.destinationPathList = destinationPathList;
	}

	@Override
    public String toString(){
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        toString(builder);
        return builder.toString();
    }


    @Override
    public void toString(IndentingToStringBuilder out) {
        out.incrementIndentation();
        out.append("name", name);
        out.append("Source Path", sourcePath);
		out.append("Output Path", destinationInputPath);
		out.append("Archive Path", archivePath);
		out.append("Error Path", errorPath);
		out.append("Rating Type", ratingType);
		out.append("Sorting Criteria", sortingCriteria);
		out.append("Sorting type", sortingType);
		out.append("File Mapping", name);
        out.decrementIndentation();
    }
    

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getDuplicateDir()
	 */
	
	public File getDuplicateDir() {
		return duplicateDir;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setDuplicateDir(java.io.File)
	 */
	
	public void setDuplicateDir(File duplicateFile) {
		duplicateDir = duplicateFile;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getPluginList()
	 */
	
	public List<Map<String, Object>> getPluginList() {
		return pluginList;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setPluginList(java.util.List)
	 */
	
	public void setPluginList(List<Map<String, Object>> pluginList) {
		this.pluginList = pluginList;
	}

	//File Alert Methods

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#isFileSequenceAlert()
	 */
	
	public boolean isFileSequenceAlert() {
		return isFileSequenceAlert;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setFileSequenceAlert(boolean)
	 */
	
	public void setFileSequenceAlert(boolean isFileSequenceAlert) {
		this.isFileSequenceAlert = isFileSequenceAlert;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getFileSequenceAlertStartIndex()
	 */
	
	public int getFileSequenceAlertStartIndex() {
		return fileSequenceAlertStartIndex;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setFileSequenceAlertStartIndex(int)
	 */
	
	public void setFileSequenceAlertStartIndex(int fileSequenceAlertStartIndex) {
		this.fileSequenceAlertStartIndex = fileSequenceAlertStartIndex;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getFileSequenceAlertEndIndex()
	 */
	
	public int getFileSequenceAlertEndIndex() {
		return fileSequenceAlertEndIndex;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setFileSequenceAlertEndIndex(int)
	 */
	
	public void setFileSequenceAlertEndIndex(int fileSequenceAlertEndIndex) {
		this.fileSequenceAlertEndIndex = fileSequenceAlertEndIndex;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getFileSequenceAlertCounter()
	 */
	
	public int getFileSequenceAlertCounter() {
		return fileSequenceAlertCounter;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setFileSequenceAlertCounter(int)
	 */
	
	public void setFileSequenceAlertCounter(int fileSequenceAlertCounter) {
		this.fileSequenceAlertCounter = fileSequenceAlertCounter;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#isInputFileCompressed()
	 */
	
	public boolean isInputFileCompressed() {
		return isInputFileCompressed;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setInputFileCompressed(boolean)
	 */
	
	public void setInputFileCompressed(boolean isInputFileCompressed) {
		this.isInputFileCompressed = isInputFileCompressed;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#isOutputFileCompressed()
	 */
	
	public boolean isOutputFileCompressed() {
		return isOutputFileCompressed;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setOutputFileCompressed(boolean)
	 */
	
	public void setOutputFileCompressed(boolean isOutputFileCompressed) {
		this.isOutputFileCompressed = isOutputFileCompressed;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setOutputFileCompressed(boolean)
	 */
	
	public String getPolicyName() {
		return policyName;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setOutputFileCompressed(boolean)
	 */
	
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getReadPathMaxFilesCount()
	 */
	
	public int getReadPathMaxFilesCount() {
		return readPathMaxFilesCount;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setReadPathMaxFilesCount(int)
	 */
	
	public void setReadPathMaxFilesCount(int readPathMaxFilesCount) {
		if(readPathMaxFilesCount > 0){
			this.readPathMaxFilesCount = readPathMaxFilesCount;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#getPathId()
	 */
	
	public String getPathId() {
		return pathId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.elitecore.mediation.commons.configuration.data.IPathData#setPathId(java.lang.String)
	 */
	
	public void setPathId(String pathId) {
		this.pathId = pathId;
	}

	public String getInProcessExtension() {
		return INP;
	}

	public int getNoOfAttempt() {
		return NUMBER_OF_ATTEMPT;
	}

	public String getName() {
		return name;
	}

	public String getSortingType() {
		return sortingType;
	}

	public String getSortingCriteria() {
		return sortingCriteria;
	}

	public String getErrorPath() {
		return errorPath;
	}

	public String getDeviceName() {
		return DEVICE;
	}

	public String getFileCopyFolders() {
		return null;
	}

	public String getFileRange() {
		return null;
	}

	public int getRecordBatchSize() {
		return 100;
		
	}

	public boolean isOverrideFileDate() {
		return false;
	}

	public String getOverrideFileDateType() {
		return null;
	}

	public String getArchivePath() {
		return archivePath;
	}

	public int getMinDiskSpaceRequired() {
		return 1;
	}

	public int getNoFileAlertInterval() {
		return FILE_ALERT_INTERVAL;
	}

	public String getCdrDateSummaryType() {
		return null;
	}

	public void setName(String name) {
		this.name = name;
		
	}

	public void setSortingType(String sortingType) {
		this.sortingType = sortingType;
	}

	public void setSortingCriteria(String sortingCriteria) {
		this.sortingCriteria = sortingCriteria;
	}

	public void setErrorPath(String errorPath) {
		this.errorPath = errorPath;
	}
	
	public FileMappingData getInputFileMappingData() {
		return inputFileMappingData;
	}

	public String getCharsetName() {
		return "UTF-8";
	}

	public String getSourceDateFormat() {
		return "";
	}

	public String getFieldSeparator() {
		return ";|,";
	}

	public String getKeyValueSeparator() {
		return null;
	}

	public boolean isKeyValueRecord() {
		return false;
	}

	public String getFileFooterContains() {
		return null;
	}

	public boolean isFooterPresent() {
		return false;
	}

	public String getHeaderType() {
		return "STANDARD";
	}

	public boolean isHeaderPresent() {
		return true;
	}

	public boolean isHeaderContainsField() {
		return true;
	}

	public String getRecordHeaderSeparator() {
		return null;
	}

	public boolean isRecordHeaderPresent() {
		return false;
	}

	public int getRecordHeaderLen() {
		return 0;
	}

	public String[] getFindAndReplace() {
		return null;
	}

	public String getDescription() {
		return description;
	}

	public Pattern getRegexPattern() {
		return Pattern.compile("(?:^|R)(\"(?:[^\"]+|\"\")*\"|[^R]*)".replace("R", getFieldSeparator())); //taken from MediationPluginConstants, decide what to do;
	}

	public boolean isPreservedSourcedField() {
		return true;
	}

	public String getRatingType() {
		return ratingType;
	}

	public String getTableName() {
		return tableName;
	}

	public @Nullable DBDataSource getDataSource() {
		return dataSource;
	}
	
	public List<DBFieldMapping> getDBFieldMappings() {
		return dbFieldMappings;
	}
	
	public List<FileMappingDetail> getOutputFileMappingDetails() {
		return outputFileMappingDetails;
	}

	public List<FileMapping> getFileMapping() {
		return inputFileMappings;
	}
	
	public int getQueryTimeout() {
		return queryTimeout;
	}
}
