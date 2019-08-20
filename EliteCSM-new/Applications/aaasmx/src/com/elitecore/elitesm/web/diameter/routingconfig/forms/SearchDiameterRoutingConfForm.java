package com.elitecore.elitesm.web.diameter.routingconfig.forms;

import java.util.Collection;
import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchDiameterRoutingConfForm extends BaseWebForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String routingTableName;
	private String routingConfName;	
	private String overloadAction;
	private int resultCode;
	private String routingScript;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private String action;
	private Collection diameterRoutingConfList;
	private List listDiameterRoutingConf;
	private List listDiameterRoutingTable;
	private String searchBy;
	private String searchValue;
	private List<ScriptInstanceData> scriptDataList;
	
	public String getRoutingConfName() {
		return routingConfName;
	}

	public void setRoutingConfName(String routingConfName) {
		this.routingConfName = routingConfName;
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
	
	public Collection getDiameterRoutingConfList() {
		return diameterRoutingConfList;
	}
	
	public void setDiameterRoutingConfList(Collection diameterRoutingConfList) {
		this.diameterRoutingConfList = diameterRoutingConfList;
	}
	
	public List getListDiameterRoutingConf() {
		return listDiameterRoutingConf;
	}
	
	public void setListDiameterRoutingConf(List listDiameterRoutingConf) {
		this.listDiameterRoutingConf = listDiameterRoutingConf;
	}

	public List getListDiameterRoutingTable() {
		return listDiameterRoutingTable;
	}

	public void setListDiameterRoutingTable(List listDiameterRoutingTable) {
		this.listDiameterRoutingTable = listDiameterRoutingTable;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getSearchBy() {
		return searchBy;
	}

	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public String getOverloadAction() {
		return overloadAction;
	}

	public void setOverloadAction(String overloadAction) {
		this.overloadAction = overloadAction;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getRoutingTableName() {
		return routingTableName;
	}

	public void setRoutingTableName(String routingTableName) {
		this.routingTableName = routingTableName;
	}

	public String getRoutingScript() {
		return routingScript;
	}

	public void setRoutingScript(String routingScript) {
		this.routingScript = routingScript;
	}

	public List<ScriptInstanceData> getScriptDataList() {
		return scriptDataList;
	}

	public void setScriptDataList(List<ScriptInstanceData> scriptDataList) {
		this.scriptDataList = scriptDataList;
	}
	
}