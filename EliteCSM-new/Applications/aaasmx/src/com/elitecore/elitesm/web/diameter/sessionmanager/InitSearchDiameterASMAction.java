package com.elitecore.elitesm.web.diameter.sessionmanager; 
  
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.sqlexception.EliteSQLGrammerException;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.sessionmanager.form.SearchDiameterASMForm;
                                                                               
public class InitSearchDiameterASMAction extends BaseWebAction{
	                                                                       
	private static final String SEARCH_FORWARD = "searchDiameterASM";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_SESSION;
	private static final String INIT_SEARCH_FORWARD = "initSearchDiameterASM";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
			
				SearchDiameterASMForm searchDiameterASMForm = (SearchDiameterASMForm) form;
			
				String strSessionManagerId = request.getParameter("sessionManagerId");
				Logger.logDebug(MODULE," strSessionManagerId: "+strSessionManagerId);
			
				String sessionManagerId;
				if(Strings.isNullOrBlank(strSessionManagerId) == false){
					sessionManagerId = strSessionManagerId;
				}else{
					sessionManagerId = searchDiameterASMForm.getSessionManagerId();
				}
				searchDiameterASMForm.setSessionManagerId(sessionManagerId);
				searchDiameterASMForm.setStatus("All");
	
				DiameterSessionManagerBLManager blManager = new DiameterSessionManagerBLManager();
				DiameterSessionManagerData diameterSessionManagerData = blManager.getDiameterSessionManagerDataById(sessionManagerId);
				searchDiameterASMForm.setSearchColumns(diameterSessionManagerData.getViewableColumns());
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				
				int requiredPageNo = Integer.parseInt(String.valueOf(searchDiameterASMForm.getPageNumber()));
				if(requiredPageNo == 0)
					requiredPageNo = 1;
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				
				if( searchDiameterASMForm.getSearchColumns() != null && searchDiameterASMForm.getSearchColumns().length() > 0){
					
					PageList customPageList  =  blManager.getASMDataByColumnName(searchDiameterASMForm.getSearchColumns(), diameterSessionManagerData.getTableName() ,requiredPageNo, pageSize, staffData, actionAlias);
					
					if( customPageList != null ){
					
						searchDiameterASMForm.setPageNumber(customPageList.getCurrentPage());
						searchDiameterASMForm.setTotalPages(customPageList.getTotalPages());
						searchDiameterASMForm.setTotalRecords(customPageList.getTotalItems());								
						searchDiameterASMForm.setAsmList(customPageList.getListData());
					}
				}
				searchDiameterASMForm.setAction(BaseConstant.LISTACTION);
				request.setAttribute("diameterSessionManagerData",diameterSessionManagerData);
				request.setAttribute("searchDiameterASMForm", searchDiameterASMForm);
				return mapping.findForward(SEARCH_FORWARD);
			}catch(EliteSQLGrammerException eliteException){
				try{
					String sessionManagerId;
					String strSessionManagerId = request.getParameter("sessionManagerId");
					SearchDiameterASMForm searchDiameterASMForm = (SearchDiameterASMForm) form;
					if(Strings.isNullOrBlank(strSessionManagerId) == false){
						sessionManagerId = strSessionManagerId;
					}else{
						sessionManagerId = searchDiameterASMForm.getSessionManagerId();
					}
					DiameterSessionManagerBLManager blManager = new DiameterSessionManagerBLManager();
					blManager.resetViewableColumnsValue("", sessionManagerId);
					return mapping.findForward(INIT_SEARCH_FORWARD);
				}catch(Exception e){
					Logger.logError(MODULE,"Error during operation, reason : "+ e.getMessage());
					Logger.logTrace(MODULE, e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
				    ActionMessage message = new ActionMessage("search.asm.operation.failure");
				    ActionMessages messages = new ActionMessages();
				    messages.add("information", message);
				    saveErrors(request, messages);
				}
			}catch(DataManagerException managerExp){
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
				Logger.logTrace(MODULE, managerExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
			    ActionMessage message = new ActionMessage("search.asm.operation.failure");
			    ActionMessages messages = new ActionMessages();
			    messages.add("information", message);
			    saveErrors(request, messages);
	
			}catch(Exception managerExp){
				Logger.logError(MODULE,"Error during operation, reason : "+ managerExp.getMessage());
				Logger.logTrace(MODULE, managerExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
			    ActionMessage message = new ActionMessage("search.asm.operation.failure");
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
		 