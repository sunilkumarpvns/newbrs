package com.elitecore.corenetvertex.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.util.commons.Supplier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by chetan on 31/5/17.
 */
public class MultiCollectionIteratorTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test_hasNext_should_give_false_when_null_collection_is_passed() {
        MultiCollectionIterator<Integer> iterator = new MultiCollectionIterator<Integer>(new Supplier<Collection<Integer>>() {
            @Override
            public Collection<Integer> supply() {
                return null;
            }
        });
        assertFalse(iterator.hasNext());
    }
    
    @Test
    public void test_hasNext_should_give_false_when_Empty_collection_is_passed() {
        MultiCollectionIterator<Integer> iterator = new MultiCollectionIterator<Integer>(new Supplier<Collection<Integer>>() {
            @Override
            public Collection<Integer> supply() {
                return Collectionz.newArrayList();
            }
        });
        assertFalse(iterator.hasNext());
    }
    
    
    @Test
    public void test_hasNext_should_give_true_multiple_times_when_element_exist_in_collection() {
        MultiCollectionIterator<Integer> iterator = new MultiCollectionIterator<Integer>(new Supplier<Collection<Integer>>() {
            @Override
            public Collection<Integer> supply() {
                return Arrays.asList(1);
            }
        });
        
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
    }
    
    /*
     * test hasNext on a collection with one item (returns true, several times)
     */
    @Test
    public void test_hasNext_next_should_give_true_first_multiple_times_when_element_exist_in_collection() {
        MultiCollectionIterator<Integer> iterator = new MultiCollectionIterator<Integer>(new Supplier<Collection<Integer>>() {
            @Override
            public Collection<Integer> supply() {
                return Arrays.asList(1);
            }
        });
        
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
    }
    /*
     * test hasNext/next on a collection with one item: hasNext returns true, next returns the item, hasNext returns false, twice
     */
    @Test
    public void test_hasNext_next_should_give_true_first_on_first_hasNext_and_false_after_next() {
        MultiCollectionIterator<Integer> iterator = new MultiCollectionIterator<Integer>(new Supplier<Collection<Integer>>() {
            @Override
            public Collection<Integer> supply() {
                return Arrays.asList(1);
            }
        });
        
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
    }

    /*
     * test next() on an empty collection (throws exception)
     */
    @Test(expected = NoSuchElementException.class)
    public void test_next_should_throw_NoSuchElementException_when_null_collection_is_passed() {
        MultiCollectionIterator<Integer> iterator = new MultiCollectionIterator<Integer>(new Supplier<Collection<Integer>>() {
            @Override
            public Collection<Integer> supply() {
                return null;
            }
        });
        
        iterator.next();
    }
    
    /*
     * test next() on an empty collection (throws exception)
     */
    @Test(expected = NoSuchElementException.class)
    public void test_next_should_throw_NoSuchElementException_when_empty_collection_is_passed() {
        MultiCollectionIterator<Integer> iterator = new MultiCollectionIterator<Integer>(new Supplier<Collection<Integer>>() {
            @Override
            public Collection<Integer> supply() {
                return Collectionz.newArrayList();
            }
        });
        
        iterator.next();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void test_next_twice_should_throw_NoSuchElementException_when_only_one_element_exist() {

        final List<Integer> list1 = Arrays.asList(16);

        MultiCollectionIterator<Integer> actualIterator = new MultiCollectionIterator<Integer>(new Supplier<Collection<Integer>>() {
            @Override
            public Collection<Integer> supply() {
                return list1;
            }
        });

        actualIterator.next(); // there is only one element
        actualIterator.next(); // at this position, exception should throw
    }
    
    /*
     * test remove on that collection: check size is 0 after
     */
    @Test
    public void test_remove_should_empty_the_collection_when_only_one_element_exist() {

        final List<Integer> list1 = new ArrayList<Integer>(Arrays.asList(16));

        MultiCollectionIterator<Integer> actualIterator = new MultiCollectionIterator<Integer>(new Supplier<Collection<Integer>>() {
            @Override
            public Collection<Integer> supply() {
                return list1;
            }
        });

        actualIterator.next();
        actualIterator.remove();
        assertEquals(0, list1.size());
    }
    

    @Test
    public void test_next_should_iterate_all_collections_in_same_order_that_passed() {

        List<Integer> list1 = Arrays.asList(16,14,13,12,15,17);
        List<Integer> list2 = Arrays.asList(2,7,6,4,9,5);

        List<List<Integer>> collections = Arrays.asList(list1, list2);

        final Iterator<List<Integer>> iterator = collections.iterator();

        MultiCollectionIterator<Integer> actualIterator = new MultiCollectionIterator<Integer>(new Supplier<Collection<Integer>>() {
            @Override
            public Collection<Integer> supply() {

                if (iterator.hasNext()) {
                    return iterator.next();
                }
                return null;
            }
        });

        Iterator<Integer> iterator1 = list1.iterator();
        Iterator<Integer> iterator2 = list2.iterator();

        while(actualIterator.hasNext()) {
            if (iterator1.hasNext()) {
                assertEquals(iterator1.next(), actualIterator.next());
            } else if (iterator2.hasNext()) {
                assertEquals(iterator2.next(), actualIterator.next());
            }
        }
    }

   
}
