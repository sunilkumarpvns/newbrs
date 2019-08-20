package com.elitecore.diameterapi.core.common.fsm;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;
import com.elitecore.diameterapi.core.common.fsm.enums.StateEnum;
import com.elitecore.diameterapi.core.common.fsm.exception.UnhandledTransitionException;
import com.elitecore.diameterapi.core.common.router.exception.InvalidRoutingPacketException;

/**
 * @author pulindani
 * @version 1.0
 * @created 17-Jul-2008 3:15:59 PM
 */
public abstract class StateMachine implements IStateMachine, IAtomicActionsExecutor {
	private String MODULE = "STATE-MACHNE";
	protected IStateEnum currentState = StateEnum.NOT_INITIALIZED;
	private long elapsedTime;
	protected IStateMachineContext stateMachineContext;
	
	private final List<State> states;
	
	private final ReentrantLock stateLock;
	
	private final IStateMachineListener stateMachineListener;
	/**
	 * 
	 */
	public StateMachine() {
		this(StateEnum.UNKNOWN);
	}

	/**
	 * 
	 */
	public StateMachine(IStateEnum state) {
		this.currentState = state;
		this.elapsedTime = System.currentTimeMillis(); 
		this.stateMachineContext = createStateMachineContext();
		this.states = createStates();
		this.stateLock = new ReentrantLock();
		this.stateMachineListener = getStateMachineListener();
		this.MODULE = getKey();
	}

