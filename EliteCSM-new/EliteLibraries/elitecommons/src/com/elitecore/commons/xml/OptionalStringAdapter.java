package com.elitecore.commons.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Strings;

/**
 * JAXB adapter that converts Optional&lt;String&gt; to String.
 * <p>For usage refer {@link OptionalAdpater} 
 * 
 * <p>
 * <b>NOTE:</b> If an optional String value is absent then default value of 
 * null is used.
 * 
 * Usage:<br/>
 * Suppose you need to keep an {@link String} property Optional and also convert
 * it to XML using JAXB then you need to create a concrete OptionalAdapter like 
 * {@link OptionalStringAdapter} and use it.
 *  
 * <pre> <code>
 * {@literal @}XMLRootElement(name = "test")
 * class Test {
 *   private Optional&lt;String&gt; optionalValue = Optional.absent();
 *   
 *   {@literal @}XMLElement(name = "optional-value", type = String.class)
 *   {@literal @}XMLJavaAdapter(value = OptionalStringAdapter.class)
 *   public Optional&lt;String&gt; getOptionalValue() {
 *     return optionalValue;
 *   }
 *   
 *   public void setOptionalValue(Optional&lt;String&gt; optionalValue) {
 *     this.optionalValue = optionalValue;
 *   }
 * }
 * </code></pre> 
 * @author chirag.i.prajapati
 *
 * @param <T> type of bound element
 */
public class OptionalStringAdapter extends XmlAdapter<String, Optional<String>> {

	@Override
	public Optional<String> unmarshal(String v) throws Exception {
		return (Strings.isNullOrEmpty(v))? Optional.<String>absent() : Optional.of(v);
	}

	@Override
	public String marshal(Optional<String> v) throws Exception {
		if (v == null) { 
			return null;
		}
		
		return v.isPresent() ? v.get() : null;
	}
}
