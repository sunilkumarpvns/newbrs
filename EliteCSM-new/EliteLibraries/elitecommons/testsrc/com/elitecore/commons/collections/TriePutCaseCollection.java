package com.elitecore.commons.collections;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cases")
public class TriePutCaseCollection {

	private List<TriePutCaseData> cases;
	
	@XmlElement(name = "case")
	public List<TriePutCaseData> getCases() {
		return cases;
	}

	public void setCases(List<TriePutCaseData> cases) {
		this.cases = cases;
	}
	
	@Override
	public String toString() {
		return cases.toString();
	}
}
