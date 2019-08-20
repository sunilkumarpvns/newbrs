package com.elitecore.netvertexsm.hibernate.servermgr.sessionmgr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.ims.MediaTypeData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.servicetype.ServiceTypeData;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.SessionDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.CoreSessionData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionConfData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionRuleData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.EliteUtility;
import com.elitecore.netvertexsm.util.logger.Logger;

public class HSessionDataManager extends HBaseDataManager implements SessionDataManager {
	private static String MODULE = "SESSION-DATA-MANAGER";
	private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimTokens();
    
    private static long secondsInMillis = 1000;
    private static long minutesInMillis = 60000; //60*1000
    private static long hoursInMillis   = 3600000; //60*60*1000

	@Override
	public SessionConfData getSessionConfData() throws DataManagerException {
		SessionConfData sessionConfData = null;
		try{	
			Session session = getSession();
			Criteria criteria = session.createCriteria(SessionConfData.class);
			List list =  criteria.list();
			if(list!=null && !list.isEmpty()){
				sessionConfData= (SessionConfData)list.get(0);
			}
			return sessionConfData;

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	@Override
	public CoreSessionData getCoreSessionData(long coreSessionId)throws DataManagerException {
		try{	
			Session session = getSession();
			Criteria criteria = session.createCriteria(CoreSessionData.class);
			criteria.add(Restrictions.eq("csID",coreSessionId));
			CoreSessionData coreSessionData = (CoreSessionData) criteria.uniqueResult();
			return coreSessionData;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	@Override
	public CoreSessionData getCoreSessionData(long coreSessionId,Connection connection)throws DataManagerException {
		String queryToGetCoreSessionData=null;
		PreparedStatement psForCoreSessionData=null;
		ResultSet resultSetForCoreSession=null;
		CoreSessionData coreSessionData;
		try{	
			queryToGetCoreSessionData="SELECT * FROM TBLMCORESESSIONS WHERE CSID=?";
			psForCoreSessionData=connection.prepareStatement(queryToGetCoreSessionData);
			psForCoreSessionData.setLong(1, coreSessionId);
			resultSetForCoreSession=psForCoreSessionData.executeQuery();
			coreSessionData=new CoreSessionData();
			while(resultSetForCoreSession.next()){
				coreSessionData.setCsID(resultSetForCoreSession.getLong("CSID"));
				coreSessionData.setCoreSessionID(resultSetForCoreSession.getString("CORESESSIONID"));
				coreSessionData.setUserIdentity(resultSetForCoreSession.getString("USERIDENTITY"));
				coreSessionData.setSessionID(resultSetForCoreSession.getString("SESSIONID"));
				coreSessionData.setSessionManagerID(resultSetForCoreSession.getString("SESSIONMANAGERID"));
				coreSessionData.setGatewayAddress(resultSetForCoreSession.getString("GATEWAYADDRESS"));
				coreSessionData.setSessionType(resultSetForCoreSession.getString("SESSIONTYPE"));
				coreSessionData.setSessionIPV4(resultSetForCoreSession.getString("SESSIONIPV4"));
				coreSessionData.setSessionIPV6(resultSetForCoreSession.getString("SESSIONIPV6"));
			}		

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}finally{
			DBUtility.closeQuietly(resultSetForCoreSession);
			DBUtility.closeQuietly(psForCoreSessionData);
		}
		return coreSessionData;
	}
	@Override
	public void updateSessionConf(SessionConfData sessionConfData)throws DataManagerException {

		try{
			Session session = getSession();
			sessionConfData.setModifiedDate(EliteUtility.getCurrentTimeStamp());
			session.saveOrUpdate(sessionConfData);

			session.flush();

		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}


	@Override
	public void deleteOldMapping(SessionConfData sessionConfData)throws DataManagerException{
		try{
			Session session = getSession();

			Criteria criteria=session.createCriteria(SessionFieldMapData.class);
			criteria.add(Restrictions.eq("sessionConfID",sessionConfData.getSessionConfID()));
			List<SessionFieldMapData> sessionFieldMapDataList=null;
			sessionFieldMapDataList=criteria.list();

			for(SessionFieldMapData sessionFieldMapData:sessionFieldMapDataList){
				session.delete(sessionFieldMapData);
			}

			session.flush();


		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}

	}

	@Override
	public void createNewMapping(SessionConfData sessionConfData)throws DataManagerException {

		try{
			Session session = getSession();
			Criteria criteria=session.createCriteria(SessionFieldMapData.class);
			Set<SessionFieldMapData> sessionFieldMapDataSet=sessionConfData.getSessionFieldMapDataset();
			for(SessionFieldMapData sessionFieldMapData:sessionFieldMapDataSet){
				session.save(sessionFieldMapData);
			}
			session.flush();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	
	private String parsePCCRuleIds(Set<String> columnValues) {
		return Strings.join(",", columnValues,new Function<Object, String>() {

			@Override
			public String apply(Object input) {
				return CommonConstants.SINGLE_QUOTE + input.toString() + CommonConstants.SINGLE_QUOTE;
			}
		});
	}
	
	private Map<String, Object> getTblColumnMap(ResultSetMetaData resultSetMetaData, ResultSet rs) throws SQLException {
		Map<String,Object> tblColumnMap = new LinkedHashMap<String, Object>();

		for (int i=1;i<=resultSetMetaData.getColumnCount();i++) {
			if(resultSetMetaData.getColumnName(i).equals("PCCRULES") || resultSetMetaData.getColumnName(i).equals("ROWNUMBER")){
				continue;
			}
			if(resultSetMetaData.getColumnType(i) == Types.TIMESTAMP){
				tblColumnMap.put(resultSetMetaData.getColumnName(i),rs.getTimestamp(i));
			}else{
				tblColumnMap.put(resultSetMetaData.getColumnName(i),rs.getObject(i));
			}						
		}
		return tblColumnMap;
	}

	private CoreSessionData getCoreSessionData(ResultSet rs) throws SQLException{
	
		CoreSessionData coreSessionData2=new CoreSessionData();
		coreSessionData2.setCsID(rs.getLong("CSID"));
		coreSessionData2.setCoreSessionID(rs.getString("CORESESSIONID"));
		coreSessionData2.setUserIdentity(rs.getString("USERIDENTITY"));
		coreSessionData2.setSessionID(rs.getString("SESSIONID"));
		coreSessionData2.setGatewayAddress(rs.getString("GATEWAYADDRESS"));
		coreSessionData2.setSessionManagerID(rs.getString("SESSIONMANAGERID"));
		coreSessionData2.setSessionType(rs.getString("SESSIONTYPE"));
		coreSessionData2.setSessionIPV4(rs.getString("SESSIONIPV4"));
		coreSessionData2.setSessionIPV6(rs.getString("SESSIONIPV6"));
		long starttime=rs.getTimestamp("STARTTIME").getTime(); 
		coreSessionData2.setTotalSessionTime(calculatetimedifference(starttime));
		coreSessionData2.setPccRules(rs.getString("PCCRULES"));
		return coreSessionData2;
	}

	private List<SessionRuleData> getSessionRuleList(Connection connection, String queryForSessionRule, String criteriaValue) throws SQLException, DataManagerException{
		PreparedStatement psForSessionRule=null; 
		ResultSet rsForSessionRuleData=null;
		List<SessionRuleData>  	sessionRuleDataList=null;
		
		try{
		psForSessionRule = connection.prepareStatement(queryForSessionRule);
		psForSessionRule.setString(1,criteriaValue);
		
		rsForSessionRuleData = psForSessionRule.executeQuery();
	    sessionRuleDataList =   new ArrayList<SessionRuleData>();
		
			  while(rsForSessionRuleData.next()){
				  SessionRuleData sessionRuleData = new SessionRuleData();
				  
				  sessionRuleData.setSessionID(rsForSessionRuleData.getString("SESSIONID"));
				  sessionRuleData.setAfApplicationId(rsForSessionRuleData.getString("AFSESSIONID"));
				  String mediaTypeStr = rsForSessionRuleData.getString("MEDIATYPE");
				  sessionRuleData.setMediaType(mediaTypeStr);
				  sessionRuleData.setPccRule(rsForSessionRuleData.getString("PCCRULE"));
				  sessionRuleData.setUplinkFlow(rsForSessionRuleData.getString("UPLINKFLOW"));
				  sessionRuleData.setDownlinkFlow(rsForSessionRuleData.getString("DOWNLINKFLOW"));
				  MediaTypeData mediaTypeData = getMediaTypeData(connection,mediaTypeStr);
				  if(mediaTypeData != null){
					  sessionRuleData.setMediaTypeData(mediaTypeData);
				  }else{
					  Logger.logWarn(MODULE, "Media Type is not configured with IMS session : "+sessionRuleData.getAfApplicationId());
				  }
				  sessionRuleDataList.add(sessionRuleData);
			  }
		  }finally{
			  DBUtility.closeQuietly(rsForSessionRuleData);
			  DBUtility.closeQuietly(psForSessionRule);
		  }
		return sessionRuleDataList;
	}
	private MediaTypeData getMediaTypeData(Connection connection,
			String mediaTypeStr)  throws SQLException {
		PreparedStatement psForMediaTypeData = null;
		ResultSet rsForMediaTypeData = null;
		Long mediaTypeId = null;
		if(Strings.isNullOrBlank(mediaTypeStr) == false){
			mediaTypeId = Long.parseLong(mediaTypeStr);
		}
		String queryForMediaTypeData = "select ID,NAME,MEDIA_IDENTIFIER,STATUS from TBLM_MEDIA_TYPE where MEDIA_IDENTIFIER ="+mediaTypeId+"";
		MediaTypeData mediaType = null;
		try {
			psForMediaTypeData = connection.prepareStatement(queryForMediaTypeData);
			rsForMediaTypeData = psForMediaTypeData.executeQuery();
			if(rsForMediaTypeData.next()){
				mediaType = new MediaTypeData();
				mediaType.setId(rsForMediaTypeData.getString("ID"));
				mediaType.setName(rsForMediaTypeData.getString("NAME"));
				mediaType.setMediaIdentifier(rsForMediaTypeData.getLong("MEDIA_IDENTIFIER"));
				mediaType.setStatus(rsForMediaTypeData.getString("STATUS"));
			}
		} finally{
			DBUtility.closeQuietly(rsForMediaTypeData);
			DBUtility.closeQuietly(psForMediaTypeData);
			
		}
		return mediaType;
	}

	private List<PCCRuleData> getActivePccRuleDataList(Connection connection , String pccRules )throws DataManagerException {
		PreparedStatement psForPccRuleData = null;
		ResultSet rsForPccRuleData = null;
		String queryForPCCRuleData = "select ID,NAME,SERVICE_TYPE_ID,TYPE from TBLM_PCC_RULE where ID in ("+ pccRules+")";
		List<PCCRuleData> pccRuleDataList = new ArrayList<PCCRuleData>();
		try {
			psForPccRuleData = connection.prepareStatement(queryForPCCRuleData);
			rsForPccRuleData = psForPccRuleData.executeQuery();
			while(rsForPccRuleData.next()){
				PCCRuleData pccRuleData = new PCCRuleData();
				pccRuleData.setId(rsForPccRuleData.getString("ID"));
				pccRuleData.setName(rsForPccRuleData.getString("NAME"));
				
				ServiceTypeData serviceTypeData = getServiceType(connection,rsForPccRuleData.getString("SERVICE_TYPE_ID"));
				if(serviceTypeData != null){
					pccRuleData.setServiceType(serviceTypeData);
				}else{
					Logger.logWarn(MODULE, "Service is not configured with PCC Rule: "+pccRuleData.getName());
				}
		
				pccRuleData.setType(rsForPccRuleData.getString("TYPE"));
				pccRuleDataList.add(pccRuleData);
			}
		} catch (SQLException e) {
			Logger.logError(MODULE, "Error while fetching Active Pcc Rule Data, Reason: "+e.getMessage());
		} finally{
			DBUtility.closeQuietly(rsForPccRuleData);
			DBUtility.closeQuietly(psForPccRuleData);
			
		}
		return pccRuleDataList;


	}
	private ServiceTypeData getServiceType(Connection connection,String serviceTypeId) throws SQLException {
		PreparedStatement psForServiceTypeData = null;
		ResultSet rsForServiceTypeData = null;
		String queryForServiceTypeData = "select ID,NAME,SERVICE_IDENTIFIER,STATUS from TBLM_SERVICE_TYPE where ID ='"+serviceTypeId+"'";
		ServiceTypeData serviceType = null;
		try {
			psForServiceTypeData = connection.prepareStatement(queryForServiceTypeData);
			rsForServiceTypeData = psForServiceTypeData.executeQuery();
			if(rsForServiceTypeData.next()){
				serviceType = new ServiceTypeData();
				serviceType.setId(rsForServiceTypeData.getString("ID"));
				serviceType.setName(rsForServiceTypeData.getString("NAME"));
				serviceType.setServiceIdentifier(rsForServiceTypeData.getLong("SERVICE_IDENTIFIER"));
				serviceType.setStatus(rsForServiceTypeData.getString("STATUS"));
			}
		} finally{
			DBUtility.closeQuietly(rsForServiceTypeData);
			DBUtility.closeQuietly(psForServiceTypeData);
			
		}
		return serviceType;
	}

	public void deleteActiveSession(Long[] csIDList, Connection connection) 		throws DataManagerException {
		String queryToDeleteFromCoreSession = null;
		String queryToDeleteFromSessionRule = null;
		String queryToGetCoreSessionID = null;
		String coreSessionID = null;
		PreparedStatement psForCoreSession = null;
		PreparedStatement psForSessionRule = null;
		PreparedStatement psForCoreSessionID=null;
		ResultSet        resultSet=null;
		try {
			queryToGetCoreSessionID= "SELECT CORESESSIONID FROM TBLMCORESESSIONS WHERE CSID=?";
			queryToDeleteFromCoreSession = "DELETE FROM TBLMCORESESSIONS WHERE CSID=?";
			queryToDeleteFromSessionRule = "DELETE FROM TBLMSESSIONRULE WHERE SESSIONID=?";
			Logger.logDebug(MODULE, "SQL QUERY:"+queryToDeleteFromCoreSession);
			Logger.logDebug(MODULE, "SQL QUERY:"+queryToDeleteFromSessionRule);
			if (connection != null) {
				connection.setAutoCommit(false);
			    for (Long csID : csIDList) {
			    	try{
			    		psForCoreSessionID=connection.prepareStatement(queryToGetCoreSessionID);
			    		psForCoreSession = connection.prepareStatement(queryToDeleteFromCoreSession);
			    		psForSessionRule = connection.prepareStatement(queryToDeleteFromSessionRule);
			    		psForCoreSessionID.setLong(1, csID);
			    		psForCoreSession.setLong(1, csID);
			    		resultSet=psForCoreSessionID.executeQuery();
			    		while(resultSet.next()){
			    			coreSessionID=resultSet.getString("CORESESSIONID");
			    		}
			    		psForSessionRule.setString(1, coreSessionID);
			    		psForSessionRule.executeUpdate();
			    		psForCoreSession.executeUpdate();
			    	}finally{
			    		DBUtility.closeQuietly(resultSet);
						DBUtility.closeQuietly(psForSessionRule);
						DBUtility.closeQuietly(psForCoreSession);
						DBUtility.closeQuietly(psForCoreSessionID);
			    	}
				}
				connection.commit();
			}
		} catch (Exception exp) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				Logger.logError(MODULE, "Error while doing rollback, reason:"+e.getMessage());
			}
			throw new DataManagerException(exp.getMessage(), exp);
		} 
	}


	@Override
	public void createSessionConfiguration(SessionConfData sessionConfData) throws DataManagerException {
		try{
			Session session = getSession();			
			session.save(sessionConfData);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}

	}

	@Override
	public DatabaseDSData getDatabaseDS(Integer dataSourceID)throws DataManagerException{
		try{
			DatabaseDSData databaseDSData = null;
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatabaseDSData.class);			
			if(dataSourceID != null){
				long id=dataSourceID;
				criteria.add(Restrictions.eq("databaseId",id));
				databaseDSData= (DatabaseDSData) criteria.uniqueResult();
			}else{
				return databaseDSData;
			}
			return databaseDSData;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
		    throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	private String createOracleQuery(CoreSessionData coreSessionData,Map<Integer,String> hashMap, int count){
		String queryString = null;
		if(coreSessionData.getUserIdentity() != null && !(coreSessionData.getUserIdentity().equalsIgnoreCase(""))){
			queryString="SELECT * FROM (SELECT a.* FROM TBLMCORESESSIONS a WHERE LOWER(USERIDENTITY) LIKE ?) WHERE ROWNUMBER >= ? AND ROWNUMBER <=?";
			hashMap.put(count++,"%"+coreSessionData.getUserIdentity()+"%");
		}
	
		if(coreSessionData.getUserIdentity() == null || coreSessionData.getUserIdentity().equalsIgnoreCase("")){
			queryString = "SELECT * FROM (SELECT a.* FROM TBLMCORESESSIONS a ) WHERE ROWNUMBER >= ? AND ROWNUMBER <=?";
		}
		
		return queryString;
	}
	
	private String createMySQLQuery(CoreSessionData coreSessionData,Map<Integer,String> hashMap, int count){
		String queryString = null;
		if(coreSessionData.getUserIdentity() != null && !(coreSessionData.getUserIdentity().equalsIgnoreCase(""))){
			queryString="SELECT * FROM (SELECT a.* FROM TBLMCORESESSIONS a WHERE LOWER(USERIDENTITY) LIKE ?) b LIMIT ?,?";
			hashMap.put(count++,"%"+coreSessionData.getUserIdentity()+"%");
		}
	
		if(coreSessionData.getUserIdentity() == null || coreSessionData.getUserIdentity().equalsIgnoreCase("")){
			queryString = "SELECT * FROM (SELECT a.* FROM TBLMCORESESSIONS a ) b LIMIT ?,?";
		}
		
		return queryString;
	}

	private String calculatetimedifference(long time){
	    long diff = System.currentTimeMillis() - time;
	    long diffSeconds = diff / secondsInMillis % 60;
	    long diffMinutes = diff / minutesInMillis % 60;
	    long diffHours = diff / hoursInMillis;
	    return diffHours+"h "+diffMinutes+"m "+diffSeconds+"s";
	}

}