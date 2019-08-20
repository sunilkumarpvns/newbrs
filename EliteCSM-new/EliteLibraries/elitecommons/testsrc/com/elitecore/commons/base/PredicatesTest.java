package com.elitecore.commons.base;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class PredicatesTest {

	@Test
	public void testNonNull_ShouldReturnPredicateReference_ThatEvaluatesToTrueIfNonNullReferenceIsPassed() {
		assertTrue(Predicates.nonNull().apply(new Object()));
	}
	
	@Test
	public void testNonNull_ShouldReturnPredicateReference_ThatEvaluatesToFalseIfNullReferenceIsPassed() {
		assertFalse(Predicates.nonNull().apply(null));
	}
	
	@Test
	@Parameters(method = "datafor_testAlwaysTrue_ShouldReturnTrueForAllObjects")
	public void testAlwaysTrue_ShouldReturnTrueForAllObjects(Object input) {
		assertTrue(Predicates.alwaysTrue().apply(input));
	}
	
	public Object[] datafor_testAlwaysTrue_ShouldReturnTrueForAllObjects() {
		return $(
				$((Object)null),
				$(new Object()),
				$("any"),
				$(false),
				$(1)
		);
	}
	
	@Test
	public void testAnd_ShouldPerformLogicalAndOnBothThePredicates_AndResultantPredicateShouldReturnFalseIfFirstPredicateIsNotSatisfied() {
		Predicate<Integer> resultant = Predicates.and(Predicates.nonNull(), Numbers.POSITIVE_INT);
		
		assertFalse(resultant.apply(null));
	}
	
	@Test
	public void testAnd_ShouldPerformLogicalAndOnBothThePredicates_AndResultantPredicateShouldReturnFalseIfOtherPredicateIsNotSatisfied() {
		Predicate<Integer> resultant = Predicates.and(Predicates.nonNull(), Numbers.POSITIVE_INT);
		
		assertFalse(resultant.apply(anyNegativeInteger()));
	}
	
	private Integer anyNegativeInteger() {
		return -1;
	}

	@Test
	public void testAnd_ShouldPerformLogicalAndOnBothThePredicates_AndResultantPredicateShouldReturnTrueIfBothPredicatesAreSatisfied() {
		Predicate<Integer> resultant = Predicates.and(Predicates.nonNull(), Numbers.POSITIVE_INT);
		
		assertTrue(resultant.apply(1));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testAnd_ShouldShortCircuitTheConditionAndNotEvaluateTheOtherPredicate_IfFirstPredicateIsNotSatisfied() {
		Predicate<Integer> mock = mock(Predicate.class);
		
		Predicate<Integer> resultant = Predicates.and(Predicates.nonNull(), mock);
		
		resultant.apply(null);
		
		verify(mock, never()).apply(null);
	}
}
