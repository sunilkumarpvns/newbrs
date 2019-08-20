package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.*;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.digestconf.DigestConfBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.constants.ServiceTypeConstants;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.UpdateRadiusServicePolicyForm;

public class ViewRadiusServicePolicyAcctServiceFlowAction extends BaseWebAction{
	private static final String VIEW_FORWARD = "viewRadiusServicePolicyAcctServiceFlow";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE="ViewRadiusServicePolicyAcctServiceFlowAction";
	private static final String ACTION_ALIAS=ConfigConstant.VIEW_RADIUS_SERVICE_POLICY;
	
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
				List<PluginInstData> postPluginList  = pluginBLManager.getAcctPluginList();
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
				
				String strRadiusPolicyID = request.getParameter("radiusPolicyId");
				String radiusPolicyID;
				
				IStaffData staffData = getStaffObject((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm"));
				
				if(strRadiusPolicyID != null){
					radiusPolicyID = strRadiusPolicyID;
				}else{
					radiusPolicyID=updateRadiusServicePolicyForm.getRadiusPolicyId();
				}
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
						convertBeanToForm(radServicePolicyData,updateRadiusServicePolicyForm);
						
						request.setAttribute("radServicePolicyData",radServicePolicyData);
						request.getSession().setAttribute("updateRadiusServicePolicyForm", updateRadiusServicePolicyForm);
				}
				return mapping.findForward(VIEW_FORWARD);
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
		List<StatefulProxySequentialHandler> lstStatefulProxySequentialHandlers = new ArrayList<>();
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

				lstStatefulProxySequentialHandlers.add(statefulProxySequentialHandler);
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
		updateRadiusServicePolicyForm.setStatefulProxySequentialHandlerList(lstStatefulProxySequentialHandlers);
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

	//This function is used to add Radius ESI Group data in UpdateRadiusServicePolicyForm
	private void setRadiusEsiGroupData(UpdateRadiusServicePolicyForm form) throws DataManagerException {
		RadiusESIGroupBLManager esiBlManager = new RadiusESIGroupBLManager();
		List<RadiusESIGroupData> radiusESIGroupDataList = esiBlManager.getRadiusESIGroupDataList();
		List<String> radiusEsiGroupNames = new ArrayList<>();

		if(Collectionz.isNullOrEmpty(radiusESIGroupDataList) == false){
			for (RadiusESIGroupData esiData:radiusESIGroupDataList) {
				radiusEsiGroupNames.add(esiData.getName());
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
	List<PluginDetails> getPluginDetails(List<PluginEntryData> pluginEntryData){
		List<PluginDetails> pluginDetails = new ArrayList<PluginDetails>();
		
		for(PluginEntryData pluginData : pluginEntryData){
			
			PluginDetails plDetails =new PluginDetails();
			plDetails.setPluginName(pluginData.getPluginName());
			plDetails.setRequestType(pluginData.isOnResponse());
			plDetails.setRuleset(pluginData.getRuleset());
			
			pluginDetails.add(plDetails);
}
		return pluginDetails;
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

