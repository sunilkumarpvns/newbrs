package com.elitecore.core.util.cli.cmd;

public class Option { 
	
	private String option;
	
	private String description;
	
	private String action;
	
	public Option(String option, String description, String action) {
		this.option = option;
		this.description = description;
		this.action = action;
	}
	
	public Option(String option, String action) {
		this.option = option;
		this.action = action;
	}
	
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

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}
	
}
