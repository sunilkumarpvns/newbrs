package com.elitecore.diameterapi.diameter.common.fsm.peer.pcb;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.diameterapi.core.common.fsm.IStateTransitionData;
import com.elitecore.diameterapi.core.common.fsm.IStateTransitionDataCode;
import com.elitecore.diameterapi.core.common.fsm.State;
import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.StateMachine;
import com.elitecore.diameterapi.core.common.fsm.exception.UnhandledTransitionException;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.enums.PCBEvents;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.enums.PCBStates;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.states.DownState;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.states.InitialState;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.states.OkayState;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.states.ReopenState;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.states.SuspectState;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.PeerDataCode;

public abstract class PCBStateMachine extends StateMachine implements PCBActionExecutor{

	private final static String MODULE = "PCB-ST-MCHN";

	private AtomicBoolean pending; //set to TRUE if there is an outstanding unanswered watchdog request
	private AtomicInteger numDwa; //Number of DWAs received during REOPEN	
	private long watchdogTimer;
	private long watchdogDuration;
	private IStackContext stackContext;
	private ScheduledFuture<?> connectionInitiatorTask;
	private ScheduledFuture<?> watchDogTask;
	private long initiateConnectionDuration;
    private final ReentrantLock timerExpireStateLock;

	
	private final Object watchDogLock = new Object();
	private final Object initConnectionLock = new Object();
	
	public PCBStateMachine(long watchDogTimerMs,int initiateConnectionDuration,IStackContext stackContext){
		this.stackContext =stackContext;
		currentState = PCBStates.INITIAL;
		numDwa = new AtomicInteger(0);
		pending = new AtomicBoolean(false);
		this.watchdogDuration = watchDogTimerMs;
		this.initiateConnectionDuration = initiateConnectionDuration;
		timerExpireStateLock = new ReentrantLock();
	}
	
	public void start() {
		scheduleConnectionInitiatorTask();
	}

	@Override
	protected StateEvent createStateEvent(IStateTransitionData transitionData) {

		if(currentState ==  PCBStates.OKAY){
			return getOkayStateEvent(transitionData);
		}else if(currentState == PCBStates.INITIAL){
			return getInitialStateEvent(transitionData); 
		}else if(currentState == PCBStates.DOWN){
			return getDownStateEvent(transitionData); 
		}else if(currentState == PCBStates.REOPEN){
			return getReopenStateEvent(transitionData); 
		}else if(currentState == PCBStates.SUSPECT){
			return getSuspectStateEvent(transitionData); 
		}

		return null;
	}

	@Override
	protected List<State> createStates() {
		List<State> states = new ArrayList<State>();		
		/* order must be same as in PCBStates */
		states.add(PCBStates.OKAY.stateOrdinal(),new OkayState(this));
		states.add(PCBStates.SUSPECT.stateOrdinal(),new SuspectState(this));
		states.add(PCBStates.DOWN.stateOrdinal(),new DownState(this));
		states.add(PCBStates.REOPEN.stateOrdinal(),new ReopenState(this));
		states.add(PCBStates.INITIAL.stateOrdinal(),new InitialState(this));	
		return states;
	}

	@Override
	public void act() {
	}

