package com.elitecore.elitesm.web.diameter.diameterpolicygroup;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterpolicygroup.DiameterPolicyGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.data.DiameterPolicyGroup;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.diameterpolicygroup.forms.DiameterPolicyGroupForm;

public class SearchDiameterPolicyGroupAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "success";	
	private static final String LIST_FORWARD = "diameterPolicySearchList";
    private static final String FAILURE_FORWARD = "failure";
    private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DIAMETER_POLICY_GROUP;

    public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception {
    	Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
    	String actionMessage="diameterpolicygroup.search.failure";
    	
    	if(checkAccess(request, ACTION_ALIAS)){
    		try{
    			DiameterPolicyGroupForm diameterPolicyGroupForm = (DiameterPolicyGroupForm)actionForm;
    			DiameterPolicyGroupBLManager blManager = new  DiameterPolicyGroupBLManager();
	    		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	    		
	    		String[] policyIds = request.getParameterValues("select");
	    		
	    		Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	    		
	    		int requiredPageNo;
	    		if(request.getParameter("pageNo") != null){
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		}else{
	    			requiredPageNo = new Long(diameterPolicyGroupForm.getPageNumber()).intValue();
	    		}
	    		if(requiredPageNo == 0)
	    			requiredPageNo =1;
	    		
	    		if(diameterPolicyGroupForm.getAction() != null){

					if(diameterPolicyGroupForm.getAction().equals("delete")){
						actionMessage="diameterpolicygroup.delete.failure";
						String actionAlias = ConfigConstant.DELETE_DIAMETER_POLICY_GROUP;
						
						checkAccess(request,actionAlias);
						blManager.deleteDiameterPolicyGroupById(Arrays.asList(policyIds),staffData);
						
						int strSelectedIdsLen = policyIds.length;
						long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,diameterPolicyGroupForm.getPageNumber(),diameterPolicyGroupForm.getTotalPages(),diameterPolicyGroupForm.getTotalRecords());

						diameterPolicyGroupForm.setAction("list");

						request.setAttribute("responseUrl","/searchDiameterPolicyGroup.do?policyname="+diameterPolicyGroupForm.getPolicyname()+"&pageNumber="+currentPageNumber+"&totalPages="+diameterPolicyGroupForm.getTotalPages()+"&totalRecords="+diameterPolicyGroupForm.getTotalRecords());
						ActionMessage message = new ActionMessage("diameterpolicygroup.delete.success");
						ActionMessages messages1 = new ActionMessages();
						messages1.add("information",message);
						saveMessages(request,messages1);
						return mapping.findForward(SUCCESS_FORWARD);
					}
				}
	    		
	    		DiameterPolicyGroup diameterPolicyGroup = new DiameterPolicyGroup();
	    		
	    		String strName=diameterPolicyGroupForm.getPolicyname();
	    		if(strName!=null)
	    			diameterPolicyGroup.setPolicyName("%"+strName+"%");
	    		else
	    			diameterPolicyGroup.setPolicyName("");
	    		
	    		PageList pageList = blManager.search(diameterPolicyGroup,requiredPageNo,pageSize,staffData);

	    		diameterPolicyGroupForm.setPolicyname(strName);
	    		diameterPolicyGroupForm.setPageNumber(pageList.getCurrentPage());
	    		diameterPolicyGroupForm.setTotalPages(pageList.getTotalPages());
	    		diameterPolicyGroupForm.setTotalRecords(pageList.getTotalItems());
	    		diameterPolicyGroupForm.setListDiameterPolicyGroup(pageList.getListData());
	    		diameterPolicyGroupForm.setDiameterPolicyGroupList(pageList.getCollectionData());
	    		diameterPolicyGroupForm.setAction(BaseConstant.LISTACTION);
	    		
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
