package com.elitecore.netvertex.gateway.radius.snmp.acct;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;

public class AcctServerCounters {

	private long servUpTime;
	private AtomicLong resetTime;
	private String servIdent;
	private final int CONFIG_RESET_Other = 1;
	private final int CONFIG_RESET_Reset = 2;
	private final int CONFIG_RESET_Initializing = 3;
	private final int CONFIG_RESET_Running = 4;
	private int configReset;
	private AtomicLong totalReq;
	private AtomicLong totalInvalidReq;
	private AtomicLong totalDupReq;
	private AtomicLong totalRes;
	private AtomicLong totalMalformedReq;
	private AtomicLong totalAccessReject;
	private AtomicLong totalBadAuthenticator;
	private AtomicLong totalPackDrop;
	private AtomicLong totalNoRecord;
	private AtomicLong totalUnknownType;
	private AtomicLong totalStartReq;
	private AtomicLong totalStopReq;
	private AtomicLong totalIntrUpdateReq;

	private Map<String, RadiusAcctClientEntry> acctClientEntryMap;
	private Map<Integer, String> clientMap;

	private boolean isInitialized , isReInitializing;
	private NetVertexServerContext serverContext;
	public AcctServerCounters(NetVertexServerContext serverContext){
		this.serverContext = serverContext;
		acctClientEntryMap = new HashMap<String, RadiusAcctClientEntry>();
		clientMap = new HashMap<Integer,String>();

		resetTime = new AtomicLong();
		configReset = CONFIG_RESET_Other;

		totalReq = new AtomicLong();
		totalInvalidReq = new AtomicLong();
		totalDupReq = new AtomicLong();
		totalRes = new AtomicLong();
		totalMalformedReq = new AtomicLong();
		totalAccessReject = new AtomicLong();
		totalBadAuthenticator = new AtomicLong();
		totalPackDrop = new AtomicLong();
		totalNoRecord = new AtomicLong();
		totalUnknownType = new AtomicLong();
		totalStartReq = new AtomicLong();
		totalStopReq = new AtomicLong();
		totalIntrUpdateReq = new AtomicLong();
	}

	
	public void init(){
		if (!isInitialized){
			configReset = CONFIG_RESET_Initializing;
			servIdent = serverContext.getServerConfiguration().getRadiusGatewayEventListenerConfiguration().getIPAddress();

			servUpTime = System.currentTimeMillis();
			resetTime.set(System.currentTimeMillis());
			Map<String, RadiusAcctClientEntry> acctClientEntryMap = new HashMap<String, RadiusAcctClientEntry>();
			Map<Integer, String> clientMap = new HashMap<Integer,String>();
			int clientIndexCounter = 0;
			Collection<RadiusGatewayConfiguration> clientAddressList =  serverContext.getServerConfiguration().getRadiusGatewayConfigurations();
			for(RadiusGatewayConfiguration clientConfiguration : clientAddressList){
				clientMap.put(clientIndexCounter, clientConfiguration.getIPAddress());
				acctClientEntryMap.put(clientConfiguration.getIPAddress(), new RadiusAcctClientEntry(clientIndexCounter, clientConfiguration.getIPAddress()
						, clientConfiguration.getName()));
				clientIndexCounter++;
			}
			this.clientMap = clientMap;
			this.acctClientEntryMap = acctClientEntryMap;
			isInitialized = true;
			configReset = CONFIG_RESET_Running;
		}
	}

	private RadiusAcctClientEntry addClientEntry(String clientIp){
		RadiusGatewayConfiguration gatewayConfiguration = serverContext.getServerConfiguration().getRadiusGatewayConfiguration(clientIp);
		
		if(gatewayConfiguration == null){
			return null;
		}
		
		synchronized (this) {
			RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
			if(clientEntry == null){
				int index = acctClientEntryMap.size();
				clientEntry = new RadiusAcctClientEntry(index, clientIp ,gatewayConfiguration.getName());
				acctClientEntryMap.put(clientIp, clientEntry);
				clientMap.put(index, clientIp);
			}
			
			return clientEntry;
		}
	}

	public static class RadiusAcctClientEntry {

		private int clientIndex;                   
		private String clientAddress;                 
		private String clientID;

