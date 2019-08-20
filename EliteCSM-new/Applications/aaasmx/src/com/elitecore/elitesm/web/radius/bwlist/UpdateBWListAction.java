package com.elitecore.elitesm.web.radius.bwlist;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.bwlist.BWListBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.bwlist.data.BWListData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.bwlist.forms.CreateBWListForm;

public class UpdateBWListAction extends BaseWebAction {
	private static final String INFORMATION = "information";
	private static final String BLACK_LIST_CANDIDATE = "BLACK LIST CANDIDATE";
	private static final String ACTION_ALIAS_UPDATE = ConfigConstant.UPDATE_BLACKLIST_CANDIDATES_ACTION;
	private static final String FAILURE_FORWARD = "failure";
	private static final String SUCCESS_FORWARD = "success";
	private static final String UPDATE_FORWARD = "uploadBWList";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Logger.logTrace(BLACK_LIST_CANDIDATE, "Entered execute method of " + getClass().getName());
		
		try {

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			CreateBWListForm bwListForm = (CreateBWListForm)form;
			BWListBLManager bwlistBLManager = new BWListBLManager();
			
			if("update".equalsIgnoreCase(bwListForm.getInputMode())){

				checkActionPermission(request, ACTION_ALIAS_UPDATE);
			
				BWListData bwListDataOld = bwlistBLManager.getBWListData(bwListForm.getBwId());
				BWListData bwlistData = bwlistBLManager.getBWListData(bwListForm.getBwId());
				
				convertFormToBean(bwlistData, bwListForm);
                
				bwlistBLManager.update(bwListDataOld, bwlistData, staffData);
				
				request.setAttribute("responseUrl", "/viewBWList.do?bwId=" + bwListForm.getBwId());
				ActionMessage message = new ActionMessage("bwlist.update.success");
				ActionMessages messages = new ActionMessages();
				messages.add(INFORMATION,message);
				saveMessages(request,messages);

			} else {
				
				BWListData bwListData = bwlistBLManager.getBWListData(bwListForm.getBwId());
				convertBeanToForm(bwListData, bwListForm);
				
				request.setAttribute("bwListData", bwListData);
				return mapping.findForward(UPDATE_FORWARD);
			
			}

			return mapping.findForward(SUCCESS_FORWARD);
		}catch(ActionNotPermitedException e){
			ActionMessages messages = new ActionMessages();
			Logger.logError(BLACK_LIST_CANDIDATE, "Action is not permitted.");
			Logger.logTrace(BLACK_LIST_CANDIDATE, e);
			ActionMessage message = new ActionMessage("general.user.restricted");
			messages.add(INFORMATION, message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);

		}catch (Exception managerExp) {
			Logger.logError(BLACK_LIST_CANDIDATE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
			Logger.logTrace(BLACK_LIST_CANDIDATE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("bwlist.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION,message);
			saveErrors(request,messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	private void convertBeanToForm(BWListData bwListData, CreateBWListForm bwListForm) {
		bwListForm.setBwId(bwListData.getBwId());
		bwListForm.setAttributeId(bwListData.getAttributeId());
		bwListForm.setAttributeValue(bwListData.getAttributeValue());
		bwListForm.setActiveStatus(bwListData.getCommonStatusId());
		bwListForm.setValidity(bwListData.getValidity());
		bwListForm.setTypeName(bwListData.getTypeName());
	}

	private void convertFormToBean(BWListData bwlistData, CreateBWListForm bwListForm){
		bwlistData.setAttributeId(bwListForm.getAttributeId());
		bwlistData.setAttributeValue(bwListForm.getAttributeValue());
		bwlistData.setTypeId("LST00");
		if(bwListForm.getActiveStatus()!=null){
			if(bwListForm.getActiveStatus().equalsIgnoreCase("1")){
				bwlistData.setCommonStatusId("CST01");
			}else{
				bwlistData.setCommonStatusId("CST02");					
			}
		}else{
			bwlistData.setCommonStatusId("CST02");				
		}

		bwlistData.setValidity(bwListForm.getValidity());
		bwlistData.setTypeName(bwListForm.getTypeName());
	}
}
