package com.elitecore.diameterapi.core.common.session;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.elitecore.commons.base.FixedTimeSource;

/**
 * 
 * @author narendra.pathai
 *
 */
public class SessionTest {

	private static final String ANY_ID = "ANY_ID";
	private static final String ANY_KEY = "ANY_KEY";
	private static final Object ANY_VALUE = "ANY_VALUE";

	private FixedTimeSource fixedTimeSource;
	private Session session;

	@Before
	public void setUp() {
		fixedTimeSource = new FixedTimeSource();
		session = new Session(ANY_ID, null, fixedTimeSource) {};
	}
	
	@Test
	public void settingAParameterInSession_MustUpdateLastAccessTimeToBeCurrentTime() {
		fixedTimeSource.setCurrentTimeInMillis(System.currentTimeMillis());

		session.setParameter(ANY_KEY, ANY_VALUE);
		
		assertThat(session.getLastAccessedTime(), equalTo(fixedTimeSource.currentTimeInMillis()));
	}

	@Test
	public void gettingAParameterFromSession_MustUpdateLastAccessTimeToBeCurrentTime() {
		fixedTimeSource.setCurrentTimeInMillis(System.currentTimeMillis());
		
		session.getParameter(ANY_KEY);

		assertThat(session.getLastAccessedTime(), equalTo(fixedTimeSource.currentTimeInMillis()));
	}

	@Test
	public void removingAParameterFromSession_MustUpdateLastAccessTimeToBeCurrentTime() {
		fixedTimeSource.setCurrentTimeInMillis(System.currentTimeMillis());
		
		session.removeParameter(ANY_KEY);

		assertThat(session.getLastAccessedTime(), equalTo(fixedTimeSource.currentTimeInMillis()));
	}
	
	@Test
	public void addingAppSession_UpdatesLastAccessTimeToBeCurrentTime() {
		fixedTimeSource.setCurrentTimeInMillis(System.currentTimeMillis());
		
		session.addAppSession(mock(AppSession.class));
		
		assertThat(session.getLastAccessedTime(), equalTo(fixedTimeSource.currentTimeInMillis()));
	}
	
	@Test
	public void fetchingAppSession_UpdatesLastAccessTimeToBeCurrentTime() {
		fixedTimeSource.setCurrentTimeInMillis(System.currentTimeMillis());
		
		session.getAppSession();
		
		assertThat(session.getLastAccessedTime(), equalTo(fixedTimeSource.currentTimeInMillis()));
	}
}
