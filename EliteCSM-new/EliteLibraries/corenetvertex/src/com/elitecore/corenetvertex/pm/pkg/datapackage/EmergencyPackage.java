package com.elitecore.corenetvertex.pm.pkg.datapackage;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;

import java.sql.Timestamp;
import java.util.List;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class EmergencyPackage extends Package {

	private static final long serialVersionUID = 2L;
	private List<GroupManageOrder> pkgGroupOrderConfs;
	public EmergencyPackage(String id, String name, PkgStatus availabilityStatus, List<QoSProfile> qosProfiles
			, PkgMode packageMode
			, String description
			, Double price
			, Timestamp availabilityStartDate, Timestamp availabilityEndDate, List<String> groupIds,
			PolicyStatus status, String failReason, String partialFailReason,String param1 , String param2, List<GroupManageOrder> pkgGroupOrderConfs) {
		super(id, name, null, availabilityStatus, qosProfiles, null, packageMode, description, price,
				availabilityStartDate, availabilityEndDate, groupIds, status, failReason, partialFailReason, param1,param2, null);
		this.pkgGroupOrderConfs = pkgGroupOrderConfs;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.IEmergencyPAckage#getType()
	 */
	@Override
	public String getType() {
		return PkgType.EMERGENCY.name();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.IEmergencyPAckage#getPackageType()
	 */
	@Override
	public PkgType getPackageType() {
		return PkgType.EMERGENCY;
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
