package com.elitecore.netvertex.cli;

import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.conf.NetvertexServerGroupConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.elitecore.corenetvertex.constants.CommonConstants.SINGLE_QUOTE;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class ServerInstanceGroupDetailProviderTest {

    private ConfigDetailProvider provider;
    private DummyNetvertexServerContextImpl context;
    private NetvertexServerGroupConfiguration serverGroupConfiguration;

    @Before
    public void setUp() {
        context = DummyNetvertexServerContextImpl.spy();
        provider = spy(new ConfigDetailProvider(context));
        serverGroupConfiguration = mock(NetvertexServerGroupConfiguration.class);
        context.getServerConfiguration().setNetvertexServerGroupConfiguration(serverGroupConfiguration);
        context.getServerConfiguration().setAlertListenerConfiguration(mock(AlertListenerConfiguration.class));
    }


    @Test
    public void toStringOfServerInstanceGroupConfigurationPrintedWhenAlertKeyKeyPassed() {

        String key = "server-group";
        String expectedOutput = "expectedOutput";
        when(serverGroupConfiguration.toString()).thenReturn(expectedOutput);

        String actualOutput = provider.execute(new String[]{key});

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void toStringOfServerInstanceGroupConfigurationPrintedWhenAnyAdditionalParameterIsPassed() {

        String alertKey = "server-group";
        String expectedOutput = "expectedOutput";
        when(serverGroupConfiguration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{alertKey, UUID.randomUUID().toString()});
        assertEquals(expectedOutput, actualOutput);
    }


    @Test
    public void helpMessageShouldContainsDescription() {

        String expectedOutput = "View Server Group Configuration";

        String actualOutput = provider.execute(new String[]{"-help"});

        assertThat(actualOutput, containsString(expectedOutput));
    }

    @Test
    public void helpMessageShouldContainsUsage() {

        String expectedOutput = "server-group";

        String actualOutput = provider.execute(new String[]{"server-group", "-help"});

        assertThat(actualOutput, containsString(expectedOutput));
    }


    @Test
    public void hotKeyHelpContainsOnlyKey() {

        String expectedOutput = SINGLE_QUOTE + "server-group" + SINGLE_QUOTE + ":{}";

        String actualOutput = provider.getHotkeyHelp();

        assertThat(actualOutput, containsString(expectedOutput));

    }
}
