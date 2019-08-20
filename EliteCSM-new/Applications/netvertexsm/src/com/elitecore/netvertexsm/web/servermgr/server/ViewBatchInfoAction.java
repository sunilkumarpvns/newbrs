package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ViewBatchInfoForm;
import com.elitecore.passwordutil.PasswordEncryption;


public class ViewBatchInfoAction extends BaseWebAction{
    private static final String MODULE          = "ViewBatchInfoAction";
    private static final String VIEW_FORWARD    = "viewBatchInfo";
    private static final String FAILURE_FORWARD = "failure";
    
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        ActionMessages messages = new ActionMessages();
        ViewBatchInfoForm viewBatchInfoForm = (ViewBatchInfoForm)form;
        
        String batchId = request.getParameter("batchId");
        
        String strNetServerId = request.getParameter("netserverid");
  
		Long netServerId=null;
		if(strNetServerId == null){
			netServerId = viewBatchInfoForm.getServerId();
		}else{
			netServerId = Long.parseLong(strNetServerId);
		}
        viewBatchInfoForm.setServerId(netServerId);
        
        try {
                NetServerBLManager netServerBLManager = new NetServerBLManager();
                INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
                
                // Call To Mbean Server
                IRemoteCommunicationManager remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
                String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
                remoteCommunicationManager.init(netServerInstanceData.getAdminHost(),netServerInstanceData.getAdminPort(),netServerCode,true);
                String strVersion = (String) remoteCommunicationManager.getAttribute("Elitecore:type=EliteAdmin","VersionInformation");
                
                if(strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())){
                    Object[] objArgValues = {batchId};
                    String[] strArgTypes = {"java.lang.String"};
                    HashMap batchInfoMap = (HashMap) remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","detailsOfBatchInfo",objArgValues,strArgTypes);
                    request.setAttribute("batchInfoMap", batchInfoMap);
                }    

            } catch (Exception e) {
                Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
                Logger.logTrace(MODULE, e);
                ActionMessage message = new ActionMessage("general.error");
                messages.add("information", message);
                saveErrors(request, messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
        return mapping.findForward(VIEW_FORWARD);  
    }

}
