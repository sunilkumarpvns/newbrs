package com.elitecore.elitesm.web.driver.diameter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthFieldMapData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.diameter.forms.UpdateDiameterLDAPAuthDriverForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewDiameterLDAPAuthDriverHistoryAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "viewDiameterLDAPAuthDriverHistory";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;
	private static final String OPEN_FORWARD = "viewDiameterLDAPAuthDriverInstance";
	private static final String MODULE="UpdateDiameterLDAPAuthDriverAction";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			UpdateDiameterLDAPAuthDriverForm updateLdapDriverForm = (UpdateDiameterLDAPAuthDriverForm)form;
			if("view".equals(updateLdapDriverForm.getAction())){
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}
			
			DriverBLManager driverBlManager = new DriverBLManager();
			LDAPDatasourceBLManager ldapBlManager = new LDAPDatasourceBLManager();
			LDAPAuthFieldMapData feildMapData =  new LDAPAuthFieldMapData();

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updateLdapDriverForm.getDriverInstanceId());
			LDAPAuthDriverData ldapdriverData = driverBlManager.getLdapDriverByDriverInstanceId(updateLdapDriverForm.getDriverInstanceId());
			List ldapList = ldapBlManager.getListOfLDAP();
			List nameValuePoolList = driverBlManager.getLogicalNameValuePoolList();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();


			if(ldapList != null){
				for(int i=0;i<ldapList.size();i++){
					LDAPDatasourceData tempDsData = (LDAPDatasourceData) ldapList.get(i);
					if(tempDsData.getLdapDsId() == ldapdriverData.getLdapDsId()){
						updateLdapDriverForm.setLdapName(tempDsData.getName());
					}

				}
			}

			updateLdapDriverForm.setDriverInstanceId(driverInstanceData.getDriverInstanceId());
			updateLdapDriverForm.setDriverInstanceName(driverInstanceData.getName());
			updateLdapDriverForm.setDriverDesp(driverInstanceData.getDescription());
			updateLdapDriverForm.setAuditUId(driverInstanceData.getAuditUId());
			//updateLdapDriverForm.setDsStatusCheckInterval(ldapdriverData.getDsStatusCheckInterval());
			updateLdapDriverForm.setExpiryDatePattern(ldapdriverData.getExpiryDatePattern());
			updateLdapDriverForm.setPasswordDecryptType(ldapdriverData.getPasswordDecryptType());
			updateLdapDriverForm.setQueryMaxExecTime(ldapdriverData.getQueryMaxExecTime());
			updateLdapDriverForm.setLdapDsId(ldapdriverData.getLdapDsId());
			updateLdapDriverForm.setLdapDriverId(ldapdriverData.getLdapDriverId());
			updateLdapDriverForm.setLdapDSList(ldapList);
			updateLdapDriverForm.setLogicalNameList(nameValuePoolList);
			updateLdapDriverForm.setUserIdentityAttributes(ldapdriverData.getUserIdentityAttributes());
			updateLdapDriverForm.setSearchFilter(ldapdriverData.getSearchFilter());
			updateLdapDriverForm.setSearchScope(ldapdriverData.getSearchScope());
			updateLdapDriverForm.setSubTreeOptions(getSubTreeOptions());
		
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId = strDriverInstanceId;
			if(driverInstanceId == null){
				driverInstanceId = updateLdapDriverForm.getDriverInstanceId();
			}

			String strAuditUid = request.getParameter("auditUid");
			String strSytemAuditId=request.getParameter("systemAuditId");
			String name=request.getParameter("name");
			
			if(strSytemAuditId != null){
				request.setAttribute("systemAuditId", strSytemAuditId);
			}
			
			if(driverInstanceId != null && Strings.isNullOrBlank(strAuditUid) == false){
				
				staffData.setAuditName(driverInstanceData.getName());
				staffData.setAuditId(driverInstanceData.getAuditUId());
				
				List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
				
				request.setAttribute("name", name);
				request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
			}
			
			
			request.setAttribute("logicalNameData", driverBlManager.getLogicalNameJson(nameValuePoolList));
			request.getSession().setAttribute("ldapAuthFieldMapList", ldapdriverData.getLdapAuthDriverFieldMapList());
			request.getSession().setAttribute("driverInstance",driverInstanceData);
			
			return mapping.findForward(SUCCESS_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}
	private Map<String, String> getSubTreeOptions() {
		Map<String, String> subTreeMap = new LinkedHashMap<String, String>(3);
		subTreeMap.put("0", "SCOPE_BASE");
		subTreeMap.put("1", "SCOPE_ONE");
		subTreeMap.put("2", "SCOPE_SUB");
		return subTreeMap;
	}

}
