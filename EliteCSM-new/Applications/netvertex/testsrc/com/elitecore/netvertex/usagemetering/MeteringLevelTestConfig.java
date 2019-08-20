package com.elitecore.netvertex.usagemetering;

import java.util.LinkedHashMap;
import java.util.List;

import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;

public class MeteringLevelTestConfig {
	private NetVertexServerContext netVertexServerContext;
	private PolicyRepository policyRepository;
	private PCRFRequest pcrfReq;
	private PCRFResponse pcrfRes;
	private ExecutionContext executionContext;
	private PCRFServiceContext serviceContext;
	private LinkedHashMap<String, Subscription> addOnSubscriptions;


	private MeteringLevelTestConfig() {
		this.policyRepository = new DummyPolicyRepository.PolicyRepositoryBuilder().build();
		DummyNetvertexServerContextImpl netVertexServerContext = new DummyNetvertexServerContextImpl();
		netVertexServerContext.setPolicyRepository(policyRepository);
		this.netVertexServerContext = netVertexServerContext;
		this.pcrfReq = new PCRFRequestImpl();
		this.pcrfRes = new PCRFResponseImpl();
	}
	public NetVertexServerContext getNetVertexServerContext() {
		return netVertexServerContext;
	}
	
	public PolicyRepository getPolicyRepository() {
		return policyRepository;
	}

	public PCRFRequest getPCRFReq() {
		return pcrfReq;
	}
	public PCRFResponse getPCRFRes() {
		return pcrfRes;
	}

	public LinkedHashMap<String, Subscription> getAddOnSubscriptions() {
		return addOnSubscriptions;
	}

	public void setAddOnSubscriptions(List<Subscription> addOnSubscriptions) {

		this.addOnSubscriptions = new LinkedHashMap<String, Subscription>(addOnSubscriptions.size());
		for (Subscription addOnSubscription : addOnSubscriptions) {
			this.addOnSubscriptions.put(addOnSubscription.getId(), addOnSubscription);
		}

	}


	public static class MeteringLevelTestBuilder{
		private final MeteringLevelTestConfig meteringLevelTestConfig;

		public MeteringLevelTestBuilder(){
			meteringLevelTestConfig = new MeteringLevelTestConfig();
		}

		public MeteringLevelTestBuilder withNetVertexServerContext(NetVertexServerContext netVertexServerContext) {
			meteringLevelTestConfig.netVertexServerContext = netVertexServerContext;
			return this;
		}
		
		public MeteringLevelTestBuilder withPolicyRepository(PolicyRepository policyRepository) {
			meteringLevelTestConfig.policyRepository = policyRepository;
			return this;
		}

		public MeteringLevelTestBuilder withPcrfReq(PCRFRequest pcrfReq) {
			meteringLevelTestConfig.pcrfReq = pcrfReq;
			return this;
		}

		public MeteringLevelTestBuilder withPcrfRes(PCRFResponse pcrfRes) {
			meteringLevelTestConfig.pcrfRes = pcrfRes;
			return this;
		}

		public MeteringLevelTestConfig build(){
			return meteringLevelTestConfig;


		}

		public MeteringLevelTestBuilder withExecutionContext(ExecutionContext executionContext) {
			meteringLevelTestConfig.executionContext = executionContext;
			return this;
		}

		public MeteringLevelTestBuilder withServiceContext(DummyPCRFServiceContext serviceContext) {
			meteringLevelTestConfig.serviceContext = serviceContext;
			return this;
		}

		public MeteringLevelTestBuilder withAddOnSubscription(List<Subscription> addOnSubscriptions) {
			meteringLevelTestConfig.setAddOnSubscriptions(addOnSubscriptions);
			return this;
		}
	}



	public PCRFServiceContext getPCRFServiceContext() {
		return serviceContext;
	}
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

}
