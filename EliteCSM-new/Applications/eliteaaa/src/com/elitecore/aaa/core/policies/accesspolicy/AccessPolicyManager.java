/*
 *  EliteRadius
 *    
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 *  Created on 28th April 2007
 *  Created By Ezhava Baiju D
 *  
 */

package com.elitecore.aaa.core.policies.accesspolicy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.policies.accesspolicy.data.AccessPolicyTime;
import com.elitecore.aaa.core.policies.accesspolicy.data.AccountAccessPolicyData;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadCacheFailedException;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.manager.cache.Cacheable;
import com.elitecore.core.serverx.policies.ManagerInitialzationException;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.io.Closeables;

/**
 * Access policy manager.
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public class AccessPolicyManager implements Cacheable{
	
	private static final String MODULE = "ACCESS POLICY MANAGER";
	
	private static final int LAST_DAY_OF_WEEK=6;
	private static final int LAST_HOUR_OF_DAY=23;
	private static final int LAST_MINUTE_OF_HOUR=59;
	private static final int LAST_SECOND_OF_MINUTE=59;
	private static final int FIRST_DAY_OF_WEEK=0;
	private static final int FIRST_HOUR_OF_DAY=0;
	private static final int FIRST_MINUTE_OF_HOUR=0;
	private static final int FIRST_SECOND_OF_MINUTE=0;

	private static AccessPolicyManager policyManagerInstance;
	
	private Map < String , AccountAccessPolicyData > accessPolicies;
	
	private static final String SUNDAY = "SUN";
	private static final String SUNDAY1 = "SUNDAY"; 
	private static final String MONDAY = "MON";
	private static final String MONDAY1 = "MONDAY"; 
	private static final String TUESDAY = "TUE";
	private static final String TUESDAY1 = "TUESDAY";
	private static final String WEDNESDAY = "WED";
	private static final String WEDNESDAY1 = "WEDNESDAY"; 
	private static final String THURSDAY = "THU";	
	private static final String THURSDAY1 = "THURSDAY"; 
	private static final String FRIDAY = "FRI";
	private static final String FRIDAY1 = "FRIDAY";
	private static final String SATURDAY = "SAT";
	private static final String SATURDAY1 = "SATURDAY";
	
	//These variable are needed for the Reload Cache 
	boolean bReadError;

	private String serverHome;
	
	public Map<String, AccountAccessPolicyData> getAcccessPolicyMap(){
		return accessPolicies;
	}
	
	protected AccessPolicyManager(){
		accessPolicies = new HashMap<String, AccountAccessPolicyData>();
	}
	
	public static AccessPolicyManager getInstance() {
		if (policyManagerInstance == null) {
			synchronized (AccessPolicyManager.class) {
				if (policyManagerInstance == null)
					policyManagerInstance = new AccessPolicyManager();
			}
		}
		return policyManagerInstance;
	}
	
	/**
	 *  If sessionTimeOut is less than 0 then it will not be  
	 *	included in reply item .
	 * 
	 * @param policyName
	 * @return returns maximum seconds the access should be allowed.
	 * @throws AccessDeniedException
	 */
	public long checkAccessPolicy(String policyName,boolean bContinueOnPolicyNotFound) throws AccessDeniedException{
		return checkAccessPolicy(policyName, new GregorianCalendar(),bContinueOnPolicyNotFound);
	}
	
	public long checkAccessPolicy(String policyName,Calendar currentTime,boolean bContinueOnPolicyNotFound) throws AccessDeniedException{
		long sessionTimeOut = -1;
		if(policyName != null && policyName.length()!=0){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Applying Access Policy: " + policyName);
			AccountAccessPolicyData accessPolicyData = accessPolicies.get(policyName);
			if(accessPolicyData != null){
				sessionTimeOut = applyAccessPolicy(accessPolicyData,currentTime);
			}else if(!bContinueOnPolicyNotFound){
				throw new AccessDeniedException("Access Policy Not Found");
			}
		}
		return sessionTimeOut;		
	}
	
	/**
	 * This method will Cache all the accesspolicy that is 
	 * defined in database.
	 * This method is called from initCache() method AuthenticationService class
	 */
	@SuppressWarnings("unchecked")
	public void initCache(String serverHome) throws ManagerInitialzationException{
		bReadError = false;
		this.serverHome = serverHome;
		Map <String, AccountAccessPolicyData> tmpAccessPolicies=null;
		
		try{
			tmpAccessPolicies = readFromDataSource();
		}catch(Exception e){
			bReadError=true;
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE,"Caching policy from Exported file. Reason : " + e.getMessage());	
			FileInputStream fis;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(serverHome + File.separator + "system" + File.separator + "_system.accesspolicy"); //NOSONAR - Reason: Resources should be closed
				ois = new ObjectInputStream(fis);
				if((tmpAccessPolicies = ((HashMap<String,AccountAccessPolicyData>)ois.readObject())) == null) {
					tmpAccessPolicies = new HashMap<String,AccountAccessPolicyData>();
				}
			} catch (Exception e1) {
				throw new ManagerInitialzationException("Problem reading the policy data from the exproted file." , e1);
			} finally {
				Closeables.closeQuietly(ois);
			}
		}		
		this.accessPolicies=tmpAccessPolicies;
	}
	
	public int getWeekDayID(String strWeekDayName) throws IllegalWeekDayNameException {
		int iWeekDayID;
		if(strWeekDayName==null || strWeekDayName.trim().length()==0){
			throw new IllegalWeekDayNameException("WeekDay Name "+strWeekDayName+" is not valid");
		}else{
			strWeekDayName = strWeekDayName.trim();
			if(strWeekDayName.equalsIgnoreCase(SUNDAY)||strWeekDayName.equalsIgnoreCase(SUNDAY1)){
				iWeekDayID = 0;
			}else if(strWeekDayName.equalsIgnoreCase(MONDAY)||strWeekDayName.equalsIgnoreCase(MONDAY1)){
				iWeekDayID = 1;
			}else if(strWeekDayName.equalsIgnoreCase(TUESDAY)||strWeekDayName.equalsIgnoreCase(TUESDAY1)){
				iWeekDayID = 2;
			}else if(strWeekDayName.equalsIgnoreCase(WEDNESDAY)||strWeekDayName.equalsIgnoreCase(WEDNESDAY1)){
				iWeekDayID = 3;
			}else if(strWeekDayName.equalsIgnoreCase(THURSDAY)||strWeekDayName.equalsIgnoreCase(THURSDAY1)){
				iWeekDayID = 4;
			}else if(strWeekDayName.equalsIgnoreCase(FRIDAY)||strWeekDayName.equalsIgnoreCase(FRIDAY1)){
				iWeekDayID = 5;
			}else if(strWeekDayName.equalsIgnoreCase(SATURDAY)||strWeekDayName.equalsIgnoreCase(SATURDAY1)){
				iWeekDayID = 6;
			}else{
				throw new IllegalWeekDayNameException("WeekDay Name "+strWeekDayName+" is not valid");
			}
		}
		return iWeekDayID;
	}
	
	public static class IllegalWeekDayNameException extends Exception{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 6521900419022965531L;

		/**
		 * @param string
		 */
		public IllegalWeekDayNameException(String string) {
			super(string);		
		}
		
	}
	
	private long calculateSessionTimeout(int startDay,int startHour,int startMinute,int startSec,
			int endDay,int stopHour,int stopMinute,int stopSec){
		long sessionTimeout=0;
		int startTime = (startDay*24*60*60) + (startHour*60*60) + (startMinute*60)+startSec;
		int endTime = (endDay*24*60*60) + (stopHour*60*60) + (stopMinute*60) + stopSec;
		if(startTime>endTime){
			long firstSessionTimeout = calculateSessionTimeout(startDay, startHour, startMinute, startSec, 6, 23, 59,59);
			long secondSessionTimeout = calculateSessionTimeout(0, 0, 0, 0, endDay, stopHour, stopMinute, stopSec);
			sessionTimeout = firstSessionTimeout+secondSessionTimeout +1;
		}else{
			sessionTimeout = endTime-startTime;
		}
		return sessionTimeout;
	}
	/**
	 * If access is not allow this method 
	 * throws <code>AccessDeniedException</code>
	 * otherwise return the sessionTimeout.
	 * 
	 * @param accessPolicyDetailInfoList
	 * @return long sessionTimeout
	 * @throws AccessDeniedException
	 */
	private  long applyAccessPolicy(AccountAccessPolicyData accessPolicyData,Calendar currentTime)throws AccessDeniedException {

		int currentSec = currentTime.get(Calendar.SECOND);
		int currentMinute = currentTime.get(Calendar.MINUTE);
		int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
		int dayOfWeek = currentTime.get(Calendar.DAY_OF_WEEK)-1;
		
		AccessPolicyTime checkTimeSlot = new AccessPolicyTime(dayOfWeek,currentHour,currentMinute,currentSec,dayOfWeek,currentHour,currentMinute,currentSec);
		
		ArrayList<AccessPolicyTime> accessPolicyTimeSlots = accessPolicyData.getAccessPolicyTimeSlots();
		char defaultAccess = accessPolicyData.getPolicyAccessFlag();
		AccessPolicyTime policyTime = null;
		for(int i=0;i<accessPolicyTimeSlots.size();i++){
			policyTime = accessPolicyTimeSlots.get(i);
			int result = policyTime.compareTo(checkTimeSlot);
			if(result == 1){
				continue;
			}else if(result == -1){
				//do default action
				if(defaultAccess == 'A'){
					long sessionTimeout =  calculateSessionTimeout(dayOfWeek, currentHour, currentMinute, currentSec, 
							policyTime.getStartDay(), policyTime.getStartHour(), policyTime.getStartMinute(),policyTime.getStartSecond());
					if(sessionTimeout==0)
						throw new AccessDeniedException("Access denied at this time");
					return sessionTimeout;
				}
				else
					throw new AccessDeniedException("Access denied at this time");
			}else if(result == 0){
				AccessPolicyTime firstPolicySlot = accessPolicyTimeSlots.get(0);
				if(policyTime.getEndDay()==LAST_DAY_OF_WEEK && policyTime.getStopHour()==LAST_HOUR_OF_DAY && policyTime.getStopMinute()==LAST_MINUTE_OF_HOUR &&
						policyTime.getStopSecond()==LAST_SECOND_OF_MINUTE && firstPolicySlot.getStartDay()==FIRST_DAY_OF_WEEK && firstPolicySlot.getStartHour()==FIRST_HOUR_OF_DAY &&
						firstPolicySlot.getStartMinute()==FIRST_MINUTE_OF_HOUR && firstPolicySlot.getStartSecond()==FIRST_SECOND_OF_MINUTE)
					policyTime = firstPolicySlot;
				if(defaultAccess == 'A')
					throw new AccessDeniedException("Access denied at this time");
				else{
					long sessionTimeout = calculateSessionTimeout(dayOfWeek, currentHour, currentMinute, currentSec, 
							policyTime.getEndDay(), policyTime.getStopHour(), policyTime.getStopMinute(),policyTime.getStopSecond());
					if(sessionTimeout==0)
						throw new AccessDeniedException("Access denied at this time");
					return sessionTimeout;
				}
			}
		}
		if(policyTime==null){
			if(defaultAccess =='D')
				throw new AccessDeniedException("Access denied at this time");
			else
				return 0;
		}else{
			if(defaultAccess=='D')
				throw new AccessDeniedException("Access denied at this time");
			else{
				policyTime = accessPolicyTimeSlots.get(0);
				long sessionTimeout = calculateSessionTimeout(dayOfWeek, currentHour, currentMinute, currentSec, 
						policyTime.getStartDay(),policyTime.getStartHour(),policyTime.getStartMinute(),policyTime.getStartSecond());
				if(sessionTimeout==0)
					throw new AccessDeniedException("Access denied at this time");
				return sessionTimeout;
			}
		}
	}
	
	protected void setPolicyMap(Map<String,AccountAccessPolicyData> policiesMap){
		this.accessPolicies = policiesMap;
	}
	
	@Override
	public CacheDetail reloadCache() {
		Map<String, AccountAccessPolicyData> tmpAccessPolicies = null;
		CacheDetailProvider cacheDetail = new CacheDetailProvider();
		cacheDetail.setName(MODULE);
		cacheDetail.setSource(getSource());
		try {
			tmpAccessPolicies = readFromDataSource();
			this.accessPolicies=tmpAccessPolicies;
			cacheDetail.setResultCode(CacheConstants.SUCCESS);
		} catch (LoadCacheFailedException e) {
			cacheDetail.setResultCode(CacheConstants.FAIL);
			cacheDetail.setDescription(e.getMessage());
		}	
		return cacheDetail;
	}
	
	private Map<String, AccountAccessPolicyData> readFromDataSource() throws LoadCacheFailedException{

		Connection connection = null;
		Map<String, AccountAccessPolicyData> tmpAccessPolicies = new HashMap<String, AccountAccessPolicyData>();
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
		} catch (DataSourceException e) {
			bReadError=true;
			throw new LoadCacheFailedException("Connection has not been established for datasource, Reason: " + e.getMessage(), e);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			preparedStatement = connection.prepareStatement("SELECT NAME , ACCESSPOLICYID,DESCRIPTION,ACCESSSTATUS FROM TBLMACCESSPOLICY WHERE COMMONSTATUSID = '"+CommonConstants.DATABASE_POLICY_STATUS_ACTIVE+"'");
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){

				String strPolicyId    = resultSet.getString("ACCESSPOLICYID");
				String strPolicyName  = resultSet.getString("NAME");
				String strDescription = resultSet.getString("DESCRIPTION");
				char defaultAccess    = resultSet.getString("ACCESSSTATUS").trim().charAt(0);

				AccountAccessPolicyData accessPolicyData = new AccountAccessPolicyData(strPolicyId,	strPolicyName,strDescription,defaultAccess);
				tmpAccessPolicies.put(strPolicyName, accessPolicyData);
			}
		}catch(SQLException e){
			this.bReadError=true;
			DBUtility.closeQuietly(connection);
			throw new LoadCacheFailedException(e.getMessage(),e);
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
		}
		try{
			Iterator<AccountAccessPolicyData> accessPolicyIterator = tmpAccessPolicies.values().iterator();
			preparedStatement = connection.prepareStatement("SELECT STARTTIME, STOPTIME, STARTDAY,ENDDAY FROM TBLMACCESSPOLICYDETAIL WHERE ACCESSPOLICYID = ?");
			while(accessPolicyIterator.hasNext()){
				AccountAccessPolicyData accessPolicyData = accessPolicyIterator.next();
				String strPolicyId = accessPolicyData.getAccessPolicyID();
				preparedStatement.setString(1,strPolicyId);
				resultSet = preparedStatement.executeQuery();
				while(resultSet.next()){
					try{
						int iStartDay = 0;
						int iEndDay = 0;
						Timestamp tStartTime,tStopTime;

						iStartDay  = getWeekDayID(resultSet.getString("STARTDAY"));
						iEndDay    = getWeekDayID(resultSet.getString("ENDDAY"));
						tStartTime = resultSet.getTimestamp("STARTTIME");
						tStopTime  = resultSet.getTimestamp("STOPTIME");

						AccessPolicyTime accessPolicyTimeSlot = new AccessPolicyTime(iStartDay,tStartTime.getHours(),tStartTime.getMinutes(),
								iEndDay,tStopTime.getHours(),tStopTime.getMinutes());
						accessPolicyData.addTimeSlot(accessPolicyTimeSlot);
					}catch (IllegalWeekDayNameException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE,"Illegal week day in policy, name of access policy is : "+accessPolicyData.getAccessPolicyName());
					}
				}
				try{
					FileOutputStream fos = new FileOutputStream(serverHome + File.separator + "system" + File.separator + "_system.accesspolicy"); //NOSONAR - Reason: Resources should be closed
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(tmpAccessPolicies);
					oos.close();
				} catch (Exception e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Error while serializing Access Policies. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				} 
			}
		}catch(SQLException e){
			this.bReadError=true;
			throw new LoadCacheFailedException(e.getMessage(),e);
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
		return tmpAccessPolicies;
	}
	private String getSource() {
		return "AAA_Server_DS";
	}
	public String getName(){
		return MODULE;
	}
	
}