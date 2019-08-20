package com.elitecore.aaa.rm.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.impl.RMDiameterDriverCompositeConfigurable;
import com.elitecore.aaa.core.conf.impl.ServerCertificateConfigurable;
import com.elitecore.aaa.diameter.conf.DiameterCompositeConfigurablePostReadProcessor;
import com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable;
import com.elitecore.aaa.diameter.conf.DiameterPeerConfiguration;
import com.elitecore.aaa.diameter.conf.DiameterStackConfigurable;
import com.elitecore.aaa.diameter.conf.ImsiBasedRoutingTableConfiguration;
import com.elitecore.aaa.diameter.conf.MsisdnBasedRoutingTableConfiguration;
import com.elitecore.aaa.diameter.conf.PriorityConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterPeerGroupConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterPeerProfileConfigurable;
import com.elitecore.aaa.diameter.conf.impl.DiameterRoutingTableConfigurable;
import com.elitecore.aaa.diameter.conf.impl.ImsiBasedRoutingTableConfigurable;
import com.elitecore.aaa.diameter.conf.impl.MsisdnBasedRoutingTableConfigurable;
import com.elitecore.aaa.diameter.conf.impl.RoutingTableConfigurable;
import com.elitecore.aaa.diameter.conf.sessionmanager.DiameterSessionManagerConfigurable;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.diameterapi.diameter.common.data.PeerData;

@ReadOrder(order = {
		"diameterStackConfigurable", "priorityConfigurable",
		"diameterSessionManagerConfigurable",
		"imsiBasedRoutingTableConfigurable",
		"msisdnBasedRoutingTableConfigurable",
		"peerGroupConfigurable",
		"diameterRoutingConfigurable",
		"diameterConcurrencyConfigurable","peerProfileConfigurable", "peersConfigurable", "rmDriverCompositeConfigurable"
})
public class RMDiameterCompositeConfigurable extends CompositeConfigurable implements DiameterPeerConfiguration {

	@Configuration private DiameterStackConfigurable diameterStackConfigurable;

	@Configuration(conditional = true) @Nullable private PriorityConfigurable priorityConfigurable;
	@Configuration(conditional = true) @Nullable private ImsiBasedRoutingTableConfigurable imsiBasedRoutingTableConfigurable;
	@Configuration(conditional = true) @Nullable private MsisdnBasedRoutingTableConfigurable msisdnBasedRoutingTableConfigurable;
	@Configuration(conditional = true) @Nullable private DiameterRoutingTableConfigurable diameterRoutingConfigurable;
	@Configuration(conditional = true) @Nullable private DiameterSessionManagerConfigurable diameterSessionManagerConfigurable;
	@Configuration(conditional = true) @Nullable private DiameterConcurrencyConfigurable diameterConcurrencyConfigurable;
	@Configuration(conditional = true) @Nullable private RMDiameterPeerConfigurable peersConfigurable;
	@Configuration(conditional = true) @Nullable protected DiameterPeerProfileConfigurable peerProfileConfigurable;
	@Configuration(conditional = true) @Nullable private DiameterPeerGroupConfigurable peerGroupConfigurable;
	@Configuration(conditional = true) @Nullable private RMDiameterDriverCompositeConfigurable rmDriverCompositeConfigurable;
	
	private List<PeerData> peerDataList;
	

	public RMDiameterCompositeConfigurable() {
		peerDataList = new ArrayList<PeerData>();
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

	@PostReload
	public void postReloadProcessing(){

	}

	public DiameterConcurrencyConfigurable getDiameterConcurrencyConfigurable() {
		return diameterConcurrencyConfigurable;
	}

	public DiameterPeerGroupConfigurable getDiameterPeerGroupConfigurable() {
		return peerGroupConfigurable;
	}
	
	public @Nullable DriverConfigurationProvider getDiameterDriverConfiguration() {
		return rmDriverCompositeConfigurable;
	}

	@Override
	public List<PeerData> getPeerDataList() {
		return peerDataList;
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		boolean isStackEnabled = diameterStackConfigurable != null && diameterStackConfigurable.isDiameterStackEnabled();
		if (isStackEnabled == false) {
			return false;
		}
		
		if (DiameterSessionManagerConfigurable.class.isAssignableFrom(configurableClass)) {
			return diameterStackConfigurable.getSessionManagerId().isPresent();
		}
		return true;
	}
}
