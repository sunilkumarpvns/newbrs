package com.elitecore.netvertexsm.datamanager.customizedmenu;

import java.util.List;



public class CustomizedMenuData {

	private String title;
	private String url;
	private String openMethod;
	private String parameters;
	private long parentID;
	private long order;
	private long customizedMenuId;
	private List<TitleData> titleList;
	private List<Long> parentIDList;
	private String isContainer;
	
	public String getIsContainer() {
		return isContainer;
	}
	public void setIsContainer(String isContainer) {
		this.isContainer = isContainer;
	}
	public List<Long> getParentIDList() {
		return parentIDList;
	}
	public void setParentIDList(List<Long> parentIDList) {
		this.parentIDList = parentIDList;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getURL() {
		return url;
	}
	public void setURL(String url) {
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
	public long getCustomizedMenuId() {
		return customizedMenuId;
	}
	public void setCustomizedMenuId(long customizedMenuId) {
		this.customizedMenuId = customizedMenuId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<TitleData> getTitleList() {
		return titleList;
	}
	public void setTitleList(List<TitleData> titleList) {
		this.titleList = titleList;
	}

}
