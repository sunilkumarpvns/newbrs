package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy;

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
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.DiameterNASPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthMethodRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthPluginConfig;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.PolicyPluginConstants;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.UpdateNASServicePolicyAuthenticationParamsForm;

public class UpdateNASServicePolicyAuthenticationParametersAction extends BaseWebAction {
	
	
	private static final String SUCCESS_FORWARD = "updateNASServicePolicyAuthenticationParams";
	private static String ACTION_ALIAS = ConfigConstant.UPDATE_NAS_SERVICE_POLICY_AUTHENTICATE_PARAMS;

	private static final String MODULE = "UpdateNasServicePolicyAuthenticationParams";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try{
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
			UpdateNASServicePolicyAuthenticationParamsForm nasPolicyForm = (UpdateNASServicePolicyAuthenticationParamsForm)form;			
			
			if(nasPolicyForm.getAction() == null) {
				DiameterNASPolicyBLManager diameterNasBlManager = new DiameterNASPolicyBLManager();
				NASPolicyInstData policyData = diameterNasBlManager.getDiameterServicePolicyDataByPolicyId(nasPolicyForm.getNasPolicyId());			
				
				List<NASPolicyAuthDriverRelData> driverRelDataList = diameterNasBlManager.getDiameterServicePolicyAuthDriverRel(nasPolicyForm.getNasPolicyId());
				List<NASPolicyAuthMethodRelData> methodRelDataList = diameterNasBlManager.getDiameterServicePolicyAuthMethodRel(nasPolicyForm.getNasPolicyId());
				
				String[] selectedAuthMethods = null;
				if(methodRelDataList != null){		
					selectedAuthMethods = new String[methodRelDataList.size()];
					for(int i =0;i<methodRelDataList.size();i++){
						selectedAuthMethods[i] = methodRelDataList.get(i).getAuthMethodTypeId().toString();
					}
				}
				nasPolicyForm.setSelectedAuthMethods(selectedAuthMethods);
				nasPolicyForm.setSelectedAuthMethodTypes(selectedAuthMethods);

				//Additional Driver 
         		List<NASPolicyAdditionalDriverRelData> nasPolicyAdditionalDriverRelDataList = diameterNasBlManager.getDiameterServicePolicyAdditionalDriver(nasPolicyForm.getNasPolicyId());
				policyData.setNasPolicyAdditionalDriverRelDataList(nasPolicyAdditionalDriverRelDataList);
				
				nasPolicyForm.setDriversList(driverRelDataList);
				nasPolicyForm.setAuthScript(policyData.getAuthScript());
				nasPolicyForm.setCaseSensitiveUserIdentity(policyData.getCaseSensitiveUserIdentity());
				nasPolicyForm.setMultipleUserIdentity(policyData.getMultipleUserIdentity());
				nasPolicyForm.setNasPolicyId(policyData.getNasPolicyId());
				nasPolicyForm.setRealmPattern(policyData.getRealmPattern());
				nasPolicyForm.setRealmSeparator(policyData.getRealmSeparator());
				nasPolicyForm.setStripUserIdentity(new Boolean(policyData.getStripUserIdentity()));
				nasPolicyForm.setTrimPassword(new Boolean(policyData.getTrimPassword()));
				nasPolicyForm.setTrimUserIdentity(new Boolean(policyData.getTrimUserIdentity()));
				nasPolicyForm.setUserNameAttribute(policyData.getUserName());
				nasPolicyForm.setUserNameResonseAttributes(policyData.getUserNameResonseAttributes());
				nasPolicyForm.setAuditUId(policyData.getAuditUId());
				nasPolicyForm.setName(policyData.getName());
				nasPolicyForm.setAnonymousProfileIdentity(policyData.getAnonymousProfileIdentity());
				
				ServicePolicyBLManager servicePolicyBLManager = new ServicePolicyBLManager();
				List<AuthMethodTypeData> authMethodTypeDataList = servicePolicyBLManager.getAuthMethodTypeList();
				
				nasPolicyForm.setMethodTypeList(authMethodTypeDataList);
				
				// plugin related
				nasPolicyForm.setNasPolicyAuthPluginConfigList(policyData.getNasPolicyAuthPluginConfigList());
				
				PluginBLManager pluginBLManager = new PluginBLManager();			
				List<PluginInstData> pluginInstDataList = pluginBLManager.getDiameterPluginList();
				
				/* Driver Script */
				ScriptBLManager scriptBLManager = new ScriptBLManager();
				List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
			
				nasPolicyForm.setDriverScriptList(driverScriptList);
				
				request.setAttribute("pluginInstDataList", pluginInstDataList);
		
				request.setAttribute("nasPolicyForm",nasPolicyForm);
				request.setAttribute("nasPolicyInstData",policyData);
				return mapping.findForward(SUCCESS_FORWARD);
			}else if(nasPolicyForm.getAction().equals("update")){
				DiameterNASPolicyBLManager diameterNasBlManager = new DiameterNASPolicyBLManager();
				NASPolicyInstData policyData = diameterNasBlManager.getDiameterServicePolicyDataByPolicyId(nasPolicyForm.getNasPolicyId());			
				
				policyData.setNasPolicyId(nasPolicyForm.getNasPolicyId());
				policyData.setCaseSensitiveUserIdentity(nasPolicyForm.getCaseSensitiveUserIdentity());
				policyData.setMultipleUserIdentity(nasPolicyForm.getMultipleUserIdentity());
				policyData.setRealmPattern(nasPolicyForm.getRealmPattern());
				policyData.setRealmSeparator(nasPolicyForm.getRealmSeparator());
				policyData.setName(nasPolicyForm.getName());
				policyData.setAuditUId(nasPolicyForm.getAuditUId());
				
				if(nasPolicyForm.getStripUserIdentity() == null){
					policyData.setStripUserIdentity("false");
				}else{
					policyData.setStripUserIdentity(nasPolicyForm.getStripUserIdentity().toString());
				}
				if(nasPolicyForm.getTrimPassword() == null){
					policyData.setTrimPassword("false");
				}else{
					policyData.setTrimPassword(nasPolicyForm.getTrimPassword().toString());
				}
				if(nasPolicyForm.getTrimUserIdentity() == null){
					policyData.setTrimUserIdentity("false");
				}else{
					policyData.setTrimUserIdentity(nasPolicyForm.getTrimUserIdentity().toString());
				}
				
				policyData.setUserName(nasPolicyForm.getUserNameAttribute());
				policyData.setUserNameResonseAttributes(nasPolicyForm.getUserNameResonseAttributes());
				policyData.setAnonymousProfileIdentity(nasPolicyForm.getAnonymousProfileIdentity());
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				String[] selectedAuthMethods = nasPolicyForm.getSelectedAuthMethodTypes();
				List methodsList = new ArrayList();
				if(selectedAuthMethods != null){
					for(int i=0;i<selectedAuthMethods.length;i++){
						NASPolicyAuthMethodRelData data = new NASPolicyAuthMethodRelData();
						data.setNasPolicyId(nasPolicyForm.getNasPolicyId());
						data.setAuthMethodTypeId(Long.parseLong(selectedAuthMethods[i]));
						methodsList.add(data);
					}
				}
				policyData.setNasPolicyAuthMethodRelList(methodsList);
				
				// drivers Related
				List<NASPolicyAuthDriverRelData>  mainDriverList = getMainDriverList(nasPolicyForm.getSelecteddriverIds());				
				policyData.setNasPolicyAuthDriverRelList(mainDriverList);
				
				// Additional Driver Data
				List<NASPolicyAdditionalDriverRelData> nasPolicyAdditionalDriverRelDataList = getAdditionalDriverList(nasPolicyForm.getSelectedAdditionalDriverIds());
				policyData.setNasPolicyAdditionalDriverRelDataList(nasPolicyAdditionalDriverRelDataList);
				
				policyData.setAuthScript(nasPolicyForm.getAuthScript());
				
				// plugins related
				String authPrePlugin =  nasPolicyForm.getAuthPrePluginJson();
				String authPostPlugin = nasPolicyForm.getAuthPostPluginJson();
				
				List<NASPolicyAuthPluginConfig> nasPolicyPluginConfigList = new ArrayList<NASPolicyAuthPluginConfig>();
				
				/* Retrive Mappings of Pre Plug-in if Exist */
				if( authPrePlugin != null && authPrePlugin.length() > 0){
					JSONArray authPrePluginArray = JSONArray.fromObject(authPrePlugin);
					
					for(Object  obj: authPrePluginArray){
						NASPolicyAuthPluginConfig nasPolicyPluginConfig = (NASPolicyAuthPluginConfig) JSONObject.toBean((JSONObject) obj, NASPolicyAuthPluginConfig.class);
						nasPolicyPluginConfig.setPluginType(PolicyPluginConstants.IN_PLUGIN);
						nasPolicyPluginConfigList.add(nasPolicyPluginConfig);
					 }
				}
				
				if( authPostPlugin != null && authPostPlugin.length() > 0){
					JSONArray authPrePluginArray = JSONArray.fromObject(authPostPlugin);
					
					for(Object  obj: authPrePluginArray){
						NASPolicyAuthPluginConfig nasPolicyPluginConfig = (NASPolicyAuthPluginConfig) JSONObject.toBean((JSONObject) obj, NASPolicyAuthPluginConfig.class);
						nasPolicyPluginConfig.setPluginType(PolicyPluginConstants.OUT_PLUGIN);
						nasPolicyPluginConfigList.add(nasPolicyPluginConfig);
					 }
				}
				
				// Assign Plugin Information in Set
				policyData.setNasPolicyAuthPluginConfigList(nasPolicyPluginConfigList);
				
				//Set Flow Type
				policyData.setFlowType(PolicyPluginConstants.AUTH_FLOW);
				
				staffData.setAuditId(policyData.getAuditUId());
				staffData.setAuditName(policyData.getName());
				
				diameterNasBlManager.updateAuthenticationParams(policyData,staffData,ACTION_ALIAS);
				
				request.setAttribute("responseUrl","/viewNASServicePolicyDetail.do?nasPolicyId="+nasPolicyForm.getNasPolicyId());
                ActionMessage message = new ActionMessage("diameter.naspolicy.basic.update.success");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveMessages(request, messages);
                return mapping.findForward(SUCCESS);
				
			}			
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

		}
		return mapping.findForward(FAILURE);
	}
	
	
	private List<NASPolicyAuthDriverRelData> getMainDriverList(String mainDrivers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException, DataManagerException{
		if(mainDrivers!=null){
			List<NASPolicyAuthDriverRelData> mainDriverRelList= new ArrayList<NASPolicyAuthDriverRelData>();
			for (int i = 0; i < mainDrivers.length; i++) {
				DriverBLManager driverBLManager=new DriverBLManager();
				String[] driverInstanceIdWgtValues = mainDrivers[i].split("-");
				String driverInstanceId = driverInstanceIdWgtValues[0];				
				NASPolicyAuthDriverRelData mainDriverRelData = new NASPolicyAuthDriverRelData();
				DriverInstanceData driverInstanceData=driverBLManager.getDriverInstanceByDriverInstanceId(driverInstanceId);
				mainDriverRelData.setDriverInstanceId(driverInstanceId);
				if(driverInstanceIdWgtValues.length > 1)
					mainDriverRelData.setWeightage(Integer.parseInt(driverInstanceIdWgtValues[1].trim()));
				else
					mainDriverRelData.setWeightage(new Integer(1));
				mainDriverRelData.setDriverData(driverInstanceData);
				mainDriverRelList.add(mainDriverRelData);
			}
			return mainDriverRelList;
		}
		return null;
	}
	
	private List<NASPolicyAdditionalDriverRelData> getAdditionalDriverList(String additionalDrivers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException, DataManagerException{
		if(additionalDrivers!=null){
			List<NASPolicyAdditionalDriverRelData> additionalDriverRelList= new ArrayList<NASPolicyAdditionalDriverRelData>();
			for (int i = 0; i < additionalDrivers.length; i++) {
				DriverBLManager driverBLManager=new DriverBLManager();
				NASPolicyAdditionalDriverRelData nasPolicyAdditionalDriverRelData = new NASPolicyAdditionalDriverRelData();
				nasPolicyAdditionalDriverRelData.setDriverInstanceId(additionalDrivers[i]);
				DriverInstanceData driverInstanceData=driverBLManager.getDriverInstanceByDriverInstanceId(additionalDrivers[i]);
				nasPolicyAdditionalDriverRelData.setDriverInstanceData(driverInstanceData);
				nasPolicyAdditionalDriverRelData.setOrderNumber((long)i+1); 
				additionalDriverRelList.add(nasPolicyAdditionalDriverRelData);
			}
			return additionalDriverRelList;
		}
		return null;
	}
	
}
