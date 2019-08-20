package com.elitecore.netvertexsm.web.servermgr.server;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.util.mbean.data.config.EliteNetServerData;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class SynchronizeBackNetServerConfigAction extends BaseWebAction {
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "NETSERVER SYNCHRONIZE BACK";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.SYNCHRONIZE_BACK_NET_SERVER_CONFIGURATION_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logInfo(MODULE,"Enter the execute method of :"+getClass().getName());
        MessageResources messageResources = getResources(request,"resultMessageResources");
        IRemoteCommunicationManager remoteCommunicationManager = null;
        try{
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            String strStaffId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();			
            Long staffId = Long.parseLong(strStaffId);
            
            String strNetServerId = request.getParameter("netserverid");
            Long netServerId=null;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
			}
            if(netServerId == null )
                throw new InvalidValueException("ServerId must be specified : currentvalue(null/blank)");

            INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);			
            INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServerId);
            String ipAddress = serverInstanceData.getAdminHost();
            int port = serverInstanceData.getAdminPort();	        

            String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);		        
            Logger.logDebug(MODULE,"NET SERVERCODE IS:"+netServerCode);
            remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
            remoteCommunicationManager.init(ipAddress,port,netServerCode,true);
            String strVersion = (String) remoteCommunicationManager.getAttribute(MBeanConstants.CONFIGURATION,"VersionInformation");

            if(strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())){
          //  if(true){
                Object[] objArgValues = {};
                String[] strArgTypes = {};

                EliteNetServerData eliteNetServerData = (EliteNetServerData)  remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"readServerConfiguration",objArgValues,strArgTypes);
//                for(int i=0;i<eliteNetServerData.getNetConfigurationList().size();i++)
//                {
//                	Logger.logTrace(MODULE, "EliteNetServerData ("+i+") "+eliteNetServerData.getNetConfigurationList().get(i).getClass());	
//                }
//                
                Logger.logTrace(MODULE, "serverId:"+netServerId+"\nstaffId:"+staffId+"\n");
                netServerBLManager.updateServerDetails(netServerId,eliteNetServerData,staffId,BaseConstant.SHOW_STATUS_ID);

                request.setAttribute("responseUrl","/updateNetServerSynchronizeConfigDetail.do?netserverid="+netServerId);
                ActionMessage message = new ActionMessage("servermgr.update.server.configuration.success");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveMessages(request,messages);
                return mapping.findForward(SUCCESS_FORWARD);
            }else{
                Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
                ActionMessage message = new ActionMessage("error.two.failed",messageResources.getMessage("error.label.server"),messageResources.getMessage("error.label.synchronization"));					
                ActionMessage message1 = new ActionMessage("servermgr.server.version.mismatch");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                messages.add("information",message1);
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
        }catch(InvalidValueException e){
            Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("error.two.failed",messageResources.getMessage("error.label.server"),messageResources.getMessage("error.label.synchronization"));                             
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
        } catch (UnidentifiedServerInstanceException commExp) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");			
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            messages.add("information",messageReason);
            saveErrors(request,messages);
        }
        catch(CommunicationException e){
            Logger.logError(MODULE,"Error during data Manager operation,reason :"+e.getMessage());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("error.two.failed",messageResources.getMessage("error.label.server"),messageResources.getMessage("error.label.synchronization"));
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);            
            saveErrors(request,messages);           
        }
        catch(ClassCastException cce){
            Logger.logError(MODULE,"Error during data Manager operation,reason :"+cce.getMessage());
            Logger.logTrace(MODULE,cce);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(cce);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("error.two.failed",messageResources.getMessage("error.label.server"),messageResources.getMessage("error.label.synchronization"));
            ActionMessage message1 = new ActionMessage("servermgr.server.synchronized.invalid.object");
            ActionMessages messages = new ActionMessages();
            messages.add("warn",message);
            messages.add("warn",message1);			
            saveErrors(request,messages);
        }catch(Exception managerExp){
            Logger.logError(MODULE,"Error during data Manager operation,reason :"+managerExp.getMessage());
            Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("error.two.failed",messageResources.getMessage("error.label.server"),messageResources.getMessage("error.label.synchronization"));			
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);		
            saveErrors(request,messages);		
        }
        finally
        {
            try{
                if(remoteCommunicationManager != null)
                    remoteCommunicationManager.close();  
            }
            catch (Throwable e) {
                remoteCommunicationManager = null;
            }
        }
        Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
        return mapping.findForward(FAILURE_FORWARD);
    }
}

