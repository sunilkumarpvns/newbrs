package com.elitecore.netvertex.cli;

import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class ServicePolicyConfigDetailProviderTest {

    private ConfigDetailProvider provider;
    private final String key = "service-policy";
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
    public void getPcrfServicePolicyNamesOfServerConfigurationCalledWhenPcrfServicePolicyKeyPassedWithoutAnyParameters() {
        netvertexServerConfiguration.spyPcrfServicePolicyConfFor("pcrf_policy");
        provider.execute(new String[]{key});

        verify(netvertexServerConfiguration, times(1)).getPCRFServicePolicyNames();
    }

    @Test
    public void toStringOfPcrfServicePolicyConfigurationPrintedWhenPcrfServicePolicyKeyPassedWithPcrfServicePolicyName() {
        String pcrfServicePolicyName = "pcrf_policy";
        PccServicePolicyConfiguration pcrfServicePolicyConfiguration = netvertexServerConfiguration.spyPcrfServicePolicyConfFor(pcrfServicePolicyName);
        String expectedOutput = "expectedOutput";
        when(pcrfServicePolicyConfiguration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, pcrfServicePolicyName});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void notFoundMessageReceivedWhenPcrfServicePolicyConfigurationNotExist() {
        String fakePcrfServicePolicyName = "pcrf";
        String actualOutput = provider.execute(new String[]{key, fakePcrfServicePolicyName});
        assertEquals("PCC service policy configuration not found for name: " + fakePcrfServicePolicyName, actualOutput);
    }

    @Test
    public void helpMessageOfConfigShouldContainsDescription() {
        String expectedOutput = "View PCC Service Policy Configuration";
        String actualOutput = provider.execute(new String[]{"-help"});
        assertThat(actualOutput, containsString(expectedOutput));
    }

    @Test
    public void helpMessageShouldContainsUsage() {
        String expectedOutput = key + " [service-policy-name]";
        String actualOutput = provider.execute(new String[]{key, "-help"});
        assertThat(actualOutput, containsString(expectedOutput));
    }

    public class getHotKeyHelp {

        @Test
        public void emptyWhenNoPcrfServicePolicyNameFound() {
            String expectedHotKey = "'service-policy':{}";
            assertThat(provider.getHotkeyHelp(), containsString(expectedHotKey));
        }

        @Test
        public void containAllPcrfServicePolicyNameFoundFromConfiguration() {
            String pcrfServicePolicyName = "pcc_policy";
            netvertexServerConfiguration.spyPcrfServicePolicyConfFor(pcrfServicePolicyName);

            String expectedHotKey = "'service-policy':{'" + pcrfServicePolicyName + "':{}}";
            assertThat(provider.getHotkeyHelp(), containsString(expectedHotKey));
        }

    }
}
