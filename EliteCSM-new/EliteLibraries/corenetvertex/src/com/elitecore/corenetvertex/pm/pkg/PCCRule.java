package com.elitecore.corenetvertex.pm.pkg;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.SystemUtils;

import com.elitecore.corenetvertex.constants.PriorityLevel;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.core.constant.ChargingModes;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.pm.constants.UsageMetering;
import com.elitecore.corenetvertex.util.ToStringStyle;

public interface PCCRule extends Comparable<PCCRule>, Serializable{
	PCCRuleQoSBaseComparator pccRuleQoSBaseComparator = new PCCRuleQoSBaseComparator();
	ToStringStyle PCC_RULE_DATA_TO_STRING_STYLE = new PCCRuleDataToString();

	long getServiceIdentifier();

	UsageMetering getUsageMetering();

	String getMonitoringKey();

	String getName();

	QCI getQCI();

	long getGBRDL();

	long getGBRUL();

	long getMBRDL();

	long getMBRUL();

	String getServiceName();

	String getServiceTypeId();

	String getId();

	int getPrecedence();

	long getChargingKey();

	String getRatingGroupId();

	String getAppServiceProviderId();

	String getSponsorIdentity();

	FlowStatus getFlowStatus();

	ChargingModes getChargingMode();

	PriorityLevel getPriorityLevel();

	boolean getPeCapability();

	boolean getPeVulnerability();

	List<String> getServiceDataFlows();

	boolean isPredifine();

	SliceInformation getSliceInformation();

	void setQCI(QCI qci);

	void setFlowStatus(FlowStatus fromValue);

	void setChargingKey(long value);

	void setPeCapability(boolean peCapability);

	void setPeVulnerability(boolean preVulnerability);

	void setPriorityLevel(PriorityLevel priorityLevel);

	void setSponsorIdentity(String sponsorIdentity);

	void setAppServiceProviderId(String appServiceProviderId);

	void setChargingMode(ChargingModes fromValue);

	void setPrecedence(int value);

	void setMBRUL(long value);

	void setMBRDL(long value);

	void setGBRUL(long value);

	void setGBRDL(long value);

	int getFupLevel();

	static class PCCRuleDataToString extends
			ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		PCCRuleDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setContentEnd("");
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(8)
					+ getTabs(3));
		}
	}

}
