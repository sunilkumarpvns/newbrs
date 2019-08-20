package com.elitecore.elitesm.ws;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.AxisFault;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.exception.DatabaseConnectionException;
import com.elitecore.elitesm.ws.exception.EmptyAttributeFieldMappingException;
import com.elitecore.elitesm.ws.exception.SessionMgmtWebServiceException;
import com.elitecore.elitesm.ws.logger.Logger;
import com.elitecore.elitesm.ws.sessionmgmt.SessionMgmtWebServiceBLManager;


public class SessionManagerWS extends BaseWS{

	private static final String MODULE ="Session Manager WS";
	private static SessionMgmtWebServiceBLManager blManager = new SessionMgmtWebServiceBLManager();
	private static final int SM_WS_FIND_BY_IPADDR = 1;
	private static final int SM_WS_FIND_BY_USER = 2;
	private static final int SM_WS_FIND_BY_SERVICE = 3;
	
	private static final String ACTION_ALIAS_FIND_BY_FRAMED_IPADDR = ConfigConstant.FIND_BY_FRAMED_IP_ADDRESS;
	private static final String ACTION_ALIAS_FIND_BY_USER = ConfigConstant.FIND_BY_USER;
	private static final String ACTION_ALIAS_FIND_BY_SERVICE = ConfigConstant.FIND_BY_SERVICE;
	private static final String ACTION_ALIAS_FIND_BY_ATTRIBUTE=ConfigConstant.FIND_BY_ATTRIBUTE;
	
	private static final int MAX_TIME_FOR_WS_EXECUTION_IN_MS = 100;
	private static final int MAX_TIME_FOR_CHECK_PERMISSION_IN_MS = 10;
	
