package com.elitecore.netvertex.gateway.radius.snmp.auth;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;

public class AuthServerCounters {

	private String servIdent;
	private long servUpTime;
	private AtomicLong resetTime;

	private final int CONFIG_RESET_Other = 1;
	private final int CONFIG_RESET_Reset = 2;
	private final int CONFIG_RESET_Initializing = 3;
	private final int CONFIG_RESET_Running = 4;
	private int configReset;

	private AtomicLong totalReq;
	private AtomicLong totalInvalidReq;
	private AtomicLong totalDupReq;

	private AtomicLong totalAccessAccept;
	private AtomicLong totalAccessReject;
	private AtomicLong totalAccessChallenges;

	private AtomicLong totalMalformedReq;
	private AtomicLong totalBadAuthenticator;
	private AtomicLong totalPackDrop;
	private AtomicLong totalNoRecord;
	private AtomicLong totalUnknownType;

	private Map<String, RadiusAuthClientEntry> clientEntryMap;
	private Map<Integer, String> clientMap;

	private boolean isInitialized , isReInitializing;
	private NetVertexServerContext serverContext;
	public AuthServerCounters(NetVertexServerContext serverContext){
		this.serverContext = serverContext;
		clientEntryMap = new HashMap<String, RadiusAuthClientEntry>();
		clientMap = new HashMap<Integer,String>();

		resetTime = new AtomicLong();
		configReset = CONFIG_RESET_Other;
		totalReq = new AtomicLong();
		totalInvalidReq = new AtomicLong();
		totalDupReq = new AtomicLong();
		totalMalformedReq = new AtomicLong();
		totalAccessReject = new AtomicLong();
		totalAccessAccept = new AtomicLong();
		totalAccessChallenges = new AtomicLong();
		totalBadAuthenticator = new AtomicLong();
		totalPackDrop = new AtomicLong();
		totalNoRecord = new AtomicLong();
		totalUnknownType = new AtomicLong();
	}

	public void init(){
		if (!isInitialized){
			configReset = CONFIG_RESET_Initializing;
			servUpTime = System.currentTimeMillis();
			resetTime.set(System.currentTimeMillis());
			servIdent = serverContext.getServerConfiguration().getRadiusGatewayEventListenerConfiguration().getIPAddress();

			int clientIndexCounter = 0;
			Collection<RadiusGatewayConfiguration> clientAddressList =  serverContext.getServerConfiguration().getRadiusGatewayConfigurations();
			for(RadiusGatewayConfiguration clientConfiguration : clientAddressList){
				clientMap.put(clientIndexCounter, clientConfiguration.getIPAddress());
				clientEntryMap.put(clientConfiguration.getIPAddress(), new RadiusAuthClientEntry(clientIndexCounter, clientConfiguration.getIPAddress(),
						clientConfiguration.getName()));
				clientIndexCounter++;
			}
			configReset = CONFIG_RESET_Running;
			isInitialized = true;
		}
	}

	private void addClientEntry(String clientIp){
		RadiusGatewayConfiguration gatewayConfiguration = serverContext.getServerConfiguration().getRadiusGatewayConfiguration(clientIp);
		if(gatewayConfiguration != null ){
			synchronized (this) {
				RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
				if(clientEntry == null){
					int index = clientEntryMap.size();
					clientEntryMap.put(clientIp, new RadiusAuthClientEntry(index, clientIp 
							, gatewayConfiguration.getName()));
					clientMap.put(index, clientIp);
				}
			}
		}
	}

	public static class RadiusAuthClientEntry {

		private int clientIndex;                   
		private String clientAddress;                 
		private String clientID;

		private AtomicLong req;
		private AtomicLong dupReq;
		private AtomicLong packDrop; 
		private AtomicLong badAuthenticator;
		private AtomicLong malformedReq;
		private AtomicLong noRecord;
		private AtomicLong unknownType;

		private AtomicLong accessAccept;
		private AtomicLong accessReject;
		private AtomicLong accessChallenges;

