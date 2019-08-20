package com.elitecore.netvertex.gateway.radius.conf.impl;

import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.util.constants.Vendor;
import com.elitecore.corenetvertex.commons.Predicate;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.data.ScriptData;
import com.elitecore.netvertex.core.data.ServiceGuide;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayProfileConfiguration;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadApplicationPacketType;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMapping;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Harsh Patel
 *
 */

public class RadiusGatewayProfileConfigurationImpl implements RadiusGatewayProfileConfiguration {
	private final GatewayComponent gatewayType;
	private final List<ServiceGuide> serviceGuides;
	private String profileId;
    private CommunicationProtocol commProtocol;
    private Vendor vendor;
    private String firmware;
    
    private boolean isAccountingResponseEnable;
    private String description;
    private String profileName;
    private String supportedVendors;
    private PCRFKeyValueConstants usageReportingType;
	private int timeout;
	private int maxRequestTimeout;
	private int statusCheckDuration;
	private boolean isICMPPingEnabled;
	private int retryCount;
	private int interimIntervalInMin;
	private PCRFKeyValueConstants revalidationMode;
    private Predicate<PCRFRequest, SessionData> interimIntervalPredicate;
    private Map<RadApplicationPacketType, PCCToRadiusMapping> pccToRADIUSPacketMappings;
    private Map<RadApplicationPacketType, RadiusToPCCMapping> radiusToPCCPacketMappings;

	private List<ScriptData> scriptDatas;

	 public RadiusGatewayProfileConfigurationImpl(String profileId, CommunicationProtocol commProtocol, Vendor vendor, String firmware,
												  boolean isAccountingResponseEnable, String description, String profileName,
												  String supportedVendors, PCRFKeyValueConstants usageReportingType, int timeout, int maxRequestTimeout, int statusCheckDuration,
												  boolean isICMPPingEnabled, int retryCount, int interimIntervalInMin, PCRFKeyValueConstants revalidationMode,
												  Predicate<PCRFRequest, SessionData> interimIntervalPredicate,
												  GatewayComponent gatewayType,
												  Map<RadApplicationPacketType, PCCToRadiusMapping> pccToRADIUSPacketMappings,
												  Map<RadApplicationPacketType, RadiusToPCCMapping> radiusToPCCPacketMappings,
												  List<ScriptData> scriptDatas, List<ServiceGuide> serviceGuides) {
			super();
			this.profileId = profileId;
			this.commProtocol = commProtocol;
			this.vendor = vendor;
			this.firmware = firmware;
			this.isAccountingResponseEnable = isAccountingResponseEnable;
			this.description = description;
			this.profileName = profileName;
			this.supportedVendors = supportedVendors;
			this.usageReportingType = usageReportingType;
			this.timeout = timeout;
			this.maxRequestTimeout = maxRequestTimeout;
			this.statusCheckDuration = statusCheckDuration;
			this.isICMPPingEnabled = isICMPPingEnabled;
			this.retryCount = retryCount;
			this.interimIntervalInMin = interimIntervalInMin;
			this.revalidationMode = revalidationMode;
			this.interimIntervalPredicate = interimIntervalPredicate;
		 	this.gatewayType = gatewayType;
			this.pccToRADIUSPacketMappings = pccToRADIUSPacketMappings;
			this.radiusToPCCPacketMappings = radiusToPCCPacketMappings;
			this.scriptDatas = scriptDatas;
			this.serviceGuides = serviceGuides;
		}
	
    @Override

    public boolean isAccountingResponseEnable() {
		return isAccountingResponseEnable;
	}


    @Override
    public String getProfileId() {
        return profileId;
    }


    @Override
    public Vendor getVendor() {
        return vendor;
    }

    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public PCRFKeyValueConstants getUsageReportingType() {
    	return usageReportingType;
    }
    

	@Override
	public int getMaxRequestTimeout() {
		return maxRequestTimeout;
	}
	
	@Override
	public int getTimeout() {
		return timeout;
	}
	
	@Override
	public int getRetryCount() {
		return retryCount;
	}

	@Override
	public int getStatusCheckDuration() {
		return statusCheckDuration;
	}

	@Override
	public boolean isICMPPingEnabled() {
		return isICMPPingEnabled;
	}
	
	@Override
	public PCRFKeyValueConstants getRevalidationMode() {
		return revalidationMode;
	}
	
    @Override
    public String toString(){
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- Radius Gateway Profile --");
		toString(builder);
		return builder.toString();
    }


	@Override
	public List<ScriptData> getScriptsConfigs() {
		return scriptDatas;
	}

	@Override
	public List<ServiceGuide> getServiceGuides(){
	 	return serviceGuides;
	}

	@Override
	public boolean isPCCLevelMonitoringSupported() {
		return false;
	}

    @Override
    public Predicate<PCRFRequest, SessionData> getInterimIntervalPredicate() {
        return interimIntervalPredicate;
    }

    @Override
	public GatewayComponent getGatewayType(){
		return gatewayType;
	}

	@Override
	public RadiusToPCCMapping getARMappings() {
		return radiusToPCCPacketMappings.get(RadApplicationPacketType.AR);
	}

	@Override
	public PCCToRadiusMapping getAAMappings() {
		return pccToRADIUSPacketMappings.get(RadApplicationPacketType.AA);
	}

	@Override
	public PCCToRadiusMapping getCOAMappings() {
		return pccToRADIUSPacketMappings.get(RadApplicationPacketType.COA);
	}

	@Override
	public RadiusToPCCMapping getACRMappings() {
		return radiusToPCCPacketMappings.get(RadApplicationPacketType.ACR);
	}

	@Override
	public PCCToRadiusMapping getDCRMappings() {
		return pccToRADIUSPacketMappings.get(RadApplicationPacketType.DCR);
	}

	@Override
	public Map<RadApplicationPacketType, PCCToRadiusMapping> getPCCToRADIUSPacketMappings() {
		return pccToRADIUSPacketMappings;
	}

	@Override
	public Map<RadApplicationPacketType, RadiusToPCCMapping> getRadiusToPCCPacketMappings() {
		return radiusToPCCPacketMappings;
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.append("Profile Id", profileId)
				.append("Profile Name", profileName)
				.append("Communication Protocol", commProtocol.getName())
				.append("Vendor", vendor.getName())
				.append("Firmware", firmware)
				.append("Description", description)
				.append("Supported Vendors", supportedVendors)
				.append("Retry Count", retryCount)
				.append("Timeout", timeout)
				.append("Max Request Timeout", maxRequestTimeout)
				.append("Status Check Duration", statusCheckDuration)
				.append("Is ICMP Ping Enabled", isICMPPingEnabled)
				.append("Usage Reporting Type", usageReportingType.val)
				.append("Revalidation Mode", revalidationMode)
				.append("Interim Interval", interimIntervalInMin)
				.appendChildObject("Scripts -- ", scriptDatas);

		if(serviceGuides.isEmpty()==false){
			builder.newline();
			builder.appendHeading("Service Guiding");
			builder.incrementIndentation();
			for (ServiceGuide serviceGuide : this.getServiceGuides()) {
				builder.append("Service", serviceGuide.getServiceId());
				builder.append("Condition", serviceGuide.getConditionStr());
				builder.newline();
			}
			builder.decrementIndentation();
		}
	}

}

