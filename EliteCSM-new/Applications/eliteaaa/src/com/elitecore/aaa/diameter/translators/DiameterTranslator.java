package com.elitecore.aaa.diameter.translators;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.diameter.translators.policy.TranslatorPolicy;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMapping;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMappingInfo;
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
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class DiameterTranslator extends BaseTranslator {
	
	private static final String MODULE = "DIA-TRNSLTR";
	private IStackContext stackContext;
	
	public DiameterTranslator(ServerContext context,IStackContext stackContext) {
		super(context);
		this.stackContext = stackContext;
	}
	
	private void addOrReplaceAvp(String avpId,String avpValue,Map<String,String>valueNodes,DiameterPacket packet){
		avpValue = getValue(avpValue,valueNodes);
		DiameterUtility.addOrReplaceAvp(avpId,packet,avpValue);
	}
	
	private void addAvp(String avpId, DiameterPacket outPacket, IDiameterAVP avp, Map<String,String>valueNodes){
		if (avp.isGrouped()){
			IDiameterAVP newAvp = DiameterDictionary.getInstance().getAttribute(avpId);
			if (newAvp.isGrouped()){
				if (newAvp!=null){
					newAvp.setStringValue(avp.getStringValue());
					outPacket.addAvp(newAvp);
				}	
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "In multimode if right hand side AVP is grouped then left hand side avp must be grouped.");
			}
		} else {
			String value = avp.getStringValue();
			if (valueNodes.get(value)!= null){
				value = valueNodes.get(value);
			}
			IDiameterAVP newAvp = DiameterUtility.createAvp(avpId, value);
			if (newAvp != null){
				outPacket.addAvp(newAvp);
			}
		}
	}
	

	@Override
	public void init(TranslatorPolicyData policyData)
			throws InitializationFailedException {
		super.init(policyData);
		
		KeywordContext keywordContext = getKeywordContext();
		
		registerKeyword(new DiameterSrcDstKeyword(TranslatorConstants.SOURCE_REQUEST,keywordContext),false);
		registerKeyword(new DiameterSrcDstKeyword(TranslatorConstants.DESTINATION_REQUEST,keywordContext),false);
		
		registerKeyword(new DiameterSrcDstKeyword(TranslatorConstants.SRCRES, keywordContext), true);
		

		registerKeyword(new StrOptKeyword(TranslatorConstants.STROPT,keywordContext), true);
		registerKeyword(new StrOptKeyword(TranslatorConstants.STROPT,keywordContext), false);


		TimeStampKeyword timeStampKeyword = new TimeStampKeyword(TranslatorConstants.TIMESTAMP,keywordContext);
		
		registerKeyword(timeStampKeyword, true);
		registerKeyword(timeStampKeyword, false);
		
		registerKeyword(new PeerSequenceKeyword(TranslatorConstants.SEQPEER,keywordContext, stackContext), true);
		registerKeyword(new SessionSequenceKeyword(TranslatorConstants.SEQSESS,policyData.getName(), keywordContext,DiameterAVPConstants.SESSION_ID, stackContext), true);
		registerKeyword(new ServerSequenceKeyword(TranslatorConstants.SEQSERV,keywordContext, stackContext), true);
		
		registerKeyword(new DiameterTimestampKeyword(TranslatorConstants.TIMESTAMP,keywordContext), true);
		registerKeyword(new DiameterTimestampKeyword(TranslatorConstants.TIMESTAMP,keywordContext), false);

		
		registerKeyword(new MathOpKeyword(TranslatorConstants.MATHOP, keywordContext), true);
		registerKeyword(new MathOpKeyword(TranslatorConstants.MATHOP, keywordContext), false);

		registerKeyword(new Mac2TGppKeyword(TranslatorConstants.MAC2TGPP, keywordContext), true);
		registerKeyword(new Mac2TGppKeyword(TranslatorConstants.MAC2TGPP, keywordContext), false);
		
		registerKeyword(new DBSessionKeyword(TranslatorConstants.DBSESSION, keywordContext), true);
		registerKeyword(new DBSessionKeyword(TranslatorConstants.DBSESSION, keywordContext), false);
	}
	
	private void applyRequestMappings(final DiameterPacket requestPacket,DiameterPacket responsePacket,Map<String,String>mappingNodes,Map<String,String>defaultValues,Map<String,String>valueNodes ,TranslatorParams params){
		/* Applying Mapping Expression. */
		//String [] defaultValues = mapping.getDefaultValues();
		// Mapping nodes <0:1, "abc">
		// OR  			<0:1, 0:1>
		//Map<String,String>mappingNodes = mapping.getMappingNodes();
		//Map<String,String>valueNodes = mapping.getValueNodes();
		
		
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			String avpId = entry.getKey();
			String value = entry.getValue();
			
			String strValue = null;

			if (isMultimodeEnabled(value)){
				value = stripKeywordName(value);
			
				List<IDiameterAVP> avpList = requestPacket.getAVPList(value);
				
				if (avpList != null && avpList.size() > 0){
					for (int i=0 ; i<avpList.size() ; i++){
						addAvp(avpId, responsePacket, avpList.get(i), valueNodes);
					}
				} else {
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE,"Value for key: "+avpId+ " is not found in packet,trying to get default value");
					String defaultValue = defaultValues.get(avpId);					
					if(defaultValue != null && defaultValue.length() > 0){
						IDiameterAVP avp = DiameterUtility.createAvp(value);
						if (avp != null){
							addAvp(avpId, responsePacket, avp, valueNodes);
						} else {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE,"AVP: " + value+ "  could not be created. Possibly Dictionary or memory Error.");
						}

					}else{
						if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE,"Skipping value for key " + avpId + ", " + "Reason: Default value not found");
					}
				}
				
			} else {

				if(isLiteral(value)){ // remove '"' from the value and set it to the give attribute 
					strValue = unquote(value);
				}else if (isKeyword(value)) {
					strValue = getKeywordValue(value, params, true, new com.elitecore.core.commons.data.ValueProvider(){

						@Override
						public String getStringValue(String identifier) {
							if(identifier!=null){
								return requestPacket.getAVPValue(identifier, true);
							}else {
								return null;
							}
						}
						
					});
				}else{ // value is Attribute
					strValue = requestPacket.getAVPValue(value, true);
				}
				if(strValue==null){
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE,"Value for key: "+avpId+ "is not found in packet ,trying to get default value");
					String defaultValue = defaultValues.get(avpId);					
					if(defaultValue != null && defaultValue.length() > 0){
						addOrReplaceAvp(avpId, defaultValue, valueNodes, responsePacket);
					}else{
						if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE,"Skipping value for key " + avpId + ", " + "Reason: Default value not found");
					}
				}else {
					addOrReplaceAvp(avpId, strValue, valueNodes, responsePacket);
				}
			}
		}		
	
	}
	
	
	private void applyResponseMappings(final DiameterPacket requestPacket,DiameterPacket responsePacket,Map<String,String>mappingNodes,Map<String,String>defaultValues,Map<String,String>valueNodes,TranslatorParams params ){
		/* Applying Mapping Expression. */
		//String [] defaultValues = mapping.getDefaultValues();
		// Mapping nodes <0:1, "abc">
		// OR  			<0:1, 0:1>
		//Map<String,String>mappingNodes = mapping.getMappingNodes();
		//Map<String,String>valueNodes = mapping.getValueNodes();
		for(Entry<String, String>entry: mappingNodes.entrySet()){
			String avpId = entry.getKey();
			String value = entry.getValue();
			
			String strValue = null ;
			
			if (isMultimodeEnabled(value)){
				value = stripKeywordName(value);
			
				List<IDiameterAVP> avpList = requestPacket.getAVPList(value);
				
				if (avpList != null && avpList.size() > 0){
					for (int i=0 ; i<avpList.size() ; i++){
						addAvp(avpId, responsePacket, avpList.get(i), valueNodes);
					}
				} else {
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE,"Value for key: "+avpId+ " is not found in packet,trying to get default value");
					String defaultValue = defaultValues.get(avpId);					
					if(defaultValue != null && defaultValue.length() > 0){
						IDiameterAVP avp = DiameterUtility.createAvp(avpId);
						if (avp != null){
							avp.setStringValue(defaultValue);
							addAvp(avpId, responsePacket, avp, valueNodes);
						} else {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE,"AVP: " + value+ "  could not be created. Possibly Dictionary or memory Error.");
						}

					}else{
						if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE,"Skipping  value for key " + avpId + ", " + "Reason: Default value not found");
					}
				}
				
			} else {

				if(isLiteral(value)){ // remove '"' from the value and set it to the give attribute 
					strValue = unquote(value);
				}else if (isKeyword(value)) {
					strValue = getKeywordValue(value, params, false,new com.elitecore.core.commons.data.ValueProvider(){

						@Override
						public String getStringValue(String identifier) {
							if(identifier!=null){
								return requestPacket.getAVPValue(identifier, true);
							}else {
								return null;
							}
						}
						
					});
					
				}else{ // value is Attribute
					strValue = requestPacket.getAVPValue(value, true);
				}	
				if(strValue==null){
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE,"Value for key: "+avpId+ " is not found in packet,trying to get default value");
					String defaultValue = defaultValues.get(avpId);					
					if(defaultValue != null && defaultValue.length() > 0){
						addOrReplaceAvp(avpId, defaultValue, valueNodes, responsePacket);
					}else{
						if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE,"Skipping  value for key " + avpId + ", " + "Reason: Default value not found");
					}
				}else {
					addOrReplaceAvp(avpId, strValue, valueNodes, responsePacket);
				}
			}
		}		
	
	}	
	
	private void translateDiameterRequest(Map<String,ParsedMappingInfo> infoMap,DiameterPacket requestPacket,DiameterPacket responsePacket,TranslatorParams params,ValueProvider valueProvider){
		LogicalExpression expression = null;
		for(ParsedMappingInfo mappingInfo : infoMap.values()){
			if(this.isMappingApplicable(mappingInfo.getInRequestExpression(), mappingInfo.getInRequestStrExpression(), valueProvider)){
				LogManager.getLogger().info(MODULE, "Request Translation using mapping: "+mappingInfo.getMappingName());
				List<ParsedMapping> mappings = mappingInfo.getParsedRequestMappingDetails(); 
				params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, mappingInfo.getMappingName());
				// Added For Dummy Response Parameters
				boolean isDummyResponse = mappingInfo.isDummyResponse();
				params.setParam(TranslatorConstants.DUMMY_MAPPING, isDummyResponse);

				for(ParsedMapping mapping : mappings){
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, mapping.getStrExpression() + " Check expression result: TRUE" );
						applyRequestMappings(requestPacket, responsePacket,mapping.getMappingNodes(),mapping.getDefaultValuesMap(),mapping.getValueNodes(),params);
					}else{
						expression = mapping.getExpression();				
						if(expression.evaluate(valueProvider)){
							if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, mapping.getStrExpression() + " Check expression result: TRUE" );
							applyRequestMappings(requestPacket, responsePacket, mapping.getMappingNodes(),mapping.getDefaultValuesMap(),mapping.getValueNodes(),params);
						}else{
							if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
								LogManager.getLogger().info(MODULE, mapping.getStrExpression() + " Check expression result: FALSE");
						}		
					}	
				}
				break;
			}
		}
	}
	
	private void translateDiameterAnswer(Map<String,ParsedMappingInfo> infoMap,DiameterPacket requestPacket,DiameterPacket responsePacket,TranslatorParams params,ValueProvider requestValueProviderImpl) throws TranslationFailedException{
		LogicalExpression expression = null;
		ValueProvider valueProvider = new DiameterAVPValueProvider(requestPacket);

	ParsedMappingInfo mappingInfo = null;
	String mappingName = (String)params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING);

	if(mappingName != null){
		mappingInfo = infoMap.get(mappingName);
		if(mappingInfo == null){
			throw new TranslationFailedException("Invalid Translation Mapping "+mappingName+" is selected during request translation");

		}
	} else {
		for(ParsedMappingInfo parsedMappingInfo : infoMap.values()){
			if(this.isMappingApplicable(parsedMappingInfo.getInRequestExpression(), parsedMappingInfo.getInRequestStrExpression(),requestValueProviderImpl)){
				mappingInfo = parsedMappingInfo;
				break;
			}
		}
	}
	
	if (mappingInfo == null) {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "No translation mapping satisfied");
		return;
	}
		LogManager.getLogger().info(MODULE, "Response Translation using mapping: "+mappingInfo.getMappingName());
				List<ParsedMapping> mappings = mappingInfo.getParsedResponseMappingDetails();
				for(ParsedMapping mapping : mappings){
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, mapping.getStrExpression() + " Check expression result: TRUE" );
						applyResponseMappings(requestPacket, responsePacket,mapping.getMappingNodes(),mapping.getDefaultValuesMap(),mapping.getValueNodes(),params);		
					}else{
						expression = mapping.getExpression();				
						if(expression.evaluate(valueProvider)){
							if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, mapping.getStrExpression() + " Check expression result: TRUE" );
							applyResponseMappings(requestPacket, responsePacket, mapping.getMappingNodes(),mapping.getDefaultValuesMap(),mapping.getValueNodes(),params);
						}else{
							if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
								LogManager.getLogger().info(MODULE, mapping.getStrExpression() + " Check expression result: FALSE");
						}		
					}	
				}
			}

		@Override
	public String getFromId() {
		return TranslatorConstants.DIAMETER_TRANSLATOR;
	}

	@Override
	public String getToId() {
		return TranslatorConstants.DIAMETER_TRANSLATOR;
	}

	@Override
	public void translateRequest(String policyDataId, TranslatorParams params) throws TranslationFailedException{
		
		DiameterPacket requestPacket = (DiameterPacket)params.getParam(TranslatorConstants.FROM_PACKET); 
		DiameterPacket responsePacket = (DiameterPacket)params.getParam(TranslatorConstants.TO_PACKET);
		
		if(requestPacket.isRequest() && responsePacket.isRequest()){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))			
				LogManager.getLogger().debug(MODULE, "Diameter request packet to translate:" + requestPacket.toString());
			TranslatorPolicy policy = policiesMap.get(policyDataId);
			ValueProvider requestValueProvider = new DiameterAVPValueProvider(requestPacket);
			if(policy != null){
				try{
					applyRequestMapping(policy, requestPacket,responsePacket, params,requestValueProvider);	
				}catch (Exception e) {
					throw new TranslationFailedException(e);
				}
					
			}else{
				LogManager.getLogger().error(MODULE, "Given Policy not found. Policy Id: " + policyDataId );
				throw new TranslationFailedException("Given Policy not found. Policy Id: " + policyDataId );
			}
		}else{
			throw new TranslationFailedException("Given Objects are not Requests");
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){				
			LogManager.getLogger().debug(MODULE, "Diameter Translated packet:" + responsePacket.toString());
		}
	}

	private void applyRequestMapping(TranslatorPolicy policy,DiameterPacket requestPacket, DiameterPacket responsePacket,TranslatorParams params,ValueProvider valueProvider) {
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyRequestMapping(basePolicy,requestPacket,responsePacket,params,valueProvider);
		}	
		translateDiameterRequest(policy.getParsedRequestMappingInfoMap(),requestPacket, responsePacket,params,valueProvider);
	}

	@Override
	public void translateResponse(String policyDataId, TranslatorParams params) throws TranslationFailedException {
		
		DiameterPacket recvAnswerPacket = (DiameterPacket)params.getParam(TranslatorConstants.FROM_PACKET); 
		DiameterPacket responsePacket = (DiameterPacket)params.getParam(TranslatorConstants.TO_PACKET);
		DiameterPacket origRequestPacket = (DiameterPacket)params.getParam(TranslatorConstants.SOURCE_REQUEST);
		if(	(recvAnswerPacket.isResponse() && responsePacket.isResponse())){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){				
				LogManager.getLogger().debug(MODULE, "Diameter response packet to translate:" + recvAnswerPacket.toString());
			}
			TranslatorPolicy policy = policiesMap.get(policyDataId);
			if(policy != null){
				
				params.setParam(TranslatorConstants.STACK_CONTEXT, stackContext);
				DiameterAVPValueProvider valueProviderImpl =  new DiameterAVPValueProvider(origRequestPacket);
				applyResponseMapping(policy,recvAnswerPacket,responsePacket,params,valueProviderImpl);
			}else{
				LogManager.getLogger().error(MODULE, "Given Policy not found. Policy Id: " + policyDataId );
				throw new TranslationFailedException("Given Policy not found. Policy Id: " + policyDataId );
			}
		}else{
			throw new TranslationFailedException("Given Objects are not Responses");
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){				
			LogManager.getLogger().debug(MODULE, "Diameter Translated packet:" + responsePacket.toString());
		}
	}
		
	private void applyResponseMapping(TranslatorPolicy policy,DiameterPacket requestPacket, DiameterPacket responsePacket,TranslatorParams params, DiameterAVPValueProvider valueProviderImpl) throws TranslationFailedException{

		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyResponseMapping(basePolicy,requestPacket, responsePacket, params, valueProviderImpl);
		}	
		translateDiameterAnswer(policy.getParsedResponseMappingInfoMap(), requestPacket, responsePacket,params,valueProviderImpl);
	}

}
