package com.elitecore.netvertexsm.web.RoutingTable.mccmncroutingtable.form;

import java.util.List;


import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableGatewayRelData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;

public class RoutingTableManagementForm extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long routingTableId;

	private String name;
	
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String action;
	private Long mccmncGroupId;
	private long gatewayId;
	private String type;
	private String routingAction;
	private String roaming;
	private String realmcondition;
	private List<GatewayData> gatewayList;
	private List<MCCMNCGroupData> mccmncGroupDataList;
	private List<RoutingTableGatewayRelData> routingTableGatewayRelData;

	private List<RoutingTableData> routingTableDataList;
	
	public List<RoutingTableGatewayRelData> getRoutingTableGatewayRelData() {
		return routingTableGatewayRelData;
	}
	public void setRoutingTableGatewayRelData(
			List<RoutingTableGatewayRelData> routingTableGatewayRelData) {
		this.routingTableGatewayRelData = routingTableGatewayRelData;
	}
	
	public long getRoutingTableId() {
		return routingTableId;
	}
	public void setRoutingTableId(long routingTableId) {
		this.routingTableId = routingTableId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public Long getMccmncGroupId() {
		return mccmncGroupId;
	}
	public void setMccmncGroupId(Long mccmncGroupId) {
		this.mccmncGroupId = mccmncGroupId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRoutingAction() {
		return routingAction;
	}
	public void setRoutingAction(String routingAction) {
		this.routingAction = routingAction;
	}
	public List<MCCMNCGroupData> getMccmncGroupDataList() {
		return mccmncGroupDataList;
	}
	public void setMccmncGroupDataList(List<MCCMNCGroupData> mccmncGroupDataList) {
		this.mccmncGroupDataList = mccmncGroupDataList;
	}
	public List<RoutingTableData> getRoutingTableDataList() {
		return routingTableDataList;
	}
	public void setRoutingTableDataList(List<RoutingTableData> routingTableDataList) {
		this.routingTableDataList = routingTableDataList;
	}
	public long getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(long gatewayId) {
		this.gatewayId = gatewayId;
	}
	public List<GatewayData> getGatewayList() {
		return gatewayList;
	}
	
	public void setGatewayList(List<GatewayData> gatewayList) {
		this.gatewayList = gatewayList;
	}
	public String getRoaming() {
		return roaming;
	}
	public void setRoaming(String roaming) {
		this.roaming = roaming;
	}
	public String getRealmcondition() {
		return realmcondition;
	}
	public void setRealmcondition(String realmcondition) {
		this.realmcondition = realmcondition;
	}
	
	
	
}

