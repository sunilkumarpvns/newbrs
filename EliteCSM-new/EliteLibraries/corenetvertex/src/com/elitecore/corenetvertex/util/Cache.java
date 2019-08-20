
package com.elitecore.corenetvertex.util;


public interface Cache<K, V> {

	V put(K key, V value);

	V remove(K key);

	/**
	 * Give Value against the specified Key from cache. if not found from cache it will load Value from {@code CacheLoader}
	 * 
	 * @param key
	 * @throws Exception, if {@code CacheLoader} gives exception during loading Value for specified Key
	 */
	//TODO Check whether to throw generic exception or not
	V get(K key) throws Exception;
	
	int flush();

	int evict();

	/**
	 * Loads Value against the specified Key from {@code CacheLoader}.
	 * 
	 * @param key
	 * @throws Exception, if {@code CacheLoader} gives exception during loading Value for specified Key
	 */
	V refresh(K key) throws Exception;

	CacheStatistics statistics();

	/**
	 * Give Value against the specified Key from cache. It will not try to load key {@code CacheLoader}
	 *
	 * @param key
	 */
	V getWithoutLoad(K key);
}
