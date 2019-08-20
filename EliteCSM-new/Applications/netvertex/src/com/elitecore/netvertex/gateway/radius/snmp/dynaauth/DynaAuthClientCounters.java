package com.elitecore.netvertex.gateway.radius.snmp.dynaauth;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;

public class DynaAuthClientCounters {

	private HashMap<String, DynAuthServerEntry> dynaAuthtServerEntryMap;
	private HashMap<Integer, String> clientMap;
	private NetVertexServerContext serverContext;

	// InetAddressType
	private static final int UNKNOWN = 0;
	private static final int IPv4 	= 1;
	private static final int IPv6 	= 2;

	private AtomicLong disInvalidAddr;  
	private AtomicLong coAInvalidAddr;

	private AtomicLong totalDisReq;
	private AtomicLong totalDisAuthOnlyReq;
	private AtomicLong totalDisRetra;
	private AtomicLong totalDisAck;
	private AtomicLong totalDisNack;
	private AtomicLong totalDisNackAuthOnlyReq;
	private AtomicLong totalDisNackSessNoCtx;

	private AtomicLong totalMalformedDisRes;
	private AtomicLong totalDisBadAuthenticator;
	private AtomicLong totalDisPenReq;

	private AtomicLong totalDisTimeout;
	private AtomicLong totalDisPackDrop;

	// COA Requests Counters

	private AtomicLong totalCoAReq;

	private AtomicLong totalCoAAuthOnlyReq;
	private AtomicLong totalCoARetra;
	private AtomicLong totalCoAAck;
	private AtomicLong totalCoANak;
	private AtomicLong totalCoANakAuthOnlyReq;
	private AtomicLong totalCoANakSessNoCtx;

	private AtomicLong totalMalformedCoARes;
	private AtomicLong totalCoABadAuthenticator;
	private AtomicLong totalCoAPenReq;
	private AtomicLong totalCoATimeout;
	private AtomicLong totalCoAPackDrop;

	private AtomicLong totalUnknownType;


	public DynaAuthClientCounters(NetVertexServerContext serverContext) {
		this.serverContext = serverContext;
		dynaAuthtServerEntryMap = new HashMap<String, DynaAuthClientCounters.DynAuthServerEntry>();
		clientMap = new HashMap<Integer, String>();
		this.coAInvalidAddr = new AtomicLong();
		this.disInvalidAddr = new AtomicLong();

		totalDisReq = new AtomicLong();
		totalDisAuthOnlyReq = new AtomicLong();
		totalDisRetra = new AtomicLong();
		totalDisAck = new AtomicLong();
		totalDisNack = new AtomicLong();
		totalDisNackAuthOnlyReq = new AtomicLong();
		totalDisNackSessNoCtx = new AtomicLong();

		totalMalformedDisRes = new AtomicLong();
		totalDisBadAuthenticator = new AtomicLong();
		totalDisPenReq = new AtomicLong();

		totalDisTimeout = new AtomicLong();
		totalDisPackDrop = new AtomicLong();

		// COA Requests Counters

		totalCoAReq = new AtomicLong();
		coAInvalidAddr = new AtomicLong();
		totalCoAAuthOnlyReq = new AtomicLong();
		totalCoARetra = new AtomicLong();
		totalCoAAck = new AtomicLong();
		totalCoANak = new AtomicLong();
		totalCoANakAuthOnlyReq = new AtomicLong();
		totalCoANakSessNoCtx = new AtomicLong();

		totalMalformedCoARes = new AtomicLong();
		totalCoABadAuthenticator = new AtomicLong();
		totalCoAPenReq = new AtomicLong();
		totalCoATimeout = new AtomicLong();
		totalCoAPackDrop = new AtomicLong();

		totalUnknownType = new AtomicLong();
	}

