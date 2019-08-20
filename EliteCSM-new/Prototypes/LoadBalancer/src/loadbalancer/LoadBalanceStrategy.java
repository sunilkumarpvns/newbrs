package loadbalancer;

import java.util.List;

import core.Processor;


/**
 * 
 * @author narendra.pathai
 */
public interface LoadBalanceStrategy<T, P extends Processor<T>> {
	int choose(List<P> processors, T input);
}
