package com.elitecore.netvertex.gateway.diameter.conf;

import com.elitecore.core.util.constants.Vendor;
import com.elitecore.corenetvertex.constants.*;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;
import com.elitecore.netvertex.core.data.ScriptData;
import com.elitecore.netvertex.core.data.ServiceGuide;
import com.elitecore.netvertex.gateway.diameter.mapping.ApplicationPacketType;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;

import java.util.List;
import java.util.Map;


/**
 * This interface provide the configure Diameter gateway profile information
 * @author Harsh Patel
 *
 */
public interface DiameterGatewayProfileConfiguration extends ToStringable {

	/**
	 * 
	 * @return int
	 */
	public String getProfileId();
	
	/**
	 * 
	 * @return String
	 */
	public String getProfileName();
	
	/**
	 * 
	 * @return CommunicationProtocol
	 */
	public CommunicationProtocol getCommProtocol();
	
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
	
	/**
	 * 
	 * @return String
	 */
	public String getSupportedVendorList();
	
	public SupportedStandard getSupportedStandard();
	
	public ChargingRuleInstallMode getChargingRuleInstallMode();

	public int getTimeout();
	

	public PCRFKeyValueConstants getUsageReportingType();
	

	public long getGxApplicationId();
	
	public long getRxApplicationId();
	
	public long getGxVendorId();
	
	public long getRxVendorId();
	
	public int getInitConnectionDuration();
	

	public int getDWInterval();
           
	public GatewayComponent getGatewayType();
	       
	public long getGyApplicationId();
           

	public long getGyVendorId();
	
	long getSyApplicationId();

	long getSyVendorId();

	long getS9ApplicationId();
	long getS9VendorId();
	
	public boolean isSessCleanupOnCER();
	
	public boolean isSessCleanupOnDPR();
	
	public boolean isSendDPROnCloseEvent();
	
	public String getAdditionalCERAVPs();
	
	public String getAdditionalDPRAVPs();
	
	public int getSocketReceiveBufferSize();
	
	public int getSocketSendBufferSize();
	
	boolean isTCPNagleAlgo();

	String getAdditionalDWRAVPs();

	TransportProtocols getTransportProtocol();

	public PCRFKeyValueConstants getRevalidationMode();

	List<ScriptData> getScriptsConfigs();
	
	UMStandard getUsageMeteringStandard();
	
	public List<String> getSessionLookupKey();

	public boolean isPCCLevelMonitoringSupported();

	SessionTypeConstant getSessionType(long applicationId);

	void setusageReportingType(PCRFKeyValueConstants usageReporting);

	PCCToDiameterMapping getGxCCAMappings();

	PCCToDiameterMapping getGyCCAMappings();

	PCCToDiameterMapping getGxRARMappings();

	PCCToDiameterMapping getGyRARMappings();

	PCCToDiameterMapping getSNAMappings();

	PCCToDiameterMapping getSLRMappings();

	PCCToDiameterMapping getSTAMappings();

	PCCToDiameterMapping getASRMappings();

	PCCToDiameterMapping getAAAMappings();

	DiameterToPCCMapping getGxCCRMappings();

	DiameterToPCCMapping getGyCCRMappings();

	DiameterToPCCMapping getGxRAAMappings();

	DiameterToPCCMapping getGyRAAMappings();

	DiameterToPCCMapping getAARMappings();

	DiameterToPCCMapping getASAMappings();

	DiameterToPCCMapping getSLAMappings();

	DiameterToPCCMapping getSNRMappings();

	DiameterToPCCMapping getSTRMappings();

    PCCToDiameterMapping getSySTRMapping();

	Map<ApplicationPacketType, PCCToDiameterMapping> getPCCToDiameterPacketMappings();

	Map<ApplicationPacketType, DiameterToPCCMapping> getDiameterToPCCPacketMappings();

	List<ServiceGuide> getServiceGuides();
}
