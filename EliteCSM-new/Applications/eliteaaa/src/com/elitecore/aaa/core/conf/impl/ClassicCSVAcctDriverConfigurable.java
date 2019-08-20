package com.elitecore.aaa.core.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration;
import com.elitecore.aaa.core.data.AttributesRelation;
import com.elitecore.aaa.core.data.StripAttributeRelation;
import com.elitecore.aaa.radius.conf.impl.FileTransferDetail;
import com.elitecore.aaa.radius.drivers.DriverConfigurable;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.commons.util.ConfigurationUtil;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;

public abstract class ClassicCSVAcctDriverConfigurable<T extends ClassicCSVAcctDriverConfigurationImpl> extends Configurable 
implements DriverConfigurable {

	@DBRead
	public void readFromDB() throws Exception {

		Connection connection = null;
		String query = "";

		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;

		PreparedStatement pstmtCSVConf=null;
		ResultSet rsCSVConf =null;

		try{
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			query = getDriverQuery();
			psForDriverInstanceId = connection.prepareStatement(query);
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {		

				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");

				try {

					pstmtCSVConf = connection.prepareStatement(getQueryForClassicCSVAcctDriver());
					pstmtCSVConf.setString(1, driverInstanceId);
					pstmtCSVConf.setString(2, driverInstanceId);
					rsCSVConf = pstmtCSVConf.executeQuery();
					if(rsCSVConf.next()){
						T classicCSVAcctConfigurationImpl = createConfigurationObject();

						classicCSVAcctConfigurationImpl.setDriverInstanceId(driverInstanceId);

						String classicCSVId = rsCSVConf.getString("classiccsvid");
						if(rsCSVConf.getString("NAME")!=null)
							classicCSVAcctConfigurationImpl.setDriverName(rsCSVConf.getString("NAME"));

						String driverName = classicCSVAcctConfigurationImpl.getDriverName();

						PreparedStatement psFields = null;
						ResultSet rsFields = null;

						try {
							psFields = connection.prepareStatement(getQueryForAttrRel());
							psFields.setString(1, classicCSVId);
							rsFields = psFields.executeQuery();

							List<AttributesRelation> attributesRelationList = new ArrayList<AttributesRelation>();
							while(rsFields.next()) {
								String attributeId = rsFields.getString("ATTRIBUTEIDS");
								AttributesRelation tempAttributesRelation = new AttributesRelation(attributeId,rsFields.getString("DEFAULTVALUE"),Boolean.parseBoolean(rsFields.getString("USEDICTIONARYVALUE")),rsFields.getString("HEADER"),null);
								if(tempAttributesRelation!=null){
									attributesRelationList.add(tempAttributesRelation);
								}	
							}
							classicCSVAcctConfigurationImpl.setAttributesRelationList(attributesRelationList);
						} finally {
							DBUtility.closeQuietly(rsFields);
							DBUtility.closeQuietly(psFields);
						}

						PreparedStatement psStripFields = null;
						ResultSet rsStripFields = null;

						try {
							psStripFields = connection.prepareStatement(getQueryForStripAttrRel());
							psStripFields.setString(1, classicCSVId);
							rsStripFields = psStripFields.executeQuery();

							ArrayList<StripAttributeRelation> stripAttributeRelationList = new ArrayList<StripAttributeRelation>();

							while(rsStripFields.next()) {
								String attributeId = rsStripFields.getString("ATTRIBUTEID");
								StripAttributeRelation stripAttributeRelation = new StripAttributeRelation(attributeId,rsStripFields.getString("PATTERN"),rsStripFields.getString("SEPARATOR"));
								stripAttributeRelationList.add(stripAttributeRelation);
							}
							classicCSVAcctConfigurationImpl.setStripAttributeRelationList(stripAttributeRelationList);
						}finally {
							DBUtility.closeQuietly(psStripFields);
							DBUtility.closeQuietly(rsStripFields);
						}

						FileTransferDetail fileTransferDetail = classicCSVAcctConfigurationImpl.getFileTransferDetail();

						if(rsCSVConf.getString("ALLOCATINGPROTOCOL")!=null && rsCSVConf.getString("ALLOCATINGPROTOCOL").trim().length()>0){
							fileTransferDetail.setAllocatingProtocol(rsCSVConf.getString("ALLOCATINGPROTOCOL"));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(getModuleName(), "Allocating Protocol Parameter is not defined , using default value");
						}

						/*
						 * In EliteAAA branch Host and port configured as host:port and inserted in IPADDRESS column as DBC change identified. 
						 * DBC Change will be taken into Trunk.
						 */
						String address = rsCSVConf.getString("IPADDRESS");
						String protocolType = fileTransferDetail.getAllocatingProtocol();
						if ("FTP".equalsIgnoreCase(protocolType) || "SMTP".equalsIgnoreCase(protocolType)){
							try {
								URLData urlData = URLParser.parse(address);
								fileTransferDetail.setIpAddress(urlData.getHost());

								int port = urlData.getPort();
								if(port <= 0) {
									if("SMTP".equalsIgnoreCase(protocolType)) {
										port = 25;
									} else if ("FTP".equalsIgnoreCase(protocolType)) {
										port = 21;
									}

									if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
										LogManager.getLogger().info(getModuleName(), "Considering default port " + port + " for " + protocolType + 
												" server, Reason: Port not configured");
								}
								fileTransferDetail.setPort(port);
							} catch (Exception e) {
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
									LogManager.getLogger().warn(getModuleName(), "Invalid Address parameter configured in File Transfer Detail. Reason: " + e.getMessage());
							}
						} else {
							fileTransferDetail.setIpAddress(address);
						} 

						fileTransferDetail.setDestinationLocation(rsCSVConf.getString("REMOTELOCATION"));
						fileTransferDetail.setUsrName(rsCSVConf.getString("USERNAME"));
						fileTransferDetail.setPassword(rsCSVConf.getString("PASSWORD"));

						if(rsCSVConf.getString("POSTOPERATION")!=null && rsCSVConf.getString("POSTOPERATION").trim().length()>0){
							fileTransferDetail.setPostOperation(rsCSVConf.getString("POSTOPERATION"));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(getModuleName(), "Post Operation Parameter for Driver :"+driverName+" is not configured, using default value");
						}
						if(rsCSVConf.getString("ARCHIVELOCATION")!=null && rsCSVConf.getString("ARCHIVELOCATION").trim().length()>0){
							fileTransferDetail.setArchiveLocations(rsCSVConf.getString("ARCHIVELOCATION"));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(getModuleName(), "Post operation ARCHIVE is disable for driver "+driverName+", Reason: Archive Location is not configured");
						}

						if(rsCSVConf.getString("FAILOVERTIME")!=null && rsCSVConf.getString("FAILOVERTIME").trim().length()>0){
							fileTransferDetail.setFailOverTime(Numbers.parseInt(rsCSVConf.getString("FAILOVERTIME").trim(),classicCSVAcctConfigurationImpl.getFailOverTime()));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(getModuleName(), "Fail Over Time Parameter for Driver :"+driverName+" is not configured, using default value");
						}

						FileDetails fileDetails = classicCSVAcctConfigurationImpl.getFileDetails();

						if(rsCSVConf.getString("FILENAME")!=null && rsCSVConf.getString("FILENAME").trim().length()>0){
							fileDetails.setFileName(rsCSVConf.getString("FILENAME"));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(getModuleName(), "File Name Parameter for Driver :"+driverName+" is not configured, using default value");
						}
						if(rsCSVConf.getString("LOCATION")!=null && rsCSVConf.getString("LOCATION").trim().length()>0){
							fileDetails.setFileLocation(rsCSVConf.getString("LOCATION"));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(getModuleName(), "Location Parameter for Driver :"+driverName+" is not configured, using default value");
						}
						if(rsCSVConf.getString("DEFAULTDIRNAME")!=null && rsCSVConf.getString("DEFAULTDIRNAME").trim().length()>0){
							fileDetails.setDefaultDirName(rsCSVConf.getString("DEFAULTDIRNAME"));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(getModuleName(), "Default Directory Name Parameter for Driver :"+driverName+" is not configured, using default value");
						}

						FileRollingParamsDetail fileRollingParamsDetail = classicCSVAcctConfigurationImpl.getFileRollingParamsDetail();

						if(rsCSVConf.getString("TIMEBOUNDRY") != null && rsCSVConf.getString("TIMEBOUNDRY").trim().length() > 0){
							fileRollingParamsDetail.setTimeBoundryRollingUnit(Numbers.parseInt(rsCSVConf.getString("TIMEBOUNDRY").trim(), fileRollingParamsDetail.getTimeBoundryRollingUnit()));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(getModuleName(), "Time Boundry Rolling Unit Parameter for Driver :"+driverName+" is not configured");
						}

						if(rsCSVConf.getString("TIMEBASEDROLLINGUNIT")!=null && rsCSVConf.getString("TIMEBASEDROLLINGUNIT").trim().length()>0){
							fileRollingParamsDetail.setTimeBaseRollingUnit(Numbers.parseInt(rsCSVConf.getString("TIMEBASEDROLLINGUNIT").trim(), fileRollingParamsDetail.getTimeBaseRollingUnit()));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(getModuleName(), "Time Based Rolling Unit Parameter for Driver :"+driverName+" is not configured");
						}

						if(rsCSVConf.getString("SIZEBASEDROLLINGUNIT") != null && rsCSVConf.getString("SIZEBASEDROLLINGUNIT").trim().length() > 0){
							fileRollingParamsDetail.setSizeBaseRollingUnit(Numbers.parseInt(rsCSVConf.getString("SIZEBASEDROLLINGUNIT").trim(), fileRollingParamsDetail.getSizeBaseRollingUnit()));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(getModuleName(), "Size Based Rolling Unit Parameter for Driver :"+driverName+" is not configured");
						}

						if(rsCSVConf.getString("RECORDBASEDROLLINGUNIT") != null && rsCSVConf.getString("RECORDBASEDROLLINGUNIT").trim().length() > 0){
							fileRollingParamsDetail.setRecordBaseRollingUnit(Numbers.parseInt(rsCSVConf.getString("RECORDBASEDROLLINGUNIT").trim(), fileRollingParamsDetail.getRecordBaseRollingUnit()));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(getModuleName(), "Record Base Rolling Unit Parameter for Driver :"+driverName+" is not configured");
						}

						if(rsCSVConf.getString("PREFIXFILENAME")!=null && rsCSVConf.getString("PREFIXFILENAME").trim().length()>0){
							fileDetails.setPrefixFileName(rsCSVConf.getString("PREFIXFILENAME"));
						}

						if(rsCSVConf.getString("FOLDERNAME")!=null && rsCSVConf.getString("FOLDERNAME").trim().length()>0){
							fileDetails.setFolderName(rsCSVConf.getString("FOLDERNAME"));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(getModuleName(), "Folder Name Parameter is for Driver :"+driverName+" is not configured, using default value");
						}
						CDRDetails cdrDetails = classicCSVAcctConfigurationImpl.getCdrDetails();
						if(rsCSVConf.getString("HEADER")!=null && rsCSVConf.getString("HEADER").trim().length()>0){
							cdrDetails.setHeader(rsCSVConf.getString("HEADER"));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(getModuleName(), "Header Parameter for Driver :"+driverName+" is not configured, using default value");
						}
						if(rsCSVConf.getString("CREATEBLANKFILE")!=null && rsCSVConf.getString("CREATEBLANKFILE").length()>0){
							fileDetails.setIsCreateBlankFile(Boolean.parseBoolean(rsCSVConf.getString("CREATEBLANKFILE").trim()));
						}

						if( rsCSVConf.getString("DELIMETER")!=null &&  rsCSVConf.getString("DELIMETER").length()>0){
							cdrDetails.setDelimeter(rsCSVConf.getString("DELIMETER"));
						}	
						if( rsCSVConf.getString("ENCLOSINGCHARACTER")!=null &&  rsCSVConf.getString("ENCLOSINGCHARACTER").length()>0){
							cdrDetails.setEnclosingChar(rsCSVConf.getString("ENCLOSINGCHARACTER"));
						}

						if(rsCSVConf.getString("MULTIVALUEDELIMETER")!=null && rsCSVConf.getString("MULTIVALUEDELIMETER").length()>0 ){
							cdrDetails.setmultiValueDelimeter(rsCSVConf.getString("MULTIVALUEDELIMETER"));
						}
						fileRollingParamsDetail.setSequenceRange(rsCSVConf.getString("RANGE"));
						fileRollingParamsDetail.setPattern(rsCSVConf.getString("PATTERN"));

						if(rsCSVConf.getString("GLOBALIZATION")!=null && rsCSVConf.getString("GLOBALIZATION").trim().length()>0){
							fileRollingParamsDetail.setGlobalization(ConfigurationUtil.stringToBoolean(rsCSVConf.getString("GLOBALIZATION"),classicCSVAcctConfigurationImpl.getGlobalization()));
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(getModuleName(), "Globalization Parameter for Driver :"+driverName+" is not configured, using default value");
						}
						if (DBUtility.isValueAvailable(rsCSVConf, "CDRTIMESTAMPHEADER")) {
							cdrDetails.setCdrTimeStampHeader(rsCSVConf.getString("CDRTIMESTAMPHEADER"));
						} else {
							if (LogManager.getLogger().isDebugLogLevel()) {
								LogManager.getLogger().debug(getModuleName(), "CDR Timestamp Header Parameter for Driver :" + driverName 
										+ " is not configured, using default value: " + ClassicCSVAcctDriverConfiguration.CDRTIMESTAMP_HEADER_DEFAULT_VALUE);
							}
						}

						cdrDetails.setCDRTimeStampFormat(rsCSVConf.getString("CDRTIMESTAMPFORMAT"));

						if (DBUtility.isValueAvailable(rsCSVConf, "CDRTIMESTAMPPOSITION")) {
							cdrDetails.setCdrTimeStampPosition(rsCSVConf.getString("CDRTIMESTAMPPOSITION"));
						} else {
							if (LogManager.getLogger().isDebugLogLevel()) {
								LogManager.getLogger().debug(getModuleName(), "CDR Timestamp Position parameter for driver :" + driverName 
										+ " is not configured, using default value: " + ClassicCSVAcctDriverConfiguration.CDRTIMESTAMP_POSITION_DEFAULT_VALUE);
							}
						}

						getDriverConfigurationList().add(classicCSVAcctConfigurationImpl);
					}
				}finally {

					DBUtility.closeQuietly(pstmtCSVConf);
					DBUtility.closeQuietly(rsCSVConf);
				}

			}

		} finally{
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(psForDriverInstanceId);	
			DBUtility.closeQuietly(connection);
		}

	}

	protected abstract T createConfigurationObject();
	protected abstract String getDriverQuery();
	protected abstract String getModuleName();

	@XmlTransient
	public abstract List<T> getDriverConfigurationList();

	@PostRead
	public void postReadProcessing() {
		List<T> classicCSVDriverConfList = getDriverConfigurationList();
		if (classicCSVDriverConfList != null) {

			int numOfDrivers = classicCSVDriverConfList.size();
			ClassicCSVAcctDriverConfigurationImpl radClassicCSVAcctConfigurationImpl;
			for(int i=0;i<numOfDrivers;i++){
				radClassicCSVAcctConfigurationImpl = classicCSVDriverConfList.get(i);

				List<AttributesRelation> relationList = radClassicCSVAcctConfigurationImpl.getAttributesRelationList();

				if(relationList!=null){
					List<AttributesRelation> validRelationList = getValidAttributesRelationList(radClassicCSVAcctConfigurationImpl,relationList);
					radClassicCSVAcctConfigurationImpl.setAttributesRelationList(validRelationList);
				}

				List<StripAttributeRelation> stripAttributeList = radClassicCSVAcctConfigurationImpl.getStripAttributeRelationList();
				if(stripAttributeList!=null){
					ArrayList<StripAttributeRelation> validStripAttrRelation = getValidStripAttrRelationList(stripAttributeList);
					radClassicCSVAcctConfigurationImpl.setStripAttributeRelationList(validStripAttrRelation);
				}


				String delimeter = radClassicCSVAcctConfigurationImpl.getDelimeter();
				if(delimeter != null && delimeter.length()%2 != 0){
					try{
						radClassicCSVAcctConfigurationImpl.setDelimeterLast(delimeter.substring(0, delimeter.length()/2));
						radClassicCSVAcctConfigurationImpl.setDelimeterFirst(delimeter.substring(delimeter.length()/2+1, delimeter.length()));
					}catch(Exception e){
					}
				}

				String fileNameStr = radClassicCSVAcctConfigurationImpl.getPrefixFileName();
				if(fileNameStr!=null && fileNameStr.length()>0){
					radClassicCSVAcctConfigurationImpl.setFileNameAttributes(fileNameStr.split(","));
				}

				String folderNameStr = radClassicCSVAcctConfigurationImpl.getFolderName();
				if(folderNameStr!=null && folderNameStr.length()>0){
					radClassicCSVAcctConfigurationImpl.setFolderNameAttributes(folderNameStr.split(","));
				}

				Map<RollingTypeConstant, Integer> rollingTypeMap = new HashMap<RollingTypeConstant, Integer>();
				FileRollingParamsDetail fileRollingParamsDetail = radClassicCSVAcctConfigurationImpl.getFileRollingParamsDetail();

				if(fileRollingParamsDetail.getTimeBoundryRollingUnit() > 0){
					rollingTypeMap.put(RollingTypeConstant.TIME_BOUNDRY_ROLLING, fileRollingParamsDetail.getTimeBoundryRollingUnit());
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(getModuleName(), "Ignore the TimeBase Rolling configuration for Driver:"+radClassicCSVAcctConfigurationImpl.getDriverName()+", Reason: Time Boundry Rolling is also configured for driver.");
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
					fileRollingParamsDetail.setTimeBoundryRollingUnit(radClassicCSVAcctConfigurationImpl.DEFAULT_TIMEBOUNDRY);
					rollingTypeMap.put(RollingTypeConstant.TIME_BOUNDRY_ROLLING, fileRollingParamsDetail.getTimeBoundryRollingUnit());

					LogManager.getLogger().debug(getModuleName(), "No Configuration for file rolling is provided in CSV accounting Driver: "+radClassicCSVAcctConfigurationImpl.getDriverName()+ 
							",so enable the default Time Boundry Rolling Task to: " + fileRollingParamsDetail.getTimeBoundryRollingUnit() + " min.");
				}

				fileRollingParamsDetail.setRollingTypeMap(rollingTypeMap);
			}
		}
	}
	@PostWrite
	public void postWriteProcessing(){

	}

	@PostReload
	public void postReloadProcessing(){

	}
	private ArrayList<StripAttributeRelation> getValidStripAttrRelationList(List<StripAttributeRelation> stripAttributeList) {
		ArrayList<StripAttributeRelation> validRelationList = new ArrayList<StripAttributeRelation>();
		int relationListSize = stripAttributeList.size();
		for(int j=0;j<relationListSize;j++){
			StripAttributeRelation stripAttributeRelation = stripAttributeList.get(j);
			if(isValidStripAttrRelation(stripAttributeRelation)){
				validRelationList.add(stripAttributeRelation);
			}
		}
		return validRelationList;
	}

	private boolean isValidStripAttrRelation(StripAttributeRelation stripAttributeRelation) {
		if(stripAttributeRelation.getAttributeId()!=null && stripAttributeRelation.getSeparator()!=null && stripAttributeRelation.getSeparator().length()>0&& stripAttributeRelation.getAttributeId().length()>0){
			return true;
		}else {
			return false;
		}
	}

	private List<AttributesRelation> getValidAttributesRelationList(ClassicCSVAcctDriverConfigurationImpl classicCSVAcctConfigurationImpl,List<AttributesRelation> relationList) {
		List<AttributesRelation> validRelationList = new ArrayList<AttributesRelation>();
		int relationListSize = relationList.size();
		AttributesRelation attributesRelation ;
		for(int j=0;j<relationListSize;j++){
			attributesRelation = relationList.get(j);
			String strAttributeId = attributesRelation.getAttributeId();
			if(strAttributeId!=null && strAttributeId.trim().length()>0){

				List<String> attributeList = new ArrayList<String>();
				StringTokenizer strVendorAttributeTokens = new StringTokenizer(strAttributeId,",;");
				while(strVendorAttributeTokens.hasMoreTokens()) {
					String attributeId = strVendorAttributeTokens.nextToken();
					attributeList.add(attributeId.trim());
				}
				attributesRelation.setAttributeList(attributeList);

				validRelationList.add(attributesRelation);
			}
		}
		return validRelationList;
	}

	private String getQueryForClassicCSVAcctDriver() {
		return "SELECT A.*,B.NAME FROM TBLMCLASSICCSVACCTDRIVER A ,TBLMDRIVERINSTANCE B WHERE A.DRIVERINSTANCEID =? AND B.DRIVERINSTANCEID=?";
	}


	private String getQueryForAttrRel() {
		return "SELECT * FROM TBLMCLASSICCSVATTRREL WHERE  CLASSICCSVID=? ORDER BY ORDERNUMBER";
	}
	private String getQueryForStripAttrRel() {
		return "SELECT * FROM TBLMCLASSICCSVSTRIPATTRREL WHERE  CLASSICCSVID=? ORDER BY ORDERNUMBER";
	}

	@DBReload
	public void reloadFromDB() throws SQLException {

	}
}
