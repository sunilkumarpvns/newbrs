package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASResponseAttributes;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.constants.ServiceTypeConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.AddNASServicePolicyForm;

public class AddNASServicePolicyAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "addNASServicePolicyDetail";
	private static String ACTION_ALIAS = ConfigConstant.CREATE_NAS_SERVICE_POLICY;

	private static final String MODULE = "AddNASServicePolicyAction";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try{
			checkActionPermission(request,ACTION_ALIAS);
			DriverBLManager driverBlManager = new DriverBLManager();
			List<DriverInstanceData> nasAuthDriverList = driverBlManager.getDriverInstanceList(ServiceTypeConstants.NAS_AUTH_APPLICATION);
			
			String[] nasAuthDriverInstanceIds = new String [nasAuthDriverList.size()];
			String[][] nasAuthDriverInstanceNames = new String[nasAuthDriverList.size()][3]; 
			
			for(int i=0;i<nasAuthDriverList.size();i++){
				DriverInstanceData data = nasAuthDriverList.get(i);				
					nasAuthDriverInstanceNames[i][0] = String.valueOf(data.getName());
					nasAuthDriverInstanceNames[i][1] = String.valueOf(data.getDescription());
					nasAuthDriverInstanceNames[i][2] = String.valueOf(data.getDriverTypeData().getDisplayName());
				nasAuthDriverInstanceIds[i] = String.valueOf(data.getDriverInstanceId());
			}
			
			request.setAttribute("nasAuthDriverInstanceIds", nasAuthDriverInstanceIds);
			request.setAttribute("nasAuthDriverInstanceNames", nasAuthDriverInstanceNames);
			request.setAttribute("nasAuthDriverList", nasAuthDriverList);
			
			List<DriverInstanceData> nasAcctDriverList = driverBlManager.getDriverInstanceList(ServiceTypeConstants.NAS_ACCT_APPLICATION);
			
			String[] nasAcctDriverInstanceIds = new String [nasAcctDriverList.size()];
			String[][] nasAcctDriverInstanceNames = new String[nasAcctDriverList.size()][3]; 
			
			for(int i=0;i<nasAcctDriverList.size();i++){
				DriverInstanceData data = nasAcctDriverList.get(i);				
					nasAcctDriverInstanceNames[i][0] = String.valueOf(data.getName());
					nasAcctDriverInstanceNames[i][1] = String.valueOf(data.getDescription());
					nasAcctDriverInstanceNames[i][2] = String.valueOf(data.getDriverTypeData().getDisplayName());
				nasAcctDriverInstanceIds[i] = String.valueOf(data.getDriverInstanceId());
			}
			
			request.setAttribute("nasAcctDriverInstanceIds", nasAcctDriverInstanceIds);
			request.setAttribute("nasAcctDriverInstanceNames", nasAcctDriverInstanceNames);
			request.setAttribute("nasAcctDriverList", nasAcctDriverList);
			
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
			AddNASServicePolicyForm addNASServicePolicyForm=(AddNASServicePolicyForm)form;
			Logger.logDebug(MODULE, "addNASServicePolicyForm     : "+addNASServicePolicyForm);

			String statusCheckBox = addNASServicePolicyForm.getStatus();
			if(statusCheckBox == null || statusCheckBox.equalsIgnoreCase("")) {
				addNASServicePolicyForm.setStatus("0");
			} else {
				addNASServicePolicyForm.setStatus("1");   
			}
			
			//Get Response Attributes data
			
			String commandCodes[] = request.getParameterValues("commandCode");
			String responseAttributes[] = request.getParameterValues("responseAttributes");
			
			Set<NASResponseAttributes> nasResponseAttributesSet = new LinkedHashSet<NASResponseAttributes>();
			
			if( commandCodes!=null){
				for (int j = 0; j < commandCodes.length; j++) {
					if(commandCodes[j]!=null && commandCodes[j].trim().length()>0){
						
						NASResponseAttributes nasResponseAttributes = new NASResponseAttributes();
						nasResponseAttributes.setCommandCodes(commandCodes[j]);
						nasResponseAttributes.setResponseAttributes(responseAttributes[j]);
					
						nasResponseAttributesSet.add(nasResponseAttributes);
						Logger.logInfo(MODULE, "nasResponseAttributes: "+ nasResponseAttributes);
					}
				}
			}
			
			addNASServicePolicyForm.setNasResponseAttributesSet(nasResponseAttributesSet);
			
			PluginBLManager pluginBLManager = new PluginBLManager();			
			List<PluginInstData> pluginInstDataList = pluginBLManager.getDiameterPluginList();
			
			/* Driver Script */
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
		
			addNASServicePolicyForm.setDriverScriptList(driverScriptList);
			
			request.setAttribute("pluginInstDataList", pluginInstDataList);
			
			request.getSession().setAttribute("addNASServicePolicyForm", addNASServicePolicyForm);
			return mapping.findForward(SUCCESS_FORWARD);
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

}
