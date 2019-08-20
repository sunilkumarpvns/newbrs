package com.elitecore.netvertex.service.pcrf;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.driver.cdr.ValueProviderExtImpl;
import com.elitecore.netvertex.core.driver.spr.DriverConfiguration;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.service.pcrf.conf.PCRFServiceConfiguration;

/**
 * This interface provide PCRF service context
 * @author Harsh Patel
 *
 */
public interface PCRFServiceContext extends ServiceContext{

	/**
	 * This method returns the PCRF service configuration
	 * @return <code>PCRFServiceConfiguration</code>
	 */
	public PCRFServiceConfiguration getPCRFServiceConfiguration();
	
	public CDRDriver<ValueProviderExtImpl> getCDRDriver(DriverConfiguration driverConfiguration) throws InitializationFailedException;
	
	public void resume(PCRFRequest pcrfRequest, PCRFResponse pcrfResponse, ExecutionContext executionContext);

	
	public NetVertexServerContext getServerContext();

	public int getRevalidationTimeDelta();
	
}
