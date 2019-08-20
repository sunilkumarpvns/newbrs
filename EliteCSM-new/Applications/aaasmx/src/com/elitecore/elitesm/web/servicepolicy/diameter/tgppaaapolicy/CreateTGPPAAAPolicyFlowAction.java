package com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.aaa.util.constants.AAAServerConstants.ProtocolType;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.TGPPAAAPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AdditionalDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.PluginEntryData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.PrimaryDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.SecondaryAndCacheDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.UpdateIdentityParamsDetail;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServiceTypeConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.form.TGPPAAAPolicyForm;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.CommandCodeFlowData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.CommandCodeResponseAttribute;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterApplicationHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterAuthenticationHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterAuthorizationHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterBroadcastCommunicationEntryData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterBroadcastCommunicationHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterCDRGenerationHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterCDRHandlerEntryData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterConcurrencyHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterExternalCommunicationEntryData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterPluginHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterPostResponseHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterProfileDriverDetails;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterSubscriberProfileRepositoryDetails;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterSynchronousCommunicationHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterSynchronousCommunicationHandlerData.ProxyMode;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.TGPPServerPolicyData;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author nayana.rathod
 *
 */

public class CreateTGPPAAAPolicyFlowAction extends BaseWebAction{

	private static final String INFORMATION = "information";
	private static final String ERROR_DETAILS = "errorDetails";
	private static final String DRIVER_SCRIPT = "driverScript";
	private static final String RULESET = "ruleset";
	private static final String ENTRIES = "entries";
	private static final String HANDLER_NAME = "handlerName";
	private static final String IS_HANDLER_ENABLED = "isHandlerEnabled";
	private static final String SUCCESS_FORWARD =	"createTGPPAAAPolicyFlow";
	private static final String FAILURE_FORWARD =	"failure";
	private static final String TGPPAAAPOLICY 	=	"CreateTGPPAAAPolicyFlowAction";
	private static final String ACTION_ALIAS	=	 ConfigConstant.CREATE_TGPP_AAA_SERVICE_POLICY;
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logTrace(TGPPAAAPOLICY,"Enter execute method of "+getClass().getName());

