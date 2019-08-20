package com.elitecore.netvertex.cli;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ConfigDetailProviderGeneralTest.class,
        AlertConfigDetailProviderTest.class,
        DeviceConfigDetailProviderTest.class,
        DiameterGatewayConfigDetailProviderTest.class,
        DiameterGatewayPacketMappingConfigDetailProviderTest.class,
        LocationConfigDetailProviderTest.class,
        NetworkConfigDetailProviderTest.class,
        RadiusGatewayConfigDetailProviderTest.class,
        RadiusGatewayPacketMappingConfigDetailProviderTest.class,
        RoutingTableConfigDetailProviderTest.class,
        ServerInstanceGroupDetailProviderTest.class,
        ServicePolicyConfigDetailProviderTest.class,
})
public class ConfigDetailProviderTest {
}
