package com.elitecore.elitesm.web.rm.concurrentloginpolicy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.rm.concurrentloginpolicy.ConcurrentLoginPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyData;
import com.elitecore.elitesm.util.constants.ConcurrentLoginPolicyConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.UpdateConcurrentLoginPolicyStatusForm;


public class UpdateConcurrentLoginPolicyStatusAction  extends BaseDictionaryAction{
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD = "updateConcLoginPolicyStatus";
	private static final String VIEW_FORWARD = "viewConcurrentLoginPolicyDetail";
	private static final String  MODULE         = "UPDATE CONCURRENT LOGIN POLICY STATUS";	
	private static final String strUpdateStatus ="updateStatus";
	private static final String ACTION_ALIAS = ConfigConstant.CHANGE_CONCURRENT_LOGIN_POLICY_STATUS_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			ConcurrentLoginPolicyBLManager blManager = new ConcurrentLoginPolicyBLManager();
			UpdateConcurrentLoginPolicyStatusForm updateConcurrentLoginPolicyStatusForm = (UpdateConcurrentLoginPolicyStatusForm)form;
			String strConcurrentLoginPolicyId = request.getParameter("concurrentLoginPolicyId");

			try {
				String concurrentLoginPolicyId=null;
				if(strConcurrentLoginPolicyId == null){
					
					concurrentLoginPolicyId = updateConcurrentLoginPolicyStatusForm.getConcurrentLoginId();
					Logger.logTrace(MODULE,"After submit setting concurrentLoginPolicyId:"+concurrentLoginPolicyId);
				}else{
					concurrentLoginPolicyId = strConcurrentLoginPolicyId;
				}

				Logger.logDebug(MODULE,"concurrentLoginPolicyId :"+concurrentLoginPolicyId);
				IConcurrentLoginPolicyData concurrentLoginPolicyData = new ConcurrentLoginPolicyData();
				
				String action = updateConcurrentLoginPolicyStatusForm.getAction();
				
				if( Strings.isNullOrEmpty(concurrentLoginPolicyId) == false){
					
					updateConcurrentLoginPolicyStatusForm.setConcurrentLoginId(concurrentLoginPolicyId);
					concurrentLoginPolicyData = blManager.getConcurrentLoginPolicyById(concurrentLoginPolicyId);
				}

				request.setAttribute("concurrentLoginPolicyData",concurrentLoginPolicyData);

				if(action != null){
					if(action.equalsIgnoreCase("update")){
						List lstConcurrentLoginId = new ArrayList();
						lstConcurrentLoginId.add(concurrentLoginPolicyId);
						String status = updateConcurrentLoginPolicyStatusForm.getStatus();
						String reason = updateConcurrentLoginPolicyStatusForm.getReason();
						if(status.equalsIgnoreCase(ConcurrentLoginPolicyConstant.SHOW_STATUS_ID)){
							status=ConcurrentLoginPolicyConstant.HIDE_STATUS_ID;
						}else{
							status=ConcurrentLoginPolicyConstant.SHOW_STATUS_ID;
						}

						IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					
						staffData.setAuditId(concurrentLoginPolicyData.getAuditUId());
						staffData.setAuditName(concurrentLoginPolicyData.getName());

						String actionAlias = ACTION_ALIAS;
						
						concurrentLoginPolicyData.setCommonStatusId(status);
						
						blManager.updateStatus(concurrentLoginPolicyData,lstConcurrentLoginId,status,reason,staffData,actionAlias);
						return mapping.findForward(VIEW_FORWARD);
					}
				}else{
					updateConcurrentLoginPolicyStatusForm.setStatus(concurrentLoginPolicyData.getCommonStatusId());
					updateConcurrentLoginPolicyStatusForm.setConcurrentLoginId(concurrentLoginPolicyId);
					updateConcurrentLoginPolicyStatusForm.setStatusChangeDate(concurrentLoginPolicyData.getStatusChangeDate());
					updateConcurrentLoginPolicyStatusForm.setAuditUId(concurrentLoginPolicyData.getAuditUId());
					
					request.setAttribute("action",strUpdateStatus);
					return mapping.findForward(UPDATE_FORWARD);
				}
			} catch (Exception e) {
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
			}
			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
