package com.elitecore.diameterapi.core.common.fsm;

/**
 * 
 * @author pulindani
 *
 */
public interface State {
	   /**
     *  Action that should be taken each time this state is entered
     */
    public StateEvent entryAction(StateEvent event);

    /**
     * Action that should be taken each time this state is exited
     */
    public void exitAction(StateEvent event);

    
    /**
     * 
     */
    public boolean processEvent(StateEvent stateEvent);
    
    public StateEvent getStateEvent(IStateTransitionData stateTransitionData);
    
}
