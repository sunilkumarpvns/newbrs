package com.elitecore.aaa.radius.conf.impl;

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
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.impl.DetailLocalDriverDetails;
import com.elitecore.aaa.core.conf.impl.DetailLocalFileDetails;
import com.elitecore.aaa.core.conf.impl.DetailLocalFileTransferDetail;
import com.elitecore.aaa.core.conf.impl.FileRollingParamsDetail;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AttributeRelation;
import com.elitecore.aaa.radius.drivers.DriverConfigurable;
import com.elitecore.aaa.radius.drivers.conf.impl.DriverSelectQueryBuilder;
import com.elitecore.aaa.radius.drivers.conf.impl.RadDetailLocalAcctConfigurationImpl;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyConfigurable;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
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

@XmlType(propOrder = {})
@XmlRootElement(name = "detail-local-acct-drivers")
@ConfigurationProperties(moduleName ="RAD-DETAIL-LOCAL-CONFIGURABLE", readWith = DBReader.class, synchronizeKey = "", writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db","services","acct","driver"},name = "detail-local-acct-drivers")
public class RadDetailLocalDriverConfigurable extends Configurable implements DriverConfigurable {
	
	private List<RadDetailLocalAcctConfigurationImpl> radDetailLocalDriverList;
	
	private static final String MODULE = "RAD-DETAIL-LOCAL-CONFIGURABLE";
	
	public RadDetailLocalDriverConfigurable() {
		this.radDetailLocalDriverList = new ArrayList<RadDetailLocalAcctConfigurationImpl>();
	}

	@DBRead
	public void readDetailLocalAcctDriverConfiguration() throws Exception {

		Connection connection = null;

		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;
		
		PreparedStatement psFields = null;
		ResultSet rsFields = null;
		
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		RadiusServicePolicyConfigurable servicePolicyConfigurable = ((AAAConfigurationContext)getConfigurationContext()).get(RadiusServicePolicyConfigurable.class);
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String querya =  new DriverSelectQueryBuilder(DriverTypes.RAD_DETAIL_LOCAL_ACCT_DRIVER, servicePolicyConfigurable.getSelectedDriverIds()).build();


			List<RadDetailLocalAcctConfigurationImpl> tempDetailLocalriverList = new ArrayList<RadDetailLocalAcctConfigurationImpl>();
			psForDriverInstanceId = connection.prepareStatement(querya);
			
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {
				
				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");
				
				ps = connection.prepareStatement(getQueryForDetailLocalAcctDriver());
				
				psFields = connection.prepareStatement(getQueryForAttrRel());
				
				ps.setString(1,driverInstanceId);
				ps.setString(2,driverInstanceId);
				rs = ps.executeQuery();

				if(rs.next()) {
					
					RadDetailLocalAcctConfigurationImpl radDetailLocalAcctConfigurationImpl = new RadDetailLocalAcctConfigurationImpl();
					
					radDetailLocalAcctConfigurationImpl.setDriverInstanceId(driverInstanceId);
					
					String detailLocalId = rs.getString("detaillocalid");
					psFields.setString(1, detailLocalId);
					rsFields = psFields.executeQuery();
					ArrayList<AttributeRelation> attributeRelationList = new ArrayList<AttributeRelation>();
					while(rsFields.next()) {
						attributeRelationList.add(new AttributeRelation(rsFields.getString("ATTRIBUTEIDS"),rsFields.getString("DEFAULTVALUE"),rsFields.getString("USEDICTIONARYVALUE")));
					}
					radDetailLocalAcctConfigurationImpl.setAttributeRelationList(attributeRelationList);
					
					DetailLocalFileTransferDetail detailLocalFileTransferDetail = radDetailLocalAcctConfigurationImpl.getDetailLocalFileTransferDetail();
					
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
					String protocolType = detailLocalFileTransferDetail.getAllocatingProtocol();
					if ("FTP".equalsIgnoreCase(protocolType) || "SMTP".equalsIgnoreCase(protocolType)){
						try {
							URLData urlData = URLParser.parse(address);
							detailLocalFileTransferDetail.setIpAddress(urlData.getHost());

							int port = urlData.getPort();
							if(port <= 0) {
								if("SMTP".equalsIgnoreCase(protocolType)) {
									port = 25;
								} else if ("FTP".equalsIgnoreCase(protocolType)) {
									port = 21;
								}

								if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
									LogManager.getLogger().info(MODULE, "Considering default port " + port + " for " + protocolType + 
											" server, Reason: Port not configured");
							}
							detailLocalFileTransferDetail.setPort(port);
						} catch (Exception e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE, "Invalid Address parameter configured in File Transfer Detail. Reason: " + e.getMessage());
						}
					} else {
						detailLocalFileTransferDetail.setIpAddress(address);
					} 
					
					String driverName = "";
					if(rs.getString("NAME")!=null){
						radDetailLocalAcctConfigurationImpl.setDriverName(rs.getString("NAME"));
						driverName = radDetailLocalAcctConfigurationImpl.getDriverName();
					}	
					
					detailLocalFileTransferDetail.setDestinationLocation(rs.getString("REMOTELOCATION"));
					detailLocalFileTransferDetail.setUsrName(rs.getString("USERNAME"));
					detailLocalFileTransferDetail.setPassword(rs.getString("PASSWORD"));
					if(rs.getString("POSTOPERATION")!=null && rs.getString("POSTOPERATION").trim().length()>0){
						detailLocalFileTransferDetail.setPostOperation(rs.getString("POSTOPERATION"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Post Operation Parameter for Driver :"+driverName+" is not configured, using default value");
					}
					if(rs.getString("ARCHIVELOCATION")!=null && rs.getString("ARCHIVELOCATION").trim().length()>0){
						detailLocalFileTransferDetail.setArchiveLocations(rs.getString("ARCHIVELOCATION"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "Post operation ARCHIVE is disable for driver "+driverName+", Reason: Archive Location is not configured");
					}
					
					if(rs.getString("FAILOVERTIME")!=null && rs.getString("FAILOVERTIME").trim().length()>0){
						detailLocalFileTransferDetail.setFailOverTime(Numbers.parseInt(rs.getString("FAILOVERTIME").trim(), radDetailLocalAcctConfigurationImpl.getFailOverTime()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Fail Over Time Parameter for Driver :"+driverName+" is not configured, using default value");
					}
					DetailLocalFileDetails detailLocalFileDetails = radDetailLocalAcctConfigurationImpl.getDetailLocalFileDetails();
					if(rs.getString("FILENAME")!=null && rs.getString("FILENAME").trim().length()>0){
						detailLocalFileDetails.setFileName(rs.getString("FILENAME"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "File Name Parameter for Driver :"+driverName+" is not configured, using default value");
					}
					if(rs.getString("LOCATION")!=null && rs.getString("LOCATION").trim().length()>0){
						detailLocalFileDetails.setLocation(rs.getString("LOCATION"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Location Parameter for Driver :"+driverName+" is not configured, using default value");
					}
					if(rs.getString("DEFAULTDIRNAME")!=null && rs.getString("DEFAULTDIRNAME").trim().length()>0){
						detailLocalFileDetails.setDefaultDirName(rs.getString("DEFAULTDIRNAME"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Default Directory Name Parameter for Driver :"+driverName+" is not configured, using default value");
					}
					FileRollingParamsDetail fileRollingParamsDetail = radDetailLocalAcctConfigurationImpl.getFileRollingParamsDetail();
					
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
					
					
					DetailLocalDriverDetails detailLocalDriverDetails = radDetailLocalAcctConfigurationImpl.getDetailLocalDriverDetails();
					if(rs.getString("EVENTDATEFORMAT")!=null && rs.getString("EVENTDATEFORMAT").trim().length()>0){
						detailLocalDriverDetails.setEventDateFormat(rs.getString("EVENTDATEFORMAT"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Event Date Format Parameter for Driver :"+driverName+" is not configured, using default value");
					}
					
					if(rs.getString("PREFIXFILENAME")!=null && rs.getString("PREFIXFILENAME").trim().length()>0){
						detailLocalFileDetails.setPrefixFileName(rs.getString("PREFIXFILENAME"));
					}
					
					if(rs.getString("FOLDERNAME")!=null && rs.getString("FOLDERNAME").trim().length()>0){
						detailLocalFileDetails.setFolderName(rs.getString("FOLDERNAME"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Folder Name Parameter for Driver :"+driverName+" is not configured, using default value");
					}
					if(rs.getString("WRITEATTRIBUTES")!=null && rs.getString("WRITEATTRIBUTES").trim().length()>0){
						detailLocalDriverDetails.setWriteAttributes(rs.getString("WRITEATTRIBUTES"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "WriteAttributes Parameter for Driver :"+driverName+" is not configured, using default value");
					}
					if(rs.getString("USEDICTIONARYVALUE")!=null && rs.getString("USEDICTIONARYVALUE").trim().length()>0){
						detailLocalDriverDetails.setIsUseDictonaryVal(Boolean.parseBoolean(rs.getString("USEDICTIONARYVALUE").trim()));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Use Dictioary Value Parameter for Driver :"+driverName+" is not configured, using default value");
					}
					if(rs.getString("AVPAIRSEPARATOR")!=null && rs.getString("AVPAIRSEPARATOR").length()>0){
						detailLocalDriverDetails.setAvPairSeparator(rs.getString("AVPAIRSEPARATOR"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "AvPair Seperator Parameter for Driver :"+driverName+" is not configured, using default value");
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
							LogManager.getLogger().debug(MODULE, "Globalization Parameter for Driver :"+driverName+" is not configured, using default value");
					}
					tempDetailLocalriverList.add(radDetailLocalAcctConfigurationImpl);
				}
			}
			this.radDetailLocalDriverList = tempDetailLocalriverList;
		} finally{
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(rsFields);
			DBUtility.closeQuietly(psFields);
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(ps);
			DBUtility.closeQuietly(connection);
		}
	}

	@DBReload
	public void reloadDetailLocalDriverConfiguration(){
		
	}
	
	@PostRead
	public void postReadProcessing() {
		if(this.radDetailLocalDriverList!=null){
			
			int numOfDrivers = radDetailLocalDriverList.size();
			RadDetailLocalAcctConfigurationImpl radDetailLocalAcctConfigurationImpl;
			for(int i=0;i<numOfDrivers;i++){
				radDetailLocalAcctConfigurationImpl = radDetailLocalDriverList.get(i);
				
				String fileNameStr = radDetailLocalAcctConfigurationImpl.getPrefixFileName();
				if(fileNameStr!=null && fileNameStr.length()>0){
					radDetailLocalAcctConfigurationImpl.setFileNameAttribute(fileNameStr.split(","));
				}
				
				String folderNameStr = radDetailLocalAcctConfigurationImpl.getFolderName();
				if(folderNameStr!=null && folderNameStr.length()>0){
					radDetailLocalAcctConfigurationImpl.setFolderNameAttribute(folderNameStr.split(","));
				}
				
				Map<RollingTypeConstant, Integer> rollingTypeMap = new HashMap<RollingTypeConstant, Integer>();
				FileRollingParamsDetail fileRollingParamsDetail = radDetailLocalAcctConfigurationImpl.getFileRollingParamsDetail();
				
				if(fileRollingParamsDetail.getTimeBoundryRollingUnit() > 0){
					rollingTypeMap.put(RollingTypeConstant.TIME_BOUNDRY_ROLLING, fileRollingParamsDetail.getTimeBoundryRollingUnit());
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Ignore the TimeBase Rolling configuration for Driver:"+radDetailLocalAcctConfigurationImpl.getDriverName()+", Reason: TimeBoundry Rolling is also configured for driver.");
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
					fileRollingParamsDetail.setTimeBoundryRollingUnit(radDetailLocalAcctConfigurationImpl.DEFAULT_TIMEBOUNDRY);
					rollingTypeMap.put(RollingTypeConstant.TIME_BOUNDRY_ROLLING, fileRollingParamsDetail.getTimeBoundryRollingUnit());

					LogManager.getLogger().debug(MODULE, "No Configuration for file rolling is provided in Radius detail local accounting Driver: "+radDetailLocalAcctConfigurationImpl.getDriverName()+ 
							",so enable the default Time Boundry Rolling Task to: " + fileRollingParamsDetail.getTimeBoundryRollingUnit() + " min.");
				}
				
				fileRollingParamsDetail.setRollingTypeMap(rollingTypeMap);
			}
		}
	}
	
	@PostWrite
	public void postWriteProcessing() {

	}
	
	@PostReload
	public void postReloadProcessing() {

	}
	
	@Override
	@XmlElement(name="detail-local-acct-driver")
	public List<RadDetailLocalAcctConfigurationImpl> getDriverConfigurationList() {
		return radDetailLocalDriverList;
	}
	
	private String getQueryForDetailLocalAcctDriver() {
		return "SELECT A.*,B.NAME FROM TBLMDETAILLOCALACCTDRIVER A ,TBLMDRIVERINSTANCE B WHERE A.DRIVERINSTANCEID =? AND B.DRIVERINSTANCEID=?";
	}

	private String getQueryForAttrRel() {
		return "SELECT * FROM tblmdetaillocalattrrel where  detaillocalid=?";
	}
}