package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;

public abstract class PeerStateCommand extends EliteBaseCommand{
	
	private static final String COMMAND_NAME = "ps";
	private static final String DESCRIPTION = "Provides Operations for Peer State";
	private static final String LIST = "-list";
	private static final String INFO = "-info";
	private static final String CLOSE = "-close";
	private static final String FORCE_CLOSE = "-forceclose";
	private static final String START = "-start";
	private static final String HELP = "-help";
	private final String[] header = { "PEERIDENTITY" , "STATE" };
	private final int[] alignment = new int[] { TableFormatter.LEFT, TableFormatter.LEFT };

	@Override
	public String execute(String parameter) {
		String responseMessage = "";
		if(parameter == null || parameter.length() == 0){
			return getAllPeersInfo();
		}
		if(parameter.equalsIgnoreCase("?") || parameter.equalsIgnoreCase(HELP)){
			responseMessage = getHelp();
			return responseMessage;
		}
		
		if(CSV.equalsIgnoreCase(parameter)){
			return getAllPeersCSVInfo();
		}
		StringTokenizer tokenizer = new StringTokenizer(parameter," ");
		if(tokenizer.hasMoreTokens()){
			parameter = tokenizer.nextToken();
		}
		
		if(parameter.equalsIgnoreCase(LIST)){
			if(tokenizer.hasMoreTokens()){
				
				parameter = tokenizer.nextToken();
				if(CSV.equalsIgnoreCase(parameter)){
					
					if(tokenizer.hasMoreTokens()){
						return "Invalid arguments." + CommonConstants.LINE_SEPARATOR + getHelp();
					}
					return getAllPeersCSVInfo();
				}
			}
			return getAllPeersInfo();
		}else if(parameter.equalsIgnoreCase(START)){
			
			String commandParam = null;
			if(tokenizer.hasMoreTokens() == false){
				return "Peer Host Identity required.\n" + getHelp();
			}
			commandParam = tokenizer.nextToken();
			commandParam = commandParam.trim();
			if(startPeer(commandParam)){
				return "Connection to Peer: " + commandParam + " has been attempted";
			}
			return "Unable to Attempt Connection for Peer: " + commandParam + ", Peer not found";
			
		}else if(parameter.equalsIgnoreCase(CLOSE)){
			
			String commandParam = null;
			if(tokenizer.hasMoreTokens() == false){
				return "Peer Host Identity required.\n" + getHelp();
			}
			commandParam = tokenizer.nextToken();
			commandParam = commandParam.trim();
			if(closePeer(commandParam)){
				return "Stop event has been generated for Peer: " + commandParam;
			}
			return "Unable to generate Stop event for Peer: " + commandParam + ", Peer not found";
			
		}else if(parameter.equalsIgnoreCase(FORCE_CLOSE)){
			
			String commandParam = null;
			if(tokenizer.hasMoreTokens() == false){
				return "Peer Host Identity required.\n" + getHelp();
			}
			commandParam = tokenizer.nextToken();
			commandParam = commandParam.trim();
			
			if(forceClosePeer(commandParam)){
				return "Close Connection has been generated for Peer: " + commandParam;
			}
			return "Unable to Close Connection for Peer: " + commandParam + ", Peer not found";
			
		}else if(parameter.equalsIgnoreCase(INFO)){
			String commandParam = null;
			if(tokenizer.hasMoreTokens()){
				commandParam = tokenizer.nextToken();
				Map<String, IStateEnum> peersStateMap = getPeersState();
				if(peersStateMap!=null){
					IStateEnum diameterPeerState = peersStateMap.get(commandParam);
					if(diameterPeerState != null){
						StringBuilder builder = new StringBuilder();
						builder.append(fillChar("", 60, '-') + "\n");
						builder.append(fillChar("HOST IDENTITY", 35) + "STATE\n");
						builder.append(fillChar("", 60, '-') + "\n");
						builder.append(fillChar(commandParam, 35) + diameterPeerState.name());
						return builder.toString();
					}else {
						return "Host not found";
					}
				}else{
					return "Host not found";
				}
				
			}else{
				return "Invalid arguments.\n" + getHelp();
			}
		}else{
			return getHelp();
		}
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getHotkeyHelp() {
		return "{'"+getCommandName()+"':{'"+INFO+"':{" + getPeersHelp() + "}, " +
				"'"+START+"':{" + getPeersHelp() + "}, " +
				"'"+CLOSE+"':{" + getPeersHelp() + "}, " +
				"'"+FORCE_CLOSE+"':{" + getPeersHelp() + "}, " +
				"'"+HELP+"':{}, '"+LIST+"':{'"+CSV+"':{}}, " +
				"'"+CSV+"':{} }}";
	}
	
	private String getPeersHelp(){
		Map<String, IStateEnum> peersStateMap = getPeersState();
		if(peersStateMap != null && peersStateMap.size() > 0){
			Set<String> hostIdentityList = peersStateMap.keySet();
			String[] peers = new String[peersStateMap.size()]; 
			Iterator<String> iterator = hostIdentityList.iterator();
			int i=0;
			while(iterator.hasNext()){
				peers[i] = getHotKeyPattern(iterator.next());
				i++;
			}
			return StringUtility.getCommaSeparated(peers);
		}
		return "";
	}
	
	private String getHotKeyPattern(String name){
		return "'"+name+"':{}";
	}
	
	private String getHelp(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getCommandName() );
		out.println("Description : " + getDescription()+"\n");
		out.println(fillChar("\t-list",30) + ":" + "Displays the list of all the peers");
		out.println(fillChar("\t"+CSV,30) + ":" + "CSV Output for all peer and its current state");
		out.println(fillChar("\t"+INFO+"[<host identity>]",30) + ":" + "Dislpays the state of a particular peer");
		out.println(fillChar("\t"+START+"[<host identity>]",30) + ":" + "Generates Start Event for Peer and attempts for Init Connection");
		out.println(fillChar("\t"+CLOSE+"[<host identity>]",30) + ":" + "Generates Stop event for Peer and attempts for Close Connection");
		out.println(fillChar("\t"+FORCE_CLOSE+"[<host identity>]",30) + ":" + "Forcefully Closes Peer Connection and marks it Closed");
		out.println(fillChar("\t-help",30) + ":" + "Displays help related to the command");
		return stringWriter.toString();
	}

	private String getAllPeersInfo(){

		TableFormatter formatter = new TableFormatter(header, 
				new int[]{40, 20}, 
				TableFormatter.ONLY_HEADER_LINE);
		
		return getAllPeersInfo(formatter);
	}
	
	private String getAllPeersCSVInfo(){
		TableFormatter formatter = new TableFormatter(header, new int[]{40, 20},
				TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
		
		return getAllPeersInfo(formatter);
	}
	
	private String getAllPeersInfo(TableFormatter tableFormatter){

		if (tableFormatter == null) {
			return "Error Occured while fetching Peer State List";
		}
		Map<String, IStateEnum> peersStateMap = getPeersState();
		for(Entry<String, IStateEnum> peer: peersStateMap.entrySet()){
			tableFormatter.addRecord(new String[]{peer.getKey(), peer.getValue().name()}, alignment);
		}
		return tableFormatter.getFormattedValues();
	}
	
	public abstract Map<String, IStateEnum> getPeersState();
	
	protected abstract boolean closePeer(String peerHostIdentity);
	protected abstract boolean forceClosePeer(String peerHostId);
	protected abstract boolean startPeer(String peerHostIdentity);

}
