package com.elitecore.netvertexsm.blmanager.customizedmenu;

public interface MenuItem {
	public String getTitle();
	public MenuType getType();
	public String getMenuScript();
	public Long getOrder();
}
