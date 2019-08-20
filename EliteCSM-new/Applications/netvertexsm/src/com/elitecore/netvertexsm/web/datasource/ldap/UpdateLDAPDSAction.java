package com.elitecore.netvertexsm.web.datasource.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.netvertexsm.util.PasswordUtility;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.ILDAPBaseDnDetailData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.ILDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.datasource.ldap.form.UpdateLDAPDatasourceForm;

public class UpdateLDAPDSAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "success";	
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_LDAP_DATASOURCE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		if(checkAccess(request,ACTION_ALIAS)){
			UpdateLDAPDatasourceForm updateLDAPDatasourceForm = (UpdateLDAPDatasourceForm)form;
			LDAPDatasourceData tempLDAPDatasourceData = new LDAPDatasourceData();
			try{

				LDAPDatasourceData ldapData = (LDAPDatasourceData)request.getSession().getAttribute("ldapDatasourceData");
				ILDAPBaseDnDetailData ldapBaseDnDetailData = new LDAPBaseDnDetailData();

				if(updateLDAPDatasourceForm.getCheckAction().equals("create")){

					String[] dnnames = request.getParameterValues("dnname");
					List<LDAPBaseDnDetailData> searchDnList = new ArrayList<LDAPBaseDnDetailData>();	        		        	
					if(dnnames != null){
						for(int i=0;i<dnnames.length;i++){
							LDAPBaseDnDetailData data = new LDAPBaseDnDetailData();
							data.setSearchBaseDn(dnnames[i]);
							searchDnList.add(data);
						}
					}
					convertFromFormToBean(updateLDAPDatasourceForm,tempLDAPDatasourceData,ldapData.getLdapDsId());
					tempLDAPDatasourceData.setSearchDnDetailList(searchDnList);

					LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					ldapDatasourceBLManager.update(tempLDAPDatasourceData ,ldapData.getLdapDsId(),staffData,ACTION_ALIAS);		
					request.getSession().setAttribute("ldapDatasourceData", tempLDAPDatasourceData);

					request.setAttribute("responseUrl", "/initSearchLDAPDS");
					ActionMessage message = new ActionMessage("datasource.ldap.update.success",tempLDAPDatasourceData.getName());
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);							
					
					return mapping.findForward(SUCCESS_FORWARD);
				}

				return mapping.findForward(FAILURE_FORWARD);
			}catch(DuplicateParameterFoundExcpetion dpf){
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE,dpf);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("ldap.create.duplicate.failure",tempLDAPDatasourceData.getName());
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        message = new ActionMessage("datasource.ldap.error.heading","updating");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);				
				return mapping.findForward(FAILURE_FORWARD);
			}catch(Exception e){

				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("datasource.ldap.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);

		        ActionMessages errorHeadingMessage = new ActionMessages();
		        message = new ActionMessage("datasource.ldap.error.heading","updating");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);				
				return mapping.findForward(FAILURE_FORWARD);

			}		
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("datasource.ldap.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	public void convertFromFormToBean(UpdateLDAPDatasourceForm updateLdapDatasourceData, ILDAPDatasourceData ldapDatasourceData,long originalLdapId) throws NoSuchEncryptionException, EncryptionFailedException {

		ldapDatasourceData.setName(updateLdapDatasourceData.getName());
		ldapDatasourceData.setAddress(updateLdapDatasourceData.getAddress());		

		ldapDatasourceData.setTimeout(updateLdapDatasourceData.getTimeout());
		ldapDatasourceData.setStatusCheckDuration(updateLdapDatasourceData.getStatusCheckDuration());
		ldapDatasourceData.setLdapSizeLimit(updateLdapDatasourceData.getLdapSizeLimit());
		ldapDatasourceData.setAdministrator(updateLdapDatasourceData.getAdministrator());
		ldapDatasourceData.setPassword(PasswordUtility.getEncryptedPassword(updateLdapDatasourceData.getPassword()));

		ldapDatasourceData.setUserDNPrefix(updateLdapDatasourceData.getUserDNPrefix());
		ldapDatasourceData.setMinimumPool(updateLdapDatasourceData.getMinimumPool());
		ldapDatasourceData.setMaximumPool(updateLdapDatasourceData.getMaximumPool());
		ldapDatasourceData.setLdapDsId(originalLdapId);
	}
}