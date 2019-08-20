package com.elitecore.elitesm.web.servermgr.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servermgr.server.forms.UpdateBatchWithFileForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class UpdateBatchWithFileAction extends BaseWebAction {
    
    private static final String MODULE          = "UpdateBatchWithFileAction";
    private static String VIEW_FORWARD          = "updateBatchWithFile";
    private static final String FAILURE_FORWARD = "failure";
    private static final String SUCCESS_FORWARD = "success";
    
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        ActionMessages messages = new ActionMessages();
        
        UpdateBatchWithFileForm updateBatchWithFileForm = (UpdateBatchWithFileForm) form;
        NetServerBLManager netServerBLManager = new NetServerBLManager();
        
        try {
            String strNetServerId = request.getParameter("netserverid");
            
            String netServerId=null;
			if(strNetServerId != null){
				netServerId = strNetServerId;
			}else{
            
				netServerId = updateBatchWithFileForm.getServerId();
			}
            
            updateBatchWithFileForm.setServerId(netServerId);
            
            INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
            List netServerTypeList = netServerBLManager.getNetServerTypeList();
            
            request.setAttribute("netServerInstanceData", netServerInstanceData);
            request.setAttribute("netServerTypeList", netServerTypeList);
            
            if (updateBatchWithFileForm.getCheckAction() != null && updateBatchWithFileForm.getCheckAction().equalsIgnoreCase("UploadFile")) {
                try {
                    IRemoteCommunicationManager remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
                    String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(), ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
                    
                    remoteCommunicationManager.init(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(), netServerCode, true);
                    String strVersion = (String) remoteCommunicationManager.getAttribute("Elitecore:type=EliteAdmin", "VersionInformation");
                    
                    if (strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())) {
                        Object[] objArgValues = { updateBatchWithFileForm.getBatchId(), updateBatchWithFileForm.getBatchFile().getFileData()};
                        String[] strArgTypes = { "java.lang.String",byte[].class.getName()};
                        remoteCommunicationManager.execute("Elitecore:type=EliteAdmin", "uploadBatchWithFile", objArgValues, strArgTypes);
                        
                        updateBatchWithFileForm.setBatchId("");
                        updateBatchWithFileForm.setCheckAction("");
                        
                        request.setAttribute("responseUrl", "/updateBatchWithFile");
                        ActionMessage message = new ActionMessage("servermgr.server.mediation.updatebatch.uploadfile.success");
                        messages.add("information", message);
                        saveMessages(request, messages);
                        return mapping.findForward(SUCCESS_FORWARD);
                    }
                    
                }
                catch (Exception e) {
                    Logger.logError(MODULE, "Error occures in server side, Reason :" + e.getMessage());
                }
            }
        }
        catch (Exception e) {
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
