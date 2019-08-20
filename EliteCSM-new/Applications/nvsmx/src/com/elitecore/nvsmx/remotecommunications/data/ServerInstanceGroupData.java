package com.elitecore.nvsmx.remotecommunications.data;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "TBLM_SERVER_INSTANCE_GROUP")
@XmlRootElement
public class ServerInstanceGroupData implements Serializable{

	private String id;
	private String name;
	private List<ServerInstanceGroupRelData> serverInstanceGroupRelDatas;
	public ServerInstanceGroupData() {
		super();
		this.serverInstanceGroupRelDatas = new ArrayList<ServerInstanceGroupRelData>();
	}

	@Id
	@Column(name = "ID")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	@JoinColumn(name="SERVER_INSTANCE_GROUP_ID")
	public List<ServerInstanceGroupRelData> getServerInstanceGroupRelDatas() {
		return serverInstanceGroupRelDatas;
	}
	
	public void setServerInstanceGroupRelDatas(List<ServerInstanceGroupRelData> netServerInstanceDatas) {
		this.serverInstanceGroupRelDatas = netServerInstanceDatas;

		if(Collectionz.isNullOrEmpty(serverInstanceGroupRelDatas) == false) {
			Collections.sort(serverInstanceGroupRelDatas, Collections.reverseOrder((o1,o2) -> o1.getServerWeight().compareTo(o2.getServerWeight())));
		}
	}

	public String toString() {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE)
				.append("Name", name).append("Server Instance(s):");

		if (Collectionz.isNullOrEmpty(serverInstanceGroupRelDatas) == false) {
           for (ServerInstanceGroupRelData serverInstanceGroupRelData : serverInstanceGroupRelDatas) {
				toStringBuilder.append(serverInstanceGroupRelData);
			}
		} else {
			toStringBuilder.append("Server Instance(s) not configured");
		}
		return toStringBuilder.toString();

	}

	public static ServerGroupData from(ServerInformation serverInformation){
		ServerGroupData serverInstanceGroupData = new ServerGroupData();
		serverInstanceGroupData.setId(serverInformation.getId());
		serverInstanceGroupData.setName(serverInformation.getName());
		return serverInstanceGroupData;
	}
}
