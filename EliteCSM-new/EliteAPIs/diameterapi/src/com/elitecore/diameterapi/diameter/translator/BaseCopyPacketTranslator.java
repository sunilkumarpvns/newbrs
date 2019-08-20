package com.elitecore.diameterapi.diameter.translator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.manager.scripts.ScriptsExecutor;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.DummyResponseDetail;
import com.elitecore.diameterapi.diameter.translator.data.CopyPacketTranslatorPolicyData;
import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketTranslationConfigDataImpl;

public abstract class BaseCopyPacketTranslator<P, A, G extends A> implements CopyPacketTranslator {

	private static final String MODULE = "BASE-CPY-PKT-TRNSLTR";
	private Map<String, CopyPacketTranslatorPolicy<P, A, G>> policies;
	private Map<String, String> dummyMappings;
	private CopyPacketTranslatorPolicyData policyData;
	private ScriptsExecutor executor;
	private Class<?> translationScriptType;

	public BaseCopyPacketTranslator(CopyPacketTranslatorPolicyData policyData, 
			ScriptsExecutor executor, Class<?> translationScriptType) {
		this.policyData = policyData;
		this.executor = executor;
		this.translationScriptType = translationScriptType;
	}

	@Override
	public String getName(){
		return policyData.getName();
	}
	
	@Override
	public void init() throws InitializationFailedException {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Copy Packet Translation Policy: \n" + policyData);
		}
		policies = createTranslationPolicies(policyData);
		dummyMappings = createDummyMappings(policyData);
	}

	private Map<String, String>  createDummyMappings(CopyPacketTranslatorPolicyData policyData) {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Initializing Dummy Mappings for Copy Packet Configuration: " + getName());
		}
		if(Collectionz.isNullOrEmpty(policyData.getDummyResponseList())){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "No Dummy Mappings found for Copy Packet Configuration: " + getName());
			}
			return Collections.emptyMap();
		}
		Map<String, String> dummyMappings = new LinkedHashMap<String, String>();
		for(DummyResponseDetail dummyResponseDetail : policyData.getDummyResponseList()){
			if (Strings.isNullOrBlank(dummyResponseDetail.getOutfield()) || 
					dummyResponseDetail.getValue() == null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Skipping Invalid Dummy Mapping, Key: " + dummyResponseDetail.getOutfield() + 
							", Value: " + dummyResponseDetail.getValue());
				}
			}
			dummyMappings.put(dummyResponseDetail.getOutfield(), dummyResponseDetail.getValue());
		}
		return dummyMappings;
	}

	private Map<String, CopyPacketTranslatorPolicy<P, A, G>> createTranslationPolicies(
			CopyPacketTranslatorPolicyData policyData)
			throws InitializationFailedException {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Initializing Copy Packet Mapping Policies for Copy Packet Configuration: " + getName());
		}
		List<CopyPacketTranslationConfigDataImpl> configDataList = policyData.getTranslationConfigDataList();

		if(Collectionz.isNullOrEmpty(configDataList)){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "No Copy Packet Mapping Policy found for Copy Packet Configuration: " + getName());
			}
			return Collections.emptyMap();
		}
		Map<String, CopyPacketTranslatorPolicy<P, A, G>> policyMap = new LinkedHashMap<String, CopyPacketTranslatorPolicy<P, A, G>>();
		
		for(CopyPacketTranslationConfigDataImpl policyConfigData : configDataList){

			CopyPacketTranslatorPolicy<P, A, G> policy = createCopyPacketTranslationPolicy();
			policy.init(policyConfigData);
			policyMap.put(policyConfigData.getMappingName(), policy);
			
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Copy Packet Mapping Policy: " + policyConfigData.getMappingName() +  
						" initialized for for Copy Packet Configuration: " + getName());
			}
		}
		return policyMap;
	}

	protected abstract CopyPacketTranslatorPolicy<P, A, G> createCopyPacketTranslationPolicy();

	protected abstract void copyRequest(TranslatorParams params) throws CloneNotSupportedException;
	
	protected abstract void copyResponse(TranslatorParams params) throws CloneNotSupportedException;
	
	@Override
	public void translateRequest(TranslatorParams params)
			throws TranslationFailedException {
		
		try {
			copyRequest(params);
		} catch (CloneNotSupportedException e) {
			throw new TranslationFailedException(e);
		}
		
		for(Map.Entry<String, CopyPacketTranslatorPolicy<P, A, G>> policy : policies.entrySet()) {
			
			if(policy.getValue().isApplicable(params, true)){
				
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Applying Copy Packet Mapping: " + policy.getKey());
				}
				policy.getValue().applyRequestOperations(params);
				params.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, policy.getKey());
				break;
			}
		}
	}

	@Override
	public void translateResponse(TranslatorParams params)
			throws TranslationFailedException {

		try {
			copyResponse(params);
		} catch (CloneNotSupportedException e) {
			throw new TranslationFailedException(e);
		}
		
		CopyPacketTranslatorPolicy<P, A, G> selectedPolicy = null;
		
		String mappingName = (String)params.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING);

		if(mappingName != null){
			
			selectedPolicy = policies.get(mappingName);
			if(selectedPolicy == null){
				throw new TranslationFailedException("Invalid Copy Packet Mapping: " + mappingName + 
						" is selected during Request Translation, This mapping does not exist.");
			}
		} else {
			
			for(Map.Entry<String, CopyPacketTranslatorPolicy<P, A, G>> policy : policies.entrySet()) {
				
				if(policy.getValue().isApplicable(params, false)){
					selectedPolicy = policy.getValue();
					mappingName = policy.getKey();
					break;
				}
			}
		}
		if(selectedPolicy != null) {
			
			if(selectedPolicy.isDummyEnabled()) {
				applyDummyResponse(params);
			}
			
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Applying Copy Packet Mapping: " + mappingName);
			}
			selectedPolicy.applyResponseOperations(params);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Response Translation failed, Reason: No Suitable Mapping Found for Packet");
			}
		}
	}
	
	protected abstract void applyDummyResponse(TranslatorParams params);

	@Override
	public final void postTranslateRequest(TranslatorParams params) {
		
		if(Strings.isNullOrBlank(policyData.getScript())){
			return;
		}
		try {
			executor.execute(
					policyData.getScript(), translationScriptType, "requestTranslationExtension", 
					new Class<?>[]{TranslatorParams.class}, 
					new Object[]{params});

		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error in executing \"requestTranslationExtension\" method of translation mapping script: " + 
						policyData.getScript() + ". Reason: " + e.getMessage());
			}
		}
	}
	
	@Override
	public final void postTranslateResponse(TranslatorParams params) {

		if(Strings.isNullOrBlank(policyData.getScript())){
			return;
		}
		try {
			executor.execute(
					policyData.getScript(), translationScriptType, "responseTranslationExtension", 
					new Class<?>[]{TranslatorParams.class}, 
					new Object[]{params});

		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error in executing \"responseTranslationExtension\" method of translation mapping script: " +
						policyData.getScript() + ". Reason: " + e.getMessage());
			}
		}
	}

	@Override
	public Map<String, String> getDummyMappings() {
		return dummyMappings;
	}

}