	private Map<String,Map<String,String>> sessionManagementGeneric(int methodType, String methodName, String methodData)
	throws AxisFault
	{
		Map<String,Map<String,String>> resultData = new HashMap<String,Map<String,String>>();
		boolean success = false;
		String errorMsg = "NO-RESULT";
		
		long currentTimeInMS = 0;
		long totalExecutionTime = 0;
		
		try {
	    		
		switch (methodType) {
		case SM_WS_FIND_BY_IPADDR:
			
			currentTimeInMS = System.currentTimeMillis();
			
			checkPermission(userName,ACTION_ALIAS_FIND_BY_FRAMED_IPADDR);
			if(System.currentTimeMillis() - currentTimeInMS > MAX_TIME_FOR_CHECK_PERMISSION_IN_MS){
				Logger.logWarn(MODULE, "Total time taken for checkpermission in Find-By-IpAdress web service: "+ (System.currentTimeMillis() - currentTimeInMS) +" ms");
			}
			resultData = blManager.findByFramedIPAddress(methodData);
			totalExecutionTime = System.currentTimeMillis() - currentTimeInMS;

			if(totalExecutionTime > MAX_TIME_FOR_WS_EXECUTION_IN_MS){
				Logger.logWarn(MODULE, "Total time taken by Find-By-IpAdress web service: "+ totalExecutionTime+" ms");
			}
			Logger.logInfo(MODULE, "Total time taken by Find-By-IpAdress web service: "+ totalExecutionTime+" ms");
			break;
			
		case SM_WS_FIND_BY_USER:
			currentTimeInMS = System.currentTimeMillis();
			
			checkPermission(userName,ACTION_ALIAS_FIND_BY_USER);
			if(System.currentTimeMillis() - currentTimeInMS > MAX_TIME_FOR_CHECK_PERMISSION_IN_MS){
				Logger.logWarn(MODULE, "Total time taken for checkpermission in Find-By-Username web service: "+ (System.currentTimeMillis() - currentTimeInMS)+" ms");
			}
			
			resultData = blManager.findByUserName(methodData);
			totalExecutionTime = System.currentTimeMillis() - currentTimeInMS;
			
			if(totalExecutionTime > MAX_TIME_FOR_WS_EXECUTION_IN_MS){
				Logger.logWarn(MODULE, "Total time taken by Find-By-Username web service: "+ totalExecutionTime+" ms");
			}
			
			Logger.logInfo(MODULE, "Total time taken by Find-By-Username web service: "+ totalExecutionTime+" ms");
			break;
			
		case SM_WS_FIND_BY_SERVICE:
			currentTimeInMS = System.currentTimeMillis();
			
			checkPermission(userName,ACTION_ALIAS_FIND_BY_SERVICE);
			if(System.currentTimeMillis() - currentTimeInMS > MAX_TIME_FOR_CHECK_PERMISSION_IN_MS){
				Logger.logWarn(MODULE, "Total time taken for checkpermission in Find-By-ServiceType web service: "+ (System.currentTimeMillis() - currentTimeInMS)+" ms");
			}
			
			resultData = blManager.findByServiceType(methodData);
			totalExecutionTime = System.currentTimeMillis() - currentTimeInMS;
			if(totalExecutionTime > MAX_TIME_FOR_WS_EXECUTION_IN_MS){
				Logger.logWarn(MODULE, "Total time taken by Find-By-ServiceType web service: "+ totalExecutionTime+" ms");
			}
			Logger.logInfo(MODULE, "Total time taken by Find-By-ServiceType web service: "+ totalExecutionTime+" ms");
			break;
		default:
			errorMsg = "No such method defined";
			break;	
		}
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
	

	

	public Map<String,Map<String,String>> findByServiceType(String serviceType) throws AxisFault
	{
		logWebServiceRequest(MODULE,"findByServiceType", serviceType);
		return sessionManagementGeneric(SM_WS_FIND_BY_SERVICE, "findByServiceType", serviceType);
	}

	public Map<String,Map<String,String>> findByFramedIPAddress(String ipAddress) throws AxisFault {
		logWebServiceRequest(MODULE,"findByFramedIPAddress", ipAddress);
		return sessionManagementGeneric(SM_WS_FIND_BY_IPADDR, "findByFramedIPAddress", ipAddress);
	}

	public Map<String,Map<String,String>> findByUserName(String userName) throws AxisFault{
		logWebServiceRequest(MODULE,"findByUserName", userName);
		return sessionManagementGeneric(SM_WS_FIND_BY_USER, "findByUserName", userName);
	}

	public Map<String,Map<String,String>> findByAttribute(String attribute,String value) throws AxisFault{
		
		
		String errorMsg = "NO-RESULT";
		String methodName= "findByAttribute";
		logWebServiceRequest(MODULE,"findByAttribute", attribute+"="+value);
		Map<String,Map<String,String>> resultData = new HashMap<String,Map<String,String>>();
		boolean success = false;
		try{
			
			long currentTimeInMS = System.currentTimeMillis();
			
			checkPermission(userName,ACTION_ALIAS_FIND_BY_ATTRIBUTE);
			
			if(System.currentTimeMillis() - currentTimeInMS > MAX_TIME_FOR_CHECK_PERMISSION_IN_MS){
				Logger.logWarn(MODULE, "Total time taken for checkpermission in Find-By-Attribute web service: "+ (System.currentTimeMillis() - currentTimeInMS)+" ms");
			}
			
			resultData = blManager.findByAttribute(attribute,value);
			
			long totalExecutionTime = System.currentTimeMillis() - currentTimeInMS;
			
			if(totalExecutionTime > MAX_TIME_FOR_WS_EXECUTION_IN_MS){
				Logger.logWarn(MODULE, "Total time taken by Find-By-Attribute web service: "+ totalExecutionTime+" ms");
			}
			
			Logger.logInfo(MODULE, "Total time taken by Find-By-Attribute web service: "+ totalExecutionTime+" ms");
			success=true;
		}catch(DataManagerException e){
			errorMsg = e.getMessage();
			Logger.logError(MODULE, methodName + errorMsg);
			logWebServiceError(MODULE,methodName,attribute+"="+value,e);

		}catch(DatabaseConnectionException e){
			errorMsg = e.getMessage();
			Logger.logError(MODULE, methodName + errorMsg);
			logWebServiceError(MODULE, "findByAttribute",attribute+"="+value, e);
		}catch(SQLException e){
			errorMsg = e.getMessage();
			Logger.logError(MODULE, methodName + errorMsg);
			logWebServiceError(MODULE, "findByAttribute",attribute+"="+value, e);
		}catch(SessionMgmtWebServiceException e){
			errorMsg = e.getMessage();
			Logger.logError(MODULE, methodName + errorMsg);
			logWebServiceError(MODULE, "findByAttribute",attribute+"="+value, e);
		}catch(EmptyAttributeFieldMappingException e){
			errorMsg = e.getMessage();
			Logger.logError(MODULE, methodName + errorMsg);
			logWebServiceError(MODULE, "findByAttribute",attribute+"="+value, e);
		}catch(Exception e){
			errorMsg = e.getMessage();
			Logger.logError(MODULE, methodName + errorMsg);
			Logger.logTrace(MODULE, e);
			logWebServiceError(MODULE, methodName,attribute+"="+value, e);
		}
		if(! success){
			throw new AxisFault(methodName + ": " + errorMsg);
		}
		if(success){
			logWebServiceResponse(MODULE, methodName, attribute+"="+value, resultData);
		}

		return resultData;
	}

	
}
