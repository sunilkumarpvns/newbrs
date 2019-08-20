package com.elitecore.elitesm.blmanager.servicepolicy.diameter;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.elitecore.aaa.core.constant.AuthMethods;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.blmanager.diameter.diameterconcurrency.DiameterConcurrencyBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.TGPPAAAPolicyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AdditionalDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.PrimaryDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.SecondaryAndCacheDriverDetail;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.XmlNodeUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.CommandCodeFlowData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.CommandCodeResponseAttribute;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterApplicationHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterAuthenticationHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterBroadcastCommunicationEntryData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterBroadcastCommunicationHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterCDRGenerationHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterCDRHandlerEntryData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterConcurrencyHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterExternalCommunicationEntryData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterPostResponseHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterProfileDriverDetails;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterSubscriberProfileRepositoryDetails;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.DiameterSynchronousCommunicationHandlerData;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.TGPPServerPolicyData;
import com.elitecore.elitesm.ws.rest.data.Status;


/**
 * @author nayana.rathod
 *
 */
public class TGPPAAAPolicyBLManager  extends BaseBLManager{
	
	public TGPPAAAPolicyDataManager getTgppAAAPolicyDataManager(IDataManagerSession session){
		TGPPAAAPolicyDataManager tgppAAAPolicyDataManager = (TGPPAAAPolicyDataManager)DataManagerFactory.getInstance().getDataManager(TGPPAAAPolicyDataManager.class, session);
		return tgppAAAPolicyDataManager;
	}

	/**
	 * @throws DataManagerException
	 */
	public void deleteTGPPAAAPolicyById(List<String> policiesToDelete, IStaffData staffData) throws DataManagerException {
		delete(policiesToDelete, staffData, BY_ID);
	}
	
	public void deleteTGPPAAAPolicyByName(List<String> policiesToDelete, IStaffData staffData) throws DataManagerException {
		delete(policiesToDelete, staffData, BY_NAME);
	}
	
