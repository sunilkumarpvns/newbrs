package com.elitecore.aaa.radius.sessionx.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.radius.sessionx.conf.impl.LocalSessionManagerConfiguration.BehaviourType;
import com.elitecore.aaa.radius.sessionx.conf.impl.LocalSessionManagerConfiguration.DBFailureActions;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
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
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.ConfigurationUtil;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.impl.FieldMappingImpl;
import com.elitecore.core.serverx.sessionx.impl.SchemaMappingImpl;

@XmlType(propOrder = {})
@XmlRootElement(name = "local-session-manager-config")
@ConfigurationProperties(moduleName ="LOCAL_SESSION_MANAGER_CONFIGURABLE",synchronizeKey ="", readWith = DBReader.class, writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db"},name = "local-sesion-manager")
public class LocalSessionManagerConfigurable extends Configurable {

	private List<LocalSessionManager> localSessionManagerList;

	public LocalSessionManagerConfigurable(){
		localSessionManagerList = new ArrayList<LocalSessionManager>();
	}

	public List<LocalSessionManager> loadSessionMangaerConfiguration() throws Exception{

		Connection connection = null;
		PreparedStatement psForSmInstance = null;
		ResultSet rsForSmInstance = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		PreparedStatement psStatForMapping = null;
		ResultSet rsForMapping = null;
		PreparedStatement psForClosureMapping = null;
		ResultSet rsForClosureMapping = null;
		List<LocalSessionManager> localSessionManagerList = new ArrayList<LocalSessionManager>();

		try{
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = connection.prepareStatement(getQueryForLocalConfig());
			if(preparedStatement == null){
				throw new SQLException("Prepared statement problem at the time of fetching local session manager configuration");
			}

			psStatForMapping = connection.prepareStatement(getQueryForGettingMapping());				
			if(psStatForMapping == null){
				throw new SQLException("Prepared Statement problem at the time of fetching local session manager feild mapping configuration");
			}
			psForClosureMapping = connection.prepareStatement(getQueryForGettingClosureMapping());			

			psForSmInstance = connection.prepareStatement(getQueryForSM());
			if(psForSmInstance == null){
				throw new LoadConfigurationException("Prepared statement problem at the time of fetching local session manager configuration");
			}
			rsForSmInstance = psForSmInstance.executeQuery();
			LocalSessionManager localSesionManager =null;
			while(rsForSmInstance.next()){

				localSesionManager = new LocalSessionManager(rsForSmInstance.getString("SMINSTANCEID"));
				String smInstanceId = rsForSmInstance.getString("SMINSTANCEID");
			
				preparedStatement.setString(1, smInstanceId);
				preparedStatement.setString(2, smInstanceId);
				preparedStatement.setString(3, smInstanceId);
				result = preparedStatement.executeQuery();

				if(result.next()){
					// primary key ..
					String smConfigId =  result.getString("SMCONFIGID");
					localSesionManager.setSmConfigId(smConfigId);

					localSesionManager.setAutoSessionCloser(ConfigurationUtil.stringToBoolean(result.getString("AUTO_SESSION_CLOSER"),false));
					localSesionManager.setCloseBatchCount(result.getLong("CLOSE_BATCH_COUNT"));
					localSesionManager.setDatabaseDsId(result.getString("DATABASEDSID"));
					localSesionManager.setSessionThreadSleepTime(result.getLong("SESSION_THREAD_SLEEP_TIME"));
					localSesionManager.setSessionTimeout(result.getLong("SESSIONTIMEOUT"));
					localSesionManager.setQueryTimeoutInSecs(Numbers.parseInt(result.getString("DBQUERYTIMEOUT"), localSesionManager.getQueryTimeoutInSecs()));
					localSesionManager.setTableName(result.getString("TABLENAME"));
					localSesionManager.setIdSequenceName(result.getString("IDSEQUENCENAME"));
					localSesionManager.setLastUpdateTimeFeild(result.getString("LASTUPDATETIMEFIELD"));
					localSesionManager.setSessionCloseAction(result.getInt("SESSIONCLOSEACTION"));
					localSesionManager.setStartTimeFeild(result.getString("STARTTIMEFIELD"));
					
					localSesionManager.setSearchCols(result.getString("SEARCHATTRIBUTE"));

					
					//session manager batch update related properties
					localSesionManager.setIsBatchUpdateEnable(ConfigurationUtil.stringToBoolean(result.getString("BATCHUPDATEENABLED"),localSesionManager.getIsBatchUpdateEnable()));
					localSesionManager.setBatchSize(Numbers.parseInt(result.getString("BATCHSIZE"), localSesionManager.getBatchSize()));
					localSesionManager.setBatchUpdateInterval(Numbers.parseInt(result.getString("BATCHUPDATEINTERVAL"), localSesionManager.getBatchUpdateInterval()));
					 
					//  session manager instance related properties ... 
					localSesionManager.setInstanceDesc(result.getString("DESCRIPTION"));
					localSesionManager.setInstanceName(result.getString("NAME"));
					localSesionManager.setBehaviourType(Numbers.parseInt(result.getString("BEHAVIOUR"),BehaviourType.ACCOUNTING.type));
					localSesionManager.setSessionOverrideAction(result.getInt("SESSIONOVERRIDEACTION"));
					localSesionManager.setSessionOverrideColumns(result.getString("SESSIONOVERRIDECOLUMN"));
					localSesionManager.setDBFailureAction(DBFailureActions.fromActionName(
							result.getString("DBFAILUREACTION"), localSesionManager.getInstanceName()));

					localSesionManager.setActionOnStop(result.getString("SESSIONSTOPACTION"));
					if (DBUtility.isValueAvailable(result, "CONCURRENCYIDENTITYFIELD")) {
						localSesionManager.setConcurrencyIdentityField(result.getString("CONCURRENCYIDENTITYFIELD"));
					}
					psStatForMapping.setString(1, smConfigId);
					rsForMapping = psStatForMapping.executeQuery();

					List<FieldMappingImpl> dbFeildMappingList = new ArrayList<FieldMappingImpl>();

					while(rsForMapping.next()){	
						FieldMappingImpl feildMapping = new FieldMappingImpl(rsForMapping.getInt("DATATYPE"),rsForMapping.getString("REFERRING_ENTITY"),rsForMapping.getString("DBFIELDNAME"),rsForMapping.getString("DEFAULT_VALUE"),rsForMapping.getString("FIELD"));
						dbFeildMappingList.add(feildMapping);
					}
					// Add For FieldMpping

					localSesionManager.setFieldMappings(dbFeildMappingList);

					if(psForClosureMapping != null){						
						psForClosureMapping.setString(1,smConfigId);						
						rsForClosureMapping = psForClosureMapping.executeQuery();
						List<String> esiList = new ArrayList<String>();
						List<String>nakEsiList = new ArrayList<String>();
						while(rsForClosureMapping.next()){
							String esiId = rsForClosureMapping.getString("ESIINSTANCEID");
							Long esiType = rsForClosureMapping.getLong("ESITYPEID");
							Integer weightage = rsForClosureMapping.getInt("WEIGHTAGE");							
							String esiWeightage = esiId + "_" + weightage;

							// session close action is DM and esi type is acct then it is for acct stop after receiving nak so
							if(esiType == 2){
								nakEsiList.add(esiWeightage);
							}else{
								esiList.add(esiWeightage);
							}							
						}
						if(esiList.size() > 0){
							localSesionManager.setEsiRelations(esiList);
						}
						if(nakEsiList.size() > 0){
							localSesionManager.setNakEsiList(nakEsiList);
						}
					}
				}
				localSessionManagerList.add(localSesionManager);
			}
		}finally{
			DBUtility.closeQuietly(result);
			DBUtility.closeQuietly(rsForMapping);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(psStatForMapping);
			DBUtility.closeQuietly(connection);
			DBUtility.closeQuietly(psForClosureMapping);
			DBUtility.closeQuietly(psForSmInstance);
		}
		return localSessionManagerList;
	}

	@DBReload
	public void reloadFromDB() {
		
	}
	
	@DBRead
	public void readFromDB() throws Exception {
		localSessionManagerList = loadSessionMangaerConfiguration();
	}
	private String getQueryForGettingClosureMapping() {
		return "select A.*,C.ESITYPEID from TBLMSMSESSIONCLOSERESIREL A, tblmesiinstance C where A.smconfigid = ? AND c.esiinstanceid = a.esiinstanceid";
	}

	private String getQueryForGettingMapping() {
		return "select * from TBLMSMDBFIELDMAP where SMCONFIGID=?";
	}

	private String getQueryForLocalConfig() {
		return "select A.*,B.*,C.NAME from TBLMSMCONFIGINSTANCE A,TBLMSESSIONMANAGERINSTANCE B ,tblmdatabaseds C where A.SMINSTANCEID = ? AND B.SMINSTANCEID = ?" +
		"AND C.databasedsid = (Select databasedsid from TBLMSMCONFIGINSTANCE where SMINSTANCEID=?)";
	}

	private String getQueryForSM() {
		return "select * from TBLMSESSIONMANAGERINSTANCE";
	}
	@XmlElement(name = "local-session-manager")
	public List<LocalSessionManager> getLocalSessionMangaerList(){
		return localSessionManagerList;
	}
	public void setLocalSessionMangaerList(List<LocalSessionManager> localSessionManger){
		this.localSessionManagerList = localSessionManger;
	}
	
	@PostRead
	public void postReadProcessing() {
		List<LocalSessionManager> localSMList = localSessionManagerList;
		if(localSMList!=null){
			int numOfLocalSM = localSMList.size();
			for(int i=0;i<numOfLocalSM;i++){
				LocalSessionManager localSessionManager = localSMList.get(i);
				FieldMapping userIdentityFieldMapping = getFieldMapping(localSessionManager.getFieldMappings(),SchemaMappingImpl.USER_IDENTITY);
				if(userIdentityFieldMapping!=null){
					localSessionManager.setIdentityFeild(userIdentityFieldMapping.getColumnName());
				}
			}
		}
	}
	
	private FieldMapping getFieldMapping(List<FieldMappingImpl> fieldMappings,
			String userIdentity) {
		if(fieldMappings==null)
			return null;
		int numOfFieldMapping = fieldMappings.size();
		for(int i=0;i<numOfFieldMapping;i++){
			if(SchemaMappingImpl.USER_IDENTITY.equals(fieldMappings.get(i).getField())){
				return fieldMappings.get(i);
			}
		}
		return null;
	}

	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		
	}
}

