package com.elitecore.elitesm.web.servermgr.eap;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.eap.forms.MiscEAPConfigForm;

public class MiscEAPConfigAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";	
	private static final String LIST_FORWARD = "list";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_EAP_CONFIGURATION;
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_EAP_CONFIGURATION;

	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());

		if((checkAccess(request, ACTION_ALIAS)) || (checkAccess(request,ACTION_ALIAS_DELETE))){
			try{

				MiscEAPConfigForm miscEAPConfigForm = (MiscEAPConfigForm)actionForm;
				EAPConfigBLManager eapcConfigBLManager = new EAPConfigBLManager();

				if(miscEAPConfigForm.getAction() != null){
					String[] strSelectedIds = request.getParameterValues("select");
					if(strSelectedIds != null){
						Date currDate = new Date(); 
						if(miscEAPConfigForm.getAction().equalsIgnoreCase("delete")){
							checkActionPermission(request, ACTION_ALIAS_DELETE);
							IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

							eapcConfigBLManager.deleteById(Arrays.asList(strSelectedIds),staffData);
							int strSelectedIdsLen = strSelectedIds.length;
							long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,miscEAPConfigForm.getPageNumber(),miscEAPConfigForm.getTotalPages(),miscEAPConfigForm.getTotalRecords());

							request.setAttribute("responseUrl","/searchEAPConfig.do?name="+miscEAPConfigForm.getName()+"&md5="+miscEAPConfigForm.getMd5()+"&mschapv2="+miscEAPConfigForm.getMschapv2()+"&tls="+miscEAPConfigForm.getTls()+"&gtc="+miscEAPConfigForm.getGtc()+"&ttls="+miscEAPConfigForm.getTtls()+"&sim="+miscEAPConfigForm.getSim()+"&aka="+miscEAPConfigForm.getSim()+"&akaPrime="+miscEAPConfigForm.getSim()+"&pageNumber="+currentPageNumber+"&totalPages="+miscEAPConfigForm.getTotalPages()+"&totalRecords="+miscEAPConfigForm.getTotalRecords());
							ActionMessage message = new ActionMessage("servermgr.eap.delete.success");
							ActionMessages messages1 = new ActionMessages();
							messages1.add("information",message);
							saveMessages(request,messages1);


							return mapping.findForward(SUCCESS_FORWARD);
						}
					}
				}

				return mapping.findForward(LIST_FORWARD);

			}catch(ActionNotPermitedException e){
				Logger.logError(MODULE,"Error :-" + e.getMessage());
				printPermitedActionAlias(request);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("general.user.restricted"));
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}catch (DataManagerException managerExp) {

				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				if(managerExp.getCause() instanceof com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException){
					ActionMessage message = new ActionMessage("servermgr.eap.delete.failure");
					ActionMessage messageReason = new ActionMessage("servermgr.eap.delete.constraint.failure");
					messages.add("information", message);
					messages.add("information", messageReason);
					saveErrors(request, messages);
				}else{
					ActionMessage message = new ActionMessage("general.error");
					messages.add("information", message);
					saveErrors(request, messages);
				}



			}catch(Exception managerExp){
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