		private AtomicLong req;
		private AtomicLong startReqCntr;
		private AtomicLong stopReqCntr;
		private AtomicLong intrUpdateReqCntr;
		private AtomicLong dupReq;
		private AtomicLong packDrop; 
		private AtomicLong badAuthenticator;
		private AtomicLong malformedReq;
		private AtomicLong res;   
		private AtomicLong noRecord;
		private AtomicLong unknownType;            

		public RadiusAcctClientEntry(int radiusAcctClientIndex, String radiusAccClientAddress , String clientID){
			this.clientIndex = radiusAcctClientIndex;
			this.clientAddress = radiusAccClientAddress;
			this.clientID = clientID;
			req = new AtomicLong();
			dupReq = new AtomicLong();
			packDrop = new AtomicLong(); 
			badAuthenticator = new AtomicLong();
			malformedReq  = new AtomicLong();
			res = new AtomicLong();   
			noRecord = new AtomicLong();
			unknownType = new AtomicLong(); 
			startReqCntr = new AtomicLong();
			stopReqCntr = new AtomicLong();
			intrUpdateReqCntr = new AtomicLong();
		}
		public String getRadiusAccClientAddress() {
			return clientAddress;
		}
		public String getID() {
			return clientID;
		}
		public int getIndex() {
			return clientIndex;
		}
		public long getPackDropCntr() {
			return packDrop.get();
		}
		private void incPackDropCntr() {
			this.packDrop.incrementAndGet();
		}
		public long getReqCntr() {
			return req.get();
		}
		public long getStartReqCntr() {
			return startReqCntr.get();
		}
		public long getStopReqCntr() {
			return stopReqCntr.get();
		}
		public long getIntrUpdateReqCntr() {
			return intrUpdateReqCntr.get();
		}
		private void incReqCntr() {
			this.req.incrementAndGet();
		}
		private void incStartReqCntr() {
			this.startReqCntr.incrementAndGet();
		}
		private void incStopReqCntr() {
			this.stopReqCntr.incrementAndGet();
		}
		private void incIntrUpdateReqCntr() {
			this.intrUpdateReqCntr.incrementAndGet();
		}
		public long getDupReqCntr() {
			return dupReq.get();
		}
		private void incDupReqCntr() {
			this.dupReq.incrementAndGet();
		}
		public long getResCntr() {
			return res.get();
		}
		private void incResCntr() {
			this.res.incrementAndGet();
		}
		public long getBadAuthenticatorCntr() {
			return badAuthenticator.get();
		}
		private void incBadAuthenticatorCntr() {
			this.badAuthenticator.incrementAndGet();
		}
		public long getRadMalformedReqCntr() {
			return malformedReq.get();
		}
		private void incMalformedReqCntr() {
			this.malformedReq.incrementAndGet();
		}
		public long getNoRecordCntr() {
			return noRecord.get();
		}
		private void incNoRecordCntr() {
			this.noRecord.incrementAndGet();
		}
		public long getUnknownTypeCntr() {
			return unknownType.get();
		}
		private void incUnknownTypeCntr() {
			this.unknownType.incrementAndGet();
		}
	}

	public String getServIdent() {
		return servIdent;
	}

	public long getServUpTime() {
		return (System.currentTimeMillis() - servUpTime)/10;
	}
	public long getResetTime() {
		return (System.currentTimeMillis() - resetTime.get()) / 10;
	}
	public void setConfigReset(int configReset) {
		if(configReset == CONFIG_RESET_Reset){
			reInitialize();
		}
	}
	
	public int getConfigReset() {
		return configReset;
	}
	
	public String getID(String ipAddress) {
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(ipAddress);
		if(clientEntry  != null ){
			return clientEntry.getID();
		}
		return "";
	}
	
