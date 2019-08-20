package com.elitecore.corenetvertex.pm.pkg.factory;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.*;
import com.elitecore.corenetvertex.core.constant.ChargingModes;
import com.elitecore.corenetvertex.pkg.dataservicetype.ServiceDataFlowData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleType;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.pm.constants.UsageMetering;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class PCCRuleFactory {

	private static final String MODULE = "PCCRule-FCTRY";
	private static final String DYNAMIC_PCC = "DYNAMIC";
	private static final String STATIC_PCC = "STATIC";
	
	
	private PackageFactory packageFactory;
	private DataServiceTypeFactory dataServiceTypeFactory;


	public PCCRuleFactory(DataServiceTypeFactory dataServiceTypeFactory, PackageFactory packageFactory) {
		this.dataServiceTypeFactory = dataServiceTypeFactory;
		this.packageFactory = packageFactory;
	}
	@Nullable
	public PCCRule createPCCRule(PCCRuleData pccRuleData,
								 int fupLevel,
								 List<String> pccRuleFailReasons,
								 List<RatingGroupData> ratingGroups) {

		validatePCCRuleData(pccRuleData, pccRuleFailReasons);

		boolean dynamic = false;
		if (pccRuleData.getType().equalsIgnoreCase(PCCRuleType.STATIC.name())) {
			dynamic = false;
		} else if (pccRuleData.getType().equalsIgnoreCase(PCCRuleType.DYNAMIC.name())) {
			dynamic = true;
		}

		UsageMetering usageMetering = UsageMetering.DISABLE_QUOTA;
		if (pccRuleData.getUsageMonitoring()) {
			boolean isTimeEnabled = false;
			if (pccRuleData.getSliceTime() > 0) {
				isTimeEnabled = true;
				usageMetering = UsageMetering.TIME_QUOTA;
			}

			boolean isVolumeEnable = false;
			if (pccRuleData.getSliceTotal() > 0 || pccRuleData.getSliceDownload() > 0 || pccRuleData.getSliceUpload() > 0) {
				isVolumeEnable = true;
				usageMetering = UsageMetering.VOLUME_QUOTA;
			}

			if (isTimeEnabled && isVolumeEnable) {
				usageMetering = UsageMetering.TIME_VOLUME_QUOTA;
			}

		}


		DataServiceType dataServiceType = null;
		if(pccRuleData.getDataServiceTypeData() != null) {
			dataServiceType = dataServiceTypeFactory.createServiceType(pccRuleData.getDataServiceTypeData());
		}

		RatingGroupData ratingGroupData = getRatingGroupData(ratingGroups, pccRuleData.getChargingKey());

		if (ratingGroupData == null) {
			pccRuleFailReasons.add("Rating group not found for charging id:" + pccRuleData.getChargingKey());
		}

		if (pccRuleFailReasons.isEmpty() == false) {
			return null;
		}

		SliceInformation sliceInformation = null;
		if (UsageMetering.DISABLE_QUOTA != usageMetering) {
			sliceInformation = new SliceInformation(getDataInBytes(pccRuleData.getSliceTotal(), pccRuleData.getSliceTotalUnit())
					, getDataInBytes(pccRuleData.getSliceUpload(), pccRuleData.getSliceUploadUnit())
					, getDataInBytes(pccRuleData.getSliceDownload(), pccRuleData.getSliceDownloadUnit())
					, getTimeInSeconds(pccRuleData.getSliceTime(), pccRuleData.getSliceTimeUnit()));
		}

		PriorityLevel priorityLevel = PriorityLevel.fromVal(pccRuleData.getArp());
		QCI qci = QCI.fromId(pccRuleData.getQci());

		return packageFactory.createPCCRule(
				pccRuleData.getId()
				, pccRuleData.getName()
				, pccRuleData.getPrecedence() == null ? 999 : pccRuleData.getPrecedence()
				, ratingGroupData.getIdentifier()
				, ratingGroupData.getId()
				, pccRuleData.getAppServiceProviderId()
				, pccRuleData.getSponsorIdentity()
				, usageMetering
				, qci == null ? QCI.QCI_NON_GBR_9 : qci
				, getQoSInBps(pccRuleData.getGbrdl(), pccRuleData.getGbrdlUnit())
				, getQoSInBps(pccRuleData.getGbrul(), pccRuleData.getGbrulUnit())
				, getQoSInBps(pccRuleData.getMbrdl(), pccRuleData.getMbrdlUnit())
				, getQoSInBps(pccRuleData.getMbrul(), pccRuleData.getMbrulUnit())
				, QoSUnit.fromVal(pccRuleData.getGbrdlUnit())
				, QoSUnit.fromVal(pccRuleData.getGbrulUnit())
				, QoSUnit.fromVal(pccRuleData.getMbrdlUnit())
				, QoSUnit.fromVal(pccRuleData.getMbrulUnit())
				, pccRuleData.getGbrdl()
				, pccRuleData.getGbrul()
				, pccRuleData.getMbrdl()
				, pccRuleData.getMbrul()
				, priorityLevel
				, pccRuleData.getPreCapability()
				, pccRuleData.getPreVulnerability()
				, dynamic
				, createServiceDataFlows(pccRuleData.getServiceDataFlowList())
				, ChargingModes.fromValue(pccRuleData.getChargingMode())
				, FlowStatus.fromValue(pccRuleData.getFlowStatus())
				, pccRuleData.getMonitoringKey()
				, dataServiceType.getServiceIdentifier()
				, dataServiceType.getName()
				, dataServiceType.getDataServiceTypeID()
				, sliceInformation, fupLevel);

	}
	
	private static long getDataInBytes(Long dataQuantity, String dataUnit) {

		if (dataQuantity == null) {
			return 0;
		}

		DataUnit usageUnit = DataUnit.valueOf(dataUnit);

		if (usageUnit != null) {
			return usageUnit.toBytes(dataQuantity);
		}

		return dataQuantity;
	}

	private static long getTimeInSeconds(Long time, String timeUnitStr) {

		if (time == null) {
			return 0;
		}

		TimeUnit timeUnit = TimeUnit.valueOf(timeUnitStr);

		if (timeUnit != null) {
			return timeUnit.toSeconds(time);
		}

		return time;
	}

	private static long getQoSInBps(long qos, String qosUnit) {

		if (qos == 0) {
			return qos;
		}

		QoSUnit unit = QoSUnit.fromVal(qosUnit);
		long bps;
		if (unit != null) {
			bps = unit.toBps(qos);
		} else {
			bps = qos;
		}

		/*
		 * while calculating bits per seconds, it may possible that bps cross
		 * the max value long value. In that case it value will be less than
		 * zero
		 */
		if (bps > CommonConstants.UNSIGNED32_MAX_VALUE) {
			bps = CommonConstants.UNSIGNED32_MAX_VALUE;
		}

		return bps;
	}
	
	private static List<String> createServiceDataFlows(List<ServiceDataFlowData> serviceDataFlowSet) {

		List<String> serviceDataFlows = new ArrayList<String>();
		for (ServiceDataFlowData serviceDataFlowData : serviceDataFlowSet) {

			serviceDataFlows.add(serviceDataFlowData.getFlowAccess()
					+ " "
					+ (serviceDataFlowData.getProtocol() == null ? "ip" : serviceDataFlowData.getProtocol()) + " from "
					+ (serviceDataFlowData.getSourceIP() == null ? "any" : serviceDataFlowData.getSourceIP())
					+ (serviceDataFlowData.getSourcePort() == null ? "" : ":" + serviceDataFlowData.getSourcePort())
					+ " to "
					+ (serviceDataFlowData.getDestinationIP() == null ? "any" : serviceDataFlowData.getDestinationIP())
					+ (serviceDataFlowData.getDestinationPort() == null ? "" : ":" + serviceDataFlowData.getDestinationPort())
					);

		}
		return serviceDataFlows;
	}
	
	private static void validatePCCRuleData(PCCRuleData pccRuleData, List<String> failReasons) {

		if (Strings.isNullOrBlank(pccRuleData.getMonitoringKey())) {
			failReasons.add("No monitoring key configured for pcc rule: " + pccRuleData.getName());
		}

		if (pccRuleData.getType() == null
				|| (pccRuleData.getType().equalsIgnoreCase(STATIC_PCC) || pccRuleData.getType().equalsIgnoreCase(DYNAMIC_PCC)) == false) {
			getLogger().warn(MODULE, "Taking default pcc rule type: " + STATIC_PCC);
			pccRuleData.setType(STATIC_PCC);
		}

		if (pccRuleData.getQci() == null) {
			getLogger().warn(MODULE, "Taking default pcc rule qci: 9");
			pccRuleData.setQci((byte) 9);
		}

		if (pccRuleData.getChargingMode() == null || ChargingModes.fromValue(pccRuleData.getChargingMode().intValue()) == null) {
			getLogger().warn(MODULE, "Taking default pcc rule charging mode: " + ChargingModes.NONE.val);
			pccRuleData.setChargingMode((byte) ChargingModes.NONE.val);
		}

		if (pccRuleData.getDataServiceTypeData() == null) {
			failReasons.add("No data service type configured for pcc rule: " + pccRuleData.getName());
		}

		if (pccRuleData.getMbrdl() == null) {
			pccRuleData.setMbrdl((long) 0);
		} else if (pccRuleData.getMbrdl() < 0) {
			failReasons.add("MBRDL value is negative(" + pccRuleData.getMbrdl() + ")");
		}

		if (pccRuleData.getMbrul() == null) {
			pccRuleData.setMbrul((long) 0);
		} else if (pccRuleData.getMbrul() < 0) {
			failReasons.add("MBRUL value is negative(" + pccRuleData.getMbrul() + ")");
		}

		if (pccRuleData.getGbrdl() == null) {
			pccRuleData.setGbrdl((long) 0);
		} else if (pccRuleData.getGbrdl() < 0) {
			failReasons.add("GBRDL value is negative(" + pccRuleData.getGbrdl() + ")");
		}

		if (pccRuleData.getGbrul() == null) {
			pccRuleData.setGbrul((long) 0);
		} else if (pccRuleData.getGbrul() < 0) {
			failReasons.add("GBRUL value is negative(" + pccRuleData.getGbrul() + ")");
		}

		if (pccRuleData.getArp() == null) {
			pccRuleData.setArp((byte)PriorityLevel.PRIORITY_LEVEL_10.val);
		}

		if (pccRuleData.getPreCapability() == null) {
			pccRuleData.setPreCapability(false);
		}

		if (pccRuleData.getPreVulnerability() == null) {
			pccRuleData.setPreVulnerability(false);
		}

		if (pccRuleData.getUsageMonitoring() == null) {
			pccRuleData.setUsageMonitoring(false);
		}

		if (pccRuleData.getSliceTotal() == null) {
			pccRuleData.setSliceTotal((long) 0);
		} else if (pccRuleData.getUsageMonitoring() && pccRuleData.getSliceTotal() < 0) {
			failReasons.add("Slice total value is negative(" + pccRuleData.getSliceTotal() + ")");
		}

		if (pccRuleData.getSliceUpload() == null) {
			pccRuleData.setSliceUpload((long) 0);
		} else if (pccRuleData.getUsageMonitoring() && pccRuleData.getSliceUpload() < 0) {
			failReasons.add("Slice upload value is negative(" + pccRuleData.getSliceUpload() + ")");
		}

		if (pccRuleData.getSliceDownload() == null) {
			pccRuleData.setSliceDownload((long) 0);
		} else if (pccRuleData.getUsageMonitoring() && pccRuleData.getSliceDownload() < 0) {
			failReasons.add("Slice download value is negative(" + pccRuleData.getSliceDownload() + ")");
		}

		if (pccRuleData.getSliceTime() == null) {
			pccRuleData.setSliceTime(0l);
		} else if (pccRuleData.getUsageMonitoring() && pccRuleData.getSliceTime() < 0l) {
			failReasons.add("Slice time value is negative(" + pccRuleData.getSliceTime() + ")");
		}

		if (pccRuleData.getUsageMonitoring()) {
			if (pccRuleData.getSliceTotal() <= 0 &&
					pccRuleData.getSliceUpload() <= 0 &&
					pccRuleData.getSliceDownload() <= 0 &&
					pccRuleData.getSliceTime() <= 0) {

				failReasons.add("Slice infomation not provide or configured as 0 or negative");
			}

		}

		FlowStatus flowStatus = null;
		if (pccRuleData.getFlowStatus() == null) {
			getLogger().warn(MODULE, "Considering flow status as " + FlowStatus.ENABLED
					+ ". Reason: No flow status configured");
			pccRuleData.setFlowStatus((byte)FlowStatus.ENABLED.val);

		} else {

			flowStatus = FlowStatus.fromValue(pccRuleData.getFlowStatus());
			if (flowStatus == null) {
				getLogger().warn(MODULE, "Considering flow status as " + FlowStatus.ENABLED
						+ ". Reason: Invalid value:" + pccRuleData.getFlowStatus() + " configured");
				pccRuleData.setFlowStatus((byte)FlowStatus.ENABLED.val);
			}
		}
	}

	/**
	 * Used for creating Rating Group for PCCRule (based on charging key)
	 *
	 * @param ratingGroups
	 * @param chargingKey
	 * @return
	 */
	public RatingGroupData getRatingGroupData(List<RatingGroupData> ratingGroups, String chargingKey) {

		if(ratingGroups == null) {
			return null;
		}

		for (RatingGroupData ratingGroupData : ratingGroups) {

			if (ratingGroupData.getId().equals(chargingKey)) {
				return ratingGroupData;
			}
		}

		return null;
	}
}
