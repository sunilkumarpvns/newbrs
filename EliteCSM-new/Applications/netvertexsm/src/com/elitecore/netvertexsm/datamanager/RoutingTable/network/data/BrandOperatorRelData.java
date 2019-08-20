package com.elitecore.netvertexsm.datamanager.RoutingTable.network.data;

import java.io.Serializable;

import com.elitecore.netvertexsm.datamanager.core.base.data.BaseData;

public class BrandOperatorRelData extends BaseData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long brandID;
	private Long operatorID;
	private BrandData brandData;
	private OperatorData operatorData;
	
	public Long getBrandID() {
		return brandID;
	}
	
	public void setBrandID(Long brandID) {
		this.brandID = brandID;
	}
	
	public Long getOperatorID() {
		return operatorID;
	}
	
	public void setOperatorID(Long operatorID) {
		this.operatorID = operatorID;
	}
	
	public BrandData getBrandData() {
		return brandData;
	}
	
	public void setBrandData(BrandData brandData) {
		this.brandData = brandData;
	}
	
	public OperatorData getOperatorData() {
		return operatorData;
	}
	
	public void setOperatorData(OperatorData operatorData) {
		this.operatorData = operatorData;
	}
}