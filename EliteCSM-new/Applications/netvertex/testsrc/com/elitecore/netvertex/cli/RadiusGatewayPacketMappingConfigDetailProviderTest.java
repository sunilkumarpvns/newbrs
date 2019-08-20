package com.elitecore.netvertex.cli;

import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMapping;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class RadiusGatewayPacketMappingConfigDetailProviderTest {
    private ConfigDetailProvider provider;
    private DummyNetvertexServerConfiguration netvertexServerConfiguration;
    private String key = "rad-gateway-packet-mapping";

    @Before
    public void setUp() {
        DummyNetvertexServerContextImpl context = DummyNetvertexServerContextImpl.spy();
        netvertexServerConfiguration = context.getServerConfiguration();
        AlertListenerConfiguration alertListenerConfiguration = spy(new AlertListenerConfiguration(new ArrayList<>(), new ArrayList<>()));;
        netvertexServerConfiguration.setAlertListenerConfiguration(alertListenerConfiguration);
        provider = spy(new ConfigDetailProvider(context));
    }

    @Test
    public void informativeMessageReturnedWhenKeyPassedWithoutParameter() {
        String expectedOutput = "Usage = rad-gateway-packet-mapping -name <rad-gateway-name>  -type <packet-type>\n" +
                "Description = View RADIUS Gateway Packet Mapping Configuration based on input provided\n" +
                "\n" +
                "Provide any of below gateway name to view detail: \n" +
                "[]\n" +
                "Provide any of below Appliction type to view detail for RADIUS gateway Packet Mapping: \n" +
                "[AR, AA, COA, ACR, DCR]";
        String actualOutput = provider.execute(new  String[]{key});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageReturnedWhenLessThanRequiredNumberOfParametersArePassed() {
        String[] params = new String[] {"-name","Gateway","-type"};
        String expectedOutput = "Input parameter missing. Expected: 4, Actual: 3\n" +
                "Usage = rad-gateway-packet-mapping -name <rad-gateway-name>  -type <packet-type>\n" +
                "Description = View RADIUS Gateway Packet Mapping Configuration based on input provided\n";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageReturnedWhenNameparameterNotProvided() {
        String[] params = new String[] {"-abc","Gateway","-type","GX_CCR"};
        String expectedOutput = "Either of gateway name or packet mapping type value is not provided.\n" +
                " Skipping the display of RADIUS gateway packet mapping Information.";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void helpMessageReturnsUsageExample() {
        String expected = "Usage = rad-gateway-packet-mapping -name <rad-gateway-name>  -type <packet-type>\n" +
                "Description = View RADIUS Gateway Packet Mapping Configuration based on input provided\n";
        String actualOutput = provider.execute(new String[]{key, "-help"});
        assertThat(actualOutput, StringContains.containsString(expected));
    }

    @Test
    public void pccToRadiusPacketMappingConfigToStringShouldBeReturnedWhenKnownNameAndTypeProvided() {
        String[] params = new String[] {"-name","Gateway","-type","DCR"};
        PCCToRadiusMapping configuration = netvertexServerConfiguration.spyPCCToRadiusMappingConfFor(params[3]);
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void pccToRadiusPacketMappingConfigNotFoundForGivenType() {
        String[] params = new String[] {"-name","Gateway","-type","DCR"};
        String expectedOutput = "PCC to RADIUS Packet Mapping Information not found for gateway: Gateway for type: DCR\n";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void radiusToPCCPacketMappingConfigNotFoundForGivenType() {
        String[] params = new String[] {"-name","Gateway","-type","ACR"};
        String expectedOutput = "RADIUS to PCC Packet Mapping Information not found for gateway: Gateway for type: ACR\n";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void radiusToPCCpacketMappingConfigToStringShouldBeReturnedWhenKnownNameAndTypeProvided() {
        String[] params = new String[] {"-name","Gateway","-type","ACR"};
        RadiusToPCCMapping configuration = netvertexServerConfiguration.spyRadiusToPCCMappingConfFor(params[3]);
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void getHotKeyHelpForDiameterGatewayPacketMapping() {
        String expectedHotKey = "'config':{'-help':{},'?':{},'session-manager':{},'misc-params':{},'dia-gateway':{}," +
                "'dia-gateway-packet-mapping':{'-name':{}},'rad-gateway':{},'rad-gateway-packet-mapping':{'-name':{}},'ddf':{}," +
                "'network':{},'location':{'-rac':{},'-mnc':{},'-ci':{},'-mcc':{},'-lac':{},'-sac':{}},'location-by-areaname':{},'alert':{}," +
                "'device-by-tac':{},'routingtable-by-mccmnc':{},'routingtable-by-name':{},'service-policy':{},'server-group':{},'system-parameter':{}," +
                "'prefix':{'-anymatch':{},'-bestmatch':{}},'lrn':{}}";
        assertEquals(provider.getHotkeyHelp(), expectedHotKey);
    }
}
