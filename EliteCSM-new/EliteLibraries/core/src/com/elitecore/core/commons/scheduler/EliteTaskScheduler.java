/*
 *  Elite Server Framework
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 7th August 2007
 *  Created By E D Baiju
 */

package com.elitecore.core.commons.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;

/**
 * Can be used to schedule task for future execution.
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public class EliteTaskScheduler {
	
	private Timer timer;
	private int taskCounter;
	private Map<String, EliteInternalTimerTask> scheduledTaskMap;
    private static final  String MODULE="Internal Task";
    private static ThreadPoolExecutor synchronousTaskExecutor;
    private static SynchronousQueue<Runnable> synchronousQueue;
    /** The default thread keep alive time for any thread used by the service */
    public static final long DEFAULT_THREAD_KEEP_ALIVE_TIME = 1000 * 60 * 60;
	private static final int MIN_THREAD = 5;
	private static final int MAX_THREAD = 25;
	private static final String SEPARATOR = " : ";
    
    static{
    	synchronousQueue = new SynchronousQueue<Runnable>();
    	synchronousTaskExecutor = new ThreadPoolExecutor(MIN_THREAD, MAX_THREAD, DEFAULT_THREAD_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, synchronousQueue);
    	synchronousTaskExecutor.setThreadFactory(new EliteThreadFactory("SCH-THR", "SCH-THR",Thread.NORM_PRIORITY));
        synchronousTaskExecutor.prestartAllCoreThreads();
    }

	public EliteTaskScheduler(String name) {
		timer = new Timer(name,true);
		scheduledTaskMap = new HashMap<String, EliteInternalTimerTask>();
	}
	
	public void cancel() {
		timer.cancel();
		synchronousTaskExecutor.shutdown();
		while(!synchronousTaskExecutor.isTerminated()){
			LogManager.getLogger().info(MODULE, "Waiting for Elite Task Scheduler to complete execution");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		LogManager.getLogger().info(MODULE, "Elite Task Scheduler terminated");
	}
	
	public int purge()  {
		return timer.purge();
	}

	public void schedule(IEliteInternalTask task){
		if (task != null ){
			EliteInternalTimerTask timerTask = new EliteInternalTimerTask(task);
			//This task needs to execute as on submit so we directly calling run method of task so that task can execute using our thread pool
			timerTask.run();
		}
	}
	
	public void schedule(IEliteInternalTask task, Date time){
		if (task != null ){
			EliteInternalTimerTask timerTask = new EliteInternalTimerTask(task);
			scheduledTaskMap.put(getInternalTaskId() + SEPARATOR + task.getTaskName(), timerTask);
			timer.schedule( timerTask, time);	
		}
	}

	public void schedule(IEliteInternalTask task, Date firstTime, long period){
		if (task != null) {
			EliteInternalTimerTask timerTask = new EliteInternalTimerTask(task);
			scheduledTaskMap.put(getInternalTaskId() + SEPARATOR + task.getTaskName(), timerTask);
			timer.schedule( timerTask, firstTime, period);
		}
	} 

	public void schedule(IEliteInternalTask task, long delay){
		if (task != null) {
			EliteInternalTimerTask timerTask = new EliteInternalTimerTask(task);
			scheduledTaskMap.put(getInternalTaskId() + SEPARATOR + task.getTaskName(), timerTask);
			timer.schedule( timerTask, delay);
		}
	} 

	public void schedule(IEliteInternalTask task, long delay, long period){
		if (task != null) {
			EliteInternalTimerTask timerTask = new EliteInternalTimerTask(task);
			scheduledTaskMap.put(getInternalTaskId() + SEPARATOR + task.getTaskName(), timerTask);
			timer.schedule( timerTask, delay, period);
		}
	}

    public void scheduleAtFixedRate(IEliteInternalTask task, Date firstTime, long period){
		if (task != null) {
			EliteInternalTimerTask timerTask = new EliteInternalTimerTask(task);
			scheduledTaskMap.put(getInternalTaskId() + SEPARATOR + task.getTaskName(), timerTask);
			timer.schedule( timerTask, firstTime, period);
		}
	} 
 
	public void scheduleAtFixedRate(IEliteInternalTask task, long delay, long period){
		if (task != null) {
			EliteInternalTimerTask timerTask = new EliteInternalTimerTask(task);
			scheduledTaskMap.put(getInternalTaskId() + SEPARATOR + task.getTaskName(), timerTask);
			timer.schedule( timerTask, delay, period);
		}
	}

    /**
     * @param task : Task to be executed
     * @param time : 
     */
   	public void schedule(EliteInternalTask task, Date time){
                schedule((IEliteInternalTask)task, time);
	}

    /**
     * @param task
     * @param firstTime
     * @param period
     */
	public void schedule(EliteInternalTask task, Date firstTime, long period){
       schedule((IEliteInternalTask)task, firstTime, period);
	}

    /**
     * @param task
     * @param delay
     */
    
	public void schedule(EliteInternalTask task, long delay){
        schedule((IEliteInternalTask)task, delay);
	}

    /**
     * @param task
     * @param delay
     * @param period
     */
	public void schedule(EliteInternalTask task, long delay, long period){
            schedule((IEliteInternalTask)task, delay,period);
	}

    /**
     * @param task
     * @param firstTime
     * @param period
     */
    public void scheduleAtFixedRate(EliteInternalTask task, Date firstTime, long period){
		scheduleAtFixedRate((IEliteInternalTask)task, firstTime, period);
	}

    /**
     * @param task
     * @param delay
     * @param period
     */
	public void scheduleAtFixedRate(EliteInternalTask task, long delay, long period){
          scheduleAtFixedRate((IEliteInternalTask)task, delay, period);
	}

	private synchronized int getInternalTaskId() {
		return taskCounter++;
	}
	
	private class EliteInternalTimerTask extends TimerTask {
		
		private IEliteInternalTask internalTask;
		
		public EliteInternalTimerTask(IEliteInternalTask internalTask) {
			this.internalTask = internalTask;
			
		}

        public void run(){
        	try{
            if(!internalTask.isRunning()){
                 /*
                  * Check whether the task is an instance of singleTask or multiTask
                  * spawn a new thread for each task if the instance is multitask
                  */
                 if(internalTask instanceof IEliteInternalMultipleTask ){

                     EliteInternalMultipleTask multiInternalTask  = (EliteInternalMultipleTask) internalTask;
                     multiInternalTask.resetTaskCounter();
                     multiInternalTask.resetParameter();
                     for(int taskCounter=1; taskCounter<=multiInternalTask.getTotalTasks(); taskCounter++ ){
                    	 synchronousTaskExecutor.execute((Runnable)multiInternalTask);
                     }
                } else {
                	synchronousTaskExecutor.execute((Runnable)internalTask);
                }

            }else{
              LogManager.getLogger().warn(MODULE, "Execution of task " + internalTask.getTaskName() + " postponed");
            }
        		
        	}catch (RejectedExecutionException e) {
        		LogManager.getLogger().warn(MODULE, "Failed to submit task: "+ internalTask.getTaskName() + "Reason: No working thread available");
			}catch (Exception e) {
        		LogManager.getLogger().warn(MODULE, "Failed to submit task: "+ internalTask.getTaskName() + "Reason: "+e.getMessage());
        }
    }
    }
	
	public List<IEliteInternalTask> getInternalTasks() {
		List<IEliteInternalTask> taskList = new ArrayList<IEliteInternalTask>();
		EliteInternalTimerTask task = null;
		
		if(scheduledTaskMap != null) {
			Iterator<String> it = scheduledTaskMap.keySet().iterator();
			while(it.hasNext()) {
				String key = it.next();
				task = scheduledTaskMap.get(key);
				if(task != null && task.internalTask != null)
					taskList.add(task.internalTask);
			}
		}
		return taskList;
	}

}
