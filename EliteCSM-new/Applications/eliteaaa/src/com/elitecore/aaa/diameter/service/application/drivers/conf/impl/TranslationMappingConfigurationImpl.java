package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.diameter.service.application.drivers.conf.TranslationMappingConfiguration;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.diameterapi.core.translator.policy.data.impl.DummyResponseDetail;
import com.elitecore.diameterapi.core.translator.policy.impl.TranslationMappingConfigurationData;


@ReadOrder(order = { "translationMappingConfigurable"})
public class TranslationMappingConfigurationImpl extends CompositeConfigurable implements TranslationMappingConfiguration{

	private final static String MODULE = "TRANSLATION-MAPPING-CNF-IMPL";	
	@Configuration private TranslationMappingConfigurable translationMappingConfigurable;
	private Map<String, TranslatorPolicyData> translatorPolicyDataMap;

	@Deprecated
	public TranslationMappingConfigurationImpl(ServerContext serverContext) {
		this.translatorPolicyDataMap = new HashMap<String, TranslatorPolicyData>();
	}
	public TranslationMappingConfigurationImpl() {

		this.translatorPolicyDataMap = new HashMap<String, TranslatorPolicyData>();
	}


	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println();
		out.println(" -- Translation Mapping Configuration -- ");
		out.println();		
		for(Entry<String, TranslatorPolicyData> entry: translatorPolicyDataMap.entrySet()){
			out.println(entry.getValue());
		}		
		out.println("    ");
		out.close();
		return stringBuffer.toString();
		 }


	@PostRead
	public void postReadProcessing(){
		List<TranslationMappingConfigurationData> translationMappingList = translationMappingConfigurable.getTranslationMappingList();
		TranslationMappingConfigurationData translationMappingConfigurationData;
		HashMap<String, TranslatorPolicyData> tempHashMap = new HashMap<String, TranslatorPolicyData>();
		if(translationMappingList!=null){
			int size = translationMappingList.size();
			if(size >0){
				for(int i = 0;i<size;i++){
					translationMappingConfigurationData = translationMappingList.get(i);
					List<DummyResponseDetail> dummyResponseList = translationMappingConfigurationData.getTranslationPolicyDataObj().getDummyResposeList();
					if(dummyResponseList != null){
						int noOfDummyRespose = dummyResponseList.size();
						DummyResponseDetail dummyResponseDetail;
						HashMap<String,String> dummyResposeMap = new HashMap<String, String>();
						for(int j =0;j<noOfDummyRespose;j++){
							dummyResponseDetail = dummyResponseList.get(j);
							if(dummyResponseDetail.getOutfield() != null){
								dummyResposeMap.put(dummyResponseDetail.getOutfield(), dummyResponseDetail.getValue());	

							}
						}
						translationMappingConfigurationData.getTranslationPolicyDataObj().setDummyResponseMap(dummyResposeMap);
					}
					if(translationMappingConfigurationData.getTranslationPolicyDataObj().getName() != null && translationMappingConfigurationData.getTranslationPolicyDataObj().getName().length() >0){
						tempHashMap.put(translationMappingConfigurationData.getTranslationPolicyDataObj().getTransMapConfId(), translationMappingConfigurationData.getTranslationPolicyDataObj());	
					}
				}
				translatorPolicyDataMap = getValidTranslatorPolicyDataMap(tempHashMap);
			}
		}
	
	}

	public Map<String, TranslatorPolicyData> getTranslatorPolicyDataMap(){
		return translatorPolicyDataMap;
	}

	private Map<String, TranslatorPolicyData> getValidTranslatorPolicyDataMap(HashMap<String, TranslatorPolicyData> localTranslatorPolicyDataMap) {

		Map<String, TranslatorPolicyData> validTranslatorPolicyDataMap = new HashMap<String, TranslatorPolicyData>();
		String tempBaseTranlationId;
		TranslatorPolicyData tempTranslatorPolicyData;
		for(Entry<String,TranslatorPolicyData> entry: localTranslatorPolicyDataMap.entrySet()){
			tempTranslatorPolicyData = entry.getValue();
			tempBaseTranlationId = tempTranslatorPolicyData.getBaseTranslationMappingId();
			if(tempBaseTranlationId==null || tempBaseTranlationId.equals("null")){
				validTranslatorPolicyDataMap.put(tempTranslatorPolicyData.getName(), tempTranslatorPolicyData);
			}else {
				if(isValidTranslatorPolicyData(tempTranslatorPolicyData,localTranslatorPolicyDataMap)){
					LogManager.getLogger().error(MODULE, "Translation Mapping "+tempTranslatorPolicyData.getName()+" is considered as invalid, Reason: Recursive Mapping Configuration detected");
				}else {
					validTranslatorPolicyDataMap.put(tempTranslatorPolicyData.getName(),tempTranslatorPolicyData);
				}
			}
		}
		return validTranslatorPolicyDataMap;
	}

	private boolean isValidTranslatorPolicyData(TranslatorPolicyData tempTranslatorPolicyData,HashMap<String, TranslatorPolicyData> localTranslatorPolicyDataMap) {
		boolean isRecursive = false;
		List<String> baseTranslationIdList = new ArrayList<String>();
		baseTranslationIdList.add(tempTranslatorPolicyData.getBaseTranslationMappingId());
		if(!isRecursiveMapping(tempTranslatorPolicyData,baseTranslationIdList,localTranslatorPolicyDataMap)){
			isRecursive = true;
		}
		return isRecursive;
	}


	private boolean isRecursiveMapping(TranslatorPolicyData tempTranslatorPolicyData,List<String> baseTranslationIdList,HashMap<String, TranslatorPolicyData> localTranslatorPolicyDataMap) {
		TranslatorPolicyData baseTranslatorPolicyData = localTranslatorPolicyDataMap.get(tempTranslatorPolicyData.getBaseTranslationMappingId());
		if(baseTranslatorPolicyData==null || baseTranslatorPolicyData.getBaseTranslationMappingId()==null){
			return true;
		}else {
			if(baseTranslationIdList.contains(baseTranslatorPolicyData.getBaseTranslationMappingId())){
				return false;
			}else {
				baseTranslationIdList.add(baseTranslatorPolicyData.getBaseTranslationMappingId());
				return isRecursiveMapping(baseTranslatorPolicyData, baseTranslationIdList, localTranslatorPolicyDataMap);
			}
		}
	}

	@Override
	public TranslatorPolicyData getTranslatorPolicyData(String transMappingConfId) {
		return translatorPolicyDataMap.get(transMappingConfId);
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		postReadProcessing();
	}
	@Override
	public boolean isEligible(
			Class<? extends com.elitecore.core.commons.config.core.Configurable> configurableClass) {
		// TODO Auto-generated method stub
		return false;
	}

}	

