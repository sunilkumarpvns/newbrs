package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.List;

public class AddOn extends com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn {

	private static final long serialVersionUID = 1L;

	public AddOn(String id, String name, QuotaProfileType quotaProfileType, PkgStatus availabilityStatus,
			List<QoSProfile> qosProfiles, boolean multipleSubscription, boolean isExclusive, int validityPeriod,
			ValidityPeriodUnit validityPeriodUnit, UsageNotificationScheme usageNotificationScheme,
			PkgMode packageMode,
			String description,
			Double price,
			Timestamp availabilityStartDate,
			Timestamp availabilityEndDate,
			List<String> groupIds,
			PolicyStatus status,
			@Nullable String failReason, @Nullable String partialFailReason, @Nullable String param1 , @Nullable String param2, QuotaNotificationScheme quotaNotificationScheme,String currency) {
		super(id, name, quotaProfileType, availabilityStatus, qosProfiles, multipleSubscription, isExclusive, validityPeriod, validityPeriodUnit, usageNotificationScheme,
				packageMode, 
				description,
				price,
				availabilityStartDate,
				availabilityEndDate
				, groupIds, status, failReason, partialFailReason,param1,param2, quotaNotificationScheme,currency);
	}
}
