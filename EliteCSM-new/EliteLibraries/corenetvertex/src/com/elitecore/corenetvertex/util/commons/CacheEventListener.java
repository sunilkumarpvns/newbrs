package com.elitecore.corenetvertex.util.commons;

import java.util.Map;
import java.util.Set;

/**
 * Created by harsh on 5/21/16.
 */
public interface CacheEventListener<K,V> {
    void cacheRemoved(K key,V value);
    void cacheAdded(K key,V value);
    void evict(Set<Map.Entry<K,V>> entrySet);
}
