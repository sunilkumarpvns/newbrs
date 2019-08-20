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
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.UpdateNetServerInstanceBasicDetailForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class UpdateNetServerInstanceBasicDetailAction extends BaseWebAction {
    private static final String FAILURE_FORWARD = "failure";
    private static final String SUCCESS_FORWARD = "success";
    private static final String UPDATE_FORWARD  = "updateNetSeverInstanceBasicDetail";
    private static final String VIEW_FORWARD = "viewNetServerInstance";
    private static final String MODULE = "UPDATE BASIC DETAIL ACTION";
    private static final String SUBMODULEACTIONALIAS = "UPDATE_NET_SERVER_INSTANCE_BASIC_DETAIL_ACTION";


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());

        IRemoteCommunicationManager remoteCommunicationManager = null;
        NetServerBLManager netServerBLManager = new NetServerBLManager();
        String strNetServerId = request.getParameter("netserverid");

        try {
            checkActionPermission(request, SUBMODULEACTIONALIAS);
    		Long netServerId=null;
			if (strNetServerId != null) {
				netServerId = Long.parseLong(strNetServerId);
			}
            UpdateNetServerInstanceBasicDetailForm updateNetServerInstanceBasicDetailForm = (UpdateNetServerInstanceBasicDetailForm)form;
            List lstNetServerType = netServerBLManager.getNetServerTypeList();

            if(updateNetServerInstanceBasicDetailForm.getAction() == null){
                if(netServerId != null){
                    INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
                    netServerInstanceData.setNetServerId(netServerId);
                    netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
                    List netServerTypeList = netServerBLManager.getNetServerTypeList();

                    updateNetServerInstanceBasicDetailForm = convertBeanToForm(netServerInstanceData);

                    request.setAttribute("netServerInstanceData",netServerInstanceData);
                    request.setAttribute("lstNetServerType",lstNetServerType);
                    request.setAttribute("updateNetServerInstanceBasicDetailForm",updateNetServerInstanceBasicDetailForm);
                    request.setAttribute("netServerTypeList",netServerTypeList);
                }
                return mapping.findForward(UPDATE_FORWARD);
            }else if(updateNetServerInstanceBasicDetailForm.getAction().equalsIgnoreCase("update")){
                
            	INetServerInstanceData inetServerInstanceData = netServerBLManager.getNetServerInstance(updateNetServerInstanceBasicDetailForm.getNetServerId());
                
            	if (inetServerInstanceData.getName().equals(updateNetServerInstanceBasicDetailForm.getName()) == false) {
	            	int port = inetServerInstanceData.getAdminPort();
	                String ipAddress = inetServerInstanceData.getAdminHost();
	                String netServerCode = PasswordEncryption.getInstance().crypt(inetServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
	                String netServerName = PasswordEncryption.getInstance().crypt(updateNetServerInstanceBasicDetailForm.getName(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
		            
		            Object[] argValues = {netServerCode,netServerName};
		            String[] argTypes = {"java.lang.String","java.lang.String"};
		            
		            remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
	                remoteCommunicationManager.init(ipAddress,port,netServerCode,true);
		            remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"writeServerInstanceDetails",argValues,argTypes);			
		            remoteCommunicationManager.close();
            	}
	            
	            inetServerInstanceData = convertFormToBean(updateNetServerInstanceBasicDetailForm);
	            netServerBLManager.updateBasicDetail(inetServerInstanceData);

                request.setAttribute("lstNetServerType",lstNetServerType);

                request.setAttribute("responseUrl","/viewNetServerInstance.do?netserverid="+inetServerInstanceData.getNetServerId());
                ActionMessage message = new ActionMessage("servermgr.server.updatebasicdetail.success");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveMessages(request,messages);
                return mapping.findForward(SUCCESS_FORWARD);
            }
        }catch(CommunicationException ce){
            Logger.logTrace(MODULE,"Returning error forward from "+getClass().getName());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ce);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.communication.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.create.liveserver.failure.reason");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            messages.add("information",messageReason);
            saveErrors(request,messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("error.heading.create.serverinstance.failure");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);      
            
            return mapping.findForward(FAILURE_FORWARD);
        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("servermgr.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);          
            
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }
        catch (Exception exp) {
            Logger.logError(MODULE,"Returning error forward from "+ getClass().getName() + "Reason:-" +exp.getMessage());
            Logger.logTrace(MODULE,exp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
        }
        ActionMessages messages = new ActionMessages();
        messages.add("information",new ActionMessage("servermgr.update.failure"));
        saveErrors(request,messages);
        
        ActionMessages errorHeadingMessage = new ActionMessages();
        ActionMessage message = new ActionMessage("servermgr.error.heading","updating");
        errorHeadingMessage.add("errorHeading",message);
        saveMessages(request,errorHeadingMessage);          
        return mapping.findForward(FAILURE_FORWARD);
    }

    private UpdateNetServerInstanceBasicDetailForm convertBeanToForm(INetServerInstanceData netServerInstanceData){
        UpdateNetServerInstanceBasicDetailForm updateNetServerInstanceBasicDetailForm = null;
        if(netServerInstanceData != null){
            updateNetServerInstanceBasicDetailForm = new UpdateNetServerInstanceBasicDetailForm();
            updateNetServerInstanceBasicDetailForm.setNetServerId(netServerInstanceData.getNetServerId());
            updateNetServerInstanceBasicDetailForm.setName(netServerInstanceData.getName());
            updateNetServerInstanceBasicDetailForm.setDescription(netServerInstanceData.getDescription());
            updateNetServerInstanceBasicDetailForm.setNetServerType(netServerInstanceData.getNetServerTypeId());
            updateNetServerInstanceBasicDetailForm.setServerHome(netServerInstanceData.getServerHome());
            updateNetServerInstanceBasicDetailForm.setJavaHome(netServerInstanceData.getJavaHome());
        }
        return updateNetServerInstanceBasicDetailForm;
    }

    private INetServerInstanceData convertFormToBean(UpdateNetServerInstanceBasicDetailForm updateNetServerInstanceBasicDetailForm){
        INetServerInstanceData netServerInstanceData = null;
        if(updateNetServerInstanceBasicDetailForm != null){
            netServerInstanceData = new NetServerInstanceData();
            netServerInstanceData.setNetServerId(updateNetServerInstanceBasicDetailForm.getNetServerId());
            netServerInstanceData.setName(updateNetServerInstanceBasicDetailForm.getName());
            netServerInstanceData.setDescription(updateNetServerInstanceBasicDetailForm.getDescription());
            netServerInstanceData.setNetServerTypeId(updateNetServerInstanceBasicDetailForm.getNetServerType());
            netServerInstanceData.setJavaHome(updateNetServerInstanceBasicDetailForm.getJavaHome());
            netServerInstanceData.setServerHome(updateNetServerInstanceBasicDetailForm.getServerHome());
        }
        return netServerInstanceData;
    }
}
