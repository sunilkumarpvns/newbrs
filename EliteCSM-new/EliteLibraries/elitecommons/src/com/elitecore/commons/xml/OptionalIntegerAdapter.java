package com.elitecore.commons.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Optional;

/**
 * JAXB adapter that converts Optional&lt;Integer&gt; to Integer.
 * <p>For usage refer {@link OptionalAdpater} 
 * 
 * <p>
 * <b>NOTE:</b> If an optional integer value is absent then default value of 
 * -1 is used.
 * 
 * Usage:<br/>
 * Suppose you need to keep an {@link Integer} property Optional and also convert
 * it to XML using JAXB then you need to create a concrete OptionalAdapter like 
 * {@link OptionalIntegerAdapter} and use it.
 *  
 * <pre> <code>
 * {@literal @}XMLRootElement(name = "test")
 * class Test {
 *   private Optional&lt;Integer&gt; optionalValue = Optional.absent();
 *   
 *   {@literal @}XMLElement(name = "optional-value", type = Integer.class)
 *   {@literal @}XMLJavaAdapter(value = OptionalIntegerAdapter.class)
 *   public Optional&lt;Integer&gt; getOptionalValue() {
 *     return optionalValue;
 *   }
 *   
 *   public void setOptionalValue(Optional&lt;Integer&gt; optionalValue) {
 *     this.optionalValue = optionalValue;
 *   }
 * }
 * </code></pre> 
 * @author narendra.pathai
 *
 * @param <T> type of bound element
 */
public class OptionalIntegerAdapter extends XmlAdapter<Integer, Optional<Integer>> {
	private static final int ABSENT_INTEGER = -1;

	@Override
	public Optional<Integer> unmarshal(Integer v) throws Exception {
		return v == ABSENT_INTEGER ? Optional.<Integer>absent() : Optional.of(v);
	}

	@Override
	public Integer marshal(Optional<Integer> v) throws Exception {
		if (v == null) { 
			return ABSENT_INTEGER;
		}
		
		return v.isPresent() ? v.get() : ABSENT_INTEGER;
	}
}
