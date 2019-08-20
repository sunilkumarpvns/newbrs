package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.usagemetering.ServiceUnit;
import com.elitecore.netvertex.usagemetering.UMLevel;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;

public class MSCCUMBuilder implements UMBuilder {
	
	private static final String MODULE="MSCC-UM-BUILDER";

	@Override
	public List<UsageMonitoringInfo> buildUsageMonitoringInfo(DiameterPacket diameterPacket) {

		List<IDiameterAVP> msccAVPs = diameterPacket.getAVPList(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
		
		if(msccAVPs == null || msccAVPs.isEmpty()){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Skipping Usage Metering. Reason: Multiple-Service-Credit-Control AVP not found");
			return null;
		}
		
		IDiameterAVP tempDiameterAVP = null;
		ArrayList<UsageMonitoringInfo> usageMonitoringInfos = new ArrayList<UsageMonitoringInfo>(msccAVPs.size());
		
		for(IDiameterAVP diameterAVP : msccAVPs){
			
			AvpGrouped msccAvp = (AvpGrouped) diameterAVP;

			AvpGrouped usedServiceUnitsAVP = (AvpGrouped)msccAvp.getSubAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
			
			if(usedServiceUnitsAVP == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Skipping Usage Metering for Ericsson, Reason: Used-Service-Unit AVP not found");
				continue;
			}
			
			long totalOctets = 0 , usedTime = 0 , inputOctets = 0 , outputOctets = 0 ;
			
			tempDiameterAVP = usedServiceUnitsAVP.getSubAttribute(DiameterAVPConstants.CC_INPUT_OCTETS);
			if(tempDiameterAVP != null) {
				inputOctets = tempDiameterAVP.getInteger();
			}
			tempDiameterAVP = usedServiceUnitsAVP.getSubAttribute(DiameterAVPConstants.CC_OUTPUT_OCTETS);
			if(tempDiameterAVP != null) {
				outputOctets = tempDiameterAVP.getInteger();
			}
			
			tempDiameterAVP = usedServiceUnitsAVP.getSubAttribute(DiameterAVPConstants.CC_TOTAL_OCTETS);
			if(tempDiameterAVP != null) {
				totalOctets = tempDiameterAVP.getInteger();
			}else{
				totalOctets = inputOctets + outputOctets;
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Total octets: " + totalOctets + " calculated from input and output octets. " +
					"Reason: Total Octets AVP not received");
				}
			}
			
			tempDiameterAVP = usedServiceUnitsAVP.getSubAttribute(DiameterAVPConstants.CC_TIME);
			if(tempDiameterAVP != null) {
				usedTime = tempDiameterAVP.getInteger();
			}
			 
			if(totalOctets <= 0 && usedTime <= 0){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Skipping Usage Metering for Ericsson. " +
					"Reason: Reported total Octets and time is ZERO");
				}
				continue;
			}
			
			ServiceUnit serviceUnit = new ServiceUnit();
			serviceUnit.setTotalOctets(totalOctets);
			serviceUnit.setTime(usedTime);
			serviceUnit.setInputOctets(inputOctets);
			serviceUnit.setOutputOctets(outputOctets);
			
			UsageMonitoringInfo monitoringInfo = new UsageMonitoringInfo();
			monitoringInfo.setMonitoringKey(diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
			monitoringInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
			monitoringInfo.setUsedServiceUnit(serviceUnit);
			
			usageMonitoringInfos.add(monitoringInfo);
		}
	
		return usageMonitoringInfos;
	}

	@Override
	public List<IDiameterAVP> buildUsageMonitoringAVP(PCRFResponse pcrfResponse) {
		
			List<UsageMonitoringInfo> usageMonitoringInfos =  pcrfResponse.getUsageMonitoringInfoList();
			
			if(usageMonitoringInfos == null || usageMonitoringInfos.isEmpty()){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Skipping Usage Metering for Ericsson. Reason: UsageMonitoringInfo list not found");
				return null;
			}
			

			List<IDiameterAVP> msccs = null;
			
			for(UsageMonitoringInfo monitoringInfo : usageMonitoringInfos){
				
				if(monitoringInfo.getUsageMonitoringLevel() == UMLevel.PCC_RULE_LEVEL){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Unable to set UM AVPs for monitoring key: "+ monitoringInfo.getMonitoringKey() 
						+". Reason: UM info is of type PCC Rule level");
					continue;
				}
				
				if(msccs == null){
					msccs = new ArrayList<IDiameterAVP>(usageMonitoringInfos.size());
				}
				
				
				ArrayList<IDiameterAVP> gsuChildAtts = new ArrayList<IDiameterAVP>(4);
				
				if(monitoringInfo.getGrantedServiceUnit() == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Skipping Usage Metering for monitoring key: "+ monitoringInfo.getMonitoringKey() 
						+". Reason:Granted Service Unit not provided in Session Level UM Info");
					continue;
				}
				

				if(monitoringInfo.getGrantedServiceUnit().getTotalOctets() > 0){
					IDiameterAVP grantedUnits = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.CC_TOTAL_OCTETS);
					grantedUnits.setInteger(monitoringInfo.getGrantedServiceUnit().getTotalOctets());
					gsuChildAtts.add(grantedUnits);
				}
				if(monitoringInfo.getGrantedServiceUnit().getInputOctets() > 0){
					IDiameterAVP grantedUnits = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.CC_INPUT_OCTETS);
					grantedUnits.setInteger(monitoringInfo.getGrantedServiceUnit().getInputOctets());
					gsuChildAtts.add(grantedUnits);
				}
				if(monitoringInfo.getGrantedServiceUnit().getOutputOctets() > 0){
					IDiameterAVP grantedUnits = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.CC_OUTPUT_OCTETS);
					grantedUnits.setInteger(monitoringInfo.getGrantedServiceUnit().getOutputOctets());
					gsuChildAtts.add(grantedUnits);
				}
				if(monitoringInfo.getGrantedServiceUnit().getTime() > 0){
					IDiameterAVP grantedUnits = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.CC_TIME);
					grantedUnits.setInteger(monitoringInfo.getGrantedServiceUnit().getTime());
					gsuChildAtts.add(grantedUnits);
				}
			
				IDiameterAVP grantedServiceUnitAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GRANTED_SERVICE_UNIT);
				ArrayList<IDiameterAVP> msccChildAVPs = new ArrayList<IDiameterAVP>(2);
				if(!gsuChildAtts.isEmpty()){
					grantedServiceUnitAVP.setGroupedAvp(gsuChildAtts);
					msccChildAVPs.add(grantedServiceUnitAVP);
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Skipping Usage Metering for Ericsson.Reason: Granted Service Unit not provided in Session Level UM Info");
					continue;
				}
				
				AvpGrouped mscc = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
				mscc.setGroupedAvp(msccChildAVPs);
				msccs.add(mscc);
			}
		
			return msccs;
	
	}

}
