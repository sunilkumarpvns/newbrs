package com.elitecore.netvertexsm.web.datasource.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.ILDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.LDAPDSConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.datasource.ldap.form.SearchLDAPDatasourceForm;

public class InitSearchLDAPDSAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "SearchLDAPDS";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_LDAP_DATASOURCE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
			List searchLDAPList = new ArrayList();
			ILDAPDatasourceData ldapDatasourceData = new LDAPDatasourceData();
			LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();

			SearchLDAPDatasourceForm searchLDAPDatasourceForm = (SearchLDAPDatasourceForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			searchLDAPList = ldapDatasourceBLManager.search(ldapDatasourceData,staffData,ACTION_ALIAS);

			request.getSession().setAttribute("searchLDAPList",searchLDAPList );	
			searchLDAPDatasourceForm.setAction(LDAPDSConstant.LISTACTION);			
			request.getSession().setAttribute("searchLDAPDSForm",searchLDAPDatasourceForm);

			return mapping.findForward(SUCCESS_FORWARD);		
		}else{
			Logger.logWarn(MODULE,"No Access On this Operation.");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}