package com.elitecore.aaa.mibs.radius.authentication.server.extended;

import com.elitecore.aaa.Version;
import com.elitecore.aaa.mibs.radius.authentication.server.RadiusAuthServiceMIBListener;
import com.elitecore.aaa.mibs.radius.authentication.server.autogen.EnumRadiusAuthServConfigReset;
import com.elitecore.aaa.mibs.radius.authentication.server.autogen.RadiusAuthServ;
import com.elitecore.aaa.mibs.radius.authentication.server.autogen.TableRadiusAuthClientTable;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.kpi.annotation.Table;
import static com.elitecore.core.util.mbean.SnmpCounterUtil.convertToCounter32;

public class RadiusAuthServMBeanImpl extends RadiusAuthServ {

	@Override
	@Column(name="TotalAccessRejects", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServTotalAccessRejects() {
		return convertToCounter32(RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessRejects());
	}

	@Override
	@Column(name="TotalAccessAccepts", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServTotalAccessAccepts() {
		return convertToCounter32(RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessAccepts());
	}

	@Override
	@Column(name = "TotalDupAccessRequests", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServTotalDupAccessRequests() {
		return convertToCounter32(RadiusAuthServiceMIBListener.getRadiusAuthServTotalDupAccessRequests());
	}

	@Override
	@Column(name = "TotalInvalidRequests", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServTotalInvalidRequests() {
		return convertToCounter32(RadiusAuthServiceMIBListener.getRadiusAuthServTotalInvalidRequests());
	}

	@Override
	@Table(name="radiusAuthClientTable")
	public TableRadiusAuthClientTable accessRadiusAuthClientTable() {
		return RadiusAuthServiceMIBListener.getAuthClientTable();
	}

	@Override
	@Column(name = "TotalUnknownTypes", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServTotalUnknownTypes() {
		return convertToCounter32(RadiusAuthServiceMIBListener.getRadiusAuthServTotalUnknownTypes ());
	}

	@Override
	@Column(name = "TotalAccessRequests", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServTotalAccessRequests() {
		return convertToCounter32(RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessRequests());
	}

	@Override
	@Column(name = "radiusAuthServConfigReset", type = java.sql.Types.VARCHAR)
	public EnumRadiusAuthServConfigReset getRadiusAuthServConfigReset() {
		if(RadiusAuthServiceMIBListener.getOtherState()) {
			return new EnumRadiusAuthServConfigReset(1);
		} else if (!RadiusAuthServiceMIBListener.getInitializedState()) {
			return new EnumRadiusAuthServConfigReset(3);
		} else {
			return new EnumRadiusAuthServConfigReset(4);
		}
	}

	@Override
	public void setRadiusAuthServConfigReset(EnumRadiusAuthServConfigReset x) {
		if(x.intValue() == 2) {
			RadiusAuthServiceMIBListener.reInitialize();
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void checkRadiusAuthServConfigReset(EnumRadiusAuthServConfigReset x) {
		 
//		has not been implemented. doing this for sonar
	}

	@Override
	@Column(name = "TotalPacketsDropped", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServTotalPacketsDropped() {
		return convertToCounter32(RadiusAuthServiceMIBListener.getRadiusAuthServTotalPacketsDropped());
	}

	@Override
	@Column(name = "radiusAuthServResetTime", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServResetTime() {
		if (RadiusAuthServiceMIBListener.getRadiusAuthServResetTime() > 0 ) {
			return ((System.currentTimeMillis() - RadiusAuthServiceMIBListener.getRadiusAuthServResetTime()) / 10);
		}
		return Long.valueOf(0);
	}

	@Override
	@Column(name = "TotalBadAuthenticators", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServTotalBadAuthenticators() {
		return convertToCounter32(RadiusAuthServiceMIBListener.getRadiusAuthServTotalBadAuthenticators());
	}

	@Override
	@Column(name="TotalMalformedAccessRequests", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServTotalMalformedAccessRequests() {
		return convertToCounter32(RadiusAuthServiceMIBListener.getRadiusAuthServTotalMalformedAccessRequests());
	}

	@Override
	@Column(name = "radiusAuthServUpTime", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServUpTime() {
		if ( RadiusAuthServiceMIBListener.getRadiusAuthServUpTime() > 0 ) { 
			return ((System.currentTimeMillis() - RadiusAuthServiceMIBListener.getRadiusAuthServUpTime()) / 10);
		}
		return Long.valueOf(0);
	}

	@Override
	@Column(name = "radiusAuthServIdent", type = java.sql.Types.VARCHAR)
	public String getRadiusAuthServIdent() {
		return "EliteRadius [ " + Version.getVersion()+" ]";
	}

	@Override
	@Column(name = "TotalAccessChallenges", type = java.sql.Types.BIGINT)
	public Long getRadiusAuthServTotalAccessChallenges() {
		return convertToCounter32(RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessChallenges());
	}

}
