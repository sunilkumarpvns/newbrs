package com.elitecore.netvertexsm.web.datasource.esiradius;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.EsiRadiusBLManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.esiradius.data.EsiRadiusData;
import com.elitecore.netvertexsm.datamanager.datasource.esiradius.data.IEsiRadiusData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.datasource.esiradius.form.SearchEsiRadiusForm;

public class SearchEsiRadiusAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String LIST_FORWARD = "searchEsiRadius";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_ESIRADIUS_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				SearchEsiRadiusForm searchEsiRadiusForm = (SearchEsiRadiusForm)actionForm;
				EsiRadiusBLManager esiRadiusBLManager = new EsiRadiusBLManager();
				IEsiRadiusData esiRadiusData = new EsiRadiusData();
				
				int requiredPageNo = Integer.parseInt(String.valueOf(searchEsiRadiusForm.getPageNumber()));
				if(requiredPageNo == 0)
					requiredPageNo=1;												
				
				String strName = searchEsiRadiusForm.getName();					
				
				if(strName!=null && strName.length()>0) {
					esiRadiusData.setName(searchEsiRadiusForm.getName());
				}else {
					esiRadiusData.setName("");
				}																
									
				String actionAlias = ACTION_ALIAS;
				PageList pageList = esiRadiusBLManager.search(esiRadiusData, requiredPageNo, 10, actionAlias);
				
				searchEsiRadiusForm.setName(strName);
				searchEsiRadiusForm.setPageNumber(pageList.getCurrentPage());
				searchEsiRadiusForm.setTotalPages(pageList.getTotalPages());
				searchEsiRadiusForm.setTotalRecords(pageList.getTotalItems());
				searchEsiRadiusForm.setListSearchEsiRadius(pageList.getListData());
				searchEsiRadiusForm.setAction(BaseConstant.LISTACTION);
				request.setAttribute("searchEsiRadiusForm", searchEsiRadiusForm);
					
				return mapping.findForward(LIST_FORWARD);
			}catch(Exception managerExp){
					
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("staff.search.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveErrors(request, messages);
			}
		    return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logWarn(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
