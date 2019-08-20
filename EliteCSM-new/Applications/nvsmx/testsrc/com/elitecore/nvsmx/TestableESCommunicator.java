package com.elitecore.nvsmx;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;

import javax.annotation.Nullable;

/**
 * Created by aditya on 4/14/17.
 */
public class TestableESCommunicator extends ESCommunicatorImpl {

    public TestableESCommunicator(@Nullable TaskScheduler scheduler) {
        super(scheduler);
    }

    @Override
    protected int getStatusCheckDuration() {
        return 0;
    }

    @Override
    public void scan() {

    }

    @Override
    public String getName() {
        return "dummy ESI";
    }

    @Override
    public String getTypeName() {
        return "dummy ESI";
    }
}
