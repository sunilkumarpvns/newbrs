package com.elitecore.aaa.core.drivers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

/**
 * Represents the stripping strategy that is performed on user identity by {@link IEliteAuthDriver}
 * after fetching {@link AccountData}. Instance of {@code strategy} can be obtained through
 * static factory method {@link StripUserIdentityStrategy#get(String)}. Three modes are supported,
 * NONE ("none"), PREFIX ("prefix"), SUFFIX ("suffix"). 
 * <br/>
 * <br/>
 * Usage:
 * <pre><code>String strippedIdentity = StripUserIdentityStrategy.get("suffix").apply(identity, separator);</code></pre>
 * 
 * @author narendra.pathai
 *
 */
public abstract class StripUserIdentityStrategy {
	public static final String MODULE = "STRIP-USER-IDENTITY";
	public static final String PREFIX_STRATEGY = "prefix";
	public static final String SUFFIX_STRATEGY = "suffix";
	public static final String NONE = "none";
	
	/**
	 * Applies the strip strategy on the {@code userIdentity} provided using the {@code separator} as the reference
	 * for stripping point
	 * 
	 * @param userIdentity string which is to be stripped 
	 * @param separator the value which marks the boundary of stripping
	 * @return stripped user identity
	 */
	public abstract String apply(@Nonnull String userIdentity, @Nonnull String separator);
	
	/**
	 * Returns instance of {@link StripUserIdentityStrategy} based on {@code stripStrategyIdentifier}.
	 * {@link NoneStrategy}, {@link PrefixStrategy} and {@link SuffixStrategy} modes are supported.
	 * @param stripStrategyIdentifier any of the modes supported, if no mode matches then {@link NoneStrategy}
	 * is returned. If {@code null} then {@link NoneStrategy} is returned. Identifier should be trimmed.
	 * @return strategy representing the identifier passed
	 */
	public static StripUserIdentityStrategy get(@Nullable String stripStrategyIdentifier) {
		if(PREFIX_STRATEGY.equalsIgnoreCase(stripStrategyIdentifier)) {
			return PrefixStrategy.INSTANCE;
		} else if(SUFFIX_STRATEGY.equalsIgnoreCase(stripStrategyIdentifier)) {
			return SuffixStrategy.INSTANCE;
		} else {
			return NoneStrategy.INSTANCE;
		}
	}
	
	/**
	 * Returns the instance of NONE strategy. Useful for default initialization.
	 * 
	 * @return {@link NoneStrategy}
	 */
	public static StripUserIdentityStrategy none() {
		return NoneStrategy.INSTANCE;
	}
	
	/**
	 * Identified by <code>"none"</code> identifier.
	 * <br/><br/>
	 * Returns the identity unchanged.
	 * 
	 * @author narendra.pathai
	 *
	 */
	@VisibleForTesting
	static class NoneStrategy extends StripUserIdentityStrategy {
		private static final NoneStrategy INSTANCE = new NoneStrategy();
		
		@Override
		public String apply(String userIdentity, String realmSeparator) {
			return userIdentity;
		}
		
	}

	/**
	 * Identified by <code>"prefix"</code> identifier
	 * <br/><br/>
	 * Strips the prefix part of identity beginning from the start to the first index of
	 * the separator found. Returns the identity unchanged if {@code separator} is not found
	 * in the identity.
	 * 
	 * Example: If <code>[domain.name]{@literal \}user_identity</code> is provided and separator is {@literal \}
	 * then <code>user_identity</code> will be returned.
	 * 
	 * @author narendra.pathai
	 *
	 */
	@VisibleForTesting
	static class PrefixStrategy extends StripUserIdentityStrategy {
		private static final PrefixStrategy INSTANCE = new PrefixStrategy();
		
		@Override
		public String apply(String userIdentity, String separator) {
			int iIndex = -1;
			iIndex = userIdentity.indexOf(separator.trim());
			if(iIndex == -1) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "User Identity: " + userIdentity 
							+  " does not contain seperator: " + separator);
				}
			} else {
				if(userIdentity.length() > iIndex) {
					userIdentity = userIdentity.substring(iIndex + 1, userIdentity.length());
				}
			}

			return userIdentity;
		}
	}

	/**
	 * Identified by <code>"suffix"</code> identifier.
	 * <br/><br/>
	 * Strips the suffix part of identity beginning from the index of separator to end of string. Returns the identity unchanged if {@code separator} is not found
	 * in the identity.
	 * 
	 * Example: If <code>user_identity@[realm]</code> is provided and separator is {@literal @}
	 * then <code>user_identity</code> will be returned.
	 * 
	 * @author narendra.pathai
	 *
	 */
	@VisibleForTesting
	static class SuffixStrategy extends StripUserIdentityStrategy {
		private static final SuffixStrategy INSTANCE = new SuffixStrategy();
		
		@Override
		public String apply(String userIdentity, String realmSeparator) {
			int iIndex = -1;
			iIndex = userIdentity.indexOf(realmSeparator.trim());
			if(iIndex == -1) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "User Identity: " + userIdentity 
							+  " does not contain seperator: " + realmSeparator);
				}
			} else {
				if(userIdentity.length() > iIndex) {
					userIdentity = userIdentity.substring(0,iIndex);
				}
			}

			return userIdentity;
		}
	}
}
