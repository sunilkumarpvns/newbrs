/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SynchronizeNetServerInstanceVersionAction.java                            
 * ModualName com.elitecore.elitesm.web.servermgr.server                                      
 * Created on Mar 5, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.web.servermgr.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.AuthorizationException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.OperationFailedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.VersionNotSupportedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.server.forms.SynchronizeNetServerInstanceVersionForm;
import com.elitecore.passwordutil.PasswordEncryption;


public class SynchronizeNetServerInstanceVersionAction extends BaseWebAction {
    
    private static final String  ENTITY_NAME = "SynchronizeNetServerInstanceVersionAction";
    private static final String FAILURE_FORWARD   = "failure";
    private static final String SUCCESS_FORWARD   = "success";
    private static final String VIEW_FORWARD   = "synchronizeServerInstanceVersion";
    private static final String ACTION_ALIAS = ConfigConstant.SYNCHRONIZE_SERVER_VERSION_ACTION;
    
    
    
    
    @Override
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        IRemoteCommunicationManager  remoteCommunicationManager = null;
        
        List netServerTypeList = null;
        
        
        try {
        	checkActionPermission(request, ACTION_ALIAS);
            Logger.logDebug(ENTITY_NAME,"Enter into execute() of "+ SynchronizeNetServerInstanceVersionAction.class.getSimpleName());
            IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
            SynchronizeNetServerInstanceVersionForm syncServerInstanceVersionForm = (SynchronizeNetServerInstanceVersionForm) form;
            
            String strNetServerId = request.getParameter("netServerInstanceId");
            String action = request.getParameter("action");
            String netServerId=null;
			if(strNetServerId != null){
				netServerId = strNetServerId;
			}
            if(netServerId == null)
            	netServerId = syncServerInstanceVersionForm.getNetServerInstanceId();
            
            if(action == null)
                action = syncServerInstanceVersionForm.getAction();
             
            NetServerBLManager netSeverBLManager = new NetServerBLManager();
            INetServerInstanceData serverInstanceData = netSeverBLManager.getNetServerInstance(netServerId);
            syncServerInstanceVersionForm.setNetServerInstanceId(serverInstanceData.getNetServerId());
            netServerTypeList = netSeverBLManager.getNetServerTypeList();
            
            
            remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
            String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
            try {
                remoteCommunicationManager.init(serverInstanceData.getAdminHost(),serverInstanceData.getAdminPort(),netServerCode,true);
                syncServerInstanceVersionForm.setNetServerStatus(true);
            }
            catch(UnidentifiedServerInstanceException e){
                Logger.logDebug(MODULE,"Unidentified Server Found.. No action taken");
                syncServerInstanceVersionForm.setNetServerStatus(true);
            }
            catch (CommunicationException e) {
                Logger.logDebug(MODULE, "Server Status : Stoped." );
                syncServerInstanceVersionForm.setNetServerStatus(false);
            }
            
            
            if(action == null || action.equals("init")) {
                
                if(syncServerInstanceVersionForm.getNetServerStatus()) {
                    String strVersion = (String) remoteCommunicationManager.getAttribute(MBeanConstants.CONFIGURATION,"VersionInformation");
                    syncServerInstanceVersionForm.setLiveServerVersion(strVersion);
                    
                    if(serverInstanceData.getVersion().equals(strVersion))
                        syncServerInstanceVersionForm.setSyncServerVesionStatus(true);
                        syncServerInstanceVersionForm.setUpgradeServerVersion(strVersion);
                }
               
                request.setAttribute("netServerInstanceData", serverInstanceData);
                request.setAttribute("netServerId", netServerId);
                request.setAttribute("netServerTypeList", netServerTypeList);
                Logger.logDebug(MODULE, "Server Status : Stoped." );
                Logger.logDebug(MODULE, "Forwarding to path :- " + VIEW_FORWARD );
                return mapping.findForward(VIEW_FORWARD);
                
            }else if(action.equals("upgrade")){
                netSeverBLManager.upgradeNetServer(syncServerInstanceVersionForm.getNetServerInstanceId(), syncServerInstanceVersionForm.getUpgradeServerVersion());
                doAuditing(staffData, ACTION_ALIAS);
                request.setAttribute("responseUrl","/synchronizeServerInstanceVersion.do?netServerInstanceId="+syncServerInstanceVersionForm.getNetServerInstanceId());
                ActionMessage message = new ActionMessage("servermgr.serverversion.synchronize.configuration.success");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveMessages(request,messages);
                return mapping.findForward(SUCCESS_FORWARD);
           }
             return null;
        }
        catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
        catch(VersionNotSupportedException e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + e.getMessage());
            Logger.logTrace(MODULE, e);
            request.setAttribute("responseUrl", "/listNetServerInstance");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.version.notsupported");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
         }
        catch (AuthorizationException authExp) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());
            Logger.logTrace(MODULE, authExp);
            request.setAttribute("responseUrl", "/listNetServerInstance");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.serverversion.synchronize.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        
        catch (CommunicationException connException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + connException.getMessage());
            Logger.logTrace(MODULE, connException);
            request.setAttribute("responseUrl", "/listNetServerInstance");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(connException);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.serverversion.synchronize.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (OperationFailedException opException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + opException.getMessage());
            Logger.logTrace(MODULE, opException);
            request.setAttribute("responseUrl", "/listNetServerInstance");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(opException);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.serverversion.synchronize.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (ConstraintViolationException conException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason :" + conException.getMessage());
            Logger.logTrace(MODULE, conException);
            request.setAttribute("responseUrl", "/listNetServerInstance");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(conException);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.serverversion.synchronize.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        catch (DataManagerException mgrException) {
            Logger.logError(MODULE, "Error during Data Manager (DataMgr) operation , reason :" + mgrException.getMessage());
            Logger.logTrace(MODULE, mgrException);
            request.setAttribute("responseUrl", "/listNetServerInstance");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(mgrException);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.serverversion.synchronize.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
            
        }
        catch (Exception exp) {
            Logger.logError(MODULE, "Error during Data Manager (DataMgr) operation , reason :" + exp.getMessage());
            Logger.logTrace(MODULE, exp);
            request.setAttribute("responseUrl", "/listNetServerInstance");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.serverversion.synchronize.configuration.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
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