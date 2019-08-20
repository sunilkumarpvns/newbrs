package com.elitecore.commons.threads;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.LogManager.LogKeyResolver;
import com.elitecore.commons.threads.EliteThreadFactory.EliteThread;

/**
 * 
 * @author narendra.pathai
 *
 */
public class EliteThreadLogKeyResolver implements LogKeyResolver {

	@VisibleForTesting ThreadResolver threadResolver = new ThreadResolver();
	
	@Override
	public String resloveKey() {
		Thread t = threadResolver.resolveThread();
		if (t instanceof EliteThread) {
			return ((EliteThread)t).getKey();
		}
		return LogManager.DEFAULT_LOGGER_KEY;
	}
	
	@VisibleForTesting static class ThreadResolver {
		public Thread resolveThread() {
			return Thread.currentThread();
		}
	}
}
