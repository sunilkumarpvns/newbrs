package loadbalancer;

import java.util.List;
import java.util.Random;

import core.Processor;


/**
 * 
 * @author narendra.pathai
 */
public class RandomLoadBalance<T, P extends Processor<T>> implements LoadBalanceStrategy<T, P> {
	private Random rand = new Random(System.currentTimeMillis());
	
	public int choose(List<P> processors, T input) {
		return rand.nextInt(processors.size());
	}
}
