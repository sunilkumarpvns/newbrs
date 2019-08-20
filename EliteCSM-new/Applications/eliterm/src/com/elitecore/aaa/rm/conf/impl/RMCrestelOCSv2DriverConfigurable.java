package com.elitecore.aaa.rm.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.config.JNDIProprtyDetail;
import com.elitecore.aaa.core.config.JNDIProprtyDetails;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.rm.drivers.conf.impl.RMCrestelOCSv2DriverConfImpl;
import com.elitecore.aaa.util.constants.AAAServerConstants;
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
import com.elitecore.core.commons.util.ConfigurationUtil;
import com.elitecore.commons.base.DBUtility;

@XmlType(propOrder = {})
@XmlRootElement(name = "rm-crestel-ocsv2-drivers")
@ConfigurationProperties(moduleName ="RM-OCSv2-DRV-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "rm-crestel-ocsv2-drivers", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","charging","driver"})
public class RMCrestelOCSv2DriverConfigurable  extends Configurable {

	
	private final String MODULE = "RM-OCSv2-DRV-CONFIGURABLE";
	public static final String RESPONSE_TIME_CALC_REQUIRED = "response.time.calc.required";

	
	private List<RMCrestelOCSv2DriverConfImpl> rmCrestelOCSv2DriverConfList;

	public RMCrestelOCSv2DriverConfigurable(){
		rmCrestelOCSv2DriverConfList = new ArrayList<RMCrestelOCSv2DriverConfImpl>();
	}
	@DBRead
	public void readFromDB() throws Exception {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Reading configuration for driver "+ DriverTypes.getDriverTypeStr(getDriverTypeId())+"configuration from database");
		}

		Connection connection = null;
		PreparedStatement pstmtJndiProperties=null;
		ResultSet rsJndiProperties =null;
		
		PreparedStatement pstmtTranslationMappingConfName=null;
		ResultSet rsTranslationMappingConfName=null;
		String query;
		
		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;

