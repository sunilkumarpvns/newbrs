package com.elitecore.netvertex.cli;

import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.alerts.conf.AlertListenerConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class DiameterGatewayPacketMappingConfigDetailProviderTest {
    private ConfigDetailProvider provider;
    private DummyNetvertexServerConfiguration netvertexServerConfiguration;
    private String key = "dia-gateway-packet-mapping";

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
        String expectedOutput = "Usage = dia-gateway-packet-mapping -name <dia-gateway-name>  -type <packet-type>\n" +
                "Description = View Diameter Gateway Packet Mapping Configuration based on input provided\n" +
                "\n" +
                "Provide any of below gateway name to view detail: \n" +
                "[]\n" +
                "Provide any of below Application Packet Type to view detail for Diameter gateway Packet Mapping: \n" +
                "[RX_ASA, GX_CCR, GX_RAA, RX_AAR, SY_SNR, GY_RAR, GY_CCA, GX_STR, SY_STA, SY_SLR, RX_ASR, GX_RAR, SY_SNA, RX_AAA, SY_SLA, GX_STA, GX_CCA, GY_CCR, GY_RAA]";
        String actualOutput = provider.execute(new  String[]{key});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageReturnedWhenLessThanRequiredNumberOfParametersArePassed() {
        String[] params = new String[] {"-name","Gateway","-type"};
        String expectedOutput = "Input parameter missing. Expected: 4, Actual: 3\n" +
                "Usage = dia-gateway-packet-mapping -name <dia-gateway-name>  -type <packet-type>\n" +
                "Description = View Diameter Gateway Packet Mapping Configuration based on input provided\n";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageReturnedWhenNameparameterNotProvided() {
        String[] params = new String[] {"-abc","Gateway","-type","GX_CCR"};
        String expectedOutput = "Either of gateway name or packet mapping type value is not provided.\n" +
                " Skipping the display of diameter gateway packet mapping Information.";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void helpMessageReturnsUsageExample() {
        String expected = "Usage = dia-gateway-packet-mapping -name <dia-gateway-name>  -type <packet-type>\n" +
                "Description = View Diameter Gateway Packet Mapping Configuration based on input provided\n";
        String actualOutput = provider.execute(new String[]{key, "-help"});
        assertThat(actualOutput, StringContains.containsString(expected));
    }

    @Test
    public void pccToDiameterPacketMappingConfigToStringShouldBeReturnedWhenKnownNameAndTypeProvided() {
        String[] params = new String[] {"-name","Gateway","-type","GX_CCA"};
        PCCToDiameterMapping configuration = netvertexServerConfiguration.spyPCCToDiameterMappingConfFor(params[3]);
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void pccToDiameterPacketMappingConfigNotFoundForGivenType() {
        String[] params = new String[] {"-name","Gateway","-type","GX_CCA"};
        String expectedOutput = "PCC to Diameter Packet Mapping Information not found for gateway: Gateway for type: GX_CCA\n";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void diameterToPCCPacketMappingConfigNotFoundForGivenType() {
        String[] params = new String[] {"-name","Gateway","-type","GX_CCR"};
        String expectedOutput = "Diameter to PCC Packet Mapping Information not found for gateway: Gateway for type: GX_CCR\n";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void diameterToPCCpacketMappingConfigToStringShouldBeReturnedWhenKnownNameAndTypeProvided() {
        String[] params = new String[] {"-name","Gateway","-type","GX_CCR"};
        DiameterToPCCMapping configuration = netvertexServerConfiguration.spyDiameterToPCCMappingConfFor(params[3]);
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void getHotKeyHelpForDiameterGatewayPacketMapping() {
        String expectedHotKey = "'config':{'-help':{},'?':{},'session-manager':{},'misc-params':{},'dia-gateway':{},'dia-gateway-packet-mapping'" +
                ":{'-name':{}},'rad-gateway':{},'rad-gateway-packet-mapping':{'-name':{}},'ddf':{},'network':{},'location':{'-rac':{},'-mnc':{},'-ci'" +
                ":{},'-mcc':{},'-lac':{},'-sac':{}},'location-by-areaname':{},'alert':{},'device-by-tac':{},'routingtable-by-mccmnc':{},'routingtable-by-name':" +
                "{},'service-policy':{},'server-group':{},'system-parameter':{},'prefix':{'-anymatch':{},'-bestmatch':{}},'lrn':{}}";
        assertEquals(provider.getHotkeyHelp(), expectedHotKey);
    }
}
