package com.elitecore.diameterapi.mibs.custom.extended;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.sql.Types;
import java.util.Map;

import javax.annotation.Nonnull;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.mibs.base.DiameterStatisticListener;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.custom.autogen.CommandCodeStatisticsEntry;
import com.elitecore.diameterapi.mibs.statistics.ApplicationStatsIdentifier;
import com.elitecore.diameterapi.mibs.statistics.CounterTuple;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;
import com.sun.management.snmp.SnmpStatusException;

/**
 * This class represents the entry of peer wise application and commandcode 
 * statistics. Each entry will uniquely identified by composite key of
 * peerIndex.applicationID.commandCode .
 *  
 * @author sanjay.dhamelia
 */

public class CommandCodeWiseStatisticsEntryMBeanImpl extends CommandCodeStatisticsEntry {
	
	private static final long serialVersionUID = 1L;
	transient @Nonnull private final ApplicationStatsIdentifier applicationIdentifier;
	transient @Nonnull private final DiameterStatisticListener diameterStatisticListener;
	private final String peerIdentity;
	private final int commandCode;
	private String compositeIndex;

	public CommandCodeWiseStatisticsEntryMBeanImpl(@Nonnull ApplicationStatsIdentifier applicationIdentifier,
			@Nonnull String hostIdentity, int commandCode,
			@Nonnull DiameterStatisticListener diameterStatisticListener) {
				
		this.applicationIdentifier = checkNotNull(applicationIdentifier, "applicationIdentifier is null.");
		this.peerIdentity = checkNotNull(hostIdentity, "hostIdentity is null.");
		this.diameterStatisticListener = checkNotNull(diameterStatisticListener, "diameterStatisticListener is null.");
		this.commandCode = commandCode;
	}

	private CounterTuple getCCWiseStatistics() throws SnmpStatusException {
		
		Map<String, GroupedStatistics> appToPeerStatisticsMap = diameterStatisticListener.
				getDiameterStatisticProvider().getApplicationPeerMap().get(this.applicationIdentifier);
		
		if(Maps.isNullOrEmpty(appToPeerStatisticsMap)) {
			throw new SnmpStatusException("Group statistics is not found for application: "+ applicationIdentifier);
		}
		
		CounterTuple counterTuple = appToPeerStatisticsMap.get(this.peerIdentity).
				getCommandCodeCountersMap().get(this.commandCode);
		
		if(counterTuple == null) {
			throw new SnmpStatusException("Statistics of command code:" + this.commandCode +" " +
					"not found for peer: "+ this.peerIdentity);
		}
		return counterTuple;
	}
	
	@Override
	@Column(name = "applicationID", type = Types.BIGINT)
	public Long getApplicationID() throws SnmpStatusException {
		return applicationIdentifier.getApplicationId();
	}

	@Override
	@Column(name = "commandCode", type = Types.BIGINT)
	public Long getCommandCode() throws SnmpStatusException {
		return new Long(this.commandCode);
	}

	@Override
	@Column(name = "peerIndex", type = Types.BIGINT)
	public Long getPeerIndex() throws SnmpStatusException {
		return diameterStatisticListener.getDiameterConfigProvider().getPeerConfig(this.peerIdentity).getDbpPeerIndex();
	}

	@Override
	@Column(name = "ccwRequestDr", type = Types.BIGINT)
	public Long getCcwRequestDr() throws SnmpStatusException {
		return getCCWiseStatistics().getRequestDroppedCount();
	}

	@Override
	@Column(name = "ccwRequestTimeOut", type = Types.BIGINT)
	public Long getCcwRequestTimeOut() throws SnmpStatusException {
		return getCCWiseStatistics().getTimeoutRequestStatistics();
	}

	@Override
	@Column(name = "ccwRequestRetransmitted", type = Types.BIGINT)
	public Long getCcwRequestRetransmitted() throws SnmpStatusException {
		return getCCWiseStatistics().getRequestsRetransmittedCount();
	}

	@Override
	@Column(name = "ccwAnswerRx", type = Types.BIGINT)
	public Long getCcwAnswerRx() throws SnmpStatusException {
		return getCCWiseStatistics().getAnswerInCount();
	}

	@Override
	@Column(name = "ccwRequestPn", type = Types.BIGINT)
	public Long getCcwRequestPn() throws SnmpStatusException {
		return getCCWiseStatistics().getPendingRequestCount();
	}

	@Override
	@Column(name = "ccwRequestTx", type = Types.BIGINT)
	public Long getCcwRequestTx() throws SnmpStatusException {
		return getCCWiseStatistics().getRequestOutCount();
	}

	@Override
	@Column(name = "ccwMalformedPacketRx", type = Types.BIGINT)
	public Long getCcwMalformedPacketRx() throws SnmpStatusException {
		return getCCWiseStatistics().getMalformedPacketReceivedCount();
	}

	@Override
	@Column(name = "ccwAnswerTx", type = Types.BIGINT)
	public Long getCcwAnswerTx() throws SnmpStatusException {
		return getCCWiseStatistics().getAnswerOutCount();
	}

	@Override
	@Column(name = "ccwDuplicateEtEAnswer", type = Types.BIGINT)
	public Long getCcwDuplicateEtEAnswer() throws SnmpStatusException {
		return getCCWiseStatistics().getDuplicateEtEAnswerCount();
	}

	@Override
	@Column(name = "ccwRequestRx", type = Types.BIGINT)
	public Long getCcwRequestRx() throws SnmpStatusException {
		return getCCWiseStatistics().getRequestInCount();
	}

	@Override
	@Column(name = "ccwDuplicateRequest", type = Types.BIGINT)
	public Long getCcwDuplicateRequest() throws SnmpStatusException {
		return getCCWiseStatistics().getDuplicateRequestCount();
	}

	@Override
	@Column(name = "ccwUnknownHbHAnswerDropped", type = Types.BIGINT)
	public Long getCcwUnknownHbHAnswerDropped()
			throws SnmpStatusException {
		return getCCWiseStatistics().getUnknownHbHAnswerDroppedCount();
	}

	@Override
	@Column(name = "ccwAnswerDr", type = Types.BIGINT)
	public Long getCcwAnswerDr() throws SnmpStatusException {
		return getCCWiseStatistics().getAnswerDroppedCount();
	}

	@Override
	@Column(name = "commandCodeName", type = Types.VARCHAR)
	public String getCommandCodeName() throws SnmpStatusException {
		return com.elitecore.diameterapi.diameter.common.util.constant.CommandCode.getDisplayName(this.commandCode);
	}

	@Override
	public String getCcwCompstIndexValue() throws SnmpStatusException {
		return compositeIndex;
	}

	@Override
	public String getCcwApplicationName() throws SnmpStatusException {
		return ApplicationIdentifier.getDisplayName(getApplicationID());
	}

	@Override
	public String getCcwPeerIdentity() throws SnmpStatusException {
		return peerIdentity;
	}

	public void setCompositeIndex(String compositeIndex) {
		this.compositeIndex = compositeIndex;
	}
	
	public String getObjectName() {
		return SnmpAgentMBeanConstant.DIAMETER_STACK_PEER_APP_CC_WISE_TABLE 
				+ peerIdentity  
				+ "-" + applicationIdentifier.getApplicationId()+ "(" + applicationIdentifier.getApplication() + ")-"
				+ commandCode + (com.elitecore.diameterapi.diameter.common.util.constant.CommandCode.isValid(commandCode) ? "(" + com.elitecore.diameterapi.diameter.common.util.constant.CommandCode.getDisplayName(commandCode)+ ")" : "");
	}
}