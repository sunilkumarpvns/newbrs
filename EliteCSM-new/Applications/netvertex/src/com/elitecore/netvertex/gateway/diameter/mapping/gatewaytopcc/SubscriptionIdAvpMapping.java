package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by harsh on 6/28/17.
 */
public class SubscriptionIdAvpMapping implements DiameterToPCCPacketMapping {


    @Override
    public void apply(@Nonnull PCRFRequestMappingValueProvider valueProvider) {
        DiameterRequest diameterPacket = valueProvider.getDiameterRequest();
        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();

        List<IDiameterAVP> subscriptionIdAVPs = diameterPacket.getAVPList(DiameterAVPConstants.SUBSCRIPTION_ID);

        if (subscriptionIdAVPs == null) {
            return;
        }

        for (IDiameterAVP avp : subscriptionIdAVPs) {
            IDiameterAVP subscriptionIdType = ((AvpGrouped) avp).getSubAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
            IDiameterAVP subscriptionIdData = ((AvpGrouped) avp).getSubAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);

            if (subscriptionIdType.getInteger() == DiameterAttributeValueConstants.DIAMETER_END_USER_E164) {
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.getVal(), subscriptionIdData.getStringValue());
            } else if (subscriptionIdType.getInteger() == DiameterAttributeValueConstants.DIAMETER_END_USER_IMSI) {
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.getVal(), subscriptionIdData.getStringValue());
            } else if (subscriptionIdType.getInteger() == DiameterAttributeValueConstants.DIAMETER_END_USER_SIP_URI) {
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_SIPURI.getVal(), subscriptionIdData.getStringValue());
            } else if (subscriptionIdType.getInteger() == (DiameterAttributeValueConstants.DIAMETER_END_USER_NAI)) {
                String subscriptionData = subscriptionIdData.getStringValue();
                String userName = RadiusUtility.getActualUserName(subscriptionData);
                String realName = RadiusUtility.getRealm(subscriptionData);
                String decoration = RadiusUtility.getDecoration(subscriptionData);

                pcrfRequest.setAttribute(PCRFKeyConstants.CS_NAI_RELATED_USERNAME.getVal(), userName);
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_NAI_REALM.getVal(), realName);
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_NAI_WIMAX_DECORATION.getVal(), decoration);

                pcrfRequest.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_NAI.getVal(), subscriptionIdData.getStringValue());
            }
        }
    }
}
