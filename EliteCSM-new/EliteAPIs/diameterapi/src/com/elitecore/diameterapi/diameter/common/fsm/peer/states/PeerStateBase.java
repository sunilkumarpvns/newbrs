package com.elitecore.diameterapi.diameter.common.fsm.peer.states;

import com.elitecore.diameterapi.core.common.fsm.StateBase;

import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;
import com.elitecore.diameterapi.core.common.peer.IPeerListener;
import com.elitecore.diameterapi.diameter.common.fsm.peer.IPeerAtomicActionsExecutor;
import com.elitecore.diameterapi.diameter.common.fsm.peer.IPeerStateMachineContext;

public abstract class PeerStateBase extends StateBase{

	protected IPeerAtomicActionsExecutor peerActionsExecutor;
	protected IPeerStateMachineContext peerStateMachineContext;
	protected IPeerListener peerListener;
	protected IStateEnum stateEnum; 
	
	protected PeerStateBase(IStateEnum stateEnum, IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
		this.stateEnum = stateEnum;
		this.peerActionsExecutor = actionsExecutor;
		this.peerStateMachineContext = peerStateMachineContext;
		//this.peerListener = this.peerStateMachineContext.getPeerListener();
	}

	protected IPeerAtomicActionsExecutor getActionExecutor() {
		return peerActionsExecutor;
	}
}
