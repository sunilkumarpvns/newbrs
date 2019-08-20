package com.elitecore.netvertexsm.web.servermgr.server;

import java.text.SimpleDateFormat;
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

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.MBeanNameConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.servermgr.server.form.SearchFileForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class SearchFileAction extends BaseWebAction{
    private static final String MODULE = "SearchFileAction";
    private static final String VIEW_FORWARD = "searchFile";
    private static final String FAILURE_FORWARD = "failure";
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception{
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        SearchFileForm searchFileForm = (SearchFileForm)form;
        
        String dateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        
        ActionMessages messages = new ActionMessages();
        NetServerBLManager netServerBLManager = new NetServerBLManager();

        List fileList = null;
        try {
            
            String strNetServerId = (String)request.getSession().getAttribute("netserverid");
            
			Long netServerId=null;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
			}
             
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

                Date startDate = null;
                if(searchFileForm.getDateFrom() != null && !searchFileForm.getDateFrom().equalsIgnoreCase("")) {
                    startDate = sdf.parse(searchFileForm.getDateFrom());
                }
                
                Date endDate = null;
                if(searchFileForm.getDateTo() != null && !searchFileForm.getDateTo().equalsIgnoreCase("")) {
                    endDate = sdf.parse(searchFileForm.getDateTo());
                }
                
                HashMap searchFileParamMap = new HashMap();
                searchFileParamMap.put(MBeanNameConstant.FIELD_NAME, searchFileForm.getFiledName());
                searchFileParamMap.put(MBeanNameConstant.DEVICE_ID, searchFileForm.getDeviceId());
                searchFileParamMap.put(MBeanNameConstant.START_DATE, startDate);
                searchFileParamMap.put(MBeanNameConstant.END_DATE, endDate);
                if(searchFileForm.getStatus().equalsIgnoreCase("All"))
                    searchFileParamMap.put(MBeanNameConstant.STATE, null);
                else
                    searchFileParamMap.put(MBeanNameConstant.STATE, searchFileForm.getStatus());
                searchFileParamMap.put(MBeanNameConstant.REASON, searchFileForm.getReason());
                searchFileParamMap.put(MBeanNameConstant.LOCATION, searchFileForm.getLocation());
                
                Object[] objArgValues = {searchFileParamMap};
                String[] strArgTypes = {"java.util.HashMap"};
                fileList = (List) remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","searchFileList",objArgValues,strArgTypes);
                
            }   
            request.setAttribute("fileList", fileList);
            
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
