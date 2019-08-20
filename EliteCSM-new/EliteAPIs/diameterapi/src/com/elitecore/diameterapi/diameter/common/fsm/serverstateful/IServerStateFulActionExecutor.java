/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.fsm.serverstateful;

import com.elitecore.diameterapi.core.common.fsm.StateEvent;

/**
 * @author pulin
 *
 */
public interface IServerStateFulActionExecutor {
	void atomicActionSendSuccessfulServSpecificAnswer(StateEvent event);
	void atomicActionSendFailedServSpecificAnswer(StateEvent event);
	void atomicActionNone(StateEvent event);
	void atomicActionSendSTA(StateEvent event);
	void atomicActionCleanup(StateEvent event);
	void atomicActionSendASR(StateEvent event);
	void atomicActionWait(StateEvent event);
	void atomicActionResendASR(StateEvent event);

}
