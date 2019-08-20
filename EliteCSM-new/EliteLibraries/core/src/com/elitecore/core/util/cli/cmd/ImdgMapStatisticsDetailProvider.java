package com.elitecore.core.util.cli.cmd;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nonnull;

import com.elitecore.core.util.cli.TableFormatter;
import com.hazelcast.core.IMap;
import com.hazelcast.monitor.LocalMapStats;

public class ImdgMapStatisticsDetailProvider extends DetailProvider {

	private static final String KEY 		= "map";

	private static final String MODULE = "IMDG-MAP-DETAIL-PROVIDER";
	private static final String COLON = ":";

	private ArrayList<IMap<Object, Object>> imdgLocalMaps;

	public ImdgMapStatisticsDetailProvider(){
		imdgLocalMaps= new ArrayList<IMap<Object, Object>>();
	}

	public void registerMapDetailProvider(@Nonnull IMap<Object, Object> iMap) {
		imdgLocalMaps.add(iMap);
	}

	@Override
	public String execute(String[] parameters) {
		if(parameters == null || parameters.length == 0) {
			return showAllMapStatistics();
		}
		if(isHelpSymbol(parameters[0])) {
			return getHelpMsg();
		}
		String mapName = parameters[0];
		for(IMap<Object, Object>  entry : imdgLocalMaps) {
			if(mapName.equals(entry.getName())) {
				return	showMapStatistics(entry);
			}
		}

		return showAllMapStatistics();
	}


	private String showMapStatistics(IMap<Object, Object> entry) {
		LocalMapStats mapStats = entry.getLocalMapStats();
		long avgPutLatency = 0; 
		long avgGetLatency = 0;
		long avgRemoveLatency =0;
		long memoryUsedByMap = 0;
		long backUpMemoryused =0;
	
		if(mapStats.getMaxPutLatency() > 0) {
			avgPutLatency = mapStats.getTotalPutLatency()/mapStats.getMaxPutLatency();
		}
		if(mapStats.getMaxGetLatency() > 0) {
			avgGetLatency = mapStats.getTotalGetLatency()/mapStats.getMaxGetLatency();
		}
		if(mapStats.getMaxRemoveLatency() > 0) {
			avgRemoveLatency= mapStats.getTotalRemoveLatency()/mapStats.getMaxRemoveLatency();
		}
		memoryUsedByMap = mapStats.getOwnedEntryMemoryCost()/1000000;
		backUpMemoryused = mapStats.getBackupEntryMemoryCost()/1000000;


		String[] header = {"","",""};
		int[] width = {50,1,49};
		TableFormatter formatter = new TableFormatter(header, width, TableFormatter.ONLY_HEADER_LINE);
		
		StringWriter writer =new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print("                             ***IMDG Local map statistics***");
		
		String[] data = new String[3];

		data[0] = "Map Name";
		data[1] = COLON;
		data[2] = entry.getName();
		formatter.addRecord(data);
		
		data[0] = "Active Sessions";
		data[1] = COLON;
		data[2] = String.valueOf(mapStats.getOwnedEntryCount());
		formatter.addRecord(data);
		
		data[0] = "Backup Sessions";
		data[1] = COLON;
		data[2] = String.valueOf(mapStats.getBackupEntryCount());
		formatter.addRecord(data);
		
		data[0] = "Memory Used by Map(MB)";
		data[1] = COLON;
		data[2] = String.valueOf(memoryUsedByMap);
		formatter.addRecord(data);
		
		data[0] = "Memory Used For BackUp(MB)";
		data[1] = COLON;
		data[2] = String.valueOf(backUpMemoryused);
		formatter.addRecord(data);
		
		data[0] = "No Of Locked Sessions";
		data[1] = COLON;
		data[2] = String.valueOf(mapStats.getLockedEntryCount());
		formatter.addRecord(data);
		
		data[0] = "Total Put Operations";
		data[1] = COLON;
		data[2] = String.valueOf(mapStats.getPutOperationCount());
		formatter.addRecord(data);
		
		data[0] = "Total Get Operations";
		data[1] = COLON;
		data[2] = String.valueOf(mapStats.getGetOperationCount());
		formatter.addRecord(data);
		
		data[0] = "Total Remove Operations";
		data[1] = COLON; 
		data[2] = String.valueOf(mapStats.getRemoveOperationCount());
		formatter.addRecord(data);
		
		data[0] = "Average Put Latency(ms)";
		data[1] = COLON;
		data[2] = String.valueOf(avgPutLatency);
		formatter.addRecord(data);
		
		data[0] = "Average Get Latency(ms)";
		data[1] = COLON;
		data[2] =String.valueOf(avgGetLatency);
		formatter.addRecord(data);
		
		data[0] = "Average Remove Latency(ms)";
		data[1] = COLON;
		data[2] = String.valueOf(avgRemoveLatency);
		formatter.addRecord(data);
		
		
		return  writer.toString() + formatter.getFormattedValues();
	}

	protected String showAllMapStatistics() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		for(IMap<Object, Object>  entry : imdgLocalMaps) {
			out.print(showMapStatistics(entry));	
		}
		out.close();
		return stringWriter.toString();
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage 	  : show imdg statistics map <option>");
		responseBuilder.append("\nDescription : " + getDescription());
		responseBuilder.append("\n\nPossible Options: any valid imdg map name \n");
		return responseBuilder.toString();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getDescription() {
		return "Diplays IMDG map statistics of the given map name";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		//	no-op
		return null;
	}

}