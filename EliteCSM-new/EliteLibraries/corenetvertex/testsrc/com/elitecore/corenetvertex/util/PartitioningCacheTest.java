package com.elitecore.corenetvertex.util;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.commons.kpi.handler.IntervalBasedTask;
import com.elitecore.commons.kpi.handler.SingleExecutionTask;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.corenetvertex.util.PartitioningCache.CacheBuilder;
import com.elitecore.corenetvertex.util.commons.CacheEventListener;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class PartitioningCacheTest {

	private PartitioningCache<String, String> cache;
	private static TaskScheduler taskScheduler;
	@Rule public PrintMethodRule rule = new PrintMethodRule();
	@Rule public ExpectedException exception = ExpectedException.none();
	private CacheLoader<String, String> cacheLoader;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		taskScheduler =  new TaskScheduler() {
			
			@Override
			public Future<?> scheduleSingleExecutionTask(SingleExecutionTask task) {
				return null;
			}
			
			@Override
			public Future<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
				return null;
			}
		};
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		cache = new CacheBuilder<String,String>(taskScheduler).build();
		cacheLoader = mock(CacheLoader.class);
		
	}

	@After
	public void tearDown() throws Exception {

		cache.flush();
	}

	@Test
	@Parameters(value={"a","b","dummy"})
	public void test_get_should_return_null_value_when_value_is_not_cache(String key) throws Exception{
		assertNull(cache.get(key));
		cache.put(key+"a","");
		assertNull(cache.get(key));
	}
	
	@Test
	public void test_put_should_add_new_entry_and_remove_old_entry() throws Exception {

		cache.put("a", "b");
		cache.evict();
		String oldEntry = cache.put("a", "a");

		assertEquals("b", oldEntry);
		assertEquals("a", cache.get("a"));
		assertSame(1L, cache.statistics().getCacheCount());
	}
	
	public Object[][] data_provider_for_put_should_cache_value_against_provided_key(){
		
		
		return new Object[][]{
			$("a","A"),
			$("b","B"),
			$("c","C"),
			$("d","D"),
			$("e","E"),
			$("z","Z"),
		};
	}
	
	@Test
	@Parameters(method="data_provider_for_put_should_cache_value_against_provided_key")
	public void test_put_should_cache_value_against_provided_key(String key,String value) throws Exception{
		cache.put(key,value);
		assertSame(value,cache.get(key));
	}
	
	@Test
	public void test_get_should_increment_hit_count_when_value_found_from_cache() throws Exception {
	
		performCachePutAndGet();
		assertSame(4L, cache.statistics().getHitCount());
		cache.evict();
		
		cache.get("a");
		
		assertSame(5L, cache.statistics().getHitCount());
		
		cache.evict();
		cache.evict();
		cache.get("a");
		
		assertSame(6L, cache.statistics().getHitCount());
				
	}
	
	@Test
	public void test_get_should_increment_miss_count_when_value_not_found_from_cache() throws Exception {
		
		performCachePutAndGet();
		assertSame(3L, cache.statistics().getMissCount());
	}
	
	@Test
	public void test_get_should_increment_cache_request_count() throws Exception {
		
		performCachePutAndGet();
		assertSame(7L, cache.statistics().getRequestCount());
	}
	
	@Test
	public void test_get_should_increment_load_count_when_value_is_loaded_from_cache_loader() throws Exception {
		
		cache = new CacheBuilder<String,String>(taskScheduler).withCacheLoader(new CacheLoader<String,String>() {

			@Override
			public String load(String t) throws RuntimeException {
				return "str";
			}

			@Override
			public String reload(String t) throws RuntimeException {
				return null;
			}
		}).build();
		performCachePutAndGet();
		assertSame(3L, cache.statistics().getLoadCount());
	}
	
	@Test
	public void test_evict_should_increment_eviction_count() throws Exception {
		
		performCachePutAndGet();
		cache.evict();
		cache.evict();
		cache.evict();

		assertSame(3L, cache.statistics().getEvictionCount());
	}
	
	@Test
	public void test_put_should_increment_cache_count() throws Exception {
		
		performCachePutAndGet();

		assertSame(5L, cache.statistics().getCacheCount());
	}
	
	@Test
	public void test_remove_should_decrement_cache_count() throws Exception {
		
		performCachePutAndGet();
		cache.remove("a");
		cache.remove("b");
		assertSame(3L, cache.statistics().getCacheCount());
	}

	@Test
	public void test_partitiningCache_should_throw_runtime_exception_thrown_by_cache_load() throws Exception {
		
		exception.expect(RuntimeException.class);
		
		when(cacheLoader.load(anyString())).thenThrow(new RuntimeException());
		
		cache = new CacheBuilder<String,String>(taskScheduler).withCacheLoader(cacheLoader).build();
		
		cache.get("a");
	}
	
	@Test
	public void test_get_should_not_increment_load_count_evenif_runtime_exception_occures_while_cache_load() throws Exception {
		when(cacheLoader.load(anyString())).thenThrow(new RuntimeException());
		
		cache = new CacheBuilder<String,String>(taskScheduler).withCacheLoader(cacheLoader).build();
		
		try {
			cache.get("a");
			cache.get("b");
		} catch (Exception e) {}
		
		assertSame(0L, cache.statistics().getLoadCount());
	}
	
	private void performCachePutAndGet() throws Exception {
		
		cache.put("a", "A");
		cache.put("b", "B");
		cache.put("c", "C");
		cache.put("d", "D");
		cache.put("e", "E");
		
		cache.get("a");
		cache.get("b");
		cache.get("c");
		cache.get("d");
		cache.get("f");
		cache.get("g");
		cache.get("h");
	}
		
	@Test
	public void test_put_should_not_allow_null_key(){
		exception.expect(NullPointerException.class);
		cache.put(null, "a");
		
	}
	

	
	public Object[][] data_provider_get_should_return_value_for_key_if_set(){
		
		
		return new Object[][]{
			$("a","A"),
			$("b","B"),
			$("c","C"),
			$("d","D"),
			$("e","E"),
			$("z","Z"),
		};
	}
	
	@Test
	@Parameters(method="data_provider_get_should_return_value_for_key_if_set")
	public void test_get_should_return_cached_value_for_key_if_set(String key,String value) throws Exception{
		cache.put(key,value);
		assertSame(value,cache.get(key));
	}
	
	
	public Object[][] data_provider_for_remove_should_remove_cached_value_for_key(){
		
		
		return new Object[][]{
			$("a","A"),
			$("b","B"),
			$("c","C"),
			$("d","D"),
			$("e","E"),
			$("z","Z"),
		};
	}
	
	@Test
	@Parameters(method="data_provider_for_remove_should_remove_cached_value_for_key")
	public void test_remove_should_return_and_remove_cached_value_for_key(String key,String value) throws Exception{
		cache.put(key,value);
		assertSame(value,cache.remove(key));
		assertNull(value,cache.get(key));
		
		cache.put(key,value);
		cache.evict();
		assertSame(value,cache.remove(key));
		assertNull(value,cache.get(key));
		
		cache.put(key,value);
		cache.evict();
		cache.evict();
		assertSame(value,cache.remove(key));
		assertNull(value,cache.get(key));
	}
	
	@Test
	public void test_evict_should_remove_cached_values_when_clalled_for_number_of_partitions_times() throws Exception{
		String key = "a";
		String value = "b";
		String key2 = "a";
		String value2 = "b";
		cache.put(key,value);
		cache.put(key2,value2);
		cache.evict();
		cache.evict();
		cache.evict();
		assertNull(cache.get(key));
		assertNull(cache.get(key2));
	}
	
	@Test
	public void test_flush_should_clear_all_cached_values() throws Exception{
		String key = "a";
		String value = "b";
		String key2 = "a";
		String value2 = "b";
		cache.put(key,value);
		cache.put(key2,value2);
		cache.flush();
		assertNull(cache.get(key));
		assertNull(cache.get(key2));
	}
	
	
	@Test
	public void test_get_should_fetch_and_cache_data_from_the_cache_loader_if_cache_not_found() throws Exception{
		
		String key = "a";
		final String value = key;
		cache = new CacheBuilder<String,String>(taskScheduler).withCacheLoader(cacheLoader).build();
		
		when(cacheLoader.load(key)).thenReturn(value);

		assertSame(value, cache.get(key));
		assertSame(value, cache.get(key));
		
		verify(cacheLoader, times(1)).load(key);
	}


	
	@Test
	public void test_get_should_not_cache_data_from_the_cache_loader_if_data_is_null() throws Exception{
		
		String key = "a";
		final String value = key;
		cache = new CacheBuilder<String,String>(taskScheduler).withCacheLoader(cacheLoader).build();
		
		when(cacheLoader.load(key)).thenReturn(null);

		assertNull(value, cache.get(key));
		assertNull(value, cache.get(key));
		
		verify(cacheLoader, times(2)).load(key);
	}

	@Test
	public void test_getWithoutLoad_should_not_fetch_data_from_the_cache_loader_if_cache_not_found() throws Exception{

		String key = "a";
		final String value = key;
		cache = new CacheBuilder<String,String>(taskScheduler).withCacheLoader(cacheLoader).build();

		when(cacheLoader.load(key)).thenReturn(value);

		assertNull(value, cache.getWithoutLoad(key));
		assertNull(value, cache.getWithoutLoad(key));

		verify(cacheLoader, times(0)).load(key);
	}

	public Object[][] data_provider_getWithoutLoad_should_return_value_for_key_if_set(){


		return new Object[][]{
				$("a","A"),
				$("b","B"),
				$("c","C"),
				$("d","D"),
				$("e","E"),
				$("z","Z"),
		};
	}

	@Test
	@Parameters(method="data_provider_getWithoutLoad_should_return_value_for_key_if_set")
	public void test_getWithoutLoad_should_return_cached_value_for_key_if_set(String key,String value) throws Exception{
		cache.put(key,value);
		assertSame(value,cache.getWithoutLoad(key));
	}

	@Test
	@Parameters(value={"a","b","dummy"})
	public void test_getWithoutLoad_should_return_null_value_when_value_is_not_cache(String key) throws Exception{
		assertNull(cache.getWithoutLoad(key));
		cache.put(key+"a","");
		assertNull(cache.getWithoutLoad(key));
	}

	@Test
	public void test_getWithoutLoad_should_increment_cache_request_count() throws Exception {

		cache.getWithoutLoad("a");
		cache.getWithoutLoad("b");
		cache.getWithoutLoad("c");

		assertSame(3L, cache.statistics().getRequestCount());
	}

	@Test
	public void test_getWithoutLoad_should_increment_hit_count_when_value_found_from_cache() throws Exception {

		cache.put("a", "A");
		cache.getWithoutLoad("a");
		assertSame(1L, cache.statistics().getHitCount());
		cache.evict();

		cache.getWithoutLoad("a");

		assertSame(2L, cache.statistics().getHitCount());

		cache.evict();
		cache.evict();
		cache.getWithoutLoad("a");

		assertSame(3L, cache.statistics().getHitCount());

	}

	@Test
	public void test_getWithoutLoad_should_increment_miss_count_when_value_not_found_from_cache() throws Exception {

		cache.put("a", "A");
		cache.getWithoutLoad("c");
		cache.getWithoutLoad("d");
		cache.getWithoutLoad("b");
		assertSame(3L, cache.statistics().getMissCount());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_refresh_should_not_cache_data_from_the_cache_loader_if_data_is_null() throws Exception{
		
		String key = "a";
		final String value = key;
		CacheLoader<String,String> cacheLoader = mock(CacheLoader.class);
		cache = new CacheBuilder<String,String>(taskScheduler).withCacheLoader(cacheLoader).build();
		
		when(cacheLoader.load(key)).thenReturn(null);
		when(cacheLoader.reload(key)).thenReturn(null);

		assertNull(value, cache.refresh(key));
		assertNull(value, cache.get(key));

		verify(cacheLoader, times(1)).load(key);
		verify(cacheLoader, times(1)).reload(key);
	}
	
	
	@Test
	public void test_refresh_should_fetch_and_cache_value_from_cacheLoader() throws Exception {
		final String data = "a";
		final String freshData = "b";
		cache = new CacheBuilder<String,String>(taskScheduler).withCacheLoader(new CacheLoader<String,String>() {

			@Override
			public String load(String t) throws RuntimeException {
				return data;
			}

			@Override
			public String reload(String t) throws RuntimeException {
				return freshData;
			}
		}).build();
		
		assertSame(data, cache.get("d"));
		assertSame(freshData, cache.refresh("d"));
		//cached
		assertSame(freshData, cache.get("d"));
	}
	
	
	@Test
	public void test_refresh_should_not_increment_load_count_evenif_runtime_exception_occures_while_cache_load() throws Exception {
		@SuppressWarnings("unchecked")
		CacheLoader<String, String> cacheLoader = mock(CacheLoader.class);
		
		when(cacheLoader.reload(anyString())).thenThrow(new RuntimeException());
		
		cache = new CacheBuilder<String,String>(taskScheduler).withCacheLoader(cacheLoader).build();
		
		try {
			cache.refresh("a");
			cache.refresh("b");
		} catch (Exception e) { }
		
		assertSame(0L, cache.statistics().getLoadCount());
	}
	



	public void test_refresh_should_throw_NullPointerException_when_null_key_provided() throws Exception {
		exception.expect(NullPointerException.class);
		exception.expectMessage("key is null");
	
		cache.refresh(null);
	}
	
	@Test
	public void test_refresh_should_return_null_when_cacheLoader_not_provided() throws Exception {
	
		cache = new CacheBuilder<String,String>(taskScheduler).build();
		cache.put("a", "A");
		
		assertNull(cache.refresh("a"));
		
	}
	@Test
	public void test_partitions_cache_should_cache_data_received_from_cache_loader() throws Exception{
				
		@SuppressWarnings("unchecked")
		CacheLoader<String, String> cacheLoader = mock(CacheLoader.class);
		
		when(cacheLoader.load(anyString())).thenReturn("c");
		
		cache = new CacheBuilder<String,String>(taskScheduler).withCacheLoader(cacheLoader).build();
		
		
		cache.get("b");
		cache.get("b");
		cache.get("b");
		cache.get("b");
		verify(cacheLoader,times(1)).load(anyString());
		
	}
	
	@Test
	public void test_remove_should_throw_NullPointerExeption_when_null_key_provided() throws Exception {
		@SuppressWarnings("unchecked")
		CacheLoader<String, String> cacheLoader = mock(CacheLoader.class);
		
		cache = new CacheBuilder<String,String>(taskScheduler).withCacheLoader(cacheLoader).build();
		
		exception.expect(NullPointerException.class);
		
		cache.remove(null);
	}

	@Test
	public void test_cache_should_promote_key_on_get_if_found() throws Exception{
		performCachePutAndGet();
		cache.evict();
		cache.evict();
		cache.get("a");
		cache.evict();

		assertEquals("A", cache.get("a"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_cache_should_notify_about_cache_added_when_element_is_added_in_cache() {
		CacheEventListener<String, String> cacheEventListener = mock(CacheEventListener.class);
		cache.registerEventListener(cacheEventListener);

		cache.put("a", "A");

		verify(cacheEventListener,times(1)).cacheAdded("a", "A");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_cache_should_notify_about_cache_removed_when_element_is_replace_with_new_element_in_cache() {
		CacheEventListener<String, String> cacheEventListener = mock(CacheEventListener.class);
		cache.registerEventListener(cacheEventListener);

		cache.put("a", "B");
		cache.put("a", "A");

		verify(cacheEventListener,times(1)).cacheRemoved("a", "B");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_cache_should_notify_to_cacheEventListener_about_cache_removed_when_element_is_removed_from_cache() {
		CacheEventListener<String, String> cacheEventListener = mock(CacheEventListener.class);
		cache.registerEventListener(cacheEventListener);

		cache.put("a", "A");
		cache.remove("a");

		verify(cacheEventListener,times(1)).cacheRemoved("a", "A");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_cache_should_not_notify_to_eventListener_about_cache_removed_when_element_is_not_removed_from_cache() {
		CacheEventListener<String, String> cacheEventListener = mock(CacheEventListener.class);
		cache.registerEventListener(cacheEventListener);

		cache.put("a", "A");
		cache.remove("b");

		verify(cacheEventListener,times(0)).cacheRemoved(anyString(), anyString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_cache_should_notify_to_cacheEventListener_about_cache_removed_for_each_element_removed_when_cache_is_flush() {
		CacheEventListener<String, String> cacheEventListener = mock(CacheEventListener.class);
		cache.registerEventListener(cacheEventListener);

		cache.put("a", "A");
		cache.evict();
		cache.put("b", "B");
		cache.evict();
		cache.put("c", "C");
		cache.put("d", "D");
		cache.flush();

		verify(cacheEventListener,times(1)).cacheRemoved("a", "A");
		verify(cacheEventListener,times(1)).cacheRemoved("b", "B");
		verify(cacheEventListener,times(1)).cacheRemoved("c", "C");
		verify(cacheEventListener,times(1)).cacheRemoved("d", "D");

	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_cache_should_notify_to_cacheEventListener_about_cache_removed_for_each_element_removed_when_cache_is_evicted() {
		CacheEventListener<String, String> cacheEventListener = mock(CacheEventListener.class);
		cache.registerEventListener(cacheEventListener);

		cache.put("a", "A");
		cache.put("b", "B");
		cache.put("c", "C");
		cache.put("d", "D");

		cache.evict();
		verify(cacheEventListener,times(0)).cacheRemoved("a", "A");
		verify(cacheEventListener,times(0)).cacheRemoved("b", "B");
		verify(cacheEventListener,times(0)).cacheRemoved("c", "C");
		verify(cacheEventListener,times(0)).cacheRemoved("d", "D");

		cache.evict();
		verify(cacheEventListener,times(0)).cacheRemoved("a", "A");
		verify(cacheEventListener,times(0)).cacheRemoved("b", "B");
		verify(cacheEventListener,times(0)).cacheRemoved("c", "C");
		verify(cacheEventListener,times(0)).cacheRemoved("d", "D");

		cache.evict();
		verify(cacheEventListener,times(1)).cacheRemoved("a", "A");
		verify(cacheEventListener,times(1)).cacheRemoved("b", "B");
		verify(cacheEventListener,times(1)).cacheRemoved("c", "C");
		verify(cacheEventListener,times(1)).cacheRemoved("d", "D");

	}


	public void test_cache_should_promote_element_when_refresh_called() throws Exception {
		cache.put("a", "A");
		cache.evict();
		cache.evict();
		cache.refresh("a");
		cache.evict();
	}
}
