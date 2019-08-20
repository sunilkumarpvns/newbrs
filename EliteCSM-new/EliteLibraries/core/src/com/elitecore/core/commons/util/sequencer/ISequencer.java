package com.elitecore.core.commons.util.sequencer;

import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public interface ISequencer extends Cloneable {
	
	/**
	 * This method will initialize the sequencer with the start and end sequence
	 * @throws InitializationFailedException if the sequence specified is invalid.
	 * For instance if start sequence is 100 and end sequence value is 1 then it will
	 * throw Initialization failed exception
	 */
	public void init() throws InitializationFailedException;
	
	/**
	 * It will return the next incremented sequence value
	 * @return
	 */
	public String getNextSequence();
	
	/**
	 * This will return the current sequence value
	 * NOTE: the value of the sequence will not be incremented if this method is called
	 * @return
	 */
	public String getSequence();
	
	/**
	 * It will just increment the sequence value and the sequence value will be reseted and the sequence 
	 * will again start from the start value
	 */
	public void increment();
	
	public String getPrefix();
	
	public String getSuffix();
	
	public boolean isInitialized();
	
	public String getStartSequence();
	
	public String getEndSequence();
	
	public Object clone() throws CloneNotSupportedException;
}