	public  void init() {
		int serverIndexCounter = 0;
		Collection<RadiusGatewayConfiguration> serverAddressList = serverContext.getServerConfiguration().getRadiusGatewayConfigurations();
		for(RadiusGatewayConfiguration serverConfiguration : serverAddressList){
			clientMap.put(serverIndexCounter, serverConfiguration.getIPAddress());
			DynAuthServerEntry authServerEntry = new DynAuthServerEntry(serverIndexCounter, 
					serverConfiguration.getIPAddress() , serverConfiguration.getPort() , serverConfiguration.getName());
			authServerEntry.init();
			dynaAuthtServerEntryMap.put(serverConfiguration.getIPAddress(), authServerEntry);
			serverIndexCounter++;
		}
	}

	public static class DynAuthServerEntry {

		private int serverIndex;
		private int serverAddressType;
		private String serverAddress;
		private int port;
		private String serverID;
		private AtomicLong clientRoundTripTime;

		private AtomicLong disconReq;
		private AtomicLong disAuthOnlyReq;  	
		private AtomicLong disRetra;

		private AtomicLong disAck;
		private AtomicLong disnNak;

		private AtomicLong disNakAuthOnlyReq;
		private AtomicLong disNakSessNoCtx;
		
		// Currently We are not detecting Malformed Packets and Bad Authenticators
		private AtomicLong malformedDisRes; 
		private AtomicLong disBadAuthenticator;
		private AtomicLong disPenReq;

		private AtomicLong disTimeout;
		private AtomicLong disPackDrop;

		private AtomicLong coAReq; 
		private AtomicLong coAAuthOnlyReq;
		private AtomicLong coARetra;
		private AtomicLong coAAck;
		private AtomicLong coANak;
		private AtomicLong coANakAuthOnlyReq;
		private AtomicLong coANakSessNoCtx;

		// Currently We are not detecting Malformed Packets and Bad Authenticators
		private AtomicLong malformedCoARes;
		private AtomicLong coABadAuthenticator;
		
		private AtomicLong coAPenReq;
		private AtomicLong coATimeout;
		private AtomicLong coAPackDrop;

		private AtomicLong unknownType;
		private long counterDiscontinuity;

		public DynAuthServerEntry(int dynAuthServerIndex, String dynAuthServerAddress , int port, String serverID) {
			this.serverIndex = dynAuthServerIndex;
			this.serverID = serverID ;
			this.serverAddress = dynAuthServerAddress;
			this.port = port;
			serverAddressType = UNKNOWN;
			clientRoundTripTime = new AtomicLong();

			disconReq = new AtomicLong();
			disAuthOnlyReq = new AtomicLong();
			disRetra = new AtomicLong();

			disAck = new AtomicLong();
			disnNak = new AtomicLong();

			disNakAuthOnlyReq = new AtomicLong();
			disNakSessNoCtx = new AtomicLong();
			malformedDisRes = new AtomicLong();
			disBadAuthenticator = new AtomicLong();
			disPenReq = new AtomicLong();

			disTimeout = new AtomicLong();
			disPackDrop = new AtomicLong();

			coAReq = new AtomicLong();
			coAAuthOnlyReq = new AtomicLong();
			coARetra = new AtomicLong();
			coAAck = new AtomicLong();
			coANak = new AtomicLong();
			coANakAuthOnlyReq = new AtomicLong();
			coANakSessNoCtx = new AtomicLong();

			malformedCoARes = new AtomicLong();

			coABadAuthenticator = new AtomicLong();
			coAPenReq = new AtomicLong();
			coATimeout = new AtomicLong();
			coAPackDrop = new AtomicLong();

			unknownType = new AtomicLong();
			counterDiscontinuity = System.currentTimeMillis(); 
		}

		private void init() {
			try {
				InetAddress inetAddress = InetAddress.getByName(serverAddress);
				if (inetAddress instanceof Inet4Address) {
					serverAddressType = IPv4;
				} else if (inetAddress instanceof Inet6Address) {
					serverAddressType = IPv6;
				}
			} catch (UnknownHostException e) {
				serverAddressType = UNKNOWN;
			}
		}

