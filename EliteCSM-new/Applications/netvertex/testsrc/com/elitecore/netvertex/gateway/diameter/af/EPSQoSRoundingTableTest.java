package com.elitecore.netvertex.gateway.diameter.af;

import static org.junit.Assert.*;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class EPSQoSRoundingTableTest {

	public Object[][] dataProviderFor_testRounding() {
		return new Object[][] {
				{
						0, 0
		},
				{
						1, 1
		},
				{
						62, 62
		},
				{
						65, 72
		},
				{
						567, 568
		},
				{
						569, 576
		},
				{
						8639, 8640
		},
				{
						8641, 8700
		},
				{
						15999, 16000
		},
				{
						160001, 162000
		},
				{
						127999, 128000
		},
				{
						128001, 130000
		},
				{
						255999, 256000
		},
				{
						256001, 260000
		},
				{
						499999, 500000
		},
				{
						500001, 510000
		},
				{
						1499999, 1500000
		},
				{
						1500001, 1600000
		},
				{
						999999, 1000000
		},
				{
						1600000, 1600000 // no rounding
		}
		};
	}

	@Test
	@Parameters(method = "dataProviderFor_testRounding")
	public void testRounding(long qoSInBytes, long expectedOutput) throws Exception {
		assertEquals(expectedOutput, EPSQoSRoundingTable.roundOff(qoSInBytes));
	}
}
