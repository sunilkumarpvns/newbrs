package com.elitecore.diameterapi.core.common.fsm;

public interface IStateTransitionData {
	
	public Object getData(IStateTransitionDataCode code);
	
	public void addObject(IStateTransitionDataCode code, Object value);
}
