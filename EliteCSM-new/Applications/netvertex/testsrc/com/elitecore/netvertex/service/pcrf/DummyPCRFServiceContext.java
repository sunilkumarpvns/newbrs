package com.elitecore.netvertex.service.pcrf;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.driver.cdr.ValueProviderExtImpl;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.service.pcrf.conf.PCRFServiceConfiguration;
import org.mockito.Mockito;

public class DummyPCRFServiceContext implements PCRFServiceContext {
	
	private DummyNetvertexServerContextImpl netvertexServerContext;

	public static DummyPCRFServiceContext spy() {
		DummyPCRFServiceContext context = new DummyPCRFServiceContext();
		context.setServerContext(DummyNetvertexServerContextImpl.spy());
		return Mockito.spy(context);
	}

	public static DummyPCRFServiceContext mock() {
		return Mockito.mock(DummyPCRFServiceContext.class);
	}

	@Override
	public DummyNetvertexServerContextImpl getServerContext() {
		return netvertexServerContext;
	}

	@Override
	public int getRevalidationTimeDelta() {
		return 600;
	}

	@Override
	public PCRFServiceConfiguration getPCRFServiceConfiguration() {
		return null;
	}

	@Override
	public CDRDriver<ValueProviderExtImpl> getCDRDriver(DriverConfiguration driverConfiguration)
			throws InitializationFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resume(PCRFRequest pcrfRequest, PCRFResponse pcrfResponse,
			ExecutionContext executionContext) {
		// TODO Auto-generated method stub

	}

	public void setServerContext(DummyNetvertexServerContextImpl netvertexServerContext) {
		this.netvertexServerContext = netvertexServerContext;
	}

}
