package com.elitecore.netvertex.service.offlinernc.core;

import java.math.BigDecimal;
import java.sql.Date;

import com.elitecore.commons.base.Equality;

public class DailyUsageStats {

	private String accountName;
	private Date usageDate;
	private BigDecimal usage;
	
	public DailyUsageStats(String accountName, Date usageDate, BigDecimal usage) {
		this.accountName = accountName;
		this.usageDate = usageDate;
		this.usage = usage;
	}
	
	public String getAccountName() {
		return accountName;
	}

	public BigDecimal getUsage() {
		return usage;
	}

	public Date getUsageDate() {
		return usageDate;
	}

	@Override
	public String toString() {
		return "DailyUsageStats [accountName=" + accountName + ", usageDate=" + usageDate + ", usage=" + usage + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountName == null) ? 0 : accountName.hashCode());
		result = prime * result + ((usage == null) ? 0 : usage.hashCode());
		result = prime * result + ((usageDate == null) ? 0 : usageDate.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		DailyUsageStats other = (DailyUsageStats) obj;
		return Equality.areEqual(accountName, other.accountName)
				&& Equality.areEqual(usage, other.usage) 
				&& Equality.areEqual(usageDate, other.usageDate);
	}
}
