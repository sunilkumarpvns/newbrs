package com.elitecore.netvertexsm.web.bitemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.bitemplate.BITemplateBLManager;
import com.elitecore.netvertexsm.datamanager.bitemplate.data.BITemplateData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.bitemplate.form.BITemplateForm;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class SearchBITemplateAction extends BaseWebAction {		
	private static final String LIST_FORWARD = "searchBITemplate";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_BICEA_TEMPLATE;
	
	@SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				BITemplateForm searchTemplateForm = (BITemplateForm) actionForm;
				BITemplateBLManager biBLManager = new BITemplateBLManager();
				BITemplateData biSearchData = new BITemplateData();
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	            int requiredPageNo;
	            if(request.getParameter("pageNo") != null)
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		else
	    			requiredPageNo = new Long(searchTemplateForm.getPageNumber()).intValue();
	    		
	            if (requiredPageNo == 0)
	                requiredPageNo = 1;								
	            
				String strName = searchTemplateForm.getName();					
				if(strName!=null && strName.length()>0) 
					biSearchData.setName(strName);
				else 
					biSearchData.setName("");
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				PageList pageList = biBLManager.search(biSearchData, requiredPageNo, pageSize,staffData, ACTION_ALIAS);
				
				searchTemplateForm.setName(strName);
				searchTemplateForm.setPageNumber(pageList.getCurrentPage());
				searchTemplateForm.setTotalPages(pageList.getTotalPages());
				searchTemplateForm.setTotalRecords(pageList.getTotalItems());
				searchTemplateForm.setListSearchTemplate(pageList.getListData());
				searchTemplateForm.setAction(BaseConstant.LISTACTION);
				request.setAttribute("biTemplateForm", searchTemplateForm);
					
				return mapping.findForward(LIST_FORWARD);
			}catch(Exception managerExp){
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("search.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveErrors(request, messages);
			}
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("bitemplate.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);	
		    return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logWarn(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("bitemplate.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);		        
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
