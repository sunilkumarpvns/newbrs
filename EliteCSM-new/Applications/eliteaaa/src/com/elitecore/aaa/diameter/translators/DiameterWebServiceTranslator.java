package com.elitecore.aaa.diameter.translators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.diameter.translators.policy.TranslatorPolicy;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMapping;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMappingInfo;
import com.elitecore.aaa.diameter.translators.policy.impl.TranslatorPolicyImpl.DiameterParsedMappingInfoImpl;
import com.elitecore.aaa.radius.translators.Mac2TGppKeyword;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class DiameterWebServiceTranslator extends BaseTranslator {
	
	private static final String MODULE = "WEBSERVICE-TRNSLTR";
	private IStackContext stackContext;
	public DiameterWebServiceTranslator(ServerContext context,IStackContext stackContext) {
		super(context);
		this.stackContext = stackContext;
	}

	@Override
	public String getFromId() {
		return TranslatorConstants.WEBSERVICE_TRANSLATOR;
	}

	@Override
	public String getToId() {
		return TranslatorConstants.DIAMETER_TRANSLATOR;
	}
	
	@Override
	public void init(TranslatorPolicyData policyData)
			throws InitializationFailedException {
		super.init(policyData);
		

		KeywordContext keywordContext = getKeywordContext();
		registerKeyword(new StrOptKeyword(TranslatorConstants.STROPT,keywordContext), true);

		StrOptKeyword strOptKeyword = new StrOptKeyword(TranslatorConstants.STROPT,keywordContext); 
		registerKeyword(strOptKeyword, true);

		
		registerKeyword(new DiameterTimestampKeyword(TranslatorConstants.TIMESTAMP,keywordContext), true);
		registerKeyword(new TimeStampKeyword(TranslatorConstants.TIMESTAMP,keywordContext), false);
		
		
		registerKeyword(new PeerSequenceKeyword(TranslatorConstants.SEQPEER,keywordContext, stackContext), true);
		registerKeyword(new SessionSequenceKeyword(TranslatorConstants.SEQSESS,policyData.getName(), keywordContext,null, stackContext), true);
		registerKeyword(new ServerSequenceKeyword(TranslatorConstants.SEQSERV,keywordContext, stackContext), true);
		

		MathOpKeyword mathOpKeyword = new MathOpKeyword(TranslatorConstants.MATHOP, keywordContext);
		registerKeyword(mathOpKeyword, true);
		registerKeyword(mathOpKeyword, false);

		registerKeyword(new Mac2TGppKeyword(TranslatorConstants.MAC2TGPP, keywordContext), true);
		registerKeyword(new Mac2TGppKeyword(TranslatorConstants.MAC2TGPP, keywordContext), false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void translateRequest(String policyDataId, TranslatorParams params)
			throws TranslationFailedException {
		
		Map<String, String> requestAttrMap = (Map<String,String>)params.getParam(TranslatorConstants.FROM_PACKET);
		DiameterPacket request = (DiameterPacket)params.getParam(TranslatorConstants.TO_PACKET);
	
		if(requestAttrMap != null && request != null &&
				request.isRequest() ){
				
				params.setParam(TranslatorConstants.STACK_CONTEXT, stackContext);
				ValueProvider requestValueProvider = new ValueProviderImpl(requestAttrMap);
				applyRequestMapping(policiesMap.get(policyDataId), requestAttrMap, request,params,requestValueProvider);
				LogManager.getLogger().info(MODULE, "Translation from WebService to Diameter Completed");
								
			}else{
				throw new TranslationFailedException("Invalid Input Parameters");
			}
		
	}

	private void applyRequestMapping(TranslatorPolicy policy,Map<String, String> requestAttrMap, DiameterPacket request,TranslatorParams params,ValueProvider requestValueProvider) {
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyRequestMapping(basePolicy,requestAttrMap,request,params,requestValueProvider);
		}	
		handleWebServiceToDiameterTranslation(policy.getParsedRequestMappingInfoMap(), requestAttrMap, request, params,requestValueProvider);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void translateResponse(String policyDataId, TranslatorParams params)
			throws TranslationFailedException {
		DiameterAnswer diameterAnswer = (DiameterAnswer)params.getParam(TranslatorConstants.FROM_PACKET);
		Map<String, String> responseAttrMap = (Map<String, String>)params.getParam(TranslatorConstants.TO_PACKET);
		Map<String, String>	requestAttrMap = (Map<String, String>)params.getParam(TranslatorConstants.SOURCE_REQUEST);
		
		if(diameterAnswer!=null){
			LogManager.getLogger().info(MODULE, "Translation from Diameter to WebService started");
			params.setParam(TranslatorConstants.STACK_CONTEXT, stackContext);
			ValueProviderImpl requestValueProvider = new ValueProviderImpl(requestAttrMap);
			applyResponseMapping(policiesMap.get(policyDataId),diameterAnswer, responseAttrMap,requestValueProvider);
			LogManager.getLogger().info(MODULE, "Translation from Diameter to WebService completed");
			
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "IN_MESSAGE: Diameter Answer is NULL");
			
			throw new TranslationFailedException("Source Diameter Answer (From Packet) is null");
		}
	}
	
	private void applyResponseMapping(TranslatorPolicy policy,DiameterAnswer diameterAnswer, Map<String, String> responseAttrMap,ValueProviderImpl requestValueProvider) {
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyResponseMapping(basePolicy,diameterAnswer, responseAttrMap,requestValueProvider);
		}	
		handleDiameterToWebServiceTranslation(policy.getParsedResponseMappingInfoMap(), diameterAnswer, responseAttrMap,requestValueProvider);
	}

	private void handleDiameterToWebServiceTranslation(Map<String,ParsedMappingInfo> infoMap, 
			DiameterAnswer diameterAnswer,Map<String, String> responseAttrMap,ValueProviderImpl requestValueProvider) {
		
		DiameterAVPValueProvider valueProvider = new DiameterAVPValueProvider(diameterAnswer);
		LogicalExpression expression = null;
		List<ParsedMapping> mappings;
		
		for(ParsedMappingInfo mappingInfo : infoMap.values()){			
			LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + mappingInfo.getInRequestStrExpression()+ " outMessageType: " + mappingInfo.getOutRequestType());
			mappings = mappingInfo.getParsedResponseMappingDetails();			
			if(this.isMappingApplicable(mappingInfo.getInRequestExpression(), mappingInfo.getInRequestStrExpression(), requestValueProvider)){
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyDiameterToWebServiceMappings(responseAttrMap, valueProvider, mapping.getMappingNodes(), mapping.getDefaultValuesMap(), mapping.getValueNodes());						
					}else{
						expression = mapping.getExpression();				
						if(expression.evaluate(valueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyDiameterToWebServiceMappings(responseAttrMap, valueProvider, mapping.getMappingNodes(), mapping.getDefaultValuesMap(), mapping.getValueNodes());
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
				break;
			}
		}
		
	}
	
	private void applyDiameterToWebServiceMappings(Map<String, String> responseAttrMap,DiameterAVPValueProvider valueProvider, Map<String, String> mappingNodes,Map<String, String> defaultValues, Map<String, String> valueNodes) {
		String avpId ;
		String value ;
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			avpId = entry.getKey();
			value = entry.getValue();
			
			if(isLiteral(value)){ // remove '"' from the value and set it to the give attribute 
				value = unquote(value);
				value = getValue(value,valueNodes);
				responseAttrMap.put(avpId, value);
				
			}else{ // value is Attribute
				try {
					value = valueProvider.getStringValue(value);
				} catch (Exception e) {
					LogManager.getLogger().debug(MODULE, "Unable to get value for: " + value 
							+ ", Reason: " + e.getMessage());
				}
				if(value != null){
					value = getValue(value,valueNodes);
					responseAttrMap.put(avpId, value);
													
				}else{ //Request doesn't contains give avp so setting default value
					String avpValue = defaultValues.get(avpId);
					if(avpValue!= null && avpValue.length() > 0){
						avpValue = getValue(avpValue, valueNodes);						
						responseAttrMap.put(avpId, avpValue);
						LogManager.getLogger().debug(MODULE,"Key: "+ value+" is not found in Packet,using default value: " + avpValue);
					}else{
						LogManager.getLogger().debug(MODULE,"Skipping " + avpId + ", " + "Reason: Not found in Packet and default value is not provided.");
					}
					
																					
				}
			}		
		}		
	}



	private class ValueProviderImpl implements ValueProvider{
		Map<String, String> requestAttrMap ;
		private ValueProviderImpl(Map<String, String> requestAttrMap){
			this.requestAttrMap = requestAttrMap;
		}

		@Override
		public long getLongValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
			String value = requestAttrMap.get(identifier);
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
			String value = requestAttrMap.get(identifier);
			if(value == null)
				return null;
			return value;
		}

		@Override
		public List<String> getStringValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
			String value = requestAttrMap.get(identifier);
			if(value != null){
				List<String> stringValues=new ArrayList<String>();
				stringValues.add(value);
				return stringValues;
			}
			else
				throw new MissingIdentifierException("Object not found: "+identifier);
		}
		
		@Override
		public List<Long> getLongValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
			String value = requestAttrMap.get(identifier);
			if(value != null){
				try{
					List<Long> longValues=new ArrayList<Long>();
					longValues.add(Long.parseLong(value));
					return longValues;
				}catch(NumberFormatException e){
					throw new InvalidTypeCastException(e.getMessage());
				}
			}
			else
				throw new MissingIdentifierException("Object not found: "+identifier);
		}

		@Override
		public Object getValue(String key) {
			return null;
		}
		
		
		
	}
	
	
	private void handleWebServiceToDiameterTranslation(
			Map<String,ParsedMappingInfo> infoMap, Map<String, String> requestAttrMap,DiameterPacket request ,TranslatorParams params,ValueProvider requestValueProvider) {
		LogicalExpression expression = null;
		List<ParsedMapping> mappings;
		for(ParsedMappingInfo mappingInfo : infoMap.values()){
			if(isMappingApplicable(mappingInfo.getInRequestExpression(),mappingInfo.getInRequestStrExpression(),requestValueProvider)){
				setCommandCodeAndAppId(request,(DiameterParsedMappingInfoImpl)mappingInfo);
				LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + mappingInfo.getInRequestStrExpression() + " outMessageType: " + mappingInfo.getOutRequestType());
				mappings = mappingInfo.getParsedRequestMappingDetails();				
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyWebServiceToDiameterMappings(requestAttrMap, request, mapping,params);		
					}else{
						expression = mapping.getExpression();				
						if(expression.evaluate(requestValueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyWebServiceToDiameterMappings(requestAttrMap, request, mapping,params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
				break;
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

	private void applyWebServiceToDiameterMappings(final Map<String, String> requestAttrMap,DiameterPacket diameterRequest, ParsedMapping mapping,TranslatorParams params){
		
		/* Applying Mapping Expression. */
		Map<String, String>defaultValues = mapping.getDefaultValuesMap();
		// Mapping nodes <0:1, "abc">
		// OR  			<0:1, 0:1>
		Map<String,String>mappingNodes = mapping.getMappingNodes();
		Map<String,String>valueNodes = mapping.getValueNodes();
		String avpId ;
		String value ;
		String strValue ;
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			avpId = entry.getKey();
			value = entry.getValue();
			strValue = null;
			
			if(isLiteral(value)){ // remove '"' from the value and set it to the give attribute 
				value = unquote(value);
			}else if (isKeyword(value)) {
				strValue = getKeywordValue(value, params, true,new com.elitecore.core.commons.data.ValueProvider(){
					
					@Override
					public String getStringValue(String identifier) {
						if(identifier!=null){
							return requestAttrMap.get(identifier);
						}else {
							return null;
						}
					}
					
				});
			}else{  // Value is WebService key
				strValue = requestAttrMap.get(value);
			}	
			if(strValue==null){
				strValue = defaultValues.get(avpId);
				if(strValue!= null && strValue.length() > 0){
					addOrReplaceAvp(avpId, strValue, valueNodes, diameterRequest);
					LogManager.getLogger().debug(MODULE,"Key: "+ value+" is not found in Packet,using default value: " + strValue);
				}else{
					LogManager.getLogger().debug(MODULE,"Skipping " + avpId + ", " + "Reason: Not found in Packet and default value is not provided.");
				}
			}else {
				addOrReplaceAvp(avpId, strValue, valueNodes, diameterRequest);
			}
		}		
	
	}
	private void addOrReplaceAvp(String avpId,String avpValue,Map<String,String>valueNodes,DiameterPacket packet){
		avpValue = getValue(avpValue,valueNodes);
		DiameterUtility.addOrReplaceAvp(avpId,packet,avpValue);
	}

}
