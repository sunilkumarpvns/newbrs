package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.HSSAuthDriverConfiguration;
import com.elitecore.aaa.core.conf.impl.AAAServerConfigurable;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMappingImpl;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.conf.impl.DiameterEAPServiceConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterNasServiceConfigurable;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.TGPPServerPolicyConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
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
import com.elitecore.diameterapi.diameter.common.data.impl.PeerInfoImpl;

@XmlType(propOrder = {})
@XmlRootElement(name = "dia-hss-auth-drivers")
@ConfigurationProperties(moduleName = "DIA-HSSADC-CONFIGURABLE", readWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "dia-hss-auth-drivers",schemaDirectories = {"system","schema"},configDirectories = {"conf","db","diameter","driver"})
public class DiameterHSSAuthDriverConfigurable extends Configurable{

	private final String MODULE = "DIA-HSSADC-CONFIGURABLE";
	private final static String FIELDMAP_TABLE = "TBLMHSSAUTHDRIVERFIELDMAP";
	/* Fields of TBLMHSSAUTHDRIVERFIELDMAP*/
	private final static String DBFIELD_LOGICALNAME = "LOGICALNAME";
	private final static String DBFIELD_FIELD = "APPLICATIONIDS";
	private final static String DEFAULT_VALUE = "DEFAULTVALUE";
	private final static String VALUE_MAPPING = "VALUEMAPPING";

	private List<DiameterHSSAuthDriverConfImpl> hssAuthdriverDetailsList =  new ArrayList<DiameterHSSAuthDriverConfImpl>();
	
	public DiameterHSSAuthDriverConfigurable(){
		hssAuthdriverDetailsList =  new ArrayList<DiameterHSSAuthDriverConfImpl>();
	}

	@XmlElement(name = "hss-auth-driver")
	public List<DiameterHSSAuthDriverConfImpl> getDiaHSSAuthDriverList() {
		return hssAuthdriverDetailsList;
	}

	public void setAuthdriverDetails(List<DiameterHSSAuthDriverConfImpl> authdriverDetails) {
		this.hssAuthdriverDetailsList = authdriverDetails;
	}
	
