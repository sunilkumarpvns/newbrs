package com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data;

public enum MandatoryDBFieldMapping {
	APPLICATION_ID("Application ID"),
	SESSION_IDENTITY("Session Identity"), 
	PDP_TYPE("PDP Type"), 
	INDIVIDUAL_IDENTITY("Individual Identity"), 
	PEER_IDENTITY("Peer Identity"), 
	GROUP_IDENTITY("Group Identity");

	private final String dbFieldMapping;

	private MandatoryDBFieldMapping(String dbFieldMapping) {
		this.dbFieldMapping = dbFieldMapping;
	}

	public String getDBFieldMapping() {
		return dbFieldMapping;
	}
}
