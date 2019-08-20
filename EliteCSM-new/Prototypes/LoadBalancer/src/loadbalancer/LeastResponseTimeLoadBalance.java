package loadbalancer;

import java.util.List;

import core.Processor;


/**
 * 
 * @author narendra.pathai
 */
public class LeastResponseTimeLoadBalance<T, P extends Processor<T>> implements LoadBalanceStrategy<T, P>{
	
	@Override
	public int choose(List<P> processors, T input) {
		int index = 0;
		for (int i = 1; i < processors.size(); i++) {
			if (processors.get(i).averageResponseTime() < processors.get(index).averageResponseTime()) {
				index = i;
			}
		}
		return index;
	}
}
