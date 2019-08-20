package com.elitecore.aaa.mibs.radius.accounting.server.extended;

import com.elitecore.aaa.Version;
import com.elitecore.aaa.mibs.radius.accounting.server.RadiusAcctServiceMIBListener;
import com.elitecore.aaa.mibs.radius.accounting.server.autogen.EnumRadiusAccServConfigReset;
import com.elitecore.aaa.mibs.radius.accounting.server.autogen.RadiusAccServ;
import com.elitecore.aaa.mibs.radius.accounting.server.autogen.TableRadiusAccClientTable;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.core.util.mbean.SnmpCounterUtil;

public class RadiusAccServImpl extends RadiusAccServ {

	@Override
	@Column(name = "TotalMalformedRequests", type = java.sql.Types.BIGINT)
	public Long getRadiusAccServTotalMalformedRequests() {
		return SnmpCounterUtil.convertToCounter32(RadiusAcctServiceMIBListener.getRadiusAccServTotalMalformedRequests());
	}

	@Override
	@Column(name = "radiusAccServTotalResponses", type = java.sql.Types.BIGINT)
	public Long getRadiusAccServTotalResponses() {
		return SnmpCounterUtil.convertToCounter32(RadiusAcctServiceMIBListener.getRadiusAccServTotalResponses());
	}

	@Override
	@Column(name = "radiusAccServTotalDupRequests" , type = java.sql.Types.BIGINT)
	public Long getRadiusAccServTotalDupRequests() {
		return SnmpCounterUtil.convertToCounter32(RadiusAcctServiceMIBListener.getRadiusAccServTotalDupRequests());
	}

	@Override
	@Column(name = "TotalInvalidRequests" , type = java.sql.Types.BIGINT)
	public Long getRadiusAccServTotalInvalidRequests() {
		return SnmpCounterUtil.convertToCounter32(RadiusAcctServiceMIBListener.getRadiusAccServTotalInvalidRequests());
	}

	@Override
	@Column(name="radiusAccServTotalRequests", type = java.sql.Types.BIGINT)
	public Long getRadiusAccServTotalRequests() {
		return SnmpCounterUtil.convertToCounter32(RadiusAcctServiceMIBListener.getRadiusAccServTotalRequests());
	}

	@Override
	@Table(name = "radiusAccClientTable")
	public TableRadiusAccClientTable accessRadiusAccClientTable() {
		return RadiusAcctServiceMIBListener.getTableRadiusAccClientTable();
	}

	@Override
	@Column(name = "radiusAccServConfigReset", type = java.sql.Types.VARCHAR)
	public EnumRadiusAccServConfigReset getRadiusAccServConfigReset() {
		if(RadiusAcctServiceMIBListener.getOtherState()) {
			return new EnumRadiusAccServConfigReset(1);
		} else if (!RadiusAcctServiceMIBListener.getInitializedState()) {
			return new EnumRadiusAccServConfigReset(3);
		} else {
			return new EnumRadiusAccServConfigReset(4);
		}
	}

	@Override
	public void setRadiusAccServConfigReset(EnumRadiusAccServConfigReset x) {
		if(x.intValue() == 2) {
			RadiusAcctServiceMIBListener.reInitialize();
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void checkRadiusAccServConfigReset(EnumRadiusAccServConfigReset x) {
		// nothing to do
	}

	@Override
	@Column(name = "radiusAccServTotalUnknownTypes", type = java.sql.Types.BIGINT) 
	public Long getRadiusAccServTotalUnknownTypes() {
		return SnmpCounterUtil.convertToCounter32(RadiusAcctServiceMIBListener.getRadiusAccServTotalUnknownTypes());
	}

	@Override
	@Column(name = "radiusAccServTotalNoRecords" , type = java.sql.Types.BIGINT)
	public Long getRadiusAccServTotalNoRecords() {
		return SnmpCounterUtil.convertToCounter32(RadiusAcctServiceMIBListener.getRadiusAccServTotalNoRecords());
	}

	@Override
	@Column(name = "radiusAccServResetTime", type = java.sql.Types.BIGINT)
	public Long getRadiusAccServResetTime() {
		if(RadiusAcctServiceMIBListener.getRadiusAcctServResetTime() > 0)
			return SnmpCounterUtil.convertToCounter32((System.currentTimeMillis() - RadiusAcctServiceMIBListener.getRadiusAcctServResetTime()) /10);
		else
			return new Long(0);
	}

	@Override
	@Column(name = "TotalPacketsDropped", type = java.sql.Types.BIGINT)
	public Long getRadiusAccServTotalPacketsDropped() {
		return RadiusAcctServiceMIBListener.getRadiusAccServTotalPacketsDropped();
	}

	@Override
	@Column(name="radiusAccServUpTime", type = java.sql.Types.BIGINT)
	public Long getRadiusAccServUpTime() {
		if(RadiusAcctServiceMIBListener.getRadiusAcctServUpTime() > 0)
			return SnmpCounterUtil.convertToCounter32((System.currentTimeMillis() - RadiusAcctServiceMIBListener.getRadiusAcctServUpTime()) /10);
		else 
			return new Long(0);
	}

	@Override
	@Column(name = "TotalBadAuthenticators", type = java.sql.Types.BIGINT)
	public Long getRadiusAccServTotalBadAuthenticators() {
		return SnmpCounterUtil.convertToCounter32(RadiusAcctServiceMIBListener.getRadiusAccServTotalBadAuthenticators());
	}

	@Override
	@Column(name = "radiusAccServIdent", type = java.sql.Types.VARCHAR)
	public String getRadiusAccServIdent() {
		return "EliteRadius [ " + Version.getVersion()+" ]";
	}

}
