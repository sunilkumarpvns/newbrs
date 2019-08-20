package com.elitecore.elitesm.web.core.system.accessgroup.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class MiscListAccessGroupForm extends BaseWebForm {
	private String name;
	private String description;
	private String action;
	private List listAccessGroup;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List getListAccessGroup() {
		return listAccessGroup;
	}
	public void setListAccessGroup(List listAccessGroup) {
		this.listAccessGroup = listAccessGroup;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
