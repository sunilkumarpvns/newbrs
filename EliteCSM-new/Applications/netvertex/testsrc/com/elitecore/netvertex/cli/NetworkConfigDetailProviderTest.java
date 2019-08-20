package com.elitecore.netvertex.cli;

import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.locationmanagement.DummyLocationRepository;
import com.elitecore.netvertex.core.locationmanagement.data.MccConfiguration;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class NetworkConfigDetailProviderTest {
    private ConfigDetailProvider provider;
    private DummyNetvertexServerContextImpl netvertexServerContext;
    private DummyLocationRepository locationRepository;

    @Before
    public void setUp() {
        netvertexServerContext = spy(new DummyNetvertexServerContextImpl());
        locationRepository = spy(new DummyLocationRepository());
        netvertexServerContext.setLocationRepository(locationRepository);
        provider = spy(new ConfigDetailProvider(netvertexServerContext));
    }

    @Test
    public void informativeMessageReturnedWhenNetworkconfigKeyPassedWithoutParameter() {
        String key = "network";
        String expectedOutput = "Provide MCC Code to view Network Information.";
        String actualOutput = provider.execute(new String[]{key});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageReturnedWhenUnknownNetworkConfigurationNamePassed() throws Exception{
        String key = "network";
        String param1 = "123";
        String expectedOutput = "Network Information is not found for provided MCC Code: " + param1;
        String actualOutput = provider.execute(new String[]{key, param1});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void toStringReturnsWhenKnowNetworkConfigurationNamePassed() throws Exception{
        String key = "network";
        String param1 = "456456";
        MccConfiguration configuration = locationRepository.spyNetworkConfWithMCCCode(Integer.parseInt(param1));
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, param1});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void helpMessageReturnsUsageExample() {
        String key = "network";
        String expected = "network <mcc-code>";
        String actualOutput = provider.execute(new String[]{key, "-help"});
        assertThat(actualOutput, StringContains.containsString(expected));
    }
}
