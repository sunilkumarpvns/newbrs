/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SearchASMAction.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.sessionmanager; 
  
import java.util.Arrays;

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
                                                                               
public class SearchDiameterASMAction extends BaseWebAction { 
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String LIST_FORWARD = "searchDiameterASM";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_SESSION;
	private static final String MODULE ="SearchDiameterASMAction";
	 
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){	
			try{
				
				SearchDiameterASMForm searchDiameterASMForm = (SearchDiameterASMForm) form;
				String strSessionManagerId = request.getParameter("sessionManagerId");
				
				Logger.logDebug(MODULE,"sessionManagerId: "+strSessionManagerId);
				
				String sessionManagerId="";
				if(Strings.isNullOrBlank(strSessionManagerId) == false){
					sessionManagerId = strSessionManagerId;
				}else{
					sessionManagerId = searchDiameterASMForm.getSessionManagerId();
				}
				
				searchDiameterASMForm.setSessionManagerId(sessionManagerId);
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				
				int requiredPageNo = Integer.parseInt(String.valueOf(searchDiameterASMForm.getPageNumber()));
				if(requiredPageNo == 0)
					requiredPageNo = 1;
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				
				DiameterSessionManagerBLManager blManager = new DiameterSessionManagerBLManager();
				DiameterSessionManagerData diameterSessionManagerData = blManager.getDiameterSessionManagerDataById(sessionManagerId);
			
				String tableName = "TBLMDIAMETERSESSIONDATA";
				String searchColumnName = searchDiameterASMForm.getSearchColumns();
				String strAction = searchDiameterASMForm.getAction();
				
				if (diameterSessionManagerData.getTableName() != null){
					tableName =  diameterSessionManagerData.getTableName();
				}
				
				if(strAction != null && strAction.equalsIgnoreCase("closeSelectedSession")){
					
					String[] sessionIds = request.getParameterValues("select");
					blManager.closeSelectedSession(Arrays.asList(sessionIds), tableName);
					
					int strSelectedIdsLen = sessionIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,searchDiameterASMForm.getPageNumber(),searchDiameterASMForm.getTotalPages(),searchDiameterASMForm.getTotalRecords());

					searchDiameterASMForm.setAction("list");

					request.setAttribute("responseUrl","/searchDiameterASM.do?sessionManagerId="+searchDiameterASMForm.getSessionManagerId()+"&pageNumber="+currentPageNumber+"&totalPages="+searchDiameterASMForm.getTotalPages()+"&totalRecords="+searchDiameterASMForm.getTotalRecords()+"&searchColumns="+searchDiameterASMForm.getSearchColumns());
					ActionMessage message = new ActionMessage("diameter.asm.close.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);
				}else if( strAction != null && strAction.equalsIgnoreCase("closeAllSession")){
					String[] sessionIds = request.getParameterValues("select");

					blManager.closeSelectedSession(Arrays.asList(sessionIds), tableName);
					
					int strSelectedIdsLen = sessionIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,searchDiameterASMForm.getPageNumber(),searchDiameterASMForm.getTotalPages(),searchDiameterASMForm.getTotalRecords());

					searchDiameterASMForm.setAction("list");

					request.setAttribute("responseUrl","/searchDiameterASM.do?sessionManagerId="+searchDiameterASMForm.getSessionManagerId()+"&pageNumber="+currentPageNumber+"&totalPages="+searchDiameterASMForm.getTotalPages()+"&totalRecords="+searchDiameterASMForm.getTotalRecords()+"&searchColumns="+searchDiameterASMForm.getSearchColumns());
					ActionMessage message = new ActionMessage("diameter.asm.close.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);
				}
				
				Logger.logInfo(MODULE, "Search Column name is  : " +searchColumnName);
				if( searchColumnName != null && searchColumnName.length() > 0){
					
					Logger.logInfo(MODULE, "Reset Viewable Columns value : " + searchColumnName);
					blManager.resetViewableColumnsValue(searchColumnName, sessionManagerId);
					
					PageList customPageList  =  blManager.getASMDataByColumnName(searchColumnName, tableName ,requiredPageNo, pageSize, staffData, actionAlias);
					
					if( customPageList != null ){
					
						searchDiameterASMForm.setPageNumber(customPageList.getCurrentPage());
						searchDiameterASMForm.setTotalPages(customPageList.getTotalPages());
						searchDiameterASMForm.setTotalRecords(customPageList.getTotalItems());								
						searchDiameterASMForm.setAsmList(customPageList.getListData());
					}
				}
				
				searchDiameterASMForm.setAction(BaseConstant.LISTACTION);
				searchDiameterASMForm.setSearchColumns(searchColumnName);
				Logger.logDebug(MODULE,"diameterSessionManagerData: "+diameterSessionManagerData);
				request.setAttribute("diameterSessionManagerData",diameterSessionManagerData);
				request.setAttribute("searchDiameterASMForm",searchDiameterASMForm);
				
				return mapping.findForward(LIST_FORWARD);
			}catch(EliteSQLGrammerException eliteException){
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ eliteException.getMessage());
				Logger.logTrace(MODULE, eliteException);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(eliteException);
				request.setAttribute("errorDetails", errorElements);
		        ActionMessage message = new ActionMessage("diameter.asm.invalidcolumns");
		        ActionMessages messages = new ActionMessages();
		        messages.add("information", message);
		        saveErrors(request, messages);
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