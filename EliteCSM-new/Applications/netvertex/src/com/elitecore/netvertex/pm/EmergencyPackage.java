package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;

import java.sql.Timestamp;
import java.util.List;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class EmergencyPackage extends com.elitecore.corenetvertex.pm.pkg.datapackage.EmergencyPackage {

	private static final long serialVersionUID = 1L;

	public EmergencyPackage(String id, String name, PkgStatus availabilityStatus, List<QoSProfile> qosProfiles
			, PkgMode packageMode
			, String description
			, Double price
			, Timestamp availabilityStartDate, Timestamp availabilityEndDate
			, List<String> groupIds, PolicyStatus status, String failReason, String partialFailReason,String param1,String param2, List<GroupManageOrder> pkgGroupOrderConfs) {
		super(id, name, availabilityStatus, qosProfiles, packageMode, description, price, availabilityStartDate, availabilityEndDate, groupIds, status, failReason, partialFailReason,param1,param2, pkgGroupOrderConfs);
	}
}
