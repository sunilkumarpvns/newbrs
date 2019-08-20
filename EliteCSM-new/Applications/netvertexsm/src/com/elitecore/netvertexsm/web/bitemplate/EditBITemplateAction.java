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

public class EditBITemplateAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_BICEA_TEMPLATE;
	private static final String MODULE = "BI-TEMPLATE";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){
			try{
				BITemplateForm editBIForm = (BITemplateForm) form;
				BITemplateBLManager biBLManager = new BITemplateBLManager();
				BITemplateData biData = new BITemplateData();
				biData.setId(editBIForm.getBiId());
				biData.setName(editBIForm.getName());
				biData.setDescription(editBIForm.getDescription());
				biData.setKey(editBIForm.getBikey());			
				String[] strKeys = request.getParameterValues("key");
				String[] strValues = request.getParameterValues("value");

				List<BISubKeyData> subKeyDatas = new ArrayList<BISubKeyData>();
				if(strKeys != null)
					for(int i=0; i<strKeys.length; i++) {
						//if(strKeys[i]!=null && strKeys[i].trim().length()>0 && !strKeys[i].trim().equalsIgnoreCase("null")){
						BISubKeyData relData = new BISubKeyData();
							relData.setKey(strKeys[i].trim());
							relData.setValue(strValues[i].trim());
						subKeyDatas.add(relData);
						//}
					}
				biData.setBiSubKeyList(subKeyDatas);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
                biBLManager.updateBITemplate(biData, staffData,ACTION_ALIAS);
                
               	ActionMessage message = new ActionMessage("bitemplate.update.success",biData.getName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/searchBITemplate.do");
				return mapping.findForward(SUCCESS_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("bitemplate.update.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("bitemplate.error.heading","updating");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);	            
	            
	            return mapping.findForward(FAILURE_FORWARD);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("bitemplate.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);		        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
