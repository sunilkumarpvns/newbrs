package com.elitecore.aaa.rm.translator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.diameter.translators.BaseTranslator;
import com.elitecore.aaa.diameter.translators.policy.TranslatorPolicy;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMapping;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMappingInfo;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class RadiusToMapTranslator extends BaseTranslator{

	private static final String MODULE = "RADIUS-TRANSLATOR";
	public RadiusToMapTranslator(ServerContext context) {
		super(context);
	}

	@Override
	public String getFromId() {
		return AAATranslatorConstants.RADIUS_TRANSLATOR;
	}

	@Override
	public String getToId() {
		return AAATranslatorConstants.MAP_TRANSLATOR;
	}
	
	private void applyRadiusToMapMappings(final RadServiceRequest requestPacket,Map<String,String>mapResponse, ParsedMapping mapping,TranslatorParams params){
		
		/* Applying Mapping Expression. */
		Map<String,String>defaultValues = mapping.getDefaultValuesMap();
		// Mapping nodes <0:1, "abc">
		// OR  			<0:1, 0:1>
		Map<String,String>mappingNodes = mapping.getMappingNodes();
		Map<String,String>valueNodes = mapping.getValueNodes();	
		String avpId ;
		String value ;
		String strValue;
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			avpId = entry.getKey();
			value = entry.getValue();
			strValue = null;
			
			if(isLiteral(value)){ // remove '"' from the value and set it to the give attribute 
				strValue = unquote(value);
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
			}else{ // value is Attribute
				IRadiusAttribute  avp = requestPacket.getRadiusAttribute(value);
				if(avp != null){
					strValue = avp.getStringValue();
				}
			}
			if(strValue==null){
				strValue = defaultValues.get(avpId);
				if(strValue != null & strValue.length() > 0){
					strValue = getValue(strValue, valueNodes);
					mapResponse.put(avpId, strValue);
				}else{
					LogManager.getLogger().debug(MODULE,"Key: "+ value+" is not found in response object: ");
					LogManager.getLogger().debug(MODULE,"DefaultValue is not provided for: " + avpId);
				}
			}else {
				strValue = defaultValues.get(avpId);
				mapResponse.put(avpId, strValue);
			}
			
		}		
	
	}
	
	private void handleRadiusToMapTranslation(Map<String,ParsedMappingInfo> infoMap,RadServiceRequest request, Map<String,String>mapResponse,TranslatorParams params){
		RequestValueProviderImpl valueProvider = new RequestValueProviderImpl(request);
		LogicalExpression expression = null;
		List<ParsedMapping> mappings;
		for(ParsedMappingInfo mappingInfo : infoMap.values()){
			if(isMappingApplicable(mappingInfo.getInRequestExpression(),mappingInfo.getInRequestStrExpression(),valueProvider)){
				LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + mappingInfo.getInRequestStrExpression() + " outMessageType: " + mappingInfo.getOutRequestType());
				mappings = mappingInfo.getParsedRequestMappingDetails();				
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyRadiusToMapMappings(request, mapResponse, mapping,params);		
					}else{
						expression = mapping.getExpression();				
						if((expression).evaluate(valueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyRadiusToMapMappings(request, mapResponse, mapping,params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
				break;
			}
			
		}
	}
	
		@SuppressWarnings("unchecked")
	@Override
	public void translateRequest(String policyDataId, TranslatorParams params)
			throws TranslationFailedException {
		RadServiceRequest request = (RadServiceRequest)params.getParam(AAATranslatorConstants.FROM_PACKET);
		Map<String, String> mapResponse = (Map<String,String>)params.getParam(AAATranslatorConstants.TO_PACKET);
		TranslatorPolicy policy = policiesMap.get(policyDataId);
		if(mapResponse == null){
			throw new TranslationFailedException("Recieved Map is null");
		}
		if(request != null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Recieved packet:" + request.toString());
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Translation from Radius to Map started");
			}				
			
			handleRadiusToMapTranslation(policy.getParsedRequestMappingInfoMap(), request,mapResponse,params);			
			
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Translation from Radius to Map Completed");
				LogManager.getLogger().info(MODULE, "Translated Map:" + mapResponse.toString());
			}
		}else{
			throw new TranslationFailedException("Given Radius Request is null");
		}
		

	}

	@Override
	public void translateResponse(String policyDataId, TranslatorParams params)
			throws TranslationFailedException {
	}
	
	private class RequestValueProviderImpl implements ValueProvider{
		private RadServiceRequest request;
		
		public RequestValueProviderImpl(RadServiceRequest request) {
			this.request = request;
		}
		
		@Override
		public String getStringValue(String identifier)throws InvalidTypeCastException,MissingIdentifierException {
			
			IRadiusAttribute attribute = request.getRadiusAttribute(identifier);
			if(attribute != null)
				return attribute.getStringValue();
			else
				throw new MissingIdentifierException("Requested value not found in the request: "+identifier);
		}

		@Override
		public long getLongValue(String identifier)throws InvalidTypeCastException,MissingIdentifierException {
			IRadiusAttribute attribute = request.getRadiusAttribute(identifier);
			if(attribute != null)
				return attribute.getLongValue();
			else
				throw new MissingIdentifierException("Requested value not found in the request: "+identifier);
		}
		
		@Override
		public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			List<String> stringValues= null;
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

}