		public RadiusAuthClientEntry(int clientIndex, String clientAddress , String gatewayName){
			this.clientIndex = clientIndex;
			this.clientAddress = clientAddress;
			this.clientID = gatewayName;
			req = new AtomicLong();
			dupReq = new AtomicLong();
			packDrop = new AtomicLong(); 
			badAuthenticator = new AtomicLong();
			malformedReq  = new AtomicLong();
			noRecord = new AtomicLong();
			unknownType = new AtomicLong();
			accessAccept = new AtomicLong();
			accessReject = new AtomicLong();
			accessChallenges = new AtomicLong();
		}
		public String getRadiusAuthClientAddress() {
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
		private void incReqCntr() {
			this.req.incrementAndGet();
		}
		public long getDupReqCntr() {
			return dupReq.get();
		}
		private void incDupReqCntr() {
			this.dupReq.incrementAndGet();
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
		public long getAccessAcceptCntr() {
			return accessAccept.get();
		}
		private void incAccessAcceptCntr() {
			this.accessAccept.incrementAndGet();
		}
		public long getAccessRejectCntr() {
			return accessReject.get();
		}
		private void incAccessRejectCntr() {
			this.accessReject.incrementAndGet();
		}
		public long getAccessChallengesCntr() {
			return this.accessChallenges.get();
		}
		private void incAccessChallengesCntr() {
			this.accessChallenges.incrementAndGet();
		}
	}

	public String getServerIdent() {
		return servIdent;
	}

	public long getServUpTime() {
		return (System.currentTimeMillis() - servUpTime)/10;
	}
	public long getResetTime() {
		return (System.currentTimeMillis() - resetTime.get())/10;
	}
	public long getReqCntr() {
		return totalReq.get();
	}
	public void incReqCntr() {
		totalReq.incrementAndGet();
	}

	public String getClientId(String clientIp) {
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getID();
		}
		return "";
	}

