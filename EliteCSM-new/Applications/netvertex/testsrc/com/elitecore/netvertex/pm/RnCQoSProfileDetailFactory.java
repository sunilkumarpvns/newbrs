package com.elitecore.netvertex.pm;


import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.IPCanQoSFactory;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.netvertex.pm.qos.rnc.RnCBaseQoSProfileDetail;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.elitecore.netvertex.pm.PCCRuleFactory.createPCCRuleWithRandomQoS;

public class RnCQoSProfileDetailFactory {


	private IPCANQoS ipcanQoS;
	private String reason;
	private RnCQoSProfileDetialBuilder builder;

	private RnCQoSProfileDetailFactory() {

	}

	public static RnCQoSProfileDetailFactory createQoSProfile() {
		return new RnCQoSProfileDetailFactory();
	}

	public RnCQoSProfileDetailFactory withSessionQoSEqualTo(IPCANQoS ipcanQoS) {
		this.ipcanQoS = IPCanQoSFactory.createSessionQoSHasEqualQoSTo(ipcanQoS);
		return this;
	}

	public RnCQoSProfileDetailFactory rejectAction(String reason) {
		this.reason = reason;
		return this;
	}

	public RnCQoSProfileDetailFactory withSessionQoSHigherThan(IPCANQoS higherBound) {
		this.ipcanQoS = IPCanQoSFactory.createSessionQoSHasHigherQoSThan(higherBound);
		return this;
	}

	public RnCQoSProfileDetailFactory withSessionQoSLowerThan(IPCANQoS lowerBound) {
		this.ipcanQoS = IPCanQoSFactory.createSessionQoSHasLowerQoSThan(lowerBound);
		return this;
	}

	public RnCQoSProfileDetialBuilder hasRnCQuota() {
		builder = new RnCQoSProfileDetialBuilder(this);
		return builder;
	}

	QoSProfileDetail build() {
		return builder.build();
	}


	public static class RnCQoSProfileDetialBuilder implements Builder{


		private List<RnCQuotaProfileDetail> quotaProfileDetails;
		private List<PCCRule> pccRules;
		private List<ChargingRuleBaseName> crbns;

		private int serviceIdentifier;
		private RnCQoSProfileDetailFactory qosProfileDetailBuilder;
		private double rate;

		private RnCQoSProfileDetialBuilder(RnCQoSProfileDetailFactory qosProfileDetailBuilder) {
			this.qosProfileDetailBuilder = qosProfileDetailBuilder;
		}

		public RnCQoSProfileDetialBuilder quotaProfileDetail(RnCQuotaProfileDetail quotaProfileDetail) {
			quotaProfileDetails = Arrays.asList(quotaProfileDetail);
			return this;
		}

		public RnCQoSProfileDetialBuilder quotaProfileDetail(List<RnCQuotaProfileDetail> quotaProfileDetails) {
			this.quotaProfileDetails = quotaProfileDetails;
			return this;
		}

		public RnCQoSProfileDetialBuilder pccRules(List<PCCRule> pccRules) {
			this.pccRules = pccRules;
			return this;
		}

		public RnCQoSProfileDetialBuilder withCRBN(List<ChargingRuleBaseName> chargingRuleBaseNames) {
			this.crbns = chargingRuleBaseNames;
			return this;
		}

		public RnCQoSProfileDetailFactory forEachServicesHasPCCRule() {

			if(Collectionz.isNullOrEmpty(quotaProfileDetails)) {
				return qosProfileDetailBuilder;
			}

			RatingGroup ratingGroup = new RatingGroup("1", "defaultRatingGroup", "", 1);
			String serviceDataFlow = "allow tcp from any:0 to any:0";



			List<PCCRule> pccRules = new ArrayList<PCCRule>();

			for(QuotaProfileDetail quotaProfileDetail : quotaProfileDetails){

				String serviceId = quotaProfileDetail.getServiceId();
				DataServiceType dataServiceType = new DataServiceType(serviceId,
						serviceId,
						serviceIdentifier++,
						Arrays.asList(serviceDataFlow),
						Arrays.asList(ratingGroup));


				PCCRule pccRule = createPCCRuleWithRandomQoS().
						withServiceType(dataServiceType).
						//withServiceTypeId(serviceType.getServiceTypeID()).
								withRatingGroup(dataServiceType.getRatingGroupList().get(0)).
								withServiceDataFlows(dataServiceType.getServiceDataFlowList()).
								build();

				pccRules.add(pccRule);
			}

			this.pccRules = pccRules;
			return qosProfileDetailBuilder;
		}


		public RnCQoSProfileDetialBuilder noPCC() {
			return this;
		}

		public RnCBaseQoSProfileDetail build() throws RuntimeException {

			QoSProfileAction action = QoSProfileAction.ACCEPT;

			if(qosProfileDetailBuilder.reason != null){
				action = QoSProfileAction.REJECT;


			}


			if(qosProfileDetailBuilder.ipcanQoS == null && action != QoSProfileAction.REJECT) {
				qosProfileDetailBuilder.ipcanQoS = IPCanQoSFactory.randomQoS();
			}


			Map<String, QuotaProfileDetail> serviceToQuotaProfileDetails = null;
			RnCQuotaProfileDetail quotaProfileDetail = null;
			if(quotaProfileDetails != null) {
				serviceToQuotaProfileDetails = quotaProfileDetails.stream().collect(Collectors.toMap(QuotaProfileDetail::getServiceId, Function.identity()));

				quotaProfileDetail = (RnCQuotaProfileDetail) serviceToQuotaProfileDetails.remove(CommonConstants.ALL_SERVICE_ID);
			}


			if(action == QoSProfileAction.REJECT) {



				return new RnCBaseQoSProfileDetail("test",
						"test",
						qosProfileDetailBuilder.reason,
						quotaProfileDetail,
						serviceToQuotaProfileDetails,
						0,
						1,
						false,
						(String)null);

			} else {
				return new RnCBaseQoSProfileDetail("test",
						"test",
						0,
						quotaProfileDetail,
						serviceToQuotaProfileDetails,
						qosProfileDetailBuilder.ipcanQoS,
						pccRules,
						1,
						true,
						(String)null,
						crbns);
			}

		}

	}


	public static interface  Builder {
		QoSProfileDetail build();
	}




}
