package com.elitecore.aaa.core.util.cli;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.util.cli.CdrFlushCommand.FlushEventHandler;
import com.elitecore.commons.base.Predicate;
import com.elitecore.core.util.cli.TableFormatter;

/**
 * 
 * @author narendra.pathai
 *
 */
public class CdrFlushCommandTest {

	private static final String PARAM_ALL = "-all";
	private static final String PARAM_CSV = "-c";
	private static final String PARAM_DETAIL_LOCAL = "-d";
	private CdrFlushCommand command;

	@Before
	public void setUp() {
		command = new CdrFlushCommand();
	}
	
	@Test
	public void testConstants() {
		assertEquals("Parameter for flush all flushable drivers did not match",
				PARAM_ALL, CdrFlushCommand.PARAM_ALL);
		
		assertEquals("Parameter for flush all csv drivers did not match",
				PARAM_CSV, CdrFlushCommand.PARAM_CSV);
		
		assertEquals("Parameter for flush all detail local drivers did not match",
				PARAM_DETAIL_LOCAL, CdrFlushCommand.PARAM_DETAIL_LOCAL);
	}
	
	@Test
	public void testExecute_ShouldCallAllFlushHandlers_WhenAllParameterIsPassed() {
		FlushEventHandler mockFlushHandler = Mockito.mock(FlushEventHandler.class);
		FlushEventHandler mockFlushHandler2 = Mockito.mock(FlushEventHandler.class);
		command.registerFlushEventHandler(mockFlushHandler);
		command.registerFlushEventHandler(mockFlushHandler2);
		
		command.execute(PARAM_ALL);
		
		verify(mockFlushHandler, times(1)).onEvent(Matchers.<Predicate<DriverTypes>>any(), Matchers.<TableFormatter>any());
		verify(mockFlushHandler2, times(1)).onEvent(Matchers.<Predicate<DriverTypes>>any(), Matchers.<TableFormatter>any());
	}
}
