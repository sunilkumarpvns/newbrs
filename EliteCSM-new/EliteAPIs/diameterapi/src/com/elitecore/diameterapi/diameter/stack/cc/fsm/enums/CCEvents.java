package com.elitecore.diameterapi.diameter.stack.cc.fsm.enums;

import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;

public enum CCEvents implements IEventEnum{
	CcInitialRequest(false),
	CcEventRequest(false),
	CcUpdateRequest(false),
	CcTerminationRequest(false),
	SessionSupervisionTimerExpired(false),
	UnknownEvent(false)
	;
	
	public final boolean isSync;

	CCEvents(boolean isSync) {
		this.isSync = isSync;
	}
	
	public boolean isSync() {
		return isSync;
	}

	@Override
	public int eventOrdinal() {
		return this.ordinal();
	}

}
