package loadbalancer;

import core.Predicate;


/**
 * 
 * @author narendra.pathai
 */
public interface PredicateFactory<T> {
	Predicate<T> predicate();
}
