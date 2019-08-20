package com.elitecore.diameterapi.core.common.fsm.enums;

public interface IStateEnum {

	public IStateEnum getNextState(IEventEnum event);
	
	public int stateOrdinal();
	
	public boolean isSync();
	public String name();

}
