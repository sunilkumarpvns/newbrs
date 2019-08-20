package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gy;

import java.util.ArrayList;
import java.util.List;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class MSCCMapping implements DiameterToPCCPacketMapping {

    private static final String MODULE = "MSCC-MAPPING";

    @Override
    public void apply(PCRFRequestMappingValueProvider valueProvider) {
        
        DiameterRequest packet = valueProvider.getDiameterRequest();
        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();
        
        ArrayList<IDiameterAVP> msccAVPList = packet.getAVPList(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
        
        if (Collectionz.isNullOrEmpty(msccAVPList)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "MSCC AVPs not found from request");
            }
            return;
        }
        
        List<MSCC> msccList = new ArrayList<>();
        
        for (IDiameterAVP diameterAVP : msccAVPList) {
            
            AvpGrouped msccAVP = (AvpGrouped)diameterAVP;
            
            MSCC reportedMSCC = new MSCC();

			setRequestedServiceUnits(reportedMSCC, msccAVP);

			List<IDiameterAVP> serviceIdentifierAVPs = msccAVP.getSubAttributeList(DiameterAVPConstants.SERVICE_IDENTIFIER);
            if (Collectionz.isNullOrEmpty(serviceIdentifierAVPs) == false) {
                List<Long> serviceIdentifiers = new ArrayList<>(); 
                for (IDiameterAVP serviceIdentifierAVP : serviceIdentifierAVPs) {
                    serviceIdentifiers.add(serviceIdentifierAVP.getInteger());
                }
                reportedMSCC.setServiceIdentifiers(serviceIdentifiers);
            }
            
            IDiameterAVP ratingGroupAVP = msccAVP.getSubAttribute(DiameterAVPConstants.RATING_GROUP);
            if (ratingGroupAVP != null) {
                reportedMSCC.setRatingGroup(ratingGroupAVP.getInteger());
            }
            
            AvpGrouped usuAVP =(AvpGrouped) msccAVP.getSubAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
            if (usuAVP != null) {
                GyServiceUnits gyUsedServiceUnits = createServiceUnits(usuAVP);
                IDiameterAVP reportingReasonAVP = usuAVP.getSubAttribute(DiameterAVPConstants.TGPP_REPORTING_REASON);
                if (reportingReasonAVP == null) {
                    // if not found from USU, try from MSCC block
                    reportingReasonAVP = msccAVP.getSubAttribute(DiameterAVPConstants.TGPP_REPORTING_REASON);
                }
                
                if (reportingReasonAVP != null) {
                    ReportingReason reportingReason = ReportingReason.fromVal((int) reportingReasonAVP.getInteger());
                    if (reportingReason != null) {
                        reportedMSCC.setReportingReason(reportingReason);
                    }
                }
                reportedMSCC.setUsedServiceUnits(gyUsedServiceUnits);
            }
            
            msccList.add(reportedMSCC);
        }

        pcrfRequest.setReportedMSCCs(msccList);
    }

	private void setRequestedServiceUnits(MSCC reportedMSCC, AvpGrouped msccAVP) {

		AvpGrouped requestedServiceUnitAVP = (AvpGrouped)msccAVP.getSubAttribute(DiameterAVPConstants.REQUESTED_SERVICE_UNIT);
		if (requestedServiceUnitAVP == null) {
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Skipping requested service unit mapping. Reason: Requested-Service-Unit AVP not received");
			}
			return;
		}

		GyServiceUnits requestedUnits = createServiceUnits(requestedServiceUnitAVP);
		reportedMSCC.setRequestedServiceUnits(requestedUnits);
	}

	private static GyServiceUnits createServiceUnits(AvpGrouped usuAVP) {
        
        long totalOctets;
        long usedTime = 0;
        long inputOctets = 0;
        long outputOctets = 0;
        long money = 0;
		long serviceSpecificUnits = 0;

        IDiameterAVP tempDiameterAVP = usuAVP.getSubAttribute(DiameterAVPConstants.CC_INPUT_OCTETS);
        if (tempDiameterAVP != null) {
            inputOctets = tempDiameterAVP.getInteger();
        }
        tempDiameterAVP = usuAVP.getSubAttribute(DiameterAVPConstants.CC_OUTPUT_OCTETS);
        if (tempDiameterAVP != null) {
            outputOctets = tempDiameterAVP.getInteger();
        }

        tempDiameterAVP = usuAVP.getSubAttribute(DiameterAVPConstants.CC_TOTAL_OCTETS);
        if (tempDiameterAVP != null) {
            totalOctets = tempDiameterAVP.getInteger();
        } else {
            totalOctets = inputOctets + outputOctets;
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Total octets: " + totalOctets
                        + " calculated from input and output octets. " + "Reason: Total Octets AVP not received");
            }
        }
        
        tempDiameterAVP = usuAVP.getSubAttribute(DiameterAVPConstants.CC_TIME);
        if(tempDiameterAVP != null) {
            usedTime = tempDiameterAVP.getInteger();
        }
        
        tempDiameterAVP = usuAVP.getSubAttribute(DiameterAVPConstants.CC_MONEY);
        if(tempDiameterAVP != null) {
            money = tempDiameterAVP.getInteger();
        }

		tempDiameterAVP = usuAVP.getSubAttribute(DiameterAVPConstants.CC_SERVICE_SPECIFIC_UNITS);
		if (tempDiameterAVP != null) {
			serviceSpecificUnits = tempDiameterAVP.getInteger();
		}
         
        GyServiceUnits usedServiceUnits = new GyServiceUnits();
        usedServiceUnits.setVolume(totalOctets);
        usedServiceUnits.setTime(usedTime);
        usedServiceUnits.setMoney(money);
		usedServiceUnits.setServiceSpecificUnits(serviceSpecificUnits);

        return usedServiceUnits;
    }
    
}
