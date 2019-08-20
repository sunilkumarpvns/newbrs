package com.elitecore.corenetvertex.util;

import com.elitecore.commons.base.Function;
import com.elitecore.commons.kpi.handler.IntervalBasedTask;
import com.elitecore.commons.kpi.handler.SingleExecutionTask;
import com.elitecore.commons.tests.PrintMethodRule;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by harsh on 6/9/16.
 */
@RunWith(JUnitParamsRunner.class)
public class InMemoryUniqueIndexCacheTest {

    private PartitioningCache<String, String> primaryCache;
    private InMemoryUniqueIndexCache<Integer, String, String> secondaryIndexCache;
    private static TaskScheduler taskScheduler;
    private SecondaryKeyFunction secondaryKeyFunction = null;
    private PrimaryKeyFunction primaryKeyFunction = null;

    @Rule
    public PrintMethodRule rule = new PrintMethodRule();
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        taskScheduler = new TaskScheduler() {

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

    @Before
    public void setUp() throws Exception {
        primaryCache = spy(new PartitioningCache.CacheBuilder<String, String>(taskScheduler).build());

        secondaryKeyFunction = spy(new SecondaryKeyFunction());
        primaryKeyFunction = spy(new PrimaryKeyFunction());

        secondaryIndexCache = spy(new InMemoryUniqueIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).build());
    }

    @After
    public void tearDown() throws Exception {
        secondaryIndexCache.flush();
        primaryCache.flush();
    }


    @Test
    @Parameters(value = {"abc", "b", "dummy"})
    public void test_get_should_return_null_value_when_value_is_not_cache(String key) throws Exception {
        assertNull(secondaryIndexCache.get(secondaryKeyFunction.apply(key)));
        secondaryIndexCache.put(secondaryKeyFunction.apply(key), key);
        assertNull(secondaryIndexCache.get(secondaryKeyFunction.apply(key)+1));
    }


    public Object[][] data_provider_for_put_should_cache_value_against_provided_key() {


        return new Object[][]{
                $("A"),
                $("AB"),
                $("ABC"),
                $("ABCD"),
                $("ABCDE"),
                $("ABCDEF"),
        };
    }

    @Test
    @Parameters(method = "data_provider_for_put_should_cache_value_against_provided_key")
    public void test_put_should_cache_value_against_provided_key(String value) throws Exception {
        Integer key = secondaryKeyFunction.apply(value);
        secondaryIndexCache.put(key, value);
        assertSame(value, secondaryIndexCache.get(key));
    }

