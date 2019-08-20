/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.elitecore.core.commons.scheduler;

import java.util.Date;

/**
 *
 * @author raghug
 */
public interface IEliteInternalTask extends Runnable {

    int NO_INITIAL_DELAY = 0;

    int ONE_HOUR_INITIAL_DELAY = 3600000;

    int ONE_HOUR_INTERVAL = 3600000;

    int ONE_MINUTE_INTERVAL = 600000;

    int THIRTY_MINUTE_INTERVAL = 1800000;

    public Date getLastExecutionTime();

    public Date getNextExecutionTime();

    public String getRemark();

    public String getTaskName();

    public Date getTaskStartupTime();

    public boolean isRunning();

    public int getProgressValue();

    public void run();

    public void setLastExecutionTime(Date lastExecutionTime);

    public void setNextExecutionTime(Date nextExecutionTime);

    public void setNextExecutionTime(long milliseconds);

    public void setProgressValue(int progressValue);

    public void setRemark(String remark);

    public void setRunning(boolean running);

}
