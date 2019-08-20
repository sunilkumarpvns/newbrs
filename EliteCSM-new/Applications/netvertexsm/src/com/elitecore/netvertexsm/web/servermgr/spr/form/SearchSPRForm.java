package com.elitecore.netvertexsm.web.servermgr.spr.form;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.servermgr.spr.data.SPRData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class SearchSPRForm extends BaseWebForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<SPRData> sprDataList;
	private Long sprId;
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;	
	private String action;
	public List<SPRData> getSprDataList() {
		return sprDataList;
	}
	public void setSprDataList(List<SPRData> sprDataList) {
		this.sprDataList = sprDataList;
	}
	public Long getSprId() {
		return sprId;
	}
	public void setSprId(Long sprId) {
		this.sprId = sprId;
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
	

}
