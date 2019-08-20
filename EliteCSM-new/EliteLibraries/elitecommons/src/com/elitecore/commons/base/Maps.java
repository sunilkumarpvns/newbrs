package com.elitecore.commons.base;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A set of commonly used utilities for {@link Map}s.
 * 
 * @author narendra.pathai
 *
 */
public class Maps {

	/**
	 * Checks whether the map is {@code null} or empty
	 * @param map a nullable map
	 * @return true if map is either {@code null} or empty, false otherwise
	 */
	public static boolean isNullOrEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * Before JDK 7, constructing new generic collections requires unpleasant code duplication:
	 * {@code Map<TypeThatsTooLongForItsOwnGood, AnotherTypeThatsTooLong> map = 
	 *	 new HashMap<TypeThatsTooLongForItsOwnGood, AnotherTypeThatsTooLong>();}
	 * 
	 * <p>But using these static factory methods, the type on the right side is automatically inferred:
	 * {@code Map<TypeThatsTooLongForItsOwnGood, AnotherTypeThatsTooLong> map = Maps.newHashMap();} 
	 * @return a new empty {@code HashMap}
	 */
	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}
	
	/**
	 * Before JDK 7, constructing new generic collections requires unpleasant code duplication:
	 * {@code Map<TypeThatsTooLongForItsOwnGood, AnotherTypeThatsTooLong> map = 
	 *	 new LinkedHashMap<TypeThatsTooLongForItsOwnGood, AnotherTypeThatsTooLong>();}
	 * 
	 * <p>But using these static factory methods, the type on the right side is automatically inferred:
	 * {@code Map<TypeThatsTooLongForItsOwnGood, AnotherTypeThatsTooLong> map = Maps.newLinkedHashMap();} 
	 * @return a new empty {@code LinkedHashMap}
	 */
	public static <K, V> HashMap<K, V> newLinkedHashMap() {
		return new LinkedHashMap<K, V>();
	}
}
