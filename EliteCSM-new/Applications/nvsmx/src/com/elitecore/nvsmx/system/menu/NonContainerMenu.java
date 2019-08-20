package com.elitecore.nvsmx.system.menu;

public class NonContainerMenu implements MenuItem{
	
	private int id;
	private String title;
	private String url;
	private MenuOpenType openType;
	private LinkType linkType;
	

	public NonContainerMenu(String title, String url) {
		this.title = title;
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public MenuOpenType getOpenType() {
		return openType;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setOpenType(MenuOpenType openType) {
		this.openType = openType;
	}

	@Override
	public LinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(LinkType linkType) {
		this.linkType = linkType;
	}

	@Override
	public String getTitle() {
		return title;
	}




	@Override
	public MenuType getType() {
		return MenuType.NONCONTAINER;
	}

	@Override
	public String toString() {
		return "NonContainerMenu [title=" + title + "]";
	}


}
