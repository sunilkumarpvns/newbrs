package com.elitecore.diameterapi.mibs.custom.extended;

import java.sql.Types;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.diameterapi.mibs.custom.autogen.StackStatistics;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;
import com.sun.management.snmp.SnmpStatusException;

public class StackStatisticsMbeanImpl extends StackStatistics {

	transient private final DiameterStatisticsProvider statisticsProvider;

	public StackStatisticsMbeanImpl(DiameterStatisticsProvider diameterStatisticsProvider) {
		this.statisticsProvider = diameterStatisticsProvider;
	}
	
	private GroupedStatistics getStackStatistics() {
		return this.statisticsProvider.getStackStatistics();
	}
	
	@Override
	@Column(name = "totalDuplicateRequest", type = Types.BIGINT)
	public Long getTotalDuplicateRequest() throws SnmpStatusException {
		return this.getStackStatistics().getTotalDuplicateRequestCount();
	}

	@Override
	@Column(name = "totalUnknownHbHAnswerDropped", type = Types.BIGINT)
	public Long getTotalUnknownHbHAnswerDropped() throws SnmpStatusException {
		return this.getStackStatistics().getTotalUnknownHbHAnswerDroppedCount();
	}

	@Override
	@Column(name = "totalRequestRetransmitted", type = Types.BIGINT)
	public Long getTotalRequestRetransmitted() throws SnmpStatusException {
		return this.getStackStatistics().getTotalRequestsRetransmittedCount();
	}

	@Override
	@Column(name = "totalRequestDr", type = Types.BIGINT)
	public Long getTotalRequestDr() throws SnmpStatusException {
		return this.getStackStatistics().getTotalRequestDroppedCount();
	}

	@Override
	@Column(name = "totalAnswerDr", type = Types.BIGINT)
	public Long getTotalAnswerDr() throws SnmpStatusException {
		return this.getStackStatistics().getTotalAnswerDroppedCount();
	}

	@Override
	@Column(name = "totalRequestPn", type = Types.BIGINT)
	public Long getTotalRequestPn() throws SnmpStatusException {
		return this.getStackStatistics().getTotalPendingRequestsCount();
	}

	@Override
	@Column(name = "totalAnswerTx", type = Types.BIGINT)
	public Long getTotalAnswerTx() throws SnmpStatusException {
		return this.getStackStatistics().getTotalAnswerOutCount();
	}

	@Override
	@Column(name = "totalRequestTimeOut", type = Types.BIGINT)
	public Long getTotalRequestTimeOut() throws SnmpStatusException {
		return this.getStackStatistics().getTotalTimeoutRequestCount();
	}

	@Override
	@Column(name = "totalAnswerRx", type = Types.BIGINT)
	public Long getTotalAnswerRx() throws SnmpStatusException {
		return this.getStackStatistics().getTotalAnswerInCount();
	}

	@Override
	@Column(name = "totalMalformedPacketRx", type = Types.BIGINT)
	public Long getTotalMalformedPacketRx() throws SnmpStatusException {
		return this.getStackStatistics().getTotalMalformedPacketInCount();
	}

	@Override
	@Column(name = "totalRequestTx", type = Types.BIGINT)
	public Long getTotalRequestTx() throws SnmpStatusException {
		return this.getStackStatistics().getTotalRequestOutCount();
	}

	@Override
	@Column(name = "totalDuplicateEtEAnswer", type = Types.BIGINT)
	public Long getTotalDuplicateEtEAnswer() throws SnmpStatusException {
		return this.getStackStatistics().getTotalDuplicateEtEAnswerCount();
	}

	@Override
	@Column(name = "totalRequestRx", type = Types.BIGINT)
	public Long getTotalRequestRx() throws SnmpStatusException {
		return this.getStackStatistics().getTotalRequestInCount();
	}
}