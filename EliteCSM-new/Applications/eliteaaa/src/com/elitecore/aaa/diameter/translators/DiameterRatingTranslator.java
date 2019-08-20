package com.elitecore.aaa.diameter.translators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
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
import com.elitecore.ratingapi.util.IRequestParameters;
import com.elitecore.ratingapi.util.IResponseObject;

public class DiameterRatingTranslator extends BaseTranslator {
	
	private static final String MODULE = "RATING-TRANSLATOR";
	private IStackContext stackContext;

	public DiameterRatingTranslator(ServerContext context,IStackContext stackContext) {
		super(context);
		this.stackContext = stackContext;
	}

	@Override
	public String getFromId() {
		return TranslatorConstants.DIAMETER_TRANSLATOR;
	}
	
	@Override
	public void init(TranslatorPolicyData policyData)
			throws InitializationFailedException {
		super.init(policyData);
		
		KeywordContext keywordContext = getKeywordContext();
		registerKeyword(new DiameterSrcDstKeyword(TranslatorConstants.SOURCE_REQUEST,keywordContext),false);
		registerKeyword(new RatingSrcDstKeyword(TranslatorConstants.DESTINATION_REQUEST,keywordContext),false);
		


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
	public String getToId() {
		return TranslatorConstants.RATING_TRANSLATOR;
	}
	/* this method is introduced only for IOT purpose */
	private void setHardCodedRatingKeys(DiameterPacket requestPacket,IRequestParameters requestParams){		
		List< IDiameterAVP> msccAvpList = requestPacket.getAVPList(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
		
		String ratingGroup = "";
		String ccOutputOctets = "";
		String ccInputOctets = "";
		String ccTime = "";
		
		if(msccAvpList != null){
			for(int i=0;i<msccAvpList.size();i++){
				if(msccAvpList != null && msccAvpList.size() > 0){
					IDiameterAVP ratingAvp = ((AvpGrouped)msccAvpList.get(i)).getSubAttribute(DiameterAVPConstants.RATING_GROUP);
					if(ratingAvp != null){
						ratingGroup = ratingGroup + ratingAvp.getStringValue()+ ";";
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
							LogManager.getLogger().info(MODULE, "Rating Group Avp not found in MSCC");
						}
					}
					
					AvpGrouped usedServiceUnitAvp = (AvpGrouped) ((AvpGrouped)msccAvpList.get(i)).getSubAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
					
					if(usedServiceUnitAvp != null){
						// CC OUTPUT OCTETS
						IDiameterAVP ccOutputOctetsAvp = usedServiceUnitAvp.getSubAttribute(DiameterAVPConstants.CC_OUTPUT_OCTETS);
						if(ccOutputOctetsAvp != null){
							ccOutputOctets = ccOutputOctets + ccOutputOctetsAvp.getStringValue() + ";";
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
								LogManager.getLogger().info(MODULE, "CC Output Octets Avp not found in MSCC");
							}
						}
						// CC INPUT OCTETS
						IDiameterAVP ccInputOctetsAvp = usedServiceUnitAvp.getSubAttribute(DiameterAVPConstants.CC_INPUT_OCTETS);
						if(ccInputOctetsAvp != null){
							ccInputOctets = ccInputOctets + ccInputOctetsAvp.getStringValue() + ";";
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
								LogManager.getLogger().info(MODULE, "CC Input Octets Avp not found in MSCC");
							}
						}
						
						// CC TIME OCTETS
						IDiameterAVP ccTimeAvp = usedServiceUnitAvp.getSubAttribute(DiameterAVPConstants.CC_TIME);
						if(ccTimeAvp != null){
							ccTime = ccTime + ccTimeAvp.getStringValue() + ";";
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
								LogManager.getLogger().info(MODULE, "CC Time Avp not found in MSCC");
							}
						}
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
							LogManager.getLogger().info(MODULE, "Used Service Unit Avp not found in MSCC");
						}
					}					
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "None MSCC Avp found in request packet");
					}	
				}
			}
		}		
		
		if(ratingGroup != null && ratingGroup.length() > 0){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Adding Rating key URL with value: " + ratingGroup);
			}
			requestParams.put("URL", ratingGroup);
		}
		if(ccInputOctets != null && ccInputOctets.length() > 0){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Adding Rating key SESSION-UPLOAD-DATA-TRANSFER with value: " + ccInputOctets);
			}
			requestParams.put("SESSION-UPLOAD-DATA-TRANSFER",ccInputOctets);
		}
		if(ccOutputOctets != null && ccOutputOctets.length() > 0){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Adding Rating key SESSION-DOWNLOAD-DATA-TRANSFER with value: " + ccOutputOctets);
			}
			requestParams.put("SESSION-DOWNLOAD-DATA-TRANSFER",ccOutputOctets);
		}
		if(ccTime != null && ccTime.length() > 0){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Adding Rating key SESSION-TIME with value: " + ccTime);
			}
			requestParams.put("SESSION-TIME", ccTime);
		}
	}	                                   
	

	@Override
	public void translateRequest(String policyDataId, TranslatorParams params) throws TranslationFailedException{
		
		DiameterPacket requestPacket = (DiameterPacket)params.getParam(TranslatorConstants.FROM_PACKET);
		TranslatorPolicy policy = policiesMap.get(policyDataId);
		IRequestParameters requestParams = (IRequestParameters)params.getParam(TranslatorConstants.TO_PACKET);
		if(requestParams == null){
			throw new TranslationFailedException("Rating Request is null");
		}
		if(requestPacket != null && requestPacket.isRequest() ){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Recieved packet:" + requestPacket.toString());
			}
			
			IDiameterAVP ccRequestTypeAvp = requestPacket.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
			if(ccRequestTypeAvp != null){
				
				params.setParam(TranslatorConstants.STACK_CONTEXT, stackContext);
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Translation from Diameter to Rating started");
				}				
				ValueProvider valueProvider = new DiameterAVPValueProvider(requestPacket);
				applyRequestMapping(policy, requestPacket, requestParams, params,valueProvider);
				setHardCodedRatingKeys(requestPacket, requestParams);
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Translation from Diameter to Rating Completed");
					LogManager.getLogger().info(MODULE, "Translated Rating packet:" + requestParams.toString());
				}				
				
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "CC-Request-Type Attribute not present in request");
				throw new TranslationFailedException("CC-Request-Type Attribute not present in request");
			}
		}else{
			throw new TranslationFailedException("Given Objects are not Requests");
		}
		
	}	
	private void applyRequestMapping(TranslatorPolicy policy , DiameterPacket requestPacket,IRequestParameters requestParameters ,TranslatorParams params,ValueProvider valueProvider){
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyRequestMapping(basePolicy,requestPacket, requestParameters, params,valueProvider);
		}	
		handleDiameterToRatingTranslation(policy.getParsedRequestMappingInfoMap(), requestPacket, requestParameters, params,valueProvider);
	}
	
	private void handleDiameterToRatingTranslation(
			Map<String,ParsedMappingInfo> infoMap, DiameterPacket requestPacket,IRequestParameters requestParameters ,TranslatorParams params,ValueProvider valueProvider) {
		LogicalExpression expression = null;
		List<ParsedMapping> mappings;
		for(ParsedMappingInfo mappingInfo : infoMap.values()){

			if(isMappingApplicable(mappingInfo.getInRequestExpression(),mappingInfo.getInRequestStrExpression(),valueProvider)){
				params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, mappingInfo.getMappingName());
				LogManager.getLogger().info(MODULE, "Request Translation using mapping: "+mappingInfo.getMappingName());
				boolean isDummyResponse = mappingInfo.isDummyResponse();
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
						applyDiameterToRatingMappings(requestPacket, requestParameters, mapping,params);		
					}else{
						expression = mapping.getExpression();				
						if((expression).evaluate(valueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyDiameterToRatingMappings(requestPacket, requestParameters, mapping,params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
				break;
			}
			
		}
	}
	

	private void handleRatingToDiameterTranslation(Map<String,ParsedMappingInfo> infoMap,IResponseObject responseObject,ApplicationResponse response,TranslatorParams params,ValueProvider requestValueProvider,Map<String,String>dummResponseMap) throws TranslationFailedException {
		ResponseValueProviderImpl valueProvider = new ResponseValueProviderImpl(responseObject);
		LogicalExpression expression = null;
		List<ParsedMapping> mappings;
		ParsedMappingInfo tempMappingInfo;
		String mappingName = (String)params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING);
		
			if(mappingName == null){
				throw new TranslationFailedException("No Translation Mapping is selected during request translation");
			}
			tempMappingInfo = infoMap.get(mappingName);
			if(tempMappingInfo == null){
				throw new TranslationFailedException("Invalid Translation Mapping "+mappingName+" is selected during request translation");
			}
			LogManager.getLogger().info(MODULE, "Applying mapping for inMessageType: " + tempMappingInfo.getInRequestStrExpression()+ " outMessageType: " + tempMappingInfo.getOutRequestType());
			mappings = tempMappingInfo.getParsedResponseMappingDetails();
			LogManager.getLogger().info(MODULE, "Response Translation using mapping: "+tempMappingInfo.getMappingName());
			
			if(tempMappingInfo.isDummyResponse()){
					translateDummyResponse(mappings, dummResponseMap, response, params);
					return;
				}
				for(ParsedMapping mapping : mappings){
					LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
					if("*".equalsIgnoreCase(mapping.getStrExpression())){
						LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
						applyRatingToDiameterMappings(response, responseObject, mapping.getMappingNodes(), mapping.getDefaultValuesMap(), mapping.getValueNodes(),params);						
					}else{
						expression = mapping.getExpression();				
						if(expression.evaluate(valueProvider)){
							LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
							applyRatingToDiameterMappings(response, responseObject, mapping.getMappingNodes(), mapping.getDefaultValuesMap(), mapping.getValueNodes(),params);
						}else{
							LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
						}		
					}	
				}
				setHardCodedAvps(response,responseObject);
			}

	
	private void applyDiameterToRatingMappings(final DiameterPacket requestPacket,IRequestParameters requestParameters, ParsedMapping mapping,TranslatorParams params){
		
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
			LogManager.getLogger().info(MODULE, "Applying mapping expression: "+ avpId + " = " + value);
			if(isLiteral(value)){ // remove '"' from the value and set it to the give attribute 
				strValue = unquote(value);
				strValue = getValue(strValue,valueNodes);
			}else if (isKeyword(value)) {
				strValue = getKeywordValue(value, params, true,new com.elitecore.core.commons.data.ValueProvider(){

					@Override
					public String getStringValue(String identifier) {
						if(identifier!=null){
							return requestPacket.getAVPValue(identifier);
						}else {
							return null;
						}
					}
					
				});
				if(strValue!=null){
					strValue = getValue(strValue, valueNodes);
				}
			}else{ // value is Attribute
				IDiameterAVP avp = requestPacket.getAVP(value);
				if(avp != null){
					strValue = avp.getStringValue();
					strValue = getValue(strValue,valueNodes);
				}					
			}		
			if(strValue==null){
				strValue = defaultValues.get(avpId);
				if(strValue != null && strValue.length() > 0){
					strValue = getValue(strValue,valueNodes);
					requestParameters.put(avpId, strValue);
				}else{
					LogManager.getLogger().debug(MODULE,"Skipping " + avpId + ", " + "Reason: Not found in response and default value is not provided.");
				}
			}else {
				requestParameters.put(avpId, strValue);
			}
		}		
	
	}
	
	private void applyRatingToDiameterMappings(ApplicationResponse response,final IResponseObject responseObject,Map<String,String>mappingNodes ,Map<String,String> defaultValues,Map<String,String>valueNodes ,TranslatorParams params){
		
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
			}else if (isKeyword(value)) {
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
				strValue = defaultValues.get(avpId);
				if(strValue != null && strValue.length() > 0){
					LogManager.getLogger().debug(MODULE,"Key: "+ value+" is not found in response object,using default value: " + strValue);
					addOrReplaceAvp(avpId, strValue, valueNodes, response.getDiameterAnswer());
				}else{
					LogManager.getLogger().debug(MODULE,"Skipping " + avpId + ", " + "Reason: Not found in response and default value is not provided.");
				}
			}else{
				addOrReplaceAvp(avpId, strValue, valueNodes, response.getDiameterAnswer());
			}
		}		
	
	}

	private void addOrReplaceAvp(String avpId,String avpValue,Map<String,String>valueNodes,DiameterPacket packet){
		avpValue = getValue(avpValue,valueNodes);
		DiameterUtility.addOrReplaceAvp(avpId,packet,avpValue);
	}
	private IDiameterAVP getFinalUnitIndicationAvp(int quota,IResponseObject responseObject){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Last-Quota = " + quota);
		}
		if(quota == 0){			
			IDiameterAVP redirectServerAddress = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.REDIRECT_SERVER_ADDRESS);
			IDiameterAVP redirectServerType = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.REDIRECT_ADDRESS_TYPE);
			AvpGrouped redirectServer = (AvpGrouped)DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.REDIRECT_SERVER);
			String url = responseObject.get("GENERAL5");
			if(url != null && url.length() >0){
				redirectServerAddress.setStringValue(url);
				redirectServer.addSubAvp(redirectServerAddress);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "REDIRECT_SERVER_ADDRESS is NULl from Rating");
				}	
			}
			
			redirectServerType.setStringValue("2"); // URL			
			redirectServer.addSubAvp(redirectServerType);			
			
			IDiameterAVP finalUnitAction = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.FINAL_UNIT_ACTION);
			finalUnitAction.setStringValue("1"); // Redirect
			
			AvpGrouped finalUnitIndication = (AvpGrouped)DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.FINAL_UNIT_INDICATION);
			
			finalUnitIndication.addSubAvp(finalUnitAction);
			finalUnitIndication.addSubAvp(redirectServer);
			
			return finalUnitIndication;
		}else{
			
			return null;
		}
	}
	private int getDiameterResultCode(int result){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Result-Code = " + result);
		}
		switch(result){
			case 1:
				return ResultCode.DIAMETER_SUCCESS.code;
			case -4002003:  // CUSTOMER_ACCOUNT_NOT_FOUND_FOR_USERNAME
				return ResultCode.DIAMETER_USER_UNKNOWN.code;
			case -4006010: // BALANCE_INSUFFICIENT
				return ResultCode.DIAMETER_CREDIT_LIMIT_REACHED.code;
		}
		if(result <=0){
			return ResultCode.DIAMETER_RATING_FAILED.code;
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "None value mapping found for result-code: " + result);
			}
		}
		return ResultCode.DIAMETER_RATING_FAILED.code;
	}
	private int setLength(int  oldLength, int newLength){
		if(oldLength < newLength)
			return newLength;
		return oldLength;
	}
	/* this method is introduced only for IOT purpose */
	private void setHardCodedAvps(ApplicationResponse response,IResponseObject responseObject){
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Applying MSCC avps");
		}
		
		String sessionDwnldDtTransfer = responseObject.get("SESSION-DOWNLOAD-DATA-TRANSFER");
		String sessionUpldDtTransfer = responseObject.get("SESSION-UPLOAD-DATA-TRANSFER");		
		String url = responseObject.get("URL");
		String maxSessionVolume = responseObject.get("MAX-SESSION-VOLUME");
		String resultCode = responseObject.get("RESULT-CODE");
		String lastQuota = responseObject.get("LAST-QUOTA");
		String maxSessionTime = responseObject.get("MAX-SESSION-TIME");
		
		String [] dwnldDatas = null;
		String [] upldDatas = null;
		String [] urls = null;
		String [] maxSessionVolumes = null;
		String [] resultCodes = null;
		String [] lastQuotas = null;
		String [] maxSessionTimes = null;
		int length = 0;
		
		if(maxSessionVolume != null && maxSessionVolume.length() > 0){
			maxSessionVolumes = maxSessionVolume.split(";");
			length = setLength(length, maxSessionVolumes.length);
		}
		
		if(resultCode != null && resultCode.length() > 0){
			resultCodes = resultCode.split(";");
			length = setLength(length, resultCodes.length);
		}
		if(lastQuota != null && lastQuota.length() >0){
			lastQuotas = lastQuota.split(";");
			length = setLength(length, lastQuotas.length);
		}
		if(maxSessionTime != null && maxSessionTime.length() > 0){
			maxSessionTimes = maxSessionTime.split(";");
			length = setLength(length, maxSessionTimes.length);
		}			
		
		if(sessionDwnldDtTransfer != null && sessionDwnldDtTransfer.length() >0 ){
			dwnldDatas = sessionDwnldDtTransfer.split(";");
			length = setLength(length, dwnldDatas.length);
		}
		if(sessionUpldDtTransfer != null && sessionUpldDtTransfer.length() >0){
			upldDatas = sessionUpldDtTransfer.split(";");
			length = setLength(length, upldDatas.length);
		}
		if(url != null && url.length() > 0){
			urls = url.split(";");
			length = setLength(length, urls.length);
		}					
		
		for(int i=0;i<length; i++){
			boolean isGrantedServiceUnitRcvd = false;
			AvpGrouped mscc = (AvpGrouped)DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
			AvpGrouped grantedSrviceUnitAvp =  (AvpGrouped)DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.GRANTED_SERVICE_UNIT);
			IDiameterAVP ccInputAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CC_INPUT_OCTETS);
			IDiameterAVP ccOutputAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CC_OUTPUT_OCTETS);			
			IDiameterAVP ccTotalOctetsAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CC_TOTAL_OCTETS);
			IDiameterAVP ccTimeAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CC_TIME);
			
			IDiameterAVP ratingGroupAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RATING_GROUP);
			IDiameterAVP resultcodeAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
				
			if(dwnldDatas != null && i < dwnldDatas.length){
				ccOutputAvp.setStringValue(dwnldDatas[i]);
				grantedSrviceUnitAvp.addSubAvp(ccOutputAvp);
				isGrantedServiceUnitRcvd = true;
			}
			
			if(upldDatas != null && i < upldDatas.length){
				ccInputAvp.setStringValue(upldDatas[i]);
				grantedSrviceUnitAvp.addSubAvp(ccInputAvp);
				isGrantedServiceUnitRcvd = true;
			}		

			if(maxSessionVolumes != null && i < maxSessionVolumes.length){
				ccTotalOctetsAvp.setStringValue(maxSessionVolumes[i]);
				grantedSrviceUnitAvp.addSubAvp(ccTotalOctetsAvp);
				isGrantedServiceUnitRcvd = true;
			}		
			
			if(maxSessionTimes != null && i < maxSessionTimes.length ){
				ccTimeAvp.setStringValue(maxSessionTimes[i]);
				grantedSrviceUnitAvp.addSubAvp(ccTimeAvp);
				isGrantedServiceUnitRcvd = true;
			}
			if(isGrantedServiceUnitRcvd){
				mscc.addSubAvp(grantedSrviceUnitAvp);
			}
			
			
			if(resultCodes != null && i < resultCodes.length){
				int result = ResultCode.DIAMETER_RATING_FAILED.code;
				try{
					 result = Integer.parseInt(resultCodes[i]);
					 result = getDiameterResultCode(result);
				}catch(NumberFormatException e){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Invalid result-code detected from rating: " + resultCodes[i] + " setting result-code: " + ResultCode.DIAMETER_RATING_FAILED.name());
					}
					result = ResultCode.DIAMETER_RATING_FAILED.code;
				}
				resultcodeAvp.setInteger(result);
				mscc.addSubAvp(resultcodeAvp);
			}				
			if(urls != null && i < urls.length){
				ratingGroupAvp.setStringValue(urls[i]);
				mscc.addSubAvp(ratingGroupAvp);
			}
			if(lastQuotas != null && i < lastQuotas.length){
				int quota = 0;
				IDiameterAVP  finalUnitIndicationAvp = null;
				try{
					quota = Integer.parseInt(lastQuotas[i]);
					finalUnitIndicationAvp = getFinalUnitIndicationAvp(quota,responseObject);
				}catch(NumberFormatException e){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Invalid Last-Quota detected from rating: " + lastQuotas[i]);
					}
				}					
				if(finalUnitIndicationAvp != null){
					mscc.addSubAvp(finalUnitIndicationAvp);
				}
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Adding MSCC Avp(" + (i+1) + ")" + mscc.toString());
			}
			response.addAVP(mscc);
		}
	}
	@Override
	public void translateResponse(String policyDataId, TranslatorParams params)
			throws TranslationFailedException {
		
		IResponseObject responseObject = (IResponseObject)params.getParam(TranslatorConstants.FROM_PACKET);
		
		ApplicationResponse response = (ApplicationResponse)params.getParam(TranslatorConstants.TO_PACKET);
		DiameterPacket	appReq = (DiameterPacket)params.getParam(TranslatorConstants.SOURCE_REQUEST);
		
		if(appReq == null){
			throw new TranslationFailedException("REQUEST Packet is null");
		}
		if(response == null){
			throw new TranslationFailedException("RESPONSE Packet is null");
		}
		if(responseObject == null && !(Boolean.parseBoolean(String.valueOf(params.getParam(TranslatorConstants.DUMMY_MAPPING))))){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Failed to Translate From Rating To Diameter, Reason: Rating Response is NULL");
			setResultCode(response, ResultCode.DIAMETER_RATING_FAILED.code);
			response.setFurtherProcessingRequired(false);		
		}else{
		if(responseObject!=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){				
				LogManager.getLogger().info(MODULE, "Rating packet to translate:" + responseObject.toString());
			}
			}
			ValueProvider requestValueProvider = new DiameterAVPValueProvider(appReq);
			applyResponseMapping(policiesMap.get(policyDataId), responseObject, response,params,requestValueProvider);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Translation from Rating to Diameter Completed");
				LogManager.getLogger().info(MODULE, "Translated Diameter packet:" + response.toString());
			}
		}
	}
	private void applyResponseMapping(TranslatorPolicy policy,IResponseObject responseObject, ApplicationResponse response, TranslatorParams params,ValueProvider requestValueProvider) throws TranslationFailedException {
		TranslatorPolicy basePolicy = policiesMap.get(policy.getBaseTranslationMappingId());
		if(basePolicy !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Appying Base Translation: "+basePolicy.getName());
			applyResponseMapping(basePolicy,responseObject, response,params,requestValueProvider);
		}	
		handleRatingToDiameterTranslation(policy.getParsedResponseMappingInfoMap(), responseObject, response,params,requestValueProvider,policy.getDummyParametersMap());
	}

	private void setResultCode(ApplicationResponse response,int resultCode) {
		IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
		resultCodeAvp.setInteger(resultCode);
		response.addAVP(resultCodeAvp);
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
			}
			else
				throw new MissingIdentifierException("Object not found: "+identifier);
		}
		
		@Override
		public List<Long> getLongValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
			String value = responseObject.get(identifier);
			if(value != null){
				try{
					List<Long> longValues=new ArrayList<Long>(1);
					longValues.add(Long.parseLong(value));
					return longValues;
				}catch(Exception e){
					throw new InvalidTypeCastException(e.getMessage());
	}
			}else
				throw new MissingIdentifierException("Object not found: "+identifier);
		}

		@Override
		public Object getValue(String key) {
			return null;
		}
	}

	private void translateDummyResponse(List<ParsedMapping> mappings,Map<String, String> dummyMap, ApplicationResponse response,TranslatorParams params) {
		LogicalExpression expression = null;
		DummyResponseValueProvider valueProvider = new DummyResponseValueProvider(dummyMap);
		for(ParsedMapping mapping : mappings){
			LogManager.getLogger().info(MODULE, "Applying check expression: "+ mapping.getStrExpression());
			if("*".equalsIgnoreCase(mapping.getStrExpression())){
				LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
				applyDummyRatingToDiameterMappings(response, dummyMap, mapping.getMappingNodes(), mapping.getDefaultValuesMap(), mapping.getValueNodes(),params);						
			}else{
				expression = mapping.getExpression();				
				if(expression.evaluate(valueProvider)){
					LogManager.getLogger().info(MODULE, "Check expression result: TRUE" );
					applyDummyRatingToDiameterMappings(response, dummyMap, mapping.getMappingNodes(), mapping.getDefaultValuesMap(), mapping.getValueNodes(),params);
				}else{
					LogManager.getLogger().info(MODULE, "Check expression result: FALSE");
				}		
			}	
		}
		
}
	/**
	 * Method will be used when Dummy Response is True.
	 */
	private void applyDummyRatingToDiameterMappings(ApplicationResponse response,
			final Map<String, String> dummyMap, Map<String, String> mappingNodes,
			Map<String, String> defaultValuesMap,
			Map<String, String> valueNodes, TranslatorParams params) {

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
			}else if (isKeyword(value)) {
				strValue = getKeywordValue(value, params, false,new com.elitecore.core.commons.data.ValueProvider(){

					@Override
					public String getStringValue(String identifier) {
						if(identifier!=null){
							return dummyMap.get(identifier);
}
						return null;
					}
				});
			}else{
				strValue = dummyMap.get(value);
			}
			if(strValue == null || !(strValue.length()>0)){
				strValue = defaultValuesMap.get(avpId);
				if(strValue != null && strValue.length() > 0){
					LogManager.getLogger().debug(MODULE,"Key: "+ value+" is not found in response object,using default value: " + strValue);
					addOrReplaceAvp(avpId, strValue, valueNodes, response.getDiameterAnswer());
				}else{
					LogManager.getLogger().debug(MODULE,"Skipping " + avpId + ", " + "Reason: Not found in response and default value is not provided.");
				}
			}else{
				addOrReplaceAvp(avpId, strValue, valueNodes, response.getDiameterAnswer());
			}
		}		
	}
}
