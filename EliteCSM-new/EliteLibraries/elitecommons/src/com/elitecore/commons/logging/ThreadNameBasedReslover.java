package com.elitecore.commons.logging;

import static com.elitecore.commons.base.Preconditions.checkArgument;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.logging.LogManager.LogKeyResolver;

/**
 * 
 * @author narendra.pathai
 *
 */
public class ThreadNameBasedReslover implements LogKeyResolver{

	private final int keyLength;
	@VisibleForTesting ThreadNameResolver nameResolver = new ThreadNameResolver();
	
	public ThreadNameBasedReslover(int keyLength) {
		checkArgument(keyLength > 0, "key length cannot be <= 0");
		this.keyLength = keyLength;
	}
	
	@Override
	public String resloveKey() {
		String threadName = nameResolver.resloveName();
		int threadNameLength = threadName.length();
		return threadNameLength < keyLength ? LogManager.DEFAULT_LOGGER_KEY
				: threadName.substring(0, keyLength);
	}
	
	@VisibleForTesting static class ThreadNameResolver {
		public String resloveName() {
			return Thread.currentThread().getName();
		}
	}
}
