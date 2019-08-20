package com.elitecore.netvertex.core.util;

import static com.elitecore.commons.base.Collectionz.newArrayList;

import java.util.*;

import javax.annotation.Nonnull;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.netvertex.pm.SyCounterBaseQuotaProfileDetail;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;

public class PCRFResponseBuilder {

	@Nonnull private final PCRFResponseImpl pcrfResponse;
	@Nonnull private final Random random;
	
	public PCRFResponseBuilder() {
		pcrfResponse = new PCRFResponseImpl();
		random = new Random();
	}
	
	
	public PCRFResponseBuilder addSubscriberIdentity(@Nonnull String subscriberIdentity){
		addAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentity);
		return this;
	}
	
	public PCRFResponseBuilder addCoreSessionID(@Nonnull String subscriberIdentity) {
		addAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val, subscriberIdentity);
		return this;
	}
	
	public PCRFResponseBuilder addAttribute(@Nonnull String attribute, @Nonnull String val) {
		pcrfResponse.setAttribute(attribute, val);
		return this;
	}
	
	public PCRFResponseBuilder addAttribute(@Nonnull PCRFKeyConstants attribute, @Nonnull String value) {
		addAttribute(attribute.val,value);
		return this;
	}
	
	public PCRFResponseBuilder addSessionType(@Nonnull SessionTypeConstant sessionType) {
		addAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, sessionType.val);
		return this;
	} 
	
	public PCRFResponseBuilder addSessionID(String sessionID) {
		addAttribute(PCRFKeyConstants.CS_SESSION_ID.val, sessionID);
		return this;
	}
	
	
	public PCRFResponse build(){
		return pcrfResponse;
	}	
	
	public PCRFResponseBuilder setInstallablePCCRules(String... string) {
		List<PCCRule> pccRules = newArrayList();
		for (String string2 : string) {
			
			pccRules.add(new com.elitecore.corenetvertex.pm.pkg.PCCRuleImpl.PCCRuleBuilder(Integer.toString(random.nextInt()), string2).build());
			
		}
		return setInstallablePCCRules(pccRules);
	}

	
	public PCRFResponseBuilder setInstallablePCCRules(PCCRule... pccRules) {
		List<PCCRule> pccRuleLst = newArrayList();
		
		for (PCCRule pccRule2 : pccRules) {
			pccRuleLst.add(pccRule2);
		}
		
		return setInstallablePCCRules(pccRuleLst);
		
	}
	
	public PCRFResponseBuilder setInstallablePCCRules(List<PCCRule> pccRule) {
		pcrfResponse.setInstallablePCCRules(pccRule);
		return this;
	}
	
	public PCRFResponseBuilder setInstallablePCCRules(Collection<PCCRule> pccRule) {
		List<PCCRule> pccRules = newArrayList();
		pccRules.addAll(pccRules);
		return setInstallablePCCRules(pccRules);
	}

	public PCRFResponseBuilder addCounterNotMatchWith(SyCounterBaseQuotaProfileDetail syCounterBaseQuotaProfileDetail) {
		for(SyCounterBaseQuotaProfileDetail.SyCounter syCounter : syCounterBaseQuotaProfileDetail.getSyCounters()) {
			addAttribute(syCounter.getKey(), createUnMatchedValue(syCounter.getValues()));
		}

		return this;
	}

	private String createUnMatchedValue(List<String> values) {

		boolean fromValues = false;
		String generatedValue;
		do {
			generatedValue = UUID.randomUUID().toString();

			for(String val : values) {
				if(val.equalsIgnoreCase(generatedValue)) {
					fromValues = true;
					break;
				}
			}
		} while (fromValues);

		return generatedValue;
	}

	public PCRFResponseBuilder addCounterMatchWith(SyCounterBaseQuotaProfileDetail syCounterBaseQuotaProfileDetail) {
		for(SyCounterBaseQuotaProfileDetail.SyCounter syCounter : syCounterBaseQuotaProfileDetail.getSyCounters()) {
			addAttribute(syCounter.getKey(), syCounter.getValues().get(0));
		}

		return this;

	}

	public PCRFResponseBuilder addCounterNotMatchWith(Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail) {
		for(QuotaProfileDetail quotaProfileDetail: serviceToQuotaProfileDetail.values()) {
			addCounterNotMatchWith((SyCounterBaseQuotaProfileDetail) quotaProfileDetail);
		}

		return this;
	}

	public PCRFResponseBuilder addCounterMatchWith(Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail) {

		for(QuotaProfileDetail quotaProfileDetail: serviceToQuotaProfileDetail.values()) {
			addCounterMatchWith((SyCounterBaseQuotaProfileDetail) quotaProfileDetail);
		}

		return this;
	}
}
