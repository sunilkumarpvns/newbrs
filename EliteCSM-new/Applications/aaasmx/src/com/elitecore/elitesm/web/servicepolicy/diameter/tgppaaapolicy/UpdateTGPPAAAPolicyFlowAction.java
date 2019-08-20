package com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.diameter.diameterconcurrency.DiameterConcurrencyBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.TGPPAAAPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AdditionalDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.PluginEntryData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.PrimaryDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.SecondaryAndCacheDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.UpdateIdentityParamsDetail;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.constants.ServiceTypeConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.form.TGPPAAAPolicyForm;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.CommandCodeFlowData;
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
import com.google.gson.Gson;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author nayana.rathod
 *
 */

public class UpdateTGPPAAAPolicyFlowAction extends BaseWebAction{

	private static final String SECONDARY_DRIVER_ID = "secondaryDriverId";
	private static final String PRIMARY_DRIVER_ID = "primaryDriverId";
	private static final String ENTRIES = "entries";
	private static final String PEER_GROUP_ID = "peerGroupId";
	private static final String ACCEPTABLE_RESULT_CODES = "acceptableResultCodes";
	private static final String SCRIPT = "script";
	private static final String TRANSLATION_MAPPING = "translationMapping";
	private static final String PROXY_MODE = "proxyMode";
	private static final String RESULT_CODE_WHEN_NO_ENTRY_SELECTED = "resultCodeWhenNoEntrySelected";
	private static final String PRIORITY_RESULT_CODES = "priorityResultCodes";
	private static final String PROTOCOL = "protocol";
	private static final String PLUGIN_ENTRIES = "pluginEntries";
	private static final String ON_RESPONSE = "onResponse";
	private static final String PLUGIN_ARGUMENT = "pluginArgument";
	private static final String PLUGIN_NAME = "pluginName";
	private static final String DRIVER_SCRIPT = "driverScript";
	private static final String ENABLED = "enabled";
	private static final String HANDLER_NAME = "handlerName";
	private static final String HANDLER_TYPE = "handlerType";
	private static final String RULESET = "ruleset";
	private static final String INFORMATION = "information";
	private static final String UPDATE_FORWARD = "updateTGPPAAAPolicyCommandCode";
	private static final String FAILURE_FORWARD = "failure";
	private static final String TGPPAAAPOLICY = "UpdateTGPPAAAPolicyFlowAction";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_TGPP_AAA_SERVICE_POLICY;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_TGPP_AAA_SERVICE_POLICY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		boolean checkAccess ;
		String viewPage = request.getParameter("isView");
		if(viewPage == null ){
			 checkAccess = (checkAccess(request, UPDATE_ACTION_ALIAS));
			 request.setAttribute("isViewPage",false);
		}else{
			 checkAccess = (checkAccess(request, VIEW_ACTION_ALIAS));
			 request.setAttribute("isViewPage",true);
		}
		if (checkAccess) {	
			
			Logger.logTrace(TGPPAAAPOLICY,"Enter execute method of"+getClass().getName());
			TGPPAAAPolicyForm tgppAAAPolicyForm = (TGPPAAAPolicyForm)form;
			
			try{
				
				TGPPAAAPolicyBLManager tgppAAAPolicyBLManager = new TGPPAAAPolicyBLManager();
				DiameterConcurrencyBLManager concurrencyBLManager=new DiameterConcurrencyBLManager();
				EAPConfigBLManager eapConfigBLManager = new EAPConfigBLManager();
				GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
				ServicePolicyBLManager servicePoilcyBLManager = new ServicePolicyBLManager();
				String action = tgppAAAPolicyForm.getAction();
				
				String strTGPPAAAPolicyID = request.getParameter("tgppAAAPolicyId");
				String tgppAAAPolicyID;
				
				IStaffData staffData = getStaffObject((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm"));
				
				if (strTGPPAAAPolicyID != null) {
					tgppAAAPolicyID = strTGPPAAAPolicyID;
				} else {
					tgppAAAPolicyID = tgppAAAPolicyForm.getTgppAAAPolicyId();
				}

				if (action == null || action.equals("")) {
					if( Strings.isNullOrBlank(tgppAAAPolicyID) == false){
						
						TGPPAAAPolicyData tgppAAAPolicyData = new TGPPAAAPolicyData();
						tgppAAAPolicyData.setTgppAAAPolicyId(tgppAAAPolicyID);
						
						/* Fetching EAP Config Data */
						List<EAPConfigData> eapConfigurationList = eapConfigBLManager.getEapConfigurationList();
						
						/* Fetching Auth Method Type Data */
						List<AuthMethodTypeData> authMethodTypeDataList = servicePoilcyBLManager.getAuthMethodTypeList();
						String[] selectedAuthMethodTypes ;
				        List<String> selectedAuthMethodTypesList = new ArrayList<String>(); 
						
				        for (int i = 0; i < authMethodTypeDataList.size(); i++) {
							long authTypeId = authMethodTypeDataList.get(i).getAuthMethodTypeId();
							if (authTypeId != 5) {
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
						
						/*Fetching Radius ESI List */
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
						
						for (int i = 0; i < cacheableDriverInstDataList.size(); i++) {
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
						
						for (int i = 0; i < driverList.size(); i++) {
							DriverInstanceData data = driverList.get(i);				
								driverInstanceNames[i][0] = String.valueOf(data.getName());
								driverInstanceNames[i][1] = String.valueOf(data.getDescription());
							driverInstanceIds[i] = String.valueOf(data.getDriverInstanceId());
						}
				        
						List<DriverInstanceData> listOfAcctDriver = driverBlManager.getDriverInstanceList(ServiceTypeConstants.NAS_ACCT_APPLICATION);
						List<DriverTypeData> driverTypeList=driverBlManager.getAcctDriverTypeList(ServiceTypeConstants.NAS_ACCT_APPLICATION);
						List<DiameterConcurrencyData> diaConcurrencyInstanceDataList= concurrencyBLManager.getDiameterConcurrencyDataList();
						
				        tgppAAAPolicyForm.setSelectedAuthMethodTypes(selectedAuthMethodTypes);
				        tgppAAAPolicyForm.setAuthMethodTypeDataList(authMethodTypeDataList);
				        tgppAAAPolicyForm.setEapConfigurationList(eapConfigurationList);
				        tgppAAAPolicyForm.setGracePolicyList(gracePolicyList);
				        tgppAAAPolicyForm.setDiaTodiaTranslationMappingConfDataList(diaTodiaTranslationMappingConfDataList);
				        tgppAAAPolicyForm.setDiaToradiusTranslationMappingConfDataList(diaToradiusTranslationMappingConfDataList);
				        tgppAAAPolicyForm.setDiaTodiaCopyPacketMappingConfDataList(diaTodiaCopyPacketMappingConfDataList);
				        tgppAAAPolicyForm.setDiaToradiusCopyPacketMappingConfDataList(diaToradiusCopyPacketMappingConfDataList);
				        tgppAAAPolicyForm.setDiaconcurrencyInstanceDataList(diaConcurrencyInstanceDataList);
						
				        /* Convert Bean to Form */
						tgppAAAPolicyData = tgppAAAPolicyBLManager.getTGPPAAAPolicyData(tgppAAAPolicyID);
						tgppAAAPolicyForm.setsessionManagement(tgppAAAPolicyData.getSessionManagement());
						/* fetch XML Data */
						String xmlDatas = new String(tgppAAAPolicyData.getTgppAAAPolicyXml());
 						StringReader stringReader =new StringReader(xmlDatas.trim());
						
 						//Convert into relevant POJO 
						JAXBContext context = JAXBContext.newInstance(TGPPServerPolicyData.class);
						Unmarshaller unmarshaller = context.createUnmarshaller();
						TGPPServerPolicyData tgppServerPolicyData = (TGPPServerPolicyData) unmarshaller.unmarshal(stringReader);
						
						JSONObject tgppServerPolicyObject = getTGPPServerPolicyDataJSON(tgppServerPolicyData);
						
						tgppAAAPolicyForm.setTgppCommandCodeJSON(tgppServerPolicyObject.toString());
						
						/* Driver Script and External Radius Script */
						ScriptBLManager scriptBLManager = new ScriptBLManager();
						List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
						List<ScriptInstanceData> externalScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.EXTERNAL_RADIUS_SCRIPT);
					
						tgppAAAPolicyForm.setDriverScriptList(driverScriptList);
						tgppAAAPolicyForm.setExternalScriptList(externalScriptList);
						
						request.setAttribute("tgppAAAPolicyData",tgppAAAPolicyData);
						request.setAttribute("tgppAAAPolicyForm",tgppAAAPolicyForm);
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
						request.getSession().setAttribute("tgppAAAPolicyForm", tgppAAAPolicyForm);
					}
				} else if (action != null && action.equals("update")) {

					TGPPAAAPolicyData tgppAAAPolicyData = new TGPPAAAPolicyData();
					tgppAAAPolicyData.setTgppAAAPolicyId(tgppAAAPolicyData.getTgppAAAPolicyId());
					
					tgppAAAPolicyData = tgppAAAPolicyBLManager.getTGPPAAAPolicyData(tgppAAAPolicyID);
					
					Logger.getLogger().info(TGPPAAAPOLICY, "JSON in update ");
					Logger.getLogger().info(TGPPAAAPOLICY, tgppAAAPolicyForm.getTgppCommandCodeJSON());
					
					tgppAAAPolicyData.setTgppAAAPolicyXml(getTGPPAAAPolicyXMLData(tgppAAAPolicyForm.getTgppCommandCodeJSON(),tgppAAAPolicyData));
					
					staffData.setAuditId(tgppAAAPolicyData.getAuditUid());
					staffData.setAuditName(tgppAAAPolicyData.getName());
					
					tgppAAAPolicyBLManager.updateTgppAAAPolicyByID(tgppAAAPolicyData, staffData, UPDATE_ACTION_ALIAS, ConfigConstant.IS_AUDIT_ENABLED);
					doAuditing(staffData, UPDATE_ACTION_ALIAS);
					
					Logger.logDebug(TGPPAAAPOLICY, "TGPPAAAPolicyData : "+tgppAAAPolicyData);
	                request.setAttribute("responseUrl","/viewTGPPAAAPolicy.do?tgppAAAPolicyId="+tgppAAAPolicyData.getTgppAAAPolicyId());
	                ActionMessage message = new ActionMessage("tgppaaaservicepolicy.update.success");
	                ActionMessages messages = new ActionMessages();
	                messages.add(INFORMATION, message);
	                saveMessages(request, messages);
	                return mapping.findForward(SUCCESS);
				}
				
				return mapping.findForward(UPDATE_FORWARD);
				
			} catch (DuplicateInstanceNameFoundException e) {
				e.printStackTrace();
				Logger.logTrace(TGPPAAAPOLICY,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("tgppaaaservicepolicy.update.failure",tgppAAAPolicyForm.getName());
			    ActionMessages messages = new ActionMessages();
			    messages.add(INFORMATION, message);
			    saveErrors(request, messages);
			    
			} catch (Exception e) {
				e.printStackTrace();
				Logger.logTrace(TGPPAAAPOLICY,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("tgppaaaservicepolicy.update.failure");
			    ActionMessages messages = new ActionMessages();
			    messages.add(INFORMATION, message);
			    saveErrors(request, messages);
			}
			return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logError(TGPPAAAPOLICY, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add(INFORMATION, message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	private JSONObject getTGPPServerPolicyDataJSON( TGPPServerPolicyData tgppServerPolicyData ) {
		
		Gson gson = new Gson();
		
		JSONObject tgppPolicyData = new JSONObject();
		tgppPolicyData.put("name", tgppServerPolicyData.getName());
		tgppPolicyData.put("description", tgppServerPolicyData.getDescription());
		tgppPolicyData.put("userIdentity",tgppServerPolicyData.getUserIdentity());
		tgppPolicyData.put(RULESET, tgppServerPolicyData.getRuleSet());
		tgppPolicyData.put("sessionManagement",tgppServerPolicyData.getSessionManagementEnabled());
        
		JSONArray commandCodeList = new JSONArray();
		List<CommandCodeFlowData> listCodeFlowDatas = tgppServerPolicyData.getCommandCodeFlows();

		if (listCodeFlowDatas != null && listCodeFlowDatas.isEmpty() == false) {
			
			JSONObject commandCodeObject = new JSONObject();
			
			for( CommandCodeFlowData commandCodeFlowData : listCodeFlowDatas ){
				
				commandCodeObject.put("commandCode", commandCodeFlowData.getCommandCode());
				commandCodeObject.put("interfaceId", commandCodeFlowData.getInterfaceId());
				commandCodeObject.put("name", commandCodeFlowData.getName());
				
				JSONArray handlerList =  new JSONArray();
				
				List<DiameterApplicationHandlerData> handlersDataList = commandCodeFlowData.getHandlersData();
				
				if (handlersDataList != null && handlersDataList.isEmpty() == false) {
				
					for (DiameterApplicationHandlerData diameterApplicationHandlerData : handlersDataList) {
					
						JSONObject handlerObject = new JSONObject();
						
						if (diameterApplicationHandlerData instanceof DiameterAuthenticationHandlerData) {

							DiameterAuthenticationHandlerData authenticationHandlerData = (DiameterAuthenticationHandlerData)diameterApplicationHandlerData;
							
							String authMethodTypes = gson.toJson(authenticationHandlerData.getSupportedAuthenticationMethods());
							
							handlerObject.put(HANDLER_TYPE, "DiameterAuthenticationHandlerData");
							handlerObject.put(HANDLER_NAME, authenticationHandlerData.getHandlerName());
							handlerObject.put(ENABLED, authenticationHandlerData.getEnabled());
							handlerObject.put("supportedAuthenticationMethods", authMethodTypes);
							handlerObject.put("eapConfigId",authenticationHandlerData.getEapConfigId() );
							
							handlerList.add(handlerObject);
							
						} else if (diameterApplicationHandlerData instanceof DiameterAuthorizationHandlerData) {
							
							DiameterAuthorizationHandlerData authorizationHandlerData = (DiameterAuthorizationHandlerData)diameterApplicationHandlerData;

							handlerObject.put(HANDLER_TYPE, "DiameterAuthorizationHandlerData");
							handlerObject.put(HANDLER_NAME, authorizationHandlerData.getHandlerName());
							handlerObject.put(ENABLED, authorizationHandlerData.getEnabled());
							handlerObject.put("rejectOnRejectItemNotFound",authorizationHandlerData.isRejectOnRejectItemNotFound());
							handlerObject.put("rejectOnCheckItemNotFound", authorizationHandlerData.isRejectOnCheckItemNotFound());
							handlerObject.put("acceptOnPolicyOnFound", authorizationHandlerData.isAcceptOnPolicyNotFound());
							handlerObject.put("wimaxEnabled", authorizationHandlerData.isWimaxEnabled());
							handlerObject.put("gracePolicy", authorizationHandlerData.getGracePolicy());
							handlerObject.put("defaultSessionTimeoutInSeconds", authorizationHandlerData.getDefaultSessionTimeoutInSeconds());
							
							handlerList.add(handlerObject);
							
						} else if (diameterApplicationHandlerData instanceof DiameterSubscriberProfileRepositoryDetails) {

							DiameterSubscriberProfileRepositoryDetails diameterSubscriberProfileRepositoryDetails = (DiameterSubscriberProfileRepositoryDetails)diameterApplicationHandlerData;
							
							handlerObject.put(HANDLER_TYPE, "DiameterSubscriberProfileRepositoryDetails");
							handlerObject.put(HANDLER_NAME, diameterSubscriberProfileRepositoryDetails.getHandlerName());
							handlerObject.put(ENABLED, diameterSubscriberProfileRepositoryDetails.getEnabled());
							handlerObject.put("anonymousProfileIdentity", diameterSubscriberProfileRepositoryDetails.getAnonymousProfileIdentity());
							handlerObject.put("stripIdentity", diameterSubscriberProfileRepositoryDetails.getUpdateIdentity().getStripIdentity());
							handlerObject.put("separator", diameterSubscriberProfileRepositoryDetails.getUpdateIdentity().getSeparator());
							handlerObject.put("trimIdentity", diameterSubscriberProfileRepositoryDetails.getUpdateIdentity().getTrimIdentity());
							handlerObject.put("trimPassword", diameterSubscriberProfileRepositoryDetails.getUpdateIdentity().getTrimPassword());
							handlerObject.put("iCase", diameterSubscriberProfileRepositoryDetails.getUpdateIdentity().getIdetityFormat());
							handlerObject.put(DRIVER_SCRIPT, diameterSubscriberProfileRepositoryDetails.getDriverDetails().getDriverScript());
							
							if(diameterSubscriberProfileRepositoryDetails.getDriverDetails() != null ){
								
								DiameterProfileDriverDetails profileDriverDetails = diameterSubscriberProfileRepositoryDetails.getDriverDetails();
							
								if( profileDriverDetails != null ){
									
									if (Collectionz.isNullOrEmpty(profileDriverDetails.getPrimaryDriverGroup()) == false) {
										String primaryDriverGroupList = gson.toJson(profileDriverDetails.getPrimaryDriverGroup());
										handlerObject.put("primaryDriverGroupList",primaryDriverGroupList);
									}
									
									if (Collectionz.isNullOrEmpty(profileDriverDetails.getSecondaryDriverGroup()) == false) {
										String secondaryDriverGroupList = gson.toJson(profileDriverDetails.getSecondaryDriverGroup());
										handlerObject.put("secondaryDriverGroupList",secondaryDriverGroupList);
									}
									
									if (Collectionz.isNullOrEmpty(profileDriverDetails.getAdditionalDriverList()) == false) {
										String additionalDriverList = gson.toJson(profileDriverDetails.getAdditionalDriverList());
										handlerObject.put("additionalDriverList",additionalDriverList);
									}
									
								}
							
							}
							
							
							handlerList.add(handlerObject);
							
						} else if (diameterApplicationHandlerData instanceof DiameterPluginHandlerData) {
						
							DiameterPluginHandlerData diameterPluginHandlerData = (DiameterPluginHandlerData) diameterApplicationHandlerData;
							
							handlerObject.put(HANDLER_TYPE, "DiameterPluginHandlerData");
							handlerObject.put(HANDLER_NAME, diameterPluginHandlerData.getHandlerName());
							handlerObject.put(ENABLED, diameterPluginHandlerData.getEnabled());
							
							List<PluginEntryData> pluginEntries = diameterPluginHandlerData.getPluginEntries();
							JSONArray pluginEntriesArray = new JSONArray();
							
							if( pluginEntries !=null && pluginEntries.isEmpty() == false ){
								
								for( PluginEntryData pluginEntryData : pluginEntries){
									
									JSONObject pluginEntryObject = new JSONObject();
									pluginEntryObject.put(RULESET, pluginEntryData.getRuleset());
									pluginEntryObject.put(PLUGIN_NAME, pluginEntryData.getPluginName());
									pluginEntryObject.put(PLUGIN_ARGUMENT, pluginEntryData.getPluginArgument());
									pluginEntryObject.put(ON_RESPONSE, pluginEntryData.isOnResponse());
									
									pluginEntriesArray.add(pluginEntryObject);
								}
							}
							
							handlerObject.put(PLUGIN_ENTRIES, pluginEntriesArray);
							handlerList.add(handlerObject);

						} else if (diameterApplicationHandlerData instanceof DiameterSynchronousCommunicationHandlerData) {

							DiameterSynchronousCommunicationHandlerData communicationHandlerData = (DiameterSynchronousCommunicationHandlerData)diameterApplicationHandlerData;
							handlerObject.put(HANDLER_TYPE, "DiameterSynchronousCommunicationHandlerData");
							handlerObject.put(HANDLER_NAME, communicationHandlerData.getHandlerName());
							handlerObject.put(ENABLED, communicationHandlerData.getEnabled());
							handlerObject.put(PROTOCOL, communicationHandlerData.getProtocol());
							handlerObject.put(PRIORITY_RESULT_CODES, communicationHandlerData.getStrPriorityResultCodes());
							handlerObject.put(RESULT_CODE_WHEN_NO_ENTRY_SELECTED, communicationHandlerData.getResultCodeOnNoEntrySelected());
							
							
							 if(ProxyMode.MULTIPLE.equals(communicationHandlerData.getProxyMode())){
								 handlerObject.put(PROXY_MODE, "MULTIPLE");
							 }else{
								 handlerObject.put(PROXY_MODE, "SINGLE");
							 }
							
							List<DiameterExternalCommunicationEntryData> entries = communicationHandlerData.getEntries();
							JSONArray entryArray = new JSONArray();
							
							if( entries != null && entries.isEmpty() == false ){
								
								for( DiameterExternalCommunicationEntryData entryData : entries ){
									
									JSONObject entryDataObj = new JSONObject();
									
									entryDataObj.put(RULESET, entryData.getRuleset());
									entryDataObj.put(TRANSLATION_MAPPING, entryData.getTranslationMapping());
									entryDataObj.put(SCRIPT, entryData.getScript());
									entryDataObj.put(ACCEPTABLE_RESULT_CODES, entryData.getAcceptableResultCodes());
									entryDataObj.put(PEER_GROUP_ID, entryData.getPeerGroupId());
									
									entryArray.add(entryDataObj);
								}
							}
							
							handlerObject.put(ENTRIES, entryArray);
							handlerList.add(handlerObject);
							
						} else if (diameterApplicationHandlerData instanceof DiameterBroadcastCommunicationHandlerData) {

							DiameterBroadcastCommunicationHandlerData communicationHandlerData = (DiameterBroadcastCommunicationHandlerData)diameterApplicationHandlerData;
							
							handlerObject.put(HANDLER_TYPE, "DiameterBroadcastCommunicationHandlerData");
							handlerObject.put(HANDLER_NAME, communicationHandlerData.getHandlerName());
							handlerObject.put(ENABLED, communicationHandlerData.getEnabled());
							handlerObject.put(PROTOCOL, communicationHandlerData.getProtocol());
							handlerObject.put(PRIORITY_RESULT_CODES, communicationHandlerData.getStrPriorityResultCodes());
							
							List<DiameterBroadcastCommunicationEntryData> entries = communicationHandlerData.getEntries();
							JSONArray entryArray = new JSONArray();
							
							if( entries != null && entries.isEmpty() == false ){
								
								for( DiameterBroadcastCommunicationEntryData entryData : entries ){
									
									JSONObject entryDataObj = new JSONObject();
									
									entryDataObj.put(RULESET, entryData.getRuleset());
									entryDataObj.put(TRANSLATION_MAPPING, entryData.getTranslationMapping());
									entryDataObj.put(SCRIPT, entryData.getScript());
									entryDataObj.put(ACCEPTABLE_RESULT_CODES, entryData.getAcceptableResultCodes());
									entryDataObj.put(PEER_GROUP_ID, entryData.getPeerGroupId());
									entryDataObj.put("wait", entryData.isWait());
									
									entryArray.add(entryDataObj);
								}
							}
							
							handlerObject.put(ENTRIES, entryArray);
							handlerList.add(handlerObject);
						}else if (diameterApplicationHandlerData instanceof DiameterConcurrencyHandlerData) {
							
							DiameterConcurrencyHandlerData concurrencyHandlerData=(DiameterConcurrencyHandlerData) diameterApplicationHandlerData;
							handlerObject.put(HANDLER_TYPE, "DiameterConcurrencyHandlerData");
							handlerObject.put(HANDLER_NAME, concurrencyHandlerData.getHandlerName());
							handlerObject.put(ENABLED, concurrencyHandlerData.getEnabled());
							handlerObject.put(RULESET,concurrencyHandlerData.getRuleSet());
							handlerObject.put("diaConConfigId", concurrencyHandlerData.getDiaConcurrencyId());
						
							handlerList.add(handlerObject);
				
						}else if (diameterApplicationHandlerData instanceof DiameterCDRGenerationHandlerData) {
							DiameterCDRGenerationHandlerData cdrGenHandlerData = (DiameterCDRGenerationHandlerData)diameterApplicationHandlerData;
							
							handlerObject.put(HANDLER_TYPE, "DiameterCDRGenerationHandlerData");
							handlerObject.put(HANDLER_NAME, cdrGenHandlerData.getHandlerName());
							handlerObject.put(ENABLED, cdrGenHandlerData.getEnabled());
						
							List<DiameterCDRHandlerEntryData> entries = cdrGenHandlerData.getEntries();
							JSONArray entryArray = new JSONArray();
							
							if( entries != null && entries.isEmpty() == false ){
								
								for( DiameterCDRHandlerEntryData entryData : entries ){
									
									JSONObject entryDataObj = new JSONObject();
									
									entryDataObj.put(RULESET, entryData.getRuleset());
									entryDataObj.put("wait", entryData.isWait());
									entryDataObj.put(DRIVER_SCRIPT, entryData.getDriverDetails().getDriverScript());
									
									if( entryData.getDriverDetails().getPrimaryDriverGroup() != null && entryData.getDriverDetails().getPrimaryDriverGroup().isEmpty() == false ){
										
										if( entryData.getDriverDetails().getPrimaryDriverGroup().get(0) != null ){
											String primaryDriverId = entryData.getDriverDetails().getPrimaryDriverGroup().get(0).getDriverInstanceId();
											entryDataObj.put(PRIMARY_DRIVER_ID,primaryDriverId);
										}
									}
									
									if( entryData.getDriverDetails().getSecondaryDriverGroup() != null && entryData.getDriverDetails().getSecondaryDriverGroup().isEmpty() == false ){
										if( entryData.getDriverDetails().getSecondaryDriverGroup().get(0) != null ){
											String primaryDriverId = entryData.getDriverDetails().getSecondaryDriverGroup().get(0).getSecondaryDriverId();
											entryDataObj.put(SECONDARY_DRIVER_ID,primaryDriverId);
										}
									} else {
										entryDataObj.put(SECONDARY_DRIVER_ID,"0");
									}
									
									entryArray.add(entryDataObj);
								}
							}
							
							handlerObject.put(ENTRIES, entryArray);
							handlerList.add(handlerObject);
						}
					}
				}
				
				JSONArray postResHandlerList =  new JSONArray();
				
				List<DiameterApplicationHandlerData> posResHandlersDataList = new ArrayList<DiameterApplicationHandlerData>();
				if(commandCodeFlowData.getPostResponseHandlerData() != null){
					posResHandlersDataList = commandCodeFlowData.getPostResponseHandlerData().getHandlersData();
				}
				
				if (posResHandlersDataList != null && posResHandlersDataList.isEmpty() == false) {
					
					for (DiameterApplicationHandlerData diameterApplicationHandlerData : posResHandlersDataList) {
						
						JSONObject handlerObject = new JSONObject();
						
						if (diameterApplicationHandlerData instanceof DiameterCDRGenerationHandlerData) {
							
							DiameterCDRGenerationHandlerData cdrGenHandlerData = (DiameterCDRGenerationHandlerData)diameterApplicationHandlerData;
							
							handlerObject.put(HANDLER_TYPE, "DiameterCDRGenerationHandlerData");
							handlerObject.put(HANDLER_NAME, cdrGenHandlerData.getHandlerName());
							handlerObject.put(ENABLED, cdrGenHandlerData.getEnabled());
						
							List<DiameterCDRHandlerEntryData> entries = cdrGenHandlerData.getEntries();
							JSONArray entryArray = new JSONArray();
							
							if( entries != null && entries.isEmpty() == false ){
								
								for( DiameterCDRHandlerEntryData entryData : entries ){
									
									JSONObject entryDataObj = new JSONObject();
									
									entryDataObj.put(RULESET, entryData.getRuleset());
									entryDataObj.put("wait", entryData.isWait());
									entryDataObj.put(DRIVER_SCRIPT, entryData.getDriverDetails().getDriverScript());
									
									if( entryData.getDriverDetails().getPrimaryDriverGroup() != null && entryData.getDriverDetails().getPrimaryDriverGroup().isEmpty() == false ){
										
										if( entryData.getDriverDetails().getPrimaryDriverGroup().get(0) != null ){
											String primaryDriverId = entryData.getDriverDetails().getPrimaryDriverGroup().get(0).getDriverInstanceId();
											entryDataObj.put(PRIMARY_DRIVER_ID,primaryDriverId);
										}
									}
									
									if( entryData.getDriverDetails().getSecondaryDriverGroup() != null && entryData.getDriverDetails().getSecondaryDriverGroup().isEmpty() == false ){
										if( entryData.getDriverDetails().getSecondaryDriverGroup().get(0) != null ){
											String primaryDriverId = entryData.getDriverDetails().getSecondaryDriverGroup().get(0).getSecondaryDriverId();
											entryDataObj.put(SECONDARY_DRIVER_ID,primaryDriverId);
										}
									}
									
									entryArray.add(entryDataObj);
								}
							}
							
							handlerObject.put(ENTRIES, entryArray);
							postResHandlerList.add(handlerObject);
						} else if (diameterApplicationHandlerData instanceof DiameterPluginHandlerData) {
						
							DiameterPluginHandlerData diameterPluginHandlerData = (DiameterPluginHandlerData) diameterApplicationHandlerData;
							
							handlerObject.put(HANDLER_TYPE, "DiameterPluginHandlerData");
							handlerObject.put(HANDLER_NAME, diameterPluginHandlerData.getHandlerName());
							handlerObject.put(ENABLED, diameterPluginHandlerData.getEnabled());
							
							List<PluginEntryData> pluginEntries = diameterPluginHandlerData.getPluginEntries();
							JSONArray pluginEntriesArray = new JSONArray();
							
							if( pluginEntries !=null && pluginEntries.isEmpty() == false ){
								
								for( PluginEntryData pluginEntryData : pluginEntries){
									
									JSONObject pluginEntryObject = new JSONObject();
									pluginEntryObject.put(RULESET, pluginEntryData.getRuleset());
									pluginEntryObject.put(PLUGIN_NAME, pluginEntryData.getPluginName());
									pluginEntryObject.put(PLUGIN_ARGUMENT, pluginEntryData.getPluginArgument());
									pluginEntryObject.put(ON_RESPONSE, pluginEntryData.isOnResponse());
									
									pluginEntriesArray.add(pluginEntryObject);
								}
							}
							
							handlerObject.put(PLUGIN_ENTRIES, pluginEntriesArray);
							postResHandlerList.add(handlerObject);
						}
					}
				}
				
				commandCodeObject.put("handlerList", handlerList);
				commandCodeObject.put("postResponseHandlerList", postResHandlerList);
				commandCodeList.add(commandCodeObject);
			}
		}

		tgppPolicyData.put("commandCodeList", commandCodeList);
		
		Logger.getLogger().info(TGPPAAAPOLICY, "TGPP Policy JSON Data is : ");
		Logger.getLogger().info(TGPPAAAPOLICY, tgppPolicyData.toString());

		return tgppPolicyData;
	}
	
	/**
	 * @param tgppCommandCodeJSON
	 * @param createTGPPAAAPolicyForm
	 * @return
	 * @throws Exception
	 */
	private byte[] getTGPPAAAPolicyXMLData(String tgppCommandCodeJSON, TGPPAAAPolicyData tgppAAAPolicyData) throws Exception {
		try{
			
			TGPPServerPolicyData tgppServerPolicyData = new TGPPServerPolicyData();
			tgppServerPolicyData.setId(tgppAAAPolicyData.getTgppAAAPolicyId());
			tgppServerPolicyData.setName(tgppAAAPolicyData.getName());
			tgppServerPolicyData.setDescription(tgppAAAPolicyData.getDescription());
			tgppServerPolicyData.setRuleSet(tgppAAAPolicyData.getRuleset());
			tgppServerPolicyData.setUserIdentity(tgppAAAPolicyData.getUserIdentity());
			tgppServerPolicyData.setSessionManagementEnabled(tgppAAAPolicyData.getSessionManagement());
			tgppServerPolicyData.setDefaultResponseBehaviorParameter(tgppAAAPolicyData.getDefaultResponseBehaviorArgument());
			tgppServerPolicyData.setDefaultResponseBehaviorType(DefaultResponseBehaviorType.valueOf(tgppAAAPolicyData.getDefaultResponseBehaviour()));
			
			TGPPServerPolicyData tgppServerPolicyDataTemp = ConfigUtil.deserialize(new String(tgppAAAPolicyData.getTgppAAAPolicyXml()), TGPPServerPolicyData.class);
			if(tgppServerPolicyDataTemp != null){
				tgppServerPolicyData.getCommandCodeResponseAttributesList().addAll(
						tgppServerPolicyDataTemp.getCommandCodeResponseAttributesList());
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
						
						/* Display label */
						String name = (String)commandCodeFlowJson.get("name");
						commandCodeFlowData.setName(name);
						
						JSONArray handlerList = commandCodeFlowJson.getJSONArray("handlerList");
						
						List<DiameterApplicationHandlerData> handlersDataList = new ArrayList<DiameterApplicationHandlerData>();
						
						 for(Object  handler : handlerList){
							
							 JSONObject jsonObject = JSONObject.fromObject(handler);
							 JSONObject handlerObject = JSONObject.fromObject(jsonObject.get("counter"));
							 String handlerType = (String)handlerObject.get(HANDLER_TYPE);
							 
							 if( handlerType != null && handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.AUTHENTICATION_HANDLER)){
								 
								 Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Authentication handler data ");
								 DiameterAuthenticationHandlerData diameterAuthenticationHandlerData = new DiameterAuthenticationHandlerData();
								
								 /* Handler Enabled/Disabled */
								 String handlerEnabled = (String)handlerObject.get("isHandlerEnabled");
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
								 String handlerEnabled = (String)handlerObject.get("isHandlerEnabled");
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
								 if (rejectOnCheckItemNotFound != null) {
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
								 if (acceptOnPolicyOnFound != null) {
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
								 
							 }else if(handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.CONCURRENCY_HANDLER)){
									
									Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Diameter concurrency handler data ");
									
									DiameterConcurrencyHandlerData diameterConcurrencyHandlerData= new DiameterConcurrencyHandlerData();
									String handlerEnabled = (String)handlerObject.get("isHandlerEnabled");	
									diameterConcurrencyHandlerData.setEnabled(handlerEnabled);
									
									
									/* Handler Name */
									String handlerName = (String)handlerObject.get(HANDLER_NAME);
									diameterConcurrencyHandlerData.setHandlerName(handlerName);
									
									/* Rule Set*/
									String ruleset = (String)handlerObject.get(RULESET);
									diameterConcurrencyHandlerData.setRuleSet(ruleset);
									
									String  dia_con_id = handlerObject.get("diaConConfigId").toString();
									diameterConcurrencyHandlerData.setDiaConcurrencyId(dia_con_id);
									handlersDataList.add(diameterConcurrencyHandlerData);
									Logger.getLogger().info(TGPPAAAPOLICY, "Concurrency hander added succesfully");
									
								}else if( handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.BROADCAST_COMMUNICATION_HANDLER)){
								 
								 Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Broadcast Communication handler data ");
								 DiameterBroadcastCommunicationHandlerData broadcastCommunicationHandlerData = new  DiameterBroadcastCommunicationHandlerData();
								
								 /* Handler Enabled/Disabled */
								 String handlerEnabled = (String)handlerObject.get("isHandlerEnabled");
								 broadcastCommunicationHandlerData.setEnabled(handlerEnabled);
								 
								 /* Handler Name */
								 String handlerName = (String)handlerObject.get(HANDLER_NAME);
								 broadcastCommunicationHandlerData.setHandlerName(handlerName);
								 
								 /* Protocol */
								 String protocol = (String)handlerObject.get(PROTOCOL);
								 broadcastCommunicationHandlerData.setProtocol(protocol);
								 
								 /* Priority Result Codes */
								 String priorityResultCodes = (String)handlerObject.get(PRIORITY_RESULT_CODES);
								 broadcastCommunicationHandlerData.setStrPriorityResultCodes(priorityResultCodes);									
								 
								 List<DiameterBroadcastCommunicationEntryData> communicationEntryDatas = new  ArrayList<DiameterBroadcastCommunicationEntryData>();
								 
								 if(protocol.equals("DIAMETER")){
									 /* Result Code When No Entry Selected */
									 long resultCodeWhenNoEntrySelected = Long.parseLong((String) handlerObject.get(RESULT_CODE_WHEN_NO_ENTRY_SELECTED));
									 broadcastCommunicationHandlerData.setResultCodeOnNoEntrySelected(resultCodeWhenNoEntrySelected);
								 }
								 
								 JSONArray entries = (JSONArray)handlerObject.get(ENTRIES);
								 
								 if( entries != null && entries.size() > 0 ){
								 
									 for(Object  entryObj : entries){
											
										 DiameterBroadcastCommunicationEntryData data = new DiameterBroadcastCommunicationEntryData();
										 JSONObject entryJsonObj = (JSONObject) entryObj;
										 
										 /* Ruleset */
										 String ruleset  = (String)entryJsonObj.get(RULESET);
										 data.setRuleset(ruleset);
										 
										 /* Translation Mapping */
										 String translationMapping  = (String)entryJsonObj.get(TRANSLATION_MAPPING);
										 data.setTranslationMapping(translationMapping);
										 
										 /* Script */
										 String script = (String)entryJsonObj.get(SCRIPT);
										 data.setScript(script);
										 
										 /* Acceptable result code */
										 String acceptableResultCodes = (String)entryJsonObj.get(ACCEPTABLE_RESULT_CODES);
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
										 String peerGroupId= (String)entryJsonObj.get(PEER_GROUP_ID);
										 data.setPeerGroupId(peerGroupId);
										 
										 communicationEntryDatas.add(data);
									 }
									 broadcastCommunicationHandlerData.getEntries().addAll(communicationEntryDatas);
								 }
								 
								 /* Adding Broadcast Hander to Handler Data List */
								 handlersDataList.add(broadcastCommunicationHandlerData);
								 Logger.getLogger().info(TGPPAAAPOLICY, "Broadcast Communication handler added succesfully");
								 
							 }else if( handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.PLUGIN_HANDLER)){
								 
								 Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Plugin handler data ");
								 DiameterPluginHandlerData pluginHandlerData = new DiameterPluginHandlerData();
								 
								 /* Handler Enabled/Disabled */
								 String handlerEnabled = (String)handlerObject.get("isHandlerEnabled");
								 pluginHandlerData.setEnabled(handlerEnabled);
								
								 /* Handler Name */
								 String handlerName = (String)handlerObject.get(HANDLER_NAME);
								 pluginHandlerData.setHandlerName(handlerName);
								 
								 List<PluginEntryData> pluginEntryDataList = new ArrayList<PluginEntryData>();
								 JSONArray entries = (JSONArray) handlerObject.get(PLUGIN_ENTRIES);
								 
								 if( entries != null && entries.size() > 0 ){
									 
									 for(Object  entryObj : entries){
									
										 PluginEntryData pluginEntryData = new PluginEntryData();
										 
										 JSONObject entryJsonObj = (JSONObject) entryObj;
										 
										 /* Ruleset */
										 String ruleset  = (String)entryJsonObj.get(RULESET);
										 pluginEntryData.setRuleset(ruleset);
										 
										 /* Plugin Name */
										 String pluginName  = (String)entryJsonObj.get(PLUGIN_NAME);
										 pluginEntryData.setPluginName(pluginName);
										 
										 /* Plugin Argument */
										 String pluginArgument  = (String)entryJsonObj.get(PLUGIN_ARGUMENT);
										 pluginEntryData.setPluginArgument(pluginArgument);
										 
										 /* onResponse */
										 String onResponse = (String)entryJsonObj.get(ON_RESPONSE);
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
								 String handlerEnabled = (String)handlerObject.get("isHandlerEnabled");
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
											 
											 String[] primaryDriverArray =  primaryStr.split(("\\),\\(|\\)|\\("));
											 String driverInstanceId = primaryDriverArray[0];
											 String weightage = primaryDriverArray[1];
											 
											 if(driverInstanceId != null && driverInstanceId.isEmpty() == false){
												 primaryDriverDetail.setDriverInstanceId(driverInstanceId);
											 }
											 
											 if(weightage != null && weightage.isEmpty() == false){
												 primaryDriverDetail.setWeightage(Integer.parseInt(weightage));
											 }
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
								 
								 Logger.getLogger().info(TGPPAAAPOLICY, "Profile lookup handler added succesfully");
							 }else if(handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.PROXY_COMMUNICATION_HANDLER)){
								 
								 Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Proxy Communication handler data ");
								 
								 DiameterSynchronousCommunicationHandlerData proxyCommunicationHandlerData = new  DiameterSynchronousCommunicationHandlerData();
								
								 /* Handler Enabled/Disabled */
								 String handlerEnabled = (String)handlerObject.get("isHandlerEnabled");
								 proxyCommunicationHandlerData.setEnabled(handlerEnabled);
								 
								 /* Handler Name */
								 String handlerName = (String)handlerObject.get(HANDLER_NAME);
								 proxyCommunicationHandlerData.setHandlerName(handlerName);
								 
								 /* Protocol */
								 String protocol = (String)handlerObject.get(PROTOCOL);
								 proxyCommunicationHandlerData.setProtocol(protocol);
								 
								 /* Priority Result Codes */
								 String priorityResultCodes = (String)handlerObject.get(PRIORITY_RESULT_CODES);
								 proxyCommunicationHandlerData.setStrPriorityResultCodes(priorityResultCodes);	
								 
								 if(protocol.equals("DIAMETER")){
									 /* Result Code When No Entry Selected */
									 long resultCodeWhenNoEntrySelected = Long.parseLong((String) handlerObject.get(RESULT_CODE_WHEN_NO_ENTRY_SELECTED));
									 proxyCommunicationHandlerData.setResultCodeOnNoEntrySelected(resultCodeWhenNoEntrySelected);
									 
									 if(ProxyMode.MULTIPLE.toString().equals(handlerObject.get(PROXY_MODE).toString())){
										 proxyCommunicationHandlerData.setProxyMode(ProxyMode.MULTIPLE);
									 }else{
										 proxyCommunicationHandlerData.setProxyMode(ProxyMode.SINGLE);
									 }
								 }
								 
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
										 String translationMapping  = (String)entryJsonObj.get(TRANSLATION_MAPPING);
										 data.setTranslationMapping(translationMapping);
										 
										 /* Script */
										 String script = (String)entryJsonObj.get(SCRIPT);
										 data.setScript(script);
										 
										 /*Acceptable result code */
										 String acceptableResultCodes = (String)entryJsonObj.get(ACCEPTABLE_RESULT_CODES);
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
										 String peerGroupId= (String)entryJsonObj.get(PEER_GROUP_ID);
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
								String handlerEnabled = (String)handlerObject.get("isHandlerEnabled");
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
										 String primaryDriverId  = (String)entryJsonObj.get(PRIMARY_DRIVER_ID);
										
										 if (primaryDriverId != null && primaryDriverId.isEmpty() == false) {
											 PrimaryDriverDetail driverDetail = new PrimaryDriverDetail();
											 driverDetail.setDriverInstanceId(primaryDriverId);
											 driverDetail.setWeightage(1);
											 
											 primaryDriverDetailsList.add(driverDetail);
										 }
										 diameterProfileDriverDetails.setPrimaryDriverGroup(primaryDriverDetailsList);
										 
										 /* Secondary Driver */
										 List<SecondaryAndCacheDriverDetail> secondaryDriverGroupList = new ArrayList<SecondaryAndCacheDriverDetail>();
										 String secondaryDriverId  = (String)entryJsonObj.get(SECONDARY_DRIVER_ID);
										 
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
									 String handlerType = (String)handlerObject.get(HANDLER_TYPE);
									 
									 if( handlerType != null && handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.CDR_HANDLER)){
										 
										Logger.getLogger().info(TGPPAAAPOLICY, "Fetching CDR handler data ");
											
										DiameterCDRGenerationHandlerData diameterCDRGenerationHandlerData = new DiameterCDRGenerationHandlerData();
										
										/* Handler Enabled/Disabled */
										String handlerEnabled = (String)handlerObject.get("isHandlerEnabled");
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
												 String primaryDriverId  = (String)entryJsonObj.get(PRIMARY_DRIVER_ID);
												
												 if (primaryDriverId != null && primaryDriverId.isEmpty() == false) {
													 PrimaryDriverDetail driverDetail = new PrimaryDriverDetail();
													 driverDetail.setDriverInstanceId(primaryDriverId);
													 driverDetail.setWeightage(1);
													 
													 primaryDriverDetailsList.add(driverDetail);
												 }
												 diameterProfileDriverDetails.setPrimaryDriverGroup(primaryDriverDetailsList);
													 
												 /* Secondary Driver */
												 List<SecondaryAndCacheDriverDetail> secondaryDriverGroupList = new ArrayList<SecondaryAndCacheDriverDetail>();
												 String secondaryDriverId  = (String)entryJsonObj.get(SECONDARY_DRIVER_ID);
												 
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
									 } else if (handlerType.equalsIgnoreCase(TGPPAAAPolicyConstant.PLUGIN_HANDLER)){
										 
										 Logger.getLogger().info(TGPPAAAPOLICY, "Fetching Plugin handler data ");
										 DiameterPluginHandlerData pluginHandlerData = new DiameterPluginHandlerData();
										 
										 /* Handler Enabled/Disabled */
										 String handlerEnabled = (String)handlerObject.get("isHandlerEnabled");
										 pluginHandlerData.setEnabled(handlerEnabled);
										
										 /* Handler Name */
										 String handlerName = (String)handlerObject.get(HANDLER_NAME);
										 pluginHandlerData.setHandlerName(handlerName);
										 
										 List<PluginEntryData> pluginEntryDataList = new ArrayList<PluginEntryData>();
										 JSONArray entries = (JSONArray) handlerObject.get(PLUGIN_ENTRIES);
										 
										 if( entries != null && entries.size() > 0 ){
											 
											 for(Object  entryObj : entries){
											
												 PluginEntryData pluginEntryData = new PluginEntryData();
												 
												 JSONObject entryJsonObj = (JSONObject) entryObj;
												 
												 /* Ruleset */
												 String ruleset  = (String)entryJsonObj.get(RULESET);
												 pluginEntryData.setRuleset(ruleset);
												 
												 /* Plugin Name */
												 String pluginName  = (String)entryJsonObj.get(PLUGIN_NAME);
												 pluginEntryData.setPluginName(pluginName);
												 
												 /* Plugin Argument */
												 String pluginArgument  = (String)entryJsonObj.get(PLUGIN_ARGUMENT);
												 pluginEntryData.setPluginArgument(pluginArgument);
												 
												 /* onResponse */
												 String onResponse = (String)entryJsonObj.get(ON_RESPONSE);
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
		    
		    Logger.getLogger().info(TGPPAAAPOLICY, "******************************* XML Data*******************************");
		    Logger.getLogger().info(TGPPAAAPOLICY, xmlDatas);
		    Logger.getLogger().info(TGPPAAAPOLICY, "***********************************************************************");
		    Logger.getLogger().info(TGPPAAAPOLICY, "XML Length : "+ xmlDatas.length());
		    Logger.getLogger().info(TGPPAAAPOLICY, "***********************************************************************");
			   
			return xmlDatas.getBytes();
		}catch (Exception e) {
			throw e;
		}
	}
}
