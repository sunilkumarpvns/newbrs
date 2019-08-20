package com.elitecore.netvertexsm.blmanager.servermgr.sessionmgr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.util.ActivePCCRuleParser;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.BaseDatasourceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.datasource.database.DatabaseDSDataManager;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.ASMDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.SessionDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.CoreSessionData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionConfData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionManagerDBConfiguration;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.exception.DataNotInSyncException;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.netvertexsm.ws.cxfws.session.data.Entry;
import com.elitecore.netvertexsm.ws.cxfws.session.data.SessionData;
import com.elitecore.netvertexsm.ws.cxfws.session.response.SessionResponse;
import com.elitecore.netvertexsm.ws.db.DBConnectionManager;
import com.elitecore.netvertexsm.ws.exception.ResultCodes;
import com.elitecore.netvertexsm.ws.logger.Logger;

public class SessionBLManager {
	
    private static final String PCCRULE_NAMES = "PCCRULENAMES";
	private static final String CHARGING_RULE_BASE_NAMES = "CHARGINGRULEBASENAMES";
	private static final String MODULE = "SESSION-BL-MANAGER";
	private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimTokens();

	public void updateStatus( List<Long> lstAccessPolicyIds , String commonStatusId, IStaffData staffData, String actionAlias ) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        SessionDataManager sessionDataManager = getSessionInstanceDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        //Date currentDate = new Date();
        
