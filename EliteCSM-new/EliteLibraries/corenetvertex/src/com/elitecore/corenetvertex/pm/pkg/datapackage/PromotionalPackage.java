package com.elitecore.corenetvertex.pm.pkg.datapackage;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.List;

public class PromotionalPackage extends Package{

	private static final long serialVersionUID = 2L;
	private final boolean preferPromotionalQoS;
	private List<GroupManageOrder> pkgGroupOrderConfs;
	
	public PromotionalPackage(
			String id, 
			String name, 
			QuotaProfileType quotaProfileType,
			PkgStatus availabilityStatus,
			List<QoSProfile> qosProfiles, 
			UsageNotificationScheme usageNotificationScheme,
			PkgMode packageMode,
			String description,
			Double price,
			Timestamp availabilityStartDate,
			Timestamp availabilityEndDate,
			List<String> groupIds, boolean preferPromotionalQoS,
			PolicyStatus status, @Nullable String failReason, @Nullable String partialFailReason,@Nullable String param1,@Nullable String param2, List<GroupManageOrder> pkgGroupOrderConfs) {
		super(id, name, quotaProfileType, availabilityStatus, qosProfiles,usageNotificationScheme, packageMode,
				description, price, availabilityStartDate, availabilityEndDate, groupIds, status, failReason, partialFailReason, param1,param2, null);
		this.preferPromotionalQoS = preferPromotionalQoS;
		this.pkgGroupOrderConfs = pkgGroupOrderConfs;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage#getType()
	 */
	@Override
	public String getType() {
		return PkgType.PROMOTIONAL.name();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.IAddOn#getPackageType()
	 */
	@Override
	public PkgType getPackageType() {
		return PkgType.PROMOTIONAL;
	}
	
	@Override
	public String toString() {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this,
				ToStringStyle.CUSTOM_TO_STRING_STYLE);
		
		toStringBuilder.append(super.toString());
		toStringBuilder.append("Prefer Promotional QoS", preferPromotionalQoS);
		return toStringBuilder.toString();

	}

	public boolean isPreferPromotionalQoS() {
		return preferPromotionalQoS;
	}
	
	public List<GroupManageOrder> getPkgGroupOrderConfs() {
		return pkgGroupOrderConfs;
	}
	
	public int getOrderNumberInGroup(String groupId) {
		
		for (GroupManageOrder groupOrderConf : pkgGroupOrderConfs) {
			if (groupOrderConf.getGroupId().equals(groupId)) {
				return groupOrderConf.getOrderNumber();
			}
		}
		
		return 0;
	}
}
