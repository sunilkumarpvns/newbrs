package com.elitecore.netvertex.gateway.diameter.af;

import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public final class PCCRuleNameGenerator {
	
	
	public static final String KEY = "netvertex.pccrule.name.generator";
	public static final String SESSION_ID = "sessionid";
	public static final String UUID = "uuid";
	public static final String COUNTER = "counter";
	
	private static NameGenerator generator = new UUIDBaseNameGenerator();
	
	private PCCRuleNameGenerator() {
	}
	
	public static void register(NameGenerator nameGenerator) {
		generator = nameGenerator;
	}
	
	public static String generate(PCRFResponse pcrfResponse, MediaComponent mediaComponent) {
		return generator.generator(pcrfResponse, mediaComponent);
	}
	
	public static interface NameGenerator {
		public String generator(PCRFResponse pcrfResponse, MediaComponent mediaComponent);
	}
	
	public static class SessionIdBaseNameGenerator implements NameGenerator{

		@Override
		public String generator(PCRFResponse pcrfResponse, MediaComponent mediaComponent) {
			String name = pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val) 
					+ '-' + mediaComponent.getMediaType()
					+ '-' + mediaComponent.getMediaComponentNumber() 
					+ '-' + mediaComponent.getFlowNumber();
			if(mediaComponent.getAfIdentifier() != null) {                    					
				name += '-' + mediaComponent.getAfIdentifier();
			}
			
			return name;
		}
		
	}
	
	public static class UUIDBaseNameGenerator implements NameGenerator{

		@Override
		public String generator(PCRFResponse pcrfResponse, MediaComponent mediaComponent) {
			return java.util.UUID.randomUUID().toString();
		}
		
	}
	
	public static class CounterBaseNameGenerator implements NameGenerator{
		
		private final AtomicLong counter;
		private NetVertexServerContext netVertexServerContext;

		public CounterBaseNameGenerator(NetVertexServerContext netVertexServerContext) {
			this.netVertexServerContext = netVertexServerContext;
			this.counter = new AtomicLong();
		}

		@Override
		public String generator(PCRFResponse pcrfResponse, MediaComponent mediaComponent) {
			return "pcc-" + counter.incrementAndGet() + "-" + netVertexServerContext.getServerInstanceId();
		}
		
	}

}
