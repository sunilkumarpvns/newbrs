/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.fsm.serverstateful.enums;

/**
 * @author pulin
 *
 */
public enum ServerStateFulState {
	Idle(false),
	Open(false),
	Discon(false),
	;
	
	public final boolean isSync;
	
	ServerStateFulState(boolean isSync) {
		this.isSync = isSync;
	}
}
