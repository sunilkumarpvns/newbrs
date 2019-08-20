package com.elitecore.diameterapi.core.common.fsm.enums;

public enum StateEnum implements IStateEnum{
	NOT_INITIALIZED(false),
	UNKNOWN(false),
	;
	public final boolean isSync;

	StateEnum(boolean isSync) {
		this.isSync = isSync;
	}
	
	public boolean isSync() {
		return isSync;
	}

	public int stateOrdinal() {
		return super.ordinal();
	}

	@Override
	public IStateEnum getNextState(IEventEnum event) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
