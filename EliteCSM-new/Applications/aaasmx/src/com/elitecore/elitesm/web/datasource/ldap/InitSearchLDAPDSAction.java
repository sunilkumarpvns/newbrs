package com.elitecore.elitesm.web.datasource.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.datasource.ldap.forms.SearchLDAPDatasourceForm;

public class InitSearchLDAPDSAction extends BaseWebAction{
	
	private static final String SEARCH_PAGE = "SearchLDAPDS";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_LDAP_DATASOURCE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		List searchLDAPList = new ArrayList();
		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		
		request.getSession().setAttribute("searchLDAPList", searchLDAPList);
		
		SearchLDAPDatasourceForm searchLDAPDS = (SearchLDAPDatasourceForm)form;
		request.setAttribute("searchLDAPDS", searchLDAPDS);
		searchLDAPDS.setAction("list");
		doAuditing(staffData, ACTION_ALIAS);
		return mapping.findForward(SEARCH_PAGE);
	}
}
