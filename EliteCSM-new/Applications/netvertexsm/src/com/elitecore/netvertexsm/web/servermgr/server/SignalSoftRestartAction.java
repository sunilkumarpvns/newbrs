package com.elitecore.netvertexsm.web.servermgr.server;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.logging.LogManager;
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
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ViewNetServerLiveDetailsForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class SignalSoftRestartAction extends BaseWebAction{
    private static final String SUCCESS_FORWARD = "success";	
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "NETSERVER SOFT RESTART";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.SIGNAL_SOFT_RESTART_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
    	LogManager.getLogger().trace(MODULE,"Enter execute method of "+getClass().getName());
        NetServerBLManager netServerBLManager = new NetServerBLManager();
        ViewNetServerLiveDetailsForm viewNetServerLiveDetailsForm = (ViewNetServerLiveDetailsForm)form;
        IRemoteCommunicationManager remoteCommunicationManager = null;

        try {
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            
            String strNetServerId = request.getParameter("netServerId");
			Long netServerId=null;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
			}
            if(netServerId != null){
                viewNetServerLiveDetailsForm.setNetServerId(netServerId);
                INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServerId);
                String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				
                remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
                remoteCommunicationManager.init(serverInstanceData.getAdminHost(),serverInstanceData.getAdminPort(),netServerCode,true);
                remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","softRestart",null,null);

                request.setAttribute("responseUrl","/viewNetServerLiveDetails.do?netServerId="+netServerId);				
                ActionMessage message = new ActionMessage("servermgr.server.soft.restart.success");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveMessages(request,messages);
                return mapping.findForward(SUCCESS_FORWARD);
            }else{
            	LogManager.getLogger().error(MODULE,"Returning error forward from "+getClass().getName());
                ActionMessage message = new ActionMessage("servermgr.server.soft.restart.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
        } catch(ActionNotPermitedException e){
        	LogManager.getLogger().error(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        } catch (UnidentifiedServerInstanceException commExp) {
            commExp.printStackTrace();
            LogManager.getLogger().error(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");			
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            messages.add("information",messageReason);
            saveErrors(request,messages);
        } catch (CommunicationException e) {
        	LogManager.getLogger().error(MODULE,"Returning error forward from "+ getClass().getName());
        	LogManager.getLogger().trace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.communication.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
        } catch (Exception e) {
        	LogManager.getLogger().error(MODULE,"Returning error forward from "+getClass().getName());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.soft.restart.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
        }
        finally{
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
