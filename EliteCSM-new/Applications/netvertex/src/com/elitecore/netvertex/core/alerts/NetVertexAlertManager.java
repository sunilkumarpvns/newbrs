package com.elitecore.netvertex.core.alerts;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.alert.AlertData;
import com.elitecore.core.serverx.alert.IAlertData;
import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.alert.SystemAlertManager;
import com.elitecore.core.serverx.alert.listeners.AggregatingAlertProcessor;
import com.elitecore.core.serverx.alert.listeners.SystemAlertProcessor;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.alerts.conf.AlertsConfiguration;
import com.elitecore.netvertex.core.alerts.conf.FileAlertListenerConfiguration;
import com.elitecore.netvertex.core.alerts.conf.TrapAlertListenerConfiguration;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/*
 * Here Listener and processor are same thing. The reason why the name of the listener is not changed to processor at some place
 * here is that in GUI we do not have to change its name to processor as the name "listener" exist for long time.
 */
public class NetVertexAlertManager extends SystemAlertManager {

    private static final String MODULE = "NTVERTX-ALRT-MGR";
    private final AlertProcessorFactory alertProcessorFactory;

    private Map<String, IAlertData> alertDetailsMap;

    public NetVertexAlertManager(NetVertexServerContext context, AlertProcessorFactory alertProcessorFactory) {
        super(context);
        this.alertProcessorFactory = alertProcessorFactory;
        alertDetailsMap = new HashMap<>();
    }

    public void init() throws InitializationFailedException {
        LogManager.getLogger().info(MODULE, "Initializing Alert Manager");

        AlertListenerConfiguration alertListenerConfiguration = getContext().getServerConfiguration().getAlertListenersConfiguration();

        /***** Initializing Default Alert Listeners *****/

        Map<String, SystemAlertProcessor> alertProcessorsMap = new HashMap<>();

        Map<String, AlertData> idToAlerts = Arrays.stream(Alerts.values())
                .collect(toMap(Alerts::id, alert -> {
                    AlertData alertData = new AlertData();
                    alertData.setAlertId(alert.id());
                    return alertData;
                }));

        addDefaultAlertProcessorToAllAlerts(idToAlerts);

        Map<SystemAlertProcessor, Set<String>> processorToAlerts = new IdentityHashMap<>();

        /***** Initializing Alert Listeners *****/
        for (TrapAlertListenerConfiguration configuration : alertListenerConfiguration.getTrapAlertListenerConfigurations()) {
            try {
                SystemAlertProcessor alertProcessor = alertProcessorFactory.create(configuration);
                alertProcessorsMap.put(configuration.getListenerId(), alertProcessor);
                addAlerts(idToAlerts, configuration.getAlertConfigurations(), alertProcessor, processorToAlerts);

            } catch (InitializationFailedException initializationFailedEx) {
                LogManager.getLogger().trace(MODULE, initializationFailedEx);

                if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
                    LogManager.getLogger().error(MODULE, "Error while initializing alert listener. Reason: " + initializationFailedEx.getMessage());
                }
            }

        }

        for (FileAlertListenerConfiguration configuration : alertListenerConfiguration.getFileAlertListenerConfigurations()) {
            try {
                SystemAlertProcessor alertProcessor = alertProcessorFactory.create(configuration);
                alertProcessorsMap.put(configuration.getListenerId(), alertProcessor);
                addAlerts(idToAlerts, configuration.getAlertConfigurations(), alertProcessor, processorToAlerts);
            } catch (InitializationFailedException initializationFailedEx) {
                LogManager.getLogger().trace(MODULE, initializationFailedEx);

                if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
                    LogManager.getLogger().error(MODULE, "Error while initializing  File alert listener. Reason: " + initializationFailedEx.getMessage());
                }
            }
        }


        createAndAddAggregatedAlertProcessor(idToAlerts, processorToAlerts);

        /***** Alert Data Initialized *****/
        this.alertDetailsMap = idToAlerts.entrySet().stream().filter(stringAlertDataEntry -> {

            if(stringAlertDataEntry.getValue().getAlertProcessorsList().isEmpty()) {
                LogManager.getLogger().info(MODULE, "Skipping alert:" + stringAlertDataEntry.getValue() + ". Reason: not handle configured");
                return false;
            }

            return true;
        }).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        startService();
        LogManager.getLogger().info(MODULE, "Alert Manager initialization completed");
    }

    private void addDefaultAlertProcessorToAllAlerts(Map<String, AlertData> idToAlerts) {
        SystemAlertProcessor defaultAlertProcessor;
        try {
            defaultAlertProcessor = alertProcessorFactory.create(createDefaultAlertConfig());
            for (AlertData alertData : idToAlerts.values()) {
                alertData.addAlertListener(defaultAlertProcessor);
            }
        } catch (InitializationFailedException initializationFailedEx) {
            LogManager.getLogger().trace(MODULE, initializationFailedEx);
            if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
                LogManager.getLogger().error(MODULE, "Error while initializing default file alert listener. Reason: " + initializationFailedEx.getMessage());
            }
        }
    }

    private void createAndAddAggregatedAlertProcessor(Map<String, AlertData> idToAlerts, Map<SystemAlertProcessor, Set<String>> processorToAlerts) {
        processorToAlerts.forEach((processor, ids) -> {

            List<IAlertEnum> alerts = ids.stream().map(Alerts::valueOf).collect(toList());
            try {
                AggregatingAlertProcessor aggregatingAlertProcessor = alertProcessorFactory.createAggregatingAlertProcessor(processor, alerts);
                ids.stream().map(idToAlerts::get).forEach(alertData -> alertData.addAlertListener(aggregatingAlertProcessor));
            } catch (InitializationFailedException initializationFailedEx) {
                LogManager.getLogger().trace(MODULE, initializationFailedEx);

                if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
                    LogManager.getLogger().error(MODULE, "Error while initializing  Aggregated alert listener. Reason: " + initializationFailedEx.getMessage());
                }
            }

        });
    }

    private void addAlerts(Map<String, AlertData> idToAlerts,
                           List<AlertsConfiguration> alertConfigurations,
                           SystemAlertProcessor alertProcessor,
                           Map<SystemAlertProcessor, Set<String>> aggregatedAlertToIds) {

        for (AlertsConfiguration alertsConfiguration : alertConfigurations) {
            String id = alertsConfiguration.getAlert().id();
            AlertData alertData = idToAlerts.computeIfAbsent(id, alertId -> new AlertData());
            alertData.setAlertId(id);

            if (alertsConfiguration.isFloodControlEnabled()) {
                aggregatedAlertToIds.computeIfAbsent(alertProcessor, processor -> new HashSet<>()).add(id);
            } else {
                alertData.addAlertListener(alertProcessor);
            }
        }

    }

    private NetVertexServerContext getContext() {
        return (NetVertexServerContext) getServerContext();
    }


    @Override
    public IAlertData getAlertData(String alertId) {
        return alertDetailsMap.get(alertId);
    }

    @Override
    protected Map<String, IAlertData> getAlertDataMap() {
        return alertDetailsMap;
    }

    private FileAlertListenerConfiguration createDefaultAlertConfig() {
        String listenerId = UUID.randomUUID().toString();
        return new FileAlertListenerConfiguration(listenerId
                , "elite-netvertex-alert.log"
                , 10
                , EliteRollingFileLogger.TIME_BASED_ROLLING_TYPE
                , EliteRollingFileLogger.TIME_BASED_ROLLING_EVERY_DAY
                , false
                , "DefaultListener-" + listenerId
                , Collections.emptyList());
    }

}