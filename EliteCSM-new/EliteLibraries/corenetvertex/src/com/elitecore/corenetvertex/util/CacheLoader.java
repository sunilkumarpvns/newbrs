package com.elitecore.corenetvertex.util;

public interface CacheLoader<K, V> {
	V load(K t) throws Exception;
	V reload(K t) throws Exception;
}
