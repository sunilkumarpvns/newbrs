package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import static com.elitecore.commons.base.Strings.repeat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AdditionalDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.HandlerConstants;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.PluginEntryData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.PrimaryDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.SecondaryAndCacheDriverDetail;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.PluginTypesConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.constants.ServiceTypeConstants;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "command-code-flow")
@ValidObject
@XmlType(propOrder = {"name", "commandCode", "interfaceId", "handlersData", "postResponseHandlerData"})
public class CommandCodeFlowData implements Differentiable, Validator{

	@NotEmpty(message = "Please Enter Command Code Value")
	private String commandCode;

	@NotEmpty(message = "Please Enter valid Interface value")
	private String interfaceId;

	@NotEmpty(message = "Command code flow name cannot be empty")
	private String name;

	@XmlElementRefs({
		@XmlElementRef(name = "authentication-handler", type = DiameterAuthenticationHandlerData.class),
		@XmlElementRef(name = "authorization-handler", type = DiameterAuthorizationHandlerData.class),
		@XmlElementRef(name = "user-profile-repository", type = DiameterSubscriberProfileRepositoryDetails.class),
		@XmlElementRef(name = "plugin-handler", type = DiameterPluginHandlerData.class),
		@XmlElementRef(name = "proxy-handler", type = DiameterSynchronousCommunicationHandlerData.class),
		@XmlElementRef(name = "broadcast-handler", type = DiameterBroadcastCommunicationHandlerData.class),
		@XmlElementRef(name = "cdr-generation", type = DiameterCDRGenerationHandlerData.class),
		@XmlElementRef(name = "dia-concurrency-handler", type = DiameterConcurrencyHandlerData.class)
	})

	@Valid
	@Size(min = 1, message = "Please Add atleast one handler")
	private List<DiameterApplicationHandlerData> handlersData = new ArrayList<DiameterApplicationHandlerData>();

	@Valid
	private DiameterPostResponseHandlerData postResponseHandlerData;

