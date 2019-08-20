package com.elitecore.nvsmx.system.menu;

import java.util.ArrayList;
import java.util.List;

public class ContainerMenu implements MenuItem {
	private int id;
	private String title;
	private List<MenuItem> menuItemList;
	
	public ContainerMenu(int id, String title) {
		this.id = id;
		this.title = title;
		menuItemList = new ArrayList<MenuItem>();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public MenuType getType() {
		return MenuType.CONTAINER;
	}
	
	public void addMenuList(MenuItem menuItem) {
		menuItemList.add(menuItem);
	}
	
	
	public List<MenuItem> getMenuItemList() {
		return menuItemList;
	}
	@Override
	public String toString() {
		return "ContainerMenu [title=" + title + ", menuItemList="
				+ menuItemList + "]";
	}
	
}
