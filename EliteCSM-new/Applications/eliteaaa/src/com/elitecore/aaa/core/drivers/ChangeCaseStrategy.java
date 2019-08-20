package com.elitecore.aaa.core.drivers;

import javax.annotation.Nonnull;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.commons.annotations.VisibleForTesting;

/**
 * Represents the change case operation that is performed on user identity by
 * {@link IEliteAuthDriver} after fetching the {@link AccountData}. The instance
 * of strategy can be obtained through {@link #get(int)} method. Presently three
 * strategies are supported:
 * <br/><br/>
 * <table border="1">
 * <tr><td>STRATEGY</td><td>ID</td></tr>
 * <tr><td>NO CHANGE</td><td>1</td></tr>
 * <tr><td>TO LOWER</td><td>2</td></tr>
 * <tr><td>TO UPPER</td><td>3</td></tr>
 * </table>
 * <br/>
 * If none of the id matches NO CHANGE is considered as default strategy. 
 *  
 * @author narendra.pathai
 *
 */
public abstract class ChangeCaseStrategy {
	private static final int CASE_LOWER = 2;
	private static final int CASE_UPPER = 3;
	
	/**
	 * Returns instance of {@link ChangeCaseStrategy} based on the {@code id} provided.
	 * If {@code id} is unknown then by default {@link NoChange} strategy is returned.
	 *  
	 * @param id identifier for the strategy
	 * @return change case strategy representing the id, {@link NoChange} strategy if
	 * id is unknown
	 */
	public static ChangeCaseStrategy get(int id) {
		switch (id) {
		case CASE_LOWER:
			return ToLowerCase.INSTANCE;
		case CASE_UPPER:
			return ToUpperCase.INSTANCE;
		default:
			return NoChange.INSTANCE;
		}
	}

	/**
	 * Returns instance of {@link NoChange} strategy, useful for default initialization.
	 * 
	 * @return instance of {@link NoChange} strategy
	 */
	public static ChangeCaseStrategy none() {
		return NoChange.INSTANCE;
	}
	
	/**
	 * Applies the change case strategy on the {@code str} provided.
	 * 
	 * @param str a non-null string on which change case strategy will be applied
	 * @return the resultant string
	 * @throws NullPointerException if {@code str} is null
	 */
	public abstract String apply(@Nonnull String str);
	
	/**
	 * Has no effect on the string passed, returns the same string.
	 * 
	 * @author narendra.pathai
	 *
	 */
	@VisibleForTesting
	static class NoChange extends ChangeCaseStrategy {
		private static final NoChange INSTANCE = new NoChange();
		
		@Override
		public String apply(String str) {
			return str;
		}
	}
	
	/**
	 * Changes the case of the string passed to lower case
	 * 
	 * @author narendra.pathai
	 *
	 */
	@VisibleForTesting
	static class ToLowerCase extends ChangeCaseStrategy {
		private static final ToLowerCase INSTANCE = new ToLowerCase();
		
		@Override
		public String apply(String str) {
			return str.toLowerCase();
		}
	}
	
	/**
	 * Changes the case of the string passed to upper case
	 * 
	 * @author narendra.pathai
	 *
	 */
	@VisibleForTesting
	static class ToUpperCase extends ChangeCaseStrategy {
		private static final ToUpperCase INSTANCE = new ToUpperCase();
		
		@Override
		public String apply(String str) {
			return str.toUpperCase();
		}
	}
}
