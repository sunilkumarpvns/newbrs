package com.elitecore.commons.collections;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cases")
public class TrieMatchCaseCollection {

	private List<TrieMatchCaseData> cases;
	
	@XmlElement(name = "case")
	public List<TrieMatchCaseData> getCases() {
		return cases;
	}

	public void setCases(List<TrieMatchCaseData> cases) {
		this.cases = cases;
	}
	
	@Override
	public String toString() {
		return cases.toString();
	}
}
