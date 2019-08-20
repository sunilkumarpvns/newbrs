package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc;

import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.netvertex.gateway.diameter.conf.impl.DiameterGatewayConfigurationImpl;
import com.elitecore.netvertex.gateway.diameter.conf.impl.DiameterGatewayProfileConfigurationImpl;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DiameterToPCCMappingTest {

    private DiameterToPCCMapping diameterToPCCMapping = new DiameterToPCCMapping(Collections.emptyList(), Collections.emptyMap());

    private DiameterGatewayConfigurationImpl diameterGatewayConfiguration = spy(new DiameterGatewayConfigurationImpl());
    private DiameterGatewayProfileConfigurationImpl diameterGatewayProfileConfiguration = new DiameterGatewayProfileConfigurationImpl();
    private DiameterRequest diameterRequest = new DiameterRequest();
    private PCRFRequest pcrfRequest = new PCRFRequestImpl();
    private PCRFRequestMappingValueProvider valueProvider = new PCRFRequestMappingValueProvider(diameterRequest, pcrfRequest, diameterGatewayConfiguration);

    @Before
    public void setUp() {
        diameterRequest.setApplicationID(ApplicationIdentifier.CC.applicationId);
        diameterGatewayProfileConfiguration.setSyApplicationId(ApplicationIdentifier.TGPP_SY.applicationId);
        diameterGatewayConfiguration.setProfile(diameterGatewayProfileConfiguration);
        diameterGatewayProfileConfiguration.setRevalidationMode(PCRFKeyValueConstants.REVALIDATION_MODE_CLIENT_INITIATED);
        diameterGatewayConfiguration.setName("test");
        diameterGatewayProfileConfiguration.setGyApplicationId(ApplicationIdentifier.CC.applicationId);
    }
    @Test
    public void addRequestCreationTimeInMilliesWhenDiameterPacketTypeIsOtherSy() {
        diameterToPCCMapping.apply(valueProvider);
        assertThat(pcrfRequest.getInterfacePacketCreateTime(), is(diameterRequest.creationTimeMillis()));
    }

    @Test
    public void addInterfaceQueueTimeInMilliesWhenDiameterPacketTypeIsOtherThanSy() {
        diameterToPCCMapping.apply(valueProvider);
        assertThat(pcrfRequest.getInterfaceQueueTimeInMillies(), is(diameterRequest.getQueueTime()));
    }

    @Test
    public void doesNotAddRequestCreationTimeInMilliesWhenDiameterPacketTypeIsSy() {
        diameterGatewayProfileConfiguration.setSyApplicationId(10);
        diameterRequest.setApplicationID(10);
        diameterToPCCMapping.apply(valueProvider);
        assertThat(pcrfRequest.getInterfacePacketCreateTime(), is(-1l));
    }

    @Test
    public void doNotAddInterfaceQueueTimeInMilliesWhenDiameterPacketTypeIsSy() {
        diameterGatewayProfileConfiguration.setSyApplicationId(10);
        diameterRequest.setApplicationID(10);
        diameterToPCCMapping.apply(valueProvider);
        assertThat(pcrfRequest.getInterfaceQueueTimeInMillies(), is(-1l));
    }

    @Test
    public void useGatewatConfigurationToCheckSyApplication() {
        diameterGatewayProfileConfiguration.setSyApplicationId(10);
        diameterRequest.setApplicationID(10);
        diameterToPCCMapping.apply(valueProvider);

        verify(diameterGatewayConfiguration, times(1)).getSyApplicationId();
    }
}
