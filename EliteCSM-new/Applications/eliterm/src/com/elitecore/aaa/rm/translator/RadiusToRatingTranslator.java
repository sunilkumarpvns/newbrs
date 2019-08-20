package com.elitecore.aaa.rm.translator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.diameter.translators.BaseTranslator;
import com.elitecore.aaa.diameter.translators.MathOpKeyword;
import com.elitecore.aaa.diameter.translators.RatingSrcDstKeyword;
import com.elitecore.aaa.diameter.translators.StrOptKeyword;
import com.elitecore.aaa.diameter.translators.TimeStampKeyword;
import com.elitecore.aaa.diameter.translators.policy.TranslatorPolicy;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMapping;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMappingInfo;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.translators.Mac2TGppKeyword;
import com.elitecore.aaa.radius.translators.RadiusSrcKeyword;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.coreradius.commons.attributes.AttributeTypeNotFoundException;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.ratingapi.util.IRequestParameters;
import com.elitecore.ratingapi.util.IResponseObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RadiusToRatingTranslator  extends BaseTranslator {
	private static final String MODULE = "RAD-RTNG-TRNSLTR";

	public RadiusToRatingTranslator(ServerContext context) {
		super(context);
	}

	@Override
	public String getFromId() {		
		return AAATranslatorConstants.RADIUS_TRANSLATOR;
	}

	@Override
	public String getToId() {
		return AAATranslatorConstants.RATING_TRANSLATOR;
	}
	
	@Override
	public void init(TranslatorPolicyData policyData)
			throws InitializationFailedException {
		super.init(policyData);
		KeywordContext keywordContext = getKeywordContext();
		registerKeyword(new RadiusSrcKeyword(AAATranslatorConstants.SOURCE_REQUEST,keywordContext),false);
		registerKeyword(new RatingSrcDstKeyword(AAATranslatorConstants.DESTINATION_REQUEST,keywordContext),false);
		

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
	}

	@Override
	public void translateRequest(String policyDataId, TranslatorParams params) throws TranslationFailedException{
		
		RadServiceRequest requestPacket = (RadServiceRequest)params.getParam(AAATranslatorConstants.FROM_PACKET);
		TranslatorPolicy policy = policiesMap.get(policyDataId);
		IRequestParameters requestParams = (IRequestParameters)params.getParam(AAATranslatorConstants.TO_PACKET);
		
		if(requestParams == null){
			throw new TranslationFailedException("Rating Request is null");
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Recieved packet:" + requestPacket.toString());
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Translation from RADIUS to Rating started");
		}				
		ValueProvider valueProvider = new RequestValueProviderImpl(requestPacket);
		String methodCall = applyRequestMapping(policy,requestPacket,requestParams,params,valueProvider);
		params.setParam(AAATranslatorConstants.RATING_METHOD_NAME, methodCall);
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Translation from RADIUS to Rating Completed");
			LogManager.getLogger().info(MODULE, "Translated Rating packet:" + requestParams.toString());
		}				
	}	
	


	private String applyRequestMapping(TranslatorPolicy policy,RadServiceRequest requestPacket,IRequestParameters requestParams, TranslatorParams params,ValueProvider valueProvider) {
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyRequestMapping(basePolicy,requestPacket,requestParams,params,valueProvider);
		}	
		return handleRadiusToRatingTranslation(policy.getParsedRequestMappingInfoMap(), requestPacket, requestParams, params,valueProvider);
	}

	private String handleRadiusToRatingTranslation(
			Map<String,ParsedMappingInfo> infoMap, RadServiceRequest requestPacket,IRequestParameters requestParameters , TranslatorParams params,ValueProvider valueProvider) {
		String methodCall = "";
		LogicalExpression expression = null;
		List<ParsedMapping> mappings;
		for(ParsedMappingInfo mappingInfo : infoMap.values()){
			LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + mappingInfo.getInRequestStrExpression() + " outMessageType: " + mappingInfo.getOutRequestType());
			if(isMappingApplicable(mappingInfo.getInRequestExpression(),mappingInfo.getInRequestStrExpression(),valueProvider)){
				LogManager.getLogger().info(MODULE, "Request Translation using mapping: "+mappingInfo.getMappingName());
				params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, mappingInfo.getMappingName());
				methodCall = mappingInfo.getOutRequestType();
				boolean isDummyResponse = mappingInfo.isDummyResponse();
				params.setParam(TranslatorConstants.DUMMY_MAPPING, isDummyResponse);
				if(isDummyResponse){
					return methodCall;
				}
				mappings = mappingInfo.getParsedRequestMappingDetails();				
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyRadiusToRatingMappings(requestPacket, requestParameters, mapping,params);		
					}else{
						expression = mapping.getExpression();				
						if((expression).evaluate(valueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyRadiusToRatingMappings(requestPacket, requestParameters, mapping,params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
				break;
			}
			
		}
		return methodCall;
	}
	
	private void handleRatingToRadiusTranslation(Map<String,ParsedMappingInfo> infoMap,IResponseObject responseObject,RadServiceResponse response,TranslatorParams params,RequestValueProviderImpl requestValueProvider,Map<String,String>dummyResponseMap) throws TranslationFailedException {
		ResponseValueProviderImpl valueProvider = new ResponseValueProviderImpl(responseObject);
		LogicalExpression expression = null;
		List<ParsedMapping> mappings;
	ParsedMappingInfo mappingInfo;
	String mappingName = (String)params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING);

	
		if(params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING) == null){
			throw new TranslationFailedException("No Translation Mapping is selected during request translation");
		}
		mappingInfo = infoMap.get((String) params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING));
		if(mappingInfo == null){
			throw new TranslationFailedException("Invalid Translation Mapping "+mappingName+" is selected during request translation");
		}
			LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + mappingInfo.getInRequestStrExpression()+ " outMessageType: " + mappingInfo.getOutRequestType());
			mappings = mappingInfo.getParsedResponseMappingDetails();
			
		LogManager.getLogger().info(MODULE, "Response Translation using mapping: "+mappingInfo.getMappingName());
				if(mappingInfo.isDummyResponse()){
					LogManager.getLogger().info(MODULE, "Applying Translation Mapping: "+mappingInfo.getMappingName());
					translateDummyResponse(mappings, dummyResponseMap, response, params);
					return;
				}
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyRatingToRadiusMappings(response, responseObject, mapping.getMappingNodes(), mapping.getDefaultValuesMap(), mapping.getValueNodes(),params);						
					}else{
						expression = mapping.getExpression();				
						if(expression.evaluate(valueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyRatingToRadiusMappings(response, responseObject, mapping.getMappingNodes(), mapping.getDefaultValuesMap(), mapping.getValueNodes(),params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
		
			}

	

	private void applyRadiusToRatingMappings(final RadServiceRequest requestPacket,IRequestParameters requestParameters, ParsedMapping mapping, TranslatorParams params){
		
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
			String strValue = null;
			
			if(isLiteral(value)){ // remove '"' from the value and set it to the give attribute 
				strValue = unquote(value);
				strValue = getValue(strValue,valueNodes);
				requestParameters.put(avpId, strValue);
				
			}else{ // value is Attribute
				if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(value)){
					strValue = String.valueOf(requestPacket.getPacketType());
				}else if (isKeyword(value)) {
					strValue = getKeywordValue(value, params, true,new com.elitecore.core.commons.data.ValueProvider(){

						@Override
						public String getStringValue(String identifier) {
							if(identifier!=null){
								IRadiusAttribute radiusAttribute = requestPacket.getRadiusAttribute(identifier, true);
								if(radiusAttribute!=null)
									return radiusAttribute.getStringValue();
							}
							return null;
						}
						
					});
				}else{
					IRadiusAttribute avp = requestPacket.getRadiusAttribute(value,true);
					if(avp != null){
						strValue = avp.getStringValue();
					}					
				}
				if(strValue==null){
					strValue = unquote(defaultValues.get(avpId));
					if( strValue != null && strValue.length() > 0) {
						strValue = getValue(strValue, valueNodes);
						requestParameters.put(avpId, strValue);
					}else{
						LogManager.getLogger().debug(MODULE,"Key: "+ value+" is not found in response object: ");
						LogManager.getLogger().debug(MODULE,"DefaultValue is not provided for: " + avpId);
					}
				}else {
					strValue = getValue(strValue, valueNodes);
					requestParameters.put(avpId, strValue);
				}
			}	
		}		
	
	}
	
	private void applyRatingToRadiusMappings(RadServiceResponse response,final IResponseObject responseObject,Map<String,String>mappingNodes ,Map<String,String>defaultValues,Map<String,String>valueNodes,TranslatorParams params){
		
		/* Applying Mapping Expression. */
		//String [] defaultValues = mapping.getDefaultValues();
		// Mapping nodes <0:1, "abc">
		// OR  			<0:1, 0:1>
		//Map<String,String>mappingNodes = mapping.getMappingNodes();
		//Map<String,String>valueNodes = mapping.getValueNodes();
		
		String avpId ;
		String value ;
		String strValue;
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			strValue  = null;
			avpId = entry.getKey();
			value = entry.getValue();
			int packetType = 0;
			if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(avpId)){
				if(value != null &&  value.length() > 0){
					if(isLiteral(value)){
						value = unquote(value);
						value = getValue(value, valueNodes);
						try{
							packetType = Integer.parseInt(value);
						}catch (NumberFormatException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
						}						
						response.setPacketType(packetType);
					}else if (isKeyword(value)) {
						strValue=null;
						strValue = getKeywordValue(value, params, false,new com.elitecore.core.commons.data.ValueProvider(){

							@Override
							public String getStringValue(String identifier) {
								if(identifier!=null){
									return responseObject.get(identifier);
								}
								return null;
							}
							
						});
						if(strValue==null){
							strValue = defaultValues.get(avpId);
						}
						if(strValue!=null){
							try{
								strValue = getValue(strValue, valueNodes);
								packetType = Integer.parseInt(value);
								response.setPacketType(packetType);
							}catch (NumberFormatException e) {
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
									LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
							}
						}	
					}else{ //value is Avp
						String avp = responseObject.get(value);
						if(avp!= null && avp.length() > 0){
							value = avp;
							value = getValue(value, valueNodes);
							try{
								packetType = Integer.parseInt(value);
								response.setPacketType(packetType);
							}catch (NumberFormatException e) {
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
									LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
							}						
						}else{ //Avp is null so using default value 
							String avpValue = unquote(defaultValues.get(avpId));
							if(avpValue != null && avpValue.length() > 0){
								try{
									avpValue = getValue(avpValue, valueNodes);								
									packetType = Integer.parseInt(avpValue);
									response.setPacketType(packetType);
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
				if(isLiteral(value)){
					try{
						strValue =  getGroupedAVPValue(unquote(value), responseObject, defaultValues, valueNodes);
					} catch (Exception e){
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
							LogManager.getLogger().info(MODULE, "failed to apply JSON format, Reason: "+e.getMessage());
							LogManager.getLogger().info(MODULE, "applying KeyValue format");
						}	
						strValue = getKeyValuePairValue(unquote(value),responseObject,defaultValues, valueNodes);
						
					}
					
				}else if (isKeyword(value)) {
					strValue=null;
					strValue = getKeywordValue(value, params, false,new com.elitecore.core.commons.data.ValueProvider(){

						@Override
						public String getStringValue(String identifier) {
							if(identifier!=null){
								return responseObject.get(identifier);
							}
							return null;
						}
						
					});
				}else{
					strValue = responseObject.get(value);
				}
				if(strValue == null || !(strValue.length()>0)){
					strValue = unquote(defaultValues.get(avpId));
					if(strValue == null || !(strValue.length()>0)){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE,"DefaultValue is not provided for: " + avpId);
					}
				}
				if(strValue !=null && strValue.length()>0)
					addAvp(avpId, strValue, valueNodes, response);
			}
		}		
	}

	
	private String getKeyValuePairValue(String expressionString, IResponseObject responseObject,Map<String,String>defaultValues, Map<String,String> valueNodes) {
		
		// 	Mapping Expression will be in format 21067:0="\"HARDCODESTRING1=\"RATINGKEY1,\"HARDCODESTRING2=\"RATINGKEY2,\"HARDCODESTRING3=\"RATINGKEY3" in which 21067:0 is AVP-Id and "\"HARDCODESTRING1=\"RATINGKEY1,\"HARDCODESTRING2=\"RATINGKEY2,\"HARDCODESTRING3=\"RATINGKEY3" is expressionString.  
		
		
		
		String strValue="";
		String hardcodeString;
		String ratingKey;
		
		if(expressionString!=null){
			String keyValueArray[] = expressionString.split(",");
			int numOfKeyValue = keyValueArray.length;
			String keyValue;
			for(int currentKeyValueIndex=0;currentKeyValueIndex<numOfKeyValue;currentKeyValueIndex++){
				keyValue = keyValueArray[currentKeyValueIndex];
				int index = keyValue.indexOf("=");
				
				if(index!=-1){
					try{
						hardcodeString = keyValue.substring(0, index+2);
						ratingKey = keyValue.substring(index+2);
						if(ratingKey!=null && ratingKey.length()>0){
							String mapValue = responseObject.get(ratingKey);
							if (mapValue == null || (mapValue.length()==0)){
								mapValue = unquote(defaultValues.get(ratingKey));
							}
							if(mapValue !=null && mapValue.length()>0){
								mapValue = getValue(mapValue, valueNodes);
							} else {
								if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
									LogManager.getLogger().warn(MODULE, "Value for key: " + hardcodeString);
								}
							}
							if(mapValue!=null && mapValue.length()>0){
								hardcodeString = unquote(hardcodeString);
								if(currentKeyValueIndex!=numOfKeyValue-1){
									strValue = strValue+hardcodeString+mapValue+",";
								}else {
									strValue = strValue+hardcodeString+mapValue;
								}	
							}
						}else {
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "No rating key configured for key :"+hardcodeString+" so key value pair will not be added to ");
						}
					}catch (IndexOutOfBoundsException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().debug(MODULE, "Invalid Key-Value pair :"+keyValue+" configured");
					}	
					
				}
				
			}
		}
		if(!(strValue.length()>0))
			strValue =expressionString;
		return strValue;
	}

	private String getGroupedAVPValue(String value,IResponseObject responseObject, Map<String,String>defaultValues, Map<String,String> valueNodes){
		// String should be in JSON format i.e. "{1:'PacketDataFlowID',2:'ServiceDataFlowID',3:'ServiceProfileID',11:{1:'ClassifierID',2:'Priority',3:'Protocol'}}";
		String resultString = value;
			JSONObject jsonObject = JSONObject.fromObject(value);
			JSONObject resultjsonObject = new JSONObject();
			Set<?> attributeSet = jsonObject.keySet();
			Iterator<?> setItr = attributeSet.iterator();
			while(setItr.hasNext()){
				String strId = (String)setItr.next();
				JSONArray valueArray = jsonObject.optJSONArray(strId);
				String key= "";
				String strValue = "";
				if(valueArray != null){
					final int valueArraySize = valueArray.size();
					for(int i=0; i<valueArraySize; i++){
						key = "";
						strValue = "";
						key = valueArray.getString(i);
						if (key.startsWith("{") && key.endsWith("}")){
							resultjsonObject.put(strId, getGroupedAVPValue(key, responseObject, defaultValues, valueNodes));
						}else {
							if(isLiteral(key)){
								strValue = unquote(key);
							}else {
								strValue = responseObject.get(key);
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
						resultjsonObject.put(strId, getGroupedAVPValue(key, responseObject, defaultValues, valueNodes));
					} else{
						if(isLiteral(key)){
							strValue = unquote(key);
						}else {
							strValue = responseObject.get(key);
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
	
	private void addOrReplaceAvp(String avpId,String avpValue,Map<String,String>valueNodes,RadServiceResponse packet){
		avpValue = getValue(avpValue,valueNodes);
		try {
			IRadiusAttribute attribute = packet.getRadiusAttribute(true,avpId);
			if(attribute != null){
				attribute.setStringValue(avpValue);
			}else{
				attribute = Dictionary.getInstance().getAttribute(avpId);
				attribute.setStringValue(avpValue);
				packet.addAttribute(attribute);
			}
		}catch (AttributeTypeNotFoundException e) {
			LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
		} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
		}
	}
	
	private void addAvp(String avpId,String avpValue,Map<String,String>valueNodes,RadServiceResponse packet){
		avpValue = getValue(avpValue,valueNodes);
		try {
			IRadiusAttribute attribute = Dictionary.getInstance().getAttribute(avpId);
			attribute.setStringValue(avpValue);
			packet.addAttribute(attribute);
		}catch (AttributeTypeNotFoundException e) {
			LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
		} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
		}
	}
	
	@Override
	public void translateResponse(String policyDataId, TranslatorParams params)
			throws TranslationFailedException {
		IResponseObject responseObject = (IResponseObject)params.getParam(AAATranslatorConstants.FROM_PACKET);
		RadServiceResponse response = (RadServiceResponse)params.getParam(AAATranslatorConstants.TO_PACKET);
		
		RadServiceRequest srcRequest = (RadServiceRequest)params.getParam(AAATranslatorConstants.SOURCE_REQUEST);
		
		
		if(srcRequest == null){
			throw new TranslationFailedException("REQUEST Packet is null");
		}
		if(response == null){
			throw new TranslationFailedException("RESPONSE Packet is null");
		}
		
		if(responseObject == null && !(Boolean.parseBoolean(String.valueOf(params.getParam(TranslatorConstants.DUMMY_MAPPING))))){

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Failed to Translate From Rating To Radius, Reason: Rating Response is NULL");
			response.setFurtherProcessingRequired(false);
		
		}else{
		if(responseObject!=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){				
				LogManager.getLogger().info(MODULE, "Rating packet to translate:" + responseObject.toString());
			}
			}
			RequestValueProviderImpl requestValueProvider = new RequestValueProviderImpl(srcRequest);
			applyResponseMapping(policiesMap.get(policyDataId), responseObject, response,params,requestValueProvider);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Translation from Rating to RADIUS Completed");
				LogManager.getLogger().info(MODULE, "Translated RADIUS packet:" + response.toString());
			}
		}
	}
	
	private void applyResponseMapping(TranslatorPolicy policy,IResponseObject responseObject, RadServiceResponse response,TranslatorParams params,RequestValueProviderImpl requestValueProvider) throws TranslationFailedException {
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyResponseMapping(basePolicy, responseObject, response,params,requestValueProvider);
		}	
		handleRatingToRadiusTranslation(policy.getParsedResponseMappingInfoMap(), responseObject, response, params,requestValueProvider,policy.getDummyParametersMap());
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
	private class ResponseValueProviderImpl implements ValueProvider{
		private IResponseObject responseObject;
		private ResponseValueProviderImpl(IResponseObject responseObject){
			this.responseObject = responseObject;
		}

		@Override
		public long getLongValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
			String value = responseObject.get(identifier);
			if(value != null)
				try{
					return Long.parseLong(value);
				}catch(Exception e){
					throw new InvalidTypeCastException(e.getMessage());
				}
				
			else
				throw new MissingIdentifierException("Object not found: "+identifier);
		}

		@Override
		public String getStringValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
			String value = responseObject.get(identifier);
			if(value != null)
				return value;								
			else
				throw new MissingIdentifierException("Object not found: "+identifier);
		}
		
		@Override
		public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			String value = responseObject.get(identifier);
			if(value != null){
				List<String> stringValues=new ArrayList<String>(1);	
				stringValues.add(value);
				return stringValues;
			}else
				throw new MissingIdentifierException("Object not found: "+identifier);
	}

		@Override
		public List<Long> getLongValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
			String value = responseObject.get(identifier);
			if(value != null)
				try{
					List<Long> longValues =new ArrayList<Long>(1);
					longValues.add(Long.parseLong(value));
					return longValues;
				}catch(Exception e){
					throw new InvalidTypeCastException(e.getMessage());
				}
			else
				throw new MissingIdentifierException("Object not found: "+identifier);	
		}

		@Override
		public Object getValue(String key) {
			return null;
		}

}
	private void translateDummyResponse(List<ParsedMapping> mappings,Map<String, String> dummyResponseMap, RadServiceResponse response,TranslatorParams params) {
		LogicalExpression expression = null;
		DummyResponseValueProvider valueProvider = new DummyResponseValueProvider(dummyResponseMap);
		for(ParsedMapping mapping : mappings){
			LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());

			if("*".equalsIgnoreCase(mapping.getStrExpression())){
				LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
				applyDummyRatingToRadiusMappings(response, dummyResponseMap, mapping.getMappingNodes(), mapping.getDefaultValuesMap(), mapping.getValueNodes(),params);						
			}else{
				expression = mapping.getExpression();				
				if(expression.evaluate(valueProvider)){
					LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
					applyDummyRatingToRadiusMappings(response, dummyResponseMap, mapping.getMappingNodes(), mapping.getDefaultValuesMap(), mapping.getValueNodes(),params);
				}else{
					LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
				}		
			}	
		}

}
	/**
	 * Method will be used when Dummy Response is True.
	 */
	private void applyDummyRatingToRadiusMappings(RadServiceResponse response,
			final Map<String, String> dummyResponseMap,
			Map<String, String> mappingNodes,
			Map<String, String> defaultValuesMap,
			Map<String, String> valueNodes, TranslatorParams params) {


		
		/* Applying Mapping Expression. */
		//String [] defaultValues = mapping.getDefaultValues();
		// Mapping nodes <0:1, "abc">
		// OR  			<0:1, 0:1>
		//Map<String,String>mappingNodes = mapping.getMappingNodes();
		//Map<String,String>valueNodes = mapping.getValueNodes();
		
		String avpId ;
		String value ;
		String strValue;
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			strValue  = null;
			avpId = entry.getKey();
			value = entry.getValue();
			int packetType = 0;
			if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(avpId)){
				if(value != null &&  value.length() > 0){
					if(isLiteral(value)){
						value = unquote(value);
						value = getValue(value, valueNodes);
						try{
							packetType = Integer.parseInt(value);
						}catch (NumberFormatException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
						}						
						response.setPacketType(packetType);
					}else if (isKeyword(value)) {
						strValue=null;
						strValue = getKeywordValue(value, params, false,new com.elitecore.core.commons.data.ValueProvider(){

							@Override
							public String getStringValue(String identifier) {
								if(identifier!=null){
									return dummyResponseMap.get(identifier);
								}
								return null;
							}
							
						});
						if(strValue==null){
							strValue = defaultValuesMap.get(avpId);
						}
						if(strValue!=null){
							try{
								strValue = getValue(strValue, valueNodes);
								packetType = Integer.parseInt(value);
								response.setPacketType(packetType);
							}catch (NumberFormatException e) {
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
									LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
							}
						}	
					}else{ //value is Avp
						String avp = dummyResponseMap.get(value);
						if(avp!= null && avp.length() > 0){
							value = avp;
							value = getValue(value, valueNodes);
							try{
								packetType = Integer.parseInt(value);
								response.setPacketType(packetType);
							}catch (NumberFormatException e) {
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
									LogManager.getLogger().warn(MODULE, "Invalid packet type configured reason: " +e.getMessage());
							}						
						}else{ //Avp is null so using default value 
							String avpValue = unquote(defaultValuesMap.get(avpId));
							if(avpValue != null && avpValue.length() > 0){
								try{
									avpValue = getValue(avpValue, valueNodes);								
									packetType = Integer.parseInt(avpValue);
									response.setPacketType(packetType);
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
				if(isLiteral(value)){
					try{
						strValue =  getGroupedAVPValue(unquote(value), dummyResponseMap, defaultValuesMap, valueNodes);
					} catch (Exception e){
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
							LogManager.getLogger().info(MODULE, "failed to apply JSON format, Reason: "+e.getMessage());
							LogManager.getLogger().info(MODULE, "applying KeyValue format");
						}	
						strValue = getKeyValuePairValue(unquote(value),dummyResponseMap,defaultValuesMap, valueNodes);
						
					}
					
				}else if (isKeyword(value)) {
					strValue=null;
					strValue = getKeywordValue(value, params, false,new com.elitecore.core.commons.data.ValueProvider(){

						@Override
						public String getStringValue(String identifier) {
							if(identifier!=null){
								return dummyResponseMap.get(identifier);
							}
							return null;
						}
						
					});
				}else{
					strValue = dummyResponseMap.get(value);
				}
				if(strValue == null || !(strValue.length()>0)){
					strValue = unquote(defaultValuesMap.get(avpId));
					if(strValue == null || !(strValue.length()>0)){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE,"DefaultValue is not provided for: " + avpId);
					}
				}
				if(strValue !=null && strValue.length()>0)
					addAvp(avpId, strValue, valueNodes, response);
			}
		}		
		
	}

	private String getKeyValuePairValue(String expressionString,
			Map<String, String> dummyResponseMap,
			Map<String, String> defaultValuesMap, Map<String, String> valueNodes) {

		
		// 	Mapping Expression will be in format 21067:0="\"HARDCODESTRING1=\"RATINGKEY1,\"HARDCODESTRING2=\"RATINGKEY2,\"HARDCODESTRING3=\"RATINGKEY3" in which 21067:0 is AVP-Id and "\"HARDCODESTRING1=\"RATINGKEY1,\"HARDCODESTRING2=\"RATINGKEY2,\"HARDCODESTRING3=\"RATINGKEY3" is expressionString.  
		
		
		
		String strValue="";
		String hardcodeString;
		String ratingKey;
		
		if(expressionString!=null){
			String keyValueArray[] = expressionString.split(",");
			int numOfKeyValue = keyValueArray.length;
			String keyValue;
			for(int currentKeyValueIndex=0;currentKeyValueIndex<numOfKeyValue;currentKeyValueIndex++){
				keyValue = keyValueArray[currentKeyValueIndex];
				int index = keyValue.indexOf("=");
				
				if(index!=-1){
					try{
						hardcodeString = keyValue.substring(0, index+2);
						ratingKey = keyValue.substring(index+2);
						if(ratingKey!=null && ratingKey.length()>0){
							String mapValue = dummyResponseMap.get(ratingKey);
							if (mapValue == null || (mapValue.length()==0)){
								mapValue = unquote(defaultValuesMap.get(ratingKey));
							}
							if(mapValue !=null && mapValue.length()>0){
								mapValue = getValue(mapValue, valueNodes);
							} else {
								if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
									LogManager.getLogger().warn(MODULE, "Value for key: " + hardcodeString);
								}
							}
							if(mapValue!=null && mapValue.length()>0){
								hardcodeString = unquote(hardcodeString);
								if(currentKeyValueIndex!=numOfKeyValue-1){
									strValue = strValue+hardcodeString+mapValue+",";
								}else {
									strValue = strValue+hardcodeString+mapValue;
								}	
							}
						}else {
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "No rating key configured for key :"+hardcodeString+" so key value pair will not be added to ");
						}
					}catch (IndexOutOfBoundsException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().debug(MODULE, "Invalid Key-Value pair :"+keyValue+" configured");
					}	
					
				}
				
			}
		}
		if(!(strValue.length()>0))
			strValue =expressionString;
		return strValue;
	}
}
