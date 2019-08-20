package com.elitecore.elitesm.ws.rest.utility;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="records")
public class ListWrapper<T> {

	private List<T> datalist;

	@XmlElement(name = "data")
	public List<T> getDatalist() {
		return datalist;
	}
	
	public void setDatalist(List<T> datalist) {
		this.datalist = datalist;
	}
}