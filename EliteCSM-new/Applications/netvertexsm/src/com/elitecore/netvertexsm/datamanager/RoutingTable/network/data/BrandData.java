package com.elitecore.netvertexsm.datamanager.RoutingTable.network.data;

import com.elitecore.netvertexsm.web.core.base.BaseData;

public class BrandData extends BaseData {
	private Long brandID;
	private String name;
	
	public Long getBrandID() {
		return brandID;
	}

	public void setBrandID(Long brandID) {
		this.brandID = brandID;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}	
}
