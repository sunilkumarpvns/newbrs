package com.elitecore.netvertexsm.web.servermgr.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.servermgr.service.form.ViewLiveServiceRequiredDetailForm;
import com.elitecore.passwordutil.PasswordEncryption;


public class ViewLiveServiceRequiredDetailAction extends BaseWebAction{
    private static final String SUCCESS_FORWARD = "success";               
        private static final String FAILURE_FORWARD = "failure";               
        private static final String MODULE ="viewLiveServiceRequiredDetailAction";
        private static final String VIEW_FORWARD = "viewLiveServiceRequiredDetail"; 

        public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
            Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
            ActionMessages messages = new ActionMessages();
                ViewLiveServiceRequiredDetailForm viewLiveServiceRequiredDetail = (ViewLiveServiceRequiredDetailForm)form;
                String dateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT);
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                
                try {
                    NetServerBLManager netServerBLManager = new NetServerBLManager();
                    NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
                    
                    List netServiceTypeList = null;
                    List netServerInstanceList = null;
                    IRemoteCommunicationManager remoteCommunicationManager = null;
    
                    String strNetServiceId = request.getParameter("netServiceId");
        			Long netServiceId=null;
        			if(strNetServiceId != null){
        				netServiceId = Long.parseLong(strNetServiceId);
        				Date d = new Date();
                        viewLiveServiceRequiredDetail.setStartDate(sdf.format(d));
                        viewLiveServiceRequiredDetail.setEndDate(sdf.format(d));
                        viewLiveServiceRequiredDetail.setEndHour(23);
                        viewLiveServiceRequiredDetail.setEndMinute(59);
                        
                        viewLiveServiceRequiredDetail.setPageNumber(1);
        			}else{
        				netServiceId = viewLiveServiceRequiredDetail.getServiceId();
                    }
                    
                    viewLiveServiceRequiredDetail.setServiceId(netServiceId);
                    INetServiceInstanceData netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceId);
                    INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServiceInstanceData.getNetServerId());
                    netServiceTypeList = netServiceBLManager.getNetServiceTypeList();
                    INetServiceTypeData netServiceTypeData = netServiceBLManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());
                    netServerInstanceList = netServerBLManager.getNetServerInstanceList();
            
                    request.setAttribute("netServerInstanceData",netServerInstanceData);
                    request.setAttribute("netServiceInstanceData",netServiceInstanceData);
                    request.setAttribute("netServiceTypeList",netServiceTypeList);
                    request.setAttribute("netServerInstanceList",netServerInstanceList);
                    request.setAttribute("netServiceId", netServiceId);
                    request.setAttribute("liveServiceSummaryMap", new LinkedHashMap());
                    
                    if(viewLiveServiceRequiredDetail.getCheckAction() != null && viewLiveServiceRequiredDetail.getCheckAction().equalsIgnoreCase("Send")) {
                        
                        Calendar tempCal = Calendar.getInstance();
                        
                        Date startDate = sdf.parse(viewLiveServiceRequiredDetail.getStartDate());
                        tempCal.setTime(startDate);
                        tempCal.set(Calendar.HOUR_OF_DAY, viewLiveServiceRequiredDetail.getStartHour());
                        tempCal.set(Calendar.MINUTE, viewLiveServiceRequiredDetail.getStartMinute());
                        startDate = tempCal.getTime();
                        
                        Date endDate = sdf.parse(viewLiveServiceRequiredDetail.getEndDate());
                        tempCal.setTime(endDate);
                        tempCal.set(Calendar.HOUR_OF_DAY, viewLiveServiceRequiredDetail.getEndHour());
                        tempCal.set(Calendar.MINUTE, viewLiveServiceRequiredDetail.getEndMinute());
                        endDate = tempCal.getTime();
                        
                        
                        System.out.println("Start date : " +startDate);
                        System.out.println("End date : "+ endDate);
                        
                        try {
                            String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);                     
                            remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);   
                            remoteCommunicationManager.init(netServerInstanceData.getAdminHost(),netServerInstanceData.getAdminPort(),netServerCode,true);
                            String strVersion = (String) remoteCommunicationManager.getAttribute("Elitecore:type=EliteAdmin","VersionInformation");
                        
                            if(strVersion.equalsIgnoreCase(netServerInstanceData.getVersion())){
                                
                                System.out.println("************** getPage N0 ******* " + viewLiveServiceRequiredDetail.getPageNumber());
                                Object[] objArgValues = {netServiceTypeData.getAlias(), netServiceInstanceData.getInstanceId(),startDate,endDate};
                                String[] strArgTypes = {"java.lang.String","java.lang.String","java.util.Date","java.util.Date"};
                                LinkedHashMap linkedHashMap = (LinkedHashMap)  remoteCommunicationManager.execute("Elitecore:type=EliteAdmin","getLiveServiceSummaryDetail",objArgValues,strArgTypes);
                                
                                viewLiveServiceRequiredDetail.setTotalPages(20);
                                viewLiveServiceRequiredDetail.setTotalRecords(200);

                                request.setAttribute("liveServiceSummaryMap", linkedHashMap);
                            }
                        } catch (Exception e) {
                            Logger.logError(MODULE, "Error during connection with live server, reason : " + e.getMessage());
                            Logger.logTrace(MODULE, e);
                            ActionMessage message = new ActionMessage("general.error");
                            messages.add("information", message);
                            saveErrors(request, messages);
                            return mapping.findForward(FAILURE_FORWARD);
                        }
                        
                    }
                    
                } catch(DataManagerException e) {
                    Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
                    Logger.logTrace(MODULE, e);
        			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
        			request.setAttribute("errorDetails", errorElements);
                    ActionMessage message = new ActionMessage("general.error");
                    messages.add("information", message);
                    saveErrors(request, messages);
                    return mapping.findForward(FAILURE_FORWARD);
                    
                } catch(Exception e) {
                    Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
                    Logger.logTrace(MODULE, e);
        			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
        			request.setAttribute("errorDetails", errorElements);
                    ActionMessage message = new ActionMessage("general.error");
                    messages.add("information", message);   
                    saveErrors(request, messages);
                    return mapping.findForward(FAILURE_FORWARD);
                }      
        
            return mapping.findForward(VIEW_FORWARD);
        }
}
