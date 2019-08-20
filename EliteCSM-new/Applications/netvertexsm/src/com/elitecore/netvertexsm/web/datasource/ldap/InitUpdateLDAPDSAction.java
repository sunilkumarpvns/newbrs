package com.elitecore.netvertexsm.web.datasource.ldap;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.netvertexsm.util.PasswordUtility;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.datasource.ldap.form.UpdateLDAPDatasourceForm;

public class InitUpdateLDAPDSAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "InitUpdateLDAPDS";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_LDAP_DATASOURCE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());

			UpdateLDAPDatasourceForm updateLDAPDatasourceForm = (UpdateLDAPDatasourceForm)form;
			try{
				LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();

				LDAPDatasourceData ldapData = ldapDatasourceBLManager.getLDAPData(updateLDAPDatasourceForm.getLdapDsId());
				List<LDAPBaseDnDetailData> listOfBaseDnDetail = ldapDatasourceBLManager.getLDAPBaseBnDetailByLdapId(ldapData.getLdapDsId());

				String[] searchBaseDns = new String[listOfBaseDnDetail.size()];

				for(int i=0;i<searchBaseDns.length;i++){
					searchBaseDns[i] = listOfBaseDnDetail.get(i).getSearchBaseDn();
				}
				ldapData.setSearchDnDetailList(listOfBaseDnDetail);

				updateLDAPDatasourceForm.setName(ldapData.getName());
				updateLDAPDatasourceForm.setAddress(ldapData.getAddress());				
				updateLDAPDatasourceForm.setAdministrator(ldapData.getAdministrator());
				updateLDAPDatasourceForm.setTimeout(ldapData.getTimeout());
				updateLDAPDatasourceForm.setLdapSizeLimit(ldapData.getLdapSizeLimit());
				updateLDAPDatasourceForm.setPassword(PasswordUtility.getDecryptedPassword(ldapData.getPassword()));
				updateLDAPDatasourceForm.setUserDNPrefix(ldapData.getUserDNPrefix());
				updateLDAPDatasourceForm.setMinimumPool(ldapData.getMinimumPool());
				updateLDAPDatasourceForm.setMaximumPool(ldapData.getMaximumPool());
				updateLDAPDatasourceForm.setStatusCheckDuration(ldapData.getStatusCheckDuration());
				updateLDAPDatasourceForm.setSearchDn(searchBaseDns);

				request.getSession().setAttribute("ldapDatasourceData", ldapData);			
				request.getSession().setAttribute("searchdns", searchBaseDns);

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
				
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        message = new ActionMessage("datasource.ldap.error.heading","updating");
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
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}