        if (sessionDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try {
            session.beginTransaction();
            
            if (lstAccessPolicyIds != null) {
                for ( int i = 0; i < lstAccessPolicyIds.size(); i++ ) {
                    if (lstAccessPolicyIds.get(i) != null) {
                        //long accessPolicyId = lstAccessPolicyIds.get(i);
                    	String transactionId = lstAccessPolicyIds.get(i).toString();
                    	//policyGroupDataManager.updateStatus(accessPolicyId, commonStatusId, new Timestamp(currentDate.getTime()));
                        
                        systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);
                    }
                }
                session.commit();
            } else {
                session.rollback();
                throw new DataManagerException("Data Manager List of Ids are null" + getClass().getName());
            }
        }catch (DataValidationException e) {
            session.rollback();
            throw e;
        } 
        catch (BaseDatasourceException e) {
            session.rollback();
            throw e;
        }
        catch (Exception exp) {
            session.rollback();
            throw new DataManagerException("Action failed : " + exp.getMessage());
        }
        finally {
            if (session != null)
                session.close();
        }    
    }
    public void closeSession(Long userId,IStaffData staffData, String actionAlias,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ASMDataManager asmDataManager = getASMDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if(asmDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();

			asmDataManager.closeSession(userId,dbConfiguration);
			
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);

			session.commit();
		}
		catch(DataNotInSyncException dnisExp){
			session.rollback();
			throw new DataManagerException("Database Not in Sync., Lock exists on some database rows : " + dnisExp.getMessage());
		}
		catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Action failed : " + exp.getMessage(),exp);
		}
		catch (Exception e) {
			session.rollback();
			throw new DataManagerException("Action failed : " + e.getMessage(),e);
		}finally{
			session.close();
		}
	}
	public void closeSession(String userIds[],IStaffData staffData, String actionAlias,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ASMDataManager asmDataManager = getASMDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if(asmDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();

			asmDataManager.closeSession(userIds,dbConfiguration);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);

			session.commit();
		}catch(DataNotInSyncException dnisExp){
			session.rollback();
			throw new DataManagerException("Database Not in Sync., Lock exists on some database rows : " + dnisExp.getMessage(),dnisExp);
		}catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Action failed : " + exp.getMessage(),exp);
		}catch (Exception e) {
			session.rollback();
			throw new DataManagerException("Action failed : " + e.getMessage(),e);
		}finally{
			session.close();
		}
	}

	public List<Map<String,Object>> purgeClosedSession(IStaffData staffData, String actionAlias,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException{
		List<Map<String,Object>> purgedSessionsList = new ArrayList<Map<String,Object>>();
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ASMDataManager asmDataManager = getASMDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if(asmDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			purgedSessionsList = asmDataManager.purgeClosedSession(dbConfiguration);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
			return purgedSessionsList;
		}
		catch(DataNotInSyncException dnisExp){
			session.rollback();
			throw new DataManagerException("Database Not in Sync., Lock exists on some database rows : " + dnisExp.getMessage(),dnisExp);
		}
		catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Action failed : " + exp.getMessage(),exp);
		}catch (Exception e) {
			session.rollback();
			throw new DataManagerException("Action failed : " + e.getMessage(),e);
		}finally{
			session.close();
		}
	}

	public void purgeAllSession(IStaffData staffData, String actionAlias,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ASMDataManager asmDataManager = getASMDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if(asmDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{

			session.beginTransaction();

			asmDataManager.purgeAllSession(dbConfiguration);
			
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);

			session.commit();
		}catch(DataNotInSyncException dnisExp){
			session.rollback();
			throw new DataManagerException("Database Not in Sync., Lock exists on some database rows : " + dnisExp.getMessage(),dnisExp);
		}catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Action failed : " + exp.getMessage(),exp);
		}catch (Exception e) {
			session.rollback();
			throw new DataManagerException("Action failed : " + e.getMessage());
		}finally{
			session.close();
		}
	}
    
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
        return systemAuditDataManager; 
    }
	
	/**
	 * @author Manjil Purohit
     * @return Returns Data Manager instance for GX Session Instance data.
     */
    public SessionDataManager getSessionInstanceDataManager(IDataManagerSession session) {
    	SessionDataManager sessionDataManager = (SessionDataManager) DataManagerFactory.getInstance().getDataManager(SessionDataManager.class, session);
        return sessionDataManager; 
    }
    
	private DatabaseDSDataManager getDatabaseDSDataManager(IDataManagerSession session) { 
		DatabaseDSDataManager sessionManagerDataManager = (DatabaseDSDataManager) DataManagerFactory.getInstance().getDataManager(DatabaseDSDataManager.class, session);
		return sessionManagerDataManager;
	}

    public ASMDataManager getASMDataManager(IDataManagerSession session) {
    	ASMDataManager asmDataManager = (ASMDataManager) DataManagerFactory.getInstance().getDataManager(ASMDataManager.class, session);
        return asmDataManager; 
    }

	public SessionConfData getSessionConfData() throws DataManagerException  {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		SessionDataManager sessionDataManager = getSessionInstanceDataManager(session);
		SessionConfData sessionConfData = null;
		
		if(sessionDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{        	
        	session.beginTransaction();	        
        	sessionConfData = sessionDataManager.getSessionConfData();                                         
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }
		return sessionConfData;
	}
	
	public CoreSessionData getCoreSessionData(long coreSessionId)throws DataManagerException  {	
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		SessionDataManager sessionDataManager = getSessionInstanceDataManager(session);
		CoreSessionData coreSessionData = null;
		
		if(sessionDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{        	
        	session.beginTransaction();	        
        	coreSessionData = sessionDataManager.getCoreSessionData(coreSessionId);                                         
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
        }finally{
        	session.close();
        }
		return coreSessionData;
	}
	
	public CoreSessionData getCoreSessionData(long coreSessionId,Connection connection)throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		SessionDataManager sessionDataManager = getSessionInstanceDataManager(session);
		CoreSessionData coreSessionData = null;
		try {
			session.beginTransaction();	
			coreSessionData = sessionDataManager.getCoreSessionData(coreSessionId, connection);
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
	return coreSessionData;
	}

	public void updateSessionConf(SessionConfData sessionConfData,IStaffData staffData,String actionAlias)throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        SessionDataManager sessionDataManager = getSessionInstanceDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        if(sessionDataManager == null || systemAuditDataManager == null){
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try {
			session.beginTransaction();
			sessionDataManager.updateSessionConf(sessionConfData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch (DataManagerException e) {
           session.rollback();
           throw e;
		}finally{
			session.close();
		}
	}

	public void deleteOldMapping(SessionConfData sessionConfData)throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        SessionDataManager sessionDataManager = getSessionInstanceDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        if(sessionDataManager == null || systemAuditDataManager == null){
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
        try{
		session.beginTransaction();
		sessionDataManager.deleteOldMapping(sessionConfData);
    	session.commit();
		session.close();
        }catch (DataManagerException e) {
            session.rollback();
            throw e;
 		}finally{
 			session.close();
 		}
	}

	public void createNewMapping(SessionConfData sessionConfData)throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        SessionDataManager sessionDataManager = getSessionInstanceDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        if(sessionDataManager == null || systemAuditDataManager == null){
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try{
			session.beginTransaction();
			sessionDataManager.createNewMapping(sessionConfData);
			session.commit();
		}catch(DataManagerException e) {
			session.rollback();
			throw e;
		}finally{
			session.close();
		}
		
	}

	public void deleteActiveSession(Long[] csIDList, Connection connection, IStaffData staffData, String actionAlias) throws DataManagerException {
				
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SessionDataManager sessionDataManager = getSessionInstanceDataManager(session);
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
		if(sessionDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		try{
			session.beginTransaction();
			if(csIDList != null){
				sessionDataManager.deleteActiveSession(csIDList,connection);				    	
			}
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(ConstraintViolationException de){
			 session.rollback();
			 session.close();
			 throw new ConstraintViolationException(de.getMessage(),de.getSQLException(),de.getConstraintName());
		 }catch(Exception exp){
			 session.rollback();
			 throw new DataManagerException("Action failed :"+exp.getMessage(),exp);
		 }finally{
			 session.close();
		 }
		
	}
	
	

	public void createSessionConfiguration(SessionConfData sessionConfData)throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		SessionDataManager sessionDataManager = getSessionInstanceDataManager(session);
		
		if(sessionDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{        	
        	session.beginTransaction();	        
        	sessionDataManager.createSessionConfiguration(sessionConfData);
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }
		
	}

	public DatabaseDSData getDatabaseDS(Integer dataSourceID)throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		SessionDataManager sessionDataManager = getSessionInstanceDataManager(session);
		DatabaseDSData databaseDSData = null;
		
		if(sessionDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{        	
        	session.beginTransaction();	        
        	databaseDSData = sessionDataManager.getDatabaseDS(dataSourceID);                                         
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return databaseDSData;
	}
	
	public SessionResponse getSessionByIP(String sessionIP, String sessionType) {
		
		SessionResponse response  = new SessionResponse();

		if(Strings.isNullOrBlank(sessionIP) ==  true){
			Logger.logError(MODULE, "Sending invalid input parameter. Reason: Session IP not provided");
			response.setResponseCode(ResultCodes.INVALID_INPUT_PARAMETER);
			response.setResponseMessage("EMPTY SESSIONIP");
			return response;
		}
		
		Connection connection = DBConnectionManager.getInstance().getPrimaryOrSecondaryDSConnection();						
		if(connection==null){
			Logger.logError(MODULE, "Datasource Connection not Available");
			response.setResponseCode(ResultCodes.SERVICE_UNAVAILABLE);
			response.setResponseMessage("SERVICE UNAVAILABLE");
			return response;
		}
		
		PreparedStatement preparedStmt = null;
		ResultSet resultSet = null;
		try {
			
			String query = null;
			if(isIPv4(sessionIP)) {
				query = "SELECT * FROM TBLMCORESESSIONS WHERE SESSIONIPV4 = ? ";
			} else {
				query = "SELECT * FROM TBLMCORESESSIONS WHERE SESSIONIPV6 = ? ";
			}
			
			
			
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, sessionIP);
			
			resultSet = preparedStmt.executeQuery();
			
			ResultSetMetaData rsmd = resultSet.getMetaData();
			
			int totalColumns = rsmd.getColumnCount();
						
			List<SessionData> sessionDataList = new ArrayList<SessionData>(4);
			
			if (Strings.isNullOrBlank(sessionType) == false) {
				while (resultSet.next()) {
					String sessionTypeValue = resultSet.getString("SESSIONTYPE");
					if (sessionType.equalsIgnoreCase(sessionTypeValue)) {
						sessionDataList.add(createCoreSessionData(resultSet,rsmd, totalColumns,connection));
					} else {
						//TODO need to ask subhash
						Logger.logDebug(MODULE, "Skip session: " + resultSet.getString("CORESESSIONID"));
					}
				}
			}else{
				while (resultSet.next()) {
					sessionDataList.add(createCoreSessionData(resultSet,rsmd, totalColumns,connection));
				}
			}
			
			if(sessionDataList.isEmpty()==true){
				response.setResponseCode(ResultCodes.NO_RECORDS_FOUND);
				response.setResponseMessage("NO RECORD FOUND");
			}else{
				response.setResponseCode(ResultCodes.SUCCESS);
				response.setResponseMessage("SUCCESS");
				response.setSessionDataList(sessionDataList);						
			}
			
			Logger.logInfo(MODULE, response.toString());
			
			Logger.logInfo(MODULE, "SessionLookup Completed Successfully");
			
		} catch (Exception e) {
			response.setResponseCode(ResultCodes.INTERNAL_ERROR);
			response.setResponseMessage("INTERNAL ERROR");
			Logger.logError(MODULE, "Error while fetching the coresessions data. Reason: "+e.getMessage());
			Logger.logTrace(MODULE, e);
			
		} finally {			
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStmt);
			DBUtility.closeQuietly(connection);			
		}
		return response;
	}
	
	private boolean isIPv4(String sessionIP) {
		if(sessionIP.contains(".")) {
			return true;
		} else {
			return false;
		}
	}
	private SessionData createCoreSessionData(ResultSet resultSet, ResultSetMetaData rsmd,
			int totalColumns,Connection connection) throws SQLException {
		List<Entry> entryList = new ArrayList<Entry>(totalColumns);
		
		for(int index=1; index<=totalColumns; index++){
			
			String columnName = rsmd.getColumnName(index); 
			String columnValue = resultSet.getString(index);
			
			
			Entry entry = new Entry();
			entry.setKey(columnName);
			if(columnName.equalsIgnoreCase("PCCRULES")){
				Map<String,String> pccRuleBasedOnSubscriptionId = ActivePCCRuleParser.deserialize(columnValue);
				String ids = parsePCCRuleIds(pccRuleBasedOnSubscriptionId.keySet());
				Entry e = new Entry();
				e.setKey(PCCRULE_NAMES);
				if (Strings.isNullOrBlank(ids) == false) {
					StringBuilder pccRuleNames = new StringBuilder();
					StringBuilder pccRuleIds = new StringBuilder();
					getPCCRulesFromId(connection, ids, pccRuleNames,pccRuleIds);
					if (pccRuleIds.toString().length() > 0) {
						e.setValue(pccRuleNames.toString());
						entry.setValue(pccRuleIds.toString());
					} 
				}
				entryList.add(e);

			} if(columnName.equalsIgnoreCase("CHARGINGRULEBASENAMES")){
				Map<String,String> chargingRuleBasedOnSubscriptionId = ActivePCCRuleParser.deserialize(columnValue);
				String ids = parsePCCRuleIds(chargingRuleBasedOnSubscriptionId.keySet());
				Entry e = new Entry();
				e.setKey(CHARGING_RULE_BASE_NAMES);
				if (Strings.isNullOrBlank(ids) == false) {
					StringBuilder chargingRuleBaseNames = new StringBuilder();
					StringBuilder chargingRuleBaseNameIds = new StringBuilder();
					getPCCRulesFromId(connection, ids, chargingRuleBaseNames,chargingRuleBaseNameIds);
					if (chargingRuleBaseNameIds.toString().length() > 0) {
						e.setValue(chargingRuleBaseNames.toString());
						entry.setValue(chargingRuleBaseNameIds.toString());
					}
				}
				entryList.add(e);
			}
			else{
				entry.setValue(columnValue);
			}
			entryList.add(entry);
		}
		
		return new SessionData(entryList);
	}
	
	private String parsePCCRuleIds(Set<String> columnValues) {
		return Strings.join(",", columnValues, new Function<Object,String>() {

			@Override
			public String apply(Object input) {
				return CommonConstants.SINGLE_QUOTE + input.toString() + CommonConstants.SINGLE_QUOTE;
			}
		});
	}

	private void getPCCRulesFromId(Connection connection,String columnValue,StringBuilder pccRuleNames,StringBuilder pccRuleIds) throws SQLException {
		PreparedStatement preparedStmt = null;
		ResultSet resultSet = null;
		String query = "select ID,NAME,SERVICE_TYPE_ID,TYPE from TBLM_PCC_RULE where ID in ("+ columnValue+")";
		try{
			preparedStmt = connection.prepareStatement(query);
			resultSet = preparedStmt.executeQuery();
			if(resultSet.next()){
				String pccRuleId = resultSet.getString("ID");
				String pccName = resultSet.getString("NAME");
				pccRuleNames.append(pccName);
				pccRuleIds.append(pccRuleId);
			}
			while (resultSet.next()) {
				String pccRuleId = resultSet.getString("ID");
				String pccName = resultSet.getString("NAME");
					pccRuleNames.append(",");
					pccRuleNames.append(pccName);
					pccRuleIds.append(",");
					pccRuleIds.append(pccRuleId);
			}	
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStmt);
		}
	}

	private void getChargingRuleBaseNamesFromId(Connection connection,String columnValue,StringBuilder chargingRuleBaseNames,StringBuilder chargingRuleBaseNameIds) throws SQLException {

		PreparedStatement preparedStmt = null;
		ResultSet resultSet = null;

		String query = "select ID,NAME from TBLM_CHARGING_RULE_BASE_NAME where ID in ("+ columnValue+")";

		try{
			preparedStmt = connection.prepareStatement(query);
			resultSet = preparedStmt.executeQuery();

			if(resultSet.next()){
				String ID = resultSet.getString("ID");
				String name = resultSet.getString("NAME");
				chargingRuleBaseNames.append(name);
				chargingRuleBaseNameIds.append(ID);
			}

			while (resultSet.next()) {
				String ID = resultSet.getString("ID");
				String name = resultSet.getString("NAME");

				chargingRuleBaseNames.append(",");
				chargingRuleBaseNames.append(name);

				chargingRuleBaseNameIds.append(",");
				chargingRuleBaseNameIds.append(ID);
			}
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStmt);
		}
	}
	
}