package com.elitecore.elitesm.web.dashboard.json;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class WidgetTemplateData extends BaseData{
	private String widgteTemplateId;
	private String title;
	private String jspUrl;
	private String configJspUrl;
	private String thumbnail;
	private String categoryId;
	private String description;
	private String widgetGroovy;
	private Long orderNumber;
	
	public String getWidgteTemplateId() {
		return widgteTemplateId;
	}
	public void setWidgteTemplateId(String widgteTemplateId) {
		this.widgteTemplateId = widgteTemplateId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getJspUrl() {
		return jspUrl;
	}
	public void setJspUrl(String jspUrl) {
		this.jspUrl = jspUrl;
	}
	public String getConfigJspUrl() {
		return configJspUrl;
	}
	public void setConfigJspUrl(String configJspUrl) {
		this.configJspUrl = configJspUrl;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getWidgetGroovy() {
		return widgetGroovy;
	}
	public void setWidgetGroovy(String widgetGroovy) {
		this.widgetGroovy = widgetGroovy;
	}
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
}
