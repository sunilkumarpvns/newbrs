package com.elitecore.diameterapi.diameter.stack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.DiameterStackContext;

public class DuplicateDetectionHandler {

	private static final int DEFAULT_ORIGINHOST_LIST_SIZE = 3;
	private static final String MODULE = "DUP-DETECTION-HNDLR";
	private int dupicatePacketPurgeIntervalInSec;
	private IDiameterStackContext stackContext;

	private DuplicateMessagePool currentMessagePool;
	private DuplicateMessagePool oldMessagePool;
	private DuplicateMessagePool oldestMessagePool;
	private boolean duplicateDetectionEnabled;

	public DuplicateDetectionHandler(IDiameterStackContext stackContext) {

		this.stackContext = stackContext;
		currentMessagePool = new DuplicateMessagePool();
		oldMessagePool = new DuplicateMessagePool();
		oldestMessagePool = new DuplicateMessagePool();

	}

	public void init() {
		if(duplicateDetectionEnabled == false) {
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Not scheduling Duplicate Message Clean-Up Task " + 
						", Reason: Duplicate detection is disabled.");
			}
			return;
		}
		stackContext.scheduleIntervalBasedTask(new DuplicateMessagePurgeIntervalTask());
	}

	public boolean isDuplicate(DiameterRequest diameterRequest) {

		if(duplicateDetectionEnabled == false) {
			return false;
		}
		String originHost = diameterRequest.getAVPValue(DiameterAVPConstants.ORIGIN_HOST);
		
		DuplicateMessagePool messagePool = getDuplicateDetectionDatasMesssagePool(diameterRequest);
		
		// Check if End-to-end Available
		if(messagePool == null){

			// Accumulate Original Request
			List<DuplicateDetectionData> duplicateDetectionDatas = new ArrayList<DuplicateDetectionData>(DEFAULT_ORIGINHOST_LIST_SIZE);
			List<DuplicateDetectionData> existingDuplicateDetectionDatas = currentMessagePool.putIfAbsent(diameterRequest.getEnd_to_endIdentifier(), duplicateDetectionDatas);
			if(existingDuplicateDetectionDatas != null){
				duplicateDetectionDatas = existingDuplicateDetectionDatas;
			}

			addToDuplicateDetectionDatas(duplicateDetectionDatas, diameterRequest);
			return false;
		}
		// End-to-end available --> If Origin Host Exists
		List<DuplicateDetectionData> duplicateDetectionDatas = messagePool.getOriginalMessagePool().get(diameterRequest.getEnd_to_endIdentifier());
		DuplicateDetectionData duplicateDetectionData = getDataByOriginHost(duplicateDetectionDatas, originHost);
		
		// Different Origin Host --> Not Duplicate
		if (duplicateDetectionData == null) {
			
			addToDuplicateDetectionDatas(duplicateDetectionDatas, diameterRequest);
			return false;
		}
		return true;
	}

	private void addToDuplicateDetectionDatas(
			List<DuplicateDetectionData> duplicateDetectionDatas,
			DiameterRequest diameterRequest) {
		
		String originHost = diameterRequest.getAVPValue(DiameterAVPConstants.ORIGIN_HOST);
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Accumulating Diameter Request with HbH-ID= " + 
					diameterRequest.getHop_by_hopIdentifier() + 
					", EtE-ID= " + diameterRequest.getEnd_to_endIdentifier() + 
					", Origin-Host: " + originHost + 
					" for Duplicate Detection.");
		}
		
		synchronized (duplicateDetectionDatas) {
			duplicateDetectionDatas.add(new DuplicateDetectionData(
					originHost, 
					diameterRequest.getHop_by_hopIdentifier()));
		}
	}

	private DuplicateDetectionData getDataByOriginHost(
			List<DuplicateDetectionData> originHostToOriginalMessage,
			String originHost) {
		
		for (int i = 0; i < originHostToOriginalMessage.size(); i++) {
			if(originHostToOriginalMessage.get(i).originHost.equals(originHost) == true) {
				return originHostToOriginalMessage.get(i);
			}
		}
		return null;
	}
	
	public DiameterAnswer storeIfAbsent(DiameterRequest diameterRequest) {

		String originHost = diameterRequest.getAVPValue(DiameterAVPConstants.ORIGIN_HOST);

		DuplicateMessagePool messagePool = getDuplicateDetectionDatasMesssagePool(diameterRequest);

		if(messagePool == null){
			messagePool = currentMessagePool;
		}

		List<DuplicateDetectionData> duplicateDetectionDatas = messagePool.getOriginalMessagePool().get(diameterRequest.getEnd_to_endIdentifier());

		if(duplicateDetectionDatas == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Duplicate Diameter Request detected with HbH-ID= " + 
						diameterRequest.getHop_by_hopIdentifier() + 
						", EtE-ID= " + diameterRequest.getEnd_to_endIdentifier() + 
						", Origin-Host: " + originHost + 
						", Diameter Answer not yet received, Waiting for Response.");
			}
			return null;
		}
		synchronized (duplicateDetectionDatas) {

			DuplicateDetectionData duplicateDetectionData = getDataByOriginHost(duplicateDetectionDatas, originHost);

			if(duplicateDetectionData.diameterAnswer == null){

				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Duplicate Diameter Request detected with HbH-ID= " + 
							diameterRequest.getHop_by_hopIdentifier() + 
							", EtE-ID= " + diameterRequest.getEnd_to_endIdentifier() + 
							", Origin-Host: " + originHost + 
							", Diameter Answer not yet received, Waiting for Response.");
				}
				messagePool.getDuplicateRequestPool().remove(originHost + diameterRequest.getEnd_to_endIdentifier());
				messagePool.getDuplicateRequestPool().put(originHost + diameterRequest.getEnd_to_endIdentifier(), diameterRequest.getHop_by_hopIdentifier());

				return null;
			} else {

				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Duplicate Diameter Request detected with HbH-ID= " + 
							diameterRequest.getHop_by_hopIdentifier() + 
							", EtE-ID= " + diameterRequest.getEnd_to_endIdentifier() + 
							", Origin-Host: " + originHost + 
							", Responding with Diameter Answer stored previously.");
				}
				duplicateDetectionData.diameterAnswer.setHop_by_hopIdentifier(diameterRequest.getHop_by_hopIdentifier());
				return duplicateDetectionData.diameterAnswer;
			}
		}

	}

	private DuplicateMessagePool getDuplicateDetectionDatasMesssagePool(DiameterPacket diameterPacket) {

		List<DuplicateDetectionData> orginHostToOriginalMessageMap = currentMessagePool.getOriginalMessagePool().get(diameterPacket.getEnd_to_endIdentifier());
		if(orginHostToOriginalMessageMap != null) {
			return currentMessagePool;
		}
		
		orginHostToOriginalMessageMap = oldMessagePool.getOriginalMessagePool().get(diameterPacket.getEnd_to_endIdentifier());
		if(orginHostToOriginalMessageMap != null) {
			return oldMessagePool;
		}
		
		orginHostToOriginalMessageMap = oldestMessagePool.getOriginalMessagePool().get(diameterPacket.getEnd_to_endIdentifier());
		if(orginHostToOriginalMessageMap != null){
			return oldestMessagePool;
		}
		
		return null;
	}


	public void decorate(DiameterAnswer diameterAnswer) {

		if(duplicateDetectionEnabled == false) {
			return;
		}
		
		DuplicateMessagePool messagePool = getDuplicateDetectionDatasMesssagePool(diameterAnswer);
		
		if(messagePool == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Not accumulating Diameter Answer with HbH-ID= " + 
						diameterAnswer.getHop_by_hopIdentifier() + 
						", EtE-ID= " + diameterAnswer.getEnd_to_endIdentifier() + 
						", Reason: original request pool is not available.");
			}
			return;
		}

		List<DuplicateDetectionData> duplicateDetectionDatas = messagePool.getOriginalMessagePool().get(diameterAnswer.getEnd_to_endIdentifier());
		if(duplicateDetectionDatas == null) {

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Not accumulating Diameter Answer with HbH-ID= " + 
						diameterAnswer.getHop_by_hopIdentifier() + 
						", EtE-ID= " + diameterAnswer.getEnd_to_endIdentifier() + 
						", Reason: original request is not found.");
			}
			return;
		
		}
		synchronized (duplicateDetectionDatas) {

			DuplicateDetectionData duplicateDetectionData = null;
			
			for (int i = 0; i < duplicateDetectionDatas.size(); i++) {
				if(duplicateDetectionDatas.get(i).hopByhop == diameterAnswer.getHop_by_hopIdentifier()) {
					duplicateDetectionData = duplicateDetectionDatas.get(i);
					break;
				}
			}
			if(duplicateDetectionData == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Not accumulating Diameter Answer with HbH-ID= " + 
							diameterAnswer.getHop_by_hopIdentifier() + 
							", EtE-ID= " + diameterAnswer.getEnd_to_endIdentifier() + 
							", Reason: original request is not found.");
				}
				return;
			}
			
			Integer hopByhop = messagePool.getDuplicateRequestPool().remove(duplicateDetectionData.originHost + diameterAnswer.getEnd_to_endIdentifier());
			if(hopByhop == null) {
				duplicateDetectionData.diameterAnswer = diameterAnswer;
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Accumulating Diameter Answer with HbH-ID= " + 
							diameterAnswer.getHop_by_hopIdentifier() + 
							", EtE-ID= " + diameterAnswer.getEnd_to_endIdentifier() + 
							" for Duplicate Detection");
				}
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Sending response for Duplicate Request with HbH-ID= " + 
							diameterAnswer.getHop_by_hopIdentifier() + 
							", EtE-ID= " + diameterAnswer.getEnd_to_endIdentifier());
				}
				diameterAnswer.setHop_by_hopIdentifier(hopByhop);
			}
			duplicateDetectionData.diameterAnswer = diameterAnswer;
		}
	}



	private class DuplicateMessagePurgeIntervalTask extends BaseIntervalBasedTask {

		@Override
		public long getInterval() {
			return dupicatePacketPurgeIntervalInSec/3;
		}

		@Override
		public void execute(AsyncTaskContext context) {

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Flushing Diameter Messages stored for Duplicate Detection");
			}

			oldestMessagePool = oldMessagePool;
			oldMessagePool = currentMessagePool;
			currentMessagePool = new DuplicateMessagePool();
		}

	}

	private class DuplicateMessagePool {

		private ConcurrentHashMap<Integer, List<DuplicateDetectionData>> currentOriginalMessagePool;
		private Map<String, Integer> currentDuplicateRequestPool;

		private DuplicateMessagePool() {
			this.currentOriginalMessagePool = new ConcurrentHashMap<Integer, List<DuplicateDetectionData>>();
			currentDuplicateRequestPool = new ConcurrentHashMap<String, Integer>();
		}

		public Map<String, Integer> getDuplicateRequestPool() {
			return currentDuplicateRequestPool;
		}
		
		public Map<Integer, List<DuplicateDetectionData>> getOriginalMessagePool() {
			return currentOriginalMessagePool;
		}
		
		public List<DuplicateDetectionData> putIfAbsent(Integer endToEnd, List<DuplicateDetectionData> duplicateDetectionData) {
			List<DuplicateDetectionData> existingData = currentOriginalMessagePool.putIfAbsent(endToEnd, duplicateDetectionData);
			if(existingData == null) {
				return duplicateDetectionData;
			}
			return existingData;
		}

	}
	
	private class DuplicateDetectionData {
		private String originHost;
		private DiameterAnswer diameterAnswer;
		private int hopByhop;
		
		public DuplicateDetectionData(String originHost, int hopByhop) {
			this.originHost = originHost;
			this.hopByhop = hopByhop;
		}
	}

	public void setDupicatePacketPurgeIntervalInSec(
			int dupicatePacketPurgeIntervalInSec) {
		this.dupicatePacketPurgeIntervalInSec = dupicatePacketPurgeIntervalInSec;
	}

	public void duplicateDetectionEnabled(boolean duplicateDetectionEnabled) {
		this.duplicateDetectionEnabled = duplicateDetectionEnabled;
	}
}