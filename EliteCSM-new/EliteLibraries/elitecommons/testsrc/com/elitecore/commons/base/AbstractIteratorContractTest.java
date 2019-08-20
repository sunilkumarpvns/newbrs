package com.elitecore.commons.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AbstractIteratorContractTest {
	private static final List<String> ANY_LIST = Arrays.asList("foo", "bar");

	@Rule public ExpectedException exception = ExpectedException.none();

	private static final String SOME_VALUE = "some value";
	private int computeNextCallCount = 0;

	@Test
	public void testHasNext_ShouldCallComputeNext_WhenCalledForTheFirstTime() {
		AbstractIterator<String> stub = counting(noDataIterator());

		stub.hasNext();

		assertEquals(1, computeNextCallCount);
	}

	@Test
	public void testHasNext_ShouldReturnFalse_OnceEndOfDataIsReached() {
		AbstractIterator<String> stub = noDataIterator();

		assertFalse(stub.hasNext());
	}

	@Test
	public void testHasNext_ShouldReturnTrue_IfImplementationProvidesSomeValueFromComputeNext() {
		AbstractIterator<String> stub = new AbstractIterator<String>() {

			@Override
			protected String computeNext() {
				computeNextCallCount++;
				return SOME_VALUE; //used to denote that there are no further elements
			}
		};

		assertTrue(stub.hasNext());
	}

	@Test
	public void testHasNext_ShouldReturnTrueIfCalledConsecutively_WithoutConsumingElementWithNext() {
		AbstractIterator<String> stub = new AbstractIterator<String>() {

			@Override
			protected String computeNext() {
				computeNextCallCount++;
				return SOME_VALUE; //used to denote that there are no further elements
			}
		};

		assertTrue(stub.hasNext());
		assertTrue(stub.hasNext());
	}

	@Test
	public void testHasNext_ShouldNotCallComputeNextTwice_OnceHasNextReturnsTrueAndValueIsNotYetConsumedUsingNext() {
		AbstractIterator<String> stub = new AbstractIterator<String>() {

			@Override
			protected String computeNext() {
				computeNextCallCount++;
				return SOME_VALUE; //used to denote that there are no further elements
			}
		};

		stub.hasNext();
		stub.hasNext();

		assertEquals(1, computeNextCallCount);
	}

	@Test
	public void testHasNext_ShouldReturnFalse_OnceIteratorHasNoMoreElementsLeft_WhichIsSignifiedByEndOfData() {
		AbstractIterator<String> iteratorUnderTest = listIterator(ANY_LIST);

		consume(ANY_LIST, iteratorUnderTest);

		assertFalse(iteratorUnderTest.hasNext());
		assertFalse(iteratorUnderTest.hasNext());
	}

	@Test
	public void testNext_ShouldReturnNoSuchElementException_IfTriedToConsumeMoreDataThanAvailable() {
		AbstractIterator<String> iteratorUnderTest = listIterator(ANY_LIST);

		consume(ANY_LIST, iteratorUnderTest);

		exception.expect(NoSuchElementException.class);

		iteratorUnderTest.next();
	}

	private void consume(List<String> list, AbstractIterator<String> iteratorUnderTest) {
		Iterator<String> iterator = list.iterator();
		while (iterator.hasNext()) {
			iterator.next();
			iteratorUnderTest.next();
		}
	}

	@Test
	public void testNext_ShouldCallComputeNext_IfCalled_WithoutCallingHasNext() {
		AbstractIterator<String> iteratorUnderTest = counting(noDataIterator());

		iteratorUnderTest.next();

		assertEquals(1, computeNextCallCount);
	}

	@Test
	public void testNext_ShouldReturnTheValueProvidedByComputeNext() {
		String ANY_STRING = "one";

		AbstractIterator<String> iteratorUnderTest = listIterator(Arrays.asList(ANY_STRING));

		assertEquals(ANY_STRING, iteratorUnderTest.next());
	}

	@Test
	public void testRemove_ShouldThrowUnsupportedOperationException() {
		AbstractIterator<String> iteratorUnderTest = new AbstractIterator<String>() {

			@Override
			protected String computeNext() {
				return null;
			}
		};

		exception.expect(UnsupportedOperationException.class);

		iteratorUnderTest.remove();
	}

	private AbstractIterator<String> counting(final AbstractIterator<String> iterator) {
		return new AbstractIterator<String>() {

			@Override
			protected String computeNext() {
				computeNextCallCount++;
				return iterator.computeNext(); //used to denote that there are no further elements
			}
		};
	}

	private AbstractIterator<String> noDataIterator() {
		return new AbstractIterator<String>() {

			@Override
			protected String computeNext() {
				return endOfData(); //used to denote that there are no further elements
			}
		};
	}

	private <T> ListIterator<T> listIterator(List<T> list) {
		return new ListIterator<T>(list.iterator());
	}

	class ListIterator<T> extends AbstractIterator<T> {
		private final Iterator<T> iterator;

		public ListIterator(Iterator<T> iterator) {
			this.iterator = iterator;
		}

		@Override
		protected T computeNext() {
			while (iterator.hasNext()) {
				return iterator.next();
			}

			return endOfData();
		}
	}
}