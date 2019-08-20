package com.elitecore.netvertex.core.mapping;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.TgppR9Builder;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.utility.AttributeFactories;
import com.elitecore.netvertex.gateway.diameter.utility.AttributeFactory;
import com.elitecore.netvertex.gateway.diameter.utility.AvpAccumalatorTestSupport;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Before;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

public class GxRarAppSepecificMappingTest {

    private static final String SESSION_RELEASE_CAUSE = "233";

    private PCCToDiameterMapping mapping;
    private DiameterPacketMappingValueProvider valueProvider;
    private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
    private PCRFResponse pcrfResponse;
    private DiameterPacket diameterPacket;
    private AttributeFactory attributeFactory = AttributeFactories.fromDummyDictionary();

    @Before
    public void before() {

        mapping = new PCCToDiameterMapping.PCCToDiameterMappingBuilder().withGxRARMapping(new TgppR9Builder()).build();
        pcrfResponse = new PCRFResponseImpl();
        pcrfResponse.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GX.val);
        diameterPacket = new DiameterRequest();
        DummyDiameterGatewayControllerContext controllerContext = new DummyDiameterGatewayControllerContext();
        DummyNetvertexServerConfiguration serverConfiguration = controllerContext.getServerContext().getServerConfiguration();
        serverConfiguration.spyMiscConf();
        valueProvider = new DiameterPacketMappingValueProvider(pcrfResponse, diameterPacket, mock(DiameterGatewayConfiguration.class), controllerContext);
        DummyDiameterDictionary.getInstance();
    }

    @Test
    public void addSessionReleaseCauseAvp_WhenSessionReleaseCauseFoundFromResponse() {

        pcrfResponse.setAttribute(PCRFKeyConstants.SESSION_RELEASE_CAUSE.getVal(), SESSION_RELEASE_CAUSE);
        mapping.apply(valueProvider, accumalator);

        ReflectionAssert.assertLenientEquals(createExpectedAVPs(), accumalator.getDiameterAVPs(DiameterAVPConstants.TGPP_SESSION_RELEASE_CAUSE));
    }

    @Test
    public void addReAuthRequestTypeAvpWithValueAuthorizeOnly() {

        mapping.apply(valueProvider, accumalator);

        ReflectionAssert.assertLenientEquals(createReAuthRequestTypeAvp(), accumalator.getDiameterAVPs(DiameterAVPConstants.RE_AUTH_REQUEST_TYPE));
    }

    private List<IDiameterAVP> createExpectedAVPs() {

        IDiameterAVP sessionReleaseCauseAVP = attributeFactory.create(DiameterAVPConstants.TGPP_SESSION_RELEASE_CAUSE);
        sessionReleaseCauseAVP.setStringValue(SESSION_RELEASE_CAUSE);
        return Arrays.asList(sessionReleaseCauseAVP);
    }

    private List<IDiameterAVP> createReAuthRequestTypeAvp() {

        IDiameterAVP reAuthRequestType = attributeFactory.create(DiameterAVPConstants.RE_AUTH_REQUEST_TYPE);
        reAuthRequestType.setInteger(DiameterAttributeValueConstants.RE_AUTH_AUTHORIZE_ONLY);
        return Arrays.asList(reAuthRequestType);
    }

    @Test
    public void test_apply_doNotAddAVPInAccumulator_WhenSessionReleaseCauseValueNotFound() {

        mapping.apply(valueProvider, accumalator);
        ReflectionAssert.assertLenientEquals(Collectionz.newArrayList(), accumalator.getDiameterAVPs(DiameterAVPConstants.TGPP_SESSION_RELEASE_CAUSE));
    }


}
