package com.elitecore.corenetvertex.pm.pkg.datapackage;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;


import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.List;

public class AddOn extends Package implements SubscriptionPackage {

	private static final long serialVersionUID = 1L;
	private final boolean isExclusive;
	private final ValidityPeriodUnit validityPeriodUnit;
	private final int validityPeriod;
	private boolean multipleSubscription;

	public AddOn(
			String id, 
			String name, 
			QuotaProfileType quotaProfileType,
			PkgStatus availabilityStatus,
			List<QoSProfile> qosProfiles, 
			boolean multipleSubscription, 
			boolean isExclusive,
			int validityPeriod, 
			ValidityPeriodUnit validityPeriodUnit,
			UsageNotificationScheme usageNotificationScheme,
			PkgMode packageMode,
			String description,
			Double price,
			Timestamp availabilityStartDate,
			Timestamp availabilityEndDate,
			List<String> groupIds,
			PolicyStatus status, @Nullable String failReason, @Nullable String partialFailReason,@Nullable String param1,@Nullable String param2, QuotaNotificationScheme quotaNotificationScheme,String currency) {
		super(id, name, quotaProfileType, availabilityStatus, qosProfiles,usageNotificationScheme, packageMode,
				description, price, availabilityStartDate, availabilityEndDate, groupIds, status, failReason, partialFailReason, param1,param2, quotaNotificationScheme,currency);
		this.multipleSubscription = multipleSubscription;
		this.isExclusive = isExclusive;
		this.validityPeriod = validityPeriod;
		this.validityPeriodUnit = validityPeriodUnit;
		
	}

	public boolean isExclusive() {
		return isExclusive;
	}


	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage#getType()
	 */
	@Override
	public String getType() {
		return PkgType.ADDON.name();
	}

	/** (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage#getValidityPeriodUnit()
	 * @deprecated
	 */
	@Deprecated
	@Override
	public ValidityPeriodUnit getValidityPeriodUnit() {
		return validityPeriodUnit;
	}


	/** (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage#getValidityPeriod()
	 * @deprecated
	 */
	@Deprecated
	@Override
	public int getValidity() {
		return validityPeriod;
	}

	/** (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage#isMultipleSubscription()
	 * @deprecated
	 */
	@Deprecated
	@Override
	public boolean isMultipleSubscription() {
		return multipleSubscription;
	}

	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	@Override
	public Timestamp getAvailabilityStartDate() {
		return super.getAvailabilityStartDate();
	}

	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	@Override
	public Timestamp getAvailabilityEndDate() {
		return super.getAvailabilityEndDate();
	}

	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	@Override
	public Double getPrice() { return super.getPrice(); }

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.IAddOn#getPackageType()
	 * @deprecated
	 */
	@Override
	public PkgType getPackageType() {
		return PkgType.ADDON;
	}

	public Integer getPriority() {
		return null;
	}
	
	@Override
	public String toString() {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this,
				ToStringStyle.CUSTOM_TO_STRING_STYLE);
		
		toStringBuilder.append(super.toString());
		toStringBuilder.append("Is Exclusive", isExclusive);

		return toStringBuilder.toString();

	}

}
