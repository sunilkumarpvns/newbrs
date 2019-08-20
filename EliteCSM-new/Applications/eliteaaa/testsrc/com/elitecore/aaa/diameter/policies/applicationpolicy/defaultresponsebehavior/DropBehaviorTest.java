package com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior;
import static com.elitecore.core.CoreLibMatchers.ServiceResponseMatchers.isDropped;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.servicepolicy.BaseServicePolicy;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public class DropBehaviorTest {

	private ApplicationRequest request;
	private ApplicationResponse response;
	private BaseServicePolicy<ApplicationRequest> dummyPolicy;
	private DropBehavior dropBehavior;

	@Before
	public void setUp() {
		dropBehavior = new DropBehavior();
		DiameterRequest diameterRequest = new DiameterRequest(true);
		request = new ApplicationRequest(diameterRequest);
		dummyPolicy = new BaseServicePolicy<ApplicationRequest>(null) {

			@Override
			public boolean assignRequest(ApplicationRequest request) {
				return false;
			}

			@Override
			public String getPolicyName() {
				return "DUMMY POLICY";
			}

			@Override
			public void init() throws InitializationFailedException {
				//TODO Is it required to throw this exception here, if yes add the same in test classes of reject and hotline behavior
				throw new UnsupportedOperationException("Should not be called.");
			}
		};
		request.setApplicationPolicy(dummyPolicy);
		response = new ApplicationResponse(diameterRequest);
	}
	
	@Test
	public void dropsTheRequestWhenApplied() {
		whenApplied();
		
		requestIsDropped();
	}

	public void whenApplied() {
		dropBehavior.apply(request, response);
	}

	public void requestIsDropped() {
		assertThat(response, isDropped());
		assertFalse(response.isFurtherProcessingRequired());
		assertTrue(response.isProcessingCompleted());
	}
}
