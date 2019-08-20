package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.impl.DetailLocalDriverDetails;
import com.elitecore.aaa.core.conf.impl.DetailLocalFileDetails;
import com.elitecore.aaa.core.conf.impl.DetailLocalFileTransferDetail;
import com.elitecore.aaa.core.conf.impl.FileRollingParamsDetail;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AttributeRelation;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.conf.impl.DiameterEAPServiceConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterNasServiceConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
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
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;

@XmlType(propOrder={})
@XmlRootElement(name = "detail-local-drivers")
@ConfigurationProperties(moduleName = "NAS_DETAIL_LOCAL_ACCT_DRIVER", readWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "detail-local-drivers",schemaDirectories = {"system","schema"},configDirectories = {"conf","db","diameter","driver"})
public class DiameterDetailLocalAcctConfigurable extends Configurable{
	
	private String MODULE="NAS_DETAIL_LOCAL_ACCT_DRIVER";
	private List<NasDetailLocalAcctConfigurationImpl> nasDetailLocalAcctConfigurationList;

	@XmlElement(name = "detail-local-driver")
	public List<NasDetailLocalAcctConfigurationImpl> getDetailLocalAcctDriverConfigList() {
		return nasDetailLocalAcctConfigurationList;
	}
	public void setDetailLocalAcctDriverConfigList(List<NasDetailLocalAcctConfigurationImpl> nasDetailLocalAcctConfigurationList) {
		this.nasDetailLocalAcctConfigurationList = nasDetailLocalAcctConfigurationList;
	}
	
