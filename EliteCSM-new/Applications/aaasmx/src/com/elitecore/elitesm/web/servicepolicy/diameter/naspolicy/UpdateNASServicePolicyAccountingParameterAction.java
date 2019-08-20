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
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.DiameterNASPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthPluginConfig;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.PolicyPluginConstants;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.UpdateNASServicePolicyAccountingParamsForm;

public class UpdateNASServicePolicyAccountingParameterAction extends BaseWebAction{
	
	
	private static final String SUCCESS_FORWARD = "updateNASServicePolicyAccountingParams";
	private static String ACTION_ALIAS = ConfigConstant.UPDATE_NAS_SERVICE_POLICY_ACCOUNTING_PARAMS;

	private static final String MODULE = "UpdateNasServicePolicyAccountingParams";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try{
			
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
			UpdateNASServicePolicyAccountingParamsForm nasPolicyForm = (UpdateNASServicePolicyAccountingParamsForm)form;			
			
			if(nasPolicyForm.getAction() == null) {
				
				DiameterNASPolicyBLManager blManager = new DiameterNASPolicyBLManager();
				NASPolicyInstData data = blManager.getDiameterServicePolicyDataByPolicyId(nasPolicyForm.getNasPolicyId());
				
				List<NASPolicyAcctDriverRelData> driverRelDataList = blManager.getDiameterServicePolicyAcctDriverRel(nasPolicyForm.getNasPolicyId());
				
				nasPolicyForm.setDriversList(driverRelDataList);
				nasPolicyForm.setAcctScript(data.getAcctScript());
				nasPolicyForm.setAuditUId(data.getAuditUId());
				nasPolicyForm.setName(data.getName());
				
				//get all plugin list
				nasPolicyForm.setNasPolicyAcctPluginConfigList(data.getNasPolicyAcctPluginConfigList());
				
				PluginBLManager pluginBLManager = new PluginBLManager();			
				List<PluginInstData> pluginInstDataList = pluginBLManager.getDiameterPluginList();
				
				/* Driver Script */
				ScriptBLManager scriptBLManager = new ScriptBLManager();
				List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
			
				nasPolicyForm.setDriverScriptList(driverScriptList);
				
				request.setAttribute("pluginInstDataList", pluginInstDataList);
				
				request.setAttribute("nasPolicyInstData",data);
				request.setAttribute("acctParmaForm",nasPolicyForm);
				return mapping.findForward(SUCCESS_FORWARD);
			}else if(nasPolicyForm.getAction().equals("update")){
				
				DiameterNASPolicyBLManager blManager = new DiameterNASPolicyBLManager();
				NASPolicyInstData policyInstData = blManager.getDiameterServicePolicyDataByPolicyId(nasPolicyForm.getNasPolicyId());
				
				policyInstData.setNasPolicyId(nasPolicyForm.getNasPolicyId());
				
				List<NASPolicyAcctDriverRelData>  mainDriverList = getMainDriverList(nasPolicyForm.getSelecteddriverIds());				
				policyInstData.setNasPolicyAcctDriverRelList(mainDriverList);
				
				policyInstData.setAcctScript(nasPolicyForm.getAcctScript());
				policyInstData.setName(nasPolicyForm.getName());
				policyInstData.setAuditUId(nasPolicyForm.getAuditUId());
				
				// plugins related
				String acctPrePlugin =  nasPolicyForm.getAcctPrePluginJson();
				String acctPostPlugin = nasPolicyForm.getAcctPostPluginJson();
				
				List<NASPolicyAcctPluginConfig> nasPolicyPluginConfigList = new ArrayList<NASPolicyAcctPluginConfig>();
				
				/* Retrive Mappings of Pre Plug-in if Exist */
				if( acctPrePlugin != null && acctPrePlugin.length() > 0){
					JSONArray acctPrePluginArray = JSONArray.fromObject(acctPrePlugin);
					
					for(Object  obj: acctPrePluginArray){
						NASPolicyAcctPluginConfig nasPolicyPluginConfig = (NASPolicyAcctPluginConfig) JSONObject.toBean((JSONObject) obj, NASPolicyAcctPluginConfig.class);
						nasPolicyPluginConfig.setPluginType(PolicyPluginConstants.IN_PLUGIN);
						nasPolicyPluginConfigList.add(nasPolicyPluginConfig);
					 }
				}
				
				if( acctPostPlugin != null && acctPostPlugin.length() > 0){
					JSONArray acctPostPluginArray = JSONArray.fromObject(acctPostPlugin);
					
					for(Object  obj: acctPostPluginArray){
						NASPolicyAcctPluginConfig nasPolicyPluginConfig = (NASPolicyAcctPluginConfig) JSONObject.toBean((JSONObject) obj, NASPolicyAcctPluginConfig.class);
						nasPolicyPluginConfig.setPluginType(PolicyPluginConstants.OUT_PLUGIN);
						nasPolicyPluginConfigList.add(nasPolicyPluginConfig);
					 }
				}
				
				/* Assign Plugin Information in Set */
				policyInstData.setNasPolicyAcctPluginConfigList(nasPolicyPluginConfigList);
				
				/* set plugin flow type */
				policyInstData.setFlowType(PolicyPluginConstants.ACCT_FLOW);
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				staffData.setAuditId(policyInstData.getAuditUId());
				staffData.setAuditName(policyInstData.getName());
				
				blManager.updateAccountingParams(policyInstData,staffData,ACTION_ALIAS);
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
	
	
	private List<NASPolicyAcctDriverRelData> getMainDriverList(String mainDrivers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException, DataManagerException{
		if(mainDrivers!=null){
			List<NASPolicyAcctDriverRelData> mainDriverRelList= new ArrayList<NASPolicyAcctDriverRelData>();
			for (int i = 0; i < mainDrivers.length; i++) {
				DriverBLManager driverBLManager=new DriverBLManager();
				String[] driverInstanceIdWgtValues = mainDrivers[i].split("-");
				String driverInstanceId = driverInstanceIdWgtValues[0];				
				NASPolicyAcctDriverRelData mainDriverRelData = new NASPolicyAcctDriverRelData();
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
}
