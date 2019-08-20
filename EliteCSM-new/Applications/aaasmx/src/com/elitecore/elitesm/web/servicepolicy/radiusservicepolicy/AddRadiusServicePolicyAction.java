package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.CreateRadiusServicePolicyForm;

public class AddRadiusServicePolicyAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static String ACTION_ALIAS = ConfigConstant.CREATE_RADIUS_SERVICE_POLICY;

	private static final String MODULE = "AddRadiusServicePolicyAuthAction";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try{
			
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
			CreateRadiusServicePolicyForm createRadiusServicePolicyForm=(CreateRadiusServicePolicyForm)form;
			Logger.logDebug(MODULE, "createRadiusServicePolicyForm     : "+createRadiusServicePolicyForm);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			ServicePolicyBLManager servicePolicyBLManager = new ServicePolicyBLManager();
			
			String acctCDRGenerationJson = createRadiusServicePolicyForm.getAcctCDRGenerationJson();
			String acctCOADmGenerationJson =createRadiusServicePolicyForm.getAcctCOADMGenerationJson();
			String acctPluginHandlerJson = createRadiusServicePolicyForm.getAcctPluginHandlerJson();
			String acctProxyCommunicationJson = createRadiusServicePolicyForm.getAcctProxyCommunicationJson();
			String acctbroadcastCommunicationJson = createRadiusServicePolicyForm.getAcctBroadcastCommunicationJson();
			String acctProfileLookupJson = createRadiusServicePolicyForm.getAcctHandlerJson();
			String concurrencyIMDGHandlerJson = createRadiusServicePolicyForm.getConcurrencyIMDGHandlerJson();
			String statefulProxySequentialJson = createRadiusServicePolicyForm.getStatefulProxySequentialHandlerJson();
			String statefulProxySequentialAcctJson = createRadiusServicePolicyForm.getStatefulProxySequentialHandlerAcctJson();
			String statefulProxyBroadcastAuthJson = createRadiusServicePolicyForm.getStatefulProxyBroadcastHandlerAuthJson();
            String statefulProxyBroadcastAcctJson = createRadiusServicePolicyForm.getStatefulProxyBroadcastHandlerAcctJson();
			
			String acctPrePluginJson  = createRadiusServicePolicyForm.getAcctPrePluginJson();
			String acctPostPluginJson = createRadiusServicePolicyForm.getAcctPostPluginJson();
			
			List<ProfileLookupDriver> profileLookupDrivers=new ArrayList<ProfileLookupDriver>();
			List<CDRGeneration> cdrGenerations = new ArrayList<CDRGeneration>();
			List<PluginHandler> pluginHandlers = new ArrayList<PluginHandler>();
			List<COADMGeneration> coaDmGenerationsList = new ArrayList<COADMGeneration>();
			List<ProxyCommunication> proxyCommunications = new ArrayList<ProxyCommunication>();
			List<BroadcastCommunication> broadcastCommunications = new ArrayList<BroadcastCommunication>();
			List<ConcurrencyIMDGHandler> concurrencyIMDGHandlers = new ArrayList<>();
			List<StatefulProxySequentialHandler> statefulProxySequentialHandlers = new ArrayList<>();
			List<StatefulProxySequentialHandler> statefulProxySequentialAcctHandlers = new ArrayList<>();
            List<StatefulProxyBroadcastHandler> statefulProxyBroadcastAuthHandlers = new ArrayList<>();
            List<StatefulProxyBroadcastHandler> statefulProxyBroadcastAcctHandlers = new ArrayList<>();

			if(acctProfileLookupJson != null && acctProfileLookupJson.length() > 0){
				 JSONArray profileDriverArray = JSONArray.fromObject(acctProfileLookupJson);
				 for(Object  obj: profileDriverArray){
					 ProfileLookupDriver profileLookupDriver = (ProfileLookupDriver) JSONObject.toBean((JSONObject) obj, ProfileLookupDriver.class);
					 profileLookupDrivers.add(profileLookupDriver);
				 }
			}
			
			if(acctCDRGenerationJson != null && acctCDRGenerationJson.length() > 0){
				 JSONArray cdrGenerationArray = JSONArray.fromObject(acctCDRGenerationJson);
				 for(Object  obj: cdrGenerationArray){
					 Map<String,Class> configObj = new HashMap<String, Class>();
					 configObj.put("cdrGenerationDetailsList", CDRGenerationDetails.class);
					 CDRGeneration cdrGeneration = (CDRGeneration) JSONObject.toBean((JSONObject) obj, CDRGeneration.class,configObj);
					 cdrGenerations.add(cdrGeneration);
				 }
			}
			
			if(acctPluginHandlerJson != null && acctPluginHandlerJson.length() > 0){
				 JSONArray pluginHandlerArray = JSONArray.fromObject(acctPluginHandlerJson);
				 for(Object  obj: pluginHandlerArray){
					 Map<String,Class> configObj = new HashMap<String, Class>();
					 configObj.put("lstPluginDetails", PluginDetails.class);
					 PluginHandler pluginHandler = (PluginHandler) JSONObject.toBean((JSONObject) obj, PluginHandler.class,configObj);
					 pluginHandlers.add(pluginHandler);
				 }
			}
			
			if(acctCOADmGenerationJson != null && acctCOADmGenerationJson.length() > 0){
				 JSONArray coaDMGenerationArray = JSONArray.fromObject(acctCOADmGenerationJson);
				 for(Object  obj: coaDMGenerationArray){
					 Map<String,Class> configObj = new HashMap<String, Class>();
					 configObj.put("coaDMGenerationDetailList", COADMGenerationDetails.class);
					 COADMGeneration coaDmGeneration = (COADMGeneration) JSONObject.toBean((JSONObject) obj, COADMGeneration.class,configObj);
					 coaDmGenerationsList.add(coaDmGeneration);
				 }
			}
			
			if(acctProxyCommunicationJson != null && acctProxyCommunicationJson.length() > 0){
				 JSONArray proxyCommunicationArray = JSONArray.fromObject(acctProxyCommunicationJson);
				 for(Object  obj: proxyCommunicationArray){
					 Map<String,Class> configObj = new HashMap<String, Class>();
					 configObj.put("proxyCommunicationList", ProxyCommunicationData.class);
					 configObj.put("esiListData", ESIServerData.class);
					 ProxyCommunication proxyCommunication = (ProxyCommunication) JSONObject.toBean((JSONObject) obj, ProxyCommunication.class,configObj);
					 proxyCommunications.add(proxyCommunication);
				 }
			}
			
			if(acctbroadcastCommunicationJson != null && acctbroadcastCommunicationJson.length() > 0){
				 JSONArray broadcastCommunicationArray = JSONArray.fromObject(acctbroadcastCommunicationJson);
				 for(Object  obj: broadcastCommunicationArray){
					 Map<String,Class> configObj = new HashMap<String, Class>();
					 configObj.put("broadcastCommunicationList", BroadcastCommunicationData.class);
					 configObj.put("esiListData", ESIServerData.class);
					 BroadcastCommunication broadcastCommunication = (BroadcastCommunication) JSONObject.toBean((JSONObject) obj, BroadcastCommunication.class,configObj);
					 broadcastCommunications.add(broadcastCommunication);
				 }
			}

			if(Strings.isNullOrEmpty(concurrencyIMDGHandlerJson) == false){
				JSONArray profileDriverArray = JSONArray.fromObject(concurrencyIMDGHandlerJson);
				for(Object  obj: profileDriverArray){
					ConcurrencyIMDGHandler concurrencyIMDGHandler = (ConcurrencyIMDGHandler) JSONObject.toBean((JSONObject) obj, ConcurrencyIMDGHandler.class);
					concurrencyIMDGHandlers.add(concurrencyIMDGHandler);
				}
			}

			if(Strings.isNullOrEmpty(statefulProxySequentialJson) == false){
				JSONArray handlerDataArray = JSONArray.fromObject(statefulProxySequentialJson);
				for (Object obj: handlerDataArray){
					Map<String,Class> configObj = new HashMap<String, Class>();
					configObj.put("sequentialHandlerEntryData", StatefulProHandlerData.class);
					StatefulProxySequentialHandler handler = (StatefulProxySequentialHandler) JSONObject.toBean((JSONObject) obj,StatefulProxySequentialHandler.class,configObj);
					statefulProxySequentialHandlers.add(handler);
				}
 			}

			if(Strings.isNullOrEmpty(statefulProxySequentialAcctJson) == false){
				JSONArray handlerDataArray = JSONArray.fromObject(statefulProxySequentialAcctJson);
				for (Object obj: handlerDataArray){
					Map<String,Class> configObj = new HashMap<String, Class>();
					configObj.put("sequentialHandlerEntryData", StatefulProHandlerData.class);
					StatefulProxySequentialHandler handler = (StatefulProxySequentialHandler) JSONObject.toBean((JSONObject) obj,StatefulProxySequentialHandler.class,configObj);
					statefulProxySequentialAcctHandlers.add(handler);
				}
			}

            if(Strings.isNullOrEmpty(statefulProxyBroadcastAuthJson) == false){
                JSONArray handlerDataArray = JSONArray.fromObject(statefulProxyBroadcastAuthJson);
                for (Object obj: handlerDataArray){
                    Map<String,Class> configObj = new HashMap<String, Class>();
                    configObj.put("broadcastHandlerEntryData", StatefulProHandlerData.class);
                    StatefulProxyBroadcastHandler handler = (StatefulProxyBroadcastHandler) JSONObject.toBean((JSONObject) obj,StatefulProxyBroadcastHandler.class,configObj);
                    statefulProxyBroadcastAuthHandlers.add(handler);
                }
            }

            if(Strings.isNullOrEmpty(statefulProxyBroadcastAcctJson) == false){
                JSONArray handlerDataArray = JSONArray.fromObject(statefulProxyBroadcastAcctJson);
                for (Object obj: handlerDataArray){
                    Map<String,Class> configObj = new HashMap<String, Class>();
                    configObj.put("broadcastHandlerEntryData", StatefulProHandlerData.class);
                    StatefulProxyBroadcastHandler handler = (StatefulProxyBroadcastHandler) JSONObject.toBean((JSONObject) obj,StatefulProxyBroadcastHandler.class,configObj);
                    statefulProxyBroadcastAcctHandlers.add(handler);
                }
            }

			/* Convert Pre plugin list to relavant POJO */
			/* Acct Pre Plugin */
			List<PolicyPluginData> acctPrePluginList = new ArrayList<PolicyPluginData>();
			if(acctPrePluginJson != null && acctPrePluginJson.length() > 0){
				 JSONArray acctPrePluginArray = JSONArray.fromObject(acctPrePluginJson);
				 for(Object  obj: acctPrePluginArray){
					
					 PolicyPluginData policyPluginData = (PolicyPluginData) JSONObject.toBean((JSONObject) obj, PolicyPluginData.class);
					 acctPrePluginList.add(policyPluginData);
				 }
			}
			createRadiusServicePolicyForm.setAcctPrePluginData(acctPrePluginList);
			
			
			/* Auth Post Plugin */
			List<PolicyPluginData> acctPostPluginList = new ArrayList<PolicyPluginData>();
			if(acctPostPluginJson != null && acctPostPluginJson.length() > 0){
				 JSONArray acctPostPluginArray = JSONArray.fromObject(acctPostPluginJson);
				 for(Object  obj: acctPostPluginArray){
					
					 PolicyPluginData policyPluginData = (PolicyPluginData) JSONObject.toBean((JSONObject) obj, PolicyPluginData.class);
					 acctPostPluginList.add(policyPluginData);
				 }
			}
			createRadiusServicePolicyForm.setAcctPostPluginData(acctPostPluginList);
			
			createRadiusServicePolicyForm.setAcctPluginHandlerList(pluginHandlers);
			createRadiusServicePolicyForm.setAcctCDRGenerationList(cdrGenerations);
			createRadiusServicePolicyForm.setAcctProfileLookupDriver(profileLookupDrivers);
			createRadiusServicePolicyForm.setAcctBroadcastCommunicationList(broadcastCommunications);
			createRadiusServicePolicyForm.setAcctCOADMGenList(coaDmGenerationsList);
			createRadiusServicePolicyForm.setAcctProxyCommunicationList(proxyCommunications);
			createRadiusServicePolicyForm.setConcurrencyIMDGHandlerList(concurrencyIMDGHandlers);
			createRadiusServicePolicyForm.setStatefulProxySequentialHandlerList(statefulProxySequentialHandlers);
			createRadiusServicePolicyForm.setStatefulProxySequentialHandlerAcctList(statefulProxySequentialAcctHandlers);
			createRadiusServicePolicyForm.setStatefulProxyBroadcastHandlersList(statefulProxyBroadcastAuthHandlers);
			createRadiusServicePolicyForm.setStatefulProxyBroadcastAcctHandlersList(statefulProxyBroadcastAcctHandlers);
			
			RadiusServicePolicyData radiusServicePolicyData = convertFormToBean(createRadiusServicePolicyForm);
			
		    servicePolicyBLManager.createRadiusServicePolicy(radiusServicePolicyData, staffData);
		    
			request.setAttribute("responseUrl", "/searchRadiusServicePolicy");
			ActionMessage message = new ActionMessage("radiusservicepolicy.create.success"); 
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveMessages(request, messages);
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
	
	private RadiusServicePolicyData convertFormToBean(CreateRadiusServicePolicyForm createRadiusServicePolicyForm) {
		RadiusServicePolicyData radiusServicePolicyData =new RadiusServicePolicyData();
		radiusServicePolicyData.setName(createRadiusServicePolicyForm.getName());
		radiusServicePolicyData.setDescription(createRadiusServicePolicyForm.getDescription());
		if(BaseConstant.ACTIVE_STATUS.equalsIgnoreCase(createRadiusServicePolicyForm.getStatus())){
			radiusServicePolicyData.setStatus(createRadiusServicePolicyForm.getStatus());
		}else {
			radiusServicePolicyData.setStatus(BaseConstant.INACTIVE_STATUS);
		}
		
		SupportedMessages supportedMessages = new SupportedMessages();
		supportedMessages.setAccountingMessageEnabled(String.valueOf(createRadiusServicePolicyForm.isAccounting()));
		supportedMessages.setAuthenticationMessageEnabled(String.valueOf(createRadiusServicePolicyForm.isAuthentication()));
		
		radiusServicePolicyData.setSupportedMessages(supportedMessages);
		radiusServicePolicyData.setAccountingRuleset(createRadiusServicePolicyForm.getRuleSetAcct());
		radiusServicePolicyData.setAuthenticationRuleset(createRadiusServicePolicyForm.getRuleSetAuth());
		
		radiusServicePolicyData.setValidatePacket(String.valueOf(createRadiusServicePolicyForm.getValidatePacket()));
		radiusServicePolicyData.setAuthResponseAttributes(createRadiusServicePolicyForm.getAuthResponseAttributes());
		radiusServicePolicyData.setAcctResponseAttributes(createRadiusServicePolicyForm.getAcctResponseAttributes());
		
		if(Strings.isNullOrBlank(createRadiusServicePolicyForm.getSessionManagerId()) == false && 
				"0".equalsIgnoreCase(createRadiusServicePolicyForm.getSessionManagerId()) == false){
			radiusServicePolicyData.setSessionManagerId(createRadiusServicePolicyForm.getSessionManagerId());
		}else{
			radiusServicePolicyData.setSessionManagerId(null);
		}
		
		radiusServicePolicyData.setHotlinePolicy(createRadiusServicePolicyForm.getHotlinePolicy());
		radiusServicePolicyData.setUserIdentity(createRadiusServicePolicyForm.getUserIdentity());
		
		if(createRadiusServicePolicyForm.isAuthentication()){
			if(AuthResponseBehaviors.REJECT.name().equals(createRadiusServicePolicyForm.getAuthResponseBehavior())){
				radiusServicePolicyData.setDefaultAuthResponseBehavior(AuthResponseBehaviors.REJECT.name());
			}else if(AuthResponseBehaviors.DROP.name().equals(createRadiusServicePolicyForm.getAuthResponseBehavior())){
				radiusServicePolicyData.setDefaultAuthResponseBehavior(AuthResponseBehaviors.DROP.name());
			}else if(AuthResponseBehaviors.HOTLINE.name().equals(createRadiusServicePolicyForm.getAuthResponseBehavior())){
				radiusServicePolicyData.setDefaultAuthResponseBehavior(AuthResponseBehaviors.HOTLINE.name());
			}
		}
		
		if(createRadiusServicePolicyForm.isAccounting()){
			if(AcctResponseBehaviors.DROP.name().equals(createRadiusServicePolicyForm.getAcctResponseBehavior())){
				radiusServicePolicyData.setDefaultAcctResponseBehavior(AcctResponseBehaviors.DROP.name());
			}else if(AcctResponseBehaviors.RESPONSE.name().equals(createRadiusServicePolicyForm.getAcctResponseBehavior())){
				radiusServicePolicyData.setDefaultAcctResponseBehavior(AcctResponseBehaviors.RESPONSE.name());
			}
		}
		
		radiusServicePolicyData.setAccountingRuleset(createRadiusServicePolicyForm.getRuleSetAcct());
		radiusServicePolicyData.setAuthenticationRuleset(createRadiusServicePolicyForm.getRuleSetAuth());
		
		ChargeableUserIdentityConfiguration cuiAttributes = new ChargeableUserIdentityConfiguration();
		cuiAttributes.setAccountingCuiAttribute(createRadiusServicePolicyForm.getAcctAttributes());
		cuiAttributes.setAuthenticationCuiAttribute(createRadiusServicePolicyForm.getAuthAttributes());
		cuiAttributes.setCui(createRadiusServicePolicyForm.getCui());
		cuiAttributes.setExpression(createRadiusServicePolicyForm.getAdvancedCuiExpression());
		
		radiusServicePolicyData.setCuiConfiguration(cuiAttributes);
		/*************************************************************************************/
		/*							AUTHENTICATION POLICY									 */
		/*************************************************************************************/
		
		if(createRadiusServicePolicyForm.isAuthentication())
		{
			AuthenticationPolicyData authenticationPolicyData = new AuthenticationPolicyData();
			Map<Integer, Object> authenticationHandlersMap=new HashMap<Integer, Object>();
			AuthPostResponseHandlerData authPostResponseHandlerData = new AuthPostResponseHandlerData();
			Map<Integer,Object> authPostResponseHandlerMapData = new  HashMap<Integer, Object>();
			
			/********** START :  Profile Lookup Driver *********/
			List<ProfileLookupDriver> profileLookupDrivers = createRadiusServicePolicyForm.getProfileLookupList();
			
			for(ProfileLookupDriver profileLookupDriver : profileLookupDrivers){
				RadiusSubscriberProfileRepositoryDetails radiusProfileRepositoryDetails = new RadiusSubscriberProfileRepositoryDetails();
			
				RadiusProfileDriversDetails radiusProfileDriverDetails = new RadiusProfileDriversDetails();
				
				List<PrimaryDriverDetail> primaryDriverDetails = new ArrayList<PrimaryDriverDetail>();
				List<PrimaryDriverRelData> primaryDriverRelationData = profileLookupDriver.getPrimaryDriverRelDataList();
				
				for(PrimaryDriverRelData primaryDriverRelData : primaryDriverRelationData){
					PrimaryDriverDetail primaryDriverData = new PrimaryDriverDetail();
					primaryDriverData.setDriverInstanceId(primaryDriverRelData.getDriverInstanceId());
					primaryDriverData.setWeightage(primaryDriverRelData.getWeightage());
					primaryDriverDetails.add(primaryDriverData);
				}
				
				List<SecondaryAndCacheDriverDetail> seconAndCacheDriverDetails = new ArrayList<SecondaryAndCacheDriverDetail>();
				List<SecondaryDriverRelData> secondaryDriverRelData = profileLookupDriver.getSecondaryDriverRelDataList();
	
				for(SecondaryDriverRelData secoDriverRelData : secondaryDriverRelData){
					SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail = new SecondaryAndCacheDriverDetail();
					secondaryAndCacheDriverDetail.setCacheDriverId(secoDriverRelData.getCacheDriverInstId());
					secondaryAndCacheDriverDetail.setSecondaryDriverId(secoDriverRelData.getSecondaryDriverInstId());
					
					seconAndCacheDriverDetails.add(secondaryAndCacheDriverDetail);
				}
				
				
				List<AdditionalDriverDetail> additionalDriverDetailsList = new ArrayList<AdditionalDriverDetail>();
				List<AdditionalDriverRelData> additionalDriverRelDatas = profileLookupDriver.getAdditionalDriverRelDataList();
				
				for(AdditionalDriverRelData additionalDriverRelData : additionalDriverRelDatas){
					AdditionalDriverDetail additionalDriverDetail = new AdditionalDriverDetail();
					additionalDriverDetail.setDriverId(additionalDriverRelData.getDriverInstanceId());
					
					additionalDriverDetailsList.add(additionalDriverDetail);
				}
				
				radiusProfileDriverDetails.setPrimaryDriverGroup(primaryDriverDetails);
				radiusProfileDriverDetails.setSecondaryDriverGroup(seconAndCacheDriverDetails);
				radiusProfileDriverDetails.setAdditionalDriverList(additionalDriverDetailsList);
				radiusProfileDriverDetails.setDriverScript(profileLookupDriver.getDriverScript());
				
				
				UpdateIdentityParamsDetail updateIdentityParamsDetail =  new UpdateIdentityParamsDetail();
				
				updateIdentityParamsDetail.setStripIdentity(profileLookupDriver.getStripIdentity());
				updateIdentityParamsDetail.setIdetityFormat(profileLookupDriver.getSelectCase());
				updateIdentityParamsDetail.setTrimIdentity(profileLookupDriver.getTrimUserIdentity());
				updateIdentityParamsDetail.setTrimPassword(profileLookupDriver.getTrimPassword());
				updateIdentityParamsDetail.setSeparator(profileLookupDriver.getSeparator());
				
				radiusProfileRepositoryDetails.setAnonymousProfileIdentity(profileLookupDriver.getAnonymousProfileIdentity());
				radiusProfileRepositoryDetails.setDriverDetails(radiusProfileDriverDetails);
				radiusProfileRepositoryDetails.setUpdateIdentity(updateIdentityParamsDetail);
				radiusProfileRepositoryDetails.setEnabled(profileLookupDriver.getIsHandlerEnabled());
				radiusProfileRepositoryDetails.setHandlerName(profileLookupDriver.getHandlerName());
				
				int orderNumber = profileLookupDriver.getOrderNumber();
				authenticationHandlersMap.put(orderNumber, radiusProfileRepositoryDetails);
				
			}
			
			/********END : Profile Lookup Driver********/
			
			/********START : AuthenticationHandlerData********/
			List<AuthenticationHandler> authenticationHandlerList = createRadiusServicePolicyForm.getAuthHandlerList();
			
			for(AuthenticationHandler authenticationHandler:authenticationHandlerList){
				AuthenticationHandlerData authenticationHandlerData=new AuthenticationHandlerData();
				
				authenticationHandlerData.setDigestConfigId(authenticationHandler.getDigestConfigId());
				authenticationHandlerData.setEapConfigId(authenticationHandler.getEapConfigId());
				authenticationHandlerData.setUserName(authenticationHandler.getUserName());
				if(authenticationHandler.getSelectedAuthMethodTypes() != null){
					authenticationHandlerData.getSupportedMethods().addAll(authenticationHandler.getSelectedAuthMethodTypes());
				}
				authenticationHandlerData.setUserNameResponseAttributeStr(authenticationHandler.getUserNameResponseAttribs());
				authenticationHandlerData.setUserNameExpression(authenticationHandler.getUserNameExpression());
				authenticationHandlerData.setEnabled(authenticationHandler.getIsHandlerEnabled());
				authenticationHandlerData.setHandlerName(authenticationHandler.getHandlerName());
				
				int orderNumber = authenticationHandler.getOrderNumber();
				authenticationHandlersMap.put(orderNumber, authenticationHandlerData);
			}
			
			/********END : AuthenticationHandlerData********/
			
			/********START : AuthorizationHandlerData ********/
			List<AuthorizationHandler> authorizationHandlerList = createRadiusServicePolicyForm.getAuthorizationList();
			
			for(AuthorizationHandler authorizationHandler:authorizationHandlerList){
				AuthorizationHandlerData authorizationHandlerData=new AuthorizationHandlerData();
				
				authorizationHandlerData.setGracePolicy(authorizationHandler.getGracePolicyId());
				authorizationHandlerData.setDefaultSessionTimeout(authorizationHandler.getDefaultSessionTimeout());
				authorizationHandlerData
						.setThreeGPPEnabled((authorizationHandler
								.getThreeGPPEnabled() != null) ? authorizationHandler
								.getThreeGPPEnabled() : "false");
				authorizationHandlerData.setWimaxEnabled((authorizationHandler
						.getWimaxEnabled() != null) ? authorizationHandler
						.getWimaxEnabled() : "false");
	
				RadiusPolicyDetail radiusPolicyDetail = new RadiusPolicyDetail();
				
				radiusPolicyDetail
						.setAcceptOnPolicyOnFound((authorizationHandler
								.getActionOnPolicyNotFound() != null) ? authorizationHandler
								.getActionOnPolicyNotFound() : "false");
				radiusPolicyDetail
						.setRejectOnCheckItemNotFound((authorizationHandler
								.getRejectOnCheckItemNotFound() != null) ? authorizationHandler
								.getRejectOnCheckItemNotFound() : "false");
				radiusPolicyDetail
						.setRejectOnRejectItemNotFound((authorizationHandler
								.getRejectOnRejectItemNotFound() != null) ? authorizationHandler
								.getRejectOnRejectItemNotFound() : "false");
		
				authorizationHandlerData.setRadiusPolicy(radiusPolicyDetail);
				authorizationHandlerData.setEnabled(authorizationHandler.getIsHandlerEnabled());
				authorizationHandlerData.setHandlerName(authorizationHandler.getHandlerName());
				
				int orderNumber = authorizationHandler.getOrderNumber();
				authenticationHandlersMap.put(orderNumber, authorizationHandlerData);
			}
			/********END : AuthorizationHandlerData ********/
			
			/********START : ConcurrencyHandler **************/
			List<ConcurrencyHandler> concurrencyHandlers = createRadiusServicePolicyForm.getConcurrencyHandlerList();
			
			for(ConcurrencyHandler concurrencyHandler : concurrencyHandlers){
				ConcurrencyHandlerData concurrencyHandlerData = new ConcurrencyHandlerData();
				concurrencyHandlerData.setSessionManagerId(concurrencyHandler.getSessionManagerId());
				concurrencyHandlerData.setRuleSet(concurrencyHandler.getRuleset());
				concurrencyHandlerData.setEnabled(concurrencyHandler.getIsHandlerEnabled());
				concurrencyHandlerData.setHandlerName(concurrencyHandler.getHandlerName());

				authenticationHandlersMap.put(concurrencyHandler.getOrderNumber(), concurrencyHandlerData);
			}
			/********END :ConcurrencyHandler **************/
			
			/********START : RadPluginHandlerData ***********/
			List<PluginHandler> pluginHandlers = createRadiusServicePolicyForm.getAuthPluginHandlerList();
		if(pluginHandlers != null && pluginHandlers.size() > 0){
			for(PluginHandler pluginHandler : pluginHandlers){
				RadPluginHandlerData radPluginHandlerData=new RadPluginHandlerData();
				
				radPluginHandlerData = getRadiusPluginHandlerData(pluginHandler);
				
				radPluginHandlerData.setHandlerName(pluginHandler.getHandlerName());
				int orderNumber = pluginHandler.getOrderNumber();

				if(pluginHandler.getIsAdditional().equals("false")){
					authenticationHandlersMap.put(orderNumber, radPluginHandlerData);
				}else {
					authPostResponseHandlerMapData.put(orderNumber, radPluginHandlerData);
				}
			}
		}
			/********END : RadPluginHandlerData ***********/
			
			/********START : SynchronousCommunicationHandlerData ***********/
			
			List<ProxyCommunication> proxyCommunicationsList = createRadiusServicePolicyForm.getAuthProxyCommunicationList();
	
			for (ProxyCommunication proxyCommunication : proxyCommunicationsList) {
				SynchronousCommunicationHandlerData synchronousCommunicationHandlerData = new SynchronousCommunicationHandlerData();
	
				List<ExternalCommunicationEntryData> communicationEntryDatas = new ArrayList<ExternalCommunicationEntryData>();
				List<ProxyCommunicationData> proxyCommunicationDatas = proxyCommunication.getProxyCommunicationList();
	
				for (ProxyCommunicationData communicationData : proxyCommunicationDatas) {
	
					ExternalCommunicationEntryData communicationEntryData = new ExternalCommunicationEntryData();
	
					communicationEntryData.setRuleset(communicationData.getRuleset());
					communicationEntryData.setAcceptOnTimeout(communicationData.getAcceptOnTimeout());
					communicationEntryData.setScript(communicationData.getScript());
					if(Strings.isNullOrBlank(communicationData.getTranslationMappingName()) == false){
							communicationEntryData.setTranslationMapping(communicationData.getTranslationMappingName());	
					}
	
					CommunicatorGroupData communicatorGroupData = new CommunicatorGroupData();
	
					List<ESIServerData> esiServerData = communicationData.getEsiListData();
					List<CommunicatorData> commDatas = new ArrayList<CommunicatorData>();
					
					for (ESIServerData esiData : esiServerData) {
	
						CommunicatorData communicatorData = new CommunicatorData();
	
						communicatorData.setId(esiData.getEsiId());
						communicatorData.setLoadFactor(esiData.getLoadFactor());
						commDatas.add(communicatorData);
					}
	
					communicatorGroupData.getCommunicatorDataList().addAll(commDatas);
	
					communicationEntryData.setCommunicatorGroupData(communicatorGroupData);
					communicationEntryDatas.add(communicationEntryData);
				}
				synchronousCommunicationHandlerData.getProxyCommunicatioEntries().addAll(communicationEntryDatas);
				synchronousCommunicationHandlerData.setEnabled(proxyCommunication.getIsHandlerEnabled());
				synchronousCommunicationHandlerData.setHandlerName(proxyCommunication.getHandlerName());
				
				int orderNumber = proxyCommunication.getOrderNumber();
				authenticationHandlersMap.put(orderNumber, synchronousCommunicationHandlerData);
			}
	
			/********END   : SynchronousCommunicationHandlerData ***********/
			
			/********START : BroadcastCommunicationHandlerData ***********/
			List<BroadcastCommunication> broadcastCommunicationList = createRadiusServicePolicyForm.getAuthBroadcastCommunicationList();
	
			for (BroadcastCommunication broadcastCommunication : broadcastCommunicationList) {
				BroadcastCommunicationHandlerData broadcastCommunicationHandlerData = new BroadcastCommunicationHandlerData();
	
				List<AsyncCommunicationEntryData> communicationEntryDatas = new ArrayList<AsyncCommunicationEntryData>();
				List<BroadcastCommunicationData> broadcastCommunicationDatas = broadcastCommunication.getBroadcastCommunicationList();
	
				for (BroadcastCommunicationData communicationData : broadcastCommunicationDatas) {
	
					AsyncCommunicationEntryData asynchCommunicationEntryData = new AsyncCommunicationEntryData();
	
					asynchCommunicationEntryData.setRuleset(communicationData.getRuleset());
					asynchCommunicationEntryData.setAcceptOnTimeout(communicationData.getAcceptOnTimeout());
					asynchCommunicationEntryData.setScript(communicationData.getScript());
					if(Strings.isNullOrBlank(communicationData.getTranslationMappingName()) == false){
							asynchCommunicationEntryData.setTranslationMapping(communicationData.getTranslationMappingName());	
					}
					asynchCommunicationEntryData.setWait(communicationData.getWaitForResponse());
	
					CommunicatorGroupData communicatorGroupData = new CommunicatorGroupData();
	
					List<ESIServerData> esiServerData = communicationData.getEsiListData();
					List<CommunicatorData> commDatas = new ArrayList<CommunicatorData>();
					
					for (ESIServerData esiData : esiServerData) {
	
						CommunicatorData communicatorData = new CommunicatorData();
	
						communicatorData.setId(esiData.getEsiId());
						communicatorData.setLoadFactor(esiData.getLoadFactor());
						commDatas.add(communicatorData);
					}
	
					communicatorGroupData.getCommunicatorDataList().addAll(commDatas);
	
					asynchCommunicationEntryData.setCommunicatorGroupData(communicatorGroupData);
					communicationEntryDatas.add(asynchCommunicationEntryData);
				}
				broadcastCommunicationHandlerData.getProxyCommunicationEntries().addAll(communicationEntryDatas);
				broadcastCommunicationHandlerData.setEnabled(broadcastCommunication.getIsHandlerEnabled());
				broadcastCommunicationHandlerData.setHandlerName(broadcastCommunication.getHandlerName());
				
				int orderNumber = broadcastCommunication.getOrderNumber();
				authenticationHandlersMap.put(orderNumber, broadcastCommunicationHandlerData);
			}
			
			/********END   : BroadcastCommunicationHandlerData ***********/
			
			
			/********START : CdrGenerationHandlerData ***********/
			List<CDRGeneration> authCdrGenerations = createRadiusServicePolicyForm.getAuthCDRGenerationList();
			for(CDRGeneration  cdrGeneration: authCdrGenerations){
				CdrGenerationHandlerData cdrGenerationHandlerData = new CdrGenerationHandlerData();
				
				if(cdrGeneration.getIsAdditional().equals("false")){
					List<CdrHandlerEntryData> cdrHandlerEntryDatas = new ArrayList<CdrHandlerEntryData>();
					List<CDRGenerationDetails> cdrGenerationDetails = cdrGeneration.getCdrGenerationDetailsList();
					
					for(CDRGenerationDetails cdrDetails :cdrGenerationDetails){
							CdrHandlerEntryData cdrHandlerEntryData = new CdrHandlerEntryData();
							cdrHandlerEntryData.setRuleset(cdrDetails.getRuleset());
							cdrHandlerEntryData.setWait(cdrDetails.getWaitForCDRDump());
							
							RadiusProfileDriversDetails radiusProfileDriversDetails =new RadiusProfileDriversDetails();
							
							//Set Primary Driver Data
							List<PrimaryDriverDetail> primaryDriverDetailsList =new ArrayList<PrimaryDriverDetail>();
							PrimaryDriverDetail primaryDriverDetail = new PrimaryDriverDetail();
							primaryDriverDetail.setDriverInstanceId(cdrDetails.getPrimaryDriverId());
							primaryDriverDetail.setWeightage(1);
							
							primaryDriverDetailsList.add(primaryDriverDetail);
							radiusProfileDriversDetails.setPrimaryDriverGroup(primaryDriverDetailsList);
							
							//Set Secondary Driver Data
							List<SecondaryAndCacheDriverDetail> secondaryDriverDetailsList =new ArrayList<SecondaryAndCacheDriverDetail>();
							SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail = new SecondaryAndCacheDriverDetail();
							secondaryAndCacheDriverDetail.setSecondaryDriverId(cdrDetails.getSecondaryDriverId());
							secondaryAndCacheDriverDetail.setCacheDriverId("0");
							
							secondaryDriverDetailsList.add(secondaryAndCacheDriverDetail);
							
							radiusProfileDriversDetails.setSecondaryDriverGroup(secondaryDriverDetailsList);
							radiusProfileDriversDetails.setDriverScript(cdrDetails.getScript());
							
							cdrHandlerEntryData.setDriverDetails(radiusProfileDriversDetails);
							cdrHandlerEntryDatas.add(cdrHandlerEntryData);
						}
						cdrGenerationHandlerData.getCdrHandlers().addAll(cdrHandlerEntryDatas);
						cdrGenerationHandlerData.setEnabled(cdrGeneration.getIsHandlerEnabled());
						cdrGenerationHandlerData.setHandlerName(cdrGeneration.getHandlerName());
						
						int orderNumber = cdrGeneration.getOrderNumber();
						authenticationHandlersMap.put(orderNumber, cdrGenerationHandlerData);
					}else{
						List<CdrHandlerEntryData> cdrHandlerEntryDatas = new ArrayList<CdrHandlerEntryData>();
						List<CDRGenerationDetails> cdrGenerationDetails = cdrGeneration.getCdrGenerationDetailsList();
						
						for(CDRGenerationDetails cdrDetails :cdrGenerationDetails){
								CdrHandlerEntryData cdrHandlerEntryData = new CdrHandlerEntryData();
								cdrHandlerEntryData.setRuleset(cdrDetails.getRuleset());
								cdrHandlerEntryData.setWait(cdrDetails.getWaitForCDRDump());
								
								RadiusProfileDriversDetails radiusProfileDriversDetails =new RadiusProfileDriversDetails();
								
								//Set Primary Driver Data
								List<PrimaryDriverDetail> primaryDriverDetailsList =new ArrayList<PrimaryDriverDetail>();
								PrimaryDriverDetail primaryDriverDetail = new PrimaryDriverDetail();
								primaryDriverDetail.setDriverInstanceId(cdrDetails.getPrimaryDriverId());
								primaryDriverDetail.setWeightage(1);
								
								primaryDriverDetailsList.add(primaryDriverDetail);
								radiusProfileDriversDetails.setPrimaryDriverGroup(primaryDriverDetailsList);
								
								//Set Secondary Driver Data
								List<SecondaryAndCacheDriverDetail> secondaryDriverDetailsList =new ArrayList<SecondaryAndCacheDriverDetail>();
								SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail = new SecondaryAndCacheDriverDetail();
								secondaryAndCacheDriverDetail.setSecondaryDriverId(cdrDetails.getSecondaryDriverId());
								secondaryAndCacheDriverDetail.setCacheDriverId("0");
								
								secondaryDriverDetailsList.add(secondaryAndCacheDriverDetail);
								
								radiusProfileDriversDetails.setSecondaryDriverGroup(secondaryDriverDetailsList);
								radiusProfileDriversDetails.setDriverScript(cdrDetails.getScript());
								
								cdrHandlerEntryData.setDriverDetails(radiusProfileDriversDetails);
								cdrHandlerEntryDatas.add(cdrHandlerEntryData);
							}
							cdrGenerationHandlerData.getCdrHandlers().addAll(cdrHandlerEntryDatas);
							
							if(cdrHandlerEntryDatas != null && cdrHandlerEntryDatas.size() > 0 ){
								cdrGenerationHandlerData.setEnabled(cdrGeneration.getIsHandlerEnabled());
								cdrGenerationHandlerData.setHandlerName(cdrGeneration.getHandlerName());
							}
							
							int orderNumber = cdrGeneration.getOrderNumber();
							authPostResponseHandlerMapData.put(orderNumber, cdrGenerationHandlerData);
					}
				}
			
			/********END   : CdrGenerationHandlerData ***********/
			
			/********START   : COADMGenerationHandlerData ***********/
			List<COADMGeneration> authCoaDmGenerations = createRadiusServicePolicyForm.getAuthCOADMGenList();
			for(COADMGeneration  coaDMGeneration: authCoaDmGenerations){
				CoADMGenerationHandlerData coaDMGenerationHandlerData = new CoADMGenerationHandlerData();
				
				if(coaDMGeneration.getIsAdditional().equals("false")){
					List<CoADMHandlerEntryData> coaDmHandlerEntryDatas = new ArrayList<CoADMHandlerEntryData>();
					List<COADMGenerationDetails> coaDMGenerationDetails = coaDMGeneration.getCoaDMGenerationDetailList();
					
					for(COADMGenerationDetails cdrDetails :coaDMGenerationDetails){
							CoADMHandlerEntryData coaDmHandlerEntryData = new CoADMHandlerEntryData();
							coaDmHandlerEntryData.setRuleset(cdrDetails.getRuleset());
							coaDmHandlerEntryData.setPacketType(cdrDetails.getPacketType());
							coaDmHandlerEntryData.setTranslationMapping(cdrDetails.getTranslationMapping());
						
							coaDmHandlerEntryDatas.add(coaDmHandlerEntryData);
					}
					
					coaDMGenerationHandlerData.getEntries().addAll(coaDmHandlerEntryDatas);
					coaDMGenerationHandlerData.setScheduleAfterInMillis(coaDMGeneration.getScheduleAfterInMillis());
					coaDMGenerationHandlerData.setEnabled(coaDMGeneration.getIsHandlerEnabled());
					coaDMGenerationHandlerData.setHandlerName(coaDMGeneration.getHandlerName());
					
					int orderNumber = coaDMGeneration.getOrderNumber();
					authenticationHandlersMap.put(orderNumber, coaDMGenerationHandlerData);
				}else{
						List<CoADMHandlerEntryData> cdrHandlerEntryDatas = new ArrayList<CoADMHandlerEntryData>();
						List<COADMGenerationDetails> cdrGenerationDetails = coaDMGeneration.getCoaDMGenerationDetailList();
						
						for(COADMGenerationDetails cdrDetails :cdrGenerationDetails){
								CoADMHandlerEntryData coaDmHandlerEntryData = new CoADMHandlerEntryData();
								coaDmHandlerEntryData.setRuleset(cdrDetails.getRuleset());
								coaDmHandlerEntryData.setPacketType(cdrDetails.getPacketType());
								coaDmHandlerEntryData.setTranslationMapping(cdrDetails.getTranslationMapping());
								
								cdrHandlerEntryDatas.add(coaDmHandlerEntryData);
						}
						
						coaDMGenerationHandlerData.getEntries().addAll(cdrHandlerEntryDatas);
						coaDMGenerationHandlerData.setScheduleAfterInMillis(coaDMGeneration.getScheduleAfterInMillis());
						if(cdrHandlerEntryDatas != null && cdrHandlerEntryDatas.size() > 0){
							coaDMGenerationHandlerData.setEnabled(coaDMGeneration.getIsHandlerEnabled());
							coaDMGenerationHandlerData.setHandlerName(coaDMGeneration.getHandlerName());
						}
						
						int orderNumber = coaDMGeneration.getOrderNumber();
						
						authPostResponseHandlerMapData.put(orderNumber, coaDMGenerationHandlerData);
					}
				}
			
			/********END   : COADMGenerationHandlerData ***********/

			List<ConcurrencyIMDGHandler> concurrencyIMDGHandlers = createRadiusServicePolicyForm.getConcurrencyIMDGHandlerList();

			for(ConcurrencyIMDGHandler conIMDGHandler : concurrencyIMDGHandlers){
				ConcurrencyIMDGHandlerData concurrencyIMDGHandlerData = new ConcurrencyIMDGHandlerData();

				concurrencyIMDGHandlerData.setRuleSet(conIMDGHandler.getRuleset());
				concurrencyIMDGHandlerData.setImdgFieldValue(conIMDGHandler.getImdgFieldName());
				concurrencyIMDGHandlerData.setEnabled(conIMDGHandler.getIsHandlerEnabled());
				concurrencyIMDGHandlerData.setHandlerName(conIMDGHandler.getHandlerName());

				authenticationHandlersMap.put(conIMDGHandler.getOrderNumber(), concurrencyIMDGHandlerData);
			}

			List<StatefulProxySequentialHandler> statefulProxySequentialHandlers = createRadiusServicePolicyForm.getStatefulProxySequentialHandlerList();

			for (StatefulProxySequentialHandler statefulProxySequentialHandler:statefulProxySequentialHandlers) {
				StatefulProxySequentialHandlerData handlerData = new StatefulProxySequentialHandlerData();
				List<StatefulProHandlerData> sequentialHandlerEntryDatas = statefulProxySequentialHandler.getSequentialHandlerEntryData();

				List<StatefulProxyHandlerEntryData> statefulProxyHandlerEntryDataList = new ArrayList<>();

				for (StatefulProHandlerData entryData:sequentialHandlerEntryDatas) {
					StatefulProxyHandlerEntryData statefulProxyHandlerEntryData = new StatefulProxyHandlerEntryData();

					statefulProxyHandlerEntryData.setRuleset(entryData.getRuleset());
					statefulProxyHandlerEntryData.setServerGroupName(entryData.getServerGroupName());
					statefulProxyHandlerEntryData.setTranslationMappingName(entryData.getTranslationMappingName());
					statefulProxyHandlerEntryData.setScript(entryData.getScript());
					statefulProxyHandlerEntryData.setAcceptOnTimeout(entryData.getAcceptOnTimeout());

					statefulProxyHandlerEntryDataList.add(statefulProxyHandlerEntryData);
				}

				handlerData.setEnabled(statefulProxySequentialHandler.getIsHandlerEnabled());
				handlerData.setHandlerName(statefulProxySequentialHandler.getHandlerName());
				handlerData.getStatefulProxyHandlerEntryDataList().addAll(statefulProxyHandlerEntryDataList);

				authenticationHandlersMap.put(statefulProxySequentialHandler.getOrderNumber(),handlerData);
			}

			List<StatefulProxyBroadcastHandler> statefulProxyBroadcastHandlers = createRadiusServicePolicyForm.getStatefulProxyBroadcastHandlersList();
			for(StatefulProxyBroadcastHandler broadcastHandler : statefulProxyBroadcastHandlers){
				StatefulProxyBroadcastHandlerData handlerData = new StatefulProxyBroadcastHandlerData();

				List<StatefulProHandlerData> broadcastHandlerEntryDatas = broadcastHandler.getBroadcastHandlerEntryData();
				List<StatefulProxyBroadcastHandlerEntryData> statefulProxyBroadcastHandlerEntryDataList = new ArrayList<>();

				for (StatefulProHandlerData entryData : broadcastHandlerEntryDatas) {
					StatefulProxyBroadcastHandlerEntryData handlerEntryData = new StatefulProxyBroadcastHandlerEntryData();

					handlerEntryData.setRuleset(entryData.getRuleset());
					handlerEntryData.setServerGroupName(entryData.getServerGroupName());
					handlerEntryData.setTranslationMappingName(entryData.getTranslationMappingName());
					handlerEntryData.setScript(entryData.getScript());
					handlerEntryData.setAcceptOnTimeout(entryData.getAcceptOnTimeout());
					handlerEntryData.setWaitForResponse(entryData.getWaitForResponse());

					statefulProxyBroadcastHandlerEntryDataList.add(handlerEntryData);
				}
				handlerData.setEnabled(broadcastHandler.getIsHandlerEnabled());
				handlerData.setHandlerName(broadcastHandler.getHandlerName());
				handlerData.getStatefulProxyHandlerEntryDataList().addAll(statefulProxyBroadcastHandlerEntryDataList);

				authenticationHandlersMap.put(broadcastHandler.getOrderNumber(),handlerData);
			}

			if( createRadiusServicePolicyForm.getAuthPrePluginData() != null && createRadiusServicePolicyForm.getAuthPrePluginData().size() > 0){
				
				List<PluginEntryDetail> pluginDataList = new ArrayList<PluginEntryDetail>();
				for (PolicyPluginData policyPluginData : createRadiusServicePolicyForm.getAuthPrePluginData()) {
					PluginEntryDetail preAndPostPluginData = new PluginEntryDetail();
					preAndPostPluginData.setPluginArgument(policyPluginData.getPluginArgument());
					preAndPostPluginData.setPluginName(policyPluginData.getPluginName());
					pluginDataList.add(preAndPostPluginData);
				}
				authenticationPolicyData.setPrePluginDataList(pluginDataList);
			}

			if( createRadiusServicePolicyForm.getAuthPostPluginData() != null && createRadiusServicePolicyForm.getAuthPostPluginData().size() > 0){
				
				List<PluginEntryDetail> pluginDataList = new ArrayList<PluginEntryDetail>();
				for (PolicyPluginData policyPluginData : createRadiusServicePolicyForm.getAuthPostPluginData()) {
					PluginEntryDetail preAndPostPluginData = new PluginEntryDetail();
					preAndPostPluginData.setPluginArgument(policyPluginData.getPluginArgument());
					preAndPostPluginData.setPluginName(policyPluginData.getPluginName());
					pluginDataList.add(preAndPostPluginData);
				}
				authenticationPolicyData.setPostPluginDataList(pluginDataList);
			}
			
			
			//Add Handler based on Order Number
			for (Map.Entry<Integer, Object> entry : authenticationHandlersMap.entrySet()) {
			    authenticationPolicyData.getHandlersData().add((AuthServicePolicyHandlerData) (entry.getValue()));
			}		
			
			for(Map.Entry<Integer, Object> postResponseHandler : authPostResponseHandlerMapData.entrySet()){
				authPostResponseHandlerData.getHandlersData().add((AuthServicePolicyHandlerData)(postResponseHandler.getValue()));
			}
			
			if(authPostResponseHandlerData != null){
				authenticationPolicyData.setPostResponseHandlerData(authPostResponseHandlerData);
			}
			
			radiusServicePolicyData.setAuthenticationPolicyData(authenticationPolicyData);
		}
		
		/*************************************************************************************/
		/*							  ACCOUNTING POLICY 								     */
		/*************************************************************************************/
		if(createRadiusServicePolicyForm.isAccounting()){
			AccountingPolicyData accountingPolicyData = new AccountingPolicyData();
			Map<Integer, Object> accountingHandlersMap=new  HashMap<Integer, Object>();
			
			AcctPostResponseHandlerData acctPostResponseHandlerData = new AcctPostResponseHandlerData();
			Map<Integer,Object> acctPostResponseHandlerMapData = new  HashMap<Integer, Object>();
			
			/********** START :  Profile Lookup Driver *********/
			List<ProfileLookupDriver> acctProfileLookupDrivers = createRadiusServicePolicyForm.getAcctProfileLookupDriver();
			
			for(ProfileLookupDriver profileLookupDriver : acctProfileLookupDrivers){
				RadiusSubscriberProfileRepositoryDetails radiusProfileRepositoryDetails = new RadiusSubscriberProfileRepositoryDetails();
			
				RadiusProfileDriversDetails radiusProfileDriverDetails = new RadiusProfileDriversDetails();
				
				List<PrimaryDriverDetail> primaryDriverDetails = new ArrayList<PrimaryDriverDetail>();
				List<PrimaryDriverRelData> primaryDriverRelationData = profileLookupDriver.getPrimaryDriverRelDataList();
				
				for(PrimaryDriverRelData primaryDriverRelData : primaryDriverRelationData){
					PrimaryDriverDetail primaryDriverDetail = new PrimaryDriverDetail();
					primaryDriverDetail.setDriverInstanceId(primaryDriverRelData.getDriverInstanceId());
					primaryDriverDetail.setWeightage(primaryDriverRelData.getWeightage());
					primaryDriverDetails.add(primaryDriverDetail);
				}
				
				List<SecondaryAndCacheDriverDetail> seconAndCacheDriverDetails = new ArrayList<SecondaryAndCacheDriverDetail>();
				List<SecondaryDriverRelData> secondaryDriverRelData = profileLookupDriver.getSecondaryDriverRelDataList();
	
				for(SecondaryDriverRelData secoDriverRelData : secondaryDriverRelData){
					SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail = new SecondaryAndCacheDriverDetail();
					secondaryAndCacheDriverDetail.setCacheDriverId(secoDriverRelData.getCacheDriverInstId());
					secondaryAndCacheDriverDetail.setSecondaryDriverId(secoDriverRelData.getSecondaryDriverInstId());
					
					seconAndCacheDriverDetails.add(secondaryAndCacheDriverDetail);
				}
				
				
				List<AdditionalDriverDetail> additionalDriverDetailsList = new ArrayList<AdditionalDriverDetail>();
				List<AdditionalDriverRelData> additionalDriverRelDatas = profileLookupDriver.getAdditionalDriverRelDataList();
				
				for(AdditionalDriverRelData additionalDriverRelData : additionalDriverRelDatas){
					AdditionalDriverDetail additionalDriverDetail = new AdditionalDriverDetail();
					additionalDriverDetail.setDriverId(additionalDriverRelData.getDriverInstanceId());
					
					additionalDriverDetailsList.add(additionalDriverDetail);
				}
				
				radiusProfileDriverDetails.setPrimaryDriverGroup(primaryDriverDetails);
				radiusProfileDriverDetails.setSecondaryDriverGroup(seconAndCacheDriverDetails);
				radiusProfileDriverDetails.setAdditionalDriverList(additionalDriverDetailsList);
				radiusProfileDriverDetails.setDriverScript(profileLookupDriver.getDriverScript());
				
				UpdateIdentityParamsDetail updateIdentityParamsDetail =  new UpdateIdentityParamsDetail();
				
				updateIdentityParamsDetail.setStripIdentity(profileLookupDriver.getStripIdentity());
				updateIdentityParamsDetail.setIdetityFormat(profileLookupDriver.getSelectCase());
				updateIdentityParamsDetail.setTrimIdentity(profileLookupDriver.getTrimUserIdentity());
				updateIdentityParamsDetail.setTrimPassword(profileLookupDriver.getTrimPassword());
				updateIdentityParamsDetail.setSeparator(profileLookupDriver.getSeparator());
				
				radiusProfileRepositoryDetails.setAnonymousProfileIdentity(profileLookupDriver.getAnonymousProfileIdentity());
				radiusProfileRepositoryDetails.setDriverDetails(radiusProfileDriverDetails);
				radiusProfileRepositoryDetails.setUpdateIdentity(updateIdentityParamsDetail);
				radiusProfileRepositoryDetails.setEnabled(profileLookupDriver.getIsHandlerEnabled());
				radiusProfileRepositoryDetails.setHandlerName(profileLookupDriver.getHandlerName());
				
				int orderNumber = profileLookupDriver.getOrderNumber();
				accountingHandlersMap.put(orderNumber, radiusProfileRepositoryDetails);
				
			}
			
			/********END : Profile Lookup Driver********/
			
	
			/********START : RadPluginHandlerData ***********/
			List<PluginHandler> acctPluginHandlers = createRadiusServicePolicyForm.getAcctPluginHandlerList();
			
		if(acctPluginHandlers != null && acctPluginHandlers.size() > 0){
			for(PluginHandler pluginHandler : acctPluginHandlers){
				RadPluginHandlerData radPluginHandlerData=new RadPluginHandlerData();
				
				radPluginHandlerData = getRadiusPluginHandlerData(pluginHandler);
				radPluginHandlerData.setHandlerName(pluginHandler.getHandlerName());
				int orderNumber = pluginHandler.getOrderNumber();

				if(pluginHandler.getIsAdditional().equals("false")){
					accountingHandlersMap.put(orderNumber, radPluginHandlerData);
				}else {
					acctPostResponseHandlerMapData.put(orderNumber, radPluginHandlerData);
				}
			}
		}
				
			/********END : RadPluginHandlerData ***********/
			
			
			/********START : SynchronousCommunicationHandlerData ***********/
			
			List<ProxyCommunication> acctProxyCommunicationsList = createRadiusServicePolicyForm.getAcctProxyCommunicationList();
	
			for (ProxyCommunication proxyCommunication : acctProxyCommunicationsList) {
				SynchronousCommunicationHandlerData synchronousCommunicationHandlerData = new SynchronousCommunicationHandlerData();
	
				List<ExternalCommunicationEntryData> communicationEntryDatas = new ArrayList<ExternalCommunicationEntryData>();
				List<ProxyCommunicationData> proxyCommunicationDatas = proxyCommunication.getProxyCommunicationList();
	
				for (ProxyCommunicationData communicationData : proxyCommunicationDatas) {
	
					ExternalCommunicationEntryData communicationEntryData = new ExternalCommunicationEntryData();
	
					communicationEntryData.setRuleset(communicationData.getRuleset());
					communicationEntryData.setAcceptOnTimeout(communicationData.getAcceptOnTimeout());
					communicationEntryData.setScript(communicationData.getScript());
					if(Strings.isNullOrBlank(communicationData.getTranslationMappingName()) == false){
							communicationEntryData.setTranslationMapping(communicationData.getTranslationMappingName());	
					}
	
					CommunicatorGroupData communicatorGroupData = new CommunicatorGroupData();
	
					List<ESIServerData> esiServerData = communicationData.getEsiListData();
					List<CommunicatorData> commDatas = new ArrayList<CommunicatorData>();
					
					for (ESIServerData esiData : esiServerData) {
	
						CommunicatorData communicatorData = new CommunicatorData();
	
						communicatorData.setId(esiData.getEsiId());
						communicatorData.setLoadFactor(esiData.getLoadFactor());
						commDatas.add(communicatorData);
					}
	
					communicatorGroupData.getCommunicatorDataList().addAll(commDatas);
	
					communicationEntryData.setCommunicatorGroupData(communicatorGroupData);
					communicationEntryDatas.add(communicationEntryData);
				}
				synchronousCommunicationHandlerData.getProxyCommunicatioEntries().addAll(communicationEntryDatas);
				synchronousCommunicationHandlerData.setEnabled(proxyCommunication.getIsHandlerEnabled());
				synchronousCommunicationHandlerData.setHandlerName(proxyCommunication.getHandlerName());
				
				int orderNumber = proxyCommunication.getOrderNumber();
				accountingHandlersMap.put(orderNumber, synchronousCommunicationHandlerData);
			}
	
			/********END   : SynchronousCommunicationHandlerData ***********/
			
			
			/********START : BroadcastCommunicationHandlerData ***********/
			List<BroadcastCommunication> acctBroadcastCommunicationList = createRadiusServicePolicyForm.getAcctBroadcastCommunicationList();
	
			for (BroadcastCommunication broadcastCommunication : acctBroadcastCommunicationList) {
				BroadcastCommunicationHandlerData broadcastCommunicationHandlerData = new BroadcastCommunicationHandlerData();
	
				List<AsyncCommunicationEntryData> communicationEntryDatas = new ArrayList<AsyncCommunicationEntryData>();
				List<BroadcastCommunicationData> broadcastCommunicationDatas = broadcastCommunication.getBroadcastCommunicationList();
	
				for (BroadcastCommunicationData communicationData : broadcastCommunicationDatas) {
	
					AsyncCommunicationEntryData asynchCommunicationEntryData = new AsyncCommunicationEntryData();
	
					asynchCommunicationEntryData.setRuleset(communicationData.getRuleset());
					asynchCommunicationEntryData.setAcceptOnTimeout(communicationData.getAcceptOnTimeout());
					asynchCommunicationEntryData.setScript(communicationData.getScript());
					if(Strings.isNullOrBlank(communicationData.getTranslationMappingName()) == false){
							asynchCommunicationEntryData.setTranslationMapping(communicationData.getTranslationMappingName());	
					}
					asynchCommunicationEntryData.setWait(communicationData.getWaitForResponse());
	
					CommunicatorGroupData communicatorGroupData = new CommunicatorGroupData();
	
					List<ESIServerData> esiServerData = communicationData.getEsiListData();
					List<CommunicatorData> commDatas = new ArrayList<CommunicatorData>();
					
					for (ESIServerData esiData : esiServerData) {
	
						CommunicatorData communicatorData = new CommunicatorData();
	
						communicatorData.setId(esiData.getEsiId());
						communicatorData.setLoadFactor(esiData.getLoadFactor());
						commDatas.add(communicatorData);
					}
	
					communicatorGroupData.getCommunicatorDataList().addAll(commDatas);
	
					asynchCommunicationEntryData.setCommunicatorGroupData(communicatorGroupData);
					communicationEntryDatas.add(asynchCommunicationEntryData);
				}
				broadcastCommunicationHandlerData.getProxyCommunicationEntries().addAll(communicationEntryDatas);
				broadcastCommunicationHandlerData.setEnabled(broadcastCommunication.getIsHandlerEnabled());
				broadcastCommunicationHandlerData.setHandlerName(broadcastCommunication.getHandlerName());
				
				int orderNumber = broadcastCommunication.getOrderNumber();
				accountingHandlersMap.put(orderNumber, broadcastCommunicationHandlerData);
			}
	
			/********END   : BroadcastCommunicationHandlerData ***********/
			
			
			/********START : CdrGenerationHandlerData ***********/
			List<CDRGeneration> cdrGenerations = createRadiusServicePolicyForm.getAcctCDRGenerationList();
			for(CDRGeneration  cdrGeneration: cdrGenerations){
				CdrGenerationHandlerData cdrGenerationHandlerData = new CdrGenerationHandlerData();
				
				if(cdrGeneration.getIsAdditional().equals("false")){
					List<CdrHandlerEntryData> cdrHandlerEntryDatas = new ArrayList<CdrHandlerEntryData>();
					
					List<CDRGenerationDetails> cdrGenerationDetails = cdrGeneration.getCdrGenerationDetailsList();
					
					for(CDRGenerationDetails cdrDetails :cdrGenerationDetails){
							CdrHandlerEntryData cdrHandlerEntryData = new CdrHandlerEntryData();
							cdrHandlerEntryData.setRuleset(cdrDetails.getRuleset());
							cdrHandlerEntryData.setWait(cdrDetails.getWaitForCDRDump());
							
							RadiusProfileDriversDetails radiusProfileDriversDetails =new RadiusProfileDriversDetails();
							
							//Set Primary Driver Data
							List<PrimaryDriverDetail> primaryDriverDetailsList =new ArrayList<PrimaryDriverDetail>();
							PrimaryDriverDetail primaryDriverDetail = new PrimaryDriverDetail();
							primaryDriverDetail.setDriverInstanceId(cdrDetails.getPrimaryDriverId());
							primaryDriverDetail.setWeightage(1);
							
							primaryDriverDetailsList.add(primaryDriverDetail);
							radiusProfileDriversDetails.setPrimaryDriverGroup(primaryDriverDetailsList);
							
							//Set Secondary Driver Data
							List<SecondaryAndCacheDriverDetail> secondaryDriverDetailsList =new ArrayList<SecondaryAndCacheDriverDetail>();
							SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail = new SecondaryAndCacheDriverDetail();
							secondaryAndCacheDriverDetail.setSecondaryDriverId(cdrDetails.getSecondaryDriverId());
							secondaryAndCacheDriverDetail.setCacheDriverId("0");
							
							secondaryDriverDetailsList.add(secondaryAndCacheDriverDetail);
							
							radiusProfileDriversDetails.setSecondaryDriverGroup(secondaryDriverDetailsList);
							radiusProfileDriversDetails.setDriverScript(cdrDetails.getScript());
							
							cdrHandlerEntryData.setDriverDetails(radiusProfileDriversDetails);
							cdrHandlerEntryDatas.add(cdrHandlerEntryData);
						}
				
						cdrGenerationHandlerData.getCdrHandlers().addAll(cdrHandlerEntryDatas);
						cdrGenerationHandlerData.setEnabled(cdrGeneration.getIsHandlerEnabled());
						cdrGenerationHandlerData.setHandlerName(cdrGeneration.getHandlerName());
						
						int orderNumber = cdrGeneration.getOrderNumber();
						accountingHandlersMap.put(orderNumber, cdrGenerationHandlerData);
				}else{
					List<CdrHandlerEntryData> cdrHandlerEntryDatas = new ArrayList<CdrHandlerEntryData>();
					List<CDRGenerationDetails> cdrGenerationDetails = cdrGeneration.getCdrGenerationDetailsList();
					
					for(CDRGenerationDetails cdrDetails :cdrGenerationDetails){
							CdrHandlerEntryData cdrHandlerEntryData = new CdrHandlerEntryData();
							cdrHandlerEntryData.setRuleset(cdrDetails.getRuleset());
							cdrHandlerEntryData.setWait(cdrDetails.getWaitForCDRDump());
							
							RadiusProfileDriversDetails radiusProfileDriversDetails =new RadiusProfileDriversDetails();
							
							//Set Primary Driver Data
							List<PrimaryDriverDetail> primaryDriverDetailsList =new ArrayList<PrimaryDriverDetail>();
							PrimaryDriverDetail primaryDriverDetail = new PrimaryDriverDetail();
							primaryDriverDetail.setDriverInstanceId(cdrDetails.getPrimaryDriverId());
							primaryDriverDetail.setWeightage(1);
							
							primaryDriverDetailsList.add(primaryDriverDetail);
							radiusProfileDriversDetails.setPrimaryDriverGroup(primaryDriverDetailsList);
							
							//Set Secondary Driver Data
							List<SecondaryAndCacheDriverDetail> secondaryDriverDetailsList =new ArrayList<SecondaryAndCacheDriverDetail>();
							SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail = new SecondaryAndCacheDriverDetail();
							secondaryAndCacheDriverDetail.setSecondaryDriverId(cdrDetails.getSecondaryDriverId());
							secondaryAndCacheDriverDetail.setCacheDriverId("0");
							
							secondaryDriverDetailsList.add(secondaryAndCacheDriverDetail);
							
							radiusProfileDriversDetails.setSecondaryDriverGroup(secondaryDriverDetailsList);
							radiusProfileDriversDetails.setDriverScript(cdrDetails.getScript());
							
							cdrHandlerEntryData.setDriverDetails(radiusProfileDriversDetails);
							cdrHandlerEntryDatas.add(cdrHandlerEntryData);
						}
						cdrGenerationHandlerData.getCdrHandlers().addAll(cdrHandlerEntryDatas);
						if(cdrHandlerEntryDatas!= null && cdrHandlerEntryDatas.size() > 0 ){
							cdrGenerationHandlerData.setEnabled(cdrGeneration.getIsHandlerEnabled());
							cdrGenerationHandlerData.setHandlerName(cdrGeneration.getHandlerName());
						}
						
						int orderNumber = cdrGeneration.getOrderNumber();
						acctPostResponseHandlerMapData.put(orderNumber, cdrGenerationHandlerData);
				}
				
				}
			
			/********END   : CdrGenerationHandlerData ***********/
			
			/********START   : COADMGenerationHandlerData ***********/
			List<COADMGeneration> acctCoaDmGenerations = createRadiusServicePolicyForm.getAcctCOADMGenList();
			for(COADMGeneration  coaDMGeneration: acctCoaDmGenerations){
				CoADMGenerationHandlerData coaDMGenerationHandlerData = new CoADMGenerationHandlerData();
				
				if(coaDMGeneration.getIsAdditional().equals("false")){
					List<CoADMHandlerEntryData> coaDmHandlerEntryDatas = new ArrayList<CoADMHandlerEntryData>();
					List<COADMGenerationDetails> coaDMGenerationDetailsList = coaDMGeneration.getCoaDMGenerationDetailList();
					
					for(COADMGenerationDetails cdrDetails :coaDMGenerationDetailsList){
							CoADMHandlerEntryData coaDmHandlerEntryData = new CoADMHandlerEntryData();
							coaDmHandlerEntryData.setRuleset(cdrDetails.getRuleset());
							coaDmHandlerEntryData.setPacketType(cdrDetails.getPacketType());
							coaDmHandlerEntryData.setTranslationMapping(cdrDetails.getTranslationMapping());
						
							coaDmHandlerEntryDatas.add(coaDmHandlerEntryData);
						}
						coaDMGenerationHandlerData.getEntries().addAll(coaDmHandlerEntryDatas);
						coaDMGenerationHandlerData.setScheduleAfterInMillis(coaDMGeneration.getScheduleAfterInMillis());
						coaDMGenerationHandlerData.setEnabled(coaDMGeneration.getIsHandlerEnabled());
						coaDMGenerationHandlerData.setHandlerName(coaDMGeneration.getHandlerName());
						
						int orderNumber = coaDMGeneration.getOrderNumber();
						accountingHandlersMap.put(orderNumber, coaDMGenerationHandlerData);
				}else{
						List<CoADMHandlerEntryData> cdrHandlerEntryDatas = new ArrayList<CoADMHandlerEntryData>();
						List<COADMGenerationDetails> cdrGenerationDetails = coaDMGeneration.getCoaDMGenerationDetailList();
						
						for(COADMGenerationDetails cdrDetails :cdrGenerationDetails){
								CoADMHandlerEntryData coaDmHandlerEntryData = new CoADMHandlerEntryData();
								coaDmHandlerEntryData.setRuleset(cdrDetails.getRuleset());
								coaDmHandlerEntryData.setPacketType(cdrDetails.getPacketType());
								coaDmHandlerEntryData.setTranslationMapping(cdrDetails.getTranslationMapping());
								
								cdrHandlerEntryDatas.add(coaDmHandlerEntryData);
						}
						
						coaDMGenerationHandlerData.getEntries().addAll(cdrHandlerEntryDatas);
						coaDMGenerationHandlerData.setScheduleAfterInMillis(coaDMGeneration.getScheduleAfterInMillis());
						
						if( cdrHandlerEntryDatas!= null && cdrHandlerEntryDatas.size() > 0){
							coaDMGenerationHandlerData.setEnabled(coaDMGeneration.getIsHandlerEnabled());
							coaDMGenerationHandlerData.setHandlerName(coaDMGeneration.getHandlerName());
						}
						int orderNumber = coaDMGeneration.getOrderNumber();
						
						acctPostResponseHandlerMapData.put(orderNumber, coaDMGenerationHandlerData);
					}
				}
			
			/********END   : COADMGenerationHandlerData ***********/

			List<StatefulProxySequentialHandler> statefulProxySequentialHandlers = createRadiusServicePolicyForm.getStatefulProxySequentialHandlerAcctList();

			for (StatefulProxySequentialHandler statefulProxySequentialHandler:statefulProxySequentialHandlers) {
				StatefulProxySequentialHandlerData handlerData = new StatefulProxySequentialHandlerData();
				List<StatefulProHandlerData> sequentialHandlerEntryDatas = statefulProxySequentialHandler.getSequentialHandlerEntryData();

				List<StatefulProxyHandlerEntryData> statefulProxyHandlerEntryDataList = new ArrayList<>();

				for (StatefulProHandlerData entryData:sequentialHandlerEntryDatas) {
					StatefulProxyHandlerEntryData statefulProxyHandlerEntryData = new StatefulProxyHandlerEntryData();

					statefulProxyHandlerEntryData.setRuleset(entryData.getRuleset());
					statefulProxyHandlerEntryData.setServerGroupName(entryData.getServerGroupName());
					statefulProxyHandlerEntryData.setTranslationMappingName(entryData.getTranslationMappingName());
					statefulProxyHandlerEntryData.setScript(entryData.getScript());
					statefulProxyHandlerEntryData.setAcceptOnTimeout(entryData.getAcceptOnTimeout());

					statefulProxyHandlerEntryDataList.add(statefulProxyHandlerEntryData);
				}

				handlerData.setEnabled(statefulProxySequentialHandler.getIsHandlerEnabled());
				handlerData.setHandlerName(statefulProxySequentialHandler.getHandlerName());
				handlerData.getStatefulProxyHandlerEntryDataList().addAll(statefulProxyHandlerEntryDataList);

				accountingHandlersMap.put(statefulProxySequentialHandler.getOrderNumber(),handlerData);
			}

			List<StatefulProxyBroadcastHandler> statefulProxyBroadcastHandlers = createRadiusServicePolicyForm.getStatefulProxyBroadcastAcctHandlersList();
			for(StatefulProxyBroadcastHandler broadcastHandler : statefulProxyBroadcastHandlers){
				StatefulProxyBroadcastHandlerData handlerData = new StatefulProxyBroadcastHandlerData();

				List<StatefulProHandlerData> broadcastHandlerEntryDatas = broadcastHandler.getBroadcastHandlerEntryData();
				List<StatefulProxyBroadcastHandlerEntryData> statefulProxyBroadcastHandlerEntryDataList = new ArrayList<>();

				for (StatefulProHandlerData entryData : broadcastHandlerEntryDatas) {
					StatefulProxyBroadcastHandlerEntryData handlerEntryData = new StatefulProxyBroadcastHandlerEntryData();

					handlerEntryData.setRuleset(entryData.getRuleset());
					handlerEntryData.setServerGroupName(entryData.getServerGroupName());
					handlerEntryData.setTranslationMappingName(entryData.getTranslationMappingName());
					handlerEntryData.setScript(entryData.getScript());
					handlerEntryData.setAcceptOnTimeout(entryData.getAcceptOnTimeout());
					handlerEntryData.setWaitForResponse(entryData.getWaitForResponse());

					statefulProxyBroadcastHandlerEntryDataList.add(handlerEntryData);
				}
				handlerData.setEnabled(broadcastHandler.getIsHandlerEnabled());
				handlerData.setHandlerName(broadcastHandler.getHandlerName());
				handlerData.getStatefulProxyHandlerEntryDataList().addAll(statefulProxyBroadcastHandlerEntryDataList);

				accountingHandlersMap.put(broadcastHandler.getOrderNumber(),handlerData);
			}
			
			if( createRadiusServicePolicyForm.getAcctPrePluginData() != null && createRadiusServicePolicyForm.getAcctPrePluginData().size() > 0){
				
				List<PluginEntryDetail> pluginDataList = new ArrayList<PluginEntryDetail>();
				for (PolicyPluginData policyPluginData : createRadiusServicePolicyForm.getAcctPrePluginData()) {
					PluginEntryDetail preAndPostPluginData = new PluginEntryDetail();
					preAndPostPluginData.setPluginArgument(policyPluginData.getPluginArgument());
					preAndPostPluginData.setPluginName(policyPluginData.getPluginName());
					pluginDataList.add(preAndPostPluginData);
				}
				accountingPolicyData.setPrePluginDataList(pluginDataList);
			}
			
			if( createRadiusServicePolicyForm.getAcctPostPluginData() != null && createRadiusServicePolicyForm.getAcctPostPluginData().size() > 0){
				
				List<PluginEntryDetail> pluginDataList = new ArrayList<PluginEntryDetail>();
				for (PolicyPluginData policyPluginData : createRadiusServicePolicyForm.getAcctPostPluginData()) {
					PluginEntryDetail preAndPostPluginData = new PluginEntryDetail();
					preAndPostPluginData.setPluginArgument(policyPluginData.getPluginArgument());
					preAndPostPluginData.setPluginName(policyPluginData.getPluginName());
					pluginDataList.add(preAndPostPluginData);
				}
				accountingPolicyData.setPostPluginDataList(pluginDataList);
			}
			
			//Add Accounting Handler based on Order Number
			for (Map.Entry<Integer, Object> handlerDetails : accountingHandlersMap.entrySet()) {
				accountingPolicyData.getHandlersData().add((AcctServicePolicyHandlerData) (handlerDetails.getValue()));
			}
			
			for(Map.Entry<Integer, Object> postResponseHandler : acctPostResponseHandlerMapData.entrySet()){
				acctPostResponseHandlerData.getHandlersData().add((AcctServicePolicyHandlerData)(postResponseHandler.getValue()));
			}
			
			if(acctPostResponseHandlerData != null){
				accountingPolicyData.setPostResponseHandlerData(acctPostResponseHandlerData);
			}
			
			radiusServicePolicyData.setAccountingPolicyData(accountingPolicyData);
		}
		
		return radiusServicePolicyData;
	}

	private RadPluginHandlerData getRadiusPluginHandlerData(PluginHandler pluginHandler) {
		
		RadPluginHandlerData radPluginHandlerData = new RadPluginHandlerData();
		radPluginHandlerData.setEnabled(pluginHandler.getIsHandlerEnabled());
		
		List<PluginEntryData> pluginEntryDataList = new ArrayList<PluginEntryData>();
		
		if( pluginHandler.getLstPluginDetails() != null && pluginHandler.getLstPluginDetails().isEmpty() == false ){
			for(PluginDetails pluginDetail :  pluginHandler.getLstPluginDetails()){
				PluginEntryData pluginEntryData = new PluginEntryData();
				pluginEntryData.setRuleset(pluginDetail.getRuleset());
				pluginEntryData.setPluginName(pluginDetail.getPluginName());
				pluginEntryData.setOnResponse(pluginDetail.isRequestType());
				pluginEntryData.setPluginArgument(pluginDetail.getPluginArgument());
				
				pluginEntryDataList.add(pluginEntryData);
}
			radPluginHandlerData.getPluginEntries().addAll(pluginEntryDataList);
		}
		
		return radPluginHandlerData;
		
	}
}
