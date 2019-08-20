package loadbalancer;

import java.util.List;

import core.Processor;


/**
 * 
 * @author narendra.pathai
 */
public class RoundRobinLoadBalance<T, P extends Processor<T>> implements LoadBalanceStrategy<T, P>{
	private int index = 0;
	
	public int choose(List<P> processors, T input) {
		if (index == processors.size()) {
			index = 0;
		}
		
		return index++;
	}
}
