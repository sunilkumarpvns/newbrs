package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.UserFileDriverConfiguration;
import com.elitecore.aaa.core.conf.impl.AAAServerConfigurable;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.conf.impl.DiameterEAPServiceConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterNasServiceConfigurable;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.TGPPServerPolicyConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
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

@XmlType(propOrder = {})
@XmlRootElement(name = "user-file-drivers")
@ConfigurationProperties(moduleName = "DIAMETER_USERFILE_DRIVER", readWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "user-file-drivers",schemaDirectories = {"system","schema"},configDirectories = {"conf","db","diameter","driver"})
public class DiameterUserFileDriverConfigurable extends Configurable {
	
	public static final String DRIVER_INSTANCE_ID = "instance-id";
	private static final String MODULE="DIAMETER_USERFILE_DRIVER";
	
	private List<DiameterUserFileDriverConfigurationImpl> diameterUserfileAuthDriverList;
	@XmlTransient
	private Map<String,UserFileDriverConfiguration> userFileAuthDriverConfMap;
	
	public DiameterUserFileDriverConfigurable(){
		diameterUserfileAuthDriverList = new ArrayList<DiameterUserFileDriverConfigurationImpl>();
		this.userFileAuthDriverConfMap = new LinkedHashMap<String, UserFileDriverConfiguration>();
	}
	
	@XmlElement(name ="user-file-driver")
	public List<DiameterUserFileDriverConfigurationImpl> getDiameterUserfileAuthDriverList() {
		return diameterUserfileAuthDriverList;
	}

	public void setDiameterUserfileAuthDriverList(List<DiameterUserFileDriverConfigurationImpl> diameterUserfileAuthDriverList) {
		this.diameterUserfileAuthDriverList = diameterUserfileAuthDriverList;
	}
	
