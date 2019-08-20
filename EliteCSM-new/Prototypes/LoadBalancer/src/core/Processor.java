package core;


/**
 * 
 * @author narendra.pathai
 */
public interface Processor<T> {
	void process(T input);
	
	/* Counters */
	double averageResponseTime();
}
