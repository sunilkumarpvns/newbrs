/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   CreateCcpolicyAction.java                 		
 * ModualName CreditControlPolicy    			      		
 * Created on 13 April, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy; 

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

import com.elitecore.elitesm.blmanager.servicepolicy.diameter.CreditControlPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CCPolicyPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CCResponseAttributes;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlDriverRelationData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.PolicyPluginConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy.forms.CreateCreditControlPolicyForm;

public class CreateCreditControlPolicyAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CREDITCONTROLPOLICY";
	private static final String ACTION_ALIAS=ConfigConstant.CREATE_CREDIT_CONTROL_SERVICE_POLICY;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {


			checkActionPermission(request,ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 

			CreateCreditControlPolicyForm policyForm = (CreateCreditControlPolicyForm)form;

			CreditControlPolicyData policyData = new CreditControlPolicyData();

			String[] driversList = (String[])request.getParameterValues("selecteddriverIds");

			List<CreditControlDriverRelationData> mainDriverList = getMainDriverList(driversList);

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			String statusCheckBox = policyForm.getStatus();
			if(statusCheckBox == null || statusCheckBox.equalsIgnoreCase("")) {
				policyForm.setStatus("0");
			} else {
				policyForm.setStatus("1");   
			}

			policyData.setDescription(policyForm.getDescription());
			policyData.setName(policyForm.getName());
			policyData.setOrderNumber(policyForm.getOrderNumber());			 
			policyData.setRuleSet(policyForm.getRuleSet());
			policyData.setSessionManagement(policyForm.getSessionManagement());
			policyData.setScript(policyForm.getScript());
			if(BaseConstant.HIDE_STATUS.equals(policyForm.getStatus())){
				policyData.setStatus(BaseConstant.HIDE_STATUS_ID);
			}else{
				policyData.setStatus(BaseConstant.SHOW_STATUS_ID);
			}
				policyData.setDefaultResponseBehaviorArgument(policyForm.getDefaultResponseBehaviorArgument());
			
			policyData.setDefaultResponseBehaviour(policyForm.getDefaultResponseBehaviour());
			policyData.setDriverList(mainDriverList);
			
			String prePluginJson = policyForm.getPrePluginsList();
			String postPluginJson = policyForm.getPostPluginList();
			
			List<CCPolicyPluginConfig> ccPolicyPluginConfigList=new ArrayList<CCPolicyPluginConfig>();
			ccPolicyPluginConfigList.addAll(getCCPolicyPluginConfigListFromJsonAry(prePluginJson, PolicyPluginConstants.IN_PLUGIN));
			ccPolicyPluginConfigList.addAll(getCCPolicyPluginConfigListFromJsonAry(postPluginJson, PolicyPluginConstants.OUT_PLUGIN));
			policyData.setCcPolicyPluginConfigList(ccPolicyPluginConfigList);
			
			//Get Response Attributes data
			String commandCodes[] = request.getParameterValues("commandCode");
			String responseAttributes[] = request.getParameterValues("responseAttributes");
			
			Set<CCResponseAttributes> ccResponseAttributesSet = new LinkedHashSet<CCResponseAttributes>();
			
			if( commandCodes!=null ){
				for (int j = 0; j < commandCodes.length; j++) {
					if(commandCodes[j]!=null && commandCodes[j].trim().length()>0){
						
						CCResponseAttributes ccResponseAttributes = new CCResponseAttributes();
						ccResponseAttributes.setCommandCodes(commandCodes[j]);
						ccResponseAttributes.setResponseAttributes(responseAttributes[j]);
					
						ccResponseAttributesSet.add(ccResponseAttributes);
						Logger.logInfo(MODULE, "ccResponseAttributes: "+ ccResponseAttributes);
					}
				}
			}
			policyData.setCcResponseAttributesSet(ccResponseAttributesSet);

			CreditControlPolicyBLManager blManager = new CreditControlPolicyBLManager();
			blManager.create(policyData, staffData);
			doAuditing(staffData, ACTION_ALIAS);

			request.setAttribute("responseUrl", "/initSearchCcpolicy");      
			ActionMessage message = new ActionMessage("diameter.ccpolicy.create");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);         				   
			Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName()); 
			return mapping.findForward(SUCCESS_FORWARD);             
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (DuplicateInstanceNameFoundException authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("diameter.ccpolicy.duplicate");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);       
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("diameter.ccpolicy.error");                                                         		    
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		return mapping.findForward(FAILURE_FORWARD);
	}             

	private List<CreditControlDriverRelationData> getMainDriverList(String mainDrivers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException{
		if(mainDrivers!=null){
			List<CreditControlDriverRelationData> mainDriverRelList= new ArrayList<CreditControlDriverRelationData>();
			for (int i = 0; i < mainDrivers.length; i++) {

				String[] driverInstanceIdWgtValues = mainDrivers[i].split("-");

				String driverInstanceId = driverInstanceIdWgtValues[0];				
				CreditControlDriverRelationData mainDriverRelData = new CreditControlDriverRelationData();
				mainDriverRelData.setDriverInstanceId(driverInstanceId);
				mainDriverRelData.setWeightage(Integer.parseInt(driverInstanceIdWgtValues[1].trim()));
				mainDriverRelList.add(mainDriverRelData);
			}
			return mainDriverRelList;
		}
		return null;
	}

	private List<CCPolicyPluginConfig> getCCPolicyPluginConfigListFromJsonAry(String configJsonArray,String pluginPolicyConstant) {
		List<CCPolicyPluginConfig> ccPolicyPluginConfigList = new ArrayList<CCPolicyPluginConfig>();
		
		if( configJsonArray != null && configJsonArray.length() > 0){
            JSONArray postPluginJsonArray = JSONArray.fromObject(configJsonArray);
            
            for(Object  obj: postPluginJsonArray){
            	CCPolicyPluginConfig ccPolicyPluginConfig = (CCPolicyPluginConfig) JSONObject.toBean((JSONObject) obj, CCPolicyPluginConfig.class);
            	ccPolicyPluginConfig.setPluginType(pluginPolicyConstant);
            	ccPolicyPluginConfigList.add(ccPolicyPluginConfig);
            }
		}
		return ccPolicyPluginConfigList;
		
	}
}
