package com.elitecore.aaa.rm.translator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.diameter.translators.BaseTranslator;
import com.elitecore.aaa.diameter.translators.CrestelOCSv2SrcDstKeyword;
import com.elitecore.aaa.diameter.translators.MathOpKeyword;
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
import com.elitecore.coreradius.commons.attributes.IRadiusGroupedAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.crestelocs.core.common.attribute.ChargingAttributeFactory;
import com.elitecore.crestelocs.fw.attribute.IChargingAttribute;
import com.elitecore.crestelocs.fw.packet.IChargingPacket;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class RadiusToCrestelOCSv2Translator extends BaseTranslator {
	
	private static final String MODULE = "RAD-OCSv2-TRNSLTR";

	public RadiusToCrestelOCSv2Translator(ServerContext serverContext) {
		super(serverContext);
	}

	@Override
	public String getFromId() {
		return AAATranslatorConstants.RADIUS_TRANSLATOR;
	}

	@Override
	public String getToId() {
		return AAATranslatorConstants.CRESTEL_OCSv2_TRANSLATOR;
	}
	
	
	@Override
	public void init(TranslatorPolicyData policyData)
			throws InitializationFailedException {
		super.init(policyData);
		KeywordContext keywordContext = getKeywordContext();
		registerKeyword(new RadiusSrcKeyword(AAATranslatorConstants.SOURCE_REQUEST,keywordContext),false);
		registerKeyword(new CrestelOCSv2SrcDstKeyword(AAATranslatorConstants.DESTINATION_REQUEST,keywordContext),false);
		

		StrOptKeyword strOptKeyword = new StrOptKeyword(TranslatorConstants.STROPT,keywordContext);
		registerKeyword(strOptKeyword, true);
		registerKeyword(strOptKeyword, false);

		TimeStampKeyword timeStampKeyword =new TimeStampKeyword(TranslatorConstants.TIMESTAMP,keywordContext);
		registerKeyword(timeStampKeyword, true);
		registerKeyword(timeStampKeyword, false);
		

		MathOpKeyword mathOpKeyword = new MathOpKeyword(TranslatorConstants.MATHOP, keywordContext);
		registerKeyword(mathOpKeyword, true);
		registerKeyword(mathOpKeyword, false);

		registerKeyword(new Mac2TGppKeyword(TranslatorConstants.MAC2TGPP, keywordContext), true);
		registerKeyword(new Mac2TGppKeyword(TranslatorConstants.MAC2TGPP, keywordContext), false);
	}


	@Override
	public void translateRequest(String policyDataId, TranslatorParams params)
	throws TranslationFailedException {

		TranslatorPolicy policy = policiesMap.get(policyDataId);
		if(policy == null)
			throw new TranslationFailedException("Invalid Policy id: " + policyDataId);

		RadServiceRequest radiusRequest =	(RadServiceRequest)params.getParam(AAATranslatorConstants.FROM_PACKET);
		IChargingPacket crestelOCSv2Request = (IChargingPacket)params.getParam(AAATranslatorConstants.TO_PACKET);

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Recieved Radius packet:" + radiusRequest.toString());
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Translation from Radius to Crestel OCSv2 started");
		}
		ValueProvider valueProvider = new RequestValueProviderImpl(radiusRequest);
		applyRequestMapping(policy,radiusRequest,crestelOCSv2Request,params,valueProvider);
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Translation from Radius to Crestel-OCSv2 Completed");
			LogManager.getLogger().debug(MODULE, "Translated Crestel-OCSv2 packet:" + crestelOCSv2Request.toString());
		}	

	}

	private void applyRequestMapping(TranslatorPolicy policy,RadServiceRequest radiusRequest,IChargingPacket crestelOCSv2Request, TranslatorParams params,ValueProvider valueProvider) {
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyRequestMapping(basePolicy,radiusRequest,crestelOCSv2Request,params,valueProvider);
		}	
		handleRadiusToCrestelOCSv2Translation(policy.getParsedRequestMappingInfoMap(),radiusRequest,crestelOCSv2Request,params,valueProvider);
		
	}

	private void handleRadiusToCrestelOCSv2Translation(Map<String,ParsedMappingInfo> infoMap,RadServiceRequest radiusRequest,IChargingPacket crestelOCSv2Request, TranslatorParams params,ValueProvider valueProvider) {

		LogicalExpression expression = null;
		List<ParsedMapping> mappings;
		for(ParsedMappingInfo mappingInfo : infoMap.values()){
			
			if(isMappingApplicable(mappingInfo.getInRequestExpression(),mappingInfo.getInRequestStrExpression(),valueProvider)){
				LogManager.getLogger().info(MODULE, "Request Translation using mapping: "+mappingInfo.getMappingName());
			//	setCommandCodeAndAppId(diameterPacket,(DiameterParsedMappingInfoImpl)mappingInfo);
				params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, mappingInfo.getMappingName());
				boolean isDummyResponse = mappingInfo.isDummyResponse();
				params.setParam(TranslatorConstants.DUMMY_MAPPING, isDummyResponse);
				if(isDummyResponse){
					return;
				}
				LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + mappingInfo.getInRequestStrExpression() + " outMessageType: " + mappingInfo.getOutRequestType());				
				mappings = mappingInfo.getParsedRequestMappingDetails();				
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyRadiusToCrestelOCSv2Mappings(radiusRequest, crestelOCSv2Request, mapping,params);		
					}else{
						expression = mapping.getExpression();				
						if((expression).evaluate(valueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyRadiusToCrestelOCSv2Mappings(radiusRequest, crestelOCSv2Request, mapping,params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
				break;
			}
			
		}
	}
	

	private void applyRadiusToCrestelOCSv2Mappings(final RadServiceRequest radiusRequest,IChargingPacket crestelOCSv2Request, ParsedMapping mapping,TranslatorParams params){

		Map<String,String>defaultValues = mapping.getDefaultValuesMap();
		Map<String,String>mappingNodes = mapping.getMappingNodes();
		Map<String,String>valueNodes = mapping.getValueNodes();

		String avpId ;
		String value ;
		String strValue;

		for(Entry<String, String>entry: mappingNodes.entrySet()){
			strValue  = null;
			avpId = entry.getKey();
			value = entry.getValue();
			if(isMultimodeEnabled(value)){
				String multiModeArgument  = getOperatorArgument(value);
				
				value = stripKeywordName(value);
				IChargingAttribute chargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(avpId, "", false);
				if(chargingAttribute!=null){
					List<IRadiusAttribute> avpList = (List<IRadiusAttribute>)radiusRequest.getRadiusAttributes(value, true);
					
					if (avpList != null && avpList.size() > 0){
						chargingAttribute =null;
						if(multiModeArgument!=null){
							try{
								int numOFAttr = avpList.size();
								for (int i=0 ; i<numOFAttr; i++){
									chargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(avpId, getValue(avpList.get(i).getStringValue(), valueNodes), false);
									buildCrestelOCSv2GroupedAVP(chargingAttribute,multiModeArgument, avpList.get(i), defaultValues, valueNodes);
									crestelOCSv2Request.addAttribute(chargingAttribute);
								}
							}catch (Exception e) {
								if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
									LogManager.getLogger().info(MODULE, "failed to apply JSON format, Reason: "+e.getMessage()+", so attribute: "+avpId+" will not be add to packet");
							}
						}else {
							int numOFAttr = avpList.size();
							for (int i=0 ; i<numOFAttr ; i++){
								chargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(avpId, "", false);
								strValue = getValue(avpList.get(i).getStringValue(), valueNodes);
								chargingAttribute.setValue(strValue);
								crestelOCSv2Request.addAttribute(chargingAttribute);
							}
						}
					}else {
						strValue = defaultValues.get(avpId);
						if(strValue!=null && strValue.length()>0){
							strValue = getValue(strValue, valueNodes);
							chargingAttribute.setValue(strValue);
							crestelOCSv2Request.addAttribute(chargingAttribute);
						}
					}
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Crestel OCSv2 Attribute not found for Attribute Id:"+avpId);
				}
			}else{
				if(isLiteral(value)){
					IChargingAttribute chargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(avpId, unquote(value), false);
					if(chargingAttribute!=null){
						try{
							strValue = unquote(value);
							buildCrestelOCSv2GroupedAVP(chargingAttribute,strValue, radiusRequest, defaultValues, valueNodes);
						} catch (Exception e){
							if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
								LogManager.getLogger().info(MODULE, "failed to apply JSON format, Reason:"+e.getMessage());
							strValue = getValue(strValue, valueNodes);
							chargingAttribute.setValue(strValue);
						}
						crestelOCSv2Request.addAttribute(chargingAttribute);
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Crestel OCSv2 Attribute not found for Attribute Id:"+avpId);
					}	
				}else{
					if (isKeyword(value)) {
						strValue = getKeywordValue(value, params, true,new com.elitecore.core.commons.data.ValueProvider(){
							@Override
							public String getStringValue(String identifier) {
								if(identifier!=null){
									IRadiusAttribute radiusAttribute = radiusRequest.getRadiusAttribute(identifier);
									if(radiusAttribute!=null)
										return radiusAttribute.getStringValue();
								}
								return null;
							}

						});
					}else{
						IRadiusAttribute radiusAttribute = radiusRequest.getRadiusAttribute(value);
						if(radiusAttribute!=null)
							strValue = radiusAttribute.getStringValue();
					}
					if(strValue == null || !(strValue.length()>0)){
						strValue = unquote(defaultValues.get(avpId));
						if(strValue == null || !(strValue.length()>0)){		
							LogManager.getLogger().debug(MODULE,"No DefaultValue provided for Atrribute Id: " + avpId);
						}
					}
					if(strValue !=null && strValue.length()>0){
						strValue = getValue(strValue, valueNodes);
						IChargingAttribute chargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(avpId, strValue, false);
						if(chargingAttribute!=null)
							crestelOCSv2Request.addAttribute(chargingAttribute);
						else {
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "Crestel OCSv2 Attribute not found for Attribute Id:"+avpId);
						}

					}

				}
			}

		}
	}
	
	private void buildCrestelOCSv2GroupedAVP(IChargingAttribute chagringGroupAttribute,String value,RadServiceRequest radiusRequest,Map<String, String> defaultValues, Map<String, String> valueNodes) {

			JSONObject jsonObject = JSONObject.fromObject(value);
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
							IChargingAttribute childChargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(strId, "", false);
							if(childChargingAttribute!=null)
								buildCrestelOCSv2GroupedAVP(childChargingAttribute,key, radiusRequest, defaultValues, valueNodes);
						}else {
							if(isLiteral(key)){
								strValue = unquote(key);
							}else {
								IRadiusAttribute radiusAttribute ;
								radiusAttribute = radiusRequest.getRadiusAttribute(key);
								if(radiusAttribute!=null)
									strValue = radiusAttribute.getStringValue();
							}
							if (strValue == null || (strValue.length()==0)){
								strValue = unquote(defaultValues.get(key));
							}
							if(strValue !=null && strValue.length()>0){
								strValue = getValue(strValue, valueNodes);
								IChargingAttribute childChargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(strId, strValue, false);
								if(childChargingAttribute!=null){
									chagringGroupAttribute.setValue(childChargingAttribute);
								}
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
						IChargingAttribute childChargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(strId, "", false);
						if(childChargingAttribute!=null)
							buildCrestelOCSv2GroupedAVP(childChargingAttribute,key, radiusRequest, defaultValues, valueNodes);
					} else{
						if(isLiteral(key)){
							strValue = unquote(key);
						}else {
							IRadiusAttribute radiusAttribute ;
							radiusAttribute = radiusRequest.getRadiusAttribute(key);
							if(radiusAttribute!=null)
								strValue = radiusAttribute.getStringValue();
						}
						if (strValue == null || (strValue.length()==0)){
							strValue = unquote(defaultValues.get(key));
						}
						if(strValue !=null && strValue.length()>0){
							strValue = getValue(strValue, valueNodes);
							IChargingAttribute childChargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(strId, strValue, false);
							if(childChargingAttribute!=null){
								chagringGroupAttribute.setValue(childChargingAttribute);
							}
						} else {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Value for key: " + key + " Not found in response or in default value configuration");
							}
						}
					}
					
				}
			}
			
	}
	
	private void buildCrestelOCSv2GroupedAVP(IChargingAttribute chagringGroupAttribute, String value,IRadiusAttribute radiusAttribute, Map<String, String> defaultValues,Map<String, String> valueNodes) {
		JSONObject jsonObject = JSONObject.fromObject(value);
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
						IChargingAttribute childChargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(strId, "", false);
						if(childChargingAttribute!=null)
							buildCrestelOCSv2GroupedAVP(childChargingAttribute,key, radiusAttribute, defaultValues, valueNodes);
					}else {
						if(isLiteral(key)){
							strValue = getValue(unquote(key), valueNodes);
							IChargingAttribute childChargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(strId, strValue, false);
							if(childChargingAttribute!=null){
								chagringGroupAttribute.setValue(childChargingAttribute);
							}
						}else {
							List<IRadiusAttribute> childAvpList = (List<IRadiusAttribute>)((IRadiusGroupedAttribute)radiusAttribute).getSubAttributes(Integer.parseInt(key));
							if(childAvpList!=null && childAvpList.size()>0){
								IChargingAttribute childChargingAttr = ChargingAttributeFactory.getInstance().makeAttribute(strId, strValue, false);
								if(childChargingAttr!=null){
									int numOfChildAvp = childAvpList.size();
									String subAttrValue;
									for(int j=0;j<numOfChildAvp;j++){
										subAttrValue = childAvpList.get(j).getStringValue();
										childChargingAttr = ChargingAttributeFactory.getInstance().makeAttribute(strId, subAttrValue, false);
										strValue  = getValue(subAttrValue, valueNodes);
										childChargingAttr.setValue(strValue);
										chagringGroupAttribute.setValue(childChargingAttr);
									}
								}
							}else {
								strValue = defaultValues.get(strId);
								if(strValue!=null && strValue.length()>0){
									strValue = getValue(strValue, valueNodes);
									IChargingAttribute childChargingAttr = ChargingAttributeFactory.getInstance().makeAttribute(strId, strValue, false);
									if(childChargingAttr!=null){
										chagringGroupAttribute.setValue(childChargingAttr);
									}
								}
							}
						}
					}
				}
			
				
			} else {
				key = jsonObject.optString(strId);
				if (key.startsWith("{") && key.endsWith("}")){
					IChargingAttribute childChargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(strId, "", false);
					if(childChargingAttribute!=null)
						buildCrestelOCSv2GroupedAVP(childChargingAttribute,key, radiusAttribute, defaultValues, valueNodes);
				} else{
					if(isLiteral(key)){
						strValue = getValue(unquote(key),valueNodes);
						IChargingAttribute childChargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(strId, strValue, false);
						if(childChargingAttribute!=null){
							chagringGroupAttribute.setValue(childChargingAttribute);
						}
					}else {
						List<IRadiusAttribute> childAvpList = (List<IRadiusAttribute>)((IRadiusGroupedAttribute)radiusAttribute).getSubAttributes(Integer.parseInt(key));
						if(childAvpList!=null && childAvpList.size()>0){
							IChargingAttribute childChargingAttr = ChargingAttributeFactory.getInstance().makeAttribute(strId, strValue, false);
							if(childChargingAttr!=null){
								int numOfChildAvp = childAvpList.size();
								String subAttrValue;
								for(int j=0;j<numOfChildAvp;j++){
									subAttrValue = childAvpList.get(j).getStringValue();
									childChargingAttr = ChargingAttributeFactory.getInstance().makeAttribute(strId,subAttrValue,false);
									strValue  = getValue(subAttrValue, valueNodes);
									childChargingAttr.setValue(strValue);
									chagringGroupAttribute.setValue(childChargingAttr);
								}
							}
						}else {
							strValue = defaultValues.get(strId);
							if(strValue!=null && strValue.length()>0){
								strValue = getValue(strValue, valueNodes);
								IChargingAttribute childChargingAttr = ChargingAttributeFactory.getInstance().makeAttribute(strId, strValue, false);
								if(childChargingAttr!=null){
									chagringGroupAttribute.setValue(childChargingAttr);
								}
							}
						}
					}
				}
			}
		}
	}


	@Override
	public void translateResponse(String policyDataId, TranslatorParams params)
	throws TranslationFailedException {

		IChargingPacket crestelOCSv2Response = (IChargingPacket)params.getParam(AAATranslatorConstants.FROM_PACKET);
		RadServiceResponse radiusResponse = (RadServiceResponse)params.getParam(AAATranslatorConstants.TO_PACKET);
		RadServiceRequest srcRequest = (RadServiceRequest)params.getParam(AAATranslatorConstants.SOURCE_REQUEST);

		if(crestelOCSv2Response != null){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){				
			LogManager.getLogger().debug(MODULE, "Crestel OCSv2 packet to translate: "+crestelOCSv2Response);
		}
		}
		
		RequestValueProviderImpl requestValueProvider = new RequestValueProviderImpl(srcRequest);
		applyResponseMapping(policiesMap.get(policyDataId), crestelOCSv2Response, radiusResponse,params,requestValueProvider);
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Translation from Crestel-OCSv2 to RADIUS Completed");
			LogManager.getLogger().debug(MODULE, "Translated RADIUS packet:" + radiusResponse.toString());
		}
	}
	private void applyResponseMapping(TranslatorPolicy translatorPolicy,IChargingPacket crestelOCSv2Response,RadServiceResponse radiusResponse, TranslatorParams params,RequestValueProviderImpl requestValueProvider) throws TranslationFailedException {
		TranslatorPolicy basePolicy = policiesMap.get(translatorPolicy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyResponseMapping(basePolicy, crestelOCSv2Response, radiusResponse,params,requestValueProvider);
		}	
		handleCrestelOCSv2ToRadiusTranslation(translatorPolicy.getParsedResponseMappingInfoMap(), crestelOCSv2Response, radiusResponse, params,requestValueProvider,translatorPolicy.getDummyParametersMap());
	}
	private void handleCrestelOCSv2ToRadiusTranslation(Map<String,ParsedMappingInfo> infoMap,IChargingPacket crestelOCSv2Response,RadServiceResponse radiusResponse, TranslatorParams params,RequestValueProviderImpl requestValueProvider,Map<String, String> dummyMap) throws TranslationFailedException {

		ResponseValueProviderImpl valueProvider = new ResponseValueProviderImpl(crestelOCSv2Response);
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
			LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + mappingInfo.getInRequestStrExpression()+ " outMessageType: " + mappingInfo.getOutRequestType());
			mappings = mappingInfo.getParsedResponseMappingDetails();
		LogManager.getLogger().info(MODULE, "Response Translation using mapping: "+mappingInfo.getMappingName());
				if(mappingInfo.isDummyResponse()){
					translateDummyResponse(mappings,dummyMap,radiusResponse,params);
					return ;
				}
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyCrestelOCSv2ToRadiusMappings(crestelOCSv2Response, radiusResponse, mapping,params);						
					}else{
						expression = mapping.getExpression();				
						if(expression.evaluate(valueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyCrestelOCSv2ToRadiusMappings(crestelOCSv2Response, radiusResponse, mapping,params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
			}
	
	
	private void translateDummyResponse(List<ParsedMapping> mappings,Map<String, String> dummyMap, RadServiceResponse radiusResponse,TranslatorParams params) {
		LogicalExpression expression = null;
		DummyResponseValueProvider valueProvider = new DummyResponseValueProvider(dummyMap);
		for(ParsedMapping mapping : mappings){
			LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
			if("*".equalsIgnoreCase(mapping.getStrExpression())){
				LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
				applyDummyCrestelOCSv2ToRadiusMappings(dummyMap, radiusResponse, mapping,params);						
			}else{
				expression = mapping.getExpression();				
				if(expression.evaluate(valueProvider)){
					LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
					applyDummyCrestelOCSv2ToRadiusMappings(dummyMap, radiusResponse, mapping,params);
				}else{
					LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
				}		
			}	
		}
		
	}
	/**
	 * Method will be used when Dummy Response is True.
	 */
	private void applyDummyCrestelOCSv2ToRadiusMappings(	final Map<String, String> dummyMap, RadServiceResponse radiusResponse,ParsedMapping mapping, TranslatorParams params) {

		
		Map<String,String>defaultValues = mapping.getDefaultValuesMap();
		// Mapping nodes <0:1, "abc">
		// OR  			<0:1, 0:1>
		Map<String,String>mappingNodes = mapping.getMappingNodes();
		Map<String,String>valueNodes = mapping.getValueNodes();
		
		String avpId ;
		String value ;
		String strValue;
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			strValue  = null;
			avpId = entry.getKey();
			value = entry.getValue();
			

			if(isLiteral(value)){
				try{
					strValue = unquote(value);
					if(!AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(avpId))
						strValue =  getGroupedAVPValue(strValue, dummyMap, defaultValues, valueNodes);
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
						return dummyMap.get(identifier);

					}
					
				});
			
			}else{
				String mapValue = dummyMap.get(value);
				if(mapValue!=null){
					strValue = mapValue;
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
						radiusResponse.setPacketType(Integer.parseInt(strValue));
					}catch (NumberFormatException e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "Invalid packet derived from: "+ value);
					}	
				}else{
					addAvp(avpId, strValue, valueNodes, radiusResponse);
				}
			}
		}		
	}
	private void applyCrestelOCSv2ToRadiusMappings(final IChargingPacket crestelOCSv2Response,RadServiceResponse radiusResponse,ParsedMapping mapping, TranslatorParams params) {
		
		Map<String,String>defaultValues = mapping.getDefaultValuesMap();
		// Mapping nodes <0:1, "abc">
		// OR  			<0:1, 0:1>
		Map<String,String>mappingNodes = mapping.getMappingNodes();
		Map<String,String>valueNodes = mapping.getValueNodes();
		
		String avpId ;
		String value ;
		String strValue;
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			strValue  = null;
			avpId = entry.getKey();
			value = entry.getValue();
			
			if (isMultimodeEnabled(value)){
				String multiModeArgument  = getOperatorArgument(value);
				value = stripKeywordName(value);
				List<IChargingAttribute> avpList = crestelOCSv2Response.getAttributes(value);
				
				if (avpList != null && avpList.size() > 0){
					if(multiModeArgument!=null){
						IRadiusAttribute radiusAttribute;
						try{
							int numOfAttr = avpList.size();
							for (int i=0 ; i<numOfAttr; i++){
								radiusAttribute = Dictionary.getInstance().getAttribute(avpId);
								strValue = getGroupedAVPValue(multiModeArgument,avpList.get(i), defaultValues, valueNodes);
								radiusAttribute.setStringValue(strValue);
								radiusResponse.addAttribute(radiusAttribute);
							}
						}catch (InvalidAttributeIdException e) {
							LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
						}catch (Exception e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
								LogManager.getLogger().info(MODULE, "failed to apply JSON format, Reason: "+e.getMessage()+", so attribute: "+avpId+" will not be add to packet");
						}
					}else {
						IRadiusAttribute radiusAttribute;
						try{
							int numOfAttr = avpList.size();
							for (int i=0 ; i<numOfAttr; i++){
								radiusAttribute = Dictionary.getInstance().getAttribute(avpId);
								strValue = getValue(avpList.get(i).getStringValue(), valueNodes);
								radiusAttribute.setStringValue(strValue);
								radiusResponse.addAttribute(radiusAttribute);
							}
						}catch (InvalidAttributeIdException e) {
							LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
						}
					}
				}else {
					strValue = defaultValues.get(avpId);
					try{
						if(strValue!=null && strValue.length()>0){
							strValue = getValue(strValue, valueNodes);
							IRadiusAttribute radiusAttribute = Dictionary.getInstance().getAttribute(avpId);
							radiusAttribute.setStringValue(strValue);
							radiusResponse.addAttribute(radiusAttribute);
						}
					}catch (InvalidAttributeIdException e) {
						LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
					}
					
				}
			}else {
				if(isLiteral(value)){
					try{
						strValue = unquote(value);
						if(!AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(avpId))
							strValue =  getGroupedAVPValue(strValue, crestelOCSv2Response, defaultValues, valueNodes);
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
							IChargingAttribute chargingAttribute = crestelOCSv2Response.getAttribute(identifier,TranslatorConstants.RATING_SEPERATOR);
							if(chargingAttribute==null)
								return null;
							return chargingAttribute.getStringValue();

						}
						
					});
				
				}else{
					IChargingAttribute chargingAttribute = crestelOCSv2Response.getAttribute(value,TranslatorConstants.RATING_SEPERATOR);;
					if(chargingAttribute!=null){
						strValue = chargingAttribute.getStringValue();
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
							radiusResponse.setPacketType(Integer.parseInt(strValue));
						}catch (NumberFormatException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE, "Invalid packet derived from: "+ value);
						}	
					}else{
						addAvp(avpId, strValue, valueNodes, radiusResponse);
					}
				}
			}
		}		
	}
	private void addOrReplaceAvp(String avpId, String avpValue,Map<String, String> valueNodes, RadServiceResponse radiusResponse) {
		avpValue = getValue(avpValue,valueNodes);
		try {
			IRadiusAttribute attribute = radiusResponse.getRadiusAttribute(true,avpId);
			if(attribute != null){
				attribute.setStringValue(avpValue);
			}else{
				attribute = Dictionary.getInstance().getAttribute(avpId);
				attribute.setStringValue(avpValue);
				radiusResponse.addAttribute(attribute);
			}
		}catch (AttributeTypeNotFoundException e) {
			LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
		} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
		}
	}
	
	private void addAvp(String avpId, String avpValue,Map<String, String> valueNodes, RadServiceResponse radiusResponse) {
		avpValue = getValue(avpValue,valueNodes);
		try {
			IRadiusAttribute attribute = Dictionary.getInstance().getAttribute(avpId);
			attribute.setStringValue(avpValue);
			radiusResponse.addAttribute(attribute);
		}catch (AttributeTypeNotFoundException e) {
			LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
		} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().error(MODULE, "Invalid Attribute id is configured AttributeID : " + e.getMessage());
		}
	}

	private String getGroupedAVPValue(String value,IChargingPacket crestelOCSv2Response,Map<String, String> defaultValues, Map<String, String> valueNodes) {

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
						
						if(key.startsWith("{") && key.endsWith("}")){
							resultjsonObject.put(strId, getGroupedAVPValue(key, crestelOCSv2Response, defaultValues, valueNodes));
						}else {
							if(isLiteral(key)){
								strValue = unquote(key);
							}else {
								IChargingAttribute chargingAttribute = crestelOCSv2Response.getAttribute(key, TranslatorConstants.RATING_TRANSLATOR);
								if(chargingAttribute!=null){
									strValue = chargingAttribute.getStringValue();
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
						resultjsonObject.put(strId, getGroupedAVPValue(key, crestelOCSv2Response, defaultValues, valueNodes));
					} else{
						if(isLiteral(key)){
							strValue = unquote(key);
						}else {
							IChargingAttribute chargingAttribute = crestelOCSv2Response.getAttribute(key, TranslatorConstants.RATING_TRANSLATOR);
							if(chargingAttribute!=null){
								strValue = chargingAttribute.getStringValue();
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
	

	private String getGroupedAVPValue(String multiModeArgument,IChargingAttribute chargingGroupAttribute,Map<String, String> defaultValues, Map<String, String> valueNodes) {

		String resultString = multiModeArgument;
			JSONObject jsonObject = JSONObject.fromObject(multiModeArgument);
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
						if(key.startsWith("{") && key.endsWith("}")){
							resultjsonObject.put(strId, getGroupedAVPValue(key, chargingGroupAttribute, defaultValues, valueNodes));
						}else {
							if(isLiteral(key)){
								strValue = getValue(unquote(key), valueNodes);
								resultjsonObject.put(strId, strValue);
							}else {
								List<IChargingAttribute> chargingAttributeList = chargingGroupAttribute.getGroupedValue(key);
								if(chargingAttributeList!=null && chargingAttributeList.size()>0){
									int numOfChildAttr = chargingAttributeList.size();
									for(int j=0;j<numOfChildAttr;j++){
										IChargingAttribute chargingAttribute = chargingAttributeList.get(j); 
										if(chargingAttribute!=null){
											strValue = getValue(chargingAttribute.getStringValue(), valueNodes);
											resultjsonObject.put(strId, strValue);
										}
									}
								}else {
									strValue = defaultValues.get(strId);
									if(strValue!=null && strValue.length()>0){
										resultjsonObject.put(strId, getValue(strValue, valueNodes));
									}
								}
							}
						}
					}
				} else {
					key = jsonObject.optString(strId);
					if (key.startsWith("{") && key.endsWith("}")){
						resultjsonObject.put(strId, getGroupedAVPValue(key, chargingGroupAttribute, defaultValues, valueNodes));
					} else{
						if(isLiteral(key)){
							strValue = getValue(unquote(key), valueNodes);
							resultjsonObject.put(strId, strValue);
						}else {
							List<IChargingAttribute> chargingAttributeList = chargingGroupAttribute.getGroupedValue(key);
							if(chargingAttributeList!=null && chargingAttributeList.size()>0){
								int numOfChildAttr = chargingAttributeList.size();
								for(int j=0;j<numOfChildAttr;j++){
									IChargingAttribute chargingAttribute = chargingAttributeList.get(j); 
									if(chargingAttribute!=null){
										strValue = getValue(chargingAttribute.getStringValue(), valueNodes);
										resultjsonObject.put(strId, strValue);
									}
								}
							}else {
								strValue = defaultValues.get(strId);
								if(strValue!=null && strValue.length()>0){
									resultjsonObject.put(strId, getValue(strValue, valueNodes));
								}
							}
						}
					}
				}
			}
			resultString = resultjsonObject.toString();
		
		return resultString;
	}
	private class RequestValueProviderImpl implements ValueProvider{
		private RadServiceRequest request;
		
		public RequestValueProviderImpl(RadServiceRequest request) {
			this.request = request;
		}
		
		@Override
		public String getStringValue(String identifier)throws InvalidTypeCastException,MissingIdentifierException {
			if(AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)){
				return String.valueOf(request.getPacketType());
			}
			IRadiusAttribute attribute = request.getRadiusAttribute(identifier,true);
			if(attribute != null)
				return attribute.getStringValue();
			else
				throw new MissingIdentifierException("Requested value not found in the request: " +identifier);
		}

		@Override
		public long getLongValue(String identifier)throws InvalidTypeCastException,MissingIdentifierException{
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
		private IChargingPacket chargingPacket;
		private ResponseValueProviderImpl(IChargingPacket chargingPacket){
			this.chargingPacket = chargingPacket;
		}

		@Override
		public long getLongValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
			IChargingAttribute chargingAttribute = chargingPacket.getAttribute(Integer.parseInt(identifier), 0);
			if(chargingAttribute != null)
				try{
					return Long.parseLong(chargingAttribute.getStringValue());
				}catch(Exception e){
					throw new InvalidTypeCastException(e.getMessage());
				}
				
			else
				throw new MissingIdentifierException("Object not found: "+identifier);
		}

		@Override
		public String getStringValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
			IChargingAttribute chargingAttribute = chargingPacket.getAttribute(Integer.parseInt(identifier), 0);
			if(chargingAttribute != null)
				return chargingAttribute.getStringValue();								
			else
				throw new MissingIdentifierException("Object not found: "+identifier);
		}
		
		@Override
		public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			List<IChargingAttribute> chargingAttributteList=chargingPacket.getAttributes(identifier);
			if(chargingAttributteList!=null){
				List<String> stringValues=new ArrayList<String>();
				for(IChargingAttribute iChargingAttribute : chargingAttributteList){
					stringValues.add(iChargingAttribute.getStringValue());
	}
				return stringValues;
			}else
				throw new MissingIdentifierException("Object not found: "+identifier);
		}

		@Override
		public List<Long> getLongValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			List<IChargingAttribute> chargingAttributteList=chargingPacket.getAttributes(identifier);
			if(chargingAttributteList!=null){
				List<Long> longValues=new ArrayList<Long>();
				for(IChargingAttribute iChargingAttribute : chargingAttributteList){
					longValues.add(iChargingAttribute.getLongValue());
				}
				return longValues;
			}else
				throw new MissingIdentifierException("Object not found: "+identifier);
		}

		@Override
		public Object getValue(String key) {
			return null;
		}

}
}
