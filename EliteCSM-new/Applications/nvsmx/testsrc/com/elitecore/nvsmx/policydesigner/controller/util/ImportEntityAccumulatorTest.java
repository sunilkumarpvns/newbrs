package com.elitecore.nvsmx.policydesigner.controller.util;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by dhyani on 9/6/17.
 */


public class ImportEntityAccumulatorTest{

    @Test
    public void test_get_list_of_values_of_givenIndex() {

        List<String> entityList = new ArrayList<String>();
        entityList.add("String1");
        entityList.add("String2");
        entityList.add("String3");
        entityList.add("String4");
        entityList.add("String5");
        String selectedEntityIndexes = "0,1,3,4";

        ImportEntityAccumulator<String> stringImportEntityAccumulator = new ImportEntityAccumulator<String>(entityList,selectedEntityIndexes);

        List<String> expectedList = new ArrayList<String>();
        expectedList.add("String1");
        expectedList.add("String2");
        expectedList.add("String4");
        expectedList.add("String5");

        assertEquals(expectedList, stringImportEntityAccumulator.get());

    }
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test_get_throwsArrayIndexOutOfBoundsException_when_EntityListDoesNotHaveValueAtPassedIndex() {
        List<String> entityList = new ArrayList<String>();
        entityList.add(null);
        entityList.add("String2");
        String selectedEntityIndexes = "0";

        ImportEntityAccumulator<String> stringImportEntityAccumulator = new ImportEntityAccumulator<String>(entityList,selectedEntityIndexes);

        expectedException.expect(ArrayIndexOutOfBoundsException.class);
        stringImportEntityAccumulator.get();
    }

    @Test
    public void test_get_throwsArrayIndexOutOfBoundsException_when_IndexIsGreaterThenListSize() {
        List<String> entityList = new ArrayList<String>();
        entityList.add(null);
        entityList.add("String2");
        String selectedEntityIndexes = "5";

        ImportEntityAccumulator<String> stringImportEntityAccumulator = new ImportEntityAccumulator<String>(entityList,selectedEntityIndexes);

        expectedException.expect(ArrayIndexOutOfBoundsException.class);
        stringImportEntityAccumulator.get();
    }

}
