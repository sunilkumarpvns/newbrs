package com.elitecore.commons.threads;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

/**
 * A thread-counting factory that creates instances of {@code EliteThread} and appends 
 * count to the thread name.
 * 
 * <p>If {@code threadNamePrefix} is {@code PREFIX} then the first thread will be named
 * {@code PREFIX-1} and so on.
 * 
 * @author narendra.pathai
 *
 */
public class EliteThreadFactory implements ThreadFactory {
	private final String threadKey;
	private int threadPriority;
	private long threadCounter;
	private final String threadNamePrefix;
	private UncaughtExceptionHandler uncaughtExceptionHandler;

	/**
	 * 
	 * @param threadKey a non-null key of the thread to be created
	 * @param threadNamePrefix a null-null prefix that will be prepended to thread names
	 * @param priority int value falling between {@link Thread#MIN_PRIORITY} and {@link Thread#MAX_PRIORITY}.
	 * If value is outside this range then {@link Thread#NORM_PRIORITY} will be considered. 
	 */
	public EliteThreadFactory(String threadKey, String threadNamePrefix, int priority) {
		this.threadKey = checkNotNull(threadKey, "threadKey is null");
		this.threadNamePrefix = checkNotNull(threadNamePrefix, "threadNamePrefix is null");
		
		if (priority >= Thread.MIN_PRIORITY && priority <= Thread.MAX_PRIORITY) {
			this.threadPriority = priority;
		} else {
			this.threadPriority = Thread.NORM_PRIORITY;
		}
	}

	/**
	 * 
	 * @param threadKey a non-null key of the thread to be created
	 * @param threadNamePrefix a null-null prefix that will be prepended to thread names
	 * @param priority int value falling between {@link Thread#MIN_PRIORITY} and {@link Thread#MAX_PRIORITY}.
	 * If value is outside this range then {@link Thread#NORM_PRIORITY} will be considered.
	 * @param uncaughtExceptionHandler a non-null handler that will be bound to each thread this factory creates. 
	 */
	public EliteThreadFactory(String threadKey, String threadNamePrefix, int priority, 
			UncaughtExceptionHandler uncaughtExceptionHandler) {
		this(threadKey, threadNamePrefix, priority);
		this.uncaughtExceptionHandler = checkNotNull(uncaughtExceptionHandler, "uncaughtExceptionHandler is null");
	}

	/**
	 * Create new instance of elite thread using the specified priority.
	 */
	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new EliteThread(r, formName(), threadKey);
		if (threadPriority != Thread.NORM_PRIORITY) {
			thread.setPriority(threadPriority);
		}
		thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
		return thread;
	}

	private String formName() {
		return threadNamePrefix + "-" + ++threadCounter;
	}
	
	/**
	 * A keyed thread, which contains a key that can be used for various purposes.
	 * One of the purpose is Logging.
	 * @author narendra.pathai
	 *
	 */
	public static class EliteThread extends Thread {
		private final String key;

		/**
		 * Creates an instance of EliteThread
		 * @param name the name of new thread
		 * @param key the key for this thread
		 * @param r the target whose {@code run} method is called
		 */
		public EliteThread(Runnable r, String name, String key) {
			super(r, name);
			this.key = checkNotNull(key, "key is null");
		}
		
		public String getKey() {
			return key;
		}
	}
}
