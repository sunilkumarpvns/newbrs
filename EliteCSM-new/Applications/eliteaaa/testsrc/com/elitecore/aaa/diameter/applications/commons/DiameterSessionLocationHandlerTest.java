package com.elitecore.aaa.diameter.applications.commons;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public class DiameterSessionLocationHandlerTest {

	private ApplicationRequest request;
	private ApplicationResponse response;
	private DiameterSessionLocationHandler<ApplicationRequest, ApplicationResponse> diameterSessionLocationHandler;
	private SessionData oldest;
	private SessionData old;
	private SessionData newer;
	private SessionData newest;
	
	@Mock private DiameterSessionManager diameterSessionManager;

	@BeforeClass
	public static void loadDictionary() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		createSessionData();

		DiameterRequest diameterRequest = new DiameterRequest();
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);
		
		Mockito.when(diameterSessionManager.locate(request.getDiameterRequest(), response.getDiameterAnswer())).thenReturn(Arrays.<SessionData>asList(newer, old, oldest, newest));

		diameterSessionLocationHandler = new DiameterSessionLocationHandler<ApplicationRequest, ApplicationResponse>(diameterSessionManager);
	}

	@Test
	public void sortsSessionsByLastUpdateTime() {
		diameterSessionLocationHandler.handleRequest(request, response, null);
		List<SessionData> actual  = request.getDiameterRequest().getLocatedSessionData();
		assertThat(actual, hasItems(newest, newer, old, oldest));
	}

	private void createSessionData() {
		String sessionStatus = "sessionStatus";
		String pdpType = "pdpType";
		String groupName = "groupName";
		long currentTime = System.currentTimeMillis();

		oldest = new SessionDataImpl("schema", "1", new Date(currentTime), new Date(currentTime));
		oldest.addValue(sessionStatus, "active");
		oldest.addValue(pdpType, "1");
		oldest.addValue(groupName, "elite");

		old = new SessionDataImpl("schema", "2", new Date(currentTime), new Date(currentTime + 1));
		old.addValue(sessionStatus, "inactive");
		old.addValue(pdpType, "12");
		old.addValue(groupName, "elite");

		newer = new SessionDataImpl("schema", "3", new Date(currentTime), new Date(currentTime + 2));
		newer.addValue(sessionStatus, "active");
		newer.addValue(pdpType, "8");

		newest = new SessionDataImpl("schema", "4", new Date(currentTime), new Date(currentTime + 3));
		newest.addValue(sessionStatus, "deleted");
		newest.addValue(pdpType, "4");
		newest.addValue(groupName, "neon");
	}
}
