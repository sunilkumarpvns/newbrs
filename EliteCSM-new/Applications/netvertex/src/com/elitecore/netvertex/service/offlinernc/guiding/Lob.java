package com.elitecore.netvertex.service.offlinernc.guiding;

public class Lob {
	
	private String name;
	private String alias;

	public Lob(String name, String alias) {
		this.name = name;
		this.alias = alias;
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

	@Override
	public String toString() {
		return "Lob [name=" + name + ", alias=" + alias + "]";
	}
}
