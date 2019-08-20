
package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import com.elitecore.aaa.core.conf.impl.ImdgConfigData;
import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.digestconf.DigestConfBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.inmemorydatagrid.InMemoryDataGridBLManager;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.inmemorydatagrid.data.InMemoryDataGridData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusEsiGroupConfigurationData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.*;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.*;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.radiusesigroup.RadiusEsiType;
import com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.UpdateRadiusServicePolicyForm;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateRadiusServicePolicyAuthServiceFlowAction extends BaseWebAction{
	private static final String UPDATE_FORWARD = "updateRadiusServicePolicyAuthServiceFlow";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE="UpdateAuthServicePolicyBasicDetailAction";
	private static final String ACTION_ALIAS=ConfigConstant.UPDATE_RADIUS_SERVICE_POLICY_AUTH_SERVICE_FLOW;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){		
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			UpdateRadiusServicePolicyForm updateRadiusServicePolicyForm = (UpdateRadiusServicePolicyForm)form;
			try{
				ServicePolicyBLManager servicePolicyBLManager = new ServicePolicyBLManager();
				SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
				EAPConfigBLManager eapConfigBLManager = new EAPConfigBLManager();
				DigestConfBLManager digestConfigBLManager = new DigestConfBLManager();
				GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
				
				List<ISessionManagerInstanceData> sessionManagerInstanceDataList = sessionManagerBLManager.getSessionManagerInstanceList();
				
				PluginBLManager pluginBLManager = new PluginBLManager();
				List<PluginInstData> prePluginList  = pluginBLManager.getAuthPluginList();
				request.setAttribute("prePluginList", prePluginList);
				
				DriverBLManager driverBlManager = new DriverBLManager();
				List<DriverInstanceData> driverList = driverBlManager.getDriverInstanceList(ServiceTypeConstants.AUTHENTICATION_SERVICE);
				updateRadiusServicePolicyForm.setDriversList(driverList);
				
				List<DriverInstanceData> cacheableDriverInstDataList = driverBlManager.getCacheableDriverData();
				updateRadiusServicePolicyForm.setCacheableDriverList(cacheableDriverInstDataList);
				
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
				
				TranslationMappingConfBLManager translationMappingConfBLManager=new TranslationMappingConfBLManager();
				List<TranslationMappingConfData> translationMappingConfDataList=translationMappingConfBLManager.getRadiusToRadiusTranslationMapping();
				
				CopyPacketTransMapConfBLManager copyPacketTranslationConfBLManager = new CopyPacketTransMapConfBLManager();
				List<CopyPacketTranslationConfData> copyPacketMappingConfDataList = copyPacketTranslationConfBLManager.getCopyPacketTransMapConfigList(TranslationMappingConfigConstants.RADIUS, TranslationMappingConfigConstants.RADIUS);
				
				
				ExternalSystemInterfaceBLManager externalSystemBLmanager=new ExternalSystemInterfaceBLManager();
				List<ExternalSystemInterfaceInstanceData> externalSystemInstanceList = new ArrayList<ExternalSystemInterfaceInstanceData>();
				List<ExternalSystemInterfaceInstanceData> nasESIList=new ArrayList<ExternalSystemInterfaceInstanceData>();
					
				externalSystemInstanceList = externalSystemBLmanager.getAllExternalSystemInstanceDataList();
				updateRadiusServicePolicyForm.setAuthBroadcastServerList(externalSystemInstanceList);	
				
				nasESIList = externalSystemBLmanager.getExternalSystemInstanceDataList(ExternalSystemConstants.NAS);
				updateRadiusServicePolicyForm.setDynaAuthRelDataList(nasESIList);
				
				List<DriverInstanceData> listOfAcctDriver = driverBlManager.getDriverInstanceList(ServiceTypeConstants.ACCOUNTING_SERVICE);
				List<DriverTypeData> driverTypeList=driverBlManager.getDriverTypeList(ServiceTypeConstants.ACCOUNTING_SERVICE);
				

				List<AuthMethodTypeData> authMethodTypeDataList = servicePolicyBLManager.getAuthMethodTypeList();
				List<DigestConfigInstanceData> digestConfigInstanceDataList = digestConfigBLManager.getDigestConfigInstanceList();
	            List<EAPConfigData> eapConfigurationList = eapConfigBLManager.getEapConfigurationList();
	            List<GracepolicyData> gracePolicyList = gracePolicyBLManager.getGracePolicyList();
	            
	            String[] selectedAuthMethodTypes = new String[authMethodTypeDataList.size()];
	            List<String> selectedAuthMethodTypesList = new ArrayList<String>(); 
	            for (int i = 0; i < authMethodTypeDataList.size(); i++) {
					long authTypeId=authMethodTypeDataList.get(i).getAuthMethodTypeId();
	            	if(authTypeId != 5){
	            		selectedAuthMethodTypesList.add(String.valueOf(authTypeId));
	            	}
	            	
				}
	            selectedAuthMethodTypes=selectedAuthMethodTypesList.toArray(new String[0]); 
	            
	            updateRadiusServicePolicyForm.setSelectedAuthMethodTypes(selectedAuthMethodTypes);
	            updateRadiusServicePolicyForm.setAuthMethodTypeDataList(authMethodTypeDataList);
	            updateRadiusServicePolicyForm.setDigestConfigInstanceDataList(digestConfigInstanceDataList);
	            updateRadiusServicePolicyForm.setEapConfigurationList(eapConfigurationList);
	            updateRadiusServicePolicyForm.setGracePolicyList(gracePolicyList);
				
				
				request.setAttribute("driverInstanceIds", driverInstanceIds);
				request.setAttribute("driverInstanceNames", driverInstanceNames);
				request.setAttribute("listOfDriver", driverList);
				request.setAttribute("listOfAcctDriver", listOfAcctDriver);
				request.setAttribute("translationMappingConfDataList", translationMappingConfDataList);
				request.setAttribute("copyPacketMappingConfDataList", copyPacketMappingConfDataList);
				request.setAttribute("driverTypeList", driverTypeList);
				updateRadiusServicePolicyForm.setSessionManagerInstanceDataList(sessionManagerInstanceDataList);
				updateRadiusServicePolicyForm.setTranslationMappingConfDataList(translationMappingConfDataList);
				updateRadiusServicePolicyForm.setCopyPacketMappingConfDataList(copyPacketMappingConfDataList);
				
				String action = updateRadiusServicePolicyForm.getAction();
				
				String strRadiusPolicyID = request.getParameter("radiusPolicyId");
				String radiusPolicyID;
				
				IStaffData staffData = getStaffObject((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm"));
				
				if(strRadiusPolicyID != null){
					radiusPolicyID = strRadiusPolicyID;
				}else{
					radiusPolicyID=updateRadiusServicePolicyForm.getRadiusPolicyId();
				}
				if(action==null || action.equals("")){
					if( Strings.isNullOrBlank(radiusPolicyID) == false){
						RadServicePolicyData radServicePolicyData = new RadServicePolicyData();
						
						updateRadiusServicePolicyForm.setSessionManagerInstanceDataList(sessionManagerInstanceDataList);
						
						radServicePolicyData = servicePolicyBLManager.getRadiusServicePolicyInstData(radiusPolicyID, staffData, ACTION_ALIAS);
						
 						String xmlDatas = new String(radServicePolicyData.getRadiusPolicyXml());
 						
 						StringReader stringReader =new StringReader(xmlDatas.trim());
						
 						//Convert into relevant POJO 
						JAXBContext context = JAXBContext.newInstance(RadiusServicePolicyData.class);
						Unmarshaller unmarshaller = context.createUnmarshaller();
						RadiusServicePolicyData radiusServicePolicyData = (RadiusServicePolicyData) unmarshaller.unmarshal(stringReader);
						
						convertXmlBeanToForm(radiusServicePolicyData,updateRadiusServicePolicyForm);

						//InMemoryDataGrid cofiguration
						imdgConfigurationData(updateRadiusServicePolicyForm);
						
						//CorrelatedRadius Group Names
						setRadiusEsiGroupData(updateRadiusServicePolicyForm);
						request.setAttribute("radServicePolicyData",radServicePolicyData);
						
						convertBeanToForm(radServicePolicyData,updateRadiusServicePolicyForm);
						
						System.out.println("XML DATA======================>");
						System.out.println(new String(radServicePolicyData.getRadiusPolicyXml()).toString());
						
						/* Driver Script and External Radius Script */
						ScriptBLManager scriptBLManager = new ScriptBLManager();
						List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
						List<ScriptInstanceData> externalScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.EXTERNAL_RADIUS_SCRIPT);
							
						updateRadiusServicePolicyForm.setDriverScriptList(driverScriptList);
						updateRadiusServicePolicyForm.setExternalScriptList(externalScriptList);
						
						request.getSession().setAttribute("updateRadiusServicePolicyForm", updateRadiusServicePolicyForm);
					}
				}else if(action!=null && action.equals("update")){

					String authenticationHandlerJson=updateRadiusServicePolicyForm.getAuthHandlerJson();
					String profileLookupDriverHanlderJson=updateRadiusServicePolicyForm.getProfileLookupDriverJson();
					String authorizationHandlerJson=updateRadiusServicePolicyForm.getAuthorizationHandlerJson();
					String concurrencyHandlerJson=updateRadiusServicePolicyForm.getConcurrencyHandlerJson();
					String cdrGenerationJson=updateRadiusServicePolicyForm.getCdrGenerationJson();
					String pluginHandlerJson=updateRadiusServicePolicyForm.getPluginHandlerJson();
					String coaDmGenerationJson=updateRadiusServicePolicyForm.getCoaDMGenerationJson();
					String proxyCommunicationJson=updateRadiusServicePolicyForm.getProxyCommunicationJson();
					String broacastCommunicationJson=updateRadiusServicePolicyForm.getBroadcastCommunicationJson();
					String concurrencyImdgHandlerJson=updateRadiusServicePolicyForm.getConcurrencyIMDGHandlerJson();
					String statefulProxySequentialHandlerJson=updateRadiusServicePolicyForm.getStatefulProxySequentialHandlerJson();
					String statefulProxyBroadcastHandlerJson = updateRadiusServicePolicyForm.getStatefulProxyBroadcastHandlerAuthJson();
					
					String authPrePluginJson  = updateRadiusServicePolicyForm.getAuthPrePluginJson();
					String authPostPluginJson = updateRadiusServicePolicyForm.getAuthPostPluginJson();
					
					List<AuthenticationHandler> authenticationHandlerList=new ArrayList<AuthenticationHandler>();
					
					if(authenticationHandlerJson != null && authenticationHandlerJson.length() > 0){
						 JSONArray authenticationHandlerArray = JSONArray.fromObject(authenticationHandlerJson);
						 for(Object  obj: authenticationHandlerArray){
							 AuthenticationHandler authenticationHandler = (AuthenticationHandler) JSONObject.toBean((JSONObject) obj, AuthenticationHandler.class);
							 authenticationHandlerList.add(authenticationHandler);
						 }
					}
					
					updateRadiusServicePolicyForm.setAuthHandlerList(authenticationHandlerList);
					
					List<ProfileLookupDriver> profileLookupDrivers=new ArrayList<ProfileLookupDriver>();
					
					if(profileLookupDriverHanlderJson != null && profileLookupDriverHanlderJson.length() > 0){
						 JSONArray profileDriverArray = JSONArray.fromObject(profileLookupDriverHanlderJson);
						 for(Object  obj: profileDriverArray){
							 ProfileLookupDriver profileLookupDriver = (ProfileLookupDriver) JSONObject.toBean((JSONObject) obj, ProfileLookupDriver.class);
							 profileLookupDrivers.add(profileLookupDriver);
						 }
					}
					
					updateRadiusServicePolicyForm.setProfileLookupList(profileLookupDrivers);
					
					List<AuthorizationHandler> authorizationHandlers=new ArrayList<AuthorizationHandler>();
					
					if(authorizationHandlerJson != null && authorizationHandlerJson.length() > 0){
						 JSONArray authorizationHandlerArray = JSONArray.fromObject(authorizationHandlerJson);
						 for(Object  obj: authorizationHandlerArray){
							 AuthorizationHandler authorizationHandler = (AuthorizationHandler) JSONObject.toBean((JSONObject) obj, AuthorizationHandler.class);
							 authorizationHandlers.add(authorizationHandler);
						 }
					}
					
					updateRadiusServicePolicyForm.setAuthorizationList(authorizationHandlers);
					
					List<ConcurrencyHandler> concurrencyHandlers=new ArrayList<ConcurrencyHandler>();
					
					if(concurrencyHandlerJson != null && concurrencyHandlerJson.length() > 0){
						 JSONArray concurrencyHandlerArray = JSONArray.fromObject(concurrencyHandlerJson);
						 for(Object  obj: concurrencyHandlerArray){
							 ConcurrencyHandler concurrencyHandler = (ConcurrencyHandler) JSONObject.toBean((JSONObject) obj, ConcurrencyHandler.class);
							 concurrencyHandlers.add(concurrencyHandler);
						 }
					}
					
					updateRadiusServicePolicyForm.setConcurrencyHandlerList(concurrencyHandlers);
					
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
					
					updateRadiusServicePolicyForm.setAuthCDRGenerationList(cdrGenerations);
					
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
					
					updateRadiusServicePolicyForm.setAuthPluginHandlerList(pluginHandlers);
					
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
					
					updateRadiusServicePolicyForm.setAuthCOADMGenList(coaDmGenerations);
					
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
					
					updateRadiusServicePolicyForm.setAuthProxyCommunicationList(proxyCommunications);
					
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
					
					updateRadiusServicePolicyForm.setAuthBroadcastCommunicationList(broadcastCommunications);

					List<ConcurrencyIMDGHandler> concurrencyIMDGHandlers=new ArrayList<ConcurrencyIMDGHandler>();

					if(Strings.isNullOrEmpty(concurrencyImdgHandlerJson) == false){
						JSONArray concurrencyImdgHandlerArray = JSONArray.fromObject(concurrencyImdgHandlerJson);
						for(Object  obj: concurrencyImdgHandlerArray){
							ConcurrencyIMDGHandler concurrencyImdgHandler = (ConcurrencyIMDGHandler) JSONObject.toBean((JSONObject) obj, ConcurrencyIMDGHandler.class);
							concurrencyIMDGHandlers.add(concurrencyImdgHandler);
						}
					}

					updateRadiusServicePolicyForm.setConcurrencyIMDGHandlerList(concurrencyIMDGHandlers);

					List<StatefulProxySequentialHandler> statefulProxySequentialHandlers = new ArrayList<>();

					if(Strings.isNullOrEmpty(statefulProxySequentialHandlerJson) == false){
						JSONArray handlerDataArray = JSONArray.fromObject(statefulProxySequentialHandlerJson);
						for (Object obj: handlerDataArray){
							Map<String,Class> configObj = new HashMap<String, Class>();
							configObj.put("sequentialHandlerEntryData", StatefulProHandlerData.class);
							StatefulProxySequentialHandler handler = (StatefulProxySequentialHandler) JSONObject.toBean((JSONObject) obj,StatefulProxySequentialHandler.class,configObj);
							statefulProxySequentialHandlers.add(handler);
						}
					}

                    List<StatefulProxyBroadcastHandler> statefulProxyBroadcastHandlers = new ArrayList<>();

                    if(Strings.isNullOrEmpty(statefulProxyBroadcastHandlerJson) == false){
                        JSONArray handlerDataArray = JSONArray.fromObject(statefulProxyBroadcastHandlerJson);
                        for (Object obj: handlerDataArray){
                            Map<String,Class> configObj = new HashMap<String, Class>();
                            configObj.put("broadcastHandlerEntryData", StatefulProHandlerData.class);
                            StatefulProxyBroadcastHandler handler = (StatefulProxyBroadcastHandler) JSONObject.toBean((JSONObject) obj,StatefulProxyBroadcastHandler.class,configObj);
                            statefulProxyBroadcastHandlers.add(handler);
                        }
                    }

					updateRadiusServicePolicyForm.setStatefulProxySequentialHandlerList(statefulProxySequentialHandlers);
                    updateRadiusServicePolicyForm.setStatefulProxyBroadcastHandlerAuthList(statefulProxyBroadcastHandlers);

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
					updateRadiusServicePolicyForm.setAuthPrePluginData(authPrePluginList);
					
					
					/* Auth Post Plugin */
					List<PolicyPluginData> authPostPluginList = new ArrayList<PolicyPluginData>();
					if(authPostPluginJson != null && authPostPluginJson.length() > 0){
						 JSONArray authPostPluginArray = JSONArray.fromObject(authPostPluginJson);
						 for(Object  obj: authPostPluginArray){
							
							 PolicyPluginData policyPluginData = (PolicyPluginData) JSONObject.toBean((JSONObject) obj, PolicyPluginData.class);
							 authPostPluginList.add(policyPluginData);
						 }
					}
					updateRadiusServicePolicyForm.setAuthPostPluginData(authPostPluginList);
					
					
					//update
					RadServicePolicyData radServicePolicyData = new RadServicePolicyData();

					radServicePolicyData = servicePolicyBLManager.getRadiusServicePolicyInstData(radiusPolicyID,staffData,ACTION_ALIAS);
					
					String xmlDatas = new String(radServicePolicyData.getRadiusPolicyXml());
						
					StringReader stringReader =new StringReader(xmlDatas.trim());
					
					JAXBContext context = JAXBContext.newInstance(RadiusServicePolicyData.class);
					Unmarshaller unmarshaller = context.createUnmarshaller();
					RadiusServicePolicyData radiusServicePolicyData = (RadiusServicePolicyData) unmarshaller.unmarshal(stringReader);
					
					radiusServicePolicyData = convertFormToBean(radiusServicePolicyData,updateRadiusServicePolicyForm);
					
				    servicePolicyBLManager.updateRadiusServicePolicyById(radiusServicePolicyData, staffData, ACTION_ALIAS);

					Logger.logDebug(MODULE, "RadServicePolicyData : "+radServicePolicyData);
	                request.setAttribute("responseUrl","/viewRadiusServicePolicy.do?radiusPolicyId="+radServicePolicyData.getRadiusPolicyId());
	                ActionMessage message = new ActionMessage("radiusservicepolicy.update.authflow.success");
	                ActionMessages messages = new ActionMessages();
	                messages.add("information", message);
	                saveMessages(request, messages);
	                return mapping.findForward(SUCCESS);
				}
				return mapping.findForward(UPDATE_FORWARD);
			}catch(DuplicateInstanceNameFoundException e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("radiusservicepolicy.update.authflow.failure",updateRadiusServicePolicyForm.getName());
			    ActionMessages messages = new ActionMessages();
			    messages.add("information", message);
			    saveErrors(request, messages);
			    
			}catch(Exception e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("radiusservicepolicy.update.authflow.failure");
			    ActionMessages messages = new ActionMessages();
			    messages.add("information", message);
			    saveErrors(request, messages);
			}
			return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	//This function is used to add Radius ESI Group data in UpdateRadiusServicePolicyForm
	private void setRadiusEsiGroupData(UpdateRadiusServicePolicyForm form) throws DataManagerException, JAXBException {
		RadiusESIGroupBLManager esiBlManager = new RadiusESIGroupBLManager();
		List<RadiusESIGroupData> radiusESIGroupDataList = esiBlManager.getRadiusESIGroupDataList();
		List<String> radiusEsiGroupNames = new ArrayList<>();

		if(Collectionz.isNullOrEmpty(radiusESIGroupDataList) == false){
			for (RadiusESIGroupData esiData:radiusESIGroupDataList) {
				String xmlDatas = new String(esiData.getEsiGroupDataXml());
				StringReader stringReader =new StringReader(xmlDatas.trim());

				RadiusEsiGroupConfigurationData esiConfigurationData = ConfigUtil.deserialize(stringReader, RadiusEsiGroupConfigurationData.class);
				if(RadiusEsiType.ACCT.name.equalsIgnoreCase(esiConfigurationData.getEsiType()) == false){
					radiusEsiGroupNames.add(esiData.getName());
				}
			}
		}
		form.setRadiusEsiGroupNames(radiusEsiGroupNames);
	}

	private void imdgConfigurationData(UpdateRadiusServicePolicyForm form) throws Exception {
		InMemoryDataGridBLManager inMemoryDataGridBLManager = new InMemoryDataGridBLManager();
		InMemoryDataGridData imdgData = inMemoryDataGridBLManager.getInMemoryDatagridConfiguration();
		ImdgConfigData existingIMDGData;
		if(imdgData == null){
			form.setImdgEnable(false);
		} else {
			String existingxmlDatas = new String(imdgData.getImdgXml());
			StringReader stringReader =new StringReader(existingxmlDatas.trim());

			//Convert into relevant POJO
			JAXBContext context = JAXBContext.newInstance(ImdgConfigData.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			existingIMDGData = (ImdgConfigData) unmarshaller.unmarshal(stringReader);

			if(existingIMDGData.isActive()){
				List<String> imdgFieldNames = new ArrayList<>();
				List<ImdgIndexDetail> imdgIndexDetailList =  existingIMDGData.getImdgRadiusConfig().getRadiusIMDGFieldMapping();

				for (ImdgIndexDetail imdgIndexDetail : imdgIndexDetailList) {
					imdgFieldNames.add(imdgIndexDetail.getImdgFieldValue());
				}
				form.setImdgEnable(true);
				form.setImdgFieldNames(imdgFieldNames);
			}else {
				form.setImdgEnable(false);
			}
		}
	}

	private void convertXmlBeanToForm(RadiusServicePolicyData radiusServicePolicyData,UpdateRadiusServicePolicyForm updateRadiusServicePolicyForm) {
		convertAuthData(radiusServicePolicyData,updateRadiusServicePolicyForm);
	}
	
	private void convertAuthData(RadiusServicePolicyData radiusServicePolicyData,UpdateRadiusServicePolicyForm updateRadiusServicePolicyForm) {
		AuthenticationPolicyData authenticationPolicyData = radiusServicePolicyData.getAuthenticationPolicyData();
		if(authenticationPolicyData == null){
			authenticationPolicyData = new AuthenticationPolicyData();
		}
		
		AuthPostResponseHandlerData authPostResponseHandlerData;
		if(radiusServicePolicyData.getAuthenticationPolicyData() != null){
			authPostResponseHandlerData = radiusServicePolicyData.getAuthenticationPolicyData().getPostResponseHandlerData();
		}else{
			authPostResponseHandlerData = new AuthPostResponseHandlerData();
		}
		
		List<AuthServicePolicyHandlerData> lstHandlerData =  authenticationPolicyData.getHandlersData();
		List<AuthServicePolicyHandlerData> lstPostResponseHandlerData = authPostResponseHandlerData.getHandlersData();
		
		List<ProfileLookupDriver> lstLookupDrivers = new ArrayList<ProfileLookupDriver>();
		List<ProxyCommunication> lstProxyCommunications = new ArrayList<ProxyCommunication>();
		List<BroadcastCommunication> lstBroadcastCommunication = new ArrayList<BroadcastCommunication>();
		List<AuthenticationHandler> lstAuthenticationHandlers = new ArrayList<AuthenticationHandler>();
		List<AuthorizationHandler> lstAuthorizationHandlers = new ArrayList<AuthorizationHandler>();
		List<PluginHandler> lstPluginHandlers =new ArrayList<PluginHandler>();
		List<ConcurrencyHandler> lstConcurrencyHandlers = new ArrayList<ConcurrencyHandler>();
		List<CDRGeneration> lstCdrGenerations = new ArrayList<CDRGeneration>();
		List<COADMGeneration> lstCOADMGeneration = new  ArrayList<COADMGeneration>();
		List<ConcurrencyIMDGHandler> lstConcurrencyIMDGHandler = new ArrayList<>();
		List<StatefulProxySequentialHandler> lstStatefulProxySequentialHandlers = new ArrayList<>();
		List<StatefulProxyBroadcastHandler> lstStatefulBroadcastHandler = new ArrayList<>();
		
		int orderNumber = 1;
		int postResponseOrderNumber = 1;
		
		for(AuthServicePolicyHandlerData authServicePolicyHandlerData : lstHandlerData){
			if(authServicePolicyHandlerData.getClass().equals(RadiusSubscriberProfileRepositoryDetails.class)){
				
				ProfileLookupDriver profileLookupDriver = new ProfileLookupDriver();

				RadiusSubscriberProfileRepositoryDetails radiusSubscriberProfileRepositoryDetails = (RadiusSubscriberProfileRepositoryDetails)authServicePolicyHandlerData;
				
				RadiusProfileDriversDetails radiusProfileDriver = radiusSubscriberProfileRepositoryDetails.getDriverDetails();
			
				//Get Primary Driver Data
				List<PrimaryDriverDetail> lstDriverDetails = radiusProfileDriver.getPrimaryDriverGroup();
				List<PrimaryDriverRelData> lstDriverRelDatas = new ArrayList<PrimaryDriverRelData>();
				
				for(PrimaryDriverDetail primaryDriverDetail : lstDriverDetails){
					PrimaryDriverRelData primaryDriverRelData = new PrimaryDriverRelData();
					primaryDriverRelData.setDriverInstanceId(primaryDriverDetail.getDriverInstanceId());
					primaryDriverRelData.setWeightage(primaryDriverDetail.getWeightage());
					lstDriverRelDatas.add(primaryDriverRelData);
				}
				
				//Get Secondary Driver Data
				
				List<SecondaryDriverRelData> driverRelDatas =  new ArrayList<SecondaryDriverRelData>();
				List<SecondaryAndCacheDriverDetail> cacheDriverDetails = radiusProfileDriver.getSecondaryDriverGroup();
				
				for(SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail :cacheDriverDetails ){
					SecondaryDriverRelData cacheDriverDetail = new SecondaryDriverRelData();
					cacheDriverDetail.setCacheDriverInstId(secondaryAndCacheDriverDetail.getCacheDriverId());
					cacheDriverDetail.setSecondaryDriverInstId(secondaryAndCacheDriverDetail.getSecondaryDriverId());
					
					driverRelDatas.add(cacheDriverDetail);
				}
				
				//Get Additional Driver Data
				
				List<AdditionalDriverDetail> additionalDriverDetails = radiusProfileDriver.getAdditionalDriverList();
				List<AdditionalDriverRelData> additionalDriverRelDatas = new ArrayList<AdditionalDriverRelData>();
				
				for(AdditionalDriverDetail additionalDriverDetail : additionalDriverDetails){
					AdditionalDriverRelData additionalDriverRelData = new AdditionalDriverRelData();
					additionalDriverRelData.setDriverInstanceId(additionalDriverDetail.getDriverId());
					additionalDriverRelDatas.add(additionalDriverRelData);
				}
				
				UpdateIdentityParamsDetail updateIdentityParamsDetail = radiusSubscriberProfileRepositoryDetails.getUpdateIdentity();
				
				profileLookupDriver.setStripIdentity(updateIdentityParamsDetail.getStripIdentity());
				profileLookupDriver.setSeparator(updateIdentityParamsDetail.getSeparator());
				profileLookupDriver.setTrimUserIdentity(updateIdentityParamsDetail.getTrimIdentity());
				profileLookupDriver.setTrimPassword(updateIdentityParamsDetail.getTrimPassword());
				profileLookupDriver.setSelectCase(updateIdentityParamsDetail.getIdetityFormat());
				profileLookupDriver.setPrimaryDriverRelDataList(lstDriverRelDatas);
				profileLookupDriver.setSecondaryDriverRelDataList(driverRelDatas);
				profileLookupDriver.setAdditionalDriverRelDataList(additionalDriverRelDatas);
				profileLookupDriver.setDriverScript(radiusProfileDriver.getDriverScript());
				profileLookupDriver.setAnonymousProfileIdentity(radiusSubscriberProfileRepositoryDetails.getAnonymousProfileIdentity());
				profileLookupDriver.setIsAdditional("false");
				profileLookupDriver.setOrderNumber(orderNumber);
				profileLookupDriver.setIsHandlerEnabled(radiusSubscriberProfileRepositoryDetails.getEnabled());
				profileLookupDriver.setHandlerName(radiusSubscriberProfileRepositoryDetails.getHandlerName());
				
				orderNumber++;
				
				lstLookupDrivers.add(profileLookupDriver);
				
			}else if(authServicePolicyHandlerData.getClass().equals(SynchronousCommunicationHandlerData.class)){
				ProxyCommunication proxyCommunication = new ProxyCommunication();
				List<ProxyCommunicationData> lstCommunicationDatas = new  ArrayList<ProxyCommunicationData>();
				
				SynchronousCommunicationHandlerData synchronousCommunicationHandlerData = (SynchronousCommunicationHandlerData)authServicePolicyHandlerData;
				List<ExternalCommunicationEntryData> lstCommunicationEntryDatas= synchronousCommunicationHandlerData.getProxyCommunicatioEntries();
				
				for(ExternalCommunicationEntryData communicationEntryData : lstCommunicationEntryDatas){
					ProxyCommunicationData communicationData = new ProxyCommunicationData();
					List<ESIServerData> esiServerDatas = new ArrayList<ESIServerData>();
					
					CommunicatorGroupData communicatorGroupData =communicationEntryData.getCommunicatorGroupData();
					List<CommunicatorData> lstCommunicatorData=communicatorGroupData.getCommunicatorDataList();
					
					for(CommunicatorData communicatorData : lstCommunicatorData){
						ESIServerData esiServerData = new ESIServerData();
						esiServerData.setEsiId(communicatorData.getId());
						esiServerData.setLoadFactor(communicatorData.getLoadFactor());
						esiServerDatas.add(esiServerData);
					}
					
					communicationData.setRuleset(communicationEntryData.getRuleset());
					communicationData.setAcceptOnTimeout(communicationEntryData.getAcceptOnTimeout());
					communicationData.setScript(communicationEntryData.getScript());
					if(communicationEntryData.getTranslationMapping()!= null){
						communicationData.setTranslationMappingName(communicationEntryData.getTranslationMapping());
					}
					communicationData.setEsiListData(esiServerDatas);
					
					lstCommunicationDatas.add(communicationData);
				}
				proxyCommunication.setProxyCommunicationList(lstCommunicationDatas);
				proxyCommunication.setOrderNumber(orderNumber);
				proxyCommunication.setIsHandlerEnabled(synchronousCommunicationHandlerData.getEnabled());
				proxyCommunication.setHandlerName(synchronousCommunicationHandlerData.getHandlerName());
				
				orderNumber++;
				
				proxyCommunication.setIsAdditional("false");
				
				lstProxyCommunications.add(proxyCommunication);
			}else if(authServicePolicyHandlerData.getClass().equals(BroadcastCommunicationHandlerData.class)){
				BroadcastCommunication broadcastCommunication = new BroadcastCommunication();
				List<BroadcastCommunicationData> lstCommunicationDatas = new  ArrayList<BroadcastCommunicationData>();
				
				BroadcastCommunicationHandlerData broadcastCommunicationHandlerData = (BroadcastCommunicationHandlerData)authServicePolicyHandlerData;
				List<AsyncCommunicationEntryData> lstCommunicationEntryDatas= broadcastCommunicationHandlerData.getProxyCommunicationEntries();
				
				for(AsyncCommunicationEntryData communicationEntryData : lstCommunicationEntryDatas){
					BroadcastCommunicationData communicationData = new BroadcastCommunicationData();
					List<ESIServerData> esiServerDatas = new ArrayList<ESIServerData>();
					
					CommunicatorGroupData communicatorGroupData =communicationEntryData.getCommunicatorGroupData();
					List<CommunicatorData> lstCommunicatorData=communicatorGroupData.getCommunicatorDataList();
					
					for(CommunicatorData communicatorData : lstCommunicatorData){
						ESIServerData esiServerData = new ESIServerData();
						esiServerData.setEsiId(communicatorData.getId());
						esiServerData.setLoadFactor(communicatorData.getLoadFactor());
						esiServerDatas.add(esiServerData);
					}
					
					communicationData.setRuleset(communicationEntryData.getRuleset());
					communicationData.setAcceptOnTimeout(communicationEntryData.getAcceptOnTimeout());
					communicationData.setScript(communicationEntryData.getScript());
					if(communicationEntryData.getTranslationMapping()!= null){
						communicationData.setTranslationMappingName(communicationEntryData.getTranslationMapping());
					}
					communicationData.setWaitForResponse(communicationEntryData.getWait());
					
					communicationData.setEsiListData(esiServerDatas);
					lstCommunicationDatas.add(communicationData);
				}
				broadcastCommunication.setBroadcastCommunicationList(lstCommunicationDatas);
				broadcastCommunication.setOrderNumber(orderNumber);
				broadcastCommunication.setIsHandlerEnabled(broadcastCommunicationHandlerData.getEnabled());
				broadcastCommunication.setHandlerName(broadcastCommunicationHandlerData.getHandlerName());
				orderNumber++;
				
				broadcastCommunication.setIsAdditional("false");
				
				lstBroadcastCommunication.add(broadcastCommunication);
			}else if(authServicePolicyHandlerData.getClass().equals(AuthenticationHandlerData.class)){
				AuthenticationHandler authenticationHandler=new AuthenticationHandler();
				AuthenticationHandlerData authenticationHandlerData = (AuthenticationHandlerData)authServicePolicyHandlerData;
				
				authenticationHandler.setDigestConfigId(authenticationHandlerData.getDigestConfigId());
				authenticationHandler.setEapConfigId(authenticationHandlerData.getEapConfigId());
				authenticationHandler.setIsAdditional("false");
				authenticationHandler.setOrderNumber(orderNumber);
				orderNumber++;
				
				authenticationHandler.setSelectedAuthMethodTypes(authenticationHandlerData.getSupportedMethods());
				authenticationHandler.setUserName(authenticationHandlerData.getUserName());
				authenticationHandler.setUserNameResponseAttribs(authenticationHandlerData.getUserNameResponseAttributeStr());
				authenticationHandler.setUserNameExpression(authenticationHandlerData.getUserNameExpression());
				authenticationHandler.setIsHandlerEnabled(authenticationHandlerData.getEnabled());
				authenticationHandler.setHandlerName(authenticationHandlerData.getHandlerName());
				
				lstAuthenticationHandlers.add(authenticationHandler);
				
			}else if(authServicePolicyHandlerData.getClass().equals(AuthorizationHandlerData.class)){
				AuthorizationHandlerData authorizationHandlerData =(AuthorizationHandlerData)authServicePolicyHandlerData;
				AuthorizationHandler authorizationHandler =new AuthorizationHandler();
				
				authorizationHandler.setGracePolicyId(authorizationHandlerData.getGracePolicy());
				authorizationHandler.setIsAdditional("false");
				authorizationHandler.setOrderNumber(orderNumber);
				authorizationHandler.setIsHandlerEnabled(authorizationHandlerData.getEnabled());
				authorizationHandler.setHandlerName(authorizationHandlerData.getHandlerName());
				orderNumber++;
				
				authorizationHandler.setDefaultSessionTimeout(authorizationHandlerData.getDefaultSessionTimeout());
				authorizationHandler.setThreeGPPEnabled(authorizationHandlerData.getThreeGPPEnabled());
				authorizationHandler.setWimaxEnabled(authorizationHandlerData.getWimaxEnabled());
				
				RadiusPolicyDetail radiusPolicyDetail = authorizationHandlerData.getRadiusPolicy();
				
				authorizationHandler.setActionOnPolicyNotFound(radiusPolicyDetail.getAcceptOnPolicyOnFound());
				authorizationHandler.setRejectOnCheckItemNotFound(radiusPolicyDetail.getRejectOnCheckItemNotFound());
				authorizationHandler.setRejectOnRejectItemNotFound(radiusPolicyDetail.getRejectOnRejectItemNotFound());
				
				lstAuthorizationHandlers.add(authorizationHandler);
				
			}else if(authServicePolicyHandlerData.getClass().equals(RadPluginHandlerData.class)){
				RadPluginHandlerData radPluginHandlerData = (RadPluginHandlerData)authServicePolicyHandlerData;
				PluginHandler pluginHandler = new PluginHandler();
				
				pluginHandler = getPluginDetailData(radPluginHandlerData);

				pluginHandler.setIsAdditional("false");
				pluginHandler.setOrderNumber(orderNumber);
				pluginHandler.setHandlerName(radPluginHandlerData.getHandlerName());
				orderNumber++;
				
				lstPluginHandlers.add(pluginHandler);
			}else if(authServicePolicyHandlerData.getClass().equals(ConcurrencyHandlerData.class)){
				ConcurrencyHandlerData concurrencyHandlerData = (ConcurrencyHandlerData)authServicePolicyHandlerData;
				ConcurrencyHandler concurrencyHandler = new ConcurrencyHandler();
				concurrencyHandler.setSessionManagerId(concurrencyHandlerData.getSessionManagerId());
				concurrencyHandler.setRuleset(concurrencyHandlerData.getRuleSet());
				concurrencyHandler.setIsAdditional("false");
				concurrencyHandler.setOrderNumber(orderNumber);
				concurrencyHandler.setIsHandlerEnabled(concurrencyHandlerData.getEnabled());
				concurrencyHandler.setHandlerName(concurrencyHandlerData.getHandlerName());
				orderNumber++;
				
				lstConcurrencyHandlers.add(concurrencyHandler);
			}else if(authServicePolicyHandlerData.getClass().equals(CdrGenerationHandlerData.class)){
				CdrGenerationHandlerData cdrGenerationHandlerData = (CdrGenerationHandlerData)authServicePolicyHandlerData;
				List<CdrHandlerEntryData> lstCdrHandlerEntryDatas = cdrGenerationHandlerData.getCdrHandlers();
				CDRGeneration cdrGeneration = new CDRGeneration();
				List<CDRGenerationDetails> lstCdrGenerationDetails = new ArrayList<CDRGenerationDetails>();
			
				for(CdrHandlerEntryData cdrHandlerEntryData:lstCdrHandlerEntryDatas){
					CDRGenerationDetails cdrGenerationDetails = new CDRGenerationDetails();
					
					cdrGenerationDetails.setRuleset(cdrHandlerEntryData.getRuleset());
					cdrGenerationDetails.setWaitForCDRDump(cdrHandlerEntryData.getWait());
					
					RadiusProfileDriversDetails radiusProfileDriver = cdrHandlerEntryData.getDriverDetails();
					
					//Get Primary Driver Data
					List<PrimaryDriverDetail> lstDriverDetails = radiusProfileDriver.getPrimaryDriverGroup();
					for(PrimaryDriverDetail primaryDriverDetail : lstDriverDetails){
						cdrGenerationDetails.setPrimaryDriverId(primaryDriverDetail.getDriverInstanceId());
					}
					
					//Get Secondary Driver Data
					
					List<SecondaryAndCacheDriverDetail> cacheDriverDetails = radiusProfileDriver.getSecondaryDriverGroup();
					for(SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail :cacheDriverDetails ){
						cdrGenerationDetails.setSecondaryDriverId(secondaryAndCacheDriverDetail.getSecondaryDriverId());
					}
					
					cdrGenerationDetails.setScript(radiusProfileDriver.getDriverScript());
					lstCdrGenerationDetails.add(cdrGenerationDetails);
				}
				
				cdrGeneration.setIsAdditional("false");
				cdrGeneration.setOrderNumber(orderNumber);
				cdrGeneration.setIsHandlerEnabled(cdrGenerationHandlerData.getEnabled());
				cdrGeneration.setHandlerName(cdrGenerationHandlerData.getHandlerName());
				orderNumber++;
				
				cdrGeneration.setCdrGenerationDetailsList(lstCdrGenerationDetails);
				
				lstCdrGenerations.add(cdrGeneration);
			}else if(authServicePolicyHandlerData.getClass().equals(CoADMGenerationHandlerData.class)){
				CoADMGenerationHandlerData coaDMGenerationHandlerData = (CoADMGenerationHandlerData)authServicePolicyHandlerData;
				List<CoADMHandlerEntryData> lstCoaDMHandlerEntryDatas = coaDMGenerationHandlerData.getEntries();
				COADMGeneration coaDMGeneration = new COADMGeneration();
				List<COADMGenerationDetails> lstCoaDMGenerationDetails = new ArrayList<COADMGenerationDetails>();
				
				for(CoADMHandlerEntryData coaDmHandlerEntryData:lstCoaDMHandlerEntryDatas){
					COADMGenerationDetails coaDMGenerationDetails = new COADMGenerationDetails();
					
					coaDMGenerationDetails.setPacketType(coaDmHandlerEntryData.getPacketType());
					coaDMGenerationDetails.setRuleset(coaDmHandlerEntryData.getRuleset());
					coaDMGenerationDetails.setTranslationMapping(coaDmHandlerEntryData.getTranslationMapping());
					
					lstCoaDMGenerationDetails.add(coaDMGenerationDetails);
				}
				
				coaDMGeneration.setIsAdditional("false");
				coaDMGeneration.setOrderNumber(orderNumber);
				coaDMGeneration.setIsHandlerEnabled(coaDMGenerationHandlerData.getEnabled());
				coaDMGeneration.setHandlerName(coaDMGenerationHandlerData.getHandlerName());
				
				orderNumber++;
				
				coaDMGeneration.setCoaDMGenerationDetailList(lstCoaDMGenerationDetails);
				coaDMGeneration.setScheduleAfterInMillis(coaDMGenerationHandlerData.getScheduleAfterInMillis());
				
				lstCOADMGeneration.add(coaDMGeneration);
			}else if(authServicePolicyHandlerData instanceof  ConcurrencyIMDGHandlerData){
				ConcurrencyIMDGHandlerData concurrencyIMDGHandlerData = (ConcurrencyIMDGHandlerData)authServicePolicyHandlerData;
				ConcurrencyIMDGHandler  concurrencyIMDGHandler = new ConcurrencyIMDGHandler();
				concurrencyIMDGHandler.setImdgFieldName(concurrencyIMDGHandlerData.getImdgFieldValue());
				concurrencyIMDGHandler.setRuleset(concurrencyIMDGHandlerData.getRuleSet());
				concurrencyIMDGHandler.setIsAdditional("false");
				concurrencyIMDGHandler.setOrderNumber(orderNumber);
				concurrencyIMDGHandler.setIsHandlerEnabled(concurrencyIMDGHandlerData.getEnabled());
				concurrencyIMDGHandler.setHandlerName(concurrencyIMDGHandlerData.getHandlerName());
				orderNumber++;

				lstConcurrencyIMDGHandler.add(concurrencyIMDGHandler);
			}else if (authServicePolicyHandlerData instanceof StatefulProxySequentialHandlerData){
				StatefulProxySequentialHandlerData handlerData = (StatefulProxySequentialHandlerData) authServicePolicyHandlerData;

				List<StatefulProxyHandlerEntryData> statefulProxyHandlerEntryDataList = handlerData.getStatefulProxyHandlerEntryDataList();
				List<StatefulProHandlerData> statefulProHandlerDatas = new ArrayList<>();

				for (StatefulProxyHandlerEntryData handlerEntryData:statefulProxyHandlerEntryDataList) {

					StatefulProHandlerData statefulProHandlerData = new StatefulProHandlerData();

					statefulProHandlerData.setRuleset(handlerEntryData.getRuleset());
					statefulProHandlerData.setServerGroupName(handlerEntryData.getServerGroupName());
					statefulProHandlerData.setTranslationMappingName(handlerEntryData.getTranslationMappingName());
					statefulProHandlerData.setScript(handlerEntryData.getScript());
					statefulProHandlerData.setAcceptOnTimeout(handlerEntryData.getAcceptOnTimeout());

					statefulProHandlerDatas.add(statefulProHandlerData);
				}

				StatefulProxySequentialHandler statefulProxySequentialHandler = new StatefulProxySequentialHandler();
				statefulProxySequentialHandler.getSequentialHandlerEntryData().addAll(statefulProHandlerDatas);
				statefulProxySequentialHandler.setHandlerName(handlerData.getHandlerName());
				statefulProxySequentialHandler.setIsHandlerEnabled(handlerData.getEnabled());
				statefulProxySequentialHandler.setOrderNumber(orderNumber);
				statefulProxySequentialHandler.setIsAdditional("false");
				orderNumber++;

				lstStatefulProxySequentialHandlers.add(statefulProxySequentialHandler);
			}else if (authServicePolicyHandlerData instanceof StatefulProxyBroadcastHandlerData){
                StatefulProxyBroadcastHandlerData handlerData = (StatefulProxyBroadcastHandlerData) authServicePolicyHandlerData;

                List<StatefulProxyBroadcastHandlerEntryData> statefulProxyHandlerEntryDataList = handlerData.getStatefulProxyHandlerEntryDataList();
                List<StatefulProHandlerData> statefulProHandlerDatas = new ArrayList<>();

                for (StatefulProxyBroadcastHandlerEntryData handlerEntryData:statefulProxyHandlerEntryDataList) {

                    StatefulProHandlerData statefulProHandlerData = new StatefulProHandlerData();

                    statefulProHandlerData.setRuleset(handlerEntryData.getRuleset());
                    statefulProHandlerData.setServerGroupName(handlerEntryData.getServerGroupName());
                    statefulProHandlerData.setTranslationMappingName(handlerEntryData.getTranslationMappingName());
                    statefulProHandlerData.setScript(handlerEntryData.getScript());
                    statefulProHandlerData.setAcceptOnTimeout(handlerEntryData.getAcceptOnTimeout());
                    statefulProHandlerData.setWaitForResponse(handlerEntryData.getWaitForResponse());

                    statefulProHandlerDatas.add(statefulProHandlerData);
                }

                StatefulProxyBroadcastHandler statefulProxyBroadcastHandler = new StatefulProxyBroadcastHandler();
                statefulProxyBroadcastHandler.getBroadcastHandlerEntryData().addAll(statefulProHandlerDatas);
                statefulProxyBroadcastHandler.setHandlerName(handlerData.getHandlerName());
                statefulProxyBroadcastHandler.setIsHandlerEnabled(handlerData.getEnabled());
                statefulProxyBroadcastHandler.setOrderNumber(orderNumber);
                statefulProxyBroadcastHandler.setIsAdditional("false");
                orderNumber++;

                lstStatefulBroadcastHandler.add(statefulProxyBroadcastHandler);
            }
		}
		
		if(lstPostResponseHandlerData != null && !(lstPostResponseHandlerData.isEmpty())){
			for(AuthServicePolicyHandlerData authServicePolicyHandlerData : lstPostResponseHandlerData){
				if(authServicePolicyHandlerData.getClass().equals(CdrGenerationHandlerData.class)){
					CdrGenerationHandlerData cdrGenerationHandlerData = (CdrGenerationHandlerData)authServicePolicyHandlerData;
					List<CdrHandlerEntryData> lstCdrHandlerEntryDatas = cdrGenerationHandlerData.getCdrHandlers();
					CDRGeneration cdrGeneration = new CDRGeneration();
					List<CDRGenerationDetails> lstCdrGenerationDetails = new ArrayList<CDRGenerationDetails>();
				
					for(CdrHandlerEntryData cdrHandlerEntryData:lstCdrHandlerEntryDatas){
						CDRGenerationDetails cdrGenerationDetails = new CDRGenerationDetails();
						
						cdrGenerationDetails.setRuleset(cdrHandlerEntryData.getRuleset());
						cdrGenerationDetails.setWaitForCDRDump(cdrHandlerEntryData.getWait());
						
						RadiusProfileDriversDetails radiusProfileDriver = cdrHandlerEntryData.getDriverDetails();
						
						//Get Primary Driver Data
						List<PrimaryDriverDetail> lstDriverDetails = radiusProfileDriver.getPrimaryDriverGroup();
						for(PrimaryDriverDetail primaryDriverDetail : lstDriverDetails){
							cdrGenerationDetails.setPrimaryDriverId(primaryDriverDetail.getDriverInstanceId());
						}
						
						//Get Secondary Driver Data
						
						List<SecondaryAndCacheDriverDetail> cacheDriverDetails = radiusProfileDriver.getSecondaryDriverGroup();
						for(SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail :cacheDriverDetails ){
							cdrGenerationDetails.setSecondaryDriverId(secondaryAndCacheDriverDetail.getSecondaryDriverId());
						}
						
						cdrGenerationDetails.setScript(radiusProfileDriver.getDriverScript());
						lstCdrGenerationDetails.add(cdrGenerationDetails);
					}
					
					cdrGeneration.setIsAdditional("true");
					cdrGeneration.setOrderNumber(postResponseOrderNumber);
					cdrGeneration.setIsHandlerEnabled(cdrGenerationHandlerData.getEnabled());
					cdrGeneration.setHandlerName(cdrGenerationHandlerData.getHandlerName());
					postResponseOrderNumber++;
					
					cdrGeneration.setCdrGenerationDetailsList(lstCdrGenerationDetails);
					
					lstCdrGenerations.add(cdrGeneration);
				}else if(authServicePolicyHandlerData.getClass().equals(CoADMGenerationHandlerData.class)){
					CoADMGenerationHandlerData coaDMGenerationHandlerData = (CoADMGenerationHandlerData)authServicePolicyHandlerData;
					List<CoADMHandlerEntryData> lstCoaDMHandlerEntryDatas = coaDMGenerationHandlerData.getEntries();
					COADMGeneration coaDMGeneration = new COADMGeneration();
					List<COADMGenerationDetails> lstCoaDMGenerationDetails = new ArrayList<COADMGenerationDetails>();
					
					for(CoADMHandlerEntryData coaDmHandlerEntryData:lstCoaDMHandlerEntryDatas){
						COADMGenerationDetails coaDMGenerationDetails = new COADMGenerationDetails();
						
						coaDMGenerationDetails.setPacketType(coaDmHandlerEntryData.getPacketType());
						coaDMGenerationDetails.setRuleset(coaDmHandlerEntryData.getRuleset());
						coaDMGenerationDetails.setTranslationMapping(coaDmHandlerEntryData.getTranslationMapping());
						
						lstCoaDMGenerationDetails.add(coaDMGenerationDetails);
					}
					
					coaDMGeneration.setIsAdditional("true");
					coaDMGeneration.setOrderNumber(postResponseOrderNumber);
					coaDMGeneration.setIsHandlerEnabled(coaDMGenerationHandlerData.getEnabled());
					coaDMGeneration.setHandlerName(coaDMGenerationHandlerData.getHandlerName());
					
					postResponseOrderNumber++;
					
					coaDMGeneration.setCoaDMGenerationDetailList(lstCoaDMGenerationDetails);
					coaDMGeneration.setScheduleAfterInMillis(coaDMGenerationHandlerData.getScheduleAfterInMillis());
					lstCOADMGeneration.add(coaDMGeneration);
				}else if(authServicePolicyHandlerData.getClass().equals(RadPluginHandlerData.class)){
					RadPluginHandlerData radPluginHandlerData = (RadPluginHandlerData)authServicePolicyHandlerData;
					PluginHandler pluginHandler = new PluginHandler();
					
					pluginHandler = getPluginDetailData(radPluginHandlerData);
					
					pluginHandler.setIsAdditional("true");
					pluginHandler.setOrderNumber(postResponseOrderNumber);
					pluginHandler.setHandlerName(radPluginHandlerData.getHandlerName());
					
					postResponseOrderNumber++;
					
					lstPluginHandlers.add(pluginHandler);
				}
			}
		}
		
		updateRadiusServicePolicyForm.setProfileLookupList(lstLookupDrivers);
		updateRadiusServicePolicyForm.setAuthProxyCommunicationList(lstProxyCommunications);
		updateRadiusServicePolicyForm.setAuthBroadcastCommunicationList(lstBroadcastCommunication);
		updateRadiusServicePolicyForm.setAuthHandlerList(lstAuthenticationHandlers);
		updateRadiusServicePolicyForm.setAuthorizationList(lstAuthorizationHandlers);
		updateRadiusServicePolicyForm.setAuthPluginHandlerList(lstPluginHandlers);
		updateRadiusServicePolicyForm.setConcurrencyHandlerList(lstConcurrencyHandlers);
		updateRadiusServicePolicyForm.setAuthCDRGenerationList(lstCdrGenerations);
		updateRadiusServicePolicyForm.setAuthCOADMGenList(lstCOADMGeneration);
		updateRadiusServicePolicyForm.setConcurrencyIMDGHandlerList(lstConcurrencyIMDGHandler);
		updateRadiusServicePolicyForm.setStatefulProxySequentialHandlerList(lstStatefulProxySequentialHandlers);
		updateRadiusServicePolicyForm.setStatefulProxyBroadcastHandlerAuthList(lstStatefulBroadcastHandler);
		
		List<PluginEntryDetail> prePluginDataList = authenticationPolicyData.getPrePluginDataList();
		List<PolicyPluginData> policyPrePluginDataList = new ArrayList<PolicyPluginData>();
		
		if( prePluginDataList != null && prePluginDataList.isEmpty() == false ){
			for(PluginEntryDetail preAndPostEntryData : prePluginDataList){
				PolicyPluginData policyPluginData = new PolicyPluginData();
				policyPluginData.setPluginArgument(preAndPostEntryData.getPluginArgument());
				policyPluginData.setPluginName(preAndPostEntryData.getPluginName());
				
				policyPrePluginDataList.add(policyPluginData);
			}
		}
		
		updateRadiusServicePolicyForm.setAuthPrePluginData(policyPrePluginDataList);
		
		List<PluginEntryDetail> postPluginEntryDataList = authenticationPolicyData.getPostPluginDataList();
		List<PolicyPluginData> policyPostPluginDataList = new ArrayList<PolicyPluginData>();
		
		if( postPluginEntryDataList != null && postPluginEntryDataList.isEmpty() == false ){
			for(PluginEntryDetail preAndPostEntryData : postPluginEntryDataList){
				PolicyPluginData policyPluginData = new PolicyPluginData();
				policyPluginData.setPluginArgument(preAndPostEntryData.getPluginArgument());
				policyPluginData.setPluginName(preAndPostEntryData.getPluginName());
					
				policyPostPluginDataList.add(policyPluginData);
			}
		}
		updateRadiusServicePolicyForm.setAuthPostPluginData(policyPostPluginDataList);
	}
	
	private RadiusServicePolicyData convertFormToBean(RadiusServicePolicyData radiusServicePolicyData,UpdateRadiusServicePolicyForm updateRadiusServicePolicyForm) {
		radiusServicePolicyData.setPolicyId(updateRadiusServicePolicyForm.getRadiusPolicyId());
		
		/*************************************************************************************/
		/*							AUTHENTICATION POLICY									 */
		/*************************************************************************************/
		
		AuthenticationPolicyData authenticationPolicyData = new AuthenticationPolicyData();
		Map<Integer, Object> authenticationHandlersMap=new  HashMap<Integer, Object>();
		
		AuthPostResponseHandlerData authPostResponseHandlerData = new AuthPostResponseHandlerData();
		Map<Integer,Object> authPostResponseHandlerMapData = new  HashMap<Integer, Object>();
		
		
		/********** START :  Profile Lookup Driver *********/
		List<ProfileLookupDriver> profileLookupDrivers = updateRadiusServicePolicyForm.getProfileLookupList();
		
		if(profileLookupDrivers != null && profileLookupDrivers.size() > 0){
			for(ProfileLookupDriver profileLookupDriver : profileLookupDrivers){
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
				authenticationHandlersMap.put(orderNumber, radiusProfileRepositoryDetails);
			}
		}
		
		/********END : Profile Lookup Driver********/
		
		/********START : AuthenticationHandlerData********/
		List<AuthenticationHandler> authenticationHandlerList = updateRadiusServicePolicyForm.getAuthHandlerList();
		
		if(authenticationHandlerList !=null && authenticationHandlerList.size() > 0){
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
		}
		/********END : AuthenticationHandlerData********/
		
		/********START : AuthorizationHandlerData ********/
		List<AuthorizationHandler> authorizationHandlerList = updateRadiusServicePolicyForm.getAuthorizationList();
		
		if(authorizationHandlerList != null && authorizationHandlerList.size() > 0){
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
		}
		/********END : AuthorizationHandlerData ********/
		
		/********START :ConcurrencyHandler **************/
		List<ConcurrencyHandler> concurrencyHandlers = updateRadiusServicePolicyForm.getConcurrencyHandlerList();
		
		if(concurrencyHandlers != null && concurrencyHandlers.size() > 0){
			for(ConcurrencyHandler concurrencyHandler : concurrencyHandlers){
				ConcurrencyHandlerData concurrencyHandlerData = new ConcurrencyHandlerData();
				concurrencyHandlerData.setSessionManagerId(concurrencyHandler.getSessionManagerId());
				concurrencyHandlerData.setRuleSet(concurrencyHandler.getRuleset());
				concurrencyHandlerData.setEnabled(concurrencyHandler.getIsHandlerEnabled());
				concurrencyHandlerData.setHandlerName(concurrencyHandler.getHandlerName());
				
				int orderNumber = concurrencyHandler.getOrderNumber();
				authenticationHandlersMap.put(orderNumber, concurrencyHandlerData);
			}
		}
		/********END : ConcurrencyHandler **************/
		
		/********START : RadPluginHandlerData ***********/
		List<PluginHandler> pluginHandlers = updateRadiusServicePolicyForm.getAuthPluginHandlerList();
		
		if(pluginHandlers != null && pluginHandlers.size() > 0){
			for(PluginHandler pluginHandler : pluginHandlers){
				RadPluginHandlerData radPluginHandlerData=new RadPluginHandlerData();
				
				radPluginHandlerData = getRadiusPluginHandlerData(pluginHandler);
				int orderNumber = pluginHandler.getOrderNumber();
				
				radPluginHandlerData.setHandlerName(pluginHandler.getHandlerName());
				
				if(pluginHandler.getIsAdditional().equals("false")){
					authenticationHandlersMap.put(orderNumber, radPluginHandlerData);
				}else {
					authPostResponseHandlerMapData.put(orderNumber, radPluginHandlerData);
				}
			}
		}
		
		/********END : RadPluginHandlerData ***********/
		
		/********START : ProxyCommunicationHandlerData ***********/
		
		List<ProxyCommunication> proxyCommunicationsList = updateRadiusServicePolicyForm.getAuthProxyCommunicationList();

		if(proxyCommunicationsList != null && proxyCommunicationsList.size() > 0){
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
		}
	

		/********END   : SynchronousCommunicationHandlerData ***********/
		
		/********START : BroadcastCommunicationHandlerData ***********/
		List<BroadcastCommunication> broadcastCommunicationList = updateRadiusServicePolicyForm.getAuthBroadcastCommunicationList();

		if(broadcastCommunicationList != null && broadcastCommunicationList.size() > 0){
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
		}
		
		/********END   : BroadcastCommunicationHandlerData ***********/
		
		/********START : CdrGenerationHandlerData ***********/
		List<CDRGeneration> authCdrGenerations = updateRadiusServicePolicyForm.getAuthCDRGenerationList();
		
		if(authCdrGenerations != null && authCdrGenerations.size() > 0){
			for(CDRGeneration  cdrGeneration: authCdrGenerations){
				CdrGenerationHandlerData cdrGenerationHandlerData = new CdrGenerationHandlerData();
				
				if(cdrGeneration.getIsAdditional().equals("false")){
					List<CdrHandlerEntryData> cdrHandlerEntryDatas = new ArrayList<CdrHandlerEntryData>();
					List<CDRGenerationDetails> cdrGenerationDetails = cdrGeneration.getCdrGenerationDetailsList();
					
					for(CDRGenerationDetails cdrGenerationDetail:cdrGenerationDetails){
						CdrHandlerEntryData cdrHandlerEntryData = new CdrHandlerEntryData();
						cdrHandlerEntryData.setRuleset(cdrGenerationDetail.getRuleset());
						cdrHandlerEntryData.setWait(cdrGenerationDetail.getWaitForCDRDump());
						RadiusProfileDriversDetails radiusProfileDriversDetails =new RadiusProfileDriversDetails();
						
						//Set Primary Driver Data
						List<PrimaryDriverDetail> primaryDriverDetailsList =new ArrayList<PrimaryDriverDetail>();
						PrimaryDriverDetail primaryDriverDetail = new PrimaryDriverDetail();
						primaryDriverDetail.setDriverInstanceId(cdrGenerationDetail.getPrimaryDriverId());
						primaryDriverDetail.setWeightage(1);
						
						primaryDriverDetailsList.add(primaryDriverDetail);
						radiusProfileDriversDetails.setPrimaryDriverGroup(primaryDriverDetailsList);
						
						//Set Secondary Driver Data
						List<SecondaryAndCacheDriverDetail> secondaryDriverDetailsList =new ArrayList<SecondaryAndCacheDriverDetail>();
						SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail = new SecondaryAndCacheDriverDetail();
						secondaryAndCacheDriverDetail.setSecondaryDriverId(cdrGenerationDetail.getSecondaryDriverId());
						secondaryAndCacheDriverDetail.setCacheDriverId("0");
						
						secondaryDriverDetailsList.add(secondaryAndCacheDriverDetail);
						
						radiusProfileDriversDetails.setSecondaryDriverGroup(secondaryDriverDetailsList);
						radiusProfileDriversDetails.setDriverScript(cdrGenerationDetail.getScript());

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
						cdrGenerationHandlerData.setEnabled(cdrGeneration.getIsHandlerEnabled());
						cdrGenerationHandlerData.setHandlerName(cdrGeneration.getHandlerName());
						
						int orderNumber = cdrGeneration.getOrderNumber();
						authPostResponseHandlerMapData.put(orderNumber, cdrGenerationHandlerData);
				}
			}
		}
		
		/********END   : CdrGenerationHandlerData ***********/
		
		/********START   : COADMGenerationHandlerData ***********/
		List<COADMGeneration> authCoaDmGenerations = updateRadiusServicePolicyForm.getAuthCOADMGenList();
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
				coaDMGenerationHandlerData.setEnabled(coaDMGeneration.getIsHandlerEnabled());
				coaDMGenerationHandlerData.setHandlerName(coaDMGeneration.getHandlerName());
				
				int orderNumber = coaDMGeneration.getOrderNumber();
				
				authPostResponseHandlerMapData.put(orderNumber, coaDMGenerationHandlerData);
			}
		}
		
		/********END   : COADMGenerationHandlerData ***********/

		/********START :ConcurrencyImdgHandler **************/
		List<ConcurrencyIMDGHandler> concurrencyImdgHandlers = updateRadiusServicePolicyForm.getConcurrencyIMDGHandlerList();

		if(Collectionz.isNullOrEmpty(concurrencyImdgHandlers) == false){
			for(ConcurrencyIMDGHandler concurrencyImdgHandler : concurrencyImdgHandlers){
				ConcurrencyIMDGHandlerData concurrencyImdgHandlerData = new ConcurrencyIMDGHandlerData();
				concurrencyImdgHandlerData.setImdgFieldValue(concurrencyImdgHandler.getImdgFieldName());
				concurrencyImdgHandlerData.setRuleSet(concurrencyImdgHandler.getRuleset());
				concurrencyImdgHandlerData.setEnabled(concurrencyImdgHandler.getIsHandlerEnabled());
				concurrencyImdgHandlerData.setHandlerName(concurrencyImdgHandler.getHandlerName());

				authenticationHandlersMap.put(concurrencyImdgHandler.getOrderNumber(), concurrencyImdgHandlerData);
			}
		}

		/********END : ConcurrencyImdgHandler **************/

		List<StatefulProxySequentialHandler> statefulProxySequentialHandlers = updateRadiusServicePolicyForm.getStatefulProxySequentialHandlerList();

		if(Collectionz.isNullOrEmpty(statefulProxySequentialHandlers) == false){

			for (StatefulProxySequentialHandler handlerData:statefulProxySequentialHandlers) {
				StatefulProxySequentialHandlerData statefulProxySequentialHandlerData = new StatefulProxySequentialHandlerData();
				List<StatefulProxyHandlerEntryData> statefulProxyHandlerEntryDataList = new ArrayList<>();

				for (StatefulProHandlerData entryData:handlerData.getSequentialHandlerEntryData()) {

					StatefulProxyHandlerEntryData statefulProxyHandlerEntryData = new StatefulProxyHandlerEntryData();

					statefulProxyHandlerEntryData.setRuleset(entryData.getRuleset());
					statefulProxyHandlerEntryData.setServerGroupName(entryData.getServerGroupName());
					statefulProxyHandlerEntryData.setTranslationMappingName(entryData.getTranslationMappingName());
					statefulProxyHandlerEntryData.setScript(entryData.getScript());
					statefulProxyHandlerEntryData.setAcceptOnTimeout(entryData.getAcceptOnTimeout());

					statefulProxyHandlerEntryDataList.add(statefulProxyHandlerEntryData);
				}
				statefulProxySequentialHandlerData.setHandlerName(handlerData.getHandlerName());
				statefulProxySequentialHandlerData.setEnabled(handlerData.getIsHandlerEnabled());
				statefulProxySequentialHandlerData.getStatefulProxyHandlerEntryDataList().addAll(statefulProxyHandlerEntryDataList);

				authenticationHandlersMap.put(handlerData.getOrderNumber(), statefulProxySequentialHandlerData);
			}
		}

		/********END : Stateful Proxy Sequential Handler **************/

        List<StatefulProxyBroadcastHandler> statefulProxyBroadcastHandlers = updateRadiusServicePolicyForm.getStatefulProxyBroadcastHandlerAuthList();

        if(Collectionz.isNullOrEmpty(statefulProxyBroadcastHandlers) == false){

            for (StatefulProxyBroadcastHandler handlerData:statefulProxyBroadcastHandlers) {
                StatefulProxyBroadcastHandlerData statefulProxyBroadcastHandlerData = new StatefulProxyBroadcastHandlerData();
                List<StatefulProxyBroadcastHandlerEntryData> statefulProxyHandlerEntryDataList = new ArrayList<>();

                for (StatefulProHandlerData entryData:handlerData.getBroadcastHandlerEntryData()) {

                    StatefulProxyBroadcastHandlerEntryData handlerEntryData = new StatefulProxyBroadcastHandlerEntryData();

                    handlerEntryData.setRuleset(entryData.getRuleset());
                    handlerEntryData.setServerGroupName(entryData.getServerGroupName());
                    handlerEntryData.setTranslationMappingName(entryData.getTranslationMappingName());
                    handlerEntryData.setScript(entryData.getScript());
                    handlerEntryData.setAcceptOnTimeout(entryData.getAcceptOnTimeout());
                    handlerEntryData.setWaitForResponse(entryData.getWaitForResponse());

                    statefulProxyHandlerEntryDataList.add(handlerEntryData);
                }
                statefulProxyBroadcastHandlerData.setHandlerName(handlerData.getHandlerName());
                statefulProxyBroadcastHandlerData.setEnabled(handlerData.getIsHandlerEnabled());
                statefulProxyBroadcastHandlerData.getStatefulProxyHandlerEntryDataList().addAll(statefulProxyHandlerEntryDataList);

                authenticationHandlersMap.put(handlerData.getOrderNumber(), statefulProxyBroadcastHandlerData);
            }
        }

        /********END : Stateful Proxy Broadcast Handler **************/

		if( updateRadiusServicePolicyForm.getAuthPrePluginData() != null && updateRadiusServicePolicyForm.getAuthPrePluginData().size() > 0){
			
			List<PluginEntryDetail> pluginDataList = new ArrayList<PluginEntryDetail>();
			for (PolicyPluginData policyPluginData : updateRadiusServicePolicyForm.getAuthPrePluginData()) {
				PluginEntryDetail preAndPostPluginData = new PluginEntryDetail();
				preAndPostPluginData.setPluginArgument(policyPluginData.getPluginArgument());
				preAndPostPluginData.setPluginName(policyPluginData.getPluginName());
				pluginDataList.add(preAndPostPluginData);
			}
			authenticationPolicyData.setPrePluginDataList(pluginDataList);
		}
		
		if( updateRadiusServicePolicyForm.getAuthPostPluginData() != null && updateRadiusServicePolicyForm.getAuthPostPluginData().size() > 0){
			
			List<PluginEntryDetail> pluginDataList = new ArrayList<PluginEntryDetail>();
			for (PolicyPluginData policyPluginData : updateRadiusServicePolicyForm.getAuthPostPluginData()) {
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
		
		return radiusServicePolicyData;
	}

	private void convertBeanToForm(RadServicePolicyData data,UpdateRadiusServicePolicyForm form) throws Exception {
		if(data!=null && form!=null){
			form.setName(data.getName());
			form.setDescription(data.getDescription());
			form.setStatus(data.getStatus());
			form.setAuthentication(Boolean.parseBoolean(data.getAuthMsg()));
			form.setAccounting(Boolean.parseBoolean(data.getAcctMsg()));
			form.setRuleSetAuth(data.getAuthRuleset());
			form.setRuleSetAcct(data.getAcctRuleset());
			form.setValidatePacket(Boolean.parseBoolean(data.getValidatepacket()));
			form.setAuthResponseBehavior(data.getDefaultAuthResBehavior());
			form.setHotlinePolicy(data.getHotlinePolicy()!=null?data.getHotlinePolicy():"");
			form.setAcctResponseBehavior(data.getDefaultAcctResBehavior());
			form.setSessionManagerId(data.getSessionManagerId());
			form.setAuthResponseAttributes(data.getAuthResponseAttributes());
			form.setAcctResponseAttributes(data.getAcctResponseAttributes());
			form.setCui(data.getCui());
			form.setAcctAttributes(data.getAcctAttribute());
			form.setAuthAttributes(data.getAuthAttribute());
			form.setAuditUid(data.getAuditUid());
		}
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
	
	private PluginHandler getPluginDetailData( RadPluginHandlerData radPluginHandlerData) {
		PluginHandler pluginHandler = new PluginHandler();
		pluginHandler.setIsHandlerEnabled(radPluginHandlerData.getEnabled());
		
		List<PluginDetails> pluginDetailList = new ArrayList<PluginDetails>();
		
		if( radPluginHandlerData.getPluginEntries() != null && radPluginHandlerData.getPluginEntries().isEmpty() == false ){
			for(PluginEntryData pluginData : radPluginHandlerData.getPluginEntries()){
				
				PluginDetails plDetails =new PluginDetails();
				plDetails.setPluginName(pluginData.getPluginName());
				plDetails.setRequestType(pluginData.isOnResponse());
				plDetails.setRuleset(pluginData.getRuleset());
				plDetails.setPluginArgument(pluginData.getPluginArgument());
				
				pluginDetailList.add(plDetails);
			}
			
			pluginHandler.setLstPluginDetails(pluginDetailList);
		}
		
		return pluginHandler;
	}
}

