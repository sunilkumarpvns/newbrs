package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

/**
 * Created by harsh on 6/28/17.
 */
public class AccessNetworkAvpMapping implements DiameterToPCCPacketMapping {
    
    
    private String ratTypeAVPId;
    private String accessNetworkAVPId;
    
    public AccessNetworkAvpMapping(String ratTypeAVPId, String accessNetworkAVPId) {
        this.ratTypeAVPId = ratTypeAVPId;
        this.accessNetworkAVPId = accessNetworkAVPId;
    }
    
    @Override
    public void apply(PCRFRequestMappingValueProvider valueProvider) {

        DiameterRequest diameterPacket = valueProvider.getDiameterRequest();
        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();

        IDiameterAVP ratType = diameterPacket.getAVP(ratTypeAVPId);
        if(ratType != null){
            switch((int)ratType.getInteger()) {

                case DiameterAttributeValueConstants.TGPP_RAT_TYPE_WLAN:
                    pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_WLAN.val);
                    break;

                case DiameterAttributeValueConstants.TGPP_RAT_TYPE_VIRTUAL:
                    pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_VIRTUAL.val);
                    break;

                case DiameterAttributeValueConstants.TGPP_RAT_TYPE_UTRAN:
                    pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_UTRAN.val);
                    break;

                case DiameterAttributeValueConstants.TGPP_RAT_TYPE_GERAN:
                    pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_GERAN.val);
                    break;

                case DiameterAttributeValueConstants.TGPP_RAT_TYPE_GAN:
                    pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_GAN.val);
                    break;

                case DiameterAttributeValueConstants.TGPP_RAT_TYPE_HSPA_EVO:
                    pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_HSPA_EVO.val);
                    break;

                case DiameterAttributeValueConstants.TGPP_RAT_TYPE_EUTRAN:
                    pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_EUTRAN.val);
                    break;

                case DiameterAttributeValueConstants.TGPP_RAT_TYPE_CDMA2000_1X:
                    pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_CDMA2000_1X.val);
                    break;

                case DiameterAttributeValueConstants.TGPP_RAT_TYPE_HRPD:
                    pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_HRPD.val);
                    break;

                case DiameterAttributeValueConstants.TGPP_RAT_TYPE_UMB :
                    pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_UMB.val);
                    break;

                case DiameterAttributeValueConstants.TGPP_RAT_TYPE_EHRPD:
                    pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_EHRPD.val);
                    break;

                default :
                    pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, ratType.getStringValue());
            }
        }

        if(pcrfRequest.getAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val) == null){
            IDiameterAVP ipCANType = diameterPacket.getAVP(accessNetworkAVPId);
            if(ipCANType != null){
                switch((int)ipCANType.getInteger()) {
                    case DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_3GPP_GPRS :
                        pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_3GPP_GPRS.val);
                        break;

                    case DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_DOCSIS :
                        pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_DOCSIS.val);
                        break;

                    case DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_XDSL :
                        pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_XDSL.val);
                        break;

                    case DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_WIMAX :
                        pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_WIMAX.val);
                        break;

                    case DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_3GPP2 :
                        pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_3GPP2.val);
                        break;


                    case DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_3GPP_EPS :
                        pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_3GPP_EPS.val);
                        break;

                    case DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_NON_3GPP_EPS :
                        pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, PCRFKeyValueConstants.ACCESS_NETWORK_NON_3GPP_EPS.val);
                        break;

                    default :
                        pcrfRequest.setAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val, ipCANType.getStringValue());
                }
            }
        }
    }
}
