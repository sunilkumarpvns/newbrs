package com.elitecore.netvertex.core.mapping;

import java.util.List;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.basic.AvpUnsigned64;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.AvpUTF8String;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;

public class MappingUtil {

    private static final String IPV6 = "0x00402402b400100e0021";
    private static final String DEFAULT_SOURCE_GW = "sourceGW";
    private static final String DEFAULT_CALLED_STATION = "calledStation";
    private static final String DEFAULT_REQ_NUM = "1";
    private static final String DEFAULT_SESSION_ID = "session@Gx";
    public static final TimeSource PCRF_REQUEST_CREATION_TIME = new TimeSource() {
        
        private final long CURRENT_TIME_MILLIS = System.currentTimeMillis();
        
        @Override
        public long currentTimeInMillis() {
            return CURRENT_TIME_MILLIS;
        }
    };
    
    public static DiameterRequest createDefaultDiameterRequest(String sourceGw, String calledStation, String reqNum, String sessionId, String reqType, String ipv6, long networkRequestSupported) {
        
        DiameterRequest diameterRequest = new DiameterRequest();
        
        addDefaultCCRMapping(diameterRequest, sourceGw, calledStation, reqNum, sessionId, reqType, ipv6, networkRequestSupported);
        addRequestedQoSMapping(diameterRequest);
        addTGPPR9Avps(diameterRequest);
        
        return diameterRequest;
    }

    public static DiameterRequest createDefaultDiameterRequest() {
        
        DiameterRequest diameterRequest = new DiameterRequest();
        
        addDefaultCCRMapping(diameterRequest, DEFAULT_SOURCE_GW, DEFAULT_CALLED_STATION, DEFAULT_REQ_NUM, DEFAULT_SESSION_ID, DEFAULT_REQ_NUM, IPV6, DiameterAttributeValueConstants.NETWORK_REQUEST_SUPPORTED);
        addRequestedQoSMapping(diameterRequest);
        addTGPPR9Avps(diameterRequest);
        
        return diameterRequest;
    }

    public static void addDefaultCCRMapping(DiameterRequest diameterRequest, String sourceGw, String calledStation, String reqNum, String sessionId, String reqType, String ipv6, long networkRequestSupported) {
        
        IDiameterAVP requestTypeAvp = new AvpUnsigned64(416, 0, (byte)0, "0:416", null);
        requestTypeAvp.setStringValue(reqType);
        diameterRequest.addAvp(requestTypeAvp);
        
        IDiameterAVP requestNumberAvp = new AvpUnsigned64(415, 0, (byte)0, "0:415", null);
        requestNumberAvp.setStringValue(reqNum);
        diameterRequest.addAvp(requestNumberAvp);
        
        
        IDiameterAVP calledStationAvp = new AvpUTF8String(30, 0, (byte)0, "0:30", null);
        calledStationAvp.setStringValue(calledStation);
        diameterRequest.addAvp(calledStationAvp);
        
        IDiameterAVP sessionIdAvp = new AvpUTF8String(263, 0, (byte)0, "0:263", null);
        sessionIdAvp.setStringValue(sessionId);
        diameterRequest.addAvp(sessionIdAvp);
        
        IDiameterAVP sourceGWAvp = new AvpUTF8String(206, 21067, (byte)0, "21067:206", null);
        sourceGWAvp.setStringValue(sourceGw);
        diameterRequest.addInfoAvp(sourceGWAvp);
        
        IDiameterAVP ipv6AVP = dictionary().getAttribute(DiameterAVPConstants.FRAMED_IPV6_PREFIX);
        ipv6AVP.setStringValue(ipv6);
        diameterRequest.addAvp(ipv6AVP);
        
        IDiameterAVP nwRequestSupport = dictionary().getAttribute(DiameterAVPConstants.NETWORK_REQUEST_SUPPORT);
        nwRequestSupport.setInteger(networkRequestSupported);
        diameterRequest.addAvp(nwRequestSupport);
    }

    public static void addAVPsToDiameterRequest(List<IDiameterAVP> diameterAVPs, DiameterRequest diameterRequest) {
        
        for (IDiameterAVP diameterAVP : diameterAVPs) {
            diameterRequest.addAvp(diameterAVP);
        }
    }

    private static void addRequestedQoSMapping(DiameterRequest diameterRequest) {
        
    }

    private static void addTGPPR9Avps(DiameterRequest diameterRequest) {
        
    }
    
    private static DiameterDictionary dictionary() {
        return DummyDiameterDictionary.getInstance();
    }

    public static DiameterRequest createDefaultGyDiameterRequest() {
        
        DiameterRequest gyDiameterRequest = createDefaultDiameterRequest();
        
        IDiameterAVP eventTimestampAVP = dictionary().getAttribute(DiameterAVPConstants.EVENT_TIMESTAMP);
        eventTimestampAVP.setStringValue("1000000");
        gyDiameterRequest.addAvp(eventTimestampAVP);
        
        IDiameterAVP multipleServiceIndicator = dictionary().getAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_INDICATOR);
        multipleServiceIndicator.setInteger(1);
        gyDiameterRequest.addAvp(multipleServiceIndicator);
        
        return gyDiameterRequest;
    }
}
