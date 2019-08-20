package com.elitecore.corenetvertex.pm.pkg.datapackage;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BasePackage extends Package {
	
	private static final long serialVersionUID = 1L;
	private List<String> applicableTopUps;

	public BasePackage(String id, String name, QuotaProfileType quotaProfileType,
					   PkgStatus availabilityStatus, List<QoSProfile> qosProfiles,
					   UsageNotificationScheme usageNotificationScheme,
					   PkgMode packageMode,
					   String description,
					   Double price,
					   Timestamp availabilityStartDate,
					   Timestamp availabilityEndDate,
					   List<String> groupIds,
					   PolicyStatus status, @Nullable String failReason, @Nullable String partialFailReason,
					   @Nullable String param1, @Nullable String param2,
					   QuotaNotificationScheme quotaNotificationScheme,
					   String currency) {
		super(id, name, quotaProfileType, availabilityStatus, qosProfiles, usageNotificationScheme, packageMode, description,
				price, availabilityStartDate, availabilityEndDate, groupIds, status, failReason, partialFailReason, param1,param2, quotaNotificationScheme,currency);
		applicableTopUps = new ArrayList<>();
	}



	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.IBasePAckage#getPackageType()
	 */
	@Override
	public PkgType getPackageType() {
		return PkgType.BASE;
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

	public void setApplicableTopUps(String topUpName) {
		applicableTopUps.add(topUpName);
	}
	
	@Override
	public String toString() {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this,
				ToStringStyle.CUSTOM_TO_STRING_STYLE);
		
		toStringBuilder.append(super.toString());
		toStringBuilder.append("Applicable TopUps", applicableTopUps);

		return toStringBuilder.toString();

	}



	@Override
	public String getType() {
		return PkgType.BASE.name();
	}

}
