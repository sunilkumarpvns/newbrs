package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.acesstime.TimeSlot;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import org.apache.commons.lang.SystemUtils;

public class QoSProfile implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final ToStringStyle QOS_PROFILE_DATA_TO_STRING_STYLE = new QoSProfileDataToString();
	
	private final String name;
	private final String packageName;
	private final QoSProfileDetail hsqLevelQoSDetail;
	private final int duration;
	private String packageId;
	@Nullable private final QuotaProfile quotaProfile;
	@Nullable private final List<QoSProfileDetail> fupLevelQoSDetails;
	@Nullable private final LogicalExpression logicalExpression;
	@Nullable private final String accessNetworkDisplayVal;
	@Nullable private final List<String> accessNetwork;
	@Nullable private final String	additionalCondition;
	@Nullable private final AccessTimePolicy accessTimePolicy;
	private final DataRateCard dataRateCard;
	private String id;

	public QoSProfile(String id, String name, String packageName, String packageId,
                      @Nullable QuotaProfile quotaProfile,
                      DataRateCard dataRateCard, List<String> accessNetwork,
                      int duration,
                      QoSProfileDetail hsqLevelQoSDetail,
                      List<QoSProfileDetail> fupLevelQoSDetails,
                      LogicalExpression logicalExpression,
                      String additionalCondition,
                      AccessTimePolicy accessTimePolicy) {
		
		super();
		this.id = id;
		this.name = name;
		this.packageName = packageName;
		this.packageId = packageId;
		this.quotaProfile = quotaProfile;
		this.accessNetwork = Collectionz.isNullOrEmpty(accessNetwork) ? null : accessNetwork;
		this.accessNetworkDisplayVal = createDisplyVal(accessNetwork);
		this.duration = duration;
		this.hsqLevelQoSDetail = hsqLevelQoSDetail;
		this.fupLevelQoSDetails = fupLevelQoSDetails;
		this.logicalExpression = logicalExpression;
		this.additionalCondition = additionalCondition;
		this.accessTimePolicy = accessTimePolicy;
		this.dataRateCard = dataRateCard;
	}

	private String createDisplyVal(List<String> accessNetwork) {
		if(Collectionz.isNullOrEmpty(accessNetwork)) {
			return null;
		}
		
		StringBuilder displayValueBuilder = new StringBuilder(accessNetwork.get(0));
		
		for(int index=1; index < accessNetwork.size(); index++) {
			displayValueBuilder.append(CommonConstants.COMMA);
			displayValueBuilder.append(accessNetwork.get(index));
		}
		return displayValueBuilder.toString();
	}

	public @Nullable QuotaProfile getQuotaProfile() {
		return quotaProfile;
	}

	public String getName() {
		return name;
	}
	
	public int getDuration() {
		return duration;
	}

	public List<QoSProfileDetail> getFupLevelQoSDetails() {
		return fupLevelQoSDetails;
	}

	public LogicalExpression getLogicalExpression() {
		return logicalExpression;
	}

	public String getAdditionalCondition() {
		return additionalCondition;
	}
	
	public QoSProfileDetail getHSQLevelQoSDetail() {
		return hsqLevelQoSDetail;
	}

	protected long checkDuration(Date sessionStartTime,Calendar currentTime) {
		
		
		if(sessionStartTime == null){
			return TimeUnit.MINUTES.toSeconds(duration);
		}else {
			
			long timeDiff = (sessionStartTime.getTime() + TimeUnit.MINUTES.toMillis(duration)) - currentTime.getTimeInMillis();
			if(timeDiff <= 0){
				return -1;
			}
			
			return (long)Math.ceil(timeDiff/1000D);
		}
	}


	public List<PCCRule> getPCCRules() {
		
		List<PCCRule> pccRules = new ArrayList<PCCRule>();
		
		if (hsqLevelQoSDetail != null && Collectionz.isNullOrEmpty(hsqLevelQoSDetail.getPCCRules()) == false) {
			
			pccRules.addAll(hsqLevelQoSDetail.getPCCRules());
		}
		
		if (fupLevelQoSDetails != null) {
			
			for(int i = 0; i < fupLevelQoSDetails.size(); i++) {
				
				
				List<PCCRule> pccRules2 = fupLevelQoSDetails.get(i).getPCCRules();
				
				if(pccRules2 != null) {
					pccRules.addAll(pccRules2);
				}
			}
		}
		
		return pccRules;
		
		
	}

	public List<ChargingRuleBaseName> getChargingRuleBaseNames() {

		List<ChargingRuleBaseName> chargingRuleBaseNames = Collectionz.newArrayList();

		if (hsqLevelQoSDetail != null && Collectionz.isNullOrEmpty(hsqLevelQoSDetail.getChargingRuleBaseNames()) == false) {
			chargingRuleBaseNames.addAll(hsqLevelQoSDetail.getChargingRuleBaseNames());
		}

		if (fupLevelQoSDetails != null) {

			for(int i = 0; i < fupLevelQoSDetails.size(); i++) {
				List<ChargingRuleBaseName> fupLevelchargingRuleBaseNames = fupLevelQoSDetails.get(i).getChargingRuleBaseNames();
				if(fupLevelchargingRuleBaseNames != null) {
					chargingRuleBaseNames.addAll(fupLevelchargingRuleBaseNames);
				}
			}
		}

		return chargingRuleBaseNames;
	}

	
	public List<String> getAccessNetwork() {
		return accessNetwork;
	}

	public String getAccessNetworkDisplayVal() {
		return accessNetworkDisplayVal;
	}

	public AccessTimePolicy getAccessTimePolicy() {
		return accessTimePolicy;
	}

	public String getPackageName() {
		return packageName;
	}

	@Override
	public String toString() {

		return toString(QOS_PROFILE_DATA_TO_STRING_STYLE);
	}
	
	public String toString(ToStringStyle toStringStyle) {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this, toStringStyle)
		.append("Name", name)
		.append("Access Network", accessNetwork)
		.append("Advanced Condition", getAdditionalCondition())
		.append("Duration", duration)
		.append("Quota Profile Attached", quotaProfile != null ? quotaProfile.getName() : "N/A")
		.append("Data Rate Card Attached", dataRateCard != null ? dataRateCard.getName() : "N/A");

		if (accessTimePolicy != null) {
			
			if (Collectionz.isNullOrEmpty(accessTimePolicy.getListTimeSlot()) == false) {
				toStringBuilder.append("Time Periods" + SystemUtils.LINE_SEPARATOR);
				
				for (TimeSlot timeSlot : accessTimePolicy.getListTimeSlot()) {
					
					toStringBuilder.append("Time Slot:" + timeSlot);
				}
			}
		} else {
			toStringBuilder.append("Time period(s) not configured");
		}
		
		toStringBuilder.append("\t");
		
		if (hsqLevelQoSDetail != null) {
			toStringBuilder.append("HSQ Details", hsqLevelQoSDetail);
		}
		
		if (Collectionz.isNullOrEmpty(fupLevelQoSDetails) == false) {
			for (QoSProfileDetail qoSProfileDetail : fupLevelQoSDetails) {
				toStringBuilder.append("QoS Profile Detail", qoSProfileDetail);
			}
		}
		
		if (hsqLevelQoSDetail == null && Collectionz.isNullOrEmpty(fupLevelQoSDetails)) {
			toStringBuilder.append("No QoS profile details found");
		}

		return toStringBuilder.toString();

	}

	public String getPackageId() {
		return packageId;
	}

	public String getId() {
		return id;
	}

	public DataRateCard getDataRateCard() {
		return dataRateCard;
	}

	private static final class QoSProfileDataToString extends ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		QoSProfileDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(4) + getTabs(1));
		}
	}

	
}

