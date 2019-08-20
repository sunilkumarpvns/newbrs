package com.elitecore.aaa.rm.translator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.diameter.translators.BaseTranslator;
import com.elitecore.aaa.diameter.translators.DiameterSrcDstKeyword;
import com.elitecore.aaa.diameter.translators.DiameterTimestampKeyword;
import com.elitecore.aaa.diameter.translators.MathOpKeyword;
import com.elitecore.aaa.diameter.translators.PeerSequenceKeyword;
import com.elitecore.aaa.diameter.translators.ServerSequenceKeyword;
import com.elitecore.aaa.diameter.translators.SessionSequenceKeyword;
import com.elitecore.aaa.diameter.translators.StrOptKeyword;
import com.elitecore.aaa.diameter.translators.TimeStampKeyword;
import com.elitecore.aaa.diameter.translators.policy.TranslatorPolicy;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMapping;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMappingInfo;
import com.elitecore.aaa.diameter.translators.policy.impl.TranslatorPolicyImpl.DiameterParsedMappingInfoImpl;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.translators.Mac2TGppKeyword;
import com.elitecore.aaa.radius.translators.RadiusSrcKeyword;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.AvpTime;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class RadiusToDiameterTranslator extends BaseTranslator {
	
	private static final String MODULE = "RAD-DIA-TRNSLTR";
	public RadiusToDiameterTranslator(ServerContext context) {
		super(context);
	}

	@Override
	public String getFromId() {		
		return AAATranslatorConstants.RADIUS_TRANSLATOR;
	}

	@Override
	public String getToId() {
		return AAATranslatorConstants.DIAMETER_TRANSLATOR;
	}
	
	@Override
	public void init(TranslatorPolicyData policyData)
			throws InitializationFailedException {
		super.init(policyData);
		
		KeywordContext keywordContext = getKeywordContext();
		registerKeyword(new RadiusSrcKeyword(AAATranslatorConstants.SOURCE_REQUEST,keywordContext),false);
		registerKeyword(new DiameterSrcDstKeyword(AAATranslatorConstants.DESTINATION_REQUEST,keywordContext),false);
		

		StrOptKeyword strOptKeyword = new StrOptKeyword(TranslatorConstants.STROPT,keywordContext); 
	
		registerKeyword(strOptKeyword, true);
		registerKeyword(strOptKeyword, false);

	
		
		registerKeyword(new PeerSequenceKeyword(TranslatorConstants.SEQPEER,keywordContext), true);
		registerKeyword(new SessionSequenceKeyword(TranslatorConstants.SEQSESS,policyData.getName(), keywordContext,String.valueOf(RadiusAttributeConstants.ACCT_SESSION_ID)), true);
		registerKeyword(new ServerSequenceKeyword(TranslatorConstants.SEQSERV,keywordContext), true);
		

		registerKeyword(new DiameterTimestampKeyword(TranslatorConstants.TIMESTAMP,keywordContext), true);
		registerKeyword(new TimeStampKeyword(TranslatorConstants.TIMESTAMP,keywordContext), false);
		
		registerKeyword(new MathOpKeyword(TranslatorConstants.MATHOP, keywordContext), true);
		registerKeyword(new MathOpKeyword(TranslatorConstants.MATHOP, keywordContext), false);
	
		registerKeyword(new Mac2TGppKeyword(TranslatorConstants.MAC2TGPP, keywordContext), true);
		registerKeyword(new Mac2TGppKeyword(TranslatorConstants.MAC2TGPP, keywordContext), false);
	}

	private void applyDiameterToRadiusMappings(final DiameterPacket diameterResponse, RadServiceResponse radiusResponse, ParsedMapping mapping,TranslatorParams params){
		/* Applying Mapping Expression. */
		Map<String,String>defaultValues = mapping.getDefaultValuesMap();
		// Mapping nodes <0:1, "abc">
		// OR  			<0:1, 0:1>
		Map<String,String>mappingNodes = mapping.getMappingNodes();
		Map<String,String>valueNodes = mapping.getValueNodes();
		String avpId ;
		String value ;
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			avpId = entry.getKey();
			value = entry.getValue();
			if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(avpId)){
				if(value != null &&  value.length() > 0){
					int packetType = 0;
					if(isLiteral(value)){
						value = unquote(value);
						value = getValue(value, valueNodes);
						try{
							packetType = Integer.parseInt(value);
						}catch (NumberFormatException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
						}						
						radiusResponse.setPacketType(packetType);
					}else if (isKeyword(value)) {
						String strValue=null;
						strValue = getKeywordValue(value, params, false,new com.elitecore.core.commons.data.ValueProvider(){

							@Override
							public String getStringValue(String identifier) {
								if(identifier!=null){
									return diameterResponse.getAVPValue(identifier);
								}else {
									return null;	
								}
								
							}
							
						});
						if(strValue==null){
							strValue = defaultValues.get(avpId);
							if(strValue!=null && strValue.length()>0){
								strValue = getValue(strValue, valueNodes);
							}
						}
						if(strValue!=null){
							try{
								packetType = Integer.parseInt(value);
							}catch (NumberFormatException e) {
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
									LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
							}
						}	
						radiusResponse.setPacketType(packetType);
					}else{ //value is Avp
						IDiameterAVP avp = diameterResponse.getAVP(value);
						if(avp!= null){
							value = avp.getStringValue();
							value = getValue(value, valueNodes);
							try{
								packetType = Integer.parseInt(value);
								radiusResponse.setPacketType(packetType);
							}catch (NumberFormatException e) {
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
									LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
							}						
						}else{ //Avp is null so using default value 
							String avpValue = defaultValues.get(avpId);
							if(avpValue != null && avpValue.length() > 0){
								try{								
									avpValue = getValue(avpValue, valueNodes);
									packetType = Integer.parseInt(avpValue);
									radiusResponse.setPacketType(packetType);
								}catch (NumberFormatException e) {
									if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
										LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
								}
							}else{
								LogManager.getLogger().debug(MODULE,"Key: "+ value+" is not found in response object: ");
								LogManager.getLogger().debug(MODULE,"DefaultValue is not provided for PacketType");
							}
						}
					}					
				}
				
			}else{
				if (isMultimodeEnabled(value)) {
					String attributeId = stripKeywordName(value);
					try {
						List<IDiameterAVP> attributeValues = diameterResponse.getAVPList(attributeId);
						addAttributes(radiusResponse, attributeValues, valueNodes, avpId);
						continue;
					} catch (Exception e) {
						if (LogManager.getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Unable to fetch " + MULTIMODE_KEYWORD + 
									" attribute-value for: " + attributeId + 
									", Reason: " + e.getMessage());
						}
					} 
				}	
				
				IRadiusAttribute radiusAttribute;
				try {
					
					radiusAttribute = Dictionary.getInstance().getAttribute(avpId);
					if(isLiteral(value)){ // remove '"' from the value and set it to the give attribute 
						value = unquote(value);
						value = getValue(value,valueNodes);
						radiusAttribute.setStringValue(value);
						radiusResponse.addAttribute(radiusAttribute);				
					}else if (isKeyword(value)) {
						String strValue=null;
						strValue = getKeywordValue(value, params, false,new com.elitecore.core.commons.data.ValueProvider(){

							@Override
							public String getStringValue(String identifier) {
								if(identifier!=null){
									return diameterResponse.getAVPValue(identifier);
								}else {
									return null;
								}
								
							}
							
						});
						if(strValue==null){
							strValue = defaultValues.get(avpId);
							if(strValue!=null && strValue.length()>0){
								strValue = getValue(strValue, valueNodes);
							}
						}
						if(strValue!=null){
							strValue = getValue(strValue,valueNodes);
							radiusAttribute.setStringValue(strValue);
							radiusResponse.addAttribute(radiusAttribute);
						}	
					}else{ // value is Attribute
						IDiameterAVP avp = diameterResponse.getAVP(value);
						if(avp != null){
							value = avp.getStringValue();
							value = getValue(value,valueNodes);
							radiusAttribute.setStringValue(value);
							radiusResponse.addAttribute(radiusAttribute);
						}else{ //Request doesn't contains give avp so setting default value
							String avpValue = defaultValues.get(avpId);
							if(avpValue != null && avpValue.length() > 0){
								avpValue = getValue(avpValue, valueNodes);								
								radiusAttribute.setStringValue(avpValue);
								radiusResponse.addAttribute(radiusAttribute);						
							}else{
								LogManager.getLogger().debug(MODULE,"Key: "+ value+" is not found in response object: ");
								LogManager.getLogger().debug(MODULE,"DefaultValue is not provided for: " + avpId);
							}																			
						}
					}						
				} catch (InvalidAttributeIdException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Skipping RadiusAttribute ID:" + avpId + " reason: " + e.getMessage());
					}				
				}
				
			}
		}		
	}
	private void addAttributes(RadServiceResponse radiusResponse,
			List<IDiameterAVP> avps, Map<String, String> valueNodes,
			String avpId) {
		String value;
		for (int i = 0; i < avps.size(); i++) {
			try {
				IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(avpId);
				if (radiusAttribute == null) {
					LogManager.getLogger().warn(MODULE, "Unable to add attribute: " + avpId + 
							", Reason: attribute not found in dictionary");
					continue;
				}
				
				value = avps.get(i).getStringValue();
				value = getValue(value,valueNodes);
				radiusAttribute.setStringValue(value);
				radiusResponse.addAttribute(radiusAttribute);
			} catch (Exception e) {
				LogManager.getLogger().warn(MODULE, "Unable to add attribute: " + avpId + 
						", Reason: " + e.getMessage());
			}
		}
		
	}

	private void addHardCodedAvps(DiameterPacket diameterRequest){
		AvpTime timeStampAvp = (AvpTime)DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.EVENT_TIMESTAMP);
		
		timeStampAvp.setStringValue(String.valueOf(System.currentTimeMillis()/1000));
		diameterRequest.addAvp(timeStampAvp);
		
		IDiameterAVP subType =  DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
		subType.setStringValue("1");
		
		IDiameterAVP avp =   DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
		avp.setStringValue("405811170258489");
		
		IDiameterAVP groupAvp =   DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
		((AvpGrouped)groupAvp).addSubAvp(subType);
		((AvpGrouped)groupAvp).addSubAvp(avp);
		diameterRequest.addAvp(groupAvp);
		diameterRequest.refreshPacketHeader();		
	}
	private void applyRadiusToDiameterMappings(final RadServiceRequest radiusRequest,DiameterPacket diameterRequest, ParsedMapping mapping,TranslatorParams params){
		/* Applying Mapping Expression. */
		Map<String,String>defaultValues = mapping.getDefaultValuesMap();
		// Mapping nodes <0:1, "abc">
		// OR  			<0:1, 0:1>
		Map<String,String>mappingNodes = mapping.getMappingNodes();
		Map<String,String>valueNodes = mapping.getValueNodes();

		String avpId ;
		String value ;

		for(Entry<String, String>entry: mappingNodes.entrySet()){
			avpId = entry.getKey();
			value = entry.getValue();
			if (isMultimodeEnabled(value)) {
				String attributeId = stripKeywordName(value);
				try {
					List<IRadiusAttribute>  avps = (List<IRadiusAttribute>) radiusRequest.getRadiusAttributes(attributeId);
					addAVPs(diameterRequest, avps, valueNodes, avpId);
					continue;
				} catch (Exception e) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to fetch " + MULTIMODE_KEYWORD + 
								" attribute-value for: " + attributeId + 
								", Reason: " + e.getMessage());
					}
				} 
			}			
			String strValue=null;
			if(isLiteral(value)){ // remove '"' from the value and set it to the give attribute 
				strValue = unquote(value);
			}else if (isKeyword(value)) {
				strValue = getKeywordValue(value, params, true,new com.elitecore.core.commons.data.ValueProvider(){

					@Override
					public String getStringValue(String identifier) {
						if(identifier!=null){
							IRadiusAttribute radiusAttribute = radiusRequest.getRadiusAttribute(identifier, true);
							if(radiusAttribute!=null)
								return radiusAttribute.getStringValue();
						}
						return null;
					}
					
				});
			}else if (AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(value)) {
				strValue = String.valueOf(radiusRequest.getPacketType());
			}else {
				IRadiusAttribute  avp = radiusRequest.getRadiusAttribute(value,true);
				if(avp != null){
					strValue = avp.getStringValue();
				}	
			}
			if(strValue==null){
				strValue = defaultValues.get(avpId);
				if(strValue!=null && strValue.length()>0){
					strValue = getValue(strValue,valueNodes);
					DiameterUtility.addOrReplaceAvp(avpId, diameterRequest,strValue);
				}else {
					LogManager.getLogger().debug(MODULE,"Key: "+ value+" is not found in response object: ");
					LogManager.getLogger().debug(MODULE,"DefaultValue is not provided for: " + avpId);
				}
			}else {
				strValue = getValue(strValue,valueNodes);
				DiameterUtility.addOrReplaceAvp(avpId, diameterRequest,strValue);
			}
		}

	}
	private void setCommandCodeAndAppId(DiameterPacket request,DiameterParsedMappingInfoImpl parsedMappingInfoImpl) {
		if(parsedMappingInfoImpl.needToSetCommandCodes()){
			request.setCommandCode(parsedMappingInfoImpl.getCommandCode());
		}	
		if(parsedMappingInfoImpl.needToSetApplicationId()){
			request.setApplicationID(parsedMappingInfoImpl.getApplicationId());
		}	
		if(parsedMappingInfoImpl.needToSetProxyFlag() && parsedMappingInfoImpl.isProxiable()){
				request.setProxiableBit();
		}
		if(parsedMappingInfoImpl.needToSetCommandCodes() && parsedMappingInfoImpl.isRequest()){
				request.setRequestBit();
		}
	}
	private void handleDiameterToRadiusTranslation(Map<String,ParsedMappingInfo> infoMap,RadServiceResponse radiusResponse,DiameterPacket diameterPacket,TranslatorParams params,RequestValueProviderImpl requestValueProviderImpl,Map<String,String>dummyResponseMap) throws TranslationFailedException {
		ValueProvider valueProvider = new DiameterAVPValueProvider(diameterPacket);
		LogicalExpression expression = null;
		List<ParsedMapping> mappings;
		ParsedMappingInfo mappingInfo;
		String mappingName = (String)params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING);

			if(mappingName == null){
				throw new TranslationFailedException("No Translation Mapping is selected during request translation");
			}
			
			mappingInfo = infoMap.get(mappingName);
			if(mappingInfo == null){
				throw new TranslationFailedException("Invalid Translation Mapping "+mappingName+" is selected during request translation");
			}
				LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + mappingInfo.getInRequestStrExpression() + " outMessageType: " + mappingInfo.getOutRequestType());				
				
				mappings = mappingInfo.getParsedResponseMappingDetails();				
			LogManager.getLogger().info(MODULE, "Response Translation using mapping: "+mappingInfo.getMappingName());
				if(mappingInfo.isDummyResponse()){
					translateDummyResponse(mappings,dummyResponseMap,radiusResponse,params);
					return ;					
				}
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyDiameterToRadiusMappings(diameterPacket, radiusResponse, mapping,params);
					}else{
						expression = mapping.getExpression();				
						if((expression).evaluate(valueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyDiameterToRadiusMappings(diameterPacket, radiusResponse, mapping,params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
			}
	private void handleRadiusToDaimeterTranslation(Map<String,ParsedMappingInfo> infoMap,RadServiceRequest radiusRequest,DiameterPacket diameterPacket ,TranslatorParams params,ValueProvider valueProvider) {
		LogicalExpression expression = null;
		List<ParsedMapping> mappings;
		for(ParsedMappingInfo mappingInfo : infoMap.values()){
			
			if(isMappingApplicable(mappingInfo.getInRequestExpression(),mappingInfo.getInRequestStrExpression(),valueProvider)){
				LogManager.getLogger().info(MODULE, "Request Translation using mapping: "+mappingInfo.getMappingName());
				params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, mappingInfo.getMappingName());
				
				boolean isDummyResponse = mappingInfo.isDummyResponse();
				params.setParam(TranslatorConstants.DUMMY_MAPPING, isDummyResponse);
				if(isDummyResponse){
					return;
				}
				setCommandCodeAndAppId(diameterPacket,(DiameterParsedMappingInfoImpl)mappingInfo);
				//Application Id is required if SEQSESS keyword has been configured.
				params.setParam(TranslatorConstants.APPLICATION_ID, diameterPacket.getApplicationID());
				LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + mappingInfo.getInRequestStrExpression() + " outMessageType: " + mappingInfo.getOutRequestType());				
				mappings = mappingInfo.getParsedRequestMappingDetails();				
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyRadiusToDiameterMappings(radiusRequest, diameterPacket, mapping,params);		
					}else{
						expression = mapping.getExpression();				
						if((expression).evaluate(valueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyRadiusToDiameterMappings(radiusRequest, diameterPacket, mapping,params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
				break;
			}
			
		}
		
	}
	@Override
	public void translateRequest(String policyDataId, TranslatorParams params)
			throws TranslationFailedException {
		TranslatorPolicy policy = policiesMap.get(policyDataId);
		if(policy == null)
			throw new TranslationFailedException("Invalid Policy id: " + policyDataId);
		
		RadServiceRequest request =	(RadServiceRequest)params.getParam(AAATranslatorConstants.FROM_PACKET);
		DiameterPacket diameterRequest = (DiameterPacket)params.getParam(AAATranslatorConstants.TO_PACKET);
		if(request != null && diameterRequest != null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Recieved packet:" + request.toString());
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Translation from Radius to Diameter started");
			}
			ValueProvider valueProvider = new RequestValueProviderImpl(request);
			applyRequestMapping(policy,request,diameterRequest,params,valueProvider);
			//addHardCodedAvps(diameterRequest);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Translation from Radius to Diameter Completed");
				if(diameterRequest != null)
				LogManager.getLogger().info(MODULE, "Translated Diameter packet:" + diameterRequest.toString());
			}	
		}else{
			throw new TranslationFailedException("Given Object is NULL");
		}
	}

	private void applyRequestMapping(TranslatorPolicy policy,RadServiceRequest request,DiameterPacket diameterRequest, TranslatorParams params,ValueProvider valueProvider) {
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyRequestMapping(basePolicy,request,diameterRequest,params,valueProvider);
		}	
		handleRadiusToDaimeterTranslation(policy.getParsedRequestMappingInfoMap(),request,diameterRequest,params,valueProvider);
	}

	@Override
	public void translateResponse(String policyDataId, TranslatorParams params)
			throws TranslationFailedException {

		TranslatorPolicy policy = policiesMap.get(policyDataId);
		if(policy == null)
			throw new TranslationFailedException("Invalid Policy id: " + policyDataId);
		
		RadServiceRequest srcRequest =	(RadServiceRequest)params.getParam(AAATranslatorConstants.SOURCE_REQUEST);
		RadServiceResponse response =	(RadServiceResponse)params.getParam(AAATranslatorConstants.TO_PACKET);
		DiameterPacket diameterResponse = (DiameterPacket)params.getParam(AAATranslatorConstants.FROM_PACKET);

		if(srcRequest != null && (diameterResponse != null || Boolean.parseBoolean(String.valueOf((params.getParam(TranslatorConstants.DUMMY_MAPPING)))))){
			if(diameterResponse!= null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Recieved packet:" + diameterResponse);
			}
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Translation from Diameter to Radius started");
			}
			RequestValueProviderImpl requestValueProviderImpl = new RequestValueProviderImpl(srcRequest);
			applyResponseMapping(policy,response, diameterResponse,params,requestValueProviderImpl);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Translation from Diameter to Radius Completed");
				if(response !=null)
				LogManager.getLogger().info(MODULE, "Translated Radius packet:" + response.toString());
			}	
		}else{
			throw new TranslationFailedException("Given Object is NULL");
		}
		
	}
	
	
	private void applyResponseMapping(TranslatorPolicy policy,RadServiceResponse response,DiameterPacket diameterResponse, TranslatorParams params,RequestValueProviderImpl requestValueProviderImpl) throws TranslationFailedException{
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyResponseMapping(basePolicy,response,diameterResponse,params,requestValueProviderImpl);
		}	
		handleDiameterToRadiusTranslation(policy.getParsedResponseMappingInfoMap(),response, diameterResponse, params,requestValueProviderImpl,policy.getDummyParametersMap());
	}


	private class RequestValueProviderImpl implements ValueProvider{
		private RadServiceRequest request;
		
		public RequestValueProviderImpl(RadServiceRequest request) {
			this.request = request;
		}
		
		@Override
		public String getStringValue(String identifier)
				throws InvalidTypeCastException,MissingIdentifierException {
			if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)){
				return String.valueOf(request.getPacketType());
			}
			IRadiusAttribute attribute = request.getRadiusAttribute(identifier,true);
			if(attribute != null)
				return attribute.getStringValue();
			else
				throw new MissingIdentifierException("Requested value not found in the request: " + identifier);
		}

		@Override
		public long getLongValue(String identifier)
				throws InvalidTypeCastException,MissingIdentifierException {
			if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)){
				return request.getPacketType();
			}
			IRadiusAttribute attribute = request.getRadiusAttribute(identifier,true);
			if(attribute != null)
				return attribute.getLongValue();
			else
				throw new MissingIdentifierException("Requested value not found in the request: "+identifier);
		
		}

		@Override
		public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			List<String> stringValues= null;
			if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)){
				stringValues=new ArrayList<String>(1);
				stringValues.add(String.valueOf(request.getPacketType()));
				return stringValues;
	}
			Collection<IRadiusAttribute> iRadiusAttributeList = request.getRadiusAttributes(identifier,true);
			if(iRadiusAttributeList!=null){
				stringValues=new ArrayList<String>();
				for(IRadiusAttribute iRadiusAttribute : iRadiusAttributeList){
					stringValues.add(iRadiusAttribute.getStringValue());
				}
			}else{
				throw new MissingIdentifierException("Requested value not found in the request: " + identifier);
			}
			return stringValues;
		}
	
		@Override
		public List<Long> getLongValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			List<Long> longValues= null;
			if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)){
				longValues=new ArrayList<Long>(1);
				longValues.add((long) request.getPacketType());
				return longValues;
			}
			Collection<IRadiusAttribute> iRadiusAttributeList = request.getRadiusAttributes(identifier,true);
			if(iRadiusAttributeList!=null){
				longValues=new ArrayList<Long>();
				for(IRadiusAttribute iRadiusAttribute : iRadiusAttributeList){
					longValues.add(iRadiusAttribute.getLongValue());
				}
			}else{
				throw new MissingIdentifierException("Requested value not found in the request: " + identifier);
			}
			return longValues;
		}

		@Override
		public Object getValue(String key) {
			return null;
		}
	}
	
	private void translateDummyResponse(List<ParsedMapping> mappings,Map<String, String> dummyMap, RadServiceResponse radiusResponse,TranslatorParams params) {
		LogicalExpression expression = null;
		DummyResponseValueProvider valueProvider = new DummyResponseValueProvider(dummyMap);
		for(ParsedMapping mapping : mappings){
			LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
			if("*".equalsIgnoreCase(mapping.getStrExpression())){
				LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
				applyDummyDiameterToRadiusMappings(dummyMap, radiusResponse, mapping,params);
			}else{
				expression = mapping.getExpression();				
				if((expression).evaluate(valueProvider)){
					LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
					applyDummyDiameterToRadiusMappings(dummyMap, radiusResponse, mapping,params);
				}else{
					LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
}
			}	
		}
		
	}
	
	/**
	 * Method will be used when Dummy Response is True.
	 */
	private void applyDummyDiameterToRadiusMappings(final Map<String, String> dummyMap,
			RadServiceResponse radiusResponse, ParsedMapping mapping,
			TranslatorParams params) {

		/* Applying Mapping Expression. */
		Map<String,String>defaultValues = mapping.getDefaultValuesMap();
		// Mapping nodes <0:1, "abc">
		// OR  			<0:1, 0:1>
		Map<String,String>mappingNodes = mapping.getMappingNodes();
		Map<String,String>valueNodes = mapping.getValueNodes();
		String avpId ;
		String value ;
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			avpId = entry.getKey();
			value = entry.getValue();
			if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(avpId)){
				if(value != null &&  value.length() > 0){
					int packetType = 0;
					if(isLiteral(value)){
						value = unquote(value);
						value = getValue(value, valueNodes);
						try{
							packetType = Integer.parseInt(value);
						}catch (NumberFormatException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
						}						
						radiusResponse.setPacketType(packetType);
					}else if (isKeyword(value)) {
						String strValue=null;
						strValue = getKeywordValue(value, params, false,new com.elitecore.core.commons.data.ValueProvider(){

							@Override
							public String getStringValue(String identifier) {
								if(identifier!=null){
									return dummyMap.get(identifier);
								}else {
									return null;	
								}
								
							}
							
						});
						if(strValue==null){
							strValue = defaultValues.get(avpId);
							if(strValue!=null && strValue.length()>0){
								strValue = getValue(strValue, valueNodes);
							}
						}
						if(strValue!=null){
							try{
								packetType = Integer.parseInt(value);
							}catch (NumberFormatException e) {
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
									LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
							}
						}	
						radiusResponse.setPacketType(packetType);
					}else{ //value is Avp
						value = dummyMap.get(value);
						if(value!= null){
							
							value = getValue(value, valueNodes);
							try{
								packetType = Integer.parseInt(value);
								radiusResponse.setPacketType(packetType);
							}catch (NumberFormatException e) {
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
									LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
							}						
						}else{ //Avp is null so using default value 
							String avpValue = defaultValues.get(avpId);
							if(avpValue != null && avpValue.length() > 0){
								try{								
									avpValue = getValue(avpValue, valueNodes);
									packetType = Integer.parseInt(avpValue);
									radiusResponse.setPacketType(packetType);
								}catch (NumberFormatException e) {
									if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
										LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
								}
							}else{
								LogManager.getLogger().debug(MODULE,"Key: "+ value+" is not found in response object: ");
								LogManager.getLogger().debug(MODULE,"DefaultValue is not provided for PacketType");
							}
						}
					}					
				}
				
			}else{
				IRadiusAttribute radiusAttribute;
				try {
					radiusAttribute = Dictionary.getInstance().getAttribute(avpId);
					if(isLiteral(value)){ // remove '"' from the value and set it to the give attribute 
						value = unquote(value);
						value = getValue(value,valueNodes);
						radiusAttribute.setStringValue(value);
						radiusResponse.addAttribute(radiusAttribute);				
					}else if (isKeyword(value)) {
						String strValue=null;
						strValue = getKeywordValue(value, params, false,new com.elitecore.core.commons.data.ValueProvider(){

							@Override
							public String getStringValue(String identifier) {
								if(identifier!=null){
									return dummyMap.get(identifier);
									 
								}else {
									return null;
								}
								
							}
							
						});
						if(strValue==null){
							strValue = defaultValues.get(avpId);
							if(strValue!=null && strValue.length()>0){
								strValue = getValue(strValue, valueNodes);
							}
						}
						if(strValue!=null){
							strValue = getValue(strValue,valueNodes);
							radiusAttribute.setStringValue(strValue);
							radiusResponse.addAttribute(radiusAttribute);
						}	
					}else{ // value is Attribute
						value  = dummyMap.get(value);
						if(value != null){
							value = getValue(value,valueNodes);
							radiusAttribute.setStringValue(value);
							radiusResponse.addAttribute(radiusAttribute);
						}else{ //Request doesn't contains give avp so setting default value
							String avpValue = defaultValues.get(avpId);
							if(avpValue != null && avpValue.length() > 0){
								avpValue = getValue(avpValue, valueNodes);								
								radiusAttribute.setStringValue(avpValue);
								radiusResponse.addAttribute(radiusAttribute);						
							}else{
								LogManager.getLogger().debug(MODULE,"Key: "+ value+" is not found in response object: ");
								LogManager.getLogger().debug(MODULE,"DefaultValue is not provided for: " + avpId);
							}																			
						}
					}						
				} catch (InvalidAttributeIdException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Skipping RadiusAttribute ID:" + avpId + " reason: " + e.getMessage());
					}				
				}
				
			}
		}		
	
		 		
	}
	private void addAVPs(DiameterPacket toPacket, List<IRadiusAttribute> avps,
			Map<String, String> valueNodes, String destAttributeId) {
		for (int i = 0 ; i < avps.size() ; i++) {
			toPacket.addAvp(
					DiameterUtility.createAvp(destAttributeId, avps.get(i).getStringValue())); 
		}
	}

}
