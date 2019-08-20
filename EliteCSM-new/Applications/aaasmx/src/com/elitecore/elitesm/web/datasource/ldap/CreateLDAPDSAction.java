package com.elitecore.elitesm.web.datasource.ldap;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.ILDAPDatasourceData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.datasource.ldap.forms.CreateLDAPDatasourceForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class CreateLDAPDSAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String MODULE = "CREATE LDAPDS ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_LDAP_DATASOURCE;
	
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			
			checkActionPermission(request, ACTION_ALIAS);
			CreateLDAPDatasourceForm createLDAPDatasourceForm = (CreateLDAPDatasourceForm)form;
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();

			LDAPDatasourceData ldapDatasourceData = new LDAPDatasourceData();

			if(createLDAPDatasourceForm.getCheckAction().equalsIgnoreCase("create")){

				String [] baseDnNames = request.getParameterValues("dnnameVal");
				ArrayList<LDAPBaseDnDetailData> tempList = new ArrayList<LDAPBaseDnDetailData>();
				if(baseDnNames != null){        		
					for(int i =0;i<baseDnNames.length;i++){
						LDAPBaseDnDetailData data = new LDAPBaseDnDetailData();
						data.setSearchBaseDn(baseDnNames[i]);
						tempList.add(data);
					}
				}

				convertFromFormToBean(createLDAPDatasourceForm,ldapDatasourceData);

				/* Encrypt User password */
				String encryptedPassword = PasswordEncryption.getInstance().crypt(ldapDatasourceData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
			
				ldapDatasourceData.setPassword(encryptedPassword);
				ldapDatasourceData.setCreatedByStaffId(currentUser);        	
				ldapDatasourceData.setLastModifiedDate(getCurrentTimeStemp());
				ldapDatasourceData.setLastModifiedByStaffId(currentUser);

				ldapDatasourceData.setSearchDnDetailList(tempList);

				LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();
				try{
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					
					ldapDatasourceBLManager.create(ldapDatasourceData,staffData);
					
					request.setAttribute("responseUrl", "/initSearchLDAPDS");
					ActionMessage message = new ActionMessage("datasource.ldap.create.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS_FORWARD);

				}catch(DuplicateParameterFoundExcpetion dpf){
					Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
					Logger.logTrace(MODULE,dpf);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("ldap.create.duplicate.failure",ldapDatasourceData.getName());
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE_FORWARD);
				}catch(DataManagerException exp){
					Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
					Logger.logTrace(MODULE,exp);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("datasource.ldap.create.failure",ldapDatasourceData.getName());
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE_FORWARD);
				}
			}
			return mapping.findForward(FAILURE_FORWARD);
			
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	

		}catch(Exception e){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}
	
	public void convertFromFormToBean(CreateLDAPDatasourceForm createLDAPDatasourceForm , ILDAPDatasourceData ldapDatasourceData){		
		ldapDatasourceData.setName(createLDAPDatasourceForm.getName());
		ldapDatasourceData.setAddress(createLDAPDatasourceForm.getAddress());
		
		ldapDatasourceData.setTimeout(createLDAPDatasourceForm.getTimeout());
		ldapDatasourceData.setStatusCheckDuration(createLDAPDatasourceForm.getStatusCheckDuration());
		ldapDatasourceData.setLdapSizeLimit(createLDAPDatasourceForm.getLdapSizeLimit());
		ldapDatasourceData.setAdministrator(createLDAPDatasourceForm.getAdministrator());
		ldapDatasourceData.setPassword(createLDAPDatasourceForm.getPassword());
		
		ldapDatasourceData.setUserDNPrefix(createLDAPDatasourceForm.getUserDnPrefix());
		ldapDatasourceData.setMinimumPool(createLDAPDatasourceForm.getMinimumPool());
		ldapDatasourceData.setMaximumPool(createLDAPDatasourceForm.getMaximumPool());
	//	ldapDatasourceData.setSearchFilter(createLDAPDatasourceForm.getSearchFilter());
		ldapDatasourceData.setCreateDate(getCurrentTimeStemp());		
		try {
			ldapDatasourceData.setLdapVersion(Integer.valueOf(createLDAPDatasourceForm.getLdapVersion()));
		}catch (NumberFormatException e) {
			ldapDatasourceData.setLdapVersion(null);
	}
	}
}