		public int getDynAuthServerIndex() {
			return serverIndex;
		}

		public int getDynAuthServerAddressType() {
			return serverAddressType;
		}

		public String getDynAuthServerAddress() {
			return serverAddress;
		}

		public String getDynAuthServerID() {
			return serverID;
		}

		public long getDynAuthServerPort() {
			return port;
		}

		public long getRoundTripTime() {
			return clientRoundTripTime.get();
		}

		public void setRoundTripTime(long roundTripTime) {
			this.clientRoundTripTime.set(roundTripTime / 10);
		}

		public long getDisReqCntr() {
			return disconReq.get();
		}

		private void incDisReqCntr() {
			this.disconReq.incrementAndGet();
		}

		public long getDisAuthOnlyReqCntr() {
			return disAuthOnlyReq.get();
		}

		private void incDisAuthOnlyReqCntr() {
			this.disAuthOnlyReq.incrementAndGet();
		}

		public long getDisRetraCntr() {
			return disRetra.get();
		}

		private void incDisRetraCntr() {
			this.disRetra.incrementAndGet();
		}

		public long getDisAckCntr() {
			return disAck.get();
		}

		private void incDisAckCntr() {
			this.disAck.incrementAndGet();
		}

		public long getDisNakCntr() {
			return disnNak.get();
		}

		private void incDisNackCntr() {
			this.disnNak.incrementAndGet();
		}

		public long getDisNakAuthOnlyReqCntr() {
			return disNakAuthOnlyReq.get();
		}

		private void incDisNackAuthOnlyReqCntr() {
			this.disNakAuthOnlyReq.incrementAndGet();
		}

		public long getDisNakSessNoCtxCntr() {
			return disNakSessNoCtx.get();
		}

		private void incDisNackSessNoCtxCntr() {
			this.disNakSessNoCtx.incrementAndGet();
		}

		public long getMalformedDisResCntr() {
			return malformedDisRes.get();
		}

		private void incDisMalformedResCntr() {
			this.malformedDisRes.incrementAndGet();
		}

		public long getDisBadAuthenticatorCntr() {
			return disBadAuthenticator.get();
		}

		private void incDisBadAuthenticatorCntr() {
			this.disBadAuthenticator.incrementAndGet();
		}

		public long getDisPenReqCntr() {
			return disPenReq.get();
		}

		private void incDisPenReqCntr() {
			this.disPenReq.incrementAndGet();
		}

		private void decDisPenReqCntr() {
			if(disPenReq.get() > 0)
				disPenReq.decrementAndGet();
		}

		public long getDisTimeoutCntr() {
			return disTimeout.get();
		}

		private void incDisTimeoutCntr() {
			this.disTimeout.incrementAndGet();
		}

		public long getDisPackDropCntr() {
			return disPackDrop.get();
		}

		private void incDisPackDropCntr() {
			this.disPackDrop.incrementAndGet();
		}

		public long getCoAReqCntr() {
			return coAReq.get();
		}

		private void incCoAReqCntr() {
			this.coAReq.incrementAndGet();
		}

		public long getCoAAuthOnlyReqCntr() {
			return coAAuthOnlyReq.get();
		}

		private void incCoAAuthOnlyReqCntr() {
			this.coAAuthOnlyReq.incrementAndGet();
		}

		public long getCoARetraCntr() {
			return coARetra.get();
		}

		private void incCoARetraCntr() {
			this.coARetra.incrementAndGet();
		}

		public long getCoAAckCntr() {
			return coAAck.get();
		}

		private void incCoAAckCntr() {
			this.coAAck.incrementAndGet();
		}

		public long getDynAuthCoANakCntr() {
			return coANak.get();
		}

		private void incCoANakCntr() {
			this.coANak.incrementAndGet();
		}

		public long getCoANakAuthOnlyReqCntr() {
			return coANakAuthOnlyReq.get();
		}

