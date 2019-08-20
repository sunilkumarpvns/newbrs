package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.conf;

import java.util.List;

import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;

public class QoSProfileDetailConf {

	
	private QoSProfileAction action;
	private String name;
	private String pkgName;
	private IPCANQoS sessionQoS;
	private List<PCCRule> pccRules;
	private List<QuotaProfileDetail> quotaProfileDetails;
	private int fupLevel;
	private String rejectCause;
	private boolean usageMonitoring;
	private SliceInformation sliceInformation;
	private QuotaProfileType quotaProfileType;
	private boolean isQuotaProfileAttached;
	private Integer orderNo;
	private QosProfileDetailData qosProfileDetailData;
	private boolean applyOnUsageUnavailability;
	private boolean isUsageRequired;
	
	public QoSProfileDetailConf(QosProfileDetailData qosProfileDetailData, String name, String pkgName, int fupLevel, boolean isQuotaProfileAttached, Integer orderNo) {
		this.qosProfileDetailData = qosProfileDetailData;
		this.name = name;
		this.pkgName = pkgName;
		this.fupLevel = fupLevel;
		this.isQuotaProfileAttached = isQuotaProfileAttached;
		this.orderNo = orderNo;
		this.isUsageRequired = true;
	}
	
	public QoSProfileDetailConf withAction(QoSProfileAction action, String reason) {
		this.action = action;
		this.rejectCause = reason;
		
		return this;
	}
	
	public QoSProfileDetailConf withSessionQos(IPCANQoS ipcanQoS) {
		this.sessionQoS = ipcanQoS;
		return this;
	}
	
	public QoSProfileDetailConf withPCCRules (List<PCCRule> pccRules) {
		this.pccRules = pccRules;
		return this;
		
	}
	
	public QoSProfileDetailConf withQuotaProfileDetails (List<QuotaProfileDetail> quotaProfileDetails) {
		this.quotaProfileDetails = quotaProfileDetails;
		return this;
		
	}
	
	public QoSProfileDetailConf withSliceInformation(SliceInformation sliceInformation) {
		this.sliceInformation = sliceInformation;
		return this;
	}
	
	public QoSProfileDetailConf withUsageMonitoring(boolean usageMonitoring) {
		this.usageMonitoring = usageMonitoring;
		return this;
	}
	
	public QoSProfileDetailConf withQuotaProfileType(QuotaProfileType type) {
		quotaProfileType = type;
		return this;
	}
	
	public QoSProfileDetailConf withApplyOnUsageUnavailability(boolean applyOnUsageUnavailability) {
		this.applyOnUsageUnavailability = applyOnUsageUnavailability;
		return this;
	}
	
	public QoSProfileAction getAction() {
		return action;
	}

	public String getName() {
		return name;
	}

	public String getPkgName() {
		return pkgName;
	}

	public IPCANQoS getSessionQoS() {
		return sessionQoS;
	}

	public List<PCCRule> getPccRules() {
		return pccRules;
	}

	public List<QuotaProfileDetail> getQuotaProfileDetails() {
		return quotaProfileDetails;
	}

	public int getFupLevel() {
		return fupLevel;
	}

	public String getRejectCause() {
		return rejectCause;
	}

	public boolean isUsageMonitoring() {
		return usageMonitoring;
	}

	public SliceInformation getSliceInformation() {
		return sliceInformation;
	}

	public QuotaProfileType getQuotaProfileType() {
		return quotaProfileType;
	}

	public boolean isQuotaProfileAttached() {
		return isQuotaProfileAttached;
	}

	public Integer getOrderNo() {
		return orderNo;
	}

	public QosProfileDetailData getQosProfileDetailData() {
		return qosProfileDetailData;
	}

	public boolean isApplyOnUsageUnavailability() {
		return applyOnUsageUnavailability;
	}

	public boolean isUsageRequired() {
		return isUsageRequired;
	}

}
