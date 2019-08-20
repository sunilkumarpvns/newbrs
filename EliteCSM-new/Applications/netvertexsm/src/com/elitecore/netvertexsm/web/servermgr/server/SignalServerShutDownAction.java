package com.elitecore.netvertexsm.web.servermgr.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ViewNetServerLiveDetailsForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class SignalServerShutDownAction  extends BaseWebAction{
    private static final String SUCCESS_FORWARD = "success";	
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "STOP NET SERVER ACTION";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.SIGNAL_SERVER_SHUTDOWN_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
    	Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
    	NetServerBLManager netServerBLManager = new NetServerBLManager();
    	ViewNetServerLiveDetailsForm netServerLiveDetailsForm = (ViewNetServerLiveDetailsForm)form;
    	IRemoteCommunicationManager remoteCommunicationManager = null;
    	try {
    		checkActionPermission(request,SUB_MODULE_ACTIONALIAS);

    		String strNetServerId = request.getParameter("netServerId");
    		Long netServerId=null;
    		if(strNetServerId != null){
    			netServerId = Long.parseLong(strNetServerId);
    		}else{
    			netServerId=netServerLiveDetailsForm.getNetServerId();
    		}

    		if(netServerId != null){
    			netServerLiveDetailsForm.setNetServerId(netServerId);
    			INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServerId);
    			String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				
    			remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
    			remoteCommunicationManager.init(serverInstanceData.getAdminHost(),serverInstanceData.getAdminPort(),netServerCode,true);
    			Boolean success = (Boolean)remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"shutdownServer",null,null);
    			if(success){
    				request.setAttribute("responseUrl","/viewNetServerInstance.do?netserverid="+netServerId);
    				ActionMessage message = new ActionMessage("servermgr.server.stop.request.success");
    				ActionMessages messages = new ActionMessages();
    				messages.add("information",message);
    				saveMessages(request,messages);
    				return mapping.findForward(SUCCESS_FORWARD);
    			}
    		}
    		Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
    		ActionMessage message = new ActionMessage("servermgr.server.stop.request.failure");
    		ActionMessages messages = new ActionMessages();
    		messages.add("information",message);
    		saveErrors(request,messages);
    		return mapping.findForward(FAILURE_FORWARD);

    	}catch(ActionNotPermitedException e){
    		Logger.logError(MODULE,"Error :-" + e.getMessage());
    		printPermitedActionAlias(request);
    		ActionMessages messages = new ActionMessages();
    		messages.add("information", new ActionMessage("general.user.restricted"));
    		saveErrors(request, messages);
    		return mapping.findForward(INVALID_ACCESS_FORWARD);
    	}catch (UnidentifiedServerInstanceException commExp) {
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
    	} catch (CommunicationException e) {
    		Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
    		Logger.logTrace(MODULE,e);
    		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    		request.setAttribute("errorDetails", errorElements);
    		ActionMessage message = new ActionMessage("servermgr.server.communication.failure");
    		ActionMessages messages = new ActionMessages();
    		messages.add("information",message);
    		saveErrors(request,messages);
    	} catch (Exception e) {
    		Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
    		Logger.logTrace(MODULE,e);
    		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    		request.setAttribute("errorDetails", errorElements);
    		ActionMessage message = new ActionMessage("servermgr.server.stop.request.failure");
    		ActionMessages messages = new ActionMessages();
    		messages.add("information",message);
    		saveErrors(request,messages);
    		Logger.logTrace(MODULE,e.getMessage());
    	}
    	finally {            
    		try{
    			if(remoteCommunicationManager != null)
    				remoteCommunicationManager.close();  
    		}
    		catch (Throwable e) {
    			remoteCommunicationManager = null;
    		}
    	}

    	return mapping.findForward(FAILURE_FORWARD);
    }
}
