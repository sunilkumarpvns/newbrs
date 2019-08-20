package com.elitecore.elitesm.web.servermgr.server;

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

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.MBeanNameConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.servermgr.server.forms.SearchBatchForm;
import com.elitecore.elitesm.web.servermgr.server.forms.SearchFileForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class SearchBatchAction extends BaseWebAction{
    private static final String MODULE = "SearchBatchAction";
    private static final String VIEW_FORWARD = "searchBatch";
    private static final String FAILURE_FORWARD = "failure";
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception{
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        SearchBatchForm searchBatchForm = (SearchBatchForm)form;
        
        String dateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        
        ActionMessages messages = new ActionMessages();
        NetServerBLManager netServerBLManager = new NetServerBLManager();

        List batchList = null;
        try {
            
        	String serverId = searchBatchForm.getServerId();
            INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(serverId);
            
            List netServerTypeList = netServerBLManager.getNetServerTypeList();

            request.setAttribute("netServerInstanceData",netServerInstanceData);
            request.setAttribute("netServerTypeList",netServerTypeList);
        
            
            if (searchBatchForm.getCheckAction().equalsIgnoreCase("search") || searchBatchForm.getCheckAction().equalsIgnoreCase("checkInBatch") || searchBatchForm.getCheckAction().equalsIgnoreCase("undoCheckedOut") || searchBatchForm.getCheckAction().equalsIgnoreCase("updateBatch")) {
                // Call To Mbean Server
                try {
                    IRemoteCommunicationManager remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
                    String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
                    
                    remoteCommunicationManager.init(netServerInstanceData.getAdminHost(),netServerInstanceData.getAdminPort(),netServerCode,true);
                    String strVersion = (String) remoteCommunicationManager.getAttribute("Elitecore:type=EliteAdmin","VersionInformation");
                    
                    if(strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())){
                        
                        if (searchBatchForm.getCheckAction().equalsIgnoreCase("search")) {
        
                            Date startDate = null;
                            if(searchBatchForm.getDateFrom() != null && !searchBatchForm.getDateFrom().equalsIgnoreCase("")) {
                                startDate = sdf.parse(searchBatchForm.getDateFrom());
                            }
                            
                            Date endDate = null;
                            if(searchBatchForm.getDateTo() != null && !searchBatchForm.getDateTo().equalsIgnoreCase("")) {
                                endDate = sdf.parse(searchBatchForm.getDateTo());
                            }
                            
                            HashMap searchBatchParamMap = new HashMap();
                            searchBatchParamMap.put(MBeanNameConstant.BATCH_ID, searchBatchForm.getBatchId());
                            searchBatchParamMap.put(MBeanNameConstant.START_DATE, startDate);
                            searchBatchParamMap.put(MBeanNameConstant.END_DATE, endDate);
                            
                            if(searchBatchForm.getStatus().equalsIgnoreCase("All"))
                                searchBatchParamMap.put(MBeanNameConstant.STATUS, null);
                            else
                                searchBatchParamMap.put(MBeanNameConstant.STATUS, searchBatchForm.getStatus());
                            
                            searchBatchParamMap.put(MBeanNameConstant.REASON, searchBatchForm.getReason());
                            searchBatchParamMap.put(MBeanNameConstant.USER_NAME, searchBatchForm.getUserName());
                            
                            Object[] objArgValues = {searchBatchParamMap};
                            String[] strArgTypes = {"java.util.HashMap"};
                            batchList = (List) remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","searchBatchList",objArgValues,strArgTypes);
                            request.getSession().setAttribute("batchList", batchList);
                        }
                        
                        if (searchBatchForm.getCheckAction().equalsIgnoreCase("checkInBatch")) {
                            String[] strSelectedIds = request.getParameterValues("select");
                            
                            Object[] objArgValues = {strSelectedIds};
                            String[] strArgTypes = {String[].class.getName()};
                            remoteCommunicationManager.execute("Elitecore:type=EliteAdmin", "checkInBatchSelectedRecords", objArgValues, strArgTypes);
                        }
                        
                        if (searchBatchForm.getCheckAction().equalsIgnoreCase("undoCheckedOut")) {
                            String[] strSelectedIds = request.getParameterValues("select");
                            
                            Object[] objArgValues = {strSelectedIds};
                            String[] strArgTypes = {String[].class.getName()};
                            remoteCommunicationManager.execute("Elitecore:type=EliteAdmin", "undoCheckedOutSelectedRecords", objArgValues, strArgTypes);
                        }
                        
                        if (searchBatchForm.getCheckAction().equalsIgnoreCase("updateBatch")) {
                            String[] strSelectedIds = request.getParameterValues("select");
                            
                            Object[] objArgValues = {strSelectedIds};
                            String[] strArgTypes = {String[].class.getName()};
                            remoteCommunicationManager.execute("Elitecore:type=EliteAdmin", "updateBatchSelectedRecords", objArgValues, strArgTypes);
                        }
                    }   
                    
                } catch(Exception e) {
                    Logger.logError(MODULE, "Error in server side , Reason : " + e.getMessage());
                    Logger.logTrace(MODULE, e);
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
