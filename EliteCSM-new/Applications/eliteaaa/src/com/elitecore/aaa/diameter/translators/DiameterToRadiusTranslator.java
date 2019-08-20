package com.elitecore.aaa.diameter.translators;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.diameter.translators.policy.TranslatorPolicy;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMapping;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMappingInfo;
import com.elitecore.aaa.radius.translators.Mac2TGppKeyword;
import com.elitecore.aaa.radius.translators.RadiusAttributeDecryptedValueProvider;
import com.elitecore.aaa.radius.translators.RadiusDstKeyword;
import com.elitecore.aaa.radius.translators.copypacket.RadiusHeaderFields;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.translator.operations.data.DiameterHeaderFields;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class DiameterToRadiusTranslator extends BaseTranslator {

	private static final String MODULE = "DIA-RAD-TRNSLTR";

	public DiameterToRadiusTranslator(ServerContext serverContext) {
		super(serverContext);
	}

	@Override
	public void init(TranslatorPolicyData policyData)
			throws InitializationFailedException {
		super.init(policyData);
		
		KeywordContext keywordContext = getKeywordContext();
		
		StrOptKeyword strOptKeyword = new StrOptKeyword(TranslatorConstants.STROPT, keywordContext); 
		registerRequestKeyword(strOptKeyword);
		registerResponseKeyword(strOptKeyword);
		
		MathOpKeyword mathOpKeyword = new MathOpKeyword(TranslatorConstants.MATHOP, keywordContext);
		registerRequestKeyword(mathOpKeyword);
		registerResponseKeyword(mathOpKeyword);

		Mac2TGppKeyword mac2tGppKeyword = new Mac2TGppKeyword(TranslatorConstants.MAC2TGPP, keywordContext);
		registerRequestKeyword(mac2tGppKeyword);
		registerResponseKeyword(mac2tGppKeyword);
		
		DBSessionKeyword translationKeyword = new DBSessionKeyword(TranslatorConstants.DBSESSION, keywordContext);
		registerRequestKeyword(translationKeyword);
		registerResponseKeyword(translationKeyword);

		registerResponseKeyword(new DiameterSrcDstKeyword(TranslatorConstants.SOURCE_REQUEST, keywordContext));
		registerResponseKeyword(new RadiusDstKeyword(AAATranslatorConstants.DESTINATION_REQUEST, keywordContext));
		
		registerRequestKeyword(new DiameterSrcDstKeyword(TranslatorConstants.SRCRES, keywordContext));
	}
	
	@Override
	public String getFromId() {
		return AAATranslatorConstants.DIAMETER_TRANSLATOR;
	}

	@Override
	public String getToId() {
		return AAATranslatorConstants.RADIUS_TRANSLATOR;
	}

	@Override
	public void translateRequest(String policyDataId, TranslatorParams params)
			throws TranslationFailedException {

		DiameterRequest fromPacket = (DiameterRequest) params.getParam(TranslatorConstants.FROM_PACKET); 
		if (fromPacket == null) {
			throw new TranslationFailedException("Diameter Packet (From Packet) not found");
		}
		
		RadiusPacket toPacket = (RadiusPacket) params.getParam(TranslatorConstants.TO_PACKET);
		if (toPacket == null) {
			throw new TranslationFailedException("Radius Packet (To Packet) not found");
		}
		
		TranslatorPolicy policy = getPolicy(policyDataId);
		
		if (LogManager.getLogger().isDebugLogLevel()) {			
			LogManager.getLogger().debug(MODULE, "Diameter request packet to translate: " + fromPacket.toString());
		}
		applyRequestTranslationPolicy(policy, toPacket, params, 
				new DiameterAVPValueProvider(fromPacket));
		
		if (LogManager.getLogger().isDebugLogLevel()) {				
			LogManager.getLogger().debug(MODULE, "Translated radius packet: " + toPacket.toString());
		}

	}

	private TranslatorPolicy getPolicy(String policyDataId)
			throws TranslationFailedException {
		TranslatorPolicy policy = policiesMap.get(policyDataId);
		if (policy == null) {
			throw new TranslationFailedException("Invalid Translaltion Policy-Id: " + policyDataId);
		}
		return policy;
	}

	private void applyRequestTranslationPolicy(TranslatorPolicy policy,
			RadiusPacket toPacket, TranslatorParams params, 
			DiameterAVPValueProvider fromPacketValueProvider) {
		
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());

		//Applying Base Policy if configured
		if (basePolicy != null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: " + basePolicy.getName());
			}
			applyRequestTranslationPolicy(basePolicy, toPacket, params, fromPacketValueProvider);
		}	
		ParsedMappingInfo mappingInfo = selectRequestMapping(policy.getParsedRequestMappingInfoMap(), fromPacketValueProvider);

		if (mappingInfo == null) {
			
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "No suitable translation mapping found " +
					"for diameter request in Translation Policy: " + policy.getName());
			}
			return;
		}
		performRequestTranslation(mappingInfo, toPacket, params, fromPacketValueProvider);
	}

	private void performRequestTranslation(
			ParsedMappingInfo mappingInfo,
			RadiusPacket toPacket, 
			TranslatorParams params, 
			DiameterAVPValueProvider fromPacketValueProvider) {

		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Request Translation using mapping: " + mappingInfo.getMappingName());
		}
		
		// Adding param for Selected Policy and Dummy Response
		params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, mappingInfo.getMappingName());
		params.setParam(TranslatorConstants.DUMMY_MAPPING, mappingInfo.isDummyResponse());

		List<ParsedMapping> mappings = mappingInfo.getParsedRequestMappingDetails();

		for (ParsedMapping mapping : mappings) {

			LogicalExpression expression = mapping.getExpression();				

			if (isMappingApplicable(expression, mapping.getStrExpression(), fromPacketValueProvider)) {

				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, mapping.getStrExpression() + " check expression result: TRUE" );
				}
				applyRequestMappings(fromPacketValueProvider, toPacket, 
						mapping.getMappingNodes(), 
						mapping.getDefaultValuesMap(), 
						mapping.getValueNodes(), params);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, mapping.getStrExpression() + " check expression result: FALSE");
				}
			}		
		}

	}

	private ParsedMappingInfo selectRequestMapping(
			Map<String, ParsedMappingInfo> parsedMappingInfoMap,
			DiameterAVPValueProvider fromPacketValueProvider) {
		
		for (ParsedMappingInfo mappingInfo : parsedMappingInfoMap.values()) {
			if (isMappingApplicable(mappingInfo.getInRequestExpression(), 
					mappingInfo.getInRequestStrExpression(), fromPacketValueProvider)){
				return mappingInfo;
			}
		}
		return null;
	}

	private void applyRequestMappings(final DiameterAVPValueProvider fromPacketValueProvider,
			RadiusPacket toPacket, Map<String, String> mappingNodes,
			Map<String, String> attributeIdToDefaultValue,
			Map<String, String> valueNodes, TranslatorParams params) {

		for (Entry<String, String> entry : mappingNodes.entrySet()) {
		
			String destinationExpression = entry.getKey();
			final String sourceExpression = entry.getValue();

			RadiusHeaderFields radiusHeaderField = RadiusHeaderFields.getHeaderField(destinationExpression);
			if (radiusHeaderField != null) {
				try {
					radiusHeaderField.apply(toPacket, unquote(sourceExpression));
				} catch (NumberFormatException e) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().warn(MODULE, "Unable to set " + radiusHeaderField.name + 
							", Reason: " + e.getMessage());
					}
				}
				continue;
			}
			
			if (isMultimodeEnabled(sourceExpression)) {
				
				String attributeId = stripKeywordName(sourceExpression);
				try {
					List<String> attributeValues = fromPacketValueProvider.getStringValues(attributeId);
					addAttributes(toPacket, attributeValues, valueNodes, destinationExpression);
					continue;
				} catch (Exception e) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to fetch " + MULTIMODE_KEYWORD + 
								" attribute-value for: " + attributeId + 
								", Reason: " + e.getMessage());
					}
				} 
			}			
			String sourceValue = null;
			
			if (isLiteral(sourceExpression)) {
				// trimming double quotes ""
				sourceValue = unquote(sourceExpression);
				
			} else if(isKeyword(sourceExpression)) {

				sourceValue = getKeywordValue(sourceExpression, params, true, new com.elitecore.core.commons.data.ValueProvider(){

					@Override
					public String getStringValue(String identifier) {
						try {
							return fromPacketValueProvider.getStringValue(identifier);
						} catch (Exception e) {
							if(LogManager.getLogger().isDebugLogLevel()) {
								LogManager.getLogger().debug(MODULE, "Unable to fetch attribute-value for: " + identifier + 
										" for keyword-expression: " + sourceExpression + 
										", Reason: " + e.getMessage());
							}
							return null;
						}
					}

				});
			} else {
				try {
					sourceValue = fromPacketValueProvider.getStringValue(sourceExpression);
				} catch (Exception e) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to fetch attribute-value for : " + 
								sourceExpression + ", Reason: " + e.getMessage());
					}
				}
			}
			if (Strings.isNullOrBlank(sourceValue)) {
				
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Getting default value for: " + destinationExpression);
				}
				sourceValue = unquote(attributeIdToDefaultValue.get(destinationExpression));
			} else {
				sourceValue = getValue(sourceValue, valueNodes);
			}
			if (Strings.isNullOrBlank(sourceValue)) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add attribute: " + destinationExpression + 
						", Reason: No default value configured");
				}
			} else {
				addAttribute(toPacket, destinationExpression, sourceValue);
			}
		}		

	}

	private void addAttributes(RadiusPacket toPacket,
			List<String> attributeValues, Map<String, String> valueNodes, 
			String destAttributeId) {
		
		for (int i = 0; i < attributeValues.size(); i++) {

			addAttribute(toPacket, destAttributeId, 
					getValue(attributeValues.get(i), valueNodes));
		}

	}

	private void addAttribute(RadiusPacket toPacket, String attributeId, String value) {
		try {
			IRadiusAttribute attribute = Dictionary.getInstance().getKnownAttribute(attributeId);
			if (attribute == null) {
				LogManager.getLogger().warn(MODULE, "Unable to add attribute: " + attributeId + 
						", Reason: attribute not found in dictionary");
				return;
			}
			attribute.setStringValue(value, AAAServerConstants.DIAMETER_TO_RADIUS_SECRET, toPacket.getAuthenticator());
			toPacket.addAttribute(attribute);
			
		} catch (Exception e) {
			LogManager.getLogger().warn(MODULE, "Unable to add attribute: " + attributeId + 
					", Reason: " + e.getMessage());
		}
		
	}

	@Override
	public void translateResponse(String policyDataId, TranslatorParams params)
			throws TranslationFailedException {
	
		TranslatorPolicy policy = getPolicy(policyDataId);

		RadiusPacket fromPacket = (RadiusPacket) params.getParam(AAATranslatorConstants.FROM_PACKET); 
		
		DiameterPacket toPacket = (DiameterPacket) params.getParam(AAATranslatorConstants.TO_PACKET);
		if (toPacket == null) {
			throw new TranslationFailedException("Radius Packet (To Packet) not found");
		}
		
		DiameterPacket originRequest = (DiameterPacket) params.getParam(AAATranslatorConstants.SOURCE_REQUEST);
		if (originRequest == null) {
			throw new TranslationFailedException("Origin Diameter request packet (Source Request) not found");
		}
		
		if (LogManager.getLogger().isDebugLogLevel()) {			
			LogManager.getLogger().debug(MODULE, "Radius response packet to translate: " + fromPacket);
		}
		applyResponseTranslationPolicy(policy, toPacket, params, 
				new DiameterAVPValueProvider(originRequest),
				fromPacket);
		
		if (LogManager.getLogger().isDebugLogLevel()) {				
			LogManager.getLogger().debug(MODULE, "Translated diameter packet: " + toPacket.toString());
		}
	}

	private void applyResponseTranslationPolicy(TranslatorPolicy policy,
			DiameterPacket toPacket, TranslatorParams params,
			DiameterAVPValueProvider originRequestValueProvider,
			RadiusPacket fromPacket) throws TranslationFailedException {
		
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if (basePolicy != null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: " + basePolicy.getName());
			}
			applyResponseTranslationPolicy(basePolicy, toPacket, params, originRequestValueProvider, fromPacket);
		}	
		ParsedMappingInfo mappingInfo = selectResponseMapping(policy.getParsedResponseMappingInfoMap(), originRequestValueProvider, params);

		if (mappingInfo == null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "No suitable translation mapping found " +
					"for radius response in Translation Policy: " + policy.getName());
			}
			return;
		}
		
		ValueProvider sourceValueProvider;
		if(mappingInfo.isDummyResponse()){	
			sourceValueProvider = new DummyResponseValueProvider(policy.getDummyParametersMap());
		} else {
			String sharedSecret = (String)params.getParam(TranslatorConstants.SHARED_SECRET);
			sourceValueProvider = new RadiusAttributeDecryptedValueProvider(fromPacket, sharedSecret);
		}
		
		performResponseTranslation(mappingInfo, toPacket, params, sourceValueProvider);
	}

	private ParsedMappingInfo selectResponseMapping(
			Map<String, ParsedMappingInfo> parsedMappingInfoMap,
			DiameterAVPValueProvider originRequestValueProvider, 
			TranslatorParams params) throws TranslationFailedException {
		
		String mappingName = (String) params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING);

		ParsedMappingInfo mappingInfo;
		if (mappingName == null) {
			mappingInfo = selectRequestMapping(parsedMappingInfoMap, originRequestValueProvider);
			if (mappingInfo == null) {
				throw new TranslationFailedException("No Translation Mapping was selected during request translation");
			}
		} else {
			mappingInfo = parsedMappingInfoMap.get(mappingName);
			if (mappingInfo == null) {
				throw new TranslationFailedException("Invalid Translation Mapping " + mappingName +
						" is selected during request translation");
			}
		}
		return mappingInfo;
	}

	private void performResponseTranslation(ParsedMappingInfo mappingInfo,
			DiameterPacket toPacket, TranslatorParams params,
			ValueProvider sourceValueProvider) {
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Response Translation using mapping: " + mappingInfo.getMappingName());
		}
		
		List<ParsedMapping> mappings = mappingInfo.getParsedResponseMappingDetails();
		
		for (ParsedMapping mapping : mappings) {

			LogicalExpression expression = mapping.getExpression();				

			if (isMappingApplicable(expression, mapping.getStrExpression(), sourceValueProvider)) {

				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, mapping.getStrExpression() + " check expression result: TRUE" );
				}
				applyResponseMappings(sourceValueProvider, toPacket, mapping.getMappingNodes(), mapping.getDefaultValuesMap(), mapping.getValueNodes(), params);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, mapping.getStrExpression() + " check expression result: FALSE");
				}
			}		
		}
		
	}

	private void applyResponseMappings(final ValueProvider sourceValueProvider,
			DiameterPacket toPacket, Map<String, String> mappingNodes,
			Map<String, String> attributeIdToDefaultValue,
			Map<String, String> valueNodes, TranslatorParams params) {
		
		
		for (Entry<String, String> entry : mappingNodes.entrySet()) {
			
			String destinationExpression = entry.getKey();
			final String sourceExpression = entry.getValue();

			DiameterHeaderFields diameterHeaderField = DiameterHeaderFields.getHeaderField(destinationExpression);
			if (diameterHeaderField != null) {
				try {
					diameterHeaderField.apply(toPacket, unquote(sourceExpression));
				} catch (NumberFormatException e) {
					LogManager.getLogger().warn(MODULE, "Unable to set " + diameterHeaderField.name + ", Reason: " + 
							e.getMessage());
				}
				continue;
			}
			
			if (isMultimodeEnabled(sourceExpression)) {
				
				String attributeId = stripKeywordName(sourceExpression);
					
				try {
					List<String> attributeValues = sourceValueProvider.getStringValues(sourceExpression);
					addAVPs(toPacket, attributeValues, valueNodes, destinationExpression);
					continue;
				} catch (Exception e) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to fetch " + MULTIMODE_KEYWORD
								+ " attribute-value for: " + attributeId + ", Reason: " + e.getMessage());
					}
				} 
			
			}			
			
			String sourceValue = null;
			if (isLiteral(sourceExpression)) {
				// trimming double quotes ""
				sourceValue = unquote(sourceExpression);
				
			} else if(isKeyword(sourceExpression)) {

				sourceValue = getKeywordValue(sourceExpression, params, false, new com.elitecore.core.commons.data.ValueProvider(){

					@Override
					public String getStringValue(String identifier) {
						try {
							return sourceValueProvider.getStringValue(identifier);
						} catch (Exception e) {
							if (LogManager.getLogger().isDebugLogLevel()) {
								LogManager.getLogger().debug(MODULE, "Unable to fetch keyword-value for: " + sourceExpression + 
										", Reason: " + e.getMessage());
							}
							return null;
						}
					}

				});
			} else {
				try {
					sourceValue = sourceValueProvider.getStringValue(sourceExpression);
				} catch (Exception e) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to fetch attribute-value for: " + sourceExpression
								+ ", Reason: " + e.getMessage());
					}
				}
			}
			if (Strings.isNullOrBlank(sourceValue)) {
				
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Getting default value for attribute: " + destinationExpression);
				}
				
				sourceValue = unquote(attributeIdToDefaultValue.get(destinationExpression));
			} else {
				sourceValue = getValue(sourceValue, valueNodes);
			}
			if (Strings.isNullOrBlank(sourceValue)) {
				
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add attribute: " + destinationExpression + 
						", Reason: No default value configured");
				}
			} else {
				DiameterUtility.addOrReplaceAvp(destinationExpression, toPacket, sourceValue);
			}
		}		
		
	}

	private void addAVPs(DiameterPacket toPacket, List<String> attributeValues,
			Map<String, String> valueNodes, String destAttributeId) {
		for (int i = 0 ; i < attributeValues.size() ; i++) {
			toPacket.addAvp(
					DiameterUtility.createAvp(destAttributeId, 
					getValue(attributeValues.get(i), valueNodes)));
		}
	}
}
