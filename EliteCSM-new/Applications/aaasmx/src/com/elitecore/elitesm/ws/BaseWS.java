package com.elitecore.elitesm.ws;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.log4j.MDC;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.ws.logger.Logger;


public abstract class BaseWS {

	protected static final String MODULE                 = "BaseWebService";
	protected MessageContext messageContext = null;
	protected String clientAddress = "255.255.255.255";
	protected String userName = "Unknown";
	protected String reqSessionID = "FFFFFFFF";
	protected boolean isPasswordCallbackEnabled = false; 
	public BaseWS() {
		messageContext = MessageContext.getCurrentContext();
		userName = (String)MDC.get("userName");
		clientAddress = messageContext.getStrProp(Constants.MC_REMOTE_ADDR); 
		MDC.put("clientAddress", clientAddress);
	
		String strPasswordCallback = (String)  MDC.get("passwordCallback");
		if(strPasswordCallback!=null && strPasswordCallback.equals("true")){
			isPasswordCallbackEnabled = true;
		}
		
		try{
			HttpServletRequest req = (HttpServletRequest)messageContext.getProperty(org.apache.axis.transport.http.HTTPConstants.MC_HTTP_SERVLETREQUEST);
			reqSessionID=req.getSession(true).getId();
		}catch(Exception e){
			
		}
	}

	/**
	 * @author pratik.chauhan
	 * @param  userName
	 * @param  actionAlias 
	 * @return void
	 * @throws DataManagerException 
	 * @purpose This method is generated to check permission for web service Method Access
	 */
	protected void checkPermission(String userName,String actionAlias) throws DataManagerException{ 
		
		try{
			if(isPasswordCallbackEnabled){
				StaffBLManager staffBLManager = new StaffBLManager();
				StaffData staffData = new StaffData();
				staffData.setUsername(userName);
				staffBLManager.checkPermission(staffData,actionAlias);
			}
		}catch(DataManagerException dExp){
			throw new DataManagerException("Action failed :"+dExp.getMessage());
		}
	}
	
	protected void logWebServiceRequest(String module, String method,String arguments){
		Logger.logInfo(module, "Request, "+reqSessionID+", "+method+", "+arguments);
	}
	protected void logWebServiceResponse(String module, String method,String arguments,Map<String,Map<String,String>>resultData){
		Logger.logInfo(module, "Response, "+reqSessionID+", "+method+", "+arguments+", " + (new ResultMap(resultData)));
	}
	protected void logWebServiceResponse(String module, String method,String arguments){
		Logger.logInfo(module, "Response, "+reqSessionID+", "+method+", "+arguments);
	}
	protected void logWebServiceError(String module, String method,String arguments,Throwable e){
		Logger.logError(module,"Response, "+reqSessionID+", "+method+", "+arguments+", " + "###0,"+e.getMessage());
	}
}
