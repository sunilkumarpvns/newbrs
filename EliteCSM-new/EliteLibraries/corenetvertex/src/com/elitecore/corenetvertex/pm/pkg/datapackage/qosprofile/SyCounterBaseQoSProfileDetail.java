
package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.SyCounterBaseQuotaProfileDetail;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class SyCounterBaseQoSProfileDetail implements QoSProfileDetail {

	@Nullable private final SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail;
	@Nullable private final Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail;
	@Nullable private final List<PCCRule> pccRules;
	@Nullable private final List<ChargingRuleBaseName> chargingRuleBaseNames;
	private final String name;
	private final String packageName;
	private final QoSProfileAction action;
	private final String rejectCause;
	private final IPCANQoS sessionQoS;
	private final int fupLevel;
	private final boolean usageMonitoring;
	private final SliceInformation sliceInformation;
	private Integer orderNo;
	@Nullable private  String redirectURL;
	
	public SyCounterBaseQoSProfileDetail(String name, 
			String packageName, 
			QoSProfileAction action,
			String reason, 
			@Nullable SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail,
			@Nullable Map<String, QuotaProfileDetail> serviceToSyQuotaProfileDetail,
			int fupLevel, Integer orderNo,@Nullable String redirectURL) {
		this(name, packageName, action, reason, fupLevel, allServiceQuotaProfileDetail, serviceToSyQuotaProfileDetail, null, null, false, null, orderNo, redirectURL, null);
	}

	public SyCounterBaseQoSProfileDetail(String name, 
			String pkgName,
			QoSProfileAction action,
			String reason,
			int fupLevel,
			@Nullable SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail,
			@Nullable Map<String, QuotaProfileDetail> serviceToSyQuotaProfileDetail,
			IPCANQoS sessionQoS,
			List<PCCRule> pccRules, 
			boolean usageMonitoring, 
			SliceInformation sliceInformation, int orderNo,@Nullable String redirectURL, List<ChargingRuleBaseName> chargingRuleBaseNames) {
		
		this.name = name;
		this.packageName = pkgName;
		this.action = action;
		this.rejectCause = reason;
		this.fupLevel = fupLevel;
		this.allServiceQuotaProfileDetail = allServiceQuotaProfileDetail;
		this.serviceToQuotaProfileDetail = serviceToSyQuotaProfileDetail;
		this.sessionQoS = sessionQoS;
		this.pccRules = pccRules;
		this.usageMonitoring = usageMonitoring;
		this.sliceInformation = sliceInformation;
		this.orderNo = orderNo;
		this.redirectURL = redirectURL;
		this.chargingRuleBaseNames = chargingRuleBaseNames;
	}

	@Override
	public SyCounterBaseQuotaProfileDetail getAllServiceQuotaProfileDetail() {
		return allServiceQuotaProfileDetail;
	}
	
	@Override
	public Map<String, QuotaProfileDetail> getServiceToQuotaProfileDetail() {
		return serviceToQuotaProfileDetail;
	}


	@Override
	public String getLevel() {
		return fupLevel == 0 ? HSQ : FUP + CommonConstants.DASH + fupLevel;
	}

	@Override
	@Nullable
	public String getRedirectURL() {
		return redirectURL;
	}

	@Override
	public @Nullable String getQuotaProfileName() {
		if(allServiceQuotaProfileDetail != null) {
			return allServiceQuotaProfileDetail.getName();			
		} else {
			return null;
		}
	}
	
	@Override
	public @Nullable String getQuotaProfileId() {
		if(allServiceQuotaProfileDetail != null) {
			return allServiceQuotaProfileDetail.getQuotaProfileId();			
		} else {
			return null;
		}
	}

	
	@Override
	public IPCANQoS getSessionQoS() {
		return sessionQoS;
	}

	@Override
	public QoSProfileAction getAction() {
		return action;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getFUPLevel() {
		return fupLevel;
	}

	@Override
	public SliceInformation getSliceInformation() {
		return sliceInformation;
	}

	@Override
	public String getRejectCause() {
		return rejectCause;
	}
	
	@Override
	public String getPackageName() {
		return packageName;
	}

	@Override
	public boolean getUsageMonitoring() {
		return usageMonitoring;
	}
	
	@Override
	public List<PCCRule> getPCCRules() {
		return pccRules;
	}

	@Override
	public List<ChargingRuleBaseName> getChargingRuleBaseNames() {
		return chargingRuleBaseNames;
	}


	@Override
	public String getUniqueName() {
		return packageName + CommonConstants.HASH + name;
	}

	@Override
	public int getOrderNo() {
		return orderNo;
	}
	
	@Override
	public String toString() {
		return toString(QOS_PROFILE_DETAIL_DATA_TO_STRING_STYLE);
	}

	public String toString(ToStringStyle toStringStyle) {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this,
				toStringStyle).append("Level", (fupLevel == 0 ? "HSQ" : "fup level " + fupLevel))
				.append("Action", getAction());

		if (sessionQoS != null) {

			sessionQoS.toString(toStringBuilder);
		}
		toStringBuilder.append("Redirect URL", redirectURL);
		if(getAction() == QoSProfileAction.ACCEPT) {
			toStringBuilder.append("");
			if (Collectionz.isNullOrEmpty(pccRules) == false) {
				for (PCCRule pccRule : pccRules) {
					toStringBuilder.append("PCC Rule", pccRule);
				}
			}
		}

		toStringBuilder.append("");
		if (Collectionz.isNullOrEmpty(chargingRuleBaseNames) == false) {
			for (ChargingRuleBaseName chargingRuleBaseName : chargingRuleBaseNames) {
				toStringBuilder.append("Charging Rule Base Name", chargingRuleBaseName);
			}
		}

		return toStringBuilder.toString();
	}
}
