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

import java.util.Calendar;
import java.util.Date;

/**
 * To implement multiple tasks per schedule that can be executed by @see EliteTaskScheduler
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public abstract class EliteInternalMultipleTask implements IEliteInternalMultipleTask {
	
	private String taskName;
	private Date taskStartupTime;
	private Date lastExecutionTime;
	private Date nextExecutionTime;
	private String remark;
    private boolean running;
    private int progressValue;
    private int totalTasks;
    private static int taskCounter=0;

	
	public EliteInternalMultipleTask(int taskCount, String taskName) {
        this.totalTasks = taskCount;
		this.taskName = taskName;
		this.taskStartupTime = new Date();
	}

	public String getTaskName() {
		return taskName;
	}

	public Date getTaskStartupTime() {
		return taskStartupTime;
	}

	public Date getNextExecutionTime() {
		return nextExecutionTime;
	}

	public void setNextExecutionTime(Date nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}

	public Date getLastExecutionTime() {
		return lastExecutionTime;
	}

	public void setLastExecutionTime(Date lastExecutionTime) {
		this.lastExecutionTime = lastExecutionTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setNextExecutionTime(long milliseconds) {
		Date next = getNextExecutionTime();
		setLastExecutionTime(next);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(next);
		calendar.setTimeInMillis(next.getTime() + milliseconds);
		setNextExecutionTime(calendar.getTime());
	}

    public boolean isRunning(){
        return running;
    }

    public void setRunning(boolean running){
        synchronized(this){
            this.running = running;
        }
    }

    public int getProgressValue(){
        return progressValue;
    }

    public void setProgressValue(int progressValue){
        this.progressValue = progressValue;
    }

    public void run(){
        incrementTaskCounter();
        run(taskCounter);
    }
    
    public int getTaskCounter(){
        return taskCounter;
    }
    
    public final void incrementTaskCounter(){
        taskCounter++;
    }

    public final void resetTaskCounter(){
        taskCounter = 0;
    }

    public abstract void run(int taskId);

    public int getTotalTasks(){
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks){
        this.totalTasks = totalTasks;
    }
}
