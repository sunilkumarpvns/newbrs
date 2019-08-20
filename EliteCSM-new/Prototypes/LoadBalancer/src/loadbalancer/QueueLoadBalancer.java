package loadbalancer;

import core.Processor;


/**
 * 
 * @author narendra.pathai
 */
public class QueueLoadBalancer<T, P extends Processor<T>> extends LoadBalancerSupport<T, P> {
	private LoadBalanceStrategy<T, P> loadBalanceStrategy;
	
	public QueueLoadBalancer() {
		this.loadBalanceStrategy = new RoundRobinLoadBalance<T, P>();
	}
	
	public QueueLoadBalancer(LoadBalanceStrategy<T, P> loadBalanceStrategy) {
		this.loadBalanceStrategy = loadBalanceStrategy;
	}
	
	public LoadBalanceStrategy<T, P> getLoadBalanceStrategy() {
		return loadBalanceStrategy;
	}

	public void setLoadBalanceStrategy(LoadBalanceStrategy<T, P> loadBalanceStrategy) {
		this.loadBalanceStrategy = loadBalanceStrategy;
	}

	public void process(T input) {
		if (getProcessors().isEmpty()) {
			return;
		}
		
		int index = loadBalanceStrategy.choose(getProcessors(), input);
		getProcessors().get(index).process(input);
	}

	@Override
	public double averageResponseTime() {
		// TODO Auto-generated method stub
		return 0;
	}
}
