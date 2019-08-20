package com.elitecore.commons.base;

import java.text.SimpleDateFormat;

import javax.annotation.Nonnull;

/**
 * 
 * Thread safe way to use {@link SimpleDateFormat}.
 * 
 * <pre>
 * <code>
 * public class SomeClass {
 * 	private SimpleDateFormatThreadLocal simpleDateFormatLocal = SimpleDateFormatLocal.create("dd/MM/yyyy");
 * 
 * 	public void usingThreadLocal() {
 * 		Date date = simpleDateFormatLocal.get().parse("01/01/2018");
 * 	}
 * } 
 * </code>
 * </pre>
 * 
 * @author narendra.pathai
 *
 */
public class SimpleDateFormatThreadLocal extends ThreadLocal<SimpleDateFormat> {

	private String format;

	public SimpleDateFormatThreadLocal(@Nonnull String dateFormat) {
		this.format = dateFormat;
	}
	
	public static SimpleDateFormatThreadLocal create(String format) {
		validatePattern(format);
		return new SimpleDateFormatThreadLocal(format);
	}

	private static void validatePattern(String format) {
		new SimpleDateFormat(format);
	}
	
	@Override
	protected SimpleDateFormat initialValue() {
		return new SimpleDateFormat(format);
	}
}
