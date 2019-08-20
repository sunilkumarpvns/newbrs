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

import com.elitecore.elitesm.blmanager.servicepolicy.diameter.DiameterNASPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.PolicyPluginConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.AddNASServicePolicyDetailForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.AddNASServicePolicyForm;
public class AddNASServicePolicyDetailAction extends BaseNASServicePolicyAction{
	private static final String SUCCESS_FORWARD = "success";
	private static String MODULE = "AddNASServicePolicyDetailAction";
	private static String ACTION_ALIAS = ConfigConstant.CREATE_NAS_SERVICE_POLICY;
		
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		checkActionPermission(request,ACTION_ALIAS);
		Logger.logInfo(MODULE, "Entered execute method of : :"+getClass().getName());
		AddNASServicePolicyForm addNASServicePolicyForm=null;
		try{
			checkActionPermission(request, ACTION_ALIAS);
			DiameterNASPolicyBLManager diameterNASPolicyBLManager = new DiameterNASPolicyBLManager();
			addNASServicePolicyForm = (AddNASServicePolicyForm)request.getSession().getAttribute("addNASServicePolicyForm");

			/*
			 * Driver List
			 */
			AddNASServicePolicyDetailForm addNASServicePolicyDetailForm=(AddNASServicePolicyDetailForm)form;
			
			
			NASPolicyInstData nasPolicyInstData = convertFormToBean(addNASServicePolicyForm);
			
			String nasAuthDrivers[] = request.getParameterValues("nasAuthDrivers");
			String nasAcctDrivers[] = request.getParameterValues("nasAcctDrivers");
			
			List<NASPolicyAuthDriverRelData> nasAuthDriverList=getNASAuthDriverList(nasAuthDrivers);
			List<NASPolicyAcctDriverRelData> nasAcctDriverList=getNASAcctDriverList(nasAcctDrivers);
			
			nasPolicyInstData.setNasPolicyAuthDriverRelList(nasAuthDriverList);
			nasPolicyInstData.setNasPolicyAcctDriverRelList(nasAcctDriverList);
			nasPolicyInstData.setAuthScript(addNASServicePolicyDetailForm.getAuthScript());
			nasPolicyInstData.setAcctScript(addNASServicePolicyDetailForm.getAcctScript());
			// Additional Driver
			String additionalDriverGroup[] = request.getParameterValues("selectedAdditionalDriverIds");
			List<NASPolicyAdditionalDriverRelData> nasPolicyAdditionalDriverRelDataList = getAdditionalDriverList(additionalDriverGroup);
			nasPolicyInstData.setNasPolicyAdditionalDriverRelDataList(nasPolicyAdditionalDriverRelDataList);
			
			/*
			 * Plugins
			 */
			
			String authPrePlugin =  addNASServicePolicyDetailForm.getAuthPrePluginJson();
			String authPostPlugin = addNASServicePolicyDetailForm.getAuthPostPluginJson();
			
			String acctPrePlugin = addNASServicePolicyDetailForm.getAcctPrePluginJson();
			String acctPostPlugin = addNASServicePolicyDetailForm.getAcctPostPluginJson();
			
			List<NASPolicyAuthPluginConfig> nasPolicyAuthPluginConfigList = new ArrayList<NASPolicyAuthPluginConfig>();
			
			/* Retrive Mappings of Pre Plug-in if Exist */
			if( authPrePlugin != null && authPrePlugin.length() > 0){
				JSONArray authPrePluginArray = JSONArray.fromObject(authPrePlugin);
				
				for(Object  obj: authPrePluginArray){
					NASPolicyAuthPluginConfig nasPolicyPluginConfig = (NASPolicyAuthPluginConfig) JSONObject.toBean((JSONObject) obj, NASPolicyAuthPluginConfig.class);
					nasPolicyPluginConfig.setPluginType(PolicyPluginConstants.IN_PLUGIN);
					nasPolicyAuthPluginConfigList.add(nasPolicyPluginConfig);
				 }
			}
			
			if( authPostPlugin != null && authPostPlugin.length() > 0){
				JSONArray authPrePluginArray = JSONArray.fromObject(authPostPlugin);
				
				for(Object  obj: authPrePluginArray){
					NASPolicyAuthPluginConfig nasPolicyPluginConfig = (NASPolicyAuthPluginConfig) JSONObject.toBean((JSONObject) obj, NASPolicyAuthPluginConfig.class);
					nasPolicyPluginConfig.setPluginType(PolicyPluginConstants.OUT_PLUGIN);
					nasPolicyAuthPluginConfigList.add(nasPolicyPluginConfig);
				 }
			}
			
			List<NASPolicyAcctPluginConfig> nasPolicyAcctPluginConfigList = new ArrayList<NASPolicyAcctPluginConfig>();
			if( acctPrePlugin != null && acctPrePlugin.length() > 0){
				JSONArray authPrePluginArray = JSONArray.fromObject(acctPrePlugin);
				
				for(Object  obj: authPrePluginArray){
					NASPolicyAcctPluginConfig nasPolicyPluginConfig = (NASPolicyAcctPluginConfig) JSONObject.toBean((JSONObject) obj, NASPolicyAcctPluginConfig.class);
					nasPolicyPluginConfig.setPluginType(PolicyPluginConstants.IN_PLUGIN);
					nasPolicyAcctPluginConfigList.add(nasPolicyPluginConfig);
				 }
			}
			
			if( acctPostPlugin != null && acctPostPlugin.length() > 0){
				JSONArray authPrePluginArray = JSONArray.fromObject(acctPostPlugin);
				
				for(Object  obj: authPrePluginArray){
					NASPolicyAcctPluginConfig nasPolicyPluginConfig = (NASPolicyAcctPluginConfig) JSONObject.toBean((JSONObject) obj, NASPolicyAcctPluginConfig.class);
					nasPolicyPluginConfig.setPluginType(PolicyPluginConstants.OUT_PLUGIN);
					nasPolicyAcctPluginConfigList.add(nasPolicyPluginConfig);
				 }
			}
			
			// Assign Plugin Information in Set
			if( nasPolicyAuthPluginConfigList != null && nasPolicyAuthPluginConfigList.size() > 0 )
				nasPolicyInstData.setNasPolicyAuthPluginConfigList(nasPolicyAuthPluginConfigList);
			
			if( nasPolicyAcctPluginConfigList != null && nasPolicyAcctPluginConfigList.size() > 0 )
				nasPolicyInstData.setNasPolicyAcctPluginConfigList(nasPolicyAcctPluginConfigList);
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			diameterNASPolicyBLManager.createPolicy(nasPolicyInstData, staffData);
	        		
			request.setAttribute("responseUrl","/initSearchNASServicePolicy"); 
			ActionMessage message = new ActionMessage("diameter.naspolicy.create.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveMessages(request,messages);
			return mapping.findForward(SUCCESS_FORWARD);

		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (DuplicateInstanceNameFoundException dpfExp) {
	        Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
	        Logger.logTrace(MODULE,dpfExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
			request.setAttribute("errorDetails", errorElements);
	        ActionMessage message = new ActionMessage("diameter.naspolicy.create.duplicate.failure",addNASServicePolicyForm.getName());
	        ActionMessages messages = new ActionMessages();
	        messages.add("information",message);
	        saveErrors(request,messages);
	   }catch(DataManagerException e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("diameter.naspolicy.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("diameter.naspolicy.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}

		return mapping.findForward(FAILURE);
	}
	private List<NASPolicyAuthDriverRelData> getNASAuthDriverList(String nasDrivers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException{
		if(nasDrivers!=null){
			List<NASPolicyAuthDriverRelData> nasDriverRelList= new ArrayList<NASPolicyAuthDriverRelData>();
			for (int i = 0; i < nasDrivers.length; i++) {
				
				String[] driverInstanceIdWgtValues = nasDrivers[i].split("-");
				
				String driverInstanceId = driverInstanceIdWgtValues[0];				
				NASPolicyAuthDriverRelData nasDriverRelData = new NASPolicyAuthDriverRelData();
				nasDriverRelData.setDriverInstanceId(driverInstanceId);
				nasDriverRelData.setWeightage(Integer.parseInt(driverInstanceIdWgtValues[1].trim()));
				nasDriverRelList.add(nasDriverRelData);
			}
			return nasDriverRelList;
		}
		return null;
	}
	
	private List<NASPolicyAcctDriverRelData> getNASAcctDriverList(String nasDrivers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException{
		if(nasDrivers!=null){
			List<NASPolicyAcctDriverRelData> nasDriverRelList= new ArrayList<NASPolicyAcctDriverRelData>();
			for (int i = 0; i < nasDrivers.length; i++) {
				
				String[] driverInstanceIdWgtValues = nasDrivers[i].split("-");
				
				String driverInstanceId = driverInstanceIdWgtValues[0];				
				NASPolicyAcctDriverRelData nasDriverRelData = new NASPolicyAcctDriverRelData();
				nasDriverRelData.setDriverInstanceId(driverInstanceId);
				nasDriverRelData.setWeightage(Integer.parseInt(driverInstanceIdWgtValues[1].trim()));
				nasDriverRelList.add(nasDriverRelData);
			}
			return nasDriverRelList;
		}
		return null;
	}	
	
	private List<NASPolicyAdditionalDriverRelData> getAdditionalDriverList(String additionalDrivers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException{
		if(additionalDrivers!=null){
			List<NASPolicyAdditionalDriverRelData> additionalDriverRelList= new ArrayList<NASPolicyAdditionalDriverRelData>();
			for (int i = 0; i < additionalDrivers.length; i++) {
				NASPolicyAdditionalDriverRelData nasPolicyAdditionalDriverRelData = new NASPolicyAdditionalDriverRelData();
				nasPolicyAdditionalDriverRelData.setDriverInstanceId(additionalDrivers[i]);
				nasPolicyAdditionalDriverRelData.setOrderNumber((long)i+1); 
				additionalDriverRelList.add(nasPolicyAdditionalDriverRelData);
			}
			return additionalDriverRelList;
		}
		return null;
	}
	
}
