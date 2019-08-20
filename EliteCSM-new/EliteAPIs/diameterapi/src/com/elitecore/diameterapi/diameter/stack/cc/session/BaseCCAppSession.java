package com.elitecore.diameterapi.diameter.stack.cc.session;

import com.elitecore.diameterapi.core.common.fsm.IStateMachine;
import com.elitecore.diameterapi.diameter.common.session.DiameterAppSession;

public abstract class BaseCCAppSession extends DiameterAppSession {

	private IStateMachine stateMachine;
	public BaseCCAppSession(String sessionId){
		super(sessionId);
	}

	public IStateMachine getStateMachine() {
		return stateMachine;
	}

	public void setStateMachine(IStateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}
}
