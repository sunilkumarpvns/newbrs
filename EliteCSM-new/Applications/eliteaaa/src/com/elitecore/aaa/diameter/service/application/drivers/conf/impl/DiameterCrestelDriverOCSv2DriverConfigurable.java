package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
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
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.DiameterCCServiceConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
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
import com.elitecore.commons.base.Strings;

@XmlType(propOrder={})
@XmlRootElement(name = "crestel-ocs-v2-drivers")
@ConfigurationProperties(moduleName = "CRESTEL_OCSV2_DRIVER", readWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "crestel-ocs-v2-drivers",schemaDirectories = {"system","schema"},configDirectories = {"conf","db","diameter","driver"})
public class DiameterCrestelDriverOCSv2DriverConfigurable extends Configurable{

	private String MODULE="CRESTEL_OCSV2_DRIVER";
	public static final String RESPONSE_TIME_CALC_REQUIRED = "response.time.calc.required";
	
	private List<DiameterCrestelOCSv2DriverConfImpl> diameterCrestelDriverConfImplList ;
	
	public DiameterCrestelDriverOCSv2DriverConfigurable(){
		diameterCrestelDriverConfImplList = new  ArrayList<DiameterCrestelOCSv2DriverConfImpl>();
	}
	
	@XmlElement(name = "crestel-ocs-v2-driver")
	public List<DiameterCrestelOCSv2DriverConfImpl> getCrestelDriverConfImplList() {
		return diameterCrestelDriverConfImplList;
	}
	public void setDiameterCrestelDriverConfImplList(List<DiameterCrestelOCSv2DriverConfImpl> nasDetailLocalAcctConfigurationList) {
		this.diameterCrestelDriverConfImplList = nasDetailLocalAcctConfigurationList;
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
		
			query = "select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+DriverTypes.DIAMETER_CRESTEL_OCSv2_DRIVER.value+"' AND ( DRIVERINSTANCEID IN"+ 
					"(select DISTINCT DRIVERINSTANCEID from tblmccpolicydriverrel where policyid in (" + getReadQueryForCCServicePolicyConfiguration()+ ")))";
			
			List<DiameterCrestelOCSv2DriverConfImpl> tempCrestelOCSv2DriverConfList = new ArrayList<DiameterCrestelOCSv2DriverConfImpl>();
			psForDriverInstanceId = connection.prepareStatement(query);

			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {		
				
				DiameterCrestelOCSv2DriverConfImpl crestelOCSv2ConfImpl = new DiameterCrestelOCSv2DriverConfImpl();
				
				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");
				crestelOCSv2ConfImpl.setDriverInstanceId(driverInstanceId);
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
				crestelOCSv2ConfImpl.setJndiProprtyDetails(jndiProprtyDetails);
				
				pstmtTranslationMappingConfName.setString(1, driverInstanceId);
				rsTranslationMappingConfName=pstmtTranslationMappingConfName.executeQuery();
				while(rsTranslationMappingConfName.next()){
					String translationMappingConfName=rsTranslationMappingConfName.getString("CONF_NAME");
					crestelOCSv2ConfImpl.setTranslationMappingName(translationMappingConfName);
					String driverName=rsTranslationMappingConfName.getString("DRIVER_NAME");
					crestelOCSv2ConfImpl.setDriverName(driverName);
				}
				tempCrestelOCSv2DriverConfList.add(crestelOCSv2ConfImpl);
			}
			this.diameterCrestelDriverConfImplList = tempCrestelOCSv2DriverConfList;
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

	@DBReload
	public void reloadDiameterCrestelDriverConfiguration() {

	}
	
	@PostRead
	public void postReadProcessing() {
		if(this.diameterCrestelDriverConfImplList!=null){
			int numOfDrivers = diameterCrestelDriverConfImplList.size();
			DiameterCrestelOCSv2DriverConfImpl diameterOCSv2DriverConfImpl;
			for(int i=0;i<numOfDrivers;i++){
				diameterOCSv2DriverConfImpl = diameterCrestelDriverConfImplList.get(i);
				JNDIProprtyDetails jndiProprtyDetails = diameterOCSv2DriverConfImpl.getJndiProprtyDetails();
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
					diameterOCSv2DriverConfImpl.setJndiPropertyMap(jndiPropertyMap);
				}
			}
		}
		
	}

	public String getJndiPropertiesQuery() {
		return " select a.name,a.value from tblmratingdriverprops a, tblmcrestelratingdriver b where a.crestelratingdriverid= b.crestelratingdriverid and b.driverinstanceid = ?";
	}


	public String getTranslationMappingNameAndDriverNameQuery() {
		return " select a.name CONF_NAME,c.name DRIVER_NAME,INSTANCENUMBER from tblmtranslationmappingconf a,tblmcrestelratingdriver b,tblmdriverinstance c where a.transmapconfid=b.transmapconfid and b.driverinstanceid = c.driverinstanceid and b.driverinstanceid = ? "; 
	}
	@XmlTransient
	private int getDriverTypeId() {
		return DriverTypes.DIAMETER_CRESTEL_OCSv2_DRIVER.value;
	}
	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		
	}
	
	private String getReadQueryForCCServicePolicyConfiguration() {
		
		DiameterCCServiceConfigurable diameterCCServiceConfigurable = getConfigurationContext().get(DiameterCCServiceConfigurable.class);
		if(diameterCCServiceConfigurable == null) {
			return "''";
}
		List<String> servicePolicies =  diameterCCServiceConfigurable.getServicePolicies();
		if(servicePolicies == null || servicePolicies.size() == 0 || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select policyid from tblmccpolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select policyid from tblmccpolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + 
				"' AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+") ";

	}
}
