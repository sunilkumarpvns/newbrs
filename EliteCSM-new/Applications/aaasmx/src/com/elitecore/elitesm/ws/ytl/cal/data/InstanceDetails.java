package com.elitecore.elitesm.ws.ytl.cal.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class InstanceDetails {

	private List<AAAData> listOfAAA = new ArrayList<AAAData>();
	
	@XmlElement(name = "aaa")
	public List<AAAData> getListOfAAA() {
		return listOfAAA;
	}
}
