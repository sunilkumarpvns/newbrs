package com.elitecore.elitesm.web.radius.radiuspolicygroup;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.radiuspolicygroup.RadiusPolicyGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.radiuspolicygroup.forms.RadiusPolicyGroupForm;

public class SearchRadiusPolicyGroupAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "success";	
	private static final String LIST_FORWARD = "radiusPolicySearchList";
    private static final String FAILURE_FORWARD = "failure";
    private static final String ACTION_ALIAS = ConfigConstant.SEARCH_RADIUS_POLICY_GROUP;

    public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception {
    	Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
    	String actionMessage="radiuspolicygroup.search.failure";
    	
    	if(checkAccess(request, ACTION_ALIAS)){
    		try{
    			RadiusPolicyGroupForm radiusPolicyGroupForm = (RadiusPolicyGroupForm)actionForm;
	    		RadiusPolicyGroupBLManager blManager = new RadiusPolicyGroupBLManager();
	    		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	    		
	    		String[] policyIds = request.getParameterValues("select");
	    		
	    		Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	    		
	    		int requiredPageNo;
	    		if(request.getParameter("pageNo") != null){
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		}else{
	    			requiredPageNo = new Long(radiusPolicyGroupForm.getPageNumber()).intValue();
	    		}
	    		if(requiredPageNo == 0)
	    			requiredPageNo =1;
	    		
	    		if(radiusPolicyGroupForm.getAction() != null){

					if(radiusPolicyGroupForm.getAction().equals("delete")){
						actionMessage="radiuspolicygroup.delete.failure";
						String actionAlias = ConfigConstant.DELETE_RADIUS_POLICY_GROUP;
						
						checkAccess(request,actionAlias);
						
						blManager.deleteRadiusPolicyGroupById(Arrays.asList(policyIds), staffData);
						
						doAuditing(staffData, actionAlias);
						
						int strSelectedIdsLen = policyIds.length;
						long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,radiusPolicyGroupForm.getPageNumber(),radiusPolicyGroupForm.getTotalPages(),radiusPolicyGroupForm.getTotalRecords());

						radiusPolicyGroupForm.setAction("list");

						request.setAttribute("responseUrl","/searchRadiusPolicyGroup.do?policyname="+radiusPolicyGroupForm.getPolicyname()+"&pageNumber="+currentPageNumber+"&totalPages="+radiusPolicyGroupForm.getTotalPages()+"&totalRecords="+radiusPolicyGroupForm.getTotalRecords());
						ActionMessage message = new ActionMessage("radiuspolicygroup.delete.success");
						ActionMessages messages1 = new ActionMessages();
						messages1.add("information",message);
						saveMessages(request,messages1);
						return mapping.findForward(SUCCESS_FORWARD);
					}
				}
	    		
	    		RadiusPolicyGroup radiusPolicyGroup = new  RadiusPolicyGroup();
	    		
	    		String strName=radiusPolicyGroupForm.getPolicyname();
	    		if(strName!=null)
	    			radiusPolicyGroup.setPolicyName("%"+strName+"%");
	    		else
	    			radiusPolicyGroup.setPolicyName("");
	    	
				PageList pageList = blManager.search(radiusPolicyGroup, requiredPageNo, pageSize, staffData);

	    		radiusPolicyGroupForm.setPolicyname(strName);
	    		radiusPolicyGroupForm.setPageNumber(pageList.getCurrentPage());
	    		radiusPolicyGroupForm.setTotalPages(pageList.getTotalPages());
	    		radiusPolicyGroupForm.setTotalRecords(pageList.getTotalItems());
	    		radiusPolicyGroupForm.setListRadiusPolicyGroup(pageList.getListData());
	    		radiusPolicyGroupForm.setRadiusPolicyGroupList(pageList.getCollectionData());
	    		radiusPolicyGroupForm.setAction(BaseConstant.LISTACTION);
	    		
	    		return mapping.findForward(LIST_FORWARD);
	    		
	    	}catch(ActionNotPermitedException e){
				Logger.logError(MODULE,"Error :-" + e.getMessage());
				printPermitedActionAlias(request);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("general.user.restricted"));
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}catch(DataManagerException e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage(actionMessage);
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}catch(Exception e){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.error");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
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
