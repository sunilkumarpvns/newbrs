package com.elitecore.diameterapi.mibs.custom.extended;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.sql.Types;

import javax.annotation.Nonnull;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.mibs.base.DiameterStatisticListener;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.custom.autogen.ApplicationStatisticsEntry;
import com.elitecore.diameterapi.mibs.statistics.ApplicationStatsIdentifier;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;
import com.sun.management.snmp.SnmpStatusException;

/**
 * This class represents the entry of application statistics.
 * So, it will maintains the global statistics for each application;
 * identified by applicationID viz index for application entry.
 * 
 * @author sanjay.dhamelia
 */
public class AppStatisticsEntryMBeanImpl extends ApplicationStatisticsEntry {

	private static final long serialVersionUID = 1L;
	@Nonnull transient private final ApplicationStatsIdentifier applicationIdentifier;
	@Nonnull transient private final DiameterStatisticListener diameterStatisticListener;
	private String compositeIndex;

	public AppStatisticsEntryMBeanImpl(
			@Nonnull ApplicationStatsIdentifier applicationIdentifier, 
			@Nonnull DiameterStatisticListener diameterStatisticListener) {
		
		this.applicationIdentifier = checkNotNull(applicationIdentifier, "applicationIdentifier is null.");
		this.diameterStatisticListener = checkNotNull(diameterStatisticListener, "diameterStatisticListener is null.");
		
	}

	private GroupedStatistics getApplicationStatistics() throws SnmpStatusException {
		GroupedStatistics groupedStatistics = diameterStatisticListener.getDiameterStatisticProvider().getApplicationMap().get(applicationIdentifier);
		if(groupedStatistics == null) {
			throw new SnmpStatusException("Group statistics is not found for application: "+ applicationIdentifier.getApplicationId());
		}
		return groupedStatistics;
	}
	
	@Override
	@Column(name = "applicationID", type = Types.BIGINT)
	public Long getApplicationID() throws SnmpStatusException {
		return this.applicationIdentifier.getApplicationId();
	}

	@Override
	@Column(name = "appStatsRequestDr", type = Types.BIGINT)
	public Long getAppStatsRequestDr() throws SnmpStatusException {
		return getApplicationStatistics().getTotalRequestDroppedCount();
	}

	@Override
	@Column(name = "appStatsRequestTimeOut", type = Types.BIGINT)
	public Long getAppStatsRequestTimeOut() throws SnmpStatusException {
		return getApplicationStatistics().getTotalTimeoutRequestCount();
	}

	@Override
	@Column(name = "appStatsRequestRetransmitted", type = Types.BIGINT)
	public Long getAppStatsRequestRetransmitted() throws SnmpStatusException {
		return getApplicationStatistics().getTotalRequestsRetransmittedCount();
	}

	@Override
	@Column(name = "appStatsAnswerRx", type = Types.BIGINT)
	public Long getAppStatsAnswerRx() throws SnmpStatusException {
		return getApplicationStatistics().getTotalAnswerInCount();
	}

	@Override
	@Column(name = "appStatsRequestPn", type = Types.BIGINT)
	public Long getAppStatsRequestPn() throws SnmpStatusException {
		return getApplicationStatistics().getTotalPendingRequestsCount();
	}

	@Override
	@Column(name = "appStatsMalformedPacketRx", type = Types.BIGINT)
	public Long getAppStatsMalformedPacketRx() throws SnmpStatusException {
		return getApplicationStatistics().getTotalMalformedPacketInCount();
	}

	@Override
	@Column(name = "appStatsRequestTx", type = Types.BIGINT)
	public Long getAppStatsRequestTx() throws SnmpStatusException {
		return getApplicationStatistics().getTotalRequestOutCount();
	}

	@Override
	@Column(name = "appStatsDuplicateEtEAnswer", type = Types.BIGINT)
	public Long getAppStatsDuplicateEtEAnswer() throws SnmpStatusException {
		return getApplicationStatistics().getTotalDuplicateEtEAnswerCount();
	}

	@Override
	@Column(name = "appStatsAnswerTx", type = Types.BIGINT)
	public Long getAppStatsAnswerTx() throws SnmpStatusException {
		return getApplicationStatistics().getTotalAnswerOutCount();
	}

	@Override
	@Column(name = "appStatsRequestRx", type = Types.BIGINT)
	public Long getAppStatsRequestRx() throws SnmpStatusException {
		return getApplicationStatistics().getTotalRequestInCount();
	}

	@Override
	@Column(name = "appStatsDuplicateRequest", type = Types.BIGINT)
	public Long getAppStatsDuplicateRequest() throws SnmpStatusException {
		return getApplicationStatistics().getTotalDuplicateRequestCount();
	}

	@Override
	@Column(name = "UnknownHbHAnswerDropped", type = Types.BIGINT)
	public Long getAppStatsUnknownHbHAnswerDropped() throws SnmpStatusException {
		return getApplicationStatistics().getTotalUnknownHbHAnswerDroppedCount();
	}

	@Override
	@Column(name = "appStatsAnswerDr", type = Types.BIGINT)
	public Long getAppStatsAnswerDr() throws SnmpStatusException {
		return getApplicationStatistics().getTotalAnswerDroppedCount();
	}
	
	@Override
	public String getApplicationName() throws SnmpStatusException {
		return ApplicationIdentifier.getDisplayName(getApplicationID());
	}
	
	@Override
	public String getApplicationIDIndexValue() throws SnmpStatusException {
		return compositeIndex;
	}

	public void setCompositeIndex(String compositeIndex) {
		this.compositeIndex = compositeIndex;
	}
	

	public String getObjectName() {
		return SnmpAgentMBeanConstant.DIAMETER_STACK_APP_STATISTICS_TABLE 
				+ applicationIdentifier.getApplicationId()+ "(" + applicationIdentifier.getApplication() + ")";
	}
}