	public long getReqCntr() {
		return totalReq.get();
	}
	public long getStartReqCntr() {
		return totalStartReq.get();
	}
	public long getStopReqCntr() {
		return totalStopReq.get();
	}
	public long getIntrUpdateReqCntr() {
		return totalIntrUpdateReq.get();
	}
	public void incReqCntr() {
		totalReq.incrementAndGet();
	}
	public long geReqCntr(String clientIp) {
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getReqCntr();
		}
		return 0L;
	}
	public long getStartReqCntr(String clientIp) {
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getStartReqCntr();
		}
		return 0L;
	}
	public long getStopReqCntr(String clientIp) {
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getStopReqCntr();
		}
		return 0L;
	}
	public long geIntrUdateReqCntr(String clientIp) {
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getIntrUpdateReqCntr();
		}
		return 0L;
	}
	public void incReqCntr(String clientIp) {
		incReqCntr();
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		
		if(clientEntry  == null ){
			clientEntry = addClientEntry(clientIp);
			
			if(clientEntry == null){
				return;
			}
		}
		
		clientEntry.incReqCntr();
	}

	public long getPackDropCntr() {
		return totalPackDrop.get();
	}
	public long getPackDropCntr(String clientIp) {
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getPackDropCntr();
		}
		return 0L;
	}
	public void incPackDropCntr() {
		totalPackDrop.incrementAndGet();
	}
	public void incPackDropCntr(String clientIp) {
		incPackDropCntr();
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		
		if(clientEntry  == null ){
			clientEntry = addClientEntry(clientIp);
			
			if(clientEntry == null){
				return;
			}
		}
		
		clientEntry.incPackDropCntr();
	}

	public long getDupReqCntr() {
		return totalDupReq.get();
	}
	public void incDupReqCntr() {
		totalDupReq.incrementAndGet();
	}
	public long getDupReqCntr(String clientIp) {
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getDupReqCntr();
		}
		return 0L;
	}
	public void incDupReqCntr(String clientIp) {
		incDupReqCntr();
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		
		if(clientEntry  == null ){
			clientEntry = addClientEntry(clientIp);
			
			if(clientEntry == null){
				return;
			}
		}
		
		clientEntry.incDupReqCntr();

	}


	public long getResCntr() {
		return totalRes.get();
	}
	public void incResCntr() {
		totalRes.incrementAndGet();
	}
	public long getResCntr(String clientIp) {
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getResCntr();
		}
		return 0L;
	}
	public void incResCntr(String clientIp) {
		incResCntr();
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		
		if(clientEntry  == null ){
			clientEntry = addClientEntry(clientIp);
			
			if(clientEntry == null){
				return;
			}
		}
		
		clientEntry.incResCntr();
	}

	public long getMalformedReqCntr() {
		return totalMalformedReq.get();
	}
	public void incMalformedReqCntr() {
		totalMalformedReq.incrementAndGet();
	}
	public long getMalformedReqCntr(String clientIp) {
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getRadMalformedReqCntr();
		}
		return 0L;
	}
	public void incMalformedReqCntr(String clientIp) {
		incMalformedReqCntr();
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		
		if(clientEntry  == null ){
			clientEntry = addClientEntry(clientIp);
			
			if(clientEntry == null){
				return;
			}
		}
		
		clientEntry.incMalformedReqCntr();
	}


	public long getBadAuthenticatorCntr() {
		return totalBadAuthenticator.get();
	}
	public void incBadAuthenticatorCntr() {
		totalBadAuthenticator.incrementAndGet();
	}
	public long getBadAuthenticatorsCntr(String clientIp) {
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getBadAuthenticatorCntr();
		}
		return 0L;
	}
	public void incBadAuthenticatorCntr(String clientIp) {
		incBadAuthenticatorCntr();
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		
		if(clientEntry  == null ){
			clientEntry = addClientEntry(clientIp);
			
			if(clientEntry == null){
				return;
			}
		}
		clientEntry.incBadAuthenticatorCntr();

	}

	public long getNoRecordCntr() {
		return totalNoRecord.get();
	}
	public void incNoRecordCntr() {
		totalNoRecord.incrementAndGet();
	}
	public long getNoRecordCntr(String clientIp) {
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getNoRecordCntr();
		}
		return 0L;
	}
	public void incNoRecordCntr(String clientIp) {
		incNoRecordCntr();
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		
		if(clientEntry  == null ){
			clientEntry = addClientEntry(clientIp);
			
			if(clientEntry == null){
				return;
			}
		}
		
		clientEntry.incNoRecordCntr();
		
		
	}

	public long getUnknownTypeCntr() {
		return totalUnknownType.get();
	}

	public void incUnknownTypeCntr() {
		totalUnknownType.incrementAndGet();
	}
	public long getUnknownTypeCntr(String clientIp) {
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getUnknownTypeCntr();
		}
		return 0L;
	}
	public void incUnknownTypeCntr(String clientIp) {
		incUnknownTypeCntr();
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		
		if(clientEntry  == null ){
			clientEntry = addClientEntry(clientIp);
			
			if(clientEntry == null){
				return;
			}
		}
		
		clientEntry.incUnknownTypeCntr();
	}

	public long getTotalInvalidReqCntr() {
		return totalInvalidReq.get();
	}

	public void incTotalInvalidReqCntr() {
		this.totalInvalidReq.incrementAndGet();
	}

	public long getTotalAccessRejectCntr() {
		return totalAccessReject.get();
	}

	public void incTotalAccessRejectCntr() {
		totalAccessReject.incrementAndGet();
	}

	public RadiusAcctClientEntry getAcctClientEntry(String clintIp) {
		return acctClientEntryMap.get(clintIp);
	}

	public void incStartReqCntr(String clientIp) {
		incStartReqCntr();
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		
		if(clientEntry  == null ){
			clientEntry = addClientEntry(clientIp);
			
			if(clientEntry == null){
				return;
			}
		}
		
		clientEntry.incStartReqCntr();
	}
	
	public void incStopReqCntr(String clientIp) {
		incStopReqCntr();
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		
		if(clientEntry  == null ){
			clientEntry = addClientEntry(clientIp);
			
			if(clientEntry == null){
				return;
			}
		}
		
		
		clientEntry.incStopReqCntr();
	}
	
	public void incIntrUpdateReqCntr(String clientIp) {
		incIntrUpdateReqCntr();
		RadiusAcctClientEntry clientEntry = acctClientEntryMap.get(clientIp);
		if(clientEntry  == null ){
			clientEntry = addClientEntry(clientIp);
			
			if(clientEntry == null){
				return;
			}
		}
		
		clientEntry.incIntrUpdateReqCntr();
	}
	
	public void incStartReqCntr() {
		totalStartReq.incrementAndGet();
	}
	public void incStopReqCntr() {
		totalStopReq.incrementAndGet();
	}
	public void incIntrUpdateReqCntr() {
		totalIntrUpdateReq.incrementAndGet();
	}
	
	private void reInitialize(){
		if(!isReInitializing){
			configReset = CONFIG_RESET_Initializing;
			isReInitializing = true;
			isInitialized = false;
			
			resetTime.set(System.currentTimeMillis());
			totalReq.set(0);
			totalInvalidReq.set(0);
			totalDupReq.set(0);
			totalRes.set(0);
			totalMalformedReq.set(0);
			totalAccessReject.set(0);
			totalBadAuthenticator.set(0);
			totalPackDrop.set(0);
			totalNoRecord.set(0);
			totalUnknownType.set(0);
			totalStartReq.set(0);
			totalStopReq.set(0);
			totalIntrUpdateReq.set(0);

			servIdent = serverContext.getServerConfiguration().getRadiusGatewayEventListenerConfiguration().getIPAddress();

			Map<String, RadiusAcctClientEntry> acctClientEntryMap = new HashMap<String, RadiusAcctClientEntry>();
			Map<Integer, String> clientMap = new HashMap<Integer,String>();
			int clientIndexCounter = 0;
			Collection<RadiusGatewayConfiguration> clientAddressList =  serverContext.getServerConfiguration().getRadiusGatewayConfigurations();
			for(RadiusGatewayConfiguration clientConfiguration : clientAddressList){
				clientMap.put(clientIndexCounter, clientConfiguration.getIPAddress());
				acctClientEntryMap.put(clientConfiguration.getIPAddress(), new RadiusAcctClientEntry(clientIndexCounter, clientConfiguration.getIPAddress() 
						, clientConfiguration.getName()));
				clientIndexCounter++;
			}
			this.clientMap = clientMap;
			this.acctClientEntryMap = acctClientEntryMap;
			isInitialized = true;
			configReset = CONFIG_RESET_Running;
			isReInitializing = false;
		}
	}
}
