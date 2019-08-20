package com.elitecore.core.commons.util.db;

public enum EliteDBConnectionProperty {

	NETWORK_READ_TIMEOUT(3000);

	public int defaultValue;

	private EliteDBConnectionProperty(int defaultValue) {
		this.defaultValue = defaultValue;
	}
}
