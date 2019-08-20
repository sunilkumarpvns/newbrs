package com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.enums;

import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;

/*
 * 
Status:
    OKAY:       The connection is up
    SUSPECT:    Failover has been initiated on the connection.
    DOWN:       Connection has been closed.
    REOPEN:     Attempting to reopen a closed connection
    INITIAL:    The initial state of the pcb when it is first created.
                The pcb has never been opened.
*/
public enum PCBStates implements IStateEnum {
	OKAY(false),
	SUSPECT(false),
	DOWN(false),
	REOPEN(false),
	INITIAL(false)
	;

	public final boolean isSync;
	
	private PCBStates(boolean isSync) {
		this.isSync = isSync;
	}

	@Override
	public boolean isSync() {		
		return isSync;
	}
	@Override
	public IStateEnum getNextState(IEventEnum event) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int stateOrdinal() {		
		return ordinal();
	}

}
