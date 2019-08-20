package com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl;

import static com.elitecore.commons.base.Numbers.parseInt;

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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.impl.PrimaryAndAdditionalDriversDetails;
import com.elitecore.aaa.core.data.AdditionalResponseAttributes;
import com.elitecore.aaa.core.drivers.StripUserIdentityStrategy;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.conf.impl.DiameterEAPServiceConfigurable;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.EapServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthorizationHandlerData;
import com.elitecore.aaa.radius.conf.impl.AdditionalDriverDetail;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.ChargeableUserIdentityConfiguration;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
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
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.plugins.data.PluginMode;
import com.elitecore.core.commons.plugins.data.ServiceTypeConstants;
import com.elitecore.core.serverx.policies.ParserUtility;


@XmlType(propOrder = {})
@XmlRootElement(name = "eap-policies")
@ConfigurationProperties(moduleName ="EAP-POLICY-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "eap-policy", schemaDirectories = {"system","schema"}, configDirectories = {"conf","diameter"})
public class EAPPolicyConfigurable extends Configurable{
	
	private static final String MODULE="EAP-POLICY-CONFIGURABLE";
	public static final int IGNORE_ON_POLICY_NOT_FOUND = 1;
	
	private List<DiameterServicePolicyConfiguration> policyList;
	private Map<String, DiameterServicePolicyConfiguration> eapServicePolicyConfigurationMap;
	
	public EAPPolicyConfigurable() {
		this.policyList = new ArrayList<DiameterServicePolicyConfiguration>();
		this.eapServicePolicyConfigurationMap = new LinkedHashMap<String, DiameterServicePolicyConfiguration>();
	}

	@DBRead
	public void readFromDB() throws Exception {

		Connection conn = null;
		PreparedStatement psForPolicy =null;
		ResultSet resultSet = null;
		
		PreparedStatement psForResponseAttribute = null;
		ResultSet rsForResponseAttribute = null;
	
		try {
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			List<DiameterServicePolicyConfiguration> tempEAPPolicyList = new ArrayList<DiameterServicePolicyConfiguration>();
			Map<String, DiameterServicePolicyConfiguration> tempEAPPolicyMap = new LinkedHashMap<String, DiameterServicePolicyConfiguration>();

			String query = getQueryForServicePolicyConfig();
			psForPolicy = conn.prepareStatement(query);
			psForPolicy.setString(1,CommonConstants.DATABASE_POLICY_STATUS_ACTIVE );			
			resultSet=psForPolicy.executeQuery();
			EapServicePolicyConfigurationData eapServicePolicyConfigurationData;				

			while(resultSet.next()){
				
				eapServicePolicyConfigurationData = new EapServicePolicyConfigurationData();
				eapServicePolicyConfigurationData.setAuthorizationHandlerData(new DiameterAuthorizationHandlerData());

				String eapPolicyId = resultSet.getString("EAPPOLICYID");
				
				String policyName = resultSet.getString("NAME");
				
				eapServicePolicyConfigurationData.setResponseBehaviorType(DefaultResponseBehaviorType.valueOf(resultSet.getString("DEFAULTRESPONSEBEHAVIORTYPE").toUpperCase()));
				eapServicePolicyConfigurationData.setResponseBehaviorParameter(resultSet.getString("DEFAULTRESPONSEBEHAVIORPARAM"));
				eapServicePolicyConfigurationData.setId(eapPolicyId);
				eapServicePolicyConfigurationData.setRuleSet(resultSet.getString("RULESET"));
				eapServicePolicyConfigurationData.setName(policyName);
				eapServicePolicyConfigurationData.getAuthorizationHandlerData().setPolicyName(policyName);
				
				eapServicePolicyConfigurationData.setRequestType(resultSet.getInt("REQUESTTYPE"));
				
				DiameterEAPAuthParamsDetails diameterEAPAuthParamsDetails = eapServicePolicyConfigurationData.getDiameterEAPAuthParamsDetails();
				
				diameterEAPAuthParamsDetails.setUserIdentityStr(resultSet.getString("MULTIPLEUID"));
				
				if(resultSet.getString("CASESENSITIVEUID")!=null && resultSet.getString("CASESENSITIVEUID").length()>0)
					diameterEAPAuthParamsDetails.setiCaseSensitivity(parseInt(resultSet.getString("CASESENSITIVEUID"), diameterEAPAuthParamsDetails.getiCaseSensitivity()));
				else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Select Case Parameter for EAP Application Policy : "+policyName+" is not defined ,using default value");
				}
				boolean isStripEnabled = false;
				if (resultSet.getString("STRIPUSERIDENTITY") != null && resultSet.getString("STRIPUSERIDENTITY").length() > 0){
					 isStripEnabled = Boolean.parseBoolean(resultSet.getString("STRIPUSERIDENTITY").trim());
				} 

				if (resultSet.getString("REALMPATTERN") != null && resultSet.getString("REALMPATTERN").length() > 0){
					if(isStripEnabled) {
						diameterEAPAuthParamsDetails.getIdentityParamsDetail().setStripIdentity(resultSet.getString("REALMPATTERN"));
					} else {
						diameterEAPAuthParamsDetails.getIdentityParamsDetail().setStripIdentity(StripUserIdentityStrategy.NONE);
					}
				}	
				
				if (resultSet.getString("REALMSEPARATOR") != null&& resultSet.getString("REALMSEPARATOR").length() > 0){
					diameterEAPAuthParamsDetails.getIdentityParamsDetail().setSeparator(resultSet.getString("REALMSEPARATOR"));
				}	

				if (resultSet.getString("TRIMUSERIDENTITY") != null&& resultSet.getString("TRIMUSERIDENTITY").trim().length() > 0){
					diameterEAPAuthParamsDetails.getIdentityParamsDetail().setIsTrimIdentity(Boolean.parseBoolean(resultSet.getString("TRIMUSERIDENTITY").trim()));
				}	
				
				if (resultSet.getString("TRIMPASSWORD") != null && resultSet.getString("TRIMPASSWORD").length()>0){
					diameterEAPAuthParamsDetails.getIdentityParamsDetail().setIsTrimPassword(Boolean.parseBoolean(resultSet.getString("TRIMPASSWORD").trim()));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Trim Password Parameter for Eap Apllication Policy : "+policyName+" is not defined ,using default value");
				}
				
				readAnonymousProfileIdentity(resultSet, diameterEAPAuthParamsDetails);
				
				readGracePolicy(resultSet, eapServicePolicyConfigurationData);
				
				eapServicePolicyConfigurationData.getAuthorizationHandlerData().setRejectOnCheckItemNotFound(Boolean.parseBoolean(resultSet.getString("REJECTONCHECKITEMNOTFOUND").trim()));
				eapServicePolicyConfigurationData.getAuthorizationHandlerData().setRejectOnRejectItemNotFound(Boolean.parseBoolean(resultSet.getString("REJECTONREJECTITEMNOTFOUND").trim()));
				eapServicePolicyConfigurationData.getAuthorizationHandlerData().setContinueOnPolicyNotFound(Boolean.parseBoolean(resultSet.getString("ACTIONONPOLICYNOTFOUND").trim()));
				
				readWimaxEnabled(resultSet, eapServicePolicyConfigurationData);
				
				readDefaultSessionTimeout(resultSet, eapServicePolicyConfigurationData);
				
				readCuiConfiguration(resultSet, eapServicePolicyConfigurationData);
				
				psForResponseAttribute = conn.prepareStatement(getQueryForResponseAttributes());
				psForResponseAttribute.setString(1, eapPolicyId);
				rsForResponseAttribute = psForResponseAttribute.executeQuery();
				
				ArrayList<CommandCodeResponseAttribute> commandCodeResponseAttributes = new ArrayList<CommandCodeResponseAttribute>();
				CommandCodeResponseAttribute commandCodeResponseAttribute = null;
				while (rsForResponseAttribute.next()) {					
					commandCodeResponseAttribute = new CommandCodeResponseAttribute();
					commandCodeResponseAttribute.setCommandCodes(rsForResponseAttribute.getString("COMMANDCODES"));
					commandCodeResponseAttribute.setResponseAttributes(rsForResponseAttribute.getString("RESPONSEATTRIBUTES"));
					commandCodeResponseAttributes.add(commandCodeResponseAttribute);
				}
				eapServicePolicyConfigurationData.setCommandCodeResponseAttributesList(commandCodeResponseAttributes);
				
				diameterEAPAuthParamsDetails.setEapConfigId(resultSet.getString("EAP_ID"));
				
				PrimaryAndAdditionalDriversDetails primaryAndAdditionalDriversDetails = eapServicePolicyConfigurationData.getProfileDrivers();
				
				//setting the driver script
				primaryAndAdditionalDriversDetails.setDriverScript(resultSet.getString("DRIVERSCRIPT"));
				
				/* reading driver details */
				PreparedStatement preparedStatementForDriver = null;
				ResultSet rSetForDriver = null;
				try{
					query = "SELECT DRIVERINSTANCEID , WEIGHTAGE FROM TBLMEAPPOLICYAUTHDRIVERREL WHERE EAPPOLICYID=?";
					preparedStatementForDriver = conn.prepareStatement(query);
					preparedStatementForDriver.setString(1, eapPolicyId);
					rSetForDriver = preparedStatementForDriver.executeQuery();			
					List<PrimaryDriverDetail> primaryDriverGroupList = new ArrayList<PrimaryDriverDetail>();
					while (rSetForDriver.next()) {		
						PrimaryDriverDetail primaryDriverDetail = new PrimaryDriverDetail();
						primaryDriverDetail.setDriverInstanceId(rSetForDriver.getString("DRIVERINSTANCEID"));
						primaryDriverDetail.setWeightage(rSetForDriver.getInt("WEIGHTAGE"));
						primaryDriverGroupList.add(primaryDriverDetail);
					}
					primaryAndAdditionalDriversDetails.setPrimaryDriverGroup(primaryDriverGroupList);
				}finally{
					DBUtility.closeQuietly(rSetForDriver);
					DBUtility.closeQuietly(preparedStatementForDriver);
				}
											
				/* reading additional driver details */
				PreparedStatement preparedStatementForAdditionalDriver = null;
				ResultSet rSetForAdditionalDriver = null;
				try{
					query = "SELECT DRIVERINSTANCEID FROM TBLMEAPPOLICYADDDRIVERREL WHERE EAPPOLICYID=?";
					preparedStatementForAdditionalDriver = conn.prepareStatement(query);
					preparedStatementForAdditionalDriver.setString(1, eapPolicyId);
					rSetForAdditionalDriver = preparedStatementForAdditionalDriver.executeQuery();
					
					List<AdditionalDriverDetail> additionalDriverList = new ArrayList<AdditionalDriverDetail>();
					while (rSetForAdditionalDriver.next()) {		
						AdditionalDriverDetail additionalDriverDetail = new AdditionalDriverDetail();
						additionalDriverDetail.setDriverId(rSetForAdditionalDriver.getString("DRIVERINSTANCEID"));
						additionalDriverList.add(additionalDriverDetail);
					}
					primaryAndAdditionalDriversDetails.setAdditionalDriverList(additionalDriverList);
				}finally{
					DBUtility.closeQuietly(rSetForAdditionalDriver);
					DBUtility.closeQuietly(preparedStatementForAdditionalDriver);
				}
				/* reading additional driver  details complete.*/
				
				tempEAPPolicyList.add(eapServicePolicyConfigurationData);
				tempEAPPolicyMap.put(eapServicePolicyConfigurationData.getId(), eapServicePolicyConfigurationData);
				
				String queryForPlugin = getQueryForPlugins(eapPolicyId);
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
					
					eapServicePolicyConfigurationData.setPrePluginDataList(prePlugins);
					eapServicePolicyConfigurationData.setPostPluginDataList(postPlugins);
				} finally {
					DBUtility.closeQuietly(pluginResultSet);
					DBUtility.closeQuietly(pluginStmt);
				}
				
				
				eapServicePolicyConfigurationData.setSessionManagementEnabled(Strings.toBoolean(resultSet.getString("SESSIONMANAGEMENT")));
				
				if (DBUtility.isValueAvailable(resultSet, "DIACONCONFIGID")) {
					eapServicePolicyConfigurationData.setDiameterConcurrencyConfigId(resultSet.getString("DIACONCONFIGID"));
				}
				if (DBUtility.isValueAvailable(resultSet, "ADDIDIACONCONFIGID")) {
					eapServicePolicyConfigurationData.setAdditionalDiameterConcuurencyConfigId(resultSet.getString("ADDIDIACONCONFIGID"));
				}
			}
			this.policyList = tempEAPPolicyList;
			this.eapServicePolicyConfigurationMap = tempEAPPolicyMap;
		} finally {
			DBUtility.closeQuietly(rsForResponseAttribute);
			DBUtility.closeQuietly(psForResponseAttribute);
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(psForPolicy);
			DBUtility.closeQuietly(conn);
		}		


	}

	private String getQueryForPlugins(String eapPolicyId) {
		return "SELECT * FROM TBLMEAPPOLICYPLUGINCONFIG WHERE EAPPOLICYID = '" + eapPolicyId + "'";
	}

	private void readCuiConfiguration(ResultSet resultSet,
			EapServicePolicyConfigurationData eapServicePolicyConfigurationData) throws SQLException {
		ChargeableUserIdentityConfiguration cuiConfiguration = new ChargeableUserIdentityConfiguration();
		cuiConfiguration.setCui(resultSet.getString("CUI"));
		cuiConfiguration.setExpression(resultSet.getString("ADVANCEDCUIEXPRESSION"));
		cuiConfiguration.setAuthenticationCuiAttribute(resultSet.getString("CUIRESPONSEATTRIBUTES"));
		eapServicePolicyConfigurationData.setCuiConfiguration(cuiConfiguration);
	}

	private void readDefaultSessionTimeout(ResultSet resultSet,
			EapServicePolicyConfigurationData eapServicePolicyConfigurationData) throws SQLException {
		if (DBUtility.isValueAvailable(resultSet, "DEFAULTSESSIONTIMEOUT")) {
			eapServicePolicyConfigurationData.getAuthorizationHandlerData().setDefaultSessionTimeoutInSeconds(Numbers.parseLong(resultSet.getString("DEFAULTSESSIONTIMEOUT"), AAAServerConstants.POLICY_DEFAULT_SESSION_TIMEOUT_IN_SECS));
		} else {
			LogManager.getLogger().info(MODULE, "Default session timeout not provided for policy: " + eapServicePolicyConfigurationData.getName() 
					+ ", will use default value: " + eapServicePolicyConfigurationData.getAuthorizationHandlerData().getDefaultSessionTimeoutInSeconds());
		}
	}

	private void readAnonymousProfileIdentity(ResultSet resultSet,
			DiameterEAPAuthParamsDetails diameterEAPAuthParamsDetails)
			throws SQLException {
		if (DBUtility.isValueAvailable(resultSet, "ANONYMOUSIDENTITY")) {
			diameterEAPAuthParamsDetails.setAnonymousProfileIdentity(resultSet.getString("ANONYMOUSIDENTITY").trim());
		}
	}
	
	private void readGracePolicy(ResultSet resultSet,
			EapServicePolicyConfigurationData eapAppPolicyConfData)
			throws SQLException {
		if (DBUtility.isValueAvailable(resultSet, "GRACEPOLICY")) {
			eapAppPolicyConfData.getAuthorizationHandlerData().setGracePolicy(resultSet.getString("GRACEPOLICY").trim());
		}
	}

	private void readWimaxEnabled(ResultSet resultSet,
			EapServicePolicyConfigurationData eapAppPolicyConfData)
			throws SQLException {
		if (DBUtility.isValueAvailable(resultSet, "WIMAX")) {
			eapAppPolicyConfData.getAuthorizationHandlerData().setWimaxEnabled(Strings.toBoolean(resultSet.getString("WIMAX")));
		}
	}
	
	private String getQueryForResponseAttributes() {
		return "SELECT COMMANDCODES, RESPONSEATTRIBUTES FROM TBLMEAPPOLICYRESPATTRREL WHERE EAPPOLICYID=?";
	}

	@DBReload
	public void reloadEAPPolicyConfiguration() throws Exception{

		int size = this.policyList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select * from TBLMEAPPOLICY where EAPPOLICYID IN (");

		for(int i = 0; i < size-1; i++){
			DiameterServicePolicyConfiguration eapAppPolicyConfigurationData = policyList.get(i);
			queryBuilder.append("'" + ((EapServicePolicyConfiguration)eapAppPolicyConfigurationData).getId() + "',");
		}
		queryBuilder.append("'" + ((EapServicePolicyConfiguration)policyList.get(size - 1)).getId() + "')");
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

				EapServicePolicyConfigurationData eapAppPolicyConfigurationData = (EapServicePolicyConfigurationData) this.eapServicePolicyConfigurationMap.get(resultSet.getInt("EAPPOLICYID"));

				/*Reading Policy Detail */

				String policyName = eapAppPolicyConfigurationData.getName();


				eapAppPolicyConfigurationData.setRuleSet(resultSet.getString("RULESET"));
				eapAppPolicyConfigurationData.setName(policyName);


				DiameterEAPAuthParamsDetails diameterEAPAuthParamsDetails = eapAppPolicyConfigurationData.getDiameterEAPAuthParamsDetails();

				diameterEAPAuthParamsDetails.setUserIdentityStr(resultSet.getString("MULTIPLEUID"));

				if(resultSet.getString("CASESENSITIVEUID")!=null && resultSet.getString("CASESENSITIVEUID").length()>0)
					diameterEAPAuthParamsDetails.setiCaseSensitivity(parseInt(resultSet.getString("CASESENSITIVEUID"), diameterEAPAuthParamsDetails.getiCaseSensitivity()));
				else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Select Case Parameter for EAP Application Policy : "+policyName+" is not defined ,using default value");
				}

				boolean isStripEnabled = false;
				if (resultSet.getString("STRIPUSERIDENTITY") != null && resultSet.getString("STRIPUSERIDENTITY").length() > 0){
					 isStripEnabled = Boolean.parseBoolean(resultSet.getString("STRIPUSERIDENTITY").trim());
				} 

				if (resultSet.getString("REALMPATTERN") != null && resultSet.getString("REALMPATTERN").length() > 0){
					if(isStripEnabled) {
						diameterEAPAuthParamsDetails.getIdentityParamsDetail().setStripIdentity(resultSet.getString("REALMPATTERN"));
					} else {
						diameterEAPAuthParamsDetails.getIdentityParamsDetail().setStripIdentity(StripUserIdentityStrategy.NONE);
					}
				}	

				if (resultSet.getString("REALMSEPARATOR") != null&& resultSet.getString("REALMSEPARATOR").length() > 0){
					diameterEAPAuthParamsDetails.getIdentityParamsDetail().setSeparator(resultSet.getString("REALMSEPARATOR"));
				}	

				if (resultSet.getString("TRIMUSERIDENTITY") != null&& resultSet.getString("TRIMUSERIDENTITY").trim().length() > 0){
					diameterEAPAuthParamsDetails.getIdentityParamsDetail().setIsTrimIdentity(Boolean.parseBoolean(resultSet.getString("TRIMUSERIDENTITY").trim()));
				}	

				if (resultSet.getString("TRIMPASSWORD") != null && resultSet.getString("TRIMPASSWORD").length()>0){
					diameterEAPAuthParamsDetails.getIdentityParamsDetail().setIsTrimPassword(Boolean.parseBoolean(resultSet.getString("TRIMPASSWORD").trim()));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Trim Password Parameter for Eap Apllication Policy : "+policyName+" is not defined ,using default value");
				}

				readAnonymousProfileIdentity(resultSet, diameterEAPAuthParamsDetails);
				
				readGracePolicy(resultSet, eapAppPolicyConfigurationData);
				
				readWimaxEnabled(resultSet, eapAppPolicyConfigurationData);
				
				readCuiConfiguration(resultSet, eapAppPolicyConfigurationData);
				
				psForResponseAttribute = conn.prepareStatement(getQueryForResponseAttributes());
				psForResponseAttribute.setString(1, eapAppPolicyConfigurationData.getId());
				rsForResponseAttribute = psForResponseAttribute.executeQuery();
				
				ArrayList<CommandCodeResponseAttribute> commandCodeResponseAttributes = new ArrayList<CommandCodeResponseAttribute>();
				CommandCodeResponseAttribute commandCodeResponseAttribute = null;
				while (rsForResponseAttribute.next()) {					
					commandCodeResponseAttribute = new CommandCodeResponseAttribute();
					commandCodeResponseAttribute.setCommandCodes(rsForResponseAttribute.getString("COMMANDCODES"));
					commandCodeResponseAttribute.setResponseAttributes(rsForResponseAttribute.getString("RESPONSEATTRIBUTES"));
					commandCodeResponseAttributes.add(commandCodeResponseAttribute);
				}
				eapAppPolicyConfigurationData.setCommandCodeResponseAttributesList(commandCodeResponseAttributes);
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
				EapServicePolicyConfigurationData eapAppPolicyConfigurationData = (EapServicePolicyConfigurationData)policyList.get(i);
				PrimaryAndAdditionalDriversDetails profileDrivers = eapAppPolicyConfigurationData.getProfileDrivers();
				if(profileDrivers!=null){
					Map<String, Integer> primaryDriverMap = new LinkedHashMap<String, Integer>();
					List<PrimaryDriverDetail> driverList = profileDrivers.getPrimaryDriverGroup();
					if(driverList!=null){
						int numOFDriver = driverList.size();
						for(int k=0;k<numOFDriver;k++){
							primaryDriverMap.put(driverList.get(k).getDriverInstanceId(), driverList.get(k).getWeightage());
						}
					}
					eapAppPolicyConfigurationData.setAuthDriverInstanceIdsMap(primaryDriverMap);
					
					List<AdditionalDriverDetail> additionalDriverList = profileDrivers.getAdditionalDriverList();
					if(additionalDriverList!=null){
						List<String> intAdditionalDriverList = new ArrayList<String>();
						for(AdditionalDriverDetail additional : additionalDriverList){
							intAdditionalDriverList.add(additional.getDriverId());
						}
						eapAppPolicyConfigurationData.setAdditionalDrivers(intAdditionalDriverList);
					}
					
				}
				
				postProcessingForMultipleUserIdentities(eapAppPolicyConfigurationData);
				postProcessingForResponseAttributes(eapAppPolicyConfigurationData);
				postReadProcessingForCUIConfiguration(eapAppPolicyConfigurationData);
				
				registerPlugins(eapAppPolicyConfigurationData);
			}
		}
	}
	
	private void registerPlugins(EapServicePolicyConfigurationData data) {
		setPluginsCallerId(data.getName(), data.getPrePlugins(), PluginMode.IN);
		((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(data.getPrePlugins());
		
		setPluginsCallerId(data.getName(), data.getPostPlugins(), PluginMode.OUT);
		((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(data.getPostPlugins());
	}
	
	private void setPluginsCallerId(String policyName, List<PluginEntryDetail> plugins, PluginMode mode) {
		for (int index = 0; index < plugins.size(); index++) {
			PluginEntryDetail data = plugins.get(index);
			PluginCallerIdentity key = PluginCallerIdentity.createAndGetIdentity(ServiceTypeConstants.DIA_EAP, mode, index, data.getPluginName())
					.setServicePolicyName(policyName).getId();
			data.setCallerId(key);
		}
	}
	
	private void postReadProcessingForCUIConfiguration(
			EapServicePolicyConfigurationData eapAppPolicyConfigurationData) {
		eapAppPolicyConfigurationData.getCuiConfiguration().postRead();
	}

	private void postProcessingForResponseAttributes(EapServicePolicyConfigurationData eapAppPolicyConfigurationData) {
		
		List<CommandCodeResponseAttribute> commandCodeResponseAttributeList = eapAppPolicyConfigurationData.getCommandCodeResponseAttributesList();
		if(Collectionz.isNullOrEmpty(commandCodeResponseAttributeList)){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "No Command-Codes wise Response Attributes configured for EAP Policy: " + eapAppPolicyConfigurationData.getName());
			}
			return;
		}
		
		Map<Integer, AdditionalResponseAttributes> commandCodeToAdditionalAttrMap = new HashMap<Integer, AdditionalResponseAttributes>();
		for(int i = 0 ; i < commandCodeResponseAttributeList.size() ; i++){
			
			String resposeAttributes = commandCodeResponseAttributeList.get(i).getResponseAttributes();
			if(Strings.isNullOrBlank(resposeAttributes)){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "No response attributes configured for Command-Codes: " + commandCodeResponseAttributeList.get(i).getCommandCodes() + 
							" for EAP Policy: " + eapAppPolicyConfigurationData.getName());
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
								" for EAP Policy: " + eapAppPolicyConfigurationData.getName());
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
								" for EAP Policy: " + eapAppPolicyConfigurationData.getName() + 
								", Reason: " + e.getMessage());
					}
				}
			}
		}
		
		eapAppPolicyConfigurationData.setCommandCodeResponseAttributesMap(commandCodeToAdditionalAttrMap);
		
	}

	private void postProcessingForMultipleUserIdentities(
			EapServicePolicyConfigurationData eapAppPolicyConfigurationData) {
		String multiUserIdentitesStr =  eapAppPolicyConfigurationData.getDiameterEAPAuthParamsDetails().getUserIdentityStr();
		if(multiUserIdentitesStr==null || !(multiUserIdentitesStr.trim().length()>0)){
			multiUserIdentitesStr = "0:1";
		}
		eapAppPolicyConfigurationData.setUserIdentities(getListFromString(multiUserIdentitesStr,","));
		
	}

	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		if(this.policyList!=null){
			int numOfPolicy = policyList.size();
			for(int i=0;i<numOfPolicy;i++){
				EapServicePolicyConfigurationData eapAppPolicyConfigurationData = (EapServicePolicyConfigurationData)policyList.get(i);
				postProcessingForMultipleUserIdentities(eapAppPolicyConfigurationData);
				postProcessingForResponseAttributes(eapAppPolicyConfigurationData);
				postReadProcessingForCUIConfiguration(eapAppPolicyConfigurationData);
			}
		}
	
	}
	@XmlElement(name="eap-policy",type=EapServicePolicyConfigurationData.class)
	public List<DiameterServicePolicyConfiguration> getPolicyList() {
		return policyList;
	}

	public void setPolicyList(List<DiameterServicePolicyConfiguration> policyList) {
		this.policyList = policyList;
	}
	

	private ArrayList<String> getListFromString(String sourceStr, String seperator) {
		ArrayList<String> list = new ArrayList<String>();
		String [] multiUserIdentites = sourceStr.split(seperator);
		for (String element : multiUserIdentites) {
			if(element!=null && element.trim().length()>0)
				list.add(element);
		}
		return list;
	}
	private String getQueryForServicePolicyConfig() {
		DiameterEAPServiceConfigurable eapApplicationConfigurable = getConfigurationContext().get(DiameterEAPServiceConfigurable.class);
		List<String> servicePolicies = eapApplicationConfigurable.getServicePolicies();
		if(servicePolicies==null || !(servicePolicies.size()>0) || servicePolicies.contains(AAAServerConstants.ALL))
			return "select * from TBLMEAPPOLICY where STATUS=? ORDER BY ORDERNUMBER";
		else{
			String query =  "select * from TBLMEAPPOLICY where STATUS=? AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+") ORDER BY ( CASE ";
			String caseString = "";
			int numOfPolicy = servicePolicies.size();
			for(int i=0;i<numOfPolicy;i++){
				caseString = caseString + " WHEN NAME = '"+servicePolicies.get(i)+"' THEN "+i;
		}
			query = query+caseString+" END )";
			
			return query;
		}
	}
	
	public DiameterServicePolicyConfiguration getEapServicePolicyConfiguration(String policyId) {
		return this.eapServicePolicyConfigurationMap.get(policyId);
	}

	@XmlTransient
	public Map<String, DiameterServicePolicyConfiguration> getEapServicePolicyConfigurationMap() {
		return this.eapServicePolicyConfigurationMap;
	}

}