	@DBRead
	public void readFromDB() throws Exception {
		
		Connection connection = null;
		String query = "";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		

		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;

		String baseQuery;
		
		AAAServerConfigurable serverConfigurable = getConfigurationContext().get(AAAServerConfigurable.class);
		if (isTGPPServerEnabled(serverConfigurable)) {
			TGPPServerPolicyConfigurable policyConfigurable = getConfigurationContext().get(TGPPServerPolicyConfigurable.class);
			
			if (policyConfigurable.getSelectedDriverIds().size() > 0) {
				baseQuery ="select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+ DriverTypes.DIAMETER_USERFILE_DRIVER.value +
						"' AND (DRIVERINSTANCEID IN ("
						+ Strings.join(",", policyConfigurable.getSelectedDriverIds(), Strings.WITHIN_SINGLE_QUOTES) + ") "
						+ " OR DRIVERINSTANCEID IN (select DISTINCT DRIVERINSTANCEID from tblmnaspolicyauthdriverrel where naspolicyid in (" + 
							getReadQueryForNasServicePolicyConfiguration()+ ")) OR DRIVERINSTANCEID IN" +
						 "(select DISTINCT DRIVERINSTANCEID from tblmeappolicyauthdriverrel where eappolicyid in (" + 
							getReadQueryForEapServicePolicyConfiguration()+ ")) OR DRIVERINSTANCEID IN" +
						 "(select DISTINCT DRIVERINSTANCEID from TBLMEAPPOLICYADDDRIVERREL  where eappolicyid in (" + 
							getReadQueryForEapServicePolicyConfiguration()+ ")) OR DRIVERINSTANCEID IN" +
						 "(select DISTINCT DRIVERINSTANCEID from TBLMNASADDAUTHDRIVERREL where naspolicyid in (" + 
							getReadQueryForNasServicePolicyConfiguration()+ ")))";
			} else {
				baseQuery = getQueryWithoutTGPPDrivers();
			}
		} else {
			baseQuery = getQueryWithoutTGPPDrivers();
		}
		
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
					
			List<DiameterUserFileDriverConfigurationImpl> userFileDriverConfigurationList = new ArrayList<DiameterUserFileDriverConfigurationImpl>();
			Map<String, UserFileDriverConfiguration> userFileDriverConfigurationMap = new LinkedHashMap<String, UserFileDriverConfiguration>();
			psForDriverInstanceId = connection.prepareStatement(baseQuery);			
			if(psForDriverInstanceId == null){
				throw new SQLException("Problem reading diameter file auth driver configuration, reason: prepared statement is null");

			}
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();
		
			while (rsForDriverInstanceId.next()) {		

				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");

				query = getQueryForUserFileAuthDriver();
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1,driverInstanceId);
				preparedStatement.setString(2,driverInstanceId);
				resultSet =  preparedStatement.executeQuery();

				if(resultSet.next()){

					DiameterUserFileDriverConfigurationImpl userFileDriverConfigurationImpl = new DiameterUserFileDriverConfigurationImpl();
					userFileDriverConfigurationImpl.setDriverInstanceId(driverInstanceId);
					String driverName = resultSet.getString("NAME");
					if(driverName!=null)
						userFileDriverConfigurationImpl.setDriverName(driverName);

					String strExpiryDatePatterns = resultSet.getString("EXPIRYDATEFORMATS"); // "," Separated values.
					if(strExpiryDatePatterns != null){
						userFileDriverConfigurationImpl.setStrExpiryPatterns(strExpiryDatePatterns);
					}else{
						LogManager.getLogger().warn(MODULE, "Expiry Date Pattern Parameter for Driver :"+driverName+" is not defined ,using default value");
					}

					if(resultSet.getString("FILELOCATIONS")!=null && resultSet.getString("FILELOCATIONS").trim().length()>0){
						userFileDriverConfigurationImpl.setUserFileLocation(resultSet.getString("FILELOCATIONS"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "File Locations Parameter for Driver :"+driverName+" is not defined ,using default value");
					}
					userFileDriverConfigurationList.add(userFileDriverConfigurationImpl);
					userFileDriverConfigurationMap.put(driverInstanceId, userFileDriverConfigurationImpl);
				}
			}
			this.diameterUserfileAuthDriverList = userFileDriverConfigurationList;
			this.userFileAuthDriverConfMap = userFileDriverConfigurationMap;
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(connection);
		}
	}
	
	private boolean isTGPPServerEnabled(AAAServerConfigurable serverConfigurable) {
		return serverConfigurable.getconfiguredServicesMap().get(AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID) !=null 
				&& serverConfigurable.getconfiguredServicesMap().get(AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID);
	}
	
	private String getQueryWithoutTGPPDrivers() {
		String baseQuery ="select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+ DriverTypes.DIAMETER_USERFILE_DRIVER.value +"' AND (DRIVERINSTANCEID IN" +
			 "(select DISTINCT DRIVERINSTANCEID from tblmnaspolicyauthdriverrel where naspolicyid in (" + 
				getReadQueryForNasServicePolicyConfiguration()+ ")) OR DRIVERINSTANCEID IN" +
			 "(select DISTINCT DRIVERINSTANCEID from tblmeappolicyauthdriverrel where eappolicyid in (" + 
				getReadQueryForEapServicePolicyConfiguration()+ ")) OR DRIVERINSTANCEID IN" +
			 "(select DISTINCT DRIVERINSTANCEID from TBLMEAPPOLICYADDDRIVERREL  where eappolicyid in (" + 
				getReadQueryForEapServicePolicyConfiguration()+ ")) OR DRIVERINSTANCEID IN" +
			 "(select DISTINCT DRIVERINSTANCEID from TBLMNASADDAUTHDRIVERREL where naspolicyid in (" + 
				getReadQueryForNasServicePolicyConfiguration()+ ")))";
		return baseQuery;
	}

	@DBReload
	public void reloadDiamterUserfileAuthDriverConfiguration() throws Exception{

		int size = this.diameterUserfileAuthDriverList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select DRIVERINSTANCEID from TBLMDRIVERINSTANCE where DRIVERINSTANCEID IN (");

		for(int i = 0; i < size-1; i++){
			DiameterUserFileDriverConfigurationImpl diameterUserFileDriverConfigurationImpl = this.diameterUserfileAuthDriverList.get(i);
			queryBuilder.append("'" + diameterUserFileDriverConfigurationImpl.getDriverInstanceId() + "',");
		}
		queryBuilder.append("'" + this.diameterUserfileAuthDriverList.get(size - 1).getDriverInstanceId() + "')");
		String queryForReload = queryBuilder.toString();

		Connection conn = null;
		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId = null;

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		


		try{
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			psForDriverInstanceId = conn.prepareStatement(queryForReload);
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while(rsForDriverInstanceId.next()){
				DiameterUserFileDriverConfigurationImpl userFileDriverConfigurationImpl = (DiameterUserFileDriverConfigurationImpl) userFileAuthDriverConfMap.get(rsForDriverInstanceId.getInt("DRIVERINSTANCEID"));

				String driverInstanceId = userFileDriverConfigurationImpl.getDriverInstanceId();

				String query = getQueryReloadingForUserFileAuthDriver();
				preparedStatement = conn.prepareStatement(query);
				preparedStatement.setString(1,driverInstanceId);
				resultSet =  preparedStatement.executeQuery();

				if(resultSet.next()){
					
					String driverName  = userFileDriverConfigurationImpl.getDriverName();

					String strExpiryDatePatterns = resultSet.getString("EXPIRYDATEFORMATS"); // "," Separated values.
					if(strExpiryDatePatterns != null){
						userFileDriverConfigurationImpl.setStrExpiryPatterns(strExpiryDatePatterns);
					}else{
						LogManager.getLogger().warn(MODULE, "Expiry Date Pattern Parameter for Driver :"+driverName+" is not defined ,using default value");
					}

					if(resultSet.getString("FILELOCATIONS")!=null && resultSet.getString("FILELOCATIONS").trim().length()>0){
						userFileDriverConfigurationImpl.setUserFileLocation(resultSet.getString("FILELOCATIONS"));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "File Locations Parameter for Driver :"+driverName+" is not defined ,using default value");
					}
					
				}
			}
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(conn);
		}

	
	}
	
	@PostRead
	public void postReadProcessing() {
		if(diameterUserfileAuthDriverList != null && diameterUserfileAuthDriverList.size() > 0){
			int size = this.diameterUserfileAuthDriverList.size();
			DiameterUserFileDriverConfigurationImpl diameterUserFileDriverConfigurationImpl;
			for(int j=0;j<size;j++){
				diameterUserFileDriverConfigurationImpl = diameterUserfileAuthDriverList.get(j);
				
				postProcessingForUserfileLocation(diameterUserFileDriverConfigurationImpl);
				
				postProcessingForExpiryDatePattern(diameterUserFileDriverConfigurationImpl);
			}
		}
	}
	
	private void postProcessingForExpiryDatePattern(
			DiameterUserFileDriverConfigurationImpl diameterUserFileDriverConfigurationImpl) {
		String strExpiryDatePattern = diameterUserFileDriverConfigurationImpl.getStrExpiryPatterns();
		String[] arrExpiryDatePatterns =null;
		
		if(strExpiryDatePattern != null && strExpiryDatePattern.trim().length() > 0){
			arrExpiryDatePatterns = strExpiryDatePattern.split(",");
		}if(arrExpiryDatePatterns != null && arrExpiryDatePatterns.length > 0){
			SimpleDateFormat[] tempSimpleDateFormats = new SimpleDateFormat[arrExpiryDatePatterns.length];
			for(int i=0;i<arrExpiryDatePatterns.length;i++){
				tempSimpleDateFormats[i] = new SimpleDateFormat(arrExpiryDatePatterns[i]);
				tempSimpleDateFormats[i].setLenient(false);
			}
			diameterUserFileDriverConfigurationImpl.setExpiryPatterns(tempSimpleDateFormats);
		}else {
			SimpleDateFormat[] tempSimpleDateFormats = new SimpleDateFormat[1];
			tempSimpleDateFormats[0] =new SimpleDateFormat("MM/dd/yyyy");
			diameterUserFileDriverConfigurationImpl.setExpiryPatterns(tempSimpleDateFormats);
		}
		
	}

	private void postProcessingForUserfileLocation(DiameterUserFileDriverConfigurationImpl diameterUserFileDriverConfigurationImpl) {
		String location = diameterUserFileDriverConfigurationImpl.getUserFileLocation();
		if(location==null || !(location.trim().length()>0)){
			location="data/userfiles";
		}
		diameterUserFileDriverConfigurationImpl.setLocations(location.split(","));
	}

	private String getQueryForUserFileAuthDriver(){
		return "SELECT A.*,B.NAME from TBLMUSERFILEAUTHDRIVER A ,TBLMDRIVERINSTANCE B where A.DRIVERINSTANCEID=? AND B.DRIVERINSTANCEID=?";
	}
	private String getQueryReloadingForUserFileAuthDriver(){
		return "SELECT * from TBLMUSERFILEAUTHDRIVER where DRIVERINSTANCEID=?";
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		postReadProcessing();
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
