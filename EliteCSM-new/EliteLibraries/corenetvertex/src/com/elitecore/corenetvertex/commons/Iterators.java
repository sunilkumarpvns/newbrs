package com.elitecore.corenetvertex.commons;

import java.util.Iterator;
import com.elitecore.corenetvertex.util.DistinctElementIterator;

/**
 * Created by chetan on 31/5/17.
 */
public class Iterators {


    /**
     * Creates Iterator that will give only distinct elements when next is called.
     *
     *
     * @param iterator
     * @param <T>
     * @return
     */
    public static <T> Iterator<T> distinctElementIterator(Iterator<T> iterator) {
           return new DistinctElementIterator<T>(iterator);
    }

}
