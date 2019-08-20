package com.elitecore.elitesm.web.servermgr.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.service.forms.AddNetServerServiceInstanceForm;

public class AddNetServerServiceInstanceAction extends BaseWebAction{
    private static final String FAILURE_FORWARD = "failure";
    private static final String UPDATE_FORWARD  = "addNetServerServiceInstance";
    private static final String VIEW_FORWARD = "viewNetServerInstance";
    private static final String MODULE = "ADD NET SERVER SERVICE ACTION";
    private static final String SUCCESS_FORWARD = "success";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.ADD_NET_SERVER_SERVICE_INSTANCE_ACTION;



    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse reponse) throws Exception{
        Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());


        try {
            checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
            String strNetServerId = request.getParameter("netserverid");    
            AddNetServerServiceInstanceForm addNetServerServiceInstanceForm = (AddNetServerServiceInstanceForm)form;
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
            String selectedServices[] = addNetServerServiceInstanceForm.getSelectedServices();
            INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
            String action = addNetServerServiceInstanceForm.getAction();
            IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			String netServerId=null;
			if(strNetServerId != null){
				netServerId = strNetServerId;
			}else{
                netServerId = addNetServerServiceInstanceForm.getNetFormServerId();
            }

            if(netServerId != null){
                netServerInstanceData.setNetServerId(netServerId);
                netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
                addNetServerServiceInstanceForm.setNetFormServerId(netServerInstanceData.getNetServerId());
                addNetServerServiceInstanceForm.setNetFormServerTypeId(netServerInstanceData.getNetServerTypeId());

                List lstNetServiceType = netServiceBLManager.getNetServiceTypeList(netServerInstanceData.getNetServerTypeId());
                
                List netServerTypeList = netServerBLManager.getNetServerTypeList();
                List netServiceInstanceList =netServiceBLManager.getNetServiceInstanceList(netServerId);
                lstNetServiceType = removeExistingServiceTypeFromList(netServiceInstanceList,lstNetServiceType);
                addNetServerServiceInstanceForm.setLstServiceType(lstNetServiceType);
                request.setAttribute("netServerInstanceData",netServerInstanceData);
                request.setAttribute("netServerTypeList",netServerTypeList);
                request.setAttribute("lstNetServiceType",lstNetServiceType);
              
                if(action != null){					

                	if(action.equalsIgnoreCase("update")){

                        List<INetServiceInstanceData> listInstanceServices = addNetServerServiceInstanceForm.getListInstanceServices();
                        if(selectedServices!=null){
                        	 NetServiceBLManager serviceBLManager = new NetServiceBLManager();
                        	 for (int i = 0; i < selectedServices.length; i++) {
                        		 INetServiceTypeData serviceTypeData = serviceBLManager.getNetServiceType(selectedServices[i]);
                        		 INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
                        		 netServiceInstanceData.setNetServiceTypeId(serviceTypeData.getNetServiceTypeId());
                        		 netServiceInstanceData.setName(serviceTypeData.getName());
                        		 netServiceInstanceData.setDisplayName(serviceTypeData.getName());
                        		 netServiceInstanceData.setDescription(serviceTypeData.getDescription());

                        		 netServiceInstanceData.setCreatedByStaffId(getLoggedInUserId(request));
                        		 netServiceInstanceData.setCreateDate(getCurrentTimeStemp());
                        		 netServiceInstanceData.setInstanceId("000");
                        		 netServiceInstanceData.setNetServerId(netServerId);
                        		 netServiceInstanceData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
                        		 netServiceInstanceData.setIsInSync(BaseConstant.HIDE_STATUS_ID);
                        		 netServiceInstanceData.setSystemGenerated(BaseConstant.NOT_A_SYSTEMRECORD);
                        		 netServiceInstanceData.setLastModifiedDate(null);
                        		 netServiceInstanceData.setLastModifiedByStaffId(null);
                        		 netServiceInstanceData.setStatusChangeDate(null);
                        		 netServiceInstanceData.setLastSyncDate(null);
                        		 netServiceInstanceData.setLastSuccessSynDate(null);
                        		 netServiceInstanceData.setLastSyncStatus(null);
                        		 Logger.logDebug(MODULE,"creating netServiceInstanceData: "+netServiceInstanceData);
                        		 netServiceBLManager.createServiceInstance(netServiceInstanceData);
                        		 doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
                        	 }
                        }

                        request.setAttribute("responseUrl","/addNetServerServiceInstance.do?netserverid="+netServerInstanceData.getNetServerId());
                        ActionMessage message = new ActionMessage("servermgr.srever.addservice.success");
                        ActionMessages messages = new ActionMessages();
                        messages.add("information",message);
                        saveMessages(request,messages);
                        return mapping.findForward(SUCCESS_FORWARD);
                    }

                }else{
                    addNetServerServiceInstanceForm.setStatus("1");
                    addNetServerServiceInstanceForm.setListInstanceServices(new ArrayList<INetServiceInstanceData>());
                    return mapping.findForward(UPDATE_FORWARD);
                }
            }
        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (Exception e) {
            Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.addserverservice.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
        }
        return mapping.findForward(FAILURE_FORWARD);
    }
   
    private List<INetServiceTypeData> removeExistingServiceTypeFromList(List<INetServiceInstanceData> existingServiceInstanceList, List<INetServiceTypeData> netServiceTypeList){
    	  if(existingServiceInstanceList!=null && !existingServiceInstanceList.isEmpty()){
          	for (Iterator<INetServiceInstanceData> iterator = existingServiceInstanceList.iterator(); iterator.hasNext();) {
					INetServiceInstanceData netServiceInstanceData= iterator.next();
					if(netServiceTypeList!=null && !netServiceTypeList.isEmpty()){
						for(int i=netServiceTypeList.size()-1; i>=0;i--){
							INetServiceTypeData netServiceTypeData = (INetServiceTypeData) netServiceTypeList.get(i);
							if(netServiceTypeData.getNetServiceTypeId().equals(netServiceInstanceData.getNetServiceTypeId())){
								netServiceTypeList.remove(i);
							}
						}
					}
				}
          }
    	  return netServiceTypeList;
    }
  
}
