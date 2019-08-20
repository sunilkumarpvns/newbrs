package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusEsiGroupConfigurationData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.*;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.radius.radiusesigroup.RadiusEsiType;
import com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.CreateRadiusServicePolicyForm;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddRadiusServicePolicyAcctAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "addAccountingHandler";
	private static String ACTION_ALIAS = ConfigConstant.CREATE_RADIUS_SERVICE_POLICY;

	private static final String MODULE = "AddRadiusServicePolicyAuthAction";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try{
			
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
			CreateRadiusServicePolicyForm createRadiusServicePolicyForm=(CreateRadiusServicePolicyForm)form;
			Logger.logDebug(MODULE, "createRadiusServicePolicyForm     : "+createRadiusServicePolicyForm);
			
			String authenticationHandlerJson=createRadiusServicePolicyForm.getAuthHandlerJson();
			String profileLookupDriverHanlderJson=createRadiusServicePolicyForm.getProfileLookupDriverJson();
			String authorizationHandlerJson=createRadiusServicePolicyForm.getAuthorizationHandlerJson();
			String concurrencyHandlerJson=createRadiusServicePolicyForm.getConcurrencyHandlerJson();
			String cdrGenerationJson=createRadiusServicePolicyForm.getCdrGenerationJson();
			String pluginHandlerJson=createRadiusServicePolicyForm.getPluginHandlerJson();
			String coaDmGenerationJson=createRadiusServicePolicyForm.getCoaDMGenerationJson();
			String proxyCommunicationJson=createRadiusServicePolicyForm.getProxyCommunicationJson();
			String broacastCommunicationJson=createRadiusServicePolicyForm.getBroadcastCommunicationJson();
			String authPrePluginJson  = createRadiusServicePolicyForm.getAuthPrePluginJson();
			String authPostPluginJson = createRadiusServicePolicyForm.getAuthPostPluginJson();
			
			List<AuthenticationHandler> authenticationHandlerList=new ArrayList<AuthenticationHandler>();
			
			if(authenticationHandlerJson != null && authenticationHandlerJson.length() > 0){
				 JSONArray authenticationHandlerArray = JSONArray.fromObject(authenticationHandlerJson);
				 for(Object  obj: authenticationHandlerArray){
					 AuthenticationHandler authenticationHandler = (AuthenticationHandler) JSONObject.toBean((JSONObject) obj, AuthenticationHandler.class);
					 authenticationHandlerList.add(authenticationHandler);
				 }
			}


			createRadiusServicePolicyForm.setAuthHandlerList(authenticationHandlerList);
			
			List<ProfileLookupDriver> profileLookupDrivers=new ArrayList<ProfileLookupDriver>();
			
			if(profileLookupDriverHanlderJson != null && profileLookupDriverHanlderJson.length() > 0){
				 JSONArray profileDriverArray = JSONArray.fromObject(profileLookupDriverHanlderJson);
				 for(Object  obj: profileDriverArray){
					 ProfileLookupDriver profileLookupDriver = (ProfileLookupDriver) JSONObject.toBean((JSONObject) obj, ProfileLookupDriver.class);
					 profileLookupDrivers.add(profileLookupDriver);
				 }
			}
			
			createRadiusServicePolicyForm.setProfileLookupList(profileLookupDrivers);
			
			List<AuthorizationHandler> authorizationHandlers=new ArrayList<AuthorizationHandler>();
			
			if(authorizationHandlerJson != null && authorizationHandlerJson.length() > 0){
				 JSONArray authorizationHandlerArray = JSONArray.fromObject(authorizationHandlerJson);
				 for(Object  obj: authorizationHandlerArray){
					 AuthorizationHandler authorizationHandler = (AuthorizationHandler) JSONObject.toBean((JSONObject) obj, AuthorizationHandler.class);
					 authorizationHandlers.add(authorizationHandler);
				 }
			}
			
			createRadiusServicePolicyForm.setAuthorizationList(authorizationHandlers);
			
			List<ConcurrencyHandler> concurrencyHandlers=new ArrayList<ConcurrencyHandler>();
			
			if(concurrencyHandlerJson != null && concurrencyHandlerJson.length() > 0){
				 JSONArray concurrencyHandlerArray = JSONArray.fromObject(concurrencyHandlerJson);
				 for(Object  obj: concurrencyHandlerArray){
					 ConcurrencyHandler concurrencyHandler = (ConcurrencyHandler) JSONObject.toBean((JSONObject) obj, ConcurrencyHandler.class);
					 concurrencyHandlers.add(concurrencyHandler);
				 }
			}
			
			createRadiusServicePolicyForm.setConcurrencyHandlerList(concurrencyHandlers);
			
			List<CDRGeneration> cdrGenerations = new ArrayList<CDRGeneration>();
			
			if(cdrGenerationJson != null && cdrGenerationJson.length() > 0){
				 JSONArray cdrGenerationArray = JSONArray.fromObject(cdrGenerationJson);
				 for(Object  obj: cdrGenerationArray){
					 Map<String,Class> configObj = new HashMap<String, Class>();
					 configObj.put("cdrGenerationDetailsList", CDRGenerationDetails.class);
					 CDRGeneration cdrGeneration = (CDRGeneration) JSONObject.toBean((JSONObject) obj, CDRGeneration.class,configObj);
					 cdrGenerations.add(cdrGeneration);
				 }
			}
			
			createRadiusServicePolicyForm.setAuthCDRGenerationList(cdrGenerations);
			
			List<PluginHandler> pluginHandlers = new ArrayList<PluginHandler>();
			
			if(pluginHandlerJson != null && pluginHandlerJson.length() > 0){
				 JSONArray pluginHandlerArray = JSONArray.fromObject(pluginHandlerJson);
				 for(Object  obj: pluginHandlerArray){
					 Map<String,Class> configObj = new HashMap<String, Class>();
					 
					 configObj.put("lstPluginDetails", PluginDetails.class);
				
					 PluginHandler pluginHandler = (PluginHandler) JSONObject.toBean((JSONObject) obj, PluginHandler.class,configObj);
					 pluginHandlers.add(pluginHandler);
				 }
			}
			
			createRadiusServicePolicyForm.setAuthPluginHandlerList(pluginHandlers);
			
			List<COADMGeneration> coaDmGenerations = new ArrayList<COADMGeneration>();
			
			if(coaDmGenerationJson != null && coaDmGenerationJson.length() > 0){
				 JSONArray coaDMGenerationArray = JSONArray.fromObject(coaDmGenerationJson);
				 for(Object  obj: coaDMGenerationArray){
					 Map<String,Class> configObj = new HashMap<String, Class>();
					 configObj.put("coaDMGenerationDetailList", COADMGenerationDetails.class);
					 COADMGeneration coaDmGeneration = (COADMGeneration) JSONObject.toBean((JSONObject) obj, COADMGeneration.class,configObj);
					 coaDmGenerations.add(coaDmGeneration);
				 }
			}
			
			createRadiusServicePolicyForm.setAuthCOADMGenList(coaDmGenerations);
			
			List<ProxyCommunication> proxyCommunications = new ArrayList<ProxyCommunication>();
			
			if(proxyCommunicationJson != null && proxyCommunicationJson.length() > 0){
				 JSONArray proxyCommunicationArray = JSONArray.fromObject(proxyCommunicationJson);
				 for(Object  obj: proxyCommunicationArray){
					 Map<String,Class> configObj = new HashMap<String, Class>();
					 
					 configObj.put("proxyCommunicationList", ProxyCommunicationData.class);
					 configObj.put("esiListData", ESIServerData.class);
					
					 ProxyCommunication proxyCommunication = (ProxyCommunication) JSONObject.toBean((JSONObject) obj, ProxyCommunication.class,configObj);
					 proxyCommunications.add(proxyCommunication);
				 }
			}
			
			createRadiusServicePolicyForm.setAuthProxyCommunicationList(proxyCommunications);
			
			List<BroadcastCommunication> broadcastCommunications = new ArrayList<BroadcastCommunication>();
			
			if(broacastCommunicationJson != null && broacastCommunicationJson.length() > 0){
				 JSONArray broadcastCommunicationArray = JSONArray.fromObject(broacastCommunicationJson);
				 for(Object  obj: broadcastCommunicationArray){
					
					 Map<String,Class> configObj = new HashMap<String, Class>();
					 
					 configObj.put("broadcastCommunicationList", BroadcastCommunicationData.class);
					 configObj.put("esiListData", ESIServerData.class);
					
					 BroadcastCommunication broadcastCommunication = (BroadcastCommunication) JSONObject.toBean((JSONObject) obj, BroadcastCommunication.class,configObj);
					 broadcastCommunications.add(broadcastCommunication);
				 }
			}
			createRadiusServicePolicyForm.setAuthBroadcastCommunicationList(broadcastCommunications);

			/* Convert Pre plugin list to relavant POJO */
			/* Auth Pre Plugin */
			List<PolicyPluginData> authPrePluginList = new ArrayList<PolicyPluginData>();
			if(authPrePluginJson != null && authPrePluginJson.length() > 0){
				 JSONArray authPrePluginArray = JSONArray.fromObject(authPrePluginJson);
				 for(Object  obj: authPrePluginArray){
					
					 PolicyPluginData policyPluginData = (PolicyPluginData) JSONObject.toBean((JSONObject) obj, PolicyPluginData.class);
					 authPrePluginList.add(policyPluginData);
				 }
			}
			createRadiusServicePolicyForm.setAuthPrePluginData(authPrePluginList);
			
			
			/* Auth Post Plugin */
			List<PolicyPluginData> authPostPluginList = new ArrayList<PolicyPluginData>();
			if(authPostPluginJson != null && authPostPluginJson.length() > 0){
				 JSONArray authPostPluginArray = JSONArray.fromObject(authPostPluginJson);
				 for(Object  obj: authPostPluginArray){
					
					 PolicyPluginData policyPluginData = (PolicyPluginData) JSONObject.toBean((JSONObject) obj, PolicyPluginData.class);
					 authPostPluginList.add(policyPluginData);
				 }
			}
			createRadiusServicePolicyForm.setAuthPostPluginData(authPostPluginList);
			
			DriverBLManager driverBlManager = new DriverBLManager();
			List<DriverInstanceData> driverList = driverBlManager.getDriverInstanceList(ServiceTypeConstants.AUTHENTICATION_SERVICE);
			createRadiusServicePolicyForm.setDriversList(driverList);
			
			List<DriverInstanceData> cacheableDriverInstDataList = driverBlManager.getCacheableDriverData();
			createRadiusServicePolicyForm.setCacheableDriverList(cacheableDriverInstDataList);
			
			SessionManagerBLManager sessionManagerBLManager=new SessionManagerBLManager();
			List<ISessionManagerInstanceData> sessionManagerList=sessionManagerBLManager.getSessionManagerInstanceList();
			
			/*translationMappingBLManager*/
			TranslationMappingConfBLManager translationMappingConfBLManager=new TranslationMappingConfBLManager();
			List<TranslationMappingConfData> translationMappingConfDataList=translationMappingConfBLManager.getRadiusToRadiusTranslationMapping();
			
			CopyPacketTransMapConfBLManager copyPacketMappingConfBLManager = new CopyPacketTransMapConfBLManager();
			List<CopyPacketTranslationConfData> copyPacketMappingConfDataList = copyPacketMappingConfBLManager.getCopyPacketTransMapConfigList(TranslationMappingConfigConstants.RADIUS,TranslationMappingConfigConstants.RADIUS);
			
			ExternalSystemInterfaceBLManager externalSystemBLmanager=new ExternalSystemInterfaceBLManager();
			List<ExternalSystemInterfaceInstanceData> externalSystemInstanceList = new ArrayList<ExternalSystemInterfaceInstanceData>();
			List<ExternalSystemInterfaceInstanceData> nasESIList=new ArrayList<ExternalSystemInterfaceInstanceData>();
				
			externalSystemInstanceList = externalSystemBLmanager.getAllExternalSystemInstanceDataList();
			
			createRadiusServicePolicyForm.setAuthBroadcastServerList(externalSystemInstanceList);	
			
			nasESIList = externalSystemBLmanager.getExternalSystemInstanceDataList(ExternalSystemConstants.NAS);
			createRadiusServicePolicyForm.setDynaAuthRelDataList(nasESIList);
			
			/* Get Accounting flow ESI */
			List<ExternalSystemInterfaceInstanceData> acctESIList=new ArrayList<ExternalSystemInterfaceInstanceData>();
			acctESIList = externalSystemBLmanager.getAcctFlowExternalSystemInstanceDataList(ExternalSystemConstants.AUTH_PROXY);
			createRadiusServicePolicyForm.setAcctESIList(acctESIList);
			
			createRadiusServicePolicyForm.setSessionManagerInstanceDataList(sessionManagerList);
			createRadiusServicePolicyForm.setTranslationMappingConfDataList(translationMappingConfDataList);
			createRadiusServicePolicyForm.setCopyPacketMappingConfDataList(copyPacketMappingConfDataList);
			
			/* Driver Script and External Radius Script */
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
			List<ScriptInstanceData> externalScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.EXTERNAL_RADIUS_SCRIPT);
				
			createRadiusServicePolicyForm.setDriverScriptList(driverScriptList);
			createRadiusServicePolicyForm.setExternalScriptList(externalScriptList);
			
			request.getSession().setAttribute("createRadiusServicePolicyForm", createRadiusServicePolicyForm);
			
			/* Redirect to save action instead of Accounting JSP */
			if(createRadiusServicePolicyForm.isAccounting() == false){
				ActionRedirect redirect = new ActionRedirect(mapping.findForward("addRadiusServiceHandler"));
				return redirect;
			}
			
			PluginBLManager pluginBLManager = new PluginBLManager();
			List<PluginInstData> postPluginList = pluginBLManager.getAcctPluginList();
			request.setAttribute("postPluginList", postPluginList);
			
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
			
			List<DriverInstanceData> listOfAcctDriver = driverBlManager.getDriverInstanceList(ServiceTypeConstants.ACCOUNTING_SERVICE);
			List<DriverTypeData> driverTypeList=driverBlManager.getDriverTypeList(ServiceTypeConstants.ACCOUNTING_SERVICE);

			setRadiusEsiGroupData(createRadiusServicePolicyForm);
			request.setAttribute("driverInstanceIds", driverInstanceIds);
			request.setAttribute("driverInstanceNames", driverInstanceNames);
			request.setAttribute("listOfDriver", driverList);
			request.setAttribute("listOfAcctDriver", listOfAcctDriver);
			request.setAttribute("translationMappingConfDataList", translationMappingConfDataList);
			request.setAttribute("copyPacketMappingConfDataList", copyPacketMappingConfDataList);
			request.setAttribute("driverTypeList", driverTypeList);
			
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

	//This function is used to add Correlated Radius ESI data in createRadiusServicePolicyForm
	private void setRadiusEsiGroupData(CreateRadiusServicePolicyForm form) throws DataManagerException, JAXBException {
		RadiusESIGroupBLManager esiBlManager = new RadiusESIGroupBLManager();
		List<RadiusESIGroupData> radiusESIGroupDataList = esiBlManager.getRadiusESIGroupDataList();
		List<String> radiusEsiGroupNames = new ArrayList<>();

		if(Collectionz.isNullOrEmpty(radiusESIGroupDataList) == false){
			for (RadiusESIGroupData esiData:radiusESIGroupDataList) {

				String xmlDatas = new String(esiData.getEsiGroupDataXml());
				StringReader stringReader =new StringReader(xmlDatas.trim());

				RadiusEsiGroupConfigurationData esiConfigurationData = ConfigUtil.deserialize(stringReader, RadiusEsiGroupConfigurationData.class);
				if(RadiusEsiType.AUTH.name.equalsIgnoreCase(esiConfigurationData.getEsiType()) == false){
					radiusEsiGroupNames.add(esiData.getName());
				}
			}
		}
		form.setRadiusEsiGroupNames(radiusEsiGroupNames);
	}
}
