package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile;

import java.util.List;

import com.elitecore.corenetvertex.constants.PriorityLevel;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.core.constant.ChargingModes;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.pm.constants.UsageMetering;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;

public class TopUpPCC implements PCCRule {

	private long gbrdl;
	private long gbrul;
	private long mbrdl;
	private long mbrul;
	private PCCRule pccRule;

	public TopUpPCC(PCCRule pccRule,
			long gbrdl,
			long gbrul,
			long mbrdl,
			long mbrul) {
		this.gbrdl = gbrdl;
		this.gbrul = gbrul;
		this.mbrdl = mbrdl;
		this.mbrul = mbrul;
		this.pccRule = pccRule;
	}

	@Override
	public long getServiceIdentifier() {
		return pccRule.getServiceIdentifier();
	}

	@Override
	public UsageMetering getUsageMetering() {
		return pccRule.getUsageMetering();
	}

	@Override
	public String getMonitoringKey() {
		return pccRule.getMonitoringKey();
	}

	@Override
	public int compareTo(PCCRule selectedPCCRule) {
		return pccRuleQoSBaseComparator.compare(this, selectedPCCRule);
	}

	@Override
	public String getName() {
		return pccRule.getName();
	}

	@Override
	public QCI getQCI() {
		return pccRule.getQCI();
	}

	@Override
	public long getGBRDL() {
		return gbrdl;
	}

	@Override
	public long getGBRUL() {
		return gbrul;
	}

	@Override
	public long getMBRDL() {
		return mbrdl;
	}

	@Override
	public long getMBRUL() {
		return mbrul;
	}

	@Override
	public String getServiceName() {
		return pccRule.getServiceName();
	}

	@Override
	public String getServiceTypeId() {
		return pccRule.getServiceTypeId();
	}

	@Override
	public String getId() {
		return pccRule.getId();
	}

	@Override
	public int getPrecedence() {
		return pccRule.getPrecedence();
	}

	@Override
	public long getChargingKey() {
		return pccRule.getChargingKey();
	}

	@Override
	public String getRatingGroupId() {
		return pccRule.getRatingGroupId();
	}

	@Override
	public String getAppServiceProviderId() {
		return pccRule.getAppServiceProviderId();
	}

	@Override
	public String getSponsorIdentity() {
		return pccRule.getSponsorIdentity();
	}

	@Override
	public FlowStatus getFlowStatus() {
		return pccRule.getFlowStatus();
	}

	@Override
	public ChargingModes getChargingMode() {
		return pccRule.getChargingMode();
	}

	@Override
	public PriorityLevel getPriorityLevel() {
		return pccRule.getPriorityLevel();
	}

	@Override
	public boolean getPeCapability() {
		return pccRule.getPeCapability();
	}

	@Override
	public boolean getPeVulnerability() {
		return pccRule.getPeVulnerability();
	}

	@Override
	public List<String> getServiceDataFlows() {
		return pccRule.getServiceDataFlows();
	}

	@Override
	public boolean isPredifine() {
		return pccRule.isPredifine();
	}

	@Override
	public SliceInformation getSliceInformation() {
		return pccRule.getSliceInformation();
	}

	@Override
	public void setQCI(QCI qci) {
		pccRule.setQCI(qci);
	}

	@Override
	public void setFlowStatus(FlowStatus fromValue) {
		pccRule.setFlowStatus(fromValue);
	}

	@Override
	public void setChargingKey(long value) {
		pccRule.setChargingKey(value);
	}

	@Override
	public void setPeCapability(boolean peCapability) {
		pccRule.setPeCapability(peCapability);
	}

	@Override
	public void setPeVulnerability(boolean preVulnerability) {
		pccRule.setPeVulnerability(preVulnerability);
	}

	@Override
	public void setPriorityLevel(PriorityLevel priorityLevel) {
		pccRule.setPriorityLevel(priorityLevel);
	}

	@Override
	public void setSponsorIdentity(String sponsorIdentity) {
		pccRule.setSponsorIdentity(sponsorIdentity);
	}

	@Override
	public void setAppServiceProviderId(String appServiceProviderId) {
		pccRule.setAppServiceProviderId(appServiceProviderId);
	}

	@Override
	public void setChargingMode(ChargingModes fromValue) {
		pccRule.setChargingMode(fromValue);
	}

	@Override
	public void setPrecedence(int value) {
		pccRule.setPrecedence(value);
	}

	@Override
	public void setMBRUL(long value) {
		pccRule.setMBRUL(value);
	}

	@Override
	public void setMBRDL(long value) {
		pccRule.setMBRDL(value);
	}

	@Override
	public void setGBRUL(long value) {
		pccRule.setGBRUL(value);
	}

	@Override
	public void setGBRDL(long value) {
		pccRule.setGBRDL(value);
	}

	@Override
	public int getFupLevel() {
		return pccRule.getFupLevel();
	}

}