	public final void onStateTransitionTrigger(IStateTransitionData stateTransitionData)throws UnhandledTransitionException{
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", Current State : " + currentState);
		try {
			if(currentState.isSync() || stateLock.isLocked()) {
				executeSyncState(stateTransitionData);
			}else {
				StateEvent stateEvent = createStateEvent(stateTransitionData);
				checkStateEvent(stateEvent);
				if(stateEvent.isSyncEvent() || stateLock.isLocked()) {
					executeSyncStateEvent(stateEvent);
				}else {
					processStateTransitionTrigger(stateEvent);
				}
			}
		}catch(UnhandledTransitionException e) {
			throw e;
		} catch (InvalidRoutingPacketException e){
			throw e;
		} catch(Exception e) {
			throw new UnhandledTransitionException(e);
		}
	}
	
	private void executeSyncState(IStateTransitionData stateTransitionData) throws UnhandledTransitionException{
		boolean bLocked = false;
		try {
			if (!stateLock.tryLock(1, TimeUnit.SECONDS)) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Unable to acquire State Machine Lock for Current State: " 
					+ currentState + ", Attempting Again.");
				if (!stateLock.tryLock(1, TimeUnit.SECONDS)) {
					LogManager.getLogger().error(MODULE, "Unable to acquire State Machine Lock for Current State: " + currentState  + ", Discarding event");
					return;
				}
			}
			bLocked = true;
			if(!currentState.isSync()) {
				StateEvent stateEvent = createStateEvent(stateTransitionData);
				checkStateEvent(stateEvent);
				if(!stateEvent.isSyncEvent()) {
					stateLock.unlock();
					bLocked = false;
					processStateTransitionTrigger(stateEvent);
				}else {
					processSyncStateTransitionTrigger(stateEvent);
					stateLock.unlock();
					bLocked = false;
				}
			}else {
				StateEvent stateEvent = createStateEvent(stateTransitionData);
				checkStateEvent(stateEvent);
				processSyncStateTransitionTrigger(stateEvent);
				stateLock.unlock();
				bLocked = false;
			}
		}catch(UnhandledTransitionException e) {
			throw e;
		}catch(Exception e) {
			throw new UnhandledTransitionException(e);
		}finally {
			if(bLocked)
				stateLock.unlock();
		}
	}
	
	private void executeSyncStateEvent(StateEvent stateEvent) throws UnhandledTransitionException{
		boolean bLocked = false;
		try {
			if (!stateLock.tryLock(1, TimeUnit.SECONDS)) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Unable to acquire State Machine Lock for Current State: " 
					+ currentState + ", Attempting Again.");
				if (!stateLock.tryLock(1, TimeUnit.SECONDS)) {
					LogManager.getLogger().error(MODULE, "Unable to acquire State Machine Lock for Current State: " + currentState + ", Discarding event");
					return;
				}
			}
			bLocked = true;
			if(currentState != stateEvent.getStateIdentifier()) {
				stateEvent = createStateEvent(stateEvent.getStateTransitionData());
				checkStateEvent(stateEvent);
				if(!stateEvent.isSyncEvent()) {
					stateLock.unlock();
					bLocked = false;
					processStateTransitionTrigger(stateEvent);
				}else {
					processSyncStateTransitionTrigger(stateEvent);
					stateLock.unlock();
					bLocked = false;
				}
			}else {
				processSyncStateTransitionTrigger(stateEvent);
				stateLock.unlock();
				bLocked = false;
			}
		}catch(UnhandledTransitionException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Peer: " + getPeerName() + ", " +e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw e;
		}catch(Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Peer: " + getPeerName() + ", " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new UnhandledTransitionException(e);
		}finally {
			if(bLocked)
				stateLock.unlock();
		}
	}
	
	private void processSyncStateTransitionTrigger(StateEvent stateEvent) {
		fetchCurrentState().processEvent(stateEvent);
		do {
			fetchCurrentState().exitAction(stateEvent);
			switchCurrentStateTo(stateEvent.getStateIdentifier(), stateEvent.getNextStateIdentifier());
			
			stateEvent = fetchCurrentState().entryAction(stateEvent);
		}while(stateEvent != null && stateEvent.isSyncEvent());
	}
	
	private void processStateTransitionTrigger(StateEvent stateEvent) {
		fetchCurrentState().processEvent(stateEvent);
	}
	
	private void notifyListener(IStateEnum oldState, IStateEnum newState) {
		if(this.stateMachineListener != null)
			this.stateMachineListener.stateSwitched(oldState, newState);
	}
	
	/**
	 * 
	 * @param state    state
	 */
	public final void switchCurrentStateTo(IStateEnum oldState, IStateEnum newState){
		if(oldState != newState) {
			if(currentState == oldState) {
				try {
					if(currentState == oldState) {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", " + this.currentState + " state is changed to " + newState + " state.");
						elapsedTime = System.currentTimeMillis();
						this.currentState = newState;
						notifyListener(oldState, newState);
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", Cannot switch from " + oldState + " to " + newState + ", because current state is " + currentState);
					}
				}catch(Exception e) {
					LogManager.getLogger().trace(MODULE, e);
				}
			}else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", Cannot switch from " + oldState + " to " + newState + ", because current state is " + currentState);
			}
		}
	}
	public final long getStateDuration(){
		return System.currentTimeMillis() - elapsedTime;
	}
	public int getCurrentState(){
		return currentState.stateOrdinal();
	}
	/**
	 * 
	 */
	public State fetchCurrentState() {
		return states.get(currentState.stateOrdinal());
	}
	
	/**
	 * 
	 * @param transitionData
	 * @return
	 */
	protected abstract StateEvent createStateEvent(IStateTransitionData transitionData);
	
	protected abstract String getPeerName();
	
	/**
	 * 
	 */
	protected abstract List<State> createStates();
	
	/**
	 * 
	 */
	public IStateEnum currentState() {
		return currentState;
	}
	
	private void checkStateEvent(StateEvent stateEvent) throws UnhandledTransitionException{
		if(stateEvent == null) {
			throw new UnhandledTransitionException("Event can't be decided " +
					"as per received transition data, peer: " + getPeerName() + 
					" remains in same state: " + currentState);
		}else if(stateEvent.getStateIdentifier() == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", Invalid Event : " + stateEvent);
			throw new UnhandledTransitionException("Invalid Event, peer remains in same state: " + currentState);
		}else if(stateEvent.getEventIdentifier() == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", Invalid Event : " + stateEvent);
			throw new UnhandledTransitionException("Invalid Event, peer remains in same state: " + currentState);
		}else if(stateEvent.getNextStateIdentifier() == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", Invalid Event : " + stateEvent);
			throw new UnhandledTransitionException("Invalid Event, peer remains in same state: " + currentState);
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", Raised Transition : " + stateEvent);
		}
	}
	
	protected String getKey() {
		return "STATE-MACHINE";
	}
	
	protected IStateMachineContext createStateMachineContext() {
		return new BaseStateMachineContext();
	}

	protected IStateMachineContext getStateMachineContext() {
		return this.stateMachineContext;
	}
	
	public boolean stop() {
		return true;
	}
	
	public IStateMachineListener getStateMachineListener() {
		return null;
	}
}