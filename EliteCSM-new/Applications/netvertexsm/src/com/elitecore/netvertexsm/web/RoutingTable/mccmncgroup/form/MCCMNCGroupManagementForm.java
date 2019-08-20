package com.elitecore.netvertexsm.web.RoutingTable.mccmncgroup.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandOperatorRelData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.OperatorData;

public class MCCMNCGroupManagementForm extends ActionForm {
	private long mccmncGroupId;
	private String name;
	private String description;
	
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String action;
	private String actionName;
	private long brandID;
	private long operatorID;
	
	private List<BrandOperatorRelData> brandOperatorRelList;
	private List<MCCMNCGroupData> mccmncGroupDataList;
	private List<NetworkData> mccmncCodesDataList;
	private List<OperatorData> operatorDataList;
	private List<BrandData> brandDataList;
	
	public List<BrandData> getBrandDataList() {
		return brandDataList;
	}
	public void setBrandDataList(List<BrandData> brandDataList) {
		this.brandDataList = brandDataList;
	}
	public long getBrandID() {
		return brandID;
	}
	public void setBrandID(long brandID) {
		this.brandID = brandID;
	}

	
	public List<OperatorData> getOperatorDataList() {
		return operatorDataList;
	}
	public void setOperatorDataList(List<OperatorData> operatorDataList) {
		this.operatorDataList = operatorDataList;
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
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}
	public long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
	public long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<MCCMNCGroupData> getMccmncGroupDataList() {
		return mccmncGroupDataList;
	}
	public void setMccmncGroupDataList(List<MCCMNCGroupData> mccmncGroupDataList) {
		this.mccmncGroupDataList = mccmncGroupDataList;
	}
	
	public long getMccmncGroupId() {
		return mccmncGroupId;
	}
	public void setMccmncGroupId(long mccmncGroupId) {
		this.mccmncGroupId = mccmncGroupId;
	}
	public List<NetworkData> getMccmncCodesDataList() {
		return mccmncCodesDataList;
	}
	public void setMccmncCodesDataList(List<NetworkData> mccmncCodesDataList) {
		this.mccmncCodesDataList = mccmncCodesDataList;
	}
	public long getOperatorID() {
		return operatorID;
	}
	public void setOperatorID(long operatorID) {
		this.operatorID = operatorID;
	}
	public List<BrandOperatorRelData> getBrandOperatorRelList() {
		return brandOperatorRelList;
	}
	public void setBrandOperatorRelList(List<BrandOperatorRelData> brandOperatorRelList) {
		this.brandOperatorRelList = brandOperatorRelList;
	}
	public String getActionName() {
		return actionName;
}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
}
