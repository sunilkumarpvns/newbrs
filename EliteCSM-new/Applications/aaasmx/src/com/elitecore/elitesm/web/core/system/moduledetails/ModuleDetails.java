package com.elitecore.elitesm.web.core.system.moduledetails;

import java.io.StringWriter;
import java.util.List;


public class ModuleDetails {
	private String jsonObject;
	private List<NestedObjectDetails> nestedObjectDetailsList;
	private String viewAdvancedDetailsLink;
	
	public String getJsonObject() {
		return jsonObject;
	}
	public void setJsonObject(String jsonObject) {
		this.jsonObject = jsonObject;
	}
	public List<NestedObjectDetails> getNestedObjectDetailsList() {
		return nestedObjectDetailsList;
	}
	public void setNestedObjectDetailsList(
			List<NestedObjectDetails> nestedObjectDetailsList) {
		this.nestedObjectDetailsList = nestedObjectDetailsList;
	}
	public String getViewAdvancedDetailsLink() {
		return viewAdvancedDetailsLink;
	}
	public void setViewAdvancedDetailsLink(String viewAdvancedDetailsLink) {
		this.viewAdvancedDetailsLink = viewAdvancedDetailsLink;
	}
	
	@Override
	public String toString() {
		StringWriter sw= new StringWriter();
		sw.append("----------Module Details---------");
		sw.append("jsonObject              : "+ jsonObject.toString());
		sw.append("nestedObjectDetailsList : "+ nestedObjectDetailsList);
		sw.append("viewAdvancedDetailsLink : "+ viewAdvancedDetailsLink);
		return sw.toString();
	}
}
