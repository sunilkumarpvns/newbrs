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
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.config.JNDIProprtyDetail;
import com.elitecore.aaa.core.config.JNDIProprtyDetails;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.rm.drivers.conf.impl.RMCrestelChargingDriverConfImpl;
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
@XmlRootElement(name = "rm-crestel-charging-drivers")
@ConfigurationProperties(moduleName ="RM-CRESTEL-DRV-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "rm-crestel-charging-drivers", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db","services","charging","driver"})
public class RMCrestelChargingDriverConfigurable extends Configurable {

	
	private final String MODULE = "RM-CRESTEL-DRV-CONFIGURABLE";
	public static final String RESPONSE_TIME_CALC_REQUIRED = "response.time.calc.required";

	
	private List<RMCrestelChargingDriverConfImpl> rmCrestelChargingDriverConfList;

	public RMCrestelChargingDriverConfigurable(){
		rmCrestelChargingDriverConfList = new ArrayList<RMCrestelChargingDriverConfImpl>();
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
			query = "select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+DriverTypes.RM_CRESTEL_CHARGING_DRIVER.value+"'";

			List<RMCrestelChargingDriverConfImpl> tempCrestelChargingDriverConfList = new ArrayList<RMCrestelChargingDriverConfImpl>();
			psForDriverInstanceId = connection.prepareStatement(query);

			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {		
				
				RMCrestelChargingDriverConfImpl rmCrestelChargingDriverConfImpl = new RMCrestelChargingDriverConfImpl();
				
				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");
				rmCrestelChargingDriverConfImpl.setDriverInstanceId(driverInstanceId);
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
				rmCrestelChargingDriverConfImpl.setJndiProprtyDetails(jndiProprtyDetails);
				
				pstmtTranslationMappingConfName.setString(1, driverInstanceId);
				rsTranslationMappingConfName=pstmtTranslationMappingConfName.executeQuery();
				while(rsTranslationMappingConfName.next()){
					String translationMappingConfName=rsTranslationMappingConfName.getString("CONF_NAME");
					rmCrestelChargingDriverConfImpl.setTranslationMappingName(translationMappingConfName);
					String driverName=rsTranslationMappingConfName.getString("DRIVER_NAME");
					rmCrestelChargingDriverConfImpl.setDriverName(driverName);
				}
				tempCrestelChargingDriverConfList.add(rmCrestelChargingDriverConfImpl);
			}
			this.rmCrestelChargingDriverConfList = tempCrestelChargingDriverConfList;
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
		if(this.rmCrestelChargingDriverConfList!=null){
			int numOfDrivers = rmCrestelChargingDriverConfList.size();
			RMCrestelChargingDriverConfImpl rmCrestelChargingDriverConfImpl;
			for(int i=0;i<numOfDrivers;i++){
				rmCrestelChargingDriverConfImpl = rmCrestelChargingDriverConfList.get(i);
				JNDIProprtyDetails jndiProprtyDetails = rmCrestelChargingDriverConfImpl.getJndiProprtyDetails();
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
					rmCrestelChargingDriverConfImpl.setJndiPropertyMap(jndiPropertyMap);
				}
			}
		}
		
	}

	@XmlElement(name ="rm-crestel-charging-driver")
	public List<RMCrestelChargingDriverConfImpl> getDriverList() {
		return this.rmCrestelChargingDriverConfList;
	}
	
	public void setDriverList(List<RMCrestelChargingDriverConfImpl> rmCrestelChargingDriverConfList) {
		this.rmCrestelChargingDriverConfList = rmCrestelChargingDriverConfList;
	}
	

	private String getJndiPropertiesQuery() {
		return " select a.name,a.value from tblmchargingdriverprops a, tblmcrestelchargingdriver b where a.crestelchargingdriverid= b.crestelchargingdriverid and b.driverinstanceid = ?";
	}

	private String getTranslationMappingNameAndDriverNameQuery() {
		return " select a.name CONF_NAME,c.name DRIVER_NAME,INSTANCENUMBER from tblmtranslationmappingconf a,tblmcrestelchargingdriver b,tblmdriverinstance c where a.transmapconfid=b.transmapconfid and b.driverinstanceid = c.driverinstanceid and b.driverinstanceid = ? ";
	}
	
	public int getDriverTypeId() {
		return DriverTypes.RM_CRESTEL_CHARGING_DRIVER.value;
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
