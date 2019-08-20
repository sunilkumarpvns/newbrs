
package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class FailedUMBaseQoSProfileDetail implements UMBaseQoSProfileDetail {

	@Nullable private final List<PCCRule> pccRules;
	private final List<ChargingRuleBaseName> chargingRuleBaseNames;
	private final String name;
	private final String packageName;
	private final QoSProfileAction action;
	private final String rejectCause;
	private final IPCANQoS sessionQoS;
	private final int fupLevel;
	private final boolean usageMonitoring;
	private final SliceInformation sliceInformation;
	private int orderNo;
	@Nullable private String redirectURL;
	
	public FailedUMBaseQoSProfileDetail(String name, 
			String packageName, 
			QoSProfileAction action,
			String reason,
			int fupLevel, int orderNo , @Nullable String redirectURL) {
		this.name = name;
		this.packageName = packageName;
		this.action = action;
		this.rejectCause = reason;
		this.fupLevel = fupLevel;
		this.orderNo = orderNo;
		this.sessionQoS = null;
		this.pccRules = null;
		this.chargingRuleBaseNames = null;
		this.sliceInformation = null;
		this.usageMonitoring = false;
		this.redirectURL = redirectURL;
	}

	public FailedUMBaseQoSProfileDetail(String name, String pkgName,
			QoSProfileAction action,String reason,
			int fupLevel,
			IPCANQoS sessionQoS,
			List<PCCRule> pccRules, 
			boolean usageMonitoring, 
			SliceInformation sliceInformation,@Nullable String redirectURL,List<ChargingRuleBaseName> chargingRuleBaseNames) {

		this.name = name;
		this.packageName = pkgName;
		this.action = action;
		this.rejectCause = reason;
		this.fupLevel = fupLevel;
		this.sessionQoS = sessionQoS;
		this.pccRules = pccRules;
		this.usageMonitoring = usageMonitoring;
		this.sliceInformation = sliceInformation;
		this.redirectURL = redirectURL;

		this.chargingRuleBaseNames = chargingRuleBaseNames;
	}

	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public List<PCCRule> getPccRules() {
		return pccRules;
	}

	public List<ChargingRuleBaseName> getChargingRuleBaseNames() {
		return chargingRuleBaseNames;
	}

	public String getName() {
		return name;
	}

	public String getPackageName() {
		return packageName;
	}

	public QoSProfileAction getAction() {
		return action;
	}

	public String getRejectCause() {
		return rejectCause;
	}

	public IPCANQoS getSessionQoS() {
		return sessionQoS;
	}

	public int getFUPLevel() {
		return fupLevel;
	}

	public boolean isUsageMonitoring() {
		return usageMonitoring;
	}

	public SliceInformation getSliceInformation() {
		return sliceInformation;
	}
	
	@Override
	public @Nullable String getQuotaProfileName() {
		return null;
	}
	
	@Override
	public @Nullable String getQuotaProfileId() {
			return null;
	}

	public boolean getUsageMonitoring() {
		return usageMonitoring;
	}

	public List<PCCRule> getPCCRules() {
		return this.pccRules;
	}

	@Override
	public String getUniqueName() {
		return packageName + CommonConstants.HASH + name;
	}

	@Override
	public QuotaProfileDetail getAllServiceQuotaProfileDetail() {
		return null;
	}

	@Override
	public Map<String, QuotaProfileDetail> getServiceToQuotaProfileDetail() {
		return null;
	}
	
	@Override
	public String toString() {

		return toString(QOS_PROFILE_DETAIL_DATA_TO_STRING_STYLE);
	}

	public String toString(ToStringStyle toStringStyle) {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this,
				toStringStyle).append("Level", (fupLevel ==0 ? "HSQ" : "fup level " + fupLevel))
				.append("QCI", sessionQoS.getQCI())
				.append("Priority Level", sessionQoS.getPriorityLevel())
				.append("Pre-emption capability", sessionQoS.getPreEmptionCapability())
				.append("Pre-emption vulnerability", sessionQoS.getPreEmptionVulnerability())
				.append("Action", getAction());

		if (sessionQoS.getAambrdlUnit() != null) {
			toStringBuilder.append("AAMBRDL", sessionQoS.getAAMBRDLInBytes() + " " + sessionQoS.getAambrdlUnit());
		}
		if (sessionQoS.getAambrulUnit() != null) {
			toStringBuilder.append("AAMBRUL", sessionQoS.getAAMBRULInBytes()+ " " + sessionQoS.getAambrulUnit());
		}
		if (sessionQoS.getMbrdlUnit() != null) {
			toStringBuilder.append("MBRDL", sessionQoS.getMBRDLInBytes()+ " " + sessionQoS.getMbrdlUnit());
		}
		if (sessionQoS.getMbrulUnit() != null) {
			toStringBuilder.append("MBRUL", sessionQoS.getMBRULInBytes()+ " " + sessionQoS.getMbrulUnit());
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

	@Override
	public String getLevel() {
		return fupLevel == 0 ? HSQ : FUP + CommonConstants.DASH + fupLevel;
	}

	@Nullable
	@Override
	public String getRedirectURL() {
		return redirectURL;
	}

	@Override
	public boolean isUsageRequired() {
		return false;
	}

	@Override
	public boolean isApplyOnUsageUnavailability() {
		return false;
	}

}
