package loadbalancer;

import core.Processor;


/**
 * 
 * @author narendra.pathai
 */
public interface WeightedProcessor<T> extends Processor<T> {
	public int weightage();
}
