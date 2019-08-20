package com.elitecore.commons.collection;

import java.util.Iterator;

import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Predicate;

/**
 * 
 * @author narendra.pathai
 *
 */
public class Iterators {
	
	/**
	 * 
	 * @param <T>
	 * @param iterator
	 * @param predicate
	 * @return
	 */
	public static <T> Iterator<T> filter(final Iterator<T> iterator, final Predicate<? super T> predicate) {
		return new AbstractIterator<T>() {
			@Override
			protected T computeNext() {
				while (iterator.hasNext()) {
					T element = iterator.next();
					if (predicate.apply(element)) {
						return element;
					}
				}
				return endOfData();
			}
		};
	}

	public static <F, T> Iterator<T> transform(final Iterator<F> iterator,
			final Function<? super F, T> transformer) {
		
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public T next() {
				return transformer.apply(iterator.next());
			}

			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}
}
