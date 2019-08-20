package com.elitecore.netvertexsm.web.datasource.ldap;

import java.util.ArrayList;

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
import com.elitecore.netvertexsm.datamanager.DataManagerException;
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
import com.elitecore.netvertexsm.web.datasource.ldap.form.CreateLDAPDatasourceForm;

public class CreateLDAPDSAction extends BaseWebAction{
	private static final String CREATE_LDAP = "createLDAPDS";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String MODULE = "CREATE LDAPDS ACTION";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_LDAP_DATASOURCE;


	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());

			CreateLDAPDatasourceForm createLDAPDatasourceForm = (CreateLDAPDatasourceForm)form;

			ILDAPDatasourceData ldapDatasourceData = new LDAPDatasourceData();
			ILDAPBaseDnDetailData ldapBaseDnDetailData = new LDAPBaseDnDetailData();

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

				ldapDatasourceData.setSearchDnDetailList(tempList);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();
				try{
					ldapDatasourceBLManager.create(ldapDatasourceData,staffData,ACTION_ALIAS);

					request.setAttribute("responseUrl", "/initSearchLDAPDS");
					ActionMessage message = new ActionMessage("datasource.ldap.create.success",ldapDatasourceData.getName());
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
					
			        ActionMessages errorHeadingMessage = new ActionMessages();
			        message = new ActionMessage("datasource.ldap.error.heading","creating");
			        errorHeadingMessage.add("errorHeading",message);
			        saveMessages(request,errorHeadingMessage);								
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
					
			        ActionMessages errorHeadingMessage = new ActionMessages();
			        message = new ActionMessage("datasource.ldap.error.heading","creating");
			        errorHeadingMessage.add("errorHeading",message);
			        saveMessages(request,errorHeadingMessage);			
					return mapping.findForward(FAILURE_FORWARD);
				}
			}
			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logWarn(MODULE,"No Access On this Operation.");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("datasource.ldap.error.heading","creating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

		public void convertFromFormToBean(CreateLDAPDatasourceForm createLDAPDatasourceForm , ILDAPDatasourceData ldapDatasourceData) throws NoSuchEncryptionException, EncryptionFailedException {

			ldapDatasourceData.setName(createLDAPDatasourceForm.getName());
			ldapDatasourceData.setAddress(createLDAPDatasourceForm.getAddress());			

			ldapDatasourceData.setTimeout(createLDAPDatasourceForm.getTimeout());
			ldapDatasourceData.setStatusCheckDuration(createLDAPDatasourceForm.getStatusCheckDuration());
			ldapDatasourceData.setLdapSizeLimit(createLDAPDatasourceForm.getLdapSizeLimit());
			ldapDatasourceData.setAdministrator(createLDAPDatasourceForm.getAdministrator());
			ldapDatasourceData.setPassword(PasswordUtility.getEncryptedPassword(createLDAPDatasourceForm.getPassword()));

			ldapDatasourceData.setUserDNPrefix(createLDAPDatasourceForm.getUserDnPrefix());
			ldapDatasourceData.setMinimumPool(createLDAPDatasourceForm.getMinimumPool());
			ldapDatasourceData.setMaximumPool(createLDAPDatasourceForm.getMaximumPool());
		}

	}
