/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.fsm.serverstateful;

import java.util.List;

import com.elitecore.diameterapi.core.common.fsm.IStateTransitionData;
import com.elitecore.diameterapi.core.common.fsm.State;
import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.StateMachine;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;

/**
 * @author pulin
 *
 */
public abstract class ServerStateFulStateMachine extends StateMachine implements IServerStateFulActionExecutor{

	/**
	 * 
	 */
	public ServerStateFulStateMachine() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param state
	 */
	public ServerStateFulStateMachine(IStateEnum state) {
		super(state);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.elitecore.diameterapi.core.common.fsm.StateMachine#createStateEvent(com.elitecore.diameterapi.core.common.fsm.IStateTransitionData)
	 */
	@Override
	protected StateEvent createStateEvent(IStateTransitionData transitionData) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.diameterapi.core.common.fsm.StateMachine#createStates()
	 */
	@Override
	protected List<State> createStates() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.diameterapi.core.common.fsm.IAtomicActionsExecutor#act()
	 */
	@Override
	public void act() {
		// TODO Auto-generated method stub

	}

}
