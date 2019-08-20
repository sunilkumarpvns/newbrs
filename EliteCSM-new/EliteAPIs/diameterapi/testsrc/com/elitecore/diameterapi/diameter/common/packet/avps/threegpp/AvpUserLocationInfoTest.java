package com.elitecore.diameterapi.diameter.common.packet.avps.threegpp;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;

public class AvpUserLocationInfoTest {

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Test
	public void testJIRA_NVSMX_1781_ECGI_ECI_FieldParsingIssue() {
		AvpUserLocationInfo userlocationInfo = new AvpUserLocationInfo(22, 10415, (byte)128, "10415:22", "false");
		userlocationInfo.setValueBytes(DiameterUtility.getBytesFromHexValue("0x8205225127160522510187ed12"));
		assertEquals(25685266, Integer.parseInt(userlocationInfo.getKeyStringValue("ECGI-ECI")));
	}
}
