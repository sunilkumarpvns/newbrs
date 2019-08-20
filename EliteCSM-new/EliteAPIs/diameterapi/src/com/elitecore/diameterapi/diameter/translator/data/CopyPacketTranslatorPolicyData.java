package com.elitecore.diameterapi.diameter.translator.data;

import java.util.List;

import com.elitecore.diameterapi.core.translator.policy.data.impl.DummyResponseDetail;
import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketTranslationConfigDataImpl;

public interface CopyPacketTranslatorPolicyData {
	
	public String getCopyPacketMapConfId();
	
	public String getName();
	
	public String getFromTranslatorId();
	
	public String getToTranslatorId();
	
	public List<CopyPacketTranslationConfigDataImpl> getTranslationConfigDataList();
	
	public List<DummyResponseDetail> getDummyResponseList();
	public String getScript();
}
