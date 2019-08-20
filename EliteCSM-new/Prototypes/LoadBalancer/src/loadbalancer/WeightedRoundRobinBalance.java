package loadbalancer;

import java.util.List;


/**
 * 
 * @author narendra.pathai
 */
public class WeightedRoundRobinBalance<T, P extends WeightedProcessor<T>>
implements LoadBalanceStrategy<T, P>{

	@Override
	public int choose(List<P> processors, T input) {
		processors.get(0).weightage();
		return 0;
	}

}
