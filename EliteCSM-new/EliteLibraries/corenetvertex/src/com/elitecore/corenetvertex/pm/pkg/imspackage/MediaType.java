package com.elitecore.corenetvertex.pm.pkg.imspackage;

import java.io.Serializable;

public class MediaType implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private final String id;
	private final long identifier;
	private final String name;
	
	
	public MediaType(String id, long identifier, String name) {
		super();
		this.id = id;
		this.identifier = identifier;
		this.name = name;
	}


	public String getId() {
		return id;
	}


	public long getIdentifier() {
		return identifier;
	}


	public String getName() {
		return name;
	}

}
