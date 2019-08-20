/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SearchASMAction.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.sessionmanager; 
  
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ASMData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.IASMData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerDBConfiguration;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.sessionmanager.forms.SearchASMForm;
                                                                               
public class SearchASMAction extends BaseWebAction { 
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String LIST_FORWARD = "searchASM";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_ACTIVE_SESSION;
                                                                                   
	private static final String MODULE ="ASM";
	 
 
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){	
			try{
				
				SearchASMForm searchASMForm = (SearchASMForm)form;
				String strSmInstancId = request.getParameter("sminstanceid");
				Logger.logDebug(MODULE,"strSmInstancId: "+strSmInstancId);
				String sessionManagerId=null;
				if(strSmInstancId!=null){
					sessionManagerId = strSmInstancId;
				}else{
					sessionManagerId = searchASMForm.getSessionManagerId();
				}
				searchASMForm.setSessionManagerId(sessionManagerId);
				
				SessionManagerBLManager blManager = new SessionManagerBLManager();
				ISessionManagerInstanceData sessionManagerInstanceData = blManager.getSessionManagerDataById(sessionManagerId);
				Logger.logDebug(MODULE,"sessionManagerInstanceData: "+sessionManagerInstanceData);
				request.setAttribute("sessionManagerInstanceData",sessionManagerInstanceData);
				searchASMForm.setStatus("All");
				request.setAttribute("searchASMForm",searchASMForm);
				SessionManagerBLManager asmBLManager = new SessionManagerBLManager();
				SessionManagerDBConfiguration sessionManagementDBConfiguration = asmBLManager.getSessionManagerDBConfiguration(sessionManagerId);

				String strAction = searchASMForm.getAction();
				
				IASMData asmSearchData = new ASMData();
				String strUserName = searchASMForm.getUserName();
				String groupName  = searchASMForm.getGroupName();
				String nasIpAddress = searchASMForm.getNasIpAddress();
				String framedIpAddress = searchASMForm.getFramedIpAddress();
				String idleTime = searchASMForm.getIdleTime();
				String groupbyCriteria = searchASMForm.getGroupbyCriteria();
				
				if(groupbyCriteria == null)
					System.out.println("Null Value groupbyCriteria : "+groupbyCriteria);
				
				if(strUserName !=null && !(strUserName.equalsIgnoreCase(""))){
					asmSearchData.setUserName(strUserName);
				}
				else{
					asmSearchData.setUserName("");
				}
				if(groupName !=null && !(groupName.equalsIgnoreCase(""))){
					asmSearchData.setGroupName(groupName);
				}
				else{
					asmSearchData.setGroupName("");
				}
				if(nasIpAddress !=null && !(nasIpAddress.equalsIgnoreCase(""))){
					asmSearchData.setNasIpAddress(nasIpAddress);
				}
				else{
					asmSearchData.setNasIpAddress("");
				}
				if(framedIpAddress !=null && !(framedIpAddress.equalsIgnoreCase(""))){
					asmSearchData.setFramedIpAddress(framedIpAddress);
				}
				else{
					asmSearchData.setFramedIpAddress("");
				}
				if(idleTime !=null && !(idleTime.equalsIgnoreCase(""))){
					asmSearchData.setIdleTime(idleTime);
				}
				else{
					asmSearchData.setIdleTime("");
				}
				if(groupbyCriteria !=null && !(groupbyCriteria.equalsIgnoreCase(""))){
					asmSearchData.setGroupbyCriteria(groupbyCriteria);
				}
				else{
					asmSearchData.setGroupbyCriteria("");
				}
				
				
				System.out.println("criteria"+searchASMForm.getGroupbyCriteria());
				
				//For Audit
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				
				int requiredPageNo = Integer.parseInt(String.valueOf(searchASMForm.getPageNumber()));
				if(requiredPageNo == 0)
					requiredPageNo = 1;
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				
					if(!groupbyCriteria.equalsIgnoreCase("")){
						asmSearchData.setGroupbyCriteria(groupbyCriteria);
						PageList pageListGroupBy = asmBLManager.searchGroupByCriteria(asmSearchData,requiredPageNo,pageSize,staffData,actionAlias,sessionManagementDBConfiguration);
						searchASMForm.setUserName(strUserName);
						
						if(pageListGroupBy!=null){
						searchASMForm.setPageNumber(pageListGroupBy.getCurrentPage());
						searchASMForm.setTotalPages(pageListGroupBy.getTotalPages());
						searchASMForm.setTotalRecords(pageListGroupBy.getTotalItems());								
						// this is List without Group BY
						searchASMForm.setAsmListGroupBy(pageListGroupBy.getListData());
						
						}
					}
					else{
						searchASMForm.setGroupbyCriteria(groupbyCriteria);
						PageList pageList = asmBLManager.searchASM(asmSearchData,requiredPageNo,pageSize,sessionManagementDBConfiguration,staffData);
						
						searchASMForm.setUserName(strUserName);
						
						if(pageList!=null){
							searchASMForm.setPageNumber(pageList.getCurrentPage());
							searchASMForm.setTotalPages(pageList.getTotalPages());
							searchASMForm.setTotalRecords(pageList.getTotalItems());								
							// this is List without Group BY
							searchASMForm.setAsmList(pageList.getListData());
							
						}
					}
					searchASMForm.setAction(strAction);	
				return mapping.findForward(LIST_FORWARD);
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