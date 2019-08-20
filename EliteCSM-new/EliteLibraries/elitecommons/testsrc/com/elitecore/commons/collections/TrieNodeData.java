package com.elitecore.commons.collections;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.commons.collections.TrieNode;

@XmlRootElement(name = "node")
public class TrieNodeData {

	private String key;
	private String value;
	private List<TrieNodeData> children;
	
	@XmlAttribute(name = "key", required = true)
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	char key() {
		return key.charAt(0);
	}
	
	@XmlAttribute(name="value")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlElement(name = "node")
	public List<TrieNodeData> getChildren() {
		return children;
	}
	public void setChildren(List<TrieNodeData> children) {
		this.children = children;
	}
	
	public TrieNode<String> create() {
		
		TrieNode<String> node = new TrieNode<String>();
		if(value != null) {
			node.putValue(value);
		}
		if (children != null) {
			for(TrieNodeData trieNodeData : children) {
				node.put(trieNodeData.key(), trieNodeData.create());
			}
		}
		return node;
	}

	@Override
	public String toString() {
		return "Key = " + key + 
				" value = " + value +
				" children = " + (children != null ? children.toString() : ""); 
	}
	
}
