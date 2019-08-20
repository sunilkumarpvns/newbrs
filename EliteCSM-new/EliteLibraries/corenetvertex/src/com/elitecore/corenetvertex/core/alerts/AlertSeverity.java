package com.elitecore.corenetvertex.core.alerts;

public enum AlertSeverity{
	CLEAR(0),
	CRITICAL(1),
	ERROR(2),
	WARN(3),
	INFO(4);
	public final int code;
	AlertSeverity(int level) {
		this.code = level;
	}
}