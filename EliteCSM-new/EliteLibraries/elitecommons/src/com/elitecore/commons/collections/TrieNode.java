package com.elitecore.commons.collections;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Equality;

public class TrieNode<V> {
	
	private Map<Character, TrieNode<V>> children; 
	private V value;
	private boolean hasValue;
	
	public TrieNode() {
		this.children = new HashMap<Character, TrieNode<V>>(); 
	}
	
	@VisibleForTesting
	TrieNode(Map<Character, TrieNode<V>> children) {
		this.children = children;
	}

	public TrieNode(V value) {
		this();
		this.value = value;
		this.hasValue = true;
	}

	public V getValue() {
		return value;
	}
	
	public void putValue(V value) {
		this.hasValue = true;
		this.value = value;
	}
	
	public TrieNode<V> get(char key) {
		return children.get(key);
	}
	
	public void put(char key, TrieNode<V> child) {
		children.put(key, child);
	}

	public boolean contains(char key) {
		return children.containsKey(key);
	}
	
	public boolean isLeaf() {
		return children.isEmpty();
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj == this) {
			return true;
		}
		if (obj instanceof TrieNode == false) {
			return false;
		}
		@SuppressWarnings("unchecked")
		TrieNode<V> that = (TrieNode<V>) obj;
		
		return this.hasValue == that.hasValue &&
				Equality.areEqual(this.value, that.value) && 
				Equality.areEqual(this.children, that.children);
	}

	boolean hasValue() {
		return hasValue;
	}

}
