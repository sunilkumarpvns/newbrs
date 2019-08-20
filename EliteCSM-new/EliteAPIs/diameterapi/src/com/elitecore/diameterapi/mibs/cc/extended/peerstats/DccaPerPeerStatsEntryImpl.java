package com.elitecore.diameterapi.mibs.cc.extended.peerstats;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.mbean.SnmpCounterUtil;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.mibs.cc.autogen.DccaPerPeerStatsEntry;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;

public class DccaPerPeerStatsEntryImpl extends DccaPerPeerStatsEntry {

	private static final String MODULE = "DCC-PEER-STATS_ENTRY";
	private String peerIdentity;
	transient private DiameterStatisticsProvider diameterStatisticProvider;
	transient private DiameterConfigProvider diameterConfigProvider;

	public DccaPerPeerStatsEntryImpl(String peerIdentity, 
			DiameterStatisticsProvider diameterStatisticProvider,
			DiameterConfigProvider diameterConfigProvider) {
		this.peerIdentity = peerIdentity;
		this.diameterStatisticProvider = diameterStatisticProvider;
		this.diameterConfigProvider = diameterConfigProvider;
	}

	@Override
	@Column(name = "dccaPerPeerStatsAAADropped", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsAAADropped(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getAnswerDroppedCount(CommandCode.AUTHENTICATION_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsAAAIn", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsAAAIn(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getAnswerInCount(CommandCode.AUTHENTICATION_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsAARDropped", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsAARDropped(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getRequestDroppedCount(CommandCode.AUTHENTICATION_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsAAROut", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsAAROut(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getRequestOutCount(CommandCode.AUTHENTICATION_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsSTADropped", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsSTADropped(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getAnswerDroppedCount(CommandCode.SESSION_TERMINATION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsSTAIn", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsSTAIn(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getAnswerInCount(CommandCode.SESSION_TERMINATION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsSTRDropped", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsSTRDropped(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getRequestDroppedCount(CommandCode.SESSION_TERMINATION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsSTROut", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsSTROut(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getRequestOutCount(CommandCode.SESSION_TERMINATION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsRAADropped", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsRAADropped(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getAnswerDroppedCount(CommandCode.RE_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsRAAOut", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsRAAOut(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getAnswerOutCount(CommandCode.RE_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsRARDropped", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsRARDropped(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getRequestDroppedCount(CommandCode.RE_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsRARIn", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsRARIn(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getRequestInCount(CommandCode.RE_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsCCADropped", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsCCADropped(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getAnswerDroppedCount(CommandCode.CREDIT_CONTROL.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsCCAOut", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsCCAOut(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getAnswerOutCount(CommandCode.CREDIT_CONTROL.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsCCAIn", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsCCAIn(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getAnswerInCount(CommandCode.CREDIT_CONTROL.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsCCRDropped", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsCCRDropped(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getRequestDroppedCount(CommandCode.CREDIT_CONTROL.code));
	}

	@Override
	@Column(name = "dccaPeerIdentity", type = java.sql.Types.VARCHAR)
	public String getDccaPeerIdentity(){
		return peerIdentity;
	}

	@Override
	@Column(name = "dccaPerPeerStatsCCROut", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsCCROut(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getRequestOutCount(CommandCode.CREDIT_CONTROL.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsCCRIn", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsCCRIn(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getRequestInCount(CommandCode.CREDIT_CONTROL.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsASADropped", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsASADropped(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getAnswerDroppedCount(CommandCode.ABORT_SESSION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsASAOut", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsASAOut(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getAnswerOutCount(CommandCode.ABORT_SESSION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsASRDropped", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsASRDropped(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getRequestDroppedCount(CommandCode.ABORT_SESSION.code));
	}

	@Override
	@Column(name = "dccaPerPeerStatsASRIn", type = java.sql.Types.BIGINT)
	public Long getDccaPerPeerStatsASRIn(){
		GroupedStatistics ccPeerStatistic = getPeerStatistics();
		if(ccPeerStatistic == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(ccPeerStatistic.getRequestInCount(CommandCode.ABORT_SESSION.code));
	}

	@Override
	public Long getDccaPeerIndex(){
		DiameterPeerConfig peerConfig = getPeerConfig();
		if(peerConfig == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(peerConfig.getDbpPeerIndex());
	}
	
	private GroupedStatistics getPeerStatistics(){
		GroupedStatistics ccPeerStatistic = diameterStatisticProvider.getPeerStatsMap().get(peerIdentity);
		if(ccPeerStatistic == null){
			LogManager.getLogger().error(MODULE, "CC Peer Statistics not found for peer:" + peerIdentity);
			Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.CCSTATISTICSNOTFOUND, MODULE, "CC Peer Statistics not found for peer:" + peerIdentity);		
			
		}
		return ccPeerStatistic;
	}
	
	private DiameterPeerConfig getPeerConfig() {
		DiameterPeerConfig ccHostConfig = diameterConfigProvider.getPeerConfig(peerIdentity);
		if(ccHostConfig == null){
			LogManager.getLogger().error(MODULE, "CC Host Config not found for peer:" + peerIdentity);
			Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.CCSTATISTICSNOTFOUND, MODULE, "CC Host Config not found for peer:" + peerIdentity);
		}
		return ccHostConfig;
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.CCA_PEER_STAT_TABLE + peerIdentity +"-"+ getPeerConfig().getPeerIpAddresses();
	}
}