		try{
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			query = "select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+DriverTypes.RM_CRESTEL_OCSV2_DRIVER.value+"'";

			List<RMCrestelOCSv2DriverConfImpl> tempCrestelOCSv2DriverConfList = new ArrayList<RMCrestelOCSv2DriverConfImpl>();
			psForDriverInstanceId = connection.prepareStatement(query);

			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {		
				
				RMCrestelOCSv2DriverConfImpl rmCrestelOCSv2ConfImpl = new RMCrestelOCSv2DriverConfImpl();
				
				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");
				rmCrestelOCSv2ConfImpl.setDriverInstanceId(driverInstanceId);
				pstmtJndiProperties =connection.prepareStatement(getJndiPropertiesQuery());
				pstmtTranslationMappingConfName=connection.prepareStatement(getTranslationMappingNameAndDriverNameQuery());

				pstmtJndiProperties.setString(1, driverInstanceId);
				rsJndiProperties=pstmtJndiProperties.executeQuery();
				
				
				List<JNDIProprtyDetail> jndiProprtyDetailList = new ArrayList<JNDIProprtyDetail>();
				
				while(rsJndiProperties.next()){
					JNDIProprtyDetail jndiProprty = new JNDIProprtyDetail();
					
					jndiProprty.setProperty(rsJndiProperties.getString("NAME"));
					jndiProprty.setValue(rsJndiProperties.getString("VALUE"));
					
					jndiProprtyDetailList.add(jndiProprty);
				}
				
				JNDIProprtyDetails jndiProprtyDetails = new JNDIProprtyDetails();
				jndiProprtyDetails.setJndiProprtyDetailList(jndiProprtyDetailList);
				rmCrestelOCSv2ConfImpl.setJndiProprtyDetails(jndiProprtyDetails);
				
				pstmtTranslationMappingConfName.setString(1, driverInstanceId);
				rsTranslationMappingConfName=pstmtTranslationMappingConfName.executeQuery();
				while(rsTranslationMappingConfName.next()){
					String translationMappingConfName=rsTranslationMappingConfName.getString("CONF_NAME");
					rmCrestelOCSv2ConfImpl.setTranslationMappingName(translationMappingConfName);
					String driverName=rsTranslationMappingConfName.getString("DRIVER_NAME");
					rmCrestelOCSv2ConfImpl.setDriverName(driverName);
				}
				tempCrestelOCSv2DriverConfList.add(rmCrestelOCSv2ConfImpl);
			}
			this.rmCrestelOCSv2DriverConfList = tempCrestelOCSv2DriverConfList;
		}finally{
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(rsTranslationMappingConfName);
			DBUtility.closeQuietly(rsJndiProperties);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(pstmtTranslationMappingConfName);
			DBUtility.closeQuietly(pstmtJndiProperties);
			DBUtility.closeQuietly(connection);
		}

		
	}

	@PostRead
	public void postReadProcessing() {
		if(this.rmCrestelOCSv2DriverConfList!=null){
			int numOfDrivers = rmCrestelOCSv2DriverConfList.size();
			RMCrestelOCSv2DriverConfImpl rmOCSv2DriverConfImpl;
			for(int i=0;i<numOfDrivers;i++){
				rmOCSv2DriverConfImpl = rmCrestelOCSv2DriverConfList.get(i);
				JNDIProprtyDetails jndiProprtyDetails = rmOCSv2DriverConfImpl.getJndiProprtyDetails();
				if(jndiProprtyDetails!=null){
					Hashtable<String,String> jndiPropertyMap = new Hashtable<String, String>();
					List<JNDIProprtyDetail> propertyList = jndiProprtyDetails.getJndiProprtyDetailList();
					if(propertyList!=null){
						int numOfProperty = propertyList.size();
						for(int j=0;j<numOfProperty;j++){
							JNDIProprtyDetail jndiProprtyDetail = propertyList.get(j); 
							jndiPropertyMap.put(jndiProprtyDetail.getProperty(),jndiProprtyDetail.getValue());
						}
					}
					boolean tempBCalculateResponseTime = true;
					String timeCalucationRequiredStr = jndiPropertyMap.get(RESPONSE_TIME_CALC_REQUIRED);
					if(timeCalucationRequiredStr==null){
						jndiPropertyMap.put(RESPONSE_TIME_CALC_REQUIRED, String.valueOf(tempBCalculateResponseTime));
					}else {
						jndiPropertyMap.put(RESPONSE_TIME_CALC_REQUIRED, String.valueOf(ConfigurationUtil.stringToBoolean(timeCalucationRequiredStr, tempBCalculateResponseTime)));
					}
					rmOCSv2DriverConfImpl.setJndiPropertyMap(jndiPropertyMap);
				}
			}
		}
		
	}

	@XmlElement(name ="rm-crestel-ocsv2-driver")
	public List<RMCrestelOCSv2DriverConfImpl> getDriverList() {
		return this.rmCrestelOCSv2DriverConfList;
	}
	
	public void setDriverList(List<RMCrestelOCSv2DriverConfImpl> rmCrestelOCSv2DriverConfList) {
		this.rmCrestelOCSv2DriverConfList = rmCrestelOCSv2DriverConfList;
	}
	

	
	private String getJndiPropertiesQuery() {
		return " select a.name,a.value from tblmchargingdriverprops a, tblmcrestelchargingdriver b where a.crestelchargingdriverid= b.crestelchargingdriverid and b.driverinstanceid = ?";
	}

	
	private String getTranslationMappingNameAndDriverNameQuery() {
		return " select a.name CONF_NAME,c.name DRIVER_NAME,INSTANCENUMBER from tblmtranslationmappingconf a,tblmcrestelchargingdriver b,tblmdriverinstance c where a.transmapconfid=b.transmapconfid and b.driverinstanceid = c.driverinstanceid and b.driverinstanceid = ? ";
	}
	
	@XmlTransient
	private int getDriverTypeId() {
		return DriverTypes.RM_CRESTEL_OCSV2_DRIVER.value;
	}

	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		
	}
	@DBReload
	public void reloadFromDB() throws SQLException {
		
	}
	
	private String getReadQueryForChargingServicePolicyConfiguration() {
		
		RMChargingServiceConfigurable rmChargingServiceConfigurable = getConfigurationContext().get(RMChargingServiceConfigurable.class);
		if(rmChargingServiceConfigurable == null) {
			return "''";
}
		List<String> servicePolicies = rmChargingServiceConfigurable.getServicePolicies();
		if(servicePolicies == null || servicePolicies.size() == 0 || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select policyid from tblmcgpolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select policyid from tblmcgpolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + 
				"' AND NAME IN ("+ConfigurationUtil.getStrFromStringArrayList(servicePolicies)+")";

	}
}
