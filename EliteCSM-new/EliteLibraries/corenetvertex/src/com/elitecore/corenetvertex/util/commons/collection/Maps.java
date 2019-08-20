package com.elitecore.corenetvertex.util.commons.collection;

import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Predicates;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains utility methods related to map
 * Created by aditya on 5/31/17.
 */
public class Maps {

    /** To create map from exiting map after applying provided predicate
     * @param map sourcemap
     * @param predicate on which the decision will be made to copy to new map or not
     * @return
     */
    public static @Nonnull <K, V> Map<K, V> copy(@Nonnull Map<K, V> map, @Nonnull Predicate<Map.Entry<K, V>> predicate) {

        Map<K, V> newMap = new HashMap<K, V>();

        for (Map.Entry<K, V> entry : map.entrySet()) {

            if (predicate.apply(entry)) {
                newMap.put(entry.getKey(), entry.getValue());
            }

        }
        return newMap;
    }

    public static
    @Nonnull
    <K, V> Map<K, V> copy(@Nonnull Map<K, V> map) {
        return copy(map, Predicates.<Map.Entry<K, V>>alwaysTrue());
    }
}
