package com.elitecore.elitesm.ws;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.AxisFault;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.exception.DatabaseConnectionException;
import com.elitecore.elitesm.ws.exception.SubscriberProfileWebServiceException;
import com.elitecore.elitesm.ws.logger.Logger;
import com.elitecore.elitesm.ws.subscriber.SubscriberProfileWebServiceBLManager;

public class SubscriberProfileWS extends BaseWS {

	private static final String MODULE ="SubscriberProfileWS";

	private static SubscriberProfileWebServiceBLManager blManager = new SubscriberProfileWebServiceBLManager();
	private static final int MAX_TIME_FOR_WS_EXECUTION_IN_MS = 100;
	private static final int MAX_TIME_FOR_CHECK_PERMISSION_IN_MS = 10;
	
	public Map<String,Map<String,String>> findByUserIdentity(String userIdentity) throws SubscriberProfileWebServiceException,AxisFault{
		Map<String,Map<String,String>> resultData = new HashMap<String,Map<String,String>>();
		boolean success = false;
		String errorMsg = "NO-RESULT";
		String methodName= "findByUserIdentity";
		String methodData=userIdentity;
		long currentTimeInMS = 0;
		long totalExecutionTime = 0;
		
		try{
			currentTimeInMS = System.currentTimeMillis();
			
			checkPermission(userName,ConfigConstant.FIND_BY_USERIDENTITY);
			
			if(System.currentTimeMillis() - currentTimeInMS > MAX_TIME_FOR_CHECK_PERMISSION_IN_MS){
				Logger.logWarn(MODULE, "Total time taken for checkpermission in Find-By-UserIdentity web service: "+ (System.currentTimeMillis() - currentTimeInMS)+" ms");
			}
			resultData = blManager.findByUserIdentity(userIdentity);
			totalExecutionTime = System.currentTimeMillis() - currentTimeInMS;
			if(totalExecutionTime > MAX_TIME_FOR_WS_EXECUTION_IN_MS){
				Logger.logWarn(MODULE, "Total time taken by Find-By-UserIdentity web service: "+ totalExecutionTime+" ms");
			}
			Logger.logInfo(MODULE, "Total time taken by Find-By-UserIdentity web service: "+ totalExecutionTime+" ms");
			success = true;

		}catch(DataManagerException e){
			errorMsg = e.getMessage();
			Logger.logError(MODULE, methodName + errorMsg);
			logWebServiceError(MODULE,methodName,methodData,e);

		}catch(DatabaseConnectionException e){
			errorMsg = e.getMessage();
			Logger.logError(MODULE, methodName + errorMsg);
			logWebServiceError(MODULE,methodName,methodData,e);

		}catch(SQLException e){
			errorMsg = e.getMessage();
			Logger.logError(MODULE, methodName + errorMsg);
			logWebServiceError(MODULE,methodName,methodData,e);

		}catch(Exception e){
			errorMsg = e.getMessage();
			Logger.logError(MODULE, methodName + errorMsg); 
			Logger.logTrace(MODULE, e);
			logWebServiceError(MODULE,methodName,methodData,e);
		}
		if(! success){
			throw new AxisFault(methodName + ": " + errorMsg);
		}
		logWebServiceResponse(MODULE,methodName,methodData,resultData);
		return resultData;
	}

	public int addSubscriber( Map<String, Object> subscriberProfileDataMap) throws SubscriberProfileWebServiceException,AxisFault{
		Logger.logInfo(MODULE, "Add Subscriber Profile Data Map" + subscriberProfileDataMap);
		String methodName= "addSubscriber";
		int noOfInserts = 0;
		
		long currentTimeInMS = 0;
		long totalExecutionTime = 0;
		
		if(subscriberProfileDataMap!=null){
			boolean success = false;
			String errorMsg = "NO-RESULT";
			
			try{
				currentTimeInMS = System.currentTimeMillis();
				//checkPermission(userName,ConfigConstant.ADD_SUBSCRIBER);
				noOfInserts =blManager.addSubscriber(subscriberProfileDataMap);
				totalExecutionTime = System.currentTimeMillis() - currentTimeInMS;
				if(totalExecutionTime > MAX_TIME_FOR_WS_EXECUTION_IN_MS){
					Logger.logWarn(MODULE, "Total time taken by Add-Subscriber web service: "+ totalExecutionTime+" ms");
				}
				Logger.logInfo(MODULE, "Total time taken by Add-Subscriber web service: "+ totalExecutionTime+" ms");
				success = true;

			}catch(DatabaseConnectionException e){
				errorMsg = e.getMessage();
				Logger.logError(MODULE, methodName + errorMsg);
				logWebServiceError(MODULE,methodName,null,e);

			}catch(SQLException e){
				errorMsg = e.getMessage();
				Logger.logError(MODULE, methodName + errorMsg);
				logWebServiceError(MODULE,methodName,null,e);

			}catch(Exception e){
				errorMsg = e.getMessage();
				Logger.logError(MODULE, methodName + errorMsg);
				Logger.logTrace(MODULE, e);
				logWebServiceError(MODULE,methodName,null,e);
			}
			if(! success){
				throw new AxisFault(methodName + ": " + errorMsg);
			}
			logWebServiceResponse(MODULE,methodName,subscriberProfileDataMap.toString());
		}else{
			logWebServiceResponse(MODULE,methodName,null);
		}
		return noOfInserts;
	}
	
