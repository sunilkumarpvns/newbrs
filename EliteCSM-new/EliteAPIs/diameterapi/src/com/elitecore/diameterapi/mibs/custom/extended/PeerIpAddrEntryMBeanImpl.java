package com.elitecore.diameterapi.mibs.custom.extended;

import java.sql.Types;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.custom.autogen.EnumPeerStatus;
import com.elitecore.diameterapi.mibs.custom.autogen.PeerIpAddrEntry;
import com.sun.management.snmp.SnmpStatusException;

public class PeerIpAddrEntryMBeanImpl extends PeerIpAddrEntry{

	private static final long serialVersionUID = 1L;
	private final String peerIdentity;
	private final long ipAddressIndex;
	transient private final DiameterConfigProvider diameterConfigProvider;
	private String compositeIndex;

	public PeerIpAddrEntryMBeanImpl(String peerIdentity,
			long ipAddressIndex, DiameterConfigProvider diameterConfigProvider) {
				this.peerIdentity = peerIdentity;
				this.ipAddressIndex = ipAddressIndex;
				this.diameterConfigProvider = diameterConfigProvider;
	}

	@Override
	@Column(name = "reconnectionCount", type =Types.BIGINT)
	public Long getReconnectionCount() throws SnmpStatusException {
		return this.diameterConfigProvider.getPeerConfig(peerIdentity).getDbpPerPeerStatsTimeoutConnAtmpts();
	}

	@Override
	@Column(name = "peerStatus", type =Types.VARCHAR)
	public EnumPeerStatus getPeerStatus() throws SnmpStatusException {
		return new EnumPeerStatus(this.diameterConfigProvider.getPeerConfig(peerIdentity).getPeerState());
	}

	@Override
	@Column(name = "peerRemoteIpAddress", type =Types.VARCHAR)
	public String getPeerRemoteIpAddress() throws SnmpStatusException {
		return this.diameterConfigProvider.getPeerConfig(peerIdentity).getPeerIpAddresses();
	}

	@Override
	@Column(name = "peerLocalIpAddress", type =Types.VARCHAR)
	public String getPeerLocalIpAddress() throws SnmpStatusException {
		return this.diameterConfigProvider.getPeerConfig(peerIdentity).getPeerLocalIpAddresses();
	}

	@Override
	@Column(name = "peerIpAddressIndex", type =Types.BIGINT)
	public Long getPeerIpAddressIndex() throws SnmpStatusException {
		return ipAddressIndex;
	}

	@Override
	@Column(name = "peerIndex", type =Types.BIGINT)
	public Long getPeerIndex() throws SnmpStatusException {
		return this.diameterConfigProvider.getPeerConfig(peerIdentity).getDbpPeerIndex();
	}

	@Override
	public String getPeerIpAddrPeerIdentity() throws SnmpStatusException {
		return peerIdentity;
	}

	@Override
	public String getPeerIpAddrCompsIndexValue() throws SnmpStatusException {
		return this.compositeIndex;
	}

	public void setCompositeIndex(String compositeIndex) {
		this.compositeIndex = compositeIndex;
	}
	
	public String getObjectName() {
		return SnmpAgentMBeanConstant.DIAMETER_STACK_PEER_CONN_TABLE
				+ peerIdentity  + "-" + diameterConfigProvider.getPeerConfig(peerIdentity).getPeerIpAddresses();
	}
}