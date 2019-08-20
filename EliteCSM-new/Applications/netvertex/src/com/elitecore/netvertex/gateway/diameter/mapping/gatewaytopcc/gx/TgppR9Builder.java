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
import com.elitecore.netvertex.usagemetering.UsageMonitoringReport;
import com.elitecore.netvertex.usagemetering.UsageMonitoringSupport;


/**
 * @author harsh patel
 * 
 * {@link TgppR9Builder} build {@link UsageMonitoringInfo} from Usage-Monitoring-Info AVP (10415:1067) and vice versa
 *
 */
public class TgppR9Builder implements UMBuilder {
	
	private static final String MODULE = "TGPP-UM-BUILDER";

	@Override
	public List<UsageMonitoringInfo> buildUsageMonitoringInfo(DiameterPacket diameterPacket) {
		
		
		List<IDiameterAVP> monitoringInfoAVPs = diameterPacket.getAVPList(DiameterAVPConstants.TGPP_USAGE_MONITORING_INFORMATION);
		
		if(monitoringInfoAVPs == null || monitoringInfoAVPs.isEmpty()){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Unable to set UsageMonitoringInfo.Reason: Usage-Monitoring-Info AVP not found from packet");
			return null;
		}
		
		//FIXME discuss about the behavior, null or list with atleast one value should be returns
		List<UsageMonitoringInfo> usageMonitoringInfos = new ArrayList<UsageMonitoringInfo>(monitoringInfoAVPs.size());

		for(IDiameterAVP monitoringInfoAVP : monitoringInfoAVPs){
			AvpGrouped usedServiceUnitsAVP = (AvpGrouped)((AvpGrouped) monitoringInfoAVP).getSubAttribute(DiameterAVPConstants.USED_SERVICE_UNIT);
			
			if(usedServiceUnitsAVP == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Unable to set UsageMonitoringInfo for Monitoring-Key: " + 
					((AvpGrouped) monitoringInfoAVP).getSubAttribute(DiameterAVPConstants.TGPP_USAGE_MONITORING_KEY) 
					+ ".Reason: Used-Service-Unit not found from the Usage-Monitoring-Info AVP");
				continue;
			}
			
			UsageMonitoringInfo monitoringInfo = new UsageMonitoringInfo();
			
			IDiameterAVP monitoringLevel = ((AvpGrouped) monitoringInfoAVP).getSubAttribute(DiameterAVPConstants.TGPP_USAGE_MONITORING_LEVEL);
			if(monitoringLevel != null){
				monitoringInfo.setUsageMonitoringLevel(UMLevel.getMonitoringLevelMap((int)monitoringLevel.getInteger()));
			} else {
				monitoringInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
			}
			
			
			if(monitoringInfo.getUsageMonitoringLevel() == UMLevel.SESSION_LEVEL){
				monitoringInfo.setMonitoringKey(diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
			} else {
				IDiameterAVP monitoringKey = ((AvpGrouped) monitoringInfoAVP).getSubAttribute(DiameterAVPConstants.TGPP_USAGE_MONITORING_KEY);
				if(monitoringKey == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Unable to set UsageMonitoringInfo .Reason: Monitoring-Key not found from the PCC Rule Level Usage-Monitoring-Info AVP");
					continue;
				}
				monitoringInfo.setMonitoringKey(monitoringKey.getStringValue());
				
			}
				
			IDiameterAVP monitoringReport = ((AvpGrouped) monitoringInfoAVP).getSubAttribute(DiameterAVPConstants.TGPP_USAGE_MONITORING_REPORT);
			if(monitoringReport != null){
				monitoringInfo.setUsageMonitoringReport(UsageMonitoringReport.getMonitoringReportMap((int)monitoringReport.getInteger()));
			}
				
			
			IDiameterAVP monitoringSupport = ((AvpGrouped) monitoringInfoAVP).getSubAttribute(DiameterAVPConstants.TGPP_USAGE_MONITORING_SUPPORT);
			if(monitoringSupport != null){
				monitoringInfo.setUsageMonitoringSupport(UsageMonitoringSupport.getMonitoringSupportMap((int)monitoringSupport.getInteger()));
			}
			
			IDiameterAVP tempDiameterAVP = null;
			
			long totalOctets = 0 , usedTime = 0 ,  inputOctets = 0 , outputOctets = 0 ;
			
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
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Skipping Usage Information building. " +
					"Reason: Reported total Octets and time is ZERO");
				}
				continue;
			}
			
			ServiceUnit usedServiceUnit = new ServiceUnit();
			usedServiceUnit.setTotalOctets(totalOctets);
			usedServiceUnit.setTime(usedTime);
			usedServiceUnit.setInputOctets(inputOctets);
			usedServiceUnit.setOutputOctets(outputOctets);
			
			monitoringInfo.setUsedServiceUnit(usedServiceUnit);
		
		
			usageMonitoringInfos.add(monitoringInfo);
		}
		
		return usageMonitoringInfos;
	
	}

	@Override
	public List<IDiameterAVP> buildUsageMonitoringAVP(PCRFResponse pcrfResponse) {
		
		List<UsageMonitoringInfo> usageMonitoringInfos = pcrfResponse.getUsageMonitoringInfoList();
		
		
		if(usageMonitoringInfos == null || usageMonitoringInfos.isEmpty()){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Unable to build Usage-Monitoring-Info AVPs.Reason: UsageMonitoringInfo list not found from PCRFResponse");
			return null;
		}

		ArrayList<IDiameterAVP> usageMonInfos = new ArrayList<IDiameterAVP>(usageMonitoringInfos.size());
		for(UsageMonitoringInfo monitoringInfo : usageMonitoringInfos){
			
			if(monitoringInfo.getMonitoringKey() == null || monitoringInfo.getMonitoringKey().length() == 0){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Unable to build Usage-Monitoring-Info AVP .Reason: Monitoring-Key not found");
				continue;
			}
			
			AvpGrouped usageMonInfo = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.TGPP_USAGE_MONITORING_INFORMATION);
			
			if(monitoringInfo.getMonitoringKey() != null && monitoringInfo.getMonitoringKey().length() > 0){
				IDiameterAVP monitoringKey = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.TGPP_USAGE_MONITORING_KEY);
				monitoringKey.setStringValue(monitoringInfo.getMonitoringKey());
				usageMonInfo.addSubAvp(monitoringKey);
			}
			
			if(monitoringInfo.getUsageMonitoringLevel() != null){
				IDiameterAVP monitoringLevel = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.TGPP_USAGE_MONITORING_LEVEL);
				monitoringLevel.setInteger(monitoringInfo.getUsageMonitoringLevel().getVal());
				usageMonInfo.addSubAvp(monitoringLevel);
			}
			
			if(monitoringInfo.getUsageMonitoringReport() != null){
				IDiameterAVP monitoringReport = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.TGPP_USAGE_MONITORING_REPORT);
				monitoringReport.setInteger(monitoringInfo.getUsageMonitoringReport().getVal());
				usageMonInfo.addSubAvp(monitoringReport);
			}
			
			if(monitoringInfo.getUsageMonitoringSupport() != null){
				IDiameterAVP monitoringSupport = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.TGPP_USAGE_MONITORING_SUPPORT);
				monitoringSupport.setInteger(monitoringInfo.getUsageMonitoringSupport().getVal());
				usageMonInfo.addSubAvp(monitoringSupport);
			}
			
			
			
			if(monitoringInfo.getGrantedServiceUnit() != null){
				IDiameterAVP grantedUnits = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.GRANTED_SERVICE_UNIT);
				if(monitoringInfo.getGrantedServiceUnit().getOutputOctets() > 0){
					IDiameterAVP outputOctets = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CC_OUTPUT_OCTETS);
					outputOctets.setInteger(monitoringInfo.getGrantedServiceUnit().getOutputOctets());
					((AvpGrouped)grantedUnits).addSubAvp(outputOctets);
				}
				if(monitoringInfo.getGrantedServiceUnit().getInputOctets() > 0){
					IDiameterAVP inputOctets = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CC_INPUT_OCTETS);
					inputOctets.setInteger(monitoringInfo.getGrantedServiceUnit().getInputOctets());
					((AvpGrouped)grantedUnits).addSubAvp(inputOctets);
				}
				if(monitoringInfo.getGrantedServiceUnit().getTotalOctets() > 0){
					IDiameterAVP totalOctets = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CC_TOTAL_OCTETS);
					totalOctets.setInteger(monitoringInfo.getGrantedServiceUnit().getTotalOctets());
					((AvpGrouped)grantedUnits).addSubAvp(totalOctets);
				}
				if(monitoringInfo.getGrantedServiceUnit().getTime() > 0){
					IDiameterAVP time = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CC_TIME);
					time.setInteger(monitoringInfo.getGrantedServiceUnit().getTime());
					((AvpGrouped)grantedUnits).addSubAvp(time);
				}
				usageMonInfo.addSubAvp(grantedUnits);
			}
			
			
			usageMonInfos.add(usageMonInfo);
		
			
		}
		
		return usageMonInfos;
	}
	
}
