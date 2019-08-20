package com.elitecore.netvertex.core.alerts;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.alert.listeners.AggregatingAlertProcessor;
import com.elitecore.core.serverx.alert.listeners.FileAlertProcessor;
import com.elitecore.core.serverx.alert.listeners.SystemAlertProcessor;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.conf.FileAlertListenerConfiguration;
import com.elitecore.netvertex.core.alerts.conf.TrapAlertListenerConfiguration;
import com.elitecore.netvertex.core.snmp.SnmpAlertProcessorImpl;

import java.util.List;

public class AlertProcessorFactory {

    private NetVertexServerContext netVertexServerContext;

    public AlertProcessorFactory(NetVertexServerContext netVertexServerContext) {

        this.netVertexServerContext = netVertexServerContext;
    }

    public SystemAlertProcessor create(TrapAlertListenerConfiguration configuration) throws InitializationFailedException {
        SystemAlertProcessor alertProcessor = new SnmpAlertProcessorImpl(netVertexServerContext, configuration);
        alertProcessor.init();
        return alertProcessor;
    }

    public SystemAlertProcessor create(FileAlertListenerConfiguration configuration) throws InitializationFailedException {
        FileAlertProcessor fileAlertProcessor = new FileAlertProcessor(netVertexServerContext
                , configuration.getListenerId()
                , configuration.getFileName()
                , configuration.getRollingType()
                , configuration.getRollingUnit()
                , configuration.getMaxRollingUnit()
                , configuration.isCompRollingUnit());
        SystemAlertProcessor alertProcessor = fileAlertProcessor;
        alertProcessor.init();
        return alertProcessor;
    }

    public AggregatingAlertProcessor createAggregatingAlertProcessor(SystemAlertProcessor alertProcessor, List<IAlertEnum> alerts) throws InitializationFailedException {
        AggregatingAlertProcessor aggregatingAlertProcessor = new AggregatingAlertProcessor(alertProcessor, netVertexServerContext.getTaskScheduler(), alerts);
        aggregatingAlertProcessor.init();
        return aggregatingAlertProcessor;
    }
}
