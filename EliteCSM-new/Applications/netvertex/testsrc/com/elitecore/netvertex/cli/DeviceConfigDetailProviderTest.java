package com.elitecore.netvertex.cli;

import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.devicemanagement.DummyDeviceManager;
import com.elitecore.netvertex.service.pcrf.TACDetail;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class DeviceConfigDetailProviderTest {
    private ConfigDetailProvider provider;
    private DummyNetvertexServerContextImpl netvertexServerContext;
    private DummyDeviceManager deviceManager;

    @Before
    public void setUp() {
        netvertexServerContext = DummyNetvertexServerContextImpl.spy();
        deviceManager = netvertexServerContext.getDeviceManager();
        provider = spy(new ConfigDetailProvider(netvertexServerContext));
    }

    @Test
    public void informativeMessageReturnedWhenTACconfigKeyPassedWithoutParameter() {
        String key = "device-by-tac";
        String expectedOutput = "Provide TAC to view Device configuration.";
        String actualOutput = provider.execute(new String[]{key});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageReturnedWhenUnknownTACPassed() throws Exception{
        String key = "device-by-tac";
        String param1 = "123";
        String expectedOutput = "Device configuration is not found for provided TAC: " + param1;
        String actualOutput = provider.execute(new String[]{key, param1});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void toStringReturnsWhenKnowTACPassed() throws Exception{
        String key = "device-by-tac";
        String param1 = "123123";
        TACDetail detail = deviceManager.spyTACdetailWithTACid(param1);
        String expectedOutput = "expectedOutput";
        when(detail.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, param1});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void helpMessageReturnsUsageExample() {
        String key = "device-by-tac";
        String expected = "Usage = device-by-tac <tac> \n" +
                "Description = View Device configuration detail by TAC";
        String actualOutput = provider.execute(new String[]{key, "-help"});
        assertThat(actualOutput, StringContains.containsString(expected));
    }
}
