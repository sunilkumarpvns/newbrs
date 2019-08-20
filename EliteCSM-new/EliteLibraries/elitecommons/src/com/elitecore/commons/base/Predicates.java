package com.elitecore.commons.base;


/**
 * A set of utility methods for {@link Predicate} and some default predicates used
 * commonly such as {@link #nonNull()}.
 * 
 * @author narendra.pathai
 *
 */
public class Predicates {

	/**
	 * Returns a predicate which evaluates to true if a reference passed is non-null
	 * @param <T>
	 * 
	 * @return a predicate which evaluates to true if a reference passed is non-null
	 */
	public static <T> Predicate<T> nonNull() {
		return ObjectPredicate.NON_NULL.withNarrowedType();
	}
	
	
	/**
	 * Returns a predicate which always return true
	 * 
	 * @return a predicate which evaluates to true
	 */
	public static <T>  Predicate<T> alwaysTrue() {
		return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
	}
	
	/**
	 * Returns a predicate which always return false
	 * 
	 * @return a predicate which evaluates to false
	 */
	public static <T>  Predicate<T> alwaysFalse() {
		return ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
	}
	
	enum ObjectPredicate implements Predicate<Object> {
		ALWAYS_TRUE {

			@Override
			public boolean apply(Object object) {
				return true;
			}
			
		},
		ALWAYS_FALSE {

			@Override
			public boolean apply(Object object) {
				return false;
			}
			
		},
		NON_NULL {

			@Override
			public boolean apply(Object input) {
				return input != null;
			}
			
			
		};

		@SuppressWarnings("unchecked")
		<T> Predicate<T> withNarrowedType() {
			return (Predicate<T>) this;
	    }

	}

	/**
	 * Returns a predicate that evaluates to {@code true} if both of its component
	 * predicates evaluate to {@code true}. The component predicates are evaluated
	 * in order and are "short circuited" as soon as first a false predicate is 
	 * found.
	 */
	public static <T> Predicate<T> and(Predicate<? super T> first,
			Predicate<T> second) {
		return new AndPredicate<T>(first, second);
	}
	
	private static class AndPredicate<T> implements Predicate<T> {
		private final Predicate<? super T> first;
		private final Predicate<? super T> second;
		
		public AndPredicate(Predicate<? super T> first,
				Predicate<? super T> second) {
			this.first = first;
			this.second = second;
		}
		
		@Override
		public boolean apply(T input) {
			return first.apply(input) && second.apply(input);
		}
	}
}
