package com.elitecore.netvertex.service.pcrf;

import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class PCRFServiceCounters {
	
	private static final String MODULE = "PCRF-SRV-COUNTERS";

	private PCRFServiceContext pcrfServiceContext;

	private long serviceUpTime;
	private long statisicsResetTime;
	public static final int STATS_OTHER = 1;
	public static final int STATS_RESET = 2;
	private final int STATS_INITIALIZING = 3;
	private final int STATS_RUNNING = 4;
	private int statisticsReset = STATS_OTHER;

	private AtomicLong totalPCRFReq;
	private AtomicLong totalPCRFResp;
	private AtomicLong totalSuccessPCRFResp;
	private AtomicLong totalRejectPCRFResp;
	private AtomicLong totalPCRFReqDropped;
	
	private long avgRequestProcessingTime;
	private long avgTPS;
	
	public PCRFServiceCounters(PCRFServiceContext pcrfServiceContext) {
		statisticsReset = STATS_INITIALIZING;
		this.pcrfServiceContext = pcrfServiceContext;
		totalPCRFReq = new AtomicLong();
		totalPCRFResp = new AtomicLong();
		totalSuccessPCRFResp = new AtomicLong();
		totalRejectPCRFResp = new AtomicLong();
		totalPCRFReqDropped = new AtomicLong();
		serviceUpTime = System.currentTimeMillis();
		statisicsResetTime = System.currentTimeMillis();
		statisticsReset = STATS_RUNNING;
	}
	public long getTotalPCRFReqCntr() {
		return totalPCRFReq.get();
	}
	public void incTotalPCRFReqCntr() {
		totalPCRFReq.incrementAndGet();
	}
	public long getTotalPCRFRespCntr() {
		return totalPCRFResp.get();
	}
	public void incTotalPCRFRespCntr() {
		totalPCRFResp.incrementAndGet();
	}
	public long getTotalSuccessPCRFRespCntr() {
		return totalSuccessPCRFResp.get();
	}
	public void incTotalSuccessPCRFRespCntr() {
		totalSuccessPCRFResp.incrementAndGet();
	}
	public long getTotalRejectPCRFRespCntr() {
		return totalRejectPCRFResp.get();
	}
	public void incTotalRejectPCRFRespCntr() {
		totalRejectPCRFResp.incrementAndGet();
	}
	public long getTotalPCRFReqDroppedCntr() {
		return totalPCRFReqDropped.get();
	}
	public void incTotalPCRFReqDroppedCntr() {
		totalPCRFReqDropped.incrementAndGet();
	}
	public long getServiceUpTime() {
		return serviceUpTime;
	}
	public long getStatisicsResetTime() {
		return statisicsResetTime;
	}
	public int getStatisticsReset() {
		return statisticsReset;
	}
	
	public void updateAvgRequestProcessingTime(long avgRequestProcessingTime) {
		this.avgRequestProcessingTime = avgRequestProcessingTime; 
	}
	
	public long getAvgRequestProcessingTime() {
		return avgRequestProcessingTime;
	}
	
	public void updateAvgTPS(long avgTPS) {
		this.avgTPS = avgTPS; 
	}
	
	public long getAvgTPS() {
		return avgTPS;
	}
	
	public synchronized void resetStatistics() {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Resetting PCRF service statistics");
		
		statisticsReset = STATS_INITIALIZING;
		totalPCRFReq.set(0);
		totalPCRFResp.set(0);
		totalSuccessPCRFResp.set(0);
		totalRejectPCRFResp.set(0);
		totalPCRFReqDropped.set(0);
		statisicsResetTime = System.currentTimeMillis();
		statisticsReset = STATS_RUNNING;
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Reset operation for PCRF service statistics is completed successfully");
	}
}