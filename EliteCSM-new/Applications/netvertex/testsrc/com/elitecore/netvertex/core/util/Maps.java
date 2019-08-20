package com.elitecore.netvertex.core.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


public class Maps {

	private Maps() {}


	/*public static <K,V> LinkedHashMap<K, V> newLinkedHashMap(Collection<Entry<K,V>> keys){
		LinkedHashMap<K, V> linkedHashMap = new LinkedHashMap<K, V>();

		for (Entry<K, V> key : keys) {
			linkedHashMap.put(key.key, key.value);
		}

		return linkedHashMap;

	}*/


	public static <K,V> LinkedHashMap<K, V> newLinkedHashMap(Collection<? extends Map.Entry<K,V>> keys){
		LinkedHashMap<K, V> linkedHashMap = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> key : keys) {
			linkedHashMap.put(key.getKey(), key.getValue());
		}

		return linkedHashMap;

	}
	public static <K,V> LinkedHashMap<K, V> newLinkedHashMap(Map.Entry<K,V>... keys){
		LinkedHashMap<K, V> linkedHashMap = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> key : keys) {
			linkedHashMap.put(key.getKey(), key.getValue());
		}

		return linkedHashMap;

	}



	public static class Entry<K,V> implements Map.Entry<K, V> {

		private V value;
		private final K key;

		private Entry(K key, V value){
			this.key = key;
			this.value = value;

		}

		@Override
		public K getKey(){
			return key;
		}

		@Override
		public V getValue(){
			return value;
		}

		@Override
		public V setValue(V newValue) {
			V oldValue = this.value;
			this.value = newValue;
			return value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Entry<?, ?> entry = (Entry<?, ?>) o;

			if (value != null ? !value.equals(entry.value) : entry.value != null) return false;
			return !(key != null ? !key.equals(entry.key) : entry.key != null);

		}

		@Override
		public int hashCode() {
			int result = value != null ? value.hashCode() : 0;
			result = 31 * result + (key != null ? key.hashCode() : 0);
			return result;
		}

		public static <K,V> Entry<K,V> newEntry(K key, V value) {
			return new Entry<K,V>(key,value);
		}
	}

}
