package com.elitecore.elitesm.web.datasource.ldap;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.datasource.ldap.forms.UpdateLDAPDatasourceForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class InitUpdateLDAPDSAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "InitUpdateLDAPDS";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = "UPDATE_LDAP_DS_ACTION";
	private static final String VIEW_ACTION_ALIAS=ConfigConstant.VIEW_LDAP_DATASOURCE;
	
public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		UpdateLDAPDatasourceForm updateLDAPDatasourceForm = (UpdateLDAPDatasourceForm)form;
		try{
			LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			LDAPDatasourceData ldapData = (LDAPDatasourceData) ldapDatasourceBLManager.getLDAPDatabaseDataById(updateLDAPDatasourceForm.getLdapDsId());
			List<LDAPBaseDnDetailData> listOfBaseDnDetail = ldapDatasourceBLManager.getLDAPBaseBnDetailByLdapId(ldapData.getLdapDsId());
			
			String[] searchBaseDns = new String[listOfBaseDnDetail.size()];
			
			for(int i=0;i<searchBaseDns.length;i++){
				searchBaseDns[i] = listOfBaseDnDetail.get(i).getSearchBaseDn();
			}
			ldapData.setSearchDnDetailList(listOfBaseDnDetail);
			
			/* decrypt User password */
			String decryptPassword = PasswordEncryption.getInstance().decrypt(ldapData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
		
			
			updateLDAPDatasourceForm.setName(ldapData.getName());
			updateLDAPDatasourceForm.setAddress(ldapData.getAddress());
			updateLDAPDatasourceForm.setAdministrator(ldapData.getAdministrator());
			updateLDAPDatasourceForm.setTimeout(ldapData.getTimeout());
			updateLDAPDatasourceForm.setLdapSizeLimit(ldapData.getLdapSizeLimit());
			updateLDAPDatasourceForm.setPassword(decryptPassword);
			updateLDAPDatasourceForm.setUserDNPrefix(ldapData.getUserDNPrefix());
			updateLDAPDatasourceForm.setMinimumPool(ldapData.getMinimumPool());
			updateLDAPDatasourceForm.setMaximumPool(ldapData.getMaximumPool());
			//updateLDAPDatasourceForm.setSearchFilter(ldapData.getSearchFilter());
			updateLDAPDatasourceForm.setStatusCheckDuration(ldapData.getStatusCheckDuration());
			updateLDAPDatasourceForm.setSearchDn(searchBaseDns);
			updateLDAPDatasourceForm.setLdapVersion(ldapData.getLdapVersion()!=null?ldapData.getLdapVersion().toString() : "");
			updateLDAPDatasourceForm.setAuditUId(ldapData.getAuditUId());
							
			request.getSession().setAttribute("ldapDatasourceData", ldapData);			
			request.getSession().setAttribute("searchdns", searchBaseDns);
			doAuditing(staffData, VIEW_ACTION_ALIAS);
		    return mapping.findForward(SUCCESS_FORWARD);
			
		}catch(Exception e){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("datasource.ldap.update.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}
}