	@DBRead
	public void readFromDB() throws Exception {
		
		Connection connection = null;
		String query = "";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		
		ResultSet resultSetForFieldMapping = null;
		PreparedStatement psForFieldMapping = null;
		
		PreparedStatement psForPeers = null;
		ResultSet rsForPeers = null;

		PreparedStatement psForDriverInstanceId = null;
		ResultSet rsForDriverInstanceId =  null;

		String baseQuery;
		
		AAAServerConfigurable serverConfigurable = getConfigurationContext().get(AAAServerConfigurable.class);
		if (isTGPPServerEnabled(serverConfigurable)) {
			TGPPServerPolicyConfigurable policyConfigurable = getConfigurationContext().get(TGPPServerPolicyConfigurable.class);
			
			if (policyConfigurable.getSelectedDriverIds().size() > 0) {
				baseQuery ="select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+ DriverTypes.DIA_HSS_AUTH_DRIVER.value +
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
					
			List<DiameterHSSAuthDriverConfImpl> driverConfigurationList = new ArrayList<DiameterHSSAuthDriverConfImpl>();
			Map<String, HSSAuthDriverConfiguration> driverConfigurationMap = new LinkedHashMap<String, HSSAuthDriverConfiguration>();
			
			psForDriverInstanceId = connection.prepareStatement(baseQuery);		
			if(psForDriverInstanceId == null){
				throw new SQLException("Problem reading diameter hss auth driver configuration, reason: prepared Statement is null");
			}
			rsForDriverInstanceId = psForDriverInstanceId.executeQuery();

			while (rsForDriverInstanceId.next()) {		
				
				String driverInstanceId = rsForDriverInstanceId.getString("DRIVERINSTANCEID");

				
				/************************************************************************
				 * getting Primary details for configuration of tablename and db properties.
				 *************************************************************************/

				query = getQueryForDiaHSSAuthDriver();			
				preparedStatement = connection.prepareStatement(query);
				if(preparedStatement == null){
						throw new SQLException("Problem reading diameter hss auth driver configuration, reason: prepared statement received is NULL");
				}
				preparedStatement.setString(1,driverInstanceId);
				preparedStatement.setString(2,driverInstanceId);

				resultSet =  preparedStatement.executeQuery();

				if(resultSet.next()){
					
					DiameterHSSAuthDriverConfImpl hssAuthDriverDetails = new DiameterHSSAuthDriverConfImpl();
					hssAuthDriverDetails.setDriverInstanceId(driverInstanceId);
					//hssAuthDriverId
					String hssAuthDriverId = resultSet.getString("HSSAUTHDRIVERID");
					
					//Name
					String driverName = resultSet.getString("DRIVERNAME");
					if(driverName!=null)
						hssAuthDriverDetails.setDriverName(driverName);

					//User Id Attribs
					if (resultSet.getString("USERIDENTITYATTRIBUTES") != null && resultSet.getString("USERIDENTITYATTRIBUTES").trim().length() > 0){
						hssAuthDriverDetails.setUserIdentity(resultSet.getString("USERIDENTITYATTRIBUTES"));
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "User Identity not configured for Driver: "+driverName);
					} 
					
					//Additional Attributes
					hssAuthDriverDetails.setAdditionalAttributes(resultSet.getString("ADDITIONALATTRIBUTES"));
					
					//ApplicationId
					if(resultSet.getString("APPLICATIONID") != null && resultSet.getString("APPLICATIONID").trim().length() > 0){
						hssAuthDriverDetails.setApplicationId(resultSet.getString("APPLICATIONID"));
					}
					
					//Number of Triplets
					hssAuthDriverDetails.setNumberOfTriplets(resultSet.getInt("NOOFTRIPLETS"));
					
					//Request T/O
					hssAuthDriverDetails.setRequestTimeout(resultSet.getInt("REQUESTTIMEOUT"));
					
					//commandCode
					hssAuthDriverDetails.setCommandCode(resultSet.getInt("COMMANDCODE"));
					
					//peers
					psForPeers = connection.prepareStatement(getQueryForPeers());
					psForPeers.setString(1, hssAuthDriverId);
					rsForPeers = psForPeers.executeQuery();

					List<PeerInfoImpl> peerInfoList = new ArrayList<PeerInfoImpl>();
					while(rsForPeers.next()){
						PeerInfoImpl peerInfoImpl = new PeerInfoImpl();
						String peerName = rsForPeers.getString("PEERNAME");
						if(peerName != null && peerName.trim().length() > 0 ){
							peerInfoImpl.setPeerName(peerName);
							peerInfoImpl.setLoadFactor(rsForPeers.getInt("weightage"));
							peerInfoList.add(peerInfoImpl);
						}
					}
					hssAuthDriverDetails.setPeerList(peerInfoList);
					/**************************************************************************
					 * setting all the configured fields for accountData
					 *************************************************************************/

					query = getQueryForFieldMapping();
					psForFieldMapping = connection.prepareStatement(query);

					if(psForFieldMapping == null){
							throw new SQLException("Problem reading diameter hss auth driver field mapping configuration, reason: prepared statement received is NULL");
					}
					
				
					psForFieldMapping.setString(1, hssAuthDriverId);			

					resultSetForFieldMapping =  psForFieldMapping.executeQuery();

					StringWriter stringBuffer = new StringWriter();
					PrintWriter out = new PrintWriter(stringBuffer);
					out.println();
					AccountDataFieldMapping tempAccountDataFieldMapping = new AccountDataFieldMapping();
					List<DataFieldMappingImpl> fieldMappingList = new ArrayList<DataFieldMappingImpl>();
					while(resultSetForFieldMapping.next()){
						String defaultValue = resultSetForFieldMapping.getString(DEFAULT_VALUE);
						String valueMapping = resultSetForFieldMapping.getString(VALUE_MAPPING);
						String dbFieldLogicalName = resultSetForFieldMapping.getString(DBFIELD_LOGICALNAME);
						String dbField = resultSetForFieldMapping.getString(DBFIELD_FIELD);
						
						DataFieldMappingImpl dataFieldMappingImpl = new DataFieldMappingImpl(dbField, defaultValue, valueMapping, dbFieldLogicalName);
						fieldMappingList.add(dataFieldMappingImpl);
						out.println("        "+dbFieldLogicalName +" = " + dbField);
						out.println("            Default Value: "  + defaultValue);
						out.println("            Value Mapping: " + valueMapping);														
					}
					tempAccountDataFieldMapping.setFieldMappingList(fieldMappingList);
					hssAuthDriverDetails.setAccountDataFieldMapping(tempAccountDataFieldMapping);
					driverConfigurationList.add(hssAuthDriverDetails);	
					driverConfigurationMap.put(hssAuthDriverDetails.getDriverInstanceId(), hssAuthDriverDetails);
					out.close();
				}
			}
			this.hssAuthdriverDetailsList = driverConfigurationList;
		} finally{
			
			DBUtility.closeQuietly(resultSetForFieldMapping);
			DBUtility.closeQuietly(psForFieldMapping);
			DBUtility.closeQuietly(rsForPeers);
			DBUtility.closeQuietly(psForPeers);
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(rsForDriverInstanceId);
			DBUtility.closeQuietly(psForDriverInstanceId);
			DBUtility.closeQuietly(connection);
			
		}
				
	}