		try{
		
			/* Check for action is permitted or not */
			checkActionPermission(request, ACTION_ALIAS);
			request.getSession().removeAttribute("tgppAAAPolicyForm");
			
			TGPPAAAPolicyForm tgppAAAPolicyForm = (TGPPAAAPolicyForm)form;
			
			ServicePolicyBLManager servicePoilcyBLManager = new ServicePolicyBLManager();
			TGPPAAAPolicyBLManager tgppaaaPolicyBLManager = new TGPPAAAPolicyBLManager();
			EAPConfigBLManager eapConfigBLManager = new EAPConfigBLManager();
			GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
			TGPPAAAPolicyData tgppAAAPolicyData = new TGPPAAAPolicyData();
			
			if ("create".equals(tgppAAAPolicyForm.getAction())) {
				
				try {
					
					IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
	
					convertFromFormToData(tgppAAAPolicyForm,tgppAAAPolicyData);

					Logger.getLogger().info(TGPPAAAPOLICY,"TGPP AAA Policy Name : " + tgppAAAPolicyForm.getName());
					
					/*Create plugin Code*/
					tgppaaaPolicyBLManager.createTGPPAAAPolicyData(tgppAAAPolicyData,staffData);
					
					/**
					 * For storing policy id in xml for fetched policy data, we need to retrieve
					 * policy id from the database and update policy id in xml(for this we need 
					 * to call update basic detail operation manually). While creation of policy, history 
					 * is not maintained, so false will be pass as a auditEnble argument.
					 */
					byte[] tgppaaaPolicyXMLData = getTGPPAAAPolicyXMLData(tgppAAAPolicyForm.getTgppCommandCodeJSON(),tgppAAAPolicyForm,tgppAAAPolicyData.getTgppAAAPolicyId());
					tgppAAAPolicyData.setTgppAAAPolicyXml(tgppaaaPolicyXMLData);
					tgppaaaPolicyBLManager.updateTgppAAAPolicyByID(tgppAAAPolicyData, staffData, ACTION_ALIAS, ConfigConstant.IS_AUDIT_DISABLED);
					
					Logger.getLogger().info(TGPPAAAPOLICY, " TGPP AAA Policy [" + tgppAAAPolicyData.getName() + "] Created Successfully");
					
					request.setAttribute("responseUrl", "/searchTGPPAAAPolicy.do");
					ActionMessage message = new ActionMessage("tgppaaaservicepolicy.create.success");
					ActionMessages messages = new ActionMessages();
					messages.add(INFORMATION, message);
					saveMessages(request, messages);
					
					return mapping.findForward(SUCCESS);
					
				}catch(DuplicateParameterFoundExcpetion dpf){
					Logger.logError(TGPPAAAPOLICY, "Returning error forward from " + getClass().getName());
					Logger.logTrace(TGPPAAAPOLICY,dpf);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
					request.setAttribute(ERROR_DETAILS, errorElements);
					ActionMessage message = new ActionMessage("tgppaaaservicepolicy.create.duplicate.failure",tgppAAAPolicyData.getName());
					ActionMessages messages = new ActionMessages();
					messages.add(INFORMATION,message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);
				}catch(Exception e) {					
					Logger.logError(TGPPAAAPOLICY, "Returning error forward from " + getClass().getName());
					Logger.logTrace(TGPPAAAPOLICY,e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute(ERROR_DETAILS, errorElements);
					ActionMessage message = new ActionMessage("tgppaaaservicepolicy.create.failure");
					ActionMessages messages = new ActionMessages();
					messages.add(INFORMATION,message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);
				}

			}else{
			
				/* Fetching EAP Config Data */
				List<EAPConfigData> eapConfigurationList = eapConfigBLManager.getEapConfigurationList();
				
				/* Fetching Auth Method Type Data */
				List<AuthMethodTypeData> authMethodTypeDataList = servicePoilcyBLManager.getAuthMethodTypeList();
				String[] selectedAuthMethodTypes ;
		        List<String> selectedAuthMethodTypesList = new ArrayList<String>(); 
		        for (int i = 0; i < authMethodTypeDataList.size(); i++) {
		        	long authTypeId=authMethodTypeDataList.get(i).getAuthMethodTypeId();
		            	if(authTypeId != 5){
		            		selectedAuthMethodTypesList.add(String.valueOf(authTypeId));
		            	}
		            	
				}
		        selectedAuthMethodTypes=selectedAuthMethodTypesList.toArray(new String[0]); 
		        
		        /* Fetching Grace Policy List */
		        List<GracepolicyData> gracePolicyList = gracePolicyBLManager.getGracePolicyList();	        
		        
		        /* Fetching Radius TranslationMapping List */
				TranslationMappingConfBLManager translationMappingConfBLManager=new TranslationMappingConfBLManager();
				List<TranslationMappingConfData> diaToradiusTranslationMappingConfDataList=translationMappingConfBLManager.getDiaToRadTranslationMappingList();
	
				/* Fetching Diameter TranslationMapping List */
				List<TranslationMappingConfData> diaTodiaTranslationMappingConfDataList=translationMappingConfBLManager.getDiaToDiaTranslationMapping();
	
				/* Fetching Copy Packet List */
				CopyPacketTransMapConfBLManager copyPacketTranslationConfBLManager = new CopyPacketTransMapConfBLManager();
				List<CopyPacketTranslationConfData> diaTodiaCopyPacketMappingConfDataList = copyPacketTranslationConfBLManager.getdiaTodiaCopyPacketMapping();
				List<CopyPacketTranslationConfData> diaToradiusCopyPacketMappingConfDataList = copyPacketTranslationConfBLManager.getdiaToradCopyPacketMapping();
				
				/* Fetching Diameter Peer group  List */
				DiameterPeerGroupBLManager diameterPeerGroupBLManager = new DiameterPeerGroupBLManager();
				List<DiameterPeerGroup> diameterPeerGroupList = diameterPeerGroupBLManager.getDiameterPeerGroupList();
				tgppAAAPolicyForm.setDiameterPeerGroupDataList(diameterPeerGroupList);
				
				/*Fetching Radius ESI List*/
				RadiusESIGroupBLManager radiusESIGroupBLManager = new RadiusESIGroupBLManager();
				List<RadiusESIGroupData> radiusESIGroupDataList = radiusESIGroupBLManager.getRadiusESIGroupDataList();
				tgppAAAPolicyForm.setRadiusESIGroupDataList(radiusESIGroupDataList);
				
				/* Fetching Plugin Details */
				PluginBLManager pluginBLManager = new PluginBLManager();
				List<PluginInstData> pluginList  = pluginBLManager.getDiameterPluginList();
				tgppAAAPolicyForm.setPluginInstDataList(pluginList);
				
		        /* Driver Related Data */
		        DriverBLManager driverBlManager = new DriverBLManager();
				List<DriverInstanceData> driverList = driverBlManager.getDriverInstanceList(ServiceTypeConstants.NAS_AUTH_APPLICATION);
				tgppAAAPolicyForm.setDriversList(driverList);
				
				List<DriverInstanceData> cacheableDriverInstDataList = driverBlManager.getCacheableDriverData();
				tgppAAAPolicyForm.setCacheableDriverList(cacheableDriverInstDataList);
				
				String[] cacheableDriverInstIds = new String [cacheableDriverInstDataList.size()];
				String[][] cacheableDriverInstanceNames = new String[cacheableDriverInstDataList.size()][2]; 
				
				for(int i=0;i<cacheableDriverInstDataList.size();i++){
					DriverInstanceData data = cacheableDriverInstDataList.get(i);				
					cacheableDriverInstanceNames[i][0] = String.valueOf(data.getName());
					cacheableDriverInstanceNames[i][1] = String.valueOf(data.getDescription());
					cacheableDriverInstIds[i] = String.valueOf(data.getDriverInstanceId());
				}
				
				request.setAttribute("cacheableDriverInstIds", cacheableDriverInstIds);
				request.setAttribute("cacheableDriverInstanceNames", cacheableDriverInstanceNames);
				request.setAttribute("cacheableDriverInstDataList", cacheableDriverInstDataList);
				
				String[] driverInstanceIds = new String [driverList.size()];
				String[][] driverInstanceNames = new String[driverList.size()][2]; 
				
				for(int i=0;i<driverList.size();i++){
					DriverInstanceData data = driverList.get(i);				
						driverInstanceNames[i][0] = String.valueOf(data.getName());
						driverInstanceNames[i][1] = String.valueOf(data.getDescription());
					driverInstanceIds[i] = String.valueOf(data.getDriverInstanceId());
				}
		        
				List<DriverInstanceData> listOfAcctDriver = driverBlManager.getDriverInstanceList(ServiceTypeConstants.NAS_ACCT_APPLICATION);
				List<DriverTypeData> driverTypeList=driverBlManager.getAcctDriverTypeList(ServiceTypeConstants.NAS_ACCT_APPLICATION);
				
		        tgppAAAPolicyForm.setSelectedAuthMethodTypes(selectedAuthMethodTypes);
		        tgppAAAPolicyForm.setAuthMethodTypeDataList(authMethodTypeDataList);
		        tgppAAAPolicyForm.setEapConfigurationList(eapConfigurationList);
		        tgppAAAPolicyForm.setGracePolicyList(gracePolicyList);
		        tgppAAAPolicyForm.setDiaTodiaTranslationMappingConfDataList(diaTodiaTranslationMappingConfDataList);
		        tgppAAAPolicyForm.setDiaToradiusTranslationMappingConfDataList(diaToradiusTranslationMappingConfDataList);
		        tgppAAAPolicyForm.setDiaTodiaCopyPacketMappingConfDataList(diaTodiaCopyPacketMappingConfDataList);
		        tgppAAAPolicyForm.setDiaToradiusCopyPacketMappingConfDataList(diaToradiusCopyPacketMappingConfDataList);
		        
		        request.setAttribute("driverInstanceIds", driverInstanceIds);
				request.setAttribute("driverInstanceNames", driverInstanceNames);
				request.setAttribute("listOfDriver", driverList);
				request.setAttribute("listOfAcctDriver", listOfAcctDriver);
				request.setAttribute("driverTypeList", driverTypeList);
				request.setAttribute("diaToradiusTranslationMappingConfDataList", diaToradiusTranslationMappingConfDataList);
				request.setAttribute("diaTodiaTranslationMappingConfDataList", diaTodiaTranslationMappingConfDataList);
				request.setAttribute("diaTodiaCopyPacketMappingConfDataList", diaTodiaCopyPacketMappingConfDataList);
				request.setAttribute("diaToradiusCopyPacketMappingConfDataList", diaToradiusCopyPacketMappingConfDataList);
				request.setAttribute("pluginList", pluginList);
				request.setAttribute("diameterPeerGroupList", diameterPeerGroupList);
				request.setAttribute("radiusESIGroupDataList", radiusESIGroupDataList);
				
				request.getSession().setAttribute("tgppAAAPolicyForm", tgppAAAPolicyForm);
				
				String viewPage = request.getParameter("isView");
				if(viewPage == null){
					request.setAttribute("isViewPage",false);
				}else{
					request.setAttribute("isViewPage",true);
				}
				
				
				return mapping.findForward(SUCCESS_FORWARD);
			}
		}catch(ActionNotPermitedException e){
            Logger.logError(TGPPAAAPOLICY,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add(INFORMATION, new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException e){
			Logger.logError(TGPPAAAPOLICY, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, message);
			saveErrors(request, messages);
			
		}catch(Exception e){
			Logger.logError(TGPPAAAPOLICY, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, message);
			saveErrors(request, messages);
			
		}
		return mapping.findForward(FAILURE_FORWARD);
	}

	private byte[] getTGPPAAAPolicyXMLData(String tgppCommandCodeJSON, TGPPAAAPolicyForm createTGPPAAAPolicyForm,String id) throws Exception {
		try{
			
			TGPPServerPolicyData tgppServerPolicyData = new TGPPServerPolicyData();
			tgppServerPolicyData.setId(id);
			tgppServerPolicyData.setName(createTGPPAAAPolicyForm.getName());
			
			if(BaseConstant.SHOW_STATUS.equals(createTGPPAAAPolicyForm.getStatus())){
				tgppServerPolicyData.setStatus(BaseConstant.SHOW_STATUS_ID);
			}else{
				tgppServerPolicyData.setStatus(BaseConstant.HIDE_STATUS_ID);
			}
			
			tgppServerPolicyData.setDescription(createTGPPAAAPolicyForm.getDescription());
			tgppServerPolicyData.setRuleSet(createTGPPAAAPolicyForm.getRuleset());
			tgppServerPolicyData.setUserIdentity(createTGPPAAAPolicyForm.getUserIdentity());
			tgppServerPolicyData.setSessionManagementEnabled(createTGPPAAAPolicyForm.getsessionManagement());
			tgppServerPolicyData.setDefaultResponseBehaviorParameter(createTGPPAAAPolicyForm.getDefaultResponseBehaviourArgument());
			tgppServerPolicyData.setDefaultResponseBehaviorType(DefaultResponseBehaviorType.valueOf(createTGPPAAAPolicyForm.getDefaultResponseBehaviour()));
			
			JSONArray resAttrJsonArray = JSONArray.fromObject(createTGPPAAAPolicyForm.getCommandCodeWiseRespAttrib());
			int resAttrJsonArraySize = resAttrJsonArray.size();
			for (int i = 0; i < resAttrJsonArraySize ; i++) {
				JSONObject resAttrObj = resAttrJsonArray.getJSONObject(i); 
				CommandCodeResponseAttribute commandCodeResponseAttribute = new CommandCodeResponseAttribute();
				commandCodeResponseAttribute.setCommandCodes(resAttrObj.getString("commandCode"));
				commandCodeResponseAttribute.setResponseAttributes(resAttrObj.getString("responseAttr"));
				tgppServerPolicyData.getCommandCodeResponseAttributesList().add(commandCodeResponseAttribute);
			}
			
			List<CommandCodeFlowData> commandCodeFlowDataList = new ArrayList<CommandCodeFlowData>();
			
			if( tgppCommandCodeJSON != null && tgppCommandCodeJSON.isEmpty() == false ){
				if(tgppCommandCodeJSON != null && tgppCommandCodeJSON.length() > 0){
					 JSONArray tgppCommandCodeJSONArray = JSONArray.fromObject(tgppCommandCodeJSON);
					 for(Object  commandCodeFlow: tgppCommandCodeJSONArray){
						
						CommandCodeFlowData commandCodeFlowData = new CommandCodeFlowData();
						
						JSONObject commandCodeFlowJson = (JSONObject) commandCodeFlow;
						
						/* Command Code */
						String commandCode = (String) commandCodeFlowJson.get("commandCode");
						commandCodeFlowData.setCommandCode(commandCode);
						
						/* Interface */
						String interfaceId = (String)commandCodeFlowJson.get("interfaceId");
						commandCodeFlowData.setInterfaceId(interfaceId);
						
						Logger.getLogger().info(TGPPAAAPOLICY, "Command Code Flow JSON ");
						Logger.getLogger().info(TGPPAAAPOLICY, commandCodeFlowJson.toString());
						
						/* Display label */
						String name = (String)commandCodeFlowJson.get("name");
						commandCodeFlowData.setName(name);
						
						JSONArray handlerList = commandCodeFlowJson.getJSONArray("handlerList");
						
						List<DiameterApplicationHandlerData> handlersDataList = new ArrayList<DiameterApplicationHandlerData>();
						
						for(Object  handler : handlerList){
							 JSONObject jsonObject = JSONObject.fromObject(handler);
							 JSONObject handlerObject = JSONObject.fromObject(jsonObject.get("counter"));
							 
							 String handlerType = (String)handlerObject.get("handlerType");
							 
							 if( handlerType != null && handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.AUTHENTICATION_HANDLER)){
								 
								 Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Authentication handler data ");
								 DiameterAuthenticationHandlerData diameterAuthenticationHandlerData = new DiameterAuthenticationHandlerData();
								
								 /* Handler Enabled/Disabled */
								 String handlerEnabled = (String)handlerObject.get(IS_HANDLER_ENABLED);
								 if(Strings.isNullOrBlank(handlerEnabled) == false){
									 diameterAuthenticationHandlerData.setEnabled(handlerEnabled);
								 }else{
									 diameterAuthenticationHandlerData.setEnabled("false");
								 }
								 
								 /* Handler Name */
								 String handlerName = (String)handlerObject.get(HANDLER_NAME);
								 diameterAuthenticationHandlerData.setHandlerName(handlerName);
								 
								 /* Supported Authentication Method */
								 List<String> supportedAuthenticationMethods = (List<String>)handlerObject.get("supportedAuthenticationMethods");
								 List<String> supportedMethods = new ArrayList<String>();
							
								 if (Collectionz.isNullOrEmpty(supportedAuthenticationMethods) == false) {
									 for(String supportedMethod : supportedAuthenticationMethods){
										 supportedMethods.add(supportedMethod);
									 }
									 diameterAuthenticationHandlerData.getSupportedAuthenticationMethods().addAll(supportedMethods);
								 }
								 
								 
								 
								 /* EAP Configuration */
								 String eapConfigId = (String)handlerObject.get("eapConfigId");
								 diameterAuthenticationHandlerData.setEapConfigId(eapConfigId);
								 
								 /* Adding Authentication Hander to Handler Data List */
								 handlersDataList.add(diameterAuthenticationHandlerData);
								 Logger.getLogger().info(TGPPAAAPOLICY, "Authentication Handler added succesfully");
								 
							 }else if( handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.AUTHORIZATION_HANDLER)){
								
								 Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Authorization handler data ");
								 DiameterAuthorizationHandlerData diameterAuthorizationHandlerData = new DiameterAuthorizationHandlerData();
								 
								 /* Handler Enabled/Disabled */
								 String handlerEnabled = (String)handlerObject.get(IS_HANDLER_ENABLED);
								 if(Strings.isNullOrBlank(handlerEnabled) == false){
									 diameterAuthorizationHandlerData.setEnabled(handlerEnabled);
								 }else{
									 diameterAuthorizationHandlerData.setEnabled("false");
								 }
								 
								 /* Handler Name */
								 String handlerName = (String)handlerObject.get(HANDLER_NAME);
								 diameterAuthorizationHandlerData.setHandlerName(handlerName);
								 
								 /* Wimax */
								 String wimaxEnabled = (String)handlerObject.get("wimaxEnabled");
								 diameterAuthorizationHandlerData.setWimaxEnabled(Boolean.parseBoolean(wimaxEnabled));
								 
								 /* Default Session Timeout */
								 String defaultSessionTimeoutInSeconds = (String)handlerObject.get("defaultSessionTimeoutInSeconds");
								 diameterAuthorizationHandlerData.setDefaultSessionTimeoutInSeconds(Long.parseLong(defaultSessionTimeoutInSeconds));
								 
								 /* Reject On Check Item Not Found */
								 String rejectOnCheckItemNotFound = (String)handlerObject.get("rejectOnCheckItemNotFound");
								 if( rejectOnCheckItemNotFound != null ){
									 diameterAuthorizationHandlerData.setRejectOnCheckItemNotFound(Boolean.parseBoolean(rejectOnCheckItemNotFound));
								 }else{
									 diameterAuthorizationHandlerData.setRejectOnCheckItemNotFound(false);
								 }
								 
								 /* Reject On Reject Item Not Found */
								 String rejectOnRejectItemNotFound = (String)handlerObject.get("rejectOnRejectItemNotFound");
								 if( rejectOnRejectItemNotFound != null ){
									 diameterAuthorizationHandlerData.setRejectOnRejectItemNotFound(Boolean.parseBoolean(rejectOnRejectItemNotFound));
								 }else{
									 diameterAuthorizationHandlerData.setRejectOnRejectItemNotFound(false);
								 }
								 
								 /* Accept On Policy Not Found */
								 String acceptOnPolicyOnFound = (String)handlerObject.get("acceptOnPolicyOnFound");
								 if( acceptOnPolicyOnFound != null ){
									 diameterAuthorizationHandlerData.setAcceptOnPolicyNotFound(Boolean.parseBoolean(acceptOnPolicyOnFound));
								 }else{
									 diameterAuthorizationHandlerData.setAcceptOnPolicyNotFound(false);
								 }
								 
								 /* Grace Policy */
								 String gracePolicy = (String)handlerObject.get("gracePolicy");
								 diameterAuthorizationHandlerData.setGracePolicy(gracePolicy);
								 
								 /* Adding Authorization Hander to Handler Data List */
								 handlersDataList.add(diameterAuthorizationHandlerData);
								 Logger.getLogger().info(TGPPAAAPOLICY, "Authentication Handler added succesfully");
								 
							 }else if( handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.BROADCAST_COMMUNICATION_HANDLER)){
								 
								 Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Broadcast Communication handler data ");
								 DiameterBroadcastCommunicationHandlerData broadcastCommunicationHandlerData = new  DiameterBroadcastCommunicationHandlerData();
								
								 /* Handler Enabled/Disabled */
								 String handlerEnabled = (String)handlerObject.get(IS_HANDLER_ENABLED);
								 broadcastCommunicationHandlerData.setEnabled(handlerEnabled);
								 
								 /* Handler Name */
								 String handlerName = (String)handlerObject.get(HANDLER_NAME);
								 broadcastCommunicationHandlerData.setHandlerName(handlerName);
								 
								 /* Protocol */
								 String protocol = (String)handlerObject.get("protocol");
								 broadcastCommunicationHandlerData.setProtocol(protocol);
								 
								 /* Priority Result Codes */
								 String priorityResultCodes = (String)handlerObject.get("priorityResultCodes");
								 broadcastCommunicationHandlerData.setStrPriorityResultCodes(priorityResultCodes);
			
								 if(ProtocolType.DIAMETER.name().equals(protocol)){
									 long resultCodeWhenNoEntrySelected = Long.parseLong((String) handlerObject.get("resultCodeWhenNoEntrySelected"));
									 broadcastCommunicationHandlerData.setResultCodeOnNoEntrySelected(resultCodeWhenNoEntrySelected);
								 }
								 
								 List<DiameterBroadcastCommunicationEntryData> communicationEntryDatas = new  ArrayList<DiameterBroadcastCommunicationEntryData>();
								 
								 JSONArray entries = (JSONArray)handlerObject.get(ENTRIES);
								 
								 if( entries != null && entries.size() > 0 ){
								 
									 for(Object  entryObj : entries){
											
										 DiameterBroadcastCommunicationEntryData data = new DiameterBroadcastCommunicationEntryData();
										 JSONObject entryJsonObj = (JSONObject) entryObj;
										 
										 /* Ruleset */
										 String ruleset  = (String)entryJsonObj.get(RULESET);
										 data.setRuleset(ruleset);
										 
										 /* Translation Mapping */
										 String translationMapping  = (String)entryJsonObj.get("translationMapping");
										 data.setTranslationMapping(translationMapping);
										 
										 /* Script */
										 String script = (String)entryJsonObj.get("script");
										 data.setScript(script);
										 
										 /* Acceptable result code */
										 String acceptableResultCodes = (String)entryJsonObj.get("acceptableResultCodes");
										 if( acceptableResultCodes != null && acceptableResultCodes.isEmpty() == false ){
										
											 List<Integer> acceptableResultCodesArray = new ArrayList<Integer>();
											 String[] strAcceptableResultCodeArray = acceptableResultCodes.split(",");
											 
											 if(strAcceptableResultCodeArray != null &&  strAcceptableResultCodeArray.length > 0 ){
												 for( String resultCode :  strAcceptableResultCodeArray ){
													 if( resultCode != null && resultCode.isEmpty() == false ){
														acceptableResultCodesArray.add(Integer.parseInt(resultCode));
													}
												 }
											 }
											 
											 data.getAcceptableResultCodes().addAll(acceptableResultCodesArray);
										 }
										 
										 /* wait */
										 String wait = ( String )entryJsonObj.get("wait");
										 data.setWait(Boolean.parseBoolean(wait));
										 
										 /* Peer Group Id */
										 String peerGroupId= (String)entryJsonObj.get("peerGroupId");
										 data.setPeerGroupId(peerGroupId);
										 
										 communicationEntryDatas.add(data);
									 }
								 
									 broadcastCommunicationHandlerData.getEntries().addAll(communicationEntryDatas);
									 
								 }
								 
								 /* Adding Broadcast Hander to Handler Data List */
								 handlersDataList.add(broadcastCommunicationHandlerData);
								 Logger.getLogger().info(TGPPAAAPOLICY, "Broadcast Communication handler added succesfully");
								 
							 }else if(handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.CONCURRENCY_HANDLER)){
									
									Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Diameter concurrency handler data ");
									DiameterConcurrencyHandlerData diameterConcurrencyHandlerData=new DiameterConcurrencyHandlerData();
									
									/* Handler Enabled/Disabled */
									String handlerEnabled = (String)handlerObject.get(IS_HANDLER_ENABLED);	
									diameterConcurrencyHandlerData.setEnabled(handlerEnabled);
									
									
									/* Handler Name */
									String handlerName = (String)handlerObject.get(HANDLER_NAME);
									diameterConcurrencyHandlerData.setHandlerName(handlerName);
									
									/* Rule Set*/
									String ruleset = (String)handlerObject.get(RULESET);
									diameterConcurrencyHandlerData.setRuleSet(ruleset);
									
									String  dia_con_id=handlerObject.get("diaConConfigId").toString();
									diameterConcurrencyHandlerData.setDiaConcurrencyId(dia_con_id);
									handlersDataList.add(diameterConcurrencyHandlerData);
									
									Logger.getLogger().info(TGPPAAAPOLICY, "Concurrency hander  handler added succesfully");
									
								}else if( handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.PLUGIN_HANDLER)){
								 
								 Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Plugin handler data ");
								 DiameterPluginHandlerData pluginHandlerData = new DiameterPluginHandlerData();
								 
								 /* Handler Enabled/Disabled */
								 String handlerEnabled = (String)handlerObject.get(IS_HANDLER_ENABLED);
								 pluginHandlerData.setEnabled(handlerEnabled);
								
								 /* Handler Name */
								 String handlerName = (String)handlerObject.get(HANDLER_NAME);
								 pluginHandlerData.setHandlerName(handlerName);
								 
								 List<PluginEntryData> pluginEntryDataList = new ArrayList<PluginEntryData>();
								 JSONArray entries = (JSONArray) handlerObject.get("pluginEntries");
								 
								 if( entries != null && entries.size() > 0 ){
								
									 for(Object  entryObj : entries){
										 PluginEntryData pluginEntryData = new PluginEntryData();
										 
										 JSONObject entryJsonObj = (JSONObject) entryObj;
										 
										 /* Ruleset */
										 String ruleset  = (String)entryJsonObj.get(RULESET);
										 pluginEntryData.setRuleset(ruleset);
										 
										 /* Plugin Name */
										 String pluginName  = (String)entryJsonObj.get("pluginName");
										 pluginEntryData.setPluginName(pluginName);
										 
										 /* Plugin Argument */
										 String pluginArgument  = (String)entryJsonObj.get("pluginArgument");
										 pluginEntryData.setPluginArgument(pluginArgument);
										 
										 /* onResponse */
										 String onResponse = (String)entryJsonObj.get("onResponse");
										 pluginEntryData.setOnResponse(Boolean.parseBoolean(onResponse));
										 
										 pluginEntryDataList.add(pluginEntryData);
									 }
								 
								  pluginHandlerData.getPluginEntries().addAll(pluginEntryDataList);
								 
							    }
								 
								 /* Adding Plugin Hander to Handler Data List */
								 handlersDataList.add(pluginHandlerData);
								 Logger.getLogger().info(TGPPAAAPOLICY, "Plugin handler added succesfully");
								 
							 }else if(handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.PROFILE_LOOKUP_HANDLER)){
								
								 Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Profile Lookup handler data ");
								
								 DiameterSubscriberProfileRepositoryDetails diameterSubscriberProfileRepositoryDetails = new  DiameterSubscriberProfileRepositoryDetails();
								 
								 /* Handler Enabled/Disabled */
								 String handlerEnabled = (String)handlerObject.get(IS_HANDLER_ENABLED);
								 if(Strings.isNullOrBlank(handlerEnabled) == false){
									 diameterSubscriberProfileRepositoryDetails.setEnabled(handlerEnabled);
								 }else{
									 diameterSubscriberProfileRepositoryDetails.setEnabled("false");
								 }

								 
								 /* Handler Name */
								 String handlerName = (String)handlerObject.get(HANDLER_NAME);
								 diameterSubscriberProfileRepositoryDetails.setHandlerName(handlerName);
								
								 UpdateIdentityParamsDetail detail = new  UpdateIdentityParamsDetail();
								 
								 /* Strip Identity */
								 String stripIdentity = (String)handlerObject.get("stripIdentity");
								 detail.setStripIdentity(stripIdentity);
								 
								 /* Separator */
								 String separator =(String)handlerObject.get("separator");
								 detail.setSeparator(separator);
								 
								 /* iCase */
								 String iCase = (String)handlerObject.get("iCase");
								 detail.setIdetityFormat(iCase);
								 
								 /* Trim User Identity */
								 String trimUserIdentity = (String)handlerObject.get("trimUserIdentity");
								 if( trimUserIdentity != null ){
									 detail.setTrimIdentity(trimUserIdentity);
								 }else{
									 detail.setTrimIdentity("false");
								 }
								 
								 /* Trim User Password*/
								 String trimPassword = (String)handlerObject.get("trimPassword");
								 if( trimPassword != null ){
									 detail.setTrimPassword(trimPassword);
								 }else{
									 detail.setTrimPassword("false");
								 }
								 
								 /* Anonymous Profile Identity */
								 String anonymousProfileIdentity = (String)handlerObject.get("anonymousProfileIdentity");
								 diameterSubscriberProfileRepositoryDetails.setAnonymousProfileIdentity(anonymousProfileIdentity);
								 
								 DiameterProfileDriverDetails diameterProfileDriverDetails = new DiameterProfileDriverDetails();
								
								 /* Driver Script */
								 String driverScript = (String)handlerObject.get(DRIVER_SCRIPT);
								 diameterProfileDriverDetails.setDriverScript(driverScript);
								 
								 
								 /* Primary Drivers */
								 JSONArray primaryDrivers = (JSONArray) handlerObject.get("selecteddriverIds");
								 
								 if(primaryDrivers != null && primaryDrivers.size() > 0){
									
									 List<PrimaryDriverDetail> primaryDriverDetailsList = new  ArrayList<PrimaryDriverDetail>();
								
									 for(Object primaryDriverStr : primaryDrivers){
										 
										 String primaryStr = (String)primaryDriverStr;
										 
										 PrimaryDriverDetail primaryDriverDetail=new PrimaryDriverDetail();
										 
										 if(primaryStr !=null && primaryStr.isEmpty() == false ){
											 
											 String[] primaryDriverArray =  primaryStr.split("\\),\\(|\\)|\\(");
											 String driverInstanceId = primaryDriverArray[0];
											 int weightage = Integer.parseInt(primaryDriverArray[1].toString());
											 if(driverInstanceId != null && driverInstanceId.isEmpty() == false){
												 primaryDriverDetail.setDriverInstanceId(driverInstanceId);
											 }
											 
											 primaryDriverDetail.setWeightage(weightage);
										 }
										 primaryDriverDetailsList.add(primaryDriverDetail);
									 }
									 diameterProfileDriverDetails.setPrimaryDriverGroup(primaryDriverDetailsList);
								 }
								 
								 /* Secondary Drivers */
								 JSONArray selectedCacheDriverIds = (JSONArray) handlerObject.get("selectedCacheDriverIds");
								 
								 if(selectedCacheDriverIds != null && selectedCacheDriverIds.size() > 0){
									
									 List<SecondaryAndCacheDriverDetail> secondaryDriverDetailsList = new  ArrayList<SecondaryAndCacheDriverDetail>();
								
									 for(Object secondaryDriverStr : selectedCacheDriverIds){
										 String secondaryStr = (String)secondaryDriverStr;

										 SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail=new SecondaryAndCacheDriverDetail();

										 if(secondaryStr !=null && secondaryStr.trim().isEmpty() == false ){
											 secondaryAndCacheDriverDetail.setSecondaryDriverId(secondaryStr);
										 }
										 secondaryDriverDetailsList.add(secondaryAndCacheDriverDetail);
									 }
									 diameterProfileDriverDetails.setSecondaryDriverGroup(secondaryDriverDetailsList);
								 }
								 
								 /* Additional Drivers */
								 JSONArray selectedAdditionalDriverIds = (JSONArray) handlerObject.get("selectedAdditionalDriverIds");
								 
								 if(selectedAdditionalDriverIds != null && selectedAdditionalDriverIds.size() > 0 ){
									
									 List<AdditionalDriverDetail> additionalDriverDetailList = new ArrayList<AdditionalDriverDetail>();
									 for(Object additionalDriverStr : selectedAdditionalDriverIds){
										
										 String additionalStr = (String)additionalDriverStr;
										 
										 AdditionalDriverDetail additionalDriverDetail = new AdditionalDriverDetail();
										 
										 if(additionalStr != null && additionalStr.isEmpty() == false){
											 additionalDriverDetail.setDriverId(additionalStr);
										 }
										 
										 additionalDriverDetailList.add(additionalDriverDetail);
									 }
									 
									 diameterProfileDriverDetails.setAdditionalDriverList(additionalDriverDetailList);
								 }
								 
								 /* Setting Driver Details & Update User Identity Details */
								 diameterSubscriberProfileRepositoryDetails.setUpdateIdentity(detail);
								 diameterSubscriberProfileRepositoryDetails.setDriverDetails(diameterProfileDriverDetails);
								 
								 /* Adding Profile Lookup Hander to Handler Data List */
								 handlersDataList.add(diameterSubscriberProfileRepositoryDetails);
								 
								 Logger.getLogger().info(TGPPAAAPOLICY, "Plugin Profile lookup handler added succesfully");
							 }else if(handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.PROXY_COMMUNICATION_HANDLER)){
								 
								 Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Proxy Communication handler data ");
								 
								 DiameterSynchronousCommunicationHandlerData proxyCommunicationHandlerData = new  DiameterSynchronousCommunicationHandlerData();
								
								 /* Handler Enabled/Disabled */
								 String handlerEnabled = (String)handlerObject.get(IS_HANDLER_ENABLED);
								 proxyCommunicationHandlerData.setEnabled(handlerEnabled);
								 
								 /* Handler Name */
								 String handlerName = (String)handlerObject.get(HANDLER_NAME);
								 proxyCommunicationHandlerData.setHandlerName(handlerName);
								 
								 /* Protocol */
								 String protocol = (String)handlerObject.get("protocol");
								 proxyCommunicationHandlerData.setProtocol(protocol);
								 
								 /* Proxy Mode */
								 if( protocol != null && protocol.equalsIgnoreCase("DIAMETER")){
									 
									 /* Result Code When No Entry Selected */
									 long resultCodeWhenNoEntrySelected = Long.parseLong((String) handlerObject.get("resultCodeWhenNoEntrySelected"));
									 proxyCommunicationHandlerData.setResultCodeOnNoEntrySelected(resultCodeWhenNoEntrySelected);
									 
									 String proxyModeStr = (String)handlerObject.get("proxyMode");
									 if( proxyModeStr != null ){
										 if( proxyModeStr.equalsIgnoreCase(ProxyMode.MULTIPLE.name()) ){
											 ProxyMode proxyMode = ProxyMode.MULTIPLE;
											 proxyCommunicationHandlerData.setProxyMode(proxyMode);
										 }else if(proxyModeStr.equalsIgnoreCase(ProxyMode.SINGLE.name())){
											 ProxyMode proxyMode = ProxyMode.SINGLE;
											 proxyCommunicationHandlerData.setProxyMode(proxyMode);
										 }
									 }
								 }
								 
								 /* Priority Result Codes */
								 String priorityResultCodes = (String)handlerObject.get("priorityResultCodes");
								 proxyCommunicationHandlerData.setStrPriorityResultCodes(priorityResultCodes);
								 
								 List<DiameterExternalCommunicationEntryData> communicationEntryDatas = new  ArrayList<DiameterExternalCommunicationEntryData>();
								 
								 JSONArray entries = (JSONArray)handlerObject.get(ENTRIES);
								 
								 if( entries != null && entries.size() > 0 ){
								 
									 for(Object  entryObj : entries){
											
										 DiameterExternalCommunicationEntryData data = new DiameterExternalCommunicationEntryData();
										 JSONObject entryJsonObj = (JSONObject) entryObj;
										 
										 /* Ruleset */
										 String ruleset  = (String)entryJsonObj.get(RULESET);
										 data.setRuleset(ruleset);
										 
										 /* Translation Mapping */
										 String translationMapping  = (String)entryJsonObj.get("translationMapping");
										 data.setTranslationMapping(translationMapping);
										 
										 /* Script */
										 String script = (String)entryJsonObj.get("script");
										 data.setScript(script);
										 
										 /*Acceptable result code */
										 String acceptableResultCodes = (String)entryJsonObj.get("acceptableResultCodes");
										 if( acceptableResultCodes != null && acceptableResultCodes.isEmpty() == false ){
										
											 List<Integer> acceptableResultCodesArray = new ArrayList<Integer>();
											 String[] strAcceptableResultCodeArray = acceptableResultCodes.split(",");
											 
											 if(strAcceptableResultCodeArray != null &&  strAcceptableResultCodeArray.length > 0 ){
												 for( String resultCode :  strAcceptableResultCodeArray ){
													 if( resultCode != null && resultCode.isEmpty() == false ){
														acceptableResultCodesArray.add(Integer.parseInt(resultCode));
													}
												 }
											 }
											 
											 data.getAcceptableResultCodes().addAll(acceptableResultCodesArray);
										 }
										 
										/* Peer Group Id */
										 String peerGroupId= (String)entryJsonObj.get("peerGroupId");
										 data.setPeerGroupId(peerGroupId);
										 
										 communicationEntryDatas.add(data);
									 }
								 
									 proxyCommunicationHandlerData.getEntries().addAll(communicationEntryDatas);
								 }
								 /* Adding Broadcast Hander to Handler Data List */
								 handlersDataList.add(proxyCommunicationHandlerData);
								 Logger.getLogger().info(TGPPAAAPOLICY, "Proxy Communication handler added succesfully");
							} else if (handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.CDR_HANDLER)) {
								
								Logger.getLogger().info(TGPPAAAPOLICY, "Fetching CDR handler data ");
								
								DiameterCDRGenerationHandlerData diameterCDRGenerationHandlerData = new DiameterCDRGenerationHandlerData();
							
								/* Handler Enabled/Disabled */
								String handlerEnabled = (String)handlerObject.get(IS_HANDLER_ENABLED);
								diameterCDRGenerationHandlerData.setEnabled(handlerEnabled);
								
								/* Handler Name */
								String handlerName = (String)handlerObject.get(HANDLER_NAME);
								diameterCDRGenerationHandlerData.setHandlerName(handlerName);
								
								List<DiameterCDRHandlerEntryData> diameterCDRHandlerEntryDataList = new ArrayList<DiameterCDRHandlerEntryData>();
								
								JSONArray entries = (JSONArray)handlerObject.get(ENTRIES);
								 
								if( entries != null && entries.size() > 0 ){
								
									 for(Object  entryObj : entries){
											
										 DiameterCDRHandlerEntryData data = new DiameterCDRHandlerEntryData();
										 JSONObject entryJsonObj = (JSONObject) entryObj;
										 
										 /* Ruleset */
										 String ruleset  = (String)entryJsonObj.get(RULESET);
										 data.setRuleset(ruleset);
										 
										 /* wait */
										 String wait = ( String )entryJsonObj.get("wait");
										 data.setWait(Boolean.parseBoolean(wait));
										
										 /* Diameter Profile Driver */
										 DiameterProfileDriverDetails diameterProfileDriverDetails = new DiameterProfileDriverDetails();
										 
										 /* Primary Driver */
										 List<PrimaryDriverDetail> primaryDriverDetailsList = new ArrayList<PrimaryDriverDetail>();
										 String primaryDriverId  = (String)entryJsonObj.get("primaryDriverId");
										
										 if (primaryDriverId != null && primaryDriverId.isEmpty() == false) {
											 PrimaryDriverDetail driverDetail = new PrimaryDriverDetail();
											 driverDetail.setDriverInstanceId(primaryDriverId);
											 driverDetail.setWeightage(1);
											 
											 primaryDriverDetailsList.add(driverDetail);
										 }
										 diameterProfileDriverDetails.setPrimaryDriverGroup(primaryDriverDetailsList);
										 
										 /* Secondary Driver */
										 List<SecondaryAndCacheDriverDetail> secondaryDriverGroupList = new ArrayList<SecondaryAndCacheDriverDetail>();
										 String secondaryDriverId  = (String)entryJsonObj.get("secondaryDriverId");
										 
										 if (secondaryDriverId != null && secondaryDriverId.isEmpty() == false && "0".equals(secondaryDriverId) == false) {
											 SecondaryAndCacheDriverDetail driverDetail = new SecondaryAndCacheDriverDetail();
											 driverDetail.setSecondaryDriverId(secondaryDriverId);
											 
											 secondaryDriverGroupList.add(driverDetail);
										 }
										 diameterProfileDriverDetails.setSecondaryDriverGroup(secondaryDriverGroupList);
										 
										 /* Driver Script */
										 String driverScript = (String)entryJsonObj.get(DRIVER_SCRIPT);
										  
										 diameterProfileDriverDetails.setDriverScript(driverScript);
										 
										 data.setDriverDetails(diameterProfileDriverDetails);
										 
										 diameterCDRHandlerEntryDataList.add(data);
									 }
									 
									 diameterCDRGenerationHandlerData.getEntries().addAll(diameterCDRHandlerEntryDataList);
								 }
									
								 /* Adding CDR Hander to Handler Data List */
								 handlersDataList.add(diameterCDRGenerationHandlerData);
								 Logger.getLogger().info(TGPPAAAPOLICY, "CDR Communication handler added succesfully");
							}
						 }
						 
						/* Post Response Command Code Flow */
						JSONArray postResponseHandlerList = commandCodeFlowJson.getJSONArray("postResponseHandlerList");
						DiameterPostResponseHandlerData postResponseHandlerData = new  DiameterPostResponseHandlerData();
							 
						List<DiameterApplicationHandlerData> postResponseHandlersDataList = new ArrayList<DiameterApplicationHandlerData>();
						
						if( postResponseHandlerList != null && postResponseHandlerList.isEmpty() == false ){
							
							for(Object  handler : postResponseHandlerList){
								
								 JSONObject jsonObject = (JSONObject) handler;
								 JSONObject handlerObject =(JSONObject)jsonObject.get("counter");
								 String handlerType = (String)handlerObject.get("handlerType");
								 
								 if( handlerType != null && handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.CDR_HANDLER)){
									 
									Logger.getLogger().info(TGPPAAAPOLICY, "Fetching CDR handler data ");
										
									DiameterCDRGenerationHandlerData diameterCDRGenerationHandlerData = new DiameterCDRGenerationHandlerData();
									
									/* Handler Enabled/Disabled */
									String handlerEnabled = (String)handlerObject.get(IS_HANDLER_ENABLED);
									diameterCDRGenerationHandlerData.setEnabled(handlerEnabled);
										
									/* Handler Name */
									String handlerName = (String)handlerObject.get(HANDLER_NAME);
									diameterCDRGenerationHandlerData.setHandlerName(handlerName);
								
									
									List<DiameterCDRHandlerEntryData> diameterCDRHandlerEntryDataList = new ArrayList<DiameterCDRHandlerEntryData>();
										
									JSONArray entries = (JSONArray)handlerObject.get(ENTRIES);
										 
									if( entries != null && entries.size() > 0 ){
										
										 for(Object  entryObj : entries){
													
											 DiameterCDRHandlerEntryData data = new DiameterCDRHandlerEntryData();
											 JSONObject entryJsonObj = (JSONObject) entryObj;
												 
											 /* Ruleset */
											 String ruleset  = (String)entryJsonObj.get(RULESET);
											 data.setRuleset(ruleset);
											 
											 /* wait */
											 String wait = ( String )entryJsonObj.get("wait");
											 data.setWait(Boolean.parseBoolean(wait));
											
											 /* Diameter Profile Driver */
											 DiameterProfileDriverDetails diameterProfileDriverDetails = new DiameterProfileDriverDetails();
											 
											 /* Primary Driver */
											 List<PrimaryDriverDetail> primaryDriverDetailsList = new ArrayList<PrimaryDriverDetail>();
											 String primaryDriverId  = (String)entryJsonObj.get("primaryDriverId");
											
											 if (primaryDriverId != null && primaryDriverId.isEmpty() == false) {
												 PrimaryDriverDetail driverDetail = new PrimaryDriverDetail();
												 driverDetail.setDriverInstanceId(primaryDriverId);
												 driverDetail.setWeightage(1);
												 
												 primaryDriverDetailsList.add(driverDetail);
											 }
											 diameterProfileDriverDetails.setPrimaryDriverGroup(primaryDriverDetailsList);
												 
											 /* Secondary Driver */
											 List<SecondaryAndCacheDriverDetail> secondaryDriverGroupList = new ArrayList<SecondaryAndCacheDriverDetail>();
											 String secondaryDriverId  = (String)entryJsonObj.get("secondaryDriverId");
											 
											 if (secondaryDriverId != null && secondaryDriverId.isEmpty() == false) {
												 SecondaryAndCacheDriverDetail driverDetail = new SecondaryAndCacheDriverDetail();
												 driverDetail.setSecondaryDriverId(secondaryDriverId);
												 
												 secondaryDriverGroupList.add(driverDetail);
											 }
											 diameterProfileDriverDetails.setSecondaryDriverGroup(secondaryDriverGroupList);
											 
											 /* Driver Script */
											 String driverScript = (String)entryJsonObj.get(DRIVER_SCRIPT);
											  
											 diameterProfileDriverDetails.setDriverScript(driverScript);
											 
											 data.setDriverDetails(diameterProfileDriverDetails);
											 
											 diameterCDRHandlerEntryDataList.add(data);
										 }
										 
										 diameterCDRGenerationHandlerData.getEntries().addAll(diameterCDRHandlerEntryDataList);
									}
											
										/* Adding CDR Hander to Handler Data List */
									postResponseHandlersDataList.add(diameterCDRGenerationHandlerData);
									Logger.getLogger().info(TGPPAAAPOLICY, "CDR Communication handler added succesfully in Post Response Command Code flow");
								 } else if( handlerType != null && handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.PLUGIN_HANDLER)){
									 
									 Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Plugin handler data ");
									 DiameterPluginHandlerData pluginHandlerData = new DiameterPluginHandlerData();
									 
									 /* Handler Enabled/Disabled */
									 String handlerEnabled = (String)handlerObject.get(IS_HANDLER_ENABLED);
									 pluginHandlerData.setEnabled(handlerEnabled);
									
									 /* Handler Name */
									 String handlerName = (String)handlerObject.get(HANDLER_NAME);
									 pluginHandlerData.setHandlerName(handlerName);
									 
									 List<PluginEntryData> pluginEntryDataList = new ArrayList<PluginEntryData>();
									 JSONArray entries = (JSONArray) handlerObject.get("pluginEntries");
									 
									 if( entries != null && entries.size() > 0 ){
									
										 for(Object  entryObj : entries){
											 PluginEntryData pluginEntryData = new PluginEntryData();
											 
											 JSONObject entryJsonObj = (JSONObject) entryObj;
											 
											 /* Ruleset */
											 String ruleset  = (String)entryJsonObj.get(RULESET);
											 pluginEntryData.setRuleset(ruleset);
											 
											 /* Plugin Name */
											 String pluginName  = (String)entryJsonObj.get("pluginName");
											 pluginEntryData.setPluginName(pluginName);
											 
											 /* Plugin Argument */
											 String pluginArgument  = (String)entryJsonObj.get("pluginArgument");
											 pluginEntryData.setPluginArgument(pluginArgument);
											 
											 /* onResponse */
											 String onResponse = (String)entryJsonObj.get("onResponse");
											 pluginEntryData.setOnResponse(Boolean.parseBoolean(onResponse));
											 
											 pluginEntryDataList.add(pluginEntryData);
										 }
									 
										 pluginHandlerData.getPluginEntries().addAll(pluginEntryDataList);
								    }
									 
									 /* Adding Plugin Hander to Handler Data List */
									 postResponseHandlersDataList.add(pluginHandlerData);
									 Logger.getLogger().info(TGPPAAAPOLICY, "Plugin handler added succesfully");
								 }
							}
							
							/* Adding Post Response Command Code Flow */
							postResponseHandlerData.getHandlersData().addAll(postResponseHandlersDataList);
						}
						
						commandCodeFlowData.getHandlersData().addAll(handlersDataList);
						commandCodeFlowData.setPostResponseHandlerData(postResponseHandlerData);
						commandCodeFlowDataList.add(commandCodeFlowData);
					 }
				}
			}
			
			tgppServerPolicyData.getCommandCodeFlows().addAll(commandCodeFlowDataList);
			
			/* Generate XML */
			JAXBContext jaxbContext = JAXBContext.newInstance(TGPPServerPolicyData.class);
		    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		    java.io.StringWriter xmlObj = new StringWriter();
		    jaxbMarshaller.marshal(tgppServerPolicyData,xmlObj);
			
		    String xmlDatas = xmlObj.toString().trim();
			return xmlDatas.getBytes();
		}catch (Exception e) {
			throw e;
		}
	}

	private void convertFromFormToData(	TGPPAAAPolicyForm tgppAAAPolicyForm, TGPPAAAPolicyData tgppAAAPolicyData) {
		tgppAAAPolicyData.setName(tgppAAAPolicyForm.getName());
		tgppAAAPolicyData.setDescription(tgppAAAPolicyForm.getDescription());
		
		if(BaseConstant.SHOW_STATUS.equals(tgppAAAPolicyForm.getStatus())){
			tgppAAAPolicyData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			tgppAAAPolicyData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}
		
		tgppAAAPolicyData.setRuleset(tgppAAAPolicyForm.getRuleset());
	
		tgppAAAPolicyData.setSessionManagement(tgppAAAPolicyForm.getsessionManagement());
		tgppAAAPolicyData.setUserIdentity(tgppAAAPolicyForm.getUserIdentity());
		tgppAAAPolicyData.setCui(tgppAAAPolicyForm.getCui());
		tgppAAAPolicyData.setDefaultResponseBehaviorArgument(tgppAAAPolicyForm.getDefaultResponseBehaviourArgument());
		tgppAAAPolicyData.setDefaultResponseBehaviour(tgppAAAPolicyForm.getDefaultResponseBehaviour());
	}
}
