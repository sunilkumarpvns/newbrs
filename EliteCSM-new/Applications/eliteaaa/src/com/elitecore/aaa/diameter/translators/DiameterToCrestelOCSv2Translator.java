package com.elitecore.aaa.diameter.translators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.translators.policy.TranslatorPolicy;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMapping;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMappingInfo;
import com.elitecore.aaa.radius.translators.Mac2TGppKeyword;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.crestelocs.core.common.attribute.ChargingAttributeFactory;
import com.elitecore.crestelocs.fw.attribute.IChargingAttribute;
import com.elitecore.crestelocs.fw.packet.IChargingPacket;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class DiameterToCrestelOCSv2Translator extends BaseTranslator {
	
	private static final String MODULE = "DIAMETER-OCSv2-TRNSLTR";
	private IStackContext stackContext;

	public DiameterToCrestelOCSv2Translator(ServerContext serverContext,IStackContext stackContext) {
		super(serverContext);
		this.stackContext = stackContext;
	}

	@Override
	public String getFromId() {
		return TranslatorConstants.DIAMETER_TRANSLATOR;
	}

	@Override
	public String getToId() {
		return TranslatorConstants.CRESTEL_OCSv2_TRANSLATOR;
	}
	
	@Override
	public void init(TranslatorPolicyData policyData)
			throws InitializationFailedException {
		super.init(policyData);
		
		KeywordContext keywordContext = getKeywordContext();
		registerKeyword(new DiameterSrcDstKeyword(TranslatorConstants.SOURCE_REQUEST,keywordContext),false);
		registerKeyword(new CrestelOCSv2SrcDstKeyword(TranslatorConstants.DESTINATION_REQUEST,keywordContext),false);

		StrOptKeyword strOptKeyword = new StrOptKeyword(TranslatorConstants.STROPT,keywordContext); 
		registerKeyword(strOptKeyword, true);
		registerKeyword(strOptKeyword, false);
		
		registerKeyword(new TimeStampKeyword(TranslatorConstants.TIMESTAMP,keywordContext), true);
		registerKeyword(new DiameterTimestampKeyword(TranslatorConstants.TIMESTAMP,keywordContext), false);
		
		registerKeyword(new PeerSequenceKeyword(TranslatorConstants.SEQPEER,keywordContext, stackContext), true);
		registerKeyword(new SessionSequenceKeyword(TranslatorConstants.SEQSESS,policyData.getName(), keywordContext,DiameterAVPConstants.SESSION_ID, stackContext), true);
		registerKeyword(new ServerSequenceKeyword(TranslatorConstants.SEQSERV,keywordContext, stackContext), true);

		MathOpKeyword  mathOpKeyword =new MathOpKeyword(TranslatorConstants.MATHOP, keywordContext);
		registerKeyword(mathOpKeyword, true);
		registerKeyword(mathOpKeyword, false);

		registerKeyword(new Mac2TGppKeyword(TranslatorConstants.MAC2TGPP, keywordContext), true);
		registerKeyword(new Mac2TGppKeyword(TranslatorConstants.MAC2TGPP, keywordContext), false);
		
	}

	@Override
	public void translateRequest(String policyDataId, TranslatorParams params)
			throws TranslationFailedException {

		DiameterPacket diameterRequestPacket = (DiameterPacket)params.getParam(TranslatorConstants.FROM_PACKET);
		TranslatorPolicy policy = policiesMap.get(policyDataId);
		IChargingPacket crestelOCSv2Request = (IChargingPacket)params.getParam(TranslatorConstants.TO_PACKET);
		
		if(diameterRequestPacket.isRequest() ){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Received Diameter Packet: " + diameterRequestPacket.toString());
			}
			
			IDiameterAVP ccRequestTypeAvp = diameterRequestPacket.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
			if(ccRequestTypeAvp != null){
				
				params.setParam(TranslatorConstants.STACK_CONTEXT, stackContext);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Translation from Diameter to OCSv2 started");
				}				
				ValueProvider valueProvider = new DiameterAVPValueProvider(diameterRequestPacket);
				applyRequestMapping(policy, diameterRequestPacket, crestelOCSv2Request, params,valueProvider);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Translation from Diameter to Rating Completed");
					LogManager.getLogger().debug(MODULE, "Translated Crestel-OCSv2 packet:" + crestelOCSv2Request.toString());
				}				
				
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "CC-Request-Type Attribute not present in request");
				throw new TranslationFailedException("CC-Request-Type Attribute not present in request");
			}
		}else{
			throw new TranslationFailedException("Given Diameter Packet is not Request");
		}
	}

	private void applyRequestMapping(TranslatorPolicy policy,DiameterPacket diameterRequestPacket,IChargingPacket crestelOCSv2Request, TranslatorParams params,ValueProvider valueProvider) {
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyRequestMapping(basePolicy,diameterRequestPacket, crestelOCSv2Request, params,valueProvider);
		}	
		handleDiameterToCrestelOCSv2Translation(policy.getParsedRequestMappingInfoMap(), diameterRequestPacket, crestelOCSv2Request, params,valueProvider);
	}

	private void handleDiameterToCrestelOCSv2Translation(Map<String,ParsedMappingInfo> parsedRequestMappingInfoMap,DiameterPacket diameterRequestPacket,IChargingPacket crestelOCSv2Request, TranslatorParams params,ValueProvider valueProvider) {
		LogicalExpression expression = null;
		List<ParsedMapping> mappings;
		for(ParsedMappingInfo mappingInfo : parsedRequestMappingInfoMap.values()){
			
			if(isMappingApplicable(mappingInfo.getInRequestExpression(),mappingInfo.getInRequestStrExpression(),valueProvider)){
				LogManager.getLogger().info(MODULE, "Request Translation using mapping: " + mappingInfo.getMappingName());
				boolean isDummyResponse = mappingInfo.isDummyResponse();
				params.setParam(TranslatorConstants.DUMMY_MAPPING,isDummyResponse);
				params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, mappingInfo.getMappingName());
				if(isDummyResponse){
					return;
				}	
				LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + mappingInfo.getInRequestStrExpression() + " outMessageType: " + mappingInfo.getOutRequestType());
				mappings = mappingInfo.getParsedRequestMappingDetails();				
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyDiameterToCrestelOCSv2Mappings(diameterRequestPacket, crestelOCSv2Request, mapping,params);		
					}else{
						expression = mapping.getExpression();				
						if((expression).evaluate(valueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyDiameterToCrestelOCSv2Mappings(diameterRequestPacket, crestelOCSv2Request, mapping,params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
				break;
			}
			
		}
	
	}

	private void applyDiameterToCrestelOCSv2Mappings(final DiameterPacket diameterRequestPacket,IChargingPacket crestelOCSv2Request, ParsedMapping mapping,TranslatorParams params) {
		Map<String,String>defaultValues = mapping.getDefaultValuesMap();
		Map<String,String>mappingNodes = mapping.getMappingNodes();
		Map<String,String>valueNodes = mapping.getValueNodes();
		String avpId ;
		String value ;
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			avpId = entry.getKey();
			value = entry.getValue();
			String strValue = null;
			LogManager.getLogger().info(MODULE, "Applying mapping expression: "+ avpId + " = " + value);
			if(isMultimodeEnabled(value)){
				String multiModeArgument  =getOperatorArgument(value);
				
				value = stripKeywordName(value);
				IChargingAttribute chargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(avpId, "", false);
				if(chargingAttribute!=null){
					List<IDiameterAVP> avpList = diameterRequestPacket.getAVPList(value,true);
					
					if (avpList != null && avpList.size() > 0){
						chargingAttribute =null;
						if(multiModeArgument!=null){
							try{
								for (int i=0 ; i<avpList.size() ; i++){
									chargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(avpId, getValue(avpList.get(i).getStringValue(), valueNodes), false);
									buildCrestelOCSv2GroupedAVP(chargingAttribute,multiModeArgument, avpList.get(i), defaultValues, valueNodes);
									crestelOCSv2Request.addAttribute(chargingAttribute);
								}
							}catch (Exception e) {
								if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
									LogManager.getLogger().info(MODULE, "failed to apply JSON format, Reason: "+e.getMessage()+", so attribute: "+avpId+" will not be add to packet");
							}
						}else {
							for (int i=0 ; i<avpList.size() ; i++){
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
			}else {
				if(isLiteral(value)){ // remove '"' from the value and set it to the give attribute
					IChargingAttribute chargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(avpId, unquote(value), false);
					if(chargingAttribute!=null){
						try{
							strValue = unquote(value);
							buildCrestelOCSv2GroupedAVP(chargingAttribute,strValue, diameterRequestPacket, defaultValues, valueNodes);
						} catch (Exception e){
							if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
								LogManager.getLogger().info(MODULE, "Configuration of translation mapping response parameters: " + value + "is not in JSON format");
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
									return diameterRequestPacket.getAVPValue(identifier,true);
								}
								return null;
							}

						});
					}else{
						strValue = diameterRequestPacket.getAVPValue(value,true);
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

	private void buildCrestelOCSv2GroupedAVP(IChargingAttribute chagringGroupAttribute, String value,IDiameterAVP diameterAVP, Map<String, String> defaultValues,Map<String, String> valueNodes) {
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
							buildCrestelOCSv2GroupedAVP(childChargingAttribute,key, diameterAVP, defaultValues, valueNodes);
					}else {
						if(isLiteral(key)){
							strValue = getValue(unquote(key), valueNodes);
							IChargingAttribute childChargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(strId, strValue, false);
							if(childChargingAttribute!=null){
								chagringGroupAttribute.setValue(childChargingAttribute);
							}
						}else {
							List<IDiameterAVP> childAvpList = ((AvpGrouped)diameterAVP).getSubAttributeList(key);
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
						buildCrestelOCSv2GroupedAVP(childChargingAttribute,key, diameterAVP, defaultValues, valueNodes);
				} else{
					if(isLiteral(key)){
						strValue = getValue(unquote(key),valueNodes);
						IChargingAttribute childChargingAttribute = ChargingAttributeFactory.getInstance().makeAttribute(strId, strValue, false);
						if(childChargingAttribute!=null){
							chagringGroupAttribute.setValue(childChargingAttribute);
						}
					}else {
						List<IDiameterAVP> childAvpList = ((AvpGrouped)diameterAVP).getSubAttributeList(key);
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
		}
	}

	@Override
	public void translateResponse(String policyDataId, TranslatorParams params)throws TranslationFailedException {

		IChargingPacket crestelOCSv2Response = (IChargingPacket)params.getParam(AAATranslatorConstants.FROM_PACKET);
		ApplicationResponse diameterResponse = (ApplicationResponse)params.getParam(TranslatorConstants.TO_PACKET);
		DiameterPacket	sourceRequest = (DiameterPacket)params.getParam(TranslatorConstants.SOURCE_REQUEST);
		
		if(crestelOCSv2Response == null && !(Boolean.parseBoolean(String.valueOf((params.getParam(TranslatorConstants.DUMMY_MAPPING)))))){

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Failed to Translate From OCSv2 To Diameter, Reason: OCSv2 Response is NULL");
			setResultCode(diameterResponse, ResultCode.DIAMETER_RATING_FAILED.code);
			diameterResponse.setFurtherProcessingRequired(false);
		
		}else{
		if(crestelOCSv2Response!=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){				
				LogManager.getLogger().info(MODULE, "Crestel OCSv2 packet to translate: "+crestelOCSv2Response);
			}
			}
			ValueProvider requestValueProvider = new DiameterAVPValueProvider(sourceRequest);
			applyResponseMapping(policiesMap.get(policyDataId), crestelOCSv2Response, diameterResponse,params,requestValueProvider);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Translation from Crestel-OCSv2 to Diameter Completed");
				LogManager.getLogger().info(MODULE, "Translated Diameter packet:" + diameterResponse.toString());
			}
		}
	}
	private void applyResponseMapping(TranslatorPolicy translatorPolicy,IChargingPacket crestelOCSv2Response,ApplicationResponse diameterResponse, TranslatorParams params,ValueProvider requestValueProvider) throws TranslationFailedException {
		TranslatorPolicy basePolicy = policiesMap.get(translatorPolicy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyResponseMapping(basePolicy,crestelOCSv2Response, diameterResponse,params,requestValueProvider);
		}	
		handleCrestelOCSv2ToDiameterTranslation(translatorPolicy.getParsedResponseMappingInfoMap(), crestelOCSv2Response, diameterResponse,params,requestValueProvider,translatorPolicy.getDummyParametersMap());
	}
	private void handleCrestelOCSv2ToDiameterTranslation(Map<String,ParsedMappingInfo> parsedResponseMappingInfoMap,IChargingPacket crestelOCSv2Response,ApplicationResponse diameterResponse, TranslatorParams params,ValueProvider requestValueProvider,Map<String,String>dummyResponseMap) throws TranslationFailedException {
		ResponseValueProviderImpl valueProvider = new ResponseValueProviderImpl(crestelOCSv2Response);
		LogicalExpression expression = null;
		List<ParsedMapping> mappings;
		ParsedMappingInfo tempMappingInfo;
		String mappingName = (String)params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING);
			if(mappingName == null){
				throw new TranslationFailedException("No Translation Mapping is selected during request translation");
			}
			tempMappingInfo = parsedResponseMappingInfoMap.get(mappingName);
			if(tempMappingInfo == null){
				throw new TranslationFailedException("Invalid Translation Mapping "+mappingName+" is selected during request translation");
			}
			LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + tempMappingInfo.getInRequestStrExpression()+ " outMessageType: " + tempMappingInfo.getOutRequestType());
			mappings = tempMappingInfo.getParsedResponseMappingDetails();
		
			LogManager.getLogger().info(MODULE, "Response Translation using mapping: "+tempMappingInfo.getMappingName());
			if(tempMappingInfo.isDummyResponse()){
					translateDummyResponse(mappings, dummyResponseMap, diameterResponse, params);
					return;
				}
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyCrestelOCSv2ToDiameterMappings(diameterResponse, crestelOCSv2Response, mapping,params);						
					}else{
						expression = mapping.getExpression();				
						if(expression.evaluate(valueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyCrestelOCSv2ToDiameterMappings(diameterResponse, crestelOCSv2Response, mapping,params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
			}
	
	private void applyCrestelOCSv2ToDiameterMappings(ApplicationResponse diameterResponse,final IChargingPacket crestelOCSv2Response, ParsedMapping mapping,TranslatorParams params) {
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
			LogManager.getLogger().info(MODULE, "Applying mapping expression: "+ avpId + " = " + value);
			if (isMultimodeEnabled(value)){
				String multiModeArgument  = getOperatorArgument(value);
				value = stripKeywordName(value);
				List<IChargingAttribute> avpList = crestelOCSv2Response.getAttributes(value);
				
				if (avpList != null && avpList.size() > 0){
					if(multiModeArgument!=null){
						try{
							IDiameterAVP diameterAVP;
							int numOfAVP = avpList.size();
							for (int i=0 ; i<numOfAVP; i++){
								diameterAVP = DiameterDictionary.getInstance().getAttribute(avpId);
								strValue = getGroupedAVPValue(multiModeArgument,avpList.get(i), defaultValues, valueNodes);
								diameterAVP.setStringValue(strValue);
								diameterResponse.addAVP(diameterAVP);
							}	
						}catch (Exception e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "failed to apply JSON format, Reason: "+e.getMessage()+", so attribute: "+avpId+" will not be add to packet");
						}
					}else {
						IDiameterAVP diameterAVP;
						int numOfAVP = avpList.size();
						for (int i=0 ; i<numOfAVP ; i++){
							diameterAVP = DiameterDictionary.getInstance().getAttribute(avpId);
							strValue = getValue(avpList.get(i).getStringValue(), valueNodes);
							diameterAVP.setStringValue(strValue);
							diameterResponse.addAVP(diameterAVP);
						}
					}
				}else {
					strValue = defaultValues.get(avpId);
					if(strValue!=null && strValue.length()>0){
						strValue = getValue(strValue, valueNodes);
						IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute(avpId);
						diameterAVP.setStringValue(strValue);
						diameterResponse.addAVP(diameterAVP);
					}
				}
			
			}else{
				if(isLiteral(value)){
					strValue = unquote(value);
					try{
						strValue =  getGroupedAVPValue(strValue, crestelOCSv2Response, defaultValues, valueNodes);
					}catch (Exception e) {
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
							IChargingAttribute chargingAttribute = crestelOCSv2Response.getAttribute(identifier, TranslatorConstants.RATING_SEPERATOR);
							if(chargingAttribute==null)
								return null;
							return chargingAttribute.getStringValue();

						}
						
					});
				}else{
					IChargingAttribute chargingAttribute = crestelOCSv2Response.getAttribute(value, TranslatorConstants.RATING_SEPERATOR);
					if(chargingAttribute!=null)
						strValue = chargingAttribute.getStringValue();
				}
				if(strValue == null || !(strValue.length()>0)){
					strValue = defaultValues.get(avpId);
					if(strValue != null && strValue.length() > 0){
						LogManager.getLogger().debug(MODULE,"Charging Attribute: "+ value+" is not found in Crestel-OCSv2 Response,using default value: " + strValue);
						addOrReplaceAvp(avpId, strValue, valueNodes, diameterResponse.getDiameterAnswer());
					}else{
						LogManager.getLogger().debug(MODULE,"Skipping " + avpId + ", Reason: "+value+ " Not found in Crestel-OCSv2 and default value is not provided.");
					}
				}else{
					addOrReplaceAvp(avpId, strValue, valueNodes, diameterResponse.getDiameterAnswer());
				}
				
			}

		}		
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

	private void addOrReplaceAvp(String avpId, String avpValue,Map<String, String> valueNodes, DiameterAnswer diameterAnswer) {
		avpValue = getValue(avpValue,valueNodes);
		DiameterUtility.addOrReplaceAvp(avpId,diameterAnswer,avpValue);
	}


	private void buildCrestelOCSv2GroupedAVP(IChargingAttribute chagringGroupAttribute,String value,DiameterPacket diameterRequestPacket,Map<String, String> defaultValues, Map<String, String> valueNodes) {

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
							buildCrestelOCSv2GroupedAVP(childChargingAttribute,key, diameterRequestPacket, defaultValues, valueNodes);
					}else {
						if(isLiteral(key)){
							strValue = unquote(key);
						}else {
							strValue = diameterRequestPacket.getAVPValue(key,true);
						}
						if (strValue == null || (strValue.length()==0)){
							strValue = defaultValues.get(strId);
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
						buildCrestelOCSv2GroupedAVP(childChargingAttribute,key, diameterRequestPacket, defaultValues, valueNodes);
				} else{
					if(isLiteral(key)){
						strValue = unquote(key);
					}else {
						strValue = diameterRequestPacket.getAVPValue(key);
					}
					if (strValue == null || (strValue.length()==0)){
						strValue =defaultValues.get(strId);
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
								strValue = unquote(strValue);
							}else {
								IChargingAttribute chargingAttribute = crestelOCSv2Response.getAttribute(key, TranslatorConstants.RATING_SEPERATOR);
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
							strValue = unquote(strValue);
						}else {
							IChargingAttribute chargingAttribute = crestelOCSv2Response.getAttribute(key, TranslatorConstants.RATING_SEPERATOR);
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
	private void setResultCode(ApplicationResponse response,int resultCode) {
		IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
		resultCodeAvp.setInteger(resultCode);
		response.addAVP(resultCodeAvp);
	}
	private void translateDummyResponse(List<ParsedMapping> mappings,Map<String, String> dummyMap, ApplicationResponse diameterResponse,TranslatorParams params) {
		LogicalExpression expression = null;
		DummyResponseValueProvider valueProvider = new DummyResponseValueProvider(dummyMap);
		for(ParsedMapping mapping : mappings){
			LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
			if("*".equalsIgnoreCase(mapping.getStrExpression())){
				LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
				applyDummyCrestelOCSv2ToDiameterMappings(diameterResponse, dummyMap, mapping,params);						
			}else{
				expression = mapping.getExpression();				
				if(expression.evaluate(valueProvider)){
					LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
					applyDummyCrestelOCSv2ToDiameterMappings(diameterResponse, dummyMap, mapping,params);
				}else{
					LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
				}		
			}	
		}

}
	/**
	 * Method will be used when Dummy Response is True.
	 */
	private void applyDummyCrestelOCSv2ToDiameterMappings(ApplicationResponse diameterResponse, final Map<String, String> dummyMap,ParsedMapping mapping, TranslatorParams params) {

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
			LogManager.getLogger().info(MODULE, "Applying mapping expression: "+ avpId + " = " + value);
			
				if(isLiteral(value)){
					strValue = unquote(value);
					try{
						strValue =  getGroupedAVPValue(strValue, dummyMap, defaultValues, valueNodes);
					}catch (Exception e) {
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
					strValue = dummyMap.get(value);
				}
				if(strValue == null || !(strValue.length()>0)){
					strValue = defaultValues.get(avpId);
					if(strValue != null && strValue.length() > 0){
						LogManager.getLogger().debug(MODULE,"Charging Attribute: "+ value+" is not found in Crestel-OCSv2 Response,using default value: " + strValue);
						addOrReplaceAvp(avpId, strValue, valueNodes, diameterResponse.getDiameterAnswer());
					}else{
						LogManager.getLogger().debug(MODULE,"Skipping " + avpId + ", Reason: "+value+ " Not found in Crestel-OCSv2 and default value is not provided.");
					}
				}else{
					addOrReplaceAvp(avpId, strValue, valueNodes, diameterResponse.getDiameterAnswer());
			}
		}		
	}
}
