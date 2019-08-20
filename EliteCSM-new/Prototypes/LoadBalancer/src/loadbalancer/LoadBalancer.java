package loadbalancer;

import core.Processor;


/**
 * 
 * @author narendra.pathai
 */
public interface LoadBalancer<T, P extends Processor<T>> extends Processor<T> {
	void addProcessor(P processor);
}
