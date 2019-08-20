package com.elitecore.netvertex.core.util;

import com.elitecore.corenetvertex.commons.Predicate;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class Predicates {
	
	
	public static <T1, T2> Predicate<T1, T2> alwaysTrue() {
	
		return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
	}
	
	public static <T1, T2> Predicate<T1, T2> alwaysFalse() {
		
		return ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
	}
	
	public static final RequestNumberPredicateForRequest requestNumberPredicateForRequest() {
		
		return RequestNumberPredicateForRequest.getInstance();
	}
	
	public static final RequestNumberPredicateForResponse requestNumberPredicateForResponse() {
		
		return RequestNumberPredicateForResponse.getInstance();
	}

	enum ObjectPredicate implements Predicate<Object, Object> {

		ALWAYS_TRUE {

			@Override
			public boolean apply(Object t1, Object t2) {
				return true;
			}
			
		},
		ALWAYS_FALSE {

			@Override
			public boolean apply(Object t1, Object t2) {
				return false;
			}
			
		};
		
		@SuppressWarnings("unchecked")
		<T1, T2> Predicate<T1, T2> withNarrowedType() {
			return (Predicate<T1, T2>) this;
	    }

	}
}
