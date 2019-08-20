package com.elitecore.commons.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Predicates;
import com.elitecore.commons.base.Strings;


public class IterablesTest {
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testFilter_ShouldThrowNPE_IfUnfilteredIterableIsNull() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("unfiltered is null");
		
		Iterables.filter(null, anyPredicate());
	}
	
	private Predicate<Object> anyPredicate() {
		return Predicates.alwaysTrue();
	}
	
	@Test
	public void testFilter_ShouldThrowNPE_IfPredicateIsNull() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("predicate is null");
		
		Iterables.filter(anyCollection(), null);
	}

	private Iterable<Object> anyCollection() {
		return Collectionz.newArrayList();
	}
	
	@Test
	public void testFilter_ShouldFilterElements_WhichDontSatisfyThePredicate() {
		List<Integer> unfiltered = Arrays.asList(-1, 0, 1, null, 2, 3, null, 4);
		
		Iterable<Integer> filtered = Iterables.filter(unfiltered, Predicates.nonNull());
		
		assertElements(Arrays.asList(-1, 0, 1, 2, 3, 4), filtered);
	}
	
	@Test
	public void testFilter_ShouldReturnIterableContainingOnlyNonNullPositiveIntegers() {
		List<Integer> unfiltered = Arrays.asList(1, null, 2, 3, null, 4);
		
		Iterable<Integer> filtered = Iterables.filter(unfiltered, 
				Predicates.and(Predicates.nonNull(), Numbers.POSITIVE_INT));
		
		assertElements(Arrays.asList(1, 2, 3, 4), filtered);
	}
	
	private static <T> void assertElements(Iterable<T> expected, Iterable<T> actual) {
		Iterator<T> expectedElementsIterator = expected.iterator();
		Iterator<T> actualElementsIterator = actual.iterator();

		while (actualElementsIterator.hasNext()) {
			assertTrue(expectedElementsIterator.hasNext());
			assertEquals(expectedElementsIterator.next(), actualElementsIterator.next());
		}
		assertFalse(expectedElementsIterator.hasNext());
	}
	
	@Test
	public void testTransform_ShouldConvertIterableFromOneTypeToAnother() {
		List<String> strings = Arrays.asList("-1", "0", "1", "2");
		
		Iterable<Integer> ints = Iterables.transform(strings, Strings.toInt());
		
		assertElements(Arrays.asList(-1, 0, 1, 2), ints);
	}
	
	@Test
	public void testTransform_CanBeUsedToEncodeStringWithinSingleQuotes() {
		List<String> strings = Arrays.asList("hello", "world!");
		
		assertElements(Arrays.asList("'hello'", "'world!'"), 
				Iterables.transform(strings, Strings.WITHIN_SINGLE_QUOTES));
	}
}
