package com.elitecore.commons.base;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A set of commonly used utilities for {@link Collection}s. Named as Collectionz to avoid
 * conflict with {@link java.util.Collections}
 * 
 * @author narendra.pathai
 */
public class Collectionz {

	/**
	 * Checks whether the collection is null or empty.
	 * 
	 * @param collection collection to be checked
	 * @return true if the collection is null or empty, false otherwise
	 */
	public static boolean isNullOrEmpty(Collection<?> collection) {
		return collection == null ? true : collection.isEmpty(); 
	}

	/**
	 * Before JDK 7, constructing new generic collections requires unpleasant code duplication:
	 * {@code List<TypeThatsTooLongForItsOwnGood> list = new ArrayList<TypeThatsTooLongForItsOwnGood>();}
	 * 
	 * <p>But using these static factory methods, the type on the right side is automatically inferred:
	 * {@code List<TypeThatsTooLongForItsOwnGood> list = Collectionz.newArrayList();} 
	 * @return a new empty {@code ArrayList}
	 */
	public static <T> ArrayList<T> newArrayList() {
		return new ArrayList<T>();
	}

	/**
	 * Before JDK 7, constructing new generic collections requires unpleasant code duplication:
	 * {@code List<TypeThatsTooLongForItsOwnGood> list = new LinkedList<TypeThatsTooLongForItsOwnGood>();}
	 * 
	 * <p>But using these static factory methods, the type on the right side is automatically inferred:
	 * {@code List<TypeThatsTooLongForItsOwnGood> list = Collectionz.newLinkedList();} 
	 * @return a new empty {@code LinkedList}
	 */
	public static <T> LinkedList<T> newLinkedList() {
		return new LinkedList<T>();
	}

	/**
	 * Before JDK 7, constructing new generic collections requires unpleasant code duplication:
	 * {@code Set<TypeThatsTooLongForItsOwnGood> set = new HashSet<TypeThatsTooLongForItsOwnGood>();}
	 * 
	 * <p>But using these static factory methods, the type on the right side is automatically inferred:
	 * {@code Set<TypeThatsTooLongForItsOwnGood> set = Collectionz.newHashSet();} 
	 * @return a new empty {@code HashSet}
	 */
	public static <T> HashSet<T> newHashSet() {
		return new HashSet<T>();
	}

	/**
	 * Before JDK 7, constructing new generic collections requires unpleasant code duplication:
	 * {@code Set<TypeThatsTooLongForItsOwnGood> set = new LinkedHashSet<TypeThatsTooLongForItsOwnGood>();}
	 * 
	 * <p>But using these static factory methods, the type on the right side is automatically inferred:
	 * {@code Set<TypeThatsTooLongForItsOwnGood> set = Collectionz.newLinkedHashSet();} 
	 * @return a new empty {@code LinkedHashSet}
	 */
	public static <T> LinkedHashSet<T> newLinkedHashSet() {
		return new LinkedHashSet<T>();
	}

	/**
	 * Transforms the collection to map using the type of collection {@code V} as value type in map
	 * and uses the {@code keyFormationFunction} to form keys of type {@code K} for each entry in collection.
	 * 
	 * @param <K> the type of key in map
	 * @param <V> the type of value in map, same as type of value in collection
	 * @param collection a non-null collection which will be transformed to map
	 * @param valueToKeyFunction a non-null function that will form key based on value
	 * @return map created using all elements in collection
	 */
	public static <K,V> Map<K, V> asHashMap(Collection<V> collection, Function<V, K> valueToKeyFunction) {
		checkNotNull(collection, "collection is null");
		checkNotNull(valueToKeyFunction, "valueToKeyFunction is null");

		Map<K,V> map = new HashMap<K, V>();
		for(V element : collection){
			map.put(valueToKeyFunction.apply(element),element);
		}
		return map;
	}
	