    @Test
    public void test_put_should_call_primary_cache_for_value_using_primary_key_stored_in_cache() throws Exception {
        String value = "ABC";
        Integer key = secondaryKeyFunction.apply(value);
        primaryCache = spy(new PartitioningCache.CacheBuilder<String, String>(taskScheduler).build());
        secondaryIndexCache = new InMemoryUniqueIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).build();
        secondaryIndexCache.put(key, value);
        secondaryIndexCache.get(key);
        verify(primaryCache, times(1)).getWithoutLoad(primaryKeyFunction.apply(value));
        assertSame(value, secondaryIndexCache.get(key));
    }

    @Test
     public void test_put_should_return_null_value_if_primary_key_function_return_different_key_as_previous_key_for_new_value_and_primary_cache_does_not_store_any_value_for_that_key() throws Exception {
        String value = "ABC";
        Integer key = secondaryKeyFunction.apply(value);

        secondaryIndexCache.put(key, value);

        String newValue = "DEF";
        Integer newKey = secondaryKeyFunction.apply(value);

        assertSame(null, secondaryIndexCache.put(newKey, newValue));
    }

    @Test
    public void test_put_should_return_old_value_stored_in_cache_if_primary_key_function_return_same_key_as_previous_key_for_new_value() throws Exception {
        String value = "ABC";
        Integer key = secondaryKeyFunction.apply(value);

        secondaryIndexCache.put(key, value);
        assertSame(value, secondaryIndexCache.put(key, value));
    }

    @Test
    public void test_put_should_return_null_value_if_no_value_stored_in_cache_for_same_key() throws Exception {
        String value = "ABC";
        Integer key = secondaryKeyFunction.apply(value);

        assertNull(secondaryIndexCache.put(key, value));
    }

    @Test
    public void test_put_should_return_null_and_not_store_value_if_primary_function_return_null_value() throws Exception {
        String value = "ABC";
        Integer key = secondaryKeyFunction.apply(value);
        when(primaryKeyFunction.apply(value)).thenReturn(null);
        assertNull(secondaryIndexCache.put(key, value));
        assertNull(secondaryIndexCache.get(key));
    }



    @Test
    public void test_get_should_increment_hit_count_when_value_found_from_cache() throws Exception {

        performCachePutAndGet();
        assertSame(4L, secondaryIndexCache.statistics().getHitCount());
        secondaryIndexCache.get(secondaryKeyFunction.apply("ABC"));
        assertSame(5L, secondaryIndexCache.statistics().getHitCount());
        secondaryIndexCache.get(secondaryKeyFunction.apply("DEFG"));
        assertSame(6L, secondaryIndexCache.statistics().getHitCount());

    }

    @Test
    public void test_get_should_increment_miss_count_when_value_not_found_from_cache() throws Exception {

        performCachePutAndGet();
        secondaryIndexCache.get(secondaryKeyFunction.apply("ABCEFGHIJKLM"));
        assertSame(1L, secondaryIndexCache.statistics().getMissCount());
        secondaryIndexCache.get(secondaryKeyFunction.apply("ABCEFGHIJKLM"));
        assertSame(2L, secondaryIndexCache.statistics().getMissCount());

    }

    @Test
    public void test_get_should_increment_cache_request_count() throws Exception {

        performCachePutAndGet();
        assertSame(4L, secondaryIndexCache.statistics().getRequestCount());
    }


    @Test
    public void test_get_should_increment_load_count_when_value_is_loaded_from_cache_loader() throws Exception {

        secondaryIndexCache = new InMemoryUniqueIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(new CacheLoader<Integer, String>() {

            @Override
            public String load(Integer t) throws RuntimeException {
                return "str";
            }

            @Override
            public String reload(Integer t) throws RuntimeException {
                return null;
            }
        }).build();
        secondaryIndexCache.get(secondaryKeyFunction.apply("ABCEFGHIJKLM"));
        secondaryIndexCache.get(secondaryKeyFunction.apply("ABCEFGHIJKLMS"));
        secondaryIndexCache.get(secondaryKeyFunction.apply("ABCEFGHIJKLMX"));
        assertSame(3L, secondaryIndexCache.statistics().getLoadCount());
    }

    @Test
    public void test_evict_should_increment_eviction_count() throws Exception {

        performCachePutAndGet();
        secondaryIndexCache.evict();
        secondaryIndexCache.evict();
        secondaryIndexCache.evict();

        assertSame(3L, secondaryIndexCache.statistics().getEvictionCount());
    }

    @Test
    public void test_put_should_increment_cache_count() throws Exception {

        performCachePutAndGet();

        assertSame(5L, secondaryIndexCache.statistics().getCacheCount());
    }

    @Test
    public void test_remove_should_decrement_cache_count() throws Exception {

        performCachePutAndGet();
        secondaryIndexCache.remove(secondaryKeyFunction.apply("ABC"));
        secondaryIndexCache.remove(secondaryKeyFunction.apply("DEFG"));
        assertSame(3L, secondaryIndexCache.statistics().getCacheCount());
    }

    @Test
    public void test_Cache_should_throw_runtime_exception_thrown_by_cache_load() throws Exception {

        exception.expect(RuntimeException.class);

        @SuppressWarnings("unchecked")
        CacheLoader<Integer, String> cacheLoader = mock(CacheLoader.class);

        when(cacheLoader.load(anyInt())).thenThrow(new RuntimeException());

        secondaryIndexCache = new InMemoryUniqueIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(cacheLoader).build();

        secondaryIndexCache.get(secondaryKeyFunction.apply("ABC"));
    }

    @Test
    public void test_get_should_not_increment_load_count_even_if_runtime_exception_occures_while_cache_load() throws Exception {
        @SuppressWarnings("unchecked")
        CacheLoader<Integer, String> cacheLoader = mock(CacheLoader.class);

        when(cacheLoader.load(anyInt())).thenThrow(new RuntimeException());

        secondaryIndexCache = new InMemoryUniqueIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(cacheLoader).build();

        try {
            secondaryIndexCache.get(secondaryKeyFunction.apply("ABC"));
        } catch (Exception e) {}

        assertSame(0L, secondaryIndexCache.statistics().getLoadCount());
    }

    private void performCachePutAndGet() throws Exception {

        primaryCache.put(primaryKeyFunction.apply("ABC"), "ABC");
        primaryCache.put(primaryKeyFunction.apply("DEFG"), "DEFG");
        primaryCache.put(primaryKeyFunction.apply("HIJKL"), "HIJKL");
        primaryCache.put(primaryKeyFunction.apply("MNOPQR"), "MNOPQR");
        primaryCache.put(primaryKeyFunction.apply("STUVWXY"), "STUVWXY");

        secondaryIndexCache.get(secondaryKeyFunction.apply("ABC"));
        secondaryIndexCache.get(secondaryKeyFunction.apply("DEFG"));
        secondaryIndexCache.get(secondaryKeyFunction.apply("HIJK"));
        secondaryIndexCache.get(secondaryKeyFunction.apply("LMNOP"));

    }

    @Test
    public void test_put_should_allow_null_key(){
        secondaryIndexCache.put(null, "a");
    }

    @Test
    public void test_put_should_not_allow_null_value(){
        exception.expect(NullPointerException.class);
        secondaryIndexCache.put(1, null);
    }




    public Object[][] data_provider_get_should_return_value_for_key_if_set(){


        return new Object[][]{
                $("A"),
                $("B"),
                $("C"),
                $("D"),
                $("E"),
                $("Z"),
        };
    }

    @Test
    @Parameters(method="data_provider_get_should_return_value_for_key_if_set")
    public void test_get_should_return_cached_value_for_key_if_primary_key_function_return_non_null_primary_key_and_primary_cache_has_value_for_that_primary_cache(String value) throws Exception{
        Integer key = secondaryKeyFunction.apply(value);
        String primaryKey = primaryKeyFunction.apply(value);
        secondaryIndexCache.put(key,value);
        assertSame(value, primaryCache.get(primaryKey));
        assertSame(value,secondaryIndexCache.get(key));
    }

    @Test
    @Parameters(method="data_provider_get_should_return_value_for_key_if_set")
    public void test_get_should_remove_value_from_primary_cache_if_primary_key_function_return_non_null_primary_key_and_primary_cache_has_not_stored_value_for_that_primary_cache(String value) throws Exception{
        Integer key = secondaryKeyFunction.apply(value);
        String primaryKey = primaryKeyFunction.apply(value);
        secondaryIndexCache.put(key,value);
        when(primaryCache.getWithoutLoad(primaryKey)).thenReturn(null);
        assertNull(value, secondaryIndexCache.get(key));
        when(primaryCache.getWithoutLoad(primaryKey)).thenCallRealMethod();
        assertNull(value, primaryCache.get(primaryKey));
    }


    public Object[][] data_provider_for_remove_should_remove_cached_value_for_key(){


        return new Object[][]{
                $("A"),
                $("B"),
                $("C"),
                $("D"),
                $("E"),
                $("Z"),
        };
    }

    @Test
    @Parameters(method="data_provider_for_remove_should_remove_cached_value_for_key")
    public void test_remove_should_return_and_remove_cached_value_for_key(String value) throws Exception{
        Integer key = secondaryKeyFunction.apply(value);
        secondaryIndexCache.put(key,value);
        assertSame(value,secondaryIndexCache.remove(key));
        assertNull(value, secondaryIndexCache.remove(key));
        assertNull(value,secondaryIndexCache.get(key));
    }


    @Test
    public void test_evict_should_remove_cached_values_when_evict() throws Exception{
        String value = "b";
        int key = secondaryKeyFunction.apply(value);
        String value2 = "av";
        int key2 = secondaryKeyFunction.apply(value2);

        secondaryIndexCache.put(key,value);
        secondaryIndexCache.put(key2,value2);
        secondaryIndexCache.evict();
        assertNull(secondaryIndexCache.get(key));
        assertNull(secondaryIndexCache.get(key2));
    }

    @Test
    public void test_flush_should_clear_all_cached_values() throws Exception{
        String value = "b";
        int key = secondaryKeyFunction.apply(value);
        String value2 = "av";
        int key2 = secondaryKeyFunction.apply(value2);

        secondaryIndexCache.put(key,value);
        secondaryIndexCache.put(key2,value2);
        secondaryIndexCache.flush();
        assertNull(secondaryIndexCache.get(key));
        assertNull(secondaryIndexCache.get(key2));
    }


    @Test
    public void test_get_should_fetch_and_cache_data_from_the_cache_loader_if_cache_not_found() throws Exception{


        final String value = "str";
        int key = secondaryKeyFunction.apply(value);
        secondaryIndexCache = new InMemoryUniqueIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(new CacheLoader<Integer, String>() {

            @Override
            public String load(Integer t) throws RuntimeException {
                return value;
            }

            @Override
            public String reload(Integer t) throws RuntimeException {
                return null;
            }
        }).build();

        assertSame(value, secondaryIndexCache.get(key));
    }

    @Test
    public void test_get_should_not_cache_data_from_the_cache_loader_if_data_is_null() throws Exception{


        final String value = "str";
        int key = secondaryKeyFunction.apply(value);
        CacheLoader<Integer, String> cacheLoader = spy(new CacheLoader<Integer, String>() {

            @Override
            public String load(Integer t) throws RuntimeException {
                return null;
            }

            @Override
            public String reload(Integer t) throws RuntimeException {
                return null;
            }
        });
        secondaryIndexCache = new InMemoryUniqueIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(cacheLoader).build();


        assertNull(value, secondaryIndexCache.get(key));


        verify(cacheLoader, times(1)).load(key);
    }

    @Test
    public void test_refresh_should_not_cache_data_from_the_cache_loader_if_data_is_null() throws Exception{

        final String value = "str";
        int key = secondaryKeyFunction.apply(value);
        CacheLoader<Integer, String> cacheLoader = spy(new CacheLoader<Integer, String>() {

            @Override
            public String load(Integer t) throws RuntimeException {
                return null;
            }

            @Override
            public String reload(Integer t) throws RuntimeException {
                return null;
            }
        });
        secondaryIndexCache = new InMemoryUniqueIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(cacheLoader).build();


        assertNull(value, secondaryIndexCache.get(key));


        verify(cacheLoader, times(1)).load(key);

        assertNull(value, secondaryIndexCache.refresh(key));
        assertNull(value, secondaryIndexCache.get(key));

        verify(cacheLoader, times(2)).load(key);
        verify(cacheLoader, times(1)).reload(key);
    }


    @Test
    public void test_refresh_should_fetch_and_cache_value_from_cacheLoader() throws Exception {
        final String value = "str";
        int key = secondaryKeyFunction.apply(value);
        secondaryIndexCache = new InMemoryUniqueIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(new CacheLoader<Integer, String>() {

            @Override
            public String load(Integer t) throws RuntimeException {
                return null;
            }

            @Override
            public String reload(Integer t) throws RuntimeException {
                return value;
            }
        }).build();

        assertSame(value, secondaryIndexCache.refresh(key));
    }


    @Test
    public void test_refresh_should_not_increment_load_count_evenif_runtime_exception_occures_while_cache_load() throws Exception {
        @SuppressWarnings("unchecked")

        CacheLoader<Integer, String> cacheLoader = mock(CacheLoader.class);

        when(cacheLoader.reload(anyInt())).thenThrow(new RuntimeException());

        secondaryIndexCache = new InMemoryUniqueIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(cacheLoader).build();



        try {
            secondaryIndexCache.refresh(secondaryKeyFunction.apply("ABC"));
        } catch (Exception e) { }

        assertSame(0L, secondaryIndexCache.statistics().getLoadCount());

    }



    public void test_refresh_should_throw_NullPointerException_when_null_key_provided() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("key is null");

        secondaryIndexCache.refresh(null);
    }



    @Test
    public void test_refresh_should_return_null_when_cacheLoader_not_provided() throws Exception {

        secondaryIndexCache = new InMemoryUniqueIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).build();
        String value = "A";
        int key = secondaryKeyFunction.apply(value);
        secondaryIndexCache.put(key, value);

        assertNull(secondaryIndexCache.refresh(key));

    }


    @Test
    public void test_cache_should_cache_data_received_from_cache_loader() throws Exception{

        final String value = "A";
        int key = secondaryKeyFunction.apply(value);

        CacheLoader<Integer, String> cacheLoader = spy(new CacheLoader<Integer, String>() {

            @Override
            public String load(Integer t) throws RuntimeException {
                return value;
            }

            @Override
            public String reload(Integer t) throws RuntimeException {
                return null;
            }
        });

        secondaryIndexCache = new InMemoryUniqueIndexCache.CacheBuilder<Integer, String, String>(taskScheduler, primaryCache, primaryKeyFunction, secondaryKeyFunction).withCacheLoader(cacheLoader).build();


        secondaryIndexCache.get(key);
        secondaryIndexCache.get(key);
        secondaryIndexCache.get(key);
        secondaryIndexCache.get(key);
        verify(cacheLoader,times(1)).load(anyInt());

    }


    @Test
    public void test_remove_should_throw_NullPointerExeption_when_null_key_provided() throws Exception {
        exception.expect(NullPointerException.class);

        secondaryIndexCache.remove(null);
    }


    @Test
    @Parameters(value = {"ABC","DEF","XYZ"})
    public void test_cache_added_should_add_key_in_secodary_cache_if_secondary_function_return_non_null_value(String value) {

        assertEquals(0L,secondaryIndexCache.statistics().getCacheCount());

        primaryCache.put(primaryKeyFunction.apply(value), value);

        assertEquals(1L,secondaryIndexCache.statistics().getCacheCount());
    }

    @Test
    @Parameters(value = {"ABC","DEF","XYZ"})
    public void test_cache_added_should_not_add_key_in_secodary_cache_if_secondary_function_return_null_value(String value) {

        when(secondaryKeyFunction.apply(anyString())).thenReturn(null);

        assertEquals(0L,secondaryIndexCache.statistics().getCacheCount());

        primaryCache.put(primaryKeyFunction.apply(value), value);

        assertEquals(0L,secondaryIndexCache.statistics().getCacheCount());
    }


    @Test
    @Parameters(value = {"ABC","DEF","XYZ"})
    public void test_cache_removed_should_remove_key_in_secodary_cache_if_secondary_function_return_non_null_value(String value) throws Exception {


        primaryCache.put(primaryKeyFunction.apply(value), value);

        assertEquals(1L,secondaryIndexCache.statistics().getCacheCount());

        primaryCache.remove(primaryKeyFunction.apply(value));

        assertEquals(0L,secondaryIndexCache.statistics().getCacheCount());
    }

    @Test
    @Parameters(value = {"ABC","DEF","XYZ"})
    public void test_cache_removed_should_not_remove_key_in_secodary_cache_if_secondary_function_return_non_null_value(String value) throws Exception {


        primaryCache.put(primaryKeyFunction.apply(value), value);

        assertEquals(1L,secondaryIndexCache.statistics().getCacheCount());

        when(secondaryKeyFunction.apply(anyString())).thenReturn(null);

        primaryCache.remove(primaryKeyFunction.apply(value));

        assertEquals(1L,secondaryIndexCache.statistics().getCacheCount());
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

    @Test
    @Parameters(value = {"ABC"})
    public void test_evict_should_remove_cache(String value) throws Exception{

        primaryCache.put(value, value);

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put(value, value);

        assertEquals(1L,secondaryIndexCache.statistics().getCacheCount());

        secondaryIndexCache.evict(hashMap.entrySet());

        assertEquals(0,secondaryIndexCache.statistics().getCacheCount());
    }
}