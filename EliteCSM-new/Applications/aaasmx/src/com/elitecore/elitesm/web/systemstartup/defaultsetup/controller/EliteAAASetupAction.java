package com.elitecore.elitesm.web.systemstartup.defaultsetup.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.MappingDispatchAction;

import com.elitecore.commons.base.Maps;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.rm.concurrentloginpolicy.ConcurrentLoginPolicyBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.form.EliteAAASetupData;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.form.EliteAAASetupForm;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.model.EliteAAADefaultModel;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.util.DefaultSetupClassNameAndProperty;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.util.DefaultSetupUtility;

public class EliteAAASetupAction extends MappingDispatchAction {

	private static LinkedHashMap<MODULE_NAME_CONSTANTS,MODULE_STATUS> moduleStatusMap = new LinkedHashMap<MODULE_NAME_CONSTANTS, MODULE_STATUS>();;

	public static void init(){
		moduleStatusMap = new LinkedHashMap<MODULE_NAME_CONSTANTS,MODULE_STATUS>();
		for(MODULE_NAME_CONSTANTS constants : MODULE_NAME_CONSTANTS.values()){
			moduleStatusMap.put(constants, MODULE_STATUS.FAILED);
		}
	}

	public static LinkedHashMap<MODULE_NAME_CONSTANTS, MODULE_STATUS> getModuleStatusMap() {
		return moduleStatusMap;
	}

	public static void updateStatus ( MODULE_NAME_CONSTANTS key, MODULE_STATUS status){
		moduleStatusMap.put(key, status);
	}

	public enum MODULE_NAME_CONSTANTS{
		DATABASEDATASOURCE("Database Datasource"), 		
		RADIUSDBAUTHDRIVER("Radius DB Auth Driver"), 
		RADIUSCLASSICCSVDRIVER("Radius Classic CSV Driver"), 
		RADIUSSESSIONMANAGER("Session Manager"), 
		TRANSACTIONLOGGERPLUGIN("Radius Transaction Logger"),
		CONCURRENTLOGINPOLICY("Concurrent Login Policy"),
		SUBSCRIBERPROFILE("Subscriber Profile"),
		RADIUSSERVICEPOLICY("Radius Service Policy");
		public String module;
		MODULE_NAME_CONSTANTS(String module){
			this.module = module;
		}		
	}

	public enum MODULE_STATUS {
		SUCCESS("Success"),
		FAILED("Failed");
		public String status;
		MODULE_STATUS(String status){
			this.status = status;
		}
	}

