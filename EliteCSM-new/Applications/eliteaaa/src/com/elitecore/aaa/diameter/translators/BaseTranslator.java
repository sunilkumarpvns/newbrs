package com.elitecore.aaa.diameter.translators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.elitecore.aaa.core.util.eap.Utility;
import com.elitecore.aaa.diameter.translators.policy.TranslatorPolicy;
import com.elitecore.aaa.diameter.translators.policy.impl.TranslatorPolicyImpl;
import com.elitecore.aaa.script.TranslationMappingScript;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.translator.Translator;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class BaseTranslator implements Translator {

	private static final String MODULE = "BS-TRANSLATOR";
	private ServerContext serverContext;
	private static final String OPERATION_ARGUMENT_SEPARATOR  = "-";
	public static final String MULTIMODE_KEYWORD = "${MULTIMODE}";


	Random random ;

	private Map<String, TranslationKeyword> translationReqKeywords ;
	private Map<String, TranslationKeyword> translationResKeywords ;
	private KeywordEvaluator keywordEvaluator;
	
	protected Map<String, TranslatorPolicy> policiesMap;
	public BaseTranslator(ServerContext serverContext){
		this.serverContext = serverContext;
		policiesMap = new HashMap<String, TranslatorPolicy>();
		this.translationReqKeywords = new HashMap<String, TranslationKeyword>();
		this.translationResKeywords = new HashMap<String, TranslationKeyword>();
		this.random = new Random();
		this.keywordEvaluator = new KeywordEvaluator(translationReqKeywords, translationResKeywords, random);
	}

	@Override
	public void init(TranslatorPolicyData policyData)
			throws InitializationFailedException {
		if(policyData==null){
			return;
		}
		TranslatorPolicy policy = new TranslatorPolicyImpl(policyData);
		try {
			policy.init();
		} catch (InitializationFailedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error in initializing Translation mapping : "+policyData.getName()+" Reason : "+e);
		}
		policiesMap.put(policy.getPolicyId(), policy);
	}

	@Override
	public final void postTranslateRequest(String policyId, TranslatorParams params) {
		TranslatorPolicy policy = policiesMap.get(policyId);
		if(policy == null){
			return;
		}

		if(policy.getScript() != null){
			try {
				serverContext.getExternalScriptsManager().execute(policy.getScript(), TranslationMappingScript.class, "requestTranslationExtension", new Class<?>[]{TranslatorParams.class}, new Object[]{params});
			} catch (Exception ex) {
				LogManager.getLogger().trace(ex);
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Error in executing \"requestTranslationExtension\" method of translation mapping script: " + policy.getScript() + ". Reason: " + ex.getMessage());
				}
			}
		}
	}

	@Override
	public final void postTranslateResponse(String policyId, TranslatorParams params) {
		TranslatorPolicy policy = policiesMap.get(policyId);
		if(policy == null){
			return;
		}

		if(policy.getScript() != null){
			try {
				serverContext.getExternalScriptsManager().execute(policy.getScript(), TranslationMappingScript.class, "responseTranslationExtension", new Class<?>[]{TranslatorParams.class}, new Object[]{params});
			} catch (Exception ex) {
				LogManager.getLogger().trace(ex);
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Error in executing \"responseTranslationExtension\" method of translation mapping script: " + policy.getScript() + ". Reason: " + ex.getMessage());
				}
			}
		}
	}



	protected boolean isLiteral(String val){
		if(val!=null && ((String.valueOf("\"")).equals(String.valueOf(val.charAt(0)))) && (String.valueOf("\"")).equals(String.valueOf(val.charAt(val.length()-1))))
			return true;
		else
			return false;
	}

	protected String unquote(String val) {
		if(val!=null){
			return val.substring(1,val.length()-1);
		}else {
			return null;	
		}	

	}
	
	protected String getKeywordValue(String strkeyword,TranslatorParams params,boolean isRequest,com.elitecore.core.commons.data.ValueProvider valueProvider){
		return keywordEvaluator.getKeywordValue(strkeyword, params, isRequest, valueProvider);
	}
	
	protected boolean isMultimodeEnabled (String value){
		return keywordEvaluator.isMultimodeEnabled(value);
	}
	
	protected boolean isKeyword(String val){
		if(val!=null && val.startsWith("${"))
			return true;
		return false;
	}
	
	protected String getValue(String avpValue, Map<String,String> valueNodes) {
		if(avpValue != null && valueNodes != null){
			String val =  valueNodes.get(avpValue);
			if(val != null && val.length() > 0){
				avpValue = unquote(val);
			}			
		}else{
			return "";
		}		
		return avpValue;		
	}
	protected boolean isMappingApplicable(LogicalExpression checkExpression,
			String checkExpressionStr,ValueProvider valueProvider) {
		if ("*".equalsIgnoreCase(checkExpressionStr)) {
			return true;
		}
		return checkExpression.evaluate(valueProvider);
	}

	protected void registerKeyword(TranslationKeyword translationKeyword,boolean isRequest){
		if(translationKeyword!=null && translationKeyword.getName()!=null && translationKeyword.getName().trim().length()>0){
			if(isRequest)
				this.translationReqKeywords.put(translationKeyword.getName(), translationKeyword);
			else {
				this.translationResKeywords.put(translationKeyword.getName(), translationKeyword);
			}
		}
	}

	protected void registerRequestKeyword(TranslationKeyword translationKeyword) {
		if (translationKeyword == null || Strings.isNullOrBlank(translationKeyword.getName())) {
			return;
		}
		this.translationReqKeywords.put(translationKeyword.getName(), translationKeyword);
	}

	protected void registerResponseKeyword(TranslationKeyword translationKeyword) {
		if (translationKeyword == null || Strings.isNullOrBlank(translationKeyword.getName())) {
			return;
		}
		this.translationResKeywords.put(translationKeyword.getName(), translationKeyword);
	}

	protected String stripKeywordName(String value){
		String returnValue = value;
		if (value.startsWith("${")){
			int index = value.lastIndexOf("}");
			if (index != -1){
				if (value.charAt(index+1) == ':')
					returnValue = value.substring(index+2);
				else 
					returnValue = value.substring(index+1);

			}
		}
		return returnValue;
	}
	
	protected String getOperatorArgument(String strKeyword){
		return keywordEvaluator.getOperatorArgument(strKeyword);
	}

	protected KeywordContext getKeywordContext(){
		return new KeywordContext(){

			@Override
			public String getKeywordValue(String strkeyword, TranslatorParams params, boolean isRequest,
					com.elitecore.core.commons.data.ValueProvider valueProvider) {
				return BaseTranslator.this.getKeywordValue(strkeyword, params, isRequest, valueProvider);
			}

			@Override
			public boolean isKeyword(String val) {
				return BaseTranslator.this.isKeyword(val);
			}

			@Override
			public ILogger getLogger() {
				return LogManager.getLogger();
			}

		};
	}


	public interface KeywordContext{
		public String getKeywordValue(String strkeyword,TranslatorParams params,boolean isRequest,com.elitecore.core.commons.data.ValueProvider valueProvider);
		public boolean isKeyword(String val);
		public ILogger getLogger() ;
	}

	public class DummyResponseValueProvider implements ValueProvider{

		private Map<String,String>dummyResposeMap = new HashMap<String, String>();

		public DummyResponseValueProvider(Map<String,String> dummyResposeMap) {
			this.dummyResposeMap = dummyResposeMap;
		}

		@Override
		public String getStringValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			String identifierValue = dummyResposeMap.get(identifier);
			if(identifierValue !=null)
				return identifierValue;
			else
				throw new MissingIdentifierException("Object not found: "+identifier);
		}

		@Override
		public long getLongValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			String identifierValue = dummyResposeMap.get(identifier);
			if(identifierValue != null){
				return Long.parseLong(identifierValue);
			}else{
				throw new MissingIdentifierException("Object not found: "+identifier);
			}
		}

		@Override
		public List<String> getStringValues(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			String identifierValue = dummyResposeMap.get(identifier);

			if(identifierValue!=null){
				List<String> stringValues=new ArrayList<String>();
				stringValues.add(identifierValue);
				return stringValues;
			}else{
				throw new MissingIdentifierException("Object not found: "+identifier);
			}
		}

		@Override
		public List<Long> getLongValues(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			String identifierValue = dummyResposeMap.get(identifier);

			if(identifierValue!=null){
				List<Long> longValues=new ArrayList<Long>();
				longValues.add(Long.parseLong(identifierValue));
				return longValues;
			}else{
				throw new MissingIdentifierException("Object not found: "+identifier);
			}
		}

		@Override
		public Object getValue(String key) {
			return null;
		}

	}
	/**
	 * Method will be used when Dummy Response is True.
	 */
	public String getGroupedAVPValue(String value,
			Map<String, String> dummyResponseMap,
			Map<String, String> defaultValues, Map<String, String> valueNodes) {


		String resultString;
		JSONObject jsonObject = JSONObject.fromObject(value);
		JSONObject resultjsonObject = new JSONObject();
		Set<?> attributeSet = jsonObject.keySet();
		Iterator<?> setItr = attributeSet.iterator();
		while(setItr.hasNext()){
			String strId = (String)setItr.next();
			JSONArray valueArray = jsonObject.optJSONArray(strId);
			String key;
			String strValue;
			if(valueArray != null){
				final int valueArraySize = valueArray.size();
				for(int i=0; i<valueArraySize; i++){
					key = valueArray.getString(i);

					if(key.startsWith("{") && key.endsWith("}")){
						resultjsonObject.put(strId, getGroupedAVPValue(key, dummyResponseMap, defaultValues, valueNodes));
					}else {
						if(isLiteral(key)){
							strValue = unquote(key);
						}else {
							strValue = dummyResponseMap.get(key);
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
					resultjsonObject.put(strId, getGroupedAVPValue(key, dummyResponseMap, defaultValues, valueNodes));
				} else{
					if(isLiteral(key)){
						strValue = unquote(key);
					}else {
						strValue = dummyResponseMap.get(key);
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

	public static class KeywordEvaluator {
		private Map<String, TranslationKeyword> requestKeywords;
		private Map<String, TranslationKeyword> responseKeywords;
		private Random random;

		public KeywordEvaluator(Map<String, TranslationKeyword> requestKeywords, Map<String, TranslationKeyword> responseKeywords, Random random) {
			this.requestKeywords = requestKeywords;
			this.responseKeywords = responseKeywords;
			this.random = random;
		}

		public String getKeywordValue(String strkeyword,TranslatorParams params,boolean isRequest,com.elitecore.core.commons.data.ValueProvider valueProvider){
			String value = null;
			String keywordName = getKeywordName(strkeyword);
			if(keywordName!=null ){
				if(TranslatorConstants.RANDOM.equals(keywordName)){
					value = String.valueOf(getRandom(strkeyword));
				}else {
					if(isRequest){
						TranslationKeyword tempKeyword = this.requestKeywords.get(keywordName);
						if(tempKeyword!=null){
							value = tempKeyword.getKeywordValue(params,strkeyword,isRequest,valueProvider);
						}
					}else {
						TranslationKeyword tempKeyword = this.responseKeywords.get(keywordName);
						if(tempKeyword!=null){
							value = tempKeyword.getKeywordValue(params,strkeyword,isRequest,valueProvider);
						}
					}	
				}
			}
			return value;
		}

		private String getKeywordName(String strKeyword){
			String keywordName = null;
			if(strKeyword!=null){
				int index = strKeyword.indexOf("}");
				if(index != -1){
					String tempString  = strKeyword.substring(0,index+1).trim();
					index  = tempString.indexOf(OPERATION_ARGUMENT_SEPARATOR);
					if(index != -1){
						keywordName = tempString.substring(0,index)+"}";
					}else {
						keywordName = tempString;
					}
				}	 
			}
			return keywordName;
		}

		private long getRandom(String strKeyword){
			String operatorArgument = getOperatorArgument(strKeyword);
			int byteArraySize = 4;
			if(operatorArgument!=null && operatorArgument.length()>0){
				try{
					int tempInt =  Integer.parseInt(operatorArgument);
					if(tempInt>0 && tempInt <=8){
						byteArraySize = tempInt;
					}
				}catch (Exception e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid Argument configured to use as Number of Bytes in creating Random, using default value : "+byteArraySize + "Reason :" +e);
				}
			}
			byte [] tempbyteArray = new byte[byteArraySize];
			random.nextBytes(tempbyteArray);
			return Utility.bytesToLong(tempbyteArray);
		}

		protected String getOperatorArgument(String strKeyword){

			String operatorArguments = null;
			int index = strKeyword.indexOf(OPERATION_ARGUMENT_SEPARATOR);

			if(index != -1){
				int braceIndex = strKeyword.lastIndexOf("}");
				if (braceIndex != -1){
					String tempString = strKeyword.substring(index+1,braceIndex);
					operatorArguments = tempString;
				}
			}

			return operatorArguments;
		}

		protected boolean isMultimodeEnabled (String value){
			String keywordName = getKeywordName(value);
			if (keywordName!=null && keywordName.equals(MULTIMODE_KEYWORD)){
				return true;
			}
			return false;
		}
		
		public boolean isKeyword(String val){
			if(val!=null && val.startsWith("${"))
				return true;
			return false;
		}
	}
}
