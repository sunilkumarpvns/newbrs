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

public class HuaweiUMBuilder implements UMBuilder {
	
	private static final String MODULE = "HUAWEI-UM-BUILDER";

	@Override
	public List<UsageMonitoringInfo> buildUsageMonitoringInfo(DiameterPacket diameterPacket) {

		
			List<IDiameterAVP> gxxHWUsageReportAVPs = diameterPacket.getAVPList(DiameterAVPConstants.GX_X_HW_USAGE_REPORT);
			
			if(gxxHWUsageReportAVPs == null || gxxHWUsageReportAVPs.isEmpty()){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Skipping Usage Metering for Huawei VSA, Reason: gx-x-hw-usage-report AVP not found");
				return null;
			}
			
			List<UsageMonitoringInfo> usageMonitoringInfos = new ArrayList<UsageMonitoringInfo>(gxxHWUsageReportAVPs.size());
			
			for(IDiameterAVP diameterAVP : gxxHWUsageReportAVPs){
				AvpGrouped gxxHWUsageReportAVP = (AvpGrouped) diameterAVP;
				
				UsageMonitoringInfo usageMonitoringInfo = createSessionLevelUMInfo((AvpGrouped) gxxHWUsageReportAVP.getSubAttribute(DiameterAVPConstants.GX_X_HW_SESSION_USAGE), diameterPacket);
				if(usageMonitoringInfo != null){
					usageMonitoringInfos.add(usageMonitoringInfo);
				}
				
			}
				
			return usageMonitoringInfos;
	
	}

	
	private UsageMonitoringInfo createSessionLevelUMInfo(AvpGrouped sessionUsageAVP, DiameterPacket diameterPacket){
		
		if(sessionUsageAVP == null){
				LogManager.getLogger().error(MODULE, "Skipping Session level Usage Metering for Huawei VSA. Reason: gx-x-hw-session-usage AVP("
				+ DiameterAVPConstants.GX_X_HW_SESSION_USAGE +") not found from gx-x-hw-Usage-Report(" 
				+ DiameterAVPConstants.GX_X_HW_USAGE_REPORT + ") AVP");
			return null;
		}
	
		long totalInputOctets = 0 ;
		long totalOutputOctets = 0;
		IDiameterAVP tempDiameterAVP = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.GX_CC_INPUT_OCTETS);
		if(tempDiameterAVP != null) {
			totalInputOctets = tempDiameterAVP.getInteger();
		}
		tempDiameterAVP = sessionUsageAVP.getSubAttribute(DiameterAVPConstants.GX_CC_OUTPUT_OCTETS);
		if(tempDiameterAVP != null) {
			totalOutputOctets = tempDiameterAVP.getInteger();
		}

		long totalOctets= totalInputOctets + totalOutputOctets;
		
		if(totalOctets <= 0){
			LogManager.getLogger().warn(MODULE, "Skipping Session level Usage Metering for Huawei VSA. Reason: " +
			"Reported Input octets and Output Octets is ZERO");
			return null;
		}
		
		ServiceUnit usedServiceUnit = new ServiceUnit();
		usedServiceUnit.setInputOctets(totalInputOctets);
		usedServiceUnit.setOutputOctets(totalOutputOctets);
		usedServiceUnit.setTotalOctets(totalOctets);
		
		UsageMonitoringInfo monitoringInfo = new UsageMonitoringInfo();
		monitoringInfo.setMonitoringKey(diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
		monitoringInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
		monitoringInfo.setUsedServiceUnit(usedServiceUnit);
		
		return monitoringInfo;
	}


	@Override
	public List<IDiameterAVP> buildUsageMonitoringAVP(PCRFResponse pcrfResponse) {
		
		List<UsageMonitoringInfo> usageMonitoringInfos = pcrfResponse.getUsageMonitoringInfoList();
		
		if(usageMonitoringInfos == null || usageMonitoringInfos.isEmpty()){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Skipping Usage Metering for Huawei. Reason: UsageMonitoringInfo list not found");
			return null;
		}
		

		List<IDiameterAVP> usageReportAVPs = new ArrayList<IDiameterAVP>(1);
		AvpGrouped gxxUsageReport = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_USAGE_REPORT);
		for(UsageMonitoringInfo monitoringInfo : usageMonitoringInfos){
				
			AvpGrouped gxxUsage = null;
			if(monitoringInfo.getUsageMonitoringLevel() == UMLevel.PCC_RULE_LEVEL){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Unable to set UM AVPs for monitoring key: "+ monitoringInfo.getMonitoringKey() 
					+". Reason: UM info is of type PCC Rule level");
				continue;

				
			} else if(monitoringInfo.getGrantedServiceUnit() == null || monitoringInfo.getGrantedServiceUnit().getTotalOctets() <= 0){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Unable to set UM AVPs for monitoring key: "+ monitoringInfo.getMonitoringKey() 
					+". Reason: granted service unit not provided");
				
				continue;
			}				
				
			
			gxxUsage = (AvpGrouped)DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_X_HW_SESSION_USAGE);
			
			if(monitoringInfo.getGrantedServiceUnit().getTotalOctets() > 0){
				IDiameterAVP outputOctets = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_CC_OUTPUT_OCTETS);
				outputOctets.setInteger(monitoringInfo.getGrantedServiceUnit().getTotalOctets());
				gxxUsage.addSubAvp(outputOctets);
			}

			if(monitoringInfo.getGrantedServiceUnit().getTotalOctets() > 0){
				IDiameterAVP inputOctets = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.GX_CC_INPUT_OCTETS);
				inputOctets.setInteger(monitoringInfo.getGrantedServiceUnit().getTotalOctets());
				gxxUsage.addSubAvp(inputOctets);
			}
			gxxUsageReport.addSubAvp(gxxUsage);
		}
	
		if(gxxUsageReport.getGroupedAvp() != null){
			usageReportAVPs.add(gxxUsageReport);
		}
		
	
		return usageReportAVPs;
	
		
	}

}
