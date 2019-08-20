package com.elitecore.netvertexsm.web.group.form;

import java.util.List;

import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.datamanager.devicemgmt.data.TACDetailData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class GroupDataForm  extends BaseWebForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String description;
	
	//Search Parameters
    private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String actionName;
	private List<GroupData> groupDatas;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public List<GroupData> getGroupDatas() {
		return groupDatas;
	}
	public void setGroupDatas(List<GroupData> groupDatas) {
		this.groupDatas = groupDatas;
	}
	
	

}
