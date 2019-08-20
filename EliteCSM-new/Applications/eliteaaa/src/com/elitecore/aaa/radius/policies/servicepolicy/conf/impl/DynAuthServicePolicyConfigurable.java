package com.elitecore.aaa.radius.policies.servicepolicy.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.data.DBFieldDetail;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.radius.conf.impl.RadDynAuthServiceConfigurable;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.DynAuthServicePolicyConfiguration;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.impl.DynAuthServicePolicyConfigurationData.DBFailureAction;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CommunicatorData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CommunicatorGroupData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.aaa.radius.service.dynauth.handlers.conf.StaticNasCommunicationHandlerData;
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
import com.elitecore.core.commons.util.ConfigurationUtil;

@XmlType(propOrder = {})
@XmlRootElement(name = "dynauth-service-policies")
@ConfigurationProperties(moduleName ="DYNAUTH-POLICY-CONFIGURABLE", readWith = DBReader.class, synchronizeKey = "", writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db","services","dynauth","servicepolicy"},name = "dynauth-service-policy")
public class DynAuthServicePolicyConfigurable extends Configurable{

	private List<DynAuthServicePolicyConfigurationData> policyConfList;
	private Map<String, DynAuthServicePolicyConfiguration> dynAuthServicePolicyConfigurationMap;
	private static final String MODULE = "DYNAUTH-POLICY-CONFIGURABLE";
	
	public DynAuthServicePolicyConfigurable() {
		this.policyConfList = new ArrayList<DynAuthServicePolicyConfigurationData>();
		this.dynAuthServicePolicyConfigurationMap = new LinkedHashMap<String, DynAuthServicePolicyConfiguration>();
	}
	
	@DBRead
	public void readDynAuthServicePolicyConfiguration() throws Exception {

		Connection conn = null;
		PreparedStatement psForRadAuthId =null;
		ResultSet resultSet = null;

		PreparedStatement psForFieldMapping = null;
		ResultSet rsForFieldMapping = null;
		
		
		PreparedStatement psDatasourceName=null;
		ResultSet rsDatasourceName=null;
		
		PreparedStatement psForNasCommDetail = null;
		ResultSet resultSetForNasCommDetail = null;

		PreparedStatement psTranslationMappingName = null;
		ResultSet resultSetForTranslationMapping = null;
		
		PreparedStatement psForCommunicatorData = null;
		ResultSet resultSetForCommunicatorData = null;
		
		try {
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			List<DynAuthServicePolicyConfigurationData> dynAuthServicePolicyList = new ArrayList<DynAuthServicePolicyConfigurationData>();

			String query = getQueryForServicePolicyConfig();
			psForRadAuthId = conn.prepareStatement(query);
			psForRadAuthId.setString(1,CommonConstants.DATABASE_POLICY_STATUS_ACTIVE );			
			resultSet=psForRadAuthId.executeQuery();
			DynAuthServicePolicyConfigurationData dynAuthServicePolicyConfData = null;				

			while(resultSet.next()){
				dynAuthServicePolicyConfData = new DynAuthServicePolicyConfigurationData();
				String policyId = resultSet.getString("DYNAUTHPOLICYID");
				dynAuthServicePolicyConfData.setPolicyId(policyId);

				String policyName = resultSet.getString("NAME");
				dynAuthServicePolicyConfData.setPolicyName(policyName);
				dynAuthServicePolicyConfData.setRadiusRuleSet(resultSet.getString("RULESET"));
				String dataSourceId = resultSet.getString("DATABASEDSID");
				
				psDatasourceName = conn.prepareStatement("select NAME from tblmdatabaseds where databasedsid=?");
				psDatasourceName.setString(1,dataSourceId);
				rsDatasourceName = psDatasourceName.executeQuery();
				if(rsDatasourceName.next()){
					dynAuthServicePolicyConfData.setDataSourceName(rsDatasourceName.getString("NAME"));
				}
						

				dynAuthServicePolicyConfData.setIsValidatePacket(ConfigurationUtil.stringToBoolean(resultSet.getString("VALIDATEPACKET"),dynAuthServicePolicyConfData.getIsValidatePacket()));

				String tableName = "TBLMCONCURRENTUSERS";
				if(resultSet.getString("TABLENAME")!=null && resultSet.getString("TABLENAME").trim().length()>0){
					tableName = resultSet.getString("TABLENAME").trim();
				}
				dynAuthServicePolicyConfData.setTableName(tableName);


				if(resultSet.getString("ELIGIBLESESSION")!=null && resultSet.getString("ELIGIBLESESSION").trim().length()>0){
					dynAuthServicePolicyConfData.setEligibleSession(Numbers.parseInt(resultSet.getString("ELIGIBLESESSION").trim(), dynAuthServicePolicyConfData.getEligibleSession()));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Eligible session Parameter for Policy:"+policyName+" not defined ,using default value");
				}
				
				String dbFailureActionString = resultSet.getString("DBFAILUREACTION");
				if (Strings.isNullOrBlank(dbFailureActionString) == false) {
					dynAuthServicePolicyConfData.setDbFailureAction(DBFailureAction.from(dbFailureActionString));
				} else {
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "DB failure action Parameter for Policy:" + policyName + " not defined ,using default value NAK");
					}
				}

				if(resultSet.getString("EVENTTIMESTAMP")!=null && resultSet.getString("EVENTTIMESTAMP").trim().length()>0){
					dynAuthServicePolicyConfData.setEventTimestampValue(Numbers.parseLong(resultSet.getString("EVENTTIMESTAMP").trim(), dynAuthServicePolicyConfData.getEventTimestampValue()));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Event Time Stamp Parameter for Policy:"+policyName+" not defined ,using default value");
				}

				dynAuthServicePolicyConfData.setResponseAttributeStr(resultSet.getString("RESPONSEATTRIBUTES"));

				/**
				 * Read configuration of NAS Communicator.
				 */
				
				String queryForNasCommDetail = "SELECT * FROM TBLMDYNAUTHPOLICYNASCLIENTS WHERE DYNAUTHPOLICYID = ? ORDER BY ORDERNUMBER";
				
				psForNasCommDetail = conn.prepareStatement(queryForNasCommDetail);
				psForNasCommDetail.setString(1, policyId);
				
				resultSetForNasCommDetail = psForNasCommDetail.executeQuery();

				StaticNasCommunicationHandlerData communicationHandlerData = new StaticNasCommunicationHandlerData();

				while (resultSetForNasCommDetail.next()) {
					
					ExternalCommunicationEntryData externalCommEntryData = new ExternalCommunicationEntryData();
					externalCommEntryData.setPolicyName(policyName);
					externalCommEntryData.setRuleset(resultSetForNasCommDetail.getString("RULESET"));
					externalCommEntryData.setScript(resultSetForNasCommDetail.getString("SCRIPT"));
					String translationMappingID = resultSetForNasCommDetail.getString("TRANSLATIONMAPCONFIGID");
					String copyPacketConfId = resultSetForNasCommDetail.getString("COPYPACKETMAPCONFID");
					
					if(Strings.isNullOrEmpty(translationMappingID) == false){
						psTranslationMappingName = conn.prepareStatement(getQueryForTransMapName());
						psTranslationMappingName.setString(1, translationMappingID);
						resultSetForTranslationMapping = psTranslationMappingName.executeQuery();
						if(resultSetForTranslationMapping.next()){
							externalCommEntryData.setTranslationMapping(resultSetForTranslationMapping.getString("NAME"));
						}
					} else if(Strings.isNullOrEmpty(copyPacketConfId) == false) {
						psTranslationMappingName = conn.prepareStatement(getQueryForCopyPacketName());
						psTranslationMappingName.setString(1, copyPacketConfId);
						resultSetForTranslationMapping = psTranslationMappingName.executeQuery();
						if(resultSetForTranslationMapping.next()){
							externalCommEntryData.setTranslationMapping(resultSetForTranslationMapping.getString("NAME"));
						}
					} else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
							LogManager.getLogger().debug(MODULE, "No Translation Mapping configured for Dyna Auth NAS Communication Group in Dyna-Auth Policy: " + policyName );
						}
					}
					
					String dynauthNasID = resultSetForNasCommDetail.getString("DYNAAUTHNASID");
					String queryForCommunicatorData = "SELECT * FROM TBLMDYNAUTHNASCLIENTDETAIL WHERE DYNAAUTHNASID = ?";
					
					psForCommunicatorData = conn.prepareStatement(queryForCommunicatorData);
					psForCommunicatorData.setString(1, dynauthNasID);
					
					resultSetForCommunicatorData = psForCommunicatorData.executeQuery();
					
					/**
					 * read ESIINSTANCE,LOADFACTOR of NAS Communicator.
					 */
					CommunicatorGroupData communicatorGroupData = new CommunicatorGroupData();
					while ( resultSetForCommunicatorData.next() ) {
						CommunicatorData communicatorData = new CommunicatorData();
						communicatorData.setId(resultSetForCommunicatorData.getString("ESIINSTANCEID"));
						communicatorData.setLoadFactor(resultSetForCommunicatorData.getInt("LOADFACTOR"));
						communicatorGroupData.getCommunicatorDataList().add(communicatorData);
					}
					
					externalCommEntryData.setCommunicatorGroupData(communicatorGroupData);
					communicationHandlerData.getProxyCommunicatioEntries().add(externalCommEntryData);
				}
				dynAuthServicePolicyConfData.setCommunicatorData(communicationHandlerData);
				query = "SELECT * FROM TBLMDYNAUTHPOLICYFIELDMAP WHERE DYNAUTHPOLICYID = ?";
				psForFieldMapping = conn.prepareStatement(query);
				psForFieldMapping.setString(1, policyId);
				rsForFieldMapping = psForFieldMapping.executeQuery();

				List<DBFieldDetail> tempDBFieldDetailList =  new ArrayList<DBFieldDetail>();

				String attributeId;
				String dbField;
				DBFieldDetail tempDBFieldDetail;


				while(rsForFieldMapping.next()){
					attributeId = rsForFieldMapping.getString("ATTRIBUTEID");
					dbField = rsForFieldMapping.getString("DBFIELD");

					if(attributeId!=null && attributeId.trim().length()>0 && dbField!=null && dbField.trim().length()>0){
						tempDBFieldDetail = new DBFieldDetail(dbField,attributeId,rsForFieldMapping.getString("DEFAULTVALUE"),Boolean.parseBoolean(rsForFieldMapping.getString("MANDATORY")));
						tempDBFieldDetailList.add(tempDBFieldDetail);
					}
				}

				dynAuthServicePolicyConfData.setDbFieldDetailList(tempDBFieldDetailList);
				dynAuthServicePolicyList.add(dynAuthServicePolicyConfData);
			}
			this.policyConfList = dynAuthServicePolicyList;
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(psForRadAuthId);
			DBUtility.closeQuietly(rsForFieldMapping);
			DBUtility.closeQuietly(psForFieldMapping);
			DBUtility.closeQuietly(rsDatasourceName);
			DBUtility.closeQuietly(psDatasourceName);
			DBUtility.closeQuietly(resultSetForCommunicatorData);
			DBUtility.closeQuietly(psForCommunicatorData);
			DBUtility.closeQuietly(resultSetForTranslationMapping);
			DBUtility.closeQuietly(psTranslationMappingName);
			DBUtility.closeQuietly(resultSetForNasCommDetail);
			DBUtility.closeQuietly(psForNasCommDetail);
			DBUtility.closeQuietly(conn);
		}		


	}

	
	@DBReload
	public void reloadDynAuthServicePolicyConfiguration() throws Exception{

		int size = this.policyConfList.size();
		if(size == 0){
			return;
		}

		StringBuilder queryBuilder = new StringBuilder("select * from TBLMDYNAUTHSERVICEPOLICY where DYNAUTHPOLICYID IN (");

		for(int i = 0; i < size-1; i++){
			DynAuthServicePolicyConfigurationData dynAuthServicePolicyData = policyConfList.get(i);
			queryBuilder.append("'" + dynAuthServicePolicyData.getPolicyId() + "',");
		}
		queryBuilder.append("'" + policyConfList.get(size - 1).getPolicyId() + "')");
		String queryForReload = queryBuilder.toString();

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try{
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = conn.prepareStatement(queryForReload);
			resultSet = preparedStatement.executeQuery();

			while(resultSet.next()){
				DynAuthServicePolicyConfigurationData dynAuthServicePolicyConfData = (DynAuthServicePolicyConfigurationData) this.dynAuthServicePolicyConfigurationMap.get(resultSet.getInt("DYNAUTHPOLICYID"));
				String policyName = dynAuthServicePolicyConfData.getPolicyName();

				dynAuthServicePolicyConfData.setRadiusRuleSet(resultSet.getString("RULESET"));

				dynAuthServicePolicyConfData.setIsValidatePacket(ConfigurationUtil.stringToBoolean(resultSet.getString("VALIDATEPACKET"),dynAuthServicePolicyConfData.getIsValidatePacket()));


				if(resultSet.getString("ELIGIBLESESSION")!=null && resultSet.getString("ELIGIBLESESSION").trim().length()>0){
					dynAuthServicePolicyConfData.setEligibleSession(Numbers.parseInt(resultSet.getString("ELIGIBLESESSION").trim(), dynAuthServicePolicyConfData.getEligibleSession()));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Eligible session Parameter for Policy:"+policyName+" not defined ,using default value");
				}

				if(resultSet.getString("EVENTTIMESTAMP")!=null && resultSet.getString("EVENTTIMESTAMP").trim().length()>0){
					dynAuthServicePolicyConfData.setEventTimestampValue(Numbers.parseLong(resultSet.getString("EVENTTIMESTAMP").trim(), dynAuthServicePolicyConfData.getEventTimestampValue()));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Event Time Stamp Parameter for Policy:"+policyName+" not defined ,using default value");
				}

				dynAuthServicePolicyConfData.setResponseAttributeStr(resultSet.getString("RESPONSEATTRIBUTES"));
			}
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(conn);
		}
	}
	
	@PostRead
	public void postReadProcessing() {
		if(this.policyConfList!=null){
			int numOfPolicy = policyConfList.size();
			for(int i=0;i<numOfPolicy;i++){
				DynAuthServicePolicyConfigurationData policyConfiguration = (DynAuthServicePolicyConfigurationData)policyConfList.get(i);
				postProcessingForConcurrentDBQuery(policyConfiguration);
				dynAuthServicePolicyConfigurationMap.put(policyConfiguration.getPolicyId(), policyConfiguration);
			}
		}
		
		
	}

	private void postProcessingForConcurrentDBQuery(
			DynAuthServicePolicyConfigurationData policyConfiguration) {
		// set concurrent db query
		List<DBFieldDetail> tempDBFieldDetailList = policyConfiguration.getDbFieldDetailList();
		StringBuilder dBQuery = new StringBuilder("SELECT ");
		String tableName = "TBLMCONCURRENTUSERS";
		if(policyConfiguration.getTableName()!=null && policyConfiguration.getTableName().trim().length()>0){
			tableName = policyConfiguration.getTableName();
		}
		if(tempDBFieldDetailList!=null && tempDBFieldDetailList.size()>0){
			int numOFAttr  = tempDBFieldDetailList.size();
			for(int j=0;j< numOFAttr;j++){
				dBQuery.append(tempDBFieldDetailList.get(j).getDbField());
				if(!(j==numOFAttr-1)){
					dBQuery.append(",");	
				}
			}
			dBQuery.append(" FROM "+tableName+" where USER_NAME=? and ACCT_SESSION_ID like ? order by START_TIME");
		}else{
			dBQuery.append("ACCT_SESSION_ID,NAS_IP_ADDRESS FROM "+tableName+" where USER_NAME=? and ACCT_SESSION_ID like ? order by START_TIME");
		}
		String concurrentDBQuery = dBQuery.toString();
		((DynAuthServicePolicyConfigurationData)policyConfiguration).setConcurrentDBQuery(concurrentDBQuery);
		
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
			}
	
	@XmlElement(name="dynauth-service-policy")
	public List<DynAuthServicePolicyConfigurationData> getServiceConfigurations() {
		return this.policyConfList;
	}
	
	private String getQueryForTransMapName(){
		return "select A.name from tblmtranslationmappingconf A where A.transmapconfid = ?";
	}
	
	private String getQueryForCopyPacketName() {
		return "select A.name from TBLMCOPYPACKETTRANSMAPCONF A where A.COPYPACKETMAPCONFID = ?";
	}
	
	private String getQueryForServicePolicyConfig() {
		
		RadDynAuthServiceConfigurable dynauthServiceConfigurable = getConfigurationContext().get(RadDynAuthServiceConfigurable.class);
		
		List<String> servicePolicies = dynauthServiceConfigurable.getServicePolicies();
		
		if (Collectionz.isNullOrEmpty(servicePolicies) || servicePolicies.contains(AAAServerConstants.ALL)) {
			return "select DYNAUTHPOLICYID,NAME,RULESET,RESPONSEATTRIBUTES,ELIGIBLESESSION,DBFAILUREACTION,VALIDATEPACKET,EVENTTIMESTAMP,DATABASEDSID,TABLENAME from TBLMDYNAUTHSERVICEPOLICY where STATUS=? ORDER BY ORDERNUMBER";
		} else {
			String query = "select DYNAUTHPOLICYID,NAME,RULESET,RESPONSEATTRIBUTES,ELIGIBLESESSION,DBFAILUREACTION,VALIDATEPACKET,EVENTTIMESTAMP,DATABASEDSID,TABLENAME from TBLMDYNAUTHSERVICEPOLICY where STATUS=? AND NAME IN ("+Strings.join(",", servicePolicies, Strings.WITHIN_SINGLE_QUOTES)+") ORDER BY ( CASE ";
			
			String caseString = "";
			int numOfPolicy = servicePolicies.size();
			for(int i=0;i<numOfPolicy;i++){
				caseString = caseString + " WHEN NAME = '"+servicePolicies.get(i)+"' THEN "+i;
		}
			query = query+caseString+" END )";
			
			return query;
		}
	}
	
	
	@XmlTransient
	public Map<String, DynAuthServicePolicyConfiguration> getDynAuthServicePolicyConfigurationMap() {
		return this.dynAuthServicePolicyConfigurationMap;		
	}

	
	public DynAuthServicePolicyConfiguration getDynAuthServicePolicyConfiguration(String policyId) {
		return dynAuthServicePolicyConfigurationMap.get(policyId);		
	}
}