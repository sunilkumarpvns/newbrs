package com.elitecore.elitesm.web.diameter.sessionmanager;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.sessionmanager.form.SearchDiameterSessionManagerForm;

public class SearchDiameterSessionManagerAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "SEARCH DIAMETER SESSION ACTION";
	private static final String LIST_FORWARD = "searchSessionManagerList";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DIAMETER_SESSION_MANAGER;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
		try{
		    checkActionPermission(request, ACTION_ALIAS);
		    SearchDiameterSessionManagerForm searchDiameterSessionManagerForm = (SearchDiameterSessionManagerForm)form;
			DiameterSessionManagerBLManager blManager = new DiameterSessionManagerBLManager();
			DiameterSessionManagerData sessionManagerData = new DiameterSessionManagerData();
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			List<IDatabaseDSData> databaseDSData =databaseDSBLManager.getDatabaseDSList();
			searchDiameterSessionManagerForm.setDatabaseDSDataList(databaseDSData);
			
			String[] sessionManagerIds = request.getParameterValues("select");
			
			int requiredPageNo;
            if(request.getParameter("pageNo") != null){
    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
    		}else{
    			requiredPageNo = new Long(searchDiameterSessionManagerForm.getPageNumber()).intValue();
    		}
			if(requiredPageNo == 0)
				requiredPageNo = 1;
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(searchDiameterSessionManagerForm.getAction() != null){

				if(searchDiameterSessionManagerForm.getAction().equals("delete")){
					String actionAlias = ConfigConstant.DELETE_DIAMETER_SESSION_MANAGER;
					
					checkAccess(request,actionAlias);
					blManager.deleteDiameterSessionManagerByID(Arrays.asList(sessionManagerIds),staffData);
					int strSelectedIdsLen = sessionManagerIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,searchDiameterSessionManagerForm.getPageNumber(),searchDiameterSessionManagerForm.getTotalPages(),searchDiameterSessionManagerForm.getTotalRecords());

					searchDiameterSessionManagerForm.setAction("list");

					request.setAttribute("responseUrl","/searchDiameterSessionManager.do?name="+searchDiameterSessionManagerForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+searchDiameterSessionManagerForm.getTotalPages()+"&totalRecords="+searchDiameterSessionManagerForm.getTotalRecords());
					ActionMessage message = new ActionMessage("diametersessionmanager.delete.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);
				}
			}
			
			String strName = searchDiameterSessionManagerForm.getName();
			Logger.logInfo(MODULE,"Name :"+strName);
			
			if(strName !=null)
				sessionManagerData.setName("%"+strName+"%");
			else
				sessionManagerData.setName("");
			
			String actionAlias = ACTION_ALIAS;
			
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			Logger.logDebug(MODULE, "PAGENO IS:"+requiredPageNo+"");
			
			PageList pageList = blManager.search(sessionManagerData,requiredPageNo,pageSize,staffData);
			searchDiameterSessionManagerForm.setName(strName);
			searchDiameterSessionManagerForm.setPageNumber(pageList.getCurrentPage());
			searchDiameterSessionManagerForm.setTotalPages(pageList.getTotalPages());
			searchDiameterSessionManagerForm.setTotalRecords(pageList.getTotalItems());
			searchDiameterSessionManagerForm.setListSessionManager(pageList.getListData());
			
			searchDiameterSessionManagerForm.setAction("list");
			request.setAttribute("searchDiameterSessionManagerForm", searchDiameterSessionManagerForm);
			
			return mapping.findForward(LIST_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Error List Display operation , reason : " + e.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
		}
		Logger.logTrace(MODULE, "Returning Error Forward From :" + getClass().getName());
		ActionMessage message = new ActionMessage("diametersessionmanager.list.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD); 
	}
}
