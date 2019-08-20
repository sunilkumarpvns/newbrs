package com.elitecore.commons.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.elitecore.commons.base.Predicates;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.collection.Iterators;

public class IteratorsTest {

	@Test
	public void testFilter_ShouldFilterElements_WhichDoNotSafisfyTheFilter() {
		List<Integer> unfiltered = Arrays.asList(-1, 0, null, 1, 2, 3);
		
		Iterator<Integer> filtered = Iterators.filter(unfiltered.iterator(), Predicates.nonNull());
		
		assertElements(Arrays.asList(-1, 0, 1, 2, 3).iterator(), filtered);
	}

	private static <T> void assertElements(Iterator<T> expected, Iterator<T> actual) {
		while (actual.hasNext()) {
			assertTrue(expected.hasNext());
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(expected.hasNext());
	}
	
	@Test
	public void testTransform_ShouldConvertIteratorFromOneTypeToAnother() {
		List<String> strings = Arrays.asList("-1", "0", "1", "2");
		
		Iterator<Integer> ints = Iterators.transform(strings.iterator(), Strings.toInt());
		
		assertElements(Arrays.asList(-1, 0, 1, 2).iterator(), ints);
	}
	
}
