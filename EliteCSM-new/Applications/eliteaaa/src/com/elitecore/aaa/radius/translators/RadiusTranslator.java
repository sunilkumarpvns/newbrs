package com.elitecore.aaa.radius.translators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.diameter.translators.BaseTranslator;
import com.elitecore.aaa.diameter.translators.MathOpKeyword;
import com.elitecore.aaa.diameter.translators.StrOptKeyword;
import com.elitecore.aaa.diameter.translators.TimeStampKeyword;
import com.elitecore.aaa.diameter.translators.policy.TranslatorPolicy;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMapping;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMappingInfo;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.coreradius.commons.attributes.AttributeTypeNotFoundException;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class RadiusTranslator extends BaseTranslator {
	
	private static final String MODULE = "RADIUS-TRNSLTR";

	public RadiusTranslator(ServerContext context) {
		super(context);
	}
	
	@Override
	public void init(TranslatorPolicyData policyData)
			throws InitializationFailedException {
		super.init(policyData);
		KeywordContext keywordContext = getKeywordContext();
		
		registerKeyword(new RadiusSrcKeyword(AAATranslatorConstants.SOURCE_REQUEST,keywordContext),false);
		registerKeyword(new RadiusDstKeyword(AAATranslatorConstants.DESTINATION_REQUEST,keywordContext),false);
		
		StrOptKeyword strOptKeyword = new StrOptKeyword(TranslatorConstants.STROPT,keywordContext);
		registerKeyword(strOptKeyword, true);
		registerKeyword(strOptKeyword, false);

		registerKeyword(new TimeStampKeyword(TranslatorConstants.TIMESTAMP,keywordContext), true);
		registerKeyword(new TimeStampKeyword(TranslatorConstants.TIMESTAMP,keywordContext), false);
		

		MathOpKeyword mathOpKeyword = new MathOpKeyword(TranslatorConstants.MATHOP, keywordContext);
		registerKeyword(mathOpKeyword, true);
		registerKeyword(mathOpKeyword, false);

		registerKeyword(new Mac2TGppKeyword(TranslatorConstants.MAC2TGPP, keywordContext), true);
		registerKeyword(new Mac2TGppKeyword(TranslatorConstants.MAC2TGPP, keywordContext), false);
		
		registerKeyword(new RadiusSrcResKeyword(TranslatorConstants.SRCRES, keywordContext), true);
	}

	@Override
	public String getFromId() {
		return AAATranslatorConstants.RADIUS_TRANSLATOR;
	}

	@Override
	public String getToId() {
		return AAATranslatorConstants.RADIUS_TRANSLATOR;
	}

	@Override
	public void translateRequest(String policyDataId, TranslatorParams params)
			throws TranslationFailedException {

		TranslatorPolicy policy = policiesMap.get(policyDataId);
		if(policy == null)
			throw new TranslationFailedException("Invalid Policy id: " + policyDataId);

		RadServiceRequest radiusRequest =	(RadServiceRequest)params.getParam(AAATranslatorConstants.FROM_PACKET);
		RadiusPacket translatedRequest = (RadiusPacket)params.getParam(AAATranslatorConstants.TO_PACKET);

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Receieved Radius packet:" + radiusRequest.toString());
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Translation from RADIUS to RADIUS started");
		}
		ValueProvider valueProvider = new RadServiceRequestValueProvider(radiusRequest);
		applyMappings(policy,radiusRequest,translatedRequest,params,valueProvider);
		translatedRequest.refreshPacketHeader();
		translatedRequest.refreshInfoPacketHeader();
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Translation from RADIUS to RADIUS-RADIUS Completed");
			LogManager.getLogger().debug(MODULE, "Translated RADIUS packet:" + translatedRequest.toString());
		}	

	}

	private void applyMappings(TranslatorPolicy policy,RadServiceRequest radiusRequest,RadiusPacket translatedRequest, TranslatorParams params,ValueProvider valueProvider) {
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyMappings(basePolicy,radiusRequest,translatedRequest,params,valueProvider);
		}	
		applyRadiusMapping(policy.getParsedRequestMappingInfoMap(),radiusRequest,translatedRequest,params,valueProvider);
	}

	private void applyRadiusMapping(Map<String,ParsedMappingInfo> parsedRequestMappingInfoMap,RadServiceRequest radiusRequest,RadiusPacket translatedRequest, TranslatorParams params,ValueProvider valueProvider) {

		LogicalExpression expression = null;
		List<ParsedMapping> mappings;
			
		for(ParsedMappingInfo mappingInfo : parsedRequestMappingInfoMap.values()){
			
			if(isMappingApplicable(mappingInfo.getInRequestExpression(),mappingInfo.getInRequestStrExpression(),valueProvider)){
				LogManager.getLogger().info(MODULE, "Request Translation using mapping: "+mappingInfo.getMappingName());
				boolean isDummyResponse = mappingInfo.isDummyResponse();
				params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, mappingInfo.getMappingName());
				if(isDummyResponse){
					params.setParam(TranslatorConstants.DUMMY_MAPPING, isDummyResponse);
					return;
				}
				LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + mappingInfo.getInRequestStrExpression() + " outMessageType: " + mappingInfo.getOutRequestType());				
				mappings = mappingInfo.getParsedRequestMappingDetails();				
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyRadiusToRadiusMappings(radiusRequest, translatedRequest, mapping,params);		
					}else{
						expression = mapping.getExpression();				
						if((expression).evaluate(valueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyRadiusToRadiusMappings(radiusRequest, translatedRequest, mapping,params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
				break;
			}
			
		}
	
		
	}

	private void applyRadiusToRadiusMappings(final RadServiceRequest radiusRequest,RadiusPacket translatedRequest, ParsedMapping mapping,TranslatorParams params) {
		
		Map<String,String>defaultValues = mapping.getDefaultValuesMap();
		Map<String,String>mappingNodes = mapping.getMappingNodes();
		Map<String,String>valueNodes = mapping.getValueNodes();
		
		String avpId ;
		String value ;
		String strValue;
		
		String sharedSecret = (String)params.getParam(TranslatorConstants.SHARED_SECRET);
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			strValue  = null;
			avpId = entry.getKey();
			value = entry.getValue();
			
			if (isMultimodeEnabled(value)){
				value = stripKeywordName(value);
				List<IRadiusAttribute> attributeList = (ArrayList<IRadiusAttribute>)radiusRequest.getRadiusAttributes(value, true);
				if (Collectionz.isNullOrEmpty(attributeList) == false) {
					IRadiusAttribute radiusAttribute;
					try{
						int numOfAttr = attributeList.size();
						for (int i=0 ; i<numOfAttr; i++){
							radiusAttribute = Dictionary.getInstance().getAttribute(avpId);
							strValue = getValue(getStringValue(translatedRequest, attributeList.get(i),sharedSecret), valueNodes);
							radiusAttribute.setStringValue(strValue, sharedSecret, translatedRequest.getAuthenticator());
							translatedRequest.addAttribute(radiusAttribute);
						}
					}catch (InvalidAttributeIdException e) {
						LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
					}
				}else {
					strValue = defaultValues.get(avpId);
					try{
						if(Strings.isNullOrBlank(strValue) == false) {
							strValue = getValue(strValue, valueNodes);
							IRadiusAttribute radiusAttribute = Dictionary.getInstance().getAttribute(avpId);
							radiusAttribute.setStringValue(strValue, sharedSecret, translatedRequest.getAuthenticator());
							translatedRequest.addAttribute(radiusAttribute);
						}
					}catch (InvalidAttributeIdException e) {
						LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
					}
				}
			}else {
				if(isLiteral(value)){
					try{
						strValue = unquote(value);
						strValue  = getGroupedAVPValue(strValue, radiusRequest, defaultValues, valueNodes, translatedRequest, sharedSecret);
					} catch (Exception e){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Problem in applying JSON format on: "+strValue+", Reason: "+e.getMessage());
					}
				}else if (isKeyword(value)) {
					
					strValue = getKeywordValue(value, params, true,new com.elitecore.core.commons.data.ValueProvider(){

						@Override
						public String getStringValue(String identifier) {
							if(identifier==null){
								return null;
							}
							IRadiusAttribute radiusAttribute = radiusRequest.getRadiusAttribute(identifier,true);
							if(radiusAttribute==null)
								return null;
							return radiusAttribute.getStringValue();

						}
						
					});
				
				}else{
					IRadiusAttribute radiusAttribute = radiusRequest.getRadiusAttribute(value,true);
					if(radiusAttribute!=null){
						strValue = getStringValue(translatedRequest, radiusAttribute, sharedSecret);
					}
				}
				if(strValue == null || !(strValue.length()>0)){
					strValue = unquote(defaultValues.get(avpId));
					if(strValue == null || !(strValue.length()>0)){		
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE,"DefaultValue is not provided for: " + avpId);
					}	
				}
				if(strValue !=null && strValue.length()>0){
					if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(avpId)){
						try{
							translatedRequest.setPacketType(Integer.parseInt(strValue));
						}catch (NumberFormatException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE, "Invalid packet Type "+ strValue);
						}	
					}else{
						addAttribute(avpId, strValue, valueNodes, translatedRequest, sharedSecret);
					}
				}
			}
		}		
	
	}

	@Override
	public void translateResponse(String policyDataId, TranslatorParams params)throws TranslationFailedException {
		
		RadiusPacket fromRadiusRequest = (RadiusPacket)params.getParam(AAATranslatorConstants.FROM_PACKET); 
		RadiusPacket toRadiudRequest = (RadiusPacket)params.getParam(AAATranslatorConstants.TO_PACKET);
		RadServiceRequest origRequestPacket = (RadServiceRequest)params.getParam(AAATranslatorConstants.SOURCE_REQUEST);

		if(fromRadiusRequest != null){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){				
				LogManager.getLogger().debug(MODULE, "Radius packet to translate:" + fromRadiusRequest.toString());
		}
		}
		TranslatorPolicy policy = policiesMap.get(policyDataId);
		if(policy != null){
			ValueProvider valueProvider =  new RadServiceRequestValueProvider(origRequestPacket);
			applyMappings(policy, fromRadiusRequest, toRadiudRequest, params, valueProvider);
			toRadiudRequest.refreshInfoPacketHeader();
			toRadiudRequest.refreshPacketHeader();
		}else{
			LogManager.getLogger().error(MODULE, "Given Policy not found. Policy Id: " + policyDataId );
			throw new TranslationFailedException("Given Policy not found. Policy Id: " + policyDataId );
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Translation from RADIUS to RADIUS-RADIUS Completed");
			LogManager.getLogger().debug(MODULE, "Translated RADIUS packet:" + toRadiudRequest.toString());
		}
	}
	
	private void applyMappings(TranslatorPolicy policy,RadiusPacket fromRadiusRequest, RadiusPacket toRadiusRequest,TranslatorParams params, ValueProvider valueProvider) throws TranslationFailedException {
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyMappings(basePolicy,fromRadiusRequest,toRadiusRequest,params,valueProvider);
		}	
		applyRadiusMapping(policy.getParsedResponseMappingInfoMap(),fromRadiusRequest,toRadiusRequest,params,valueProvider,policy.getDummyParametersMap());
		
	}

	private void applyRadiusMapping(Map<String,ParsedMappingInfo> parsedResponseMappingInfoMap,RadiusPacket fromRadiusPacket, RadiusPacket toRadiusRequest,TranslatorParams params, ValueProvider valueProvider,Map<String,String>dummyResponseMap) throws TranslationFailedException {

		LogicalExpression expression = null;
		List<ParsedMapping> mappings = null;
		ParsedMappingInfo mappingInfo;
		RadiusAttributeValueProvider responseValueProviderImpl = new RadiusAttributeValueProvider(fromRadiusPacket);
	String mappingName = (String)params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING);
			
		if(mappingName == null){
			throw new TranslationFailedException("No Translation Mapping is selected during request translation");
		}
		mappingInfo = parsedResponseMappingInfoMap.get(mappingName);
		if(mappingInfo == null){
			throw new TranslationFailedException("Invalid Translation Mapping "+mappingName+" is selected during request translation");
		}
				LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + mappingInfo.getInRequestStrExpression() + " outMessageType: " + mappingInfo.getOutRequestType());				
				mappings = mappingInfo.getParsedResponseMappingDetails();				
		LogManager.getLogger().info(MODULE, "Response Translation using mapping: "+mappingInfo.getMappingName());
				
				if(mappingInfo.isDummyResponse()){	
					translateDummyResponse(mappings,toRadiusRequest,dummyResponseMap,params);
					return;
				}
				
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyRadiusToRadiusMappings(fromRadiusPacket, toRadiusRequest, mapping,params);		
					}else{
						expression = mapping.getExpression();				
						if((expression).evaluate(responseValueProviderImpl)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyRadiusToRadiusMappings(fromRadiusPacket, toRadiusRequest, mapping,params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
			}
			
	private void translateDummyResponse(List<ParsedMapping> mappings,
			RadiusPacket radiusRequest, Map<String, String> dummyResponseMap,
			TranslatorParams params) {
		// TODO Auto-generated method stub
		LogicalExpression expression = null;
		DummyResponseValueProvider valueProvider = new DummyResponseValueProvider(dummyResponseMap);
		for(ParsedMapping mapping : mappings){
			LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
			if("*".equalsIgnoreCase(mapping.getStrExpression())){
				LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
				applyDummyRadiusToRadiusMappings(radiusRequest, dummyResponseMap, mapping,params);		
			}else{
				expression = mapping.getExpression();				
				if((expression).evaluate(valueProvider)){
					LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
					applyDummyRadiusToRadiusMappings(radiusRequest, dummyResponseMap, mapping,params);
				}else{
					LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
				}		
			}	
		}

	}
	/**
	 * Method will be used when Dummy Response is True.
	 */
	private void applyDummyRadiusToRadiusMappings(RadiusPacket radiusRequest,
			final Map<String, String> dummyResponseMap, ParsedMapping mapping,
			TranslatorParams params) {
		
		Map<String,String>defaultValues = mapping.getDefaultValuesMap();
		Map<String,String>mappingNodes = mapping.getMappingNodes();
		Map<String,String>valueNodes = mapping.getValueNodes();
		
		String avpId ;
		String value ;
		String strValue;
		
		String sharedSecret = (String)params.getParam(TranslatorConstants.SHARED_SECRET);
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			strValue  = null;
			avpId = entry.getKey();
			value = entry.getValue();
			
				if(isLiteral(value)){
					try{
						strValue = unquote(value);
						strValue  = getGroupedAVPValue(strValue, dummyResponseMap, defaultValues, valueNodes);
					} catch (Exception e){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Problem in applying JSON format on: "+strValue+", Reason: "+e.getMessage());
					}
				}else if (isKeyword(value)) {
					strValue = getKeywordValue(value, params, false,new com.elitecore.core.commons.data.ValueProvider(){

						@Override
						public String getStringValue(String identifier) {
							if(identifier==null){
								return null;
							}
							return dummyResponseMap.get(identifier);

						}
						
					});				
				}else{
					String values = dummyResponseMap.get(value);
						strValue = values;
				}
				if(strValue == null || !(strValue.length()>0)){
					strValue = unquote(defaultValues.get(avpId));
					if(strValue == null || !(strValue.length()>0)){		
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE,"DefaultValue is not provided for: " + avpId);
					}	
				}
				if(strValue !=null && strValue.length()>0){
					if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(avpId)){
						try{
							radiusRequest.setPacketType(Integer.parseInt(strValue));
						}catch (NumberFormatException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE, "Invalid packet Type "+ strValue);
						}	
					}else{
						addAttribute(avpId, strValue, valueNodes, radiusRequest,sharedSecret);
					}
				}
			}			
		}

	private String getStringValue(final RadiusPacket packet, final IRadiusAttribute attribute,
			final String sharedSecret) {
		return attribute.getStringValue(sharedSecret, packet.getAuthenticator());

	}

	private void applyRadiusToRadiusMappings(final RadiusPacket fromRadiusRequest,
			RadiusPacket toRadiusPacket, ParsedMapping mapping,
			TranslatorParams params) {

		
		Map<String,String>defaultValues = mapping.getDefaultValuesMap();
		Map<String,String>mappingNodes = mapping.getMappingNodes();
		Map<String,String>valueNodes = mapping.getValueNodes();
		
		String avpId ;
		String value ;
		String strValue;
		
		String sharedSecret = (String)params.getParam(TranslatorConstants.SHARED_SECRET);
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			strValue  = null;
			avpId = entry.getKey();
			value = entry.getValue();
			
			if (isMultimodeEnabled(value)){
				value = stripKeywordName(value);
				List<IRadiusAttribute> attributeList = (ArrayList<IRadiusAttribute>)fromRadiusRequest.getRadiusAttributes(value, true);
				if(Collectionz.isNullOrEmpty(attributeList) == false){
					IRadiusAttribute radiusAttribute;
					try{
						int numOfAttr = attributeList.size();
						for (int i=0 ; i<numOfAttr; i++){
							radiusAttribute = Dictionary.getInstance().getAttribute(avpId);
							strValue = getValue(getStringValue(fromRadiusRequest, attributeList.get(i), sharedSecret), valueNodes);
							radiusAttribute.setStringValue(strValue, sharedSecret, toRadiusPacket.getAuthenticator());
							toRadiusPacket.addAttribute(radiusAttribute);
						}
					}catch (InvalidAttributeIdException e) {
						LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
					}
				}else {
					strValue = defaultValues.get(avpId);
					try{
						if(strValue!=null && strValue.length()>0){
							strValue = getValue(strValue, valueNodes);
							IRadiusAttribute radiusAttribute = Dictionary.getInstance().getAttribute(avpId);
							radiusAttribute.setStringValue(strValue, sharedSecret, toRadiusPacket.getAuthenticator());
							toRadiusPacket.addAttribute(radiusAttribute);
						}
					}catch (InvalidAttributeIdException e) {
						LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
					}
				}
			}else {
				if(isLiteral(value)){
					try{
						strValue = unquote(value);
						strValue  = getGroupedAVPValue(strValue, fromRadiusRequest, defaultValues, valueNodes, sharedSecret);
					} catch (Exception e){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Problem in applying JSON format on: "+strValue+", Reason: "+e.getMessage());
					}
				}else if (isKeyword(value)) {
					strValue = getKeywordValue(value, params, false,new com.elitecore.core.commons.data.ValueProvider(){

						@Override
						public String getStringValue(String identifier) {
							if(identifier==null){
								return null;
							}
							IRadiusAttribute radiusAttribute = fromRadiusRequest.getRadiusAttribute(identifier,true);
							if(radiusAttribute==null)
								return null;
							return radiusAttribute.getStringValue();

						}
						
					});
				
				}else{
					IRadiusAttribute radiusAttribute = fromRadiusRequest.getRadiusAttribute(value,true);
					if(radiusAttribute!=null){
						strValue = getStringValue(fromRadiusRequest, radiusAttribute,sharedSecret);
					}
				}
				if(strValue == null || !(strValue.length()>0)){
					strValue = unquote(defaultValues.get(avpId));
					if(strValue == null || !(strValue.length()>0)){		
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE,"DefaultValue is not provided for: " + avpId);
					}	
				}
				if(strValue !=null && strValue.length()>0){
					if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(avpId)){
						try{
							toRadiusPacket.setPacketType(Integer.parseInt(strValue));
						}catch (NumberFormatException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE, "Invalid packet Type "+ strValue);
						}	
					}else{
						addAttribute(avpId, strValue, valueNodes, toRadiusPacket, sharedSecret);
					}
				}
			}
		}		
		
	}

	private void addAttribute(String avpId,String avpValue,Map<String,String>valueNodes,RadiusPacket packet, final String sharedSecret){
		avpValue = getValue(avpValue,valueNodes);
		try {
			IRadiusAttribute attribute = Dictionary.getInstance().getAttribute(avpId);
			attribute.setStringValue(avpValue, sharedSecret, packet.getAuthenticator());
			packet.addAttribute(attribute);
		}catch (AttributeTypeNotFoundException e) {
			LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
		} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	private String getGroupedAVPValue(String value,RadServiceRequest radiusPacket,Map<String, String> defaultValues, Map<String, String> valueNodes, RadiusPacket translatedRequest, String sharedSecret) {

		String resultString = value;
			JSONObject jsonObject = JSONObject.fromObject(value);
			JSONObject resultjsonObject = new JSONObject();
			Set<String> attributeSet = jsonObject.keySet();
			Iterator<String> setItr = attributeSet.iterator();
			while(setItr.hasNext()){
				String strId = setItr.next();
				JSONArray valueArray = jsonObject.optJSONArray(strId);
				String key= "";
				String strValue = "";
				if(valueArray != null){
					final int valueArraySize = valueArray.size();
					for(int i=0; i<valueArraySize; i++){
						key = "";
						strValue = "";
						key = valueArray.getString(i);
						
						if(key.startsWith("{") && key.endsWith("}")){
							resultjsonObject.put(strId, getGroupedAVPValue(key, radiusPacket, defaultValues, valueNodes, translatedRequest, sharedSecret));
						}else {
							if(isLiteral(key)){
								strValue = unquote(key);
							}else {
								IRadiusAttribute radiusAttribute = radiusPacket.getRadiusAttribute(key,true);
								if(radiusAttribute!=null){
									strValue = radiusAttribute.getStringValue(sharedSecret,translatedRequest.getAuthenticator());
								}
							}
							if (strValue == null || (strValue.length()==0)){
								strValue = unquote(defaultValues.get(key));
							}
							if(strValue !=null && strValue.length()>0){
								strValue = getValue(strValue, valueNodes);
								resultjsonObject.put(strId, strValue);
							} else {
								if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
									LogManager.getLogger().warn(MODULE, "Value for key: " + key + " Not found in response or in default value configuration");
								}
							}
						}
					}
				} else {
					key = jsonObject.optString(strId);
					if (key.startsWith("{") && key.endsWith("}")){
						resultjsonObject.put(strId, getGroupedAVPValue(key, radiusPacket, defaultValues, valueNodes, translatedRequest, sharedSecret));
					} else{
						if(isLiteral(key)){
							strValue = unquote(key);
						}else {
							IRadiusAttribute radiusAttribute = radiusPacket.getRadiusAttribute(key, true);
							if(radiusAttribute!=null){
								strValue = getStringValue(translatedRequest, radiusAttribute, sharedSecret);
							}
						}
						if (strValue == null || (strValue.length()==0)){
							strValue = unquote(defaultValues.get(key));
						}
						if(strValue !=null && strValue.length()>0){
							strValue = getValue(strValue, valueNodes);
							resultjsonObject.put(strId, strValue);
						} else {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Value for key: " + key + " Not found in response or in default value configuration");
							}
						}
					}
				}
			}
			resultString = resultjsonObject.toString();
		
		return resultString;
	
	}
	
	@SuppressWarnings("unchecked")
	private String getGroupedAVPValue(String value,RadiusPacket radiusPacket,Map<String, String> defaultValues, Map<String, String> valueNodes, String sharedSecret) {

		String resultString = value;
			JSONObject jsonObject = JSONObject.fromObject(value);
			JSONObject resultjsonObject = new JSONObject();
			Set<String> attributeSet = jsonObject.keySet();
			Iterator<String> setItr = attributeSet.iterator();
			while(setItr.hasNext()){
				String strId = setItr.next();
				JSONArray valueArray = jsonObject.optJSONArray(strId);
				String key= "";
				String strValue = "";
				if(valueArray != null){
					final int valueArraySize = valueArray.size();
					for(int i=0; i<valueArraySize; i++){
						key = "";
						strValue = "";
						key = valueArray.getString(i);
						
						if(key.startsWith("{") && key.endsWith("}")){
							resultjsonObject.put(strId, getGroupedAVPValue(key, radiusPacket, defaultValues, valueNodes, sharedSecret));
						}else {
							if(isLiteral(key)){
								strValue = unquote(key);
							}else {
								IRadiusAttribute radiusAttribute = radiusPacket.getRadiusAttribute(key,true);
								if(radiusAttribute!=null){
									strValue = getStringValue(radiusPacket, radiusAttribute, sharedSecret);
								}
							}
							if (Strings.isNullOrEmpty(strValue)){
								strValue = unquote(defaultValues.get(key));
							}
							if(strValue !=null && strValue.length()>0){
								strValue = getValue(strValue, valueNodes);
								resultjsonObject.put(strId, strValue);
							} else {
								if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
									LogManager.getLogger().warn(MODULE, "Value for key: " + key + " Not found in response or in default value configuration");
								}
							}
						}
					}
				} else {
					key = jsonObject.optString(strId);
					if (key.startsWith("{") && key.endsWith("}")){
						resultjsonObject.put(strId, getGroupedAVPValue(key, radiusPacket, defaultValues, valueNodes,sharedSecret));
					} else{
						if(isLiteral(key)){
							strValue = unquote(key);
						}else {
							IRadiusAttribute radiusAttribute = radiusPacket.getRadiusAttribute(key, true);
							if(radiusAttribute!=null){
								strValue = getStringValue(radiusPacket, radiusAttribute, sharedSecret);
							}
						}
						if (strValue == null || (strValue.length()==0)){
							strValue = unquote(defaultValues.get(key));
						}
						if(strValue !=null && strValue.length()>0){
							strValue = getValue(strValue, valueNodes);
							resultjsonObject.put(strId, strValue);
						} else {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Value for key: " + key + " Not found in response or in default value configuration");
							}
						}
					}
				}
			}
			resultString = resultjsonObject.toString();
		return resultString;
	
	}
}
