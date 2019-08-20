package com.elitecore.netvertex.gateway.radius.conf;

import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.util.constants.Vendor;
import com.elitecore.corenetvertex.commons.Predicate;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyEnforcementMethod;
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
 * This interface provides the configure redius gateway information
 * @author Harsh Patel
 *
 */
public interface RadiusGatewayConfiguration extends ToStringable{

	/**
	 * 
	 * @return int
	 */
	public String getGatewayId();
	/**
	 * 
	 * @return String
	 */
	public String getDescription();
	
	
	/**
	 * 
	 * @return String
	 */
	public String getConnectionURL();
	
	public String getIPAddress();
	
	public int getPort();
	
	/**
	 * 
	 * @return String
	 */
	public String getSharedSecret();
	
	/**
	 * 
	 * @return int
	 */
	public int getTimeout();
	
	/**
	 * 
	 * @return int
	 */
	public int getMaxRequestTimeout();
	
	/**
	 * 
	 * @return int
	 */
	public String getProfileId();

	
	/**
	 * 
	 * @return Vendor
	 */
	public Vendor getVendor();



	public int getMinimumLocalPort();
	
	/**
	 * Retry Count is total number of retry attempts to send a request to any External Radius. 
	 * @return int
	 */
	public int getRetryCount();
	/**
	 * Status Check Duration parameter refers to the duration after which 
	 * NetVertex server should check whether the connectivity with the External Radius is available or not. 
	 * @return
	 */
	public int getStatusCheckDuration();
	
	/**
	 * This parameter is used when Status Server message is Timeout.
	 * TRUE  : ICMP message will be sent to Gateway and if it is reachable then we will mark this gateway as ALIVE. 
	 * FALSE : ICMP message will not be sent to Gateway and gateway marked as dead.
	 * @return
	 */
	public boolean isICMPPingEnabled();

	public PCRFKeyValueConstants getRevalidationMode();

	public PCRFKeyValueConstants getUsageReportingType();

	public boolean isAccountingResponseEnable();
	

	PolicyEnforcementMethod getPolicyEnforcementMethod();
	String getName();
	List<ScriptData> getScriptsConfigs();
	public boolean isPCCLevelMonitoringSupported();
	public Predicate<PCRFRequest, SessionData> getInterimIntervalPredicate();
	void setIpAddress(String ipAddress);
	void setDescription(String description);
	RadiusToPCCMapping getARMappings();

	PCCToRadiusMapping getAAMappings();

	PCCToRadiusMapping getCOAMappings();

	RadiusToPCCMapping getACRMappings();

	PCCToRadiusMapping getDCRMappings();

	Map<RadApplicationPacketType, PCCToRadiusMapping> getPCCToRADIUSPacketMappings();

	Map<RadApplicationPacketType, RadiusToPCCMapping> getRadiusToPCCPacketMappings();

	GatewayComponent getGatewayType();
	List<ServiceGuide> getServiceGuides();
}
