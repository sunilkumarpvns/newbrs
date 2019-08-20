package com.elitecore.elitesm.ws.rest.util;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement(name = "list")
public class ListWrapper<T> {
	
	@Valid
	@NotEmpty(message="List cannot be empty")
	private List<T> list;
	
	public ListWrapper() {
		this.list = new ArrayList<T>();
	}

	@XmlAnyElement(lax = true)
	public List<T> getList() {
		return list;
	}
}