		private void incCoANakAuthOnlyReqCntr() {
			this.coANakAuthOnlyReq.incrementAndGet();
		}

		public long getCoANakSessNoCtxCntr() {
			return coANakSessNoCtx.get();
		}

		private void incCoANakSessNoCtxCntr() {
			this.coANakSessNoCtx.incrementAndGet();
		}

		public long getCoAMalformedResCntr() {
			return malformedCoARes.get();
		}

		private void incCoAMalformedResCntr() {
			this.malformedCoARes.incrementAndGet();
		}

		public long getCoABadAuthenticatorCntr() {
			return coABadAuthenticator.get();
		}

		private void incCoABadAuthenticatorCntr() {
			this.coABadAuthenticator.incrementAndGet();
		}

		public long getCoAPenReqCntr() {
			return coAPenReq.get();
		}

		private void incCoAPenReqCntr() {
			this.coAPenReq.incrementAndGet();
		}

		private void decCoAPenReqCntr() {
			if(coAPenReq.get() > 0)
				coAPenReq.decrementAndGet();
		}

		public long getCoATimeoutCntr() {
			return coATimeout.get();
		}

		private void incCoATimeoutCntr() {
			this.coATimeout.incrementAndGet();
		}

		public long getCoAPackDropCntr() {
			return coAPackDrop.get();
		}

		private void incCoAPackDropCntr() {
			this.coAPackDrop.incrementAndGet();
		}

		public long getUnknownTypeCntr() {
			return unknownType.get();
		}

		private void incUnknownTypeCntr() {
			this.unknownType.incrementAndGet();
		}

		public long getCounterDiscontinuity() {
			return (System.currentTimeMillis() - counterDiscontinuity) / 10;
		}
	}

