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

import com.elitecore.elitesm.util.constants.MBeanNameConstant;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.servermgr.server.forms.SearchCDRForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class SearchCDRAction extends BaseWebAction {
    
    private static final String MODULE          = "SearchCDRAction";
    private static final String VIEW_FORWARD    = "searchCDR";
    private static final String FAILURE_FORWARD = "failure";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.SEARCH_CDR_FILE_ACTION;
    
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        
        ActionMessages messages = new ActionMessages();
        SearchCDRForm searchCDRForm = (SearchCDRForm) form;
        
        String dateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        
        String serverId = searchCDRForm.getServerId();
        NetServerBLManager netServerBLManager = new NetServerBLManager();
        
        try {
        	checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(serverId);
            List netServerTypeList = netServerBLManager.getNetServerTypeList();
            
            request.setAttribute("netServerInstanceData", netServerInstanceData);
            request.setAttribute("netServerTypeList", netServerTypeList);
            
            if (searchCDRForm.getCheckAction().equalsIgnoreCase("Add")) {
                HashMap fieldMap = new HashMap();
                fieldMap.put(MBeanNameConstant.FIELD_NAME, searchCDRForm.getFieldName());
                fieldMap.put(MBeanNameConstant.OPERATOR, searchCDRForm.getOperator());
                fieldMap.put(MBeanNameConstant.FIELD_VALUE, searchCDRForm.getValue());
                fieldMap.put(MBeanNameConstant.LOGICAL_CONNECTOR, searchCDRForm.getLogicalConnector());
                ((List) request.getSession().getAttribute("fieldList")).add(fieldMap);
                searchCDRForm.setValue("");
            }
            
            if (searchCDRForm.getCheckAction().equalsIgnoreCase("Remove")) {
                int index = searchCDRForm.getItemIndex();
                ((List) request.getSession().getAttribute("fieldList")).remove(index);
            }
            
            if (searchCDRForm.getCheckAction().equalsIgnoreCase("Save")) {
                System.out.println("Inside Save" + searchCDRForm.getSearchName());
            }
            
            if (searchCDRForm.getCheckAction().equalsIgnoreCase("Search") || searchCDRForm.getCheckAction().equalsIgnoreCase("markForReprocess") || searchCDRForm.getCheckAction().equalsIgnoreCase("update") || searchCDRForm.getCheckAction().equalsIgnoreCase("checkOutSelected") || searchCDRForm.getCheckAction().equalsIgnoreCase("checkedOutAll")) {
                try {
                    IRemoteCommunicationManager remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
                    String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(), ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
                    
                    remoteCommunicationManager.init(netServerInstanceData.getAdminHost(), netServerInstanceData.getAdminPort(), netServerCode, true);
                    String strVersion = (String) remoteCommunicationManager.getAttribute("Elitecore:type=EliteAdmin", "VersionInformation");
                    
                    if (strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())) {
                        if (searchCDRForm.getCheckAction().equalsIgnoreCase("Search")) {
                            
                            Date startDate = null;
                            if (searchCDRForm.getDateFrom() != null && !searchCDRForm.getDateFrom().equalsIgnoreCase("")) {
                                startDate = sdf.parse(searchCDRForm.getDateFrom());
                            }
                            
                            Date endDate = null;
                            if (searchCDRForm.getDateTo() != null && !searchCDRForm.getDateTo().equalsIgnoreCase("")) {
                                endDate = sdf.parse(searchCDRForm.getDateTo());
                            }
                            
                            HashMap searchCDRParamMap = new HashMap();
                            searchCDRParamMap.put(MBeanNameConstant.SEARCH_CRITERIA, searchCDRForm.getSearchCriteria());
                            searchCDRParamMap.put(MBeanNameConstant.REASON, searchCDRForm.getReason());
                            searchCDRParamMap.put(MBeanNameConstant.DEVICE_ID, searchCDRForm.getDeviceId());
                            searchCDRParamMap.put(MBeanNameConstant.FILE_NAME, searchCDRForm.getFileName());
                            searchCDRParamMap.put(MBeanNameConstant.STATE, searchCDRForm.getStatus());
                            searchCDRParamMap.put(MBeanNameConstant.DISTRIBUTION_STATUS, searchCDRForm.getDistributionStatus());
                            searchCDRParamMap.put(MBeanNameConstant.START_DATE, startDate);
                            searchCDRParamMap.put(MBeanNameConstant.END_DATE, endDate);
                            searchCDRParamMap.put(MBeanNameConstant.HIDE_CHECKED_OUT, searchCDRForm.getHideCheckedOut());
                            searchCDRParamMap.put(MBeanNameConstant.FIELD_LIST, (List) request.getSession().getAttribute("fieldList"));
                            
                            Object[] objArgValues = { searchCDRParamMap };
                            String[] strArgTypes = { "java.util.HashMap" };
                            List cdrDataList = (List) remoteCommunicationManager.execute("Elitecore:type=EliteAdmin", "searchCDRList", objArgValues, strArgTypes);
                            request.getSession().setAttribute("cdrDataList", cdrDataList);
                        }
                        
                        if (searchCDRForm.getCheckAction().equalsIgnoreCase("markForReprocess")) {
                            String[] strSelectedIds = request.getParameterValues("select");
                            
                            Object[] objArgValues = {strSelectedIds};
                            String[] strArgTypes = {String[].class.getName()};
                            remoteCommunicationManager.execute("Elitecore:type=EliteAdmin", "markForReprocessSelectedCDR", objArgValues, strArgTypes);
                        }
                        
                        if (searchCDRForm.getCheckAction().equalsIgnoreCase("update")) {
                            String[] strSelectedIds = request.getParameterValues("select");
                            
                            Object[] objArgValues = {strSelectedIds};
                            String[] strArgTypes = {String[].class.getName()};
                            remoteCommunicationManager.execute("Elitecore:type=EliteAdmin", "updateSelectedCDR", objArgValues, strArgTypes);
                        }
                        
                        if (searchCDRForm.getCheckAction().equalsIgnoreCase("checkOutSelected")) {
                            String[] strSelectedIds = request.getParameterValues("select");
                            
                            Object[] objArgValues = {strSelectedIds};
                            String[] strArgTypes = {String[].class.getName()};
                            remoteCommunicationManager.execute("Elitecore:type=EliteAdmin", "checkOutSelectedCDR", objArgValues, strArgTypes);
                        }
                        
                        if (searchCDRForm.getCheckAction().equalsIgnoreCase("checkedOutAll")) {
                            String[] strSelectedIds = request.getParameterValues("select");
                            
                            Object[] objArgValues = {strSelectedIds};
                            String[] strArgTypes = {String[].class.getName()};
                            remoteCommunicationManager.execute("Elitecore:type=EliteAdmin", "checkOutAllCDR", objArgValues, strArgTypes);
                        }
                    }
                }
                catch (Exception e) {
                    Logger.logError(MODULE, "Error in server side.. Reason : " + e.getMessage());
                    Logger.logTrace(MODULE, e);
                }
                
            }
            return mapping.findForward(VIEW_FORWARD);
            
        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (Exception e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
            ActionMessage message = new ActionMessage("general.error");
            messages.add("information", message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        
    }
}
