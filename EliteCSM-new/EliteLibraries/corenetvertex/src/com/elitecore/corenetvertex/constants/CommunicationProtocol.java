package com.elitecore.corenetvertex.constants;


public enum CommunicationProtocol {

	RADIUS("RADIUS","DTY001"),
	DIAMETER("DIAMETER","DTY002");



	public final String name;
	public final String id;

	private CommunicationProtocol(String name, String id) {
		this.name = name;
		this.id = id;
	}

	public static CommunicationProtocol fromId(String id) {

		if (RADIUS.id.equalsIgnoreCase(id)) {
			return RADIUS;
		} else if (DIAMETER.id.equalsIgnoreCase(id)) {
			return DIAMETER;
		}
		return null;
	}
	
	public static CommunicationProtocol fromName(String name) {

		if (RADIUS.name.equalsIgnoreCase(name)) {
			return RADIUS;
		} else if (DIAMETER.name.equalsIgnoreCase(name)) {
			return DIAMETER;
		}
		return null;
	}


	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
