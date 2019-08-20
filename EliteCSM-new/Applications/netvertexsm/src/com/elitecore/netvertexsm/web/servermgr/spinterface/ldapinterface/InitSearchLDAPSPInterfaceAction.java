package com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.util.constants.ActionMessageConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface.form.SearchLDAPSPInterfaceForm;

public class InitSearchLDAPSPInterfaceAction extends BaseWebAction {
	private static final String SEARCH_FORWARD = "initSearchLDAPSPInterface";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			SearchLDAPSPInterfaceForm searchLDAPSPInterfaceForm = (SearchLDAPSPInterfaceForm) form;
			DriverBLManager ldapDriverBLManager = new DriverBLManager();
			LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();
			List<LDAPDatasourceData> ldapDSList = ldapDatasourceBLManager.getLDAPDSList();
			List<DriverInstanceData> driverInstanceList = ldapDriverBLManager.getDriverInstanceList();

			searchLDAPSPInterfaceForm.setLdapDsList(ldapDSList);
			searchLDAPSPInterfaceForm.setDriverInstanceList(driverInstanceList);

			return mapping.findForward(SEARCH_FORWARD);
		}catch(Exception managerExp){

			Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
			Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute(ActionMessageConstant.ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("spinterface.search.failure");
			ActionMessages messages = new ActionMessages();
			messages.add(ActionMessageConstant.INFORMATION , message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
}

