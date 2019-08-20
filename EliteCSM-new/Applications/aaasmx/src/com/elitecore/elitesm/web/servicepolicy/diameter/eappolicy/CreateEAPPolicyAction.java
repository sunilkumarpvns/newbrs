package com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.EAPPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyAuthDriverRelationData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPResponseAttributes;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.PolicyPluginConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms.CreateEAPPolicyForm;

public class CreateEAPPolicyAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_EAP_POLICY;
	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="EAPPOLICY";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			CreateEAPPolicyForm createEAPPolicyForm = (CreateEAPPolicyForm)form;
			String[] driversList = (String[])request.getParameterValues("selecteddriverIds");

			List<EAPPolicyAuthDriverRelationData> mainDriverList = getMainDriverList(driversList);
			
			EAPPolicyData eapPolicyData = convertFormToBean(createEAPPolicyForm);
			
			String statusCheckBox = createEAPPolicyForm.getStatus();
			if(statusCheckBox == null || statusCheckBox.equalsIgnoreCase("")) {
				createEAPPolicyForm.setStatus("0");
			} else {
				createEAPPolicyForm.setStatus("1");   
			}
			
			if(BaseConstant.HIDE_STATUS.equals(createEAPPolicyForm.getStatus())){
				eapPolicyData.setStatus(BaseConstant.HIDE_STATUS_ID);
			}else{
				eapPolicyData.setStatus(BaseConstant.SHOW_STATUS_ID);
			}
			
			eapPolicyData.setDriverList(mainDriverList);
			
			//Additional Driver
			String additionalDriverGroup[] = request.getParameterValues("selectedAdditionalDriverIds");
			List<EAPPolicyAdditionalDriverRelData> eapAdditionalDriverRelDataList = getEAPPolicyAdditionalDriverRelDataList(additionalDriverGroup);
			eapPolicyData.setEapAdditionalDriverRelDataList(eapAdditionalDriverRelDataList);
			
			//Get Response Attributes data
			
			String commandCodes[] = request.getParameterValues("commandCode");
			String responseAttributes[] = request.getParameterValues("responseAttributes");
			
			Set<EAPResponseAttributes> eapResponseAttributesSet = new LinkedHashSet<EAPResponseAttributes>();
			
			if( commandCodes!=null ){
				for (int j = 0; j < commandCodes.length; j++) {
					if(commandCodes[j]!=null && commandCodes[j].trim().length()>0){
						
						EAPResponseAttributes eapResponseAttributes = new EAPResponseAttributes();
						eapResponseAttributes.setCommandCodes(commandCodes[j]);
						eapResponseAttributes.setResponseAttributes(responseAttributes[j]);
					
						eapResponseAttributesSet.add(eapResponseAttributes);
						Logger.logInfo(MODULE, "nasResponseAttributes: "+ eapResponseAttributes);
					}
				}
			}
			
			eapPolicyData.setEapResponseAttributesSet(eapResponseAttributesSet);

			EAPPolicyBLManager blManager = new EAPPolicyBLManager();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String actionAlias=ACTION_ALIAS;
			blManager.create(eapPolicyData, staffData);
			doAuditing(staffData, actionAlias);
			
			request.setAttribute("responseUrl", "/initSearchEAPPolicy");      
			ActionMessage message = new ActionMessage("diameter.eappolicy.create");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);         				   
			Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName()); 
			return mapping.findForward(SUCCESS_FORWARD);             
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	

		}catch (DuplicateInstanceNameFoundException authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("diameter.eappolicy.duplicate");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);       
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("diameter.eappolicy.error");                                                         		    
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		return mapping.findForward(FAILURE_FORWARD);
	}      

	private EAPPolicyData convertFormToBean(CreateEAPPolicyForm createEAPPolicyForm) {
		EAPPolicyData eapPolicyData = new EAPPolicyData();
		
		/* Basic Details */
		eapPolicyData.setDescription(createEAPPolicyForm.getDescription());
		eapPolicyData.setName(createEAPPolicyForm.getName());
		eapPolicyData.setOrderNumber(createEAPPolicyForm.getOrderNumber());			 
		eapPolicyData.setRuleSet(createEAPPolicyForm.getRuleSet());
		eapPolicyData.setSessionManagement(createEAPPolicyForm.getSessionManagement());
		eapPolicyData.setStatus("CST01");
		eapPolicyData.setEapConfigId(createEAPPolicyForm.getEapConfigId());
		eapPolicyData.setRequestType(createEAPPolicyForm.getRequestType());
		eapPolicyData.setDefaultResponseBehaviorArgument(createEAPPolicyForm.getDefaultResponseBehaviorArgument());
		eapPolicyData.setDefaultResponseBehaviour(createEAPPolicyForm.getDefaultResponseBehaviour());
		
		/* Authentication Parameter */
		eapPolicyData.setMultipleUserIdentity(createEAPPolicyForm.getMultipleUserIdentity());
		eapPolicyData.setStripUserIdentity(createEAPPolicyForm.getStripUserIdentity());
		eapPolicyData.setRealmPattern(createEAPPolicyForm.getRealmPattern());
		eapPolicyData.setRealmSeparator(createEAPPolicyForm.getRealmSeparator());
		eapPolicyData.setTrimUserIdentity(createEAPPolicyForm.getTrimUserIdentity());
		eapPolicyData.setTrimPassword(createEAPPolicyForm.getTrimPassword());
		eapPolicyData.setCaseSensitiveUserIdentity(createEAPPolicyForm.getCaseSensitiveUserIdentity());
		eapPolicyData.setAnonymousProfileIdentity(createEAPPolicyForm.getAnonymousProfileIdentity());
		
		/* Authorizations Parameter */
		eapPolicyData.setWimax(createEAPPolicyForm.getWimax());
		eapPolicyData.setRejectOnCheckItemNotFound(createEAPPolicyForm.getRejectOnCheckItemNotFound());
		eapPolicyData.setRejectOnRejectItemNotFound(createEAPPolicyForm.getRejectOnRejectItemNotFound());
		eapPolicyData.setActionOnPolicyNotFound(createEAPPolicyForm.getActionOnPolicyNotFound());
		
		eapPolicyData.setGracePolicy(createEAPPolicyForm.getGracePolicy());
		
		if(Strings.isNullOrBlank(createEAPPolicyForm.getDiameterConcurrency()) || "0".equals(createEAPPolicyForm.getDiameterConcurrency())){
			eapPolicyData.setDiameterConcurrency(null);
		}else{
			eapPolicyData.setDiameterConcurrency(createEAPPolicyForm.getDiameterConcurrency());
		}
		
		if(Strings.isNullOrBlank(createEAPPolicyForm.getAdditionalDiameterConcurrency()) || "0".equals(createEAPPolicyForm.getAdditionalDiameterConcurrency())){
			eapPolicyData.setAdditionalDiameterConcurrency(null);
		}else{
			eapPolicyData.setAdditionalDiameterConcurrency(createEAPPolicyForm.getAdditionalDiameterConcurrency());
		}
		
		eapPolicyData.setCui(createEAPPolicyForm.getCui());
		eapPolicyData.setCuiResponseAttributes(createEAPPolicyForm.getCuiResponseAttributes());
		eapPolicyData.setAdvancedCuiExpression(createEAPPolicyForm.getAdvancedCuiExpression());
		
		eapPolicyData.setScript(createEAPPolicyForm.getScript());
		
		List<EAPPolicyPluginConfig> eapPolicyPluginConfigList = new ArrayList<EAPPolicyPluginConfig>();
		eapPolicyPluginConfigList.addAll(getEAPPolicyPluginConfigListFromJsonAry(createEAPPolicyForm.getPrePluginsList(), PolicyPluginConstants.IN_PLUGIN));
		eapPolicyPluginConfigList.addAll(getEAPPolicyPluginConfigListFromJsonAry(createEAPPolicyForm.getPostPluginList(), PolicyPluginConstants.OUT_PLUGIN));
		eapPolicyData.setEapPolicyPluginConfigList(eapPolicyPluginConfigList);
		
		eapPolicyData.setDefaultSessionTimeout(createEAPPolicyForm.getDefaultSessionTimeout());
		return eapPolicyData;
	}

	private List<EAPPolicyAuthDriverRelationData> getMainDriverList(String mainDrivers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException{
		if(mainDrivers!=null){
			List<EAPPolicyAuthDriverRelationData> mainDriverRelList= new ArrayList<EAPPolicyAuthDriverRelationData>();
			for (int i = 0; i < mainDrivers.length; i++) {
				String[] driverInstanceIdWgtValues = mainDrivers[i].split("-");
				String driverInstanceId = driverInstanceIdWgtValues[0];				
				EAPPolicyAuthDriverRelationData mainDriverRelData = new EAPPolicyAuthDriverRelationData();
				mainDriverRelData.setDriverInstanceId(driverInstanceId);
				mainDriverRelData.setWeightage(Integer.parseInt(driverInstanceIdWgtValues[1].trim()));
				mainDriverRelList.add(mainDriverRelData);
			}
			return mainDriverRelList;
		}
		return null;
	}
	
	private List<EAPPolicyAdditionalDriverRelData> getEAPPolicyAdditionalDriverRelDataList(String additionalDrivers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException{
		if(additionalDrivers != null) {
			 List<EAPPolicyAdditionalDriverRelData> eapPolicyAdditionalDriverRelDataList = new ArrayList<EAPPolicyAdditionalDriverRelData>();
			for (int i = 0; i < additionalDrivers.length; i++) {
				EAPPolicyAdditionalDriverRelData eapPolicyAdditionalDriverRelData = new EAPPolicyAdditionalDriverRelData();
				eapPolicyAdditionalDriverRelData.setDriverInstanceId(additionalDrivers[i]);
				eapPolicyAdditionalDriverRelData.setOrderNumber((long) i+1);
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
