package com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.data.AdditionalResponseAttributes;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
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
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.plugins.data.PluginMode;
import com.elitecore.core.commons.plugins.data.ServiceTypeConstants;
import com.elitecore.core.serverx.policies.ParserUtility;

@XmlType(propOrder = {})
@XmlRootElement(name = "cc-policies")
@ConfigurationProperties(moduleName ="CC-POLICY-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "cc-policy", schemaDirectories = {"system","schema"}, configDirectories = {"conf","diameter"})
public class CCPolicyConfigurable extends Configurable{
	
	private static final String MODULE="CC-POLICY-CONFIGURABLE";

	private List<DiameterServicePolicyConfiguration> policyList;
	private Map<String, DiameterServicePolicyConfiguration> ccPolicyConfigurationMap;
	
	
	public CCPolicyConfigurable() {
		this.policyList = new ArrayList<DiameterServicePolicyConfiguration>();
		this.ccPolicyConfigurationMap = new LinkedHashMap<String, DiameterServicePolicyConfiguration>();
	}

	@DBRead
	public void readFromDB() throws Exception {

		Connection conn = null;
		PreparedStatement psForPolicy =null;
		ResultSet rsForPolicy = null;

		PreparedStatement psForResponseAttribute = null;
		ResultSet rsForResponseAttribute = null;
	
		try {
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			List<DiameterServicePolicyConfiguration> tempCCPolicyList = new ArrayList<DiameterServicePolicyConfiguration>();
			Map<String,DiameterServicePolicyConfiguration> tempCCPolicyMap =  new LinkedHashMap<String,DiameterServicePolicyConfiguration>();

			String query = getQueryForServicePolicyConfig();
			psForPolicy = conn.prepareStatement(query);
			psForPolicy.setString(1,CommonConstants.DATABASE_POLICY_STATUS_ACTIVE );			
			rsForPolicy=psForPolicy.executeQuery();
			CcServicePolicyConfigurationData ccPolicyConfigurationData;				

			while(rsForPolicy.next()){
				
				ccPolicyConfigurationData = new CcServicePolicyConfigurationData();
				
				String policyId = rsForPolicy.getString("POLICYID"); 
				
				ccPolicyConfigurationData.setName(rsForPolicy.getString("NAME"));
				ccPolicyConfigurationData.setId(policyId);
				ccPolicyConfigurationData.setRuleSet(rsForPolicy.getString("RULESET"));
				ccPolicyConfigurationData.setDriverScript(rsForPolicy.getString("DRIVERSCRIPT"));
				
				ccPolicyConfigurationData.setResponseBehaviorType(DefaultResponseBehaviorType.valueOf(rsForPolicy.getString("DEFAULTRESPONSEBEHAVIORTYPE").toUpperCase()));
				ccPolicyConfigurationData.setResponseBehaviorParameter(rsForPolicy.getString("DEFAULTRESPONSEBEHAVIORPARAM"));
			
				psForResponseAttribute = conn.prepareStatement(getQueryForResponseAttributes());
				psForResponseAttribute.setString(1, policyId);
				rsForResponseAttribute = psForResponseAttribute.executeQuery();
				
				ArrayList<CommandCodeResponseAttribute> commandCodeResponseAttributes = new ArrayList<CommandCodeResponseAttribute>();
				CommandCodeResponseAttribute commandCodeResponseAttribute = null;
				while (rsForResponseAttribute.next()) {					
					commandCodeResponseAttribute = new CommandCodeResponseAttribute();
					commandCodeResponseAttribute.setCommandCodes(rsForResponseAttribute.getString("COMMANDCODES"));
					commandCodeResponseAttribute.setResponseAttributes(rsForResponseAttribute.getString("RESPONSEATTRIBUTES"));
					commandCodeResponseAttributes.add(commandCodeResponseAttribute);
				}
				ccPolicyConfigurationData.setCommandCodeResponseAttributesList(commandCodeResponseAttributes);
				
				PreparedStatement preparedStatementForDriver = null;
				ResultSet rSetForDriver = null;
				try{
					query = "SELECT driverinstanceid ,weightage FROM tblmccpolicydriverrel WHERE policyid=?";
					preparedStatementForDriver = conn.prepareStatement(query);
					preparedStatementForDriver.setString(1, policyId);
					rSetForDriver = preparedStatementForDriver.executeQuery();			
					List<PrimaryDriverDetail> driverList = new ArrayList<PrimaryDriverDetail>();
					while (rSetForDriver.next()) {
						PrimaryDriverDetail primaryDriverDetail = new PrimaryDriverDetail();
						primaryDriverDetail.setDriverInstanceId(rSetForDriver.getString("driverinstanceid"));
						primaryDriverDetail.setWeightage(rSetForDriver.getInt("weightage"));
						driverList.add(primaryDriverDetail);
					}
					ccPolicyConfigurationData.setDriverList(driverList);
					
				}finally{
					DBUtility.closeQuietly(rSetForDriver);
					DBUtility.closeQuietly(preparedStatementForDriver);
				}
				
				String queryForPlugin = getQueryForPlugins(policyId);
				PreparedStatement pluginStmt = null;
				ResultSet pluginResultSet = null;
				try {
					pluginStmt = conn.prepareStatement(queryForPlugin);
					pluginResultSet = pluginStmt.executeQuery();
					
					List<PluginEntryDetail> prePlugins = new ArrayList<PluginEntryDetail>();
					List<PluginEntryDetail> postPlugins = new ArrayList<PluginEntryDetail>();
					
					while (pluginResultSet.next()) {

						PluginEntryDetail plugin = new PluginEntryDetail();
						plugin.setPluginName(pluginResultSet.getString("PLUGINNAME"));
						plugin.setPluginArgument(pluginResultSet.getString("PLUGINARGUMENT"));
						
						String pluginType = pluginResultSet.getString("PLUGINTYPE");
						if ("IN".equalsIgnoreCase(pluginType)) {
							prePlugins.add(plugin);
						} else if ("OUT".equalsIgnoreCase(pluginType)) {
							postPlugins.add(plugin);
						}
					}
					
					ccPolicyConfigurationData.setPrePlugins(prePlugins);
					ccPolicyConfigurationData.setPostPlugins(postPlugins);
				
				} finally {
					DBUtility.closeQuietly(pluginResultSet);
					DBUtility.closeQuietly(pluginStmt);
				}

				ccPolicyConfigurationData.setSessionManagementEnabled(Strings.toBoolean(rsForPolicy.getString("SESSIONMANAGEMENT")));

				tempCCPolicyList.add(ccPolicyConfigurationData);
				tempCCPolicyMap.put(ccPolicyConfigurationData.getId(), ccPolicyConfigurationData);
			}
			
			this.policyList = tempCCPolicyList;
			this.ccPolicyConfigurationMap = tempCCPolicyMap;
		} finally {
			DBUtility.closeQuietly(rsForResponseAttribute);
			DBUtility.closeQuietly(psForResponseAttribute);
			DBUtility.closeQuietly(rsForPolicy);
			DBUtility.closeQuietly(psForPolicy);
			DBUtility.closeQuietly(conn);
		}		
	}

	private String getQueryForPlugins(String ccPolicyId) {
		return "SELECT * FROM TBLMCCPOLICYPLUGINCONFIG WHERE CCPOLICYID = '" + ccPolicyId + "'";
	}
	
	private String getQueryForResponseAttributes() {
		return "SELECT COMMANDCODES, RESPONSEATTRIBUTES FROM TBLMCCPOLICYRESPATTRREL WHERE POLICYID=?";
	}

	@DBReload
	public void reloadCCPolicyConfiguration() throws Exception{

		int size = this.policyList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select * from tblmccpolicy where POLICYID IN (");

		for(int i = 0; i < size-1; i++){
			CcServicePolicyConfigurationData ccAppPolicyConfigurationData = (CcServicePolicyConfigurationData)policyList.get(i);
			queryBuilder.append("'" + ccAppPolicyConfigurationData.getId() + "',");
		}
		queryBuilder.append("'" + ((CcServicePolicyConfigurationData)policyList.get(size - 1)).getId() + "')");
		String queryForReload = queryBuilder.toString();

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		PreparedStatement psForResponseAttribute = null;
		ResultSet rsForResponseAttribute = null;
		
		try{
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = conn.prepareStatement(queryForReload);
			resultSet = preparedStatement.executeQuery();

			while(resultSet.next()){
				CcServicePolicyConfigurationData ccAppPolicyConfigurationData = (CcServicePolicyConfigurationData) this.ccPolicyConfigurationMap.get(resultSet.getInt("POLICYID"));
				ccAppPolicyConfigurationData.setRuleSet(resultSet.getString("RULESET"));
				
				psForResponseAttribute = conn.prepareStatement(getQueryForResponseAttributes());
				psForResponseAttribute.setString(1, ccAppPolicyConfigurationData.getId());
				rsForResponseAttribute = psForResponseAttribute.executeQuery();
				
				ArrayList<CommandCodeResponseAttribute> commandCodeResponseAttributes = new ArrayList<CommandCodeResponseAttribute>();
				CommandCodeResponseAttribute commandCodeResponseAttribute = null;
				while (rsForResponseAttribute.next()) {					
					commandCodeResponseAttribute = new CommandCodeResponseAttribute();
					commandCodeResponseAttribute.setCommandCodes(rsForResponseAttribute.getString("COMMANDCODES"));
					commandCodeResponseAttribute.setResponseAttributes(rsForResponseAttribute.getString("RESPONSEATTRIBUTES"));
					commandCodeResponseAttributes.add(commandCodeResponseAttribute);
				}
				ccAppPolicyConfigurationData.setCommandCodeResponseAttributesList(commandCodeResponseAttributes);
			}
		} finally{
			DBUtility.closeQuietly(rsForResponseAttribute);
			DBUtility.closeQuietly(psForResponseAttribute);
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(conn);
		}
	}

	@PostRead
	public void postReadProcessing() {
		if(this.policyList!=null){
			int numOfPolicy = policyList.size();
			for(int i=0;i<numOfPolicy;i++){
				CcServicePolicyConfigurationData ccPolicyConfigurationData = (CcServicePolicyConfigurationData)policyList.get(i);
				List<PrimaryDriverDetail> driverList = ccPolicyConfigurationData.getDriverList();
				Map<String, Integer> driverMap = new LinkedHashMap<String, Integer>();
				if(driverList!=null){
					int numOFDriver = driverList.size();
					for(int k=0;k<numOFDriver;k++){
						driverMap.put(driverList.get(k).getDriverInstanceId(), driverList.get(k).getWeightage());
					}
				}
				ccPolicyConfigurationData.setDriverInstanceIdMap(driverMap);
				postReadProcessingForResponseAttributes(ccPolicyConfigurationData);

				registerPlugins(ccPolicyConfigurationData);
			}
		}
	}

	private void registerPlugins(CcServicePolicyConfigurationData data) {
		String policyName = data.getName();
		
		setPluginsCallerId(policyName, data.getPrePlugins(), PluginMode.IN);
		((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(data.getPrePlugins());
		
		setPluginsCallerId(policyName, data.getPostPlugins(), PluginMode.OUT);
		((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(data.getPostPlugins());
	}
	
	private void setPluginsCallerId(String policyName, List<PluginEntryDetail> plugins, PluginMode mode) {
		for (int index = 0; index < plugins.size(); index++) {
			PluginEntryDetail data = plugins.get(index);
			PluginCallerIdentity key = PluginCallerIdentity.createAndGetIdentity(ServiceTypeConstants.DIA_CC, mode, index, data.getPluginName())
					.setServicePolicyName(policyName).getId();
			data.setCallerId(key);
		}
	}
	
	private void postReadProcessingForResponseAttributes(CcServicePolicyConfigurationData ccPolicyConfigurationData) {
		
		List<CommandCodeResponseAttribute> commandCodeResponseAttributeList = ccPolicyConfigurationData.getCommandCodeResponseAttributesList();
		if(Collectionz.isNullOrEmpty(commandCodeResponseAttributeList)){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "No Command-Codes wise Response Attributes configured for CC Policy: " + ccPolicyConfigurationData.getName());
			}
			return;
		}
		
		Map<Integer, AdditionalResponseAttributes> commandCodeToAdditionalAttrMap = new HashMap<Integer, AdditionalResponseAttributes>();
		for(int i = 0 ; i < commandCodeResponseAttributeList.size() ; i++){
			
			String resposeAttributes = commandCodeResponseAttributeList.get(i).getResponseAttributes();
			if(Strings.isNullOrBlank(resposeAttributes)){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "No Response Attributes Configured for Command-Codes: "+ commandCodeResponseAttributeList.get(i).getCommandCodes() +  
							" for CC Policy: " + ccPolicyConfigurationData.getName());
				}
				continue;
			}
			AdditionalResponseAttributes additionalResponseAttributes = new AdditionalResponseAttributes(resposeAttributes.trim());
			String[] commandCodes = ParserUtility.splitString(commandCodeResponseAttributeList.get(i).getCommandCodes(), ',' , ';');
			for(int j = 0 ; j < commandCodes.length ; j++){

				if(Strings.isNullOrBlank(commandCodes[j])){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Skipping invalid Command-Code: " + commandCodes[j] + 
								" from Command-Codes: " + commandCodeResponseAttributeList.get(i).getCommandCodes() + 
								" for CC Policy: " + ccPolicyConfigurationData.getName());
					}
					continue;
				}
				try {
					int commandCode = Integer.parseInt(commandCodes[j].trim());
					commandCodeToAdditionalAttrMap.put(commandCode, additionalResponseAttributes);
				} catch (NumberFormatException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Skipping invalid Command-Code: " + commandCodes[j] + 
								" from Command-Codes: " + commandCodeResponseAttributeList.get(i).getCommandCodes() + 
								" for CC Policy: " + ccPolicyConfigurationData.getName() + 
								", Reason: " + e.getMessage());
					}
				}
			}
		}
		
		ccPolicyConfigurationData.setCommandCodeResponseAttributesMap(commandCodeToAdditionalAttrMap);
		
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		if(Collectionz.isNullOrEmpty(policyList)){
			return;
		}
		int numOfPolicy = policyList.size();
		for(int i=0;i<numOfPolicy;i++){
			CcServicePolicyConfigurationData ccPolicyConfigurationData = (CcServicePolicyConfigurationData)policyList.get(i);
			postReadProcessingForResponseAttributes(ccPolicyConfigurationData);
		}
	}
	@XmlElement(name="cc-policy",type=CcServicePolicyConfigurationData.class)
	public List<DiameterServicePolicyConfiguration> getPolicyList() {
		return policyList;
	}

	public void setPolicyList(List<DiameterServicePolicyConfiguration> policyList) {
		this.policyList = policyList;
	}

	private String getQueryForServicePolicyConfig() {
		DiameterCCServiceConfigurable ccApplicationConfigurable = getConfigurationContext().get(DiameterCCServiceConfigurable.class);
		List<String> servicePolicies = ccApplicationConfigurable.getServicePolicies();
		if(servicePolicies==null || !(servicePolicies.size()>0) || servicePolicies.contains(AAAServerConstants.ALL))
			return "select * from tblmccpolicy where STATUS=? ORDER BY ORDERNUMBER";
		else{
			String query =  "select * from tblmccpolicy where STATUS=? AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+") ORDER BY ( CASE ";
			
			String caseString = "";
			int numOfPolicy = servicePolicies.size();
			for(int i=0;i<numOfPolicy;i++){
				caseString = caseString + " WHEN NAME = '"+servicePolicies.get(i)+"' THEN "+i;
			}
			query = query+caseString+" END )";
			
			return query;
		}
	}
	
	public DiameterServicePolicyConfiguration getCcPolicyConfiguration(String policyId) {
		return this.ccPolicyConfigurationMap.get(policyId);
	}
	
	@XmlTransient
	public Map<String, DiameterServicePolicyConfiguration> getCcPolicyConfigurationsMap() {
		return this.ccPolicyConfigurationMap;
	}

}
