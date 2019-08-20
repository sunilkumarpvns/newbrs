package com.elitecore.core.systemx.esix;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class ESCommunicatorGroupImplTest {

	private ESCommunicatorGroupImpl<ESCommunicator> group;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		group = spy(ESCommunicatorGroupImpl.class);
	}
	
	@Test
	public void testGetGroupSize_GroupShouldHaveInitialSizeOf0() {
		assertEquals(0, group.getGroupSize());
	}

	@Test
	public void testGetGroupSize_ShouldIncreaseGroupSizeBy1_OnAddingANewPrimaryCommunicator() {
		int initialSize = group.getGroupSize();
		
		group.addCommunicator(mock(ESCommunicator.class));
		
		assertEquals(initialSize + 1, group.getGroupSize());
	}
	
	@Test
	public void testGetGroupSize_ShouldNotIncreaseGroupSize_OnAddingSecondaryCommunicator() {
		int initialSize = group.getGroupSize();
		
		group.addCommunicator(mock(ESCommunicator.class), ESCommunicatorGroup.SECONDARY_COMM_WEIGHTAGE);
		
		assertEquals(initialSize + 1, group.getGroupSize());
	}
	
	@Test
	@Parameters({"1", "2", "3"})
	public void testGetGroupSize_ShouldOnlyIncreaseGroupSizeBy1_OnAddingANewWeightedPrimaryCommunicator(int weight) {
		int initialSize = group.getGroupSize();
		
		group.addCommunicator(mock(ESCommunicator.class), weight);
		
		assertEquals(initialSize + 1, group.getGroupSize());
	}

}
