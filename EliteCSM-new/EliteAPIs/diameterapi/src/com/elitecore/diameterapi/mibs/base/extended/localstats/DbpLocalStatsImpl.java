package com.elitecore.diameterapi.mibs.base.extended.localstats;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.core.util.mbean.SnmpCounterUtil;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.mibs.base.autogen.DbpLocalStats;
import com.elitecore.diameterapi.mibs.base.autogen.EnumDbpLocalConfigReset;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;

public class DbpLocalStatsImpl extends DbpLocalStats {
	
	
	
	transient private DiameterStatisticsProvider diameterStatisticProvider;

	public DbpLocalStatsImpl(DiameterStatisticsProvider diameterStatisticProvider) {
		this.diameterStatisticProvider = diameterStatisticProvider;
	}

	@Override
	@Column(name = "dbpLocalConfigReset", type = java.sql.Types.VARCHAR)
	public EnumDbpLocalConfigReset getDbpLocalConfigReset(){
		return new EnumDbpLocalConfigReset();
	}

	@Override
	public void setDbpLocalConfigReset(EnumDbpLocalConfigReset x){}

	@Override
	public void checkDbpLocalConfigReset(EnumDbpLocalConfigReset x){}

	@Override
	@Column(name = "dbpLocalResetTime", type = java.sql.Types.BIGINT)
	public Long getDbpLocalResetTime(){
		return SnmpCounterUtil.convertToCounter32((System.currentTimeMillis() - Parameter.getInstance().getStackUpTime().getTime())/10);
	}

	@Override
	@Column(name = "dbpLocalStatsTotalUpTime", type = java.sql.Types.BIGINT)
	public Long getDbpLocalStatsTotalUpTime(){
		return SnmpCounterUtil.convertToCounter32((System.currentTimeMillis() - diameterStatisticProvider.getStackStatistics().getLastResetTime().getTime())/10);
	}

	@Override
	@Column(name = "dbpLocalStatsTotalMessagesOut", type = java.sql.Types.BIGINT)
	public Long getDbpLocalStatsTotalMessagesOut()  {
		return SnmpCounterUtil.convertToCounter32(diameterStatisticProvider.geTotalOutMessages());
	}

	@Override
	@Column(name = "dbpLocalStatsTotalMessagesIn", type = java.sql.Types.BIGINT)
	public Long getDbpLocalStatsTotalMessagesIn()  {
		return SnmpCounterUtil.convertToCounter32(diameterStatisticProvider.getTotalInMessages());
	}

}
