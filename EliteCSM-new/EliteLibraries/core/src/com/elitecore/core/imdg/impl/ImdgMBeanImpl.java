package com.elitecore.core.imdg.impl;

import com.elitecore.core.imdg.autogen.Imdg;
import com.elitecore.core.imdg.impl.IMDG_MIBImpl.ClusterInfo;
import com.sun.management.snmp.SnmpStatusException;

public class ImdgMBeanImpl extends Imdg {

	private static final long serialVersionUID = 1L;
	private ClusterInfo clusterInfo; // NO SONAR

	public ImdgMBeanImpl(ClusterInfo clusterInfo) {
		this.clusterInfo = clusterInfo;
	}

	/**
	 * Getter for the "OwnState" variable.
	 */
	@Override
	public String getOwnState() throws SnmpStatusException {
		return this.clusterInfo.getClusterState();
	}

	/**
	 * Getter for the "OwnPort" variable.
	 */
	@Override
	public Integer getOwnPort() throws SnmpStatusException {
		return this.clusterInfo.getOwnPort();
	}

	/**
	 * Getter for the "OwnIpAddress" variable.
	 */
	@Override
	public String getOwnIpAddress() throws SnmpStatusException {
		return this.clusterInfo.getOwnIp();
	}

	/**
	 * Getter for the "ClusterState" variable.
	 */
	@Override
	public String getClusterState() throws SnmpStatusException {
		return this.clusterInfo.getClusterState();
	}

	/**
	 * Getter for the "ClusterMembersCount" variable.
	 */
	@Override
	public Long getClusterMembersCount() throws SnmpStatusException {
		return this.clusterInfo.getClusterMemberCount();
	}

	/**
	 * Getter for the "ClusterName" variable.
	 */
	@Override
	public String getClusterName() throws SnmpStatusException {
		return this.clusterInfo.getClusterName();
	}

	/**
	 * Getter for the "ClusterVersion" variable.
	 */
	@Override
	public String getClusterVersion() throws SnmpStatusException {
		return this.clusterInfo.getClusterVesion();
	}

}
