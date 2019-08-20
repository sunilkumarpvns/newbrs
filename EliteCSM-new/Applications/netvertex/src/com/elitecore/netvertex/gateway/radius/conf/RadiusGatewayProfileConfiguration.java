package com.elitecore.netvertex.gateway.radius.conf;

import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.util.constants.Vendor;
import com.elitecore.corenetvertex.commons.Predicate;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.core.data.ScriptData;
import com.elitecore.netvertex.core.data.ServiceGuide;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadApplicationPacketType;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMapping;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import java.util.List;
import java.util.Map;

/**
 * This interface provides the configure redius gateway profile information
 * @author Harsh Patel
 *
 */
public interface RadiusGatewayProfileConfiguration extends ToStringable{

	public String getProfileId();

	
	/**
	 * 
	 * @return Vendor
	 */
	public Vendor getVendor();

	
	/**
	 * 
	 * @return String
	 */
	public String getDescription();



	public PCRFKeyValueConstants getUsageReportingType();

	public boolean isAccountingResponseEnable();
	public PCRFKeyValueConstants getRevalidationMode();
	
	public int getTimeout();
	public int getMaxRequestTimeout();
	public int getRetryCount();
	public int getStatusCheckDuration();
	public boolean isICMPPingEnabled();

	public List<ScriptData> getScriptsConfigs();

	public boolean isPCCLevelMonitoringSupported();

    public Predicate<PCRFRequest, SessionData> getInterimIntervalPredicate();

	GatewayComponent getGatewayType();

	public RadiusToPCCMapping getARMappings();

	public PCCToRadiusMapping getAAMappings();

	public PCCToRadiusMapping getCOAMappings();

	public RadiusToPCCMapping getACRMappings();

	public PCCToRadiusMapping getDCRMappings();

	public Map<RadApplicationPacketType, PCCToRadiusMapping> getPCCToRADIUSPacketMappings();

	public Map<RadApplicationPacketType, RadiusToPCCMapping> getRadiusToPCCPacketMappings();

	List<ServiceGuide> getServiceGuides();

}
