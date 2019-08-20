package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.digestconf.DigestConfBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
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
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateRadiusServicePolicyAcctServiceFlowAction extends BaseWebAction{
	private static final String UPDATE_FORWARD = "updateRadiusServicePolicyAcctServiceFlow";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE="UpdateAuthServicePolicyBasicDetailAction";
	private static final String ACTION_ALIAS=ConfigConstant.UPDATE_RADIUS_SERVICE_POLICY_ACCT_SERVICE_FLOW;
	
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
				List<PluginInstData> postPluginList = pluginBLManager.getAcctPluginList();
				request.setAttribute("postPluginList", postPluginList);
				
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
				
				/* Get Accounting flow ESI */
				List<ExternalSystemInterfaceInstanceData> acctESIList=new ArrayList<ExternalSystemInterfaceInstanceData>();
				acctESIList = externalSystemBLmanager.getAcctFlowExternalSystemInstanceDataList(ExternalSystemConstants.AUTH_PROXY);
				updateRadiusServicePolicyForm.setAcctESIList(acctESIList);
				
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
						
						radServicePolicyData = servicePolicyBLManager.getRadiusServicePolicyInstData(radiusPolicyID,staffData,ACTION_ALIAS);
						
 						String xmlDatas = new String(radServicePolicyData.getRadiusPolicyXml());
 						
 						StringReader stringReader =new StringReader(xmlDatas.trim());
						
						JAXBContext context = JAXBContext.newInstance(RadiusServicePolicyData.class);
						Unmarshaller unmarshaller = context.createUnmarshaller();
						RadiusServicePolicyData radiusServicePolicyData = (RadiusServicePolicyData) unmarshaller.unmarshal(stringReader);

						setRadiusEsiGroupData(updateRadiusServicePolicyForm);
						convertXmlBeanToForm(radiusServicePolicyData,updateRadiusServicePolicyForm);
						
						request.setAttribute("radServicePolicyData",radServicePolicyData);
						convertBeanToForm(radServicePolicyData,updateRadiusServicePolicyForm);
						
						/* Driver Script and External Radius Script */
						ScriptBLManager scriptBLManager = new ScriptBLManager();
						List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
						List<ScriptInstanceData> externalScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.EXTERNAL_RADIUS_SCRIPT);
							
						updateRadiusServicePolicyForm.setDriverScriptList(driverScriptList);
						updateRadiusServicePolicyForm.setExternalScriptList(externalScriptList);
						
						request.getSession().setAttribute("updateRadiusServicePolicyForm", updateRadiusServicePolicyForm);
					}
				}else if(action!=null && action.equals("update")){

					String acctProfileLookupDriverHanlderJson=updateRadiusServicePolicyForm.getAcctprofileLookupDriverJson();
					String acctCdrGenerationJson=updateRadiusServicePolicyForm.getAcctCDRGenerationJson();
					String acctPluginHandlerJson=updateRadiusServicePolicyForm.getAcctPluginHandlerJson();
					String acctCOADmGenerationJson=updateRadiusServicePolicyForm.getAcctCOADMGenerationJson();
					String acctProxyCommunicationJson=updateRadiusServicePolicyForm.getAcctProxyCommunicationJson();
					String acctBroacastCommunicationJson=updateRadiusServicePolicyForm.getAcctBroadcastCommunicationJson();
					String statefulProxySequentialJson = updateRadiusServicePolicyForm.getStatefulProxySequentialHandlerJson();
					String statefulProxySequentialAcctJson = updateRadiusServicePolicyForm.getStatefulProxySequentialHandlerAcctJson();
					String statefulProxyBroadcastHandlorAcctJson = updateRadiusServicePolicyForm.getStatefulProxyBroadcastHandlerAcctJson();
				
					String acctPrePluginJson  = updateRadiusServicePolicyForm.getAcctPrePluginJson();
					String acctPostPluginJson = updateRadiusServicePolicyForm.getAcctPostPluginJson();
					
					
					List<ProfileLookupDriver> profileLookupDrivers=new ArrayList<ProfileLookupDriver>();
					
					if(acctProfileLookupDriverHanlderJson != null && acctProfileLookupDriverHanlderJson.length() > 0){
						 JSONArray profileDriverArray = JSONArray.fromObject(acctProfileLookupDriverHanlderJson);
						 for(Object  obj: profileDriverArray){
							 ProfileLookupDriver profileLookupDriver = (ProfileLookupDriver) JSONObject.toBean((JSONObject) obj, ProfileLookupDriver.class);
							 profileLookupDrivers.add(profileLookupDriver);
						 }
					}
					
					updateRadiusServicePolicyForm.setAcctProfileLookupDriver(profileLookupDrivers);
					
					List<CDRGeneration> cdrGenerations = new ArrayList<CDRGeneration>();
					
					if(acctCdrGenerationJson != null && acctCdrGenerationJson.length() > 0){
						 JSONArray cdrGenerationArray = JSONArray.fromObject(acctCdrGenerationJson);
						 for(Object  obj: cdrGenerationArray){
							 Map<String,Class> configObj = new HashMap<String, Class>();
							 configObj.put("cdrGenerationDetailsList", CDRGenerationDetails.class);
							 CDRGeneration cdrGeneration = (CDRGeneration) JSONObject.toBean((JSONObject) obj, CDRGeneration.class,configObj);
							 cdrGenerations.add(cdrGeneration);
						 }
					}
					
					updateRadiusServicePolicyForm.setAcctCDRGenerationList(cdrGenerations);
					
					List<PluginHandler> pluginHandlers = new ArrayList<PluginHandler>();
					
					if(acctPluginHandlerJson != null && acctPluginHandlerJson.length() > 0){
						 JSONArray pluginHandlerArray = JSONArray.fromObject(acctPluginHandlerJson);
						 for(Object  obj: pluginHandlerArray){
							 Map<String,Class> configObj = new HashMap<String, Class>();
							 
							 configObj.put("lstPluginDetails", PluginDetails.class);
						
							 PluginHandler pluginHandler = (PluginHandler) JSONObject.toBean((JSONObject) obj, PluginHandler.class,configObj);
							 pluginHandlers.add(pluginHandler);
						 }
					}
					
					updateRadiusServicePolicyForm.setAcctPluginHandlerList(pluginHandlers);
					
					List<COADMGeneration> coaDmGenerations = new ArrayList<COADMGeneration>();
					
					if(acctCOADmGenerationJson != null && acctCOADmGenerationJson.length() > 0){
						 JSONArray coaDMGenerationArray = JSONArray.fromObject(acctCOADmGenerationJson);
						 for(Object  obj: coaDMGenerationArray){
							 Map<String,Class> configObj = new HashMap<String, Class>();
							 configObj.put("coaDMGenerationDetailList", COADMGenerationDetails.class);
							 COADMGeneration coaDmGeneration = (COADMGeneration) JSONObject.toBean((JSONObject) obj, COADMGeneration.class,configObj);
							 coaDmGenerations.add(coaDmGeneration);
						 }
					}
					
					updateRadiusServicePolicyForm.setAcctCOADMGenList(coaDmGenerations);
					
					List<ProxyCommunication> proxyCommunications = new ArrayList<ProxyCommunication>();
					
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
					
					updateRadiusServicePolicyForm.setAcctProxyCommunicationList(proxyCommunications);
					
					List<BroadcastCommunication> broadcastCommunications = new ArrayList<BroadcastCommunication>();
					
					if(acctBroacastCommunicationJson != null && acctBroacastCommunicationJson.length() > 0){
						 JSONArray broadcastCommunicationArray = JSONArray.fromObject(acctBroacastCommunicationJson);
						 for(Object  obj: broadcastCommunicationArray){
							
							 Map<String,Class> configObj = new HashMap<String, Class>();
							 
							 configObj.put("broadcastCommunicationList", BroadcastCommunicationData.class);
							 configObj.put("esiListData", ESIServerData.class);
							
							 BroadcastCommunication broadcastCommunication = (BroadcastCommunication) JSONObject.toBean((JSONObject) obj, BroadcastCommunication.class,configObj);
							 broadcastCommunications.add(broadcastCommunication);
						 }
					}
					updateRadiusServicePolicyForm.setAcctBroadcastCommunicationList(broadcastCommunications);

					List<StatefulProxySequentialHandler> statefulProxySequentialHandlers = new ArrayList<>();
					if(Strings.isNullOrEmpty(statefulProxySequentialJson) == false){
						JSONArray handlerDataArray = JSONArray.fromObject(statefulProxySequentialJson);
						for (Object obj: handlerDataArray){
							Map<String,Class> configObj = new HashMap<String, Class>();
							configObj.put("sequentialHandlerEntryData", StatefulProHandlerData.class);
							StatefulProxySequentialHandler handler = (StatefulProxySequentialHandler) JSONObject.toBean((JSONObject) obj,StatefulProxySequentialHandler.class,configObj);
							statefulProxySequentialHandlers.add(handler);
						}
					}
					updateRadiusServicePolicyForm.setStatefulProxySequentialHandlerList(statefulProxySequentialHandlers);

					List<StatefulProxySequentialHandler> statefulProxySequentialAcctHandlers = new ArrayList<>();
					if(Strings.isNullOrEmpty(statefulProxySequentialAcctJson) == false){
						JSONArray handlerDataArray = JSONArray.fromObject(statefulProxySequentialAcctJson);
						for (Object obj: handlerDataArray){
							Map<String,Class> configObj = new HashMap<String, Class>();
							configObj.put("sequentialHandlerEntryData", StatefulProHandlerData.class);
							StatefulProxySequentialHandler handler = (StatefulProxySequentialHandler) JSONObject.toBean((JSONObject) obj,StatefulProxySequentialHandler.class,configObj);
							statefulProxySequentialAcctHandlers.add(handler);
						}
					}
                    updateRadiusServicePolicyForm.setStatefulProxySequentialHandlerAcctList(statefulProxySequentialAcctHandlers);

                    List<StatefulProxyBroadcastHandler> statefulProxyBroadcastAcctHandlers = new ArrayList<>();
                    if(Strings.isNullOrEmpty(statefulProxyBroadcastHandlorAcctJson) == false){
                        JSONArray handlerDataArray = JSONArray.fromObject(statefulProxyBroadcastHandlorAcctJson);
                        for (Object obj: handlerDataArray){
                            Map<String,Class> configObj = new HashMap<String, Class>();
                            configObj.put("broadcastHandlerEntryData", StatefulProHandlerData.class);
                            StatefulProxyBroadcastHandler handler = (StatefulProxyBroadcastHandler) JSONObject.toBean((JSONObject) obj,StatefulProxyBroadcastHandler.class,configObj);
                            statefulProxyBroadcastAcctHandlers.add(handler);
                        }
                    }
                    updateRadiusServicePolicyForm.setStatefulProxyBroadcastHandlerAcctList(statefulProxyBroadcastAcctHandlers);

					/* Convert Pre plugin list to relavant POJO */
					/* Acct Pre Plugin */
					List<PolicyPluginData> acctPrePluginList = new ArrayList<PolicyPluginData>();
					if(acctPrePluginJson != null && acctPrePluginJson.length() > 0){
						 JSONArray authPrePluginArray = JSONArray.fromObject(acctPrePluginJson);
						 for(Object  obj: authPrePluginArray){
							
							 PolicyPluginData policyPluginData = (PolicyPluginData) JSONObject.toBean((JSONObject) obj, PolicyPluginData.class);
							 acctPrePluginList.add(policyPluginData);
						 }
					}
					updateRadiusServicePolicyForm.setAcctPrePluginData(acctPrePluginList);
					
					
					/* Acct Post Plugin */
					List<PolicyPluginData> acctPostPluginList = new ArrayList<PolicyPluginData>();
					if(acctPostPluginJson != null && acctPostPluginJson.length() > 0){
						 JSONArray authPostPluginArray = JSONArray.fromObject(acctPostPluginJson);
						 for(Object  obj: authPostPluginArray){
							
							 PolicyPluginData policyPluginData = (PolicyPluginData) JSONObject.toBean((JSONObject) obj, PolicyPluginData.class);
							 acctPostPluginList.add(policyPluginData);
						 }
					}
					updateRadiusServicePolicyForm.setAcctPostPluginData(acctPostPluginList);
					
					//update Code
					RadServicePolicyData radServicePolicyData = new RadServicePolicyData();

					radServicePolicyData = servicePolicyBLManager.getRadiusServicePolicyInstData(radiusPolicyID, staffData,ACTION_ALIAS);
					
					String xmlDatas = new String(radServicePolicyData.getRadiusPolicyXml());
					
					StringReader stringReader =new StringReader(xmlDatas.trim());
					
					JAXBContext context = JAXBContext.newInstance(RadiusServicePolicyData.class);
					Unmarshaller unmarshaller = context.createUnmarshaller();
					RadiusServicePolicyData radiusServicePolicyData = (RadiusServicePolicyData) unmarshaller.unmarshal(stringReader);
				
					radiusServicePolicyData = convertFormToBean(radiusServicePolicyData,updateRadiusServicePolicyForm);
					
					JAXBContext jaxbContext = JAXBContext.newInstance(RadiusServicePolicyData.class);
				    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				 
				    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				    java.io.StringWriter xmlObj = new StringWriter();
				    
				    jaxbMarshaller.marshal(radiusServicePolicyData,xmlObj);
				    
				    String xmlData = xmlObj.toString().trim();
				    
				    System.out.println("**********************Updated XML Datas*****************************");
				    System.out.println(xmlData);
				    System.out.println("********************************************************************");
				    System.out.println("XML Length : "+xmlData.length());
				    System.out.println("********************************************************************");
					
				    radServicePolicyData.setRadiusPolicyXml(xmlData.getBytes());
				    
				    staffData.setAuditId(radServicePolicyData.getAuditUid());
					staffData.setAuditName(radServicePolicyData.getName());
					
				    servicePolicyBLManager.updateRadiusServicePolicyById(radiusServicePolicyData, staffData, ACTION_ALIAS);
					doAuditing(staffData, ACTION_ALIAS);
					
					Logger.logDebug(MODULE, "RadServicePolicyData : "+radServicePolicyData);
	                request.setAttribute("responseUrl","/viewRadiusServicePolicy.do?radiusPolicyId="+radServicePolicyData.getRadiusPolicyId());
	                ActionMessage message = new ActionMessage("radiusservicepolicy.update.acctflow.success");
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
	private void convertXmlBeanToForm(RadiusServicePolicyData radiusServicePolicyData,UpdateRadiusServicePolicyForm updateRadiusServicePolicyForm) {
		convertAcctData(radiusServicePolicyData,updateRadiusServicePolicyForm);
	}
	private void convertAcctData(RadiusServicePolicyData radiusServicePolicyData,UpdateRadiusServicePolicyForm updateRadiusServicePolicyForm) {
		AccountingPolicyData accountingPolicyData = radiusServicePolicyData.getAccountingPolicyData();
		if(accountingPolicyData == null){
			accountingPolicyData = new AccountingPolicyData();
		}
		
		AcctPostResponseHandlerData authPostResponseHandlerData;
		if(radiusServicePolicyData.getAccountingPolicyData() != null){
			authPostResponseHandlerData = radiusServicePolicyData.getAccountingPolicyData().getPostResponseHandlerData();
		}else{
			authPostResponseHandlerData = new AcctPostResponseHandlerData();
		}
		
		List<AcctServicePolicyHandlerData> lstHandlerData =  accountingPolicyData.getHandlersData();
		List<AcctServicePolicyHandlerData> lstPostResponseHandlerData = authPostResponseHandlerData.getHandlersData();
		
		List<CDRGeneration> lstAcctCDRGenerationList = new ArrayList<CDRGeneration>();
		List<PluginHandler> lstAcctPluginHandlerList  = new ArrayList<PluginHandler>();
		List<COADMGeneration> lstAcctCOADMGeneration = new  ArrayList<COADMGeneration>();
		List<ProxyCommunication> lstAcctProxyCommunicationList  = new ArrayList<ProxyCommunication>();
		List<BroadcastCommunication> lstAcctBroadcastCommunicationList  = new ArrayList<BroadcastCommunication>();
		List<ProfileLookupDriver> lstAcctProfileLookupDriver  = new ArrayList<ProfileLookupDriver>();
		List<StatefulProxySequentialHandler> lstStatefulProxySequntialHandler = new ArrayList<>();
		List<StatefulProxyBroadcastHandler> lstStatefulProxyBroadcastHandler = new ArrayList<>();
		
		int orderNumber = 1; 
		int postResponseOrderNumber = 1;
		
		for(AcctServicePolicyHandlerData acctServicePolicyHandlerData : lstHandlerData){
			if(acctServicePolicyHandlerData.getClass().equals(RadiusSubscriberProfileRepositoryDetails.class)){
				
				ProfileLookupDriver profileLookupDriver = new ProfileLookupDriver();

				RadiusSubscriberProfileRepositoryDetails radiusSubscriberProfileRepositoryDetails = (RadiusSubscriberProfileRepositoryDetails)acctServicePolicyHandlerData;
				
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
				
				lstAcctProfileLookupDriver.add(profileLookupDriver);
				
			}else if(acctServicePolicyHandlerData.getClass().equals(SynchronousCommunicationHandlerData.class)){
				ProxyCommunication proxyCommunication = new ProxyCommunication();
				List<ProxyCommunicationData> lstCommunicationDatas = new  ArrayList<ProxyCommunicationData>();
				
				SynchronousCommunicationHandlerData synchronousCommunicationHandlerData = (SynchronousCommunicationHandlerData)acctServicePolicyHandlerData;
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
				
				lstAcctProxyCommunicationList.add(proxyCommunication);
			}else if(acctServicePolicyHandlerData.getClass().equals(BroadcastCommunicationHandlerData.class)){
				BroadcastCommunication broadcastCommunication = new BroadcastCommunication();
				List<BroadcastCommunicationData> lstCommunicationDatas = new  ArrayList<BroadcastCommunicationData>();
				
				BroadcastCommunicationHandlerData broadcastCommunicationHandlerData = (BroadcastCommunicationHandlerData)acctServicePolicyHandlerData;
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
				
				lstAcctBroadcastCommunicationList.add(broadcastCommunication);
			}else if(acctServicePolicyHandlerData.getClass().equals(RadPluginHandlerData.class)){
				RadPluginHandlerData radPluginHandlerData = (RadPluginHandlerData)acctServicePolicyHandlerData;
				PluginHandler pluginHandler = new PluginHandler();
				
				pluginHandler = getPluginDetailData(radPluginHandlerData);
				
				pluginHandler.setIsAdditional("false");
				pluginHandler.setOrderNumber(orderNumber);
				pluginHandler.setHandlerName(radPluginHandlerData.getHandlerName());
				orderNumber++;
				
				lstAcctPluginHandlerList.add(pluginHandler);
			}else if(acctServicePolicyHandlerData.getClass().equals(CdrGenerationHandlerData.class)){
				CdrGenerationHandlerData cdrGenerationHandlerData = (CdrGenerationHandlerData)acctServicePolicyHandlerData;
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
				
				lstAcctCDRGenerationList.add(cdrGeneration);
			}else if(acctServicePolicyHandlerData.getClass().equals(CoADMGenerationHandlerData.class)){
				CoADMGenerationHandlerData coaDMGenerationHandlerData = (CoADMGenerationHandlerData)acctServicePolicyHandlerData;
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
				orderNumber++;
				
				coaDMGeneration.setCoaDMGenerationDetailList(lstCoaDMGenerationDetails);
				coaDMGeneration.setScheduleAfterInMillis(coaDMGenerationHandlerData.getScheduleAfterInMillis());
				coaDMGeneration.setIsHandlerEnabled(coaDMGenerationHandlerData.getEnabled());
				coaDMGeneration.setHandlerName(coaDMGenerationHandlerData.getHandlerName());
				
				lstAcctCOADMGeneration.add(coaDMGeneration);
			}else if (acctServicePolicyHandlerData instanceof StatefulProxySequentialHandlerData){
				StatefulProxySequentialHandlerData handlerData = (StatefulProxySequentialHandlerData) acctServicePolicyHandlerData;

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

				lstStatefulProxySequntialHandler.add(statefulProxySequentialHandler);
			}else if (acctServicePolicyHandlerData instanceof StatefulProxyBroadcastHandlerData){
                StatefulProxyBroadcastHandlerData handlerData = (StatefulProxyBroadcastHandlerData) acctServicePolicyHandlerData;

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

                lstStatefulProxyBroadcastHandler.add(statefulProxyBroadcastHandler);
            }
		}

		if(lstPostResponseHandlerData != null && !(lstPostResponseHandlerData.isEmpty())){
			for(AcctServicePolicyHandlerData acctServicePolicyHandlerData : lstPostResponseHandlerData){
				if(acctServicePolicyHandlerData.getClass().equals(CdrGenerationHandlerData.class)){
					CdrGenerationHandlerData cdrGenerationHandlerData = (CdrGenerationHandlerData)acctServicePolicyHandlerData;
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
					
					lstAcctCDRGenerationList.add(cdrGeneration);
			}else if(acctServicePolicyHandlerData.getClass().equals(CoADMGenerationHandlerData.class)){
					CoADMGenerationHandlerData coaDMGenerationHandlerData = (CoADMGenerationHandlerData)acctServicePolicyHandlerData;
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
					
					lstAcctCOADMGeneration.add(coaDMGeneration);
			 }else if(acctServicePolicyHandlerData.getClass().equals(RadPluginHandlerData.class)){
					RadPluginHandlerData radPluginHandlerData = (RadPluginHandlerData)acctServicePolicyHandlerData;
					PluginHandler pluginHandler = new PluginHandler();
					
					pluginHandler = getPluginDetailData(radPluginHandlerData);
					
					pluginHandler.setIsAdditional("true");
					pluginHandler.setOrderNumber(postResponseOrderNumber);
					pluginHandler.setHandlerName(radPluginHandlerData.getHandlerName());
					postResponseOrderNumber++;
					
					lstAcctPluginHandlerList.add(pluginHandler);
				}
			}
		}
		updateRadiusServicePolicyForm.setAcctBroadcastCommunicationList(lstAcctBroadcastCommunicationList);
		updateRadiusServicePolicyForm.setAcctCDRGenerationList(lstAcctCDRGenerationList);
		updateRadiusServicePolicyForm.setAcctPluginHandlerList(lstAcctPluginHandlerList);
		updateRadiusServicePolicyForm.setAcctProxyCommunicationList(lstAcctProxyCommunicationList);
		updateRadiusServicePolicyForm.setAcctBroadcastCommunicationList(lstAcctBroadcastCommunicationList);
		updateRadiusServicePolicyForm.setAcctProfileLookupDriver(lstAcctProfileLookupDriver);
		updateRadiusServicePolicyForm.setAcctCOADMGenList(lstAcctCOADMGeneration);
		updateRadiusServicePolicyForm.setStatefulProxySequentialHandlerList(lstStatefulProxySequntialHandler);
		updateRadiusServicePolicyForm.setStatefulProxyBroadcastHandlerAcctList(lstStatefulProxyBroadcastHandler);

		List<PluginEntryDetail> prePluginDataList = accountingPolicyData.getPrePluginDataList();
		List<PolicyPluginData> policyPrePluginDataList = new ArrayList<PolicyPluginData>();
			
		if( prePluginDataList != null && prePluginDataList.isEmpty() == false ){
			for(PluginEntryDetail preAndPostEntryData : prePluginDataList){
				PolicyPluginData policyPluginData = new PolicyPluginData();
				policyPluginData.setPluginArgument(preAndPostEntryData.getPluginArgument());
				policyPluginData.setPluginName(preAndPostEntryData.getPluginName());
				
				policyPrePluginDataList.add(policyPluginData);
			}
		}
		
		updateRadiusServicePolicyForm.setAcctPrePluginData(policyPrePluginDataList);
		
		List<PluginEntryDetail> postPluginDataList = accountingPolicyData.getPostPluginDataList();
		List<PolicyPluginData> policyPostPluginDataList = new ArrayList<PolicyPluginData>();
			
		if( postPluginDataList != null && postPluginDataList.isEmpty() == false ){
				for(PluginEntryDetail preAndPostEntryData : postPluginDataList){
					PolicyPluginData policyPluginData = new PolicyPluginData();
					policyPluginData.setPluginArgument(preAndPostEntryData.getPluginArgument());
					policyPluginData.setPluginName(preAndPostEntryData.getPluginName());
					
					policyPostPluginDataList.add(policyPluginData);
				}
			}
		updateRadiusServicePolicyForm.setAcctPostPluginData(policyPostPluginDataList);
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
				if(RadiusEsiType.AUTH.name.equalsIgnoreCase(esiConfigurationData.getEsiType()) == false){
					radiusEsiGroupNames.add(esiData.getName());
				}
			}
		}
		form.setRadiusEsiGroupNames(radiusEsiGroupNames);
	}

	private void convertBeanToForm(RadServicePolicyData data,UpdateRadiusServicePolicyForm form){
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
	
	private RadiusServicePolicyData convertFormToBean(RadiusServicePolicyData radiusServicePolicyData,UpdateRadiusServicePolicyForm updateRadiusServicePolicyForm) {
		radiusServicePolicyData.setPolicyId(updateRadiusServicePolicyForm.getRadiusPolicyId());
		
		/*************************************************************************************/
		/*							  ACCOUNTING POLICY 								     */
		/*************************************************************************************/
			
		AccountingPolicyData accountingPolicyData = new AccountingPolicyData();
		Map<Integer, Object> accountingHandlersMap=new  HashMap<Integer, Object>();
		
		AcctPostResponseHandlerData acctPostResponseHandlerData = new AcctPostResponseHandlerData();
		Map<Integer,Object> acctPostResponseHandlerMapData = new  HashMap<Integer, Object>();
		
		
		/********** START :  Profile Lookup Driver *********/
		List<ProfileLookupDriver> acctProfileLookupDrivers = updateRadiusServicePolicyForm.getAcctProfileLookupDriver();
		
		if(acctProfileLookupDrivers != null && acctProfileLookupDrivers.size() > 0){
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
		}
		
		/********END : Profile Lookup Driver********/
		

		/********START : RadPluginHandlerData ***********/
		List<PluginHandler> acctPluginHandlers = updateRadiusServicePolicyForm.getAcctPluginHandlerList();
		
		if(acctPluginHandlers != null && acctPluginHandlers.size() > 0){
			for(PluginHandler pluginHandler : acctPluginHandlers){
				RadPluginHandlerData radPluginHandlerData=new RadPluginHandlerData();
				
				radPluginHandlerData = getRadiusPluginHandlerData(pluginHandler);
				
				int orderNumber = pluginHandler.getOrderNumber();
				radPluginHandlerData.setHandlerName(pluginHandler.getHandlerName());
				
				if(pluginHandler.getIsAdditional().equals("false")){
					accountingHandlersMap.put(orderNumber, radPluginHandlerData);
				}else {
					acctPostResponseHandlerMapData.put(orderNumber, radPluginHandlerData);
				}
			}
		}
		/********END : RadPluginHandlerData ***********/
		
		
		/********START : SynchronousCommunicationHandlerData ***********/
		
		List<ProxyCommunication> acctProxyCommunicationsList = updateRadiusServicePolicyForm.getAcctProxyCommunicationList();

		if(acctProxyCommunicationsList != null && acctProxyCommunicationsList.size() > 0){
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

		}
		
		/********END   : SynchronousCommunicationHandlerData ***********/
		
		
		/********START : BroadcastCommunicationHandlerData ***********/
		List<BroadcastCommunication> acctBroadcastCommunicationList = updateRadiusServicePolicyForm.getAcctBroadcastCommunicationList();

		if(acctBroadcastCommunicationList != null && acctBroadcastCommunicationList.size() >0){
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
		}

		/********END   : BroadcastCommunicationHandlerData ***********/
		
		
		/********START : CdrGenerationHandlerData ***********/
		List<CDRGeneration> cdrGenerations = updateRadiusServicePolicyForm.getAcctCDRGenerationList();
		if(cdrGenerations != null && cdrGenerations.size() > 0){
			for(CDRGeneration  cdrGeneration: cdrGenerations){
				CdrGenerationHandlerData cdrGenerationHandlerData = new CdrGenerationHandlerData();
				
				if(cdrGeneration.getIsAdditional().equals("false")){
					List<CdrHandlerEntryData> cdrHandlerEntryDatas = new ArrayList<CdrHandlerEntryData>();
					List<CDRGenerationDetails> cdrGenerationDetails = cdrGeneration.getCdrGenerationDetailsList();
					
					if(cdrGenerationDetails != null && cdrGenerationDetails.size() > 0){
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
					}
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
						acctPostResponseHandlerMapData.put(orderNumber, cdrGenerationHandlerData);
				}
				
				
			}
		}
		
		/********END   : CdrGenerationHandlerData ***********/
		
		/********START   : COADMGenerationHandlerData ***********/
		List<COADMGeneration> acctCoaDmGenerations = updateRadiusServicePolicyForm.getAcctCOADMGenList();
		if(acctCoaDmGenerations != null && acctCoaDmGenerations.size() > 0){
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
						coaDMGenerationHandlerData.setEnabled(coaDMGeneration.getIsHandlerEnabled());
						coaDMGenerationHandlerData.setHandlerName(coaDMGeneration.getHandlerName());
						
						int orderNumber = coaDMGeneration.getOrderNumber();
						
						acctPostResponseHandlerMapData.put(orderNumber, coaDMGenerationHandlerData);
					}
				}
		}
		
		/********END   : COADMGenerationHandlerData ***********/

		List<StatefulProxySequentialHandler> statefulProxySequentialHandlers = updateRadiusServicePolicyForm.getStatefulProxySequentialHandlerAcctList();

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

				accountingHandlersMap.put(handlerData.getOrderNumber(), statefulProxySequentialHandlerData);
			}
		}

        List<StatefulProxyBroadcastHandler> statefulProxyBroadcastHandlers = updateRadiusServicePolicyForm.getStatefulProxyBroadcastHandlerAcctList();
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

		/********END : Stateful Proxy Sequential Handler **************/
		
		if( updateRadiusServicePolicyForm.getAcctPrePluginData() != null && updateRadiusServicePolicyForm.getAcctPrePluginData().size() > 0){
			
			List<PluginEntryDetail> pluginDataList = new ArrayList<PluginEntryDetail>();
			for (PolicyPluginData policyPluginData : updateRadiusServicePolicyForm.getAcctPrePluginData()) {
				PluginEntryDetail preAndPostPluginData = new PluginEntryDetail();
				preAndPostPluginData.setPluginArgument(policyPluginData.getPluginArgument());
				preAndPostPluginData.setPluginName(policyPluginData.getPluginName());
				pluginDataList.add(preAndPostPluginData);
			}
			accountingPolicyData.setPrePluginDataList(pluginDataList);
		}
		
		if( updateRadiusServicePolicyForm.getAcctPostPluginData() != null && updateRadiusServicePolicyForm.getAcctPostPluginData().size() > 0){
			
			List<PluginEntryDetail> pluginDataList = new ArrayList<PluginEntryDetail>();
			for (PolicyPluginData policyPluginData : updateRadiusServicePolicyForm.getAcctPostPluginData()) {
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

