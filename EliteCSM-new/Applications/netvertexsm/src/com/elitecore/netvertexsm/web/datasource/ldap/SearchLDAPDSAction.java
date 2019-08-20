package com.elitecore.netvertexsm.web.datasource.ldap;

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

import com.elitecore.netvertexsm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ReferenceFoundException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.ILDAPBaseDnDetailData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.ILDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.LDAPDSConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.datasource.ldap.form.SearchLDAPDatasourceForm;

public class SearchLDAPDSAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "searchSuccess";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String MODULE = "SEARCH LDAPDS ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_LDAP_DATASOURCE;
	private static final String ACTION_ALIAS_DELETE= ConfigConstant.DELETE_LDAP_DATASOURCE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS) || checkAccess(request,ACTION_ALIAS_DELETE)){
			Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());

			SearchLDAPDatasourceForm searchLDAPDatasourceForm = (SearchLDAPDatasourceForm)form;
			ILDAPDatasourceData ldapDatasourceData = new LDAPDatasourceData();
			ILDAPBaseDnDetailData ldapBaseDnDetailData = new LDAPBaseDnDetailData();
			List listOfSearchData = new ArrayList();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			ldapDatasourceData.setName(searchLDAPDatasourceForm.getName());
			LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();

			String []ldapDSIds = request.getParameterValues("select");
			String errorheading="searching";
			try {
				
				if(searchLDAPDatasourceForm.getAction().equals("delete")){
					errorheading="deleting";
					ldapDatasourceBLManager.delete(Arrays.asList(ldapDSIds),staffData,ACTION_ALIAS_DELETE);
					request.setAttribute("responseUrl", "/initSearchLDAPDS");
					ActionMessage message = new ActionMessage("datasource.ldap.delete.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);					
				}
				
				listOfSearchData = ldapDatasourceBLManager.search(ldapDatasourceData,staffData,ACTION_ALIAS);
				request.getSession().setAttribute("searchLDAPList",listOfSearchData );	
				searchLDAPDatasourceForm.setAction(LDAPDSConstant.LISTACTION);			
				request.getSession().setAttribute("searchLDAPDSForm",searchLDAPDatasourceForm);				
				
				return mapping.findForward(SUCCESS_FORWARD);

			} catch (ReferenceFoundException e) {
				String fieldName = e.getSourceField();
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("ldap.search.childrecord",fieldName);
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        message = new ActionMessage("datasource.ldap.error.heading",errorheading);
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);				
				return mapping.findForward(FAILURE_FORWARD);
			}catch (Exception e) {
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("ldap.search.exception",ldapDatasourceData.getName());
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        message = new ActionMessage("datasource.ldap.error.heading",errorheading);
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);				
				return mapping.findForward(FAILURE_FORWARD);
			}					
		}else{
			Logger.logWarn(MODULE,"No Access On this Operation.");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("datasource.ldap.error.heading","searching");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}

	}
}