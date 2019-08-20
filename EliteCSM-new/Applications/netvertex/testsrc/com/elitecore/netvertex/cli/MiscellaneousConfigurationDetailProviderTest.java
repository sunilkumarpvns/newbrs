package com.elitecore.netvertex.cli;

import java.util.ArrayList;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.conf.MiscellaneousConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import org.junit.Before;
import org.junit.Test;


import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class MiscellaneousConfigurationDetailProviderTest {

    private ConfigDetailProvider provider;
    private final String key = "misc-params";
    private DummyNetvertexServerConfiguration netvertexServerConfiguration;

    @Before
    public void setUp() {
        DummyNetvertexServerContextImpl context = DummyNetvertexServerContextImpl.spy();
        netvertexServerConfiguration = context.getServerConfiguration();
        AlertListenerConfiguration alertListenerConfiguration = alertListenerConfiguration = spy(new AlertListenerConfiguration(new ArrayList<>(), new ArrayList<>()));;
        netvertexServerConfiguration.setAlertListenerConfiguration(alertListenerConfiguration);
        provider = spy(new ConfigDetailProvider(context));
    }

    @Test
    public void helpMessageOfConfigShouldContainsDescription() {
        String expectedOutput = "View Miscellaneous Configuration";
        String actualOutput = provider.execute(new String[]{"-help"});
        assertThat(actualOutput, containsString(expectedOutput));
    }

    @Test
    public void helpMessageOfDiameterOptionShouldContainsDescription() {
        String expectedOutput = "View Miscellaneous Configuration";
        String actualOutput = provider.execute(new String[]{key, "-help"});
        assertThat(actualOutput, containsString(expectedOutput));
    }

    @Test
    public void toStringOfMiscConfigurationPrintedWhenMiscConfigKeyPassedWithoutParameter() {
        MiscellaneousConfiguration configuration = netvertexServerConfiguration.spyMiscConf();
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void toStringOfMiscConfigurationPrintedWhenMiscConfigKeyPassedWithParameter() {
        MiscellaneousConfiguration configuration = netvertexServerConfiguration.spyMiscConf();
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, "abc"});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void helpMessageShouldContainsUsage() {
        String expectedOutput = key;
        String actualOutput = provider.execute(new String[]{key, "-help"});
        assertThat(actualOutput, containsString(expectedOutput));
    }
}
