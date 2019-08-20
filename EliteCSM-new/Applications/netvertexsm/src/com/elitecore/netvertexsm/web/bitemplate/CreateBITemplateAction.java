package com.elitecore.netvertexsm.web.bitemplate;

import java.util.ArrayList;
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
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.bitemplate.form.BITemplateForm;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class CreateBITemplateAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_BICEA_TEMPLATE;
	private static final String MODULE = "BI-TEMPLATE";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){
			BITemplateForm createBIForm = null;
			try{
				createBIForm = (BITemplateForm) form;
				BITemplateBLManager biBLManager = new BITemplateBLManager();
				BITemplateData biData = new BITemplateData();
				biData.setName(createBIForm.getName());
				biData.setKey(createBIForm.getBikey());
				biData.setDescription(createBIForm.getDescription());			
				String[] strKeys = request.getParameterValues("key");

				List<BISubKeyData> subKeyDatas = new ArrayList<BISubKeyData>();
				if(strKeys != null)
					for(int i=0; i<strKeys.length; i++) {
						BISubKeyData relData = new BISubKeyData();
						relData.setKey(strKeys[i].trim());
						relData.setValue("0");
						subKeyDatas.add(relData);
					}
				biData.setBiSubKeyList(subKeyDatas);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				biBLManager.create(biData,staffData, ACTION_ALIAS);
				ActionMessage message = new ActionMessage("bitemplate.create.success", createBIForm.getName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
				request.setAttribute("responseUrl","/searchBITemplate.do");
				return mapping.findForward(SUCCESS_FORWARD);
			}catch (Exception e) {
				Logger.logError(MODULE,"Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("bitemplate.create.failure",createBIForm.getName()));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("bitemplate.error.heading","creating");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);	            
	            return mapping.findForward(FAILURE_FORWARD);
			}    
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("bitemplate.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);	            
            
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
