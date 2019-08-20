package com.elitecore.elitesm.web.driver.diameter;

import java.util.ArrayList;
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

import com.elitecore.elitesm.blmanager.datasource.LDAPDatasourceBLManager;
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

public class UpdateDiameterLDAPAuthDriverAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "openUpdateDiameterLDAPAuthDriver";
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



			if(updateLdapDriverForm.getAction() != null){

				if(updateLdapDriverForm.getAction().equals("Update")){
					
					LDAPAuthDriverData updatedLdapAuthDriverData = new LDAPAuthDriverData();
					updatedLdapAuthDriverData.setDriverInstanceId(updateLdapDriverForm.getDriverInstanceId());
					//updatedLdapAuthDriverData.setDsStatusCheckInterval(updateLdapDriverForm.getDsStatusCheckInterval());
					updatedLdapAuthDriverData.setExpiryDatePattern(updateLdapDriverForm.getExpiryDatePattern());
					updatedLdapAuthDriverData.setLdapAuthDriverFieldMapList(getLdapAuthFieldMapData(request));
					//updatedLdapAuthDriverData.setLdapDriverId(updateLdapDriverForm.getLdapDriverId());
					updatedLdapAuthDriverData.setPasswordDecryptType(updateLdapDriverForm.getPasswordDecryptType());
					updatedLdapAuthDriverData.setQueryMaxExecTime(updateLdapDriverForm.getQueryMaxExecTime());
					updatedLdapAuthDriverData.setLdapDsId(updateLdapDriverForm.getLdapDsId());
					updatedLdapAuthDriverData.setUserIdentityAttributes(updateLdapDriverForm.getUserIdentityAttributes());
					updatedLdapAuthDriverData.setSearchFilter(updateLdapDriverForm.getSearchFilter());
					updatedLdapAuthDriverData.setSearchScope(updateLdapDriverForm.getSearchScope());
					
					DriverInstanceData updatedDriverInstance = new DriverInstanceData();

					updatedDriverInstance.setDriverInstanceId(updateLdapDriverForm.getDriverInstanceId());
					updatedDriverInstance.setName(updateLdapDriverForm.getDriverInstanceName());
					updatedDriverInstance.setDescription(updateLdapDriverForm.getDriverDesp());
					updatedDriverInstance.setLastModifiedDate(getCurrentTimeStemp());
					updatedDriverInstance.setLastModifiedByStaffId(currentUser);
					updatedDriverInstance.setAuditUId(updateLdapDriverForm.getAuditUId());
					
					staffData.setAuditName(driverInstanceData.getName());
					staffData.setAuditId(driverInstanceData.getAuditUId());
					
					driverBlManager.updateLdapAuthDriverById(updatedDriverInstance, updatedLdapAuthDriverData,staffData);
					
					request.setAttribute("responseUrl", "/viewDriverInstance.do?driverInstanceId=" + updateLdapDriverForm.getDriverInstanceId());
					ActionMessage message = new ActionMessage("driver.update.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
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
			request.setAttribute("logicalNameData", driverBlManager.getLogicalNameJson(nameValuePoolList));
			request.getSession().setAttribute("ldapAuthFieldMapList", ldapdriverData.getLdapAuthDriverFieldMapList());
			request.getSession().setAttribute("driverInstance",driverInstanceData);
			
			if("view".equals(updateLdapDriverForm.getAction())) {
				doAuditing(staffData, VIEW_ACTION_ALIAS);
			}
			if(updateLdapDriverForm.getAction() != null){
				return mapping.findForward(OPEN_FORWARD);
			}

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
	private List<LDAPAuthFieldMapData> getLdapAuthFieldMapData(HttpServletRequest httpRequest){
		List<LDAPAuthFieldMapData> dbAuthFieldMapDataList = new ArrayList<LDAPAuthFieldMapData>();
		String[] logicalNames = httpRequest.getParameterValues("logicalnmVal");
		String[] ldapAttribute = httpRequest.getParameterValues("ldapAttribute");
		String defaultValues[] = httpRequest.getParameterValues("defaultValue");
		String valueMappings[] = httpRequest.getParameterValues("valueMapping");
		if(logicalNames != null && ldapAttribute!=null && defaultValues!=null && valueMappings!=null){
			for(int index=0; index<logicalNames.length; index++){
				LDAPAuthFieldMapData ldapAuthFieldMapData = new LDAPAuthFieldMapData();
				ldapAuthFieldMapData.setLogicalName(logicalNames[index]);
				ldapAuthFieldMapData.setLdapAttribute(ldapAttribute[index]);
				ldapAuthFieldMapData.setDefaultValue(defaultValues[index]);
				ldapAuthFieldMapData.setValueMapping(valueMappings[index]);
				dbAuthFieldMapDataList.add(ldapAuthFieldMapData);
			}
		}
		return dbAuthFieldMapDataList;
	}
	private Map<String, String> getSubTreeOptions() {
		Map<String, String> subTreeMap = new LinkedHashMap<String, String>(3);
		subTreeMap.put("0", "SCOPE_BASE");
		subTreeMap.put("1", "SCOPE_ONE");
		subTreeMap.put("2", "SCOPE_SUB");
		return subTreeMap;
	}

}
