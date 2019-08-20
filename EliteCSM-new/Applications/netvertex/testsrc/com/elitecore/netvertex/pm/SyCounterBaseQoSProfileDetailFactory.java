package com.elitecore.netvertex.pm;


import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.IPCanQoSFactory;
import com.elitecore.corenetvertex.pm.pkg.*;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;

import java.util.*;

import static com.elitecore.corenetvertex.pm.factory.PCCRuleFactory.createPCCRuleWithRandomQoS;

public class SyCounterBaseQoSProfileDetailFactory {
	
	
	private IPCANQoS ipcanQoS;
	private String reason;
	private List<ChargingRuleBaseName> crbns;
	private List<SyCounterBaseQuotaProfileDetail> quotaProfileDetails;
	private List<PCCRule> pccRules;

	private SyCounterBaseQoSProfileDetailFactory() {
		
	}

	public static SyCounterBaseQoSProfileDetailFactory create() {
		return new SyCounterBaseQoSProfileDetailFactory();
	}

	public SyCounterBaseQoSProfileDetailFactory quotaProfileDetail(SyCounterBaseQuotaProfileDetail... quotaProfileDetails) {

		if(quotaProfileDetails == null) {
			this.quotaProfileDetails = new ArrayList<SyCounterBaseQuotaProfileDetail>();
		}

		for(SyCounterBaseQuotaProfileDetail quotaProfileDetail : quotaProfileDetails) {

			this.quotaProfileDetails.add(quotaProfileDetail);
		}

		return this;
	}

	public SyCounterBaseQoSProfileDetailFactory addQuotaProfileDetail(List<SyCounterBaseQuotaProfileDetail> quotaProfileDetail) {

		if(quotaProfileDetails == null) {
			this.quotaProfileDetails = new ArrayList<SyCounterBaseQuotaProfileDetail>();
		}

		quotaProfileDetails.addAll(quotaProfileDetail);

		return this;
	}
	
	public SyCounterBaseQoSProfileDetailFactory addQuotaProfileDetail(SyCounterBaseQuotaProfileDetail quotaProfileDetail) {
		return addQuotaProfileDetail(Arrays.asList(quotaProfileDetail));
	}

	
	public SyCounterBaseQoSProfileDetailFactory withRejectAction(String reason) {
		this.reason = reason;
		return this;
	}

    public SyCounterBaseQoSProfileDetailFactory withRandomQoS() {
        ipcanQoS = IPCanQoSFactory.randomQoS();
        return this;
    }

    public SyCounterBaseQoSProfileDetailFactory hasMandatoryCounterOf(String serviceId) {
		String keyAndValue = UUID.randomUUID().toString();
        return addQuotaProfileDetail(SyCounterQuotaProfileDetailFactory.create(serviceId).mandatoryCounter(keyAndValue, keyAndValue).build());
    }

	public SyCounterBaseQoSProfileDetailFactory hasOptionalCounterOf(String serviceId) {
		String keyAndValue = UUID.randomUUID().toString();
		return addQuotaProfileDetail(SyCounterQuotaProfileDetailFactory.create(serviceId).optionalCounter(keyAndValue, keyAndValue).build());
	}

	public void withCRBN(ChargingRuleBaseName crbn) {
		if (crbn != null) {
			this.crbns = new ArrayList<ChargingRuleBaseName>();
		}
		this.crbns.add(crbn);
	}

	public SyCounterBaseQoSProfileDetailFactory pccRules(List<PCCRule> pccRules) {
		this.pccRules = pccRules;
		return this;
	}

	public SyCounterBaseQoSProfileDetailFactory addPCCRules(List<PCCRule> pccRules) {
		if(this.pccRules == null) {
			this.pccRules = new ArrayList<PCCRule>();
		}
		this.pccRules.addAll(pccRules);
		return this;
	}

	public SyCounterBaseQoSProfileDetailFactory forEachServicesHasPCCRule() {

		if(Collectionz.isNullOrEmpty(quotaProfileDetails)) {
			return this;
		}

		RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);


		int serviceIdentifier = new Random().nextInt(10000);


		List<PCCRule> pccRules = new ArrayList<PCCRule>();

		for(QuotaProfileDetail quotaProfileDetail : quotaProfileDetails){

			String serviceId = quotaProfileDetail.getServiceId();
			DataServiceType dataServiceType = new DataServiceType(serviceId,
					serviceId,
					serviceIdentifier++,
					Arrays.asList("allow tcp from any to any"),
					Arrays.asList(ratingGroup));


			PCCRule pccRule = createPCCRuleWithRandomQoS().
					withServiceType(dataServiceType).
					withRatingGroup(dataServiceType.getRatingGroupList().get(0)).
					withServiceDataFlows(dataServiceType.getServiceDataFlowList()).
					build();

			pccRules.add(pccRule);
		}

		this.pccRules = pccRules;
		return this;
	}


	private SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail() {
		for(SyCounterBaseQuotaProfileDetail quotaProfileDetail : quotaProfileDetails){
			if(quotaProfileDetail.getServiceId().equals(CommonConstants.ALL_SERVICE_ID)) {
				return quotaProfileDetail;
			}
		}
		return null;
	}

	private Map<String, QuotaProfileDetail> otherServiceQuotaProfileDetail() {

		Map<String, QuotaProfileDetail> otherServiceQuotaProfileDetail = new HashMap<String, QuotaProfileDetail>();
		for(SyCounterBaseQuotaProfileDetail quotaProfileDetail : quotaProfileDetails){
			if(quotaProfileDetail.getServiceId().equals(CommonConstants.ALL_SERVICE_ID) == false) {
				otherServiceQuotaProfileDetail.put(quotaProfileDetail.getServiceId(), quotaProfileDetail);
			}
		}
		return otherServiceQuotaProfileDetail;
	}
	
	public SyCounterBaseQoSProfileDetail build() throws RuntimeException {
		
		
		QoSProfileAction action = QoSProfileAction.ACCEPT;
		
		if(reason != null){
			action = QoSProfileAction.REJECT;
		}
		
		if (action != QoSProfileAction.REJECT) {
			if(ipcanQoS == null) {
				ipcanQoS = IPCanQoSFactory.randomQoS();
			}

            return new SyCounterBaseQoSProfileDetail(UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    action,
                    reason,
                    0,
                    allServiceQuotaProfileDetail(),
                    otherServiceQuotaProfileDetail(),
                    ipcanQoS,
                    pccRules,
                    false,
                    null,
                    0,
                    null,
                    crbns);
		} else {

            return new SyCounterBaseQoSProfileDetail(UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    action,
                    reason,
                    allServiceQuotaProfileDetail(),
                    otherServiceQuotaProfileDetail(),
                    0,
                    0,
                    null);

        }
		 

	}

}
