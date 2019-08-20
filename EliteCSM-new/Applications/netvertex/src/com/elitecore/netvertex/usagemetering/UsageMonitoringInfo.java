package com.elitecore.netvertex.usagemetering;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.Balance;

public class UsageMonitoringInfo {

	private String monitoringKey;
	private UMLevel usageMonitoringLevel;
	private UsageMonitoringReport usageMonitoringReport;
	private UsageMonitoringSupport usageMonitoringSupport;
	private ServiceUnit grantedServiceUnit;
	private ServiceUnit usedServiceUnit;

	public String getMonitoringKey() {
		return monitoringKey;
	}

	public void setMonitoringKey(String monitoringKey) {
		this.monitoringKey = monitoringKey;
	}

	public UMLevel getUsageMonitoringLevel() {
		return usageMonitoringLevel;
	}

	public void setUsageMonitoringLevel(UMLevel usageMonitoringLevel) {
		this.usageMonitoringLevel = usageMonitoringLevel;
	}

	public UsageMonitoringReport getUsageMonitoringReport() {
		return usageMonitoringReport;
	}

	public void setUsageMonitoringReport(UsageMonitoringReport usageMonitoringReport) {
		this.usageMonitoringReport = usageMonitoringReport;
	}

	public UsageMonitoringSupport getUsageMonitoringSupport() {
		return usageMonitoringSupport;
	}

	public void setUsageMonitoringSupport(
			UsageMonitoringSupport usageMonitoringSupport) {
		this.usageMonitoringSupport = usageMonitoringSupport;
	}

	public ServiceUnit getGrantedServiceUnit() {
		return grantedServiceUnit;
	}

	public void setGrantedServiceUnit(ServiceUnit grantedServiceUnit) {
		this.grantedServiceUnit = grantedServiceUnit;
	}

	public ServiceUnit getUsedServiceUnit() {
		return usedServiceUnit;
	}

	public void setUsedServiceUnit(ServiceUnit usedServiceUnit) {
		this.usedServiceUnit = usedServiceUnit;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((monitoringKey == null) ? 0 : monitoringKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		try {
			UsageMonitoringInfo other = (UsageMonitoringInfo) obj;

			if (monitoringKey == null) {
				return other.monitoringKey == null;
			}

			return monitoringKey.equals(other.monitoringKey);

		} catch (Exception ex) {
			LogManager.ignoreTrace(ex);
		}

		return false;

	}


	public static class UsageMonitoringInfoBuilder {
		private UsageMonitoringInfo usageMonitoringInfo;
		private ServiceUnit grantedServiceUnit;

		public UsageMonitoringInfoBuilder(String monitoringKey, UMLevel umLevel, SliceInformation sliceInformation) {
			usageMonitoringInfo = new UsageMonitoringInfo();
			usageMonitoringInfo.monitoringKey = monitoringKey;
			usageMonitoringInfo.usageMonitoringLevel = umLevel;

			grantedServiceUnit = new ServiceUnit();
			grantedServiceUnit.setTotalOctets(sliceInformation.getSliceTotal());
			grantedServiceUnit.setOutputOctets(sliceInformation.getSliceDownload());
			grantedServiceUnit.setInputOctets(sliceInformation.getSliceUpload());
			grantedServiceUnit.setTime(sliceInformation.getSliceTime());
		}

		public UsageMonitoringInfoBuilder withBalance(Balance balance) {

			if (balance == null || balance == Balance.UNLIMITED) {
				return this;
			}

			grantedServiceUnit.setTotalOctets(Math.min(grantedServiceUnit.getTotalOctets(), balance.total()));
			grantedServiceUnit.setOutputOctets(Math.min(grantedServiceUnit.getOutputOctets(), balance.download()));
			grantedServiceUnit.setInputOctets(Math.min(grantedServiceUnit.getInputOctets(), balance.upload()));
			grantedServiceUnit.setTime(Math.min(grantedServiceUnit.getTime(), balance.time()));
			return this;
		}

		public UsageMonitoringInfoBuilder withBalance(com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.TotalBalance totalBalance) {
			if (totalBalance == null) {
				return this;
			}

			withBalance(totalBalance.getDailyBalance());
			withBalance(totalBalance.getWeeklyBalance());
			withBalance(totalBalance.getCustomBalance());
			withBalance(totalBalance.getBillingCycleBalance());

			return this;
		}

		public UsageMonitoringInfo build() {
			usageMonitoringInfo.setGrantedServiceUnit(grantedServiceUnit);
			return usageMonitoringInfo;
		}
	}
}
