package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.CounterPresence;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;

import java.util.List;
import java.util.Map;

public class UMBaseQuotaProfile extends QuotaProfile{

	private static final long serialVersionUID = 1L;
	private final CounterPresence usagePresence;


	public UMBaseQuotaProfile(String name, String pkgName, String id,
							  Integer			renewalInterval,
							  RenewalIntervalUnit renewalIntervalUnit, QuotaProfileType quotaProfileType,
							  List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetais,
							  CounterPresence counterPresence, BalanceLevel balanceLevel) {
		super(name, pkgName, id, balanceLevel, renewalInterval, renewalIntervalUnit, quotaProfileType, fupLevelserviceWiseQuotaProfileDetais, CommonStatusValues.DISABLE.isBooleanValue(), CommonStatusValues.DISABLE.isBooleanValue());
		this.usagePresence = counterPresence;

		
	}
	
	public CounterPresence getUsagePresence() {
		return usagePresence;
	}


}
