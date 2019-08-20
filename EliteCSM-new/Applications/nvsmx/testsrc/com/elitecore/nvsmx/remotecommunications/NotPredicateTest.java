package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Predicates;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by aditya on 6/14/17.
 */
public class NotPredicateTest {

   @Rule
   public ExpectedException expected = ExpectedException.none();

    @Test
    public void applyThrowsNullPointerExceptionWhenGivenPredicateIsNull(){
        NotPredicate<Object> notPredicate = NotPredicate.of(null);
        Object object = new Object();
        expected.expect(NullPointerException.class);
        notPredicate.apply(object);
    }

    @Test
    public void applyReverseTheResultOfGivenPredicate(){
        NotPredicate<Object> objectShouldBeNull = NotPredicate.of(Predicates.nonNull());
        Object nullObject = null;
        assertTrue(objectShouldBeNull.apply(nullObject));
    }

}