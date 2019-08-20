package com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.enums;

import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;

public enum PCBEvents implements IEventEnum {
	ReceiveNonDWA(true),
	ReceiveDWA(true),
	ReceiveDWAAndNumEqualsTwo(true),
	ReceiveDWAAndNumLessThanTwo(true),
	TimerExpires(true),
	TimerExpiresAndPending(true),
	TimerExpiresAndNotPending(true),
	TimerExpiresAndPendingAndDWALessThanZero(true),
	TimerExpiresAndPendingAndDWANotLessThanZero(true),
	ConnectionUp(true),
	ConnectionDown(true)
	;

	private final boolean isSync;
	
	PCBEvents(boolean isSync) {
		this.isSync = isSync;
	}
	@Override
	public int eventOrdinal() {
		return ordinal();
	}

	@Override
	public boolean isSync() {		
		return isSync;
	}

}
