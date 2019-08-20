package com.elitecore.netvertexsm.web.RoutingTable.network.form;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandOperatorRelData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.OperatorData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class NetworkManagementForm extends BaseWebForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long networkID;
	private Long brandID;
	private Long operatorID;
	private String networkName;
	private Long countryID;
	private Integer mcc;
	private Integer mnc;
	private String technology;
	private String circle;
	private List<NetworkData> mccmncCodesDataList;
	private List<CountryData> countryDataList;
	private List<OperatorData> operatorDataList;
	private List<BrandData> brandDataList;
	private List<BrandOperatorRelData> brandOperatorRelDataList;
	
	 //Search Parameters
    private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String actionName;
	
	public String getTechnology() {
		return technology;
	}
	public void setTechnology(String technology) {
		this.technology = technology;
	}
	public String getCircle() {
		return circle;
	}
	public void setCircle(String circle) {
		this.circle = circle;
	}
	public Long getBrandID() {
		return brandID;
	}
	public void setBrandID(Long brandID) {
		this.brandID = brandID;
	}
	public List<BrandOperatorRelData> getBrandOperatorRelDataList() {
		return brandOperatorRelDataList;
	}
	public void setBrandOperatorRelDataList(
			List<BrandOperatorRelData> brandOperatorRelDataList) {
		this.brandOperatorRelDataList = brandOperatorRelDataList;
	}
	public List<BrandData> getBrandDataList() {
		return brandDataList;
	}
	public void setBrandDataList(List<BrandData> brandDataList) {
		this.brandDataList = brandDataList;
	}
	public List<CountryData> getCountryDataList() {
		return countryDataList;
	}
	public void setCountryDataList(List<CountryData> countryDataList) {
		this.countryDataList = countryDataList;
	}
	public List<OperatorData> getOperatorDataList() {
		return operatorDataList;
	}
	public void setOperatorDataList(List<OperatorData> operatorDataList) {
		this.operatorDataList = operatorDataList;
	}
	public List<NetworkData> getMccmncCodesDataList() {
		return mccmncCodesDataList;
	}
	public void setMccmncCodesDataList(List<NetworkData> mccmncCodesDataList) {
		this.mccmncCodesDataList = mccmncCodesDataList;
	}
	public Long getNetworkID() {
		return networkID;
	}
	public void setNetworkID(Long networkID) {
		this.networkID = networkID;
	}
	public Long getOperatorID() {
		return operatorID;
	}
	public void setOperatorID(Long operatorID) {
		this.operatorID = operatorID;
	}
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public Long getCountryID() {
		return countryID;
	}
	public void setCountryID(Long countryID) {
		this.countryID = countryID;
	}
	public Integer getMcc() {
		return mcc;
	}
	public void setMcc(Integer mcc) {
		this.mcc = mcc;
	}
	public Integer getMnc() {
		return mnc;
	}
	public void setMnc(Integer mnc) {
		this.mnc = mnc;
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
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
}