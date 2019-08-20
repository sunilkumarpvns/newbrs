package com.elitecore.elitesm.datamanager.sessionmanager.data;

public enum MandatoryDBFieldMapping {
	SESSION_ID("Session ID"), 
	PDP_TYPE("PDP Type"), 
	SESSION_TIMEOUT("Session Timeout"), 
	AAA_ID("AAA ID"), 
	NAS_ID("NAS ID"), 
	USER_IDENTITY("User Identity");

	private final String dbFieldMapping;

	private MandatoryDBFieldMapping(String dbFieldMapping) {
		this.dbFieldMapping = dbFieldMapping;
	}

	public String getDBFieldMapping() {
		return dbFieldMapping;
	}
}
