package com.elitecore.corenetvertex.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(value = JUnitParamsRunner.class)
public class ConcurrentLinkedQueueTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private ConcurrentLinkedQueue<Object> queue;
	private static Object[] elements;
	
	@BeforeClass
	public static void setUp() {
		Object element1 = new Object();
		Object element2 = new Object();
		Object element3 = new Object();
		Object element4 = new Object();
		Object element5 = new Object();
		
		elements = new Object[]{element1, element2, element3, element4, element5};
	}
	
	public Object[][] dataProviderFor_test_constructor_should_throw_IllegalArgumentException_when_zero_or_negative_capacity_passed() {
		return new Object[][] {
				{
					0
				},
				{
					-1
				}
		};
	}
	
	
	@Test
	@Parameters(method="dataProviderFor_test_constructor_should_throw_IllegalArgumentException_when_zero_or_negative_capacity_passed")
	public void test_constructor_should_throw_IllegalArgumentException_when_zero_or_negative_capacity_passed(int capacity) {
		expectedException.expect(IllegalArgumentException.class);;
		queue = new ConcurrentLinkedQueue<Object>(capacity);
	}

	@Test
	public void test_add_operation_should_throw_NullPointerException_when_null_element_added() {
		expectedException.expect(NullPointerException.class);;
		
		queue = new ConcurrentLinkedQueue<Object>(2);
		queue.add(null);
	}
	
	@Test
	public void test_defaultConstructor_should_create_queue_with_no_elements_and_default_capacity() throws Exception {
		queue = new ConcurrentLinkedQueue<Object>();
		
		assertEquals(0, queue.size());
		assertEquals(Long.MAX_VALUE, queue.getCapacity());
	}
	
	@Test
	public void test_constructor_should_create_queue_with_no_elements_and_provided_capacity() throws Exception {
		queue = new ConcurrentLinkedQueue<Object>(5);
		
		assertEquals(0, queue.size());
		assertEquals(5, queue.getCapacity());
	}
	
	@Test
	public void test_add_operation_should_return_boolean_value() throws Exception {

		queue = new ConcurrentLinkedQueue<Object>(3);
		assertTrue(queue.add(elements[0]));
		assertTrue(queue.add(elements[1]));
		assertTrue(queue.add(elements[2]));
		
		//adding after capacity reached
		assertFalse(queue.add(elements[3])); 
		assertFalse(queue.add(elements[4]));
	}
	
	
		@Test
	public void test_contains_should_return_boolean() throws Exception {
		queue = new ConcurrentLinkedQueue<Object>(5);
		queue.add(elements[0]);
		assertTrue(queue.contains(elements[0]));
		
		//element1 is not in queue
		assertFalse(queue.contains(elements[1]));
	}
	
	@Test
	public void test_isEmpty_should_return_boolean() throws Exception {
		queue = new ConcurrentLinkedQueue<Object>(1);
		assertTrue(queue.isEmpty());
		
		queue.add(elements[0]);
		assertFalse(queue.isEmpty());
	}
	
	@Test
	public void test_offer_should_throw_NullPointerException_when_null_element_added() throws Exception {
		expectedException.expect(NullPointerException.class);
		
		queue = new ConcurrentLinkedQueue<Object>(1);
		queue.offer(null);
	}
	
	@Test
	public void test_offer_should_return_boolean_value() throws Exception {
		queue = new ConcurrentLinkedQueue<Object>(3);
		assertTrue(queue.offer(elements[0]));
		assertTrue(queue.offer(elements[1]));
		assertTrue(queue.offer(elements[2]));
		
		//adding after capacity reached
		assertFalse(queue.offer(elements[3])); 
		assertFalse(queue.offer(elements[4]));
	}

	@Test
	public void test_poll_should_return_null_when_queue_is_empty() throws Exception {
		queue = new ConcurrentLinkedQueue<Object>(3);
		
		assertNull(queue.poll());
	}

	@Test
	public void test_poll_should_remove_head_element() throws Exception {
		setUpFor_test_poll_should_remove_head_element();
		
		assertSame(elements[0], queue.poll());
		assertFalse(queue.contains(elements[0]));
	}

	private void setUpFor_test_poll_should_remove_head_element() {
		queue = new ConcurrentLinkedQueue<Object>(3);
		queue.add(elements[0]);
		queue.add(elements[1]);
		queue.add(elements[2]);
	}
	
	@Test
	public void test_peek_should_return_null_when_queue_is_empty() throws Exception {
		setUpFor_test_poll_should_remove_head_element();
		queue = new ConcurrentLinkedQueue<Object>(3);
		
		assertNull(queue.peek());
	}

	@Test
	public void test_peek_should_return_head_element_and_should_not_remove() throws Exception {
		setUpFor_test_poll_should_remove_head_element();
		assertSame(elements[0], queue.peek());
		assertTrue(queue.contains(elements[0]));
	}
	
	@Test
	public void test_size_should_return_number_of_elements_of_queue() throws Exception {
		queue =  new ConcurrentLinkedQueue<Object>(3);
		queue.add(new Object());
		assertEquals(1, queue.size());
		queue.add(new Object());
		assertEquals(2, queue.size());
		queue.add(new Object());
		assertEquals(3, queue.size());
		queue.poll();
		assertEquals(2, queue.size());
		queue.poll();
		assertEquals(1, queue.size());
		queue.poll();
		assertEquals(0, queue.size());
	}
	
	public Object[][] dataProviderFor_test_remove_should_return_boolean_value() {
		 Object element1 = new Object();
		 Object element2 = new Object();
		 Object element3 = new Object();
		 
		 return new Object[][] {
				{
					Arrays.asList(), null, false
				},
				{
					Arrays.asList(element1), element2, false
				},
				{
					Arrays.asList(element1, element2), element1, true
				},
				{
					Arrays.asList(element1, element2, element3), element2, true
				}
		};
	}
	
	
	/**
	 * 
	 * @param elementList elements to insert in queue
	 * @param elementToBeRemoved, remove operation will be performed for this operation
	 * @param expectedResult,expected boolean value after remove operation
	 * 
	 */
	@Test
	@Parameters(method="dataProviderFor_test_remove_should_return_boolean_value")
	public void test_remove_should_return_boolean_value(
			List<Object> elementList,
			Object elementToBeRemoved,
			boolean expectedResult) throws Exception {
		setUpFor_test_remove_should_return_boolean_value(elementList);
		
		assertEquals(expectedResult, queue.remove(elementToBeRemoved));
	}
	
	private void setUpFor_test_remove_should_return_boolean_value(List<Object> elementList) {
		queue = new ConcurrentLinkedQueue<Object>(5);
		
		for(Object element : elementList) {
			queue.add(element);
		}
	}
	
	/**
	 * same element exist multiple time in queue, remove should delete only single element.
	 */
	@Test
	public void test_remove_should_remove_single_element_evenif_same_element_exist() throws Exception {
		setUpFor_test_remove_should_remove_single_element_evenif_same_element_exist();
	
		assertEquals(3, queue.size());
		// now remove element1
		queue.remove(elements[0]);
		assertEquals(2, queue.size());
		assertTrue(queue.contains(elements[0]));
	}

	/*
	 * elements[0] added two times. 
	 */
	private void setUpFor_test_remove_should_remove_single_element_evenif_same_element_exist() {
		queue = new ConcurrentLinkedQueue<Object>(5);
		queue.add(elements[0]);
		queue.add(elements[0]);
		queue.add(elements[1]);
	}
	
	@Test
	public void test_clear_should_remove_all_elements_of_queue() throws Exception {
		setUpFor_test_clear_should_remove_all_elements_of_queue();

		queue.clear();
		assertEquals(0, queue.size());
	}

	private void setUpFor_test_clear_should_remove_all_elements_of_queue() {
		queue = new ConcurrentLinkedQueue<Object>(3);
		queue.add(elements[0]);
		queue.add(elements[1]);
		queue.add(elements[2]);
		assertEquals(3, queue.size());
	}
	
	
	public Object[][] dataProviderFor_test_toArray2_should_return_array_of_elements() {
		
		Number number1 = new Integer(10);
		Number number2 = new Integer(20);
		
		return new Object[][] {
				{
					Arrays.<Number>asList(), new Integer[]{}
				},
				{
					Arrays.asList(number1), new Integer[]{10}
				},
				{
					Arrays.asList(number1, number2), new Integer[]{10, 20}
				}
		}; 
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_toArray2_should_return_array_of_elements")
	public void test_toArray2_should_return_array_of_elements(
			List<Number> elementList,
			Integer[] expectedArray) throws Exception {
		
		ConcurrentLinkedQueue<Number> numberQueue = createQueueWithElements( elementList );
		assertArrayEquals(expectedArray, numberQueue.toArray(new Integer[0]));
	}

	private <T> ConcurrentLinkedQueue<T> createQueueWithElements(List<T> elementList) {
		ConcurrentLinkedQueue<T> numberQueue = new ConcurrentLinkedQueue<T>();
		for (T element : elementList) {
			numberQueue.add(element);
		}
		
		return numberQueue;
	}
	
	/**
	 * Queue contains type of Object.
	 * toArray called for Integer[] that is not supertype of Object, so it will throw ArrayStoreException
	 * 
	 */
	@Test
	public void test_toArray2_should_throw_ArrayStoreException_when_destination_array_type_is_not_superType_of_element() throws Exception {
		queue = createQueueWithElements(Arrays.asList(elements[0],elements[1],elements[2]));
		expectedException.expect(ArrayStoreException.class);
		
		queue.toArray(new Integer[0]);
	}
	
	@Test
	public void test_toArray2_should_throw_NullPointerException_when_provided_destination_array_is_null() throws Exception {
		queue = new ConcurrentLinkedQueue<Object>(3);
		
		expectedException.expect(NullPointerException.class);
		queue.toArray(null);
		
	}
	
	public Object[][] dataProviderFor_test_toArray_should_return_array_of_elements_of_same_type() {
		Object element1 = new Object();
		Object element2 = new Object();

		return new Object[][] {
				{
					Arrays.asList(), new Object[]{}
				},
				{
					Arrays.asList(element1), new Object[]{element1}
				},
				{
					Arrays.asList(element1, element2), new Object[]{element1, element2}
				}
		}; 
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_toArray_should_return_array_of_elements_of_same_type")
	public void test_toArray_should_return_array_of_elements_of_same_type(
			List<Object> elementList,
			Object[] expectedArray) throws Exception {
		setUpFor_toArray_should_return_array_of_elements_of_same_type(elementList);
		
		assertArrayEquals(expectedArray, queue.toArray());
	}

	private void setUpFor_toArray_should_return_array_of_elements_of_same_type(
			List<Object> elementList) {
		queue = new ConcurrentLinkedQueue<Object>(3);
		
		for (Object element : elementList) {
			queue.add(element);
		}
	}
	
	@Test
	public void test_iterator_should_give_all_element_of_list() throws Exception {
		setUpFor_test_iterator_should_give_all_element_of_list();
		List<Object> expectedElements = Arrays.asList(elements[0],elements[1],elements[2]);
		
		
		Iterator<Object> iterator = queue.iterator();
		
		while (iterator.hasNext()) {
			if(expectedElements.contains(iterator.next()) == false) {
				throw new AssertionError("elements not exist");
			}
			iterator.remove();
		}
		
		assertEquals(0, queue.size());
	}

	private void setUpFor_test_iterator_should_give_all_element_of_list() {
		queue = new ConcurrentLinkedQueue<Object>(5);
		queue.add(elements[0]);
		queue.add(elements[1]);
		queue.add(elements[2]);
		
	}
}
