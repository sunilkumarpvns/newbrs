package com.elitecore.core.serverx.manager.cache;

public class CacheDetailProvider implements CacheDetail{
	
	private String name;
	private int resultCode;
	private String description;
	private String source;
	
	public CacheDetailProvider(){
		this.name = "";
		this.resultCode = 0;
		this.description= "";
		this.source = "";
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSource(String source) {
		this.source = source;
	}


	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public int getResultCode() {
		return resultCode;
	}

	public String getSource() {
		return source;
	}
}
