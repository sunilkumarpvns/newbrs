package com.elitecore.aaa.mibs.radius.authentication.server.extended;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.radius.authentication.server.RadiusAuthServiceMIBListener;
import com.elitecore.aaa.mibs.radius.authentication.server.autogen.RadiusAuthClientEntry;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.commons.kpi.annotation.Column;

public class RadiusAuthClientEntryMBeanImpl extends RadiusAuthClientEntry {

	private int index;
	private String clientAddress;

	public RadiusAuthClientEntryMBeanImpl(int index, String clientAddress) {
		this.index = index;
		this.clientAddress = clientAddress;
	}
	
	@Override
	@Column(name="MalformedAccessRequests", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServMalformedAccessRequests() {
		return RadiusAuthServiceMIBListener.getRadiusAuthServTotalMalformedAccessRequests(clientAddress);
	}

	@Override
	@Column(name="radiusAuthServAccessChallenges", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServAccessChallenges()  {
		return RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessChallenges(clientAddress);
	}

	@Override
	@Column(name = "radiusAuthServAccessRejects", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServAccessRejects()  {
		return RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessRejects(clientAddress);
	}

	@Override
	@Column(name = "radiusAuthServAccessAccepts", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServAccessAccepts()  {
		return RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessAccepts(clientAddress);
	}

	@Override
	@Column(name = "DupAccessRequests", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServDupAccessRequests()  {
		return RadiusAuthServiceMIBListener.getRadiusAuthServTotalDupAccessRequests(clientAddress);
	}

	@Override
	@Column(name = "radiusAuthServAccessRequests", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServAccessRequests()  {
		return RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessRequests(clientAddress);
	}

	@Override
	@Column(name = "radiusAuthServUnknownTypes", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServUnknownTypes()  {
		return RadiusAuthServiceMIBListener.getRadiusAuthServTotalUnknownTypes(clientAddress);
	}

	@Override
	@Column(name = "radiusAuthClientID", type = java.sql.Types.VARCHAR)
	public String getRadiusAuthClientID()  {
		RadClientData clientData = RadiusAuthServiceMIBListener.getClientData(clientAddress);
		if (clientData == null) {
			return "NOT AVAILABLE";
		} else {
			String value = (clientData.getVendorName() + " - " + clientData.getVendorId() + " [ "+ clientData.getVendorType() + " ]" );
			return value;
		}
	}

	@Override
	@Column(name = "radiusAuthClientAddress", type = java.sql.Types.VARCHAR)
	public String getRadiusAuthClientAddress()  {
		return clientAddress;
	}

	@Override
	@Column(name = "radiusAuthServPacketsDropped", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServPacketsDropped()  {
		return RadiusAuthServiceMIBListener.getRadiusAuthServTotalPacketsDropped(clientAddress);
	}

	@Override
	@Column(name = "BadAuthenticators", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServBadAuthenticators()  {
		return RadiusAuthServiceMIBListener.getRadiusAuthServTotalBadAuthenticators(clientAddress);
	}

	@Override
	@Column(name = "radiusAuthClientIndex", type = java.sql.Types.INTEGER)
	public Integer getRadiusAuthClientIndex()  {
		return index;
	}

	public String getObjectName() {
		return SnmpAgentMBeanConstant.RAD_AUTH_CLIENT_TABLE + clientAddress;
	}
}
