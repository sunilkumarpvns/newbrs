package com.elitecore.elitesm.web.datasource.ldap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.datasource.LDAPDatasourceBLManager;
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
import com.elitecore.elitesm.web.datasource.ldap.forms.UpdateLDAPDatasourceForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class UpdateLDAPDSAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "UpdateLDAPDS";	
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_LDAP_DATASOURCE;
	
public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		UpdateLDAPDatasourceForm updateLDAPDatasourceForm = (UpdateLDAPDatasourceForm)form;
		String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
		LDAPDatasourceData tempLDAPDatasourceData = new LDAPDatasourceData();
		LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();
		try{
			checkActionPermission(request, ACTION_ALIAS);
			LDAPDatasourceData ldapData = (LDAPDatasourceData)request.getSession().getAttribute("ldapDatasourceData");
			if(updateLDAPDatasourceForm.getCheckAction().equals("create")){
							
	        	String[] dnnames = request.getParameterValues("dnname");
	        	List<LDAPBaseDnDetailData> searchDnList = new ArrayList<LDAPBaseDnDetailData>();	 
	        	Set<LDAPBaseDnDetailData> ldapBaseDnList= new  HashSet<LDAPBaseDnDetailData>();
	        	
	        	if(dnnames != null){
	        		for(int i=0;i<dnnames.length;i++){
	        			LDAPBaseDnDetailData data = new LDAPBaseDnDetailData();
	        			data.setSearchBaseDn(dnnames[i]);
	        			searchDnList.add(data);
	        			ldapBaseDnList.add(data);
	        		}
	        	}
	        	tempLDAPDatasourceData = (LDAPDatasourceData) ldapDatasourceBLManager.getLDAPDatabaseDataById(ldapData.getLdapDsId());
	        	convertFromFormToBean(updateLDAPDatasourceForm,tempLDAPDatasourceData,ldapData.getLdapDsId());
	        	
	        	/* Encrypt User password */
				String encryptedPassword = PasswordEncryption.getInstance().crypt(tempLDAPDatasourceData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
				tempLDAPDatasourceData.setPassword(encryptedPassword);
			
	        	tempLDAPDatasourceData.setSearchDnDetailList(searchDnList);
	            tempLDAPDatasourceData.setLdapBaseDnDetail(ldapBaseDnList);
	             
	        	tempLDAPDatasourceData.setLastModifiedDate(getCurrentTimeStemp());
	        	tempLDAPDatasourceData.setLastModifiedByStaffId(currentUser);
	        	
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				staffData.setAuditId(tempLDAPDatasourceData.getAuditUId());
				staffData.setAuditName(tempLDAPDatasourceData.getName());
				
	    		ldapDatasourceBLManager.updateLDAPDatasourceDataById(tempLDAPDatasourceData,staffData);	
	    		
	    		request.getSession().setAttribute("ldapDatasourceData", tempLDAPDatasourceData);

	    		return mapping.findForward(SUCCESS_FORWARD);
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

		}catch(DuplicateParameterFoundExcpetion dpf){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,dpf);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("datasource.ldap.update.failure");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
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
			return mapping.findForward(FAILURE_FORWARD);
			
		}
	
  }

	public void convertFromFormToBean(UpdateLDAPDatasourceForm updateLdapDatasourceData, ILDAPDatasourceData ldapDatasourceData,String originalLdapId){
	
		ldapDatasourceData.setName(updateLdapDatasourceData.getName());
		ldapDatasourceData.setAddress(updateLdapDatasourceData.getAddress());
		
		ldapDatasourceData.setTimeout(updateLdapDatasourceData.getTimeout());
		ldapDatasourceData.setStatusCheckDuration(updateLdapDatasourceData.getStatusCheckDuration());
		ldapDatasourceData.setLdapSizeLimit(updateLdapDatasourceData.getLdapSizeLimit());
		ldapDatasourceData.setAdministrator(updateLdapDatasourceData.getAdministrator());
		ldapDatasourceData.setPassword(updateLdapDatasourceData.getPassword());
		
		ldapDatasourceData.setUserDNPrefix(updateLdapDatasourceData.getUserDNPrefix());
		ldapDatasourceData.setMinimumPool(updateLdapDatasourceData.getMinimumPool());
		ldapDatasourceData.setMaximumPool(updateLdapDatasourceData.getMaximumPool());
		ldapDatasourceData.setLdapDsId(originalLdapId);
		ldapDatasourceData.setCreateDate(getCurrentTimeStemp());
		ldapDatasourceData.setAuditUId(updateLdapDatasourceData.getAuditUId());
		try {
			ldapDatasourceData.setLdapVersion(Integer.valueOf(updateLdapDatasourceData.getLdapVersion()));
		}catch (NumberFormatException e) {
			ldapDatasourceData.setLdapVersion(null);
	}
	}
}