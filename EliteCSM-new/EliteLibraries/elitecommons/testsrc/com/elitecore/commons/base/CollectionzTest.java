package com.elitecore.commons.base;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class CollectionzTest {

	private static final String SOME_STRING = "";
	
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testIsNullOrEmpty_ShouldReturnTrue_IfCollectionIsNull(){
		assertTrue(Collectionz.isNullOrEmpty(null));
	}
	
	@Test
	public void testIsNullOrEmpty_ShouldReturnTrue_IfCollectionIsEmpty(){
		assertTrue(Collectionz.isNullOrEmpty(Collections.emptyList()));
	}
	
	@Test
	public void testIsNullOrEmpty_ShouldReturnFalse_IfCollectionIsNotEmpty(){
		Collection<String> nonEmptyCollection = new ArrayList<String>();
		nonEmptyCollection.add(SOME_STRING);
		
		assertFalse(Collectionz.isNullOrEmpty(nonEmptyCollection));
	}
	
	@Test
	public void testNewArrayList_ShouldCreateANewArrayList(){
		assertNotNull(Collectionz.newArrayList());
	}
	
	@Test
	public void testNewArrayList_ShouldAlwaysCreateAFreshArrayList(){
		assertNotSame(Collectionz.newArrayList(), Collectionz.newArrayList());
	}
	
	@Test
	public void testNewLinkedList_ShouldCreateANewLinkedList(){
		assertNotNull(Collectionz.newLinkedList());
	}
	
	@Test
	public void testNewLinkedList_ShouldAlwaysCreateAFreshLinkedList(){
		assertNotSame(Collectionz.newLinkedList(), Collectionz.newLinkedList());
	}
	
	@Test
	public void testNewHashSet_ShouldCreateANewHashSet(){
		assertNotNull(Collectionz.newHashSet());
	}
	
	@Test
	public void testNewHashSet_ShouldAlwaysCreateAFreshHashSet(){
		assertNotSame(Collectionz.newHashSet(), Collectionz.newHashSet());
	}
	
	@Test
	public void testNewLinkedHashSet_ShouldCreateANewLinkedHashSet(){
		assertNotNull(Collectionz.newLinkedHashSet());
	}
	
	@Test
	public void testNewLinkedHashSet_ShouldAlwaysCreateAFreshLinkedHashSet(){
		assertNotSame(Collectionz.newLinkedHashSet(), Collectionz.newLinkedHashSet());
	}
	
	@Test
	public void testAsHashMap_ShouldReturnAnInstanceOfHashMap() {
		assertTrue(Collectionz.asHashMap(Collections.emptyList(), new AnyFunction()) instanceof HashMap);
	}

	@Test
	public void testAsHashMap_ShouldThrowNullPointerException_IfCollectionPassedIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("collection is null");
		
		Collectionz.asHashMap(null, new AnyFunction());
	}
	
	@Test
	public void testAsHashMap_ShouldThrowNullPointerException_IfValueToKeyFunctionPassedIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("valueToKeyFunction is null");
		
		Collectionz.asHashMap(Collections.<String>emptyList(), null);
	}
	
	@Test
	public void testAsHashMap_ShouldConvertCollectionToMap(){
		Collection<String> collection = new ArrayList<String>();
		collection.add("1");
		
		Map<Integer, String> asHashMap = Collectionz.asHashMap(collection, new Function<String, Integer>() {

			@Override
			public Integer apply(String input) {
				return Integer.parseInt(input);
			}
		});
		
		assertTrue(asHashMap.size() == 1);
		assertTrue(asHashMap.keySet().contains(1));
		assertTrue(asHashMap.values().contains("1"));
	}
	
	@Test
	public void testAsHashMap_ShouldRethrowAnyExceptionThatOccursInFunction(){
		Collection<String> collection = new ArrayList<String>();
		collection.add(SOME_STRING);

		exception.expect(RuntimeException.class);
		exception.expectMessage("test");
		
		Collectionz.asHashMap(collection, new ExceptionThrowingFunction());
	}
	
	@Test
	public void testAsLinkedHashMap_ShouldReturnAnInstanceOfHashMap() {
		assertTrue(Collectionz.asLinkedHashMap(Collections.emptyList(), new AnyFunction()) instanceof LinkedHashMap);
	}
	
	@Test
	public void testAsLinkedHashMap_ShouldThrowNullPointerException_IfCollectionPassedIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("collection is null");
		
		Collectionz.asLinkedHashMap(null, new AnyFunction());
	}
	
	@Test
	public void testAsLinkedHashMap_ShouldThrowNullPointerException_IfValueToKeyFunctionPassedIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("valueToKeyFunction is null");
		
		Collectionz.asLinkedHashMap(Collections.<String>emptyList(), null);
	}
	
	@Test
	public void testAsLinkedHashMap_ShouldConvertCollectionToMap(){
		Collection<String> collection = new ArrayList<String>();
		collection.add("1");
		
		Map<Integer, String> asLinkedHashMap = Collectionz.asLinkedHashMap(collection, new Function<String, Integer>() {

			@Override
			public Integer apply(String input) {
				return Integer.parseInt(input);
			}
		});
		
		assertTrue(asLinkedHashMap.size() == 1);
		assertTrue(asLinkedHashMap.keySet().contains(1));
		assertTrue(asLinkedHashMap.values().contains("1"));
	}
	
	@Test
	public void testAsLinkedHashMap_ShouldRethrowAnyExceptionThatOccursInFunction(){
		Collection<String> collection = new ArrayList<String>();
		collection.add(SOME_STRING);

		exception.expect(RuntimeException.class);
		exception.expectMessage("test");
		
		Collectionz.asLinkedHashMap(collection, new ExceptionThrowingFunction());
	}
	
	static class ExceptionThrowingFunction implements Function<String, Integer>{

		@Override
		public Integer apply(String input) {
			throw new RuntimeException("test");
		}
	}
	
	static class AnyFunction implements Function<Object, Object>{

		@Override
		public Object apply(Object input) {
			//does not matter
			return input;
		}
	}
	
	@Test
	@Parameters(method = "dataFor_testFirstNonEmpty")
	public void testFirstNonEmtpy(List<Collection<?>> collections,
			boolean expectedPresence, Collection<?> expectedCollection) {
		
		Optional<Collection<?>> optionalCollection = Collectionz
		.firstNonEmpty(
				((Collection<?>[])collections.toArray())
				);
		
		assertEquals(expectedPresence, optionalCollection.isPresent());
		
		if(expectedPresence) {
			assertSame(expectedCollection, optionalCollection.get());
		}
	}
	
	public Object[] dataFor_testFirstNonEmpty() {
		List<Integer> intList = Arrays.asList(1);
		List<Integer> otherIntList = Arrays.asList(1, 2);
		return $(
				//input collections												expected presence  expected collection		
				$(Arrays.<Collection<?>>asList(null, intList), 							true, 		intList),
				$(Arrays.<Collection<?>>asList(null, Collections.emptyList()), 			false,		null),
				$(Arrays.<Collection<?>>asList(intList, otherIntList),		 			true,		intList),
				$(Arrays.<Collection<?>>asList(Collections.emptyList(), otherIntList),	true,	 	otherIntList)
		);
	}
	
	@Test
	public void testMap_ShouldReturnABlankList_IfCollectionIsNull() { 
		List<Object> result = 
			Collectionz.map(null, new FunctionStub());
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void testMap_ShouldReturnAMutableList_IfCollectionIsNull() { 
		Collectionz.map(null, new FunctionStub()).add(new Object());
	}
	
	class FunctionStub implements Function<Object, Object> {
		@Override
		public Object apply(Object input) {
			return null;
		}
	}
	
	@Test
	public void testMap_ShouldThrowNPE_IfMapperFunctionPassedIsNull() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("mapper function is null");
		Collectionz.map(Collections.emptyList(), null);
	}
	
	@Test
	public void testMap_ShouldApplyTheMapperFunctionOnAllElementsAndReturnResultantList() {
		List<String> initialList = Arrays.asList("1", "2");
		List<Integer> expectedList = Arrays.asList(1, 2);
		List<Integer> resultantList = 
			Collectionz.map(initialList, new StringToInt());
		assertEquals(expectedList, resultantList);
	}
	
	class StringToInt implements Function<String, Integer> {
		@Override
		public Integer apply(String input) {
			return Integer.parseInt(input);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testFilter_ShouldThrowNPE_IfCollectionPassedIsNull() {
		exception.expect(NullPointerException.class);
		
		Collectionz.filter(null, Mockito.mock(Predicate.class));
	}
	
	@Test
	public void testFilter_ShouldThrowNPE_IfPredicatePassedIsNull() {
		exception.expect(NullPointerException.class);
		Collectionz.filter(Collections.<String>emptyList(), (Predicate<String>)null);
	}
	
	@Test
	public void testFilter_ShouldFilterAllElementsFromCollection_WhichDontSatisfyThePredicate() {
		Collection<Integer> collection = new ArrayList<Integer>();
		collection.add(2);
		collection.add(null);
		collection.add(3);
		
		Collectionz.filter(collection, Predicates.nonNull());
		assertEquals(Arrays.asList(2, 3), collection);
	}
	
}
