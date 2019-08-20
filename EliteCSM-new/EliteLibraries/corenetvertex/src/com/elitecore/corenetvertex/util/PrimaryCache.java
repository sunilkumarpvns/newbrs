package com.elitecore.corenetvertex.util;

import com.elitecore.corenetvertex.util.commons.CacheEventListener;

/**
 * Created by harsh on 6/9/16.
 */
public interface PrimaryCache<K, V> extends  Cache<K,V> {
    void registerEventListener(CacheEventListener<K,V> eventListener);
}