	@XmlElement(name = "command-code")
	public String getCommandCode() {
		return commandCode;
	}

	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}

	@XmlElement(name = "interface-id")
	public String getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DiameterApplicationHandlerData> getHandlersData() {
		return handlersData;
	}

	@XmlElement(name = "post-response-handler")
	public DiameterPostResponseHandlerData getPostResponseHandlerData() {
		return postResponseHandlerData;
	}

	public void setPostResponseHandlerData(DiameterPostResponseHandlerData postResponseHandlerData) {
		this.postResponseHandlerData = postResponseHandlerData;
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println("Command code(s): " + commandCode + "| Interface id(s): " + interfaceId);
		out.println(repeat("-", 70));
		out.println("Command code flow handlers");
		for (DiameterApplicationHandlerData handlerData : handlersData) {
			out.println(handlerData);
		}

		if (postResponseHandlerData != null) {
			out.println(repeat("-", 70));
			out.println("Post response flow");
			out.println(postResponseHandlerData);
		}

		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Command Code", commandCode);
		object.put("Interface", interfaceId);

		List<String> flowOrderList = new  ArrayList<String>();
		if(Collectionz.isNullOrEmpty(handlersData) == false){
			for(DiameterApplicationHandlerData diameterApplicationHandlerData : handlersData){
				object.put(diameterApplicationHandlerData.getHandlerName(), diameterApplicationHandlerData.toJson());
				flowOrderList.add(diameterApplicationHandlerData.getHandlerName());
			}
		}

		object.put("Post Response Command Code Flow", postResponseHandlerData.toJson());
		object.put(name + " Handlers Order", EliteUtility.getServicePolicyOrder(flowOrderList));
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		int count = 0;
		final String ADDTIONAL_DRIVER = "Additional Driver";
		final String SECONDARY_DRIVER = "Secondary Driver";
		final String PRIMARY_DRIVER = "Primary Driver";
		final String PROFILE_LOOKUP_HANDLER = "Profile Lookup Handler";
		boolean isValid = true;
		
		Set<String> handlerNameSet = new HashSet<String>();
		for (DiameterApplicationHandlerData handler : getHandlersData()) {		
			if (handler instanceof DiameterAuthenticationHandlerData) {
				count++;
			}
			
			if (handler instanceof DiameterAuthenticationHandlerData) {
				boolean isHandlerNameValid = handleValidation(handler.getHandlerName(), handler.getEnabled(), 
						HandlerConstants.AUTHENTICATION_HANDLER, handlerNameSet, context);
				if(isHandlerNameValid == false){
					isValid = false;
				}
			} else if (handler instanceof DiameterAuthorizationHandlerData) {
				boolean isHandlerNameValid = handleValidation(handler.getHandlerName(), handler.getEnabled(), 
						HandlerConstants.AUTHORIZATION_HANDLER, handlerNameSet, context);
				if(isHandlerNameValid == false){
					isValid = false;
				}
			} else if (handler instanceof DiameterSubscriberProfileRepositoryDetails) {
				boolean isHandlerNameValid = handleValidation(handler.getHandlerName(), handler.getEnabled(), 
						HandlerConstants.PROFILE_LOOKUP_HANDLER, handlerNameSet, context);
				if(isHandlerNameValid == false){
					isValid = false;
				}

				DiameterProfileDriverDetails allDriver = ((DiameterSubscriberProfileRepositoryDetails) handler).getDriverDetails();

				if(Collectionz.isNullOrEmpty(allDriver.getPrimaryDriverGroup()) == false){

					//Validate Primary Driver
					Set<String> priamryDriverNames = new HashSet<String>();
					for (PrimaryDriverDetail driverName : allDriver.getPrimaryDriverGroup()) {

						if(Strings.isNullOrBlank(driverName.getDriverInstanceId())){
							RestUtitlity.setValidationMessage(context, "Primary Driver name must be specified in " + PROFILE_LOOKUP_HANDLER);
							isValid = false;
						} else {
							boolean isAdded = priamryDriverNames.add(driverName.getDriverInstanceId());
							if(!isAdded){
								RestUtitlity.setValidationMessage(context, "Duplicate driver: " + driverName.getDriverInstanceId() + " found in Primary Driver Group of " + PROFILE_LOOKUP_HANDLER);
								isValid = false;
							} 
						}
					}
					if(validateProfileLookupDrivers(context,priamryDriverNames,PRIMARY_DRIVER) == false){
						isValid = false;
					}
				} else {
					RestUtitlity.setValidationMessage(context, "Provide at least one primary driver in " + PROFILE_LOOKUP_HANDLER+ " for Command Code: " + commandCode);
					isValid = false;
				}

				if(Collectionz.isNullOrEmpty(allDriver.getSecondaryDriverGroup()) == false){

					//Validate Secondary Driver
					Set<String> secondaryDriverNames = new HashSet<String>();
					for (SecondaryAndCacheDriverDetail driverName : allDriver.getSecondaryDriverGroup()) {
						if(Strings.isNullOrBlank(driverName.getSecondaryDriverId()) == false){
							boolean isAdded = secondaryDriverNames.add(driverName.getSecondaryDriverId());
							if(!isAdded){
								RestUtitlity.setValidationMessage(context, "Duplicate driver: " + driverName.getSecondaryDriverId() + " found in Secondary Driver Group of " + PROFILE_LOOKUP_HANDLER);
								isValid = false;
							}
						}
					}
					if(validateProfileLookupDrivers(context,secondaryDriverNames,SECONDARY_DRIVER) == false){
						isValid = false;
					}
				}


				if(Collectionz.isNullOrEmpty(allDriver.getAdditionalDriverList()) == false){

					////Validate Additional Driver
					Set<String> addtionalDriverNames = new HashSet<String>();
					for (AdditionalDriverDetail driverName : allDriver.getAdditionalDriverList()) {
						if(Strings.isNullOrBlank(driverName.getDriverId()) == false){
							boolean isAdded = addtionalDriverNames.add(driverName.getDriverId());
							if(!isAdded){
								RestUtitlity.setValidationMessage(context, "Duplicate driver: " + driverName.getDriverId() + " found in Additional Driver Group of " + PROFILE_LOOKUP_HANDLER);
								isValid = false;
							}
						}
					}
					if(validateProfileLookupDrivers(context,addtionalDriverNames,ADDTIONAL_DRIVER) == false){
						isValid = false;
					}
				}

				//validate driver in Primary Group,Secondary Group and Additional Group is DuplicateOrNot
				if(checkDriverNameIsDuplicateOrNot(context,allDriver,PROFILE_LOOKUP_HANDLER) == false){
					isValid = false;
				}

			} else if (handler instanceof DiameterPluginHandlerData) { 
				DiameterPluginHandlerData pluginData = (DiameterPluginHandlerData)handler;
				PluginBLManager blManager = new PluginBLManager();
				List<PluginEntryData> configuredPlugins = pluginData.getPluginEntries();

				for (PluginEntryData pluginEntryData : configuredPlugins) {
					String pluginName = pluginEntryData.getPluginName();
					try {
						if (Strings.isNullOrBlank(pluginName)) {
							isValid = false;
							RestUtitlity.setValidationMessage(context, "Plugin name must be specified for: " + handler.getHandlerName());
						} else {
							PluginInstData data = blManager.getPluginInstDataByName(pluginName);
							String pluginType = data.getPluginTypeId();
							if (PluginTypesConstants.UNIVERSAL_DIAMETER_PLUGIN.equals(pluginType) == false && 
									PluginTypesConstants.DIAMETER_GROOVY_PLUGIN.equals(pluginType) == false &&    
									PluginTypesConstants.DIAMETER_TRANSACTION_LOGGER.equals(pluginType) == false) {
								isValid = false;
								RestUtitlity.setValidationMessage(context, "Plugin: " + pluginName + " is not of diameter type");
							}
						}
					} catch (DataManagerException e) {
						isValid = false;
						RestUtitlity.setValidationMessage(context, "Plugin: " + pluginName + " does not exist");
					}
				}

				boolean isHandlerNameValid = handleValidation(handler.getHandlerName(), handler.getEnabled(), 
						HandlerConstants.PLUGIN_HANDLER, handlerNameSet, context);
				if(isHandlerNameValid == false){
					isValid = false;
				}

			}else if(handler instanceof DiameterSynchronousCommunicationHandlerData){
				DiameterSynchronousCommunicationHandlerData handlerData = (DiameterSynchronousCommunicationHandlerData)handler;
				
				if (handlerData != null) {
					List<DiameterExternalCommunicationEntryData> entries = handlerData.getEntries();
					if(Collectionz.isNullOrEmpty(entries) == false){

						boolean isValidProxyHandler =  checkValidExternalCommunicationEntryData(entries, context);

						if( isValidProxyHandler == false ){
							isValid = false;
						}
					}
					
					Long resultCodeOnNoEntrySelected = handlerData.getResultCodeOnNoEntrySelected();
					String protocol = handlerData.getProtocol();

					isValid = checkResultCodeEntry(context, resultCodeOnNoEntrySelected, protocol, handler.getHandlerName());
				}
				
				boolean isHandlerNameValid = handleValidation(handler.getHandlerName(), handler.getEnabled(), 
						HandlerConstants.PROXY_HANDLER, handlerNameSet, context);
				if(isHandlerNameValid == false){
					isValid = false;
				}
			}else if(handler instanceof DiameterBroadcastCommunicationHandlerData){
				DiameterBroadcastCommunicationHandlerData handlerData = (DiameterBroadcastCommunicationHandlerData)handler;
				if (handlerData != null) {

					List<DiameterBroadcastCommunicationEntryData> entries = handlerData.getEntries();
					if(Collectionz.isNullOrEmpty(entries) == false){

						boolean isValidProxyHandler = checkValidAsynchCommunicationEntryData(entries, context);

						if (isValidProxyHandler == false) {
							isValid = false;
						}
					}
					
					Long resultCodeOnNoEntrySelected = handlerData.getResultCodeOnNoEntrySelected();
					String protocol = handlerData.getProtocol();
					
					isValid = checkResultCodeEntry(context, resultCodeOnNoEntrySelected, protocol, handler.getHandlerName());
				}
				
				boolean isHandlerNameValid = handleValidation(handler.getHandlerName(), handler.getEnabled(), 
						HandlerConstants.BROADCAST_HANDLER, handlerNameSet, context);
				if(isHandlerNameValid == false){
					isValid = false;
				}
			}else if(handler instanceof DiameterCDRGenerationHandlerData){
				DiameterCDRGenerationHandlerData handlerData = (DiameterCDRGenerationHandlerData)handler;
				if(handlerData != null){
					List<DiameterCDRHandlerEntryData> entries = handlerData.getEntries();
					if(Collectionz.isNullOrEmpty(entries) == false){

						boolean isValidProxyHandler = checkCDRHandlerEntryData(entries, context);

						if (isValidProxyHandler == false) {
							isValid = false;
						}
					}
				}
				
				boolean isHandlerNameValid = handleValidation(handler.getHandlerName(), handler.getEnabled(), 
						HandlerConstants.CDR_HANDLER, handlerNameSet, context);
				if(isHandlerNameValid == false){
					isValid = false;
				}
			}
		}

		if (count > 1) {
			RestUtitlity.setValidationMessage(context, "Only single Authentication Handler is allowed");
			isValid = false;
		}
		return isValid;
	}

	private boolean checkResultCodeEntry(ConstraintValidatorContext context, Long resultCodeOnNoEntrySelected, String protocol, String handlerName) {
		boolean isValid = true;
		if (ConfigConstant.RADIUS.equals(protocol)) {
			if (resultCodeOnNoEntrySelected != null) {
				RestUtitlity.setValidationMessage(context, "'Result code when no entry selected' is not allowed for: " + handlerName);
				isValid = false;
			}
			
			
		} else if (ConfigConstant.DIAMETER.equals(protocol)) {
			if (resultCodeOnNoEntrySelected == null || resultCodeOnNoEntrySelected < 0) {
				RestUtitlity.setValidationMessage(context, "'Result code when no entry selected' must be specified for: " + handlerName);
				isValid = false;
			}
		}
		return isValid;
	}
	
	private boolean validateProfileLookupDrivers(ConstraintValidatorContext context, Set<String> driverNames,String driverType) {
		boolean isValid = true;
		DriverBLManager driverBLManager = new DriverBLManager();
		
		for (String driverName : driverNames) {
			try {
				DriverInstanceData driverData = driverBLManager.getDriverInstanceByName(driverName);
				DriverTypeData driverTypeData = driverData.getDriverTypeData();
				if ((ServiceTypeConstants.NAS_AUTH_APPLICATION == driverTypeData.getServiceTypeId()) == false) {
					RestUtitlity.setValidationMessage(context, "Specified driver: " + driverName + " in " + driverType + " is not Diameter Auth Type driver");
					isValid = false;
				} 
			} catch (DataManagerException e) {
				RestUtitlity.setValidationMessage(context,  driverType + " [ "  + driverName + " ] configured in Profile lookup handler does not exist");
				isValid = false;
			}
		}
		return isValid;
	}


	private boolean checkValidAsynchCommunicationEntryData(List<DiameterBroadcastCommunicationEntryData> entries,
			ConstraintValidatorContext context) {
		boolean isValid = true;
	    String flowType = "Broadcast(Parallel)";
		for(DiameterBroadcastCommunicationEntryData asynchCommunicationEntryData : entries ){
			if (asynchCommunicationEntryData != null) {
				
				boolean isValidEsi = checkValidESI(asynchCommunicationEntryData.getPeerGroupId(), context, flowType);
				boolean isValidTranslationMapping = checkValidTransaltionMapping(asynchCommunicationEntryData.getTranslationMapping(), context, flowType);
				
				if( isValidEsi == false || isValidTranslationMapping == false){
					isValid = false;
				}
			}
		}
		
		return isValid;
	}

	private boolean checkCDRHandlerEntryData(List<DiameterCDRHandlerEntryData> entries,
			ConstraintValidatorContext context) {
		boolean isValid = true;
		final String CDR_HANDLER = "CDR Handler";

		if( Collectionz.isNullOrEmpty(entries) == false ){

			for(DiameterCDRHandlerEntryData cdrHandlerEntryData : entries){

				if(cdrHandlerEntryData.getDriverDetails() != null){

					DiameterProfileDriverDetails driversDetails = cdrHandlerEntryData.getDriverDetails();
					if(Collectionz.isNullOrEmpty(driversDetails.getPrimaryDriverGroup()) == false){

						List<PrimaryDriverDetail> primaryDriverDataList = driversDetails.getPrimaryDriverGroup();
						List<SecondaryAndCacheDriverDetail> cacheDriverDetails = driversDetails.getSecondaryDriverGroup();
						DriverBLManager driverBLManager = new DriverBLManager();

						try{
							List<DriverInstanceData> listOfDriver = driverBLManager.getDriverInstanceList(ServiceTypeConstants.NAS_ACCT_APPLICATION);
							
							for(PrimaryDriverDetail primaryDriverDetail : primaryDriverDataList){

								String primaryDriverName = primaryDriverDetail.getDriverInstanceId();

								if( Strings.isNullOrBlank(primaryDriverName) ){
									RestUtitlity.setValidationMessage(context, "Primary Driver name must be specified in CDR handler");
									isValid = false;
								}else{
									boolean isDriverMatched = false;

									for(DriverInstanceData driverInstanceData : listOfDriver){
										if(driverInstanceData.getName().equals(primaryDriverName.trim())){
											isDriverMatched = true;
											break;
										}
									}

									if( isDriverMatched == false ){
										RestUtitlity.setValidationMessage(context, "Invalid type of Primary Driver name in CDR handler");
										isValid = false;
									}
								}
							}

							if( Collectionz.isNullOrEmpty(cacheDriverDetails) == false ){

								for(SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail : cacheDriverDetails){
									String secondaryDriverName = secondaryAndCacheDriverDetail.getSecondaryDriverId();

									if( Strings.isNullOrBlank(secondaryDriverName) == false ){
										boolean isDriverMatched = false;

										for(DriverInstanceData driverInstanceData : listOfDriver){
											if(driverInstanceData.getName().equals(secondaryDriverName.trim())){
												isDriverMatched = true;
												break;
											}
										}

										if( isDriverMatched == false ){
											RestUtitlity.setValidationMessage(context, "Invalid type of Secondary Driver name in CDR handler");
											isValid = false;
										}
									}
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}

					} else {
						RestUtitlity.setValidationMessage(context, "Provide at least one primary driver in " + CDR_HANDLER+ " for Command Code: " + commandCode);
						isValid = false;
					}
					
					if(isValid == true){
						isValid = checkDriverNameIsDuplicateOrNot(context,cdrHandlerEntryData.getDriverDetails(),CDR_HANDLER);
					}
				}else{
					RestUtitlity.setValidationMessage(context, "Please specify drivers details in CDR handler");
					isValid = false;
				}
			}
		}else{
			RestUtitlity.setValidationMessage(context, "Please specify drivers details in CDR handler");
			isValid = false;
		}
		return isValid;
	}

	private boolean checkValidExternalCommunicationEntryData(List<DiameterExternalCommunicationEntryData> entries,
			ConstraintValidatorContext context) {
		boolean isValid = true;
		String flowType = "Proxy(Sequential)";
		
		for(DiameterExternalCommunicationEntryData externalCommunicationEntryData : entries){
			
			if (externalCommunicationEntryData != null) {
				
				boolean isValidEsi = checkValidESI(externalCommunicationEntryData.getPeerGroupId(), context, flowType);
				boolean isValidTranslationMapping = checkValidTransaltionMapping(externalCommunicationEntryData.getTranslationMapping(), context, flowType);
				if( isValidEsi == false || isValidTranslationMapping == false){
					isValid = false;
				}
			}
			
		}
		return isValid;
	}

	private boolean checkValidTransaltionMapping(String translationMapping,
			ConstraintValidatorContext context, String flowType) {
		boolean isValid = true;
		try {
			
			if( Strings.isNullOrBlank(translationMapping) == false){
				TranslationMappingConfBLManager blManager = new TranslationMappingConfBLManager();
				List<TranslationMappingConfData> d2dTranslationMapping = blManager.getDiaToDiaTranslationMapping();
				d2dTranslationMapping.addAll(blManager.getDiaToRadTranslationMappingList());
				
				boolean isValidTranslationMapping = false;
				for (TranslationMappingConfData data : d2dTranslationMapping) {
					isValidTranslationMapping = data.getName().equals(translationMapping) ? true : false;
					if (isValidTranslationMapping == true) {
						break;
					}
				}
				
				if (isValidTranslationMapping == false) {
					CopyPacketTransMapConfBLManager copyPktBLManager = new CopyPacketTransMapConfBLManager();
					List<CopyPacketTranslationConfData> d2dCopyPktMapping = copyPktBLManager.getCopyPacketTransMapConfigList(TranslationMappingConfigConstants.RADIUS, TranslationMappingConfigConstants.RADIUS);
					d2dCopyPktMapping.addAll(copyPktBLManager.getCopyPacketTransMapConfigList(TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.DIAMETER));
					
					boolean isValidCopyPacketMapping = false;
					for (CopyPacketTranslationConfData cpPktdata : d2dCopyPktMapping) {
						isValidCopyPacketMapping = cpPktdata.getName().equals(translationMapping) ? true : false;

						if (isValidCopyPacketMapping == true) {
							break;
						}	
					}
					
					if (isValidTranslationMapping == false && isValidCopyPacketMapping == false) {
						RestUtitlity.setValidationMessage(context, "Translation or Copy Packet Mapping: " + translationMapping + " is not of type Diameter To Diameter or Diameter To Radius in " + flowType);
						isValid = false;
					}
				}
			}
		} catch (DataManagerException e) {
			e.printStackTrace();
			RestUtitlity.setValidationMessage(context, "Failed to retrive Translation mapping in "+flowType+" handler");
			isValid = false;
		}
		return isValid;
	}

	private boolean checkValidESI(String groupId, ConstraintValidatorContext context, String flowType) {
		boolean isValid = true;

		if (Strings.isNullOrBlank(groupId) == false) {
			try {
				DiameterPeerGroupBLManager peerGroupBLManager = new DiameterPeerGroupBLManager();
				peerGroupBLManager.getDiameterPeerGroupByName(groupId);
			} catch (DataManagerException de) {
				try {
					RadiusESIGroupBLManager radiusESIGroupBLManager = new RadiusESIGroupBLManager();
					radiusESIGroupBLManager.getRadiusESIGroupByName(groupId);
				}catch(Exception e){
					RestUtitlity.setValidationMessage(context, "Configured Peer/Radius ESI Group ["+groupId+"] is invalid in " + flowType+ " handler");
					isValid = false;
				}
			}
		} else {
			RestUtitlity.setValidationMessage(context, "Atleast one Peer Group must be specified in Peer Group of " + flowType+ " handler");
			isValid = false;
		}

		return isValid;
	}
	
	private boolean handleValidation(String handlerName, String enabledHandler, String handlerType,
			Set<String> handlerNameList, ConstraintValidatorContext context) {
		boolean isValid = true;
//		
		if( Strings.isNullOrBlank(handlerName) ){
			RestUtitlity.setValidationMessage(context, handlerType + " name must be specified in Authentication Service flow");
			isValid = false;
		} else {
			boolean isDuplicate = handlerNameList.add(handlerName);

			if(isDuplicate == false){
				
				RestUtitlity.setValidationMessage(context, "Duplicate Handler Name found in Authetication Service flow");
				isValid = false;
			} else {
				
				Pattern BOOLEAN_REGEX = Pattern.compile(RestValidationMessages.BOOLEAN_REGEX);
				
				if (Strings.isNullOrBlank(enabledHandler)) {
					RestUtitlity.setValidationMessage(context, "[ " + handlerName + " ] Handler's enabled/disable value must be specified");
					isValid = false;
				}else if(BOOLEAN_REGEX.matcher(enabledHandler).matches() == false){
					
					RestUtitlity.setValidationMessage(context,
							"Invalid value of enabled/disable field of [ " + handlerName+" ] handler, It could be 'true' or 'false'");
					isValid = false;
				}
			}
		}
		
		return isValid;
	}
	
	private boolean checkDriverNameIsDuplicateOrNot(ConstraintValidatorContext context, DiameterProfileDriverDetails allDrivers, String handler){
		boolean isValid = true;
		for (PrimaryDriverDetail primaryDriver : allDrivers.getPrimaryDriverGroup()) {

			for (SecondaryAndCacheDriverDetail secondaryDriver : allDrivers.getSecondaryDriverGroup()) {

				if (Strings.isNullOrBlank(secondaryDriver.getSecondaryDriverId()) == false && primaryDriver.getDriverInstanceId().equals(secondaryDriver.getSecondaryDriverId())) {
					RestUtitlity.setValidationMessage(context, " In "+ handler +" Primary Group and Secondary Group does not have same driver");
					isValid = false;
				}

				for (AdditionalDriverDetail additionalDriver : allDrivers.getAdditionalDriverList()) {

					if (Strings.isNullOrBlank(additionalDriver.getDriverId()) == false && 
							primaryDriver.getDriverInstanceId().equals(additionalDriver.getDriverId())) {
						RestUtitlity.setValidationMessage(context, " In "+ handler +" Primary Group and Additional Group does not have same driver");
						isValid = false;
					}

					if (Strings.isNullOrBlank(secondaryDriver.getSecondaryDriverId()) == false &&
							Strings.isNullOrBlank(additionalDriver.getDriverId())== false && secondaryDriver.getSecondaryDriverId().equals(additionalDriver.getDriverId())) {
						RestUtitlity.setValidationMessage(context, " In "+ handler +" Secondary Group and Additional Group does not have same driver");
						isValid = false;
					}
				}
			}
		}
		return isValid;
	}
}