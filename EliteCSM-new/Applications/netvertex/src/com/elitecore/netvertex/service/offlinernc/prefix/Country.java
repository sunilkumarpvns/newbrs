package com.elitecore.netvertex.service.offlinernc.prefix;

/**
 * @author elitecore
 *
 */
public class Country {
	
	private String id;
	private String name;
	private String code;
	
	public Country(String id, String name, String code) {
		this.id = id;
		this.name = name;
		this.code = code;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}
}
