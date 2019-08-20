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

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.CreditControlPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CCPolicyPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CCResponseAttributes;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlDriverRelationData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.PolicyPluginConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy.forms.UpdateCreditControlPolicyForm;
                                                                               
public class UpdateCreditControlPolicyAction extends BaseWebAction { 
	                                                                       
	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CREDITCONTROLPOLICY";
	private static final String ACTION_ALIAS=ConfigConstant.UPDATE_CREDIT_CONTROL_SERVICE_POLICY;
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			 Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			 UpdateCreditControlPolicyForm policyForm = (UpdateCreditControlPolicyForm)form;
			 List<CreditControlDriverRelationData>  mainDriverList = getMainDriverList(policyForm.getSelecteddriverIds());				
			 CreditControlPolicyBLManager blManager = new CreditControlPolicyBLManager();
			 
			 IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			 
			 CreditControlPolicyData data = new CreditControlPolicyData();
			 data = blManager.getPolicyDataByPolicyId(policyForm.getPolicyId(), staffData);
			 data.setDescription(policyForm.getDescription());
			 data.setName(policyForm.getName());
			 data.setPolicyId(policyForm.getPolicyId());
			 data.setRuleSet(policyForm.getRuleSet());
			 data.setSessionManagement(policyForm.getSessionManagement());
			 data.setScript(policyForm.getScript());
			 data.setDriverList(mainDriverList);
			 data.setAuditUId(policyForm.getAuditUId());

				 data.setDefaultResponseBehaviorArgument(policyForm.getDefaultResponseBehaviorArgument());
			 data.setDefaultResponseBehaviour(policyForm.getDefaultResponseBehaviour());
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
			data.setCcResponseAttributesSet(ccResponseAttributesSet);

			//Update pre-plugin and post-plugin
			String prePluginJson = policyForm.getPrePluginsList();
			String postPluginJson = policyForm.getPostPluginList();
			List<CCPolicyPluginConfig> ccPolicyPluginConfigList=new ArrayList<CCPolicyPluginConfig>();
			ccPolicyPluginConfigList.addAll(getCCPolicyPluginConfigListFromJsonAry(prePluginJson, PolicyPluginConstants.IN_PLUGIN));
			ccPolicyPluginConfigList.addAll(getCCPolicyPluginConfigListFromJsonAry(postPluginJson, PolicyPluginConstants.OUT_PLUGIN));
			data.setCcPolicyPluginConfigList(ccPolicyPluginConfigList);
			
			 staffData.setAuditId(data.getAuditUId());
			 staffData.setAuditName(data.getName());
			
			 blManager.updateByID(data,staffData);
			 
			 request.setAttribute("ccPolicyData",data);
			 
			 request.setAttribute("responseUrl", "/initSearchCcpolicy"); 
			 ActionMessage message = new ActionMessage("diameter.ccpolicy.update");
			 ActionMessages messages = new ActionMessages();          
			 messages.add("information", message);                    
			 saveMessages(request,messages);         				   
			 Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName()); 
			 return mapping.findForward(SUCCESS_FORWARD);
			 
		}catch (Exception authExp) {                                                                                           
		    Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
		    Logger.logTrace(MODULE, authExp);                                                                                               
		    ActionMessage message = new ActionMessage("diameter.ccpolicy.updateerror");                                                         
		    ActionMessages messages = new ActionMessages();                                                                                 
		    messages.add("information", message);                                                                                           
		    saveErrors(request, messages);                                                                                                  
		}                                                                                                                                   
		return mapping.findForward(FAILURE_FORWARD); 
	}
	
	
	private List<CreditControlDriverRelationData> getMainDriverList(String mainDrivers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException, DataManagerException{
		if(mainDrivers!=null){
			List<CreditControlDriverRelationData> mainDriverRelList= new ArrayList<CreditControlDriverRelationData>();
			for (int i = 0; i < mainDrivers.length; i++) {
				
				DriverBLManager driverBLManager=new DriverBLManager();
				String[] driverInstanceIdWgtValues = mainDrivers[i].split("-");
				String driverInstanceId = driverInstanceIdWgtValues[0];				
				CreditControlDriverRelationData mainDriverRelData = new CreditControlDriverRelationData();
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
