/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitNetServerStartStopAction.java                             
 * ModualName                                     
 * Created on Dec 11, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.web.servermgr.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;


public class InitNetServerCLIAction extends BaseWebAction {
    
    private static final String VIEW_FORWARD    = "viewNetserverCLI";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE          = "INIT NETSERVER CLI";
    private static final String ACTION_ALIAS          = ConfigConstant.CLI_ACTION;
    public ActionForward execute( ActionMapping mapping ,ActionForm form ,HttpServletRequest request ,HttpServletResponse response ) throws Exception {
    	Logger.logInfo(MODULE, "Enter execute method of " + getClass().getName());
        String forwardPath=null;
    	try {
    		checkActionPermission(request,ACTION_ALIAS);
    		String strNetserverid=request.getParameter("netserverid");
    		NetServerBLManager netServerBLManager = new NetServerBLManager();
    		INetServerInstanceData netServerInstanceData=netServerBLManager.getNetServerInstance(strNetserverid);
    		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
     		String serverInstanceName=netServerInstanceData.getName();
    		String adminHost=netServerInstanceData.getAdminHost();
    		int adminPort=netServerInstanceData.getAdminPort();
    			
    		forwardPath=mapping.findForward(VIEW_FORWARD).getPath();
    		
    		if(forwardPath!=null){
				ActionForward actionForward = new ActionForward();
				actionForward.setPath(forwardPath + "?serverInstanceName="+serverInstanceName+"&adminHost="+adminHost+"&adminPort="+adminPort);
				doAuditing(staffData, ACTION_ALIAS);
				return actionForward;
			}
    		
    	}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception e) {
    		Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
    		Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
    		ActionMessage message = new ActionMessage("servermgr.server.cli.failure");
    		ActionMessages messages = new ActionMessages();
    		messages.add("information", message);
    		saveErrors(request, messages);
    	}
    	
    	return mapping.findForward(FAILURE_FORWARD);
    	
    }	
   
}
