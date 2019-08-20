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
import com.elitecore.diameterapi.mibs.custom.autogen.AppWiseStatisticsEntry;
import com.elitecore.diameterapi.mibs.statistics.ApplicationStatsIdentifier;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;
import com.sun.management.snmp.SnmpStatusException;

/**
 * This class represents the entry of peer wise application statistics.
 * Each entry will uniquely identified by composite key of
 * peerIndex.applicationID .
 *  
 * @author sanjay.dhamelia
 */

public class AppWiseStatisticsEntryMBeanImpl extends AppWiseStatisticsEntry {

	private static final long serialVersionUID = 1L;
	transient @Nonnull private final ApplicationStatsIdentifier applicationIdentifier;
	transient @Nonnull private final DiameterStatisticListener diameterStatisticListener;
	@Nonnull private String peerIdentity;
	private String compositeIndex;

	public AppWiseStatisticsEntryMBeanImpl(
			@Nonnull ApplicationStatsIdentifier applicationIdentifier,
			@Nonnull String peerIdentity,
			@Nonnull DiameterStatisticListener diameterStatisticListener) {

		this.applicationIdentifier = checkNotNull(applicationIdentifier, "applicationIdentifier is null.");
		this.peerIdentity = checkNotNull(peerIdentity, "peerIdentity is null.");
		this.diameterStatisticListener = checkNotNull(diameterStatisticListener, "diameterStatisticListener is null.");
	}

	private GroupedStatistics getAppToPeerStatistics() throws SnmpStatusException {
		
		Map<String, GroupedStatistics> appToPeerStatisticsMap = diameterStatisticListener.
				getDiameterStatisticProvider().getApplicationPeerMap().get(this.applicationIdentifier);
		
		if(Maps.isNullOrEmpty(appToPeerStatisticsMap)) {
			throw new SnmpStatusException("Group statistics is not found for application: "
						+ applicationIdentifier.getApplicationId());
		}
		
		GroupedStatistics appToPeerStats = appToPeerStatisticsMap.get(this.peerIdentity);
		
		if(appToPeerStats == null) {
			throw new SnmpStatusException("Peer Statistics is not found for peer: " + this.peerIdentity);
		}
		return appToPeerStats;
	}
	
	@Override
	@Column(name = "appwUnknownHbHAnswerDropped",type = Types.BIGINT)
	public Long getAppwUnknownHbHAnswerDropped() throws SnmpStatusException {
		return getAppToPeerStatistics().getTotalUnknownHbHAnswerDroppedCount();
	}

	@Override
	@Column(name = "appwAnswerDr",type = Types.BIGINT)
	public Long getAppwAnswerDr() throws SnmpStatusException {
		return getAppToPeerStatistics().getTotalAnswerDroppedCount();
	}

	@Override
	@Column(name = "appwRequestDr",type = Types.BIGINT)
	public Long getAppwRequestDr() throws SnmpStatusException {
		return getAppToPeerStatistics().getTotalRequestDroppedCount();
	}

	@Override
	@Column(name = "appwRequestTimeOut",type = Types.BIGINT)
	public Long getAppwRequestTimeOut() throws SnmpStatusException {
		return getAppToPeerStatistics().getTotalTimeoutRequestCount();
	}

	@Override
	@Column(name = "appwRequestRetransmitted",type = Types.BIGINT)
	public Long getAppwRequestRetransmitted() throws SnmpStatusException {
		return getAppToPeerStatistics().getTotalRequestsRetransmittedCount();
	}

	@Override
	@Column(name = "appwRequestPn",type = Types.BIGINT)
	public Long getAppwRequestPn() throws SnmpStatusException {
		return getAppToPeerStatistics().getTotalPendingRequestsCount();
	}

	@Override
	@Column(name = "appwAnswerRx",type = Types.BIGINT)
	public Long getAppwAnswerRx() throws SnmpStatusException {
		return getAppToPeerStatistics().getTotalAnswerInCount();
	}

	@Override
	@Column(name = "appwRequestTx",type = Types.BIGINT)
	public Long getAppwRequestTx() throws SnmpStatusException {
		return getAppToPeerStatistics().getTotalRequestOutCount();
	}

	@Override
	@Column(name = "appwMalformedPacketRx",type = Types.BIGINT)
	public Long getAppwMalformedPacketRx() throws SnmpStatusException {
		return getAppToPeerStatistics().getTotalMalformedPacketInCount();
	}

	@Override
	@Column(name = "appwDuplicateEtEAnswer",type = Types.BIGINT)
	public Long getAppwDuplicateEtEAnswer() throws SnmpStatusException {
		return getAppToPeerStatistics().getTotalDuplicateEtEAnswerCount();
	}

	@Override
	@Column(name = "appwAnswerTx",type = Types.BIGINT)
	public Long getAppwAnswerTx() throws SnmpStatusException {
		return getAppToPeerStatistics().getTotalAnswerOutCount();
	}

	@Override
	@Column(name = "appwDuplicateRequest",type = Types.BIGINT)
	public Long getAppwDuplicateRequest() throws SnmpStatusException {
		return getAppToPeerStatistics().getTotalDuplicateRequestCount();
	}

	@Override
	@Column(name = "appwRequestRx",type = Types.BIGINT)
	public Long getAppwRequestRx() throws SnmpStatusException {
		return getAppToPeerStatistics().getTotalRequestInCount();
	}

	@Override
	@Column(name = "peerIndex",type = Types.BIGINT)
	public Long getPeerIndex() throws SnmpStatusException {
		return this.diameterStatisticListener.getDiameterConfigProvider().
				getPeerConfig(this.peerIdentity).getDbpPeerIndex();
	}

	@Override
	@Column(name = "applicationID",type = Types.BIGINT)
	public Long getApplicationID() throws SnmpStatusException {
		return this.applicationIdentifier.getApplicationId();
	}
	
	@Override
	public String getAppwPeerIdentity() throws SnmpStatusException {
		return peerIdentity;
	}
	
	@Override
	public String getAppwApplicationName() throws SnmpStatusException {
		return ApplicationIdentifier.getDisplayName(getApplicationID());
	}
	
	@Override
	public String getAppwCompstIndexValue() throws SnmpStatusException {
		return this.compositeIndex;
	}

	public void setCompositeIndex(String compositeIndex) {
		this.compositeIndex = compositeIndex;
	}
	
	public String getObjectName() {
		return SnmpAgentMBeanConstant.DIAMETER_STACK_PEER_APP_WISE_TABLE 
				+ peerIdentity  
				+ "-" + applicationIdentifier.getApplicationId()+ "(" + applicationIdentifier.getApplication() + ")";
	}
}