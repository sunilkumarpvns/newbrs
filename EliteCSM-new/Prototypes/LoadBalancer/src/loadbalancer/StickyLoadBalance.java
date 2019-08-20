package loadbalancer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import core.Function;
import core.Key;
import core.Processor;


/**
 * 
 * @author narendra.pathai
 */
public class StickyLoadBalance<T, P extends Processor<T>> implements LoadBalanceStrategy<T, P> {
	private Function<T, Key> keyGenerator;
	//Need some timeout logic here
	private Map<Key, Integer> keyToProcessorIndex = new HashMap<Key, Integer>();
	private Random rand = new Random(System.currentTimeMillis());
	
	public StickyLoadBalance(Function<T, Key> function) {
		keyGenerator = function;
	}

	public int choose(List<P> processors, T input) {
		Key key = keyGenerator.apply(input);
		Integer index = keyToProcessorIndex.get(key);
		if (index == null) {
			index = (int) rand.nextInt(processors.size());
			this.keyToProcessorIndex.put(key, index);
		}
		return index;
	}
}
