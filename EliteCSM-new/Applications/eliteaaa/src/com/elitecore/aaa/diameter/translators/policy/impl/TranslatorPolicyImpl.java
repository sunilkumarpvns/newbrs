package com.elitecore.aaa.diameter.translators.policy.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.diameter.translators.policy.TranslatorPolicy;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMapping;
import com.elitecore.aaa.diameter.translators.policy.data.ParsedMappingInfo;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.util.MappingParser;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.MappingData;
import com.elitecore.diameterapi.core.translator.policy.data.TranslationDetail;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.diameterapi.core.translator.policy.data.impl.MappingDataImpl;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslationDetailImpl;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class TranslatorPolicyImpl implements TranslatorPolicy{

	private static final String MODULE = "TRNSLTR-POLICY";
	private TranslatorPolicyData policyData;
	private boolean isInitialized = false;	
	private com.elitecore.exprlib.compiler.Compiler compiler;
	
	private LinkedHashMap<String, ParsedMappingInfo> parsedRequestMappingInfoMap;
	private LinkedHashMap<String, ParsedMappingInfo> parsedResponseMappingInfoMap;
	
	
	
	public TranslatorPolicyImpl(TranslatorPolicyData policyData){
		this.policyData = policyData;
		this.compiler = com.elitecore.exprlib.compiler.Compiler.getDefaultCompiler();
		
		this.parsedRequestMappingInfoMap = new LinkedHashMap<String, ParsedMappingInfo>();
		this.parsedResponseMappingInfoMap = new LinkedHashMap<String, ParsedMappingInfo>();
		
	}
	private void initializeMappingData(List<MappingDataImpl> reqMappingDatas,List<ParsedMapping> mappingDetails) throws InitializationFailedException{
		ParsedMappingImpl parsedMapping = null;
		for(MappingData mappingData: reqMappingDatas){
			parsedMapping = new ParsedMappingImpl();
			try {
				parsedMapping.strExpression = mappingData.getCheckExpression();
				if(!"*".equalsIgnoreCase(parsedMapping.strExpression)){
					parsedMapping.expression = this.compiler.parseLogicalExpression(parsedMapping.strExpression);
				}				
				parsedMapping.mappedNodes = getParsedNodes(mappingData.getMappingExpression());
				String defaultString = mappingData.getDefaultValue();
				if(defaultString != null && defaultString.length() > 0)
					parsedMapping.defaultValuesMap = getParsedNodes(defaultString);
				
				String valueMappings = mappingData.getValueMapping();
				if(valueMappings != null && valueMappings.length() > 0){
					parsedMapping.valueNodes = getParsedNodes(mappingData.getValueMapping());
				}				
				mappingDetails.add(parsedMapping);
				
			}catch (InvalidExpressionException e) {
				throw new InitializationFailedException("Invalid Expression : "+parsedMapping.strExpression);					
			}
		}
		
	}
	
	@Override
	public void init() throws InitializationFailedException {		
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Translation Policy: \n" + policyData);
		}
		
		LinkedHashMap<String, ParsedMappingInfo> tempParsedRequestMappingInfoMap = new LinkedHashMap<String, ParsedMappingInfo>();
		LinkedHashMap<String, ParsedMappingInfo> tempParsedResponseMappingInfoMap = new LinkedHashMap<String, ParsedMappingInfo>();
		
		
		List< TranslationDetailImpl> translationDetails = policyData.getTranslationDetailList();
		ParsedMappingInfoImpl parsedMappingTypes = null;
		for(TranslationDetail translationDetail: translationDetails ){
			if(policyData.getToTranslatorId().equalsIgnoreCase(TranslatorConstants.DIAMETER_TRANSLATOR)){
				parsedMappingTypes = new DiameterParsedMappingInfoImpl();
				parsedMappingTypes.outRequestType = translationDetail.getOutRequestType();
				parseOutRequestType((DiameterParsedMappingInfoImpl)parsedMappingTypes);
			}else{
				parsedMappingTypes = new ParsedMappingInfoImpl();
				parsedMappingTypes.outRequestType = translationDetail.getOutRequestType();
			}
				
			try {
				parsedMappingTypes.inRequestStrExpression = translationDetail.getInRequestType();
				parsedMappingTypes.inRequestExpression = this.compiler.parseLogicalExpression(translationDetail.getInRequestType());
			} catch (InvalidExpressionException e) {
				throw new InitializationFailedException("Invalid Expression : "+translationDetail.getInRequestType());				
			}
			//Added For Dummy Response Parameters
			parsedMappingTypes.isDummyResponse = translationDetail.getIsDummyResponse();
			
			
			parsedMappingTypes.mappingName = translationDetail.getMappingName();
			
			initializeMappingData(translationDetail.getRequestMappingDataList(),parsedMappingTypes.parsedRequestMappingDetails);			
			tempParsedRequestMappingInfoMap.put(translationDetail.getMappingName(), parsedMappingTypes);
			
			initializeMappingData(translationDetail.getResponseMappingDataList(),parsedMappingTypes.parsedResponseMappingDetails);
			tempParsedResponseMappingInfoMap.put(translationDetail.getMappingName(), parsedMappingTypes);

		}
		
		this.parsedRequestMappingInfoMap = tempParsedRequestMappingInfoMap;
		this.parsedResponseMappingInfoMap = tempParsedResponseMappingInfoMap;
		
		this.isInitialized = true;
	}

	private void parseOutRequestType(DiameterParsedMappingInfoImpl parsedMappingTypes) {
		String outRequestType = parsedMappingTypes.getOutRequestType();
		if(outRequestType!=null && outRequestType.split(",").length>=1){
			String []strArray = outRequestType.split(",");
			int size = strArray.length;
			String [] paramArray;
			for(int i=0;i<size;i++){
				paramArray = strArray[i].split("=");
				if(paramArray.length==2){
					if(paramArray[0].equalsIgnoreCase("CommandCode")){
						parsedMappingTypes.commandCode = getStringToIntValue(paramArray[1],parsedMappingTypes.commandCode);
						parsedMappingTypes.bFlagForCommandCode = true;
					}else if (paramArray[0].equalsIgnoreCase("ApplicationId")) {
						parsedMappingTypes.applicationId =getStringToIntValue(paramArray[1],parsedMappingTypes.applicationId);
						parsedMappingTypes.bFlagForApplicationId = true;
					}else if (paramArray[0].equalsIgnoreCase("ProxyFlag")) {
						parsedMappingTypes.isProxiable = getStringToBooleanValue(paramArray[1],parsedMappingTypes.isProxiable);
						parsedMappingTypes.bFlagForProxyFlag = true;
					}else if (paramArray[0].equalsIgnoreCase("RequestFlag")) {
						parsedMappingTypes.isRequest = getStringToBooleanValue(paramArray[1],parsedMappingTypes.isRequest);
						parsedMappingTypes.bFlagForRequestFlag = true;
					}
				}	
			}
		}
	}
	private boolean getStringToBooleanValue(String originalString, boolean defaultValue) {
		try{
			return Boolean.parseBoolean(originalString.trim());
		}catch (Exception e) {
			return defaultValue;
		}
		
	}
	protected String getValue(String val){
		return val.replaceAll("\"", "");
	}
	private int getStringToIntValue(String originalString, int defaultValue) {
		try{
			return Integer.parseInt(getValue(originalString.trim()));
		}catch (Exception e) {
			return defaultValue;
		}
	}
	private Map<String,String> getParsedNodes(String mappingExpression){
		Map<String,String> parsedNodesMap = MappingParser.getParsedMap(mappingExpression);  
		if(parsedNodesMap == null){
			parsedNodesMap = new HashMap<String, String>();
		}
		return parsedNodesMap;
	}
	@Override
	public String getFromInterpreterId() {
		return policyData.getFromTranslatorId();
	}

	@Override
	public String getToInterpreterId() {
		return policyData.getToTranslatorId();
	}

	@Override
	public String getPolicyId() {		
		return policyData.getTransMapConfId();
	}
	
	@Override
	public String getBaseTranslationMappingId() {		
		return policyData.getBaseTranslationMappingId();
	}

	@Override
	public boolean isInitialized() {
		return isInitialized;
	}


	private class ParsedMappingImpl implements ParsedMapping{
		private LogicalExpression expression;
		private String strExpression = "";
		private Map<String,String> mappedNodes;
		private Map<String,String> valueNodes;
		private Map<String,String> defaultValuesMap;
		public ParsedMappingImpl(){
			mappedNodes = new LinkedHashMap<String, String>();
			valueNodes = new LinkedHashMap<String, String>();
			defaultValuesMap = new LinkedHashMap<String, String>();
		}
		@Override
		public Map<String,String> getDefaultValuesMap() {
			return defaultValuesMap;
		}
		@Override
		public LogicalExpression getExpression() {
			return expression;
		}
		@Override
		public Map<String, String> getMappingNodes() {
			return mappedNodes;
		}
		@Override
		public Map<String, String> getValueNodes() {
			return valueNodes;
		}
		@Override
		public String getStrExpression() {
			return strExpression;
		}		
		
	}
	
	private class ParsedMappingInfoImpl implements ParsedMappingInfo{
		private LogicalExpression inRequestExpression;
		private String inRequestStrExpression;
		private String outRequestType;
		private List<ParsedMapping> parsedRequestMappingDetails;
		private List<ParsedMapping> parsedResponseMappingDetails;
		private boolean isDummyResponse = false;
		private String mappingName;

		private ParsedMappingInfoImpl(){
			parsedRequestMappingDetails = new ArrayList<ParsedMapping>();
			parsedResponseMappingDetails = new ArrayList<ParsedMapping>();
		}

		@Override
		public LogicalExpression getInRequestExpression() {
			return inRequestExpression;
		}

		@Override
		public String getOutRequestType() {
			return outRequestType;
		}

		@Override
		public List<ParsedMapping> getParsedRequestMappingDetails() {
			return parsedRequestMappingDetails;
		}

		@Override
		public List<ParsedMapping> getParsedResponseMappingDetails() {
			return parsedResponseMappingDetails;
		}

		@Override
		public String getInRequestStrExpression() {			
			return inRequestStrExpression;
		}

		@Override
		public boolean isDummyResponse() {
			return isDummyResponse;
		}
		
		@Override
		public String getMappingName() {
			return mappingName;
	}
		
	}
	public class DiameterParsedMappingInfoImpl extends ParsedMappingInfoImpl{
		private int commandCode;
		private int applicationId;
		private boolean isProxiable = true;
		private boolean isRequest = true;
		
		private boolean bFlagForCommandCode ;
		private boolean bFlagForApplicationId ;
		private boolean bFlagForProxyFlag ;
		private boolean bFlagForRequestFlag;
		
		public int getCommandCode() {
			return commandCode;
		}
		public int getApplicationId() {
			return applicationId;
		}
		public boolean isProxiable() {
			return isProxiable;
		}
		public boolean isRequest() {
			return isRequest;
		}
		public boolean needToSetCommandCodes() {
			return bFlagForCommandCode;
		}
		public boolean needToSetApplicationId() {
			return bFlagForApplicationId;
		}
		public boolean needToSetProxyFlag() {
			return bFlagForProxyFlag;
		}
		public boolean needToSetRequestFlag() {
			return bFlagForRequestFlag;
		}
		
		
	}

	
	@Override
	public Map<String, String> getDummyParametersMap() {		
		return policyData.getDummyResponseMap();
	}
	@Override
	public String getName() {
		return policyData.getName();
	}
	@Override
	public String getScript() {
		return policyData.getScript();
	}
	@Override
	public Map<String, ParsedMappingInfo> getParsedRequestMappingInfoMap() {
		return this.parsedRequestMappingInfoMap;
	}
	@Override
	public Map<String, ParsedMappingInfo> getParsedResponseMappingInfoMap() {
		return this.parsedResponseMappingInfoMap;
	}

}
