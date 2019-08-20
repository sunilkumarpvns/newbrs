package com.elitecore.netvertex.cli;

import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class RadiusGatewayConfigDetailProviderTest {
    private ConfigDetailProvider provider;
    private final String key = "rad-gateway";
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
        netvertexServerConfiguration.spyRadiusGatewayConf();
        provider.execute(new String[]{key});
        verify(netvertexServerConfiguration, times(1)).getRadiusGatewayNames();
    }

    @Test
    public void toStringOfGatewayConfigurationPrintedWhenGatewayKeyPassedWithGatewayName() {
        String gatewayName1 = "gateway1";
        RadiusGatewayConfiguration g1 = netvertexServerConfiguration.spyRadiusGatewayConfFor(gatewayName1);
        String expectedOutput = "expectedOutput";
        when(g1.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, gatewayName1});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void notFoundMessageReceivedWhenGatewayConfigurationNotExist() {
        String fakeGatewayName = "gateway2";
        String actualOutput = provider.execute(new String[]{key, fakeGatewayName});
        assertEquals("Radius gateway configuration not found for name: " + fakeGatewayName, actualOutput);
    }

    @Test
    public void emptyWhenNoGatewayNameFound() {
        String expectedHotKey = "'rad-gateway':{}";
        assertThat(provider.getHotkeyHelp(), containsString(expectedHotKey));
    }

    @Test
    public void ContainAllGatewayNameFoundFromConfiguration() {
        String gatewayName1 = "gateway1";
        netvertexServerConfiguration.spyRadiusGatewayConfFor(gatewayName1);

        String expectedHotKey = "'rad-gateway':{'" + gatewayName1 + "':{}}";
        assertThat(provider.getHotkeyHelp(), containsString(expectedHotKey));
    }

    @Test
    public void helpMessageReturnsUsageExample() {
        String key = "rad-gateway";
        String expected = "Usage = rad-gateway [gateway-name]\n" +
                "Description = View Radius Gateway Configuration\n";
        String actualOutput = provider.execute(new String[]{key, "-help"});
        assertThat(actualOutput, StringContains.containsString(expected));
    }

}
