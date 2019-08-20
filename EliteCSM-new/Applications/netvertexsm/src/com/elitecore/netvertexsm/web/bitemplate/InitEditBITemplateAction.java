package com.elitecore.netvertexsm.web.bitemplate;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.bitemplate.BITemplateBLManager;
import com.elitecore.netvertexsm.datamanager.bitemplate.data.BISubKeyData;
import com.elitecore.netvertexsm.datamanager.bitemplate.data.BITemplateData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.bitemplate.form.BITemplateForm;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;

public class InitEditBITemplateAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "initEditBITemp";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_BICEA_TEMPLATE;
	private static final String MODULE = "BI-TEMPLATE";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				BITemplateForm biForm = (BITemplateForm) form;
				BITemplateData biData = new BITemplateData();
				BITemplateBLManager biBLManager = new BITemplateBLManager();
				
				long biId = Long.parseLong(request.getParameter("id"));
				
				biData.setId(biId);
				biData = biBLManager.getBITemplateData(biData);
				
				biForm.setBiId(biId);
				biForm.setDescription(biData.getDescription());
				biForm.setBikey(biData.getKey());
				biForm.setName(biData.getName());
				
				List<BISubKeyData> subKeyList = biBLManager.getBISubKeyList(biId);
				biForm.setSubKeyList(subKeyList);
				request.setAttribute("biTemplateForm", biForm);
				
				return mapping.findForward(SUCCESS_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("init.edit.failure");
				messages.add("information",message1);
				saveErrors(request,messages);
			} 
			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
