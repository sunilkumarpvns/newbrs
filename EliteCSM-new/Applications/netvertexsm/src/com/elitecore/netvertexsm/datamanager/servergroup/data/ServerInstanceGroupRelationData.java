package com.elitecore.netvertexsm.datamanager.servergroup.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TBLM_NET_SERVER_GROUP_REL")
public class ServerInstanceGroupRelationData implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String serverInstanceGroupId;
	private String netServerInstanceId;
	private Integer serverWeightage;
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(generator = "eliteSequenceGenerator")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "SERVER_INSTANCE_GROUP_ID")
	public String getServerInstanceGroupId() {
		return serverInstanceGroupId;
	}

	public void setServerInstanceGroupId(String serverInstanceGroupId) {
		this.serverInstanceGroupId = serverInstanceGroupId;
	}

	@Column(name = "NETSERVER_INSTANCE_ID")
	public String getNetServerInstanceId() {
		return netServerInstanceId;
	}

	public void setNetServerInstanceId(String netServerInstanceId) {
		this.netServerInstanceId = netServerInstanceId;
	}

	@Column(name = "SERVER_WEIGHTAGE")
	public Integer getServerWeightage() {
		return serverWeightage;
	}

	public void setServerWeightage(Integer serverWeightage) {
		this.serverWeightage = serverWeightage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((netServerInstanceId == null) ? 0 : netServerInstanceId
						.hashCode());
		result = prime
				* result
				+ ((serverInstanceGroupId == null) ? 0 : serverInstanceGroupId
						.hashCode());
		result = prime * result
				+ ((serverWeightage == null) ? 0 : serverWeightage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerInstanceGroupRelationData other = (ServerInstanceGroupRelationData) obj;
		if (netServerInstanceId == null) {
			if (other.netServerInstanceId != null)
				return false;
		} else if (!netServerInstanceId.equals(other.netServerInstanceId))
			return false;
		if (serverInstanceGroupId == null) {
			if (other.serverInstanceGroupId != null)
				return false;
		} else if (!serverInstanceGroupId.equals(other.serverInstanceGroupId))
			return false;
		if (serverWeightage == null) {
			if (other.serverWeightage != null)
				return false;
		} else if (!serverWeightage.equals(other.serverWeightage))
			return false;
		return true;
	}
	
	

}
