package com.elitecore.elitesm.web.radius.policies.radiuspolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.policies.radiuspolicy.RadiusPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.SearchRadiusPolicyForm;

public class SearchRadiusPolicyAction extends BaseDictionaryAction{
	
	private static final String LIST_FORWARD = "radiusPolicySearchList";
    private static final String FAILURE_FORWARD = "failure";
    private static final String ACTION_ALIAS = ConfigConstant.SEARCH_RADIUS_POLICY_ACTION;

    public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception {
    	Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
    	
    	if(checkAccess(request, ACTION_ALIAS)){
	    	try{
	    		SearchRadiusPolicyForm searchRadiusPolicyForm=(SearchRadiusPolicyForm)actionForm;
	    		RadiusPolicyBLManager blManager = new RadiusPolicyBLManager();
	    		
	    		Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	    		
	    		int requiredPageNo;
	    		if(request.getParameter("pageNo") != null){
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		}else{
	    			requiredPageNo = new Long(searchRadiusPolicyForm.getPageNumber()).intValue();
	    		}
	    		if(requiredPageNo == 0)
	    			requiredPageNo =1;
	    		
	    		IRadiusPolicyData radiusPolicySearchData = new RadiusPolicyData();
	    		String strName=searchRadiusPolicyForm.getName();
	    		if(strName!=null)
	    			radiusPolicySearchData.setName("%"+strName+"%");
	    		else
	    			radiusPolicySearchData.setName("");
	    		
	    		
	    		//System.out.println("here the value of the commonstatusid is :"+searchRadiusPolicyForm.getStatus());
	    		if(request.getParameter("resultStatus")!= null){
	    			searchRadiusPolicyForm.setStatus(request.getParameter("resultStatus"));
	    		}
	    		if(searchRadiusPolicyForm.getStatus() != null){
		    		if(searchRadiusPolicyForm.getStatus().equalsIgnoreCase("Show"))
		    			radiusPolicySearchData.setCommonStatusId("CST01");
		    		else if(searchRadiusPolicyForm.getStatus().equalsIgnoreCase("Hide"))
		    			radiusPolicySearchData.setCommonStatusId("CST02");
	    		}
	    		
	    		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	    		
	    		PageList pageList = blManager.search(radiusPolicySearchData,staffData,requiredPageNo,pageSize);
	    		searchRadiusPolicyForm.setName(strName);
	    		searchRadiusPolicyForm.setPageNumber(pageList.getCurrentPage());
	    		searchRadiusPolicyForm.setTotalPages(pageList.getTotalPages());
	    		searchRadiusPolicyForm.setTotalRecords(pageList.getTotalItems());
	    		searchRadiusPolicyForm.setListRadiusPolicy(pageList.getListData());
	    		searchRadiusPolicyForm.setRadiusPolicyList(pageList.getCollectionData());
	    		searchRadiusPolicyForm.setAction(BaseConstant.LISTACTION);
	    		
	    		return mapping.findForward(LIST_FORWARD);
	    		
	    	}catch(Exception managerExp){
	    		managerExp.printStackTrace();
	    		Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());

	    		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
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
