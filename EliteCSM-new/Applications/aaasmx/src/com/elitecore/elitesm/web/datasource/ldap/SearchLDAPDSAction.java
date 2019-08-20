package com.elitecore.elitesm.web.datasource.ldap;

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

import com.elitecore.elitesm.blmanager.core.base.GenericBLManager;
import com.elitecore.elitesm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ReferenceFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.ILDAPDatasourceData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.LDAPDSConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.datasource.ldap.forms.SearchLDAPDatasourceForm;

public class SearchLDAPDSAction extends BaseWebAction{
	private static final String FAILURE_FORWARD = "failure";
	private static final String SEARCH_PAGE = "SearchLDAPDS";
	private static final String SEARCHALLLDAPDS = "searchAllLDAPDS";
	private static final String MODULE = "SEARCH LDAPDS ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_LDAP_DATASOURCE;
	private static final String ACTION_ALIAS_DELETE= ConfigConstant.DELETE_LDAP_DATASOURCE;
	private static final String SHOW_ALL_ALIAS=ConfigConstant.SHOW_ALL_DATASOURCE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		if(checkAccess(request,ACTION_ALIAS) || checkAccess(request,ACTION_ALIAS_DELETE)){
			SearchLDAPDatasourceForm searchLDAPDatasourceForm = (SearchLDAPDatasourceForm)form;
			GenericBLManager genericBLManager=null;
			ILDAPDatasourceData ldapDatasourceData = new LDAPDatasourceData();
			List listOfSearchData = new ArrayList();

			LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();

			String []ldapDSIds = request.getParameterValues("select");
			
			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(searchLDAPDatasourceForm.getPageNumber()).intValue();
			}
			
			if(requiredPageNo == 0)
				requiredPageNo = 1;
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			
			try {
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS_DELETE;
				long currentPageNumber = 0;
				if(searchLDAPDatasourceForm.getAction() != null && searchLDAPDatasourceForm.getAction().equals("delete")){			
					ldapDatasourceBLManager.deleteById(Arrays.asList(ldapDSIds), staffData);
					doAuditing(staffData, actionAlias);
					int strLdapDsLen = ldapDSIds.length;
					currentPageNumber = getCurrentPageNumberAfterDel(strLdapDsLen,searchLDAPDatasourceForm.getPageNumber(),searchLDAPDatasourceForm.getTotalPages(),searchLDAPDatasourceForm.getTotalRecords());
				}else if(searchLDAPDatasourceForm.getAction() != null && searchLDAPDatasourceForm.getAction().equalsIgnoreCase("showall")){
					genericBLManager = new GenericBLManager();
					PageList pageList = genericBLManager.getAllRecords(LDAPDatasourceData.class,"ldapDsId",true);
					searchLDAPDatasourceForm.setPageNumber(pageList.getCurrentPage());
					searchLDAPDatasourceForm.setTotalPages(pageList.getTotalPages());
					searchLDAPDatasourceForm.setTotalRecords(pageList.getTotalItems());
					searchLDAPDatasourceForm.setSearchList(pageList.getListData());
					request.setAttribute("searchLDAPDS", searchLDAPDatasourceForm);
					doAuditing(staffData, SHOW_ALL_ALIAS);
					return mapping.findForward(SEARCHALLLDAPDS); 
				}	
				request.setAttribute("responseUrl","/searchLDAPDS.do?name="+searchLDAPDatasourceForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+searchLDAPDatasourceForm.getTotalPages()+"&totalRecords="+searchLDAPDatasourceForm.getTotalRecords());

				if(searchLDAPDatasourceForm.getAction() != null && searchLDAPDatasourceForm.getAction().equals("delete")){	
					
					ActionMessage message = new ActionMessage("ldap.delete.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS);
				}
				
				request.getSession().setAttribute("searchLDAPList",listOfSearchData );	

				searchLDAPDatasourceForm.setAction(LDAPDSConstant.LISTACTION);			
				
				String strLdapDsName =request.getParameter("name");
				if(strLdapDsName != null){
					ldapDatasourceData.setName(strLdapDsName);
				}else if(searchLDAPDatasourceForm.getName() != null){
					ldapDatasourceData.setName(searchLDAPDatasourceForm.getName());
				}else{
					ldapDatasourceData.setName("");
				}
				
				PageList pageList = ldapDatasourceBLManager.search(ldapDatasourceData,staffData,requiredPageNo,pageSize);
				
				searchLDAPDatasourceForm.setPageNumber(pageList.getCurrentPage());
				searchLDAPDatasourceForm.setTotalPages(pageList.getTotalPages());
				searchLDAPDatasourceForm.setTotalRecords(pageList.getTotalItems());
				searchLDAPDatasourceForm.setSearchList(pageList.getListData());
				searchLDAPDatasourceForm.setAction(BaseConstant.LISTACTION);
				
				request.setAttribute("searchLDAPDS",searchLDAPDatasourceForm);
				return mapping.findForward(SEARCH_PAGE);
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
				return mapping.findForward(FAILURE_FORWARD);
			}	
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