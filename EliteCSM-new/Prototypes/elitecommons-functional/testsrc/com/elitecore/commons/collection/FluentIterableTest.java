package com.elitecore.commons.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.base.Consumer;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Predicates;
import com.elitecore.commons.base.Strings;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class FluentIterableTest {

	public class RegressionTests {
		
		@Test
		public void testFrom_FactoryMethod_ShouldReturnAnInstanceOfFluentIterable() {
			List<String> list = anyList();
			
			FluentIterable<String> fluentIterable = FluentIterable.from(list);
			assertNotNull(fluentIterable);
		}
		
		@Test
		public void testFrom_FactoryMethod_ShouldReturnAnInstanceOfFluentIterable_WhichCanBeUsedToGetToTheUnderlyingIterable() {
			List<String> list = anyList();
			
			FluentIterable<String> fluentIterable = FluentIterable.from(list);
			
			assertElements(list, fluentIterable);
		}
		
		@Test
		public void testFilter_ShouldReturnAnIterable_ThatFiltersTheElementsThatDontSatisfyThePredicate() {
			List<String> anyListContainingNulls = Arrays.asList("hello", null, "world", null, null, "!");
			
			FluentIterable<String> listWithNoNulls = 
				FluentIterable.from(anyListContainingNulls)
				.filter(Predicates.nonNull());
			
			assertElements(Arrays.asList("hello", "world", "!"), listWithNoNulls);
		}
		
		@Test
		public void testTransform_ShouldReturnAnIterable_WhichTransformsTheIterableFromSourceToDestinationType() {
			List<String> anyListOfIntegersInString = Arrays.asList( "-1", "0", "1", "2");
			
			FluentIterable<Integer> listOfInts = 
				FluentIterable.from(anyListOfIntegersInString)
				.transform(Strings.toInt());
			
			assertElements(Arrays.asList(-1, 0, 1, 2), listOfInts);
		}
		
		@Test
		public void testForEach_ShouldPassEachElementOfIterable_ToTheConsumerOfForEach() {
			List<String> anyList = anyList();
			Collector collect = new Collector();
			
			FluentIterable.from(anyList).forEach(collect);
			
			assertEquals(anyList, collect.getCollection());
		}

	}
	
	public class UsageTests {
		
		@Test
		public void canBeUsedToFilterNullsAndConvertAListOfStringsToListOfInteger() {
			List<String> anyListOfIntegersInStringContainingNulls = Arrays.asList("-1", null, "0", "1", null);
			
			FluentIterable<Integer> listOfInts = 
				FluentIterable.from(anyListOfIntegersInStringContainingNulls)
				.filter(Predicates.nonNull())
				.transform(Strings.toInt());
			
			assertElements(Arrays.asList(-1, 0, 1), listOfInts);
		}
		
		@Test
		public void canBeUsedToFilterNullsAndConvertAListOfStringsToListOfPositiveIntegers() {
			List<String> anyListOfIntegersInStringContainingNullsAndNegativeInts = Arrays.asList("-1", null, "0", "1", null, "2");
			
			FluentIterable<Integer> listOfInts = 
				FluentIterable.from(anyListOfIntegersInStringContainingNullsAndNegativeInts)
				.filter(Predicates.nonNull())
				.transform(Strings.toInt())
				.filter(Numbers.POSITIVE_INT);
			
			assertElements(Arrays.asList(1, 2), listOfInts);
		}
		
		@Test
		public void canBeUsedToCountHowManyIntegersInAListOfStringsArePositive() {
			List<String> anyListOfIntegersInStringContainingNulls = Arrays.asList("-1", null, "0", "1", null, "2");
			Counter count = new Counter();
			
			FluentIterable.from(anyListOfIntegersInStringContainingNulls)
			.filter(Predicates.nonNull())
			.transform(Strings.toInt())
			.filter(Numbers.POSITIVE_INT)
			.forEach(count);

			assertEquals(2, count.getCount());
		}
	}
	
	class Counter implements Consumer<Object> {

		private int counter;

		@Override
		public void accept(Object input) {
			counter++;
		}
		
		public int getCount() {
			return counter;
		}
	}

	class Collector implements Consumer<Object> {

		private List<Object> collection = new ArrayList<Object>();

		@Override
		public void accept(Object input) {
			collection.add(input);
		}

		public Collection<Object> getCollection() {
			return collection;
		}
	}
	
	private void assertElements(Iterable<?> expected, Iterable<?> actual) {
		Iterator<?> expectedIterator = expected.iterator();
		Iterator<?> actualIterator = actual.iterator();
		while (expectedIterator.hasNext()) {
			assertTrue(actualIterator.hasNext());
			assertEquals(expectedIterator.next(), actualIterator.next());
		}
		assertFalse(actualIterator.hasNext());
	}

	private List<String> anyList() {
		List<String> list = new ArrayList<String>();
		list.add("any");
		list.add("value");
		return list;
	}
}
