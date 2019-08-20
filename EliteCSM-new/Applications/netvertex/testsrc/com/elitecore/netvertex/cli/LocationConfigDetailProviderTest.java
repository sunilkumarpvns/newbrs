package com.elitecore.netvertex.cli;

import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.locationmanagement.DummyLocationRepository;
import com.elitecore.netvertex.core.locationmanagement.data.LocationInformationConfiguration;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class LocationConfigDetailProviderTest {
    private ConfigDetailProvider provider;
    private DummyNetvertexServerContextImpl netvertexServerContext;
    private DummyLocationRepository locationRepository;

    @Before
    public void setUp() {
        netvertexServerContext = DummyNetvertexServerContextImpl.spy();
        locationRepository = netvertexServerContext.getLocationRepository();
        provider = spy(new ConfigDetailProvider(netvertexServerContext));
    }

    @Test
    public void informativeMessageReturnedWhenLocationConfigByAreaNameKeyPassedWithoutParameter() {
        String key = "location-by-areaname";
        String expectedOutput = "Provide Area Name to view Location Information by using Area Name.";
        String actualOutput = provider.execute(new  String[]{key});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageReturnedWhenUnknowLocationConfigurationByAreanamePassed() throws Exception{
        String key = "location-by-areaname";
        String param1 = "123";
        String expectedOutput = "Location Information is not found for provided Area Name: " + param1;
        String actualOutput = provider.execute(new String[]{key, param1});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void toStringMessageReturnedWhenKnowLocationConfigurationByAreanamePassed() throws Exception{
        String key = "location-by-areaname";
        String areaName = "Gujarat";
        LocationInformationConfiguration configuration = locationRepository.spylocationConfByAreaNameWithAreaName(areaName);
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, areaName});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageShouldReturnedWhenLocationconfigurationKeyPassedWithoutParameter() {
        String key = "location";
        String expectedOutput = "Provide required parameters to view Location Information.\n" +
                "Usage = location -mcc <mcc-code> -mnc <mnc-code> -lac <lac-code> [-ci <ci-value>] [-rac <rac-value>] [-sac <sac-value>]\n" +
                "Description = View Location Configuration based on input provided\n";
        String actualOutput = provider.execute(new String[]{key});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageReturnedWhenLessThanRequiredNumberOfParametersArePassedForLocationConfiguration() {
        String key = "location";
        String[] params = new String[] {"-abc","1","-mnc","1","-lac","1","-ci"};
        String expectedOutput = "Input parameter missing. Expected: 8, Actual: 7";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3], params[4], params[5],
                params[6]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageReturnedWhenMCCparameterNotProvidedForLocationConfigurtaion() {
        String key = "location";
        String[] params = new String[] {"-abc","1","-mnc","1","-lac","1","-ci","1","-efg","1","-hij","1"};
        String expectedOutput = "Either of MCC, MNC or LAC value is not provided. Skipping the display of Location Information.";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3], params[4], params[5],
                params[6], params[7], params[8], params[9], params[10], params[11]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageReturnedWhenAnyOfCIandRACandSACparameterNotProvidedAndMCCandMNCandLACareProvidedForLocationConfigurtaion() {
        String key = "location";
        String[] params = new String[] {"-mcc","1","-mnc","1","-lac","1","-abc","1","-efg","1","-hij","1"};
        String expectedOutput = "Either of CI, RAC or SAC value is not provided. Skipping the display of Location Information.";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3], params[4], params[5],
                params[6], params[7], params[8], params[9], params[10], params[11]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void toStringReturnedWhenKnownCIprovided() {
        String key = "location";
        String[] params = new String[] {"-mcc","1","-mnc","1","-lac","1","-ci","1","-efg","1","-hij","1"};
        LocationInformationConfiguration configuration = locationRepository.spylocationConfByCGI("1");
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3], params[4], params[5],
                params[6], params[7], params[8], params[9], params[10], params[11]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageReturnedWhenUnknownCIprovided() {
        String key = "location";
        String[] params = new String[] {"-mcc","1","-mnc","1","-lac","1","-ci","1","-efg","1","-hij","1"};
        String expectedOutput = "Location Information is not found for provided MCC Code: 1, MNC Code: 1, LAC Code: 1, CGI Code: 1";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3], params[4], params[5],
                params[6], params[7], params[8], params[9], params[10], params[11]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void toStringShouldBeReturnedWhenKnownRACprovided() {
        String key = "location";
        String[] params = new String[] {"-mcc","1","-mnc","1","-lac","1","-efg","1","-rac","1","-hij","1"};
        LocationInformationConfiguration configuration = locationRepository.spylocationConfByRAC("1");
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3], params[4], params[5],
                params[6], params[7], params[8], params[9], params[10], params[11]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageShouldBeReturnedWhenUnknownRACprovided() {
        String key = "location";
        String[] params = new String[] {"-mcc","1","-mnc","1","-lac","1","-efg","1","-rac","1","-hij","1"};
        String expectedOutput = "Location Information is not found for provided MCC Code: 1, MNC Code: 1, LAC Code: 1, RAC Code: 1";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3], params[4], params[5],
                params[6], params[7], params[8], params[9], params[10], params[11]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void locationConfigToStringShouldBeReturnedWhenKnownSACprovided() {
        String key = "location";
        String[] params = new String[] {"-mcc","1","-mnc","1","-lac","1","-abc","1","-efg","1","-sac","1"};
        LocationInformationConfiguration configuration = locationRepository.spylocationConfBySAC("1");
        String expectedOutput = "expectedOutput";
        when(configuration.toString()).thenReturn(expectedOutput);
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3], params[4], params[5],
                params[6], params[7], params[8], params[9], params[10], params[11]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void informativeMessageShouldBeReturnedWhenUnknownSACprovided() {
        String key = "location";
        String[] params = new String[] {"-mcc","1","-mnc","1","-lac","1","-abc","1","-efg","1","-sac","1"};
        String expectedOutput = "Location Information is not found for provided MCC Code: 1, MNC Code: 1, LAC Code: 1, SAC Code: 1";
        String actualOutput = provider.execute(new String[]{key, params[0], params[1], params[2], params[3], params[4], params[5],
                params[6], params[7], params[8], params[9], params[10], params[11]});
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void helpMessageReturnsUsageExampleforLocation() {
        String key = "location";
        String expected = "-mcc <mcc-code> -mnc <mnc-code> -lac <lac-code> [-ci <ci-value>] [-rac <rac-value>] [-sac <sac-value>]";
        String actualOutput = provider.execute(new String[]{key, "-help"});
        assertThat(actualOutput, StringContains.containsString(expected));
    }

    @Test
    public void helpMessageReturnsUsageExampleforLocationByAreaName() {
        String key = "location-by-areaname";
        String expected = key + " <areaname>";
        String actualOutput = provider.execute(new String[]{key, "-help"});

        assertThat(actualOutput, StringContains.containsString(expected));
    }
}
