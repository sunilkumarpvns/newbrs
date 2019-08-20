/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitNetServerStartStopAction.java                             
 * ModualName                                     
 * Created on Dec 11, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.web.servermgr.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.util.mbean.data.live.EliteNetServerDetails;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.InitNetServerLicenseForm;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.util.SystemUtil;
import com.elitecore.passwordutil.PasswordEncryption;

/**
 * @author kaushikvira
 */
public class InitNetServerLicenseAction extends BaseDictionaryAction {
    
    private static final String VIEW_FORWARD    = "viewNetserverLicense";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE          = "INIT NETSERVER LICENSE";
    
    public ActionForward execute( ActionMapping mapping ,
    		ActionForm form ,
    		HttpServletRequest request ,
    		HttpServletResponse response ) throws Exception {
    	Logger.logInfo(MODULE, "Enter execute method of " + getClass().getName());
    	List netServerTypeList = null;
    	IRemoteCommunicationManager remoteCommunicationManager = null;
    	InitNetServerLicenseForm initNetServerLicenseForm = null;

    	try {
    		initNetServerLicenseForm = (InitNetServerLicenseForm) form;
    		String strNetServerId = request.getParameter("netServerId");

			if (strNetServerId == null) {
				strNetServerId = (String) request.getAttribute("netserverid");
			}

    		String netServerId;
    		if (strNetServerId == null) {
    			netServerId = initNetServerLicenseForm.getNetServerId();
    		}else{
    			netServerId = strNetServerId.trim();	
    		}

    		
    		NetServerBLManager netServerBLManager = new NetServerBLManager();

    		INetServerInstanceData netServerInstanceData = new NetServerInstanceData();

    		netServerTypeList = netServerBLManager.getNetServerTypeList();

    		if (netServerId == null)
    			throw new InvalidValueException("netServerId must be specified.");

    		if ( Strings.isNullOrBlank(netServerId) == false) {

    			netServerInstanceData.setNetServerId(netServerId);
    			netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
    			request.setAttribute("netServerInstanceData", netServerInstanceData);
    			request.setAttribute("netServerTypeList", netServerTypeList);
    			request.setAttribute("netServerId", netServerId);
    			String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);                
    			remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
    			remoteCommunicationManager.init(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(),netServerCode,true);
    			List<LicenseData> lstLicenseData = new ArrayList<LicenseData>();
    			try {
    				String compounedLicenseKey = (String) remoteCommunicationManager.execute(MBeanConstants.LICENSE, "readLicense", null, null);
    				if (compounedLicenseKey == null)
    					throw new InvalidLicenseKeyException("compounedLicenseKey From server found null");
    				
    				Map<String,LicenseData> licenseDataMap = SystemUtil.getLicenseInformationMap(compounedLicenseKey);
    				if(licenseDataMap!=null){
	    				for (Iterator<String> iterator = licenseDataMap.keySet().iterator(); iterator.hasNext();) {
							String key = iterator.next();
							LicenseData licenseData = licenseDataMap.get(key);
							lstLicenseData.add(licenseData);
						}
    				}
    			}
    			catch (InvalidLicenseKeyException e) {
    				Logger.logWarn(MODULE, "Invalid License Data found  " + getClass().getName());
    				Logger.logTrace(MODULE, e);
    			}
    			catch (Exception e) {
    				Logger.logWarn(MODULE, "Invalid License Data found  " + getClass().getName());
    				Logger.logTrace(MODULE, e);
    			}
    			
    			initNetServerLicenseForm.setLicenseData(lstLicenseData);
    			Logger.logDebug(MODULE, "Server Status : start");
    			initNetServerLicenseForm.setErrorCode("0");
    			request.setAttribute("initNetServerLicenseForm", initNetServerLicenseForm);
    			
    		}
    		Logger.logInfo(MODULE, "Retuning from the execute() " + getClass().getName());
    		return mapping.findForward(VIEW_FORWARD);
    	} catch (UnidentifiedServerInstanceException commExp) {
    		commExp.printStackTrace();
    		Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
    		ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
    		ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");			
    		ActionMessages messages = new ActionMessages();
    		messages.add("information",message);
    		messages.add("information",messageReason);
    		saveErrors(request,messages);
    		return mapping.findForward(FAILURE_FORWARD);
    	}

    	catch (CommunicationException e) {
    		Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
    		Logger.logTrace(MODULE, e);
    		initNetServerLicenseForm.setErrorCode("-1");
    		EliteNetServerDetails eliteServerDetails = new EliteNetServerDetails();
    		request.setAttribute("eliteLiveServerDetails", eliteServerDetails);
    		return mapping.findForward(VIEW_FORWARD);
    	}
    	catch (Exception e) {
    		Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
    		Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
    		ActionMessage message = new ActionMessage("servermgr.server.initlicense.failure");
    		ActionMessages messages = new ActionMessages();
    		messages.add("information", message);
    		saveErrors(request, messages);
    	}
    	finally {
    		if (remoteCommunicationManager != null)
    			remoteCommunicationManager.close();
    	}
    	return mapping.findForward(FAILURE_FORWARD);
    }
}
