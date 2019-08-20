package com.elitecore.elitesm.datamanager.dashboard.category.data;

import java.io.Serializable;
import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.web.dashboard.json.WidgetTemplateData;

public class CategoryData extends BaseData implements Serializable{

	private static final long serialVersionUID = 1L;
	private String categoryId;
	private String categoryName;
	private String jsonURL;
	private Set<WidgetTemplateData> widgetTemplateData;
	
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getJsonURL() {
		return jsonURL;
	}
	public void setJsonURL(String jsonURL) {
		this.jsonURL = jsonURL;
	}
	public Set<WidgetTemplateData> getWidgetTemplateData() {
		return widgetTemplateData;
	}
	public void setWidgetTemplateData(Set<WidgetTemplateData> widgetTemplateData) {
		this.widgetTemplateData = widgetTemplateData;
	}
}
