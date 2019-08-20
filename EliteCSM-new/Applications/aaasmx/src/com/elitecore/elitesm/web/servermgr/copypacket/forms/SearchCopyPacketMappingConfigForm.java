package com.elitecore.elitesm.web.servermgr.copypacket.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchCopyPacketMappingConfigForm extends BaseWebForm {
	private static final long serialVersionUID = 1L;
	private String name;
	private String action;
	private String status;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private List<CopyPacketTranslationConfData> configList;
	
	
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public List<CopyPacketTranslationConfData> getConfigList() {
		return configList;
	}
	public void setConfigList(List<CopyPacketTranslationConfData> configList) {
		this.configList = configList;
	}

}