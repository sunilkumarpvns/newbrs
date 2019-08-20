/**
 * 
 */
package com.elitecore.diameterapi.core.common.transport;

/**
 * @author pulin
 *
 */
public enum ConnectorState {

	NOT_STARTED,
	STARTUP_IN_PROGRESS,
	RUNNING,
	BLOCKED_EXECUTION,
	SHUTDOWN_IN_PROGRESS,
	STOPPED;
}
