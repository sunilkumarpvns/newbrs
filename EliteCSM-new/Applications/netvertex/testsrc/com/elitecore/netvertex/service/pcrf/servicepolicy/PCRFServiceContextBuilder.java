package com.elitecore.netvertex.service.pcrf.servicepolicy;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.conf.PCRFServiceConfiguration;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.impl.PccServicePolicyConfigurationImpl;

import java.util.List;

public class PCRFServiceContextBuilder {
	private NetVertexServerContext serverContext = new DummyNetvertexServerContextImpl();


	private PCRFServiceConfiguration pcrfServiceConfiguration = new PCRFServiceConfiguration() {

		@Override
		public void toString(IndentingToStringBuilder builder) {

		}

		@Override
		public int getWorkerThreadPriority() {
			return 7;
		}


		@Override
		public int getQueueSize() {
			return 10000;
		}


		@Override
		public int getMinimumThread() {
			return 5;
		}

		@Override
		public int getMaximumThread() {
			return 10;
		}


	};


	public PCRFServiceContext build(){
		return new DummyPCRFServiceContext();
	}

	private class DummyPCRFServiceContext implements PCRFServiceContext{

		@Override
		public NetVertexServerContext getServerContext() {
			return serverContext;
		}

		@Override
		public int getRevalidationTimeDelta() {
			return 600;
		}

		@Override
		public PCRFServiceConfiguration getPCRFServiceConfiguration() {
			return pcrfServiceConfiguration;
		}

		@Override
		public CDRDriver getCDRDriver(DriverConfiguration driverConfiguration)
				throws InitializationFailedException {
			return null;
		}

		@Override
		public void resume(PCRFRequest pcrfRequest, PCRFResponse pcrfResponse,
						   ExecutionContext executionContext) {

		}

	}
}
