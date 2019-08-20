package com.elitecore.netvertex.service.pcrf.conf.impl;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.service.pcrf.conf.PCRFServiceConfiguration;

/**
 * @author Harsh Patel
 *
 */
public class PCRFServiceConfigurationImpl implements PCRFServiceConfiguration, ToStringable{



    private int queueSize = 1500;
    private int minimumThread = 1;
    private int maximumThread = 100;
    private int workerThreadPriority = 7;

    public PCRFServiceConfigurationImpl(int queueSize, int minimumThread, int maximumThread, int workerThreadPriority) {
        this.queueSize = queueSize;
        this.minimumThread = minimumThread;
        this.maximumThread = maximumThread;
        this.workerThreadPriority = workerThreadPriority;
    }

    /**
	 * To have a default configuration when RnC server instance is running
	 */
    public PCRFServiceConfigurationImpl() {
		// no-op
	}

	@Override
    public int getQueueSize() {
        return queueSize;
    }

    @Override
    public int getMaximumThread() {
        return maximumThread;
    }

    @Override
    public int getMinimumThread() {
        return minimumThread;
    }

    @Override
    public int getWorkerThreadPriority() {
        return workerThreadPriority;
    }


    @Override
    public String toString(){
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- PCRF Service Configuration -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder out) {
        out.incrementIndentation();
        out.append("Queue Size", queueSize);
        out.append("Minimum Thread",minimumThread);
        out.append("Maximum Thread",maximumThread);
        out.append("Worker Thread Priority", workerThreadPriority);
        out.decrementIndentation();
    }
}
