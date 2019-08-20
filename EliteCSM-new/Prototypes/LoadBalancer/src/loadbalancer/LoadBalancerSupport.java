package loadbalancer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import core.Processor;


/**
 * 
 * @author narendra.pathai
 */
public abstract class LoadBalancerSupport<T, P extends Processor<T>> implements LoadBalancer<T, P> {
	private List<P> processors = new CopyOnWriteArrayList<P>();
	
	public void addProcessor(P processor) {
		this.processors.add(processor);
	}
	
	public List<P> getProcessors() {
		return processors;
	}
}
