package com.elitecore.netvertexsm.web.customizedmenu;


import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;
import com.elitecore.netvertexsm.datamanager.customizedmenu.CustomizedMenuData;

import java.util.List;

public class CustomizedMenuForm extends BaseWebForm {
	private String title;
	private String url;
	private String openMethod;
	private String parameters;
	private long parentID;
	private long order;
	private List<String> titleList;
	private long customizedMenuId;
	private List<CustomizedMenuData> customizedMenuList;
	private List<CustomizedMenuData> parentIDList;
	
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	private String actionName;
	private String isContainer;
	
	public String getIsContainer() {
		return isContainer;
	}
	public void setIsContainer(String isContainer) {
		this.isContainer = isContainer;
	}
	
	public List<CustomizedMenuData> getParentIDList() {
		return parentIDList;
	}
	public void setParentIDList(List<CustomizedMenuData> parentIDList) {
		this.parentIDList = parentIDList;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getOpenMethod() {
		return openMethod;
	}
	public void setOpenMethod(String openMethod) {
		this.openMethod = openMethod;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	
	public long getParentID() {
		return parentID;
	}
	public void setParentID(long parentID) {
		this.parentID = parentID;
	}
	public long getOrder() {
		return order;
	}
	public void setOrder(long order) {
		this.order = order;
	}
	
	public List<String> getTitleList() {
		return titleList;
	}
	public void setTitleList(List<String> titleList) {
		this.titleList = titleList;
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
	public long getCustomizedMenuId() {
		return customizedMenuId;
	}
	public void setCustomizedMenuId(long customizedMenuId) {
		this.customizedMenuId = customizedMenuId;
	}
	public List<CustomizedMenuData> getCustomizedMenuList() {
		return customizedMenuList;
	}
	public void setCustomizedMenuList(List<CustomizedMenuData> customizedMenuList) {
		this.customizedMenuList = customizedMenuList;
	}
}
