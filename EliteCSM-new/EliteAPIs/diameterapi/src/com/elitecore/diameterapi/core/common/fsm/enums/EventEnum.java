package com.elitecore.diameterapi.core.common.fsm.enums;

public enum EventEnum implements IEventEnum{

	UNKNOWN(false);

	public final boolean isSync;

	EventEnum(boolean isSync) {
		this.isSync = isSync;
	}
	
	public boolean isSync() {
		return isSync;
	}

	public int eventOrdinal() {
		return this.ordinal();
	}
}
