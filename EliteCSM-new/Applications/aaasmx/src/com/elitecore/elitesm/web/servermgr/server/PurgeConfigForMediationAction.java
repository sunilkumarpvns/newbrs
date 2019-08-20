package com.elitecore.elitesm.web.servermgr.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.servermgr.server.forms.PurgeConfigForMediationForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class PurgeConfigForMediationAction extends BaseWebAction{
    private static final String MODULE          = "PurgeConfigForMediationAction";
    private static final String VIEW_FORWARD    = "purgeConfigForMediation";
    private static final String FAILURE_FORWARD = "failure";
    private static final String SUCCESS_FORWARD = "success";
    
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        ActionMessages messages = new ActionMessages();
        
        String dateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        PurgeConfigForMediationForm purgeConfigForMediationForm = (PurgeConfigForMediationForm) form;
        NetServerBLManager netServerBLManager = new NetServerBLManager();
        
        try {
            String strNetServerId = request.getParameter("netserverid");
            
            String netServerId=null;
			if(strNetServerId != null){
				netServerId = strNetServerId;
			}else{
				netServerId =purgeConfigForMediationForm.getServerId();
			}

            
            purgeConfigForMediationForm.setServerId(netServerId);
            
            INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
            List netServerTypeList = netServerBLManager.getNetServerTypeList();
            
            request.setAttribute("netServerInstanceData", netServerInstanceData);
            request.setAttribute("netServerTypeList", netServerTypeList);
            
            if (purgeConfigForMediationForm.getCheckAction() != null && !purgeConfigForMediationForm.getCheckAction().equalsIgnoreCase("")) {
                try {
                    IRemoteCommunicationManager remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
                    String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(), ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
                    
                    remoteCommunicationManager.init(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(), netServerCode, true);
                    String strVersion = (String) remoteCommunicationManager.getAttribute("Elitecore:type=EliteAdmin", "VersionInformation");
                    
                    if (strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())) {
                            Calendar purgeCal = Calendar.getInstance();
                            
                            Date purgeDate = sdf.parse(purgeConfigForMediationForm.getPurgeDate());
                            purgeCal.setTime(purgeDate);
                            purgeCal.set(Calendar.HOUR_OF_DAY, purgeConfigForMediationForm.getPurgeHour());
                            purgeCal.set(Calendar.MINUTE, purgeConfigForMediationForm.getPurgeMinute());

                            purgeDate = purgeCal.getTime();
                            
                            if (purgeConfigForMediationForm.getCheckAction().equalsIgnoreCase("purgeNow")) {
                                Object[] objArgValues = {purgeDate};
                                String[] strArgTypes = {"java.util.Date"};
                                remoteCommunicationManager.execute("Elitecore:type=EliteAdmin", "purgeNow", objArgValues, strArgTypes);
                            }
                    }
                    request.setAttribute("responseUrl", "/purgeConfigForMediation?netserverid="+netServerId);
                    ActionMessage message = new ActionMessage("servermgr.server.mediation.purge.config.success");
                    messages.add("information", message);
                    saveMessages(request, messages);
                    return mapping.findForward(SUCCESS_FORWARD);
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
