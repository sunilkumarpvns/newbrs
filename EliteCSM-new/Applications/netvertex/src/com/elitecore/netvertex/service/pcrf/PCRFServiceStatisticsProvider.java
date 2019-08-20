package com.elitecore.netvertex.service.pcrf;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.mbean.SnmpCounterUtil;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode.EnumPcrfStatisticsReset;
import com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode.PcrfStatistics;

public class PCRFServiceStatisticsProvider extends PcrfStatistics{

	private static final String MODULE = "PCRF-SRV-STATS-PROVIDER";
	transient private PCRFServiceCounters pcrfServiceCounters;
	transient private PCRFServiceContext pcrfServiceContext;
	public PCRFServiceStatisticsProvider(PCRFServiceCounters pcrfServiceCounters, PCRFServiceContext pcrfServiceContext) {
		this.pcrfServiceContext = pcrfServiceContext;
		this.pcrfServiceCounters = pcrfServiceCounters;
	}

	@Override
	public Long getTotalRejectResponses(){
		return SnmpCounterUtil.convertToCounter32(pcrfServiceCounters.getTotalRejectPCRFRespCntr());
	}

	@Override
	public Long getTotalSuccessResponses(){
		return SnmpCounterUtil.convertToCounter32(pcrfServiceCounters.getTotalSuccessPCRFRespCntr());
	}

	@Override
	public Long getTotalRequestDropped(){
		return SnmpCounterUtil.convertToCounter32(pcrfServiceCounters.getTotalPCRFReqDroppedCntr());
	}

	@Override
	public Long getTotalResponses(){
		return SnmpCounterUtil.convertToCounter32(pcrfServiceCounters.getTotalPCRFRespCntr());
	}

	@Override
	public Long getTotalRequests(){
		return SnmpCounterUtil.convertToCounter32(pcrfServiceCounters.getTotalPCRFReqCntr());
	}

	@Override
	public EnumPcrfStatisticsReset getPcrfStatisticsReset(){
		int statsReset = pcrfServiceCounters.getStatisticsReset();
		try{
			return new EnumPcrfStatisticsReset(statsReset);
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Error while creating PCRF Statistics Reset Enum. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return new EnumPcrfStatisticsReset(PCRFServiceCounters.STATS_OTHER);
		}
	}

	@Override
	public void setPcrfStatisticsReset(EnumPcrfStatisticsReset pcrfStatisticsReset) throws IllegalArgumentException{
		if(pcrfStatisticsReset == null){
			LogManager.getLogger().error(MODULE, "Unable to reset PCRF service statistics. Reason: statistics reset value received is null");
			throw new IllegalArgumentException();
		}

		if(pcrfStatisticsReset.intValue() == PCRFServiceCounters.STATS_RESET){
			pcrfServiceCounters.resetStatistics();
		}else{
			LogManager.getLogger().error(MODULE, "Unable to reset PCRF service statistics. Reason: Invalid statistics reset value received: " + pcrfStatisticsReset.intValue());
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void checkPcrfStatisticsReset(EnumPcrfStatisticsReset x){
	}

	@Override
	public Long getPcrfStatisticsResetTime(){
		return SnmpCounterUtil.convertToCounter32((System.currentTimeMillis() - pcrfServiceCounters.getStatisicsResetTime()) / 10);
	}

	@Override
	public Long getPcrfServiceUpTime(){
		return SnmpCounterUtil.convertToCounter32((System.currentTimeMillis() - pcrfServiceCounters.getServiceUpTime()) / 10);
	}
	
	@Override
	public Long getAvgRequestProcessingTime(){
		return SnmpCounterUtil.convertToCounter32(pcrfServiceCounters.getAvgRequestProcessingTime());
	}

	@Override
	public Long getAvgTPS(){
		return SnmpCounterUtil.convertToCounter32(pcrfServiceCounters.getAvgTPS());
	}
}