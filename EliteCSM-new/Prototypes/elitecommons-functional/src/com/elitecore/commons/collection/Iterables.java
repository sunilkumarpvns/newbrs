package com.elitecore.commons.collection;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.Iterator;

import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Predicate;


/**
 * 
 * @author narendra.pathai
 *
 */
public class Iterables {

	public static <T> Iterable<T> filter(final Iterable<T> unfiltered,
			final Predicate<? super T> predicate) {
		checkNotNull(unfiltered, "unfiltered is null");
		checkNotNull(predicate, "predicate is null");
		
		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {
				return Iterators.filter(unfiltered.iterator(), predicate);
			}
			
		};
	}

	public static <F, T> Iterable<T> transform(final Iterable<F> iterable, final Function<? super F, T> transformer) {
		return new FluentIterable<T>() {

			@Override
			public Iterator<T> iterator() {
				return Iterators.transform(iterable.iterator(), transformer);
			}
		};
	}
}
