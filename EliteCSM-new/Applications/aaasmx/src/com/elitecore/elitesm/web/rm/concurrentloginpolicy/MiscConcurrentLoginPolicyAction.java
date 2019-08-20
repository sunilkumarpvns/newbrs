package com.elitecore.elitesm.web.rm.concurrentloginpolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.rm.concurrentloginpolicy.ConcurrentLoginPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyData;
import com.elitecore.elitesm.util.constants.ConcurrentLoginPolicyConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.MiscConcurrentLoginPolicyForm;
import com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.SearchConcurrentLoginPolicyForm;

public class MiscConcurrentLoginPolicyAction extends BaseDictionaryAction{
	private static final String MODULE = "MISC. CONC. LOGIN POLICY ACTION";
	private static final String  LIST_FORWARD="searchConc";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CHANGE_CONCURRENT_LOGIN_POLICY_STATUS_ACTION;
	private static final String ACTION_ALIAS_DELETE =ConfigConstant.DELETE_CONCURRENT_LOGIN_POLICY_ACTION;
	private static final String SUCCESS_FORWARD = "success";
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());

		if((checkAccess(request, ACTION_ALIAS)) || (checkAccess(request,ACTION_ALIAS_DELETE))){
			try {
				MiscConcurrentLoginPolicyForm miscConcurrentLoginPolicyForm = (MiscConcurrentLoginPolicyForm)actionForm;
				ConcurrentLoginPolicyBLManager concurrentLoginPolicyBLManager = new ConcurrentLoginPolicyBLManager();
				if(miscConcurrentLoginPolicyForm.getAction() != null){
					String[] strSelectedIds = request.getParameterValues("select");
					if(strSelectedIds!=null)
						System.out.println("strSelectedIds array:"+ strSelectedIds.toString());
					if(strSelectedIds != null){
						List<String> selectedIDList = new ArrayList<String>();
	                    for (int i = 0; i < strSelectedIds.length; i++) {
	                    	selectedIDList.add(strSelectedIds[i]);
						}
						
						if(miscConcurrentLoginPolicyForm.getAction().equalsIgnoreCase("show")){
							checkActionPermission(request, ACTION_ALIAS);

							IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
							String actionAlias = ACTION_ALIAS;
							
							staffData.setAuditId(miscConcurrentLoginPolicyForm.getAuditUId());
							staffData.setAuditName(miscConcurrentLoginPolicyForm.getName());
							
							IConcurrentLoginPolicyData concurrentLoginPolicyData = new ConcurrentLoginPolicyData();
							
							concurrentLoginPolicyBLManager.updateStatus(concurrentLoginPolicyData, selectedIDList, ConcurrentLoginPolicyConstant.SHOW_STATUS_ID,"show",staffData,actionAlias);
						}else if(miscConcurrentLoginPolicyForm.getAction().equalsIgnoreCase("hide")){
							checkActionPermission(request, ACTION_ALIAS);
							IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
							String actionAlias = ACTION_ALIAS;
							
							staffData.setAuditId(miscConcurrentLoginPolicyForm.getAuditUId());
							staffData.setAuditName(miscConcurrentLoginPolicyForm.getName());

							IConcurrentLoginPolicyData concurrentLoginPolicyData = new ConcurrentLoginPolicyData();
							
							concurrentLoginPolicyBLManager.updateStatus(concurrentLoginPolicyData, selectedIDList, ConcurrentLoginPolicyConstant.HIDE_STATUS_ID,"hide",staffData,actionAlias);
						}else if (miscConcurrentLoginPolicyForm.getAction().equalsIgnoreCase("delete")){
							checkActionPermission(request, ACTION_ALIAS_DELETE);
							IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

							concurrentLoginPolicyBLManager.deleteConcurrentLoginPolicyById(Arrays.asList(strSelectedIds),staffData);
							
							int strSelectedIdsLen = strSelectedIds.length;
	                        long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen, miscConcurrentLoginPolicyForm.getPageNumber(), miscConcurrentLoginPolicyForm.getTotalPages(), miscConcurrentLoginPolicyForm.getTotalRecords());
	                   
							request.setAttribute("responseUrl","/searchConcurrentLoginPolicy.do?name="+miscConcurrentLoginPolicyForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+miscConcurrentLoginPolicyForm.getTotalPages()+"&totalRecords="+miscConcurrentLoginPolicyForm.getTotalRecords()+"&status="+miscConcurrentLoginPolicyForm.getStatus()+"&concurrentLoginPolicyId="+miscConcurrentLoginPolicyForm.getConcurrentLoginPolicyId());
							ActionMessage message = new ActionMessage("rm.concurrent.login.policy.delete.success");
							ActionMessages messages1 = new ActionMessages();
							messages1.add("information",message);
							saveMessages(request,messages1);

							return mapping.findForward(SUCCESS_FORWARD);

						}
					}
				}

				SearchConcurrentLoginPolicyForm searchConcurrentLoginPolicyForm = new SearchConcurrentLoginPolicyForm();
				searchConcurrentLoginPolicyForm.setName(miscConcurrentLoginPolicyForm.getName());
				searchConcurrentLoginPolicyForm.setPageNumber(miscConcurrentLoginPolicyForm.getPageNumber());
				searchConcurrentLoginPolicyForm.setTotalPages(miscConcurrentLoginPolicyForm.getTotalPages());
				searchConcurrentLoginPolicyForm.setTotalRecords(miscConcurrentLoginPolicyForm.getTotalRecords());
				searchConcurrentLoginPolicyForm.setStatus(miscConcurrentLoginPolicyForm.getStatus());
				searchConcurrentLoginPolicyForm.setConcurrentLoginPolicyId(miscConcurrentLoginPolicyForm.getConcurrentLoginPolicyId());
				searchConcurrentLoginPolicyForm.setAction(ConcurrentLoginPolicyConstant.LISTACTION);
				searchConcurrentLoginPolicyForm.setAuditUId(miscConcurrentLoginPolicyForm.getAuditUId());
				request.setAttribute("searchConcurrentLoginPolicyForm",searchConcurrentLoginPolicyForm);
				return mapping.findForward(LIST_FORWARD);


			} catch(ActionNotPermitedException e){
                Logger.logError(MODULE,"Error :-" + e.getMessage());
                printPermitedActionAlias(request);
                ActionMessages messages = new ActionMessages();
                messages.add("information", new ActionMessage("general.user.restricted"));
                saveErrors(request, messages);
                return mapping.findForward(INVALID_ACCESS_FORWARD);
            }catch(DataManagerException managerExp){
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				if(managerExp.getMessage()!=null && managerExp.getMessage().equalsIgnoreCase("Cannot open connection")){
		            ActionMessage message = new ActionMessage("datasource.exception");
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", message);
		            saveErrors(request, messages);
				}else{
					ActionMessage message = new ActionMessage("concurrentloginpolicy.delete.failuremessage2");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveErrors(request, messages);
				}
				
				
			}catch (Exception managerExp){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("concurrentloginpolicy.delete.failuremessage1");
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
