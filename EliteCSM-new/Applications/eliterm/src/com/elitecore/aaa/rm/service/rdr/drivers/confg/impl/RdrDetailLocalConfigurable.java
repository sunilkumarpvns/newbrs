package com.elitecore.aaa.rm.service.rdr.drivers.confg.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.data.AttributeRelation;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.util.ConfigurationUtil;

@XmlType(propOrder = {})
@XmlRootElement(name = "rdr-detail-local-driver")
@ConfigurationProperties(moduleName ="RDR-DETAIL-LOCAL-CONFIGURABLE", readWith = DBReader.class, synchronizeKey = "", writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db","services","rdr","driver"},name = "rdr-detail-local-driver")
public class RdrDetailLocalConfigurable extends Configurable{

	private static final String QUERY_FOR_DETAIL_LOCAL_ACCT_DRIVER = "SELECT A.*,B.NAME FROM TBLMDETAILLOCALACCTDRIVER A ,TBLMDRIVERINSTANCE B WHERE A.DRIVERINSTANCEID =? AND B.DRIVERINSTANCEID=?";
	private static final String QUERY_FOR_ATTRITBUTES_RELATION = "SELECT * FROM tblmdetaillocalattrrel where  detaillocalid=?";
	private static final String MODULE = "RDR-DETAIL-LOCAL-CONFIGURABLE";
	private String driverInstanceId="40";
	private String driverName="";
	private RdrDetailLocalConfigurationImpl configurationImpl;
	
	public void setConfigurationImpl(RdrDetailLocalConfigurationImpl configurationImpl ){
		this.configurationImpl = configurationImpl;
	}
	@XmlElement(name ="rdr-detail-local")
	public RdrDetailLocalConfigurationImpl getconfigurationImpl(){
		return configurationImpl;
	}


	@DBRead
	public void readRdrDetailLocalConfiguration() throws Exception{
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement psFields = null;
		ResultSet rsFields = null;
		LogManager.getLogger().info(MODULE, "Loading RDR Detail Local driver configuration from database");
		RdrDetailLocalConfigurationImpl rdrDetailLocalConfigurationImpl = null;
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			ps = connection.prepareStatement(getQueryForDetailLocalAcctDriver());
			if(ps == null){
				LogManager.getLogger().debug(MODULE,"PreparedStatement is null.");
				throw new LoadConfigurationException("Prepared statement is null.");
			}
			psFields = connection.prepareStatement(getQueryForAttrRel());
			if(psFields == null){
				LogManager.getLogger().debug(MODULE,"PreparedStatement is null.");
				throw new LoadConfigurationException("Prepared statement is null.");
			}
			ps.setString(1,this.driverInstanceId);
			ps.setString(2,this.driverInstanceId);
			rs = ps.executeQuery();
			while(rs.next()) {
				rdrDetailLocalConfigurationImpl = new RdrDetailLocalConfigurationImpl();
				String detailLocalId = rs.getString("detaillocalid");
				psFields.setString(1, detailLocalId);
				rsFields = psFields.executeQuery();
				ArrayList<AttributeRelation> attributeRelationList = new ArrayList<AttributeRelation>();

				while(rsFields.next()) {
					attributeRelationList.add(new AttributeRelation(rsFields.getString("ATTRIBUTEIDS"),rsFields.getString("DEFAULTVALUE"),rsFields.getString("USEDICTIONARYVALUE")));
				}	
				rdrDetailLocalConfigurationImpl.setAttributeRelationList(attributeRelationList);
				String allocatingProtocol = rs.getString("ALLOCATINGPROTOCOL");
				if(Strings.isNullOrBlank(allocatingProtocol) == false){
					rdrDetailLocalConfigurationImpl.setAllocatingProtocol(allocatingProtocol);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Allocating Protocol Parameter is not defined , using default value");
				}
				rdrDetailLocalConfigurationImpl.setIpAddress(rs.getString("IPADDRESS"));
				
				if(rs.getString("NAME")!=null)
					driverName  = rs.getString("NAME");
				rdrDetailLocalConfigurationImpl.setDriverName(driverName);
				rdrDetailLocalConfigurationImpl.setPort(rs.getInt("PORT"));
				rdrDetailLocalConfigurationImpl.setRemoteLocation(rs.getString("REMOTELOCATION"));
				rdrDetailLocalConfigurationImpl.setUsrName(rs.getString("USERNAME"));
				rdrDetailLocalConfigurationImpl.setPassword(rs.getString("PASSWORD"));
				String postOperation = rs.getString("POSTOPERATION");
				if(Strings.isNullOrBlank(postOperation) == false){
					rdrDetailLocalConfigurationImpl.setPostOperation(postOperation);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Post Operation Parameter for Driver :"+driverName+" is not defined , using default value");
				}
				String archiveLocation = rs.getString("ARCHIVELOCATION");
				if(Strings.isNullOrBlank(archiveLocation) == false){
					rdrDetailLocalConfigurationImpl.setArchieveLocation(archiveLocation);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Archive Location Parameter for Driver :"+driverName+" is not defined , using default value");
				}
				String failOverTime = rs.getString("FAILOVERTIME");
				if(Strings.isNullOrBlank(failOverTime) == false){
					rdrDetailLocalConfigurationImpl.setFailOverTime(ConfigurationUtil.stringToInteger(failOverTime.trim(), rdrDetailLocalConfigurationImpl.getFailOverTime()));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Fail Over Time Parameter for Driver :"+driverName+" is not defined , using default value");
				}
				String fileName = rs.getString("FILENAME");
				if(Strings.isNullOrBlank(fileName) == false){
					rdrDetailLocalConfigurationImpl.setFileName(fileName);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "File Name Parameter for Driver :"+driverName+" is not defined , using default value");
				}
				String location = rs.getString("LOCATION");
				if(Strings.isNullOrBlank(location) == false){
					rdrDetailLocalConfigurationImpl.setFileLocation(location);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Location Parameter for Driver :"+driverName+" is not defined , using default value");
				}
				String defaultDirName = rs.getString("DEFAULTDIRNAME");
				if(Strings.isNullOrBlank(defaultDirName) == false){
					rdrDetailLocalConfigurationImpl.setDefauleDirName(defaultDirName);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Default Directory Name Parameter for Driver :"+driverName+" is not defined , using default value");
				}
				String eventDateFormat = rs.getString("EVENTDATEFORMAT");
				if(Strings.isNullOrBlank(eventDateFormat) == false){
					rdrDetailLocalConfigurationImpl.setEventDateFormat(eventDateFormat);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Event Date Format Parameter for Driver :"+driverName+" is not defined , using default value");
				}
				String prefixFileName = rs.getString("PREFIXFILENAME");
				if(Strings.isNullOrBlank(prefixFileName) == false){
					rdrDetailLocalConfigurationImpl.setPrefixFileName(prefixFileName);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Prefix File Name Parameter for Driver :"+driverName+" is not defined , using default value");
				}
				String folderName = rs.getString("FOLDERNAME");
				if(Strings.isNullOrBlank(folderName) == false){
					rdrDetailLocalConfigurationImpl.setFolderName(folderName);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Folder Name Parameter for Driver :"+driverName+" is not defined , using default value");
				}
				String writeAttributes = rs.getString("WRITEATTRIBUTES");
				if(Strings.isNullOrBlank(writeAttributes) == false){
					rdrDetailLocalConfigurationImpl.setWriteAttributes(writeAttributes);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "WriteAttributes Parameter for Driver :"+driverName+" is not defined , using default value");
				}
				String useDictionaryValue = rs.getString("USEDICTIONARYVALUE");
				if(Strings.isNullOrBlank(useDictionaryValue) == false){
					rdrDetailLocalConfigurationImpl.setUseDictionaryValue(ConfigurationUtil.stringToBoolean(useDictionaryValue.trim(), rdrDetailLocalConfigurationImpl.getUseDictionaryValue()));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Use Dictioary Value Parameter for Driver :"+driverName+" is not defined , using default value");
				}
				String avpPairSeparator = rs.getString("AVPAIRSEPARATOR");
				if(Strings.isNullOrBlank(avpPairSeparator) == false){
					rdrDetailLocalConfigurationImpl.setAvPairSeparator(avpPairSeparator);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "AvPair Seperator Parameter for Driver :"+driverName+" is not defined , using default value");
				}
				rdrDetailLocalConfigurationImpl.setRange(rs.getString("RANGE"));
				rdrDetailLocalConfigurationImpl.setPattern(rs.getString("PATTERN"));

				String globalization = rs.getString("GLOBALIZATION");
				if(Strings.isNullOrBlank(globalization) == false){
					rdrDetailLocalConfigurationImpl.setGlobalization(ConfigurationUtil.stringToBoolean(globalization.trim(), rdrDetailLocalConfigurationImpl.getGlobalization()));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Globalization Parameter for Driver :"+driverName+" is not defined , using default value");
				}
			}	
			 this.configurationImpl = rdrDetailLocalConfigurationImpl;
		}finally{
			DBUtility.closeQuietly(rsFields);
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(psFields);
			DBUtility.closeQuietly(ps);
			DBUtility.closeQuietly(connection);
		}
	}
	
	private String getQueryForAttrRel() {
		return QUERY_FOR_ATTRITBUTES_RELATION;
	}
	private String getQueryForDetailLocalAcctDriver() {
		return QUERY_FOR_DETAIL_LOCAL_ACCT_DRIVER;
	}
	
	@PostRead
	public void postReadProcessing(){
		if(configurationImpl != null){
			int fileRollingType =  configurationImpl.getFileRollingType();
			if(fileRollingType == EliteFileWriter.RECORD_BASED_ROLLING)
				configurationImpl.setRollingUnit(EliteFileWriter.RECORD_BASED_MAX_NUMBER_OF_RECORDS);
		}
	}	
	@PostWrite
	public void postWriteProcessing() {
		// Do nothing
	}
	
	@PostReload
	public void postReloadProcessing() {
		// Do nothing
	}
	@DBReload
	public void reloadRdrDetailLocalConfiguration() throws LoadConfigurationException{
		// Do nothing
	}
}
