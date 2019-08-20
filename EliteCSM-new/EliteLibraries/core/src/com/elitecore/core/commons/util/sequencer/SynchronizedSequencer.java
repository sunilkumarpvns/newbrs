package com.elitecore.core.commons.util.sequencer;

import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class SynchronizedSequencer extends Sequencer{

	private static final long serialVersionUID = -2909131645793062370L;
	private Integer sequencerLock = new Integer(-1);
	
	public SynchronizedSequencer(String startSequence, String endSequence,String prefix, String suffix) {
		super(startSequence, endSequence, prefix, suffix);
	}
	
	@Override
	public void init() throws InitializationFailedException{
		super.init();
	}
	
	
	@Override
	public String getNextSequence(){
		synchronized (sequencerLock) {
			return super.getNextSequence();
		}
		
	}
	
	@Override
	public void increment(){
		synchronized (sequencerLock) {
			super.increment();
		}
	}
	
	@Override
	public String getSequence(){
		synchronized (sequencerLock) {
			return super.getSequence();
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		return (SynchronizedSequencer)super.clone();
	}
}

	

