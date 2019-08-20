package com.elitecore.aaa.mibs.sm.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;

public class RemoteSessionManagerClientMIB {

	private AAAServerContext serverContext;
	private static Map<String, RemoteSessionManagerESICounter> remoteSmESINameToCounterMap;
	private static Map<String, String> remoteSmESINameToAddressMap;
	private boolean isInitialized;
	private static long DEFAULT_CNT_VALUE = 0;
	
	public RemoteSessionManagerClientMIB(AAAServerContext serverContext) {
		this.serverContext = serverContext;
		remoteSmESINameToCounterMap = new HashMap<String, RemoteSessionManagerESICounter>();
		remoteSmESINameToAddressMap = new HashMap<String, String>();
		isInitialized = false;
	}

	public void init() {
		if(!isInitialized){
			List<DefaultExternalSystemData> remoteSMESIList = serverContext.getServerConfiguration().getRadESConfiguration().getESListByType(RadESTypeConstants.SESSION_MANAGER.type);
			
			if(remoteSMESIList != null && !(remoteSMESIList.isEmpty())){

				for (DefaultExternalSystemData radUDPExternalSystem : remoteSMESIList) {
					String remoteSMName = radUDPExternalSystem.getName();
					String remoteSMAddress = radUDPExternalSystem.getIPAddress().getHostAddress();
					if(remoteSMAddress != null && !(remoteSMAddress.trim().isEmpty())){
						remoteSmESINameToAddressMap.put(remoteSMName, remoteSMAddress);
						remoteSmESINameToCounterMap.put(remoteSMName, new RemoteSessionManagerESICounter(remoteSMName, remoteSMAddress));
					}
				}
			}
			isInitialized = true;
		}
	}
	
	private class RemoteSessionManagerESICounter {

		private String remoteSMName;
		private String remoteSMIPAddress;

		private AtomicLong smRequestRx = new AtomicLong(0);
		private AtomicLong smResponsesTx = new AtomicLong(0);
		private AtomicLong smRequestDropped = new AtomicLong(0);
		private AtomicLong smUnknownRequestType = new AtomicLong(0);
		private AtomicLong smRequestTimeout = new AtomicLong(0);
		private AtomicLong smAccessRequestRx = new AtomicLong(0);
		private AtomicLong smAccessAcceptTx = new AtomicLong(0);
		private AtomicLong smAccessRejectTx = new AtomicLong(0);
		private AtomicLong smAcctRequestRx = new AtomicLong(0);
		private AtomicLong smAcctStartRequestRx = new AtomicLong(0);
		private AtomicLong smAcctUpdateRequestRx = new AtomicLong(0);
		private AtomicLong smAcctStopRequestRx = new AtomicLong(0);
		private AtomicLong smAcctResponseTx = new AtomicLong(0);
		
		public RemoteSessionManagerESICounter(String remoteSMName,String remoteSMIPAddress) {
			this.remoteSMName = remoteSMName;
			this.remoteSMIPAddress = remoteSMIPAddress;
		}

		public long getSmRequestRx() {
			return smRequestRx.get();
		}

		public void incrSmRequestRx() {
			this.smRequestRx.incrementAndGet();
		}

		public long getSmResponsesTx() {
			return smResponsesTx.get();
		}

		public void incrSmResponsesTx() {
			this.smResponsesTx.incrementAndGet();
		}

		public long getSmRequestDropped() {
			return smRequestDropped.get();
		}

		public void incrSmRequestDropped() {
			this.smRequestDropped.incrementAndGet();
		}

		public long getSmUnknownRequestType() {
			return smUnknownRequestType.get();
		}

		public void incrSmUnknownRequestType() {
			this.smUnknownRequestType.incrementAndGet();
		}

		public long getSmRequestTimeout() {
			return smRequestTimeout.get();
		}
		
		public void incrSmRequestTimeout() {
			this.smRequestTimeout.incrementAndGet();
		}
		
		public long getSmAccessRequestRx() {
			return smAccessRequestRx.get();
		}

		public void incrSmAccessRequestRx() {
			this.smAccessRequestRx.incrementAndGet();
		}

		public long getSmAccessAcceptTx() {
			return smAccessAcceptTx.get();
		}

		public void incrSmAccessAcceptTx() {
			this.smAccessAcceptTx.incrementAndGet();
		}

		public long getSmAccessRejectTx() {
			return smAccessRejectTx.get();
		}

		public void incrSmAccessRejectTx() {
			this.smAccessRejectTx.incrementAndGet();
		}

		public long getSmAcctRequestRx() {
			return smAcctRequestRx.get();
		}

		public void incrSmAcctRequestRx() {
			this.smAcctRequestRx.incrementAndGet();
		}

		public long getSmAcctStartRequestRx() {
			return smAcctStartRequestRx.get();
		}

		public void incrSmAcctStartRequestRx() {
			this.smAcctStartRequestRx.incrementAndGet();
		}

		public long getSmAcctUpdateRequestRx() {
			return smAcctUpdateRequestRx.get();
		}

		public void incrSmAcctUpdateRequestRx() {
			this.smAcctUpdateRequestRx.incrementAndGet();
		}

