package com.elitecore.elitesm.web.servermgr.server;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.util.mbean.data.live.EliteNetServerDetails;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.server.forms.ViewNetServerLiveDetailsForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class ViewNetServerLiveDetailsAction  extends BaseDictionaryAction{
    private static final String VIEW_FORWARD = "viewNetServerDetail";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "VIEW NET SERVER INSTANCE ACTION";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.VIEW_SERVER_DETAIL_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
        NetServerBLManager netServerBLManager = new NetServerBLManager();
        ViewNetServerLiveDetailsForm viewNetServerLiveDetailsForm = (ViewNetServerLiveDetailsForm) form;
        IRemoteCommunicationManager remoteCommunicationManager = null;
        try {
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            String strNetServerId = request.getParameter("netServerId");
            IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
            String netServerId=null;
    		if(strNetServerId != null){
    			netServerId = strNetServerId;
    		}
            if(netServerId != null){
                INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
                netServerInstanceData.setNetServerId(netServerId);
                netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
                List netServerTypeList = netServerBLManager.getNetServerTypeList();
                List netServerInstanceList = netServerBLManager.getNetServerInstanceList();

                request.setAttribute("netServerInstanceData",netServerInstanceData);
                request.setAttribute("netServerInstanceList",netServerInstanceList);			
                request.setAttribute("netServerTypeList",netServerTypeList);

                INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServerId);
                String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				
                remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
                remoteCommunicationManager.init(serverInstanceData.getAdminHost(),serverInstanceData.getAdminPort(),netServerCode,true);
                EliteNetServerDetails eliteServerDetails = (EliteNetServerDetails) remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"readServerDetails",null,null);
                //List lstNetServerAgentData = (ArrayList) remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","readAgentTasks",null,null);

                request.setAttribute("eliteLiveServerDetails",eliteServerDetails);
              //  request.setAttribute("lstNetServerAgentData",lstNetServerAgentData);
                viewNetServerLiveDetailsForm.setErrorCode("0");		
                doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
                return mapping.findForward(VIEW_FORWARD);
            }else{
                Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
                ActionMessage message = new ActionMessage("servermgr.server.livedetails");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
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
            return mapping.findForward(FAILURE_FORWARD);			
        }catch(CommunicationException sue){
            Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
            Logger.logTrace(MODULE,sue);
            viewNetServerLiveDetailsForm.setErrorCode("-1");
            EliteNetServerDetails eliteServerDetails = new EliteNetServerDetails();
            request.setAttribute("eliteLiveServerDetails",eliteServerDetails);
            return mapping.findForward(VIEW_FORWARD);			
        } catch (Exception e) {
            Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.livedetails");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);
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
    }
}