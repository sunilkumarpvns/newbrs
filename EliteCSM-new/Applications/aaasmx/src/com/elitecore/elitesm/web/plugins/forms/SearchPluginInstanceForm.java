package com.elitecore.elitesm.web.plugins.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

/**
 * @author nayana.rathod
 *
 */
public class SearchPluginInstanceForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String action;
	private List pluginServiceList;
	private String selectedPlugin;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private List<PluginInstData> pluginInstanceDataList;
	private String status;
	private boolean radiusTransactionLoggerEnabled;
	private boolean diameterTransactionLoggerEnabled;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List getPluginServiceList() {
		return pluginServiceList;
	}
	public void setPluginServiceList(List pluginServiceList) {
		this.pluginServiceList = pluginServiceList;
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
	public List<PluginInstData> getPluginInstanceDataList() {
		return pluginInstanceDataList;
	}
	public void setPluginInstanceDataList(List<PluginInstData> pluginInstanceDataList) {
		this.pluginInstanceDataList = pluginInstanceDataList;
	}
	public String getSelectedPlugin() {
		return selectedPlugin;
	}
	public void setSelectedPlugin(String selectedPlugin) {
		this.selectedPlugin = selectedPlugin;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isRadiusTransactionLoggerEnabled() {
		return radiusTransactionLoggerEnabled;
	}
	public void setRadiusTransactionLoggerEnabled(
			boolean radiusTransactionLoggerEnabled) {
		this.radiusTransactionLoggerEnabled = radiusTransactionLoggerEnabled;
	}
	public boolean isDiameterTransactionLoggerEnabled() {
		return diameterTransactionLoggerEnabled;
	}
	public void setDiameterTransactionLoggerEnabled(
			boolean diameterTransactionLoggerEnabled) {
		this.diameterTransactionLoggerEnabled = diameterTransactionLoggerEnabled;
	}
}