	public ActionForward createDefault(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
	{
		String strReturnToStarupPage = "loginFailedInitFailed";
		
		if(ConfigManager.isInitCompleted() == false){
			return mapping.findForward(strReturnToStarupPage);
		}
		EliteAAASetupForm eliteAAASetupForm = null;
		
		try{
			eliteAAASetupForm = (EliteAAASetupForm) form;
			EliteAAASetupData eliteAAASetupData = convertFormToBean(eliteAAASetupForm);
			EliteAAADefaultModel eliteAAADefaultModel = new EliteAAADefaultModel();
			EliteAAASetupAction.init();
			eliteAAADefaultModel.createDefaultConfigurationSetup(eliteAAASetupData);
			eliteAAASetupForm.setSuccess("true");
		}catch(Exception e){
			eliteAAASetupForm.setSuccess("false");
			if(Maps.isNullOrEmpty(moduleStatusMap) == false){
				for(MODULE_NAME_CONSTANTS key:moduleStatusMap.keySet()){
					String status =moduleStatusMap.get(key).status;
					if(status.equalsIgnoreCase("Success") == false){
						moduleStatusMap.put(key, MODULE_STATUS.FAILED);
					}
				}
			} else {
				for (MODULE_NAME_CONSTANTS constants : MODULE_NAME_CONSTANTS.values()) {
					moduleStatusMap.put(constants, MODULE_STATUS.FAILED);
				}
			}
			eliteAAASetupForm.setModuleStatusMap(moduleStatusMap);
		}
		eliteAAASetupForm.setModuleStatusMap(EliteAAASetupAction.getModuleStatusMap());
		request.setAttribute("eliteAAASetupForm", eliteAAASetupForm);
		return mapping.findForward(DefaultSetupUtility.SETUP_CREATED);
	}

	public ActionForward deleteAlreadyExistData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
	{
		String strReturnToStarupPage = "loginFailedInitFailed";
		
		if(ConfigManager.isInitCompleted() == false){
			return mapping.findForward(strReturnToStarupPage);
		}
		
		EliteAAASetupForm eliteAAASetupForm = (EliteAAASetupForm) form;
		try {
			StaffBLManager staffBLManager = new StaffBLManager();
			IStaffData staffData = staffBLManager.getStaffDataByUserName("admin");

			List<String> duplicateInstanceValue = DefaultSetupUtility.verifyAndCollectDuplicateModule();

			for (String moduleName : duplicateInstanceValue) {
				if(DefaultSetupClassNameAndProperty.TRANSACTIONLOGGERPLUGIN.propertyName.equalsIgnoreCase(moduleName)){
					PluginBLManager pluginBLManager = new PluginBLManager();
					List<String> transactionLoggerPlugin = new ArrayList<String>();
					transactionLoggerPlugin.add(moduleName);
					pluginBLManager.delete(transactionLoggerPlugin, staffData, false);
				}  else if(DefaultSetupClassNameAndProperty.RADIUSSERVICEPOLICY.propertyName.equalsIgnoreCase(moduleName)){
					ServicePolicyBLManager servicePolicyBLManager = new ServicePolicyBLManager();
					List<String> radiusServicePolicyName = new ArrayList<String>();
					radiusServicePolicyName.add(moduleName);
					servicePolicyBLManager.deleteRadiusServicePolicyByName(radiusServicePolicyName, (StaffData) staffData);
				} else if(DefaultSetupClassNameAndProperty.CONCURRENTLOGINPOLICY.propertyName.equalsIgnoreCase(moduleName)){
					ConcurrentLoginPolicyBLManager coLoginPolicyBLManager = new ConcurrentLoginPolicyBLManager();
					List<String> concurrentLoginPolicyNames = new ArrayList<String>();
					concurrentLoginPolicyNames.add(moduleName);
					coLoginPolicyBLManager.deleteConcurrentLoginPolicyByName(concurrentLoginPolicyNames, staffData, ConfigManager.chekForCaseSensitivity());
				}  else if(DefaultSetupClassNameAndProperty.RADIUSSESSIONMANAGER.propertyName.equalsIgnoreCase(moduleName)){
					SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
					List<String> sessionManagerNames = new ArrayList<String>();
					sessionManagerNames.add(moduleName);
					sessionManagerBLManager.deleteByName(sessionManagerNames, staffData);
				} else if(DefaultSetupClassNameAndProperty.RADIUSCLASSICCSVDRIVER.propertyName.equalsIgnoreCase(moduleName)){
					DriverBLManager classicCSVDriverManager = new DriverBLManager();
					List<String> radiusClassicCSVDriver = new ArrayList<String>();
					radiusClassicCSVDriver.add(moduleName);
					classicCSVDriverManager.deleteByName(radiusClassicCSVDriver, staffData, radiusClassicCSVDriver);
				} else if(DefaultSetupClassNameAndProperty.RADIUSDBAUTHDRIVER.propertyName.equalsIgnoreCase(moduleName)){
					DriverBLManager dbAuthDriverManager = new DriverBLManager();
					List<String> radiusDBAuthDriver = new ArrayList<String>();
					radiusDBAuthDriver.add(moduleName);
					dbAuthDriverManager.deleteByName(radiusDBAuthDriver, staffData, radiusDBAuthDriver);
				} else if(DefaultSetupClassNameAndProperty.DATABASEDATASOURCE.propertyName.equalsIgnoreCase(moduleName)){
					DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
					List<String> databaseDatasource = new ArrayList<String>();
					databaseDatasource.add(moduleName);
					databaseDSBLManager.deleteDatabaseDSDetailByName(databaseDatasource, staffData);
				}
			}
			return mapping.findForward(DefaultSetupUtility.DEFAULT_SETUP);
		} catch (Exception e) {
			eliteAAASetupForm.setSuccess("false");
			request.setAttribute("eliteAAASetupForm", eliteAAASetupForm);
		}
		return mapping.findForward(DefaultSetupUtility.SETUP_CREATED);
	}
	
	private EliteAAASetupData convertFormToBean(EliteAAASetupForm eliteAAASetupForm) {
		EliteAAASetupData eliteAAASetupData = new EliteAAASetupData();
		eliteAAASetupData.setUserName(eliteAAASetupForm.getUserName());   
		eliteAAASetupData.setPassword(eliteAAASetupForm.getPassword());
		eliteAAASetupData.setMacAddress(eliteAAASetupForm.getMACAddress());
		eliteAAASetupData.setIpAddress(eliteAAASetupForm.getIPAddress());
		eliteAAASetupData.setConcurrentLoginLimit(eliteAAASetupForm.getConcurrentLoginLimit());
		return eliteAAASetupData;
		
	}
}
