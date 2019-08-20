package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS.IPCANQoSBuilder;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

/**
 * Created by harsh on 6/28/17.
 */
public class RequestedQoSMapping implements DiameterToPCCPacketMapping {
    @Override
    public void apply(PCRFRequestMappingValueProvider valueProvider) {


        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();

        DiameterRequest diameterRequest = valueProvider.getDiameterRequest();
        IPCANQoSBuilder ipcanQoSBuilder = new IPCANQoSBuilder();

        addRequestQoS(ipcanQoSBuilder, diameterRequest);

        addDefaultEPSBearer(ipcanQoSBuilder, diameterRequest);

        String qosUpgradeAvp = diameterRequest.getAVPValue(DiameterAVPConstants.TGPP_QOS_UPGRADE);
        if(qosUpgradeAvp != null) {
            pcrfRequest.setAttribute(PCRFKeyConstants.QOS_UPGRADE.getVal(), qosUpgradeAvp);
        }

        if(PCRFKeyValueConstants.QOS_UPGRADE_SUPPORTED.val.equals(pcrfRequest.getAttribute(PCRFKeyConstants.QOS_UPGRADE.val)) == true){
            ipcanQoSBuilder.withQoSUpgrade(true);
        } else {
            ipcanQoSBuilder.withQoSUpgrade(false);
        }


        IPCANQoS ipcanQoS = ipcanQoSBuilder.build();

        if(ipcanQoS != null){
            pcrfRequest.setRequestedQoS(ipcanQoS);
        }

    }

    private void addRequestQoS(IPCANQoSBuilder builder, DiameterRequest diameterRequest) {
        AvpGrouped qosInfomationAVP = (AvpGrouped)diameterRequest.getAVP(DiameterAVPConstants.TGPP_QOS_INFORMATION);
        if(qosInfomationAVP != null){
            IDiameterAVP qciAvp = qosInfomationAVP.getSubAttribute(DiameterAVPConstants.TGPP_QCI);
            if(qciAvp != null){
                builder.withQCI((int) qciAvp.getInteger());
            }

            IDiameterAVP  aambrdlAvp = qosInfomationAVP.getSubAttribute(DiameterAVPConstants.TGPP_APN_AGGREGATED_MAX_BR_DL);
            if(aambrdlAvp != null){
                builder.withAAMBRDL(aambrdlAvp.getInteger());
            }

            aambrdlAvp = qosInfomationAVP.getSubAttribute(DiameterAVPConstants.TGPP_MAX_REQUESTED_BW_DL);
            if(aambrdlAvp != null){
                builder.withMBRDL(aambrdlAvp.getInteger());
            }

            IDiameterAVP  aambrulAvp = qosInfomationAVP.getSubAttribute(DiameterAVPConstants.TGPP_APN_AGGREGATED_MAX_BR_UL);
            if(aambrulAvp != null){
                builder.withAAMBRUL(aambrulAvp.getInteger());
            }

            aambrulAvp = qosInfomationAVP.getSubAttribute(DiameterAVPConstants.TGPP_MAX_REQUESTED_BW_UL);
            if(aambrulAvp != null){
                builder.withMBRUL(aambrulAvp.getInteger());
            }


            addARPAvp(builder, qosInfomationAVP);
        }
    }

    private void addARPAvp(IPCANQoSBuilder builder, AvpGrouped qosInfomationAVP) {
        AvpGrouped arpAvp = (AvpGrouped)qosInfomationAVP.getSubAttribute(DiameterAVPConstants.TGPP_ALLOCATION_RETENTION_PRIORIY);

        if(arpAvp != null){
            IDiameterAVP priorityLvl = arpAvp.getSubAttribute(DiameterAVPConstants.TGPP_PRIORIY_LEVEL);
            if(priorityLvl != null){
                builder.withPriorityLevel((int) priorityLvl.getInteger());
            }

            IDiameterAVP preEmptionCapablity = arpAvp.getSubAttribute(DiameterAVPConstants.TGPP_PRE_EMPTION_CAPABILITY);
            if(preEmptionCapablity != null){
                builder.withPreEmptionCapability(preEmptionCapablity.getStringValue());
            }

            IDiameterAVP preEmptionValnerability = arpAvp.getSubAttribute(DiameterAVPConstants.TGPP_PRE_EMPTION_VALNERABILITY);
            if(preEmptionValnerability != null){
                builder.withPreEmptionValnerability(preEmptionValnerability.getStringValue());
            }

        }
    }

    private void addDefaultEPSBearer(IPCANQoSBuilder builder, DiameterRequest diameterRequest) {
        AvpGrouped defaultEPSBearerQoSAVP = (AvpGrouped)diameterRequest.getAVP(DiameterAVPConstants.TGPP_DEFAULT_EPS_BEARER_QOS);
        if(defaultEPSBearerQoSAVP != null){
            IDiameterAVP qciAvp = defaultEPSBearerQoSAVP.getSubAttribute(DiameterAVPConstants.TGPP_QCI);
            if(qciAvp != null){
                builder.withQCI((int) qciAvp.getInteger());
            }

            addARPAvp(builder, defaultEPSBearerQoSAVP);
        }
    }
}
