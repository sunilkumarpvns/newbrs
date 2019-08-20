package com.elitecore.elitesm.web.externalsystem;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.externalsystem.forms.UpdateESIInstanceForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewESIHistoryAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "viewESIHistory";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String MODULE = "INIT UPDATE ACTION";
	private static final String ACTION_ALIAS = "INIT_UPDATE	_ACTION";
	private static final String VIEW_ACTION_ALIAS=ConfigConstant.VIEW_EXTERNAL_SYSTEM;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		UpdateESIInstanceForm updateESIForm = (UpdateESIInstanceForm)form;
		String esiInstanceId = updateESIForm.getEsiInstanceId();			
		
		ExternalSystemInterfaceBLManager esiBLManager = new ExternalSystemInterfaceBLManager();
		
		try {
			
			ESITypeAndInstanceData esiTypeAndInstanceData = esiBLManager.getESIInstanceDetail(esiInstanceId);			
			List esiTypeList = esiBLManager.getListOfESIType();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			updateESIForm.setDescription(esiTypeAndInstanceData.getDescription());
			updateESIForm.setEsiInstanceId(esiTypeAndInstanceData.getEsiInstanceId());
			
			updateESIForm.setEsiTypeName(esiTypeAndInstanceData.getEsiTypeName());
			updateESIForm.setAddress(esiTypeAndInstanceData.getAddress());
			updateESIForm.setName(esiTypeAndInstanceData.getName());
			updateESIForm.setRealmNames(esiTypeAndInstanceData.getRealmNames());
			updateESIForm.setMinLocalPort(esiTypeAndInstanceData.getMinLocalPort());
			updateESIForm.setTimeout(esiTypeAndInstanceData.getTimeout());
			updateESIForm.setExpiredRequestLimitCount(esiTypeAndInstanceData.getExpiredRequestLimitCount());
			updateESIForm.setRetryLimit(esiTypeAndInstanceData.getRetryLimit());
			updateESIForm.setStatusCheckDuration(esiTypeAndInstanceData.getStatusCheckDuration());
			updateESIForm.setSharedSecret(esiTypeAndInstanceData.getSharedSecret());
			updateESIForm.setSupportedAttribute(esiTypeAndInstanceData.getSupportedAttribute());
			updateESIForm.setUnSupportedAttribute(esiTypeAndInstanceData.getUnSupportedAttribute());
			updateESIForm.setStatusCheckMethod(esiTypeAndInstanceData.getStatusCheckMethod());
			updateESIForm.setPacketBytes(esiTypeAndInstanceData.getPacketBytes());
			
			
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strInstanceId = request.getParameter("esiInstanceId");
			String esiInstId = strInstanceId;
			if(esiInstId == null){
				esiInstId = updateESIForm.getEsiInstanceId();
			}

			String strAuditUid = request.getParameter("auditUid");
			String strSytemAuditId=request.getParameter("systemAuditId");
			String name=request.getParameter("name");
			
			if(strSytemAuditId != null){
				request.setAttribute("systemAuditId", strSytemAuditId);
			}
			
			if(esiInstId != null && Strings.isNullOrBlank(strAuditUid) == false){
				esiTypeAndInstanceData.setEsiInstanceId(esiInstId);
				String actionAlias = ACTION_ALIAS;
				
				staffData.setAuditName(esiTypeAndInstanceData.getName());
				staffData.setAuditId(esiTypeAndInstanceData.getAuditUId());
				
				List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
				
				request.setAttribute("name", name);
				request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
				request.getSession().setAttribute("esiTypeInstance",esiTypeAndInstanceData);
			}
			return mapping.findForward(SUCCESS_FORWARD);
		} catch (DataManagerException e) {
			
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("esi.update.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}
}
