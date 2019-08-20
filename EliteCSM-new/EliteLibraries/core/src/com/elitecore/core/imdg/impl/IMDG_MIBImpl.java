package com.elitecore.core.imdg.impl;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.elitecore.core.imdg.autogen.IMDG_MIB;
import com.hazelcast.core.Cluster;

public class IMDG_MIBImpl extends IMDG_MIB {

	private ImdgMBeanImpl imdgMbean;

 	private ClusterInfo clusterInfo;  // NO SONAR

	public IMDG_MIBImpl(Cluster cluster, String groupName) {
		this.clusterInfo = new ClusterInfo(groupName, cluster);
		this.imdgMbean = new ImdgMBeanImpl(clusterInfo);
	}

	@Override
	protected Object createImdgMBean(String groupName, String groupOid, ObjectName groupObjname, MBeanServer server) {
		return this.imdgMbean;
	}
	
	class ClusterInfo {

		private Cluster cluster;
		private String groupName;

		public ClusterInfo(String groupName, Cluster cluster) {
			this.cluster = cluster;
			this.groupName = groupName;
		}

		public String getClusterState() {
			return this.cluster.getClusterState().name();
		}

		public Integer getOwnPort() {
			return this.cluster.getLocalMember().getAddress().getPort();
		}

		public String getOwnIp() {
			return this.cluster.getLocalMember().getAddress().getHost();
		}

		public String getOwnState() {
			if (this.cluster.getLocalMember().getAddress().getPort() > 0 ) {
				return "ACTIVE";
			}
			return "INACTIVE";
		}

		public long getClusterMemberCount() {
			return this.cluster.getMembers().size();
		}

		public String getClusterName() {
			return groupName;
		}

		public String getClusterVesion() {
			return this.cluster.getClusterVersion().toString(); 
		}

	}
}