		public long getSmAcctStopRequestRx() {
			return smAcctStopRequestRx.get();
		}

		public void incrSmAcctStopRequestRx() {
			this.smAcctStopRequestRx.incrementAndGet();
		}

		public long getSmAcctResponseTx() {
			return smAcctResponseTx.get();
		}

		public void incrSmAcctResponseTx() {
			this.smAcctResponseTx.incrementAndGet();
		}
		
		@Override
		public String toString() {
			StringBuilder responseBuilder = new StringBuilder();
			
			responseBuilder.append("\n    Request Summary Of ESI : " + remoteSMName);
			responseBuilder.append("\n----------------------------------------------------------------");
			responseBuilder.append("\nsmIPAddress                :"+ this.remoteSMIPAddress);
			responseBuilder.append("\nsmRequestRx                :"+ this.smRequestRx);
			responseBuilder.append("\nsmResponsesTx              :"+ this.smResponsesTx);
			responseBuilder.append("\nsmRequestDropped           :"+ this.smRequestDropped);
			responseBuilder.append("\nsmUnknownRequestType       :"+ this.smUnknownRequestType);
			responseBuilder.append("\nsmAccessRequestRx          :"+ this.smAccessRequestRx);
			responseBuilder.append("\nsmAccessAcceptTx           :"+ this.smAccessAcceptTx);
			responseBuilder.append("\nsmAccessRejectTx           :"+ this.smAccessRejectTx);
			responseBuilder.append("\nsmAcctRequestRx            :"+ this.smAcctRequestRx);
			responseBuilder.append("\nsmAcctStartRequestRx       :"+ this.smAcctStartRequestRx);
			responseBuilder.append("\nsmAcctUpdateRequestRx      :"+ this.smAcctUpdateRequestRx);
			responseBuilder.append("\nsmAcctStopRequestRx        :"+ this.smAcctStopRequestRx);
			responseBuilder.append("\nsmAcctResponseTx           :"+ this.smAcctStopRequestRx);
			responseBuilder.append("\n------------------------------------------");
			return responseBuilder.toString();
		}
	}
	public static long getSmRequestRx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			return serverEntry.getSmRequestRx();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrSmRequestRx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			serverEntry.incrSmRequestRx();
		}
	}

	public static long getSmResponsesTx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			return serverEntry.getSmResponsesTx();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrSmResponsesTx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			serverEntry.incrSmResponsesTx();
		}
	}

	public static long getSmRequestDropped(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			return serverEntry.getSmRequestDropped();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrSmRequestDropped(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			serverEntry.incrSmRequestDropped();
		}
	}

	public static long getSmRequestTimeout(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			return serverEntry.getSmRequestTimeout();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrSmRequestTimeout(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			serverEntry.incrSmRequestTimeout();
		}
	}

	public static long getSmUnknownRequestType(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			return serverEntry.getSmUnknownRequestType();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrSmUnknownRequestType(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			serverEntry.incrSmUnknownRequestType();
		}
	}

	public static long getSmAccessRequestRx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			return serverEntry.getSmAccessRequestRx();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrSmAccessRequestRx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			serverEntry.incrSmAccessRequestRx();
		}
	}

	public static long getSmAccessAcceptTx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			return serverEntry.getSmAccessAcceptTx();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrSmAccessAcceptTx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			serverEntry.incrSmAccessAcceptTx();
		}
	}

	public static long getSmAccessRejectTx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			return serverEntry.getSmAccessRejectTx();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrSmAccessRejectTx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			serverEntry.incrSmAccessRejectTx();
		}
	}

	public static long getSmAcctRequestRx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			return serverEntry.getSmAcctRequestRx();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrSmAcctRequestRx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			serverEntry.incrSmAcctRequestRx();
		}
	}

	public static long getSmAcctStartRequestRx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			return serverEntry.getSmAcctStartRequestRx();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrSmAcctStartRequestRx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			serverEntry.incrSmAcctStartRequestRx();
		}
	}

	public static long getSmAcctUpdateRequestRx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			return serverEntry.getSmAcctUpdateRequestRx();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrSmAcctUpdateRequestRx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			serverEntry.incrSmAcctUpdateRequestRx();
		}
	}

	public static long getSmAcctStopRequestRx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			return serverEntry.getSmAcctStopRequestRx();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrSmAcctStopRequestRx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			serverEntry.incrSmAcctStopRequestRx();
		}
	}

	public static long getSmAcctResponseTx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			return serverEntry.getSmAcctResponseTx();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrSmAcctResponseTx(String serverAddress){
		RemoteSessionManagerESICounter serverEntry = remoteSmESINameToCounterMap.get(serverAddress);
		if(serverEntry != null){
			serverEntry.incrSmAcctResponseTx();
		}
	}
	
	public static String getSummary(String esiName) {
		if(remoteSmESINameToCounterMap != null && remoteSmESINameToCounterMap.get(esiName)!=null){
			return remoteSmESINameToCounterMap.get(esiName).toString();
		}else {
			return "ESI Not Found";
		}
	}
	
	public static Map<String, String> getRemoteSmESINameToAddressMap() {
		return remoteSmESINameToAddressMap;
	}
}
