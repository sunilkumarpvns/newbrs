package com.elitecore.diameterapi.mibs.base.extended.peerstats;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.mbean.SnmpCounterUtil;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.mibs.base.autogen.DbpPerPeerInfoEntry;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpPerPeerInfoLastDiscCause;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpPerPeerInfoState;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpPerPeerInfoWhoInitDisconnect;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpPerPeerStatsDWCurrentStatus;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;

public class DbpPerPeerInfoEntryImpl extends DbpPerPeerInfoEntry {
	
	private static final String MODULE = "DBP-PEER-INFO-ENTRY";
	private String hostIdentity;
	transient private DiameterStatisticsProvider diameterStatisticProvider;
	transient private DiameterConfigProvider diameterConfigProvider;
	
	public DbpPerPeerInfoEntryImpl(String hostIdentity, 
			DiameterStatisticsProvider diameterStatisticProvider,
			DiameterConfigProvider diameterConfigProvider){
		this.hostIdentity = hostIdentity;
		this.diameterStatisticProvider = diameterStatisticProvider;
		this.diameterConfigProvider = diameterConfigProvider;
	}

	@Override
	@Column(name = "dbpPerPeerStatsRAAsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsRAAsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerInCount(CommandCode.RE_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsRARsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsRARsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestOutCount(CommandCode.RE_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsRARsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsRARsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestInCount(CommandCode.RE_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsDPAsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsDPAsOut()  {
		
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerOutCount(CommandCode.DISCONNECT_PEER.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsDPAsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsDPAsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerInCount(CommandCode.DISCONNECT_PEER.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsDPRsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsDPRsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestOutCount(CommandCode.DISCONNECT_PEER.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsDPRsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsDPRsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestInCount(CommandCode.DISCONNECT_PEER.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsDWAsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsDWAsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerOutCount(CommandCode.DEVICE_WATCHDOG.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsDWAsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsDWAsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerInCount(CommandCode.DEVICE_WATCHDOG.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsDWRsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsDWRsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestOutCount(CommandCode.DEVICE_WATCHDOG.code));
	}

	@Override
	@Column(name = "dbpPeerIdentity", type = java.sql.Types.VARCHAR)
	public String getDbpPeerIdentity()  {
		return this.hostIdentity;
	}

	@Override
	@Column(name = "dbpPerPeerStatsTransportDown", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsTransportDown()  {
			// FIXME
			return 0L;
	}

	@Override
	@Column(name = "dbpPerPeerStatsASAsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsASAsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerInCount(CommandCode.ABORT_SESSION.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsASRsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsASRsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestOutCount(CommandCode.ABORT_SESSION.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsDWRsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsDWRsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestInCount(CommandCode.DEVICE_WATCHDOG.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsASRsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsASRsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestInCount(CommandCode.ABORT_SESSION.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsCEAsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsCEAsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerOutCount(CommandCode.CAPABILITIES_EXCHANGE.code));
	}

	@Override
	@Column(name = "PermanentFailures", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsPermanentFailures(){
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getResultCodeOutCount(ResultCodeCategory.RC5XXX.value));
	}

	@Override
	@Column(name = "StatsTimeoutConnAtmpts", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsTimeoutConnAtmpts(){
		DiameterPeerConfig peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerConfig.getDbpPerPeerStatsTimeoutConnAtmpts());
	}

	@Override
	@Column(name = "dbpPerPeerStatsCEAsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsCEAsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerInCount(CommandCode.CAPABILITIES_EXCHANGE.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsDWCurrentStatus", type = java.sql.Types.VARCHAR)
	public EnumDbpPerPeerStatsDWCurrentStatus getDbpPerPeerStatsDWCurrentStatus(){
		
		DiameterPeerConfig peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return new EnumDbpPerPeerStatsDWCurrentStatus();
		}
		try{
			return new EnumDbpPerPeerStatsDWCurrentStatus(peerConfig.getPCBState() + 1);
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in creating DBP peer DW current status for peer("+hostIdentity+"). Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			return new EnumDbpPerPeerStatsDWCurrentStatus();
		}
	}

	@Override
	@Column(name = "dbpPerPeerStatsCERsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsCERsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestOutCount(CommandCode.CAPABILITIES_EXCHANGE.code));
	}

	@Override
	@Column(name = "TransientFailures", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsTransientFailures(){
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getResultCodeOutCount(ResultCodeCategory.RC4XXX.value));
	}

	@Override
	@Column(name = "WhoInitDisconnect", type = java.sql.Types.VARCHAR)
	public EnumDbpPerPeerInfoWhoInitDisconnect getDbpPerPeerInfoWhoInitDisconnect(){
		return new EnumDbpPerPeerInfoWhoInitDisconnect();
	}

	@Override
	@Column(name = "dbpPerPeerStatsCERsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsCERsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestInCount(CommandCode.CAPABILITIES_EXCHANGE.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsProtocolErrors", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsProtocolErrors()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getResultCodeOutCount(ResultCodeCategory.RC3XXX.value) - 
				peerStatistic.getResultCodeOutCount(ResultCode.DIAMETER_REDIRECT_INDICATION.code));
	}

	@Override
	@Column(name = "dbpPerPeerInfoLastDiscCause", type = java.sql.Types.VARCHAR)
	public EnumDbpPerPeerInfoLastDiscCause getDbpPerPeerInfoLastDiscCause(){
		return new EnumDbpPerPeerInfoLastDiscCause();
	}

	@Override
	@Column(name = "dbpPerPeerStatsACAsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsACAsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerOutCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsUnknownTypes", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsUnknownTypes()  {
		// FIXME
		return 0L;
	}

	@Override
	@Column(name = "dbpPerPeerInfoStateDuration", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerInfoStateDuration()  {
		DiameterPeerConfig peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return 0L;
		}
		return peerConfig.getDbpPerPeerInfoStateDuration() / 10;
	}

	@Override
	@Column(name = "dbpPerPeerStatsACAsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsACAsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerInCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsEToEDupMessages", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsEToEDupMessages()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getTotalDuplicateEtEAnswerCount());
	}

	@Override
	@Column(name = "dbpPerPeerInfoState", type = java.sql.Types.VARCHAR)
	public EnumDbpPerPeerInfoState getDbpPerPeerInfoState(){
		DiameterPeerConfig peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return new EnumDbpPerPeerInfoState();
		}
		
		try{
			return new EnumDbpPerPeerInfoState(peerConfig.getPeerState() +1);
			
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in creating DBP peer info stats for peer("+hostIdentity+"). Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			return new EnumDbpPerPeerInfoState();
		}
	}

	@Override
	@Column(name = "dbpPerPeerStatsACRsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsACRsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestOutCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "HByHDropMessages", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsHByHDropMessages()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getTotalUnknownHbHAnswerDroppedCount());
	}

	@Override
	@Column(name = "dbpPerPeerStatsACRsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsACRsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestInCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "AccReqstsDropped", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsAccReqstsDropped()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestDroppedCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsASAsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsASAsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerOutCount(CommandCode.ABORT_SESSION.code));
	}

	@Override
	@Column(name = "AccPendReqstsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsAccPendReqstsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getPendingRequestsCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsTotalRetrans", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsTotalRetrans()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getTotalRequestsRetransmittedCount() - 
				peerStatistic.getRequestsRetransmittedCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsAccRetrans", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsAccRetrans()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestsRetransmittedCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsAccsNotRecorded", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsAccsNotRecorded()  {
		// FIXME
		return 0L;
	}

	@Override
	@Column(name = "dbpPerPeerStatsMalformedReqsts", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsMalformedReqsts()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getTotalMalformedPacketInCount());
	}

	@Override
	@Column(name = "dbpPerPeerStatsAccDupRequests", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsAccDupRequests()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getDuplicateRequestCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsRedirectEvents", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsRedirectEvents()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getResultCodeInCount(ResultCode.DIAMETER_REDIRECT_INDICATION.code));
	}

	@Override
	@Column(name = "dbpPerPeerInfoDWReqTimer", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerInfoDWReqTimer()  {
		DiameterPeerConfig peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerConfig.getPeerWatchDogInterval());
	}

	@Override
	@Column(name = "dbpPerPeerStatsSTAsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsSTAsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerOutCount(CommandCode.SESSION_TERMINATION.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsSTAsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsSTAsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerInCount(CommandCode.SESSION_TERMINATION.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsSTRsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsSTRsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestOutCount(CommandCode.SESSION_TERMINATION.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsSTRsIn", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsSTRsIn()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getRequestInCount(CommandCode.SESSION_TERMINATION.code));
	}

	@Override
	@Column(name = "dbpPerPeerStatsRAAsOut", type = java.sql.Types.BIGINT)
	public Long getDbpPerPeerStatsRAAsOut()  {
		GroupedStatistics peerStatistic = getDiameterPeerStatistic();
		if(peerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerStatistic.getAnswerOutCount(CommandCode.RE_AUTHORIZATION.code));
	}

	@Override
	public Long getDbpPeerIndex()  {
		DiameterPeerConfig peerConfig = getDiameterPeerConfig();
		if(peerConfig == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerConfig.getDbpPeerIndex());
	}
	
	private DiameterPeerConfig getDiameterPeerConfig(){
		DiameterPeerConfig peerConfig = diameterConfigProvider.getPeerConfig(hostIdentity);
		if(peerConfig == null){
			LogManager.getLogger().error(MODULE, "Base Peer Config not found for peer:" + hostIdentity);
			Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.BASESTATISTICSNOTFOUND, MODULE, "Base Peer Config not found for peer:" + hostIdentity);
		}
		return peerConfig;
	}
	
	private GroupedStatistics getDiameterPeerStatistic(){
		GroupedStatistics peerStatistic = diameterStatisticProvider.getPeerStatsMap().get(hostIdentity);
		if(peerStatistic == null){
			LogManager.getLogger().error(MODULE, "Base Peer Statistics not found for peer:" + hostIdentity);
			Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.BASESTATISTICSNOTFOUND, MODULE, "Base Peer Statistics not found for peer:" + hostIdentity);
		}
		return peerStatistic;
	}

	public String getObjectName(){
		return SnmpAgentMBeanConstant.PEER_INFO_TABLE + hostIdentity+"-"+getDiameterPeerConfig().getPeerIpAddresses();
	}
}
