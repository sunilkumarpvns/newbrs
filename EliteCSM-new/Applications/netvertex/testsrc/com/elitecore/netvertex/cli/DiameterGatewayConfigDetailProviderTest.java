package com.elitecore.netvertex.cli;

import java.util.ArrayList;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class DiameterGatewayConfigDetailProviderTest {

    private ConfigDetailProvider provider;
    private final String key = "dia-gateway";
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
    public void getDiameterGatewayNamesOfServerConfigurationCalledWhenGatewayKeyPassedWithoutAnyParameters() {
        netvertexServerConfiguration.spyDiameterGatewayConfFor("gateway1");
        netvertexServerConfiguration.spyDiameterGatewayConfFor("gateway2");
        provider.execute(new String[]{key});

        verify(netvertexServerConfiguration, times(1)).getDiameterGatewayNames();
    }

    @Test
    public void toStringOfGatewayConfigurationPrintedWhenGatewayKeyPassedWithGatewayName() {
        String gatewayName1 = "gateway1";
        DiameterGatewayConfiguration g1 = netvertexServerConfiguration.spyDiameterGatewayConfFor(gatewayName1);
        String expectedOutput = "expectedOutput";
        when(g1.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, gatewayName1});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void notFoundMessageReceivedWhenGatewayConfigurationNotExist() {
        String fakeGatewayName = "gateway2";
        String actualOutput = provider.execute(new String[]{key, fakeGatewayName});
        assertEquals("Diameter gateway configuration not found for name: " + fakeGatewayName, actualOutput);
    }



    @Test
    public void helpMessageOfConfigShouldContainsDescription() {
        String expectedOutput = "View Diameter Gateway Configuration";
        String actualOutput = provider.execute(new String[]{"-help"});
        assertThat(actualOutput, containsString(expectedOutput));
    }

    @Test
    public void helpMessageOfDiameterOptionShouldContainsDescription() {
        String expectedOutput = "View Diameter Gateway Configuration";
        String actualOutput = provider.execute(new String[]{key, "-help"});
        assertThat(actualOutput, containsString(expectedOutput));
    }


    @Test
    public void helpMessageShouldContainsUsage() {
        String expectedOutput = key + " [gateway-name]";
        String actualOutput = provider.execute(new String[]{key, "-help"});
        assertThat(actualOutput, containsString(expectedOutput));
    }

    public class getHotKeyHelp {

        @Test
        public void emptyWhenNoGatewayNameFound() {
            String expectedHotKey = "'dia-gateway':{}";
            assertThat(provider.getHotkeyHelp(), containsString(expectedHotKey));
        }

        @Test
        public void ContainAllGatewayNameFoundFromConfiguration() {
            String gatewayName1 = "gateway1";
            netvertexServerConfiguration.spyDiameterGatewayConfFor(gatewayName1);

            String expectedHotKey = "'dia-gateway':{'" + gatewayName1 + "':{}}";
            assertThat(provider.getHotkeyHelp(), containsString(expectedHotKey));
        }

    }

}
