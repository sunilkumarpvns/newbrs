package com.elitecore.netvertexsm.web.servermgr.server;

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
import com.elitecore.corenetvertex.data.GatewayInfo;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ViewNetworkTreeForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class ViewNetworkTreeAction  extends BaseWebAction{
	private static final String VIEW_FORWARD = "viewNetworkTree";
	private static final String VIEW_FAILED ="viewNetworkTreeFailed";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "VIEW NETWORK TREE ACTION";
	private static final String VIEW_NETWORK_TREE_ACTION = ConfigConstant.VIEW_NETWORK_TREE_ACTION; 
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		 IRemoteCommunicationManager remoteCommunicationManager = null;
		  NetServerBLManager netServerBLManager = new NetServerBLManager();
			ViewNetworkTreeForm viewNetworkTreeForm = (ViewNetworkTreeForm) form; 
		try {
			
			checkActionPermission(request, VIEW_NETWORK_TREE_ACTION);
			
		
			
			String strNetServerId = request.getParameter("netServerId");
			Long netServerId=null;
    		if(strNetServerId != null){
    			netServerId = Long.parseLong(strNetServerId);
    		}
    		
    		 if(netServerId != null)
    		 {
    			 
    			 INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
                 netServerInstanceData.setNetServerId(netServerId);
                 netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
                 List netServerTypeList = netServerBLManager.getNetServerTypeList();
                 List netServerInstanceList = netServerBLManager.getNetServerInstanceList();

                 request.setAttribute("netServerInstanceData",netServerInstanceData);
                 request.setAttribute("netServerInstanceList",netServerInstanceList);			
                 request.setAttribute("netServerTypeList",netServerTypeList);
//    			 System.out.println("netserverid : " + netServerId);
    			 INetServerInstanceData serverInstanceData = netServerBLManager.getNetServerInstance(netServerId);
                 String netServerCode = PasswordEncryption.getInstance().crypt(serverInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				
                 remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
                 remoteCommunicationManager.init(serverInstanceData.getAdminHost(),serverInstanceData.getAdminPort(),netServerCode,true);
                 Object params[] = new Object[1];
                 params[0]="";
                 String sign[] = new String[1];
                 sign[0]="java.lang.String";
                 List<GatewayInfo> gatewayInfoList  = ( List<GatewayInfo>) remoteCommunicationManager.execute(MBeanConstants.GATEWAYINFORMATION,"gatewayInformation",params,sign);
                if(gatewayInfoList!= null)
                {
                	request.setAttribute("gatewayInfoList",gatewayInfoList);
                }
               
    		 }
    		 viewNetworkTreeForm.setNetServerId(netServerId);
    		 viewNetworkTreeForm.setErrorCode("0");
    		 return mapping.findForward(VIEW_FORWARD);
			
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
        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch(CommunicationException sue){
            Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
            Logger.logTrace(MODULE,sue);
            viewNetworkTreeForm.setErrorCode("-1");
            EliteNetServerDetails eliteServerDetails = new EliteNetServerDetails();
            request.setAttribute("eliteLiveServerDetails",eliteServerDetails);
            return mapping.findForward(VIEW_FAILED);			
        } 
		catch (Exception e) {
			Logger.logTrace(MODULE,e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
		}
		
        Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.viewserver.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}
}