	public int getServerIndex(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDynAuthServerIndex();
		}
		return 0;
	}

	public int getServerAddressType(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDynAuthServerAddressType();
		}
		return 0;
	}

	public String getServerAddress(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDynAuthServerAddress();
		}
		return "";
	}

	public String getServerID(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDynAuthServerID();
		}
		return "Client with IP : "+ serverIp +" Not Found.";
	}

	public Long getServerPort(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDynAuthServerPort();
		}
		return 0l;
	}

	public long getRoundTripTime(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getRoundTripTime();
		}
		return 0L;
	}

	public void setRoundTripTime(String serverIp,long roundTripTime) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.setRoundTripTime(roundTripTime);
		}
	}

	public long getDisReqCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDisReqCntr();
		}
		return 0L;
	}

	public void incDisReqCntr(String serverIp) {
		incDisReqCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incDisReqCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getDisAuthOnlyReqCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDisAuthOnlyReqCntr();
		}
		return 0L;
	}

	public void incDisAuthOnlyReqCntr(String serverIp) {
		incDisAuthOnlyReqCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incDisAuthOnlyReqCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getDisRetraCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDisRetraCntr();
		}
		return 0L;
	}

	public void incDisRetraCntr(String serverIp) {
		incDisRetraCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incDisRetraCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getDisAckCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDisAckCntr();
		}
		return 0L;
	}

	public void incDisAckCntr(String serverIp) {
		incDisAckCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incDisAckCntr();
		}else{
			incDisInvalidAddrCntr();
			addServerEntry(serverIp);
		}
	}

	public long getDisNakCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDisNakCntr();
		}
		return 0L;
	}

	public void incDisNackCntr(String serverIp) {
		incDisNackCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incDisNackCntr();
		}else{
			incDisInvalidAddrCntr();
			addServerEntry(serverIp);
		}
	}

	public long getDisNakAuthOnlyReqCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDisNakAuthOnlyReqCntr();
		}
		return 0L;
	}

	public void incDisNackAuthOnlyReqCntr(String serverIp) {
		incDisNackthOnlyReqCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incDisNackAuthOnlyReqCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getDisNakSessNoCtxCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDisNakSessNoCtxCntr();
		}
		return 0L;
	}

	public void incDisNackSessNoCtxCntr(String serverIp) {
		incDisNackSessNoCtxCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incDisNackSessNoCtxCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getDisMalformedResCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getMalformedDisResCntr();
		}
		return 0L;
	}

	public void incDisMalfomedResCntr(String serverIp){
		incDisMalformedResCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incDisMalformedResCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getDisBadAuthenticatorCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDisBadAuthenticatorCntr();
		}
		return 0L;
	}

	public void incDisBadAuthenticatorCntr(String serverIp) {
		incDisBadAuthenticatorCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incDisBadAuthenticatorCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getDisPenReqCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDisPenReqCntr();
		}
		return 0L;
	}

	public void incDisPenReqCntr(String serverIp) {
		incDisPenReqCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incDisPenReqCntr();
		}else
			addServerEntry(serverIp);
	}

	public void decDisPenReqCntr(String ipAddress) {
		decDisPenReqCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(ipAddress);
		if(serverEntry !=null){
			serverEntry.decDisPenReqCntr();
		}else
			addServerEntry(ipAddress);
	}

	public void decDisPenReqCntr() {
		if(totalDisPenReq.get() > 0)
			totalDisPenReq.decrementAndGet();
	}

	public long getDisTimeoutCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDisTimeoutCntr();
		}
		return 0L;
	}

	public void incDisTimeoutCntr(String serverIp) {
		incDisTimeoutCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incDisTimeoutCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getDisPackDropCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDisPackDropCntr();
		}
		return 0L;
	}

	public void incDisPackDropCntr(String serverIp) {
		incDisPackDropCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incDisPackDropCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getCoAReqCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getCoAReqCntr();
		}
		return 0L;
	}

	public void incCoAReqCntr(String serverIp) {
		incCoAReqCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incCoAReqCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getCoAAuthOnlyReqCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getCoAAuthOnlyReqCntr();
		}
		return 0L;
	}

	public void incCoAAuthOnlyReqCntr(String serverIp) {
		incCoAAuthOnlyReqCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incCoAAuthOnlyReqCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getCoARetraCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getCoARetraCntr();
		}
		return 0L;
	}

	public void incCoARetraCntr(String serverIp) {
		incCoARetraCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incCoARetraCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getCoAAckCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getCoAAckCntr();
		}
		return 0L;
	}

	public void incCoAAckCntr(String serverIp) {
		incCoAAckCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incCoAAckCntr();
		}else{
			incCoAInvalidAddrCntr();
			addServerEntry(serverIp);
		}
	}

	public long getCoANakCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getDynAuthCoANakCntr();
		}
		return 0L;
	}

	public void incCoANakCntr(String serverIp) {
		incCoANakCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incCoANakCntr();
		}else{
			incCoAInvalidAddrCntr();
			addServerEntry(serverIp);
		}
	}

	public long getCoANakAuthOnlyReqCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getCoANakAuthOnlyReqCntr();
		}
		return 0L;
	}

	public void incCoANakAuthOnlyReqCntr(String serverIp) {
		incCoANakAuthOnlyReqCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incCoANakAuthOnlyReqCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getCoANakSessNoCtxCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getCoANakSessNoCtxCntr();
		}
		return 0L;
	}

	public void incCoANakSessNoCtxCntr(String serverIp) {
		incCoANakSessNoCtxCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incCoANakSessNoCtxCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getCoAMalformedResCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getCoAMalformedResCntr();
		}
		return 0L;
	}

	public void incCoAMalformedResCntr(String serverIp) {
		incCoAMalformedResCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incCoAMalformedResCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getCoABadAuthenticatorCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getCoABadAuthenticatorCntr();
		}
		return 0L;
	}

	public void incCoABadAuthenticatorCntr(String serverIp){ 
		incrCoABadAuthenticatorCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incCoABadAuthenticatorCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getCoAPenReqCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getCoAPenReqCntr();
		}
		return 0L;
	}

	public void incCoAPenReqCntr(String serverIp) {
		incCoAPenReqCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incCoAPenReqCntr();
		}else
			addServerEntry(serverIp);
	}

	public void decCoAPenReqCntr(String ipAddress) {
		decCoAPenReqCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(ipAddress);
		if(serverEntry !=null){
			serverEntry.decCoAPenReqCntr();
		}else
			addServerEntry(ipAddress);
	}

	private void decCoAPenReqCntr() {
		if(totalCoAPenReq.get() > 0)
			totalCoAPenReq.decrementAndGet();		
	}

	public long getCoATimeoutCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getCoATimeoutCntr();
		}
		return 0L;
	}

	public void incCoATimeoutCntr(String serverIp) {
		incCoATimeoutCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incCoATimeoutCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getCoAPackDropCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getCoAPackDropCntr();
		}
		return 0L;
	}

	public void incCoAPackDropCntr(String serverIp) {
		incCoAPackDropCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incCoAPackDropCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getUnknownTypeCntr(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getUnknownTypeCntr();
		}
		return 0L;
	}

	public void incUnknownTypeCntr(String serverIp) {
		incCoAUnknownTypeCntr();
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			serverEntry.incUnknownTypeCntr();
		}else
			addServerEntry(serverIp);
	}

	public long getCounterDiscontinuity(String serverIp) {
		DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
		if(serverEntry !=null){
			return serverEntry.getCounterDiscontinuity();
		}
		return 0L;
	}
	
	public DynAuthServerEntry getClientEntry(String serverIp){
		return dynaAuthtServerEntryMap.get(serverIp);
	}

	private void addServerEntry(String serverIp) {
		RadiusGatewayConfiguration gatewayConfiguration = serverContext.getServerConfiguration().getRadiusGatewayConfiguration(serverIp);
		if(gatewayConfiguration != null ){
			synchronized (this) {
				DynAuthServerEntry serverEntry = dynaAuthtServerEntryMap.get(serverIp);
				if(serverEntry == null){
					int index = dynaAuthtServerEntryMap.size();
					DynAuthServerEntry authServerEntry = new DynAuthServerEntry(index, serverIp , gatewayConfiguration.getPort() 
							, gatewayConfiguration.getName());
					authServerEntry.init();
					dynaAuthtServerEntryMap.put(serverIp, authServerEntry );
					clientMap.put(index, serverIp);
				}
			}
		}
	}

	// Total Dyna Auth Counters 

	public long getDisInvalidAddrCntr() {
		return disInvalidAddr.get();
	}

	public void incDisInvalidAddrCntr() {
		this.disInvalidAddr.incrementAndGet();
	}

	public long getCoAInvalidServerAddrCntr() {
		return coAInvalidAddr.get();
	}

	public void incCoAInvalidAddrCntr() {
		this.coAInvalidAddr.incrementAndGet();
	}

	public long getDisReqCntr() {
		return totalDisReq.get();
	}

	public void incDisReqCntr() {
		this.totalDisReq.incrementAndGet();
	}

	public long getDisAuthOnlyReqCntr() {
		return totalDisAuthOnlyReq.get();
	}

	public void incDisAuthOnlyReqCntr() {
		this.totalDisAuthOnlyReq.incrementAndGet();
	}

	public long getDisRetraCntr() {
		return totalDisRetra.get();
	}

	public void incDisRetraCntr() {
		this.totalDisRetra.incrementAndGet();
	}

	public long getDisAckCntr() {
		return totalDisAck.get();
	}

	public void incDisAckCntr() {
		this.totalDisAck.incrementAndGet();
	}

	public long getDisNackCntr() {
		return totalDisNack.get();
	}

	public void incDisNackCntr() {
		this.totalDisNack.incrementAndGet();
	}

	public long getDisNackAuthOnlyReqCntr() {
		return totalDisNackAuthOnlyReq.get();
	}

	public void incDisNackthOnlyReqCntr() {
		this.totalDisNackAuthOnlyReq.incrementAndGet();
	}

	public long getDisNackSessNoCtxCntr() {
		return totalDisNackSessNoCtx.get();
	}

	public void incDisNackSessNoCtxCntr() {
		this.totalDisNackSessNoCtx.incrementAndGet();
	}

	public long getDisMalformedResCntr() {
		return totalMalformedDisRes.get();
	}

	public void incDisMalformedResCntr() {
		this.totalMalformedDisRes.incrementAndGet();
	}

	public long getDisBadAuthenticatorCntr() {
		return totalDisBadAuthenticator.get();
	}

	public void incDisBadAuthenticatorCntr() {
		this.totalDisBadAuthenticator.incrementAndGet();
	}

	public long getDisPenReqCntr() {
		return totalDisPenReq.get();
	}

	public void incDisPenReqCntr() {
		this.totalDisPenReq.incrementAndGet();
	}

	public long getDisTimeoutCntr() {
		return totalDisTimeout.get();
	}

	public void incDisTimeoutCntr() {
		this.totalDisTimeout.incrementAndGet();
	}

	public long getDisPackDropCntr() {
		return totalDisPackDrop.get();
	}

	public void incDisPackDropCntr() {
		this.totalDisPackDrop.incrementAndGet();
	}

	public long getCoAReqCntr() {
		return totalCoAReq.get();
	}

	public void incCoAReqCntr() {
		this.totalCoAReq.incrementAndGet();
	}

	public long getCoAAuthOnlyReqCntr() {
		return totalCoAAuthOnlyReq.get();
	}

	public void incCoAAuthOnlyReqCntr() {
		this.totalCoAAuthOnlyReq.incrementAndGet();
	}

	public long getCoARetraCntr() {
		return totalCoARetra.get();
	}

	public void incCoARetraCntr() {
		this.totalCoARetra.incrementAndGet();
	}

	public long getCoAAckCntr() {
		return totalCoAAck.get();
	}

	public void incCoAAckCntr() {
		this.totalCoAAck.incrementAndGet();
	}

	public long getCoANakCntr() {
		return totalCoANak.get();
	}

	public void incCoANakCntr() {
		this.totalCoANak.incrementAndGet();
	}

	public long getCoANakAuthOnlyReqCntr() {
		return totalCoANakAuthOnlyReq.get();
	}

	public void incCoANakAuthOnlyReqCntr() {
		this.totalCoANakAuthOnlyReq.incrementAndGet();
	}

	public long getCoANakSessNoCtxCntr() {
		return totalCoANakSessNoCtx.get();
	}

	public void incCoANakSessNoCtxCntr() {
		this.totalCoANakSessNoCtx.incrementAndGet();
	}

	public long getCoAMalformedResCntr() {
		return totalMalformedCoARes.get();
	}

	public void incCoAMalformedResCntr() {
		this.totalMalformedCoARes.incrementAndGet();
	}

	public long getCoABadAuthenticatorCntr() {
		return totalCoABadAuthenticator.get();
	}

	public void incrCoABadAuthenticatorCntr() {
		this.totalCoABadAuthenticator.incrementAndGet();
	}

	public long getCoAPenReqCntr() {
		return totalCoAPenReq.get();
	}

	public void incCoAPenReqCntr() {
		this.totalCoAPenReq.incrementAndGet();
	}

	public long getCoATimeoutCntr() {
		return totalCoATimeout.get();
	}

	public void incCoATimeoutCntr() {
		this.totalCoATimeout.incrementAndGet();
	}

	public long getCoAPackDropCntr() {
		return totalCoAPackDrop.get();
	}

	public void incCoAPackDropCntr() {
		this.totalCoAPackDrop.incrementAndGet();
	}

	public long getCoAUnknownTypeCntr() {
		return totalUnknownType.get();
	}

	public void incCoAUnknownTypeCntr() {
		this.totalUnknownType.incrementAndGet();
	}
}