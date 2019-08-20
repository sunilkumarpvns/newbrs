package com.elitecore.elitesm.web.servermgr.server;

import java.util.HashMap;
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
import com.elitecore.elitesm.web.servermgr.server.forms.ViewCDRInfoForm;
import com.elitecore.passwordutil.PasswordEncryption;


public class ViewCDRInfoAction extends BaseWebAction {
    private static final String MODULE          = "ViewCDRInfoAction";
    private static final String VIEW_FORWARD    = "viewCDRInfo";
    private static final String FAILURE_FORWARD = "failure";
    
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        
        ActionMessages messages = new ActionMessages();
        ViewCDRInfoForm viewCDRInfoForm = (ViewCDRInfoForm) form;
        
        String cdrId = request.getParameter("cdrId");
        String strNetServerId = request.getParameter("netserverid");

        String netServerId=null;
		if(strNetServerId == null){
			netServerId = viewCDRInfoForm.getServerId();
		}else{
			netServerId = strNetServerId;
		}
        viewCDRInfoForm.setServerId(netServerId);
        
        try {
                NetServerBLManager netServerBLManager = new NetServerBLManager();
                INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
                
                // Call To Mbean Server
                IRemoteCommunicationManager remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
                String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
                remoteCommunicationManager.init(netServerInstanceData.getAdminHost(),netServerInstanceData.getAdminPort(),netServerCode,true);
                String strVersion = (String) remoteCommunicationManager.getAttribute("Elitecore:type=EliteAdmin","VersionInformation");
                
                if(strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())){
                    Object[] objArgValues = {cdrId};
                    String[] strArgTypes = {"java.lang.String"};
                    HashMap cdrInfoMap = (HashMap) remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","detailsOfCDRInfo",objArgValues,strArgTypes);
                    request.setAttribute("cdrInfoMap", cdrInfoMap);
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
