package com.elitecore.elitesm.web.servicepolicy.dynauth;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.dynauth.forms.AddDynAuthServicePolicyForm;

public class InitAddDynAuthServicePolicyAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "addDynAuthServicePolicy";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "InitAddDynAuthServicePolicyAction";
	private static final String ACTION_ALIAS=ConfigConstant.CREATE_DYNAUTH_POLICY;
	
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{


		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			request.getSession().removeAttribute("addDynAuthServicePolicyForm");
			AddDynAuthServicePolicyForm addDynAuthServicePolicyForm = (AddDynAuthServicePolicyForm)form;
			addDynAuthServicePolicyForm.setStatus("1");
			addDynAuthServicePolicyForm.setDescription(getDefaultDescription(userName));
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			ExternalSystemInterfaceBLManager externalSystemBLmanager = new ExternalSystemInterfaceBLManager();
			
			/* get NAS Client List*/
			List<ExternalSystemInterfaceInstanceData> nasInstanceList = externalSystemBLmanager.getExternalSystemInstanceDataList(ExternalSystemConstants.NAS);
			String nasInstanceIds[] = new String[nasInstanceList.size()];
			String nasInstanceNames[][] = new String[nasInstanceList.size()][4]; 
			for(int i=0;i<nasInstanceList.size();i++) {
				ExternalSystemInterfaceInstanceData	externalSystemData = nasInstanceList.get(i);
				nasInstanceNames[i][0] = String.valueOf(externalSystemData.getName());
				nasInstanceNames[i][1] = String.valueOf(externalSystemData.getAddress());
				nasInstanceNames[i][2] = String.valueOf(externalSystemData.getMinLocalPort());
				nasInstanceNames[i][3] = String.valueOf(externalSystemData.getExpiredRequestLimitCount());
				nasInstanceIds[i] = String.valueOf(externalSystemData.getEsiInstanceId());
			}
								
			request.getSession().setAttribute("nasInstanceIds", nasInstanceIds);
			request.getSession().setAttribute("nasInstanceNames", nasInstanceNames);
			request.getSession().setAttribute("nasInstanceList", nasInstanceList);
			
			TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
			addDynAuthServicePolicyForm.setTranslationMappingConfDataList(translationMappingConfBLManager.getRadiusToRadiusTranslationMapping());
			
			CopyPacketTransMapConfBLManager copyPacketTranslationConfBLManager = new CopyPacketTransMapConfBLManager();
			List<CopyPacketTranslationConfData> copyPacketMappingConfDataList = copyPacketTranslationConfBLManager.getCopyPacketTransMapConfigList(TranslationMappingConfigConstants.RADIUS, TranslationMappingConfigConstants.RADIUS);
			addDynAuthServicePolicyForm.setCopyPacketMappingConfDataList(copyPacketMappingConfDataList);
			
			addDynAuthServicePolicyForm.setDatabaseDatasourceList(databaseDSBLManager.getDatabaseDSList());
			
			/* External Radius Script */
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			List<ScriptInstanceData> externalScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.EXTERNAL_RADIUS_SCRIPT);
				
			addDynAuthServicePolicyForm.setExternalScriptList(externalScriptList);
			
			request.getSession().setAttribute("addDynAuthServicePolicyForm", addDynAuthServicePolicyForm);
			return mapping.findForward(SUCCESS_FORWARD);

		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
}