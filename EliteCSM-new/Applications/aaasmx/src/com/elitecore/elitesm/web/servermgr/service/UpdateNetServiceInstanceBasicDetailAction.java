package com.elitecore.elitesm.web.servermgr.service;

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
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.servermgr.service.forms.UpdateNetServiceInstanceBasicDetailForm;

public class UpdateNetServiceInstanceBasicDetailAction extends BaseDictionaryAction {
	
	private static final String FAILURE_FORWARD = "failure";
	private static final String SUCCESS_FORWARD = "success";
	private static final String UPDATE_FORWARD  = "updateNetServiceInstanceBasicDetail";
	private static final String VIEW_FORWARD = "viewNetServiceInstance";
	private static final String MODULE = "UPDATE SERVICE BASIC DETAIL ACTION";
	private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.UPDATE_SERVICE_BASIC_DETAIL_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
		
		NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		String strNetServiceId = request.getParameter("netserviceid");
		List netServerInstanceList = netServerBLManager.getNetServerInstanceList();
		List lstNetServiceType = null;
		List netServiceTypeList = null;
		
		try{
			checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
			UpdateNetServiceInstanceBasicDetailForm updateNetServiceInstanceBasicDetailForm = (UpdateNetServiceInstanceBasicDetailForm)form;
			lstNetServiceType = netServiceBLManager.getNetServiceTypeList();
			String netServiceId=null;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			if(strNetServiceId != null){
				netServiceId = strNetServiceId;
			}
            
			if(updateNetServiceInstanceBasicDetailForm.getAction() == null){
				
				if(netServiceId != null ){
					
					INetServiceInstanceData netServiceInstanceData = new NetServiceInstanceData();
					netServiceInstanceData.setNetServiceId(netServiceId);
					
					netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceInstanceData.getNetServiceId());
					netServiceTypeList = netServiceBLManager.getNetServiceTypeList();
					
					request.setAttribute("netServiceInstanceData",netServiceInstanceData);
					updateNetServiceInstanceBasicDetailForm = convertBeanToForm(netServiceInstanceData);
					request.setAttribute("lstNetServiceType",lstNetServiceType);
					request.setAttribute("updateNetServiceInstanceBasicDetailForm",updateNetServiceInstanceBasicDetailForm);
					request.setAttribute("netServiceTypeList",netServiceTypeList);
					request.setAttribute("netServerInstanceList",netServerInstanceList);
				}
				return mapping.findForward(UPDATE_FORWARD);
			}
			else if(updateNetServiceInstanceBasicDetailForm.getAction().equalsIgnoreCase("update")){
				INetServiceInstanceData inetServiceInstanceData = convertFormToBean(updateNetServiceInstanceBasicDetailForm);
				netServiceBLManager.updateBasicDetail(inetServiceInstanceData);
				request.setAttribute("lstNetServiceType",lstNetServiceType);
				request.setAttribute("netServiceTypeList",netServiceTypeList);
				
				doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
				request.setAttribute("responseUrl","/updateNetServiceInstanceBasicDetail.do?netserviceid="+inetServiceInstanceData.getNetServiceId());
				ActionMessage message = new ActionMessage("servermgr.service.updatebasicdetail.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);
//				return mapping.findForward(VIEW_FORWARD);
			}
		
		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch(Exception e){
			e.printStackTrace();
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
		}
		Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.service.update.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveMessages(request, messages);
		return mapping.findForward(FAILURE_FORWARD);
		
	}
	private UpdateNetServiceInstanceBasicDetailForm convertBeanToForm(INetServiceInstanceData netServiceInstanceData){
		UpdateNetServiceInstanceBasicDetailForm updateNetServiceInstanceBasicDetailForm = null;
		if(netServiceInstanceData != null){
			updateNetServiceInstanceBasicDetailForm = new UpdateNetServiceInstanceBasicDetailForm();
			updateNetServiceInstanceBasicDetailForm.setNetServiceId(netServiceInstanceData.getNetServiceId());
			updateNetServiceInstanceBasicDetailForm.setName(netServiceInstanceData.getName());
			updateNetServiceInstanceBasicDetailForm.setDescription(netServiceInstanceData.getDescription());
			updateNetServiceInstanceBasicDetailForm.setNetServiceType(netServiceInstanceData.getNetServiceTypeId());
                        
		}
		return updateNetServiceInstanceBasicDetailForm;
	}
	private INetServiceInstanceData convertFormToBean(UpdateNetServiceInstanceBasicDetailForm updateNetServiceInstanceBasicDetailForm){
		INetServiceInstanceData netServiceInstanceData = null;
		if(updateNetServiceInstanceBasicDetailForm != null){
			netServiceInstanceData = new NetServiceInstanceData();
			netServiceInstanceData.setNetServiceId(updateNetServiceInstanceBasicDetailForm.getNetServiceId());
			netServiceInstanceData.setName(updateNetServiceInstanceBasicDetailForm.getName());
			netServiceInstanceData.setDescription(updateNetServiceInstanceBasicDetailForm.getDescription());
			netServiceInstanceData.setNetServiceTypeId(updateNetServiceInstanceBasicDetailForm.getNetServiceType());
			
		}
		return netServiceInstanceData;
	}
}