	public long geReqCntr(String clientIp) {
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getReqCntr();
		}
		return 0L;
	}
	public void incReqCntr(String clientIp) {
		incReqCntr();
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			clientEntry.incReqCntr();
		}else{
			addClientEntry(clientIp);
		}
	}

	public long getPackDropCntr() {
		return totalPackDrop.get();
	}
	public long getPackDropCntr(String clientIp) {
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
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
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			clientEntry.incPackDropCntr();
		}else{
			addClientEntry(clientIp);
		}
	}

	public long getDupReqCntr() {
		return totalDupReq.get();
	}
	public void incDupReqCntr() {
		totalDupReq.incrementAndGet();
	}
	public long getDupReqCntr(String clientIp) {
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getDupReqCntr();
		}
		return 0L;
	}
	public void incDupReqCntr(String clientIp) {
		incDupReqCntr();
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			clientEntry.incDupReqCntr();
		}else{
			addClientEntry(clientIp);
		}
	}

	public long getAccessAcceptCntr(String clientIp) {
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getAccessAcceptCntr();
		}
		return 0L;
	}
	public void incAccessAcceptCntr(String clientIp) {
		intAccessAcceptCntr();
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			clientEntry.incAccessAcceptCntr();
		}else{
			addClientEntry(clientIp);
		}
	}

	public long getAccessRejectCntr(String clientIp) {
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getAccessRejectCntr();
		}
		return 0L;
	}
	public void incAccessRejectCntr(String clientIp) {
		incAccessRejectCntr();
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			clientEntry.incAccessRejectCntr();
		}else{
			addClientEntry(clientIp);
		}
	}

	public void incAccessChallengesCntr(String clientIp) {
		incAccessChallengesCntr();
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			clientEntry.incAccessChallengesCntr();
		}else{
			addClientEntry(clientIp);
		}
	}
	public long getAccessChallengesCntr(String clientIp) {
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getAccessChallengesCntr();
		}
		return 0L;
	}

	public long getMalformedReqCntr() {
		return totalMalformedReq.get();
	}
	public void incMalformedReqCntr() {
		totalMalformedReq.incrementAndGet();
	}
	public long getMalformedReqCntr(String clientIp) {
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getRadMalformedReqCntr();
		}
		return 0L;
	}
	public void incMalformedReqCntr(String clientIp) {
		incMalformedReqCntr();
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			clientEntry.incMalformedReqCntr();
		}else{
			addClientEntry(clientIp);
		}
	}


	public long getBadAuthenticatorCntr() {
		return totalBadAuthenticator.get();
	}
	public void incBadAuthenticatorCntr() {
		totalBadAuthenticator.incrementAndGet();
	}
	public long getBadAuthenticatorsCntr(String clientIp) {
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getBadAuthenticatorCntr();
		}
		return 0L;
	}
	public void incBadAuthenticatorCntr(String clientIp) {
		incBadAuthenticatorCntr();
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			clientEntry.incBadAuthenticatorCntr();
		}else{
			addClientEntry(clientIp);
		}
	}

	public long getNoRecordCntr() {
		return totalNoRecord.get();
	}
	public void incNoRecordCntr() {
		totalNoRecord.incrementAndGet();
	}
	public long getNoRecordCntr(String clientIp) {
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getNoRecordCntr();
		}
		return 0L;
	}
	public void incNoRecordCntr(String clientIp) {
		incNoRecordCntr();
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			clientEntry.incNoRecordCntr();
		}else{
			addClientEntry(clientIp);
		}
	}

	public long getUnknownTypeCntr() {
		return totalUnknownType.get();
	}

	public void incUnknownTypeCntr() {
		totalUnknownType.incrementAndGet();
	}
	public long getUnknownTypeCntr(String clientIp) {
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			return clientEntry.getUnknownTypeCntr();
		}
		return 0L;
	}
	public void incUnknownTypeCntr(String clientIp) {
		incUnknownTypeCntr();
		RadiusAuthClientEntry clientEntry = clientEntryMap.get(clientIp);
		if(clientEntry  !=null ){
			clientEntry.incUnknownTypeCntr();
		}else{
			addClientEntry(clientIp);
		}
	}

	public long getInvalidReqCntr() {
		return totalInvalidReq.get();
	}

	public void incTotalInvalidReqCntr() {
		this.totalInvalidReq.incrementAndGet();
	}

	public long getAccessRejectCntr() {
		return totalAccessReject.get();
	}

	public void incAccessRejectCntr() {
		totalAccessReject.incrementAndGet();
	}

	public long getAccessAcceptCntr() {
		return totalAccessAccept.get();
	}

	public void intAccessAcceptCntr() {
		totalAccessAccept.incrementAndGet();
	}

	public Long getAccessChallenges() {
		return totalAccessChallenges.get();
	}

	public Long incAccessChallengesCntr() {
		return totalAccessChallenges.incrementAndGet();
	}

	public RadiusAuthClientEntry getAuthClientEntry(String clintIp) {
		return clientEntryMap.get(clintIp);
	}

	public int getConfigReset() {
		return configReset;
	}

	public void setConfigReset(int configReset) {
		if(configReset == CONFIG_RESET_Reset ){
			reInitialize();
		}

	}

	private void reInitialize() {
		if(!isReInitializing){
			configReset = CONFIG_RESET_Initializing;
			isReInitializing = true;
			isInitialized = false;

			servIdent = serverContext.getServerConfiguration().getRadiusGatewayEventListenerConfiguration().getIPAddress();
			resetTime.set(System.currentTimeMillis());

			totalReq.set(0);
			totalInvalidReq.set(0);
			totalDupReq.set(0);

			totalAccessAccept.set(0);
			totalAccessReject.set(0);

			totalMalformedReq.set(0);
			totalBadAuthenticator.set(0);
			totalPackDrop.set(0);
			totalNoRecord.set(0);
			totalUnknownType.set(0);
			Map<String, RadiusAuthClientEntry> clientEntryMap = new HashMap<String, AuthServerCounters.RadiusAuthClientEntry>();
			Map<Integer, String> clientMap = new HashMap<Integer, String>();
			int clientIndexCounter = 0;
			Collection<RadiusGatewayConfiguration> clientAddressList =  serverContext.getServerConfiguration().getRadiusGatewayConfigurations();
			for(RadiusGatewayConfiguration clientConfiguration : clientAddressList){
				clientMap.put(clientIndexCounter, clientConfiguration.getIPAddress());
				clientEntryMap.put(clientConfiguration.getIPAddress(), new RadiusAuthClientEntry(clientIndexCounter, clientConfiguration.getIPAddress() ,
						clientConfiguration.getName()));
				clientIndexCounter++;
			}
			this.clientEntryMap = clientEntryMap;
			this.clientMap = clientMap;
			configReset = CONFIG_RESET_Running;
			isInitialized = true;
			isReInitializing = false;
		}
	}
}
