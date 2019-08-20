package com.elitecore.netvertexsm.blmanager.customizedmenu;

import java.util.List;

public class ContainerMenu implements MenuItem {

	private Long id;
	private Long order;
	private String title;
	private List<MenuItem> menuItemsList;
	
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

	public List<MenuItem> getMenuItemsList() {
		return menuItemsList;
	}

	public void setMenuItemsList(List<MenuItem> menuItemsList) {
		this.menuItemsList = menuItemsList;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	

	public Long getOrder() {
		return order;
	}

	public void setOrder(Long order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "ContainerMenu [title=" + title + ", menuItemsList="
				+ menuItemsList + "]";
	}
	@Override
	public String getMenuScript(){
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("stm_aix(\""+id +"\",\"\",[0,\" "+title+"\",\"\",\"\",-1,-1,0,\"\",\"\",\"\",\"\",\"\",\"\",0,0,0,\"\",\"\",7,7,0,0,0,\"#005197\",0,\"rgb(3, 44, 77)\",0,\"\",\"\",3,3,1,1,\"#007CC3 #005197 #005197 #007CC3\",\"#007CC3 #005197 #005197 #007CC3\",\"#FFFFFF\",\"#FFFFFF\",\"9pt Arial\",\"9pt Arial\"],190);\n");
		stringBuilder.append("stm_bpx(\""+id+"\",\"\",[1,2,0,0,0,4,0,0,100,\"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)\",-2,\"\",-2,85,0,0,\"#7F7F7F\",\"#FFFFFF\"]);\n");
		for(MenuItem menuItem : menuItemsList){
			stringBuilder.append(menuItem.getMenuScript());
		}
		stringBuilder.append("stm_ep()\n");
		return stringBuilder.toString();
		
	}

	
	
}
