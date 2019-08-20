package com.elitecore.diameterapi.diameter.common.fsm.peer.pcb;

import com.elitecore.diameterapi.core.common.fsm.IAtomicActionsExecutor;
import com.elitecore.diameterapi.core.common.fsm.IStateTransitionData;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;

public interface PCBActionExecutor extends IAtomicActionsExecutor {

	public void setWatchdog();
	
	public void onReceive(IStateTransitionData stateTransitionData);
	
	public void onTimerElapsed();
	
	public void onConnectionUp();
	
	public void onConnectionDown();
	
	public void throwaway();
	
	public void sendWatchdog();
	
	public void attemptOpen();
	
	public void sendDPR();
	
	public void closeConnection(ConnectionEvents event);
	
	public void failover();
	
	public void failback();
	
	public void setPending(boolean pending);
	
	public int getNumDwa();
	
	public void setNumDwa(int numDwa);
	
	public void incrementNumDwa();
}
