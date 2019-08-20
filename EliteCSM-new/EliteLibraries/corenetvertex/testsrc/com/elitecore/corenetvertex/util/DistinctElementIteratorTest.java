package com.elitecore.corenetvertex.util;

import com.elitecore.corenetvertex.commons.Iterators;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by chetan on 1/6/17.
 */
@RunWith(JUnitParamsRunner.class)
public class DistinctElementIteratorTest {


    @Test
    public void test_hasNext_should_give_false_when_provided_iterator_is_empty() {

        List<String> emptyList = new ArrayList<String>();

        Iterator<String> emptyIterator = emptyList.iterator();

        DistinctElementIterator<String> iterator  = new DistinctElementIterator<String>(emptyIterator);

        assertFalse(iterator.hasNext());
    }

    @Test
    public void test_hasNext_should_give_same_result_if_invoked_without_next() {

        List<Integer> list = Arrays.asList(1);

        DistinctElementIterator<Integer> iterator  = new DistinctElementIterator<Integer>(list.iterator());

        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());

        iterator.next();

        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
    }


    @Test
    public void test_next_should_give_all_element_even_if_hasNext_not_called() {

        List<Integer> list = Arrays.asList(1,1,2);

        DistinctElementIterator<Integer> iterator  = new DistinctElementIterator<Integer>(list.iterator());

        assertEquals(list.get(0), iterator.next());
        assertEquals(list.get(2), iterator.next());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_next_should_give_throw_NoSuchElementException_when_provided_iterator_is_empty() {

        List<String> emptyList = new ArrayList<String>();

        Iterator<String> emptyIterator = emptyList.iterator();

        DistinctElementIterator<String> iterator  = new DistinctElementIterator<String>(emptyIterator);

        iterator.next();
    }

    @Test(expected = NullPointerException.class)
    public void test_next_should_give_throw_IllegalArgumentException_when_provided_iterator_is_null() {

        Iterator<String> emptyIterator = null;

        DistinctElementIterator<String> iterator  = new DistinctElementIterator<String>(emptyIterator);

        iterator.next();
    }

    public Object[][] dataProvideFor_test_next_should_give_distinct_elements_only_from_provided_iterator() {
        return new Object[][] {
                {
                        Arrays.asList(1,1), Arrays.asList(1)
                },
                {
                        Arrays.asList(1,2,3,1,2,3),Arrays.asList(1,2,3)
                },
                {
                        Arrays.asList(1,1,2,2,3,3),Arrays.asList(1,2,3)
                },
                {
                        Arrays.asList(1,1,1,2,2,2,3,3,3),Arrays.asList(1,2,3)
                }
        };
    }


    @Test
    @Parameters(method = "dataProvideFor_test_next_should_give_distinct_elements_only_from_provided_iterator")
    public void test_next_should_give_distinct_elements_only_from_provided_iterator(List<Integer> dataList, List<Integer> expectedDataList) {

        DistinctElementIterator<Integer> actualIterator  = new DistinctElementIterator<Integer>(dataList.iterator());

        Iterator expectedIterator = expectedDataList.iterator();

        for (int i=0; i<expectedDataList.size(); i++) {
            assertEquals(expectedIterator.next(), actualIterator.next());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_remove_should_throw_UnsupportedOperationException() {
        DistinctElementIterator<Integer> iterator  = new DistinctElementIterator<Integer>(Arrays.asList(1,1,2).iterator());
        iterator.remove();
    }
}
