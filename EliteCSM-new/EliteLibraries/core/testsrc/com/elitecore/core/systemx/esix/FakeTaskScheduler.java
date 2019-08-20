package com.elitecore.core.systemx.esix;

import com.elitecore.commons.threads.SettableFuture;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A task scheduler to be used in test cases that just holds on to the tasks in list and does not execute the tasks 
 * itself. If needed those tasks can be executed using the method {@link #tick()}.
 * 
 * @author narendra.pathai
 *
 */
public class FakeTaskScheduler implements TaskScheduler {
	private LinkedList<SingleExecutionAsyncTask> singleExecutionAsyncTasks = new LinkedList<SingleExecutionAsyncTask>();
	private LinkedList<IntervalBasedTask> intervalBasedTasks = new LinkedList<IntervalBasedTask>();

	@Nullable
	@Override
	public Future<?> scheduleSingleExecutionTask(@Nullable final SingleExecutionAsyncTask task) {
		final SettableFuture<?> future = SettableFuture.create();
		SingleExecutionAsyncTask futureDecoratorTask = new SingleExecutionAsyncTask() {
			@Override
			public long getInitialDelay() {
				return task.getInitialDelay();
			}

			@Override
			public TimeUnit getTimeUnit() {
				return task.getTimeUnit();
			}

			@Override
			public void execute(AsyncTaskContext context) {
				task.execute(context);
				future.set(null);
			}
		};
		singleExecutionAsyncTasks.add(futureDecoratorTask);
		return future;
	}

	@Nullable
	@Override
	public Future<?> scheduleIntervalBasedTask(@Nullable final IntervalBasedTask task) {
		final SettableFuture<?> future = SettableFuture.create();
		IntervalBasedTask futureDecoratorTask = new IntervalBasedTask() {
			@Override
			public long getInitialDelay() {
				return task.getInitialDelay();
			}

			@Override
			public long getInterval() {
				return task.getInterval();
			}

			@Override
			public boolean isFixedDelay() {
				return task.isFixedDelay();
			}

			@Override
			public TimeUnit getTimeUnit() {
				return task.getTimeUnit();
			}

			@Override
			public void preExecute(AsyncTaskContext context) {
				task.preExecute(context);
			}

			@Override
			public void execute(AsyncTaskContext context) {
				task.execute(context);
				future.set(null);
			}

			@Override
			public void postExecute(AsyncTaskContext context) {
				task.postExecute(context);
			}
		};
		intervalBasedTasks.add(futureDecoratorTask);
		return future;
	}

	@Override
	public void execute(Runnable command) {
		command.run();
	}

	/**
	 * Runs all the single execution and interval based tasks that it holds on to.
	 */
	public void tick() {
		for (SingleExecutionAsyncTask task : singleExecutionAsyncTasks) {
			task.execute(new AsyncTaskContextImpl());
		}
		singleExecutionAsyncTasks.clear();
		
		for (IntervalBasedTask task : intervalBasedTasks) {
			AsyncTaskContextImpl context = new AsyncTaskContextImpl();
			task.preExecute(context);
			task.execute(context);
			task.postExecute(context);
		}
	}

	private class AsyncTaskContextImpl implements AsyncTaskContext {

		private Map<String, Object> attributes;

		public AsyncTaskContextImpl() {
		}

		@Override
		public synchronized void setAttribute(String key, Object attribute) {
			if (attributes == null) {
				attributes = new HashMap<String, Object>();
			}

			attributes.put(key, attribute);
		}

		@Override
		public Object getAttribute(String key) {
			if (attributes != null) {
				return attributes.get(key);
			}
			return null;
		}

	}

	public LinkedList<SingleExecutionAsyncTask> getSingleExecutionAsyncTasks() {
		return singleExecutionAsyncTasks;
	}

	public LinkedList<IntervalBasedTask> getIntervalBasedTasks() {
		return intervalBasedTasks;
	}
}
