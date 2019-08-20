package com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data;

import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandData;
import com.elitecore.netvertexsm.web.core.base.BaseData;

public class  MCCMNCGroupData extends BaseData {

	
	
	private long mccmncGroupId;
	private String name;
	private String description;
	private long brandId;
	private BrandData brandData;
	
	private List<MCCMNCCodeGroupRelData> mccmncCodeGroupRelDataList;
	private Set<MCCMNCCodeGroupRelData> mccmncCodeGroupRelSet;
	
	
	public long getMccmncGroupId() {
		return mccmncGroupId;
	}
	public void setMccmncGroupId(long mccmncGroupId) {
		this.mccmncGroupId = mccmncGroupId;
	}
	
	public long getBrandId() {
		return brandId;
	}
	public BrandData getBrandData() {
		return brandData;
	}
	public void setBrandId(long brandId) {
		this.brandId = brandId;
	}
	public void setBrandData(BrandData brandData) {
		this.brandData = brandData;
	}
	
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<MCCMNCCodeGroupRelData> getMccmncCodeGroupRelDataList() {
		return mccmncCodeGroupRelDataList;
	}
	public void setMccmncCodeGroupRelDataList(
			List<MCCMNCCodeGroupRelData> mccmncCodeGroupRelDataList) {
		this.mccmncCodeGroupRelDataList = mccmncCodeGroupRelDataList;
	}
	public Set<MCCMNCCodeGroupRelData> getMccmncCodeGroupRelSet() {
		return mccmncCodeGroupRelSet;
	}
	public void setMccmncCodeGroupRelSet(
			Set<MCCMNCCodeGroupRelData> mccmncCodeGroupRelSet) {
		this.mccmncCodeGroupRelSet = mccmncCodeGroupRelSet;
	}
	
	
	
}