	private String getQueryWithoutTGPPDrivers() {
		String baseQuery ="select DRIVERINSTANCEID, DRIVERTYPEID from tblmdriverinstance where DRIVERTYPEID='"+ DriverTypes.DIA_HSS_AUTH_DRIVER.value +"' AND (DRIVERINSTANCEID IN" +
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

	private boolean isTGPPServerEnabled(AAAServerConfigurable serverConfigurable) {
		return serverConfigurable.getconfiguredServicesMap().get(AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID) !=null 
				&& serverConfigurable.getconfiguredServicesMap().get(AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID);
	}

	@DBReload
	public void reloadDiameterDBAuthDriverConfiguration() throws SQLException{
		
	}
	
	@PostRead
	public void postReadProcessing() {
		if(hssAuthdriverDetailsList != null && hssAuthdriverDetailsList.size() >0){
			
			int size = hssAuthdriverDetailsList.size();
			DiameterHSSAuthDriverConfImpl hssAuthDriverDetails;
			for(int i = 0;i<size;i++){
				hssAuthDriverDetails = hssAuthdriverDetailsList.get(i);
				
				postProcessingForUserIdentityAttributes(hssAuthDriverDetails);
				
				List<DataFieldMappingImpl> fieldMappingdList = hssAuthDriverDetails.getAccountDataFieldMapping().getFieldMappingList();
				Map<String, List<DataFieldMapping>> dataFieldMappingMap = new HashMap<String, List<DataFieldMapping>>();
				if(fieldMappingdList!=null){
					int numOfFieldMapping = fieldMappingdList.size();
					for(int j=0;j<numOfFieldMapping;j++){
						DataFieldMappingImpl dataFieldMapping = fieldMappingdList.get(j);
						String logicalName  = dataFieldMapping.getLogicalName();
						if(logicalName!=null){
							List<DataFieldMapping> list  = dataFieldMappingMap.get(logicalName);
							if(list==null){
								list = new ArrayList<DataFieldMapping>();
								dataFieldMappingMap.put(logicalName, list);
							}
							list.add(dataFieldMapping);
						}
					}
				}
				hssAuthDriverDetails.getAccountDataFieldMapping().setFieldMapping(dataFieldMappingMap);
			}
		}
	}
			
	private void postProcessingForUserIdentityAttributes(
			DiameterHSSAuthDriverConfImpl hssAuthDriverDetails) {
		String strUserIdentity = hssAuthDriverDetails.getUserIdentity();
		if(strUserIdentity!=null){
			String[] multipleUIds = strUserIdentity.split(",");
			List<String>userIdentityAttribute = new ArrayList<String>();
			for (String userId : multipleUIds){
				if (userId.trim().length() > 0)
					userIdentityAttribute.add(userId);
			}
			hssAuthDriverDetails.setUserIdentityAttributes(userIdentityAttribute);
		}
		
	}

	private String getQueryForDiaHSSAuthDriver(){
		return "SELECT A.*, C.NAME AS DRIVERNAME " +
				"FROM tblmhssauthdriver A, tblmdriverinstance C " +
				"WHERE A.driverinstanceid=? AND C.driverinstanceid=?";
	}
	
	private String getQueryForPeers(){
		return "select A.* from TBLMHSSPEERGROUPREL A  where A.hssauthdriverid = ?"; 
	}

	private String getQueryForFieldMapping(){
		return "SELECT * FROM " + FIELDMAP_TABLE +" WHERE HSSAUTHDRIVERID=?";
	}
			
	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
	
	}
	
	private String getReadQueryForNasServicePolicyConfiguration() {
		DiameterNasServiceConfigurable nasApplicationConfigurable = getConfigurationContext().get(DiameterNasServiceConfigurable.class);
		if(nasApplicationConfigurable == null) {
			return "''";
		}
		List<String> servicePolicies = nasApplicationConfigurable.getServicePolicies();
		if(Collectionz.isNullOrEmpty(servicePolicies) || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select naspolicyid from tblmnaspolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select naspolicyid from tblmnaspolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + 
				"' AND NAME IN ("+ Strings.join(",", servicePolicies , Strings.WITHIN_SINGLE_QUOTES) +")";

	}
	
	private String getReadQueryForEapServicePolicyConfiguration() {
		
		DiameterEAPServiceConfigurable eapApplicationConfigurable = getConfigurationContext().get(DiameterEAPServiceConfigurable.class);

		if(eapApplicationConfigurable == null){
			return "''";
		}
		List<String> servicePolicies = eapApplicationConfigurable.getServicePolicies();
		if(Collectionz.isNullOrEmpty(servicePolicies) || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select eappolicyid from tblmeappolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + "'";
		}
		return "select eappolicyid from tblmeappolicy where STATUS = '" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE + 
				"' AND NAME IN ("+ Strings.join(",", servicePolicies , Strings.WITHIN_SINGLE_QUOTES) + ") ";
	}
}

