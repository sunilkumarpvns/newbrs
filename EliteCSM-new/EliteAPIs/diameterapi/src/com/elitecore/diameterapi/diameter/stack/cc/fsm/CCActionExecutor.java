package com.elitecore.diameterapi.diameter.stack.cc.fsm;

import com.elitecore.diameterapi.core.common.fsm.IAtomicActionsExecutor;
import com.elitecore.diameterapi.core.common.fsm.StateEvent;

public interface CCActionExecutor extends IAtomicActionsExecutor {
	
	public void startTcc(StateEvent event);
	
	public void restartTcc(StateEvent event);
	
	public void stopTcc(StateEvent event);
	
	public void sendCcAnswer(StateEvent event);
	
	public void sendErrorMessage(StateEvent event);
	
	public void sendAbortSessionRequest(StateEvent event);
	
}
