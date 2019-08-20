package com.elitecore.corenetvertex.pm.pkg.datapackage.conf;

import java.io.Serializable;

import com.elitecore.corenetvertex.pkg.PkgType;

public class GroupManageOrder implements Serializable {

	private static final long serialVersionUID = 2L;
	private String id;
	private String groupId;
	private PkgType packageType;
	private int orderNumber;
	private String packageId;

	public GroupManageOrder(String id, String groupId, PkgType packageType, int orderNumber, String packageId) {
		this.id = id;
		this.groupId = groupId;
		this.packageType = packageType;
		this.orderNumber = orderNumber;
		this.packageId = packageId;
	}

	public String getId() {
		return id;
	}

	public String getGroupId() {
		return groupId;
	}

	public PkgType getPackageType() {
		return packageType;
	}

	public int getOrderNumber() {
		return orderNumber;
	}
	
	public String getPackageId() {
		return packageId;
	}

	@Override
	public String toString() {
		return "[id=" + id + ", groupId=" + groupId + ", packageType=" + packageType + ", orderNumber=" + orderNumber
				+ ", packageId=" + packageId + "]";
	}
}
