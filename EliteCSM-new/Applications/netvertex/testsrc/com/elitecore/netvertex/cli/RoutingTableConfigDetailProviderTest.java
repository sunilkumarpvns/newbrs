package com.elitecore.netvertex.cli;

import java.util.Collections;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.roaming.DummyMCCMNCRoutingConfigurationImpl;
import com.elitecore.netvertex.core.roaming.MCCMNCRoutingEntry;
import com.elitecore.netvertex.core.roaming.RoutingEntry;
import org.junit.Before;
import org.junit.Test;


import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class RoutingTableConfigDetailProviderTest {
    private ConfigDetailProvider provider;
    private DummyNetvertexServerContextImpl context;
    private DummyNetvertexServerConfiguration netvertexServerConfiguration;
    private DummyMCCMNCRoutingConfigurationImpl mccmncRoutingConfiguration;

    @Before
    public void setUp() {
        context = DummyNetvertexServerContextImpl.spy();
        netvertexServerConfiguration = context.getServerConfiguration();
        provider = spy(new ConfigDetailProvider(context));
        mccmncRoutingConfiguration = netvertexServerConfiguration.spyMCCMNCRoutingConfigurationImpl();
        context.getServerConfiguration().setAlertListenerConfiguration(new AlertListenerConfiguration(Collections.emptyList(), Collections.emptyList()));
    }

    @Test
    public void informativeMessageReturnedWhenRoutingTableByMCCMNCKeyPassedWithoutParameter() {
        String key = "routingtable-by-mccmnc";
        String expectedOutput = "Provide MCCMNC to view Routing Table configuration.";
        String actualOutput = provider.execute(new String[]{key});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void notFoundMessageReturnedWhenUnknownMCCMNCPassed() throws Exception{
        String key = "routingtable-by-mccmnc";
        String param1 = "123";
        String expectedOutput = "Routing Table configuration is not found for provided MCCMNC: " + param1;
        String actualOutput = provider.execute(new String[]{key, param1});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void toStringReturnsWhenKnownMCCMNCPassed() throws Exception{
        String key = "routingtable-by-mccmnc";
        String param1 = "456456";
        MCCMNCRoutingEntry configuration = mccmncRoutingConfiguration.spyRoutingTableByMCCMNCconf(param1);
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, param1});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void helpMessageForRoutingTableByMCCMNCContainsUsageExample() {
        String key = "routingtable-by-mccmnc";
        String expected = "Usage = routingtable-by-mccmnc <mccmnc> \n" +
                "Description = View Routing Table configuration by MCCMNC";
        String actualOutput = provider.execute(new String[]{key, "-help"});
        assertThat(actualOutput, containsString(expected));
    }


    @Test
    public void informativeMessageReturnedWhenRoutingTbleByNameKeyPassedWithoutParameter() {
        String key = "routingtable-by-name";
        String expectedOutput = "Provide name to view Routing tabel configuration.";
        String actualOutput = provider.execute(new String[]{key});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void notFoundMessageReturnedWhenUnknownRoutingTableByNamePassed() throws Exception{
        String key = "routingtable-by-name";
        String param1 = "123";
        String expectedOutput = "Routing Table configuration is not found for provided name: " + param1;
        String actualOutput = provider.execute(new String[]{key, param1});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void toStringReturnsWhenKnownRoutingTableByNamePassed() throws Exception{
        String key = "routingtable-by-name";
        String param1 = "456456";
        RoutingEntry configuration = mccmncRoutingConfiguration.spyRoutingTableByNameconf(param1);
        String expectedOutput = "expected output";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, param1});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void helpMessageForRoutingTableByNameReturnsUsageExample() {
        String key = "routingtable-by-name";
        String expected = "Usage = routingtable-by-name <name> \n" +
                "Description = View Routing Table configuration by name\n";
        String actualOutput = provider.execute(new String[]{key, "-help"});
        assertThat(actualOutput, containsString(expected));
    }

    @Test
    public void ContainAllMccMncFoundFromConfiguration() {
        String mccMnc1 = "405756";
        mccmncRoutingConfiguration.spyRoutingTableByMCCMNCconf(mccMnc1);

        String expectedHotKey = "'config':{'-help':{},'?':{},'session-manager':{},'misc-params':{}," +
                "'dia-gateway':{},'dia-gateway-packet-mapping':{'-name':{}},'rad-gateway':{},'rad-gateway-packet-mapping':" +
                "{'-name':{}},'ddf':{},'network':{},'location':{'-rac':{},'-mnc':{},'-ci':{},'-mcc':{},'-lac':{},'-sac':{}}," +
                "'location-by-areaname':{},'alert':{},'device-by-tac':{},'routingtable-by-mccmnc':{'405756':{}},'routingtable-by-name':{}," +
                "'service-policy':{},'server-group':{},'system-parameter':{},'prefix':{'-anymatch':{},'-bestmatch':{}},'lrn':{}}";
        assertEquals(provider.getHotkeyHelp(), expectedHotKey);
    }

    @Test
    public void ContainAllroutingentryNameFoundFromConfiguration() {
        String name = "routing";
        mccmncRoutingConfiguration.spyRoutingTableByNameconf(name);

        String expectedHotKey = "'routingtable-by-name':{'" + name + "':{}}";
        assertThat(provider.getHotkeyHelp(), containsString(expectedHotKey));
    }
}
