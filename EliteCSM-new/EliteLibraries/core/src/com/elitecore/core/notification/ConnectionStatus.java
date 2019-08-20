package com.elitecore.core.notification;

public enum ConnectionStatus {

	CONNECTED(1),
	CONNECTION_FAILURE(2),
	PERMANENT_FAILURE(3),
	DISCONNECTED(4);
	
	public final int status;
	private ConnectionStatus(int status) {
		
		this.status = status;
	}
	
}
