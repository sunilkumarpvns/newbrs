/**
 * 
 */
package com.elitecore.diameterapi.core.common.fsm;

import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;

/**
 * @author pulin
 *
 */
public interface IStateMachineListener {

	public void stateSwitched(IStateEnum oldState, IStateEnum newState);
}
