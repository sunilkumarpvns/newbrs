package com.elitecore.diameterapi.diameter.stack.cc.fsm.enums;

import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;

public enum CCState implements IStateEnum {

	IDLE(false),
	OPEN(false)
	;

	public final boolean isSync;

	CCState(boolean isSync) {
		this.isSync = isSync;
	}
	
	public boolean isSync() {
		return isSync;
	}

	@Override
	public int stateOrdinal() {
		// TODO Auto-generated method stub
		return this.ordinal();
	}

	@Override
	public IStateEnum getNextState(IEventEnum event) {
		
		return null;
	}

}
