
package com.elitecore.core.commons.scheduler;


/**
 *
 * @author raghug
 */
public interface IEliteInternalMultipleTask extends IEliteInternalTask {

    public void incrementTaskCounter();
    public void resetTaskCounter();
    public void run(int taskId);
    public int getTaskCounter();
    public void setTotalTasks(int totalTasks);
    public int getTotalTasks();
    public void resetParameter();

}
