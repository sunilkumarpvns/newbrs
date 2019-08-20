package com.elitecore.commons.collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.elitecore.commons.collections.Trie;
import com.elitecore.commons.collections.TrieNode;

public class TrieMatchCaseData {

	private String name;
	private String expectedValue;
	private String queryString;
	private List<TrieNodeData> treeNodes;
	
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "query", required = true)
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String keys) {
		this.queryString = keys;
	}
	
	@XmlAttribute(name = "expected")
	public String getExpectedValue() {
		return expectedValue;
	}
	public void setExpectedValue(String expectedData) {
		this.expectedValue = expectedData;
	}
	
	@XmlElementWrapper(name = "tree")
	@XmlElement(name = "node")
	public List<TrieNodeData> getTreeNodes() {
		return treeNodes;
	}
	public void setTreeNodes(List<TrieNodeData> treeNodes) {
		this.treeNodes = treeNodes;
	}
	
	public Trie<String>  createTree() {
		return new Trie<String>(create(treeNodes));
	}
	
	private Map<Character, TrieNode<String>> create(List<TrieNodeData> trieNodes) {
		Map<Character, TrieNode<String>> links = 
				new HashMap<Character, TrieNode<String>>();
		
		for(TrieNodeData data : trieNodes) {
			links.put(data.key(), data.create());
		}
		return links;
	}
	
	@Override
	public String toString() {
		return "Name = " + name + 
				" Expected = " + expectedValue +
				" Query-String = " + queryString +
				" Tree = " + treeNodes.toString();
	}
	
}
