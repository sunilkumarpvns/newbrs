package com.elitecore.aaa.mibs.radius.accounting.server.extended;

import com.elitecore.aaa.core.constant.SnmpAgentMBeanConstant;
import com.elitecore.aaa.mibs.radius.accounting.server.RadiusAcctServiceMIBListener;
import com.elitecore.aaa.mibs.radius.accounting.server.autogen.RadiusAccClientEntry;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.logging.LogManager;

public class RadiusAccClientEntryMBeanImpl extends RadiusAccClientEntry {

	private final static String MODULE = "RAD-ACC-CLNT-ENTRY";
	
	private int clientIndex;
	private String clientAddress;
	
	public RadiusAccClientEntryMBeanImpl(int index, String ipAddress) {
		this.clientIndex = index;
		this.clientAddress = ipAddress;
	}
	
	@Override
	@Column(name = "radiusAccServMalformedRequests" , type = java.sql.Types.BIGINT) 
	public Long getRadiusAccServMalformedRequests() {
		return RadiusAcctServiceMIBListener.getRadiusAccServTotalMalformedRequests(clientAddress);
	}

	@Override
	@Column(name = "radiusAccServBadAuthenticators", type = java.sql.Types.BIGINT)
	public Long getRadiusAccServBadAuthenticators() {
		return RadiusAcctServiceMIBListener.getRadiusAccServTotalBadAuthenticators(clientAddress);
	}

	@Override
	@Column(name = "radiusAccServResponses", type = java.sql.Types.BIGINT)
	public Long getRadiusAccServResponses() {
		return RadiusAcctServiceMIBListener.getRadiusAccServTotalResponses(clientAddress);
	}

	@Override
	@Column(name="radiusAccServDupRequests", type = java.sql.Types.BIGINT)
	public Long getRadiusAccServDupRequests() {
		return RadiusAcctServiceMIBListener.getRadiusAccServTotalDupRequests(clientAddress);
	}

	@Override
	@Column(name = "radiusAccServRequests", type = java.sql.Types.BIGINT)
	public Long getRadiusAccServRequests() {
		return RadiusAcctServiceMIBListener.getRadiusAccServTotalRequests(clientAddress);
	}

	@Override
	@Column(name="radiusAccServPacketsDropped", type = java.sql.Types.BIGINT)
	public Long getRadiusAccServPacketsDropped() {
		return RadiusAcctServiceMIBListener.getRadiusAccServTotalPacketsDropped(clientAddress);
	}

	@Override
	@Column(name = "radiusAccClientID" , type = java.sql.Types.VARCHAR)
	public String getRadiusAccClientID() {
		RadClientData clientData = RadiusAcctServiceMIBListener.getClientData(clientAddress);
		if(clientData == null) {
			LogManager.getLogger().warn(MODULE, "Client Data not found to fetch client ID for client Address: " +  clientAddress);
			return "NOT AVAILABLE";
		} else {
			String value = (clientData.getVendorName() + " - " + clientData.getVendorId() + " [ "+ clientData.getVendorType() + " ]" );
			return value;	
		}
	}

	@Override
	@Column(name = "radiusAccClientAddress", type = java.sql.Types.VARCHAR)
	public String getRadiusAccClientAddress() {
		return clientAddress;
	}

	@Override
	@Column(name = "radiusAccServUnknownTypes", type = java.sql.Types.BIGINT) 
	public Long getRadiusAccServUnknownTypes() {
		return RadiusAcctServiceMIBListener.getRadiusAccServTotalUnknownTypes(clientAddress);
	}

	@Override
	@Column(name = "radiusAccServNoRecords", type = java.sql.Types.BIGINT) 
	public Long getRadiusAccServNoRecords() {
		return RadiusAcctServiceMIBListener.getRadiusAccServTotalNoRecords(clientAddress);
	}

	@Override
	@Column(name = "radiusAccClientIndex", type = java.sql.Types.INTEGER)
	public Integer getRadiusAccClientIndex() {
		return clientIndex;
	}

	public String getObjectName() {
		return SnmpAgentMBeanConstant.RAD_ACCT_CLIENT_TABLE + clientAddress;
	}
}
