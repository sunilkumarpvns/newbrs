package com.elitecore.aaa.util.constants;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class AAAServerConstantsTest {
	
	@Test
	public void defaultQueueSizeIs3000() {
		assertEquals(3000, AAAServerConstants.DEFAULT_QUEUE_SIZE);
	}

}
