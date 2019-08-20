package com.elitecore.netvertex.service.offlinernc.partner;

public class PartnerGroup {
	
	private String name;
	
	public PartnerGroup(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "PartnerGroup [name=" + name + "]";
	}
	
}
