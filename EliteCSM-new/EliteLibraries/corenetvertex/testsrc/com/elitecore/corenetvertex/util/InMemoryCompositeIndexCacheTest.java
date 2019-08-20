package com.elitecore.corenetvertex.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.elitecore.commons.base.Function;

/**
 * 
 * @author Jay Trivedi
 *
 */
@RunWith(JUnitParamsRunner.class)
public class InMemoryCompositeIndexCacheTest {

	private static final String DEFAULT_CACHE_ELEMENT = "CacheElement1";
	private static TestableTaskScheduler taskScheduler;
    private SecondaryKeyFunction secondaryKeyFunction = null;
    private PrimaryKeyFunction primaryKeyFunction = null;
    private PartitioningCache<String, String> primaryCache;
    private InMemoryCompositeIndexCache<Integer, String, String> secondaryCompositeCache = null;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        
    }
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
    	taskScheduler = new TestableTaskScheduler();
    	primaryCache = spy(new PartitioningCache.CacheBuilder<String, String>(new TestableTaskScheduler()).build());
    	secondaryKeyFunction = spy(new SecondaryKeyFunction());
        primaryKeyFunction = spy(new PrimaryKeyFunction());
        secondaryCompositeCache = spy(new InMemoryCompositeIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).build());
    }

    @After
    public void tearDown() throws Exception {
    	secondaryCompositeCache.flush();
    	primaryCache.flush();
    }
    
    //ADD/PUT to Cache Test Cases
	@Test
	@Parameters(value = {DEFAULT_CACHE_ELEMENT,"CacheElement2","CacheElement3","CacheElement4","CacheElement5"})
	public void test_cache_Added_should_add_entry_in_secondary_cache(String value) throws Exception{
		
		 	assertEquals(0L,secondaryCompositeCache.statistics().getCacheCount());
	        primaryCache.put(primaryKeyFunction.apply(value), value);
	        assertEquals(1L,secondaryCompositeCache.statistics().getCacheCount());
	}

	@Test
	public void test_add_should_cache_entry_in_composite_cache_and_in_primary_cache() throws Exception{
		String value = DEFAULT_CACHE_ELEMENT;
		 	assertEquals(0L,secondaryCompositeCache.statistics().getCacheCount());
		 	secondaryCompositeCache.add(secondaryKeyFunction.apply(value), value);
	        assertEquals(1L,secondaryCompositeCache.statistics().getCacheCount());
	        assertSame(primaryCache.get(primaryKeyFunction.apply(value)), value);
	        assertSame(secondaryCompositeCache.get(secondaryKeyFunction.apply(value)).next(), value);
	}
	
	@Test
	public void test_add_should_allow_null_secondary_key_without_exception() throws Exception{
		 	secondaryCompositeCache.add(null, "VALUE");
	}
	
	@Test
	public void test_add_should_allow_null_primary_key_without_exception() throws Exception{
		
		when(primaryKeyFunction.apply(Mockito.anyString())).thenReturn(null);
		secondaryCompositeCache.add(2, "VALUE");
	}
	
	@Test
	@Parameters(value = {DEFAULT_CACHE_ELEMENT,"CacheElement2","CacheElement3","CacheElement4","CacheElement5"})
	public void test_put_collection_should_cache_all_elements_in_composite_cache_and_in_primary_cache(String value) throws Exception{
		
		 	assertEquals(0L,secondaryCompositeCache.statistics().getCacheCount());
		 	Collection<String> values = new  ConcurrentLinkedQueue<String>();
		 	values.add(value);
		 	secondaryCompositeCache.put(secondaryKeyFunction.apply(value), values);
	        assertSame(primaryCache.get(primaryKeyFunction.apply(value)), value);
	        assertSame(secondaryCompositeCache.get(secondaryKeyFunction.apply(value)).next(), value);
	}
	
	@Test
	@Parameters(value = {DEFAULT_CACHE_ELEMENT,"CacheElement2","CacheElement3","CacheElement4","CacheElement5"})
	public void test_put_iterator_should_cache_all_elements_in_composite_cache_and_in_primary_cache(String value) throws Exception{
		
		 	assertEquals(0L,secondaryCompositeCache.statistics().getCacheCount());
		 	Collection<String> values = new  ConcurrentLinkedQueue<String>();
		 	values.add(value);
		 	secondaryCompositeCache.put(secondaryKeyFunction.apply(value), values.iterator());
	        assertSame(primaryCache.get(primaryKeyFunction.apply(value)), value);
	        assertSame(secondaryCompositeCache.get(secondaryKeyFunction.apply(value)).next(), value);
	}
	
	@Test
	@Parameters(value = {DEFAULT_CACHE_ELEMENT,"CacheElement2"})
	public void test_put_iterator_should_return_previous_value_if_key_inserted_by_iterator_exists_in_composite_cache(String value) throws Exception{
		
		 	Collection<String> values = new  ConcurrentLinkedQueue<String>();
		 	values.add(value);
		 	secondaryCompositeCache.put(secondaryKeyFunction.apply(value), values.iterator());
	        assertSame(value, secondaryCompositeCache.put(secondaryKeyFunction.apply(value), values.iterator()).next());
	}
	
	@Test
	@Parameters(value = {DEFAULT_CACHE_ELEMENT,"CacheElement2"})
	public void test_put_collection_should_return_previous_value_if_key_inserted_by_collection_exists_in_composite_cache(String value) throws Exception{
		
		 	Collection<String> values = new  ConcurrentLinkedQueue<String>();
		 	values.add(value);
		 	secondaryCompositeCache.put(secondaryKeyFunction.apply(value), values);
	        assertSame(value, secondaryCompositeCache.put(secondaryKeyFunction.apply(value), values).next());
	}
	
	@Test
	@Parameters(value = {DEFAULT_CACHE_ELEMENT,"CacheElement2"})
	public void test_put_iterator_should_return_null_if_key_inserted_by_iterator_not_exists_in_composite_cache(String value) throws Exception{
		
		 	Collection<String> values = new ConcurrentLinkedQueue<String>();
		 	values.add(value);
	        assertNull(secondaryCompositeCache.put(secondaryKeyFunction.apply(value), values.iterator()));
	}
	
	@Test
	@Parameters(value = {DEFAULT_CACHE_ELEMENT,"CacheElement2"})
	public void test_put_collection_should_return_null_if_key_inserted_by_collection_not_exists_in_composite_cache(String value) throws Exception{
		
		 	Collection<String> values = new  ConcurrentLinkedQueue<String>();
		 	values.add(value);
		 	assertNull(secondaryCompositeCache.put(secondaryKeyFunction.apply(value), values));
	}
	
	@Test
	@Parameters(value = {DEFAULT_CACHE_ELEMENT,"CacheElement2"})
    public void test_put_collection_should_return_null_and_not_store_value_if_primary_function_return_null_value(String value) throws Exception {
        
		Integer secondaryKey = secondaryKeyFunction.apply(value);
        when(primaryKeyFunction.apply(value)).thenReturn(null);
        Collection<String> values = new ConcurrentLinkedQueue<String>();
	 	values.add(value);
        assertNull(secondaryCompositeCache.put(secondaryKey, values));
        assertNull(secondaryCompositeCache.get(secondaryKey));
    }
	
	@Test
	@Parameters(value = {DEFAULT_CACHE_ELEMENT,"CacheElement2"})
    public void test_put_iterator_should_return_null_and_not_store_value_if_primary_function_return_null_value(String value) throws Exception {
        
		Integer secondaryKey = secondaryKeyFunction.apply(value);
        when(primaryKeyFunction.apply(value)).thenReturn(null);
        Collection<String> values = new ConcurrentLinkedQueue<String>();
	 	values.add(value);
        assertNull(secondaryCompositeCache.put(secondaryKey, values.iterator()));
        assertNull(secondaryCompositeCache.get(secondaryKey));
    }
	
	@Test
    public void test_put_collection_should_allow_null_key(){
		Collection<String> values = new ArrayList<String>();
		values.add("CacheEment1");
		values.add("CacheEment11");
		secondaryCompositeCache.put(null, values);
    }

	@Test
    public void test_put_iterator_should_allow_null_key(){
		Collection<String> values = new ArrayList<String>();
		values.add("CacheEment1");
		values.add("CacheEment11");
		secondaryCompositeCache.put(null, values.iterator());
    }
	
	@Test
	public void test_cacheAdded_should_not_add_value_if_secondary_key_function_returns_null() throws Exception{

		when(secondaryKeyFunction.apply(Mockito.anyString())).thenReturn(null);
		secondaryCompositeCache.add(1,"A");
		assertNull(secondaryCompositeCache.get(1));
	}
	
	@Test
	public void test_cacheAdded_should_add_entry_in_collection() throws Exception{
	 	assertEquals(0L,secondaryCompositeCache.statistics().getCacheCount());
        primaryCache.put(primaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT), DEFAULT_CACHE_ELEMENT);
        Integer secondaryIndexKey = secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT);
        assertEquals(DEFAULT_CACHE_ELEMENT,secondaryCompositeCache.get(secondaryIndexKey).next());
	}
	
	@Test
	public void test_cacheAdded_should_add_entry_in_against_same_key_if_already_exist() throws Exception{
		//setUp
        primaryCache.put(primaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT), DEFAULT_CACHE_ELEMENT);
        primaryCache.put(primaryKeyFunction.apply("CacheElement2"), "CacheElement2");
        Integer secondaryIndexKey = secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT);
		Iterator<String> iterator = secondaryCompositeCache.get(secondaryIndexKey);
		assertEquals(DEFAULT_CACHE_ELEMENT,iterator.next());
        assertEquals("CacheElement2",iterator.next());
	}
	
	//STATISTICS
    @Test
    public void test_get_should_increment_hit_count_when_value_found_from_cache() throws Exception {

        performCachePutAndGet();
        assertSame(4L, secondaryCompositeCache.statistics().getHitCount());
        secondaryCompositeCache.get(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT));
        assertSame(5L, secondaryCompositeCache.statistics().getHitCount());
        secondaryCompositeCache.get(secondaryKeyFunction.apply("CacheElement11"));
        assertSame(6L, secondaryCompositeCache.statistics().getHitCount());

    }

    @Test
    public void test_get_should_increment_miss_count_when_value_not_found_from_cache() throws Exception {

        performCachePutAndGet();
        secondaryCompositeCache.get(secondaryKeyFunction.apply("CacheElement11111111"));
        assertSame(1L, secondaryCompositeCache.statistics().getMissCount());
        secondaryCompositeCache.get(secondaryKeyFunction.apply("CacheElement11111111"));
        assertSame(2L, secondaryCompositeCache.statistics().getMissCount());
    }

    @Test
    public void test_get_should_increment_cache_request_count() throws Exception {

        performCachePutAndGet();
        assertSame(4L, secondaryCompositeCache.statistics().getRequestCount());
    }
	
    @Test
    public void test_get_should_increment_load_count_when_value_is_loaded_from_cache_loader() throws Exception {

        secondaryCompositeCache = new InMemoryCompositeIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(new CacheLoader<Integer, Collection<String>>() {

			@Override
			public Collection<String> load(Integer t) throws Exception {
				
				Collection<String> values = new ConcurrentLinkedQueue<String>();
				values.add("LoadElement");
				return values;
			}

			@Override
			public Collection<String> reload(Integer t) throws Exception {
				return null;
			}
		}).build();
        
        secondaryCompositeCache.get(secondaryKeyFunction.apply("LoadElemet11"));
        secondaryCompositeCache.get(secondaryKeyFunction.apply("LoadElemet111"));
        secondaryCompositeCache.get(secondaryKeyFunction.apply("LoadElemet1111"));
        assertSame(3L, secondaryCompositeCache.statistics().getLoadCount());
    }
	
    @Test
    public void test_evict_should_increment_eviction_count() throws Exception {

        performCachePutAndGet();
        secondaryCompositeCache.evict();
        secondaryCompositeCache.evict();
        secondaryCompositeCache.evict();

        assertSame(3L, secondaryCompositeCache.statistics().getEvictionCount());
    }

    @Test
    public void test_put_collection_should_increment_cache_count() throws Exception {

    	String value = "test";
    	assertEquals(0L,secondaryCompositeCache.statistics().getCacheCount());
	 	Collection<String> values = new  ConcurrentLinkedQueue<String>();
	 	values.add(value);
	 	secondaryCompositeCache.put(secondaryKeyFunction.apply(value), values);
        assertSame(1L, secondaryCompositeCache.statistics().getCacheCount());
    }
    
    @Test
    public void test_put_iterator_should_increment_cache_count() throws Exception {

    	String value = "test";
    	assertEquals(0L,secondaryCompositeCache.statistics().getCacheCount());
	 	Collection<String> values = new  ConcurrentLinkedQueue<String>();
	 	values.add(value);
	 	secondaryCompositeCache.put(secondaryKeyFunction.apply(value), values);
        assertSame(1L, secondaryCompositeCache.statistics().getCacheCount());
    }

    @Test
    public void test_remove_should_decrement_cache_count() throws Exception {

        performCachePutAndGet();
        secondaryCompositeCache.remove(secondaryKeyFunction.apply("CacheElement11"));
        secondaryCompositeCache.remove(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT));
        assertSame(3L, secondaryCompositeCache.statistics().getCacheCount());
    }
    
    
    
	//GET from Cache Test Cases
	@Test
	public void test_get_should_return_iterator_from_secondary_cache_if_key_exist_in_secondry_cahe_and_value_found_from_primary_cache() throws Exception{
		String value = DEFAULT_CACHE_ELEMENT;
		Integer secondaryKey = secondaryKeyFunction.apply(value);
        secondaryCompositeCache.add(secondaryKey,value);
        assertSame(value, secondaryCompositeCache.get(secondaryKey).next());
	}
	
	@Test
   
    public void test_get_should_return_null_value_when_value_is_not_cache() throws Exception {
		String value = DEFAULT_CACHE_ELEMENT;
		Integer secondaryKey = secondaryKeyFunction.apply(value);
		
		assertNull(secondaryCompositeCache.get(secondaryKey));
		secondaryCompositeCache.add(secondaryKey, value);
        assertNull(secondaryCompositeCache.get(secondaryKey + "Other Value".length()));
    }
	
	@Test
    public void test_get_should_throw_runtime_exception_thrown_by_cache_load() throws Exception {

        exception.expect(RuntimeException.class);

        @SuppressWarnings("unchecked")
        CacheLoader<Integer, Collection<String>> cacheLoader = mock(CacheLoader.class);

        when(cacheLoader.load(anyInt())).thenThrow(new RuntimeException());

        secondaryCompositeCache = new InMemoryCompositeIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(cacheLoader).build();
        secondaryCompositeCache.get(secondaryKeyFunction.apply("ABC"));
    }
	

    @Test
    public void test_get_should_not_increment_load_count_if_runtime_exception_occures_while_cache_load() throws Exception {
        @SuppressWarnings("unchecked")
        CacheLoader<Integer, Collection<String>> cacheLoader = mock(CacheLoader.class);

        when(cacheLoader.load(anyInt())).thenThrow(new RuntimeException());

        secondaryCompositeCache = new InMemoryCompositeIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(cacheLoader).build();

        try {
        	secondaryCompositeCache.get(secondaryKeyFunction.apply("ABC"));
        	org.junit.Assert.fail("Get should throw exception");
        } catch (Exception e) {        	
        	assertSame(0L, secondaryCompositeCache.statistics().getLoadCount());
        }
        

    }
	
    @Test
    public void test_get_should_not_put_value_in_cache_when_cache_loader_loads_null_value() throws Exception{
    	
    	CacheLoader<Integer, Collection<String>> cacheLoader = mock(CacheLoader.class);

        secondaryCompositeCache = new InMemoryCompositeIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(cacheLoader).build();
        
        Iterator<String> iterator = secondaryCompositeCache.get(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT));
        verify(cacheLoader, times(1)).load(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT));
		assertNull(iterator);
    }
    
    
	//REMOVE from Cache Test Cases
	@Test
	@Parameters(value = {DEFAULT_CACHE_ELEMENT,"CacheElement2","CacheElement3","CacheElement4","CacheElement5"})
	public void test_remove_should_remove_entry_from_secondary_cache_and_primary_cache(String value) throws Exception{
		
        secondaryCompositeCache.add(secondaryKeyFunction.apply(value),value);
        assertSame(1L,secondaryCompositeCache.statistics().getCacheCount());
		assertSame(value, primaryCache.remove(primaryKeyFunction.apply(value)));
        assertSame(0L,secondaryCompositeCache.statistics().getCacheCount());
	}
	
	@Test
	@Parameters(value = {DEFAULT_CACHE_ELEMENT,"CacheElement2","CacheElement3","CacheElement4","CacheElement5"})
	public void test_cacheRemoved_should_remove_entry_from_secondary_cache(String value) throws Exception{
		
		secondaryCompositeCache.add(secondaryKeyFunction.apply(value),value);
		assertSame(1L,secondaryCompositeCache.statistics().getCacheCount());
		assertSame(value, secondaryCompositeCache.remove(secondaryKeyFunction.apply(value)).next());
        assertSame(0L,secondaryCompositeCache.statistics().getCacheCount());
	}

	@Test
	public void test_remove_should_bypass_null_key() throws Exception{
		secondaryCompositeCache.add(1,"A");
		assertSame(null, secondaryCompositeCache.remove(null));
	}
	
	@Test
	public void test_cacheRemoved_should_not_remove_value_if_secondary_key_function_returns_null() throws Exception{
		secondaryCompositeCache.add(1,"A");
		when(secondaryKeyFunction.apply(Mockito.anyString())).thenReturn(null);
		primaryCache.remove(primaryKeyFunction.apply("A"));
		assertSame(1L, secondaryCompositeCache.statistics().getCacheCount());
	}
	
	//DEFAULT_CACHE_ELEMENT and CacheElement2 both are having same length so secondary key will be same 1key -> 2values
	@Test
	public void test_cacheRemoved_should_not_remove_entry_from_secondary_cache_when_other_values_exists_with_secondary_key() throws Exception{
		
        secondaryCompositeCache.add(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT),DEFAULT_CACHE_ELEMENT);
        secondaryCompositeCache.add(secondaryKeyFunction.apply("CacheElement2"),"CacheElement2");
        assertSame(1L,secondaryCompositeCache.statistics().getCacheCount());
		assertSame(DEFAULT_CACHE_ELEMENT, primaryCache.remove(primaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT)));
        assertSame(1L,secondaryCompositeCache.statistics().getCacheCount());
	}
	
	
	
	//REFRESH the cache
    @Test
    public void test_refresh_should_not_cache_data_from_the_cache_loader_if_data_is_null() throws Exception{

        final String value = "str";
        int key = secondaryKeyFunction.apply(value);
        CacheLoader<Integer, Collection<String>> cacheLoader = spy(new CacheLoader<Integer, Collection<String>>() {

            @Override
            public Collection<String> load(Integer t) throws RuntimeException {
                return null;
            }

            @Override
            public Collection<String> reload(Integer t) throws RuntimeException {
                return null;
            }
        });
        secondaryCompositeCache = new InMemoryCompositeIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(cacheLoader).build();

        assertNull(value, secondaryCompositeCache.get(key));

        verify(cacheLoader, times(1)).load(key);

        assertNull(value, secondaryCompositeCache.refresh(key));
        assertNull(value, secondaryCompositeCache.get(key));

        verify(cacheLoader, times(2)).load(key);
        verify(cacheLoader, times(1)).reload(key);
    }

    @Test
    public void test_refresh_should_fetch_and_cache_value_from_cacheLoader() throws Exception {
        
    	String value = "str";
        final Collection<String> values = new ArrayList<String>();
        values.add(value);
        int key = secondaryKeyFunction.apply(value);
        secondaryCompositeCache = new InMemoryCompositeIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(new CacheLoader<Integer, Collection<String>>() {

            @Override
            public Collection<String> load(Integer t) throws RuntimeException {
                return null;
            }

            @Override
            public Collection<String> reload(Integer t) throws RuntimeException {
                return values;
            }
        }).build();

        assertSame(value, secondaryCompositeCache.refresh(key).next());
    }


    @Test
    public void test_refresh_should_not_increment_load_count_evenif_runtime_exception_occures_while_cache_load() throws Exception {
        @SuppressWarnings("unchecked")

        CacheLoader<Integer, Collection<String>> cacheLoader = mock(CacheLoader.class);

        when(cacheLoader.reload(anyInt())).thenThrow(new RuntimeException());

        secondaryCompositeCache = new InMemoryCompositeIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(cacheLoader).build();

        try {
        	secondaryCompositeCache.refresh(secondaryKeyFunction.apply("ABC"));
        } catch (Exception e) { }

        assertSame(0L, secondaryCompositeCache.statistics().getLoadCount());

    }



    public void test_refresh_should_throw_NullPointerException_when_null_key_provided() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("key is null");

        secondaryCompositeCache.refresh(null);
    }



    @Test
    public void test_refresh_should_return_null_when_cacheLoader_not_provided() throws Exception {

    	secondaryCompositeCache = new InMemoryCompositeIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).build();
        String value = "A";
        int key = secondaryKeyFunction.apply(value);
        Collection<String> values = new ArrayList<String>();
        values.add(value);
        secondaryCompositeCache.put(key, values);

        assertNull(secondaryCompositeCache.refresh(key));

    }


	 private static class PrimaryKeyFunction implements Function<String, String> {

	        @Override
	        public String apply(String input) {
	            return new StringBuilder(input).reverse().toString();
	        }
	    }

	 private static class SecondaryKeyFunction implements Function<String, Integer> {

	        @Override
	        public Integer apply(String input) {
	            return input.length();
	        }


	    }
	 
	 private void performCachePutAndGet() throws Exception {

	        primaryCache.put(primaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT), DEFAULT_CACHE_ELEMENT);
	        primaryCache.put(primaryKeyFunction.apply("CacheElement11"), "CacheElement11");
	        primaryCache.put(primaryKeyFunction.apply("CacheElement111"), "CacheElement111");
	        primaryCache.put(primaryKeyFunction.apply("CacheElement1111"), "CacheElement1111");
	        primaryCache.put(primaryKeyFunction.apply("CacheElement11111"), "CacheElement11111");

	        secondaryCompositeCache.get(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT));
	        secondaryCompositeCache.get(secondaryKeyFunction.apply("CacheElement11"));
	        secondaryCompositeCache.get(secondaryKeyFunction.apply("CacheElement111"));
	        secondaryCompositeCache.get(secondaryKeyFunction.apply("CacheElement1111"));

	    }
	 
	 
	 
	 //Iterator test cases
	 
	 @Test
	 public void test_hasNext_returns_true_multiple_times_calling_on_non_empty_iterator() throws Exception {
		 
		 secondaryCompositeCache.add(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT), DEFAULT_CACHE_ELEMENT);
		 
		 Iterator<String> valueIterator = secondaryCompositeCache.get(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT));
		 
		 //Calling it multiple times deliberately 
		 assertSame(true, valueIterator.hasNext());
		 assertSame(true, valueIterator.hasNext());
		 assertSame(true, valueIterator.hasNext());
	 }
	 
	 @Test
	 public void test_hasNext_returns_false_when_no_element_left_in_iterator() throws Exception {
		 
		 secondaryCompositeCache.add(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT), DEFAULT_CACHE_ELEMENT);
		 Iterator<String> valueIterator = secondaryCompositeCache.get(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT));
		 valueIterator.next();
		 
		 //Calling it multiple times deliberately 
		 assertSame(false, valueIterator.hasNext());
		 assertSame(false, valueIterator.hasNext());
		 assertSame(false, valueIterator.hasNext());
	 }
	 
	 @Test
	 public void test_next_returns_stored_value() throws Exception {
		 
		 //next added 2 entries will have same secondary key
		 secondaryCompositeCache.add(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT), DEFAULT_CACHE_ELEMENT);
		 secondaryCompositeCache.add(secondaryKeyFunction.apply("CacheElement2"), "CacheElement2");
		 Iterator<String> valueIterator = secondaryCompositeCache.get(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT));
		 assertSame(DEFAULT_CACHE_ELEMENT, valueIterator.next());
		 assertSame("CacheElement2", valueIterator.next());
	 }
	 
	 @Test
	 public void test_remove_from_iterator_throws_exception() throws Exception {
		 
		 secondaryCompositeCache.add(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT), DEFAULT_CACHE_ELEMENT);
		 Iterator<String> valueIterator = secondaryCompositeCache.get(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT));
		 
		 exception.expect(UnsupportedOperationException.class);
		 valueIterator.remove();
	 }
	 
	 @Test
	 public void test_next_and_hasNext_together_works_fine_with_multiple_elements() throws Exception {
		 
		 secondaryCompositeCache.add(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT), DEFAULT_CACHE_ELEMENT);
		 secondaryCompositeCache.add(secondaryKeyFunction.apply("CacheElement2"), "CacheElement2");
		 secondaryCompositeCache.add(secondaryKeyFunction.apply("CacheElement3"), "CacheElement3");
		 secondaryCompositeCache.add(secondaryKeyFunction.apply("CacheElement4"), "CacheElement4");
		 secondaryCompositeCache.add(secondaryKeyFunction.apply("CacheElement5"), "CacheElement5");
		 
		 Iterator<String> valueIterator = secondaryCompositeCache.get(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT));
		 
		 int counter = 1;
		 while (valueIterator.hasNext()) {
			 assertEquals("CacheElement" + counter, valueIterator.next());
			 counter++;
		 }
		 
		 assertSame(6, counter);
	 }
	 
	 @Test
	 public void test_next_should_throw_exception_if_no_element_found() throws Exception {
		 
		 secondaryCompositeCache.add(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT), DEFAULT_CACHE_ELEMENT);
		 Iterator<String> valueIterator = secondaryCompositeCache.get(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT));
		 
		 exception.expect(NoSuchElementException.class);
		 valueIterator.next();
		 valueIterator.next();
	 }
	 
	 @Test
	 public void test_next_should_remove_primary_key_if_no_associated_value_found() throws Exception {
		 
		 secondaryCompositeCache.add(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT), DEFAULT_CACHE_ELEMENT);
		 Iterator<String> valueIterator = secondaryCompositeCache.get(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT));
		 
		 when(primaryCache.getWithoutLoad(primaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT))).thenReturn(null);
		 
		 assertSame(false, valueIterator.hasNext());
	 }
	 
	 @Test
	 public void test_cache_clean_schedular_run_execute_method_with_eviction_of_all_cache_elements() {
		 secondaryCompositeCache.add(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT), DEFAULT_CACHE_ELEMENT);
		 secondaryCompositeCache.add(secondaryKeyFunction.apply(DEFAULT_CACHE_ELEMENT + 1), DEFAULT_CACHE_ELEMENT + 1);
		 assertSame(2L, secondaryCompositeCache.statistics().getCacheCount());
		 taskScheduler.executeIntervalBaseTask();
		 assertSame(0L, secondaryCompositeCache.statistics().getCacheCount());
		 assertSame(1L, secondaryCompositeCache.statistics().getEvictionCount());
	 }

	@Test
	@Parameters(value = {"ABC"})
	public void test_evict_should_remove_cache(String value) throws Exception{

		primaryCache.put(value, value);

		Map<String, String> hashMap = new HashMap<>();
		hashMap.put(value, value);

		assertEquals(1L,secondaryCompositeCache.statistics().getCacheCount());

		secondaryCompositeCache.evict(hashMap.entrySet());

		assertEquals(0,secondaryCompositeCache.statistics().getCacheCount());
	}
}
