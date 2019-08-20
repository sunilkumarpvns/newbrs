package com.elitecore.nvsmx.commons.model.sessionmanager;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;
import org.apache.commons.lang.SystemUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * 
 * @author Jay Trivedi
 *
 */

@Entity
@Table(name = "TBLMNETSERVERINSTANCE")
public class NetServerInstanceData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final ToStringStyle SERVER_DATA_TO_STRING_STYLE = new ServerInstanceDataToString();
	private String id;
	private String netServerCode;
	private String name;
	private String description;
	private String version;
	private String netServerTypeId;
	private String adminHost;
	private Integer adminPort;
	private String systemGenerated;
	private String restServerAddress;

	@Id
	@Column(name = "NETSERVERID")
	public String getId() {
		return id;
	}

	public void setId(String netServerId) {
		this.id = netServerId;
	}

	@Column(name = "NETSERVERCODE")
	public String getNetServerCode() {
		return netServerCode;
	}

	public void setNetServerCode(String netServerCode) {
		this.netServerCode = netServerCode;
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "VERSION")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Column(name = "NETSERVERTYPEID")
	public String getNetServerTypeId() {
		return netServerTypeId;
	}

	public void setNetServerTypeId(String netServerTypeId) {
		this.netServerTypeId = netServerTypeId;
	}

	@Column(name = "ADMINHOST")
	public String getAdminHost() {
		return adminHost;
	}

	public void setAdminHost(String adminHost) {
		this.adminHost = adminHost;
	}

	@Column(name = "ADMINPORT")
	public Integer getAdminPort() {
		return adminPort;
	}

	public void setAdminPort(Integer adminPort) {
		this.adminPort = adminPort;
	}

	@Column(name = "SYSTEMGENERATED")
	public String getSystemGenerated() {
		return systemGenerated;
	}

	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}

	@Override
	public String toString(){
		return toString(SERVER_DATA_TO_STRING_STYLE);
	}

	public String toString(ToStringStyle toStringStyle) {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this, toStringStyle)
				.append("Name", name)
				.append("netServerId" , id)
				.append("netServerCode" , netServerCode)
				.append("description", description)
				.append("version" , version)
				.append("netServerTypeId", netServerTypeId)
				.append("adminHost", adminHost)
				.append("adminPort", adminPort)
				.append("systemGenerated", systemGenerated)
				.append("restAddress",restServerAddress);

		return toStringBuilder.toString();
	}


	@Transient
	public String getRestServerAddress() {
		return restServerAddress;
	}

	public void setRestServerAddress(String restServerAddress) {
		this.restServerAddress = restServerAddress;
	}

	@Transient
	public static NetServerInstanceData from(ServerInformation serverInformation){
		NetServerInstanceData netServerInstanceData = new NetServerInstanceData();
		netServerInstanceData.setNetServerCode(serverInformation.getNetServerCode());
		netServerInstanceData.setName(serverInformation.getName());
		netServerInstanceData.setId(serverInformation.getId());
		return netServerInstanceData;
	}


	private static final class ServerInstanceDataToString extends ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		ServerInstanceDataToString() {
			super();
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(4) + getTabs(2));
		}
	}
}