package com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.EAPPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyAuthDriverRelationData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyPluginConfig;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.PolicyPluginConstants;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.constants.ServiceTypeConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms.UpdateEAPPolicyForm;

public class UpdateEAPPolicyDriverProfileAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_EAP_PROFILE_DRIVER;
	private static final String UPDATE_EAP_DRIVER_PROFILE = "updateEAPDriverProfiles";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		try{
			Logger.logInfo(ACTION_ALIAS, "Enter execute method of "+getClass().getName());
			checkActionPermission(request, ACTION_ALIAS);
			UpdateEAPPolicyForm updateEAPPolicyForm = (UpdateEAPPolicyForm) form;
			EAPPolicyBLManager blManager = new EAPPolicyBLManager();	
			EAPPolicyData eapPolicyData;
			if("update".equals(updateEAPPolicyForm.getAction())){
				eapPolicyData = new EAPPolicyData();
				eapPolicyData = blManager.getEAPPolicyById(updateEAPPolicyForm.getPolicyId());
				eapPolicyData.setEapPolicyId(updateEAPPolicyForm.getPolicyId());
				eapPolicyData.setScript(updateEAPPolicyForm.getScript());
				eapPolicyData.setAuditUId(updateEAPPolicyForm.getAuditUId());
				eapPolicyData.setName(updateEAPPolicyForm.getName());

				//Update pre-plugin and post-plugin
				String prePluginJson = updateEAPPolicyForm.getPrePluginsList();
				String postPluginJson = updateEAPPolicyForm.getPostPluginList();
				List<EAPPolicyPluginConfig> eapPolicyPluginConfigList=new ArrayList<EAPPolicyPluginConfig>();
				eapPolicyPluginConfigList.addAll(getEAPPolicyPluginConfigListFromJsonAry(prePluginJson, PolicyPluginConstants.IN_PLUGIN));
				eapPolicyPluginConfigList.addAll(getEAPPolicyPluginConfigListFromJsonAry(postPluginJson, PolicyPluginConstants.OUT_PLUGIN));
				eapPolicyData.setEapPolicyPluginConfigList(eapPolicyPluginConfigList);
				
				/* Driver */ 
				String[] driversList = (String[])request.getParameterValues("selecteddriverIds");
				List<EAPPolicyAuthDriverRelationData> mainDriverList = getMainDriverList(driversList);
				eapPolicyData.setDriverList(mainDriverList);
				
				/*Additional Driver*/
				String additionalDriverGroup[] = request.getParameterValues("selectedAdditionalDriverIds");
				List<EAPPolicyAdditionalDriverRelData> eapAdditionalDriverRelDataList = getEAPPolicyAdditionalDriverRelDataList(additionalDriverGroup);
				eapPolicyData.setEapAdditionalDriverRelDataList(eapAdditionalDriverRelDataList);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				staffData.setAuditId(eapPolicyData.getAuditUId());
				staffData.setAuditName(eapPolicyData.getName());
				/*Update in DataBase*/
				blManager.updateDriverProfile(eapPolicyData,staffData,ACTION_ALIAS);
				request.setAttribute("responseUrl", "/initViewEAPPolicy.do?policyId="+eapPolicyData.getEapPolicyId()); 
				ActionMessage message = new ActionMessage("diameter.eappolicy.update");
				ActionMessages messages = new ActionMessages();          
				messages.add("information", message);                    
				saveMessages(request,messages);         				   
				return mapping.findForward(SUCCESS);
			}else{
				eapPolicyData = blManager.getEAPPolicyById(updateEAPPolicyForm.getPolicyId());
				updateEAPPolicyForm.setDriversList(eapPolicyData.getDriverList());
				updateEAPPolicyForm.setAdditionalDriverRelDataList(eapPolicyData.getEapAdditionalDriverRelDataList());
				updateEAPPolicyForm.setScript(eapPolicyData.getScript());
				updateEAPPolicyForm.setName(eapPolicyData.getName());
				updateEAPPolicyForm.setAuditUId(eapPolicyData.getAuditUId());
				/*updateEAPPolicyForm.setPostPluginList(eapPolicyData.getPostPlugins());
				updateEAPPolicyForm.setPrePluginsList(eapPolicyData.getPrePlugins());*/
				
				DriverBLManager driverManager = new DriverBLManager();
				List<DriverInstanceData> listOfDriver = driverManager.getDriverInstanceList(ServiceTypeConstants.NAS_AUTH_APPLICATION);
				
				/** fetching pre and post plugins */
				List<EAPPolicyPluginConfig> eapPolicyPluginConfigList = blManager.getPolicyPluginConfigList(updateEAPPolicyForm.getPolicyId());
				eapPolicyData.setEapPolicyPluginConfigList(eapPolicyPluginConfigList);
				
				PluginBLManager pluginBLManager = new PluginBLManager();
				List<PluginInstData> pluginInstDataList = pluginBLManager.getDiameterPluginList();
			
				/* Driver Script */
				ScriptBLManager scriptBLManager = new ScriptBLManager();
				List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
			
				updateEAPPolicyForm.setDriverScriptList(driverScriptList);
				
				request.setAttribute("pluginInstDataList", pluginInstDataList);
				request.setAttribute("driverList", listOfDriver);
				request.setAttribute("eapPolicyData", eapPolicyData);
				return mapping.findForward(UPDATE_EAP_DRIVER_PROFILE);
			}
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
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + e.getMessage());                              
			Logger.logTrace(MODULE, e);                                                                                               
			ActionMessage message = new ActionMessage("diameter.eappolicy.updateerror");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);  
		}
		return mapping.findForward(FAILURE);
	}
	
	
	private List<EAPPolicyAuthDriverRelationData> getMainDriverList(String mainDrivers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException, DataManagerException{
		if(mainDrivers!=null){
			List<EAPPolicyAuthDriverRelationData> mainDriverRelList= new ArrayList<EAPPolicyAuthDriverRelationData>();
			for (int i = 0; i < mainDrivers.length; i++) {
				DriverBLManager driverBLManager=new DriverBLManager();
				String[] driverInstanceIdWgtValues = mainDrivers[i].split("-");
				String driverInstanceId = driverInstanceIdWgtValues[0];				
				EAPPolicyAuthDriverRelationData mainDriverRelData = new EAPPolicyAuthDriverRelationData();
				DriverInstanceData driverInstanceData=driverBLManager.getDriverInstanceByDriverInstanceId(driverInstanceId);
				mainDriverRelData.setDriverInstanceId(driverInstanceId);
				mainDriverRelData.setWeightage(Integer.parseInt(driverInstanceIdWgtValues[1].trim()));
				mainDriverRelData.setDriverData(driverInstanceData);
				mainDriverRelList.add(mainDriverRelData);
			}
			return mainDriverRelList;
		}
		return null;
	}
	
	private List<EAPPolicyAdditionalDriverRelData> getEAPPolicyAdditionalDriverRelDataList(String additionalDrivers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException, DataManagerException{
		if(additionalDrivers != null) {
			 List<EAPPolicyAdditionalDriverRelData> eapPolicyAdditionalDriverRelDataList = new ArrayList<EAPPolicyAdditionalDriverRelData>();
			for (int i = 0; i < additionalDrivers.length; i++) {
				DriverBLManager driverBLManager=new DriverBLManager();
				EAPPolicyAdditionalDriverRelData eapPolicyAdditionalDriverRelData = new EAPPolicyAdditionalDriverRelData();
				eapPolicyAdditionalDriverRelData.setDriverInstanceId(additionalDrivers[i]);
				DriverInstanceData driverInstanceData=driverBLManager.getDriverInstanceByDriverInstanceId(additionalDrivers[i]);
				eapPolicyAdditionalDriverRelData.setOrderNumber((long) i+1);
				eapPolicyAdditionalDriverRelData.setDriverInstanceData(driverInstanceData);
				eapPolicyAdditionalDriverRelDataList.add(eapPolicyAdditionalDriverRelData);
			}
			return eapPolicyAdditionalDriverRelDataList;
		}
		return null;
	}
	
	private List<EAPPolicyPluginConfig> getEAPPolicyPluginConfigListFromJsonAry(String configJsonArray,String pluginPolicyConstant) {
		List<EAPPolicyPluginConfig> eapPolicyPluginConfigList = new ArrayList<EAPPolicyPluginConfig>();
		
		if( configJsonArray != null && configJsonArray.length() > 0){
            JSONArray postPluginJsonArray = JSONArray.fromObject(configJsonArray);
            
            for(Object  obj: postPluginJsonArray){
            	EAPPolicyPluginConfig eapPolicyPluginConfig = (EAPPolicyPluginConfig) JSONObject.toBean((JSONObject) obj, EAPPolicyPluginConfig.class);
            	eapPolicyPluginConfig.setPluginType(pluginPolicyConstant);
            	eapPolicyPluginConfigList.add(eapPolicyPluginConfig);
            }
		}
		return eapPolicyPluginConfigList;
		
	}
}
