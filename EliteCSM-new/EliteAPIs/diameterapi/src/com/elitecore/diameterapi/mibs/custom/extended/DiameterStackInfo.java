package com.elitecore.diameterapi.mibs.custom.extended;

import java.sql.Types;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.stack.constant.Status;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.mibs.custom.autogen.DiameterStack;
import com.elitecore.diameterapi.mibs.custom.autogen.EnumStackConfigReset;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticResetter;
import com.sun.management.snmp.SnmpStatusException;

public class DiameterStackInfo extends DiameterStack {

	private static final String MODULE = "DIAMETER-STACK-MBEAN";
	private final Status stackStatus;
	private static final int RESET = 6;
	transient private final DiameterStatisticResetter diameterStatisticResetter;

	public DiameterStackInfo(Status stackStatus, DiameterStatisticResetter diameterStatisticResetter) {
		this.stackStatus = stackStatus;
		this.diameterStatisticResetter = diameterStatisticResetter;
	}

	@Override
	@Column(name = "stackListeningPort", type = Types.INTEGER)
	public Integer getStackListeningPort() throws SnmpStatusException {
		return Parameter.getInstance().getHostListeningPort();
	}

	@Override
	@Column(name = "stackIPAddress", type = Types.VARCHAR)
	public String getStackIPAddress() throws SnmpStatusException {
		return Parameter.getInstance().getHostIPAddress();
	}

	@Override
	@Column(name = "stackConfigReset", type = Types.VARCHAR)
	public EnumStackConfigReset getStackConfigReset()
			throws SnmpStatusException {
		return new EnumStackConfigReset(this.stackStatus.status);
	}

	@Override
	public void setStackConfigReset(EnumStackConfigReset x)
			throws SnmpStatusException {
		
		if (this.stackStatus == Status.RUNNING && x.intValue() == RESET) {
			diameterStatisticResetter.reset();
			LogManager.getLogger().info(MODULE, "Diameter Statistics Reset successfully.");
		}
	}

	@Override
	public void checkStackConfigReset(EnumStackConfigReset x)
			throws SnmpStatusException {
		
	}

	@Override
	public Long getStackResetTime() throws SnmpStatusException {
		return (System.currentTimeMillis() - Parameter.getInstance().getStackUpTime().getTime())/10;
	}

	@Override
	public Long getStackUpTime() throws SnmpStatusException {
		return (System.currentTimeMillis() - Parameter.getInstance().getStackUpTime().getTime())/10;
	}

	@Override
	@Column(name = "stackRealm", type = Types.VARCHAR)
	public String getStackRealm() throws SnmpStatusException {
		return Parameter.getInstance().getOwnDiameterRealm();
	}

	@Override
	@Column(name = "stackURI", type = Types.VARCHAR)
	public String getStackURI() throws SnmpStatusException {
		return Parameter.getInstance().getOwnDiameterURI();
	}

	@Override
	@Column(name = "stackIdentity", type = Types.VARCHAR)
	public String getStackIdentity() throws SnmpStatusException {
		return Parameter.getInstance().getOwnDiameterIdentity();
	}
}