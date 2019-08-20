package loadbalancer;

import java.util.ArrayList;
import java.util.List;

import core.Predicate;
import core.Processor;


/**
 * 
 * @author narendra.pathai
 */
public class FailOverLoadBalancer<T, P extends Processor<T>> extends LoadBalancerSupport<T, P> {
	private PredicateFactory<T> failoverPredicateFactory;
	
	public FailOverLoadBalancer(PredicateFactory<T> factory) {
		this.failoverPredicateFactory = factory;
	}

	public void process(T input) {
		Predicate<T> predicate = failoverPredicateFactory.predicate();
		List<P> processorsLocal = new ArrayList<P>(getProcessors());
		int index = 0;
		
		while (predicate.apply(input)) {
			if (index == processorsLocal.size()) {
				break;
			}
			processorsLocal.get(index++).process(input);
		}
	}

	@Override
	public double averageResponseTime() {
		// TODO Auto-generated method stub
		return 0;
	}
}