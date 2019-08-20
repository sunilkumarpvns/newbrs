package com.elitecore.nvsmx.system.menu;

public enum MenuOpenType {
	NEW("new"),
	SAME("same");
	
	public String type;
	private MenuOpenType(String type){
		this.type = type;
	}
}
