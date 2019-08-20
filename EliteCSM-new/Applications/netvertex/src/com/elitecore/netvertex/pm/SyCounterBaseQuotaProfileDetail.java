package com.elitecore.netvertex.pm;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Splitter;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.conf.SyCounterQuotaProfileConf;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.conf.SyCounterQuotaProfileConf.CouterDetail;
import com.elitecore.corenetvertex.util.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SyCounterBaseQuotaProfileDetail extends com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.SyCounterBaseQuotaProfileDetail {

	private static final Splitter COMMA_BASE_SPLITTER = Splitter.on(CommonConstants.COMMA).trimTokens();
	private final List<SyCounter> syCounters;

	public SyCounterBaseQuotaProfileDetail(String quotaProfileId, String name, String pkgName, String serviceId, String serviceName, int fupLevel,
			List<CouterDetail> counterDetails) {
		super(quotaProfileId, name, pkgName, serviceId, serviceName, fupLevel);

		this.syCounters = getSyCounters(counterDetails);
	}

	private List<SyCounter> getSyCounters(List<CouterDetail> counterDetails) {
		
		List<SyCounter> syCounters = new ArrayList<SyCounterBaseQuotaProfileDetail.SyCounter>();
		for (SyCounterQuotaProfileConf.CouterDetail counterDetail : counterDetails) {
			syCounters.add(new SyCounter(counterDetail.getKey(), COMMA_BASE_SPLITTER.split(counterDetail.getValue()), counterDetail.isOptional()));
		}
		return syCounters;
	}

	public boolean isUsageExceeded(PolicyContext policyContext) {

		for (int i = 0; i < syCounters.size(); i++) {
			if (syCounters.get(i).evaluate(policyContext) == false) {
				return true;
			}
		}
		return false;
	}

	public List<SyCounter> getSyCounters() {
		return syCounters;
	}

	public static class SyCounter implements Serializable{
		private final String name;
		private final String key;
		private final List<String> values;
		private final boolean isCounterRequired;

		public SyCounter(String name, List<String> values, boolean isCounterRequired) {
			super();
			this.name = name;
			this.key = PCRFKeyConstants.SY_COUNTER_PREFIX.val + name;
			this.values = values;
			this.isCounterRequired = isCounterRequired;
		}

		public String getName() {
			return name;
		}

		public String getKey() {
			return key;
		}

		public List<String> getValues() {
			return values;
		}

		public boolean isCounterRequired() {
			return isCounterRequired;
		}

		@Override
		public String toString() {
			return "SyCounter [name=" + name + ", key=" + key + ", values=" + values + ", isCounterRequired=" + isCounterRequired + "]";
		}

		public boolean evaluate(PolicyContext policyContext) {
			String value = policyContext.getPCRFResponse().getAttribute(key);

			if (value == null) {
				return isCounterRequired == false;
			} else {
				for (int counterValueIndex = 0; counterValueIndex < values.size(); counterValueIndex++) {
					if (value.equalsIgnoreCase(values.get(counterValueIndex))) {
						return true;
					}
				}
				return false;
			}
		}
	}
	
	@Override
	public String toString() {
		
		ToStringBuilder builder = new ToStringBuilder(this, QUOTA_PROFILE_DETAIL_TO_STRING_STYLE)
			.append(super.toString());
		
			if (Collectionz.isNullOrEmpty(syCounters) == false) {
				for (SyCounter counter : syCounters) {
					builder.append(counter);
				}
			} else {
				builder.append("No counters configured");
			}
		
		
		
		return builder.toString();
	}

}
