package com.elitecore.core.systemx.esix.statistics;

import com.elitecore.core.util.cli.TableFormatter;

/**
 * Used to represent statistics for external systems which fail due to 
 * permanent failures. 
 * 
 * <p>
 * The status of ESI is {@link #FAIL}ed
 * 
 * <p>
 *  No statistics are maintained and canned values are returned.
 *  
 * @author narendra.pathai
 *
 */
public class PermanentFailureStatistics implements ESIStatistics {
	public static final String FAIL = "FAIL";
	private final int[] alignment = new int[] {TableFormatter.LEFT, TableFormatter.LEFT};
	private final String esiName;
	private final String esiType;

	public PermanentFailureStatistics(String esiName, String esiType) {
		this.esiName = esiName;
		this.esiType = esiType;
	}
	
	@Override
	public String getName() {
		return esiName;
	}

	@Override
	public String getTypeName() {
		return esiType;
	}

	@Override
	public String currentStatus() {
		return FAIL;
	}

	@Override
	public float getLastMinAvgResponseTime() {
		return 0;
	}

	@Override
	public float getLastTenMinAvgResponseTime() {
		return 0;
	}

	@Override
	public float getLastHourAvgResponseTime() {
		return 0;
	}

	@Override
	public long getLastScanTimestamp() {
		return 0;
	}

	@Override
	public long getLastDeadTimestamp() {
		return 0;
	}

	@Override
	public long getTotalTimedouts() {
		return 0;
	}

	@Override
	public long getTotalRequests() {
		return 0;
	}

	@Override
	public long getTotalSuccesses() {
		return 0;
	}

	@Override
	public long getTotalErrors() {
		return 0;
	}

	@Override
	public long getDeadCount() {
		return 0;
	}

	@Override
	public long getLastMarkDeadTimestamp() {
		return 0;
	}
	
	@Override
	public String toString(){
		
		int[] width= {35,30};
		
		String[] header={};
		
		TableFormatter esiStatsTableFormatter = new TableFormatter(header, width,TableFormatter.ONLY_HEADER_LINE);
		esiStatsTableFormatter.addRecord(new String[]{"ESI Name",":"+getName()},alignment);
		esiStatsTableFormatter.addRecord(new String[]{"ESI Type",":"+getTypeName()},alignment);
		esiStatsTableFormatter.addRecord(new String[]{"ESI Status",":"+currentStatus()},alignment);
		esiStatsTableFormatter.addRecord(new String[]{"ESI Name",":"+getName()},alignment);
		
		esiStatsTableFormatter.addRecord(new String[]{"Total Requests", ":"+String.valueOf(getTotalRequests())},alignment);
		esiStatsTableFormatter.addRecord(new String[]{"Total Error Response", ":"+String.valueOf(getTotalErrors())},alignment);
		esiStatsTableFormatter.addRecord(new String[]{"Total Success Response", ":"+String.valueOf(getTotalSuccesses())},alignment);
		esiStatsTableFormatter.addRecord(new String[]{"Total Timed out Response", ":"+String.valueOf(getTotalTimedouts())},alignment);
		esiStatsTableFormatter.addRecord(new String[]{"Total Dead Count", ":"+String.valueOf(getDeadCount())},alignment);
		esiStatsTableFormatter.addRecord(new String[]{"Last Scan Timestamp", ":"+ "-NA-"},alignment);
		esiStatsTableFormatter.addRecord(new String[]{"Last Dead Timestamp", ":"+"-NA-"},alignment);
		esiStatsTableFormatter.addRecord(new String[]{"Last Minute Avg. Response Time",":"+String.valueOf(getLastMinAvgResponseTime())},alignment);
		esiStatsTableFormatter.addRecord(new String[]{"Last 10 Minute Avg. Response Time", ":"+String.valueOf(getLastTenMinAvgResponseTime())},alignment);
		esiStatsTableFormatter.addRecord(new String[]{"Last Hour Avg. Response Time", ":"+String.valueOf(getLastHourAvgResponseTime())},alignment);
		
		return esiStatsTableFormatter.getFormattedValues();
	}
}
