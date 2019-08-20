package com.elitecore.commons.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 *  This a generic wrapper class with a List property. 
 *  
 *  This is helpful for marshalling/unmarshalling a list 
 *  with a JAXB implementation.
 *  
 *  <p> By default, {@link XmlRootElement} for this is kept as "list". 
 *  This can be overridden as per requirement by {@link XmlElement}
 *  </p>
 *  <pre>{@code
 *<list>
 *  	<item name="A" />
 *  	<item name="B" />
 *  	<item name="C" />
 *  	<item name="D" />
 *</list>
 *  }</pre>
 *  Illustrated example shows list of Items in generic terms {@code ListWrapper<Items>}
 *  
 * @param <T> is Type of object contained within list.
 */
@XmlRootElement(name = "list")
public class ListWrapper<T> {

	private List<T> list;

	@XmlAnyElement(lax=true)
	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

}