	public DiameterDetailLocalAcctConfigurable(){
		this.nasDetailLocalAcctConfigurationList = new  ArrayList<NasDetailLocalAcctConfigurationImpl>();
	}
	@DBRead
	public void readFromDB() throws Exception {

		Connection connection = null;

		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;
		
		PreparedStatement psFields = null;
		ResultSet rsFields = null;
		
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String querya = "select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+ DriverTypes.NAS_DETAIL_LOCAL_ACCT_DRIVER.value +"' AND (DRIVERINSTANCEID IN" +
						 "(select DISTINCT DRIVERINSTANCEID from tblmnaspolicyauthdriverrel where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
						 "(select DISTINCT DRIVERINSTANCEID from tblmnaspolicyacctdriverrel where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
						 "(select DISTINCT DRIVERINSTANCEID from tblmeappolicyauthdriverrel where eappolicyid in (" + getReadQueryForEapServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
						 "(select DISTINCT DRIVERINSTANCEID from TBLMEAPPOLICYADDDRIVERREL  where eappolicyid in (" + getReadQueryForEapServicePolicyConfiguration() +")) OR DRIVERINSTANCEID IN" +
						 "(select DISTINCT DRIVERINSTANCEID from TBLMNASADDAUTHDRIVERREL where naspolicyid in (" + getReadQueryForNasServicePolicyConfiguration() +")))";
				
			
			List<NasDetailLocalAcctConfigurationImpl> tempDetailLocalriverList = new ArrayList<NasDetailLocalAcctConfigurationImpl>();
			psForDriverInstanceId = connection.prepareStatement(querya); //NOSONAR - Reason: SQL binding mechanisms should be used
			
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {
				
				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");
				
				ps = connection.prepareStatement(getQueryForDetailLocalAcctDriver());
				
				psFields = connection.prepareStatement(getQueryForAttrRel());
				
				ps.setString(1,driverInstanceId);
				ps.setString(2,driverInstanceId);
				rs = ps.executeQuery();

				if(rs.next()) {
					
					NasDetailLocalAcctConfigurationImpl nasDetailLocalAcctConfigurationImpl = new NasDetailLocalAcctConfigurationImpl();
					
					nasDetailLocalAcctConfigurationImpl.setDriverInstanceId(driverInstanceId);
					
					String detailLocalId = rs.getString("detaillocalid");
					psFields.setString(1, detailLocalId);
					rsFields = psFields.executeQuery();
					ArrayList<AttributeRelation> attributeRelationList = new ArrayList<AttributeRelation>();
					while(rsFields.next()) {
						attributeRelationList.add(new AttributeRelation(rsFields.getString("ATTRIBUTEIDS"),rsFields.getString("DEFAULTVALUE"),rsFields.getString("USEDICTIONARYVALUE")));
					}
					nasDetailLocalAcctConfigurationImpl.setAttributeRelationList(attributeRelationList);
					
					DetailLocalFileTransferDetail detailLocalFileTransferDetail = nasDetailLocalAcctConfigurationImpl.getDetailLocalFileTransferDetail();
					
					if(rs.getString("ALLOCATINGPROTOCOL")!=null && rs.getString("ALLOCATINGPROTOCOL").trim().length()>0){
						detailLocalFileTransferDetail.setAllocatingProtocol(rs.getString("ALLOCATINGPROTOCOL"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Allocating Protocol Parameter is not defined , using default value");
					}
					
					/*
					 * In EliteAAA branch Host and port configured as host:port and inserted in IPADDRESS column as DBC change identified. 
					 * DBC Change will be taken into Trunk.
					 */
					String address = rs.getString("IPADDRESS");
					if ("FTP".equalsIgnoreCase(detailLocalFileTransferDetail.getAllocatingProtocol())){
						try {
							URLData urlData = URLParser.parse(address);
							detailLocalFileTransferDetail.setIpAddress(urlData.getHost());
							detailLocalFileTransferDetail.setPort(urlData.getPort());
						} catch (Exception e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE, "Invalid Address parameter configured in File Transfer Detail. Reason: " + e.getMessage());
						}
					} else {
						detailLocalFileTransferDetail.setIpAddress(address);
					} 
					
					String driverName = "";
					if(rs.getString("NAME")!=null){
						nasDetailLocalAcctConfigurationImpl.setDriverName(rs.getString("NAME"));
						driverName = nasDetailLocalAcctConfigurationImpl.getDriverName();
					}	
					
					detailLocalFileTransferDetail.setDestinationLocation(rs.getString("REMOTELOCATION"));
					detailLocalFileTransferDetail.setUsrName(rs.getString("USERNAME"));
					detailLocalFileTransferDetail.setPassword(rs.getString("PASSWORD"));
					if(rs.getString("POSTOPERATION")!=null && rs.getString("POSTOPERATION").trim().length()>0){
						detailLocalFileTransferDetail.setPostOperation(rs.getString("POSTOPERATION"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Post Operation Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("ARCHIVELOCATION")!=null && rs.getString("ARCHIVELOCATION").trim().length()>0){
						detailLocalFileTransferDetail.setArchiveLocations(rs.getString("ARCHIVELOCATION"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Archive Location Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("FAILOVERTIME")!=null && rs.getString("FAILOVERTIME").trim().length()>0){
						detailLocalFileTransferDetail.setFailOverTime(Numbers.parseInt(rs.getString("FAILOVERTIME").trim(), nasDetailLocalAcctConfigurationImpl.getFailOverTime()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Fail Over Time Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					DetailLocalFileDetails detailLocalFileDetails = nasDetailLocalAcctConfigurationImpl.getDetailLocalFileDetails();
					if(rs.getString("FILENAME")!=null && rs.getString("FILENAME").trim().length()>0){
						detailLocalFileDetails.setFileName(rs.getString("FILENAME"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "File Name Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("LOCATION")!=null && rs.getString("LOCATION").trim().length()>0){
						detailLocalFileDetails.setLocation(rs.getString("LOCATION"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Location Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("DEFAULTDIRNAME")!=null && rs.getString("DEFAULTDIRNAME").trim().length()>0){
						detailLocalFileDetails.setDefaultDirName(rs.getString("DEFAULTDIRNAME"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Default Directory Name Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					
					FileRollingParamsDetail fileRollingParamsDetail = nasDetailLocalAcctConfigurationImpl.getFileRollingParamsDetail();
					
 					if(rs.getString("TIMEBOUNDRY") != null && rs.getString("TIMEBOUNDRY").trim().length() > 0){
						fileRollingParamsDetail.setTimeBoundryRollingUnit(Numbers.parseInt(rs.getString("TIMEBOUNDRY").trim(), fileRollingParamsDetail.getTimeBoundryRollingUnit()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Time Boundry Rolling Unit Parameter for Driver :"+driverName+" is not configured");
					}
					
					if(rs.getString("TIMEBASEDROLLINGUNIT")!=null && rs.getString("TIMEBASEDROLLINGUNIT").trim().length()>0){
						fileRollingParamsDetail.setTimeBaseRollingUnit(Numbers.parseInt(rs.getString("TIMEBASEDROLLINGUNIT").trim(), fileRollingParamsDetail.getTimeBaseRollingUnit()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Time Based Rolling Unit Parameter for Driver :"+driverName+" is not configured");
					}

					if(rs.getString("SIZEBASEDROLLINGUNIT") != null && rs.getString("SIZEBASEDROLLINGUNIT").trim().length() > 0){
						fileRollingParamsDetail.setSizeBaseRollingUnit(Numbers.parseInt(rs.getString("SIZEBASEDROLLINGUNIT").trim(), fileRollingParamsDetail.getSizeBaseRollingUnit()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Size Based Rolling Unit Parameter for Driver :"+driverName+" is not configured");
					}
					
					if(rs.getString("RECORDBASEDROLLINGUNIT") != null && rs.getString("RECORDBASEDROLLINGUNIT").trim().length() > 0){
						fileRollingParamsDetail.setRecordBaseRollingUnit(Numbers.parseInt(rs.getString("RECORDBASEDROLLINGUNIT").trim(), fileRollingParamsDetail.getRecordBaseRollingUnit()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Record Base Rolling Unit Parameter for Driver :"+driverName+" is not configured");
					}
					
					DetailLocalDriverDetails detailLocalDriverDetails = nasDetailLocalAcctConfigurationImpl.getDetailLocalDriverDetails();
					if(rs.getString("EVENTDATEFORMAT")!=null && rs.getString("EVENTDATEFORMAT").trim().length()>0){
						detailLocalDriverDetails.setEventDateFormat(rs.getString("EVENTDATEFORMAT"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Event Date Format Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					
					if(rs.getString("PREFIXFILENAME")!=null && rs.getString("PREFIXFILENAME").trim().length()>0){
						detailLocalFileDetails.setPrefixFileName(rs.getString("PREFIXFILENAME"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Prefix File Name Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("FOLDERNAME")!=null && rs.getString("FOLDERNAME").trim().length()>0){
						detailLocalFileDetails.setFolderName(rs.getString("FOLDERNAME"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Folder Name Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("WRITEATTRIBUTES")!=null && rs.getString("WRITEATTRIBUTES").trim().length()>0){
						detailLocalDriverDetails.setWriteAttributes(rs.getString("WRITEATTRIBUTES"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "WriteAttributes Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("USEDICTIONARYVALUE")!=null && rs.getString("USEDICTIONARYVALUE").trim().length()>0){
						detailLocalDriverDetails.setIsUseDictonaryVal(Boolean.parseBoolean(rs.getString("USEDICTIONARYVALUE").trim()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Use Dictioary Value Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("AVPAIRSEPARATOR")!=null && rs.getString("AVPAIRSEPARATOR").length()>0){
						detailLocalDriverDetails.setAvPairSeparator(rs.getString("AVPAIRSEPARATOR"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "AvPair Seperator Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					if(rs.getString("CREATEBLANKFILE")!=null && rs.getString("CREATEBLANKFILE").trim().length()>0){
						detailLocalFileDetails.setIsCreateBlankFile(Boolean.parseBoolean(rs.getString("CREATEBLANKFILE").trim()));
					}
					fileRollingParamsDetail.setSequenceRange(rs.getString("RANGE"));
					fileRollingParamsDetail.setPattern(rs.getString("PATTERN"));

					if(rs.getString("GLOBALIZATION")!=null && rs.getString("GLOBALIZATION").trim().length()>0){
						fileRollingParamsDetail.setGlobalization(Boolean.parseBoolean(rs.getString("GLOBALIZATION").trim()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Globalization Parameter for Driver :"+driverName+" is not defined , using default value");
					}
					tempDetailLocalriverList.add(nasDetailLocalAcctConfigurationImpl);
				}
			}
			this.nasDetailLocalAcctConfigurationList = tempDetailLocalriverList;
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(rsFields);
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(ps);
			DBUtility.closeQuietly(psFields);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(connection);
		}
	}

	@DBReload
	public void reloadDiameterDetailLocalDriverConfiguration(){
		
	}
	
	@PostRead
	public void postReadProcessing() {
		if(this.nasDetailLocalAcctConfigurationList!=null){
			
			int numOfDrivers = nasDetailLocalAcctConfigurationList.size();
			NasDetailLocalAcctConfigurationImpl nasDetailLocalAcctConfigurationImpl;
			for(int i=0;i<numOfDrivers;i++){
				nasDetailLocalAcctConfigurationImpl = nasDetailLocalAcctConfigurationList.get(i);
				
				String fileNameStr = nasDetailLocalAcctConfigurationImpl.getPrefixFileName();
				if(fileNameStr!=null && fileNameStr.length()>0){
					nasDetailLocalAcctConfigurationImpl.setFileNameAttribute(fileNameStr.split(","));
				}
				
				String folderNameStr = nasDetailLocalAcctConfigurationImpl.getFolderName();
				if(folderNameStr!=null && folderNameStr.length()>0){
					nasDetailLocalAcctConfigurationImpl.setFolderNameAttribute(folderNameStr.split(","));
				}
				
				Map<RollingTypeConstant, Integer> rollingTypeMap = new HashMap<RollingTypeConstant, Integer>();
				FileRollingParamsDetail fileRollingParamsDetail = nasDetailLocalAcctConfigurationImpl.getFileRollingParamsDetail();
				
				if(fileRollingParamsDetail.getTimeBoundryRollingUnit() > 0){
					rollingTypeMap.put(RollingTypeConstant.TIME_BOUNDRY_ROLLING, fileRollingParamsDetail.getTimeBoundryRollingUnit());
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Ignore the TimeBase Rolling configuration for Driver:"+nasDetailLocalAcctConfigurationImpl.getDriverName()+", Reason: TimeBoundry Rolling is also configured for driver.");
					}
				}else if(fileRollingParamsDetail.getTimeBaseRollingUnit() > 0){
						rollingTypeMap.put(RollingTypeConstant.TIME_BASED_ROLLING, fileRollingParamsDetail.getTimeBaseRollingUnit());
				}
				
				if(fileRollingParamsDetail.getSizeBaseRollingUnit() > 0) 
					rollingTypeMap.put(RollingTypeConstant.SIZE_BASED_ROLLING, fileRollingParamsDetail.getSizeBaseRollingUnit());
				if(fileRollingParamsDetail.getRecordBaseRollingUnit() > 0)
					rollingTypeMap.put(RollingTypeConstant.RECORD_BASED_ROLLING, fileRollingParamsDetail.getRecordBaseRollingUnit());
				
				/**
				 * Set TimeBoundryRolling Unit = 24 * 60 = 1440
				 * if rolling unit is not configured. 
				 * i.e 0 for all Rolling Task in configuration.
				 */
				if(rollingTypeMap.isEmpty()){
					fileRollingParamsDetail.setTimeBoundryRollingUnit(nasDetailLocalAcctConfigurationImpl.DEFAULT_TIMEBOUNDRY);
					rollingTypeMap.put(RollingTypeConstant.TIME_BOUNDRY_ROLLING,fileRollingParamsDetail.getTimeBoundryRollingUnit());

					LogManager.getLogger().debug(MODULE, "No Configuration for file rolling is provided in NAS detail local accounting Driver: "+nasDetailLocalAcctConfigurationImpl.getDriverName()+ 
							",so enable the default Time Boundry Rolling Task to: " + fileRollingParamsDetail.getTimeBoundryRollingUnit() + " min.");
				}
				
				fileRollingParamsDetail.setRollingTypeMap(rollingTypeMap);
			}
		}
	}
	
	private String getQueryForDetailLocalAcctDriver() {
		return "SELECT A.*,B.NAME FROM TBLMDETAILLOCALACCTDRIVER A ,TBLMDRIVERINSTANCE B WHERE A.DRIVERINSTANCEID =? AND B.DRIVERINSTANCEID=?";
	}
	
	private String getQueryForAttrRel() {
		return "SELECT * FROM tblmdetaillocalattrrel where  detaillocalid=?";
	}
	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		
	}
	
	private String getReadQueryForNasServicePolicyConfiguration() {
		DiameterNasServiceConfigurable diameterNasServiceConfigurable = getConfigurationContext().get(DiameterNasServiceConfigurable.class);
		if(diameterNasServiceConfigurable == null) {
			return "''";
}
		List<String> servicePolicies = diameterNasServiceConfigurable.getServicePolicies();
		if(servicePolicies == null || servicePolicies.size() == 0 || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select naspolicyid from tblmnaspolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select naspolicyid from tblmnaspolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + 
				"' AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+")";

	}
	
	private String getReadQueryForEapServicePolicyConfiguration() {

		DiameterEAPServiceConfigurable diameterEAPServiceConfigurable = getConfigurationContext().get(DiameterEAPServiceConfigurable.class);
		if(diameterEAPServiceConfigurable == null) {
			return "''";
		}
		List<String> servicePolicies = diameterEAPServiceConfigurable.getServicePolicies();
		if(servicePolicies == null || servicePolicies.size() == 0 || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select eappolicyid from tblmeappolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select eappolicyid from tblmeappolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + 
				"' AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+") ";

	}
}
