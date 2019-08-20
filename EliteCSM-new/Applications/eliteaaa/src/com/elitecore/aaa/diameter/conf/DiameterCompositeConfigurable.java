package com.elitecore.aaa.diameter.conf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.impl.AAAServerConfigurable;
import com.elitecore.aaa.core.conf.impl.ServerCertificateConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterCCServiceConfigurationImpl;
import com.elitecore.aaa.diameter.conf.impl.DiameterDriverCompositeConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterEAPServiceConfigurationImpl;
import com.elitecore.aaa.diameter.conf.impl.DiameterNASServiceConfigurationImpl;
import com.elitecore.aaa.diameter.conf.impl.DiameterPeerGroupConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterPeerProfileConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterPeersConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterRoutingTableConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterTGPPServerCompositeConfigurable;
import com.elitecore.aaa.diameter.conf.impl.ImsiBasedRoutingTableConfigurable;
import com.elitecore.aaa.diameter.conf.impl.MsisdnBasedRoutingTableConfigurable;
import com.elitecore.aaa.diameter.conf.impl.RoutingTableConfigurable;
import com.elitecore.aaa.diameter.conf.sessionmanager.DiameterSessionManagerConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.diameterapi.diameter.common.data.PeerData;

/**
 * 
 * This is Composite Configurable entity for Diameter Related Configurations.
 * All the Configurations those are related to or dependent upon Diameter Stack 
 * are wrapped in here.
 * 
 * @author monica.lulla
 *
 */

@ReadOrder(order = {
		"diameterStackConfigurable", "priorityConfigurable",
		 "diameterSessionManagerConfigurable",
		 "imsiBasedRoutingTableConfigurable",
		 "msisdnBasedRoutingTableConfigurable",
		 "peerGroupConfigurable",
		 "diameterNASServiceConfigurationImpl","diameterEAPServiceConfigurationImpl","diameterCCServiceConfigurationImpl",
		 "diameterTGPPServerCompositeConfigurable", "diameterDriverCompositeConfigurable",
		 "diameterRoutingConfigurable",
		 "diameterConcurrencyConfigurable","peerProfileConfigurable", "peersConfigurable"
})
public class DiameterCompositeConfigurable extends CompositeConfigurable implements DiameterPeerConfiguration {

	private static final String MODULE = "DIA-COMPOSITE-CONFIGURABLE";

	@Configuration private DiameterStackConfigurable diameterStackConfigurable;
	
	@Configuration(conditional = true) @Nullable private PriorityConfigurable priorityConfigurable;
	@Configuration(conditional = true) @Nullable private ImsiBasedRoutingTableConfigurable imsiBasedRoutingTableConfigurable;
	@Configuration(conditional = true) @Nullable private MsisdnBasedRoutingTableConfigurable msisdnBasedRoutingTableConfigurable;
	@Configuration(conditional = true) @Nullable private DiameterRoutingTableConfigurable diameterRoutingConfigurable;
	@Configuration(conditional = true) @Nullable private DiameterSessionManagerConfigurable diameterSessionManagerConfigurable;
	@Configuration(conditional = true) @Nullable private DiameterConcurrencyConfigurable diameterConcurrencyConfigurable;
	@Configuration(conditional = true) @Nullable private DiameterNASServiceConfigurationImpl diameterNASServiceConfigurationImpl;
	@Configuration(conditional = true) @Nullable private DiameterEAPServiceConfigurationImpl diameterEAPServiceConfigurationImpl;
	@Configuration(conditional = true) @Nullable private DiameterCCServiceConfigurationImpl diameterCCServiceConfigurationImpl;
	@Configuration(conditional = true) @Nullable private DiameterTGPPServerCompositeConfigurable diameterTGPPServerCompositeConfigurable;
	@Configuration(conditional = true) @Nullable private DiameterDriverCompositeConfigurable diameterDriverCompositeConfigurable;
	@Configuration(conditional = true) @Nullable protected DiameterPeersConfigurable peersConfigurable;
	@Configuration(conditional = true) @Nullable protected DiameterPeerProfileConfigurable peerProfileConfigurable;
	@Configuration(conditional = true) @Nullable private DiameterPeerGroupConfigurable peerGroupConfigurable;

	private List<PeerData> peerDataList;
	
