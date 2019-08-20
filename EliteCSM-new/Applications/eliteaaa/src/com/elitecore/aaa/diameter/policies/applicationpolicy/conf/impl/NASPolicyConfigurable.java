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
import com.elitecore.aaa.core.constant.AuthMethods;
import com.elitecore.aaa.core.data.AdditionalResponseAttributes;
import com.elitecore.aaa.core.drivers.StripUserIdentityStrategy;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.conf.impl.DiameterNasServiceConfigurable;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthenticationHandlerData;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepositoryDetails;
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
import com.elitecore.core.commons.plugins.data.ServicePolicyFlow;
import com.elitecore.core.commons.plugins.data.ServiceTypeConstants;
import com.elitecore.core.serverx.policies.ParserUtility;

@XmlType(propOrder = {})
@XmlRootElement(name = "nas-policies")
@ConfigurationProperties(moduleName ="NAS-POLICY-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "nas-policy", schemaDirectories = {"system","schema"}, configDirectories = {"conf","diameter"})
public class NASPolicyConfigurable  extends Configurable{

	private static final String MODULE="NAS-POLICY-CONFIGURABLE";
	public static final int IGNORE_ON_POLICY_NOT_FOUND = 1;

	private List<DiameterServicePolicyConfiguration> policyList;
	private Map<String, DiameterServicePolicyConfiguration> nasServicePolicyConfigurationMap;
	
	public NASPolicyConfigurable() {
		this.policyList = new ArrayList<DiameterServicePolicyConfiguration>();
		this.nasServicePolicyConfigurationMap = new LinkedHashMap<String, DiameterServicePolicyConfiguration>();
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
			List<DiameterServicePolicyConfiguration> tempNASPolicyList = new ArrayList<DiameterServicePolicyConfiguration>();
			Map<String, DiameterServicePolicyConfiguration> tempNASPolicyMap = new LinkedHashMap<String, DiameterServicePolicyConfiguration>();

			String query = getQueryForServicePolicyConfig();
			psForPolicy = conn.prepareStatement(query);
			psForPolicy.setString(1,CommonConstants.DATABASE_POLICY_STATUS_ACTIVE );			
			resultSet=psForPolicy.executeQuery();
			NasServicePolicyConfigurationData nasPolicyConfigurationData;	
			
			while(resultSet.next()){
				DiameterSubscriberProfileRepositoryDetails sprDetails = new DiameterSubscriberProfileRepositoryDetails();
				nasPolicyConfigurationData = new NasServicePolicyConfigurationData();
				
				String policyId = resultSet.getString("NASPOLICYID");
				nasPolicyConfigurationData.setPolicyId(policyId);
	
				DiameterAuthenticationHandlerData diameterAuthenticationHandlerData = nasPolicyConfigurationData.getAuthenticationHandlerData();
				
				List<String> getMultipleId = new ArrayList<String>();
				getMultipleId.add(resultSet.getString("MULTIPLEUID"));
				sprDetails.setUserIdentities(getMultipleId);
				
				nasPolicyConfigurationData.setRuleSet(resultSet.getString("RULESET")); 
				
				String policyName = resultSet.getString("NAME");
				nasPolicyConfigurationData.setName(policyName);
				nasPolicyConfigurationData.getAuthenticationHandlerData().setPolicyName(policyName);
				nasPolicyConfigurationData.getAuthorizationHandlerData().setPolicyName(policyName);
				
				nasPolicyConfigurationData.getAuthenticationHandlerData().setPolicyName(policyName);
				nasPolicyConfigurationData.getAuthorizationHandlerData().setPolicyName(policyName);
				
				nasPolicyConfigurationData.setResponseBehaviorType(DefaultResponseBehaviorType.valueOf(resultSet.getString("DEFAULTRESPONSEBEHAVIORTYPE").toUpperCase()));
				nasPolicyConfigurationData.setResponseBehaviorParameter(resultSet.getString("DEFAULTRESPONSEBEHAVIORPARAM"));
				
				if(Strings.isNullOrEmpty(resultSet.getString("CASESENSITIVEUID")) == false)
					sprDetails.getUpdateIdentity().setCase(parseInt(resultSet.getString("CASESENSITIVEUID").trim(),sprDetails.getUpdateIdentity().getCase()));
				else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Select Case Parameter for NAS Application Policy : "+policyName+" is not defined ,using default value");
				}

				boolean isStripEnabled = false;
				if (Strings.isNullOrEmpty(resultSet.getString("STRIPUSERIDENTITY")) == false){
					 isStripEnabled = Boolean.parseBoolean(resultSet.getString("STRIPUSERIDENTITY").trim());
				} 

				if (Strings.isNullOrEmpty(resultSet.getString("REALMPATTERN")) == false){
					if(isStripEnabled) {
						sprDetails.getUpdateIdentity().setStripIdentity(resultSet.getString("REALMPATTERN"));
					} else {
						sprDetails.getUpdateIdentity().setStripIdentity(StripUserIdentityStrategy.NONE);
					}
				}	
				
				if (Strings.isNullOrEmpty(resultSet.getString("REALMSEPARATOR")) == false)
					sprDetails.getUpdateIdentity().setSeparator(resultSet.getString("REALMSEPARATOR"));
			

				if (Strings.isNullOrEmpty(resultSet.getString("TRIMUSERIDENTITY")) == false)
					sprDetails.getUpdateIdentity().setIsTrimIdentity(Boolean.parseBoolean(resultSet.getString("TRIMUSERIDENTITY").trim()));
				
				if (Strings.isNullOrEmpty(resultSet.getString("TRIMPASSWORD")) == false){
					sprDetails.getUpdateIdentity().setIsTrimPassword(Boolean.parseBoolean(resultSet.getString("TRIMPASSWORD").trim()));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Trim Password Parameter for NAS Application Policy : "+policyName+" is not defined ,using default value");
				}
				
				if (Strings.isNullOrEmpty(resultSet.getString("USERNAME")) == false)
					sprDetails.setUserName(resultSet.getString("USERNAME"));

				String userNameResponseAttributeList = resultSet.getString("usernameresponseattributes");
				sprDetails.setUserNameResponseAttribute(userNameResponseAttributeList);
				
				nasPolicyConfigurationData.setRequestType(resultSet.getInt("REQUESTTYPE"));
				
				readAnonymousProfileIdentity(resultSet, sprDetails);
				
				diameterAuthenticationHandlerData.setSubscriberProfileRepositoryDetails(sprDetails);
				
				readWimaxEnabled(resultSet, nasPolicyConfigurationData);
				
				readGracePolicy(resultSet, nasPolicyConfigurationData);
				
				nasPolicyConfigurationData.getAuthorizationHandlerData().setRejectOnCheckItemNotFound(Boolean.parseBoolean(resultSet.getString("REJECTONCHECKITEMNOTFOUND").trim()));
				nasPolicyConfigurationData.getAuthorizationHandlerData().setRejectOnRejectItemNotFound(Boolean.parseBoolean(resultSet.getString("REJECTONREJECTITEMNOTFOUND").trim()));
				nasPolicyConfigurationData.getAuthorizationHandlerData().setContinueOnPolicyNotFound(Boolean.parseBoolean(resultSet.getString("ACTIONONPOLICYNOTFOUND").trim()));
				
				readDefaultSessionTimeout(resultSet, nasPolicyConfigurationData);
				
				readCuiConfiguration(resultSet, nasPolicyConfigurationData);
				
				/* reading policy details complete */
				
				NasAuthDetail nasAuthDetail = nasPolicyConfigurationData.getNasAuthDetail();
				nasAuthDetail.setScript(resultSet.getString("AUTHDRIVERSCRIPT"));

				readNasAuthFlowPlugins(nasAuthDetail, conn, policyId);
				
				
				NasAcctDetail nasAcctDetail =  nasPolicyConfigurationData.getNasAcctDetail();
				nasAcctDetail.setScript(resultSet.getString("ACCTDRIVERSCRIPT"));
				
				readNasAcctFlowPlugins(nasAcctDetail, conn, policyId);
				
				/* reading AutheMethodHandlerTypes */				
				PreparedStatement preparedStatementForMethodHandler = null;
				ResultSet rSetForMethodHandler = null;
				try{
					query = "select authmethodtypeid from tblmnaspolicyamrel where naspolicyid=?";
					preparedStatementForMethodHandler = conn.prepareStatement(query);
					preparedStatementForMethodHandler.setString(1, policyId);
					rSetForMethodHandler = preparedStatementForMethodHandler.executeQuery();
					
					while (rSetForMethodHandler.next()) {
						int authMethodType = rSetForMethodHandler.getInt("authmethodtypeid");
						nasPolicyConfigurationData.getAuthenticationHandlerData().getAutheMethodHandlerTypes().add(authMethodType);
					}
					
				}finally{
					DBUtility.closeQuietly(rSetForMethodHandler);
					DBUtility.closeQuietly(preparedStatementForMethodHandler);
				}
				
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
				nasPolicyConfigurationData.setCommandCodeResponseAttributesList(commandCodeResponseAttributes);
				
				/* reading driver details */
				PreparedStatement preparedStatementForDriver = null;
				ResultSet rSetForDriver = null;
				try{
					query = "SELECT driverinstanceid ,weightage FROM tblmnaspolicyauthdriverrel WHERE naspolicyid=?";
					preparedStatementForDriver = conn.prepareStatement(query);
					preparedStatementForDriver.setString(1, policyId);
					rSetForDriver = preparedStatementForDriver.executeQuery();
					List<PrimaryDriverDetail> primaryDriverList = new ArrayList<PrimaryDriverDetail>();
					
					while (rSetForDriver.next()) {					
						PrimaryDriverDetail primaryDriverDetail = new PrimaryDriverDetail();
						primaryDriverDetail.setDriverInstanceId(rSetForDriver.getString("driverinstanceid"));
						primaryDriverDetail.setWeightage(rSetForDriver.getInt("weightage"));
						primaryDriverList.add(primaryDriverDetail);
					}
					nasAuthDetail.setPrimaryDriverDetails(primaryDriverList);
				}finally{
					DBUtility.closeQuietly(rSetForDriver);
					DBUtility.closeQuietly(preparedStatementForDriver);
				}
				
				/* reading additional driver details */
				PreparedStatement preparedStatementForAdditionalDriver = null;
				ResultSet rSetForAdditionalDriver = null;
				try{
					query = "SELECT DRIVERINSTANCEID FROM TBLMNASADDAUTHDRIVERREL WHERE NASPOLICYID=?";
					preparedStatementForAdditionalDriver = conn.prepareStatement(query);
					preparedStatementForAdditionalDriver.setString(1, policyId);
					rSetForAdditionalDriver = preparedStatementForAdditionalDriver.executeQuery();
					List<AdditionalDriverDetail> additionalDriverList = new ArrayList<AdditionalDriverDetail>();
					while (rSetForAdditionalDriver.next()) {		
						AdditionalDriverDetail additionalDriverDetail = new AdditionalDriverDetail();
						additionalDriverDetail.setDriverId(rSetForAdditionalDriver.getString("DRIVERINSTANCEID"));
						additionalDriverList.add(additionalDriverDetail);
					}
					nasAuthDetail.setAdditionalDriverDetails(additionalDriverList);
				}finally{
					DBUtility.closeQuietly(rSetForAdditionalDriver);
					DBUtility.closeQuietly(preparedStatementForAdditionalDriver);
				}
				/* reading additional driver  details complete.*/
				
				
				
				/* reading Acct driver details */
				PreparedStatement preparedStatementForAcctDriver = null;
				ResultSet rSetForAcctDriver = null;
				try{
					query = "SELECT driverinstanceid ,weightage FROM tblmnaspolicyacctdriverrel WHERE naspolicyid=?";
					preparedStatementForAcctDriver = conn.prepareStatement(query);
					preparedStatementForAcctDriver.setString(1, policyId);
					rSetForAcctDriver = preparedStatementForAcctDriver.executeQuery();			
					ArrayList<PrimaryDriverDetail> primaryAcctDrivers = new ArrayList<PrimaryDriverDetail>();
					while (rSetForAcctDriver.next()) {					
						PrimaryDriverDetail primaryDriverDetail = new PrimaryDriverDetail();
						primaryDriverDetail.setDriverInstanceId(rSetForAcctDriver.getString("driverinstanceid"));
						primaryDriverDetail.setWeightage(rSetForAcctDriver.getInt("weightage"));
						primaryAcctDrivers.add(primaryDriverDetail);
					}
					nasAcctDetail.setPrimaryDriverDetails(primaryAcctDrivers);
				}finally{
					DBUtility.closeQuietly(rSetForAcctDriver);
					DBUtility.closeQuietly(preparedStatementForAcctDriver);
				}
				/* reading Acct driver details complete.*/
				
				tempNASPolicyList.add(nasPolicyConfigurationData);
				tempNASPolicyMap.put(nasPolicyConfigurationData.getId(), nasPolicyConfigurationData);
				
				nasPolicyConfigurationData.setSessionManagementEnabled(Strings.toBoolean(resultSet.getString("SESSIONMANAGEMENT")));
				
				if (DBUtility.isValueAvailable(resultSet, "DIACONCONFIGID")) {
					nasPolicyConfigurationData.setDiameterConcurrencyConfigId(resultSet.getString("DIACONCONFIGID"));
				}
				if (DBUtility.isValueAvailable(resultSet, "ADDIDIACONCONFIGID")) {
					nasPolicyConfigurationData.setAdditionalDiameterConcuurencyConfigId(resultSet.getString("ADDIDIACONCONFIGID"));
				}
			}
			this.policyList = tempNASPolicyList;
			this.nasServicePolicyConfigurationMap = tempNASPolicyMap;
		} finally {
			DBUtility.closeQuietly(rsForResponseAttribute);
			DBUtility.closeQuietly(psForResponseAttribute);
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(psForPolicy);
			DBUtility.closeQuietly(conn);
		}		
	}
	
	private void readNasAcctFlowPlugins(NasAcctDetail acctDetail, Connection conn, String policyId) throws SQLException {
		
		String queryForPlugin = getQueryForPlugins("TBLMNASPOLICYACCTPLUGINCONFIG", policyId);
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
			
			acctDetail.setPrePlugins(prePlugins);
			acctDetail.setPostPlugins(postPlugins);
		} finally {
			DBUtility.closeQuietly(pluginResultSet);
			DBUtility.closeQuietly(pluginStmt);
		}
	}

	private void readNasAuthFlowPlugins(NasAuthDetail authDetail, Connection conn, String policyId) throws SQLException {
		String queryForPlugin = getQueryForPlugins("TBLMNASPOLICYAUTHPLUGINCONFIG", policyId);
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
			
			authDetail.setPrePlugins(prePlugins);
			authDetail.setPostPlugins(postPlugins);
		} finally {
			DBUtility.closeQuietly(pluginResultSet);
			DBUtility.closeQuietly(pluginStmt);
		}
	}

	private String getQueryForPlugins(String tableName, String nasPolicyId) {
		return "SELECT * FROM " + tableName + " WHERE NASPOLICYID = '" + nasPolicyId + "'";
	}
	
	private void readDefaultSessionTimeout(ResultSet resultSet,
			NasServicePolicyConfigurationData nasServicePolicyConfigurationData) throws SQLException {
		if (DBUtility.isValueAvailable(resultSet, "DEFAULTSESSIONTIMEOUT")) {
			nasServicePolicyConfigurationData.getAuthorizationHandlerData().setDefaultSessionTimeoutInSeconds(Numbers.parseLong(resultSet.getString("DEFAULTSESSIONTIMEOUT"), AAAServerConstants.POLICY_DEFAULT_SESSION_TIMEOUT_IN_SECS));
		} else {
			LogManager.getLogger().info(MODULE, "Default session timeout not provided for policy: " + nasServicePolicyConfigurationData.getName() 
					+ ", will use default value: " + nasServicePolicyConfigurationData.getAuthorizationHandlerData().getDefaultSessionTimeoutInSeconds());
		}
	}

	private void readCuiConfiguration(ResultSet resultSet,
			NasServicePolicyConfigurationData nasServicePolicyConfigurationData) throws SQLException {
		ChargeableUserIdentityConfiguration cuiConfiguration = new ChargeableUserIdentityConfiguration();
		cuiConfiguration.setCui(resultSet.getString("CUI"));
		cuiConfiguration.setExpression(resultSet.getString("ADVANCEDCUIEXPRESSION"));
		cuiConfiguration.setAuthenticationCuiAttribute(resultSet.getString("CUIRESPONSEATTRIBUTES"));
		nasServicePolicyConfigurationData.setCuiConfiguration(cuiConfiguration);
	}
	
	private String getQueryForResponseAttributes() {
		return "SELECT COMMANDCODES, RESPONSEATTRIBUTES FROM TBLMNASPOLICYRESPATTRREL WHERE NASPOLICYID=?";
	}

	@DBReload
	public void reloadNASPolicyConfiguration() throws Exception{

		int size = this.policyList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select * from tblmnaspolicy where NASPOLICYID IN (");

		for(int i = 0; i < size-1; i++){
			NasServicePolicyConfigurationData nasAppPolicyConfigurationData = (NasServicePolicyConfigurationData)policyList.get(i);
			queryBuilder.append("'" + nasAppPolicyConfigurationData.getId() + "',");
		}
		queryBuilder.append("'" + ((NasServicePolicyConfigurationData)policyList.get(size - 1)).getId() + "')");
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
				
				DiameterSubscriberProfileRepositoryDetails sprDetails = new DiameterSubscriberProfileRepositoryDetails();
				NasServicePolicyConfigurationData nasAppPolicyConfData = (NasServicePolicyConfigurationData) nasServicePolicyConfigurationMap.get(resultSet.getInt("NASPOLICYID"));

				/*Reading Policy Detail */

				String policyName = nasAppPolicyConfData.getName();

				DiameterAuthenticationHandlerData diameterAuthenticationHandlerData = nasAppPolicyConfData.getAuthenticationHandlerData();
				
				List<String> getMultipleId = new ArrayList<String>();
				getMultipleId.add(resultSet.getString("MULTIPLEUID"));
				sprDetails.setUserIdentities(getMultipleId);
			
				nasAppPolicyConfData.setRuleSet(resultSet.getString("RULESET")); 


				if(Strings.isNullOrEmpty(resultSet.getString("CASESENSITIVEUID")) == false)
					sprDetails.getUpdateIdentity().setCase(parseInt(resultSet.getString("CASESENSITIVEUID").trim(), sprDetails.getUpdateIdentity().getCase()));
				else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Select Case Parameter for NAS Application Policy : "+policyName+" is not defined ,using default value");
				}

				boolean isStripEnabled = false;
				if (Strings.isNullOrEmpty(resultSet.getString("STRIPUSERIDENTITY")) == false){
					 isStripEnabled = Boolean.parseBoolean(resultSet.getString("STRIPUSERIDENTITY").trim());
				} 

				if (Strings.isNullOrEmpty(resultSet.getString("REALMPATTERN")) == false){
					if(isStripEnabled) {
						sprDetails.getUpdateIdentity().setStripIdentity(resultSet.getString("REALMPATTERN"));
					} else {
						sprDetails.getUpdateIdentity().setStripIdentity(StripUserIdentityStrategy.NONE);
					}
				}	

				if (Strings.isNullOrEmpty(resultSet.getString("REALMSEPARATOR")) == false)
					sprDetails.getUpdateIdentity().setSeparator(resultSet.getString("REALMSEPARATOR"));


				if (Strings.isNullOrEmpty(resultSet.getString("TRIMUSERIDENTITY")) == false)
					sprDetails.getUpdateIdentity().setIsTrimIdentity(Boolean.parseBoolean(resultSet.getString("TRIMUSERIDENTITY").trim()));

				if (Strings.isNullOrEmpty(resultSet.getString("TRIMPASSWORD")) == false){
					sprDetails.getUpdateIdentity().setIsTrimPassword(Boolean.parseBoolean(resultSet.getString("TRIMPASSWORD").trim()));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Trim Password Parameter for NAS Application Policy : "+policyName+" is not defined ,using default value");
				}

				if (Strings.isNullOrEmpty(resultSet.getString("USERNAME")) == false)
					sprDetails.setUserName(resultSet.getString("USERNAME"));

				String userNameResponseAttributeList = resultSet.getString("usernameresponseattributes");
				sprDetails.setUserNameResponseAttribute(userNameResponseAttributeList);

				readAnonymousProfileIdentity(resultSet, sprDetails);
				
				diameterAuthenticationHandlerData.setSubscriberProfileRepositoryDetails(sprDetails);
				
				readWimaxEnabled(resultSet, nasAppPolicyConfData);
				
				readGracePolicy(resultSet, nasAppPolicyConfData);
				
				readCuiConfiguration(resultSet, nasAppPolicyConfData);
				
				if (Strings.isNullOrEmpty(resultSet.getString("REJECTONCHECKITEMNOTFOUND")) == false){
					nasAppPolicyConfData.getAuthorizationHandlerData().setRejectOnCheckItemNotFound(Boolean.parseBoolean(resultSet.getString("REJECTONCHECKITEMNOTFOUND").trim()));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Reject On Check Item Not Found Parameter for NAS Application Policy : "+policyName+" is not defined ,using default value " + nasAppPolicyConfData.getAuthorizationHandlerData().isRejectOnCheckItemNotFound());
				}

				if (Strings.isNullOrEmpty(resultSet.getString("REJECTONREJECTITEMNOTFOUND")) == false){
					nasAppPolicyConfData.getAuthorizationHandlerData().setRejectOnRejectItemNotFound(Boolean.parseBoolean(resultSet.getString("REJECTONREJECTITEMNOTFOUND").trim()));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Reject On Reject Item Not Found  Parameter for NAS Application Policy : "+policyName+" is not defined ,using default value " + nasAppPolicyConfData.getAuthorizationHandlerData().isRejectOnRejectItemNotFound());
				}

				if (Strings.isNullOrEmpty(resultSet.getString("ACTIONONPOLICYNOTFOUND")) == false){
					int iActionOnPolicyNotFound = resultSet.getInt("ACTIONONPOLICYNOTFOUND");
					nasAppPolicyConfData.getAuthorizationHandlerData().setContinueOnPolicyNotFound(iActionOnPolicyNotFound == IGNORE_ON_POLICY_NOT_FOUND);
				}

				psForResponseAttribute = conn.prepareStatement(getQueryForResponseAttributes());
				psForResponseAttribute.setString(1, nasAppPolicyConfData.getId());
				rsForResponseAttribute = psForResponseAttribute.executeQuery();
				
				ArrayList<CommandCodeResponseAttribute> commandCodeResponseAttributes = new ArrayList<CommandCodeResponseAttribute>();
				CommandCodeResponseAttribute commandCodeResponseAttribute = null;
				while (rsForResponseAttribute.next()) {					
					commandCodeResponseAttribute = new CommandCodeResponseAttribute();
					commandCodeResponseAttribute.setCommandCodes(rsForResponseAttribute.getString("COMMANDCODES"));
					commandCodeResponseAttribute.setResponseAttributes(rsForResponseAttribute.getString("RESPONSEATTRIBUTES"));
					commandCodeResponseAttributes.add(commandCodeResponseAttribute);
				}
				nasAppPolicyConfData.setCommandCodeResponseAttributesList(commandCodeResponseAttributes);

			}
		} finally{
			DBUtility.closeQuietly(rsForResponseAttribute);
			DBUtility.closeQuietly(psForResponseAttribute);
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(conn);
		}
	}

	private void readGracePolicy(ResultSet resultSet,
			NasServicePolicyConfigurationData nasAppPolicyConfData)
			throws SQLException {
		if (DBUtility.isValueAvailable(resultSet, "GRACEPOLICY")) {
			nasAppPolicyConfData.getAuthorizationHandlerData().setGracePolicy(resultSet.getString("GRACEPOLICY").trim());
		}
	}

	private void readWimaxEnabled(ResultSet resultSet,
			NasServicePolicyConfigurationData nasAppPolicyConfData)
			throws SQLException {
		if (DBUtility.isValueAvailable(resultSet, "WIMAX")) {
			nasAppPolicyConfData.getAuthorizationHandlerData().setWimaxEnabled(Strings.toBoolean(resultSet.getString("WIMAX")));
		}
	}
	
	private void readAnonymousProfileIdentity(ResultSet resultSet,
			DiameterSubscriberProfileRepositoryDetails diameterSubscriberProfileRepositoryDetails)
			throws SQLException {
		if (DBUtility.isValueAvailable(resultSet, "ANONYMOUSIDENTITY")) {
			diameterSubscriberProfileRepositoryDetails.setAnonymousProfileIdentity(resultSet.getString("ANONYMOUSIDENTITY").trim());
		}
	}

	@PostRead
	public void postReadProcessing() {
		if(this.policyList!=null){
			int numOfPolicy = policyList.size();
			for(int i=0;i<numOfPolicy;i++){
				NasServicePolicyConfigurationData nasAppPolicyConfigurationData = (NasServicePolicyConfigurationData)policyList.get(i);
				
				postProcessingForMultipleUserIdentities(nasAppPolicyConfigurationData);
				
				postProcessingForCUIResponseAttributes(nasAppPolicyConfigurationData);
				
				postProcessingForUserNameResponseAttributes(nasAppPolicyConfigurationData);
				
				List<PrimaryDriverDetail> primaryAuthDriverList = nasAppPolicyConfigurationData.getNasAuthDetail().getPrimaryDriverDetails();
				Map<String, Integer> authDriverMap = new LinkedHashMap<String, Integer>();
				if(primaryAuthDriverList!=null){
					for(int j=0;j<primaryAuthDriverList.size();j++){
						authDriverMap.put(primaryAuthDriverList.get(j).getDriverInstanceId(), primaryAuthDriverList.get(j).getWeightage());
					}
				}
				nasAppPolicyConfigurationData.setAuthDriverInstanceIdsMap(authDriverMap);
				
				List<AdditionalDriverDetail> additionalAuthDrivers = nasAppPolicyConfigurationData.getNasAuthDetail().getAdditionalDriverDetails();
				List<String> intAdditionalAuthDriverList = new ArrayList<String>();
				if(additionalAuthDrivers!=null){
					for(int j=0;j<additionalAuthDrivers.size();j++){
						intAdditionalAuthDriverList.add(additionalAuthDrivers.get(j).getDriverId());
					}
				}
				nasAppPolicyConfigurationData.setAdditionalDriversList(intAdditionalAuthDriverList);
				
				List<PrimaryDriverDetail> primaryAcctDriverList = nasAppPolicyConfigurationData.getNasAcctDetail().getPrimaryDriverDetails();
				Map<String, Integer> acctDriverMap = new LinkedHashMap<String, Integer>();
				if(primaryAcctDriverList!=null){
					for(int j=0;j<primaryAcctDriverList.size();j++){
						acctDriverMap.put(primaryAcctDriverList.get(j).getDriverInstanceId(), primaryAcctDriverList.get(j).getWeightage());
					}
				}
				nasAppPolicyConfigurationData.setAcctDriverInstanceIdsMap(acctDriverMap);
			
				nasAppPolicyConfigurationData.getAuthenticationHandlerData()
					.getSubscriberProfileRepositoryDetails().postResponseProcessingForUserNameResponeAttribute();
				
				/*List<Integer> enableAuthMethods = nasAppPolicyConfigurationData.getAuthenticationHandlerData().getAutheMethodHandlerTypes();
				if(enableAuthMethods==null || !(enableAuthMethods.size()>0)){
					nasAppPolicyConfigurationData.getDiameterNASAuthParamsDetails().setAuthMethodHandlerTypes(getDefaultAuthMethodHandlers());
				}*/
				postProcessingForRealmPatternAndSeparator(nasAppPolicyConfigurationData);
				postReadProcessingForResponseAttributes(nasAppPolicyConfigurationData);
				
				registerPlugins(nasAppPolicyConfigurationData);
			}
		}
	}

	private void registerPlugins(NasServicePolicyConfigurationData data) {
		String policyName = data.getName();

		NasAuthDetail nasAuthDetail = data.getNasAuthDetail();
		
		List<PluginEntryDetail> authPrePlugins = nasAuthDetail.getPrePlugins();
		setPluginsCallerId(policyName, authPrePlugins, PluginMode.IN, ServicePolicyFlow.AUTH_FLOW);
		((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(authPrePlugins);
		
		List<PluginEntryDetail> authPostPlugins = nasAuthDetail.getPostPlugins();
		setPluginsCallerId(policyName, authPostPlugins, PluginMode.OUT, ServicePolicyFlow.AUTH_FLOW);
		((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(authPostPlugins);
		
		
		NasAcctDetail nasAcctDetail = data.getNasAcctDetail();
		
		List<PluginEntryDetail> acctPrePlugins = nasAcctDetail.getPrePlugins();
		setPluginsCallerId(policyName, acctPrePlugins, PluginMode.IN, ServicePolicyFlow.ACCT_FLOW);
		((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(acctPrePlugins);
		
		List<PluginEntryDetail> acctPostPlugins = nasAcctDetail.getPostPlugins();
		setPluginsCallerId(policyName, acctPostPlugins, PluginMode.OUT, ServicePolicyFlow.ACCT_FLOW);
		((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(acctPostPlugins);
	}
	
	private void setPluginsCallerId(String policyName, List<PluginEntryDetail> plugins, PluginMode mode, ServicePolicyFlow policyFlow) {
		for (int index = 0; index < plugins.size(); index++) {
			PluginEntryDetail data = plugins.get(index);
			PluginCallerIdentity key = PluginCallerIdentity.createAndGetIdentity(ServiceTypeConstants.DIA_EAP, mode, index, data.getPluginName())
					.setServicePolicyName(policyName).setServicePolicyFlow(policyFlow).getId();
			data.setCallerId(key);
		}
	}
	
	private void postReadProcessingForResponseAttributes(NasServicePolicyConfigurationData nasAppPolicyConfigurationData) {
		
		List<CommandCodeResponseAttribute> commandCodeResponseAttributeList = nasAppPolicyConfigurationData.getCommandCodeResponseAttributesList();
		if(Collectionz.isNullOrEmpty(commandCodeResponseAttributeList)){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "No Command-Codes wise Response Attributes configured for NAS Policy: " + nasAppPolicyConfigurationData.getName());
			}
			return;
		}
		
		Map<Integer, AdditionalResponseAttributes> commandCodeToAdditionalAttrMap = new HashMap<Integer, AdditionalResponseAttributes>();
		for(int i = 0 ; i < commandCodeResponseAttributeList.size() ; i++){
			
			String resposeAttributes = commandCodeResponseAttributeList.get(i).getResponseAttributes();
			if(Strings.isNullOrBlank(resposeAttributes)){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "No response attributes configured for Command-Codes: " + commandCodeResponseAttributeList.get(i).getCommandCodes() + 
							" for NAS Policy: " + nasAppPolicyConfigurationData.getName());
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
								" for NAS Policy: " + nasAppPolicyConfigurationData.getName());
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
								" for NAS Policy: " + nasAppPolicyConfigurationData.getName() + 
								", Reason: " + e.getMessage());
					}
				}
			}
		}
		
		nasAppPolicyConfigurationData.setCommandCodeResponseAttributesMap(commandCodeToAdditionalAttrMap);
		
	}

	private void postProcessingForUserNameResponseAttributes(NasServicePolicyConfigurationData nasAppPolicyConfigurationData) {

		String userNameResponseAttribute = nasAppPolicyConfigurationData.getAuthenticationHandlerData().getSubscriberProfileRepositoryDetails().getUserNameResponseAttribute();
		if (Strings.isNullOrBlank(userNameResponseAttribute) == false) {
			ArrayList<String> userNameResponseAttributeList = new ArrayList<String>();
			for (String userNameAttr : userNameResponseAttribute.split(",")) {
				userNameResponseAttributeList.add(userNameAttr);
			}
			nasAppPolicyConfigurationData.setUserNameRespAttrList(userNameResponseAttributeList);
		}
	}

	private void postProcessingForCUIResponseAttributes(NasServicePolicyConfigurationData nasAppPolicyConfigurationData) {
		nasAppPolicyConfigurationData.getCuiConfiguration().postRead();
	}

	private void postProcessingForMultipleUserIdentities(
			NasServicePolicyConfigurationData nasAppPolicyConfigurationData) {
		
		List<String> multipleUserId = nasAppPolicyConfigurationData.getAuthenticationHandlerData().getSubscriberProfileRepositoryDetails().getUserIdentities();
		
		for (String userId : multipleUserId) {
			if (Strings.isNullOrBlank(userId)) {
				userId = "0:1";
			}
			ArrayList<String> userIdentityAttrList = new ArrayList<String>();
			String[] multipleUserIds = userId.split(",");
			for (String userIdentity : multipleUserIds) {
				userIdentityAttrList.add(userIdentity);
			}
			nasAppPolicyConfigurationData.setUserIdentities(userIdentityAttrList);
		}
	}

	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		if(this.policyList!=null){
			int numOfPolicy = policyList.size();
			for(int i=0;i<numOfPolicy;i++){
				NasServicePolicyConfigurationData nasAppPolicyConfigurationData = (NasServicePolicyConfigurationData)policyList.get(i);
				
				postProcessingForMultipleUserIdentities(nasAppPolicyConfigurationData);
				
				postProcessingForCUIResponseAttributes(nasAppPolicyConfigurationData);
				
				postProcessingForUserNameResponseAttributes(nasAppPolicyConfigurationData);
				postProcessingForRealmPatternAndSeparator(nasAppPolicyConfigurationData);
				postReadProcessingForResponseAttributes(nasAppPolicyConfigurationData);
			
			}
		}
	}
	
	
	@XmlElement(name="nas-policy",type=NasServicePolicyConfigurationData.class)
	public List<DiameterServicePolicyConfiguration> getPolicyList() {
		return policyList;
	}

	public void setPolicyList(List<DiameterServicePolicyConfiguration> policyList) {
		this.policyList = policyList;
	}

	@XmlTransient
	private ArrayList<Integer> getDefaultAuthMethodHandlers() {
		ArrayList<Integer> defaultAuthMethodHandlers = new ArrayList<Integer>();
		
			defaultAuthMethodHandlers.add(AuthMethods.PAP);
			defaultAuthMethodHandlers.add(AuthMethods.CHAP);
		return defaultAuthMethodHandlers;
	}

	private String getQueryForServicePolicyConfig() {
		DiameterNasServiceConfigurable nasApplicationConfigurable = getConfigurationContext().get(DiameterNasServiceConfigurable.class);
		List<String> servicePolicies = nasApplicationConfigurable.getServicePolicies();
		if(servicePolicies==null || !(servicePolicies.size()>0) || servicePolicies.contains(AAAServerConstants.ALL))
			return "select * from tblmnaspolicy where STATUS=? ORDER BY ORDERNUMBER";
		else{
			String query = "select * from tblmnaspolicy where STATUS=? AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+") ORDER BY ( CASE ";

			String caseString = "";
			int numOfPolicy = servicePolicies.size();
			for(int i=0;i<numOfPolicy;i++){
				caseString = caseString + " WHEN NAME = '"+servicePolicies.get(i)+"' THEN "+i;
			}
			query = query+caseString+" END )";

			return query;
		}
	}

	private void postProcessingForRealmPatternAndSeparator(NasServicePolicyConfigurationData nasAppPolicyConfigurationData) {
		if (nasAppPolicyConfigurationData.getAuthenticationHandlerData().getSubscriberProfileRepositoryDetails().getUpdateIdentity().getSeparator() == null) {
			nasAppPolicyConfigurationData.getAuthenticationHandlerData().getSubscriberProfileRepositoryDetails().getUpdateIdentity().setStripIdentity(StripUserIdentityStrategy.NONE);
		}

	}

	@XmlTransient
	public Map<String, DiameterServicePolicyConfiguration> getNASServicePolicyConfigurationMap() {
		return this.nasServicePolicyConfigurationMap;		
	}


	public DiameterServicePolicyConfiguration getNASServicePolicyConfiguration(String policyId) {
		return nasServicePolicyConfigurationMap.get(policyId);		
	}
}
