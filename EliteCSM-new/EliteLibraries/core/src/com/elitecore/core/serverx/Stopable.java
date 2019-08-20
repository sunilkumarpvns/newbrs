package com.elitecore.core.serverx;

/**
 * 
 * Implementor of this interface indicates that implementor should be stopped before 
 * shutting down the server.<br/>
 * 
 * You can register implementor through <tt>registerStopable</tt> method of {@link ServerContext}. 
 * This will in turn call the <tt>stop</tt> method from implementor.
 * 
 * @author malav.desai
 *
 */
public interface Stopable {

	/**
	 * Implementation of this method must free all the resources acquired by
	 * the component.<br/>
	 * 
	 * Can be used to do other processes before stopping the component.
	 * 
	 * @param none
	 * @return void
	 */
	public void stop();
	
}
