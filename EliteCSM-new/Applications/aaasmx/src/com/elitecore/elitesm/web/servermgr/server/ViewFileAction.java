package com.elitecore.elitesm.web.servermgr.server;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.util.constants.MBeanNameConstant;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servermgr.server.forms.ViewFileForm;
import com.elitecore.passwordutil.PasswordEncryption;


public class ViewFileAction extends BaseWebAction{
    private static final String MODULE = "ViewFileAction";
    private static final String VIEW_FORWARD = "viewFile";
    private static final String FAILURE_FORWARD = "failure";
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception{
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        ViewFileForm viewFileForm = (ViewFileForm)form;
        
        ActionMessages messages = new ActionMessages();
        NetServerBLManager netServerBLManager = new NetServerBLManager();

        try {
            
            String strNetServerId = (String)request.getSession().getAttribute("netserverid");
            String netServerId=null;
    		if(strNetServerId != null){
    			netServerId = strNetServerId;
    		}
            String fileNameToBePassed = request.getParameter("fileName") ;
                
            INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
            List netServerTypeList = netServerBLManager.getNetServerTypeList();

            request.setAttribute("netServerInstanceData",netServerInstanceData);
            request.setAttribute("netServerTypeList",netServerTypeList);
        
            // Call To Mbean Server
            IRemoteCommunicationManager remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
            String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
            remoteCommunicationManager.init(netServerInstanceData.getAdminHost(),netServerInstanceData.getAdminPort(),netServerCode,true);
            String strVersion = (String) remoteCommunicationManager.getAttribute("Elitecore:type=EliteAdmin","VersionInformation");
            
            if(strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())){
                Object[] objArgValues = {fileNameToBePassed};
                String[] strArgTypes = {"java.lang.String"};
                HashMap fileInfoMap = (HashMap) remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","fileInfoMap",objArgValues,strArgTypes);
                
                if(fileInfoMap != null && fileInfoMap.size() > 0) {
                    String fileId =         (String)fileInfoMap.get(MBeanNameConstant.FILE_ID);
                    String fileName =       (String)fileInfoMap.get(MBeanNameConstant.FILE_NAME);      
                    String deviceId =       (String)fileInfoMap.get(MBeanNameConstant.DEVICE_ID);      
                    String dateAndTime =    ((Date)fileInfoMap.get(MBeanNameConstant.DATE_AND_TIME)).toString();     
                    String state =          (String)fileInfoMap.get(MBeanNameConstant.STATE);
                    String reason =         (String)fileInfoMap.get(MBeanNameConstant.REASON);
                    String location =       (String)fileInfoMap.get(MBeanNameConstant.LOCATION);
                    
                    viewFileForm.setFileId(fileId);
                    viewFileForm.setFileName(fileName);
                    viewFileForm.setDeviceId(deviceId);
                    viewFileForm.setDateAndTime(dateAndTime);
                    viewFileForm.setStatus(state);
                    viewFileForm.setReason(reason);
                    viewFileForm.setLocation(location);
                }    
                
            }   
            
        }  catch(Exception e) {
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
