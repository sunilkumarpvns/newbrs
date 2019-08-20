package com.elitecore.diameterapi.core.translator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.PolicyDataRegistrationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.diameterapi.diameter.translator.CopyPacketTranslator;

public final class TranslationAgent implements ITranslationAgent {
	private static final String MODULE = "TRNSLTN-AGNT";
	private Map<String,TranslatorPolicyData> translaterPolicyDataMap;
	private Map<String, Translator>translatersMap;
	
	private Map<String, CopyPacketTranslator> copyPacketTranslatorMap;
	
	private static TranslationAgent translationAgent=null;
	
	private TranslationAgent(){
		translaterPolicyDataMap = new HashMap<String,TranslatorPolicyData>();
		translatersMap = new HashMap<String, Translator>();
		copyPacketTranslatorMap = new ConcurrentHashMap<String, CopyPacketTranslator>();
	}
	
	public static TranslationAgent getInstance(){
		if(translationAgent == null){
			translationAgent = new TranslationAgent();
		}
		return translationAgent;
	}
	
	public void registerPolicyData(TranslatorPolicyData policyData) throws PolicyDataRegistrationFailedException{
		if(policyData==null){
			return;
		}
		
			try {
				Translator translator = translatersMap.get(policyData.getFromTranslatorId()+policyData.getToTranslatorId());
				if(translator == null){
					throw new PolicyDataRegistrationFailedException("The requiered translator is not registered.");
				}
				translator.init(policyData);
				LogManager.getLogger().info(MODULE, "Translation Configuration registered for: " + policyData.getName());
				
			} catch (InitializationFailedException e) {
				throw new PolicyDataRegistrationFailedException(e.getMessage());
			}
			translaterPolicyDataMap.put(policyData.getName(), policyData);
		}
	
	public void registerTranslator(Translator translater){
		if(translater == null){
			return;
		}
		String translatorId = translater.getFromId() + translater.getToId();
		if(!translatersMap.containsKey(translatorId)){
			translatersMap.put(translatorId, translater);
		}else{
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Translater with ID: "+ translatorId +" already registered.");
		}
	}
	
	public void registerCopyPacketTranslator(CopyPacketTranslator translator){
		
		String translatorName = translator.getName();

		if(copyPacketTranslatorMap.containsKey(translatorName)) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Copy Packet Translation Configuration: " + translatorName + 
						" is already registered, Over-Writing Translation Configuration.");
			}
		}
		try {
			translator.init();
			copyPacketTranslatorMap.put(translatorName, translator);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Registered Copy Packet Translation Configuration: "+ translatorName);
			}
		} catch (InitializationFailedException e) {

			LogManager.getLogger().trace(e.getCause());
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Copy Packet Translation Configuration: " + translatorName + 
						" registration failed, Reason: " + e.getMessage());
			}
		}
	}
	
	@Override
	public void translate(String policyId,TranslatorParams params,boolean isRequest) throws TranslationFailedException{
		
		// policyId is Translation Policy Name
		CopyPacketTranslator copyPacketTranslator = copyPacketTranslatorMap.get(policyId);
		if(copyPacketTranslator != null) {
			
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Applying Packet Translation using Policy: " + policyId);
			}
			if(isRequest){
				copyPacketTranslator.translateRequest(params);
				copyPacketTranslator.postTranslateRequest(params);
			} else {
				copyPacketTranslator.translateResponse(params);
				copyPacketTranslator.postTranslateResponse(params);
			}
			return;
		}
		TranslatorPolicyData policy = translaterPolicyDataMap.get(policyId);
		if(policy == null){
			throw new TranslationFailedException("Translator Policy: "+ policyId + " is not registered");		
		}
		String translatorId = policy.getFromTranslatorId() + policy.getToTranslatorId();
		Translator translator = translatersMap.get(translatorId);
		if(translator == null){
			throw new TranslationFailedException("Translator for From : "+ policy.getFromTranslatorId() + " To : "  + policy.getToTranslatorId() + " is not registered");
		}else{
			try{
				if(isRequest){
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "Translating Request Using policy: " + policy.getName());

					translator.translateRequest(policy.getTransMapConfId(),params);
					translator.postTranslateRequest(policy.getTransMapConfId(), params);

				}else{
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "Translating Response Using policy: " + policy.getName());

					translator.translateResponse(policy.getTransMapConfId(),params);
					translator.postTranslateResponse(policy.getTransMapConfId(), params);
				}
			}catch(Exception e){
				throw new TranslationFailedException(e);
			}
		}
	}
	
	@Override
	public boolean isExists(String transMappName){
		if (transMappName == null)
			return false;
		boolean isExists = copyPacketTranslatorMap.containsKey(transMappName);
		if(isExists){
			return isExists;
		}
		return translaterPolicyDataMap.containsKey(transMappName);
	}
	
	public Map<String, String> getDummyResponseMap(String translationMapping) {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Fetching Dummy Mappings for Translation Configuration: " + translationMapping);
		}
		CopyPacketTranslator copyPacketTranslator = copyPacketTranslatorMap.get(translationMapping);
		if(copyPacketTranslator != null){
			return copyPacketTranslator.getDummyMappings();
		}
		TranslatorPolicyData data = translaterPolicyDataMap.get(translationMapping);
		if (data != null) {
			return data.getDummyResponseMap();
		}
		return null;
	}
	
}