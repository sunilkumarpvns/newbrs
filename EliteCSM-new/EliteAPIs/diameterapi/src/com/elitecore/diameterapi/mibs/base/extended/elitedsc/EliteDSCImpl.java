package com.elitecore.diameterapi.mibs.base.extended.elitedsc;

import com.elitecore.core.util.mbean.SnmpCounterUtil;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;
import com.elitecore.diameterapi.mibs.base.autogen.EliteDSC;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;
import com.sun.management.snmp.SnmpStatusException;

// TODO remove routing session count counter from MIB
public class EliteDSCImpl extends EliteDSC {
	
	transient private IDiameterStackContext context; 
	transient private DiameterStatisticsProvider diameterStatisticProvider;
	public EliteDSCImpl(IDiameterStackContext context, DiameterStatisticsProvider diameterStatisticProvider) {
		this.context = context;
		this.diameterStatisticProvider = diameterStatisticProvider;
	}

	@Override
	public Long getAvgIncomingMPS() throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(diameterStatisticProvider.getAvgIncomingMPS());
	}

	@Override
	public Long getAvgRoundTripTime() throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(diameterStatisticProvider.getAvgRoundTripTime());
	}

	@Override
	public Long getTotalActiveSession() throws SnmpStatusException {
		return SnmpCounterUtil.convertToCounter32(context.getTotalActiveSessionCount());
	}
}