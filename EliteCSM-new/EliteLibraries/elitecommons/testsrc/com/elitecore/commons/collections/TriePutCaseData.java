package com.elitecore.commons.collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.commons.collections.Trie;
import com.elitecore.commons.collections.TrieNode;

@XmlRootElement(name = "case")
public class TriePutCaseData {

	private String name;
	private String value;
	private String keys;
	private List<Branch> branches;
	private List<TrieNodeData> expectedTrieNodes;
	
	@XmlAttribute(name = "name", required = true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "keys", required = true)
	public String getKeys() {
		return keys;
	}
	public void setKeys(String keys) {
		this.keys = keys;
	}
	
	@XmlAttribute(name = "value", required = true)
	public String getValue() {
		return value;
	}
	public void setValue(String expectedData) {
		this.value = expectedData;
	}
	
	@XmlElementWrapper(name = "expected")
	@XmlElement(name = "node")
	public List<TrieNodeData> getExpectedTrieNodes() {
		return expectedTrieNodes;
	}
	public void setExpectedTrieNodes(List<TrieNodeData> nodes) {
		this.expectedTrieNodes = nodes;
	}
	
	@XmlElementWrapper(name = "existing")
	@XmlElement(name = "branch")
	public List<Branch> getBranches() {
		return branches;
	}
	public void setBranches(List<Branch> existingTreeNodes) {
		this.branches = existingTreeNodes;
	}
	
	public Trie<String> createExpectedTree() {
		return new Trie<String>(create(expectedTrieNodes));
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
				" keys = " + keys +
				" Data = " + value +
				" Nodes = " + expectedTrieNodes.toString();
	}
	
}