	private void delete(List<String> policiesToDelete, IStaffData staffData, boolean deleteByIdOrName) throws DataManagerException {
		
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		TGPPAAAPolicyDataManager tgppAAAPolicyDataManager = getTgppAAAPolicyDataManager(session);

		if (tgppAAAPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			for (String policyToDelete : policiesToDelete) {
				String deletedPolicy;
				if (deleteByIdOrName) {
					deletedPolicy = tgppAAAPolicyDataManager.deleteTGPPAAAPolicyById(policyToDelete);
				} else {
					deletedPolicy = tgppAAAPolicyDataManager.deleteTGPPAAAPolicyByName(policyToDelete);
				}
				staffData.setAuditName(deletedPolicy);
				AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_TGPP_AAA_SERVICE_POLICY);
			}
			
			commit(session);
		}catch(DataManagerException e){
        	rollbackSession(session);
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	rollbackSession(session);
        	throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * @param tgppAAAPolicyData
	 * @param requiredPageNo
	 * @param pageSize
	 * @return
	 * @throws DataManagerException
	 */
	public PageList searchTGPPAAAPolicy(TGPPAAAPolicyData tgppAAAPolicyData,int requiredPageNo, Integer pageSize) 
			throws DataManagerException {
		
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		TGPPAAAPolicyDataManager tgppAAAPolicyDataManager = getTgppAAAPolicyDataManager(session);
		if (tgppAAAPolicyDataManager==null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {				     
		    PageList pageList = tgppAAAPolicyDataManager.searchTGPPAAAPolicy(tgppAAAPolicyData, requiredPageNo, pageSize);
			return pageList;
		} catch(DataManagerException e) {
        	throw e;
		} catch(Exception e) {
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public Map<String, List<Status>> createTGPPAAAPolicyData(List<TGPPAAAPolicyData> tgppaaaPolicyDatas, IStaffData staffData,String partialSuccess) throws JAXBException, DataManagerException {

		for (TGPPAAAPolicyData tgppAAAPolicyData : tgppaaaPolicyDatas) {
			if(tgppAAAPolicyData.getTgppAAAPolicyXml() !=  null) {
				tgppAAAPolicyData = getConvertedTgppAAAPolicyData(tgppAAAPolicyData);
			}
		}	
		return insertRecords(TGPPAAAPolicyDataManager.class, tgppaaaPolicyDatas, staffData, ConfigConstant.CREATE_TGPP_AAA_SERVICE_POLICY, partialSuccess);
	}
	
	/**
	 * @param tgppAAAPolicyData
	 * @throws DataManagerException
	 * @throws JAXBException 
	 */
	public void createTGPPAAAPolicyData(TGPPAAAPolicyData tgppAAAPolicyData, IStaffData staffData) throws DataManagerException, JAXBException {
		
		List<TGPPAAAPolicyData> tgppPolicyData = new ArrayList<TGPPAAAPolicyData>();
		tgppPolicyData.add(tgppAAAPolicyData);
		createTGPPAAAPolicyData(tgppPolicyData, staffData, "false");
	}

	public TGPPAAAPolicyData getTGPPAAAPolicyData(String tgppAAAPolicyID) throws DataManagerException, JAXBException {
		return getTgppPolicy(tgppAAAPolicyID, BY_ID);
	}
	
	private byte[] getUpdatedTgppAAAPolicyDataWithId(TGPPServerPolicyData tgppServerPolicyData) throws DataManagerException, JAXBException {
		TGPPServerPolicyData updatedTGPPServerPolicyData = new TGPPServerPolicyData();
		List<CommandCodeFlowData> listCodeFlowDatas = tgppServerPolicyData.getCommandCodeFlows();
		List<CommandCodeFlowData> commandCodeFlowList = new ArrayList<CommandCodeFlowData>();
		ServicePolicyBLManager servicePolicyBlmanager = new ServicePolicyBLManager();
		updatedTGPPServerPolicyData.setId(tgppServerPolicyData.getId());
		updatedTGPPServerPolicyData.setName(tgppServerPolicyData.getName());
		updatedTGPPServerPolicyData.setDescription(tgppServerPolicyData.getDescription());
		updatedTGPPServerPolicyData.setCommandCodeResponseAttributesList((ArrayList<CommandCodeResponseAttribute>) tgppServerPolicyData.getCommandCodeResponseAttributesList());
		
		updatedTGPPServerPolicyData.setRuleSet(tgppServerPolicyData.getRuleSet());
	
		updatedTGPPServerPolicyData.setSessionManagementEnabled(tgppServerPolicyData.getSessionManagementEnabled());
		updatedTGPPServerPolicyData.setUserIdentity(tgppServerPolicyData.getUserIdentity());
		updatedTGPPServerPolicyData.setCui(tgppServerPolicyData.getCui());
		updatedTGPPServerPolicyData.setDefaultResponseBehaviorParameter(tgppServerPolicyData.getDefaultResponseBehaviorParameter());
		updatedTGPPServerPolicyData.setDefaultResponseBehaviorType(tgppServerPolicyData.getDefaultResponseBehaviorType());
		
		final String MODULE =	"UpdateTGPPAAAPolicyFlowAction";
		
		if (Collectionz.isNullOrEmpty(listCodeFlowDatas) == false) {
			
			for( CommandCodeFlowData commandCodeFlow : listCodeFlowDatas ){
				
				CommandCodeFlowData commandCodeFlowData = new CommandCodeFlowData();
				commandCodeFlowData.setCommandCode(commandCodeFlow.getCommandCode());
				commandCodeFlow.setInterfaceId(commandCodeFlow.getInterfaceId());
				commandCodeFlow.setName(commandCodeFlow.getName());

				List<DiameterApplicationHandlerData> handlersDataList = commandCodeFlow.getHandlersData();
				List<DiameterApplicationHandlerData> handlersData = new ArrayList<DiameterApplicationHandlerData>();
				
				if (Collectionz.isNullOrEmpty(handlersDataList) == false) {
				
					for (DiameterApplicationHandlerData diameterApplicationHandlerData : handlersDataList) {
						
						if (diameterApplicationHandlerData instanceof DiameterAuthenticationHandlerData) {
							
							DiameterAuthenticationHandlerData authenticationHandlerData = (DiameterAuthenticationHandlerData)diameterApplicationHandlerData;
							
							String eapConfName = servicePolicyBlmanager.getEAPConfName(authenticationHandlerData.getEapConfigId());
							authenticationHandlerData.setEapConfigId(eapConfName);
							
							List<String> supportedMethodIds = authenticationHandlerData.getSupportedAuthenticationMethods();
							List<String> supportedMethodsNames = new ArrayList<String>();
							
							for (String authMethod : supportedMethodIds) {
								int authMethodId = Integer.parseInt(authMethod);
								if (1 == authMethodId) {
									supportedMethodsNames.add("PAP");
								} else if (2 == authMethodId) {
									supportedMethodsNames.add("CHAP");
								} else if (3 == authMethodId) {
									supportedMethodsNames.add("EAP");
								}
							}
							authenticationHandlerData.setSupportedAuthenticationMethods(supportedMethodsNames);
							
							handlersData.add(authenticationHandlerData);
							
						} else if (diameterApplicationHandlerData instanceof DiameterSubscriberProfileRepositoryDetails) {

							DiameterSubscriberProfileRepositoryDetails diameterSubscriberProfileRepositoryDetails = (DiameterSubscriberProfileRepositoryDetails)diameterApplicationHandlerData;
							
							if(diameterSubscriberProfileRepositoryDetails.getDriverDetails() != null ) {
								
								DiameterProfileDriverDetails profileDriverDetails = diameterSubscriberProfileRepositoryDetails.getDriverDetails();
							
								if( profileDriverDetails != null ) {
									
									if (Collectionz.isNullOrEmpty(profileDriverDetails.getPrimaryDriverGroup()) == false) {
										
										List<PrimaryDriverDetail> primaryDriverDetailList = new ArrayList<PrimaryDriverDetail>();
										
										if (Collectionz.isNullOrEmpty(profileDriverDetails.getPrimaryDriverGroup()) == false) {
											
											for(PrimaryDriverDetail details : profileDriverDetails.getPrimaryDriverGroup()) {
												
												String driverName = servicePolicyBlmanager.getDriverName(details.getDriverInstanceId());
												details.setDriverInstanceId(driverName);
												primaryDriverDetailList.add(details);
											}
											
											profileDriverDetails.setPrimaryDriverGroup(primaryDriverDetailList);
										}
										
										List<SecondaryAndCacheDriverDetail> secondaryAndCacheDriverDetailList = new ArrayList<SecondaryAndCacheDriverDetail>();
										
										if (Collectionz.isNullOrEmpty(profileDriverDetails.getSecondaryDriverGroup()) == false) {
											
											for(SecondaryAndCacheDriverDetail details : profileDriverDetails.getSecondaryDriverGroup()) {
												
												String driverName = servicePolicyBlmanager.getDriverName(details.getSecondaryDriverId());
												details.setSecondaryDriverId(driverName);
												secondaryAndCacheDriverDetailList.add(details);
											}
											
											profileDriverDetails.setSecondaryDriverGroup(secondaryAndCacheDriverDetailList);
										}
										
										List<AdditionalDriverDetail> additionalDriverList = new ArrayList<AdditionalDriverDetail>();
										
										if (Collectionz.isNullOrEmpty(profileDriverDetails.getAdditionalDriverList()) == false) {
											
											for(AdditionalDriverDetail details : profileDriverDetails.getAdditionalDriverList()) {
												
												String driverName = servicePolicyBlmanager.getDriverName(details.getDriverId());
												details.setDriverId(driverName);
												additionalDriverList.add(details);
											}
											
											profileDriverDetails.setAdditionalDriverList(additionalDriverList);
										}
										
										diameterSubscriberProfileRepositoryDetails.setDriverDetails(profileDriverDetails);
										handlersData.add(diameterSubscriberProfileRepositoryDetails);
									}
								}
							
							}
							
						} else if (diameterApplicationHandlerData instanceof DiameterConcurrencyHandlerData) {
							
							DiameterConcurrencyHandlerData concurrencyHandlerData=(DiameterConcurrencyHandlerData) diameterApplicationHandlerData;
						
							DiameterConcurrencyBLManager blManager = new DiameterConcurrencyBLManager();
							
							DiameterConcurrencyData concurrencyData = blManager.getDiameterConcurrencyDataById(concurrencyHandlerData.getDiaConcurrencyId());
							concurrencyHandlerData.setDiaConcurrencyId(concurrencyData.getName());
							
							
							handlersData.add(concurrencyHandlerData);
				
						}else if (diameterApplicationHandlerData instanceof DiameterCDRGenerationHandlerData) {
							DiameterCDRGenerationHandlerData cdrGenHandlerData = (DiameterCDRGenerationHandlerData)diameterApplicationHandlerData;
						
							List<DiameterCDRHandlerEntryData> entries = cdrGenHandlerData.getEntries();
							
							if( Collectionz.isNullOrEmpty(entries) == false ){
								
								for( DiameterCDRHandlerEntryData entryData : entries ){
									
									if( Collectionz.isNullOrEmpty(entryData.getDriverDetails().getPrimaryDriverGroup()) == false ){
										PrimaryDriverDetail primaryDriverDetail = entryData.getDriverDetails().getPrimaryDriverGroup().get(0);
										
										if( primaryDriverDetail != null ){
											String primaryDriverName = servicePolicyBlmanager.getDriverName(primaryDriverDetail.getDriverInstanceId());
											primaryDriverDetail.setDriverInstanceId(primaryDriverName);
										}
									}
									
									if( Collectionz.isNullOrEmpty(entryData.getDriverDetails().getSecondaryDriverGroup()) == false ){
										SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail = entryData.getDriverDetails().getSecondaryDriverGroup().get(0);

										if( secondaryAndCacheDriverDetail != null ){
											String secondaryDriverName = servicePolicyBlmanager.getDriverName(secondaryAndCacheDriverDetail.getSecondaryDriverId());
											secondaryAndCacheDriverDetail.setSecondaryDriverId(secondaryDriverName);
										}
									}
								}
							}
							handlersData.add(cdrGenHandlerData);
					} else if (diameterApplicationHandlerData instanceof DiameterSynchronousCommunicationHandlerData) {

						DiameterSynchronousCommunicationHandlerData communicationHandlerData = (DiameterSynchronousCommunicationHandlerData)diameterApplicationHandlerData;
						
						List<DiameterExternalCommunicationEntryData> entries = communicationHandlerData.getEntries();
							if( Collectionz.isNullOrEmpty(entries) == false ){
								for( DiameterExternalCommunicationEntryData entryData : entries ){
									
									String peerName = getPeerNameFromDiameterPeerGroupOrRadiusESIGroup(entryData.getPeerGroupId());
									entryData.setPeerGroupId(peerName);
								}
							}
						handlersData.add(communicationHandlerData);
					} else if (diameterApplicationHandlerData instanceof DiameterBroadcastCommunicationHandlerData) {

						DiameterBroadcastCommunicationHandlerData communicationHandlerData = (DiameterBroadcastCommunicationHandlerData)diameterApplicationHandlerData;
						
						List<DiameterBroadcastCommunicationEntryData> entries = communicationHandlerData.getEntries();
						
						if(Collectionz.isNullOrEmpty(entries) == false ){
							
							for( DiameterBroadcastCommunicationEntryData entryData : entries ){
								
								String peerName = getPeerNameFromDiameterPeerGroupOrRadiusESIGroup(entryData.getPeerGroupId());
								entryData.setPeerGroupId(peerName);
								
							}
						}
						
						handlersData.add(communicationHandlerData);
					}
						
					}
				}
				
				List<DiameterApplicationHandlerData> postResHandlersDataList = new ArrayList<DiameterApplicationHandlerData>();
				List<DiameterApplicationHandlerData> postResHandlersData = new ArrayList<DiameterApplicationHandlerData>();
				DiameterPostResponseHandlerData postResponseHandlerData = new  DiameterPostResponseHandlerData();
				if(commandCodeFlow.getPostResponseHandlerData() != null){
					postResHandlersDataList = commandCodeFlow.getPostResponseHandlerData().getHandlersData();
				}
				
				if (Collectionz.isNullOrEmpty(postResHandlersDataList) == false) {
					
					for (DiameterApplicationHandlerData diameterApplicationHandlerData : postResHandlersDataList) {
						
						if (diameterApplicationHandlerData instanceof DiameterCDRGenerationHandlerData) {
							
							DiameterCDRGenerationHandlerData cdrGenHandlerData = (DiameterCDRGenerationHandlerData)diameterApplicationHandlerData;
							
							List<DiameterCDRHandlerEntryData> entries = cdrGenHandlerData.getEntries();
							
							if( Collectionz.isNullOrEmpty(entries) == false ){
								
								for( DiameterCDRHandlerEntryData entryData : entries ){
									
									if( Collectionz.isNullOrEmpty(entryData.getDriverDetails().getPrimaryDriverGroup()) == false ){
										PrimaryDriverDetail primaryDriverDetail = entryData.getDriverDetails().getPrimaryDriverGroup().get(0);
										
										if( primaryDriverDetail != null ){
											String primaryDriverName = servicePolicyBlmanager.getDriverName(primaryDriverDetail.getDriverInstanceId());
											primaryDriverDetail.setDriverInstanceId(primaryDriverName);
										}
									}
									
									if( Collectionz.isNullOrEmpty(entryData.getDriverDetails().getSecondaryDriverGroup()) == false ){
										SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail = entryData.getDriverDetails().getSecondaryDriverGroup().get(0);

										if( secondaryAndCacheDriverDetail != null ){
											String secondaryDriverName = servicePolicyBlmanager.getDriverName(secondaryAndCacheDriverDetail.getSecondaryDriverId());
											secondaryAndCacheDriverDetail.setSecondaryDriverId(secondaryDriverName);
										}
									}
								}
							}

							postResHandlersData.add(cdrGenHandlerData);
						} 
						
					}
					postResponseHandlerData.getHandlersData().addAll(postResHandlersData);
				}
				
				commandCodeFlowData.getHandlersData().addAll(handlersData);
				commandCodeFlowData.setPostResponseHandlerData(postResponseHandlerData);
				commandCodeFlowList.add(commandCodeFlow);
			}
			updatedTGPPServerPolicyData.getCommandCodeFlows().addAll(commandCodeFlowList);
		}	
		
		JAXBContext jaxbContext = JAXBContext.newInstance(TGPPServerPolicyData.class);
	    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    java.io.StringWriter xmlObj = new StringWriter();
	    jaxbMarshaller.marshal(updatedTGPPServerPolicyData,xmlObj);
		
	    String xmlDatas = xmlObj.toString().trim();
	    Logger.getLogger().info(MODULE, "******************************* XML Data*******************************");
	    Logger.getLogger().info(MODULE, xmlDatas);
	    Logger.getLogger().info(MODULE, "***********************************************************************");
	    Logger.getLogger().info(MODULE, "XML Length : "+ xmlDatas.length());
	    Logger.getLogger().info(MODULE, "***********************************************************************");
	    
	    return xmlDatas.getBytes();
	}

	private String getPeerIdFromDiameterPeerGroupOrRadiusESIGroup (String name) throws DataManagerException {
		if(Strings.isNullOrBlank(name) == false && "0".equals(name) == false){
			try{
				DiameterPeerGroupBLManager blManager = new DiameterPeerGroupBLManager();
				DiameterPeerGroup peerGroup = blManager.getDiameterPeerGroupByName(name);
				return String.valueOf(peerGroup.getPeerGroupId());
			} catch (Exception e) {
				RadiusESIGroupBLManager blManager = new RadiusESIGroupBLManager();
				RadiusESIGroupData data = blManager.getRadiusESIGroupByName(name);
				return String.valueOf(data.getId());
			}
		} else {
			return "0";
		} 
	}
	
	private String getIdFromTranslationMappingOrCopyPacket(String name) throws DataManagerException {
		if(Strings.isNullOrBlank(name) == false) {
			try{
				TranslationMappingConfBLManager translationMappingBLManager = new TranslationMappingConfBLManager();
				TranslationMappingConfData data = translationMappingBLManager.getTranslationMappingConfDataByName(name);
				return String.valueOf(data.getTranslationMapConfigId());
			} catch(Exception e) {
				CopyPacketTransMapConfBLManager copyTransMapConfBLManager = new CopyPacketTransMapConfBLManager();
				CopyPacketTranslationConfData copyPacketData = copyTransMapConfBLManager.getCopyPacketTransMapConfigDetailDataByName(name);
				return String.valueOf(copyPacketData.getCopyPacketTransConfId());
			}
		}
		else {
			return "0";
		}
		
	}
	
	private String getPeerNameFromDiameterPeerGroupOrRadiusESIGroup (String id) throws DataManagerException {
		if(Strings.isNullOrBlank(id) == false ){
			try{
				DiameterPeerGroupBLManager blManager = new DiameterPeerGroupBLManager();
				DiameterPeerGroup peerGroup = blManager.getDiameterPeerGroupById(id);
				return peerGroup.getPeerGroupName();
			} catch (Exception e) {
				RadiusESIGroupBLManager blManager = new RadiusESIGroupBLManager();
				RadiusESIGroupData data = blManager.getRadiusESIGroupById(id);
				return data.getName();
			}
		} else {
			return "";
		} 
	}
	
	private String getNameFromTranslationMappingOrCopyPacket(String id) throws DataManagerException {
		if(Strings.isNullOrBlank(id) == false && "0".equals(id) == false) {
			try{
					TranslationMappingConfBLManager translationMappingBLManager = new TranslationMappingConfBLManager();
					TranslationMappingConfData data = translationMappingBLManager.getTranslationMappingConfDataById(id);
					return String.valueOf(data.getName());
				}
			 catch(Exception e) {
				CopyPacketTransMapConfBLManager copyTransMapConfBLManager = new CopyPacketTransMapConfBLManager();
				CopyPacketTranslationConfData copyPacketData = copyTransMapConfBLManager.getCopyPacketTransMapConfigDetailDataById(id);
				return String.valueOf(copyPacketData.getName());
			} 
		} else {
			return "";
		}
		
	}
	
	public TGPPAAAPolicyData getTGPPAAAPolicyDataByName(String policyName) throws DataManagerException {
		return getTgppPolicy(policyName, BY_NAME);
	}

	private TGPPAAAPolicyData getTgppPolicy(Object policyToGet, boolean getByIdOrName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		TGPPAAAPolicyDataManager tgppAAAPolicyDataManager = getTgppAAAPolicyDataManager(session);
		if (tgppAAAPolicyDataManager == null) {
			throw new DataManagerException( "Data Manager Not Found: TGPPAAAPolicyDataManager.");
		}
		try {
			TGPPAAAPolicyData tgppaaaPolicyData;
			if (getByIdOrName) {
				tgppaaaPolicyData = tgppAAAPolicyDataManager.getTGPPAAAPolicyData((String)policyToGet);
			} else {
				tgppaaaPolicyData = tgppAAAPolicyDataManager.getTGPPAAAPolicyDataByName((String)policyToGet);
			}
			
			String xmlDatas = new String(tgppaaaPolicyData.getTgppAAAPolicyXml());
			StringReader stringReader =new StringReader(xmlDatas.trim());
			
			JAXBContext context = JAXBContext.newInstance(TGPPServerPolicyData.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			TGPPServerPolicyData tgppServerPolicyData = (TGPPServerPolicyData) unmarshaller.unmarshal(stringReader);
			
			byte[] updatedTgppAAAPolicyDataWithId = getUpdatedTgppAAAPolicyDataWithId(tgppServerPolicyData);
			tgppaaaPolicyData.setTgppAAAPolicyXml(updatedTgppAAAPolicyDataWithId);
			
			return tgppaaaPolicyData;
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateTgppAAAPolicyByID(TGPPAAAPolicyData policyData, IStaffData staffData, String actionAlias, boolean auditable) throws DataManagerException {
		updateTGPPAAABasicDetail(policyData, null, staffData, actionAlias, auditable);
	}
	
	public void updateTgppAAAPolicyByName(TGPPAAAPolicyData policyData, String policyToUpdate, IStaffData staffData) 
			throws DataManagerException {
		updateTGPPAAABasicDetail(policyData, policyToUpdate, staffData, ConfigConstant.UPDATE_TGPP_AAA_SERVICE_POLICY, ConfigConstant.IS_AUDIT_DISABLED);
	}
	
	private void updateTGPPAAABasicDetail(TGPPAAAPolicyData tgppAAAPolicyData, String policyToUpdate, IStaffData staffData, 
			String actionAlias, boolean auditable) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		TGPPAAAPolicyDataManager tgppAAAPolicyDataManager = getTgppAAAPolicyDataManager(session);
		if (tgppAAAPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			session.beginTransaction();
			
			tgppAAAPolicyData = getConvertedTgppAAAPolicyData(tgppAAAPolicyData);
			
			if (policyToUpdate == null) {
				tgppAAAPolicyDataManager.updateTgppAAAPolicyById(tgppAAAPolicyData, staffData, auditable);
			} else {
				tgppAAAPolicyDataManager.updateTgppAAAPolicyByName(tgppAAAPolicyData, policyToUpdate, staffData, true);
			}
			commit(session);
		} catch(DuplicateInstanceNameFoundException e) {
        	rollbackSession(session);
        	throw new DuplicateInstanceNameFoundException(e.getMessage(),e);	
		} catch(DataManagerException e) {
        	rollbackSession(session);
        	throw e;
		} catch(Exception e) {
			e.printStackTrace();
			rollbackSession(session);
        	throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	private TGPPAAAPolicyData getConvertedTgppAAAPolicyData(TGPPAAAPolicyData tgppAAAPolicyData) throws JAXBException, DataManagerException {
		
		final String MODULE =	"CreateTGPPAAAPolicyFlowAction";
		byte[] tgppAAAPolicyXMl = tgppAAAPolicyData.getTgppAAAPolicyXml();
								
		TGPPServerPolicyData tgppServerPolicyData = ConfigUtil.deserialize(new String(tgppAAAPolicyXMl), TGPPServerPolicyData.class);
		
		List<CommandCodeFlowData> listCodeFlowDatas = tgppServerPolicyData.getCommandCodeFlows();
		List<CommandCodeFlowData> commandCodeFlowDataList = new ArrayList<CommandCodeFlowData>();
		
		if (Collectionz.isNullOrEmpty(listCodeFlowDatas) == false) {
			
			for( CommandCodeFlowData data : listCodeFlowDatas ){
				
				List<DiameterApplicationHandlerData> handlersDataList = data.getHandlersData();
				List<DiameterApplicationHandlerData> handlerList = new ArrayList<DiameterApplicationHandlerData>();
				CommandCodeFlowData commandCodeFlowData = new CommandCodeFlowData();
				commandCodeFlowData.setCommandCode(data.getCommandCode());
				
				/* Interface */
				commandCodeFlowData.setInterfaceId(data.getInterfaceId());
				
				commandCodeFlowData.setName(data.getName());
				ServicePolicyBLManager servicePolicyBlmanager = new ServicePolicyBLManager();
				if (Collectionz.isNullOrEmpty(handlersDataList) == false) {
					for (DiameterApplicationHandlerData diameterApplicationHandlerData : handlersDataList) {
						
						if (diameterApplicationHandlerData instanceof DiameterAuthenticationHandlerData) {

							DiameterAuthenticationHandlerData authenticationHandlerData = (DiameterAuthenticationHandlerData)diameterApplicationHandlerData;
							String eapConfId = servicePolicyBlmanager.getEAPConfigId(authenticationHandlerData.getEapConfigId());
							authenticationHandlerData.setEapConfigId(eapConfId);
							List<String> supportedMethods = authenticationHandlerData.getSupportedAuthenticationMethods();
							List<String> supportedMethodsId = new ArrayList<String>();
							
							for (String authMethod : supportedMethods) {
								if ("PAP".equalsIgnoreCase(authMethod)) {
									supportedMethodsId.add(String.valueOf(AuthMethods.PAP));
								} else if ("CHAP".equalsIgnoreCase(authMethod)) {
									supportedMethodsId.add(String.valueOf(AuthMethods.CHAP));
								} else if ("EAP".equalsIgnoreCase(authMethod)) {
									supportedMethodsId.add(String.valueOf(AuthMethods.EAP));
								}
							}
							authenticationHandlerData.setSupportedAuthenticationMethods(supportedMethodsId);
							handlerList.add(authenticationHandlerData);
							
						} else if (diameterApplicationHandlerData instanceof DiameterSubscriberProfileRepositoryDetails) {

							DiameterSubscriberProfileRepositoryDetails diameterSubscriberProfileRepositoryDetails = (DiameterSubscriberProfileRepositoryDetails)diameterApplicationHandlerData;
							
							if(diameterSubscriberProfileRepositoryDetails.getDriverDetails() != null ){
								
								DiameterProfileDriverDetails profileDriverDetails = diameterSubscriberProfileRepositoryDetails.getDriverDetails();
								
								if( profileDriverDetails != null ){
									
									List<PrimaryDriverDetail> primaryDriverDetailList = new ArrayList<PrimaryDriverDetail>();
									
									if (Collectionz.isNullOrEmpty(profileDriverDetails.getPrimaryDriverGroup()) == false) {
										
										for(PrimaryDriverDetail details : profileDriverDetails.getPrimaryDriverGroup()) {
											
											String driverId = servicePolicyBlmanager.getDriverId(details.getDriverInstanceId());
											details.setDriverInstanceId(driverId);
											primaryDriverDetailList.add(details);
										}
										
									}
									
									List<SecondaryAndCacheDriverDetail> secondaryAndCacheDriverDetailList = new ArrayList<SecondaryAndCacheDriverDetail>();
									
									if (Collectionz.isNullOrEmpty(profileDriverDetails.getSecondaryDriverGroup()) == false) {
										
										for(SecondaryAndCacheDriverDetail details : profileDriverDetails.getSecondaryDriverGroup()) {
											
											String driverId = servicePolicyBlmanager.getDriverId(details.getSecondaryDriverId());
											details.setSecondaryDriverId(driverId);
											secondaryAndCacheDriverDetailList.add(details);
										}
										
										profileDriverDetails.setSecondaryDriverGroup(secondaryAndCacheDriverDetailList);
									}
									
									List<AdditionalDriverDetail> additionalDriverList = new ArrayList<AdditionalDriverDetail>();
									
									if (Collectionz.isNullOrEmpty(profileDriverDetails.getAdditionalDriverList()) == false) {
										
										for(AdditionalDriverDetail details : profileDriverDetails.getAdditionalDriverList()) {
											
											String driverId = servicePolicyBlmanager.getDriverId(details.getDriverId());
											details.setDriverId(driverId);
											additionalDriverList.add(details);
										}
										
										profileDriverDetails.setAdditionalDriverList(additionalDriverList);
									}
									profileDriverDetails.setPrimaryDriverGroup(primaryDriverDetailList);
							
								}	
								diameterSubscriberProfileRepositoryDetails.setDriverDetails(profileDriverDetails);
							}
							handlerList.add(diameterSubscriberProfileRepositoryDetails);
						} else if (diameterApplicationHandlerData instanceof DiameterConcurrencyHandlerData) {
							
							DiameterConcurrencyHandlerData concurrencyHandlerData=(DiameterConcurrencyHandlerData) diameterApplicationHandlerData;
							
							DiameterConcurrencyBLManager blManager = new DiameterConcurrencyBLManager();
							DiameterConcurrencyData conncurrancyData = blManager.getDiameterConcurrencyDataByName(concurrencyHandlerData.getDiaConcurrencyId());
							
							concurrencyHandlerData.setDiaConcurrencyId(String.valueOf(conncurrancyData.getDiaConConfigId()));
							
							handlerList.add(concurrencyHandlerData);
				
						}else if (diameterApplicationHandlerData instanceof DiameterCDRGenerationHandlerData) {
							DiameterCDRGenerationHandlerData cdrGenHandlerData = (DiameterCDRGenerationHandlerData)diameterApplicationHandlerData;
						
							List<DiameterCDRHandlerEntryData> entries = cdrGenHandlerData.getEntries();
							
							if( Collectionz.isNullOrEmpty(entries) == false ){
								
								for( DiameterCDRHandlerEntryData entryData : entries ){
									
									if( Collectionz.isNullOrEmpty(entryData.getDriverDetails().getPrimaryDriverGroup()) == false ){
										PrimaryDriverDetail primaryDriverDetail = entryData.getDriverDetails().getPrimaryDriverGroup().get(0);
										
										if( primaryDriverDetail != null ){
											String primaryDriverId = servicePolicyBlmanager.getDriverId(primaryDriverDetail.getDriverInstanceId());
											primaryDriverDetail.setDriverInstanceId(primaryDriverId);
										}
									}
									
									if( Collectionz.isNullOrEmpty(entryData.getDriverDetails().getSecondaryDriverGroup()) == false ){
										SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail = entryData.getDriverDetails().getSecondaryDriverGroup().get(0);

										if( secondaryAndCacheDriverDetail != null ){
											String secondaryDriverId = servicePolicyBlmanager.getDriverId(secondaryAndCacheDriverDetail.getSecondaryDriverId());
											secondaryAndCacheDriverDetail.setSecondaryDriverId(secondaryDriverId);
										}
									}
								}
							}
							handlerList.add(cdrGenHandlerData);
					} else if (diameterApplicationHandlerData instanceof DiameterSynchronousCommunicationHandlerData) {

						DiameterSynchronousCommunicationHandlerData communicationHandlerData = (DiameterSynchronousCommunicationHandlerData)diameterApplicationHandlerData;
						
						List<DiameterExternalCommunicationEntryData> entries = communicationHandlerData.getEntries();
						if(entries != null){
							if( Collectionz.isNullOrEmpty(entries) == false ){
								for( DiameterExternalCommunicationEntryData entryData : entries ){
									
									String peerid = getPeerIdFromDiameterPeerGroupOrRadiusESIGroup(entryData.getPeerGroupId());
									entryData.setPeerGroupId(peerid);
								}
							}
						}
						handlerList.add(communicationHandlerData);
					}else if (diameterApplicationHandlerData instanceof DiameterBroadcastCommunicationHandlerData) {

						DiameterBroadcastCommunicationHandlerData communicationHandlerData = (DiameterBroadcastCommunicationHandlerData)diameterApplicationHandlerData;
						
						List<DiameterBroadcastCommunicationEntryData> entries = communicationHandlerData.getEntries();
						
						if( Collectionz.isNullOrEmpty(entries) == false ) {
							
							for( DiameterBroadcastCommunicationEntryData entryData : entries ){
								
								String peerid = getPeerIdFromDiameterPeerGroupOrRadiusESIGroup(entryData.getPeerGroupId());
								entryData.setPeerGroupId(peerid);
							}
						}
						
					}
				}
			}
				List<DiameterApplicationHandlerData> postResHandlersDataList = new ArrayList<DiameterApplicationHandlerData>();
				List<DiameterApplicationHandlerData> postResHandlers = new ArrayList<DiameterApplicationHandlerData>();
				DiameterPostResponseHandlerData postResponseHandlerData = new  DiameterPostResponseHandlerData();
				if(data.getPostResponseHandlerData() != null){
					postResHandlersDataList = data.getPostResponseHandlerData().getHandlersData();
				}
				
				if (Collectionz.isNullOrEmpty(postResHandlersDataList) == false) {
					
					for (DiameterApplicationHandlerData diameterApplicationHandlerData : postResHandlersDataList) {
						
						if (diameterApplicationHandlerData instanceof DiameterCDRGenerationHandlerData) {
							
							DiameterCDRGenerationHandlerData cdrGenHandlerData = (DiameterCDRGenerationHandlerData)diameterApplicationHandlerData;
							
							List<DiameterCDRHandlerEntryData> entries = cdrGenHandlerData.getEntries();
							
							if( Collectionz.isNullOrEmpty(entries) == false ){
								
								for( DiameterCDRHandlerEntryData entryData : entries ){
									
									if( Collectionz.isNullOrEmpty(entryData.getDriverDetails().getPrimaryDriverGroup()) == false ){
										PrimaryDriverDetail primaryDriverDetail = entryData.getDriverDetails().getPrimaryDriverGroup().get(0);
										
										if( primaryDriverDetail != null ){
											String primaryDriverId = servicePolicyBlmanager.getDriverId(primaryDriverDetail.getDriverInstanceId());
											primaryDriverDetail.setDriverInstanceId(primaryDriverId);
										}
									}
									
									if( Collectionz.isNullOrEmpty(entryData.getDriverDetails().getSecondaryDriverGroup()) == false ){
										SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail = entryData.getDriverDetails().getSecondaryDriverGroup().get(0);

										if( secondaryAndCacheDriverDetail != null ){
											String secondaryDriverId = servicePolicyBlmanager.getDriverId(secondaryAndCacheDriverDetail.getSecondaryDriverId());
											secondaryAndCacheDriverDetail.setSecondaryDriverId(secondaryDriverId);
										}
									}
								}
							}

							postResHandlers.add(cdrGenHandlerData);
						} 
						
					}
					postResponseHandlerData.getHandlersData().addAll(postResHandlers);
				}
				
				
				commandCodeFlowData.getHandlersData().addAll(handlerList);
				commandCodeFlowDataList.add(commandCodeFlowData);
		}
		}
		
		/* Generate XML */
		JAXBContext jaxbContext = JAXBContext.newInstance(TGPPServerPolicyData.class);
	    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    java.io.StringWriter xmlObj = new StringWriter();
	    jaxbMarshaller.marshal(tgppServerPolicyData,xmlObj);
		
	    String xmlDatas = xmlObj.toString().trim();
	    tgppAAAPolicyData.setTgppAAAPolicyXml(xmlDatas.getBytes());
	    Logger.getLogger().info(MODULE, "******************************* XML Data*******************************");
	    Logger.getLogger().info(MODULE, xmlDatas);
	    Logger.getLogger().info(MODULE, "***********************************************************************");
	    Logger.getLogger().info(MODULE, "XML Length : "+ xmlDatas.length());
	    Logger.getLogger().info(MODULE, "***********************************************************************");
	    return tgppAAAPolicyData;
		
	}

	/**
	 * This method used for updating active or inactive status
	 * @param asList
	 * @param showStatusId
	 * @throws DataManagerException
	 */
	public void updateTgppAAAServicePolicyStatus(List<String> asList,String showStatusId) throws DataManagerException {
		
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		TGPPAAAPolicyDataManager tgppaaapolicyDataManager = getTgppAAAPolicyDataManager(session);
		if (tgppaaapolicyDataManager==null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {				     
			session.beginTransaction();
			tgppaaapolicyDataManager.updateStatus(asList, showStatusId);
			commit(session);
		} catch(DataManagerException e) {
        	rollbackSession(session);
        	throw e;
		} catch(Exception e) {
			e.printStackTrace();
	       	rollbackSession(session);
        	throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * This method fetch the list of service policy which are active 
	 * @return
	 * @throws DataManagerException
	 */
	public List<TGPPAAAPolicyData> searchActiveTGPPAAAServicePolicy() throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		TGPPAAAPolicyDataManager tgppAAAPolicyDataManager = getTgppAAAPolicyDataManager(session);
		if (tgppAAAPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {				     
		    List<TGPPAAAPolicyData> policyList = tgppAAAPolicyDataManager.searchActiveTGPPAAAServicePolicy();
			return policyList;
		} catch(DataManagerException e) {
        	throw e;
		} catch(Exception e) {
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	/**
	 * This is used for get all TGPP service policy included with Active and Inactive status .
	 * @return List of TGPP policy
	 * @throws DataManagerException
	 */
	public List<TGPPAAAPolicyData> getTGPPAAAServicePolicyList() throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		TGPPAAAPolicyDataManager tgppAAAPolicyDataManager = getTgppAAAPolicyDataManager(session);
		if (tgppAAAPolicyDataManager==null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {				     
			List<TGPPAAAPolicyData> policyList = tgppAAAPolicyDataManager.getTGPPAAAServicePolicyList();
			return policyList;
		} catch(DataManagerException e) {
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	/**
	 * This method is used to get binded plugin-names with 3gpp service policies .
	 * @param tagName Name of tag that contains plugin name
	 * @return set of binded plugin-names .
	 * @throws DataManagerException
	 */
	public Set<String> getBindedPluginNames(String tagName) throws DataManagerException {
		Set<String> bindendPluginNames = new HashSet<String>();
		try {
			List<TGPPAAAPolicyData> tgppServicePolicyDatasList = getTGPPAAAServicePolicyList();
			if (Collectionz.isNullOrEmpty(tgppServicePolicyDatasList) == false) {
				for (TGPPAAAPolicyData tgppServicePolicyData : tgppServicePolicyDatasList) {
					String xmlDatas = new String(tgppServicePolicyData.getTgppAAAPolicyXml());
					bindendPluginNames.addAll(XmlNodeUtility.getElementValueByTagName(xmlDatas,tagName));
				}
			}
		} catch(DataManagerException e) {
			throw e;
		}
		return bindendPluginNames;
	}
	
	/**
	 * This method return the values in the tag-names of 3GPP Service Policy 
	 * @param tagNames List<String> that contains tag-name
	 * @return Set<String> values of tag-name
	 * @throws DataManagerException
	 */
	public Set<String> getBindedValues(List<String> tagNames) throws DataManagerException {
		Set<String> bindendPluginNames = new HashSet<String>();
		try {
			List<TGPPAAAPolicyData> tgppServicePolicyDatasList = getTGPPAAAServicePolicyList();
			if (Collectionz.isNullOrEmpty(tgppServicePolicyDatasList) == false) {
				for (TGPPAAAPolicyData tgppServicePolicyData : tgppServicePolicyDatasList) {
					String xmlDatas = new String(tgppServicePolicyData.getTgppAAAPolicyXml());
					bindendPluginNames.addAll(XmlNodeUtility.getElementValueByTagName(xmlDatas,tagNames));
				}
			}
			return bindendPluginNames;
		} catch (DataManagerException e){
			throw e;
		}
	}
}