	public DiameterCompositeConfigurable() {
		peerDataList = new ArrayList<PeerData>();
	}
	
	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {

		boolean isStackEnabled = diameterStackConfigurable != null && diameterStackConfigurable.isDiameterStackEnabled();
		if (isStackEnabled == false) {
			return false;
		}
		
		AAAServerConfigurable aaaServerConfigurable = getConfigurationContext().get(AAAServerConfigurable.class);
		if (DiameterSessionManagerConfigurable.class.isAssignableFrom(configurableClass)) {
			return diameterStackConfigurable.getSessionManagerId().isPresent();
		} else if (DiameterCCServiceConfigurationImpl.class.isAssignableFrom(configurableClass)) {
			return aaaServerConfigurable.isServiceEnabled(AAAServerConstants.DIA_CC_SERVICE_ID);
		} else if (DiameterNASServiceConfigurationImpl.class.isAssignableFrom(configurableClass)) {
			return aaaServerConfigurable.isServiceEnabled(AAAServerConstants.DIA_NAS_SERVICE_ID);
		} else if (DiameterEAPServiceConfigurationImpl.class.isAssignableFrom(configurableClass)) {
			return aaaServerConfigurable.isServiceEnabled(AAAServerConstants.DIA_EAP_SERVICE_ID);
		} else if (DiameterTGPPServerCompositeConfigurable.class.isAssignableFrom(configurableClass)) {
			return aaaServerConfigurable.isServiceEnabled(AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID);
		}
		
		return true;
	}
	
	public ImsiBasedRoutingTableConfiguration getImsiBasedRoutingTableConfiguration() {
		return imsiBasedRoutingTableConfigurable;
	}
	
	public DiameterStackConfigurable getDiameterStackConfiguration() {
		return diameterStackConfigurable;
	}
	
	public PriorityConfigurable getPriorityConfigurable() {
		return priorityConfigurable;
	}
	
	public DiameterPeerConfiguration getDiameterPeerConfiguration() {
		return this;
	}

	public RoutingTableConfigurable getDiameterRoutingConfiguration() {
		return this.diameterRoutingConfigurable;
	}

	public DiameterSessionManagerConfigurable getDiameterSessionManagerConfiguration() {
		return diameterSessionManagerConfigurable;
	}

	public MsisdnBasedRoutingTableConfiguration getMsisdnBasedRoutingTableConfiguration() {
		return msisdnBasedRoutingTableConfigurable;
	}

	@PostRead
	@PostReload
	public void postReadProcessing(){
		List<PeerData> licenseExceededPeers = new ArrayList<PeerData>();
		if (diameterStackConfigurable.isDiameterStackEnabled() == false) {
			return;
		}
		
		DiameterCompositeConfigurablePostReadProcessor postProcessor = new DiameterCompositeConfigurablePostReadProcessor(
				((AAAConfigurationContext)getConfigurationContext()).getServerContext(),
				peersConfigurable, 
				peerProfileConfigurable, 
				getConfigurationContext().get(ServerCertificateConfigurable.class));
		postProcessor.postRead();
		
		this.peerDataList = postProcessor.getPeerDataList();
		licenseExceededPeers = postProcessor.getLicenseExceededPeers();
	}

	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	public DiameterConcurrencyConfigurable getDiameterConcurrencyConfigurable() {
		return diameterConcurrencyConfigurable;
	}
	
	public DiameterPeerGroupConfigurable getDiameterPeerGroupConfigurable() {
		return peerGroupConfigurable;
	}
	
	public DiameterServiceConfigurationDetail getDiameterServiceConfiguration(String serviceId) {
		if(AAAServerConstants.DIA_NAS_SERVICE_ID.equals(serviceId)){
			return diameterNASServiceConfigurationImpl ;
		}else if (AAAServerConstants.DIA_EAP_SERVICE_ID.equals(serviceId)) {
			return  diameterEAPServiceConfigurationImpl;
		}else if (AAAServerConstants.DIA_CC_SERVICE_ID.equals(serviceId)) {
			return  diameterCCServiceConfigurationImpl;
		} else if (AAAServerConstants.DIA_TGPP_SERVER_SERVICE_ID.equals(serviceId)) {
			return diameterTGPPServerCompositeConfigurable;
		}
		return null;
	}
	
	public @Nullable DriverConfigurationProvider getDiameterDriverConfiguration() {
		return diameterDriverCompositeConfigurable;
	}

	@Override
	public List<PeerData> getPeerDataList() {
		return peerDataList;
	}
}
