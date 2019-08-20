package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.sm.ResourceData;

public class ResourceDataStatusFilter implements Predicate<ResourceData> {

	private static ResourceDataStatusFilter statusFilter;

	private ResourceDataStatusFilter() {
	}

	static {
		statusFilter = new ResourceDataStatusFilter();
	}

	public static ResourceDataStatusFilter getInstance() {
		return statusFilter;
	}

	@Override
	public boolean apply(ResourceData resourceData) {
		/*
		 * Only DELETED and INACTIVE should not skipped.
		 * 
		 * Null Status is allowed. (For null status, taking default ACTIVE status in package parsing)
		 */
		
		return (CommonConstants.STATUS_DELETED.equals(resourceData.getStatus())
				|| PkgStatus.INACTIVE.val.equalsIgnoreCase(resourceData.getStatus())) == false;
	}
}
