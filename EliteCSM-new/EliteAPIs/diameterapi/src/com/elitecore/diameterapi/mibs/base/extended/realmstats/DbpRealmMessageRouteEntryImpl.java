package com.elitecore.diameterapi.mibs.base.extended.realmstats;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.mbean.SnmpCounterUtil;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.mibs.base.autogen.DbpRealmMessageRouteEntry;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpRealmMessageRouteAction;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpRealmMessageRouteType;
import com.elitecore.diameterapi.mibs.constants.SnmpAgentMBeanConstant;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;
import com.elitecore.diameterapi.mibs.statistics.RealmIdentifier;

public class DbpRealmMessageRouteEntryImpl extends DbpRealmMessageRouteEntry {
	
	private static final String MODULE = "DBP-REALM-MSG-ROUTE-ENTRY";
	transient private RealmIdentifier realm;
	private long index;
	transient private DiameterStatisticsProvider diameterStatisticProvider;
	
	public DbpRealmMessageRouteEntryImpl(int index,RealmIdentifier realm, DiameterStatisticsProvider diameterStatisticProvider){
		this.index = index;
		this.realm = realm;
		this.diameterStatisticProvider = diameterStatisticProvider;
	}

	@Override
	@Column(name = "dbpRealmMessageRouteASRsOut", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteASRsOut(){
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getRequestOutCount(CommandCode.ABORT_SESSION.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteASRsIn", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteASRsIn()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getRequestInCount(CommandCode.ABORT_SESSION.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteSTAsOut", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteSTAsOut()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getAnswerOutCount(CommandCode.SESSION_TERMINATION.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteSTAsIn", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteSTAsIn()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getAnswerInCount(CommandCode.SESSION_TERMINATION.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteSTRsOut", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteSTRsOut()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getRequestOutCount(CommandCode.SESSION_TERMINATION.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteSTRsIn", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteSTRsIn()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getRequestInCount(CommandCode.SESSION_TERMINATION.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteRAAsOut", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteRAAsOut()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getAnswerOutCount(CommandCode.RE_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteRAAsIn", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteRAAsIn()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getAnswerInCount(CommandCode.RE_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteRARsOut", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteRARsOut()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getRequestOutCount(CommandCode.RE_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteRARsIn", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteRARsIn()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getRequestInCount(CommandCode.RE_AUTHORIZATION.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteACAsOut", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteACAsOut()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getAnswerOutCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteACAsIn", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteACAsIn()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getAnswerInCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteACRsOut", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteACRsOut()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getRequestOutCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteACRsIn", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteACRsIn()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getRequestInCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteAction", type = java.sql.Types.VARCHAR)
	public EnumDbpRealmMessageRouteAction getDbpRealmMessageRouteAction(){
		return new EnumDbpRealmMessageRouteAction(realm.getDbpRealmMessageRouteAction().routingAction);
	}

	@Override
	@Column(name = "dbpRealmMessageRouteType", type = java.sql.Types.VARCHAR)
	public EnumDbpRealmMessageRouteType getDbpRealmMessageRouteType(){
		return new EnumDbpRealmMessageRouteType(realm.getDbpRealmMessageRouteType().code);
	}

	@Override
	@Column(name = "ReqstsDrop", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteReqstsDrop()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getTotalRequestDroppedCount());
	}

	@Override
	@Column(name = "dbpRealmMessageRouteApp", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteApp()  {
		return SnmpCounterUtil.convertToCounter32(realm.getDbpRealmMessageRouteApp());
	}

	@Override
	@Column(name = "PendReqstsOut", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRoutePendReqstsOut() {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getPendingRequestsCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "AccDupReqsts", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteAccDupReqsts() {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getDuplicateRequestCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteRealm", type = java.sql.Types.VARCHAR)
	public String getDbpRealmMessageRouteRealm()  {
		return realm.getDbpRealmMessageRouteRealm();
	}

	@Override
	@Column(name = "dbpRealmMessageRouteIndex", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteIndex()  {
		return SnmpCounterUtil.convertToCounter32(index);
	}

	@Override
	@Column(name = "dbpRealmMessageRouteAccRetrans", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteAccRetrans()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getRequestsRetransmittedCount(CommandCode.ACCOUNTING.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteASAsOut", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteASAsOut()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getAnswerOutCount(CommandCode.ABORT_SESSION.code));
	}

	@Override
	@Column(name = "dbpRealmMessageRouteASAsIn", type = java.sql.Types.BIGINT)
	public Long getDbpRealmMessageRouteASAsIn()  {
		GroupedStatistics realmStatistics = getDiameterRealmStatistics();
		if(realmStatistics == null){
			return 0L;
		}
		return SnmpCounterUtil.convertToCounter32(realmStatistics.getAnswerInCount(CommandCode.ABORT_SESSION.code));
	}
	
	private GroupedStatistics getDiameterRealmStatistics(){
		GroupedStatistics realmStatistic = diameterStatisticProvider.getRealmStatsMap().get(realm);
		if(realmStatistic == null){
			LogManager.getLogger().error(MODULE, "Realm Statistics not found for realm:" + realm);
			Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.BASESTATISTICSNOTFOUND, MODULE, "Base Realm Statistics not found for realm:" + realm);
		}
		return realmStatistic;
	}
	
	public String getObjectName(){
		return SnmpAgentMBeanConstant.REALM_MSG_ROUTE_TABLE + realm.getRealmName();
	}
}