package com.elitecore.netvertex.core.alerts;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.alert.AlertData;
import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.alert.listeners.AggregatingAlertProcessor;
import com.elitecore.core.serverx.alert.listeners.SystemAlertProcessor;
import com.elitecore.core.serverx.snmp.mib.mib2.autogen.System;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.alerts.conf.AlertsConfiguration;
import com.elitecore.netvertex.core.alerts.conf.FileAlertListenerConfiguration;
import com.elitecore.netvertex.core.alerts.conf.TrapAlertListenerConfiguration;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toMap;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class NetVertexAlertManagerTest {

    private NetVertexAlertManager alertManager;
    private AlertListenerConfiguration listenersConfiguration;
    private DummyNetvertexServerContextImpl context;
    @Mock
    private AlertProcessorFactory alertProcessorFactory;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        context = new DummyNetvertexServerContextImpl();
        alertManager = new NetVertexAlertManager(context, alertProcessorFactory);
    }

    @Test
    public void addDefaultFileAlertProcessor_For_AlertsNotEnabledInAnyConfiguredProcessor() throws InitializationFailedException {

        AlertsConfiguration alertsConfiguration = new AlertsConfiguration(Alerts.DB_NO_CONNECTION, false);

        AlertsConfiguration aggregatedAlerts = new AlertsConfiguration(Alerts.UNKNOWN_USER, true);

        FileAlertListenerConfiguration fileAlertListenerConf = createFileAlertListener(alertsConfiguration, aggregatedAlerts);
        TrapAlertListenerConfiguration snmpAlertListenerConf = createTrapAlertListener(alertsConfiguration, aggregatedAlerts);


        SystemAlertProcessor defaultAlertProcessor = alertProcessorForDefaultAlerts();

        SystemAlertProcessor fileAlertProcessor = alertProcessorFor(fileAlertListenerConf);
        SystemAlertProcessor snmpAlertProcessor = alertProcessorFor(snmpAlertListenerConf);

        AggregatingAlertProcessor fileAggregatedAlertProcessor = aggreagatedAlertProcessorFor(fileAlertProcessor);
        AggregatingAlertProcessor snmpAggregatedAlertProcessor = aggreagatedAlertProcessorFor(snmpAlertProcessor);

        addListener(fileAlertListenerConf, snmpAlertListenerConf);

        alertManager.init();

        Map<String, AlertData> collect = createAlertDataAndAddDefaultProcessor(defaultAlertProcessor);
        collect.get(Alerts.DB_NO_CONNECTION.id()).addAlertListener(fileAlertProcessor);
        collect.get(Alerts.DB_NO_CONNECTION.id()).addAlertListener(snmpAlertProcessor);
        collect.get(Alerts.UNKNOWN_USER.id()).addAlertListener(fileAggregatedAlertProcessor);
        collect.get(Alerts.UNKNOWN_USER.id()).addAlertListener(snmpAggregatedAlertProcessor);
        ;

        ReflectionAssert.assertLenientEquals(collect, alertManager.getAlertDataMap());
    }


    @Test
    public void addDefaultFileAlertProcessor_To_AllAlertsWhenNoProcessorConfigured() throws InitializationFailedException {

        SystemAlertProcessor systemAlertProcessor = alertProcessorForDefaultAlerts();

        listenersConfiguration = new AlertListenerConfiguration(Collections.emptyList(), Collections.emptyList());
        this.context.getServerConfiguration().setAlertListenerConfiguration(listenersConfiguration);

        alertManager.init();

        Map<String, AlertData> idToAlerts = createAlertDataAndAddDefaultProcessor(systemAlertProcessor);

        ReflectionAssert.assertLenientEquals(idToAlerts, alertManager.getAlertDataMap());
    }


    @Test
    public void createAggregatedSystemAlertProcessor_When_FloodControlIsEnabled() throws InitializationFailedException {
        AlertsConfiguration aggregatedAlerts = new AlertsConfiguration(Alerts.DB_HIGH_QUERY_RESPONSE_TIME, true);
        FileAlertListenerConfiguration fileAlertListenerConf = createFileAlertListener(aggregatedAlerts);

        SystemAlertProcessor defaultAlertProcessor = alertProcessorForDefaultAlerts();
        AggregatingAlertProcessor configuredAggregatedAlertProcessor = aggreagatedAlertProcessorFor(alertProcessorFor(fileAlertListenerConf));

        addListener(fileAlertListenerConf);

        alertManager.init();

        String id = Alerts.DB_HIGH_QUERY_RESPONSE_TIME.id();
        Assert.assertNotNull(alertManager.getAlertDataMap().get(id));
        ReflectionAssert.assertLenientEquals(alertManager.getAlertDataMap().get(id).getAlertProcessorsList(), Arrays.asList(configuredAggregatedAlertProcessor, defaultAlertProcessor));

    }


    @Test
    public void createSystemAlertProcessor_When_FloodControlIsDisabled() throws InitializationFailedException {


        AlertsConfiguration aggregatedAlerts = new AlertsConfiguration(Alerts.DIAMETER_PEER_HIGH_RESPONSE_TIME, false);
        FileAlertListenerConfiguration fileAlertListenerConf = createFileAlertListener(aggregatedAlerts);
        TrapAlertListenerConfiguration snmpAlertListenerConf = createTrapAlertListener(aggregatedAlerts);

        SystemAlertProcessor defaultAlertProcessor = alertProcessorForDefaultAlerts();
        SystemAlertProcessor fileAlertProcessor = alertProcessorFor(fileAlertListenerConf);
        SystemAlertProcessor snmpAlertProcessor = alertProcessorFor(snmpAlertListenerConf);
        addListener(fileAlertListenerConf, snmpAlertListenerConf);

        alertManager.init();

        String id = Alerts.DIAMETER_PEER_HIGH_RESPONSE_TIME.id();
        ReflectionAssert.assertLenientEquals(alertManager.getAlertDataMap().get(id).getAlertProcessorsList(), Arrays.asList(fileAlertProcessor, snmpAlertProcessor, defaultAlertProcessor));
    }


    @Test
    public void createFileSystemAlertProcessor_When_ListenerTypeIsFile() throws InitializationFailedException {

        AlertsConfiguration aggregatedAlerts = new AlertsConfiguration(Alerts.PCRF_STARTUP_FAILED, false);

        FileAlertListenerConfiguration fileAlertListenerConf = createFileAlertListener(aggregatedAlerts);

        SystemAlertProcessor defaultAlertProcessor = alertProcessorForDefaultAlerts();
        SystemAlertProcessor systemAlertProcessor = alertProcessorFor(fileAlertListenerConf);

        addListener(fileAlertListenerConf);

        alertManager.init();

        String id = Alerts.PCRF_STARTUP_FAILED.id();
        ReflectionAssert.assertLenientEquals(alertManager.getAlertDataMap().get(id).getAlertProcessorsList(), Arrays.asList(systemAlertProcessor, defaultAlertProcessor));
    }


    @Test
    public void createFileSystemAlertProcessor_When_ListenerTypeIsSNMPTrap() throws InitializationFailedException {

        AlertsConfiguration aggregatedAlerts = new AlertsConfiguration(Alerts.PCRF_STARTUP_FAILED, false);

        TrapAlertListenerConfiguration trapAlertListenerConfiguration = createTrapAlertListener(aggregatedAlerts);

        SystemAlertProcessor defaultAlertProcessor = alertProcessorForDefaultAlerts();
        SystemAlertProcessor systemAlertProcessor = alertProcessorFor(trapAlertListenerConfiguration);

        addListener(trapAlertListenerConfiguration);

        alertManager.init();

        String id = Alerts.PCRF_STARTUP_FAILED.id();
        ReflectionAssert.assertLenientEquals(alertManager.getAlertDataMap().get(id).getAlertProcessorsList(), Arrays.asList(systemAlertProcessor, defaultAlertProcessor));
    }

    @Test
    public void skipDefaultAlertProcessor_When_DefaultProcessorInitializationFail() throws InitializationFailedException {

        AlertsConfiguration aggregatedAlerts = new AlertsConfiguration(Alerts.PCRF_STARTUP_FAILED, false);

        TrapAlertListenerConfiguration trapAlertListenerConfiguration = createTrapAlertListener(aggregatedAlerts);

        doThrow(new InitializationFailedException("Fail from test")).when(alertProcessorFactory).create(any(FileAlertListenerConfiguration.class));
        SystemAlertProcessor systemAlertProcessor = alertProcessorFor(trapAlertListenerConfiguration);

        addListener(trapAlertListenerConfiguration);

        alertManager.init();

        String id = Alerts.PCRF_STARTUP_FAILED.id();
        ReflectionAssert.assertLenientEquals(alertManager.getAlertDataMap().get(id).getAlertProcessorsList(), Arrays.asList(systemAlertProcessor));
    }

    @Test
    public void skipAlertsData_Which_doesNotHaveAnyProcessor() throws InitializationFailedException {

        AlertsConfiguration aggregatedAlerts = new AlertsConfiguration(Alerts.PCRF_STARTUP_FAILED, false);

        TrapAlertListenerConfiguration trapAlertListenerConfiguration = createTrapAlertListener(aggregatedAlerts);

        doThrow(new InitializationFailedException("Fail from test")).when(alertProcessorFactory).create(any(FileAlertListenerConfiguration.class));
        SystemAlertProcessor systemAlertProcessor = alertProcessorFor(trapAlertListenerConfiguration);

        addListener(trapAlertListenerConfiguration);

        alertManager.init();

        String id = Alerts.PCRF_STARTUP_FAILED.id();
        Assert.assertEquals(1, alertManager.getAlertDataMap().size());
        ReflectionAssert.assertLenientEquals(alertManager.getAlertDataMap().get(id).getAlertProcessorsList(), Arrays.asList(systemAlertProcessor));
    }

    @Test
    public void handleAndSkipInitializationErrorThrownByFactory() throws InitializationFailedException {


        AlertsConfiguration alertsConfiguration = new AlertsConfiguration(Alerts.NOTIFICATION_STARTUP_FAILED, false);
        AlertsConfiguration aggregatedAlerts = new AlertsConfiguration(Alerts.SPR_DOWN, true);

        SystemAlertProcessor defaultAlertProcessor = alertProcessorForDefaultAlerts();


        FileAlertListenerConfiguration fileAlertListenerConf = createFileAlertListener(alertsConfiguration);
        FileAlertListenerConfiguration aggregatingFileAlertListenerConf = createFileAlertListener(aggregatedAlerts);
        FileAlertListenerConfiguration throwErrorFileAlertListenerConf = createFileAlertListener(alertsConfiguration);
        FileAlertListenerConfiguration throwExceptionOnAggregatingFileAlertListenerConf = createFileAlertListener(aggregatedAlerts);

        AggregatingAlertProcessor aggregatingFileAlertListenerProcessor = aggreagatedAlertProcessorFor(alertProcessorFor(aggregatingFileAlertListenerConf));
        when(alertProcessorFactory.createAggregatingAlertProcessor(eq(alertProcessorFor(throwExceptionOnAggregatingFileAlertListenerConf)), anyListOf(IAlertEnum.class))).thenThrow(new InitializationFailedException("Fail from test"));
        SystemAlertProcessor fileAlertListenerProcessor = alertProcessorFor(fileAlertListenerConf);
        doThrow(new InitializationFailedException("Fail from test")).when(alertProcessorFactory).create(throwErrorFileAlertListenerConf);

        TrapAlertListenerConfiguration trapAlertListenerConf = createTrapAlertListener(alertsConfiguration);
        TrapAlertListenerConfiguration aggregatingTrapAlertListenerConf = createTrapAlertListener(aggregatedAlerts);
        TrapAlertListenerConfiguration throwErrorTrapAlertListenerConf = createTrapAlertListener(alertsConfiguration);
        TrapAlertListenerConfiguration throwExceptionOnAggregationTrapAlertListenerConf = createTrapAlertListener(aggregatedAlerts);

        AggregatingAlertProcessor aggregatingTrapAlertListenerProcessor = aggreagatedAlertProcessorFor(alertProcessorFor(aggregatingTrapAlertListenerConf));
        when(alertProcessorFactory.createAggregatingAlertProcessor(eq(alertProcessorFor(throwExceptionOnAggregationTrapAlertListenerConf)), anyListOf(IAlertEnum.class))).thenThrow(new InitializationFailedException("Fail from test"));
        SystemAlertProcessor trapAlertListenerProcessor = alertProcessorFor(trapAlertListenerConf);
        doThrow(new InitializationFailedException("Fail from test")).when(alertProcessorFactory).create(throwErrorTrapAlertListenerConf);


        addListener(Arrays.asList(fileAlertListenerConf,
                throwErrorFileAlertListenerConf,
                aggregatingFileAlertListenerConf,
                throwExceptionOnAggregatingFileAlertListenerConf)
                , Arrays.asList(trapAlertListenerConf,
                        throwErrorTrapAlertListenerConf,
                        aggregatingTrapAlertListenerConf,
                        throwExceptionOnAggregationTrapAlertListenerConf));

        alertManager.init();

        Assert.assertNotNull(alertManager.getAlertDataMap().get(Alerts.NOTIFICATION_STARTUP_FAILED.id()));
        Assert.assertNotNull(alertManager.getAlertDataMap().get(Alerts.SPR_DOWN.id()));
        ReflectionAssert.assertLenientEquals(alertManager.getAlertDataMap().get(Alerts.NOTIFICATION_STARTUP_FAILED.id()).getAlertProcessorsList(),
                Arrays.asList(defaultAlertProcessor, trapAlertListenerProcessor, fileAlertListenerProcessor));
        ReflectionAssert.assertLenientEquals(alertManager.getAlertDataMap().get(Alerts.SPR_DOWN.id()).getAlertProcessorsList(),
                Arrays.asList(defaultAlertProcessor, aggregatingTrapAlertListenerProcessor, aggregatingFileAlertListenerProcessor));
    }

    private Map<String, AlertData> createAlertDataAndAddDefaultProcessor(SystemAlertProcessor defaultAlertProcessor) {
        Map<String, AlertData> collect = Arrays.stream(Alerts.values())
                .collect(toMap(Alerts::id, alert -> new AlertData()));

        collect.entrySet().forEach(stringAlertDataEntry -> {
            String id = stringAlertDataEntry.getKey();
            AlertData alertData = stringAlertDataEntry.getValue();
            alertData.setAlertId(id);
            alertData.addAlertListener(defaultAlertProcessor);
        });
        return collect;
    }

    private AggregatingAlertProcessor aggreagatedAlertProcessorFor(SystemAlertProcessor systemAlertProcessor) throws InitializationFailedException {
        AggregatingAlertProcessor configuredAggregatedAlertProcessor = mock(AggregatingAlertProcessor.class);
        when(alertProcessorFactory.createAggregatingAlertProcessor(eq(systemAlertProcessor), anyListOf(IAlertEnum.class))).thenReturn(configuredAggregatedAlertProcessor);
        return configuredAggregatedAlertProcessor;
    }

    private SystemAlertProcessor alertProcessorForDefaultAlerts() throws InitializationFailedException {
        SystemAlertProcessor defaultAlertProcessor = mock(SystemAlertProcessor.class);
        doReturn(defaultAlertProcessor).when(alertProcessorFactory).create(any(FileAlertListenerConfiguration.class));
        return defaultAlertProcessor;
    }

    private SystemAlertProcessor alertProcessorFor(FileAlertListenerConfiguration fileAlertListenerConfiguration) throws InitializationFailedException {
        SystemAlertProcessor defaultAlertProcessor = mock(SystemAlertProcessor.class);
        doReturn(defaultAlertProcessor).when(alertProcessorFactory).create(fileAlertListenerConfiguration);
        return defaultAlertProcessor;
    }

    private SystemAlertProcessor alertProcessorFor(TrapAlertListenerConfiguration trapAlertListenerConfiguration) throws InitializationFailedException {
        SystemAlertProcessor defaultAlertProcessor = mock(SystemAlertProcessor.class);
        doReturn(defaultAlertProcessor).when(alertProcessorFactory).create(trapAlertListenerConfiguration);
        return defaultAlertProcessor;
    }

    private TrapAlertListenerConfiguration createTrapAlertListener(AlertsConfiguration... alertsConfigurations) {
        TrapAlertListenerConfiguration trapAlertListenerConfiguration = mock(TrapAlertListenerConfiguration.class);
        when(trapAlertListenerConfiguration.getListenerId()).thenReturn(UUID.randomUUID().toString());
        when(trapAlertListenerConfiguration.getAlertConfigurations()).thenReturn(Arrays.stream(alertsConfigurations).collect(Collectors.toList()));
        return trapAlertListenerConfiguration;
    }

    private FileAlertListenerConfiguration createFileAlertListener(AlertsConfiguration... alertsConfigurations) {
        FileAlertListenerConfiguration fileAlertListenerConfiguration = mock(FileAlertListenerConfiguration.class);
        when(fileAlertListenerConfiguration.getListenerId()).thenReturn(UUID.randomUUID().toString());
        when(fileAlertListenerConfiguration.getAlertConfigurations()).thenReturn(Arrays.stream(alertsConfigurations).collect(Collectors.toList()));

        return fileAlertListenerConfiguration;
    }


    private void addListener(FileAlertListenerConfiguration fileAlertListenerConf, TrapAlertListenerConfiguration snmpAlertListenerConf) {
        listenersConfiguration = new AlertListenerConfiguration(Arrays.asList(snmpAlertListenerConf), Arrays.asList(fileAlertListenerConf));

        this.context.getServerConfiguration().setAlertListenerConfiguration(listenersConfiguration);
    }

    private void addListener(List<FileAlertListenerConfiguration> fileAlertListenerConf, List<TrapAlertListenerConfiguration> snmpAlertListenerConf) {
        listenersConfiguration = new AlertListenerConfiguration(snmpAlertListenerConf, fileAlertListenerConf);

        this.context.getServerConfiguration().setAlertListenerConfiguration(listenersConfiguration);
    }

    private void addListener(FileAlertListenerConfiguration... fileAlertListenerConf) {
        listenersConfiguration = new AlertListenerConfiguration(Collections.emptyList(), Arrays.stream(fileAlertListenerConf).collect(Collectors.toList()));

        this.context.getServerConfiguration().setAlertListenerConfiguration(listenersConfiguration);
    }

    private void addListener(TrapAlertListenerConfiguration... snmpAlertListenerConf) {
        listenersConfiguration = new AlertListenerConfiguration(Arrays.stream(snmpAlertListenerConf).collect(Collectors.toList()), Collections.emptyList());

        this.context.getServerConfiguration().setAlertListenerConfiguration(listenersConfiguration);
    }

}