
package com.elitecore.netvertex.gateway.diameter.conf;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.elitecore.core.util.constants.Vendor;
import com.elitecore.corenetvertex.constants.*;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;
import com.elitecore.netvertex.core.data.ScriptData;
import com.elitecore.netvertex.core.data.ServiceGuide;
import com.elitecore.netvertex.gateway.diameter.mapping.ApplicationPacketType;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;


/**
 * This method provide the configure diameter gateway information
 * @author Harsh Patel
 *
 */
public interface DiameterGatewayConfiguration extends ToStringable {



	/**
	 * 
	 * @return int
	 */
	public String getGatewayId();
	
	/**
	 * 
	 * @return CommunicationProtocol
	 */
	public CommunicationProtocol getCommProtocolId();
	
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
	
	/**
	 * 
	 * @return String
	 */
	public String getHostIdentity();
	
	/**
	 * 
	 * @return String
	 */
	public String getRealm();
	
	/**
	 * 
	 * @return int
	 */
	public int getTimeout();
	

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
	
	/**
	 * 
	 * @return <code>String</code>
	 */
	public String getHostIPAddress();
	
	public long getGxApplicationId();
	
	public long getRxApplicationId();
	
	public long getGxVendorId();
	
	public long getRxVendorId();
	
	public int getInitConnectionDuration();
	
	int getDWInterval();

	GatewayComponent getGatewayType();
	
	public SupportedStandard getSupportedStandard();

	long getGyApplicationId();

	long getGyVendorId();
	
	long getSyApplicationId();

	long getSyVendorId();

	public PCRFKeyValueConstants getUsageReportingType();

	PCRFKeyValueConstants getRevalidationMode();

	ChargingRuleInstallMode getChargingRuleInstallMode();

	InetAddress getHostInetAddress();
	PolicyEnforcementMethod getEnforcementMethod();
	
	public boolean isSessCleanupOnCER();
	
	public boolean isSessCleanupOnDPR();
	
	public boolean isSendDPROnCloseEvent();
	
	boolean isTCPNagalAlgo();
	
	public String getAdditionalCERAVPs();
	
	public String getAdditionalDPRAVPs();
	
	public int getSocketReceiveBufferSize();
	
	public int getSocketSendBufferSize();

	int getLocalPort();

	String getLocalIPAddress();

	int getHostPort();
	
	String getAdditionalDWRAVPs();
	
	TransportProtocols getTransportProtocol();

	String getName();
	
	List<ScriptData> getScriptsConfigs();
	
	long getRequestTimeout();
	
	int getRetransmissionCount();
	
	UMStandard getUsageMeteringStandard();
	
	public List<String> getSessionLookupKey();

	public boolean isPCCLevelMonitoringSupported();

	String getAlternateHostName();

	@Nullable DiameterPeerGroupParameter getDiameterPeerGroupParameter();

	@Nonnull SessionTypeConstant getSessionType(long applicationId);

	void setProfileId(String profileId);

	void setDescription(String description);

	void setEnforcementMethod(PolicyEnforcementMethod policyEnforcementMethod);

	PCCToDiameterMapping getGxCCAMappings();

	PCCToDiameterMapping getGyCCAMappings();

	PCCToDiameterMapping getGxRARMappings();

	PCCToDiameterMapping getGyRARMappings();

	PCCToDiameterMapping getSNAMappings();

	PCCToDiameterMapping getSLRMappings();

	PCCToDiameterMapping getSTAMappings();

	PCCToDiameterMapping getASRMappings();

	DiameterToPCCMapping getGxCCRMappings();

	DiameterToPCCMapping getGyCCRMappings();

	DiameterToPCCMapping getGxRAAMappings();

	DiameterToPCCMapping getGyRAAMappings();

	DiameterToPCCMapping getAARMappings();

	DiameterToPCCMapping getASAMappings();

	DiameterToPCCMapping getSLAMappings();

	DiameterToPCCMapping getSNRMappings();

	DiameterToPCCMapping getSTRMappings();

    PCCToDiameterMapping getAAAMappings();

	PCCToDiameterMapping getSySTRMapping();

	long getS9ApplicationId();

	long getS9VendorId();

	Map<ApplicationPacketType, PCCToDiameterMapping> getPCCToDiameterPacketMappings();

	Map<ApplicationPacketType, DiameterToPCCMapping> getDiameterToPCCPacketMappings();

	List<ServiceGuide> getServiceGuides();
}
