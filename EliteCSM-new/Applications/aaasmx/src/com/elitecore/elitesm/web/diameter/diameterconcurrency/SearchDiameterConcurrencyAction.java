package com.elitecore.elitesm.web.diameter.diameterconcurrency;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterconcurrency.DiameterConcurrencyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.diameterconcurrency.form.DiameterConcurrencyForm;

public class SearchDiameterConcurrencyAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "success";	
	private static final String FAILURE_FORWARD = "failure";
	private static final String LIST_FORWARD = "listDiameterConcurrency";
    private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DIAMETER_CONCURRENCY;
    private static final String MODULE = ConfigConstant.DIAMETER_CONCURRENCY;
    
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
    	Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
    	String actionMessage="diameterconcurrency.search.failure";
    	
    	if(checkAccess(request, ACTION_ALIAS)){
    		try{
    			DiameterConcurrencyForm diameterConcurrencyForm = (DiameterConcurrencyForm)form;
    			DiameterConcurrencyBLManager blManager = new  DiameterConcurrencyBLManager();
	    		
    			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	    		Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	    		String[] policyIds = request.getParameterValues("select");
	    		
	    		int requiredPageNo;
	    		if(request.getParameter("pageNo") != null){
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		}else{
	    			requiredPageNo = new Long(diameterConcurrencyForm.getPageNumber()).intValue();
	    		}
	    		if(requiredPageNo == 0)
	    			requiredPageNo =1;
	    		
	    		if(diameterConcurrencyForm.getAction() != null){

					if(diameterConcurrencyForm.getAction().equals("delete")){
						actionMessage="diameterconcurrency.delete.failure";
						String actionAlias = ConfigConstant.DELETE_DIAMETER_CONCURRENCY;
						
						checkAccess(request,actionAlias);
						blManager.deleteById(Arrays.asList(policyIds),staffData);
						
						int strSelectedIdsLen = policyIds.length;
						long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,diameterConcurrencyForm.getPageNumber(),diameterConcurrencyForm.getTotalPages(),diameterConcurrencyForm.getTotalRecords());

						diameterConcurrencyForm.setAction("list");

						request.setAttribute("responseUrl","/searchDiameterConcurrency.do?name="+diameterConcurrencyForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+diameterConcurrencyForm.getTotalPages()+"&totalRecords="+diameterConcurrencyForm.getTotalRecords());
						ActionMessage message = new ActionMessage("diameterconcurrency.delete.success");
						ActionMessages messages1 = new ActionMessages();
						messages1.add("information",message);
						saveMessages(request,messages1);
						return mapping.findForward(SUCCESS_FORWARD);
					}
				}
	    		
	    		DiameterConcurrencyData diameterConcurrencyData = new DiameterConcurrencyData();
	    		
	    		String strName=diameterConcurrencyForm.getName();
	    		if(strName!=null)
	    			diameterConcurrencyData.setName("%"+strName+"%");
	    		else
	    			diameterConcurrencyData.setName("");
	    		
	    		PageList pageList = blManager.search(diameterConcurrencyData,requiredPageNo,pageSize,staffData);
	    		doAuditing(staffData, ACTION_ALIAS);
	    		diameterConcurrencyForm.setName(strName);
	    		diameterConcurrencyForm.setPageNumber(pageList.getCurrentPage());
	    		diameterConcurrencyForm.setTotalPages(pageList.getTotalPages());
	    		diameterConcurrencyForm.setTotalRecords(pageList.getTotalItems());
	    		diameterConcurrencyForm.setListDiameterConcurrencyGroup(pageList.getListData());
	    		diameterConcurrencyForm.setDiameterConcurrencyList(pageList.getCollectionData());
	    		diameterConcurrencyForm.setAction(BaseConstant.LISTACTION);
	    		
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