	public int updateSubscriber(Map<String,Object> subscriberProfileDataMap,String userIdentity) throws SubscriberProfileWebServiceException,AxisFault{
		Logger.logInfo(MODULE, "Update Subscriber Profile Data" + subscriberProfileDataMap+ "User Identity:"+userIdentity);
		String methodName= "updateSubscriber";
		int noOfUpdates=0;
		
		long currentTimeInMS = 0;
		long totalExecutionTime = 0;
		
		if(subscriberProfileDataMap!=null){
			boolean success = false;
			String errorMsg = "NO-RESULT";
			
			try{
				currentTimeInMS = System.currentTimeMillis();
				//checkPermission(userName,ConfigConstant.UPDATE_SUBSCRIBER);
				noOfUpdates = blManager.updateSubscriber(subscriberProfileDataMap,userIdentity);
				totalExecutionTime = System.currentTimeMillis() - currentTimeInMS;
				if(totalExecutionTime > MAX_TIME_FOR_WS_EXECUTION_IN_MS){
					Logger.logWarn(MODULE, "Total time taken by Update-Subscriber web service: "+ totalExecutionTime+" ms");
				}
				Logger.logInfo(MODULE, "Total time taken by Update-Subscriber web service: "+ totalExecutionTime+" ms");
				success = true;

			}catch(DatabaseConnectionException e){
				errorMsg = e.getMessage();
				Logger.logError(MODULE, methodName + errorMsg);
				logWebServiceError(MODULE,methodName,null,e);

			}catch(SQLException e){
				errorMsg = e.getMessage();
				Logger.logError(MODULE, methodName + errorMsg);
				logWebServiceError(MODULE,methodName,null,e);

			}catch(Exception e){
				errorMsg = e.getMessage();
				Logger.logError(MODULE, methodName + errorMsg);
				Logger.logTrace(MODULE, e);
				logWebServiceError(MODULE,methodName,null,e);
			}
			if(! success){
				throw new AxisFault(methodName + ": " + errorMsg);
			}
			logWebServiceResponse(MODULE,methodName,subscriberProfileDataMap + ","+userIdentity);
		}else{
			logWebServiceResponse(MODULE,methodName,subscriberProfileDataMap + ","+userIdentity);
		}
		return noOfUpdates;
	} 
	
	public int delSubscriber(String userIdentity) throws SubscriberProfileWebServiceException,AxisFault{
		Logger.logInfo(MODULE, "Delete profile, User Identity: " + userIdentity);
		boolean success = false;
		String errorMsg = "NO-RESULT";
		String methodName= "delSubscriber";
		int noOfDeletes = 0;
		
		long currentTimeInMS = 0;
		long totalExecutionTime = 0;
		
		try{
			//checkPermission(userName,ACTION_ALIAS_DELETE_SUBSCRIBER);
			currentTimeInMS = System.currentTimeMillis();
			noOfDeletes = blManager.delSubscriber(userIdentity);
			totalExecutionTime = System.currentTimeMillis() - currentTimeInMS;
			if(totalExecutionTime > MAX_TIME_FOR_WS_EXECUTION_IN_MS){
				Logger.logWarn(MODULE, "Total time taken by Delete-Subscriber web service: "+ totalExecutionTime+" ms");
			}
			Logger.logInfo(MODULE, "Total time taken by Delete-Subscriber web service: "+ totalExecutionTime+" ms");
			success = true;

		}catch(DatabaseConnectionException e){
			errorMsg = e.getMessage();
			Logger.logError(MODULE, methodName + errorMsg);
			logWebServiceError(MODULE,methodName,null,e);

		}catch(SQLException e){
			errorMsg = e.getMessage();
			Logger.logError(MODULE, methodName + errorMsg);
			logWebServiceError(MODULE,methodName,null,e);

		}catch(Exception e){
			errorMsg = e.getMessage();
			Logger.logError(MODULE, methodName + errorMsg);
			Logger.logTrace(MODULE, e);
			logWebServiceError(MODULE,methodName,null,e);
		}
		if(! success){
			throw new AxisFault(methodName + ": " + errorMsg);
		}
		logWebServiceResponse(MODULE,methodName,userIdentity);
		return noOfDeletes;
	} 
}
