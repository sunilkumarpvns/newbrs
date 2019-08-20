package com.elitecore.core.commons.config.core;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.logging.LogManager;

/**
 * 
 * An XML Adapter for Enum's that can be used to read the XMLEnum values from XML in a
 * case-insensitive manner. Adapter also supports using a default value when an invalid
 * or null value is encountered.
 * 
 * <p><b>NOTE:</b> It is required that all enum constants MUST be in upper case for this
 * class to work. If any enum constant has lower case letters then adapter will use 
 * the default value passed.
 * 
 * <p>Usage:
 * <pre>
 * <code>
 * {@literal @}XmlEnum
 * enum TestEnum {
 * 	{@literal @}XmlEnumValue(value = "TEST_VALUE1")
 *	TEST_VALUE1,
 *		
 *	{@literal @}XmlEnumValue(value = "DEFAULT")
 *	DEFAULT;
 *
 *	static class TestXMLAdapter extends CaseInsensitiveEnumAdapter&lt;TestEnum&gt; {
 *		public TestXMLAdapter() {
 *			super(TestEnum.class, DEFAULT);
 *		}
 *	}
 * }
 * </code>
 * </pre>
 * @author narendra.pathai
 *
 * @param <E> type of Enum
 */
public abstract class CaseInsensitiveEnumAdapter<E extends Enum<E>> extends XmlAdapter<String, E> {
	private static final String MODULE = "ENUM-ADAPTER";
	private final Class<E> clazz;
	private final E defaultValue;

	/**
	 * Creates a new case insensitive enum adapter for the provided enum class
	 * with the default value, that will be returned if some invalid value is 
	 * configured.
	 * 
	 * @param clazz a non-null enum class 
	 * @param defaultValue value to be used when adapter encounters some
	 * invalid or absent value. defaultValue can be null.
	 */
	public CaseInsensitiveEnumAdapter(@Nonnull Class<E> clazz, @Nullable E defaultValue) {
		this.clazz = checkNotNull(clazz, "clazz is null");
		this.defaultValue = defaultValue;
	}

	@Override
	public E unmarshal(String v) throws Exception {
		if(v == null) return defaultValue;
		E result;
		try {
			result = Enum.<E>valueOf(clazz, v.toUpperCase().trim());
		} catch(IllegalArgumentException ex) {
			result = defaultValue;
			LogManager.getLogger().debug(MODULE, "Invalid value: " + v + " for enum: " + clazz
					+ ", using default value: " + defaultValue);
		}
		return result;
	}

	@Override
	public String marshal(E v) throws Exception {
		return v.name();
	}
}
