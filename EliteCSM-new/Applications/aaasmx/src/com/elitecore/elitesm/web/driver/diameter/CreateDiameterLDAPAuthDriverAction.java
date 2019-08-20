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
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthFieldMapData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.CreateDriverConfig;
import com.elitecore.elitesm.web.driver.diameter.forms.CreateDiameterLDAPAuthDriverForm;

public class CreateDiameterLDAPAuthDriverAction extends BaseWebAction{



	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String MODULE = CreateDiameterLDAPAuthDriverAction.class.getSimpleName();
	private static final String CREATE = "cDiameterLDAPAuthDriver";


	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			CreateDiameterLDAPAuthDriverForm ldapDriverForm = (CreateDiameterLDAPAuthDriverForm)form;	
			DriverBLManager driverBlManager = new DriverBLManager();
			LDAPDatasourceBLManager ldapBlManager = new LDAPDatasourceBLManager();
			DriverInstanceData driverInstance = new DriverInstanceData();
			LDAPAuthDriverData ldapDriverData = new LDAPAuthDriverData();
			CreateDriverConfig driverConfig = new CreateDriverConfig();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			if(ldapDriverForm.getAction() != null){

				if(ldapDriverForm.getAction().equals("create")){
					////  create related activites

					try{
						convertFromFormToData(ldapDriverForm,driverInstance,ldapDriverData);

						driverInstance.setCreatedByStaffId(currentUser);        	
						driverInstance.setLastModifiedDate(getCurrentTimeStemp());
						driverInstance.setLastModifiedByStaffId(currentUser);

						driverConfig.setDriverInstanceData(driverInstance);
						driverConfig.setLdapAuthDriverData(ldapDriverData);				
						ldapDriverData.setLdapAuthDriverFieldMapList((getLdapAuthFieldMapData(request)));
						driverBlManager.createLDAPDriver(driverConfig,staffData);
						request.setAttribute("responseUrl", "/initSearchDriver");
						ActionMessage message = new ActionMessage("driver.create.success");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveMessages(request, messages);
						return mapping.findForward(SUCCESS_FORWARD);

					}catch(Exception e) {

						Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
						Logger.logTrace(MODULE,e);
						Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
						request.setAttribute("errorDetails", errorElements);
						ActionMessage message = new ActionMessage("driver.create.failure");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveErrors(request,messages);
						return mapping.findForward(FAILURE_FORWARD);
					}

				}else{

					// getting the ldap ds values 

					List<LDAPDatasourceData> listOfLDAP = ldapBlManager.getListOfLDAP();
					ldapDriverForm.setLdapDSList(listOfLDAP);

					//setting from request context to form values ......
					if(ldapDriverForm.getDriverDesp() == null || ldapDriverForm.getDriverRelatedId() == null || ldapDriverForm.getDriverInstanceName() == null){
						ldapDriverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
						ldapDriverForm.setDriverDesp((String)request.getAttribute("desp"));
						Long driverId =(Long)request.getAttribute("driverId");
						ldapDriverForm.setDriverRelatedId(driverId.toString());
					}

					// populating list of logical names ....
					List<LogicalNameValuePoolData> nameValuePoolList = driverBlManager.getLogicalNameValuePoolList();
					ldapDriverForm.setLogicalNameList(nameValuePoolList);

					ldapDriverForm.setSearchScope("2");
					ldapDriverForm.setSubTreeOptions(getSubTreeOptions());

					request.setAttribute("logicalNameData", driverBlManager.getLogicalNameJson(nameValuePoolList));
					request.setAttribute("defaultMapping", ldapDriverForm.getDefaultmapping());
					return mapping.findForward(CREATE); 
				}
			}

			return mapping.findForward(SUCCESS_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception dme){

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dme);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}



	}


	private void convertFromFormToData(CreateDiameterLDAPAuthDriverForm ldapDriverForm,DriverInstanceData driverInstance,LDAPAuthDriverData ldapDriverData) {

		//ldapDriverData.setDsStatusCheckInterval(ldapDriverForm.getDsStatusCheckInterval());
		ldapDriverData.setExpiryDatePattern(ldapDriverForm.getExpiryDatePattern());
		ldapDriverData.setLdapDsId(ldapDriverForm.getLdapDsId());
		ldapDriverData.setPasswordDecryptType(ldapDriverForm.getPasswordDecryptType());
		ldapDriverData.setQueryMaxExecTime(ldapDriverForm.getQueryMaxExecTime());	
		ldapDriverData.setUserIdentityAttributes(ldapDriverForm.getUserIdentityAttributes());
		ldapDriverData.setSearchFilter(ldapDriverForm.getSearchFilter());
		ldapDriverData.setSearchScope(ldapDriverForm.getSearchScope());
		
		driverInstance.setCreateDate(getCurrentTimeStemp());
		driverInstance.setDescription(ldapDriverForm.getDriverDesp());
		driverInstance.setDriverTypeId(Long.parseLong(ldapDriverForm.getDriverRelatedId()));
		driverInstance.setName(ldapDriverForm.getDriverInstanceName());
		driverInstance.setStatus("CST01");
	}
	private List<LDAPAuthFieldMapData> getLdapAuthFieldMapData(HttpServletRequest request){
		List<LDAPAuthFieldMapData> ldapAuthFieldMapDataList = new ArrayList<LDAPAuthFieldMapData>();
		String[] logicalNames = request.getParameterValues("logicalnmVal");
		String[] ldapAttribute = request.getParameterValues("ldapAttribute");
		String defaultValues[] = request.getParameterValues("defaultValue");
		String valueMappings[] = request.getParameterValues("valueMapping");
		if(logicalNames != null && ldapAttribute!=null && defaultValues!=null && valueMappings!=null){
			for(int index=0; index<logicalNames.length; index++){
				LDAPAuthFieldMapData ldapAuthFieldMapData = new LDAPAuthFieldMapData();
				ldapAuthFieldMapData.setLogicalName(logicalNames[index]);
				ldapAuthFieldMapData.setLdapAttribute(ldapAttribute[index]);
				ldapAuthFieldMapData.setDefaultValue(defaultValues[index]);
				ldapAuthFieldMapData.setValueMapping(valueMappings[index]);
				ldapAuthFieldMapDataList.add(ldapAuthFieldMapData);
			}
		}
		return ldapAuthFieldMapDataList;
	}
	private Map<String, String> getSubTreeOptions() {
		Map<String, String> subTreeMap = new LinkedHashMap<String, String>(3);
		subTreeMap.put("0", "SCOPE_BASE");
		subTreeMap.put("1", "SCOPE_ONE");
		subTreeMap.put("2", "SCOPE_SUB");
		return subTreeMap;
	}

}
