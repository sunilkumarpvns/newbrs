package com.elitecore.diameterapi.core.common.transport.priority;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.diameterapi.core.common.transport.tcp.collections.FairnessPolicy;
import com.elitecore.diameterapi.core.common.transport.tcp.collections.WeightedFairBlockingQueue.Range;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.PacketProcess;

public class PriorityTable implements FairnessPolicy<Runnable> {

	private List<PriorityEntry> priorityEntries;

	public PriorityTable(List<PriorityEntry> priorities) {
		priorityEntries = new ArrayList<PriorityEntry>(priorities);
	}

	@Override
	public int prioritize(Runnable e) {

		PacketProcess process = (PacketProcess) e;
		DiameterPacket diameterPacket = (DiameterPacket) process.getPacket();
		InetAddress ipAddress = process.getConnectionHandler().getSourceInetAddress();
		
		if(Collectionz.isNullOrEmpty(priorityEntries) == true) {
			return Priority.MEDIUM.val;
		}
		

		for(PriorityEntry priorityEntry : priorityEntries){
			if(priorityEntry.isApplicable(diameterPacket, ipAddress)) {
				return priorityEntry.getPriority().val;
			}
		}
		
		return Priority.MEDIUM.val;
	}

	public Range range(){
		
		Range range = null;
		
		for(PriorityEntry priorityEntry : priorityEntries){
			if(Priority.HIGH == priorityEntry.getPriority()){
				if(range == Range.LOW_NORMAL) {
					range = Range.LOW_TO_HIGH;
				} else {
					range = Range.NORMAL_HIGH;
				}
			} else if(Priority.LOW == priorityEntry.getPriority()){
				if(range == Range.NORMAL_HIGH) {
					range = Range.LOW_TO_HIGH;
				} else {
					range = Range.LOW_NORMAL;
				}
			} 
			
			if(range == Range.LOW_TO_HIGH){
				break;
			}
		}
		
		return range == null ? Range.LOW_NORMAL :range ;
	}
}
