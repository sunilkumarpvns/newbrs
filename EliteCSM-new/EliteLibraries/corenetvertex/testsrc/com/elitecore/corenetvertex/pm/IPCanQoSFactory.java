package com.elitecore.corenetvertex.pm;


import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQosBasedComparator;

import java.util.Random;

import static com.elitecore.commons.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;

public class IPCanQoSFactory {
	private static final Random RANDOM = new Random();
	private static final IPCANQosBasedComparator IPCAN_QOS_BASED_COMPARATOR = new IPCANQosBasedComparator();
    public static IPCANQoS randomQoS() {
        return new IPCANQoS.IPCANQoSBuilder().withQCI(QCI.QCI_NON_GBR_9)
                .withAAMBRDL(random())
                .withAAMBRUL(random())
                .withMBRDL(random())
                .withMBRUL(random())
                .withGBRDL(random())
                .withGBRUL(random())
                .build();
    }

	public static IPCANQoS createSessionQoSHasLowerQoSThan(IPCANQoS sessionQoS) {

		IPCANQoS newSessionQoS = new IPCANQoS.IPCANQoSBuilder().withQCI(sessionQoS.getQCI())
				.withAAMBRDL(sessionQoS.getAAMBRDLInBytes() - 1)
				.withAAMBRUL(sessionQoS.getAAMBRULInBytes() - 1)
				.withMBRDL(sessionQoS.getMBRDL() - 1)
				.withMBRUL(sessionQoS.getMBRUL() - 1)
				.withGBRDL(sessionQoS.getGbrdlInBytes() - 1)
				.withGBRUL(sessionQoS.getGbrulInBytes() - 1)
				.build();
		assertEquals(-1, IPCAN_QOS_BASED_COMPARATOR.compare(newSessionQoS,sessionQoS));
		return checkNotNull(newSessionQoS, "Session level qos should not be null");
	}

	public static IPCANQoS createSessionQoSHasEqualQoSTo(IPCANQoS sessionQoS) {
		IPCANQoS newSessionQoS = new IPCANQoS.IPCANQoSBuilder().withQCI(sessionQoS.getQCI())
				.withAAMBRDL(sessionQoS.getAAMBRDLInBytes())
				.withAAMBRUL(sessionQoS.getAAMBRULInBytes())
				.withMBRDL(sessionQoS.getMBRDLInBytes())
				.withMBRUL(sessionQoS.getMBRULInBytes())
				.withGBRDL(sessionQoS.getGbrdlInBytes())
				.withGBRUL(sessionQoS.getGbrulInBytes())
				.build();
		
		assertEquals(0, IPCAN_QOS_BASED_COMPARATOR.compare(newSessionQoS,sessionQoS));
		return checkNotNull(newSessionQoS, "Session level qos should not be null");
	}

	public static IPCANQoS createSessionQoSHasHigherQoSThan(IPCANQoS sessionQoS) {
		IPCANQoS newSessionQoS = new IPCANQoS.IPCANQoSBuilder().withQCI(sessionQoS.getQCI())
				.withAAMBRDL(sessionQoS.getAAMBRDLInBytes() + 1)
				.withAAMBRUL(sessionQoS.getAAMBRULInBytes() + 1)
				.withMBRDL(sessionQoS.getMBRDL() + 1)
				.withMBRUL(sessionQoS.getMBRUL() + 1)
				.withGBRDL(sessionQoS.getGbrdlInBytes() + 1)
				.withGBRUL(sessionQoS.getGbrulInBytes() + 1)
				.build();
		
		assertEquals(1, IPCAN_QOS_BASED_COMPARATOR.compare(newSessionQoS,sessionQoS));
		return checkNotNull(newSessionQoS, "Session level qos should not be null");
	}



	public static BoundryQoSBuilder create() {
		return new BoundryQoSBuilder();
	}
	
	
	public static class BoundryQoSBuilder {
		private IPCANQoS lowerBoundQoS;
		private IPCANQoS upperBoundQoS;
		private IPCANQoS equalQoS;
		
		public BoundryQoSBuilder hasLowerQoSThan(IPCANQoS ipcanQoS) {
			this.upperBoundQoS = ipcanQoS;
			return this;
		}
		
		public BoundryQoSBuilder hasHigherQoSThan(IPCANQoS ipcanQoS) {
			this.lowerBoundQoS = ipcanQoS;
			return this;
		}
		

		public BoundryQoSBuilder hasEqualQoSThan(IPCANQoS equalSessionQoS) {
			this.equalQoS = equalSessionQoS;
			return this;
		}
		
		public IPCANQoS build() {
			
			if(equalQoS == null && lowerBoundQoS == null && upperBoundQoS == null){
				return IPCanQoSFactory.randomQoS();
			}
			
			if(equalQoS == null) {
				
				if(upperBoundQoS == null) {
					return IPCanQoSFactory.createSessionQoSHasHigherQoSThan(lowerBoundQoS);
				}
				
				if(lowerBoundQoS == null) {
					return IPCanQoSFactory.createSessionQoSHasLowerQoSThan(upperBoundQoS);
				}
				
				
				if(IPCAN_QOS_BASED_COMPARATOR.compare(upperBoundQoS,lowerBoundQoS) <= 0) {
					throw new RuntimeException("Lower bound session qos has higher QoS than higher bound sesion QoS");
				}
				
				IPCANQoS sessionQoS = IPCanQoSFactory.createSessionQoSHasLowerQoSThan(upperBoundQoS);
				
				if(IPCAN_QOS_BASED_COMPARATOR.compare(sessionQoS,lowerBoundQoS) <= 0) {
					throw new RuntimeException("Difference between lower bound qos and higher bound qos has one");
				}
				
				return sessionQoS;
			} else {
				if(lowerBoundQoS != null || upperBoundQoS != null) {
					throw new RuntimeException("Either Equality or higher and lower addOn creation is possible. "
							+ "here you call hasEqualQosThan as well as hasLowerQosThan or hasHigherQosThan this is a confusion for me");
				}
				
				return IPCanQoSFactory.createSessionQoSHasEqualQoSTo(equalQoS);
			}
			
		}

	}
	
	


	private static int random() {
		return RANDOM.nextInt(Integer.MAX_VALUE);
	}
}
