package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.ArrayList;
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
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.RemoveNetServerServiceInstanceForm;

public class RemoveNetServerServiceInstanceAction extends BaseWebAction{
    private static final String UPDATE_FORWARD = "removeNetServerServiceInstance";
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String MODULE = "REMOVE NET SERVER SERVICE ACTION";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.REMOVE_NET_SERVER_SERVICE_INSTANCE_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
        String strNetServerId = request.getParameter("netserverid");

        try {
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            RemoveNetServerServiceInstanceForm removeNetServerServiceInstanceForm = (RemoveNetServerServiceInstanceForm)form;
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            NetServiceBLManager netServiceBLManager = new NetServiceBLManager();

            String action = removeNetServerServiceInstanceForm.getAction();
			Long netServerId=null;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
				removeNetServerServiceInstanceForm.setNetServerId(netServerId);
			}else{
                netServerId = removeNetServerServiceInstanceForm.getNetServerId();
            }
			INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
            if(netServerId != null){
               
                netServerInstanceData.setNetServerId(netServerId);

                if(action != null ){
	                	if(action.equalsIgnoreCase("remove")){
	                    String[] strSelectedIds = request.getParameterValues("select");
	                    List<Long> selectedIdList = new ArrayList<Long>();
	                    if(strSelectedIds != null){
	                    	for (int i = 0; i < strSelectedIds.length; i++) {
	                    		selectedIdList.add(Long.parseLong(strSelectedIds[i]));
							}
	                        if(removeNetServerServiceInstanceForm.getAction().equalsIgnoreCase("remove")){
	                            netServiceBLManager.deleteService(selectedIdList,netServerId);
	                        }
	                    }
	                    request.setAttribute("responseUrl","/removeNetServerServiceInstance.do?netserverid="+netServerInstanceData.getNetServerId());
	                    ActionMessage message = new ActionMessage("servermgr.remove.success");
	                    ActionMessages messages = new ActionMessages();
	                    messages.add("information",message);
	                    saveMessages(request,messages);
	                    return mapping.findForward(SUCCESS_FORWARD);
	                }
	                	
                }else{
	                netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
	                List netServerTypeList = netServerBLManager.getNetServerTypeList();
	                List netServiceInstanceList = netServiceBLManager.getNetServiceInstanceList(netServerInstanceData.getNetServerId());
	                List lstNetServiceType = netServiceBLManager.getNetServiceTypeList();	
	                removeNetServerServiceInstanceForm.setListServices(netServiceInstanceList);
	
	                request.setAttribute("netServerInstanceData",netServerInstanceData);
	                request.setAttribute("netServiceInstanceList",netServiceInstanceList);
	                request.setAttribute("netServerTypeList",netServerTypeList);
	                request.setAttribute("lstNetServiceType",lstNetServiceType);
	
	                return mapping.findForward(UPDATE_FORWARD);
                }
            }else{
                Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
                ActionMessage message = new ActionMessage("servermgr.remove.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveErrors(request,messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (Exception exp) {
            Logger.logError(MODULE,"Error during data Manager Operation, reason :"+exp.getMessage());
            Logger.logTrace(MODULE,exp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
        }
        Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
        ActionMessage message = new ActionMessage("servermgr.remove.failure");
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
        return mapping.findForward(FAILURE_FORWARD);
    }
}
