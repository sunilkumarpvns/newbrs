package com.elitecore.elitesm.web.sessionmanager.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateSessionManagerBasicDetailForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private String action;
	private String smInstanceId;
	
	public String getSmInstanceId() {
		return smInstanceId;
	}
	public void setSmInstanceId(String smInstanceId) {
		this.smInstanceId = smInstanceId;
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
    
  
	
	

}
