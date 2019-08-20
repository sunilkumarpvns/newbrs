package com.elitecore.aaa.rm.service.rdr;

import java.util.HashMap;
import java.util.Map;

public enum RDRTagConstants {
	
	TransactionRDR (4042321936L),
	TransactionUsageRDR (4042323000L),
	HTTPTransactionUsageRDR (4042323004L),
	AnonymizedHTTPTransactionUsageRDR (4042323260L),
	RTSPTransactionUsageRDR (4042323008L),
	VoIPTransactionUsageRDR (4042323050L),
	VideoTransactionUsageRDR (4042323072L),
	GenericUsageRDR (4042322064L),
	//Generic Usage RDRForIPV6 (40423219360L),
	SubscriberUsageRDR (4042321920L),
	RealTimeSubscriberUsageRDR (4042321922L),
	
	LinkUsageRDR (4042321925L),
	ZoneUsageRDR (4042321928L),
	PackageUsageRDR (4042321924L),
	VirtualLinksUsageRDR (4042321926L),
	BlockingRDR (4042321984L),
	QuotaBreachRDR (4042322034L),
	QuotaStatusRDR (4042322033L),
	
	QuotaThresholdBreachRDR (4042322035L),
	SessionCreationRDR (4042322032L),
	DHCPRDR (4042321986L),
	RADIUSRDR (4042321987L),
	FlowStartRDR (4042321942L),
	FlowEndRDR (4042321944L),
	OngoingFlowRDR (4042321943L),
	MediaFlowRDR (4042323052L),
	AttackStartRDR (4042321945L),
	AttackEndRDR (4042321946L),
	MaliciousTrafficPeriodicRDR (4042322000L),
	SpamRDR (4042322048L);
	
	public final long tagID;
	 
	 RDRTagConstants (long tag){
			this.tagID = tag;
		}
	 
	 public static final RDRTagConstants[] tags = values();
		private static final Map<Long , RDRTagConstants> map;

		static {
			map = new HashMap<Long,RDRTagConstants>();
			for ( RDRTagConstants tag : tags){
				map.put(tag.tagID, tag);
			}
		}
		
		public long getTypeID (){
			return tagID;
		}

		public static RDRTagConstants fromTypeID (long tag){
			return map.get(tag);
		}
		public boolean isValid (long value){
			return map.containsKey(value);
		}
}
