package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;

public class GroupManageOrderFactory {

	public static List<GroupManageOrder> create(List<PkgGroupOrderData> pkgGroupWiseOrders) {

		List<GroupManageOrder> groupOrderConfs = Collectionz.newArrayList();

		if (Collectionz.isNullOrEmpty(pkgGroupWiseOrders)) {
			return groupOrderConfs;
		}

		for (PkgGroupOrderData data : pkgGroupWiseOrders) {
			groupOrderConfs.add(new GroupManageOrder(data.getId(), data.getGroupId(), PkgType.valueOf(data.getType()), data.getOrderNumber(), data
					.getPkgData().getId()));
		}

		return groupOrderConfs;
	}

}
