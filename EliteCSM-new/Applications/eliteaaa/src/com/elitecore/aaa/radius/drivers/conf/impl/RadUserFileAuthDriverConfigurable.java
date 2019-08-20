package com.elitecore.aaa.radius.drivers.conf.impl;

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
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.radius.drivers.DriverConfigurable;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyConfigurable;
import com.elitecore.commons.base.DBUtility;
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
@XmlRootElement(name = "userfile-auth-drivers")
@ConfigurationProperties(moduleName ="RAD-UFADC-CNFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "userfile-auth-drivers", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","auth","driver"})
public class RadUserFileAuthDriverConfigurable extends Configurable implements DriverConfigurable {

	public static final String DRIVER_INSTANCE_ID = "instance-id";
	private final String MODULE = "RAD-UFADC-CNFIGURABLE";

	private List<RadUserFileAuthDriverConfigurationImpl> radUserfileAuthDriverList;
	@XmlTransient
	private Map<String,UserFileDriverConfiguration> userFileAuthDriverConfMap;

	public RadUserFileAuthDriverConfigurable(){
		radUserfileAuthDriverList = new ArrayList<RadUserFileAuthDriverConfigurationImpl>();
		this.userFileAuthDriverConfMap = new LinkedHashMap<String, UserFileDriverConfiguration>();
	}                                                                                
	
	@Override
	@XmlElement(name ="userfile-auth-driver")
	public List<RadUserFileAuthDriverConfigurationImpl> getDriverConfigurationList() {
		return radUserfileAuthDriverList;
	}

	public void setRadUserfileAuthDriverList(List<RadUserFileAuthDriverConfigurationImpl> radUserfileAuthDriverList) {
		this.radUserfileAuthDriverList = radUserfileAuthDriverList;
	}

	@DBRead
	public void readFromDB() throws Exception {

		Connection connection = null;
		String query = "";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		

		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;

		RadiusServicePolicyConfigurable servicePolicyConfigurable = ((AAAConfigurationContext)getConfigurationContext()).get(RadiusServicePolicyConfigurable.class);
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String querya = new DriverSelectQueryBuilder(DriverTypes.RAD_USERFILE_AUTH_DRIVER, servicePolicyConfigurable.getSelectedDriverIds()).build();	

			List<RadUserFileAuthDriverConfigurationImpl> userFileDriverConfigurationList = new ArrayList<RadUserFileAuthDriverConfigurationImpl>();
			Map<String, UserFileDriverConfiguration> userFileDriverConfigurationMap = new LinkedHashMap<String, UserFileDriverConfiguration>();
			psForDriverInstanceId = connection.prepareStatement(querya);		
			if(psForDriverInstanceId == null){
				throw new SQLException("Problem reading rad file auth driver configuration, reason: prepared statement is null");

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

					RadUserFileAuthDriverConfigurationImpl userFileDriverConfigurationImpl = new RadUserFileAuthDriverConfigurationImpl();
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
			this.radUserfileAuthDriverList = userFileDriverConfigurationList;
			this.userFileAuthDriverConfMap = userFileDriverConfigurationMap;
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@DBReload
	public void reloadUserfileConfiguration() throws Exception {

		int size = this.radUserfileAuthDriverList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select DRIVERINSTANCEID from TBLMDRIVERINSTANCE where DRIVERINSTANCEID IN (");

		for(int i = 0; i < size-1; i++){
			RadUserFileAuthDriverConfigurationImpl radUserFileAuthDriverConfigurationImpl = this.radUserfileAuthDriverList.get(i);
			queryBuilder.append("'" + radUserFileAuthDriverConfigurationImpl.getDriverInstanceId() + "',");
		}
		queryBuilder.append("'" + radUserfileAuthDriverList.get(size - 1).getDriverInstanceId() + "')");
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
				RadUserFileAuthDriverConfigurationImpl userFileDriverConfigurationImpl = (RadUserFileAuthDriverConfigurationImpl) userFileAuthDriverConfMap.get(rsForDriverInstanceId.getInt("DRIVERINSTANCEID"));

				String driverInstanceId = userFileDriverConfigurationImpl.getDriverInstanceId();

				String query = getQueryForReloadingUserFileAuthDriver();
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

	private String getQueryForReloadingUserFileAuthDriver() {
		return "SELECT * from TBLMUSERFILEAUTHDRIVER where DRIVERINSTANCEID=?";
	}
	@PostRead
	public void postReadProcessing() {
		if(radUserfileAuthDriverList != null && radUserfileAuthDriverList.size() > 0){
			int size = this.radUserfileAuthDriverList.size();
			RadUserFileAuthDriverConfigurationImpl radUserFileAuthDriverConfigurationImpl;
			for(int j=0;j<size;j++){
				radUserFileAuthDriverConfigurationImpl = radUserfileAuthDriverList.get(j);
				postProcessingOfUserFileLocation(radUserFileAuthDriverConfigurationImpl);
				postProcessingOfExpiryDatePattern(radUserFileAuthDriverConfigurationImpl);
			}
		}

	}
	private void postProcessingOfExpiryDatePattern(
			RadUserFileAuthDriverConfigurationImpl radUserFileAuthDriverConfigurationImpl) {
		String strExpiryDatePattern = radUserFileAuthDriverConfigurationImpl.getStrExpiryPatterns();
		String[] arrExpiryDatePatterns =null;
		
		if(strExpiryDatePattern != null && strExpiryDatePattern.trim().length() > 0){
			arrExpiryDatePatterns = strExpiryDatePattern.split(",");
		}if(arrExpiryDatePatterns != null && arrExpiryDatePatterns.length > 0){
			SimpleDateFormat[] tempSimpleDateFormats = new SimpleDateFormat[arrExpiryDatePatterns.length];
			for(int i=0;i<arrExpiryDatePatterns.length;i++){
				tempSimpleDateFormats[i] = new SimpleDateFormat(arrExpiryDatePatterns[i]);
				tempSimpleDateFormats[i].setLenient(false);
			}
			radUserFileAuthDriverConfigurationImpl.setExpiryPatterns(tempSimpleDateFormats);
		}else {
			SimpleDateFormat[] tempSimpleDateFormats = new SimpleDateFormat[1];
			tempSimpleDateFormats[0] =new SimpleDateFormat("MM/dd/yyyy");
			radUserFileAuthDriverConfigurationImpl.setExpiryPatterns(tempSimpleDateFormats);
		}
	}
	private void postProcessingOfUserFileLocation(
			RadUserFileAuthDriverConfigurationImpl radUserFileAuthDriverConfigurationImpl) {
		String location = radUserFileAuthDriverConfigurationImpl.getUserFileLocation();
		if(location==null || !(location.trim().length()>0)){
			location="data/userfiles";
		}
		radUserFileAuthDriverConfigurationImpl.setLocations(location.split(","));
	}
	
	private String getQueryForUserFileAuthDriver() throws SQLException{

		return "SELECT A.*,B.NAME from TBLMUSERFILEAUTHDRIVER A ,TBLMDRIVERINSTANCE B where A.DRIVERINSTANCEID=? AND B.DRIVERINSTANCEID=?";
	}

	
	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		postReadProcessing();
	}
}
