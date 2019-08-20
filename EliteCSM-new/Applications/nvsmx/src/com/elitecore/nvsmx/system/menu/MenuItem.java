package com.elitecore.nvsmx.system.menu;

public interface MenuItem {
	public String getTitle();
	public MenuType getType();

	default LinkType getLinkType(){
		return LinkType.INTERNAL;
	}
}
