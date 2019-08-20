package com.elitecore.commons.collection;

import java.util.Iterator;

import com.elitecore.base.Consumer;
import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Predicate;

public abstract class FluentIterable<E> implements Iterable<E> {
	private final Iterable<E> iterable;
	
	public FluentIterable() {
		this.iterable = this;
	}
	
	public <T> FluentIterable<T> transform(final Function<? super E, T> transformer) {
		return from(Iterables.transform(iterable, transformer));
	}
	
	public FluentIterable<E> filter(final Predicate<? super E> predicate) {
		return from(Iterables.filter(iterable, predicate));
	}
	
	public static <E> FluentIterable<E> from(final Iterable<E> iterable) {
		return (iterable instanceof FluentIterable) 
			? (FluentIterable<E>) iterable 
			: new FluentIterable<E>() {
				
				@Override
				public Iterator<E> iterator() {
					return iterable.iterator();
				}
			}; 
	}
	
	public void forEach(Consumer<? super E> consumer) {
		for (E element : this) {
			consumer.accept(element);
		}
	}
}
