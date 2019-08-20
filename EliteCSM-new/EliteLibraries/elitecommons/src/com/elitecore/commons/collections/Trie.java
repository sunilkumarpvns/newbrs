package com.elitecore.commons.collections;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * <p>This is Prefix Tree Implementation.</p> 
 * <p>
 * Nodes are not stored in order. 
 * Any node can contain Value.   
 * </p>
 * 
 * <pre>
 *                          ROOT
 *                    +---------------+ 
 *                   /                 \
 *                  A(value)            E
 *                 +--------+         +---+
 *                /          \          | 
 *               B           D(value)   F(value)
 *             +---+          
 *               |
 *               C(value)  
 * </pre>
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a tree concurrently, 
 * and at least one of the threads modifies the map structurally, 
 * it <i>must</i> be synchronized externally.
 * </p>
 * 
 * @param V type of value
 */
@NotThreadSafe
public class Trie<V> {
	
	private TrieNode<V> root;
	
	public Trie() {
		root = new TrieNode<V>();
	}
	
	public Trie(Map<Character, TrieNode<V>> treeBranches) {
		root = new TrieNode<V>(treeBranches);
	}
	
	/**
	 * <p>
	 * This will insert a key into trie. 
	 * Every character of input key is inserted 
	 * as an individual trie node.
	 * </p> 
	 * 
	 * <p>Time Complexity: Time complexity of 
	 * putting in trie is O(n) 
	 * where n is length of the input key.
	 * </p>
	 * @param key input for inserting nodes
	 * @param value against leaf node
	 */
	public void put(String key, V value) {

		TrieNode<V> parent = root;
		
		int index = 0;
		while (index < key.length()) {

			char nodeIndex = key.charAt(index++);
			
			TrieNode<V> child = parent.get(nodeIndex);
			if (child == null) {
				child = new TrieNode<V>();
				parent.put(nodeIndex, child);
			}
			parent = child;
		}
		parent.putValue(value);
	}
	
	/**
	 * <p>
	 * For below Trie,</p>
	 * <pre>
	 *                          ROOT
	 *                    +---------------+ 
	 *                   /                 \
	 *                  a(A)                e
	 *                 +--------+         +---+
	 *                /          \          | 
	 *               b           d(D)       f(F)
	 *             +---+          
	 *               |
	 *               c(C)  
	 * </pre>
	 * <table>
	 * <tr>
	 * <th>Search Key</th>
	 * <th>Value</th>
	 * <th>Time Complexity</th>
	 * </tr>
	 * <tr>
	 * <td>abcdef</td>
	 * <td>C</td>
	 * <td>3</td>
	 * </tr>
	 * <tr>
	 * <td>abdcef</td>
	 * <td>A</td>
	 * <td>2</td>
	 * </tr>
	 * <tr>
	 * <td>adbcef</td>
	 * <td>D</td>
	 * <td>2</td>
	 * </tr>
	 * <tr>
	 * <td>ab</td>
	 * <td>A</td>
	 * <td>2</td>
	 * </tr>
	 * <tr>
	 * <td>xyz</td>
	 * <td>{@code null}</td>
	 * <td>1</td>
	 * </tr>
	 * <tr>
	 * <td>abxyz</td>
	 * <td>{@code null}</td>
	 * <td>2</td>
	 * </tr>
	 * </table>
	 * 
	 * <p>In general, <strong>Time Complexity:</strong> Time complexity of finding 
	 * the longest prefix is O(n) 
	 * where n is length of the search key or maximum matched nodes, 
	 * whichever is smaller.
	 * </p>
	 * @param key input string for which longest prefix match
	 * @return value derived after finding longest match
	 */
	public V longestPrefixKeyMatch(String key) {
		return fetch(key, root, 0);
	}
	
	private V fetch(String key, TrieNode<V> node, int level) {
		/*
		 * This is best traversal.
		 */
		if (node.isLeaf()) {
			return node.getValue();
		}
	
		/*
		 * Limited data for match 
		 * 		--> will send last match possible.
		 */
		if (key.length() <= level) {
			return node.getValue();
		}

		/*
		 * No further match found
		 * 		--> send node data
		 */
		char nextNodeIndex = key.charAt(level);
		TrieNode<V> nextNode = node.get(nextNodeIndex);
		if (nextNode == null) {
			return node.getValue();
		}
		
		/*
		 * If branch is unable to match data
		 * 		--> send last match data
		 */
		V value = fetch(key, nextNode, ++level);
		if (value == null && nextNode.hasValue() == false) {
			value = node.getValue();
		}
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {

		if (obj == this) {
			return true;
		}
		if (obj instanceof Trie == false) {
			return false;
		}
		@SuppressWarnings("unchecked")
		Trie<V> that = (Trie<V>) obj;
		
		return this.root.equals(that.root);
	}
	
	public void clear() {
		this.root = new TrieNode<V>();
	}
}

