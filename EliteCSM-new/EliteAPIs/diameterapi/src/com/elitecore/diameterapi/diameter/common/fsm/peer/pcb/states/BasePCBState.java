package com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.states;

import com.elitecore.diameterapi.core.common.fsm.StateBase;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.PCBActionExecutor;

public abstract class BasePCBState extends StateBase{
	
	protected PCBActionExecutor actionExecutor;
	public BasePCBState(PCBActionExecutor actionExecutor){
		this.actionExecutor = actionExecutor;
	}
}
