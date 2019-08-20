package com.elitecore.elitesm.web.rm.concurrentloginpolicy;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.blmanager.rm.concurrentloginpolicy.ConcurrentLoginPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.UpdateConcurrentLoginPolicyBasicDetailForm;

public class UpdateConcurrentLoginPolicyBasicDetailAction extends BaseDictionaryAction {
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD  = "updateConcurrentLoginPolicyDetail";
	private static final String VIEW_FORWARD    = "viewConcurrentLoginPolicyDetail";
	private static final String  MODULE         = "UPDATE CONCURRENT LOGIN POLICY BASIC DETAIL";
	private static final String strUpdateBasicDetails = "updateBasicDetails";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_CONCURRENT_LOGIN_POLICY_ACTION;
	private static final long STANDARD_VENDOR_ID=0;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			ConcurrentLoginPolicyBLManager concurrentLoginPolicyBLManager = new ConcurrentLoginPolicyBLManager();

			ActionErrors errors = new ActionErrors();
			String strConcurrentLoginPolicyId = request.getParameter("concurrentLoginPolicyId");
		
			
			try {

				UpdateConcurrentLoginPolicyBasicDetailForm updateConcurrentLoginPolicyBaseDetailForm = (UpdateConcurrentLoginPolicyBasicDetailForm)form;

				if(updateConcurrentLoginPolicyBaseDetailForm.getAction()==null){
					if (Strings.isNullOrEmpty(strConcurrentLoginPolicyId) == false) {
						IConcurrentLoginPolicyData concurrentLoginPolicyData = new ConcurrentLoginPolicyData();
						
						concurrentLoginPolicyData= concurrentLoginPolicyBLManager.getConcurrentLoginPolicyById(strConcurrentLoginPolicyId);
						
						request.setAttribute("concurrentLoginPolicyData",concurrentLoginPolicyData);
						updateConcurrentLoginPolicyBaseDetailForm = convertBeanToForm(concurrentLoginPolicyData);
						request.setAttribute("action",strUpdateBasicDetails);
						RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
						
						DictionaryData dictionaryData = dictionaryBLManager.getDictionaryDataByVendor(STANDARD_VENDOR_ID);
						List<DictionaryParameterDetailData> dictionaryParameterDetailList = dictionaryData.getDictionaryParameterDetailList();
						Collections.sort(dictionaryParameterDetailList);
						updateConcurrentLoginPolicyBaseDetailForm.setDictionaryParameterDetailList(dictionaryParameterDetailList);
						
						request.setAttribute("updateConcurrentLoginPolicyBasicDetailForm",updateConcurrentLoginPolicyBaseDetailForm);
					}
					return mapping.findForward(UPDATE_FORWARD);
				}else if(updateConcurrentLoginPolicyBaseDetailForm.getAction().equalsIgnoreCase("update")){
					System.out.println(" update called ");
					System.out.println("updateConcurrentLoginPolicyBaseDetailForm"+updateConcurrentLoginPolicyBaseDetailForm.getConcurrentLoginId());
					IConcurrentLoginPolicyData concurrentLoginPolicyData = new ConcurrentLoginPolicyData();
					
					concurrentLoginPolicyData= concurrentLoginPolicyBLManager.getConcurrentLoginPolicyById(updateConcurrentLoginPolicyBaseDetailForm.getConcurrentLoginId());
					
					concurrentLoginPolicyData =convertFormToBean(updateConcurrentLoginPolicyBaseDetailForm,concurrentLoginPolicyData);

					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;

					staffData.setAuditId(concurrentLoginPolicyData.getAuditUId());
					staffData.setAuditName(concurrentLoginPolicyData.getName());
					
					concurrentLoginPolicyBLManager.updateBasicDetail(concurrentLoginPolicyData,staffData,ACTION_ALIAS);
					
					return mapping.findForward(VIEW_FORWARD);
				}
			} catch (Exception e) {

				Logger.logError(MODULE, "Error during Data Manager operation ");
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("concurrentloginpolicy.update.failuremessage");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				// TODO: handle exception
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
	
	private UpdateConcurrentLoginPolicyBasicDetailForm convertBeanToForm(IConcurrentLoginPolicyData concurrentLoginPolicyData){
		UpdateConcurrentLoginPolicyBasicDetailForm updateConcurrentLoginPolicyBasicDetailForm=null;
		if(concurrentLoginPolicyData != null){
			updateConcurrentLoginPolicyBasicDetailForm = new UpdateConcurrentLoginPolicyBasicDetailForm();
			System.out.println("(((((((((((  (((( "+concurrentLoginPolicyData.getConcurrentLoginId());
			updateConcurrentLoginPolicyBasicDetailForm.setConcurrentLoginId(concurrentLoginPolicyData.getConcurrentLoginId());
			updateConcurrentLoginPolicyBasicDetailForm.setConcurrentLoginPolicyMode(concurrentLoginPolicyData.getConcurrentLoginPolicyModeId());
			updateConcurrentLoginPolicyBasicDetailForm.setName(concurrentLoginPolicyData.getName());
			
			if(concurrentLoginPolicyData.getLogin()== -1){
				updateConcurrentLoginPolicyBasicDetailForm.setMaxLogin("U");
			}else{
				updateConcurrentLoginPolicyBasicDetailForm.setMaxLogin("L");
			}
			updateConcurrentLoginPolicyBasicDetailForm.setAttribute(concurrentLoginPolicyData.getAttribute());
			updateConcurrentLoginPolicyBasicDetailForm.setLogin(concurrentLoginPolicyData.getLogin());
			updateConcurrentLoginPolicyBasicDetailForm.setDescription(concurrentLoginPolicyData.getDescription());
			updateConcurrentLoginPolicyBasicDetailForm.setConcurrentLoginPolicy(concurrentLoginPolicyData.getConcurrentLoginPolicyTypeId());
			updateConcurrentLoginPolicyBasicDetailForm.setStatus(concurrentLoginPolicyData.getCommonStatusId());
			updateConcurrentLoginPolicyBasicDetailForm.setAuditUId(concurrentLoginPolicyData.getAuditUId());
		}
		
		return updateConcurrentLoginPolicyBasicDetailForm;
	}
	
	private IConcurrentLoginPolicyData convertFormToBean(UpdateConcurrentLoginPolicyBasicDetailForm updateConcurrentLoginPolicyBasicDetailForm,IConcurrentLoginPolicyData concurrentLoginPolicyData){
		if(updateConcurrentLoginPolicyBasicDetailForm != null){
			System.out.println("jsp to action - Conc. Id "+updateConcurrentLoginPolicyBasicDetailForm.getConcurrentLoginId());
			concurrentLoginPolicyData.setConcurrentLoginId(updateConcurrentLoginPolicyBasicDetailForm.getConcurrentLoginId());
			concurrentLoginPolicyData.setConcurrentLoginPolicyTypeId(updateConcurrentLoginPolicyBasicDetailForm.getConcurrentLoginPolicy());
			concurrentLoginPolicyData.setConcurrentLoginPolicyModeId(updateConcurrentLoginPolicyBasicDetailForm.getConcurrentLoginPolicyMode());
			concurrentLoginPolicyData.setLogin(updateConcurrentLoginPolicyBasicDetailForm.getLogin());
			concurrentLoginPolicyData.setAttribute(updateConcurrentLoginPolicyBasicDetailForm.getAttribute());
			concurrentLoginPolicyData.setName(updateConcurrentLoginPolicyBasicDetailForm.getName());
			concurrentLoginPolicyData.setDescription(updateConcurrentLoginPolicyBasicDetailForm.getDescription());
			concurrentLoginPolicyData.setAuditUId(updateConcurrentLoginPolicyBasicDetailForm.getAuditUId());
		}
		return concurrentLoginPolicyData;
	}
}