	/**
	 * Transforms the collection to map using the type of collection {@code V} as value type in map
	 * and uses the {@code keyFormationFunction} to form keys of type {@code K} for each entry in collection.
	 * 
	 * @param <K> the type of key in map
	 * @param <V> the type of value in map, same as type of value in collection
	 * @param collection a non-null collection which will be transformed to map
	 * @param valueToKeyFunction a non-null function that will form key based on value
	 * @return map created using all elements in collection
	 */
	public static <K,V> Map<K, V> asLinkedHashMap(Collection<V> collection, Function<V, K> valueToKeyFunction) {
		checkNotNull(collection, "collection is null");
		checkNotNull(valueToKeyFunction, "valueToKeyFunction is null");

		Map<K,V> map = new LinkedHashMap<K, V>();
		for(V element : collection){
			map.put(valueToKeyFunction.apply(element),element);
		}
		return map;
	}

	/**
	 * From the given collections returns the first collection that is non-null and non-empty
	 * wrapped in optional.
	 * If no suitable collection is found then returns an {@link Optional#absent()}. Before
	 * getting the collection check to see if it is present using {@link Optional#isPresent()}.
	 * 
	 * @param <T> any sub-type of collection
	 * @param collections a non-null collection array
	 * @return optional instance containing collection if a non-null and non-empty collection
	 * is found, {@code Optional.absent()} otherwise 
	 */
	public static <T extends Collection<?>> Optional<T> firstNonEmpty(T... collections) {
		for(T collection : collections) {
			if(Collectionz.isNullOrEmpty(collection) == false) {
				return Optional.of(collection);
			}
		}
		return Optional.absent();
	}

	/**
	 * Returns a list as a resultant of applying function {@code mapper} to each element
	 * of the collection {@code coll}. Returns an empty mutable list if {@code coll} is
	 * {@code null} or if the collection is empty.  
	 * 
	 * @param <X> type of elements in source collection
	 * @param <Y> type of elements in mapped collection
	 * @param coll source collection on which mapping will be applied
	 * @param mapper a non-null function
	 * @return resultant list obtained by applying function {@code mapper} on collection, 
	 * empty mutable list if {@code coll} is null or empty
	 * @throws NullPointerException is mapper function is null
	 */
	public static <X, Y> List<Y> map(Collection<X> coll, Function<X, Y> mapper) {
		checkNotNull(mapper, "mapper function is null");

		if (isNullOrEmpty(coll)) {
			return new ArrayList<Y>(0);
		}

		List<Y> result = new ArrayList<Y>();
		for (X element : coll) {
			result.add(mapper.apply(element));
		}
		return result;
	}

	/**
	 * Filters the elements from the collection that don't satisfy the predicate passed.
	 * <p>This method eagerly evaluates the filter and removes the elements from the passed
	 * {@code unfiltered} collection. Uses {@link Iterator#remove()} method to remove elements
	 * that don't satisfy the filter, so remove method MUST be supported for this method
	 * to work.
	 * 
	 * <p><b>NOTE:</b> This method eagerly evaluates the filter giving O(n) complexity.
	 * 
	 * <p>Usage:
	 * <pre><code>
	 * Collection&lt;String&gt; unfiltered = ...
	 * Collectionz.filter(unfiltered, Predicates.nonNull());
	 * </code></pre>
	 * 
	 * @param <T> type of elements in the collection
	 * @param unfiltered a non-null collection to be filtered
	 * @param predicate a non-null filter which will be applied to all elements in the collection
	 * @throws NullPointerException if any parameter passed is null
	 */
	public static <T> void filter(Collection<T> unfiltered,
			Predicate<? super T> predicate) {
		checkNotNull(unfiltered, "collection is null");
		checkNotNull(predicate, "predicate is null");
		
		Iterator<? extends T> iterator = unfiltered.iterator();
		while (iterator.hasNext()) {
			if (predicate.apply(iterator.next()) == false) {
				iterator.remove();
			}
		}
	}

}