	@Override
	public void onConnectionUp() {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", Connection is Up so generating ConnectionUp event.");
		}
		cancelConnectionInitiatorTask();

		IStateTransitionData transitionData = new IStateTransitionData() {
			Map<IStateTransitionDataCode, Object> data = new HashMap<IStateTransitionDataCode, Object>();
			public Object getData(IStateTransitionDataCode key) {
				return data.get(key);
			}

			public void addObject(IStateTransitionDataCode key, Object value) {
				data.put(key, value);
			}
		};
		transitionData.addObject(PeerDataCode.PEER_EVENT,  PCBEvents.ConnectionUp);

		try{
			this.onStateTransitionTrigger(transitionData);
		}catch(UnhandledTransitionException e){

		}
		scheduleWatchDogTask();

	}
	@Override
	public void onConnectionDown() {		
		if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
			LogManager.getLogger().warn(MODULE, "Peer: " + getPeerName() + ", Connection goes down so generating ConnectionDown event");
		}
		cancelWatchDogTask();

		IStateTransitionData transitionData = new IStateTransitionData() {
			Map<IStateTransitionDataCode, Object> data = new HashMap<IStateTransitionDataCode, Object>();
			public Object getData(IStateTransitionDataCode key) {
				return data.get(key);
			}

			public void addObject(IStateTransitionDataCode key, Object value) {
				data.put(key, value);
			}
		};
		transitionData.addObject(PeerDataCode.PEER_EVENT,  PCBEvents.ConnectionDown);
		try{
			this.onStateTransitionTrigger(transitionData);
		}catch(UnhandledTransitionException e){

		}
		scheduleConnectionInitiatorTask();
		
	}

	/**
	 * called whenever a message
	 * is received from the peer.  This message MAY
	 * be a request or an answer, and can include
	 * DWR and DWA messages.  Pending is assumed to
	 * be a global variable.
	 */
	@Override
	public void onReceive(IStateTransitionData transitionData) {
		try{
			this.onStateTransitionTrigger(transitionData);
		}catch(UnhandledTransitionException e){

		}
	}

	@Override
	public void onTimerElapsed() {
		
		boolean lockAquired = false;
		
		try {
			if ( (lockAquired = timerExpireStateLock.tryLock()) == false) {
				if(LogManager.getLogger().isDebugLogLevel()){
					LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", TimerExpires event is already generated.");
				}
				return;
			}
			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", Timer elapsed so generating TimerExpires event.");
			}

			IStateTransitionData transitionData = new IStateTransitionData() {
				Map<IStateTransitionDataCode, Object> data = new HashMap<IStateTransitionDataCode, Object>();
				public Object getData(IStateTransitionDataCode key) {
					return data.get(key);
				}

				public void addObject(IStateTransitionDataCode key, Object value) {
					data.put(key, value);
				}
			};
			transitionData.addObject(PeerDataCode.PEER_EVENT,  PCBEvents.TimerExpires);
			this.onStateTransitionTrigger(transitionData);
		}catch(UnhandledTransitionException e){
		} finally {
			if(lockAquired) {
				try{
					timerExpireStateLock.unlock();
				} catch (Exception e) {
					ignoreTrace(e);
				} 
			}
		}
	}

	@Override
	public void setWatchdog() {
		/*
		 * timer.cancel():-
		 * 
		 * Terminates this timer, discarding any currently scheduled tasks. 
		 * Does not interfere with a currently executing task (if it exists). 
		 * Once a timer has been terminated, its execution thread terminates gracefully, 
		 * and no more tasks may be scheduled on it.
		 * 

		timer.cancel();
		timer.purge();
		timer = new Timer();
		timer.schedule(new WatchDogTask(), tw);
		 */
		watchdogTimer = watchdogDuration + System.currentTimeMillis();
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", Rescheduling the watchdog timer to " + watchdogDuration + " ms.");
		}
	}


	@Override
	public void setPending(boolean pending) {
		this.pending.set(pending);
	}

	@Override
	public int getNumDwa() {
		return numDwa.get();
	}

	@Override
	public void incrementNumDwa() {
		this.numDwa.incrementAndGet();		
	}

	@Override
	public void setNumDwa(int numDwa) {
		this.numDwa.set(numDwa);
	}


	private StateEvent getOkayStateEvent(IStateTransitionData transitionData){
		PCBEvents events = (PCBEvents)transitionData.getData(PeerDataCode.PEER_EVENT);
		DiameterPacket request = (DiameterPacket)transitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		StateEvent stateEvent = null;
		if(events!=null){
			switch (events) {
			case ConnectionDown:
				stateEvent = new StateEvent(currentState, PCBEvents.ConnectionDown,PCBStates.DOWN, transitionData);
				break;
			case TimerExpires:
				if(!isPending()){
					stateEvent = new StateEvent(currentState, PCBEvents.TimerExpiresAndNotPending,PCBStates.OKAY, transitionData);
				}else{
					stateEvent = new StateEvent(currentState, PCBEvents.TimerExpiresAndPending,PCBStates.SUSPECT, transitionData);
				}
			default:

				break;
			}
		}else if(request!=null){
			if(isDwa(request)){
				stateEvent = new StateEvent(currentState, PCBEvents.ReceiveDWA,PCBStates.OKAY, transitionData);
			}else{
				stateEvent = new StateEvent(currentState, PCBEvents.ReceiveNonDWA,PCBStates.OKAY, transitionData);
			}
		}

		return stateEvent;
	}

	private StateEvent getSuspectStateEvent(IStateTransitionData transitionData){
		PCBEvents events = (PCBEvents)transitionData.getData(PeerDataCode.PEER_EVENT);
		DiameterPacket request = (DiameterPacket)transitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		StateEvent stateEvent = null;
		if(events!=null){
			switch (events) {
			case ConnectionDown:
				stateEvent = new StateEvent(currentState, PCBEvents.ConnectionDown,PCBStates.DOWN, transitionData);
				break;
			case TimerExpires:
				stateEvent = new StateEvent(currentState, PCBEvents.TimerExpires,PCBStates.DOWN, transitionData);				
			default:				
				break;
			}
		}else if(request!=null){
			if(isDwa(request)){
				stateEvent = new StateEvent(currentState, PCBEvents.ReceiveDWA,PCBStates.OKAY, transitionData);
			}else{
				stateEvent = new StateEvent(currentState, PCBEvents.ReceiveNonDWA,PCBStates.OKAY, transitionData);
			}
		}

		return stateEvent;
	}

	private StateEvent getInitialStateEvent(IStateTransitionData transitionData){
		PCBEvents events = (PCBEvents)transitionData.getData(PeerDataCode.PEER_EVENT);
		DiameterPacket request = (DiameterPacket)transitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		StateEvent stateEvent = null;
		if(events!=null){
			switch (events) {
			case ConnectionUp:
				stateEvent = new StateEvent(currentState, PCBEvents.ConnectionUp,PCBStates.OKAY, transitionData);
				break;
			case TimerExpires:
				stateEvent = new StateEvent(currentState, PCBEvents.TimerExpires,PCBStates.INITIAL, transitionData);
				break;
			default:				
				break;
			}
		}else if(request!=null){
			if(isDwa(request)){
				stateEvent = new StateEvent(currentState, PCBEvents.ReceiveDWA,PCBStates.INITIAL, transitionData);
			}else{
				stateEvent = new StateEvent(currentState, PCBEvents.ReceiveNonDWA,PCBStates.INITIAL, transitionData);
			}
		}

		return stateEvent;
	}

	private StateEvent getReopenStateEvent(IStateTransitionData transitionData){
		PCBEvents events = (PCBEvents)transitionData.getData(PeerDataCode.PEER_EVENT);
		DiameterPacket request = (DiameterPacket)transitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		StateEvent stateEvent = null;
		if(events!=null){
			switch (events) {
			case ConnectionDown:
				stateEvent = new StateEvent(currentState, PCBEvents.ConnectionDown,PCBStates.DOWN, transitionData);
				break;
			case TimerExpires:
				if(isPending()){
					if(getNumDwa()<0)
						stateEvent = new StateEvent(currentState, PCBEvents.TimerExpiresAndPendingAndDWALessThanZero,PCBStates.DOWN, transitionData);
					else
						stateEvent = new StateEvent(currentState, PCBEvents.TimerExpiresAndPendingAndDWANotLessThanZero,PCBStates.REOPEN, transitionData);					
				}else{			
					stateEvent = new StateEvent(currentState, PCBEvents.TimerExpiresAndNotPending,PCBStates.REOPEN, transitionData);
				}
				break;
			default:				
				break;
			}
		}else if(request!=null){
			if(isDwa(request)){
				if(getNumDwa() == 2)
					stateEvent = new StateEvent(currentState, PCBEvents.ReceiveDWAAndNumEqualsTwo,PCBStates.OKAY, transitionData);
				else if(getNumDwa() < 2)
					stateEvent = new StateEvent(currentState, PCBEvents.ReceiveDWAAndNumLessThanTwo,PCBStates.REOPEN, transitionData);
			}else{
				stateEvent = new StateEvent(currentState, PCBEvents.ReceiveNonDWA,PCBStates.REOPEN, transitionData);
			}
		}

		return stateEvent;
	}

	private StateEvent getDownStateEvent(IStateTransitionData transitionData){
		PCBEvents events = (PCBEvents)transitionData.getData(PeerDataCode.PEER_EVENT);
		DiameterPacket request = (DiameterPacket)transitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		StateEvent stateEvent = null;
		if(events!=null){
			switch (events) {
			case ConnectionUp:
				stateEvent = new StateEvent(currentState, PCBEvents.ConnectionUp,PCBStates.REOPEN, transitionData);
				break;
			case TimerExpires:
				stateEvent = new StateEvent(currentState, PCBEvents.TimerExpires,PCBStates.DOWN, transitionData);				
			default:				
				break;
			}
		}else if(request!=null){
			if(isDwa(request)){
				stateEvent = new StateEvent(currentState, PCBEvents.ReceiveDWA,PCBStates.DOWN, transitionData);
			}else{
				stateEvent = new StateEvent(currentState, PCBEvents.ReceiveNonDWA,PCBStates.DOWN, transitionData);
			}
		}

		return stateEvent;
	}

	private boolean isPending() {
		return pending.get();
	}

	private boolean isDwa(DiameterPacket packet){
		return packet.getCommandCode() == CommandCode.DEVICE_WATCHDOG.code && !packet.isRequest();
	}
	private boolean isTimerElapsed(){
		long timeMillies = System.currentTimeMillis();
		long diff = timeMillies -watchdogTimer ;
		if(diff >= -1000) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", Watchdog timer elapsed");
			return true;
		}
		return false;
	}
	
	private class ConnectionInitiatorTask extends BaseIntervalBasedTask{
		private long interval = 30;
		private ConnectionInitiatorTask(long interval){
			this.interval = interval;
		}
		@Override
		public void execute(AsyncTaskContext context) {
			attemptOpen();
		}

		@Override
		public long getInterval() {			
			return interval;
		}

		@Override
		public boolean isFixedDelay() {			
			return true;
		}

		@Override
		public long getInitialDelay() {
			return 10*1000;
		}
		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.MILLISECONDS;
		}

	}	

	private class WatchDogTask extends BaseIntervalBasedTask{
		private long interval = 30;
		private WatchDogTask(long interval){
			this.interval = interval;
		}
		@Override
		public void execute(AsyncTaskContext context) {
			if(isTimerElapsed())
				onTimerElapsed();
		}

		@Override
		public long getInterval() {			
			return interval;
		}

		@Override
		public boolean isFixedDelay() {			
			return true;
		}

		@Override
		public long getInitialDelay() {
			return interval;
		}
		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.MILLISECONDS;
		}

	}	
	public int getTimeout(){
		return 3000;
	}
	@Override
	protected String getKey() {
		return MODULE;
	}	
	
	private void scheduleConnectionInitiatorTask() {

		if(isInitiateConnection() == false || connectionInitiatorTask != null)
			return;

		synchronized (initConnectionLock) {
			if(connectionInitiatorTask != null){
				return;
			}
			connectionInitiatorTask = stackContext.scheduleIntervalBasedTask(new ConnectionInitiatorTask(initiateConnectionDuration));
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Initiate Connection Task scheduled for Peer: " + getPeerName());
	}
	
	protected abstract boolean isInitiateConnection();

	private void scheduleWatchDogTask() {

		if(watchdogDuration <= 0 || watchDogTask != null)
			return;

		synchronized (watchDogLock) {
			if(watchDogTask != null){
				return;
			}
			watchDogTask = stackContext.scheduleIntervalBasedTask(new WatchDogTask(watchdogDuration));
			watchdogTimer = watchdogDuration + System.currentTimeMillis();
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Watch Dog Task scheduled for Peer: " + getPeerName());
	}
	
	private void cancelConnectionInitiatorTask() {
		if(connectionInitiatorTask == null) {
			return;
		}
		boolean isCancelled = true;
		synchronized (initConnectionLock) {
			if(connectionInitiatorTask == null){
				return;
			}
			isCancelled = connectionInitiatorTask.cancel(false); 
			connectionInitiatorTask = null;
		}
		if (isCancelled == false) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Unable to cancel Connection initiator Task for Peer: " + getPeerName());
			return;
		}
		stackContext.purgeCancelledTasks();
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Connection initiator Task cancelled for Peer: " + getPeerName());
	}
	
	private void cancelWatchDogTask() {

		if(watchDogTask == null) {
			return;
		}
		boolean isCancelled = true;
		synchronized (watchDogLock) {
			if(watchDogTask == null){
				return;
			}
			isCancelled = watchDogTask.cancel(false); 
			watchDogTask = null;
		}
		if (isCancelled == false) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Unable to cancel Watch Dog Task for Peer: " + getPeerName());
			return;
		}
		stackContext.purgeCancelledTasks();
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Watch Dog Task cancelled for Peer: " + getPeerName());
	}
}
