package com.elitecore.diameterapi.diameter.common.fsm.peer;

import javax.annotation.Nullable;

import com.elitecore.diameterapi.core.common.fsm.IAtomicActionsExecutor;

import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerEvent;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

public interface IPeerAtomicActionsExecutor extends IAtomicActionsExecutor{

	void atomicActionSndConnReq(StateEvent event);
	
	
	boolean atomicActionRAccept(StateEvent event);
	ResultCode atomicActionProcessCER(StateEvent event);
	
	//?????
	void atomicActionRSndCEA(StateEvent event,ResultCode resultCode);
	void atomicActionRSndCEA(StateEvent event);
	//?????
	
	
	void atomicActionISndCEA(StateEvent event);
	void atomicActionSndCER(StateEvent event);
	void atomicActionCleanup(StateEvent event, ConnectionEvents connEvent);
	void atomicActionError(StateEvent event, ConnectionEvents connEvent);
	void atomicActionElect(StateEvent event);
	void atomicActionIDisc(StateEvent event);
	ResultCode atomicActionProcessCEA(StateEvent event);
	void atomicActionRDisc(StateEvent event);
	void atomicActionRReject(@Nullable NetworkConnectionHandler connection);
	void atomicActionSndMessage(StateEvent event);
	void atomicActionProcess(StateEvent event);
	void atomicActionProcessDWR(StateEvent event);
	void atomicActionRSndDWA(StateEvent event);
	void atomicActionISndDWA(StateEvent event);
	void atomicActionProcessDWA(StateEvent event);
	void atomicActionRSndDPR(StateEvent event , DiameterPeerEvent diameterPeerEvent);
	void atomicActionISndDPR(StateEvent event,DiameterPeerEvent diameterPeerEvent);
	void atomicActionRSndDPA(StateEvent event);
	void atomicActionISndDPA(StateEvent event);
	void onConnectionUp();
	void onConnectionDown();
	void startTimeoutEventTimer();
	void atomicActionProcessDuplicateConnection(StateEvent stateEvent);
}
